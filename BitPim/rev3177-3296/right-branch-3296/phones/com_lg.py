"""Phonebook conversations with LG phones"""
import com_brew
import com_phone
import p_lg
import prototypes
import common
class LGPhonebook:
    pbterminator="\x7e"
    MODEPHONEBOOK="modephonebook" # can speak the phonebook protocol
    def __init__(self):
        self.pbseq=0
    def _setmodelgdmgo(self):
        for baud in (0, 115200, 19200, 38400, 230400):
            if baud:
                if not self.comm.setbaudrate(baud):
                    continue
            try:
                self.comm.write("AT$LGDMGO\r\n")
            except:
                self.mode=self.MODENONE
                self.comm.shouldloop=True
                raise
            try:
                if self.comm.readsome().find("OK")>=0:
                    return True
            except com_phone.modeignoreerrortypes:
                self.log("No response to setting DM mode")
        return False
    def _setmodephonebook(self):
        req=p_lg.pbinitrequest()
        respc=p_lg.pbinitresponse
        for baud in 0,38400,115200,230400,19200:
            if baud:
                if not self.comm.setbaudrate(baud):
                    continue
            try:
                self.sendpbcommand(req, respc, callsetmode=False)
                return True
            except com_phone.modeignoreerrortypes:
                pass
        self._setmodelgdmgo()
        for baud in 0,38400,115200:
            if baud:
                if not self.comm.setbaudrate(baud):
                    continue
            try:
                self.sendpbcommand(req, respc, callsetmode=False)
                return True
            except com_phone.modeignoreerrortypes:
                pass
        return False
    def sendpbcommand(self, request, responseclass, callsetmode=True):
        if callsetmode:
            self.setmode(self.MODEPHONEBOOK)
        buffer=prototypes.buffer()
        request.header.sequence=self.pbseq
        self.pbseq+=1
        if self.pbseq>0xff:
            self.pbseq=0
        request.writetobuffer(buffer, logtitle="lg phonebook request")
        data=buffer.getvalue()
        data=common.pppescape(data+common.crcs(data))+common.pppterminator
        firsttwo=data[:2]
        try:
            data=self.comm.writethenreaduntil(data, False, common.pppterminator, logreaduntilsuccess=False)
        except com_phone.modeignoreerrortypes:
            self.mode=self.MODENONE
            self.raisecommsdnaexception("manipulating the phonebook")
        self.comm.success=True
        origdata=data
        d=data.rfind(common.pppterminator,0,-1)
        if d>=0:
            self.log("Multiple LG packets in data - taking last one starting at "+`d+1`)
            self.logdata("Original LG data", origdata, None)
            data=data[d+1:]
        data=common.pppunescape(data)
        crc=data[-3:-1]
        data=data[:-3]
        calccrc=common.crcs(data)
        if calccrc!=crc:
            d=data.find(firsttwo)
            if d>0:
                self.log("Junk at begining of LG packet, data at "+`d`)
                self.logdata("Original LG data", origdata, None)
                self.logdata("Working on LG data", data, None)
                data=data[d:]
                calccrc=common.crcs(data)
            if calccrc!=crc:
                self.logdata("Original LG data", origdata, None)
                self.logdata("Working on LG data", data, None)
                raise common.CommsDataCorruption("LG packet failed CRC check", self.desc)
        if ord(data[0])==0x13:
            raise com_brew.BrewBadBrewCommandException()
        if ord(data[0])==0x14:
            raise com_brew.BrewMalformedBrewCommandException()
        buffer=prototypes.buffer(data)
        res=responseclass()
        res.readfrombuffer(buffer, logtitle="lg phonebook response")
        return res
def cleanupstring(str):
    str=str.replace("\r", "\n")
    str=str.replace("\n\n", "\n")
    str=str.strip()
    return str.split("\n")
