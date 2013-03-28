"""Communicate with Motorola phones using AT commands"""
import sha
import commport
import com_brew
import com_gsm
import phoneinfo
import prototypes
import p_moto
import sms
class Phone(com_gsm.Phone, com_brew.BrewProtocol):
    """Talk to a generic Motorola phone.
    """
    desc='Motorola'
    protocolclass=p_moto
    _switch_mode_cmd='\x44\x58\xf4\x7e'
    MODEPHONEBOOK="modephonebook"
    def __init__(self, logtarget, commport):
        com_gsm.Phone.__init__(self, logtarget, commport)
        com_brew.BrewProtocol.__init__(self)
        self.mode=self.MODENONE
    def set_mode(self, mode):
        """Set the current phone mode"""
        _req=self.protocolclass.modeset()
        _req.mode=mode
        self.sendATcommand(_req, None)
        self.comm.sendatcommand('')
    def charset_ascii(self):
        """Set the charset to ASCII (default)"""
        _req=self.protocolclass.charset_set_req()
        _req.charset=self.protocolclass.CHARSET_ASCII
        self.sendATcommand(_req, None)
    def charset_ucs2(self):
        """Set the charset to UCS-2, used for most string values"""
        _req=self.protocolclass.charset_set_req()
        _req.charset=self.protocolclass.CHARSET_UCS2
        self.sendATcommand(_req, None)
    def select_phonebook(self, phonebook=None):
        _req=self.protocolclass.select_phonebook_req()
        if phonebook:
            _req.pb_type=phonebook
        self.sendATcommand(_req, None)
    def ucs2_to_ascii(self, v):
        """convert an UCS-2 to ASCII string"""
        return v.decode('hex').decode('utf_16be')
    def ascii_to_ucs2(self, v):
        """convert an ascii string to UCS-2"""
        return v.encode('utf_16be').encode('hex').upper()
    def _setmodephonebooktobrew(self):
        self.setmode(self.MODEMODEM)
        self.setmode(self.MODEBREW)
        return True
    def _setmodemodemtobrew(self):
        self.log('Switching from modem to BREW')
        try:
            self.comm.sendatcommand('$QCDMG')
            return True
        except:
            pass
        self.log('Retry switching from modem to BREW')
        try:
            self.comm.sendatcommand('$QCDMG')
            return True
        except commport.ATError:
	    return False
	except:
            if __debug__:
                self.log('Got an excepetion')
            return False
    def _setmodebrew(self):
        self.log('Switching from None to BREW')
        if not self._setmodemodem():
            return False
        return self._setmodemodemtobrew()
    def _setmodebrewtomodem(self):
        self.log('Switching from BREW to modem')
        try:
            self.comm.write(self._switch_mode_cmd, False)
            self.comm.readsome(numchars=5, log=False)
            return True
        except:
            pass
        try:
            self.comm.write(self._switch_mode_cmd, False)
            self.comm.readsome(numchars=5, log=False)
            return True
        except:
            return False
    def _setmodemodemtophonebook(self):
        self.log('Switching from modem to phonebook')
        self.set_mode(self.protocolclass.MODE_PHONEBOOK)
        return True
    def _setmodemodem(self):
        self.log('Switching to modem')
        try:
            self.comm.sendatcommand('E0V1')
            self.set_mode(self.protocolclass.MODE_MODEM)
            return True
        except:
            pass
        self.log('trying to switch from BREW mode')
        if not self._setmodebrewtomodem():
            return False
        try:
            self.comm.sendatcommand('E0V1')
            self.set_mode(self.protocolclass.MODE_MODEM)
            return True
        except:
            return False
    def _setmodephonebook(self):
        self.setmode(self.MODEMODEM)
        self.setmode(self.MODEPHONEBOOK)
        return True
    def _setmodephonebooktomodem(self):
        self.log('Switching from phonebook to modem')
        self.set_mode(self.protocolclass.MODE_MODEM)
        return True
    def decode_utf16(self, v):
        """Decode a Motorola unicode string"""
        _idx=v.find('\x00\x00')
        if _idx==-1:
            return v.decode('utf_16_le')
        else:
            return v[:_idx+1].decode('utf_16_le')
    def encode_utf16(self, v):
        """Encode a unicode/string into a Motorola unicode"""
        return (v+'\x00').encode('utf_16_le')
    def get_model(self):
        _req=self.protocolclass.model_req()
        return self.sendATcommand(_req, self.protocolclass.string_resp)[0].value
    def get_manufacturer(self):
        _req=self.protocolclass.manufacturer_req()
        return self.sendATcommand(_req, self.protocolclass.string_resp)[0].value
    def get_phone_number(self):
        self.setmode(self.MODEPHONEBOOK)
        _req=self.protocolclass.number_req()
        _s=self.sendATcommand(_req, self.protocolclass.string_resp)[0].value
        self.setmode(self.MODEMODEM)
        return _s.replace(',', '')
    def get_firmware_version(self):
        _req=self.protocolclass.firmware_req()
        return self.sendATcommand(_req, self.protocolclass.string_resp)[0].value
    def get_signal_quality(self):
        _req=self.protocolclass.signal_req()
        _res=self.sendATcommand(_req, self.protocolclass.signal_resp)[0]
        return str(100*int(_res.rssi)/31)+'%'
    def get_battery_level(self):
        _req=self.protocolclass.battery_req()
        _res=self.sendATcommand(_req, self.protocolclass.battery_resp)[0]
        return '%d%%'%_res.level
    def getphoneinfo(self, phone_info):
        self.log('Getting Phone Info')
        self.setmode(self.MODEMODEM)
        _total_keys=len(phoneinfo.PhoneInfo.standard_keys)
        for _cnt,e in enumerate(phoneinfo.PhoneInfo.standard_keys):
            self.progress(_cnt, _total_keys,
                          'Retrieving Phone '+e[1])
            f=getattr(self, 'get_'+e[0])
            setattr(phone_info, e[0], f())
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
        self.progress(0, 100, 'Retrieving fundamental phone information')
        self.setmode(self.MODEPHONEBOOK)
        self.charset_ascii()
        self.log("Phone serial number")
        results['uniqueserial']=sha.new(self.get_esn()).hexdigest()
        self.log("Reading group information")
        results['groups']=self._get_groups()
        self.setmode(self.MODEBREW)
        self.log('Reading Ringtone Index')
        results['ringtone-index']=self._get_ringtone_index()
        self.log('Reading Wallpaper Index')
        results['wallpaper-index']=self._get_wallpaper_index()
        self._update_group_ringtone(results)
        self.log("Fundamentals retrieved")
        self.setmode(self.MODEMODEM)
        return results
    def _update_group_ringtone(self, results):
        _ringtone_index=results.get('ringtone-index', {})
        _groups=results.get('groups', {})
        for _key,_entry in _groups.items():
            _rt_idx=_entry['ringtone']
            _groups[_key]['ringtone']=_ringtone_index.get(_rt_idx, {}).get('name', None)
        results['groups']=_groups
    def _setup_ringtone_name_dict(self, fundamentals):
        """Create a new ringtone dict keyed by name for lookup"""
        _rt_index=fundamentals.get('ringtone-index', {})
        _rt_name_index={}
        for _key,_entry in _rt_index.items():
            _rt_name_index[_entry['name']]=_key
        return _rt_name_index
    def _setup_group_name_dict(self, fundamentals):
        """Create a new group dict keyed by name for lookup"""
        _grp_name_index={}
        for _key,_entry in fundamentals.get('groups', {}).items():
            _grp_name_index[_entry['name']]=_key
        return _grp_name_index
    def _mark_used_slots(self, entries, sd_slots, key_name):
        """Mark the speed dial slots being used"""
        for _key,_entry in enumerate(entries):
            _sd=_entry.get('speeddial', None)
            if _sd is not None:
                if sd_slots[_sd]:
                    entries[_key]['speeddial']=None
                else:
                    sd_slots[_sd]=_entry[key_name]
    def _get_sd_slot(self, entries, sd_slots, key_name):
        """Populate the next available speed dial"""
        for _index,_entry in enumerate(entries):
            if _entry.get('speeddial', None) is None:
                try:
                    _new_sd=sd_slots.index(False)
                    entries[_index]['speeddial']=_new_sd
                    sd_slots[_new_sd]=_entry[key_name]
                except ValueError:
                    self.log('Failed to allocate speed dial value')
    def _ensure_speeddials(self, fundamentals):
        """Make sure that each and every number/email/mail list has a
        speed dial, which is being used as the slot/index number
        """
        _pb_book=fundamentals.get('phonebook', {})
        _sd_slots=[False]*(self.protocolclass.PB_TOTAL_ENTRIES+1)
        _sd_slots[0]=True
        for _key,_pb_entry in _pb_book.items():
            self._mark_used_slots(_pb_entry.get('numbers', []), _sd_slots,
                                  'number')
            self._mark_used_slots(_pb_entry.get('emails', []), _sd_slots,
                                  'email')
            self._mark_used_slots(_pb_entry.get('maillist', []), _sd_slots,
                                  'entry')
        for _key, _pb_entry in _pb_book.items():
            self._get_sd_slot(_pb_entry.get('numbers', []), _sd_slots,
                              'number')
            self._get_sd_slot(_pb_entry.get('emails', []), _sd_slots,
                              'email')
            self._get_sd_slot(_pb_entry.get('maillist', []), _sd_slots,
                              'entry')
        return _sd_slots
    def _get_groups(self):
        raise NotImplementedError
    def _get_ringtone_index(self):
        raise NotImplementedError
    def _get_wallpaper_index(self):
        raise NotImplementedError
    def _save_groups(self, fundamentals):
        raise NotImplementedError
    def _build_pb_entry(self, entry, pb_book, fundamentals):
        raise NotImplementedError
    def _build_pb_entry(self, entry, pb_book, fundamentals):
        """Build a BitPim phonebook entry based on phon data.
        Need to to implement in subclass for each phone
        """
        raise NotImplementedError
    def _update_mail_list(self, pb_book, fundamentals):
        raise NotImplementedError
    def getphonebook(self, result):
        """Reads the phonebook data.  The L{getfundamentals} information will
        already be in result."""
        self.log('Getting phonebook')
        self.setmode(self.MODEPHONEBOOK)
        self.select_phonebook()
        pb_book={}
        result['pb_list']=[]
        result['sd_dict']={}
        _total_entries=self.protocolclass.PB_TOTAL_ENTRIES
        _req=self.protocolclass.read_pb_req()
        for _start_idx in range(1, _total_entries+1, 10):
            _end_idx=min(_start_idx+9, _total_entries)
            _req.start_index=_start_idx
            _req.end_index=_end_idx
            for _retry_cnt in range(2):
                try:
                    self.progress(_total_entries, _end_idx,
                                  'Reading conatct entry %d to %d'%(_start_idx, _end_idx))
                    _res=self.sendATcommand(_req, self.protocolclass.read_pb_resp)
                    for _entry in _res:
                        self._build_pb_entry(_entry, pb_book, result)
                    break
                except:
                    if _retry_cnt:
                        self.log('Failed to read phonebook data')
                    else:
                        self.log('Failed to read phonebook data, retrying...')
        self._update_mail_list(pb_book, result)
        self.setmode(self.MODEMODEM)
        del result['pb_list'], result['sd_dict']
        _keys=result['groups'].keys()
        result['categories']=[x['name'] for _,x in result['groups'].items()]
        result['phonebook']=pb_book
        return pb_book
    def savephonebook(self, result):
        "Saves out the phonebook"
        self.log('Writing phonebook')
        self.setmode(self.MODEPHONEBOOK)
        result['ringtone-name-index']=self._setup_ringtone_name_dict(result)
        result['group-name-index']=self._setup_group_name_dict(result)
        result['sd-slots']=self._ensure_speeddials(result)
        self._save_groups(result)
        self._write_pb_entries(result)
        del result['ringtone-name-index'], result['group-name-index']
        del result['sd-slots']
        self.setmode(self.MODEMODEM)
        return result
    def _build_cal_entry(self, entry, calendar, fundamentals):
        """Build a BitPim calendar object from phonebook data"""
        raise NotImplementedError
    def del_calendar_entry(self, index):
        _req=self.protocolclass.calendar_write_ex_req()
        _req.index=index
        _req.nth_event=0
        _req.ex_event_flag=0
        self.sendATcommand(_req, None)
    def lock_calendar(self, lock=True):
        """Lock the calendar to access it"""
        _req=self.protocolclass.calendar_lock_req()
        if lock:
            _req.lock=1
        else:
            _req.lock=0
        self.sendATcommand(_req, None)
    def getcalendar(self,result):
        """Read all calendars from the phone"""
        self.log('Reading calendar entries')
        self.setmode(self.MODEPHONEBOOK)
        self.lock_calendar()
        _total_entries=self.protocolclass.CAL_TOTAL_ENTRIES
        _max_entry=self.protocolclass.CAL_MAX_ENTRY
        _req=self.protocolclass.calendar_read_req()
        _calendar={ 'exceptions': [] }
        for _start_idx in range(0, _total_entries, 10):
            _end_idx=min(_start_idx+9, _max_entry)
            _req.start_index=_start_idx
            _req.end_index=_end_idx
            for _retry in range(2):
                try:
                    self.progress(_total_entries, _end_idx,
                                  'Reading calendar entry %d to %d'%(_start_idx, _end_idx))
                    _res=self.sendATcommand(_req, self.protocolclass.calendar_req_resp)
                    for _entry in _res:
                        self._build_cal_entry(_entry, _calendar, result)
                except:
                    if _retry:
                        self.log('Failed to read calendar data')
                    else:
                        self.log('Failed to read calendar data, retrying ...')
        self._process_exceptions(_calendar)
        del _calendar['exceptions']
        self.lock_calendar(False)
        self.setmode(self.MODEMODEM)
        result['calendar']=_calendar
        return result
    def savecalendar(self, result, merge):
        """Save calendar entries to the phone"""
        self.log('Writing calendar entries')
        self.setmode(self.MODEPHONEBOOK)
        self.lock_calendar()
        self._write_calendar_entries(result)
        self.lock_calendar(False)
        self.setmode(self.MODEMODEM)
        return result
    def _read_media(self, index_key, fundamentals):
        """Read the contents of media files and return"""
        _media={}
        for _key,_entry in fundamentals.get(index_key, {}).items():
            if _entry.get('filename', None):
                try:
                    _media[_entry['name']]=self.getfilecontents(_entry['filename'],
                                                                True)
                except (com_brew.BrewNoSuchFileException,
                        com_brew.BrewBadPathnameException,
                        com_brew.BrewNameTooLongException,
                        com_brew.BrewAccessDeniedException):
                    self.log("Failed to read media file: %s"%_entry['name'])
                except:
                    self.log('Failed to read media file.')
                    if __debug__:
                        raise
        return _media
    def getringtones(self, fundamentals):
        """Retrieve ringtones data"""
        self.log('Reading ringtones')
        self.setmode(self.MODEPHONEBOOK)
        self.setmode(self.MODEBREW)
        try:
            fundamentals['ringtone']=self._read_media('ringtone-index',
                                                      fundamentals)
        except:
            if __debug__:
                raise
        self.setmode(self.MODEMODEM)
        return fundamentals
    def getwallpapers(self, fundamentals):
        """Retrieve wallpaper data"""
        self.log('Reading wallpapers')
        self.setmode(self.MODEPHONEBOOK)
        self.setmode(self.MODEBREW)
        try:
            fundamentals['wallpapers']=self._read_media('wallpaper-index',
                                                        fundamentals)
        except:
            if __debug__:
                raise
        self.setmode(self.MODEMODEM)
        return fundamentals
    def select_default_SMS(self):
        """Select the default SMS storage"""
        _req=self.protocolclass.sms_sel_req()
        self.sendATcommand(_req, None)
    def _process_sms_header(self, _header, _entry):
        _addr=_header.sms_addr.strip(' ').replace('"', '')
        if _header.has_date:
            _entry.datetime=_header.sms_date
        if _header.sms_type==self.protocolclass.SMS_REC_UNREAD:
            _entry.read=False
            _entry.folder=_entry.Folder_Inbox
            _entry._from=_addr
        elif _header.sms_type==self.protocolclass.SMS_REC_READ:
            _entry.read=True
            _entry.folder=_entry.Folder_Inbox
            _entry._from=_addr
        elif _header.sms_type==self.protocolclass.SMS_STO_UNSENT:
            _entry._to=_addr
            _entry.folder=_entry.Folder_Saved
        else:
            _entry._to=_addr
            _entry.folder=_entry.Folder_Sent
    def _process_sms_text(self, res, entry):
        _s=res[1]
        _open_p=_s.find('(')
        _close_p=_s.find(')')
        if _open_p==0 and _close_p!=-1:
            entry.subject=_s[1:_close_p]
            res[1]=_s[_close_p+1:]
        entry.text='\n'.join(res[1:])
    def _process_sms_result(self, _res, _sms, fundamentals):
        """Process an SMS result as returned from the phone"""
        _buf=prototypes.buffer(_res[0])
        _header=self.protocolclass.sms_m_read_resp()
        _header.has_date=len(_res[0].split(','))>2
        _header.readfrombuffer(_buf, logtitle='Reading SMS Response')
        _entry=sms.SMSEntry()
        self._process_sms_header(_header, _entry)
        self._process_sms_text(_res, _entry)
        _sms[_entry.id]=_entry
    def getsms(self, fundamentals):
        """Read SMS messages from the phone"""
        self.log('Reading SMS messages')
        self.setmode(self.MODEPHONEBOOK)
        _sms={}
        try:
            self.select_default_SMS()
            _req=self.protocolclass.sms_list_req()
            _sms_list=self.sendATcommand(_req, None, True)
            _sms_item=self.protocolclass.sms_list_resp()
            for _entry in _sms_list:
                _buf=prototypes.buffer(_entry)
                _sms_item.readfrombuffer(_buf,
                                         logtitle='Reading an SMS List Item')
                try:
                    _res=self.comm.sendatcommand('+MMGR=%d'%_sms_item.index)
                    self._process_sms_result(_res, _sms, fundamentals)
                except commport.ATError:
                    pass
        except:
            if __debug__:
                self.setmode(self.MODEMODEM)
                raise
        self.setmode(self.MODEMODEM)
        fundamentals['canned_msg']=[]
        fundamentals['sms']=_sms
        return fundamentals
parentprofile=com_gsm.Profile
class Profile(parentprofile):
    BP_Calendar_Version=3
