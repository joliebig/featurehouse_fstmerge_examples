"""Communicate with the LG VX3200 cell phone
The VX3200 is somewhat similar to the VX4400
"""

import time

import cStringIO

import sha

import re

import common

import copy

import p_lgvx3200

import com_lgvx4400

import com_brew

import com_phone

import com_lg

import prototypes

import phone_media_codec

import conversions

media_codec=phone_media_codec.codec_name

class  Phone (com_lgvx4400.Phone) :
	"Talk to the LG VX3200 cell phone"
	    desc="LG-VX3200"
	    wallpaperindexfilename="download/dloadindex/brewImageIndex.map"
	    ringerindexfilename="download/dloadindex/brewRingerIndex.map"
	    protocolclass=p_lgvx3200
	    serialsname='lgvx3200'
	    imagelocations=(
        ( 11, "download/dloadindex/brewImageIndex.map", "download", "images", 3) ,
        )
	    ringtonelocations=(
        ( 27, "download/dloadindex/brewRingerIndex.map", "user/sound/ringer", "ringers", 30),
        )
	    builtinimages= ('Sport 1', 'Sport 2', 'Nature 1', 'Nature 2',
                    'Animal', 'Martini', 'Goldfish', 'Umbrellas',
                    'Mountain climb', 'Country road')
	    builtinringtones= ('Ring 1', 'Ring 2', 'Ring 3', 'Ring 4', 'Ring 5', 'Ring 6',
                       'Ring 7', 'Ring 8', 'Annen Polka', 'Pachelbel Canon', 
                       'Hallelujah', 'La Traviata', 'Leichte Kavallerie Overture', 
                       'Mozart Symphony No.40', 'Bach Minuet', 'Farewell', 
                       'Mozart Piano Sonata', 'Sting', 'O solemio', 
                       'Pizzicata Polka', 'Stars and Stripes Forever', 
                       'Pineapple Rag', 'When the Saints Go Marching In', 'Latin', 
                       'Carol 1', 'Carol 2')
	    def __init__(self, logtarget, commport):

        com_lgvx4400.Phone.__init__(self,logtarget,commport)

        self.mode=self.MODENONE

        self.mediacache=self.DirCache(self)

	def makeentry(self, counter, entry, dict):

        e=com_lgvx4400.Phone.makeentry(self, counter, entry, dict)

        e.entrysize=0x202

        return e

	def getindex(self, indexfile):

        "Read an index file"

        index={}

        if re.search("ImageIndex", indexfile) is not None:

            ind=0

            for ifile in 'wallpaper', 'poweron', 'poweroff':

                ifilefull="download/"+ifile+".bit"

                try:

                    mediafiledata=self.mediacache.readfile(ifilefull)

                    if len(mediafiledata)!=0:

                        index[ind]=ifile

                        ind = ind + 1

                        self.log("Index file "+indexfile+" entry added: "+ifile)

                except:

                    pass

        else:

            try:

                buf=prototypes.buffer(self.getfilecontents(indexfile))

            except com_brew.BrewNoSuchFileException:

                return index

            g=self.protocolclass.indexfile()

            g.readfrombuffer(buf)

            self.logdata("Index file %s read with %d entries" % (indexfile,g.numactiveitems), buf.getdata(), g)

            for i in g.items:

                if i.index!=0xffff:

                    ifile=re.sub("\.mid|\.MID", "", i.name)

                    self.log("Index file "+indexfile+" entry added: "+ifile)

                    index[i.index]=ifile

        return index

	def getmedia(self, maps, result, key):

        """Returns the contents of media as a dict where the key is a name as returned
        by getindex, and the value is the contents of the media"""

        media={}

        type=None

        for offset,indexfile,location,type,maxentries in maps:

            index=self.getindex(indexfile)

            for i in index:

                if type=="images":

                    mediafilename=index[i]+".bit"

                else:

                    mediafilename=index[i]+".mid"

                try:

                    media[index[i]]=self.mediacache.readfile(location+"/"+mediafilename)

                except com_brew.BrewNoSuchFileException:

                    self.log("Missing index file: "+location+"/"+mediafilename)

        result[key]=media

        return result

	def savemedia(self, mediakey, mediaindexkey, maps, results, merge, reindexfunction):

        """Actually saves out the media
        @param mediakey: key of the media (eg 'wallpapers' or 'ringtone')
        @param mediaindexkey:  index key (eg 'wallpaper-index')
        @param maps: list index files and locations
        @param results: results dict
        @param merge: are we merging or overwriting what is there?
        @param reindexfunction: the media is re-indexed at the end.  this function is called to do it
        """

        print results.keys()

        wp=results[mediakey].copy()

        wpi=results[mediaindexkey].copy()

        for k in wp.keys():

            wp[k]['name']=re.sub("\....$", "", wp[k]['name'])

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

                        if wp[w]['name']==name:

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

                            if type=="ringers":

                                entryname=entry['name']+".mid"

                            else:

                                entryname=entry['name']+".bit"

                            if entryname in dirlisting:

                                dellist.append(entryname)

                            else:

                                self.log("%s in %s index but not filesystem" % (entryname, type))

            print "deleting",dellist

            for f in dellist:

                self.mediacache.rmfile(location+"/"+f)

            if type=="images":

                losem=[]

                wpi=results[mediaindexkey]

                for idx in index:

                    delit=True

                    for i in wpi:

                        entry=wpi[i]

                        if entry['origin']==type:

                            if index[idx]['name']==entry['name']:

                                delit=False

                                break

                    if delit:

                        self.log("Inhibited upload of illegit image (not originally on phone): "+index[idx]['name'])

                        losem.append(idx)

                for idx in losem:

                    del index[idx]

            while len(index)<maxentries and len(wp):

                idx=-1

                while idx in index:

                    idx-=1

                k=wp.keys()[0]

                index[idx]=wp[k]

                del wp[k]

            index=self._normaliseindices(index)  

            if len(index)>maxentries:

                keys=index.keys()

                keys.sort()

                for k in keys[maxentries:]:

                    idx=-1

                    while idx in wp:

                        idx-=1

                    wp[idx]=index[k]

                    del index[k]

            for k in index.keys():

                if type=="ringers":

                    index[k]['name']=index[k]['name']+".mid"

                else:

                    index[k]['name']=index[k]['name']+".bit"

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

            ifile.writetobuffer(buffer)

            if type!="images":

                self.logdata("Updated index file "+indexfile, buffer.getvalue(), ifile)

                self.writefile(indexfile, buffer.getvalue())

            for k in keys:

                entry=index[k]

                entryname=entry['name']

                data=entry.get("data", None)

                if type=="images":

                    if entryname!="wallpaper.bit" and entryname!="poweron.bit" and entryname!="poweroff.bit":

                        self.log("The wallpaper files can only be wallpaper.bmp, poweron.bmp or poweroff.bmp. "+entry['name']+" does not conform - skipping upload.")

                        continue

                if data is None:

                    if entryname not in dirlisting:

                        self.log("Index error.  I have no data for "+entryname+" and it isn't already in the filesystem - skipping upload.")

                    continue

                if type=="images" and data[0:2]=="BM":

                    data=conversions.convertbmptolgbit(data)

                    if data is None:

                        self.log("The wallpaper BMP images must be 8BPP or 24BPP, "+entry['name']+", does not comply - skipping upload.")

                        continue

                if type=="images" and (common.LSBUint16(data[0:2])!=128 or common.LSBUint16(data[2:4])!=128):

                        self.log("The wallpaper must be 128x128, "+entry['name']+", does not comply - skipping upload.")

                        continue

                if type!="images":

                    if entryname in dirlisting and len(data)==dirlisting[entryname]['size']:

                        self.log("Skipping writing %s/%s as there is already a file of the same length" % (location,entryname))

                        continue

                self.mediacache.writefile(location+"/"+entryname, data)

                self.log("Wrote media file: "+location+"/"+entryname)

        if len(wp):

            for k in wp:

                self.log("Unable to put %s on the phone as there weren't any spare index entries" % (wp[k]['name'],))

        del results[mediakey] 

        reindexfunction(results)

        return results

	my_model='AX3200'
	"Talk to the LG VX3200 cell phone"
