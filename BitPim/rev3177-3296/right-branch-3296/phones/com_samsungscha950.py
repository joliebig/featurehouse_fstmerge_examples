"""Communicate with the Samsung SCH-A950 Phone"""
import datetime
import sha
import time
import bpcalendar
import common
import commport
import com_brew
import com_phone
import datetime
import fileinfo
import memo
import nameparser
import prototypes
import pubsub
import p_samsungscha950
import sqlite2_file
class Phone(com_phone.Phone, com_brew.BrewProtocol):
    desc='SCH-A950'
    protocolclass=p_samsungscha950
    serialsname='scha950'
    ringtone_noring_range='range_tones_preloaded_el_15'
    ringtone_default_range='range_tones_preloaded_el_01'
    builtin_ringtones={
        'VZW Default Tone': ringtone_default_range,
        'Melody 1': 'range_tones_preloaded_el_02',
        'Melody 2': 'range_tones_preloaded_el_03',
        'Bell 1': 'range_tones_preloaded_el_04',
        'Bell 2': 'range_tones_preloaded_el_05',
        'Beep Once': 'range_tones_preloaded_el_06',
        'No Ring': ringtone_noring_range,
        }
    builtin_sounds={
        'Birthday': 'range_sound_preloaded_el_birthday',
        'Crowd Roar': 'range_sound_preloaded_el_crowed_roar',
        'Train': 'range_sound_preloaded_el_train',
        'Rainforest': 'range_sound_preloaded_el_rainforest',
        'Clapping': 'range_sound_preloaded_el_clapping',
        'Sound Beep Once': 'range_sound_preloaded_el_beep_once',
        'Sound No Ring': 'range_sound_preloaded_el_no_rings',
        }
    builtin_wallpapers={
        'Wallpaper 1': 'range_f_wallpaper_preloaded_el_01',
        'Wallpaper 2': 'range_f_wallpaper_preloaded_el_02',
        'Wallpaper 3': 'range_f_wallpaper_preloaded_el_03',
        'Wallpaper 4': 'range_f_wallpaper_preloaded_el_04',
        'Wallpaper 5': 'range_f_wallpaper_preloaded_el_05',
        'Wallpaper 6': 'range_f_wallpaper_preloaded_el_06',
        'Wallpaper 7': 'range_f_wallpaper_preloaded_el_07',
        'Wallpaper 8': 'range_f_wallpaper_preloaded_el_08',
        'Wallpaper 9': 'range_f_wallpaper_preloaded_el_09',
        }
    def __init__(self, logtarget, commport):
        "Calls all the constructors and sets initial modes"
        com_phone.Phone.__init__(self, logtarget, commport)
	com_brew.BrewProtocol.__init__(self)
        self.mode=self.MODEBREW
    def get_esn(self):
        if hasattr(self, '_fs_path'):
            return '12345678'
        _req=self.protocolclass.ESN_req()
        _resp=self.sendbrewcommand(_req, self.protocolclass.ESN_resp)
        return '%08X'%_resp.esn
    def _time_now(self):
        return datetime.datetime.now().timetuple()[:5]
    def get_groups(self):
        _res={ 0: { 'name': 'No Group' } }
        try:
            _file_name=None
            _path_name=self.protocolclass.GROUP_INDEX_FILE_NAME
            for i in range(256):
                _name='%s%d'%(_path_name, i)
                if self.statfile(_name):
                    _file_name=_name
                    break
            if not _file_name:
                return _res
            _buf=prototypes.buffer(self.getfilecontents(_file_name))
            _index_file=self.protocolclass.GroupIndexFile()
            _index_file.readfrombuffer(_buf)
            _idx=1
            for _entry in _index_file.items[1:]:
                if _entry.name:
                    _res[_entry.index]={ 'name': _entry.name }
                    _idx+=1
        except IndexError:
            pass
        except:
            if __debug__:
                raise
        return _res
    def _get_builtin_ringtone_index(self, idx, result):
        for _entry in self.builtin_ringtones:
            result[idx]= { 'name': _entry,
                           'origin': 'builtin',
                           }
            idx+=1
        for _entry in self.builtin_sounds:
            result[idx]={ 'name': _entry,
                          'origin': 'builtin',
                          }
            idx+=1
        return idx
    def _get_file_ringtone_index(self, idx, result,
                                 index_file_name, index_file_class,
                                 origin):
        try:
            _buf=prototypes.buffer(self.getfilecontents(index_file_name))
        except (com_brew.BrewNoSuchFileException,
                com_brew.BrewBadPathnameException,
                com_brew.BrewFileLockedException,
                com_brew.BrewAccessDeniedException):
            return idx
        except:
            if __debug__:
                raise
            return idx
        _index_file=index_file_class()
        _index_file.readfrombuffer(_buf)
        for _entry in _index_file.items:
            if _entry.pathname.startswith('/ff/'):
                _file_name=_entry.pathname[4:]
            else:
                _file_name=_entry.pathname
            result[idx]= { 'name': common.basename(_entry.pathname),
                           'filename': _file_name,
                           'origin': origin,
                           }
            idx+=1
        return idx
    def get_ringtone_index(self):
        _res={}
        _idx=self._get_builtin_ringtone_index(0, _res)
        _idx=self._get_file_ringtone_index(_idx, _res,
                                  self.protocolclass.RT_INDEX_FILE_NAME,
                                  self.protocolclass.RRingtoneIndexFile,
                                           'ringers')
        _idx=self._get_file_ringtone_index(_idx, _res,
                                           self.protocolclass.SND_INDEX_FILE_NAME,
                                           self.protocolclass.RSoundsIndexFile,
                                           'sounds')
        return _res
    def _get_builtin_wallpaper_index(self, idx, result):
        for _entry in self.builtin_wallpapers:
            result[idx]={ 'name': _entry,
                          'origin': 'builtin',
                          }
            idx+=1
        return idx
    def _get_file_wallpaper_index(self, idx, result):
        try:
            _buf=prototypes.buffer(self.getfilecontents(self.protocolclass.PIC_INDEX_FILE_NAME))
        except (com_brew.BrewNoSuchFileException,
                com_brew.BrewBadPathnameException,
                com_brew.BrewFileLockedException,
                com_brew.BrewAccessDeniedException):
            return idx
        except:
            if __debug__:
                raise
            return idx
        _index_file=self.protocolclass.RPictureIndexFile()
        _index_file.readfrombuffer(_buf)
        for _entry in _index_file.items[1:]:
            if _entry.pathname.startswith('/ff/'):
                _file_name=_entry.pathname[4:]
            else:
                _file_name=_entry.pathname
            result[idx]={ 'name': _entry.name,
                          'filename': _file_name,
                          'origin': 'images',
                          }
            idx+=1
        return idx
    def get_wallpaper_index(self):
        _res={}
        _idx=self._get_builtin_wallpaper_index(0, _res)
        _idx=self._get_file_wallpaper_index(_idx, _res)
        return _res
    def _read_ringtone_range(self, fundamentals):
        _res={}
        try:
            _data=self.getfilecontents(self.protocolclass.PREF_DB_FILE_NAME)
            _db=sqlite2_file.DBFile(_data)
            for _row in _db.get_table_data('dynamic_range_els'):
                _res[_row[2]]=_row[0]
        except:
            if __debug__:
                raise
        fundamentals['ringtone-range']=_res
    def get_ringtone_range(self, name, fundamentals):
        if not name:
            return self.ringtone_noring_range
        if self.builtin_ringtones.has_key(name):
            return self.builtin_ringtones[name]
        if self.builtin_sounds.has_key(name):
            return self.builtin_sounds[name]
        if not fundamentals.has_key('ringtone-range'):
            self._read_ringtone_range(fundamentals)
        _rt_range=fundamentals['ringtone-range']
        return _rt_range.get(name, self.ringtone_default_range)
    def ringtone_name_from_range(self, range, fundamentals):
        for _key,_value in self.builtin_ringtones.items():
            if range==_value:
                return _key
        for _key,_value in self.builtin_sounds.items():
            if range==_value:
                return _key
        if not fundamentals.has_key('ringtone-range'):
            self._read_ringtone_range(fundamentals)
        for _key,_value in fundamentals['ringtone-range'].items():
            if _value==range:
                return _key
    def get_wallpaper_range(self, wallpaper, fundamentals):
        return fundamentals.get('wallpaper-range', {}).get(wallpaper, None)
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
        results['uniqueserial']=sha.new(self.get_esn()).hexdigest()
        results['groups']=self.get_groups()
        results['ringtone-index']=self.get_ringtone_index()
        results['wallpaper-index']=self.get_wallpaper_index()
        return results
    def _get_media_from_index(self, index_key, media_key,
                              fundamentals):
        _index=fundamentals.get(index_key, {})
        _media={}
        for _key,_entry in _index.items():
            if _entry.has_key('filename') and _entry['filename']:
                try:
                    _media[_entry['name']]=self.getfilecontents(_entry['filename'],
                                                                True)
                except:
                    self.log('Failed to read file %s'%_entry['filename'])
        fundamentals[media_key]=_media
        return fundamentals
    def getringtones(self, fundamentals):
        return self._get_media_from_index('ringtone-index', 'ringtone',
                                          fundamentals)
    def _get_del_new_list(self, index_key, media_key, merge, fundamentals):
        """Return a list of media being deleted and being added"""
        _index=fundamentals.get(index_key, {})
        _media=fundamentals.get(media_key, {})
        _index_file_list=[_entry['name'] for _,_entry in _index.items() \
                          if _entry.has_key('filename')]
        _bp_file_list=[_entry['name'] for _,_entry in _media.items()]
        if merge:
            _del_list=[]
            _new_list=_bp_file_list
        else:
            _del_list=[x for x in _index_file_list if x not in _bp_file_list]
            _new_list=_bp_file_list
        return _del_list, _new_list
    def _item_from_index(self, name, item_key, index_dict):
        for _key,_entry in index_dict.items():
            if _entry.get('name', None)==name:
                if item_key:
                    return _entry.get(item_key, None)
                else:
                    return _key
    def _del_files(self, index_key, _del_list, fundamentals):
        """Delete specified media files, need to be in OBEX mode"""
        _index=fundamentals.get(index_key, {})
        for _file in _del_list:
            _file_name=self._item_from_index(_file, 'filename', _index)
            if _file_name:
                try:
                    self.rmfile(_file_name)
                except Exception, e:
                    self.log('Failed to delete file %s: %s'%(_file_name, str(e)))
    def _replace_files(self, index_key, media_key, new_list, fundamentals):
        """Replace existing files with new contents using BREW"""
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
                        self.log('Failed to write BREW file '+_file_name)
                        if __debug__:
                            raise
    def _add_files(self, index_key, media_key,
                   new_list, fundamentals):
        """Add new file using BEW"""
        _index=fundamentals.get(index_key, {})
        _media=fundamentals.get(media_key, {})
        for _file in new_list:
            _data=self._item_from_index(_file, 'data', _media)
            if not _data:
                self.log('Failed to write file %s due to no data'%_file)
                continue
            if self._item_from_index(_file, None, _index) is None:
                _origin=self._item_from_index(_file, 'origin', _media)
                if _origin=='ringers':
                    _path=self.protocolclass.RT_PATH
                elif _origin=='sounds':
                    _path=self.protocolclass.SND_PATH
                elif _origin=='images':
                    _path=self.protocolclass.PIC_PATH
                else:
                    selg.log('File %s has unknown origin, skip!'%_file)
                    continue
                _file_name=_path+'/'+_file
                try:
                    self.writefile(_file_name, _data)
                except:
                    self.log('Failed to write file '+_file_name)
                    if __debug__:
                        raise
    def _update_media_index(self, index_file_class, index_entry_class,
                            media_path, excluded_files,
                            index_file_name):
        _index_file=index_file_class()
        _filelists={}
        for _path in media_path:
            _filelists.update(self.listfiles(_path))
        _files=_filelists.keys()
        _files.sort()
        for _f in _files:
            _file_name=common.basename(_f)
            if _file_name in excluded_files:
                continue
            _entry=index_entry_class()
            _entry.name=_file_name
            _entry.pathname=_f
            _index_file.items.append(_entry)
        _buf=prototypes.buffer()
        _index_file.writetobuffer(_buf)
        self.writefile(index_file_name, _buf.getvalue())
    def saveringtones(self, fundamentals, merge):
        """Save ringtones to the phone"""
        self.log('Writing ringtones to the phone')
        try:
            _del_list, _new_list=self._get_del_new_list('ringtone-index',
                                                        'ringtone',
                                                        merge,
                                                        fundamentals)
            if __debug__:
                self.log('Delete list: '+','.join(_del_list))
                self.log('New list: '+','.join(_new_list))
            self._replace_files('ringtone-index', 'ringtone',
                                _new_list, fundamentals)
            self._del_files('ringtone-index',
                            _del_list, fundamentals)
            self._add_files('ringtone-index', 'ringtone',
                            _new_list, fundamentals)
            self._update_media_index(self.protocolclass.WRingtoneIndexFile,
                                     self.protocolclass.WRingtoneIndexEntry,
                                     [self.protocolclass.RT_PATH,
                                      self.protocolclass.RT_PATH2],
                                     self.protocolclass.RT_EXCLUDED_FILES,
                                     self.protocolclass.RT_INDEX_FILE_NAME)
            self._update_media_index(self.protocolclass.WSoundsIndexFile,
                                     self.protocolclass.WSoundsIndexEntry,
                                     [self.protocolclass.SND_PATH,
                                      self.protocolclass.SND_PATH2],
                                     self.protocolclass.SND_EXCLUDED_FILES,
                                     self.protocolclass.SND_INDEX_FILE_NAME)
            fundamentals['rebootphone']=True
        except:
            if __debug__:
                raise
        return fundamentals
    def getwallpapers(self, fundamentals):
        return self._get_media_from_index('wallpaper-index', 'wallpapers',
                                          fundamentals)
    def savewallpapers(self, fundamentals, merge):
        """Save ringtones to the phone"""
        self.log('Writing wallpapers to the phone')
        try:
            _del_list, _new_list=self._get_del_new_list('wallpaper-index',
                                                        'wallpapers',
                                                        merge,
                                                        fundamentals)
            if __debug__:
                self.log('Delete list: '+','.join(_del_list))
                self.log('New list: '+','.join(_new_list))
            self._replace_files('wallpaper-index', 'wallpapers',
                                _new_list, fundamentals)
            self._del_files('wallpaper-index',
                            _del_list, fundamentals)
            self._add_files('wallpaper-index', 'wallpapers',
                            _new_list, fundamentals)
            self._update_media_index(self.protocolclass.WPictureIndexFile,
                                     self.protocolclass.WPictureIndexEntry,
                                     [self.protocolclass.PIC_PATH,
                                      self.protocolclass.PIC_PATH2],
                                     self.protocolclass.PIC_EXCLUDED_FILES,
                                     self.protocolclass.PIC_INDEX_FILE_NAME)
            fundamentals['rebootphone']=True
        except:
            if __debug__:
                raise
        return fundamentals
    def _read_calendar_index(self):
        _buf=prototypes.buffer(self.getfilecontents(self.protocolclass.CAL_INDEX_FILE_NAME))
        _res=self.protocolclass.CalIndexFile()
        _res.readfrombuffer(_buf)
        return _res
    def getcalendar(self, fundamentals):
        self.log('Reading calendar')
        _cal_index=self._read_calendar_index()
        _res={}
        for _cnt in range(_cal_index.numofevents):
            _cal_file_name='%s%04d'%(self.protocolclass.CAL_FILE_NAME_PREFIX,
                                     _cal_index.events[_cnt].index)
            _buf=prototypes.buffer(self.getfilecontents(_cal_file_name))
            _bpcal=CalendarEntry(self, _buf, fundamentals).getvalue()
            _res[_bpcal.id]=_bpcal
        fundamentals['calendar']=_res
        return fundamentals
    def _del_existing_cal_entries(self):
        self.log('Deleting existing calendar entries')
        _cal_index=self._read_calendar_index()
        for _idx in range(_cal_index.numofevents):
            _cal_file_name='%s%04d'%(self.protocolclass.CAL_FILE_NAME_PREFIX,
                                     _cal_index.events[_idx].index)
            try:
                self.rmfile(_cal_file_name)
            except:
                self.log('Failed to delete file: '+_cal_file_name)
        return _cal_index.next_index
    def _write_cal_entries(self, next_index, fundamentals):
        _cal_dict=fundamentals.get('calendar', {})
        _idx=next_index
        _cnt=0
        for _key,_entry in _cal_dict.items():
            if _cnt>=self.protocolclass.CAL_MAX_EVENTS:
                break
            try:
                _cal_entry=CalendarEntry(self, _entry, fundamentals)
                _buf=prototypes.buffer()
                _cal_entry.writetobuffer(_buf)
                _cal_file_name='%s%04d'%(self.protocolclass.CAL_FILE_NAME_PREFIX,
                                         _idx)
                self.writefile(_cal_file_name, _buf.getvalue())
                _idx+=1
                _cnt+=1
            except:
                self.log('Failed to write calendar entry')
                if __debug__:
                    raise
        return _idx
    def _write_cal_index(self, next_index, fundamentals):
        _cal_index=self._read_calendar_index()
        for _idx in range(_cal_index.numofevents):
            _cal_index.events[_idx].index=0
        for _idx in range(_cal_index.numofactiveevents):
            _cal_index.activeevents[_idx].index=0
        _old_next_index=_cal_index.next_index
        _num_entries=next_index-_old_next_index
        _cal_index.next_index=next_index
        _cal_index.numofevents=_num_entries
        _cal_index.numofactiveevents=_num_entries
        _cnt=0
        for _idx in range(_old_next_index, next_index):
            _cal_index.events[_cnt].index=_idx
            _cal_index.activeevents[_cnt].index=_idx
            _cnt+=1
        _buf=prototypes.buffer()
        _cal_index.writetobuffer(_buf)
        self.writefile(self.protocolclass.CAL_INDEX_FILE_NAME,
                       _buf.getvalue())
    def savecalendar(self, fundamentals, merge):
        self.log("Sending calendar entries")
        _next_idx=self._del_existing_cal_entries()
        _next_idx=self._write_cal_entries(_next_idx, fundamentals)
        self._write_cal_index(_next_idx, fundamentals)
        fundamentals['rebootphone']=True
        return fundamentals
    def getmemo(self, fundamentals):
        self.log('Reading note pad items')
        _index_file=self._read_calendar_index()
        _res={}
        for _idx in range(_index_file.numofnotes):
            _file_name='%s%04d'%(self.protocolclass.NP_FILE_NAME_PREFIX,
                                 _index_file.notes[_idx].index)
            _buf=prototypes.buffer(self.getfilecontents(_file_name))
            _note=self.protocolclass.NotePadEntry()
            _note.readfrombuffer(_buf)
            _memo=memo.MemoEntry()
            _memo.text=_note.text
            _res[_memo.id]=_memo
        fundamentals['memo']=_res
        return fundamentals
    def _del_existing_memo_entries(self):
        self.log('Deleting existing memo entries')
        _file_index=self._read_calendar_index()
        for _idx in range(_file_index.numofnotes):
            _file_name='%s%04d'%(self.protocolclass.NP_FILE_NAME_PREFIX,
                                 _file_index.notes[_idx].index)
            try:
                self.rmfile(_file_name)
            except:
                self.log('Failed to delete file: '+_file_name)
        return _file_index.next_index
    def _write_memo_entries(self, next_index, fundamentals):
        _memo_dict=fundamentals.get('memo', {})
        _idx=next_index
        _cnt=0
        for _key,_entry in _memo_dict.items():
            if _cnt>=self.protocolclass.NP_MAX_ENTRIES:
                break
            try:
                _memo_entry=self.protocolclass.NotePadEntry()
                _text_len=min(self.protocolclass.NP_MAX_LEN,
                              len(_entry.text))
                _memo_entry.textlen=_text_len
                _memo_entry.text=_entry.text[:_text_len]
                _memo_entry.creation=self._time_now()
                _buf=prototypes.buffer()
                _memo_entry.writetobuffer(_buf)
                _file_name='%s%04d'%(self.protocolclass.NP_FILE_NAME_PREFIX,
                                     _idx)
                self.writefile(_file_name, _buf.getvalue())
                _idx+=1
                _cnt+=1
            except:
                self.log('Failed to write memo endar entry')
                if __debug__:
                    raise
        return _idx
    def _write_memo_index(self, next_index, fundamentals):
        _file_index=self._read_calendar_index()
        for _idx in range(_file_index.numofnotes):
            _file_index.notes[_idx].index=0
        _old_next_index=_file_index.next_index
        _num_entries=next_index-_old_next_index
        _file_index.next_index=next_index
        _file_index.numofnotes=_num_entries
        _cnt=0
        for _idx in range(_old_next_index, next_index):
            _file_index.notes[_cnt].index=_idx
            _cnt+=1
        _buf=prototypes.buffer()
        _file_index.writetobuffer(_buf)
        self.writefile(self.protocolclass.CAL_INDEX_FILE_NAME,
                       _buf.getvalue())
    def savememo(self, fundamentals, merge):
        self.log('Writing memo/notepad items')
        _next_index=self._del_existing_memo_entries()
        _next_index=self._write_memo_entries(_next_index, fundamentals)
        self._write_memo_index(_next_index, fundamentals)
        fundamentals['rebootphone']=True
        return fundamentals
    my_model='SCH-A950/DM'
    my_manufacturer='SAMSUNG'
    def is_mode_brew(self):
        req=self.protocolclass.memoryconfigrequest()
        respc=self.protocolclass.memoryconfigresponse
        for baud in 0, 38400, 115200:
            if baud:
                if not self.comm.setbaudrate(baud):
                    continue
            try:
                self.sendbrewcommand(req, respc, callsetmode=False)
                return True
            except com_phone.modeignoreerrortypes:
                pass
        return False
    def check_my_phone(self, res):
        try:
            _req=self.protocolclass.firmwarerequest()
            _resp=self.sendbrewcommand(_req, self.protocolclass.DefaultResponse)
            if _resp.data[31:35]=='A950':
                res['model']=self.my_model
                res['manufacturer']=self.my_manufacturer
                res['esn']=self.get_esn()
        except:
            if __debug__:
                raise
    def detectphone(coms, likely_ports, res, _module, _log):
        if not likely_ports:
            return None
        for port in likely_ports:
            if not res.has_key(port):
                res[port]={ 'mode_modem': None, 'mode_brew': None,
                            'manufacturer': None, 'model': None,
                            'firmware_version': None, 'esn': None,
                            'firmwareresponse': None }
            try:
                if res[port]['mode_brew']==False or \
                   res[port]['model']:
                    continue
                p=_module.Phone(_log, commport.CommConnection(_log, port, timeout=1))
                if res[port]['mode_brew'] is None:
                    res[port]['mode_brew']=p.is_mode_brew()
                if res[port]['mode_brew']:
                    p.check_my_phone(res[port])
                p.comm.close()
            except:
                if __debug__:
                    raise
    detectphone=staticmethod(detectphone)
    def _del_private_dicts(self, fundamentals):
        for _key in ('ringtone-range', 'wallpaper-range', 'numberlist',
                     'emaillist', 'wplist', 'sdlist'):
            if fundamentals.has_key(_key):
                del fundamentals[_key]
    def _extract_entries(self, filename, res, fundamentals):
        _buf=prototypes.buffer(self.getfilecontents(filename))
        _rec_file=self.protocolclass.PBFileHeader()
        _rec_file.readfrombuffer(_buf)
        for _len in _rec_file.lens:
            if _len.itemlen:
                _entry=self.protocolclass.PBEntry()
                _entry.readfrombuffer(_buf)
                _pbentry=PBEntry(self, _entry, fundamentals).getvalue()
                res[len(res)]=_pbentry
    def getphonebook(self, fundamentals):
        self.log('Reading phonebook contacts')
        _file_cnt=0
        _res={}
        while True:
            _file_name='%s%04d'%(self.protocolclass.PB_ENTRY_FILE_PREFIX,
                                 _file_cnt)
            if self.statfile(_file_name):
                self._extract_entries(_file_name, _res, fundamentals)
                _file_cnt+=1
            else:
                break
        fundamentals['phonebook']=_res
        fundamentals['categories']=[x['name'] for _,x in \
                                    fundamentals.get('groups', {}).items()]
        self._del_private_dicts(fundamentals)
        return fundamentals
    def _write_pb_rec(self, pb_list, rec_cnt, fundamentals):
        _filename='%s%04d'%(self.protocolclass.PB_ENTRY_FILE_PREFIX,
                            rec_cnt)
        _rec_file=self.protocolclass.PBFile()
        for _rec in pb_list:
            _item_len=self.protocolclass.LenEntry()
            if _rec:
                _buf=prototypes.buffer()
                _rec.writetobuffer(_buf)
                _item_len.itemlen=len(_buf.getvalue())
                _rec_file.items.append(_rec.pb)
            _rec_file.lens.append(_item_len)
        _buf=prototypes.buffer()
        _rec_file.writetobuffer(_buf)
        self.writefile(_filename, _buf.getvalue())
    _main_data='\x01\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x03\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x08\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x80\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x08\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00'
    def _write_journal_file(self, journal_file):
        _file_postfix=None
        for _cnt in range(256):
            _filename='%s%d'%(self.protocolclass.PB_MAIN_FILE_PREFIX, _cnt)
            if self.statfile(_filename):
                _file_postfix=_cnt
                break
        if _file_postfix is None:
            self.log('Failed to find main_ postfix')
            return
        _filename='%s%d'%(self.protocolclass.PB_JRNL_FILE_PREFIX,
                          _file_postfix)
        _buf=prototypes.buffer()
        journal_file.writetobuffer(_buf)
        self.writefile(_filename, _buf.getvalue())
    def _collect_wp_range(self, filename, res):
        _buf=prototypes.buffer(self.getfilecontents(filename))
        _rec=self.protocolclass.PBFile()
        _rec.readfrombuffer(_buf)
        for _entry in _rec.items:
            if _entry.info&self.protocolclass.PB_FLG_WP:
                _idx=_entry.wallpaper.rfind('$')+1
                _wp=_entry.wallpaper[_idx:]
                res[_wp]={ 'filename': _entry.wallpaper,
                           'range': _entry.wallpaper_range }
    def _get_wallpaper_range(self, fundamentals):
        _idx=0
        _res={}
        while True:
            _filename='%s%04d'%(self.protocolclass.PB_ENTRY_FILE_PREFIX,
                                _idx)
            if self.statfile(_filename):
                self._collect_wp_range(_filename, _res)
                _idx+=1
            else:
                break
        fundamentals['wallpaper-range']=_res
    _journal_numbers_list=(
        ('cell', protocolclass.PB_FLG_CELL, 'cellsd'),
        ('work', protocolclass.PB_FLG_WORK, 'worksd'),
        ('home', protocolclass.PB_FLG_HOME, 'homesd'),
        ('fax', protocolclass.PB_FLG_FAX, 'faxsd'),
        ('cell2', protocolclass.PB_FLG_CELL2, 'cell2sd'))
    _journal_others_list=(
        ('email', protocolclass.PB_FLG_EMAIL, 'emaillist'),
        ('email2', protocolclass.PB_FLG_EMAIL2, 'emaillist'),
        ('wallpaper', protocolclass.PB_FLG_WP, 'wplist'))
    def _insert_order_lists(self, item, lst):
        for _idx,_entry in enumerate(lst):
            if item<_entry:
                return _idx, lst[:_idx]+[item]+lst[_idx:]
        return len(lst), lst+[item]
    def _gen_bitmap(self, numstr):
        _res=0
        for ch in numstr:
            _res|=(1<<int(ch))
        return _res
    def _write_journal_number(self, pbentry, numtype, sdtype, flg,
                              journal_entry, fundamentals):
        _num_entry=getattr(pbentry, numtype)
        _number=_num_entry.number
        _j_number=self.protocolclass.JournalNumber()
        _j_number.index, fundamentals['numberlist']=self._insert_order_lists(_number,
                                                    fundamentals['numberlist'])
        _j_number.bitmap=self._gen_bitmap(_number)
        setattr(journal_entry, numtype, _j_number)
        if _num_entry.option & self.protocolclass.PB_FLG_SPEEDDIAL:
            _j_sd=self.protocolclass.JournalSpeeddial()
            _j_sd.index=len(fundamentals['sdlist'])
            fundamentals['sdlist'].append(_number)
            _j_sd.speeddial=_num_entry.speeddial
            _j_sd.bitmap=self._gen_bitmap('%03d'%_num_entry.speeddial)
            journal_entry.speeddial_info|=flg
            setattr(journal_entry, sdtype, _j_sd)
    def _write_journal_others(self, pbentry, keytype, listtype,
                             journal_entry, fundamentals):
        _item=getattr(pbentry, keytype)
        _index, fundamentals[listtype]=self._insert_order_lists(_item,
                                                                fundamentals[listtype])
        setattr(journal_entry, keytype, _index)
    def _update_journal_file(self, journal_file, entry, index,
                             fundamentals):
        _j_entry=self.protocolclass.JournalEntry()
        _j_entry.index=index
        _j_entry.number_info=entry.pb.info
        _j_entry.speeddial_info=0
        for _key,_flg,_sdkey in self._journal_numbers_list:
            if entry.pb.info & _flg:
                self._write_journal_number(entry.pb, _key, _sdkey, _flg,
                                           _j_entry, fundamentals)
        for _key,_flg,_list in self._journal_others_list:
            if entry.pb.info & _flg:
                self._write_journal_others(entry.pb, _key, _list,
                                           _j_entry, fundamentals)
        _j_rec=self.protocolclass.JournalRec()
        _j_rec.entry=_j_entry
        _j_rec.blocklen=_j_rec.packetsize()
        journal_file.items.append(_j_rec)
    def savephonebook(self, fundamentals):
        self.log('Writing phonebook contacts')
        self._get_wallpaper_range(fundamentals)
        _pb_dict=fundamentals.get('phonebook', {})
        _rec_cnt=0
        _entry_cnt=1
        _total_cnt=0
        _pbentry_list=[None]
        fundamentals['numberlist']=[]
        fundamentals['emaillist']=[]
        fundamentals['wplist']=[]
        fundamentals['sdlist']=[]
        _pb_list=[(nameparser.getfullname(_entry['names'][0]), _key) \
                  for _key,_entry in _pb_dict.items()]
        _pb_list.sort()
        _journal_file=self.protocolclass.JournalFile()
        for _name,_key in _pb_list:
            _entry=_pb_dict[_key]
            _pbentry=PBEntry(self, _entry, fundamentals)
            _pbentry_list.append(_pbentry)
            _entry_cnt+=1
            _total_cnt+=1
            self._update_journal_file(_journal_file, _pbentry, _total_cnt,
                                      fundamentals)
            if _entry_cnt>7:
                self._write_pb_rec(_pbentry_list, _rec_cnt, fundamentals)
                _pbentry_list=[]
                _rec_cnt+=1
                _entry_cnt=0
        if _entry_cnt:
            self._write_pb_rec(_pbentry_list, _rec_cnt, fundamentals)
        self._write_journal_file(_journal_file)
        fundamentals['rebootphone']=True
        self._del_private_dicts(fundamentals)
        return fundamentals
