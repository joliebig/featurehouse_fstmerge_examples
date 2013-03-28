"""Communicate with the LG VX8100 cell phone
The VX8100 is similar to the VX7000 but also supports video.
"""
import re
import time
import cStringIO
import sha
import common
import commport
import copy
import com_lgvx4400
import p_brew
import p_lgvx8100
import com_lgvx7000
import com_brew
import com_phone
import com_lg
import prototypes
import bpcalendar
import call_history
import sms
import memo
from prototypes import *
class Phone(com_lg.LGNewIndexedMedia2,com_lgvx7000.Phone):
    "Talk to the LG VX8100 cell phone"
    desc="LG-VX8100"
    protocolclass=p_lgvx8100
    serialsname='lgvx8100'
    builtinringtones= ('Low Beep Once', 'Low Beeps', 'Loud Beep Once', 'Loud Beeps', 'VZW Default Ringtone') + \
                      tuple(['Ringtone '+`n` for n in range(1,11)]) + \
                      ('No Ring',)
    ringtonelocations= (
        ( 'ringers', 'dload/my_ringtone.dat', 'dload/my_ringtonesize.dat', 'brew/16452/lk/mr', 100, 150, 0x201, 1, 0),
        ( 'sounds', 'dload/mysound.dat', 'dload/mysoundsize.dat', 'brew/16452/ms', 100, 150, 2, 0, 151),
        )
    calendarlocation="sch/newschedule.dat"
    calendarexceptionlocation="sch/newschexception.dat"
    calenderrequiresreboot=0
    memolocation="sch/neomemo.dat"
    builtinwallpapers = () # none
    wallpaperlocations= (
        ( 'images', 'dload/image.dat', 'dload/imagesize.dat', 'brew/16452/mp', 100, 50, 0, 0, 0),
        ( 'video', 'dload/video.dat', None, 'brew/16452/mf', 1000, 50, 0x0304, 0, 0),
        )
    _rs_path='mmc1/'
    _rs_ringers_path=_rs_path+'ringers'
    _rs_images_path=_rs_path+'images'
    media_info={ 'ringers': {
            'localpath': 'brew/16452/lk/mr',
            'rspath': _rs_ringers_path,
            'vtype': protocolclass.MEDIA_TYPE_RINGTONE,
            'icon': protocolclass.MEDIA_RINGTONE_DEFAULT_ICON,
            'index': 100,  # starting index
            'maxsize': 155,
            'indexfile': 'dload/my_ringtone.dat',
            'sizefile': 'dload/my_ringtonesize.dat',
            'dunno': 0, 'date': False,
        },
         'sounds': {
             'localpath': 'brew/16452/ms',
             'rspath': None,
             'vtype': protocolclass.MEDIA_TYPE_SOUND,
             'icon': protocolclass.MEDIA_IMAGE_DEFAULT_ICON,
             'index': 100,
             'maxsize': 155,
             'indexfile': 'dload/mysound.dat',
             'sizefile': 'dload/mysoundsize.dat',
             'dunno': 0, 'date': False },
         'images': {
             'localpath': 'brew/16452/mp',
             'rspath': _rs_images_path,
             'vtype': protocolclass.MEDIA_TYPE_IMAGE,
             'icon': protocolclass.MEDIA_IMAGE_DEFAULT_ICON,
             'index': 100,
             'maxsize': 155,
             'indexfile': 'dload/image.dat',
             'sizefile': 'dload/imagesize.dat',
             'dunno': 0, 'date': False },
         'video': {
             'localpath': 'brew/16452/mf',
             'rspath': None,
             'vtype': protocolclass.MEDIA_TYPE_VIDEO,
             'icon': protocolclass.MEDIA_VIDEO_DEFAULT_ICON,
             'index': 1000,
             'maxsize': 155,
             'indexfile': 'dload/video.dat',
             'sizefile': 'dload/videosize.dat',
             'dunno': 0, 'date': True },
         }
    def __init__(self, logtarget, commport):
        com_lgvx4400.Phone.__init__(self, logtarget, commport)
        self.mode=self.MODENONE
    def getfundamentals(self, results):
        """Gets information fundamental to interopating with the phone and UI.
        Currently this is:
          - 'uniqueserial'     a unique serial number representing the phone
          - 'groups'           the phonebook groups
          - 'wallpaper-index'  map index numbers to names
          - 'ringtone-index'   map index numbers to ringtone names
        This method is called before we read the phonebook data or before we
        write phonebook data.
        """
        self.log("Retrieving fundamental phone information")
        self.log("Phone serial number")
        results['uniqueserial']=sha.new(self.getfilecontents("nvm/$SYS.ESN")).hexdigest()
        self.log("Reading group information")
        buf=prototypes.buffer(self.getfilecontents("pim/pbgroup.dat"))
        g=self.protocolclass.pbgroups()
        g.readfrombuffer(buf, logtitle="Groups read")
        groups={}
        for i in range(len(g.groups)):
            if len(g.groups[i].name): # sometimes have zero length names
                groups[i]={'name': g.groups[i].name }
        results['groups']=groups
        self.getwallpaperindices(results)
        self.getringtoneindices(results)
        self.log("Fundamentals retrieved")
        return results
    def savegroups(self, data):
        groups=data['groups']
        keys=groups.keys()
        keys.sort()
        g=self.protocolclass.pbgroups()
        for k in keys:
            e=self.protocolclass.pbgroup()
            e.name=groups[k]['name']
            g.groups.append(e)
        buffer=prototypes.buffer()
        g.writetobuffer(buffer, logtitle="New group file")
        self.writefile("pim/pbgroup.dat", buffer.getvalue())
    def getmemo(self, result):
        res={}
        try:
            stat_res=self.statfile(self.memolocation)
            if stat_res!=None and stat_res['size']!=0:
                buf=prototypes.buffer(self.getfilecontents(self.memolocation))
                text_memo=self.protocolclass.textmemofile()
                text_memo.readfrombuffer(buf, logtitle="Read memo file"+self.memolocation)
                for m in text_memo.items:
                    entry=memo.MemoEntry()
                    entry.text=m.text
                    try:
                        entry.set_date_isostr("%d%02d%02dT%02d%02d00" % ((m.memotime)))
                    except ValueError:
                        continue
                    res[entry.id]=entry
        except com_brew.BrewNoSuchFileException:
            pass
        result['memo']=res
        return result
    def savememo(self, result, merge):
        text_memo=self.protocolclass.textmemofile()
        memo_dict=result.get('memo', {})
        keys=memo_dict.keys()
        keys.sort()
        text_memo.itemcount=len(keys)
        for k in keys:
            entry=self.protocolclass.textmemo()
            entry.text=memo_dict[k].text
            t=time.strptime(memo_dict[k].date, '%b %d, %Y %H:%M')
            entry.memotime=(t.tm_year, t.tm_mon, t.tm_mday, t.tm_hour, t.tm_min)
            if 'GPStime' in entry.getfields():
                entry.GPStime = (t.tm_year, t.tm_mon, t.tm_mday, t.tm_hour, t.tm_min, t.tm_sec)    
            text_memo.items.append(entry)
        buf=prototypes.buffer()
        text_memo.writetobuffer(buf, logtitle="Updated memo "+self.memolocation)
        self.writefile(self.memolocation, buf.getvalue())
        return result
    def getcalendar(self,result):
        res={}
        try:
            buf=prototypes.buffer(self.getfilecontents(self.calendarexceptionlocation))
            ex=self.protocolclass.scheduleexceptionfile()
            ex.readfrombuffer(buf, logtitle="Calendar exceptions")
            exceptions={}
            for i in ex.items:
                try:
                    exceptions[i.pos].append( (i.year,i.month,i.day) )
                except KeyError:
                    exceptions[i.pos]=[ (i.year,i.month,i.day) ]
        except com_brew.BrewNoSuchFileException:
            exceptions={}
        try:
            buf=prototypes.buffer(self.getfilecontents(self.calendarlocation))
            if len(buf.getdata())<3:
                raise com_brew.BrewNoSuchFileException()
            sc=self.protocolclass.schedulefile()
            sc.readfrombuffer(buf, logtitle="Calendar")
            for event in sc.events:
                if event.pos==0: #invalid entry
                    continue                   
                entry=bpcalendar.CalendarEntry()
                entry.desc_loc=event.description
                try: # delete events are still in the calender file but have garbage dates
                    entry.start=event.start
                    entry.end=event.end
                except ValueError:
                    continue
                if event.alarmindex_vibrate&0x1:
                    entry.vibrate=0 # vibarate bit is inverted in phone 0=on, 1=off
                else:
                    entry.vibrate=1
                entry.repeat = self.makerepeat(event.repeat)
                min=event.alarmminutes
                hour=event.alarmhours
                if min==0x64 or hour==0x64:
                    entry.alarm=None # no alarm set
                else:
                    entry.alarm=hour*60+min
                entry.ringtone=result['ringtone-index'][event.ringtone]['name']
                entry.snoozedelay=0
                if event.repeat[3] and exceptions.has_key(event.pos):
                    for year, month, day in exceptions[event.pos]:
                        entry.suppress_repeat_entry(year, month, day)
                res[entry.id]=entry
            assert sc.numactiveitems==len(res)
        except com_brew.BrewNoSuchFileException:
            pass # do nothing if file doesn't exist
        result['calendar']=res
        return result
    def makerepeat(self, repeat):
        type,dow,interval,interval2,exceptions=repeat
        if type==0:
            repeat_entry=None
        else:
            repeat_entry=bpcalendar.RepeatEntry()
            if type==1: #daily
                repeat_entry.repeat_type=repeat_entry.daily
                repeat_entry.interval=interval
            elif type==5: #'monfri'
                repeat_entry.repeat_type=repeat_entry.daily
                repeat_entry.interval=0
            elif type==2: #'weekly'
                repeat_entry.repeat_type=repeat_entry.weekly
                repeat_entry.dow=dow
                repeat_entry.interval=interval
            elif type==3: #'monthly'
                repeat_entry.repeat_type=repeat_entry.monthly
                repeat_entry.interval2=interval2
                repeat_entry.dow=0
            elif type==6: #'monthly' #Xth Y day (e.g. 2nd friday each month)
                repeat_entry.repeat_type=repeat_entry.monthly
                repeat_entry.interval=interval #X
                repeat_entry.dow=dow #Y
                repeat_entry.interval2=interval2
            else: # =4 'yearly'
                repeat_entry.repeat_type=repeat_entry.yearly
        return repeat_entry
    def savecalendar(self, dict, merge):
        eventsf=self.protocolclass.schedulefile()
        exceptionsf=self.protocolclass.scheduleexceptionfile()
        cal=dict['calendar']
        newcal={}
        keys=[(x.start, k) for k,x in cal.items()]
        keys.sort()
        eventsf.numactiveitems=len(keys)
        pos=1
        contains_alarms=False        
        for (_,k) in keys:
            entry=cal[k]
            data=self.protocolclass.scheduleevent()
            data.pos=eventsf.packetsize()
            data.description=entry.desc_loc
            data.start=entry.start
            data.end=entry.end
            alarm_set=self.setalarm(entry, data)
            data.ringtone=0
            if alarm_set:
                if entry.ringtone=="No Ring" and not entry.vibrate:
                    alarm_name="Low Beep Once"
                else:
                    alarm_name=entry.ringtone
            else:# set alarm to "No Ring" gets rid of alarm icon on phone
                alarm_name="No Ring"
            for i in dict['ringtone-index']:
                if dict['ringtone-index'][i]['name']==alarm_name:
                    data.ringtone=i
            exceptions=0
            if entry.repeat!=None:
                if data.end[:3]==entry.no_end_date:
                    data.end=(2100, 12, 31)+data.end[3:]
                for i in entry.repeat.suppressed:
                    de=self.protocolclass.scheduleexception()
                    de.pos=data.pos
                    de.day=i.date.day
                    de.month=i.date.month
                    de.year=i.date.year
                    exceptions=1
                    exceptionsf.items.append(de)
            if entry.repeat != None:
                data.repeat=(self.getrepeattype(entry, exceptions))
            else:
                data.repeat=((0,0,0,0,0))
            data.unknown1=0
            data.unknown2=0
            if data.alarmindex_vibrate!=1: # if alarm set
                contains_alarms=True
            entry=copy.copy(entry)
            newcal[data.pos]=entry
            eventsf.events.append(data)
        buf=prototypes.buffer()
        eventsf.writetobuffer(buf, logtitle="New Calendar")
        if buf.getvalue()!=self.getfilecontents(self.calendarlocation):
            self.writefile(self.calendarlocation, buf.getvalue())
            if contains_alarms or self.calenderrequiresreboot:
                self.log("Your phone has to be rebooted due to the calendar changing")
                dict["rebootphone"]=True
        else:
            self.log("Phone calendar unchanged, no update required")
        buf=prototypes.buffer()
        exceptionsf.writetobuffer(buf, logtitle="Writing calendar exceptions")
        self.writefile(self.calendarexceptionlocation, buf.getvalue())
        dict['calendar']=newcal
        return dict
    def getrepeattype(self, entry, exceptions):
        repeat_entry=bpcalendar.RepeatEntry()
        interval2=0
        if entry.repeat.repeat_type==repeat_entry.monthly:
            dow=entry.repeat.dow
            interval2=entry.repeat.interval2
            if entry.repeat.dow==0:
                interval=entry.start[2]
                type=3
            else:
                interval=entry.repeat.interval
                type=6
        elif entry.repeat.repeat_type==repeat_entry.daily:
            dow=entry.repeat.dow
            interval=entry.repeat.interval
            if entry.repeat.interval==0:
                type=5
            else:
                type=1
        elif entry.repeat.repeat_type==repeat_entry.weekly:
            dow=entry.repeat.dow
            interval=entry.repeat.interval
            type=2
        elif entry.repeat.repeat_type==repeat_entry.yearly:
            interval=entry.start[2]
            dow=entry.start[1]
            type=4
        return (type, dow, interval, interval2, exceptions)
    def setalarm(self, entry, data):
        rc=True
        if entry.alarm>=2880:
            entry.alarm=2880
            data.alarmminutes=0
            data.alarmhours=48
            data.alarmindex_vibrate=0x10
        elif entry.alarm>=1440:
            entry.alarm=1440
            data.alarmminutes=0
            data.alarmhours=24
            data.alarmindex_vibrate=0xe
        elif entry.alarm>=120:
            entry.alarm=120
            data.alarmminutes=0
            data.alarmhours=2
            data.alarmindex_vibrate=0xc
        elif entry.alarm>=60:
            entry.alarm=60
            data.alarmminutes=0
            data.alarmhours=1
            data.alarmindex_vibrate=0xa
        elif entry.alarm>=15:
            entry.alarm=15
            data.alarmminutes=15
            data.alarmhours=0
            data.alarmindex_vibrate=0x8
        elif entry.alarm>=10:
            entry.alarm=10
            data.alarmminutes=10
            data.alarmhours=0
            data.alarmindex_vibrate=0x6
        elif entry.alarm>=5:
            entry.alarm=5
            data.alarmminutes=5
            data.alarmhours=0
            data.alarmindex_vibrate=0x4
        elif entry.alarm>=0:
            entry.alarm=0
            data.alarmminutes=0
            data.alarmhours=0
            data.alarmindex_vibrate=0x2
        else: # no alarm
            data.alarmminutes=0x64
            data.alarmhours=0x64
            data.alarmindex_vibrate=1
            rc=False
        if data.alarmindex_vibrate > 1 and entry.vibrate==0:
            data.alarmindex_vibrate+=1
        return rc
    my_model='VX8100'
    def getphoneinfo(self, phone_info):
        self.log('Getting Phone Info')
        try:
            s=self.getfilecontents('brew/version.txt')
            if s[:6]==self.my_model:
                phone_info.append('Model:', self.my_model)
                phone_info.append('ESN:', self.get_brew_esn())
                req=p_brew.firmwarerequest()
                res=self.sendbrewcommand(req, self.protocolclass.firmwareresponse)
                phone_info.append('Firmware Version:', res.firmware)
                txt=self.getfilecontents("nvm/nvm/nvm_cdma")[180:190]
                phone_info.append('Phone Number:', txt)
        except:
            pass
        return
    def _is_rs_file(self, filename):
        return filename.startswith(self._rs_path)
    def getmedia(self, maps, results, key):
        media={}
        for type, indexfile, sizefile, directory, lowestindex, maxentries, typemajor, def_icon, idx_ofs  in maps:
            for item in self.getindex(indexfile):
                try:
                    if not self._is_rs_file(item.filename):
                        media[common.basename(item.filename)]=self.getfilecontents(
                            item.filename, True)
                except (com_brew.BrewNoSuchFileException,com_brew.BrewBadPathnameException,com_brew.BrewNameTooLongException):
                    self.log("It was in the index, but not on the filesystem")
                except com_brew.BrewAccessDeniedException:
                    self.log('Failed to read file: '+item.filename)
                    media[common.basename(item.filename)]=''
        results[key]=media
        return results
    def _write_index_file(self, type):
        _info=self.media_info.get(type, None)
        if not _info:
            return
        _files={}
        _local_dir=_info['localpath']
        _rs_dir=_info['rspath']
        _vtype=_info['vtype']
        _icon=_info['icon']
        _index=_info['index']
        _maxsize=_info['maxsize']
        _dunno=_info['dunno']
        indexfile=_info['indexfile']
        sizefile=_info['sizefile']
        _need_date=_info['date']
        try:
            _files=self.listfiles(_local_dir)
        except (com_brew.BrewNoSuchDirectoryException,
                com_brew.BrewBadPathnameException):
            pass
        try:
            if _rs_dir:
                _files.update(self.listfiles(_rs_dir))
        except (com_brew.BrewNoSuchDirectoryException,
                com_brew.BrewBadPathnameException):
            pass
        _idx_keys={}
        for _i in xrange(_index, _index+_maxsize):
            _idx_keys[_i]=True
        for _item in self.getindex(indexfile):
            if _files.has_key(_item.filename):
                _files[_item.filename]['index']=_item.index
                _idx_keys[_item.index]=False
        _idx_keys_list=[k for k,x in _idx_keys.items() if x]
        _idx_keys_list.sort()
        _idx_cnt=0
        _file_list=[x for x in _files if not _files[x].get('index', None)]
        _file_list.sort()
        if len(_file_list)>len(_idx_keys_list):
            _file_list=_file_list[:len(_idx_keys_list)]
        for i in _file_list:
            _files[i]['index']=_idx_keys_list[_idx_cnt]
            _idx_cnt+=1
        _res_list=[(x['index'],k) for k,x in _files.items() if x.get('index', None)]
        _res_list.sort()
        _res_list.reverse()
        ifile=self.protocolclass.indexfile()
        _file_size=0
        for index,idx in _res_list:
            _fs_size=_files[idx]['size']
            ie=self.protocolclass.indexentry()
            ie.index=index
            ie.type=_vtype
            ie.filename=idx
            if _need_date:
                _stat=self.statfile(_files[idx]['name'])
                if _stat:
                    ie.date=_stat['datevalue']-time.timezone
            ie.dunno=_dunno
            ie.icon=_icon
            ie.size=_fs_size
            ifile.items.append(ie)
            if not self._is_rs_file(idx):
                _file_size+=_fs_size
        buf=prototypes.buffer()
        ifile.writetobuffer(buf, logtitle="Index file "+indexfile)
        self.log("Writing index file "+indexfile+" for type "+type+" with "+`len(_res_list)`+" entries.")
        self.writefile(indexfile, buf.getvalue())
        if sizefile:
            szfile=self.protocolclass.sizefile()
            szfile.size=_file_size
            buf=prototypes.buffer()
            szfile.writetobuffer(buf, logtitle="Updated size file for "+type)
            self.log("You are using a total of "+`_file_size`+" bytes for "+type)
            self.writefile(sizefile, buf.getvalue())
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
        for type,_,_,_,lowestindex,_,typemajor,_,_ in maps:
            init[type]={}
            for k in wpi.keys():
                if wpi[k]['origin']==type:
                    index=k
                    name=wpi[k]['name']
                    fullname=wpi[k]['filename']
                    vtype=wpi[k]['vtype']
                    icon=wpi[k]['icon']
                    data=None
                    del wpi[k]
                    for w in wp.keys():
                        if wp[w]['name']==name:
                            data=wp[w]['data']
                            del wp[w]
                    if not merge and data is None:
                        continue
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
        for type, indexfile, sizefile, directory, lowestindex, maxentries,typemajor,def_icon,_  in maps:
            names=[init[type][x]['name'] for x in init[type]]
            for item in self.getindex(indexfile):
                if common.basename(item.filename) not in names and \
                   not self._is_rs_file(item.filename):
                    self.log(item.filename+" is being deleted")
                    self.rmfile(item.filename)
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
                fstat=self.statfile(filename)
                if 'data' not in entry:
                    if fstat is None:
                        self.log("Entry "+entry['name']+" is in index "+indexfile+" but there is no data for it and it isn't in the filesystem.  The index entry will be removed.")
                        del init[type][idx]
                        continue
                data=entry['data']
                if data is None:
                    assert merge 
                    continue # we are doing an add and don't have data for this existing entry
                elif not data:
                    self.log('Not writing file: '+filename)
                    continue # data is blank, could be a result of not being able to do a previous get
                if fstat is not None and len(data)==fstat['size']:
                    self.log("Not writing "+filename+" as a file of the same name and length already exists.")
                else:
                    self.writefile(filename, data)
            self._write_index_file(type)
        return reindexfunction(results)
