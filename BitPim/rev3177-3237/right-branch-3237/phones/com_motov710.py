"""Communicate with Motorola phones using AT commands"""
import datetime
import sha
import bpcalendar
import common
import commport
import com_brew
import com_moto
import fileinfo
import nameparser
import prototypes
import p_motov710
class Phone(com_moto.Phone):
    """ Talk to a Motorola V710 phone"""
    desc='Moto-V710'
    protocolclass=p_motov710
    serialsname='motov710'
    builtinringtones=(
        (0, ('Silent',)),
        (5, ('Vibe Dot', 'Vibe Dash', 'Vibe Dot Dot', 'Vibe Dot Dash',
             'Vibe Pulse')),
        (11, ('Alert', 'Standard', 'Bells', 'Triads', 'Up and Down',
              'Jitters', 'Upbeat')),
        (22, ('Guitar Strings', 'High Impact')),
        (30, ('Moonlit Haze', 'Nightlife', 'Wind Chime', 'Random',
              'Bit & Bytes', 'Door Bell', 'Ding', 'One Moment', 'Provincial',
              'Harmonics', 'Interlude', 'Snaggle', 'Cosmic', 'Gyroscope')),
        (49, ('Chimes high', 'Chimes low', 'Ding', 'TaDa', 'Notify', 'Drum',
              'Claps', 'Fanfare', 'Chord high', 'Chord low'))
        )
    def __init__(self, logtarget, commport):
        com_moto.Phone.__init__(self, logtarget, commport)
    def _get_groups(self):
        _req=self.protocolclass.read_group_req()
        _res=self.sendATcommand(_req, self.protocolclass.read_group_resp)
        res={}
        for e in _res:
            res[e.index]={ 'name': e.name, 'ringtone': e.ringtone }
        return res
    def _save_groups(self, fundamentals):
        """Save the Group(Category) data"""
        _groups=fundamentals.get('groups', {})
        _name2ringtone={}
        for _key,_entry in _groups.items():
            _name2ringtone[_entry['name']]=_entry['ringtone']
        _keys=_groups.keys()
        _keys.sort()
        _group_list=[_groups[x]['name'] for x in _keys]
        _cats=fundamentals.get('categories', [])
        if 'General' not in _cats:
            _cats.append('General')
        _new_list=[x for x in _group_list if x in _cats]
        _new_list+=[x for x in _cats if x not in _group_list]
        _new_group={}
        for _idx,_entry in enumerate(_new_list):
            _new_group[_idx+1]={ 'name': _entry,
                                 'ringtone': _name2ringtone.get(_entry, None) }
        _rt_name_index=fundamentals.get('ringtone-name-index', {})
        _req=self.protocolclass.read_group_req()
        _res=self.sendATcommand(_req, self.protocolclass.read_group_resp)
        _req=self.protocolclass.del_group_req()
        for e in _res:
            if e.index==1:  # Group 'General': can't delete, add, or modify
                continue
            _req.index=e.index
            self.sendATcommand(_req, None)
        _req=self.protocolclass.write_group_req()
        for _key,_entry in _new_group.items():
            if _key==1:
                continue
            _req.index=_key
            _req.name=_entry['name']
            _req.ringtone=_rt_name_index.get(_entry.get('ringtone', None), 255)
            self.sendATcommand(_req, None)
        fundamentals['groups']=_new_group
    def _get_ringtone_index(self):
        res={}
        for _l in self.builtinringtones:
            _idx=_l[0]
            for _e in _l[1]:
                res[_idx]={ 'name': _e, 'origin': 'builtin' }
                _idx+=1
        _buf=prototypes.buffer(self.getfilecontents(
            self.protocolclass.RT_INDEX_FILE))
        _idx_file=self.protocolclass.ringtone_index_file()
        _idx_file.readfrombuffer(_buf, logtitle='Read ringtone index file')
        _path_len=len(self.protocolclass.RT_PATH)+1
        for _entry in _idx_file.items:
            _filename=self.decode_utf16(_entry.name)
            res[_entry.index]={ 'name': common.basename(_filename),
                                'filename': _filename,
                                'type': _entry.ringtone_type,
                                'origin': 'ringers' }
        return res
    def _get_wallpaper_index(self):
        res={}
        _files=self.listfiles(self.protocolclass.WP_PATH).keys()
        _files.sort()
        _wp_path_len=len(self.protocolclass.WP_PATH)+1
        for _index,_name in enumerate(_files):
            res[_index]={ 'name': common.basename(_name),
                          'filename': _name,
                          'origin': 'images' }
        return res
    def _populate_pb_misc(self, pb_entry, pb_sub_entry, key_name,
                          entry, fundamentals):
        """Populate ringtone, wallpaper to a number, email, or mail list
        """
        _rt_index=fundamentals.get('ringtone-index', {})
        _rt_name=_rt_index.get(entry.ringtone, {}).get('name', None)
        if _rt_name:
            pb_sub_entry['ringtone']=_rt_name
        if entry.picture_name:
            pb_sub_entry['wallpaper']=common.basename(entry.picture_name)
        if entry.is_primary:
            pb_entry[key_name]=[pb_sub_entry]+pb_entry.get(key_name, [])
        else:
            pb_entry.setdefault(key_name, []).append(pb_sub_entry)
    def _populate_pb_number(self, pb_entry, entry, fundamentals):
        """extract the number into BitPim phonebook entry"""
        _number_type=self.protocolclass.NUMBER_TYPE_NAME.get(entry.number_type, None)
        _number={ 'number': entry.number, 'type': _number_type,
                  'speeddial': entry.index }
        self._populate_pb_misc(pb_entry, _number, 'numbers', entry,
                               fundamentals)
        fundamentals['sd_dict'][entry.index]=entry.number
    def _populate_pb_email(self, pb_entry, entry, fundamentals):
        """Extract the email component"""
        _email={ 'email': entry.number,
                 'speeddial': entry.index }
        self._populate_pb_misc(pb_entry, _email, 'emails', entry,
                               fundamentals)
        fundamentals['sd_dict'][entry.index]=entry.number
    def _populate_pb_maillist(self, pb_entry, entry, fundamentals):
        """Extract the mailing list component"""
        _num_list=entry.number.split(' ')
        for _idx,_entry in enumerate(_num_list):
            _num_list[_idx]=int(_entry)
        _maillist={ 'entry': _num_list,
                    'speeddial': entry.index }
        self._populate_pb_misc(pb_entry, _maillist, 'maillist', entry,
                               fundamentals)
        fundamentals['sd_dict'][entry.index]=entry.number
    def _populate_pb_entry(self, pb_entry, entry, fundamentals):
        """Populate a BitPim phonebook entry with one from the phone
        """
        _num_type=entry.number_type
        if _num_type<self.protocolclass.NUMBER_TYPE_EMAIL:
            self._populate_pb_number(pb_entry, entry, fundamentals)
        elif _num_type==self.protocolclass.NUMBER_TYPE_EMAIL:
            self._populate_pb_email(pb_entry, entry, fundamentals)
    def _build_pb_entry(self, entry, pb_book, fundamentals):
        """Build a BitPim phonebook entry based on phone data.
        """
        try:
            _idx=fundamentals['pb_list'].index(entry.name)
        except ValueError:
            _idx=None
        if _idx is None:
            _idx=len(fundamentals['pb_list'])
            fundamentals['pb_list'].append(entry.name)
            _group=fundamentals.get('groups', {}).get(entry.group, None)
            pb_book[_idx]={ 'names': [{ 'full': entry.name }] }
            if _group.get('name', None):
                pb_book[_idx]['categories']=[{'category': _group['name'] }]
        self._populate_pb_entry(pb_book[_idx], entry, fundamentals)
    def _update_a_mail_list(self, entry, sd_dict):
        for _entry in entry['maillist']:
            _name_list=[]
            for m in _entry['entry']:
                if sd_dict.has_key(m):
                    _name_list.append(sd_dict[m])
                _entry['entry']='\x00\x00'.join(_name_list)
    def _update_mail_list(self, pb_book, fundamentals):
        """Translate the contents of each mail list from speed-dial
        into the corresponding names or numbers.
        """
        _sd_dict=fundamentals.get('sd_dict', {})
        for _key,_entry in pb_book.items():
            if _entry.has_key('maillist'):
                self._update_a_mail_list(_entry, _sd_dict)
    def _del_pb_entry(self, entry_index):
        """Delete the phonebook entry index from the phone"""
        _req=self.protocolclass.del_pb_req()
        _req.index=entry_index
        try:
            self.sendATcommand(_req, None)
        except:
            self.log('Failed to delete contact index %d'%entry_index)
            if __debug__:
                raise
    def _get_group_code(self, entry, fundamentals):
        """Return the group index of the group.  Return 1(General) if none found
        """
        _grp_name=entry.get('categories', [{}])[0].get('category', None)
        if not _grp_name:
            return 1
        for _key,_entry in fundamentals.get('groups', {}).items():
            if _entry.get('name', None)==_grp_name:
                return _key
        return 1
    def _get_ringtone_code(self, entry, fundamentals):
        """Return the ringtone code of this entry"""
        _ringtone_name=entry.get('ringtone', None)
        if not _ringtone_name:
            return 255
        for _key,_entry in fundamentals.get('ringtone-index', {}).items():
            if _entry['name']==_ringtone_name:
                return _key
        return 255
    def _get_wallpaper_name(self, entry, fundamentals):
        """Return the full path name for the wallpaper"""
        _wp_name=entry.get('wallpaper', None)
        if not _wp_name:
            return ''
        return '/a/'+self.protocolclass.WP_PATH+'/'+_wp_name
    def _get_primary_code(self, fundamentals):
        if fundamentals['primary']:
            return 0
        fundamentals['primary']=True
        return 1
    def _build_pb_maillist(self, entry, fundamentals):
        """Translate the mail list from text name to indices"""
        _sd_list=fundamentals.get('sd-slots', [])
        _names_list=entry.get('entry', '').split('\x00\x00')
        _codes_list=[]
        for _name in _names_list:
            try:
                _codes_list.append('%d'%_sd_list.index(_name))
            except ValueError:
                pass
        return ' '.join(_codes_list)
    def _set_pb_entry_misc(self, req, entry, fundamentals):
        """Set the ringtone, wallpaper, and primary parameters"""
        req.ringtone=self._get_ringtone_code(entry, fundamentals)
        req.is_primary=self._get_primary_code(fundamentals)
        req.picture_name=self._get_wallpaper_name(entry, fundamentals)
    def _write_pb_entry_numbers(self, entry, req, fundamentals):
        """Write all the numbers to the phone"""
        req.local_type=self.protocolclass.LOCAL_TYPE_LOCAL
        for _entry in entry.get('numbers', []):
            req.index=_entry['speeddial']
            if req.index>self.protocolclass.PB_TOTAL_ENTRIES:
                continue
            req.number=_entry['number']
            req.number_type=self.protocolclass.NUMBER_TYPE_CODE.get(
                _entry['type'], self.protocolclass.NUMBER_TYPE_WORK)
            self._set_pb_entry_misc(req, _entry, fundamentals)
            self._del_pb_entry(req.index)
            self.sendATcommand(req, None)
    def _write_pb_entry_emails(self, entry, req, fundamentals):
        """Write all emails to the phone"""
        req.number_type=self.protocolclass.NUMBER_TYPE_EMAIL
        req.local_type=self.protocolclass.LOCAL_TYPE_UNKNOWN
        for _entry in entry.get('emails', []):
            req.index=_entry['speeddial']
            if req.index>self.protocolclass.PB_TOTAL_ENTRIES:
                continue
            req.number=_entry['email']
            self._set_pb_entry_misc(req, _entry, fundamentals)
            self._del_pb_entry(req.index)
            self.sendATcommand(req, None)
    def _write_pb_entry_maillist(self, entry, req, fundamentals):
        """Write all the mail lists to the phone"""
        req.number_type=self.protocolclass.NUMBER_TYPE_MAILING_LIST
        req.local_type=self.protocolclass.LOCAL_TYPE_UNKNOWN
        for _entry in entry.get('maillist', []):
            req.index=_entry['speeddial']
            if req.index>self.protcolclass.PB_TOTAL_ENTRIES:
                continue
            req.number=self._build_pb_maillist(_entry, fundamentals)
            self._set_pb_entry_misc(req, _entry, fundamentals)
            self._del_pb_entry(req.index)
            self.sendATcommand(req, None)
    def _write_pb_entry(self, entry, fundamentals):
        """Write an phonebook entry to the phone"""
        _req=self.protocolclass.write_pb_req()
        _req.name=nameparser.getfullname(entry['names'][0])
        _req.group=self._get_group_code(entry, fundamentals)
        fundamentals['primary']=False
        self._write_pb_entry_numbers(entry, _req, fundamentals)
        self._write_pb_entry_emails(entry, _req, fundamentals)
        del fundamentals['primary']
    def _write_pb_entries(self, fundamentals):
        """Write out the phonebook to the phone"""
        _pb_book=fundamentals.get('phonebook', {})
        _total_entries=len(_pb_book)
        _cnt=0
        for _key,_entry in _pb_book.items():
            try:
                _name=nameparser.getfullname(_entry['names'][0])
            except:
                _name='<Unknown>'
            _cnt+=1
            self.progress(_cnt, _total_entries,
                          'Writing contact %d: %s'%(_cnt, _name))
            self._write_pb_entry(_entry, fundamentals)
        for _index,_entry in enumerate(fundamentals.get('sd-slots', [])):
            if not _entry:
                self.progress(_index, self.protocolclass.PB_TOTAL_ENTRIES,
                              'Deleting contact slot %d'%_index)
                self._del_pb_entry(_index)
    def _dow(self, ymd):
        """Return a bitmap dayofweek"""
        return 1<<(datetime.date(*ymd).isoweekday()%7)
    def _build_repeat_part(self, entry, calendar, fundamentals):
        """Build and return a repeat object of this entry"""
        _rep=None
        _repeat_type=entry.repeat_type
        if _repeat_type==self.protocolclass.CAL_REP_DAILY:
            _rep=bpcalendar.RepeatEntry()
            _rep.interval=1
        elif _repeat_type==self.protocolclass.CAL_REP_WEEKLY:
            _rep=bpcalendar.RepeatEntry(bpcalendar.RepeatEntry.weekly)
            _rep.interval=1
        elif _repeat_type==self.protocolclass.CAL_REP_MONTHLY:
            _rep=bpcalendar.RepeatEntry(bpcalendar.RepeatEntry.monthly)
            _rep.interval2=1
            _rep.dow=0
        elif _repeat_type==self.protocolclass.CAL_REP_MONTHLY_NTH:
            _rep=bpcalendar.RepeatEntry(bpcalendar.RepeatEntry.monthly)
            _rep.interval=_rep.get_nthweekday(entry.start_date)
            _rep.interval2=1
            _rep.dow=self._dow(entry.start_date)
        elif _repeat_type==self.protocolclass.CAL_REP_YEARLY:
            _rep=bpcalendar.RepeatEntry(bpcalendar.RepeatEntry.yearly)
        return _rep
    def _build_regular_cal_entry(self, entry, calendar, fundamentals):
        """ Build a regular BitPim entry frm phone data"""
        _bp_entry=bpcalendar.CalendarEntry()
        _bp_entry.id=`entry.index`
        _bp_entry.desc_loc=entry.title
        _bp_entry.start=entry.start_date+entry.start_time
        _t0=datetime.datetime(*_bp_entry.start)
        _t1=_t0+datetime.timedelta(minutes=entry.duration)
        _bp_entry.end=(_t1.year, _t1.month, _t1.day, _t1.hour, _t1.minute)
        if entry.alarm_timed and entry.alarm_enabled:
            _t3=datetime.datetime(*(entry.alarm_date+entry.alarm_time))
            if _t0>=_t3:
                _bp_entry.alarm=(_t0-_t3).seconds/60
        _rep=self._build_repeat_part(entry, calendar, fundamentals)
        if _rep:
            _bp_entry.repeat=_rep
            _bp_entry.end=bpcalendar.CalendarEntry.no_end_date+_bp_entry.end[3:]
        calendar[_bp_entry.id]=_bp_entry
    def _process_exceptions(self, calendar):
        """Process all exceptions"""
        for _idx,_exc in calendar.get('exceptions', []):
            if not calendar.has_key(`_idx`):
                continue
            _rep=calendar[`_idx`].repeat
            if _rep:
                _date=calendar[`_idx`].start[:3]
                for _i in range(_exc):
                    _date=_rep.next_date(_date)
                calendar[`_idx`].suppress_repeat_entry(*_date)
    def _build_cal_entry(self, entry, calendar, fundamentals):
        """Build a BitPim calendar object from phonebook data"""
        if hasattr(entry, 'title'):
            self._build_regular_cal_entry(entry, calendar, fundamentals)
        else:
            calendar['exceptions'].append((entry.index, entry.ex_event))
    def _build_phone_repeat_entry(self, entry, calendar):
        """Build the repeat part of this phone entry"""
        _rep=calendar.repeat
        if _rep:
            if _rep.repeat_type==_rep.daily:
                entry.repeat_type=self.protocolclass.CAL_REP_DAILY
            elif _rep.repeat_type==_rep.weekly:
                entry.repeat_type=self.protocolclass.CAL_REP_WEEKLY
            elif _rep.repeat_type==_rep.monthly:
                if _rep.dow:
                    entry.repeat_type=self.protocolclass.CAL_REP_MONTHLY_NTH
                else:
                    entry.repeat_type=self.protocolclass.CAL_REP_MONTHLY
            else:
                entry.repeat_type=self.protocolclass.CAL_REP_YEARLY
        else:
            entry.repeat_type=self.protocolclass.CAL_REP_NONE
    def _build_phone_alarm_entry(self, entry, calendar):
        _alarm=calendar.alarm
        if _alarm is None or _alarm==-1:
            entry.alarm_timed=1
            entry.alarm_enabled=0
            entry.alarm_time=(0,0)
            entry.alarm_date=(2000,0,0)
        else:
            entry.alarm_timed=1
            entry.alarm_enabled=1
            _d1=datetime.datetime(*calendar.start)-datetime.timedelta(minutes=_alarm)
            entry.alarm_date=(_d1.year, _d1.month, _d1.day)
            entry.alarm_time=(_d1.hour, _d1.minute)
    def _build_phone_entry(self, entry, calendar):
        """Build a phone entry based on a BitPim calendar entry"""
        entry.title=calendar.desc_loc
        entry.start_time=calendar.start[3:]
        entry.start_date=calendar.start[:3]
        entry.duration=(datetime.datetime(*calendar.start[:3]+calendar.end[3:])-
                        datetime.datetime(*calendar.start)).seconds/60
        self._build_phone_repeat_entry(entry, calendar)
        self._build_phone_alarm_entry(entry, calendar)
    def _build_phone_exception_entry(self, entry, calendar, exceptions):
        """Build a phone exception entry based on a BitPim entry"""
        _rep=calendar.repeat
        _end_date=calendar.end[:3]
        _date=calendar.start[:3]
        _ex_date=exceptions.get()[:3]
        _cnt=0
        while _date<=_end_date:
            if _date==_ex_date:
                entry.nth_event=_cnt
                return True
            _date=_rep.next_date(_date)
            _cnt+=1
        return False
    def _write_calendar_entries(self, fundamentals):
        """Write the calendar entries to the phone"""
        _calendar=fundamentals.get('calendar', {})
        _req=self.protocolclass.calendar_write_req()
        _req_ex=self.protocolclass.calendar_write_ex_req()
        _max_entry=self.protocolclass.CAL_MAX_ENTRY
        _total_entries=len(_calendar)
        _cal_cnt=0
        for _,_cal in _calendar.items():
            if _cal_cnt>_max_entry:\
                break
            self._build_phone_entry(_req, _cal)
            _req.index=_cal_cnt
            self.progress(_cal_cnt, _total_entries,
                          'Writing event: %s'%_cal.description)
            self.sendATcommand(_req, None)
            if _cal.repeat:
                for _ex in _cal.repeat.suppressed[:self.protocolclass.CAL_TOTAL_ENTRY_EXCEPTIONS]:
                    if self._build_phone_exception_entry(_req_ex, _cal, _ex):
                        _req_ex.index=_cal_cnt
                        self.sendATcommand(_req_ex, None)
            _cal_cnt+=1
        for _index in range(_cal_cnt, self.protocolclass.CAL_TOTAL_ENTRIES):
            self.progress(_index, _total_entries,
                          'Deleting event #%d'%_index)
            self.del_calendar_entry(_index)
    def _get_new_list(self, index_key, media_key, fundamentals):
        """Return a list of media being replaced"""
        _index=fundamentals.get(index_key, {})
        _media=fundamentals.get(media_key, {})
        _index_file_list=[_entry['name'] for _,_entry in _index.items() \
                          if _entry.has_key('filename')]
        _bp_file_list=[_entry['name'] for _,_entry in _media.items()]
        return [x for x in _bp_file_list if x in _index_file_list]
    def _item_from_index(self, name, item_key, index_dict):
        for _key,_entry in index_dict.items():
            if _entry.get('name', None)==name:
                if item_key:
                    return _entry.get(item_key, None)
                else:
                    return _key
    def _replace_files(self, index_key, media_key,
                   new_list, fundamentals):
        """Replace existing media files with new contents"""
        _index=fundamentals.get(index_key, {})
        _media=fundamentals.get(media_key, {})
        for _file in new_list:
            _data=self._item_from_index(_file, 'data', _media)
            if not _data:
                self.log('Failed to write file %s due to no data'%_file)
                continue
            _file_name=self._item_from_index(_file, 'filename', _index)
            if _file_name:
                _stat=self.statfile(_file_name)
                if _stat and _stat['size']!=len(_data):
                    try:
                        self.writefile(_file_name, _data)
                    except:
                        self.log('Failed to write file '+_file_name)
                        if __debug__:
                            raise
    def saveringtones(self, fundamentals, merge):
        """Save ringtones to the phone"""
        self.log('Writing ringtones to the phone')
        self.setmode(self.MODEPHONEBOOK)
        self.setmode(self.MODEBREW)
        try: 
            _new_list=self._get_new_list('ringtone-index', 'ringtone',
                                         fundamentals)
            self._replace_files('ringtone-index', 'ringtone',
                                _new_list, fundamentals)
        except:
            if __debug__:
                self.setmode(self.MODEMODEM)
                raise
        self.setmode(self.MODEMODEM)
        return fundamentals
    def savewallpapers(self, fundamentals, merge):
        """Save wallpapers to the phone"""
        self.log('Writing wallpapers to the phone')
        self.setmode(self.MODEPHONEBOOK)
        self.setmode(self.MODEBREW)
        try: 
            _new_list=self._get_new_list('wallpaper-index', 'wallpapers',
                                         fundamentals)
            self._replace_files('wallpaper-index', 'wallpapers',
                                _new_list, fundamentals)
        except:
            if __debug__:
                self.setmode(self.MODEMODEM)
                raise
        self.setmode(self.MODEMODEM)
        return fundamentals
    def _detectphone(coms, likely_ports, res, _module, _log):
        for port in likely_ports:
            _model=res.get(port, {}).get('model', None)
            if _model==_module.Profile.phone_model:
                return port
            elif _model==_module.Profile.generic_phone_model:
                try:
                    _comm=commport.CommConnection(_log, port)
                    _comm.sendatcommand('+MODE=2')
                    _comm.sendatcommand('')
                    _s=_comm.sendatcommand('+GMM')[0]
                    _comm.sendatcommand('+MODE=0')
                    _comm.close()
                    _model=_s.split(': ')[1].split(',')[-1].replace('"', '').split('=')[1]
                    if _model=='V710':
                        _model='V710 '
                    res[port]['model']=_model
                    if _model==_module.Profile.phone_model:
                        return port
                except:
                    _comm.close()
                    if __debug__:
                        raise
    detectphone=staticmethod(_detectphone)