class LGIndexedMedia:
    "Implements media for LG phones that use index files"
    def __init__(self):
        pass
    def getwallpapers(self, result):
        return self.getmedia(self.imagelocations, result, 'wallpapers')
    def getringtones(self, result):
        return self.getmedia(self.ringtonelocations, result, 'ringtone')
    def savewallpapers(self, results, merge):
        return self.savemedia('wallpapers', 'wallpaper-index', self.imagelocations, results, merge, self.getwallpaperindices)
    def saveringtones(self, results, merge):
        return self.savemedia('ringtone', 'ringtone-index', self.ringtonelocations, results, merge, self.getringtoneindices)
    def getmediaindex(self, builtins, maps, results, key):
        """Gets the media (wallpaper/ringtone) index
        @param builtins: the builtin list on the phone
        @param results: places results in this dict
        @param maps: the list of index files and locations
        @param key: key to place results in
        """
        self.log("Reading "+key)
        media={}
        c=1
        for name in builtins:
            media[c]={'name': name, 'origin': 'builtin' }
            c+=1
        type=None
        for offset,indexfile,location,type,maxentries in maps:
            if type=="camera": break
            index=self.getindex(indexfile)
            for i in index:
                media[i+offset]={'name': index[i], 'origin': type}
        if type=="camera":
            index=self.getcameraindex()
            for i in index:
                media[i+offset]=index[i]
        results[key]=media
        return media
    def getindex(self, indexfile):
        "Read an index file"
        index={}
        try:
            buf=prototypes.buffer(self.getfilecontents(indexfile))
        except com_brew.BrewNoSuchFileException:
            return index
        g=self.protocolclass.indexfile()
        g.readfrombuffer(buf, logtitle="Index file %s read" % (indexfile,))
        for i in g.items:
            if i.index!=0xffff and len(i.name):
                index[i.index]=i.name
        return index
    def getmedia(self, maps, result, key):
        """Returns the contents of media as a dict where the key is a name as returned
        by getindex, and the value is the contents of the media"""
        media={}
        type=None
        for offset,indexfile,location,type,maxentries in maps:
            if type=="camera": break
            index=self.getindex(indexfile)
            for i in index:
                try:
                    media[index[i]]=self.getfilecontents(location+"/"+index[i], True)
                except (com_brew.BrewNoSuchFileException,com_brew.BrewBadPathnameException,com_brew.BrewNameTooLongException):
                    self.log("It was in the index, but not on the filesystem")
        if type=="camera":
            index=self.getcameraindex()
            for i in index:
                try:
                    media[index[i]['name']]=self.getfilecontents("cam/pic%02d.jpg" % (i,), True)
                except com_brew.BrewNoSuchFileException:
                    self.log("It was in the index, but not on the filesystem")
        result[key]=media
        return result
    def savemedia(self, mediakey, mediaindexkey, maps, results, merge, reindexfunction):
        """Actually saves out the media
        @param mediakey: key of the media (eg 'wallpapers' or 'ringtones')
        @param mediaindexkey:  index key (eg 'wallpaper-index')
        @param maps: list index files and locations
        @param results: results dict
        @param merge: are we merging or overwriting what is there?
        @param reindexfunction: the media is re-indexed at the end.  this function is called to do it
        """
        print results.keys()
        wp=results[mediakey].copy()
        wpi=results[mediaindexkey].copy()
        for k in wpi.keys():
            if wpi[k]['origin']=='builtin':
                del wpi[k]
        init={}
        for offset,indexfile,location,type,maxentries in maps:
            init[type]={}
            for k in wpi.keys():
                if wpi[k]['origin']==type:
                    index=k-offset
                    name=wpi[k]['name']
                    data=None
                    del wpi[k]
                    for w in wp.keys():
                        if wp[w]['name']==name and wp[w]['origin']==type:
                            data=wp[w]['data']
                            del wp[w]
                    if not merge and data is None:
                        continue
                    init[type][index]={'name': name, 'data': data}
        print init.keys()
        for w in wp.keys():
            o=wp[w].get("origin", "")
            if o is not None and len(o) and o in init:
                idx=-1
                while idx in init[o]:
                    idx-=1
                init[o][idx]=wp[w]
                del wp[w]
        for offset,indexfile,location,type,maxentries in maps:
            if type=="camera": break
            index=init[type]
            try:
                dirlisting=self.getfilesystem(location)
            except com_brew.BrewNoSuchDirectoryException:
                self.mkdirs(location)
                dirlisting={}
            for i in dirlisting.keys():
                dirlisting[i[len(location)+1:]]=dirlisting[i]
                del dirlisting[i]
            dellist=[]
            if not merge:
                wpi=results[mediaindexkey]
                for i in wpi:
                    entry=wpi[i]
                    if entry['origin']==type:
                        delit=True
                        for idx in index:
                            if index[idx]['name']==entry['name']:
                                delit=False
                                break
                        if delit:
                            if entry['name'] in dirlisting:
                                dellist.append(entry['name'])
                            else:
                                self.log("%s in %s index but not filesystem" % (entry['name'], type))
            print "deleting",dellist
            for f in dellist:
                self.rmfile(location+"/"+f)
            while len(index)<maxentries and len(wp):
                idx=-1
                while idx in index:
                    idx-=1
                k=wp.keys()[0]
                index[idx]=wp[k]
                del wp[k]
            index=self._normaliseindices(index)  # hey look, I called a function!
            if len(index)>maxentries:
                keys=index.keys()
                keys.sort()
                for k in keys[maxentries:]:
                    idx=-1
                    while idx in wp:
                        idx-=1
                    wp[idx]=index[k]
                    del index[k]
            keys=index.keys()
            keys.sort()
            ifile=self.protocolclass.indexfile()
            ifile.numactiveitems=len(keys)
            for k in keys:
                entry=self.protocolclass.indexentry()
                entry.index=k
                entry.name=index[k]['name']
                ifile.items.append(entry)
            while len(ifile.items)<maxentries:
                ifile.items.append(self.protocolclass.indexentry())
            buffer=prototypes.buffer()
            ifile.writetobuffer(buffer, logtitle="Updated index file "+indexfile)
            self.writefile(indexfile, buffer.getvalue())
            for k in keys:
                entry=index[k]
                data=entry.get("data", None)
                if data is None:
                    if entry['name'] not in dirlisting:
                        self.log("Index error.  I have no data for "+entry['name']+" and it isn't already in the filesystem")
                    continue
                if entry['name'] in dirlisting and len(data)==dirlisting[entry['name']]['size']:
                    self.log("Skipping writing %s/%s as there is already a file of the same length" % (location,entry['name']))
                    continue
                self.writefile(location+"/"+entry['name'], data)
        if len(wp):
            for k in wp:
                self.log("Unable to put %s on the phone as there weren't any spare index entries" % (wp[k]['name'],))
        del results[mediakey] # done with it
        reindexfunction(results)
        return results