class CalendarEntry(object):
    """Transient class to handle calendar data being sent to, retrieved from
    the phone.
    """
    REP_NONE=0
    REP_ONCE=0
    REP_DAILY=2
    REP_WEEKLY=5
    REP_MONTHLY=6
    REP_YEARLY=7
    ALARM_ONTIME=0
    ALARM_5M=1
    ALARM_10M=2
    ALARM_15M=3
    ALARM_30M=4
    ALARM_1HR=5
    ALARM_3HR=6
    ALARM_5HR=7
    ALARM_1D=8
    ALERT_TONE=0
    ALERT_VIBRATE=1
    ALERT_LIGHT=2
    TZ_EST=0
    TZ_EDT=1
    TZ_CST=2
    TZ_CDT=3
    TZ_MST=4
    TZ_MDT=5
    TZ_PST=6
    TZ_PDT=7
    TZ_AKST=8
    TZ_AKDT=9
    TZ_HAST=10
    TZ_HADT=11
    TZ_GMT=12
    def __init__(self, phone, value, fundamentals):
        self.phone=phone
        self.fundamentals=fundamentals
        self.cal=phone.protocolclass.CalEntry()
        if isinstance(value, bpcalendar.CalendarEntry):
            self._build(value)
        elif isinstance(value, prototypes.buffer):
            self.cal.readfrombuffer(value)
        else:
            raise TypeError('Expecting type bpcalendar.CalendarEntry or prototypes.buffer')
    def writetobuffer(self, buf):
        self.cal.writetobuffer(buf)
    _build_repeat_dict={
        bpcalendar.RepeatEntry.daily: REP_DAILY,
        bpcalendar.RepeatEntry.weekly: REP_WEEKLY,
        bpcalendar.RepeatEntry.monthly: REP_MONTHLY,
        bpcalendar.RepeatEntry.yearly: REP_YEARLY,
        }
    _build_alarm_dict={
        0: ALARM_ONTIME,
        5: ALARM_5M,
        10: ALARM_10M,
        15: ALARM_15M,
        30: ALARM_30M,
        60: ALARM_1HR,
        180: ALARM_3HR,
        300: ALARM_5HR,
        1440: ALARM_1D,
        }
    _build_tz_dict={
        0: TZ_GMT,
        18000: TZ_EST,
        21600: TZ_CST,
        25200: TZ_MST,
        28800: TZ_PST,
        32400: TZ_AKST,
        36000: TZ_HAST,
        }
    def _build_duration(self, entry):
        return (datetime.datetime(*entry.end)-\
                datetime.datetime(*entry.start)).seconds
    def _build_repeat(self, entry):
        rep=entry.repeat
        if not rep:
            return self.REP_ONCE
        return self._build_repeat_dict.get(rep.repeat_type, self.REP_ONCE)
    def _build_alarm(self, entry):
        _keys=self._build_alarm_dict.keys()
        _keys.sort()
        _alarm=entry.alarm
        for k in _keys:
            if _alarm<=k:
                return self._build_alarm_dict[k]
        return self.ALARM_ONTIME
    def _build_alert(self, entry):
        if entry.vibrate:
            return self.ALERT_VIBRATE
        return self.ALERT_TONE
    def _build_tz(self):
        _tz=self._build_tz_dict.get(time.timezone, self.TZ_EST)
        if time.daylight:
            _tz+=1
        return _tz
    def _build(self, entry):
        self.cal.titlelen=len(entry.desc_loc)
        self.cal.title=entry.desc_loc
        self.cal.start=entry.start
        self.cal.exptime=entry.end[3:5]
        self.cal.repeat=self._build_repeat(entry)
        self.cal.alarm=self._build_alarm(entry)
        self.cal.alert=self._build_alert(entry)
        self.cal.duration=self._build_duration(entry)
        self.cal.timezone=self._build_tz()
        _now=self.phone._time_now()
        self.cal.creationtime=_now
        self.cal.modifiedtime=_now
        _ringtone=self.phone.get_ringtone_range(entry.ringtone,
                                                self.fundamentals)
        self.cal.ringtonelen=len(_ringtone)
        self.cal.ringtone=_ringtone
    def _extract_end(self):
        return (datetime.datetime(*self.cal.start)+\
                datetime.timedelta(seconds=self.cal.duration)).timetuple()[:5]
    def _extract_alarm(self):
        for _value,_code in self._build_alarm_dict.items():
            if self.cal.alarm==_code:
                return _value
    def _extract_repeat(self):
        if self.cal.repeat==self.REP_ONCE:
            return None
        _rep_type=None
        for _type, _code in self._build_repeat_dict.items():
            if self.cal.repeat==_code:
                _rep_type=_type
                break
        if not _rep_type:
            return None
        _rep=bpcalendar.RepeatEntry(_rep_type)
        if _rep_type==_rep.daily:
            _rep.inteval=1
        elif _rep_type==_rep.weekly:
            _rep.interval=1
        elif _rep_type==_rep.monthly:
            _rep.interval2=1
            _rep.dow=0
        return _rep
    def getvalue(self):
        _entry=bpcalendar.CalendarEntry()
        _entry.desc_loc=self.cal.title
        _entry.start=self.cal.start
        _entry.end=self._extract_end()
        _entry.alarm=self._extract_alarm()
        _entry.repeat=self._extract_repeat()
        if _entry.repeat:
            _entry.end=_entry.no_end_date+_entry.end[3:]
        _entry.ringtone=self.phone.ringtone_name_from_range(self.cal.ringtone,
                                                            self.fundamentals)
        _entry.vibrate=self.cal.alert==self.ALERT_VIBRATE
        return _entry