parentprofile=com_moto.Profile
class Profile(parentprofile):
    serialsname=Phone.serialsname
    WALLPAPER_WIDTH=176
    WALLPAPER_HEIGHT=220
    MAX_WALLPAPER_BASENAME_LENGTH=37
    WALLPAPER_FILENAME_CHARS="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789()_ .-"
    WALLPAPER_CONVERT_FORMAT="jpg"
    usbids=( ( 0x22B8, 0x2A22, 1),)
    deviceclasses=("modem",)
    phone_manufacturer='Motorola'
    phone_model='V710 '
    generic_phone_model='Motorola CDMA v710 Phone'
    imageorigins={}
    imageorigins.update(common.getkv(parentprofile.stockimageorigins, "images"))
    def GetImageOrigins(self):
        return self.imageorigins
    imagetargets={}
    imagetargets.update(common.getkv(parentprofile.stockimagetargets, "wallpaper",
                                      {'width': 176, 'height': 200, 'format': "JPEG"}))
    imagetargets.update(common.getkv(parentprofile.stockimagetargets, "outsidelcd",
                                      {'width': 176, 'height': 140, 'format': "JPEG"}))
    imagetargets.update(common.getkv(parentprofile.stockimagetargets, "fullscreen",
                                      {'width': 176, 'height': 220, 'format': "JPEG"}))
    def GetTargetsForImageOrigin(self, origin):
        return self.imagetargets
    def __init__(self):
        parentprofile.__init__(self)
    _supportedsyncs=(
        ('phonebook', 'read', None),  # all phonebook reading
        ('phonebook', 'write', 'OVERWRITE'),  # only overwriting phonebook
        ('calendar', 'read', None),   # all calendar reading
        ('calendar', 'write', 'OVERWRITE'),   # only overwriting calendar
        ('ringtone', 'read', None),   # all ringtone reading
        ('ringtone', 'write', 'OVERWRITE'),
        ('wallpaper', 'read', None),  # all wallpaper reading
        ('wallpaper', 'write', 'OVERWRITE'),
        ('sms', 'read', None),     # all SMS list reading DJP
        )
    def convertphonebooktophone(self, helper, data):
        return data
    def QueryAudio(self, origin, currentextension, afi):
        if afi.format in ("MIDI", "QCP", "PMD"):
            return currentextension, afi
        if afi.format=="MP3":
            if afi.channels==1 and 8<=afi.bitrate<=64 and 16000<=afi.samplerate<=22050:
                return currentextension, afi
        return ("mp3", fileinfo.AudioFileInfo(afi, **{'format': 'MP3', 'channels': 1, 'bitrate': 48, 'samplerate': 44100}))
    field_color_data={
        'phonebook': {
            'name': {
                'first': 1, 'middle': 1, 'last': 1, 'full': 1,
                'nickname': 0, 'details': 1 },
            'number': {
                'type': True, 'speeddial': True, 'number': True,
                'details': True,
                'ringtone': True, 'wallpaper': True },
            'email': True,
            'email_details': {
                'emailspeeddial': True, 'emailringtone': True,
                'emailwallpaper': True },
            'address': {
                'type': 0, 'company': 0, 'street': 0, 'street2': 0,
                'city': 0, 'state': 0, 'postalcode': 0, 'country': 0,
                'details': 0 },
            'url': 0,
            'memo': 0,
            'category': 1,
            'wallpaper': 0,
            'ringtone': 0,
            'storage': 0,
            },
        'calendar': {
            'description': True, 'location': True, 'allday': False,
            'start': True, 'end': True, 'priority': False,
            'alarm': True, 'vibrate': False,
            'repeat': True,
            'memo': False,
            'category': False,
            'wallpaper': False,
            'ringtone': False,
            },
        'memo': {
            'subject': False,
            'date': False,
            'secret': False,
            'category': False,
            'memo': False,
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