class LGNewIndexedMedia:
    "Implements media for LG phones that use the new index format such as the VX7000/8000"
    _minor_typemap={
        0: {  # images
        ".jpg": 3,
        ".bmp": 1,
        },
        1: { # audio
        ".qcp": 5,
        ".mid": 4,
        },
        2: { # video
        ".3g2": 3,
        }
    }
    def __init__(self):
        pass
    def getmediaindex(self, builtins, maps, results, key):
        """Gets the media (wallpaper/ringtone) index
        @param builtins: the builtin list on the phone
        @param results: places results in this dict
        @param maps: the list of index files and locations
        @param key: key to place results in
        """
        self.log("Reading "+key)
        media={}
        for i,n in enumerate(builtins): # nb zero based index whereas previous phones used 1
            media[i]={'name': n, 'origin': 'builtin'}
        for type, indexfile, sizefile, directory, lowestindex, maxentries, typemajor  in maps:
            for item in self.getindex(indexfile):
                if item.type&0xff!=typemajor:
                    self.log("Entry "+item.filename+" has wrong type for this index.  It is %d and should be %d" % (item.type&0xff, typemajor))
                    self.log("This is going to cause you all sorts of problems.")
                media[item.index]={
                    'name': basename(item.filename),
                    'filename': item.filename,
                    'origin': type,
                    'vtype': item.type,
                    }
                if item.date!=0:
                    media[item.index]['date']=item.date
        results[key]=media
    def getindex(self, filename):
        "read an index file"
        try:
            buf=prototypes.buffer(self.getfilecontents(filename))
        except com_brew.BrewNoSuchFileException:
            return []
        g=self.protocolclass.indexfile()
        try:
            g.readfrombuffer(buf, logtitle="Index file "+filename)
        except:
            self.log("Corrupt index file "+`filename`+", this might cause you all sorts of problems.")
        return g.items
    def getmedia(self, maps, results, key):
        origins={}
        origins['new_media_version']=1
        for type, indexfile, sizefile, directory, lowestindex, maxentries, typemajor  in maps:
            media={}
            for item in self.getindex(indexfile):
                data=None
                timestamp=None
                try:
                    stat_res=self.statfile(item.filename)
                    if stat_res!=None:
                        timestamp=stat_res['date'][0]
                    if not self._is_rs_file(item.filename):
                        data=self.getfilecontents(item.filename, True)
                except (com_brew.BrewNoSuchFileException,com_brew.BrewBadPathnameException,com_brew.BrewNameTooLongException):
                    self.log("It was in the index, but not on the filesystem")
                if data!=None:
                    media[common.basename(item.filename)]={ 'data': data, 'timestamp': timestamp}
            origins[type]=media
        results[key]=origins
        return results
    def savemedia(self, mediakey, mediaindexkey, maps, results, merge, reindexfunction):
        """Actually saves out the media
        @param mediakey: key of the media (eg 'wallpapers' or 'ringtones')
        @param mediaindexkey:  index key (eg 'wallpaper-index')
        @param maps: list index files and locations
        @param results: results dict
        @param merge: are we merging or overwriting what is there?
        @param reindexfunction: the media is re-indexed at the end.  this function is called to do it
        """
        wp=results[mediakey].copy()  # the media we want to save
        wpi=results[mediaindexkey].copy() # what is already in the index files
        for k in wpi.keys():
            if wpi[k].get('origin', "")=='builtin':
                del wpi[k]
        init={}
        for type,_,_,_,lowestindex,_,typemajor in maps:
            init[type]={}
            for k in wpi.keys():
                if wpi[k]['origin']==type:
                    index=k
                    name=wpi[k]['name']
                    fullname=wpi[k]['filename']
                    vtype=wpi[k]['vtype']
                    data=None
                    del wpi[k]
                    for w in wp.keys():
                        if wp[w]['name']==name and wp[w]['origin']==type:
                            data=wp[w]['data']
                            del wp[w]
                    if not merge and data is None:
                        continue
                    assert index>=lowestindex
                    init[type][index]={'name': name, 'data': data, 'filename': fullname, 'vtype': vtype}
        assert len(wpi)==0
        print init.keys()
        for w in wp.keys():
            o=wp[w].get("origin", "")
            if o is not None and len(o) and o in init:
                idx=-1
                while idx in init[o]:
                    idx-=1
                init[o][idx]=wp[w]
                del wp[w]
        for type,_,_,_,lowestindex,maxentries,typemajor in maps:
            for w in wp.keys():
                if len(init[type])>=maxentries:
                    break
                idx=-1
                while idx in init[type]:
                    idx-=1
                init[type][idx]=wp[w]
                del wp[w]
        dircache=self.DirCache(self)
        for type, indexfile, sizefile, directory, lowestindex, maxentries,typemajor  in maps:
            names=[init[type][x]['name'] for x in init[type]]
            for item in self.getindex(indexfile):
                if basename(item.filename) not in names:
                    self.log(item.filename+" is being deleted")
                    try:
                        dircache.rmfile(item.filename)
                    except com_brew.BrewNoSuchFileException:
                        self.log("Hmm, it didn't exist!")
            fixups=[k for k in init[type].keys() if k<lowestindex]
            fixups.sort()
            for f in fixups:
                for ii in xrange(lowestindex, lowestindex+maxentries):
                    if ii not in init[type]:
                        init[type][ii]=init[type][f]
                        del init[type][f]
                        break
            fixups=[k for k in init[type].keys() if k<lowestindex]
            for f in fixups:
                self.log("There is no space in the index for "+type+" for "+init[type][f]['name'])
                del init[type][f]
            for idx in init[type].keys():
                entry=init[type][idx]
                filename=entry.get('filename', directory+"/"+entry['name'])
                entry['filename']=filename
                fstat=dircache.stat(filename)
                if 'data' not in entry:
                    if fstat is None:
                        self.log("Entry "+entry['name']+" is in index "+indexfile+" but there is no data for it and it isn't in the filesystem.  The index entry will be removed.")
                        del init[type][idx]
                        continue
                data=entry['data']
                if data is None:
                    assert merge 
                    continue # we are doing an add and don't have data for this existing entry
                if fstat is not None and len(data)==fstat['size']:
                    self.log("Not writing "+filename+" as a file of the same name and length already exists.")
                else:
                    dircache.writefile(filename, data)
            ifile=self.protocolclass.indexfile()
            idxlist=init[type].keys()
            idxlist.sort()
            idxlist.reverse() # the phone has them in reverse order for some reason so we do the same
            for idx in idxlist:
                ie=self.protocolclass.indexentry()
                ie.index=idx
                vtype=init[type][idx].get("vtype", None)
                if vtype is None:
                    vtype=self._guessvtype(init[type][idx]['filename'], typemajor)
                    print "guessed vtype of "+`vtype`+" for "+init[type][idx]['filename']
                else:
                    print init[type][idx]['filename']+" already had a vtype of "+`vtype`
                ie.type=vtype
                ie.filename=init[type][idx]['filename']
                ie.dunno=0 # mmmm
                ifile.items.append(ie)
            buf=prototypes.buffer()
            ifile.writetobuffer(buf, logtitle="Index file "+indexfile)
            self.log("Writing index file "+indexfile+" for type "+type+" with "+`len(idxlist)`+" entries.")
            dircache.writefile(indexfile, buf.getvalue()) # doesn't really need to go via dircache
            size=0
            for idx in idxlist:
                fstat=dircache.stat(init[type][idx]['filename'])
                size+=fstat['size']
            szfile=self.protocolclass.sizefile()
            szfile.size=size
            buf=prototypes.buffer()
            szfile.writetobuffer(buf, logtitle="size file for "+type)
            self.log("You are using a total of "+`size`+" bytes for "+type)
            dircache.writefile(sizefile, buf.getvalue())
        return reindexfunction(results)
    def _guessvtype(self, filename, typemajor):
        lookin=self._minor_typemap[typemajor]
        for ext,val in lookin.items():
            if filename.lower().endswith(ext):
                return typemajor+(256*val)
        return typemajor # implicit val of zero
    def getwallpaperindices(self, results):
        return self.getmediaindex(self.builtinwallpapers, self.wallpaperlocations, results, 'wallpaper-index')
    def getringtoneindices(self, results):
        return self.getmediaindex(self.builtinringtones, self.ringtonelocations, results, 'ringtone-index')
    def getwallpapers(self, result):
        return self.getmedia(self.wallpaperlocations, result, 'wallpapers')
    def getringtones(self, result):
        return self.getmedia(self.ringtonelocations, result, 'ringtone')
    def savewallpapers(self, results, merge):
        return self.savemedia('wallpapers', 'wallpaper-index', self.wallpaperlocations, results, merge, self.getwallpaperindices)
    def saveringtones(self, results, merge):
        return self.savemedia('ringtone', 'ringtone-index', self.ringtonelocations, results, merge, self.getringtoneindices)