class PBEntry(object):
    def __init__(self, phone, data, fundamentals):
        self.phone=phone
        self.fundamentals=fundamentals
        if isinstance(data, phone.protocolclass.PBEntry):
            self.pb=data
        elif isinstance(data, dict):
            self.pb=phone.protocolclass.PBEntry()
            self._build(data)
        else:
            raise TypeError('Should be PBEntry or phone dict')
    def writetobuffer(self, buf):
        self.pb.writetobuffer(buf)
    _pb_type_dict={
        'home': (Phone.protocolclass.PB_FLG_HOME, 'home'),
        'office': (Phone.protocolclass.PB_FLG_WORK, 'work'),
        'cell': (Phone.protocolclass.PB_FLG_CELL, 'cell'),
        'fax': (Phone.protocolclass.PB_FLG_FAX, 'fax'),
        }
    def _build_number(self, number, ringtone, primary):
        _info_list=self._pb_type_dict.get(number['type'], None)
        if not _info_list:
            return
        if (_info_list[0]==self.phone.protocolclass.PB_FLG_CELL) and \
           (self.pb.info&self.phone.protocolclass.PB_FLG_CELL):
            _info_list=(self.phone.protocolclass.PB_FLG_CELL2,
                        'cell2')
        _entry=self.phone.protocolclass.NumberEntry()
        _entry.number=number['number']
        _sd=number.get('speeddial', None)
        _rt=ringtone
        _entry.option=0
        if _sd is not None:
            _entry.option|=self.phone.protocolclass.PB_FLG_SPEEDDIAL
            _entry.speeddial=_sd
        if _rt is not None:
            _entry.option|=self.phone.protocolclass.PB_FLG_RINGTONE
            _entry.ringtone=self.phone.get_ringtone_range(_rt,
                                                          self.fundamentals)
        if primary:
            _entry.option|=self.phone.protocolclass.PB_FLG_PRIMARY
        self.pb.info|=_info_list[0]
        setattr(self.pb, _info_list[1], _entry)
    def _build_email(self, emails):
        if len(emails) and emails[0].get('email', None):
            self.pb.info|=self.phone.protocolclass.PB_FLG_EMAIL
            self.pb.email=emails[0]['email']
        if len(emails)>1 and emails[1].get('email', None):
            self.pb.info|=self.phone.protocolclass.PB_FLG_EMAIL2
            self.pb.email2=emails[1]['email']
    def _build_group(self, cat):
        if not cat:
            return
        _cat_list=self.fundamentals.get('groups', {})
        for _key,_cat in _cat_list.items():
            if _key and _cat.get('name', None)==cat:
                self.pb.info|=self.phone.protocolclass.PB_FLG_GROUP
                self.pb.group=_key
                break
    def _build_wallpaper(self, wallpaper):
        if not wallpaper:
            return
        _wp=self.phone.get_wallpaper_range(wallpaper, self.fundamentals)
        if _wp:
            self.pb.info|=self.phone.protocolclass.PB_FLG_WP
            self.pb.wallpaper=_wp['filename']
            self.pb.wallpaper_range=_wp['range']
    def _build(self, entry):
        self.pb.info=self.phone.protocolclass.PB_FLG_NONE
        self.pb.name=nameparser.getfullname(entry['names'][0])
        _ringtone=entry.get('ringtones', [{}])[0].get('ringtone', None)
        _primary=True   # the first number is the primary one
        for _number in entry.get('numbers', []):
            self._build_number(_number, _ringtone, _primary)
            _primary=False
        self._build_email(entry.get('emails', []))
        self.pb.datetime=self.phone._time_now()
        self._build_group(entry.get('categories', [{}])[0].get('category', None))
        self._build_wallpaper(entry.get('wallpapers', [{}])[0].get('wallpaper', None))
    def _extract_emails(self, entry, p_class):
        if self.pb.info & p_class.PB_FLG_EMAIL:
            entry['emails']=[{ 'email': self.pb.email }]
        if self.pb.info & p_class.PB_FLG_EMAIL2:
            entry.setdefault('emails', []).append({ 'email': self.pb.email2 })
    _number_type_dict={
        'cell': (Phone.protocolclass.PB_FLG_CELL, 'cell'),
        'home': (Phone.protocolclass.PB_FLG_HOME, 'home'),
        'work': (Phone.protocolclass.PB_FLG_WORK, 'office'),
        'fax': (Phone.protocolclass.PB_FLG_FAX, 'fax'),
        'cell2': (Phone.protocolclass.PB_FLG_CELL2, 'cell'),
        }
    def _extract_numbers(self, entry, p_class):
        entry['numbers']=[]
        for _key,_info_list in self._number_type_dict.items():
            if self.pb.info&_info_list[0]:
                _num_entry=getattr(self.pb, _key)
                _number={ 'number': _num_entry.number,
                          'type': _info_list[1] }
                if _num_entry.option&p_class.PB_FLG_SPEEDDIAL:
                    _number['speeddial']=_num_entry.speeddial
                if _num_entry.option&p_class.PB_FLG_RINGTONE and \
                   not entry.has_key('ringtones'):
                    _ringtone=self.phone.ringtone_name_from_range(
                        _num_entry.ringtone, self.fundamentals)
                    entry['ringtones']=[{ 'ringtone': _ringtone,
                                          'use': 'call' }]
                if _num_entry.option & p_class.PB_FLG_PRIMARY:
                    entry['numbers']=[_number]+entry['numbers']
                else:
                    entry['numbers'].append(_number)
    def _extract_group(self, entry, p_class):
        if not self.pb.info&p_class.PB_FLG_GROUP:
            return
        _groups=self.fundamentals.get('groups', {})
        if _groups.has_key(self.pb.group):
            entry['categories']=[{ 'category': _groups[self.pb.group]['name'] }]
    def _extract_wallpaper(self, entry, p_class):
        if not self.pb.info&p_class.PB_FLG_WP:
            return
        _idx=self.pb.wallpaper.rfind('$')+1
        _wp=self.pb.wallpaper[_idx:]
        entry['wallpapers']=[{ 'wallpaper': _wp,
                               'use': 'call' }]
    def getvalue(self):
        _entry={}
        _p_class=self.phone.protocolclass
        _entry['names']=[{ 'full': self.pb.name }]
        self._extract_emails(_entry, _p_class)
        self._extract_numbers(_entry, _p_class)
        self._extract_group(_entry, _p_class)
        self._extract_wallpaper(_entry, _p_class)
        return _entry