parentprofile=com_lgvx7000.Profile
class Profile(parentprofile):
    protocolclass=Phone.protocolclass
    serialsname=Phone.serialsname
    BP_Calendar_Version=3
    phone_manufacturer='LG Electronics Inc'
    phone_model='VX8100'
    WALLPAPER_WIDTH=160
    WALLPAPER_HEIGHT=120
    MAX_WALLPAPER_BASENAME_LENGTH=32
    WALLPAPER_FILENAME_CHARS="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789()_ .-"
    WALLPAPER_CONVERT_FORMAT="jpg"
    DIALSTRING_CHARS="[^0-9PW#*]"
    MAX_RINGTONE_BASENAME_LENGTH=32
    RINGTONE_FILENAME_CHARS="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789()_ .-"
    bluetooth_mfg_id="001256"
    imageorigins={}
    imageorigins.update(common.getkv(parentprofile.stockimageorigins, "images"))
    def GetImageOrigins(self):
        return self.imageorigins
    imagetargets={}
    imagetargets.update(common.getkv(parentprofile.stockimagetargets, "wallpaper",
                                      {'width': 320, 'height': 240, 'format': "JPEG"}))
    def GetTargetsForImageOrigin(self, origin):
        return self.imagetargets
    def __init__(self):
        parentprofile.__init__(self)
    _supportedsyncs=(
        ('phonebook', 'read', None),   # all phonebook reading
        ('calendar', 'read', None),    # all calendar reading
        ('wallpaper', 'read', None),   # all wallpaper reading
        ('ringtone', 'read', None),    # all ringtone reading
        ('call_history', 'read', None),# all call history list reading
        ('sms', 'read', None),         # all SMS list reading
        ('memo', 'read', None),        # all memo list reading
        ('phonebook', 'write', 'OVERWRITE'),  # only overwriting phonebook
        ('calendar', 'write', 'OVERWRITE'),   # only overwriting calendar
        ('wallpaper', 'write', 'MERGE'),      # merge and overwrite wallpaper
        ('wallpaper', 'write', 'OVERWRITE'),
        ('ringtone', 'write', 'MERGE'),       # merge and overwrite ringtone
        ('ringtone', 'write', 'OVERWRITE'),
        ('sms', 'write', 'OVERWRITE'),        # all SMS list writing
        ('memo', 'write', 'OVERWRITE'),       # all memo list writing
        )
    field_color_data={
        'phonebook': {
            'name': {
                'first': 1, 'middle': 1, 'last': 1, 'full': 1,
                'nickname': 0, 'details': 1 },
            'number': {
                'type': 5, 'speeddial': 5, 'number': 5, 'details': 5 },
            'email': 2,
            'address': {
                'type': 0, 'company': 0, 'street': 0, 'street2': 0,
                'city': 0, 'state': 0, 'postalcode': 0, 'country': 0,
                'details': 0 },
            'url': 0,
            'memo': 1,
            'category': 1,
            'wallpaper': 1,
            'ringtone': 2,
            'storage': 0,
            },
        'calendar': {
            'description': True, 'location': True, 'allday': True,
            'start': True, 'end': True, 'priority': False,
            'alarm': True, 'vibrate': True,
            'repeat': True,
            'memo': False,
            'category': False,
            'wallpaper': False,
            'ringtone': True,
            },
        'memo': {
            'subject': True,
            'date': True,
            'secret': False,
            'category': False,
            'memo': True,
            },
        'todo': {
            'summary': False,
            'status': False,
            'due_date': False,
            'percent_complete': False,
            'completion_date': False,
            'private': False,
            'priority': False,
            'category': False,
            'memo': False,
            },
        }
