"""
Handle data recording stuff
"""
import cPickle
import struct
import threading
import time
import common
import pubsub
DR_Version=(0, 0, 1, 0) # 00.10
DR_Signature='BitPimDR'
DR_Rec_Marker='<-->'
DR_Type_All=0xff
DR_Type_Note=0x01
DR_Type_Write=0x02
DR_Type_Read=0x04
DR_Type_Read_ATResponse=0x08
DR_Type_Read_Some=0x10
DR_Type_Read_Until=0x20
DR_Type_Data=0x40
DR_Type_Name={
    DR_Type_Note: 'Note',
    DR_Type_Write: 'Write',
    DR_Type_Read: 'Read',
    DR_Type_Read_ATResponse: 'Read ATResponse',
    DR_Type_Read_Some: 'Read Some',
    DR_Type_Read_Until: 'Read Until',
    DR_Type_Data: 'Data',
    }
class DataRecordingError(Exception):
    def __init__(self, value):
        Exception.__init__(self, value)
DR_On=False
DR_Play=False
_the_recorder=None
_the_player=None
def record_to_file(file_name, append=False):
    "start a data recording session into the specified file"
    global DR_On, _the_recorder
    if DR_On and _the_recorder:
        _the_recorder.stop()
    _rec=DR_Rec_File(file_name, append)
    _rec.start()
    pubsub.publish(pubsub.DR_RECORD, data=_rec)
def playback_from_file(file_name):
    "start a playback session from the specified file"
    global DR_Play, _the_player
    if DR_Play and _the_player:
        _the_player.stop()
    _player=DR_Read_File(file_name)
    _player.start()
    pubsub.publish(pubsub.DR_PLAY, data=_player)
def stop():
    "stop a recording and/or playback session"
    global DR_Play, DR_On, _the_recorder, _the_player
    if DR_On and _the_recorder:
        _the_recorder.stop()
    if DR_Play and _the_player:
        _the_player.stop()
    pubsub.publish(pubsub.DR_STOP)
def register(start_recording=None, start_playback=None, stop=None):
    if start_recording:
        pubsub.subscribe(start_recording, pubsub.DR_RECORD)
    if start_playback:
        pubsub.subscribe(start_playback, pubsub.DR_PLAY)
    if stop:
        pubsub.subscribe(stop, pubsub.DR_STOP)
def unregister(start_recording=None, start_playback=None, stop=None):
    if start_recording:
        pubsub.unsubscribe(start_recording)
    if start_playback:
        pubsub.unsubscribe(start_playback)
    if stop:
        pubsub.unsubscribe(stop)
def get_data(data_type):
    global DR_Play, _the_player
    if DR_Play and _the_player:
        return _the_player.get_data(data_type)
    raise DataRecordingError('Data Playback not active')
def record(dr_type, dr_data, dr_class=None):
    global DR_On, _the_recorder
    if DR_On and _the_recorder:
        _the_recorder.record(dr_type, dr_data, dr_class)
    elif __debug__:
        raise DataRecordingError('Data Recording not active')
def filename():
    "return the current file name being used for recording or playback"
    if _the_recorder:
        return _the_recorder._file_name
    elif _the_player:
        return _the_player._file_name
class DR_Record(object):
    def __init__(self, dr_type, dr_data, klass=None):
        self._type=dr_type
        self._data=dr_data
        self._time=time.time()
        if klass:
            try:
                self._class_module=klass.__module__
                self._class_name=klass.__name__
            except:
                klass=klass.__class__
                self._class_module=klass.__module__
                self._class_name=klass.__name__
        else:
            self._class_module=None
            self._class_name=None
    def get(self):
        return self._data
    def set(self, data):
        self._data=data
    def __repr__(self):
        t=time.localtime(self._time)
        _s="%d:%02d:%02d.%03d " % (t[3], t[4], t[5],
                                   int((self._time-int(self._time))*1000))
        if self._type==DR_Type_Note:
            _s+=self._data
        else:
            _s+=" %s - %d bytes\n" % (DR_Type_Name.get(self._type, 'Data'),
                                      len(self._data))
            if self._class_module and self._class_name:
                _s+="<#! %s.%s !#>\n" % (self._class_module, self._class_name)
            _s+=common.datatohexstring(self._data)
        _s+='\n'
        return _s