parentprofile=com_phone.Profile
class Profile(parentprofile):
    serialsname=Phone.serialsname
    WALLPAPER_WIDTH=176
    WALLPAPER_HEIGHT=220
    autodetect_delay=3
    usbids=( ( 0x04e8, 0x6640, 2),)
    deviceclasses=("serial",)
    BP_Calendar_Version=3
    phone_manufacturer=Phone.my_manufacturer
    phone_model=Phone.my_model
    RINGTONE_LIMITS= {
        'MAXSIZE': 100000
    }
    def __init__(self):
        parentprofile.__init__(self)
    _supportedsyncs=(
        ('phonebook', 'read', None),  # all phonebook reading
        ('calendar', 'read', None),   # all calendar reading
        ('calendar', 'write', 'OVERWRITE'),   # only overwriting calendar
        ('ringtone', 'read', None),   # all ringtone reading
        ('ringtone', 'write', 'MERGE'),
        ('wallpaper', 'read', None),  # all wallpaper reading
        ('wallpaper', 'write', None),
        ('memo', 'read', None),     # all memo list reading DJP
        ('memo', 'write', 'OVERWRITE'),  # all memo list writing DJP
        )
    def QueryAudio(self, origin, currentextension, afi):
        _max_size=self.RINGTONE_LIMITS['MAXSIZE']
        setattr(afi, 'MAXSIZE', _max_size)
        if afi.format in ("MIDI", "QCP", "PMD"):
            return currentextension, afi
        if afi.format=="MP3":
            if afi.channels==1 and 8<=afi.bitrate<=64 and 16000<=afi.samplerate<=22050:
                return currentextension, afi
        return ("mp3", fileinfo.AudioFileInfo(afi, **{'format': 'MP3',
                                                      'channels': 2,
                                                      'bitrate': 48,
                                                      'samplerate': 44100,
                                                      'MAXSIZE': _max_size }))
    imageorigins={}
    imageorigins.update(common.getkv(parentprofile.stockimageorigins, "images"))
    def GetImageOrigins(self):
        return self.imageorigins
    imagetargets={}
    imagetargets.update(common.getkv(parentprofile.stockimagetargets, "wallpaper",
                                      {'width': 176, 'height': 186, 'format': "JPEG"}))
    imagetargets.update(common.getkv(parentprofile.stockimagetargets, "outsidelcd",
                                      {'width': 128, 'height': 96, 'format': "JPEG"}))
    imagetargets.update(common.getkv(parentprofile.stockimagetargets, "fullscreen",
                                      {'width': 176, 'height': 220, 'format': "JPEG"}))
    def GetTargetsForImageOrigin(self, origin):
        return self.imagetargets
    def convertphonebooktophone(self, helper, data):
        return data