class LGNewIndexedMedia2(LGNewIndexedMedia):
    """Implements media for LG phones that use the newer index format such as the VX8100/5200
    Similar ot the other new media type hence the subclassing, but the type field in the
    PACKET has a different meaning so different code is required and it has support for an icon
    field that affects whcih icon is displayed next to the tone on the phone
    """
    _minor_typemap={
        ".jpg": 3,
        ".bmp": 1,
        ".qcp": 5,
        ".mid": 4,
        ".3g2": 3,
        }
    def _guessvtype(self, filename, typemajor):
        if typemajor > 2: # some index files are hard coded
            return typemajor
        for ext,val in self._minor_typemap.items():
            if filename.lower().endswith(ext):
                return typemajor+(256*val)
        return typemajor # implicit val of zero
    def getmediaindex(self, builtins, maps, results, key):
        """Gets the media (wallpaper/ringtone) index
        @param builtins: the builtin list on the phone
        @param results: places results in this dict
        @param maps: the list of index files and locations
        @param key: key to place results in
        """
        self.log("Reading "+key)
        media={}
        for i,n in enumerate(builtins): # nb zero based index whereas previous phones used 1
            media[i]={'name': n, 'origin': 'builtin'}
        for type, indexfile, sizefile, directory, lowestindex, maxentries, typemajor, def_icon, idx_ofs  in maps:
            for item in self.getindex(indexfile):
                if item.type&0xff!=typemajor&0xff:
                    self.log("Entry "+item.filename+" has wrong type for this index.  It is %d and should be %d" % (item.type&0xff, typemajor))
                    self.log("This is going to cause you all sorts of problems.")
                _idx=item.index+idx_ofs
                media[_idx]={
                    'name': basename(item.filename),
                    'filename': item.filename,
                    'origin': type,
                    'vtype': item.type,
                    'icon': item.icon
                    }
                if item.date!=0:
                    media[_idx]['date']=item.date
        results[key]=media
    def getmedia(self, maps, results, key):
        origins={}
        origins['new_media_version']=1
        for type, indexfile, sizefile, directory, lowestindex, maxentries, typemajor, def_icon, idx_ofs  in maps:
            media={}
            for item in self.getindex(indexfile):
                data=None
                timestamp=None
                try:
                    stat_res=self.statfile(item.filename)
                    if stat_res!=None:
                        timestamp=stat_res['date'][0]
                    if not self._is_rs_file(item.filename):
                        data=self.getfilecontents(item.filename, True)
                except (com_brew.BrewNoSuchFileException,com_brew.BrewBadPathnameException,com_brew.BrewNameTooLongException):
                    self.log("It was in the index, but not on the filesystem")
                except com_brew.BrewAccessDeniedException:
                    self.log('Failed to read file: '+item.filename)
                    data=''
                if data!=None:
                    media[common.basename(item.filename)]={ 'data': data, 'timestamp': timestamp}
            origins[type]=media
        results[key]=origins
        return results
    def getmedia(self, maps, results, key):
        media={}
        for type, indexfile, sizefile, directory, lowestindex, maxentries, typemajor, def_icon, idx_ofs in maps:
            for item in self.getindex(indexfile):
                try:
                    media[basename(item.filename)]=self.getfilecontents(item.filename,
                                                                        True)
                except (com_brew.BrewNoSuchFileException,com_brew.BrewBadPathnameException,com_brew.BrewNameTooLongException):
                    self.log("It was in the index, but not on the filesystem")
        results[key]=media
        return results
    def savemedia(self, mediakey, mediaindexkey, maps, results, merge, reindexfunction):
        """Actually saves out the media
        @param mediakey: key of the media (eg 'wallpapers' or 'ringtones')
        @param mediaindexkey:  index key (eg 'wallpaper-index')
        @param maps: list index files and locations
        @param results: results dict
        @param merge: are we merging or overwriting what is there?
        @param reindexfunction: the media is re-indexed at the end.  this function is called to do it
        """
        wp=results[mediakey].copy()  # the media we want to save
        wpi=results[mediaindexkey].copy() # what is already in the index files
        for k in wpi.keys():
            if wpi[k].get('origin', "")=='builtin':
                del wpi[k]
        init={}
        for type,_,_,_,lowestindex,_,typemajor,_,idx_ofs in maps:
            init[type]={}
            for k in wpi.keys():
                if wpi[k]['origin']==type:
                    index=k-idx_ofs
                    name=wpi[k]['name']
                    fullname=wpi[k]['filename']
                    vtype=wpi[k]['vtype']
                    icon=wpi[k]['icon']
                    data=None
                    del wpi[k]
                    for w in wp.keys():
                        if wp[w]['name']==name and wp[w]['origin']==type:
                            data=wp[w]['data']
                            del wp[w]
                    if not merge and data is None:
                        continue
                    assert index>=lowestindex
                    init[type][index]={'name': name, 'data': data, 'filename': fullname, 'vtype': vtype, 'icon': icon}
        assert len(wpi)==0
        print init.keys()
        for w in wp.keys():
            o=wp[w].get("origin", "")
            if o is not None and len(o) and o in init:
                idx=-1
                while idx in init[o]:
                    idx-=1
                init[o][idx]=wp[w]
                del wp[w]
        for type,_,_,_,lowestindex,maxentries,typemajor,def_icon,_ in maps:
            for w in wp.keys():
                if len(init[type])>=maxentries:
                    break
                idx=-1
                while idx in init[type]:
                    idx-=1
                init[type][idx]=wp[w]
                del wp[w]
        dircache=self.DirCache(self)
        for type, indexfile, sizefile, directory, lowestindex, maxentries,typemajor,def_icon,_  in maps:
            names=[init[type][x]['name'] for x in init[type]]
            for item in self.getindex(indexfile):
                if basename(item.filename) not in names:
                    self.log(item.filename+" is being deleted")
                    try:
                        dircache.rmfile(item.filename)
                    except com_brew.BrewNoSuchFileException:
                        self.log("Hmm, it didn't exist!")
            fixups=[k for k in init[type].keys() if k<lowestindex]
            fixups.sort()
            for f in fixups:
                for ii in xrange(lowestindex, lowestindex+maxentries):
                    if ii not in init[type]:
                        init[type][ii]=init[type][f]
                        del init[type][f]
                        break
            fixups=[k for k in init[type].keys() if k<lowestindex]
            for f in fixups:
                self.log("There is no space in the index for "+type+" for "+init[type][f]['name'])
                del init[type][f]
            for idx in init[type].keys():
                entry=init[type][idx]
                filename=entry.get('filename', directory+"/"+entry['name'])
                entry['filename']=filename
                fstat=dircache.stat(filename)
                if 'data' not in entry:
                    if fstat is None:
                        self.log("Entry "+entry['name']+" is in index "+indexfile+" but there is no data for it and it isn't in the filesystem.  The index entry will be removed.")
                        del init[type][idx]
                        continue
                data=entry['data']
                if data is None:
                    assert merge 
                    continue # we are doing an add and don't have data for this existing entry
                if fstat is not None and len(data)==fstat['size']:
                    self.log("Not writing "+filename+" as a file of the same name and length already exists.")
                else:
                    dircache.writefile(filename, data)
            ifile=self.protocolclass.indexfile()
            idxlist=init[type].keys()
            idxlist.sort()
            idxlist.reverse() # the phone has them in reverse order for some reason so we do the same
            for idx in idxlist:
                ie=self.protocolclass.indexentry()
                ie.index=idx
                vtype=init[type][idx].get("vtype", None)
                if vtype is None:
                    vtype=self._guessvtype(init[type][idx]['filename'], typemajor)
                ie.type=vtype
                ie.filename=init[type][idx]['filename']
                ie.dunno=0 # mmmm
                icon=init[type][idx].get("icon", None)
                if icon is None:
                    icon=def_icon
                ie.icon=icon
                ifile.items.append(ie)
            buf=prototypes.buffer()
            ifile.writetobuffer(buf, logtitle="Index file "+indexfile)
            self.log("Writing index file "+indexfile+" for type "+type+" with "+`len(idxlist)`+" entries.")
            dircache.writefile(indexfile, buf.getvalue()) # doesn't really need to go via dircache
            if sizefile != '':
                size=0
                for idx in idxlist:
                    fstat=dircache.stat(init[type][idx]['filename'])
                    size+=fstat['size']
                szfile=self.protocolclass.sizefile()
                szfile.size=size
                buf=prototypes.buffer()
                szfile.writetobuffer(buf, logtitle="Writing size file "+sizefile)
                self.log("You are using a total of "+`size`+" bytes for "+type)
                dircache.writefile(sizefile, buf.getvalue())
        return reindexfunction(results)