class DR_File(object):
    def __init__(self, file_name):
        self._file_name=file_name
        self._file=None
        self._valid=False
    def _check_header(self):
        try:
            return file(self._file_name, 'rb').read(len(DR_Signature))==DR_Signature
        except IOError:
            return False
        except:
            if __debug__:
                raise
            return False
    def stop(self):
        global DR_On
        self._file.close()
        DR_On=False
class DR_Rec_File(DR_File):
    def __init__(self, file_name, append=False):
        super(DR_Rec_File, self).__init__(file_name)
        self._pause=None
        if not append:
            self._file=file(self._file_name, 'wb')
            self._write_header()
            self._valid=True
        else:
            if self._check_header():
                self._file=file(self._file_name, 'ab')
                self._valid=True
            else:
                self._valid=False
    def _write_header(self):
        self._file.write(DR_Signature)
        _s=''
        for e in DR_Version:
            _s+=struct.pack('B', e)
        self._file.write(_s)
    def can_record(self):
        return bool(self._valid and self._file)
    def record(self, dr_type, dr_data, dr_class=None):
        if self._pause and (self._pause & dr_type):
            return
        if self._file:
            _t=threading.Thread(target=self._write_record,
                                args=(dr_type, dr_data, dr_class))
            _t.start()
    def _write_record(self, dr_type, dr_data, dr_class=None):
        _rec=DR_Record(dr_type, dr_data, dr_class)
        _s=cPickle.dumps(_rec)
        self._file.write(DR_Rec_Marker+struct.pack('L', len(_s))+_s)
    def stop(self):
        global DR_On, _the_recorder
        self._file.close()
        self._file=None
        self._valid=False
        DR_On=False
        _the_recorder=None
    def start(self):
        global DR_On, _the_recorder
        if self._file is None and self._file_name:
            self._file=file(self._file_name, 'ab')
            self._valid=True
        DR_On=True
        _the_recorder=self
    def pause(self, data_type=DR_Type_All):
        self._pause_type=data_type
    def unpause(self):
        self._pause=None
class DR_Read_File(DR_File):
    def __init__(self, file_name):
        super(DR_Read_File, self).__init__(file_name)
        if self._check_header():
            self._valid=True
            self._file=file(self._file_name, 'rb')
        else:
            self._valid=False
        self._data=[]
        self._read_data()
        self._start_index=0
        self._end_index=len(self._data)
        self._current_index=0
    def _read_rec(self):
        _marker=self._file.read(len(DR_Rec_Marker))
        if _marker!=DR_Rec_Marker:
            return None
        _slen=self._file.read(struct.calcsize('L'))
        _data_len=struct.unpack('L', _slen)[0]
        _sdata=self._file.read(_data_len)
        try:
            return cPickle.loads(_sdata)
        except UnpicklingError:
            return None
        except:
            if __debug__:
                raise
            return None
    def _read_data(self):
        self._file.seek(len(DR_Signature)+len(DR_Version))
        _rec=self._read_rec()
        while _rec:
            self._data.append(_rec)
            _rec=self._read_rec()
        self._file.close()
    def get_string_data(self):
        _s=''
        for e in self._data:
            _s+=`e`
        return _s
    def start(self):
        global DR_Play, _the_player
        self._current_index=self._start_index
        DR_Play=True
        _the_player=self
    def stop(self):
        global DR_Play, _the_player
        DR_Play=False
        _the_player=None
    def get_data(self, data_type):
        if self._current_index>=self._end_index:
            raise DataRecordingError('No more data available for playback')
        _idx=self._current_index
        for i,e in enumerate(self._data[_idx:]):
            if e._type==data_type:
                self._current_index+=i+1
                return e.get()
        raise DataRecordingError('Failed to find playback data type')