parentprofile=com_lgvx4400.Profile
class  Profile (parentprofile) :
	protocolclass=Phone.protocolclass
	    serialsname=Phone.serialsname
	    phone_manufacturer='LG Electronics Inc'
	    phone_model='VX3200'
	    phone_manufacturer='LG Electronics Inc.'
	    phone_model='VX3200 107'
	    usbids=com_lgvx4400.Profile.usbids_usbtoserial
	    def convertphonebooktophone(self, helper, data):

        """Converts the data to what will be used by the phone
        @param data: contains the dict returned by getfundamentals
                     as well as where the results go"""

        results={}

        speeds={}

        self.normalisegroups(helper, data)

        for pbentry in data['phonebook']:

            if len(results)==self.protocolclass.NUMPHONEBOOKENTRIES:

                break

            e={} 

            entry=data['phonebook'][pbentry] 

            try:

                serial1=helper.getserial(entry.get('serials', []), self.serialsname, data['uniqueserial'], 'serial1', 0)

                serial2=helper.getserial(entry.get('serials', []), self.serialsname, data['uniqueserial'], 'serial2', serial1)

                e['serial1']=serial1

                e['serial2']=serial2

                for ss in entry["serials"]:

                    if ss["sourcetype"]=="bitpim":

                        e['bitpimserial']=ss

                assert e['bitpimserial']

                e['name']=helper.getfullname(entry.get('names', []),1,1,22)[0]

                cat=helper.makeone(helper.getcategory(entry.get('categories', []),0,1,22), None)

                if cat is None:

                    e['group']=0

                else:

                    key,value=self._getgroup(cat, data['groups'])

                    if key is not None:

                        if key>5:

                            e['group']=0

                            print "Custom Groups in PB not supported - setting to No Group for "+e['name']

                        else:

                            e['group']=key

                    else:

                        e['group']=0

                emails=helper.getemails(entry.get('emails', []) ,0,self.protocolclass.NUMEMAILS,48)

                e['emails']=helper.filllist(emails, self.protocolclass.NUMEMAILS, "")

                e['url']=helper.makeone(helper.geturls(entry.get('urls', []), 0,1,48), "")

                e['memo']=helper.makeone(helper.getmemos(entry.get('memos', []), 0, 1, self.protocolclass.MEMOLENGTH-1), "")

                minnumbers=1

                if len(emails): minnumbers=0

                numbers=helper.getnumbers(entry.get('numbers', []),minnumbers,self.protocolclass.NUMPHONENUMBERS)

                e['numbertypes']=[]

                e['numbers']=[]

                for numindex in range(len(numbers)):

                    num=numbers[numindex]

                    b4=len(e['numbertypes'])

                    type=num['type']

                    for i,t in enumerate(self.protocolclass.numbertypetab):

                        if type==t:

                            if i in e['numbertypes'] and t[-1]!='2':

                                type+='2'

                                continue

                            e['numbertypes'].append(i)

                            break

                        if t=='none': 

                            e['numbertypes'].append(i)

                            break

                    if len(e['numbertypes'])==b4:

                        continue 

                    number=self.phonize(num['number'])

                    if len(number)==0:

                        continue

                    if len(number)>48: 

                        number=number[:48] 

                    e['numbers'].append(number)

                    sd=num.get("speeddial", -1)

                    if self.protocolclass.NUMSPEEDDIALS:

                        if sd>=self.protocolclass.FIRSTSPEEDDIAL and sd<=self.protocolclass.LASTSPEEDDIAL:

                            speeds[sd]=(e['bitpimserial'], numindex)

                e['numbertypes']=helper.filllist(e['numbertypes'], 5, 0)

                e['numbers']=helper.filllist(e['numbers'], 5, "")

                ecring=helper.getringtone(entry.get('ringtones', []), 'call', None)

                if ecring is not None:

                    if ecring not in Phone.builtinringtones:

                        print "Ringers past Carol 2 in PB not supported - setting to Default Ringer for "+e['name']+" id was: "+ecring

                        ecring=None

                e['ringtone']=ecring

                emring=helper.getringtone(entry.get('ringtones', []), 'message', None)

                if emring is not None:

                    if emring not in Phone.builtinringtones:

                        print "Ringers past Carol 2 in PB not supported - setting to Default MsgRinger for "+e['name']+" id was: "+emring

                        emring=None

                e['msgringtone']=emring

                ewall=helper.getwallpaper(entry.get('wallpapers', []), 'call', None)

                if ewall is not None:

                    print "Custom Wallpapers in PB not supported - setting to Default Wallpaper for "+e['name']

                e['wallpaper']=None

                e['secret']=helper.getflag(entry.get('flags',[]), 'secret', False)

                results[pbentry]=e

            except helper.ConversionFailed:

                continue

        if self.protocolclass.NUMSPEEDDIALS:

            data['speeddials']=speeds

        data['phonebook']=results

        return data

	_supportedsyncs=(
        ('phonebook', 'read', None),  
        ('calendar', 'read', None),   
        ('wallpaper', 'read', None),  
        ('ringtone', 'read', None),   
        ('phonebook', 'write', 'OVERWRITE'),  
        ('calendar', 'write', 'OVERWRITE'),   
        ('wallpaper', 'write', 'OVERWRITE'),   
        ('ringtone', 'write', 'MERGE'),      
        ('ringtone', 'write', 'OVERWRITE'),
        ('call_history', 'read', None),
        ('memo', 'read', None),     
        ('memo', 'write', 'OVERWRITE'),  
        ('sms', 'read', None),
        ('sms', 'write', 'OVERWRITE'),
        )
	    WALLPAPER_WIDTH=128
	    WALLPAPER_HEIGHT=128
	    MAX_WALLPAPER_BASENAME_LENGTH=19
	    WALLPAPER_FILENAME_CHARS="abcdefghijklmnopqrstuvwxyz0123456789 ."
	    WALLPAPER_CONVERT_FORMAT="bmp"
	    MAX_RINGTONE_BASENAME_LENGTH=19
	    RINGTONE_FILENAME_CHARS="abcdefghijklmnopqrstuvxwyz0123456789 ."
	    imageorigins={}
	    imageorigins.update(common.getkv(parentprofile.stockimageorigins, "images"))
	    def GetImageOrigins(self):

        return self.imageorigins

	imagetargets={}
	    imagetargets.update(common.getkv(parentprofile.stockimagetargets, "wallpaper",
                                      {'width': 128, 'height': 128, 'format': "BMP"}))
	    def GetTargetsForImageOrigin(self, origin):

        return self.imagetargets

	def __init__(self):

        parentprofile.__init__(self)

	    imageorigins.update(common.getkv(parentprofile.stockimageorigins, "images"))
	    imagetargets.update(common.getkv(parentprofile.stockimagetargets, "wallpaper",
                                      {'width': 128, 'height': 128, 'format': "BMP"}))