class LGDirectoryMedia:
    """The media is stored one per directory with .desc and body files"""
    def __init__(self):
        pass
    def getmediaindex(self, builtins, maps, results, key):
        """Gets the media (wallpaper/ringtone) index
        @param builtins: the builtin list on the phone
        @param results: places results in this dict
        @param maps: the list of index files and locations
        @param key: key to place results in
        """
        self.log("Reading "+key)
        media={}
        c=1
        for name in builtins:
            media[c]={'name': name, 'origin': 'builtin' }
            c+=1
        for offset,location,origin,maxentries in maps:
            index=self.getindex(location)
            for i in index:
                media[i+offset]={'name': index[i], 'origin': origin}
        results[key]=media
        return media
    __mimetoextensionmapping={
        'image/jpg': '.jpg',
        'image/bmp': '.bmp',
        'image/png': '.png',
        'image/gif': '.gif',
        'image/bci': '.bci',
        'audio/mp3': '.mp3',
        'audio/mid': '.mid',
        'audio/qcp': '.qcp'
        }
    def _createnamewithmimetype(self, name, mt):
        name=basename(name)
        if mt=="image/jpeg":
            mt="image/jpg"
        try:
            return name+self.__mimetoextensionmapping[mt]
        except KeyError:
            self.log("Unable to figure out extension for mime type "+mt)
            return name
    def _getmimetype(self, name):
        ext=getext(name.lower())
        if len(ext): ext="."+ext
        if ext==".jpeg":
            return "image/jpg" # special case
        for mt,extension in self.__mimetoextensionmapping.items():
            if ext==extension:
                return mt
        self.log("Unable to figure out a mime type for "+name)
        assert False, "No idea what type "+ext+" is"
        return "x-unknown/x-unknown"
    def getindex(self, location, getmedia=False):
        """Returns an index based on the sub-directories of location.
        The key is an integer, and the value is the corresponding name"""
        index={}
        try:
            dirlisting=self.getfilesystem(location)
        except com_brew.BrewNoSuchDirectoryException:
            return index
        for item in dirlisting:
            if dirlisting[item]['type']!='directory':
                continue
            try:
                buf=prototypes.buffer(self.getfilecontents(dirlisting[item]['name']+"/.desc"))
            except com_brew.BrewNoSuchFileException:
                self.log("No .desc file in "+dirlisting[item]['name']+" - ignoring directory")
                continue
            desc=self.protocolclass.mediadesc()
            desc.readfrombuffer(buf, logtitle=".desc file %s/.desc read" % (dirlisting[item]['name'],))
            filename=self._createnamewithmimetype(dirlisting[item]['name'], desc.mimetype)
            if not getmedia:
                index[desc.index]=filename
            else:
                try:
                    contents=self.getfilecontents(dirlisting[item]['name']+"/"+desc.filename)
                except (com_brew.BrewNoSuchFileException,com_brew.BrewNoSuchDirectoryException):
                    try:
                        contents=self.getfilecontents(dirlisting[item]['name']+"/body")
                    except (com_brew.BrewNoSuchFileException,com_brew.BrewNoSuchDirectoryException,com_brew.BrewNameTooLongException):
                        self.log("Can't find the actual content in "+dirlisting[item]['name'])
                        continue
                index[filename]=contents
        return index
    def getmedia(self, maps, result, key):
        """Returns the contents of media as a dict where the key is a name as returned
        by getindex, and the value is the contents of the media"""
        media={}
        for offset,location,origin,maxentries in maps:
            media.update(self.getindex(location, getmedia=True))
        result[key]=media
        return result
    def savemedia(self, mediakey, mediaindexkey, maps, results, merge, reindexfunction):
        """Actually saves out the media
        @param mediakey: key of the media (eg 'wallpapers' or 'ringtones')
        @param mediaindexkey:  index key (eg 'wallpaper-index')
        @param maps: list index files and locations
        @param results: results dict
        @param merge: are we merging or overwriting what is there?
        @param reindexfunction: the media is re-indexed at the end.  this function is called to do it
        """
        print results.keys()
        wp=results[mediakey].copy()
        wpi=results[mediaindexkey].copy()
        for k in wpi.keys():
            if wpi[k]['origin']=='builtin':
                del wpi[k]
        init={}
        for offset,location,type,maxentries in maps:
            init[type]={}
            for k in wpi.keys():
                if wpi[k]['origin']==type:
                    index=k-offset
                    name=wpi[k]['name']
                    data=None
                    del wpi[k]
                    for w in wp.keys():
                        if wp[w]['name']==name and wp[w]['origin']==type:
                            data=wp[w]['data']
                            del wp[w]
                    if not merge and data is None:
                        continue
                    init[type][index]={'name': name, 'data': data}
        print init.keys()
        for w in wp.keys():
            o=wp[w].get("origin", "")
            if o is not None and len(o) and o in init:
                idx=-1
                while idx in init[o]:
                    idx-=1
                init[o][idx]=wp[w]
                del wp[w]
        for offset,location,type,maxentries in maps:
            if type=="camera": break
            index=init[type]
            try:
                dirlisting=self.getfilesystem(location)
            except com_brew.BrewNoSuchDirectoryException:
                self.mkdirs(location)
                dirlisting={}
            for i in dirlisting.keys():
                dirlisting[i[len(location)+1:]]=dirlisting[i]
                del dirlisting[i]
            dellist=[]
            if not merge:
                wpi=results[mediaindexkey]
                for i in wpi:
                    entry=wpi[i]
                    if entry['origin']==type:
                        delit=True
                        for idx in index:
                            if index[idx]['name']==entry['name']:
                                delit=False
                                break
                        if delit:
                            if stripext(entry['name']) in dirlisting:
                                dellist.append(entry['name'])
                            else:
                                self.log("%s in %s index but not filesystem" % (entry['name'], type))
            print "deleting",dellist
            for f in dellist:
                self.rmdirs(location+"/"+f)
            while len(index)<maxentries and len(wp):
                idx=-1
                while idx in index:
                    idx-=1
                k=wp.keys()[0]
                index[idx]=wp[k]
                del wp[k]
            index=self._normaliseindices(index)  # hey look, I called a function!
            if len(index)>maxentries:
                keys=index.keys()
                keys.sort()
                for k in keys[maxentries:]:
                    idx=-1
                    while idx in wp:
                        idx-=1
                    wp[idx]=index[k]
                    del index[k]
            listing=self.getfilesystem(location, 1)
            for key in index:
                efile=index[key]['name']
                content=index[key]['data']
                if content is None:
                    continue # in theory we could rewrite .desc file in case index number has changed
                mimetype=self._getmimetype(efile)
                dirname=stripext(efile)
                desc=self.protocolclass.mediadesc()
                desc.index=key
                desc.filename="body"
                desc.mimetype=mimetype
                desc.totalsize=0
                desc.totalsize=desc.packetsize()+len(content)
                buf=prototypes.buffer()
                descfile="%s/%s/.desc" % (location, dirname)
                desc.writetobuffer(buf, logtitle="Desc file at "+descfile)
                try:
                    self.mkdir("%s/%s" % (location,dirname))
                except com_brew.BrewDirectoryExistsException:
                    pass
                self.writefile(descfile, buf.getvalue())
                bodyfile="%s/%s/body" % (location, dirname)
                if bodyfile in listing and len(content)==listing[bodyfile]['size']:
                    self.log("Skipping writing %s as there is already a file of the same length" % (bodyfile,))
                else:
                    self.writefile(bodyfile, content)
        if len(wp):
            for k in wp:
                self.log("Unable to put %s on the phone as there weren't any spare index entries" % (wp[k]['name'],))
        del results[mediakey] # done with it
        reindexfunction(results)
        return results
def basename(name):
    if name.rfind('/')>=0:
        pos=name.rfind('/')
        name=name[pos+1:]
    return name
def dirname(name):
    if name.rfind("/")<1:
        return ""
    return name[:name.rfind("/")]
def stripext(name):
    if name.rfind('.')>=0:
        name=name[:name.rfind('.')]
    return name
def getext(name):
    name=basename(name)
    if name.rfind('.')>=0:
        return name[name.rfind('.')+1:]
    return ''
