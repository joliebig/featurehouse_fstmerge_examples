"""Various descriptions of data specific to the Samsung SCH-A950 Phone"""
from prototypes import *
from prototypes_samsung import *
from p_brew import *
UINT=UINTlsb
BOOL=BOOLlsb
RT_PATH='brew/16452/mr'
RT_PATH2='brew/16452/lk/mr'
RT_INDEX_FILE_NAME=RT_PATH+'/MrInfo.db'
RT_EXCLUDED_FILES=('MrInfo.db',)
SND_PATH='brew/16452/ms'
SND_PATH2='brew/16452/lk/ms'
SND_INDEX_FILE_NAME=SND_PATH+'/MsInfo.db'
SND_EXCLUDED_FILES=('MsInfo.db', 'ExInfo.db')
PIC_PATH='brew/16452/mp'
PIC_PATH2='brew/16452/lk/mp'
PIC_INDEX_FILE_NAME=PIC_PATH+'/Default Album.alb'
PIC_EXCLUDED_FILES=('Default Album.alb', 'Graphics.alb')
PREF_DB_FILE_NAME='current_prefs.db'
GROUP_INDEX_FILE_NAME='pb/pbgroups_'
CAL_PATH='sch_event'
CAL_INDEX_FILE_NAME=CAL_PATH+'/usr_tsk'
CAL_FILE_NAME_PREFIX=CAL_PATH+'/usr_tsk_'
CAL_MAX_EVENTS=100
NP_MAX_ENTRIES=30
NP_MAX_LEN=130
NP_PATH=CAL_PATH
NP_FILE_NAME_PREFIX=CAL_FILE_NAME_PREFIX
PB_PATH='pb'
PB_JRNL_FILE_PREFIX=PB_PATH+'/jrnl_'
PB_ENTRY_FILE_PREFIX=PB_PATH+'/recs_'
PB_MAIN_FILE_PREFIX=PB_PATH+'/main_'
PB_WP_CACHE_PATH='cache/pb'
PB_MAX_NAME_LEN=32
PB_MAX_EMAIL_LEN=48
PB_MAX_NUMBER_LEN=48
PB_FLG_NONE=0x0000
PB_FLG_NAME=0x0001
PB_FLG_DATE=0x0400
PB_FLG_FAX=0x0080
PB_FLG_CELL=0x0020
PB_FLG_WORK=0x0010
PB_FLG_HOME=0X0008
PB_FLG_EMAIL2=0X0004
PB_FLG_EMAIL=0X0002
PB_FLG_WP=0X8000
PB_FLG_GROUP=0X0800
PB_FLG_CELL2=0X0100
PB_FLG_SPEEDDIAL=0x01
PB_FLG_RINGTONE=0x10
PB_FLG_PRIMARY=0x02
SS_CMD_SW_VERSION=0
SS_CMD_HW_VERSION=1
SS_CMD_PB_COUNT=2
SS_CMD_PB_VOICEMAIL_READ=5
SS_CMD_PB_VOICEMAIL_WRITE=6
SS_CMD_PB_READ=0x14
SS_CMD_PB_WRITE=0x15
SS_CMD_PB_CLEAR=0x1D
SS_CMD_PB_VOICEMAIL_PARAM=0x19
PB_DEFAULT_VOICEMAIL_NUMBER='*86'
class DefaultResponse(BaseProtogenClass):
    __fields=['data']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(DefaultResponse,self).__init__(**dict)
        if self.__class__ is DefaultResponse:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(DefaultResponse,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(DefaultResponse,kwargs)
        if len(args):
            dict2={}
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_data=DATA(*args,**dict2)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_data.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_data=DATA()
        self.__field_data.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_data(self):
        return self.__field_data.getvalue()
    def __setfield_data(self, value):
        if isinstance(value,DATA):
            self.__field_data=value
        else:
            self.__field_data=DATA(value,)
    def __delfield_data(self): del self.__field_data
    data=property(__getfield_data, __setfield_data, __delfield_data, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('data', self.__field_data, None)
class WRingtoneIndexEntry(BaseProtogenClass):
    __fields=['name', 'path_prefix', 'pathname', 'eor']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(WRingtoneIndexEntry,self).__init__(**dict)
        if self.__class__ is WRingtoneIndexEntry:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(WRingtoneIndexEntry,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(WRingtoneIndexEntry,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
        if getattr(self, '__field_name', None) is None:
            self.__field_name=STRING()
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_path_prefix
        except:
            self.__field_path_prefix=STRING(**{ 'terminator': None,               'default': '/ff/' })
        self.__field_path_prefix.writetobuffer(buf)
        self.__field_pathname.writetobuffer(buf)
        try: self.__field_eor
        except:
            self.__field_eor=STRING(**{ 'terminator': None,               'default': '|2\x0A' })
        self.__field_eor.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_path_prefix=STRING(**{ 'terminator': None,               'default': '/ff/' })
        self.__field_path_prefix.readfrombuffer(buf)
        self.__field_pathname=STRING(**{ 'terminator': None })
        self.__field_pathname.readfrombuffer(buf)
        self.__field_eor=STRING(**{ 'terminator': None,               'default': '|2\x0A' })
        self.__field_eor.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_name(self):
        return self.__field_name.getvalue()
    def __setfield_name(self, value):
        if isinstance(value,STRING):
            self.__field_name=value
        else:
            self.__field_name=STRING(value,)
    def __delfield_name(self): del self.__field_name
    name=property(__getfield_name, __setfield_name, __delfield_name, None)
    def __getfield_path_prefix(self):
        try: self.__field_path_prefix
        except:
            self.__field_path_prefix=STRING(**{ 'terminator': None,               'default': '/ff/' })
        return self.__field_path_prefix.getvalue()
    def __setfield_path_prefix(self, value):
        if isinstance(value,STRING):
            self.__field_path_prefix=value
        else:
            self.__field_path_prefix=STRING(value,**{ 'terminator': None,               'default': '/ff/' })
    def __delfield_path_prefix(self): del self.__field_path_prefix
    path_prefix=property(__getfield_path_prefix, __setfield_path_prefix, __delfield_path_prefix, None)
    def __getfield_pathname(self):
        return self.__field_pathname.getvalue()
    def __setfield_pathname(self, value):
        if isinstance(value,STRING):
            self.__field_pathname=value
        else:
            self.__field_pathname=STRING(value,**{ 'terminator': None })
    def __delfield_pathname(self): del self.__field_pathname
    pathname=property(__getfield_pathname, __setfield_pathname, __delfield_pathname, None)
    def __getfield_eor(self):
        try: self.__field_eor
        except:
            self.__field_eor=STRING(**{ 'terminator': None,               'default': '|2\x0A' })
        return self.__field_eor.getvalue()
    def __setfield_eor(self, value):
        if isinstance(value,STRING):
            self.__field_eor=value
        else:
            self.__field_eor=STRING(value,**{ 'terminator': None,               'default': '|2\x0A' })
    def __delfield_eor(self): del self.__field_eor
    eor=property(__getfield_eor, __setfield_eor, __delfield_eor, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('name', self.__field_name, None)
        yield ('path_prefix', self.__field_path_prefix, None)
        yield ('pathname', self.__field_pathname, None)
        yield ('eor', self.__field_eor, None)
class WRingtoneIndexFile(BaseProtogenClass):
    __fields=['items']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(WRingtoneIndexFile,self).__init__(**dict)
        if self.__class__ is WRingtoneIndexFile:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(WRingtoneIndexFile,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(WRingtoneIndexFile,kwargs)
        if len(args):
            dict2={ 'elementclass': WRingtoneIndexEntry }
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_items=LIST(*args,**dict2)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_items
        except:
            self.__field_items=LIST(**{ 'elementclass': WRingtoneIndexEntry })
        self.__field_items.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_items=LIST(**{ 'elementclass': WRingtoneIndexEntry })
        self.__field_items.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_items(self):
        try: self.__field_items
        except:
            self.__field_items=LIST(**{ 'elementclass': WRingtoneIndexEntry })
        return self.__field_items.getvalue()
    def __setfield_items(self, value):
        if isinstance(value,LIST):
            self.__field_items=value
        else:
            self.__field_items=LIST(value,**{ 'elementclass': WRingtoneIndexEntry })
    def __delfield_items(self): del self.__field_items
    items=property(__getfield_items, __setfield_items, __delfield_items, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('items', self.__field_items, None)
class RRingtoneIndexEntry(BaseProtogenClass):
    __fields=['pathname', 'misc']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(RRingtoneIndexEntry,self).__init__(**dict)
        if self.__class__ is RRingtoneIndexEntry:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(RRingtoneIndexEntry,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(RRingtoneIndexEntry,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_pathname.writetobuffer(buf)
        self.__field_misc.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_pathname=STRING(**{ 'terminator': 0x7C })
        self.__field_pathname.readfrombuffer(buf)
        self.__field_misc=STRING(**{ 'terminator': 0x0A })
        self.__field_misc.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_pathname(self):
        return self.__field_pathname.getvalue()
    def __setfield_pathname(self, value):
        if isinstance(value,STRING):
            self.__field_pathname=value
        else:
            self.__field_pathname=STRING(value,**{ 'terminator': 0x7C })
    def __delfield_pathname(self): del self.__field_pathname
    pathname=property(__getfield_pathname, __setfield_pathname, __delfield_pathname, None)
    def __getfield_misc(self):
        return self.__field_misc.getvalue()
    def __setfield_misc(self, value):
        if isinstance(value,STRING):
            self.__field_misc=value
        else:
            self.__field_misc=STRING(value,**{ 'terminator': 0x0A })
    def __delfield_misc(self): del self.__field_misc
    misc=property(__getfield_misc, __setfield_misc, __delfield_misc, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('pathname', self.__field_pathname, None)
        yield ('misc', self.__field_misc, None)
class RRingtoneIndexFile(BaseProtogenClass):
    __fields=['items']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(RRingtoneIndexFile,self).__init__(**dict)
        if self.__class__ is RRingtoneIndexFile:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(RRingtoneIndexFile,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(RRingtoneIndexFile,kwargs)
        if len(args):
            dict2={ 'elementclass': RRingtoneIndexEntry }
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_items=LIST(*args,**dict2)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_items
        except:
            self.__field_items=LIST(**{ 'elementclass': RRingtoneIndexEntry })
        self.__field_items.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_items=LIST(**{ 'elementclass': RRingtoneIndexEntry })
        self.__field_items.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_items(self):
        try: self.__field_items
        except:
            self.__field_items=LIST(**{ 'elementclass': RRingtoneIndexEntry })
        return self.__field_items.getvalue()
    def __setfield_items(self, value):
        if isinstance(value,LIST):
            self.__field_items=value
        else:
            self.__field_items=LIST(value,**{ 'elementclass': RRingtoneIndexEntry })
    def __delfield_items(self): del self.__field_items
    items=property(__getfield_items, __setfield_items, __delfield_items, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('items', self.__field_items, None)
class WSoundsIndexEntry(BaseProtogenClass):
    __fields=['name', 'path_prefix', 'pathname', 'eor']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(WSoundsIndexEntry,self).__init__(**dict)
        if self.__class__ is WSoundsIndexEntry:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(WSoundsIndexEntry,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(WSoundsIndexEntry,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
        if getattr(self, '__field_name', None) is None:
            self.__field_name=STRING()
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_path_prefix
        except:
            self.__field_path_prefix=STRING(**{ 'terminator': None,               'default': '/ff/' })
        self.__field_path_prefix.writetobuffer(buf)
        self.__field_pathname.writetobuffer(buf)
        try: self.__field_eor
        except:
            self.__field_eor=STRING(**{ 'terminator': None,               'default': '|0|7\x0A' })
        self.__field_eor.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_path_prefix=STRING(**{ 'terminator': None,               'default': '/ff/' })
        self.__field_path_prefix.readfrombuffer(buf)
        self.__field_pathname=STRING(**{ 'terminator': None })
        self.__field_pathname.readfrombuffer(buf)
        self.__field_eor=STRING(**{ 'terminator': None,               'default': '|0|7\x0A' })
        self.__field_eor.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_name(self):
        return self.__field_name.getvalue()
    def __setfield_name(self, value):
        if isinstance(value,STRING):
            self.__field_name=value
        else:
            self.__field_name=STRING(value,)
    def __delfield_name(self): del self.__field_name
    name=property(__getfield_name, __setfield_name, __delfield_name, None)
    def __getfield_path_prefix(self):
        try: self.__field_path_prefix
        except:
            self.__field_path_prefix=STRING(**{ 'terminator': None,               'default': '/ff/' })
        return self.__field_path_prefix.getvalue()
    def __setfield_path_prefix(self, value):
        if isinstance(value,STRING):
            self.__field_path_prefix=value
        else:
            self.__field_path_prefix=STRING(value,**{ 'terminator': None,               'default': '/ff/' })
    def __delfield_path_prefix(self): del self.__field_path_prefix
    path_prefix=property(__getfield_path_prefix, __setfield_path_prefix, __delfield_path_prefix, None)
    def __getfield_pathname(self):
        return self.__field_pathname.getvalue()
    def __setfield_pathname(self, value):
        if isinstance(value,STRING):
            self.__field_pathname=value
        else:
            self.__field_pathname=STRING(value,**{ 'terminator': None })
    def __delfield_pathname(self): del self.__field_pathname
    pathname=property(__getfield_pathname, __setfield_pathname, __delfield_pathname, None)
    def __getfield_eor(self):
        try: self.__field_eor
        except:
            self.__field_eor=STRING(**{ 'terminator': None,               'default': '|0|7\x0A' })
        return self.__field_eor.getvalue()
    def __setfield_eor(self, value):
        if isinstance(value,STRING):
            self.__field_eor=value
        else:
            self.__field_eor=STRING(value,**{ 'terminator': None,               'default': '|0|7\x0A' })
    def __delfield_eor(self): del self.__field_eor
    eor=property(__getfield_eor, __setfield_eor, __delfield_eor, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('name', self.__field_name, None)
        yield ('path_prefix', self.__field_path_prefix, None)
        yield ('pathname', self.__field_pathname, None)
        yield ('eor', self.__field_eor, None)
class WSoundsIndexFile(BaseProtogenClass):
    __fields=['items']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(WSoundsIndexFile,self).__init__(**dict)
        if self.__class__ is WSoundsIndexFile:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(WSoundsIndexFile,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(WSoundsIndexFile,kwargs)
        if len(args):
            dict2={ 'elementclass': WSoundsIndexEntry }
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_items=LIST(*args,**dict2)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_items
        except:
            self.__field_items=LIST(**{ 'elementclass': WSoundsIndexEntry })
        self.__field_items.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_items=LIST(**{ 'elementclass': WSoundsIndexEntry })
        self.__field_items.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_items(self):
        try: self.__field_items
        except:
            self.__field_items=LIST(**{ 'elementclass': WSoundsIndexEntry })
        return self.__field_items.getvalue()
    def __setfield_items(self, value):
        if isinstance(value,LIST):
            self.__field_items=value
        else:
            self.__field_items=LIST(value,**{ 'elementclass': WSoundsIndexEntry })
    def __delfield_items(self): del self.__field_items
    items=property(__getfield_items, __setfield_items, __delfield_items, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('items', self.__field_items, None)
class RSoundIndexEntry(BaseProtogenClass):
    __fields=['pathname', 'misc']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(RSoundIndexEntry,self).__init__(**dict)
        if self.__class__ is RSoundIndexEntry:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(RSoundIndexEntry,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(RSoundIndexEntry,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_pathname.writetobuffer(buf)
        self.__field_misc.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_pathname=STRING(**{ 'terminator': 0x7C })
        self.__field_pathname.readfrombuffer(buf)
        self.__field_misc=STRING(**{ 'terminator': 0x0A })
        self.__field_misc.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_pathname(self):
        return self.__field_pathname.getvalue()
    def __setfield_pathname(self, value):
        if isinstance(value,STRING):
            self.__field_pathname=value
        else:
            self.__field_pathname=STRING(value,**{ 'terminator': 0x7C })
    def __delfield_pathname(self): del self.__field_pathname
    pathname=property(__getfield_pathname, __setfield_pathname, __delfield_pathname, None)
    def __getfield_misc(self):
        return self.__field_misc.getvalue()
    def __setfield_misc(self, value):
        if isinstance(value,STRING):
            self.__field_misc=value
        else:
            self.__field_misc=STRING(value,**{ 'terminator': 0x0A })
    def __delfield_misc(self): del self.__field_misc
    misc=property(__getfield_misc, __setfield_misc, __delfield_misc, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('pathname', self.__field_pathname, None)
        yield ('misc', self.__field_misc, None)
class RSoundsIndexFile(BaseProtogenClass):
    __fields=['items']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(RSoundsIndexFile,self).__init__(**dict)
        if self.__class__ is RSoundsIndexFile:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(RSoundsIndexFile,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(RSoundsIndexFile,kwargs)
        if len(args):
            dict2={ 'elementclass': RSoundIndexEntry }
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_items=LIST(*args,**dict2)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_items
        except:
            self.__field_items=LIST(**{ 'elementclass': RSoundIndexEntry })
        self.__field_items.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_items=LIST(**{ 'elementclass': RSoundIndexEntry })
        self.__field_items.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_items(self):
        try: self.__field_items
        except:
            self.__field_items=LIST(**{ 'elementclass': RSoundIndexEntry })
        return self.__field_items.getvalue()
    def __setfield_items(self, value):
        if isinstance(value,LIST):
            self.__field_items=value
        else:
            self.__field_items=LIST(value,**{ 'elementclass': RSoundIndexEntry })
    def __delfield_items(self): del self.__field_items
    items=property(__getfield_items, __setfield_items, __delfield_items, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('items', self.__field_items, None)
class WPictureIndexEntry(BaseProtogenClass):
    __fields=['name', 'path_prefix', 'pathname', 'eor']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(WPictureIndexEntry,self).__init__(**dict)
        if self.__class__ is WPictureIndexEntry:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(WPictureIndexEntry,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(WPictureIndexEntry,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_name.writetobuffer(buf)
        try: self.__field_path_prefix
        except:
            self.__field_path_prefix=STRING(**{ 'terminator': None,               'default': '|/ff/' })
        self.__field_path_prefix.writetobuffer(buf)
        self.__field_pathname.writetobuffer(buf)
        try: self.__field_eor
        except:
            self.__field_eor=STRING(**{ 'terminator': None,               'default': '|0|0|3|>\x0A\xF4' })
        self.__field_eor.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_name=STRING(**{ 'terminator': None })
        self.__field_name.readfrombuffer(buf)
        self.__field_path_prefix=STRING(**{ 'terminator': None,               'default': '|/ff/' })
        self.__field_path_prefix.readfrombuffer(buf)
        self.__field_pathname=STRING(**{ 'terminator': None })
        self.__field_pathname.readfrombuffer(buf)
        self.__field_eor=STRING(**{ 'terminator': None,               'default': '|0|0|3|>\x0A\xF4' })
        self.__field_eor.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_name(self):
        return self.__field_name.getvalue()
    def __setfield_name(self, value):
        if isinstance(value,STRING):
            self.__field_name=value
        else:
            self.__field_name=STRING(value,**{ 'terminator': None })
    def __delfield_name(self): del self.__field_name
    name=property(__getfield_name, __setfield_name, __delfield_name, None)
    def __getfield_path_prefix(self):
        try: self.__field_path_prefix
        except:
            self.__field_path_prefix=STRING(**{ 'terminator': None,               'default': '|/ff/' })
        return self.__field_path_prefix.getvalue()
    def __setfield_path_prefix(self, value):
        if isinstance(value,STRING):
            self.__field_path_prefix=value
        else:
            self.__field_path_prefix=STRING(value,**{ 'terminator': None,               'default': '|/ff/' })
    def __delfield_path_prefix(self): del self.__field_path_prefix
    path_prefix=property(__getfield_path_prefix, __setfield_path_prefix, __delfield_path_prefix, None)
    def __getfield_pathname(self):
        return self.__field_pathname.getvalue()
    def __setfield_pathname(self, value):
        if isinstance(value,STRING):
            self.__field_pathname=value
        else:
            self.__field_pathname=STRING(value,**{ 'terminator': None })
    def __delfield_pathname(self): del self.__field_pathname
    pathname=property(__getfield_pathname, __setfield_pathname, __delfield_pathname, None)
    def __getfield_eor(self):
        try: self.__field_eor
        except:
            self.__field_eor=STRING(**{ 'terminator': None,               'default': '|0|0|3|>\x0A\xF4' })
        return self.__field_eor.getvalue()
    def __setfield_eor(self, value):
        if isinstance(value,STRING):
            self.__field_eor=value
        else:
            self.__field_eor=STRING(value,**{ 'terminator': None,               'default': '|0|0|3|>\x0A\xF4' })
    def __delfield_eor(self): del self.__field_eor
    eor=property(__getfield_eor, __setfield_eor, __delfield_eor, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('name', self.__field_name, None)
        yield ('path_prefix', self.__field_path_prefix, None)
        yield ('pathname', self.__field_pathname, None)
        yield ('eor', self.__field_eor, None)
class WPictureIndexFile(BaseProtogenClass):
    __fields=['header', 'items']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(WPictureIndexFile,self).__init__(**dict)
        if self.__class__ is WPictureIndexFile:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(WPictureIndexFile,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(WPictureIndexFile,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_header
        except:
            self.__field_header=STRING(**{ 'terminator': None,               'default': '0|/ff/brew/16452/mp/Default Album|\x0A\x0A\xF4' })
        self.__field_header.writetobuffer(buf)
        try: self.__field_items
        except:
            self.__field_items=LIST(**{ 'elementclass': WPictureIndexEntry })
        self.__field_items.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_header=STRING(**{ 'terminator': None,               'default': '0|/ff/brew/16452/mp/Default Album|\x0A\x0A\xF4' })
        self.__field_header.readfrombuffer(buf)
        self.__field_items=LIST(**{ 'elementclass': WPictureIndexEntry })
        self.__field_items.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_header(self):
        try: self.__field_header
        except:
            self.__field_header=STRING(**{ 'terminator': None,               'default': '0|/ff/brew/16452/mp/Default Album|\x0A\x0A\xF4' })
        return self.__field_header.getvalue()
    def __setfield_header(self, value):
        if isinstance(value,STRING):
            self.__field_header=value
        else:
            self.__field_header=STRING(value,**{ 'terminator': None,               'default': '0|/ff/brew/16452/mp/Default Album|\x0A\x0A\xF4' })
    def __delfield_header(self): del self.__field_header
    header=property(__getfield_header, __setfield_header, __delfield_header, None)
    def __getfield_items(self):
        try: self.__field_items
        except:
            self.__field_items=LIST(**{ 'elementclass': WPictureIndexEntry })
        return self.__field_items.getvalue()
    def __setfield_items(self, value):
        if isinstance(value,LIST):
            self.__field_items=value
        else:
            self.__field_items=LIST(value,**{ 'elementclass': WPictureIndexEntry })
    def __delfield_items(self): del self.__field_items
    items=property(__getfield_items, __setfield_items, __delfield_items, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('header', self.__field_header, None)
        yield ('items', self.__field_items, None)
class RPictureIndexEntry(BaseProtogenClass):
    __fields=['name', 'pathname', 'misc']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(RPictureIndexEntry,self).__init__(**dict)
        if self.__class__ is RPictureIndexEntry:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(RPictureIndexEntry,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(RPictureIndexEntry,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_name.writetobuffer(buf)
        self.__field_pathname.writetobuffer(buf)
        self.__field_misc.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_name=STRING(**{ 'terminator': 0x7C })
        self.__field_name.readfrombuffer(buf)
        self.__field_pathname=STRING(**{ 'terminator': 0x7C })
        self.__field_pathname.readfrombuffer(buf)
        self.__field_misc=STRING(**{ 'terminator': 0xF4,               'raiseonunterminatedread': False })
        self.__field_misc.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_name(self):
        return self.__field_name.getvalue()
    def __setfield_name(self, value):
        if isinstance(value,STRING):
            self.__field_name=value
        else:
            self.__field_name=STRING(value,**{ 'terminator': 0x7C })
    def __delfield_name(self): del self.__field_name
    name=property(__getfield_name, __setfield_name, __delfield_name, None)
    def __getfield_pathname(self):
        return self.__field_pathname.getvalue()
    def __setfield_pathname(self, value):
        if isinstance(value,STRING):
            self.__field_pathname=value
        else:
            self.__field_pathname=STRING(value,**{ 'terminator': 0x7C })
    def __delfield_pathname(self): del self.__field_pathname
    pathname=property(__getfield_pathname, __setfield_pathname, __delfield_pathname, None)
    def __getfield_misc(self):
        return self.__field_misc.getvalue()
    def __setfield_misc(self, value):
        if isinstance(value,STRING):
            self.__field_misc=value
        else:
            self.__field_misc=STRING(value,**{ 'terminator': 0xF4,               'raiseonunterminatedread': False })
    def __delfield_misc(self): del self.__field_misc
    misc=property(__getfield_misc, __setfield_misc, __delfield_misc, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('name', self.__field_name, None)
        yield ('pathname', self.__field_pathname, None)
        yield ('misc', self.__field_misc, None)
class RPictureIndexFile(BaseProtogenClass):
    __fields=['items']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(RPictureIndexFile,self).__init__(**dict)
        if self.__class__ is RPictureIndexFile:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(RPictureIndexFile,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(RPictureIndexFile,kwargs)
        if len(args):
            dict2={ 'elementclass': RPictureIndexEntry }
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_items=LIST(*args,**dict2)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_items
        except:
            self.__field_items=LIST(**{ 'elementclass': RPictureIndexEntry })
        self.__field_items.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_items=LIST(**{ 'elementclass': RPictureIndexEntry })
        self.__field_items.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_items(self):
        try: self.__field_items
        except:
            self.__field_items=LIST(**{ 'elementclass': RPictureIndexEntry })
        return self.__field_items.getvalue()
    def __setfield_items(self, value):
        if isinstance(value,LIST):
            self.__field_items=value
        else:
            self.__field_items=LIST(value,**{ 'elementclass': RPictureIndexEntry })
    def __delfield_items(self): del self.__field_items
    items=property(__getfield_items, __setfield_items, __delfield_items, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('items', self.__field_items, None)
class GroupEntry(BaseProtogenClass):
    __fields=['index', 'dunno1', 'datetime', 'name', 'numofmembers', 'members']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(GroupEntry,self).__init__(**dict)
        if self.__class__ is GroupEntry:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(GroupEntry,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(GroupEntry,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_index.writetobuffer(buf)
        self.__field_dunno1.writetobuffer(buf)
        self.__field_datetime.writetobuffer(buf)
        self.__field_name.writetobuffer(buf)
        self.__field_numofmembers.writetobuffer(buf)
        if self.numofmembers:
            self.__field_members.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_index=UINT(**{'sizeinbytes': 1})
        self.__field_index.readfrombuffer(buf)
        self.__field_dunno1=UNKNOWN(**{'sizeinbytes': 4})
        self.__field_dunno1.readfrombuffer(buf)
        self.__field_datetime=DateTime(**{'sizeinbytes': 4})
        self.__field_datetime.readfrombuffer(buf)
        self.__field_name=STRING(**{'sizeinbytes': 68,  'terminator': 0 })
        self.__field_name.readfrombuffer(buf)
        self.__field_numofmembers=UINT(**{'sizeinbytes': 2})
        self.__field_numofmembers.readfrombuffer(buf)
        if self.numofmembers:
            self.__field_members=LIST(**{'elementclass': _gen_p_samsungscha950_150,  'length': self.numofmembers })
            self.__field_members.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_index(self):
        return self.__field_index.getvalue()
    def __setfield_index(self, value):
        if isinstance(value,UINT):
            self.__field_index=value
        else:
            self.__field_index=UINT(value,**{'sizeinbytes': 1})
    def __delfield_index(self): del self.__field_index
    index=property(__getfield_index, __setfield_index, __delfield_index, None)
    def __getfield_dunno1(self):
        return self.__field_dunno1.getvalue()
    def __setfield_dunno1(self, value):
        if isinstance(value,UNKNOWN):
            self.__field_dunno1=value
        else:
            self.__field_dunno1=UNKNOWN(value,**{'sizeinbytes': 4})
    def __delfield_dunno1(self): del self.__field_dunno1
    dunno1=property(__getfield_dunno1, __setfield_dunno1, __delfield_dunno1, None)
    def __getfield_datetime(self):
        return self.__field_datetime.getvalue()
    def __setfield_datetime(self, value):
        if isinstance(value,DateTime):
            self.__field_datetime=value
        else:
            self.__field_datetime=DateTime(value,**{'sizeinbytes': 4})
    def __delfield_datetime(self): del self.__field_datetime
    datetime=property(__getfield_datetime, __setfield_datetime, __delfield_datetime, None)
    def __getfield_name(self):
        return self.__field_name.getvalue()
    def __setfield_name(self, value):
        if isinstance(value,STRING):
            self.__field_name=value
        else:
            self.__field_name=STRING(value,**{'sizeinbytes': 68,  'terminator': 0 })
    def __delfield_name(self): del self.__field_name
    name=property(__getfield_name, __setfield_name, __delfield_name, None)
    def __getfield_numofmembers(self):
        return self.__field_numofmembers.getvalue()
    def __setfield_numofmembers(self, value):
        if isinstance(value,UINT):
            self.__field_numofmembers=value
        else:
            self.__field_numofmembers=UINT(value,**{'sizeinbytes': 2})
    def __delfield_numofmembers(self): del self.__field_numofmembers
    numofmembers=property(__getfield_numofmembers, __setfield_numofmembers, __delfield_numofmembers, None)
    def __getfield_members(self):
        return self.__field_members.getvalue()
    def __setfield_members(self, value):
        if isinstance(value,LIST):
            self.__field_members=value
        else:
            self.__field_members=LIST(value,**{'elementclass': _gen_p_samsungscha950_150,  'length': self.numofmembers })
    def __delfield_members(self): del self.__field_members
    members=property(__getfield_members, __setfield_members, __delfield_members, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('index', self.__field_index, None)
        yield ('dunno1', self.__field_dunno1, None)
        yield ('datetime', self.__field_datetime, None)
        yield ('name', self.__field_name, None)
        yield ('numofmembers', self.__field_numofmembers, None)
        if self.numofmembers:
            yield ('members', self.__field_members, None)
class _gen_p_samsungscha950_150(BaseProtogenClass):
    'Anonymous inner class'
    __fields=['index']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(_gen_p_samsungscha950_150,self).__init__(**dict)
        if self.__class__ is _gen_p_samsungscha950_150:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(_gen_p_samsungscha950_150,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(_gen_p_samsungscha950_150,kwargs)
        if len(args):
            dict2={'sizeinbytes': 2}
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_index=UINT(*args,**dict2)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_index.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_index=UINT(**{'sizeinbytes': 2})
        self.__field_index.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_index(self):
        return self.__field_index.getvalue()
    def __setfield_index(self, value):
        if isinstance(value,UINT):
            self.__field_index=value
        else:
            self.__field_index=UINT(value,**{'sizeinbytes': 2})
    def __delfield_index(self): del self.__field_index
    index=property(__getfield_index, __setfield_index, __delfield_index, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('index', self.__field_index, None)
class GroupIndexFile(BaseProtogenClass):
    __fields=['num_of_entries', 'items']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(GroupIndexFile,self).__init__(**dict)
        if self.__class__ is GroupIndexFile:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(GroupIndexFile,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(GroupIndexFile,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_num_of_entries.writetobuffer(buf)
        try: self.__field_items
        except:
            self.__field_items=LIST(**{ 'elementclass': GroupEntry })
        self.__field_items.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_num_of_entries=UINT(**{'sizeinbytes': 1})
        self.__field_num_of_entries.readfrombuffer(buf)
        self.__field_items=LIST(**{ 'elementclass': GroupEntry })
        self.__field_items.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_num_of_entries(self):
        return self.__field_num_of_entries.getvalue()
    def __setfield_num_of_entries(self, value):
        if isinstance(value,UINT):
            self.__field_num_of_entries=value
        else:
            self.__field_num_of_entries=UINT(value,**{'sizeinbytes': 1})
    def __delfield_num_of_entries(self): del self.__field_num_of_entries
    num_of_entries=property(__getfield_num_of_entries, __setfield_num_of_entries, __delfield_num_of_entries, None)
    def __getfield_items(self):
        try: self.__field_items
        except:
            self.__field_items=LIST(**{ 'elementclass': GroupEntry })
        return self.__field_items.getvalue()
    def __setfield_items(self, value):
        if isinstance(value,LIST):
            self.__field_items=value
        else:
            self.__field_items=LIST(value,**{ 'elementclass': GroupEntry })
    def __delfield_items(self): del self.__field_items
    items=property(__getfield_items, __setfield_items, __delfield_items, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('num_of_entries', self.__field_num_of_entries, None)
        yield ('items', self.__field_items, None)
class CalIndexEntry(BaseProtogenClass):
    __fields=['index']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(CalIndexEntry,self).__init__(**dict)
        if self.__class__ is CalIndexEntry:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(CalIndexEntry,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(CalIndexEntry,kwargs)
        if len(args):
            dict2={'sizeinbytes': 2,  'default': 0 }
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_index=UINT(*args,**dict2)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_index
        except:
            self.__field_index=UINT(**{'sizeinbytes': 2,  'default': 0 })
        self.__field_index.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_index=UINT(**{'sizeinbytes': 2,  'default': 0 })
        self.__field_index.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_index(self):
        try: self.__field_index
        except:
            self.__field_index=UINT(**{'sizeinbytes': 2,  'default': 0 })
        return self.__field_index.getvalue()
    def __setfield_index(self, value):
        if isinstance(value,UINT):
            self.__field_index=value
        else:
            self.__field_index=UINT(value,**{'sizeinbytes': 2,  'default': 0 })
    def __delfield_index(self): del self.__field_index
    index=property(__getfield_index, __setfield_index, __delfield_index, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('index', self.__field_index, None)
class CalIndexFile(BaseProtogenClass):
    __fields=['next_index', 'zero1', 'numofevents', 'zero2', 'numofnotes', 'zero3', 'numofactiveevents', 'zero4', 'events', 'notes', 'activeevents']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(CalIndexFile,self).__init__(**dict)
        if self.__class__ is CalIndexFile:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(CalIndexFile,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(CalIndexFile,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_next_index.writetobuffer(buf)
        try: self.__field_zero1
        except:
            self.__field_zero1=UNKNOWN(**{'sizeinbytes': 12,  'pad': 0 })
        self.__field_zero1.writetobuffer(buf)
        self.__field_numofevents.writetobuffer(buf)
        try: self.__field_zero2
        except:
            self.__field_zero2=UNKNOWN(**{'sizeinbytes': 6,  'pad': 0 })
        self.__field_zero2.writetobuffer(buf)
        self.__field_numofnotes.writetobuffer(buf)
        try: self.__field_zero3
        except:
            self.__field_zero3=UNKNOWN(**{'sizeinbytes': 2,  'pad': 0 })
        self.__field_zero3.writetobuffer(buf)
        self.__field_numofactiveevents.writetobuffer(buf)
        try: self.__field_zero4
        except:
            self.__field_zero4=UNKNOWN(**{'sizeinbytes': 112,  'pad': 0 })
        self.__field_zero4.writetobuffer(buf)
        try: self.__field_events
        except:
            self.__field_events=LIST(**{ 'elementclass': CalIndexEntry,             'length': 103,             'createdefault': True })
        self.__field_events.writetobuffer(buf)
        try: self.__field_notes
        except:
            self.__field_notes=LIST(**{ 'elementclass': CalIndexEntry,             'length': 30,             'createdefault': True })
        self.__field_notes.writetobuffer(buf)
        try: self.__field_activeevents
        except:
            self.__field_activeevents=LIST(**{ 'elementclass': CalIndexEntry,             'length': 324,             'createdefault': True })
        self.__field_activeevents.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_next_index=UINT(**{'sizeinbytes': 2})
        self.__field_next_index.readfrombuffer(buf)
        self.__field_zero1=UNKNOWN(**{'sizeinbytes': 12,  'pad': 0 })
        self.__field_zero1.readfrombuffer(buf)
        self.__field_numofevents=UINT(**{'sizeinbytes': 2})
        self.__field_numofevents.readfrombuffer(buf)
        self.__field_zero2=UNKNOWN(**{'sizeinbytes': 6,  'pad': 0 })
        self.__field_zero2.readfrombuffer(buf)
        self.__field_numofnotes=UINT(**{'sizeinbytes': 2})
        self.__field_numofnotes.readfrombuffer(buf)
        self.__field_zero3=UNKNOWN(**{'sizeinbytes': 2,  'pad': 0 })
        self.__field_zero3.readfrombuffer(buf)
        self.__field_numofactiveevents=UINT(**{'sizeinbytes': 2})
        self.__field_numofactiveevents.readfrombuffer(buf)
        self.__field_zero4=UNKNOWN(**{'sizeinbytes': 112,  'pad': 0 })
        self.__field_zero4.readfrombuffer(buf)
        self.__field_events=LIST(**{ 'elementclass': CalIndexEntry,             'length': 103,             'createdefault': True })
        self.__field_events.readfrombuffer(buf)
        self.__field_notes=LIST(**{ 'elementclass': CalIndexEntry,             'length': 30,             'createdefault': True })
        self.__field_notes.readfrombuffer(buf)
        self.__field_activeevents=LIST(**{ 'elementclass': CalIndexEntry,             'length': 324,             'createdefault': True })
        self.__field_activeevents.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_next_index(self):
        return self.__field_next_index.getvalue()
    def __setfield_next_index(self, value):
        if isinstance(value,UINT):
            self.__field_next_index=value
        else:
            self.__field_next_index=UINT(value,**{'sizeinbytes': 2})
    def __delfield_next_index(self): del self.__field_next_index
    next_index=property(__getfield_next_index, __setfield_next_index, __delfield_next_index, None)
    def __getfield_zero1(self):
        try: self.__field_zero1
        except:
            self.__field_zero1=UNKNOWN(**{'sizeinbytes': 12,  'pad': 0 })
        return self.__field_zero1.getvalue()
    def __setfield_zero1(self, value):
        if isinstance(value,UNKNOWN):
            self.__field_zero1=value
        else:
            self.__field_zero1=UNKNOWN(value,**{'sizeinbytes': 12,  'pad': 0 })
    def __delfield_zero1(self): del self.__field_zero1
    zero1=property(__getfield_zero1, __setfield_zero1, __delfield_zero1, None)
    def __getfield_numofevents(self):
        return self.__field_numofevents.getvalue()
    def __setfield_numofevents(self, value):
        if isinstance(value,UINT):
            self.__field_numofevents=value
        else:
            self.__field_numofevents=UINT(value,**{'sizeinbytes': 2})
    def __delfield_numofevents(self): del self.__field_numofevents
    numofevents=property(__getfield_numofevents, __setfield_numofevents, __delfield_numofevents, None)
    def __getfield_zero2(self):
        try: self.__field_zero2
        except:
            self.__field_zero2=UNKNOWN(**{'sizeinbytes': 6,  'pad': 0 })
        return self.__field_zero2.getvalue()
    def __setfield_zero2(self, value):
        if isinstance(value,UNKNOWN):
            self.__field_zero2=value
        else:
            self.__field_zero2=UNKNOWN(value,**{'sizeinbytes': 6,  'pad': 0 })
    def __delfield_zero2(self): del self.__field_zero2
    zero2=property(__getfield_zero2, __setfield_zero2, __delfield_zero2, None)
    def __getfield_numofnotes(self):
        return self.__field_numofnotes.getvalue()
    def __setfield_numofnotes(self, value):
        if isinstance(value,UINT):
            self.__field_numofnotes=value
        else:
            self.__field_numofnotes=UINT(value,**{'sizeinbytes': 2})
    def __delfield_numofnotes(self): del self.__field_numofnotes
    numofnotes=property(__getfield_numofnotes, __setfield_numofnotes, __delfield_numofnotes, None)
    def __getfield_zero3(self):
        try: self.__field_zero3
        except:
            self.__field_zero3=UNKNOWN(**{'sizeinbytes': 2,  'pad': 0 })
        return self.__field_zero3.getvalue()
    def __setfield_zero3(self, value):
        if isinstance(value,UNKNOWN):
            self.__field_zero3=value
        else:
            self.__field_zero3=UNKNOWN(value,**{'sizeinbytes': 2,  'pad': 0 })
    def __delfield_zero3(self): del self.__field_zero3
    zero3=property(__getfield_zero3, __setfield_zero3, __delfield_zero3, None)
    def __getfield_numofactiveevents(self):
        return self.__field_numofactiveevents.getvalue()
    def __setfield_numofactiveevents(self, value):
        if isinstance(value,UINT):
            self.__field_numofactiveevents=value
        else:
            self.__field_numofactiveevents=UINT(value,**{'sizeinbytes': 2})
    def __delfield_numofactiveevents(self): del self.__field_numofactiveevents
    numofactiveevents=property(__getfield_numofactiveevents, __setfield_numofactiveevents, __delfield_numofactiveevents, None)
    def __getfield_zero4(self):
        try: self.__field_zero4
        except:
            self.__field_zero4=UNKNOWN(**{'sizeinbytes': 112,  'pad': 0 })
        return self.__field_zero4.getvalue()
    def __setfield_zero4(self, value):
        if isinstance(value,UNKNOWN):
            self.__field_zero4=value
        else:
            self.__field_zero4=UNKNOWN(value,**{'sizeinbytes': 112,  'pad': 0 })
    def __delfield_zero4(self): del self.__field_zero4
    zero4=property(__getfield_zero4, __setfield_zero4, __delfield_zero4, None)
    def __getfield_events(self):
        try: self.__field_events
        except:
            self.__field_events=LIST(**{ 'elementclass': CalIndexEntry,             'length': 103,             'createdefault': True })
        return self.__field_events.getvalue()
    def __setfield_events(self, value):
        if isinstance(value,LIST):
            self.__field_events=value
        else:
            self.__field_events=LIST(value,**{ 'elementclass': CalIndexEntry,             'length': 103,             'createdefault': True })
    def __delfield_events(self): del self.__field_events
    events=property(__getfield_events, __setfield_events, __delfield_events, None)
    def __getfield_notes(self):
        try: self.__field_notes
        except:
            self.__field_notes=LIST(**{ 'elementclass': CalIndexEntry,             'length': 30,             'createdefault': True })
        return self.__field_notes.getvalue()
    def __setfield_notes(self, value):
        if isinstance(value,LIST):
            self.__field_notes=value
        else:
            self.__field_notes=LIST(value,**{ 'elementclass': CalIndexEntry,             'length': 30,             'createdefault': True })
    def __delfield_notes(self): del self.__field_notes
    notes=property(__getfield_notes, __setfield_notes, __delfield_notes, None)
    def __getfield_activeevents(self):
        try: self.__field_activeevents
        except:
            self.__field_activeevents=LIST(**{ 'elementclass': CalIndexEntry,             'length': 324,             'createdefault': True })
        return self.__field_activeevents.getvalue()
    def __setfield_activeevents(self, value):
        if isinstance(value,LIST):
            self.__field_activeevents=value
        else:
            self.__field_activeevents=LIST(value,**{ 'elementclass': CalIndexEntry,             'length': 324,             'createdefault': True })
    def __delfield_activeevents(self): del self.__field_activeevents
    activeevents=property(__getfield_activeevents, __setfield_activeevents, __delfield_activeevents, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('next_index', self.__field_next_index, None)
        yield ('zero1', self.__field_zero1, None)
        yield ('numofevents', self.__field_numofevents, None)
        yield ('zero2', self.__field_zero2, None)
        yield ('numofnotes', self.__field_numofnotes, None)
        yield ('zero3', self.__field_zero3, None)
        yield ('numofactiveevents', self.__field_numofactiveevents, None)
        yield ('zero4', self.__field_zero4, None)
        yield ('events', self.__field_events, None)
        yield ('notes', self.__field_notes, None)
        yield ('activeevents', self.__field_activeevents, None)
class CalEntry(BaseProtogenClass):
    __fields=['titlelen', 'title', 'start', 'zero1', 'start2', 'zero2', 'exptime', 'zero3', 'one', 'repeat', 'three', 'alarm', 'alert', 'zero4', 'duration', 'timezone', 'creationtime', 'zero5', 'modifiedtime', 'zero6', 'ringtonelen', 'ringtone', 'zero7']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(CalEntry,self).__init__(**dict)
        if self.__class__ is CalEntry:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(CalEntry,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(CalEntry,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_titlelen.writetobuffer(buf)
        self.__field_title.writetobuffer(buf)
        self.__field_start.writetobuffer(buf)
        try: self.__field_zero1
        except:
            self.__field_zero1=UNKNOWN(**{'sizeinbytes': 4,  'pad': 0 })
        self.__field_zero1.writetobuffer(buf)
        try: self.__field_start2
        except:
            self.__field_start2=DateTime(**{'sizeinbytes': 4,  'default': self.start })
        self.__field_start2.writetobuffer(buf)
        try: self.__field_zero2
        except:
            self.__field_zero2=UNKNOWN(**{'sizeinbytes': 4,  'pad': 0 })
        self.__field_zero2.writetobuffer(buf)
        self.__field_exptime.writetobuffer(buf)
        try: self.__field_zero3
        except:
            self.__field_zero3=UNKNOWN(**{'sizeinbytes': 4,  'pad': 0 })
        self.__field_zero3.writetobuffer(buf)
        try: self.__field_one
        except:
            self.__field_one=UINT(**{'sizeinbytes': 1,  'default': 1 })
        self.__field_one.writetobuffer(buf)
        self.__field_repeat.writetobuffer(buf)
        try: self.__field_three
        except:
            self.__field_three=UINT(**{'sizeinbytes': 1,  'default': 3 })
        self.__field_three.writetobuffer(buf)
        self.__field_alarm.writetobuffer(buf)
        self.__field_alert.writetobuffer(buf)
        try: self.__field_zero4
        except:
            self.__field_zero4=UNKNOWN(**{'sizeinbytes': 6,  'pad': 0 })
        self.__field_zero4.writetobuffer(buf)
        self.__field_duration.writetobuffer(buf)
        self.__field_timezone.writetobuffer(buf)
        self.__field_creationtime.writetobuffer(buf)
        try: self.__field_zero5
        except:
            self.__field_zero5=UNKNOWN(**{'sizeinbytes': 4,  'pad': 0 })
        self.__field_zero5.writetobuffer(buf)
        self.__field_modifiedtime.writetobuffer(buf)
        try: self.__field_zero6
        except:
            self.__field_zero6=UNKNOWN(**{'sizeinbytes': 4,  'pad': 0 })
        self.__field_zero6.writetobuffer(buf)
        self.__field_ringtonelen.writetobuffer(buf)
        self.__field_ringtone.writetobuffer(buf)
        try: self.__field_zero7
        except:
            self.__field_zero7=UNKNOWN(**{'sizeinbytes': 2,  'pad': 0 })
        self.__field_zero7.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_titlelen=UINT(**{'sizeinbytes': 2})
        self.__field_titlelen.readfrombuffer(buf)
        self.__field_title=STRING(**{ 'sizeinbytes': self.titlelen,               'terminator': None })
        self.__field_title.readfrombuffer(buf)
        self.__field_start=DateTime(**{'sizeinbytes': 4})
        self.__field_start.readfrombuffer(buf)
        self.__field_zero1=UNKNOWN(**{'sizeinbytes': 4,  'pad': 0 })
        self.__field_zero1.readfrombuffer(buf)
        self.__field_start2=DateTime(**{'sizeinbytes': 4,  'default': self.start })
        self.__field_start2.readfrombuffer(buf)
        self.__field_zero2=UNKNOWN(**{'sizeinbytes': 4,  'pad': 0 })
        self.__field_zero2.readfrombuffer(buf)
        self.__field_exptime=ExpiringTime(**{'sizeinbytes': 4})
        self.__field_exptime.readfrombuffer(buf)
        self.__field_zero3=UNKNOWN(**{'sizeinbytes': 4,  'pad': 0 })
        self.__field_zero3.readfrombuffer(buf)
        self.__field_one=UINT(**{'sizeinbytes': 1,  'default': 1 })
        self.__field_one.readfrombuffer(buf)
        self.__field_repeat=UINT(**{'sizeinbytes': 1})
        self.__field_repeat.readfrombuffer(buf)
        self.__field_three=UINT(**{'sizeinbytes': 1,  'default': 3 })
        self.__field_three.readfrombuffer(buf)
        self.__field_alarm=UINT(**{'sizeinbytes': 1})
        self.__field_alarm.readfrombuffer(buf)
        self.__field_alert=UINT(**{'sizeinbytes': 1})
        self.__field_alert.readfrombuffer(buf)
        self.__field_zero4=UNKNOWN(**{'sizeinbytes': 6,  'pad': 0 })
        self.__field_zero4.readfrombuffer(buf)
        self.__field_duration=UINT(**{'sizeinbytes': 4})
        self.__field_duration.readfrombuffer(buf)
        self.__field_timezone=UINT(**{'sizeinbytes': 1})
        self.__field_timezone.readfrombuffer(buf)
        self.__field_creationtime=DateTime(**{'sizeinbytes': 4})
        self.__field_creationtime.readfrombuffer(buf)
        self.__field_zero5=UNKNOWN(**{'sizeinbytes': 4,  'pad': 0 })
        self.__field_zero5.readfrombuffer(buf)
        self.__field_modifiedtime=DateTime(**{'sizeinbytes': 4})
        self.__field_modifiedtime.readfrombuffer(buf)
        self.__field_zero6=UNKNOWN(**{'sizeinbytes': 4,  'pad': 0 })
        self.__field_zero6.readfrombuffer(buf)
        self.__field_ringtonelen=UINT(**{'sizeinbytes': 2})
        self.__field_ringtonelen.readfrombuffer(buf)
        self.__field_ringtone=STRING(**{ 'sizeinbytes': self.ringtonelen,               'terminator': None })
        self.__field_ringtone.readfrombuffer(buf)
        self.__field_zero7=UNKNOWN(**{'sizeinbytes': 2,  'pad': 0 })
        self.__field_zero7.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_titlelen(self):
        return self.__field_titlelen.getvalue()
    def __setfield_titlelen(self, value):
        if isinstance(value,UINT):
            self.__field_titlelen=value
        else:
            self.__field_titlelen=UINT(value,**{'sizeinbytes': 2})
    def __delfield_titlelen(self): del self.__field_titlelen
    titlelen=property(__getfield_titlelen, __setfield_titlelen, __delfield_titlelen, None)
    def __getfield_title(self):
        return self.__field_title.getvalue()
    def __setfield_title(self, value):
        if isinstance(value,STRING):
            self.__field_title=value
        else:
            self.__field_title=STRING(value,**{ 'sizeinbytes': self.titlelen,               'terminator': None })
    def __delfield_title(self): del self.__field_title
    title=property(__getfield_title, __setfield_title, __delfield_title, None)
    def __getfield_start(self):
        return self.__field_start.getvalue()
    def __setfield_start(self, value):
        if isinstance(value,DateTime):
            self.__field_start=value
        else:
            self.__field_start=DateTime(value,**{'sizeinbytes': 4})
    def __delfield_start(self): del self.__field_start
    start=property(__getfield_start, __setfield_start, __delfield_start, None)
    def __getfield_zero1(self):
        try: self.__field_zero1
        except:
            self.__field_zero1=UNKNOWN(**{'sizeinbytes': 4,  'pad': 0 })
        return self.__field_zero1.getvalue()
    def __setfield_zero1(self, value):
        if isinstance(value,UNKNOWN):
            self.__field_zero1=value
        else:
            self.__field_zero1=UNKNOWN(value,**{'sizeinbytes': 4,  'pad': 0 })
    def __delfield_zero1(self): del self.__field_zero1
    zero1=property(__getfield_zero1, __setfield_zero1, __delfield_zero1, None)
    def __getfield_start2(self):
        try: self.__field_start2
        except:
            self.__field_start2=DateTime(**{'sizeinbytes': 4,  'default': self.start })
        return self.__field_start2.getvalue()
    def __setfield_start2(self, value):
        if isinstance(value,DateTime):
            self.__field_start2=value
        else:
            self.__field_start2=DateTime(value,**{'sizeinbytes': 4,  'default': self.start })
    def __delfield_start2(self): del self.__field_start2
    start2=property(__getfield_start2, __setfield_start2, __delfield_start2, None)
    def __getfield_zero2(self):
        try: self.__field_zero2
        except:
            self.__field_zero2=UNKNOWN(**{'sizeinbytes': 4,  'pad': 0 })
        return self.__field_zero2.getvalue()
    def __setfield_zero2(self, value):
        if isinstance(value,UNKNOWN):
            self.__field_zero2=value
        else:
            self.__field_zero2=UNKNOWN(value,**{'sizeinbytes': 4,  'pad': 0 })
    def __delfield_zero2(self): del self.__field_zero2
    zero2=property(__getfield_zero2, __setfield_zero2, __delfield_zero2, None)
    def __getfield_exptime(self):
        return self.__field_exptime.getvalue()
    def __setfield_exptime(self, value):
        if isinstance(value,ExpiringTime):
            self.__field_exptime=value
        else:
            self.__field_exptime=ExpiringTime(value,**{'sizeinbytes': 4})
    def __delfield_exptime(self): del self.__field_exptime
    exptime=property(__getfield_exptime, __setfield_exptime, __delfield_exptime, None)
    def __getfield_zero3(self):
        try: self.__field_zero3
        except:
            self.__field_zero3=UNKNOWN(**{'sizeinbytes': 4,  'pad': 0 })
        return self.__field_zero3.getvalue()
    def __setfield_zero3(self, value):
        if isinstance(value,UNKNOWN):
            self.__field_zero3=value
        else:
            self.__field_zero3=UNKNOWN(value,**{'sizeinbytes': 4,  'pad': 0 })
    def __delfield_zero3(self): del self.__field_zero3
    zero3=property(__getfield_zero3, __setfield_zero3, __delfield_zero3, None)
    def __getfield_one(self):
        try: self.__field_one
        except:
            self.__field_one=UINT(**{'sizeinbytes': 1,  'default': 1 })
        return self.__field_one.getvalue()
    def __setfield_one(self, value):
        if isinstance(value,UINT):
            self.__field_one=value
        else:
            self.__field_one=UINT(value,**{'sizeinbytes': 1,  'default': 1 })
    def __delfield_one(self): del self.__field_one
    one=property(__getfield_one, __setfield_one, __delfield_one, None)
    def __getfield_repeat(self):
        return self.__field_repeat.getvalue()
    def __setfield_repeat(self, value):
        if isinstance(value,UINT):
            self.__field_repeat=value
        else:
            self.__field_repeat=UINT(value,**{'sizeinbytes': 1})
    def __delfield_repeat(self): del self.__field_repeat
    repeat=property(__getfield_repeat, __setfield_repeat, __delfield_repeat, None)
    def __getfield_three(self):
        try: self.__field_three
        except:
            self.__field_three=UINT(**{'sizeinbytes': 1,  'default': 3 })
        return self.__field_three.getvalue()
    def __setfield_three(self, value):
        if isinstance(value,UINT):
            self.__field_three=value
        else:
            self.__field_three=UINT(value,**{'sizeinbytes': 1,  'default': 3 })
    def __delfield_three(self): del self.__field_three
    three=property(__getfield_three, __setfield_three, __delfield_three, None)
    def __getfield_alarm(self):
        return self.__field_alarm.getvalue()
    def __setfield_alarm(self, value):
        if isinstance(value,UINT):
            self.__field_alarm=value
        else:
            self.__field_alarm=UINT(value,**{'sizeinbytes': 1})
    def __delfield_alarm(self): del self.__field_alarm
    alarm=property(__getfield_alarm, __setfield_alarm, __delfield_alarm, None)
    def __getfield_alert(self):
        return self.__field_alert.getvalue()
    def __setfield_alert(self, value):
        if isinstance(value,UINT):
            self.__field_alert=value
        else:
            self.__field_alert=UINT(value,**{'sizeinbytes': 1})
    def __delfield_alert(self): del self.__field_alert
    alert=property(__getfield_alert, __setfield_alert, __delfield_alert, None)
    def __getfield_zero4(self):
        try: self.__field_zero4
        except:
            self.__field_zero4=UNKNOWN(**{'sizeinbytes': 6,  'pad': 0 })
        return self.__field_zero4.getvalue()
    def __setfield_zero4(self, value):
        if isinstance(value,UNKNOWN):
            self.__field_zero4=value
        else:
            self.__field_zero4=UNKNOWN(value,**{'sizeinbytes': 6,  'pad': 0 })
    def __delfield_zero4(self): del self.__field_zero4
    zero4=property(__getfield_zero4, __setfield_zero4, __delfield_zero4, None)
    def __getfield_duration(self):
        return self.__field_duration.getvalue()
    def __setfield_duration(self, value):
        if isinstance(value,UINT):
            self.__field_duration=value
        else:
            self.__field_duration=UINT(value,**{'sizeinbytes': 4})
    def __delfield_duration(self): del self.__field_duration
    duration=property(__getfield_duration, __setfield_duration, __delfield_duration, None)
    def __getfield_timezone(self):
        return self.__field_timezone.getvalue()
    def __setfield_timezone(self, value):
        if isinstance(value,UINT):
            self.__field_timezone=value
        else:
            self.__field_timezone=UINT(value,**{'sizeinbytes': 1})
    def __delfield_timezone(self): del self.__field_timezone
    timezone=property(__getfield_timezone, __setfield_timezone, __delfield_timezone, None)
    def __getfield_creationtime(self):
        return self.__field_creationtime.getvalue()
    def __setfield_creationtime(self, value):
        if isinstance(value,DateTime):
            self.__field_creationtime=value
        else:
            self.__field_creationtime=DateTime(value,**{'sizeinbytes': 4})
    def __delfield_creationtime(self): del self.__field_creationtime
    creationtime=property(__getfield_creationtime, __setfield_creationtime, __delfield_creationtime, None)
    def __getfield_zero5(self):
        try: self.__field_zero5
        except:
            self.__field_zero5=UNKNOWN(**{'sizeinbytes': 4,  'pad': 0 })
        return self.__field_zero5.getvalue()
    def __setfield_zero5(self, value):
        if isinstance(value,UNKNOWN):
            self.__field_zero5=value
        else:
            self.__field_zero5=UNKNOWN(value,**{'sizeinbytes': 4,  'pad': 0 })
    def __delfield_zero5(self): del self.__field_zero5
    zero5=property(__getfield_zero5, __setfield_zero5, __delfield_zero5, None)
    def __getfield_modifiedtime(self):
        return self.__field_modifiedtime.getvalue()
    def __setfield_modifiedtime(self, value):
        if isinstance(value,DateTime):
            self.__field_modifiedtime=value
        else:
            self.__field_modifiedtime=DateTime(value,**{'sizeinbytes': 4})
    def __delfield_modifiedtime(self): del self.__field_modifiedtime
    modifiedtime=property(__getfield_modifiedtime, __setfield_modifiedtime, __delfield_modifiedtime, None)
    def __getfield_zero6(self):
        try: self.__field_zero6
        except:
            self.__field_zero6=UNKNOWN(**{'sizeinbytes': 4,  'pad': 0 })
        return self.__field_zero6.getvalue()
    def __setfield_zero6(self, value):
        if isinstance(value,UNKNOWN):
            self.__field_zero6=value
        else:
            self.__field_zero6=UNKNOWN(value,**{'sizeinbytes': 4,  'pad': 0 })
    def __delfield_zero6(self): del self.__field_zero6
    zero6=property(__getfield_zero6, __setfield_zero6, __delfield_zero6, None)
    def __getfield_ringtonelen(self):
        return self.__field_ringtonelen.getvalue()
    def __setfield_ringtonelen(self, value):
        if isinstance(value,UINT):
            self.__field_ringtonelen=value
        else:
            self.__field_ringtonelen=UINT(value,**{'sizeinbytes': 2})
    def __delfield_ringtonelen(self): del self.__field_ringtonelen
    ringtonelen=property(__getfield_ringtonelen, __setfield_ringtonelen, __delfield_ringtonelen, None)
    def __getfield_ringtone(self):
        return self.__field_ringtone.getvalue()
    def __setfield_ringtone(self, value):
        if isinstance(value,STRING):
            self.__field_ringtone=value
        else:
            self.__field_ringtone=STRING(value,**{ 'sizeinbytes': self.ringtonelen,               'terminator': None })
    def __delfield_ringtone(self): del self.__field_ringtone
    ringtone=property(__getfield_ringtone, __setfield_ringtone, __delfield_ringtone, None)
    def __getfield_zero7(self):
        try: self.__field_zero7
        except:
            self.__field_zero7=UNKNOWN(**{'sizeinbytes': 2,  'pad': 0 })
        return self.__field_zero7.getvalue()
    def __setfield_zero7(self, value):
        if isinstance(value,UNKNOWN):
            self.__field_zero7=value
        else:
            self.__field_zero7=UNKNOWN(value,**{'sizeinbytes': 2,  'pad': 0 })
    def __delfield_zero7(self): del self.__field_zero7
    zero7=property(__getfield_zero7, __setfield_zero7, __delfield_zero7, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('titlelen', self.__field_titlelen, None)
        yield ('title', self.__field_title, None)
        yield ('start', self.__field_start, None)
        yield ('zero1', self.__field_zero1, None)
        yield ('start2', self.__field_start2, None)
        yield ('zero2', self.__field_zero2, None)
        yield ('exptime', self.__field_exptime, None)
        yield ('zero3', self.__field_zero3, None)
        yield ('one', self.__field_one, None)
        yield ('repeat', self.__field_repeat, None)
        yield ('three', self.__field_three, None)
        yield ('alarm', self.__field_alarm, None)
        yield ('alert', self.__field_alert, None)
        yield ('zero4', self.__field_zero4, None)
        yield ('duration', self.__field_duration, None)
        yield ('timezone', self.__field_timezone, None)
        yield ('creationtime', self.__field_creationtime, None)
        yield ('zero5', self.__field_zero5, None)
        yield ('modifiedtime', self.__field_modifiedtime, None)
        yield ('zero6', self.__field_zero6, None)
        yield ('ringtonelen', self.__field_ringtonelen, None)
        yield ('ringtone', self.__field_ringtone, None)
        yield ('zero7', self.__field_zero7, None)
class NotePadEntry(BaseProtogenClass):
    __fields=['textlen', 'text', 'creation', 'zero1', 'creation2', 'zero2', 'five', 'zero3', 'modified', 'zero4', 'modified2', 'zero5']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(NotePadEntry,self).__init__(**dict)
        if self.__class__ is NotePadEntry:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(NotePadEntry,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(NotePadEntry,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_textlen.writetobuffer(buf)
        self.__field_text.writetobuffer(buf)
        self.__field_creation.writetobuffer(buf)
        try: self.__field_zero1
        except:
            self.__field_zero1=UNKNOWN(**{'sizeinbytes': 4,  'pad': 0 })
        self.__field_zero1.writetobuffer(buf)
        try: self.__field_creation2
        except:
            self.__field_creation2=DateTime(**{'sizeinbytes': 4,  'default': self.creation })
        self.__field_creation2.writetobuffer(buf)
        try: self.__field_zero2
        except:
            self.__field_zero2=UNKNOWN(**{'sizeinbytes': 14,  'pad': 0 })
        self.__field_zero2.writetobuffer(buf)
        try: self.__field_five
        except:
            self.__field_five=UINT(**{'sizeinbytes': 1,  'default': 5 })
        self.__field_five.writetobuffer(buf)
        try: self.__field_zero3
        except:
            self.__field_zero3=UNKNOWN(**{'sizeinbytes': 13,  'pad': 0 })
        self.__field_zero3.writetobuffer(buf)
        try: self.__field_modified
        except:
            self.__field_modified=DateTime(**{'sizeinbytes': 4,  'default': self.creation })
        self.__field_modified.writetobuffer(buf)
        try: self.__field_zero4
        except:
            self.__field_zero4=UNKNOWN(**{'sizeinbytes': 4,  'pad': 0 })
        self.__field_zero4.writetobuffer(buf)
        try: self.__field_modified2
        except:
            self.__field_modified2=DateTime(**{'sizeinbytes': 4,  'default': self.modified })
        self.__field_modified2.writetobuffer(buf)
        try: self.__field_zero5
        except:
            self.__field_zero5=UNKNOWN(**{'sizeinbytes': 8,  'pad': 0 })
        self.__field_zero5.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_textlen=UINT(**{'sizeinbytes': 2})
        self.__field_textlen.readfrombuffer(buf)
        self.__field_text=STRING(**{ 'terminator': None,               'sizeinbytes': self.textlen })
        self.__field_text.readfrombuffer(buf)
        self.__field_creation=DateTime(**{'sizeinbytes': 4})
        self.__field_creation.readfrombuffer(buf)
        self.__field_zero1=UNKNOWN(**{'sizeinbytes': 4,  'pad': 0 })
        self.__field_zero1.readfrombuffer(buf)
        self.__field_creation2=DateTime(**{'sizeinbytes': 4,  'default': self.creation })
        self.__field_creation2.readfrombuffer(buf)
        self.__field_zero2=UNKNOWN(**{'sizeinbytes': 14,  'pad': 0 })
        self.__field_zero2.readfrombuffer(buf)
        self.__field_five=UINT(**{'sizeinbytes': 1,  'default': 5 })
        self.__field_five.readfrombuffer(buf)
        self.__field_zero3=UNKNOWN(**{'sizeinbytes': 13,  'pad': 0 })
        self.__field_zero3.readfrombuffer(buf)
        self.__field_modified=DateTime(**{'sizeinbytes': 4,  'default': self.creation })
        self.__field_modified.readfrombuffer(buf)
        self.__field_zero4=UNKNOWN(**{'sizeinbytes': 4,  'pad': 0 })
        self.__field_zero4.readfrombuffer(buf)
        self.__field_modified2=DateTime(**{'sizeinbytes': 4,  'default': self.modified })
        self.__field_modified2.readfrombuffer(buf)
        self.__field_zero5=UNKNOWN(**{'sizeinbytes': 8,  'pad': 0 })
        self.__field_zero5.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_textlen(self):
        return self.__field_textlen.getvalue()
    def __setfield_textlen(self, value):
        if isinstance(value,UINT):
            self.__field_textlen=value
        else:
            self.__field_textlen=UINT(value,**{'sizeinbytes': 2})
    def __delfield_textlen(self): del self.__field_textlen
    textlen=property(__getfield_textlen, __setfield_textlen, __delfield_textlen, None)
    def __getfield_text(self):
        return self.__field_text.getvalue()
    def __setfield_text(self, value):
        if isinstance(value,STRING):
            self.__field_text=value
        else:
            self.__field_text=STRING(value,**{ 'terminator': None,               'sizeinbytes': self.textlen })
    def __delfield_text(self): del self.__field_text
    text=property(__getfield_text, __setfield_text, __delfield_text, None)
    def __getfield_creation(self):
        return self.__field_creation.getvalue()
    def __setfield_creation(self, value):
        if isinstance(value,DateTime):
            self.__field_creation=value
        else:
            self.__field_creation=DateTime(value,**{'sizeinbytes': 4})
    def __delfield_creation(self): del self.__field_creation
    creation=property(__getfield_creation, __setfield_creation, __delfield_creation, None)
    def __getfield_zero1(self):
        try: self.__field_zero1
        except:
            self.__field_zero1=UNKNOWN(**{'sizeinbytes': 4,  'pad': 0 })
        return self.__field_zero1.getvalue()
    def __setfield_zero1(self, value):
        if isinstance(value,UNKNOWN):
            self.__field_zero1=value
        else:
            self.__field_zero1=UNKNOWN(value,**{'sizeinbytes': 4,  'pad': 0 })
    def __delfield_zero1(self): del self.__field_zero1
    zero1=property(__getfield_zero1, __setfield_zero1, __delfield_zero1, None)
    def __getfield_creation2(self):
        try: self.__field_creation2
        except:
            self.__field_creation2=DateTime(**{'sizeinbytes': 4,  'default': self.creation })
        return self.__field_creation2.getvalue()
    def __setfield_creation2(self, value):
        if isinstance(value,DateTime):
            self.__field_creation2=value
        else:
            self.__field_creation2=DateTime(value,**{'sizeinbytes': 4,  'default': self.creation })
    def __delfield_creation2(self): del self.__field_creation2
    creation2=property(__getfield_creation2, __setfield_creation2, __delfield_creation2, None)
    def __getfield_zero2(self):
        try: self.__field_zero2
        except:
            self.__field_zero2=UNKNOWN(**{'sizeinbytes': 14,  'pad': 0 })
        return self.__field_zero2.getvalue()
    def __setfield_zero2(self, value):
        if isinstance(value,UNKNOWN):
            self.__field_zero2=value
        else:
            self.__field_zero2=UNKNOWN(value,**{'sizeinbytes': 14,  'pad': 0 })
    def __delfield_zero2(self): del self.__field_zero2
    zero2=property(__getfield_zero2, __setfield_zero2, __delfield_zero2, None)
    def __getfield_five(self):
        try: self.__field_five
        except:
            self.__field_five=UINT(**{'sizeinbytes': 1,  'default': 5 })
        return self.__field_five.getvalue()
    def __setfield_five(self, value):
        if isinstance(value,UINT):
            self.__field_five=value
        else:
            self.__field_five=UINT(value,**{'sizeinbytes': 1,  'default': 5 })
    def __delfield_five(self): del self.__field_five
    five=property(__getfield_five, __setfield_five, __delfield_five, None)
    def __getfield_zero3(self):
        try: self.__field_zero3
        except:
            self.__field_zero3=UNKNOWN(**{'sizeinbytes': 13,  'pad': 0 })
        return self.__field_zero3.getvalue()
    def __setfield_zero3(self, value):
        if isinstance(value,UNKNOWN):
            self.__field_zero3=value
        else:
            self.__field_zero3=UNKNOWN(value,**{'sizeinbytes': 13,  'pad': 0 })
    def __delfield_zero3(self): del self.__field_zero3
    zero3=property(__getfield_zero3, __setfield_zero3, __delfield_zero3, None)
    def __getfield_modified(self):
        try: self.__field_modified
        except:
            self.__field_modified=DateTime(**{'sizeinbytes': 4,  'default': self.creation })
        return self.__field_modified.getvalue()
    def __setfield_modified(self, value):
        if isinstance(value,DateTime):
            self.__field_modified=value
        else:
            self.__field_modified=DateTime(value,**{'sizeinbytes': 4,  'default': self.creation })
    def __delfield_modified(self): del self.__field_modified
    modified=property(__getfield_modified, __setfield_modified, __delfield_modified, None)
    def __getfield_zero4(self):
        try: self.__field_zero4
        except:
            self.__field_zero4=UNKNOWN(**{'sizeinbytes': 4,  'pad': 0 })
        return self.__field_zero4.getvalue()
    def __setfield_zero4(self, value):
        if isinstance(value,UNKNOWN):
            self.__field_zero4=value
        else:
            self.__field_zero4=UNKNOWN(value,**{'sizeinbytes': 4,  'pad': 0 })
    def __delfield_zero4(self): del self.__field_zero4
    zero4=property(__getfield_zero4, __setfield_zero4, __delfield_zero4, None)
    def __getfield_modified2(self):
        try: self.__field_modified2
        except:
            self.__field_modified2=DateTime(**{'sizeinbytes': 4,  'default': self.modified })
        return self.__field_modified2.getvalue()
    def __setfield_modified2(self, value):
        if isinstance(value,DateTime):
            self.__field_modified2=value
        else:
            self.__field_modified2=DateTime(value,**{'sizeinbytes': 4,  'default': self.modified })
    def __delfield_modified2(self): del self.__field_modified2
    modified2=property(__getfield_modified2, __setfield_modified2, __delfield_modified2, None)
    def __getfield_zero5(self):
        try: self.__field_zero5
        except:
            self.__field_zero5=UNKNOWN(**{'sizeinbytes': 8,  'pad': 0 })
        return self.__field_zero5.getvalue()
    def __setfield_zero5(self, value):
        if isinstance(value,UNKNOWN):
            self.__field_zero5=value
        else:
            self.__field_zero5=UNKNOWN(value,**{'sizeinbytes': 8,  'pad': 0 })
    def __delfield_zero5(self): del self.__field_zero5
    zero5=property(__getfield_zero5, __setfield_zero5, __delfield_zero5, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('textlen', self.__field_textlen, None)
        yield ('text', self.__field_text, None)
        yield ('creation', self.__field_creation, None)
        yield ('zero1', self.__field_zero1, None)
        yield ('creation2', self.__field_creation2, None)
        yield ('zero2', self.__field_zero2, None)
        yield ('five', self.__field_five, None)
        yield ('zero3', self.__field_zero3, None)
        yield ('modified', self.__field_modified, None)
        yield ('zero4', self.__field_zero4, None)
        yield ('modified2', self.__field_modified2, None)
        yield ('zero5', self.__field_zero5, None)
class JournalNumber(BaseProtogenClass):
    __fields=['index', 'bitmap']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(JournalNumber,self).__init__(**dict)
        if self.__class__ is JournalNumber:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(JournalNumber,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(JournalNumber,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_index.writetobuffer(buf)
        self.__field_bitmap.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_index=UINT(**{'sizeinbytes': 2})
        self.__field_index.readfrombuffer(buf)
        self.__field_bitmap=UINT(**{'sizeinbytes': 2})
        self.__field_bitmap.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_index(self):
        return self.__field_index.getvalue()
    def __setfield_index(self, value):
        if isinstance(value,UINT):
            self.__field_index=value
        else:
            self.__field_index=UINT(value,**{'sizeinbytes': 2})
    def __delfield_index(self): del self.__field_index
    index=property(__getfield_index, __setfield_index, __delfield_index, None)
    def __getfield_bitmap(self):
        return self.__field_bitmap.getvalue()
    def __setfield_bitmap(self, value):
        if isinstance(value,UINT):
            self.__field_bitmap=value
        else:
            self.__field_bitmap=UINT(value,**{'sizeinbytes': 2})
    def __delfield_bitmap(self): del self.__field_bitmap
    bitmap=property(__getfield_bitmap, __setfield_bitmap, __delfield_bitmap, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('index', self.__field_index, None)
        yield ('bitmap', self.__field_bitmap, None)
class JournalSpeeddial(BaseProtogenClass):
    __fields=['index', 'speeddial', 'bitmap']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(JournalSpeeddial,self).__init__(**dict)
        if self.__class__ is JournalSpeeddial:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(JournalSpeeddial,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(JournalSpeeddial,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_index.writetobuffer(buf)
        self.__field_speeddial.writetobuffer(buf)
        self.__field_bitmap.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_index=UINT(**{'sizeinbytes': 2})
        self.__field_index.readfrombuffer(buf)
        self.__field_speeddial=UINT(**{'sizeinbytes': 2})
        self.__field_speeddial.readfrombuffer(buf)
        self.__field_bitmap=UINT(**{'sizeinbytes': 2})
        self.__field_bitmap.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_index(self):
        return self.__field_index.getvalue()
    def __setfield_index(self, value):
        if isinstance(value,UINT):
            self.__field_index=value
        else:
            self.__field_index=UINT(value,**{'sizeinbytes': 2})
    def __delfield_index(self): del self.__field_index
    index=property(__getfield_index, __setfield_index, __delfield_index, None)
    def __getfield_speeddial(self):
        return self.__field_speeddial.getvalue()
    def __setfield_speeddial(self, value):
        if isinstance(value,UINT):
            self.__field_speeddial=value
        else:
            self.__field_speeddial=UINT(value,**{'sizeinbytes': 2})
    def __delfield_speeddial(self): del self.__field_speeddial
    speeddial=property(__getfield_speeddial, __setfield_speeddial, __delfield_speeddial, None)
    def __getfield_bitmap(self):
        return self.__field_bitmap.getvalue()
    def __setfield_bitmap(self, value):
        if isinstance(value,UINT):
            self.__field_bitmap=value
        else:
            self.__field_bitmap=UINT(value,**{'sizeinbytes': 2})
    def __delfield_bitmap(self): del self.__field_bitmap
    bitmap=property(__getfield_bitmap, __setfield_bitmap, __delfield_bitmap, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('index', self.__field_index, None)
        yield ('speeddial', self.__field_speeddial, None)
        yield ('bitmap', self.__field_bitmap, None)
class JournalEntry(BaseProtogenClass):
    __fields=['number_info', 'speeddial_info', 'index', 'data1', 'previndex', 'home', 'nohome', 'work', 'nowork', 'cell', 'nocell', 'data2', 'fax', 'nofax', 'cell2', 'nocell2', 'homesd', 'nohomesd', 'worksd', 'noworksd', 'cellsd', 'nocellsd', 'data3', 'faxsd', 'nofaxsd', 'cell2sd', 'nocell2sd', 'previndex2', 'previndex3', 'data4', 'email', 'email2', 'wallpaper']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(JournalEntry,self).__init__(**dict)
        if self.__class__ is JournalEntry:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(JournalEntry,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(JournalEntry,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
        if getattr(self, '__field_number_info', None) is None:
            self.__field_number_info=UINT(**{ 'default': 0 })
        if getattr(self, '__field_speeddial_info', None) is None:
            self.__field_speeddial_info=UINT(**{ 'default': 0 })
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_index.writetobuffer(buf)
        try: self.__field_data1
        except:
            self.__field_data1=DATA(**{'sizeinbytes': 1,  'default': '\x00' })
        self.__field_data1.writetobuffer(buf)
        try: self.__field_previndex
        except:
            self.__field_previndex=UINT(**{'sizeinbytes': 2,  'default': self.index-1 })
        self.__field_previndex.writetobuffer(buf)
        if self.number_info & PB_FLG_HOME:
            self.__field_home.writetobuffer(buf)
        else:
            try: self.__field_nohome
            except:
                self.__field_nohome=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
            self.__field_nohome.writetobuffer(buf)
        if self.number_info & PB_FLG_WORK:
            self.__field_work.writetobuffer(buf)
        else:
            try: self.__field_nowork
            except:
                self.__field_nowork=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
            self.__field_nowork.writetobuffer(buf)
        if self.number_info & PB_FLG_CELL:
            self.__field_cell.writetobuffer(buf)
        else:
            try: self.__field_nocell
            except:
                self.__field_nocell=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
            self.__field_nocell.writetobuffer(buf)
        try: self.__field_data2
        except:
            self.__field_data2=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
        self.__field_data2.writetobuffer(buf)
        if self.number_info & PB_FLG_FAX:
            self.__field_fax.writetobuffer(buf)
        else:
            try: self.__field_nofax
            except:
                self.__field_nofax=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
            self.__field_nofax.writetobuffer(buf)
        if self.number_info&PB_FLG_CELL2:
            self.__field_cell2.writetobuffer(buf)
        else:
            try: self.__field_nocell2
            except:
                self.__field_nocell2=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
            self.__field_nocell2.writetobuffer(buf)
        if self.speeddial_info & PB_FLG_HOME:
            self.__field_homesd.writetobuffer(buf)
        else:
            try: self.__field_nohomesd
            except:
                self.__field_nohomesd=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
            self.__field_nohomesd.writetobuffer(buf)
        if self.speeddial_info & PB_FLG_WORK:
            self.__field_worksd.writetobuffer(buf)
        else:
            try: self.__field_noworksd
            except:
                self.__field_noworksd=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
            self.__field_noworksd.writetobuffer(buf)
        if self.speeddial_info&PB_FLG_CELL:
            self.__field_cellsd.writetobuffer(buf)
        else:
            try: self.__field_nocellsd
            except:
                self.__field_nocellsd=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
            self.__field_nocellsd.writetobuffer(buf)
        try: self.__field_data3
        except:
            self.__field_data3=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
        self.__field_data3.writetobuffer(buf)
        if self.speeddial_info&PB_FLG_FAX:
            self.__field_faxsd.writetobuffer(buf)
        else:
            try: self.__field_nofaxsd
            except:
                self.__field_nofaxsd=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
            self.__field_nofaxsd.writetobuffer(buf)
        if self.speeddial_info&PB_FLG_CELL2:
            self.__field_cell2sd.writetobuffer(buf)
        else:
            try: self.__field_nocell2sd
            except:
                self.__field_nocell2sd=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
            self.__field_nocell2sd.writetobuffer(buf)
        try: self.__field_previndex2
        except:
            self.__field_previndex2=UINT(**{'sizeinbytes': 2,  'default': self.previndex })
        self.__field_previndex2.writetobuffer(buf)
        try: self.__field_previndex3
        except:
            self.__field_previndex3=UINT(**{'sizeinbytes': 2,  'default': self.previndex })
        self.__field_previndex3.writetobuffer(buf)
        try: self.__field_data4
        except:
            self.__field_data4=DATA(**{'sizeinbytes': 4,  'default': '\x10\x00\x0C\x04' })
        self.__field_data4.writetobuffer(buf)
        try: self.__field_email
        except:
            self.__field_email=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
        self.__field_email.writetobuffer(buf)
        try: self.__field_email2
        except:
            self.__field_email2=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
        self.__field_email2.writetobuffer(buf)
        try: self.__field_wallpaper
        except:
            self.__field_wallpaper=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
        self.__field_wallpaper.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_index=UINT(**{'sizeinbytes': 2})
        self.__field_index.readfrombuffer(buf)
        self.__field_data1=DATA(**{'sizeinbytes': 1,  'default': '\x00' })
        self.__field_data1.readfrombuffer(buf)
        self.__field_previndex=UINT(**{'sizeinbytes': 2,  'default': self.index-1 })
        self.__field_previndex.readfrombuffer(buf)
        if self.number_info & PB_FLG_HOME:
            self.__field_home=JournalNumber()
            self.__field_home.readfrombuffer(buf)
        else:
            self.__field_nohome=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
            self.__field_nohome.readfrombuffer(buf)
        if self.number_info & PB_FLG_WORK:
            self.__field_work=JournalNumber()
            self.__field_work.readfrombuffer(buf)
        else:
            self.__field_nowork=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
            self.__field_nowork.readfrombuffer(buf)
        if self.number_info & PB_FLG_CELL:
            self.__field_cell=JournalNumber()
            self.__field_cell.readfrombuffer(buf)
        else:
            self.__field_nocell=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
            self.__field_nocell.readfrombuffer(buf)
        self.__field_data2=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
        self.__field_data2.readfrombuffer(buf)
        if self.number_info & PB_FLG_FAX:
            self.__field_fax=JournalNumber()
            self.__field_fax.readfrombuffer(buf)
        else:
            self.__field_nofax=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
            self.__field_nofax.readfrombuffer(buf)
        if self.number_info&PB_FLG_CELL2:
            self.__field_cell2=JournalNumber()
            self.__field_cell2.readfrombuffer(buf)
        else:
            self.__field_nocell2=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
            self.__field_nocell2.readfrombuffer(buf)
        if self.speeddial_info & PB_FLG_HOME:
            self.__field_homesd=JournalSpeeddial()
            self.__field_homesd.readfrombuffer(buf)
        else:
            self.__field_nohomesd=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
            self.__field_nohomesd.readfrombuffer(buf)
        if self.speeddial_info & PB_FLG_WORK:
            self.__field_worksd=JournalSpeeddial()
            self.__field_worksd.readfrombuffer(buf)
        else:
            self.__field_noworksd=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
            self.__field_noworksd.readfrombuffer(buf)
        if self.speeddial_info&PB_FLG_CELL:
            self.__field_cellsd=JournalSpeeddial()
            self.__field_cellsd.readfrombuffer(buf)
        else:
            self.__field_nocellsd=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
            self.__field_nocellsd.readfrombuffer(buf)
        self.__field_data3=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
        self.__field_data3.readfrombuffer(buf)
        if self.speeddial_info&PB_FLG_FAX:
            self.__field_faxsd=JournalSpeeddial()
            self.__field_faxsd.readfrombuffer(buf)
        else:
            self.__field_nofaxsd=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
            self.__field_nofaxsd.readfrombuffer(buf)
        if self.speeddial_info&PB_FLG_CELL2:
            self.__field_cell2sd=JournalSpeeddial()
            self.__field_cell2sd.readfrombuffer(buf)
        else:
            self.__field_nocell2sd=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
            self.__field_nocell2sd.readfrombuffer(buf)
        self.__field_previndex2=UINT(**{'sizeinbytes': 2,  'default': self.previndex })
        self.__field_previndex2.readfrombuffer(buf)
        self.__field_previndex3=UINT(**{'sizeinbytes': 2,  'default': self.previndex })
        self.__field_previndex3.readfrombuffer(buf)
        self.__field_data4=DATA(**{'sizeinbytes': 4,  'default': '\x10\x00\x0C\x04' })
        self.__field_data4.readfrombuffer(buf)
        self.__field_email=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
        self.__field_email.readfrombuffer(buf)
        self.__field_email2=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
        self.__field_email2.readfrombuffer(buf)
        self.__field_wallpaper=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
        self.__field_wallpaper.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_number_info(self):
        try: self.__field_number_info
        except:
            self.__field_number_info=UINT(**{ 'default': 0 })
        return self.__field_number_info.getvalue()
    def __setfield_number_info(self, value):
        if isinstance(value,UINT):
            self.__field_number_info=value
        else:
            self.__field_number_info=UINT(value,**{ 'default': 0 })
    def __delfield_number_info(self): del self.__field_number_info
    number_info=property(__getfield_number_info, __setfield_number_info, __delfield_number_info, None)
    def __getfield_speeddial_info(self):
        try: self.__field_speeddial_info
        except:
            self.__field_speeddial_info=UINT(**{ 'default': 0 })
        return self.__field_speeddial_info.getvalue()
    def __setfield_speeddial_info(self, value):
        if isinstance(value,UINT):
            self.__field_speeddial_info=value
        else:
            self.__field_speeddial_info=UINT(value,**{ 'default': 0 })
    def __delfield_speeddial_info(self): del self.__field_speeddial_info
    speeddial_info=property(__getfield_speeddial_info, __setfield_speeddial_info, __delfield_speeddial_info, None)
    def __getfield_index(self):
        return self.__field_index.getvalue()
    def __setfield_index(self, value):
        if isinstance(value,UINT):
            self.__field_index=value
        else:
            self.__field_index=UINT(value,**{'sizeinbytes': 2})
    def __delfield_index(self): del self.__field_index
    index=property(__getfield_index, __setfield_index, __delfield_index, None)
    def __getfield_data1(self):
        try: self.__field_data1
        except:
            self.__field_data1=DATA(**{'sizeinbytes': 1,  'default': '\x00' })
        return self.__field_data1.getvalue()
    def __setfield_data1(self, value):
        if isinstance(value,DATA):
            self.__field_data1=value
        else:
            self.__field_data1=DATA(value,**{'sizeinbytes': 1,  'default': '\x00' })
    def __delfield_data1(self): del self.__field_data1
    data1=property(__getfield_data1, __setfield_data1, __delfield_data1, None)
    def __getfield_previndex(self):
        try: self.__field_previndex
        except:
            self.__field_previndex=UINT(**{'sizeinbytes': 2,  'default': self.index-1 })
        return self.__field_previndex.getvalue()
    def __setfield_previndex(self, value):
        if isinstance(value,UINT):
            self.__field_previndex=value
        else:
            self.__field_previndex=UINT(value,**{'sizeinbytes': 2,  'default': self.index-1 })
    def __delfield_previndex(self): del self.__field_previndex
    previndex=property(__getfield_previndex, __setfield_previndex, __delfield_previndex, None)
    def __getfield_home(self):
        return self.__field_home.getvalue()
    def __setfield_home(self, value):
        if isinstance(value,JournalNumber):
            self.__field_home=value
        else:
            self.__field_home=JournalNumber(value,)
    def __delfield_home(self): del self.__field_home
    home=property(__getfield_home, __setfield_home, __delfield_home, None)
    def __getfield_nohome(self):
        try: self.__field_nohome
        except:
            self.__field_nohome=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
        return self.__field_nohome.getvalue()
    def __setfield_nohome(self, value):
        if isinstance(value,UINT):
            self.__field_nohome=value
        else:
            self.__field_nohome=UINT(value,**{'sizeinbytes': 2,  'default': 0xffff })
    def __delfield_nohome(self): del self.__field_nohome
    nohome=property(__getfield_nohome, __setfield_nohome, __delfield_nohome, None)
    def __getfield_work(self):
        return self.__field_work.getvalue()
    def __setfield_work(self, value):
        if isinstance(value,JournalNumber):
            self.__field_work=value
        else:
            self.__field_work=JournalNumber(value,)
    def __delfield_work(self): del self.__field_work
    work=property(__getfield_work, __setfield_work, __delfield_work, None)
    def __getfield_nowork(self):
        try: self.__field_nowork
        except:
            self.__field_nowork=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
        return self.__field_nowork.getvalue()
    def __setfield_nowork(self, value):
        if isinstance(value,UINT):
            self.__field_nowork=value
        else:
            self.__field_nowork=UINT(value,**{'sizeinbytes': 2,  'default': 0xffff })
    def __delfield_nowork(self): del self.__field_nowork
    nowork=property(__getfield_nowork, __setfield_nowork, __delfield_nowork, None)
    def __getfield_cell(self):
        return self.__field_cell.getvalue()
    def __setfield_cell(self, value):
        if isinstance(value,JournalNumber):
            self.__field_cell=value
        else:
            self.__field_cell=JournalNumber(value,)
    def __delfield_cell(self): del self.__field_cell
    cell=property(__getfield_cell, __setfield_cell, __delfield_cell, None)
    def __getfield_nocell(self):
        try: self.__field_nocell
        except:
            self.__field_nocell=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
        return self.__field_nocell.getvalue()
    def __setfield_nocell(self, value):
        if isinstance(value,UINT):
            self.__field_nocell=value
        else:
            self.__field_nocell=UINT(value,**{'sizeinbytes': 2,  'default': 0xffff })
    def __delfield_nocell(self): del self.__field_nocell
    nocell=property(__getfield_nocell, __setfield_nocell, __delfield_nocell, None)
    def __getfield_data2(self):
        try: self.__field_data2
        except:
            self.__field_data2=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
        return self.__field_data2.getvalue()
    def __setfield_data2(self, value):
        if isinstance(value,UINT):
            self.__field_data2=value
        else:
            self.__field_data2=UINT(value,**{'sizeinbytes': 2,  'default': 0xffff })
    def __delfield_data2(self): del self.__field_data2
    data2=property(__getfield_data2, __setfield_data2, __delfield_data2, None)
    def __getfield_fax(self):
        return self.__field_fax.getvalue()
    def __setfield_fax(self, value):
        if isinstance(value,JournalNumber):
            self.__field_fax=value
        else:
            self.__field_fax=JournalNumber(value,)
    def __delfield_fax(self): del self.__field_fax
    fax=property(__getfield_fax, __setfield_fax, __delfield_fax, None)
    def __getfield_nofax(self):
        try: self.__field_nofax
        except:
            self.__field_nofax=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
        return self.__field_nofax.getvalue()
    def __setfield_nofax(self, value):
        if isinstance(value,UINT):
            self.__field_nofax=value
        else:
            self.__field_nofax=UINT(value,**{'sizeinbytes': 2,  'default': 0xffff })
    def __delfield_nofax(self): del self.__field_nofax
    nofax=property(__getfield_nofax, __setfield_nofax, __delfield_nofax, None)
    def __getfield_cell2(self):
        return self.__field_cell2.getvalue()
    def __setfield_cell2(self, value):
        if isinstance(value,JournalNumber):
            self.__field_cell2=value
        else:
            self.__field_cell2=JournalNumber(value,)
    def __delfield_cell2(self): del self.__field_cell2
    cell2=property(__getfield_cell2, __setfield_cell2, __delfield_cell2, None)
    def __getfield_nocell2(self):
        try: self.__field_nocell2
        except:
            self.__field_nocell2=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
        return self.__field_nocell2.getvalue()
    def __setfield_nocell2(self, value):
        if isinstance(value,UINT):
            self.__field_nocell2=value
        else:
            self.__field_nocell2=UINT(value,**{'sizeinbytes': 2,  'default': 0xffff })
    def __delfield_nocell2(self): del self.__field_nocell2
    nocell2=property(__getfield_nocell2, __setfield_nocell2, __delfield_nocell2, None)
    def __getfield_homesd(self):
        return self.__field_homesd.getvalue()
    def __setfield_homesd(self, value):
        if isinstance(value,JournalSpeeddial):
            self.__field_homesd=value
        else:
            self.__field_homesd=JournalSpeeddial(value,)
    def __delfield_homesd(self): del self.__field_homesd
    homesd=property(__getfield_homesd, __setfield_homesd, __delfield_homesd, None)
    def __getfield_nohomesd(self):
        try: self.__field_nohomesd
        except:
            self.__field_nohomesd=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
        return self.__field_nohomesd.getvalue()
    def __setfield_nohomesd(self, value):
        if isinstance(value,UINT):
            self.__field_nohomesd=value
        else:
            self.__field_nohomesd=UINT(value,**{'sizeinbytes': 2,  'default': 0xffff })
    def __delfield_nohomesd(self): del self.__field_nohomesd
    nohomesd=property(__getfield_nohomesd, __setfield_nohomesd, __delfield_nohomesd, None)
    def __getfield_worksd(self):
        return self.__field_worksd.getvalue()
    def __setfield_worksd(self, value):
        if isinstance(value,JournalSpeeddial):
            self.__field_worksd=value
        else:
            self.__field_worksd=JournalSpeeddial(value,)
    def __delfield_worksd(self): del self.__field_worksd
    worksd=property(__getfield_worksd, __setfield_worksd, __delfield_worksd, None)
    def __getfield_noworksd(self):
        try: self.__field_noworksd
        except:
            self.__field_noworksd=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
        return self.__field_noworksd.getvalue()
    def __setfield_noworksd(self, value):
        if isinstance(value,UINT):
            self.__field_noworksd=value
        else:
            self.__field_noworksd=UINT(value,**{'sizeinbytes': 2,  'default': 0xffff })
    def __delfield_noworksd(self): del self.__field_noworksd
    noworksd=property(__getfield_noworksd, __setfield_noworksd, __delfield_noworksd, None)
    def __getfield_cellsd(self):
        return self.__field_cellsd.getvalue()
    def __setfield_cellsd(self, value):
        if isinstance(value,JournalSpeeddial):
            self.__field_cellsd=value
        else:
            self.__field_cellsd=JournalSpeeddial(value,)
    def __delfield_cellsd(self): del self.__field_cellsd
    cellsd=property(__getfield_cellsd, __setfield_cellsd, __delfield_cellsd, None)
    def __getfield_nocellsd(self):
        try: self.__field_nocellsd
        except:
            self.__field_nocellsd=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
        return self.__field_nocellsd.getvalue()
    def __setfield_nocellsd(self, value):
        if isinstance(value,UINT):
            self.__field_nocellsd=value
        else:
            self.__field_nocellsd=UINT(value,**{'sizeinbytes': 2,  'default': 0xffff })
    def __delfield_nocellsd(self): del self.__field_nocellsd
    nocellsd=property(__getfield_nocellsd, __setfield_nocellsd, __delfield_nocellsd, None)
    def __getfield_data3(self):
        try: self.__field_data3
        except:
            self.__field_data3=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
        return self.__field_data3.getvalue()
    def __setfield_data3(self, value):
        if isinstance(value,UINT):
            self.__field_data3=value
        else:
            self.__field_data3=UINT(value,**{'sizeinbytes': 2,  'default': 0xffff })
    def __delfield_data3(self): del self.__field_data3
    data3=property(__getfield_data3, __setfield_data3, __delfield_data3, None)
    def __getfield_faxsd(self):
        return self.__field_faxsd.getvalue()
    def __setfield_faxsd(self, value):
        if isinstance(value,JournalSpeeddial):
            self.__field_faxsd=value
        else:
            self.__field_faxsd=JournalSpeeddial(value,)
    def __delfield_faxsd(self): del self.__field_faxsd
    faxsd=property(__getfield_faxsd, __setfield_faxsd, __delfield_faxsd, None)
    def __getfield_nofaxsd(self):
        try: self.__field_nofaxsd
        except:
            self.__field_nofaxsd=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
        return self.__field_nofaxsd.getvalue()
    def __setfield_nofaxsd(self, value):
        if isinstance(value,UINT):
            self.__field_nofaxsd=value
        else:
            self.__field_nofaxsd=UINT(value,**{'sizeinbytes': 2,  'default': 0xffff })
    def __delfield_nofaxsd(self): del self.__field_nofaxsd
    nofaxsd=property(__getfield_nofaxsd, __setfield_nofaxsd, __delfield_nofaxsd, None)
    def __getfield_cell2sd(self):
        return self.__field_cell2sd.getvalue()
    def __setfield_cell2sd(self, value):
        if isinstance(value,JournalSpeeddial):
            self.__field_cell2sd=value
        else:
            self.__field_cell2sd=JournalSpeeddial(value,)
    def __delfield_cell2sd(self): del self.__field_cell2sd
    cell2sd=property(__getfield_cell2sd, __setfield_cell2sd, __delfield_cell2sd, None)
    def __getfield_nocell2sd(self):
        try: self.__field_nocell2sd
        except:
            self.__field_nocell2sd=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
        return self.__field_nocell2sd.getvalue()
    def __setfield_nocell2sd(self, value):
        if isinstance(value,UINT):
            self.__field_nocell2sd=value
        else:
            self.__field_nocell2sd=UINT(value,**{'sizeinbytes': 2,  'default': 0xffff })
    def __delfield_nocell2sd(self): del self.__field_nocell2sd
    nocell2sd=property(__getfield_nocell2sd, __setfield_nocell2sd, __delfield_nocell2sd, None)
    def __getfield_previndex2(self):
        try: self.__field_previndex2
        except:
            self.__field_previndex2=UINT(**{'sizeinbytes': 2,  'default': self.previndex })
        return self.__field_previndex2.getvalue()
    def __setfield_previndex2(self, value):
        if isinstance(value,UINT):
            self.__field_previndex2=value
        else:
            self.__field_previndex2=UINT(value,**{'sizeinbytes': 2,  'default': self.previndex })
    def __delfield_previndex2(self): del self.__field_previndex2
    previndex2=property(__getfield_previndex2, __setfield_previndex2, __delfield_previndex2, None)
    def __getfield_previndex3(self):
        try: self.__field_previndex3
        except:
            self.__field_previndex3=UINT(**{'sizeinbytes': 2,  'default': self.previndex })
        return self.__field_previndex3.getvalue()
    def __setfield_previndex3(self, value):
        if isinstance(value,UINT):
            self.__field_previndex3=value
        else:
            self.__field_previndex3=UINT(value,**{'sizeinbytes': 2,  'default': self.previndex })
    def __delfield_previndex3(self): del self.__field_previndex3
    previndex3=property(__getfield_previndex3, __setfield_previndex3, __delfield_previndex3, None)
    def __getfield_data4(self):
        try: self.__field_data4
        except:
            self.__field_data4=DATA(**{'sizeinbytes': 4,  'default': '\x10\x00\x0C\x04' })
        return self.__field_data4.getvalue()
    def __setfield_data4(self, value):
        if isinstance(value,DATA):
            self.__field_data4=value
        else:
            self.__field_data4=DATA(value,**{'sizeinbytes': 4,  'default': '\x10\x00\x0C\x04' })
    def __delfield_data4(self): del self.__field_data4
    data4=property(__getfield_data4, __setfield_data4, __delfield_data4, None)
    def __getfield_email(self):
        try: self.__field_email
        except:
            self.__field_email=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
        return self.__field_email.getvalue()
    def __setfield_email(self, value):
        if isinstance(value,UINT):
            self.__field_email=value
        else:
            self.__field_email=UINT(value,**{'sizeinbytes': 2,  'default': 0xffff })
    def __delfield_email(self): del self.__field_email
    email=property(__getfield_email, __setfield_email, __delfield_email, None)
    def __getfield_email2(self):
        try: self.__field_email2
        except:
            self.__field_email2=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
        return self.__field_email2.getvalue()
    def __setfield_email2(self, value):
        if isinstance(value,UINT):
            self.__field_email2=value
        else:
            self.__field_email2=UINT(value,**{'sizeinbytes': 2,  'default': 0xffff })
    def __delfield_email2(self): del self.__field_email2
    email2=property(__getfield_email2, __setfield_email2, __delfield_email2, None)
    def __getfield_wallpaper(self):
        try: self.__field_wallpaper
        except:
            self.__field_wallpaper=UINT(**{'sizeinbytes': 2,  'default': 0xffff })
        return self.__field_wallpaper.getvalue()
    def __setfield_wallpaper(self, value):
        if isinstance(value,UINT):
            self.__field_wallpaper=value
        else:
            self.__field_wallpaper=UINT(value,**{'sizeinbytes': 2,  'default': 0xffff })
    def __delfield_wallpaper(self): del self.__field_wallpaper
    wallpaper=property(__getfield_wallpaper, __setfield_wallpaper, __delfield_wallpaper, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('number_info', self.__field_number_info, None)
        yield ('speeddial_info', self.__field_speeddial_info, None)
        yield ('index', self.__field_index, None)
        yield ('data1', self.__field_data1, None)
        yield ('previndex', self.__field_previndex, None)
        if self.number_info & PB_FLG_HOME:
            yield ('home', self.__field_home, None)
        else:
            yield ('nohome', self.__field_nohome, None)
        if self.number_info & PB_FLG_WORK:
            yield ('work', self.__field_work, None)
        else:
            yield ('nowork', self.__field_nowork, None)
        if self.number_info & PB_FLG_CELL:
            yield ('cell', self.__field_cell, None)
        else:
            yield ('nocell', self.__field_nocell, None)
        yield ('data2', self.__field_data2, None)
        if self.number_info & PB_FLG_FAX:
            yield ('fax', self.__field_fax, None)
        else:
            yield ('nofax', self.__field_nofax, None)
        if self.number_info&PB_FLG_CELL2:
            yield ('cell2', self.__field_cell2, None)
        else:
            yield ('nocell2', self.__field_nocell2, None)
        if self.speeddial_info & PB_FLG_HOME:
            yield ('homesd', self.__field_homesd, None)
        else:
            yield ('nohomesd', self.__field_nohomesd, None)
        if self.speeddial_info & PB_FLG_WORK:
            yield ('worksd', self.__field_worksd, None)
        else:
            yield ('noworksd', self.__field_noworksd, None)
        if self.speeddial_info&PB_FLG_CELL:
            yield ('cellsd', self.__field_cellsd, None)
        else:
            yield ('nocellsd', self.__field_nocellsd, None)
        yield ('data3', self.__field_data3, None)
        if self.speeddial_info&PB_FLG_FAX:
            yield ('faxsd', self.__field_faxsd, None)
        else:
            yield ('nofaxsd', self.__field_nofaxsd, None)
        if self.speeddial_info&PB_FLG_CELL2:
            yield ('cell2sd', self.__field_cell2sd, None)
        else:
            yield ('nocell2sd', self.__field_nocell2sd, None)
        yield ('previndex2', self.__field_previndex2, None)
        yield ('previndex3', self.__field_previndex3, None)
        yield ('data4', self.__field_data4, None)
        yield ('email', self.__field_email, None)
        yield ('email2', self.__field_email2, None)
        yield ('wallpaper', self.__field_wallpaper, None)
class JournalRec(BaseProtogenClass):
    __fields=['command', 'blocklen', 'entry']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(JournalRec,self).__init__(**dict)
        if self.__class__ is JournalRec:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(JournalRec,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(JournalRec,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_command
        except:
            self.__field_command=UINT(**{'sizeinbytes': 1,  'default': 1 })
        self.__field_command.writetobuffer(buf)
        try: self.__field_blocklen
        except:
            self.__field_blocklen=UINT(**{'sizeinbytes': 2,  'default': 0 })
        self.__field_blocklen.writetobuffer(buf)
        self.__field_entry.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_command=UINT(**{'sizeinbytes': 1,  'default': 1 })
        self.__field_command.readfrombuffer(buf)
        self.__field_blocklen=UINT(**{'sizeinbytes': 2,  'default': 0 })
        self.__field_blocklen.readfrombuffer(buf)
        self.__field_entry=JournalEntry()
        self.__field_entry.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_command(self):
        try: self.__field_command
        except:
            self.__field_command=UINT(**{'sizeinbytes': 1,  'default': 1 })
        return self.__field_command.getvalue()
    def __setfield_command(self, value):
        if isinstance(value,UINT):
            self.__field_command=value
        else:
            self.__field_command=UINT(value,**{'sizeinbytes': 1,  'default': 1 })
    def __delfield_command(self): del self.__field_command
    command=property(__getfield_command, __setfield_command, __delfield_command, None)
    def __getfield_blocklen(self):
        try: self.__field_blocklen
        except:
            self.__field_blocklen=UINT(**{'sizeinbytes': 2,  'default': 0 })
        return self.__field_blocklen.getvalue()
    def __setfield_blocklen(self, value):
        if isinstance(value,UINT):
            self.__field_blocklen=value
        else:
            self.__field_blocklen=UINT(value,**{'sizeinbytes': 2,  'default': 0 })
    def __delfield_blocklen(self): del self.__field_blocklen
    blocklen=property(__getfield_blocklen, __setfield_blocklen, __delfield_blocklen, None)
    def __getfield_entry(self):
        return self.__field_entry.getvalue()
    def __setfield_entry(self, value):
        if isinstance(value,JournalEntry):
            self.__field_entry=value
        else:
            self.__field_entry=JournalEntry(value,)
    def __delfield_entry(self): del self.__field_entry
    entry=property(__getfield_entry, __setfield_entry, __delfield_entry, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('command', self.__field_command, None)
        yield ('blocklen', self.__field_blocklen, None)
        yield ('entry', self.__field_entry, None)
class JournalFile(BaseProtogenClass):
    __fields=['items']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(JournalFile,self).__init__(**dict)
        if self.__class__ is JournalFile:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(JournalFile,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(JournalFile,kwargs)
        if len(args):
            dict2={ 'elementclass': JournalRec }
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_items=LIST(*args,**dict2)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_items
        except:
            self.__field_items=LIST(**{ 'elementclass': JournalRec })
        self.__field_items.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_items=LIST(**{ 'elementclass': JournalRec })
        self.__field_items.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_items(self):
        try: self.__field_items
        except:
            self.__field_items=LIST(**{ 'elementclass': JournalRec })
        return self.__field_items.getvalue()
    def __setfield_items(self, value):
        if isinstance(value,LIST):
            self.__field_items=value
        else:
            self.__field_items=LIST(value,**{ 'elementclass': JournalRec })
    def __delfield_items(self): del self.__field_items
    items=property(__getfield_items, __setfield_items, __delfield_items, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('items', self.__field_items, None)
class NumberEntry(BaseProtogenClass):
    __fields=['number', 'option', 'speeddial', 'ringtone']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(NumberEntry,self).__init__(**dict)
        if self.__class__ is NumberEntry:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(NumberEntry,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(NumberEntry,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_number.writetobuffer(buf)
        self.__field_option.writetobuffer(buf)
        if self.option & PB_FLG_SPEEDDIAL:
            self.__field_speeddial.writetobuffer(buf)
        if self.option & PB_FLG_RINGTONE:
            self.__field_ringtone.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_number=STRING(**{ 'terminator': None,               'pascal': True })
        self.__field_number.readfrombuffer(buf)
        self.__field_option=UINT(**{'sizeinbytes': 1})
        self.__field_option.readfrombuffer(buf)
        if self.option & PB_FLG_SPEEDDIAL:
            self.__field_speeddial=UINT(**{'sizeinbytes': 2})
            self.__field_speeddial.readfrombuffer(buf)
        if self.option & PB_FLG_RINGTONE:
            self.__field_ringtone=STRING(**{ 'terminator': None,                   'pascal': True })
            self.__field_ringtone.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_number(self):
        return self.__field_number.getvalue()
    def __setfield_number(self, value):
        if isinstance(value,STRING):
            self.__field_number=value
        else:
            self.__field_number=STRING(value,**{ 'terminator': None,               'pascal': True })
    def __delfield_number(self): del self.__field_number
    number=property(__getfield_number, __setfield_number, __delfield_number, None)
    def __getfield_option(self):
        return self.__field_option.getvalue()
    def __setfield_option(self, value):
        if isinstance(value,UINT):
            self.__field_option=value
        else:
            self.__field_option=UINT(value,**{'sizeinbytes': 1})
    def __delfield_option(self): del self.__field_option
    option=property(__getfield_option, __setfield_option, __delfield_option, None)
    def __getfield_speeddial(self):
        return self.__field_speeddial.getvalue()
    def __setfield_speeddial(self, value):
        if isinstance(value,UINT):
            self.__field_speeddial=value
        else:
            self.__field_speeddial=UINT(value,**{'sizeinbytes': 2})
    def __delfield_speeddial(self): del self.__field_speeddial
    speeddial=property(__getfield_speeddial, __setfield_speeddial, __delfield_speeddial, None)
    def __getfield_ringtone(self):
        return self.__field_ringtone.getvalue()
    def __setfield_ringtone(self, value):
        if isinstance(value,STRING):
            self.__field_ringtone=value
        else:
            self.__field_ringtone=STRING(value,**{ 'terminator': None,                   'pascal': True })
    def __delfield_ringtone(self): del self.__field_ringtone
    ringtone=property(__getfield_ringtone, __setfield_ringtone, __delfield_ringtone, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('number', self.__field_number, None)
        yield ('option', self.__field_option, None)
        if self.option & PB_FLG_SPEEDDIAL:
            yield ('speeddial', self.__field_speeddial, None)
        if self.option & PB_FLG_RINGTONE:
            yield ('ringtone', self.__field_ringtone, None)
class PBEntry(BaseProtogenClass):
    __fields=['info', 'zero1', 'name', 'email', 'email2', 'home', 'work', 'cell', 'fax', 'cell2', 'datetime', 'group', 'wallpaper', 'wallpaper_range']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(PBEntry,self).__init__(**dict)
        if self.__class__ is PBEntry:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(PBEntry,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(PBEntry,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_info.writetobuffer(buf)
        try: self.__field_zero1
        except:
            self.__field_zero1=UINT(**{'sizeinbytes': 2,  'default': 0 })
        self.__field_zero1.writetobuffer(buf)
        if self.info & PB_FLG_NAME:
            self.__field_name.writetobuffer(buf)
        if self.info & PB_FLG_EMAIL:
            self.__field_email.writetobuffer(buf)
        if self.info & PB_FLG_EMAIL2:
            self.__field_email2.writetobuffer(buf)
        if self.info & PB_FLG_HOME:
            self.__field_home.writetobuffer(buf)
        if self.info & PB_FLG_WORK:
            self.__field_work.writetobuffer(buf)
        if self.info & PB_FLG_CELL:
            self.__field_cell.writetobuffer(buf)
        if self.info & PB_FLG_FAX:
            self.__field_fax.writetobuffer(buf)
        if self.info & PB_FLG_CELL2:
            self.__field_cell2.writetobuffer(buf)
        if self.info & PB_FLG_DATE:
            self.__field_datetime.writetobuffer(buf)
        if self.info & PB_FLG_GROUP:
            self.__field_group.writetobuffer(buf)
        if self.info & PB_FLG_WP:
            self.__field_wallpaper.writetobuffer(buf)
            self.__field_wallpaper_range.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_info=UINT(**{'sizeinbytes': 2})
        self.__field_info.readfrombuffer(buf)
        self.__field_zero1=UINT(**{'sizeinbytes': 2,  'default': 0 })
        self.__field_zero1.readfrombuffer(buf)
        if self.info & PB_FLG_NAME:
            self.__field_name=STRING(**{ 'terminator': None,                   'pascal': True })
            self.__field_name.readfrombuffer(buf)
        if self.info & PB_FLG_EMAIL:
            self.__field_email=STRING(**{ 'terminator': None,                   'pascal': True })
            self.__field_email.readfrombuffer(buf)
        if self.info & PB_FLG_EMAIL2:
            self.__field_email2=STRING(**{ 'terminator': None,                   'pascal': True })
            self.__field_email2.readfrombuffer(buf)
        if self.info & PB_FLG_HOME:
            self.__field_home=NumberEntry()
            self.__field_home.readfrombuffer(buf)
        if self.info & PB_FLG_WORK:
            self.__field_work=NumberEntry()
            self.__field_work.readfrombuffer(buf)
        if self.info & PB_FLG_CELL:
            self.__field_cell=NumberEntry()
            self.__field_cell.readfrombuffer(buf)
        if self.info & PB_FLG_FAX:
            self.__field_fax=NumberEntry()
            self.__field_fax.readfrombuffer(buf)
        if self.info & PB_FLG_CELL2:
            self.__field_cell2=NumberEntry()
            self.__field_cell2.readfrombuffer(buf)
        if self.info & PB_FLG_DATE:
            self.__field_datetime=DateTime(**{'sizeinbytes': 4})
            self.__field_datetime.readfrombuffer(buf)
        if self.info & PB_FLG_GROUP:
            self.__field_group=UINT(**{'sizeinbytes': 1})
            self.__field_group.readfrombuffer(buf)
        if self.info & PB_FLG_WP:
            self.__field_wallpaper=STRING(**{ 'terminator': None,                   'pascal': True })
            self.__field_wallpaper.readfrombuffer(buf)
            self.__field_wallpaper_range=UINT(**{'sizeinbytes': 4})
            self.__field_wallpaper_range.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_info(self):
        return self.__field_info.getvalue()
    def __setfield_info(self, value):
        if isinstance(value,UINT):
            self.__field_info=value
        else:
            self.__field_info=UINT(value,**{'sizeinbytes': 2})
    def __delfield_info(self): del self.__field_info
    info=property(__getfield_info, __setfield_info, __delfield_info, None)
    def __getfield_zero1(self):
        try: self.__field_zero1
        except:
            self.__field_zero1=UINT(**{'sizeinbytes': 2,  'default': 0 })
        return self.__field_zero1.getvalue()
    def __setfield_zero1(self, value):
        if isinstance(value,UINT):
            self.__field_zero1=value
        else:
            self.__field_zero1=UINT(value,**{'sizeinbytes': 2,  'default': 0 })
    def __delfield_zero1(self): del self.__field_zero1
    zero1=property(__getfield_zero1, __setfield_zero1, __delfield_zero1, None)
    def __getfield_name(self):
        return self.__field_name.getvalue()
    def __setfield_name(self, value):
        if isinstance(value,STRING):
            self.__field_name=value
        else:
            self.__field_name=STRING(value,**{ 'terminator': None,                   'pascal': True })
    def __delfield_name(self): del self.__field_name
    name=property(__getfield_name, __setfield_name, __delfield_name, None)
    def __getfield_email(self):
        return self.__field_email.getvalue()
    def __setfield_email(self, value):
        if isinstance(value,STRING):
            self.__field_email=value
        else:
            self.__field_email=STRING(value,**{ 'terminator': None,                   'pascal': True })
    def __delfield_email(self): del self.__field_email
    email=property(__getfield_email, __setfield_email, __delfield_email, None)
    def __getfield_email2(self):
        return self.__field_email2.getvalue()
    def __setfield_email2(self, value):
        if isinstance(value,STRING):
            self.__field_email2=value
        else:
            self.__field_email2=STRING(value,**{ 'terminator': None,                   'pascal': True })
    def __delfield_email2(self): del self.__field_email2
    email2=property(__getfield_email2, __setfield_email2, __delfield_email2, None)
    def __getfield_home(self):
        return self.__field_home.getvalue()
    def __setfield_home(self, value):
        if isinstance(value,NumberEntry):
            self.__field_home=value
        else:
            self.__field_home=NumberEntry(value,)
    def __delfield_home(self): del self.__field_home
    home=property(__getfield_home, __setfield_home, __delfield_home, None)
    def __getfield_work(self):
        return self.__field_work.getvalue()
    def __setfield_work(self, value):
        if isinstance(value,NumberEntry):
            self.__field_work=value
        else:
            self.__field_work=NumberEntry(value,)
    def __delfield_work(self): del self.__field_work
    work=property(__getfield_work, __setfield_work, __delfield_work, None)
    def __getfield_cell(self):
        return self.__field_cell.getvalue()
    def __setfield_cell(self, value):
        if isinstance(value,NumberEntry):
            self.__field_cell=value
        else:
            self.__field_cell=NumberEntry(value,)
    def __delfield_cell(self): del self.__field_cell
    cell=property(__getfield_cell, __setfield_cell, __delfield_cell, None)
    def __getfield_fax(self):
        return self.__field_fax.getvalue()
    def __setfield_fax(self, value):
        if isinstance(value,NumberEntry):
            self.__field_fax=value
        else:
            self.__field_fax=NumberEntry(value,)
    def __delfield_fax(self): del self.__field_fax
    fax=property(__getfield_fax, __setfield_fax, __delfield_fax, None)
    def __getfield_cell2(self):
        return self.__field_cell2.getvalue()
    def __setfield_cell2(self, value):
        if isinstance(value,NumberEntry):
            self.__field_cell2=value
        else:
            self.__field_cell2=NumberEntry(value,)
    def __delfield_cell2(self): del self.__field_cell2
    cell2=property(__getfield_cell2, __setfield_cell2, __delfield_cell2, None)
    def __getfield_datetime(self):
        return self.__field_datetime.getvalue()
    def __setfield_datetime(self, value):
        if isinstance(value,DateTime):
            self.__field_datetime=value
        else:
            self.__field_datetime=DateTime(value,**{'sizeinbytes': 4})
    def __delfield_datetime(self): del self.__field_datetime
    datetime=property(__getfield_datetime, __setfield_datetime, __delfield_datetime, None)
    def __getfield_group(self):
        return self.__field_group.getvalue()
    def __setfield_group(self, value):
        if isinstance(value,UINT):
            self.__field_group=value
        else:
            self.__field_group=UINT(value,**{'sizeinbytes': 1})
    def __delfield_group(self): del self.__field_group
    group=property(__getfield_group, __setfield_group, __delfield_group, None)
    def __getfield_wallpaper(self):
        return self.__field_wallpaper.getvalue()
    def __setfield_wallpaper(self, value):
        if isinstance(value,STRING):
            self.__field_wallpaper=value
        else:
            self.__field_wallpaper=STRING(value,**{ 'terminator': None,                   'pascal': True })
    def __delfield_wallpaper(self): del self.__field_wallpaper
    wallpaper=property(__getfield_wallpaper, __setfield_wallpaper, __delfield_wallpaper, None)
    def __getfield_wallpaper_range(self):
        return self.__field_wallpaper_range.getvalue()
    def __setfield_wallpaper_range(self, value):
        if isinstance(value,UINT):
            self.__field_wallpaper_range=value
        else:
            self.__field_wallpaper_range=UINT(value,**{'sizeinbytes': 4})
    def __delfield_wallpaper_range(self): del self.__field_wallpaper_range
    wallpaper_range=property(__getfield_wallpaper_range, __setfield_wallpaper_range, __delfield_wallpaper_range, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('info', self.__field_info, None)
        yield ('zero1', self.__field_zero1, None)
        if self.info & PB_FLG_NAME:
            yield ('name', self.__field_name, None)
        if self.info & PB_FLG_EMAIL:
            yield ('email', self.__field_email, None)
        if self.info & PB_FLG_EMAIL2:
            yield ('email2', self.__field_email2, None)
        if self.info & PB_FLG_HOME:
            yield ('home', self.__field_home, None)
        if self.info & PB_FLG_WORK:
            yield ('work', self.__field_work, None)
        if self.info & PB_FLG_CELL:
            yield ('cell', self.__field_cell, None)
        if self.info & PB_FLG_FAX:
            yield ('fax', self.__field_fax, None)
        if self.info & PB_FLG_CELL2:
            yield ('cell2', self.__field_cell2, None)
        if self.info & PB_FLG_DATE:
            yield ('datetime', self.__field_datetime, None)
        if self.info & PB_FLG_GROUP:
            yield ('group', self.__field_group, None)
        if self.info & PB_FLG_WP:
            yield ('wallpaper', self.__field_wallpaper, None)
            yield ('wallpaper_range', self.__field_wallpaper_range, None)
class LenEntry(BaseProtogenClass):
    __fields=['itemlen']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(LenEntry,self).__init__(**dict)
        if self.__class__ is LenEntry:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(LenEntry,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(LenEntry,kwargs)
        if len(args):
            dict2={'sizeinbytes': 2,  'default': 0 }
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_itemlen=UINT(*args,**dict2)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_itemlen
        except:
            self.__field_itemlen=UINT(**{'sizeinbytes': 2,  'default': 0 })
        self.__field_itemlen.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_itemlen=UINT(**{'sizeinbytes': 2,  'default': 0 })
        self.__field_itemlen.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_itemlen(self):
        try: self.__field_itemlen
        except:
            self.__field_itemlen=UINT(**{'sizeinbytes': 2,  'default': 0 })
        return self.__field_itemlen.getvalue()
    def __setfield_itemlen(self, value):
        if isinstance(value,UINT):
            self.__field_itemlen=value
        else:
            self.__field_itemlen=UINT(value,**{'sizeinbytes': 2,  'default': 0 })
    def __delfield_itemlen(self): del self.__field_itemlen
    itemlen=property(__getfield_itemlen, __setfield_itemlen, __delfield_itemlen, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('itemlen', self.__field_itemlen, None)
class PBFile(BaseProtogenClass):
    __fields=['lens', 'items']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(PBFile,self).__init__(**dict)
        if self.__class__ is PBFile:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(PBFile,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(PBFile,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_lens
        except:
            self.__field_lens=LIST(**{ 'elementclass': LenEntry,             'length': 8,             'createdefault': True })
        self.__field_lens.writetobuffer(buf)
        try: self.__field_items
        except:
            self.__field_items=LIST(**{ 'elementclass': PBEntry })
        self.__field_items.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_lens=LIST(**{ 'elementclass': LenEntry,             'length': 8,             'createdefault': True })
        self.__field_lens.readfrombuffer(buf)
        self.__field_items=LIST(**{ 'elementclass': PBEntry })
        self.__field_items.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_lens(self):
        try: self.__field_lens
        except:
            self.__field_lens=LIST(**{ 'elementclass': LenEntry,             'length': 8,             'createdefault': True })
        return self.__field_lens.getvalue()
    def __setfield_lens(self, value):
        if isinstance(value,LIST):
            self.__field_lens=value
        else:
            self.__field_lens=LIST(value,**{ 'elementclass': LenEntry,             'length': 8,             'createdefault': True })
    def __delfield_lens(self): del self.__field_lens
    lens=property(__getfield_lens, __setfield_lens, __delfield_lens, None)
    def __getfield_items(self):
        try: self.__field_items
        except:
            self.__field_items=LIST(**{ 'elementclass': PBEntry })
        return self.__field_items.getvalue()
    def __setfield_items(self, value):
        if isinstance(value,LIST):
            self.__field_items=value
        else:
            self.__field_items=LIST(value,**{ 'elementclass': PBEntry })
    def __delfield_items(self): del self.__field_items
    items=property(__getfield_items, __setfield_items, __delfield_items, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('lens', self.__field_lens, None)
        yield ('items', self.__field_items, None)
class PBFileHeader(BaseProtogenClass):
    __fields=['lens']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(PBFileHeader,self).__init__(**dict)
        if self.__class__ is PBFileHeader:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(PBFileHeader,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(PBFileHeader,kwargs)
        if len(args):
            dict2={ 'elementclass': LenEntry,             'length': 8,             'createdefault': True }
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_lens=LIST(*args,**dict2)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_lens
        except:
            self.__field_lens=LIST(**{ 'elementclass': LenEntry,             'length': 8,             'createdefault': True })
        self.__field_lens.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_lens=LIST(**{ 'elementclass': LenEntry,             'length': 8,             'createdefault': True })
        self.__field_lens.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_lens(self):
        try: self.__field_lens
        except:
            self.__field_lens=LIST(**{ 'elementclass': LenEntry,             'length': 8,             'createdefault': True })
        return self.__field_lens.getvalue()
    def __setfield_lens(self, value):
        if isinstance(value,LIST):
            self.__field_lens=value
        else:
            self.__field_lens=LIST(value,**{ 'elementclass': LenEntry,             'length': 8,             'createdefault': True })
    def __delfield_lens(self): del self.__field_lens
    lens=property(__getfield_lens, __setfield_lens, __delfield_lens, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('lens', self.__field_lens, None)
class ss_cmd_hdr(BaseProtogenClass):
    __fields=['commandcode', 'command']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(ss_cmd_hdr,self).__init__(**dict)
        if self.__class__ is ss_cmd_hdr:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(ss_cmd_hdr,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(ss_cmd_hdr,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_commandcode
        except:
            self.__field_commandcode=UINT(**{'sizeinbytes': 4,  'default': 0xfa4b })
        self.__field_commandcode.writetobuffer(buf)
        self.__field_command.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_commandcode=UINT(**{'sizeinbytes': 4,  'default': 0xfa4b })
        self.__field_commandcode.readfrombuffer(buf)
        self.__field_command=UINT(**{'sizeinbytes': 1})
        self.__field_command.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_commandcode(self):
        try: self.__field_commandcode
        except:
            self.__field_commandcode=UINT(**{'sizeinbytes': 4,  'default': 0xfa4b })
        return self.__field_commandcode.getvalue()
    def __setfield_commandcode(self, value):
        if isinstance(value,UINT):
            self.__field_commandcode=value
        else:
            self.__field_commandcode=UINT(value,**{'sizeinbytes': 4,  'default': 0xfa4b })
    def __delfield_commandcode(self): del self.__field_commandcode
    commandcode=property(__getfield_commandcode, __setfield_commandcode, __delfield_commandcode, None)
    def __getfield_command(self):
        return self.__field_command.getvalue()
    def __setfield_command(self, value):
        if isinstance(value,UINT):
            self.__field_command=value
        else:
            self.__field_command=UINT(value,**{'sizeinbytes': 1})
    def __delfield_command(self): del self.__field_command
    command=property(__getfield_command, __setfield_command, __delfield_command, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('commandcode', self.__field_commandcode, None)
        yield ('command', self.__field_command, None)
class ss_cmd_resp(BaseProtogenClass):
    __fields=['cmd_hdr', 'data']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(ss_cmd_resp,self).__init__(**dict)
        if self.__class__ is ss_cmd_resp:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(ss_cmd_resp,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(ss_cmd_resp,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_cmd_hdr.writetobuffer(buf)
        self.__field_data.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_cmd_hdr=ss_cmd_hdr()
        self.__field_cmd_hdr.readfrombuffer(buf)
        self.__field_data=DATA()
        self.__field_data.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_cmd_hdr(self):
        return self.__field_cmd_hdr.getvalue()
    def __setfield_cmd_hdr(self, value):
        if isinstance(value,ss_cmd_hdr):
            self.__field_cmd_hdr=value
        else:
            self.__field_cmd_hdr=ss_cmd_hdr(value,)
    def __delfield_cmd_hdr(self): del self.__field_cmd_hdr
    cmd_hdr=property(__getfield_cmd_hdr, __setfield_cmd_hdr, __delfield_cmd_hdr, None)
    def __getfield_data(self):
        return self.__field_data.getvalue()
    def __setfield_data(self, value):
        if isinstance(value,DATA):
            self.__field_data=value
        else:
            self.__field_data=DATA(value,)
    def __delfield_data(self): del self.__field_data
    data=property(__getfield_data, __setfield_data, __delfield_data, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('cmd_hdr', self.__field_cmd_hdr, None)
        yield ('data', self.__field_data, None)
class ss_sw_req(BaseProtogenClass):
    __fields=['hdr']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(ss_sw_req,self).__init__(**dict)
        if self.__class__ is ss_sw_req:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(ss_sw_req,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(ss_sw_req,kwargs)
        if len(args):
            dict2={ 'command': SS_CMD_SW_VERSION }
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_hdr=ss_cmd_hdr(*args,**dict2)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_hdr
        except:
            self.__field_hdr=ss_cmd_hdr(**{ 'command': SS_CMD_SW_VERSION })
        self.__field_hdr.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_hdr=ss_cmd_hdr(**{ 'command': SS_CMD_SW_VERSION })
        self.__field_hdr.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_hdr(self):
        try: self.__field_hdr
        except:
            self.__field_hdr=ss_cmd_hdr(**{ 'command': SS_CMD_SW_VERSION })
        return self.__field_hdr.getvalue()
    def __setfield_hdr(self, value):
        if isinstance(value,ss_cmd_hdr):
            self.__field_hdr=value
        else:
            self.__field_hdr=ss_cmd_hdr(value,**{ 'command': SS_CMD_SW_VERSION })
    def __delfield_hdr(self): del self.__field_hdr
    hdr=property(__getfield_hdr, __setfield_hdr, __delfield_hdr, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('hdr', self.__field_hdr, None)
class ss_sw_resp(BaseProtogenClass):
    __fields=['hdr', 'sw_version']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(ss_sw_resp,self).__init__(**dict)
        if self.__class__ is ss_sw_resp:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(ss_sw_resp,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(ss_sw_resp,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_hdr.writetobuffer(buf)
        self.__field_sw_version.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_hdr=ss_cmd_hdr()
        self.__field_hdr.readfrombuffer(buf)
        self.__field_sw_version=STRING(**{ 'terminator': 0 })
        self.__field_sw_version.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_hdr(self):
        return self.__field_hdr.getvalue()
    def __setfield_hdr(self, value):
        if isinstance(value,ss_cmd_hdr):
            self.__field_hdr=value
        else:
            self.__field_hdr=ss_cmd_hdr(value,)
    def __delfield_hdr(self): del self.__field_hdr
    hdr=property(__getfield_hdr, __setfield_hdr, __delfield_hdr, None)
    def __getfield_sw_version(self):
        return self.__field_sw_version.getvalue()
    def __setfield_sw_version(self, value):
        if isinstance(value,STRING):
            self.__field_sw_version=value
        else:
            self.__field_sw_version=STRING(value,**{ 'terminator': 0 })
    def __delfield_sw_version(self): del self.__field_sw_version
    sw_version=property(__getfield_sw_version, __setfield_sw_version, __delfield_sw_version, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('hdr', self.__field_hdr, None)
        yield ('sw_version', self.__field_sw_version, None)
class ss_hw_req(BaseProtogenClass):
    __fields=['hdr']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(ss_hw_req,self).__init__(**dict)
        if self.__class__ is ss_hw_req:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(ss_hw_req,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(ss_hw_req,kwargs)
        if len(args):
            dict2={ 'command': SS_CMD_HW_VERSION }
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_hdr=ss_cmd_hdr(*args,**dict2)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_hdr
        except:
            self.__field_hdr=ss_cmd_hdr(**{ 'command': SS_CMD_HW_VERSION })
        self.__field_hdr.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_hdr=ss_cmd_hdr(**{ 'command': SS_CMD_HW_VERSION })
        self.__field_hdr.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_hdr(self):
        try: self.__field_hdr
        except:
            self.__field_hdr=ss_cmd_hdr(**{ 'command': SS_CMD_HW_VERSION })
        return self.__field_hdr.getvalue()
    def __setfield_hdr(self, value):
        if isinstance(value,ss_cmd_hdr):
            self.__field_hdr=value
        else:
            self.__field_hdr=ss_cmd_hdr(value,**{ 'command': SS_CMD_HW_VERSION })
    def __delfield_hdr(self): del self.__field_hdr
    hdr=property(__getfield_hdr, __setfield_hdr, __delfield_hdr, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('hdr', self.__field_hdr, None)
class ss_hw_resp(BaseProtogenClass):
    __fields=['hdr', 'hw_version']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(ss_hw_resp,self).__init__(**dict)
        if self.__class__ is ss_hw_resp:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(ss_hw_resp,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(ss_hw_resp,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_hdr.writetobuffer(buf)
        self.__field_hw_version.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_hdr=ss_cmd_hdr()
        self.__field_hdr.readfrombuffer(buf)
        self.__field_hw_version=STRING(**{ 'terminator': 0 })
        self.__field_hw_version.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_hdr(self):
        return self.__field_hdr.getvalue()
    def __setfield_hdr(self, value):
        if isinstance(value,ss_cmd_hdr):
            self.__field_hdr=value
        else:
            self.__field_hdr=ss_cmd_hdr(value,)
    def __delfield_hdr(self): del self.__field_hdr
    hdr=property(__getfield_hdr, __setfield_hdr, __delfield_hdr, None)
    def __getfield_hw_version(self):
        return self.__field_hw_version.getvalue()
    def __setfield_hw_version(self, value):
        if isinstance(value,STRING):
            self.__field_hw_version=value
        else:
            self.__field_hw_version=STRING(value,**{ 'terminator': 0 })
    def __delfield_hw_version(self): del self.__field_hw_version
    hw_version=property(__getfield_hw_version, __setfield_hw_version, __delfield_hw_version, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('hdr', self.__field_hdr, None)
        yield ('hw_version', self.__field_hw_version, None)
class ss_pb_count_req(BaseProtogenClass):
    __fields=['hdr']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(ss_pb_count_req,self).__init__(**dict)
        if self.__class__ is ss_pb_count_req:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(ss_pb_count_req,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(ss_pb_count_req,kwargs)
        if len(args):
            dict2={ 'command': SS_CMD_PB_COUNT }
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_hdr=ss_cmd_hdr(*args,**dict2)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_hdr
        except:
            self.__field_hdr=ss_cmd_hdr(**{ 'command': SS_CMD_PB_COUNT })
        self.__field_hdr.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_hdr=ss_cmd_hdr(**{ 'command': SS_CMD_PB_COUNT })
        self.__field_hdr.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_hdr(self):
        try: self.__field_hdr
        except:
            self.__field_hdr=ss_cmd_hdr(**{ 'command': SS_CMD_PB_COUNT })
        return self.__field_hdr.getvalue()
    def __setfield_hdr(self, value):
        if isinstance(value,ss_cmd_hdr):
            self.__field_hdr=value
        else:
            self.__field_hdr=ss_cmd_hdr(value,**{ 'command': SS_CMD_PB_COUNT })
    def __delfield_hdr(self): del self.__field_hdr
    hdr=property(__getfield_hdr, __setfield_hdr, __delfield_hdr, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('hdr', self.__field_hdr, None)
class ss_pb_count_resp(BaseProtogenClass):
    __fields=['hdr', 'zero', 'count']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(ss_pb_count_resp,self).__init__(**dict)
        if self.__class__ is ss_pb_count_resp:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(ss_pb_count_resp,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(ss_pb_count_resp,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_hdr.writetobuffer(buf)
        self.__field_zero.writetobuffer(buf)
        self.__field_count.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_hdr=ss_cmd_hdr()
        self.__field_hdr.readfrombuffer(buf)
        self.__field_zero=UINT(**{'sizeinbytes': 1})
        self.__field_zero.readfrombuffer(buf)
        self.__field_count=UINT(**{'sizeinbytes': 2})
        self.__field_count.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_hdr(self):
        return self.__field_hdr.getvalue()
    def __setfield_hdr(self, value):
        if isinstance(value,ss_cmd_hdr):
            self.__field_hdr=value
        else:
            self.__field_hdr=ss_cmd_hdr(value,)
    def __delfield_hdr(self): del self.__field_hdr
    hdr=property(__getfield_hdr, __setfield_hdr, __delfield_hdr, None)
    def __getfield_zero(self):
        return self.__field_zero.getvalue()
    def __setfield_zero(self, value):
        if isinstance(value,UINT):
            self.__field_zero=value
        else:
            self.__field_zero=UINT(value,**{'sizeinbytes': 1})
    def __delfield_zero(self): del self.__field_zero
    zero=property(__getfield_zero, __setfield_zero, __delfield_zero, None)
    def __getfield_count(self):
        return self.__field_count.getvalue()
    def __setfield_count(self, value):
        if isinstance(value,UINT):
            self.__field_count=value
        else:
            self.__field_count=UINT(value,**{'sizeinbytes': 2})
    def __delfield_count(self): del self.__field_count
    count=property(__getfield_count, __setfield_count, __delfield_count, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('hdr', self.__field_hdr, None)
        yield ('zero', self.__field_zero, None)
        yield ('count', self.__field_count, None)
class ss_pb_read_req(BaseProtogenClass):
    __fields=['hdr', 'zero', 'index']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(ss_pb_read_req,self).__init__(**dict)
        if self.__class__ is ss_pb_read_req:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(ss_pb_read_req,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(ss_pb_read_req,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_hdr
        except:
            self.__field_hdr=ss_cmd_hdr(**{ 'command': SS_CMD_PB_READ })
        self.__field_hdr.writetobuffer(buf)
        try: self.__field_zero
        except:
            self.__field_zero=UINT(**{'sizeinbytes': 1,  'default': 0 })
        self.__field_zero.writetobuffer(buf)
        self.__field_index.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_hdr=ss_cmd_hdr(**{ 'command': SS_CMD_PB_READ })
        self.__field_hdr.readfrombuffer(buf)
        self.__field_zero=UINT(**{'sizeinbytes': 1,  'default': 0 })
        self.__field_zero.readfrombuffer(buf)
        self.__field_index=UINT(**{'sizeinbytes': 2})
        self.__field_index.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_hdr(self):
        try: self.__field_hdr
        except:
            self.__field_hdr=ss_cmd_hdr(**{ 'command': SS_CMD_PB_READ })
        return self.__field_hdr.getvalue()
    def __setfield_hdr(self, value):
        if isinstance(value,ss_cmd_hdr):
            self.__field_hdr=value
        else:
            self.__field_hdr=ss_cmd_hdr(value,**{ 'command': SS_CMD_PB_READ })
    def __delfield_hdr(self): del self.__field_hdr
    hdr=property(__getfield_hdr, __setfield_hdr, __delfield_hdr, None)
    def __getfield_zero(self):
        try: self.__field_zero
        except:
            self.__field_zero=UINT(**{'sizeinbytes': 1,  'default': 0 })
        return self.__field_zero.getvalue()
    def __setfield_zero(self, value):
        if isinstance(value,UINT):
            self.__field_zero=value
        else:
            self.__field_zero=UINT(value,**{'sizeinbytes': 1,  'default': 0 })
    def __delfield_zero(self): del self.__field_zero
    zero=property(__getfield_zero, __setfield_zero, __delfield_zero, None)
    def __getfield_index(self):
        return self.__field_index.getvalue()
    def __setfield_index(self, value):
        if isinstance(value,UINT):
            self.__field_index=value
        else:
            self.__field_index=UINT(value,**{'sizeinbytes': 2})
    def __delfield_index(self): del self.__field_index
    index=property(__getfield_index, __setfield_index, __delfield_index, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('hdr', self.__field_hdr, None)
        yield ('zero', self.__field_zero, None)
        yield ('index', self.__field_index, None)
class ss_pb_read_resp(BaseProtogenClass):
    __fields=['hdr', 'dunno1', 'index', 'dunno2', 'data']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(ss_pb_read_resp,self).__init__(**dict)
        if self.__class__ is ss_pb_read_resp:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(ss_pb_read_resp,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(ss_pb_read_resp,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_hdr.writetobuffer(buf)
        self.__field_dunno1.writetobuffer(buf)
        self.__field_index.writetobuffer(buf)
        self.__field_dunno2.writetobuffer(buf)
        self.__field_data.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_hdr=ss_cmd_hdr()
        self.__field_hdr.readfrombuffer(buf)
        self.__field_dunno1=UINT(**{'sizeinbytes': 1})
        self.__field_dunno1.readfrombuffer(buf)
        self.__field_index=UINT(**{'sizeinbytes': 2})
        self.__field_index.readfrombuffer(buf)
        self.__field_dunno2=UINT(**{'sizeinbytes': 1})
        self.__field_dunno2.readfrombuffer(buf)
        self.__field_data=DATA()
        self.__field_data.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_hdr(self):
        return self.__field_hdr.getvalue()
    def __setfield_hdr(self, value):
        if isinstance(value,ss_cmd_hdr):
            self.__field_hdr=value
        else:
            self.__field_hdr=ss_cmd_hdr(value,)
    def __delfield_hdr(self): del self.__field_hdr
    hdr=property(__getfield_hdr, __setfield_hdr, __delfield_hdr, None)
    def __getfield_dunno1(self):
        return self.__field_dunno1.getvalue()
    def __setfield_dunno1(self, value):
        if isinstance(value,UINT):
            self.__field_dunno1=value
        else:
            self.__field_dunno1=UINT(value,**{'sizeinbytes': 1})
    def __delfield_dunno1(self): del self.__field_dunno1
    dunno1=property(__getfield_dunno1, __setfield_dunno1, __delfield_dunno1, None)
    def __getfield_index(self):
        return self.__field_index.getvalue()
    def __setfield_index(self, value):
        if isinstance(value,UINT):
            self.__field_index=value
        else:
            self.__field_index=UINT(value,**{'sizeinbytes': 2})
    def __delfield_index(self): del self.__field_index
    index=property(__getfield_index, __setfield_index, __delfield_index, None)
    def __getfield_dunno2(self):
        return self.__field_dunno2.getvalue()
    def __setfield_dunno2(self, value):
        if isinstance(value,UINT):
            self.__field_dunno2=value
        else:
            self.__field_dunno2=UINT(value,**{'sizeinbytes': 1})
    def __delfield_dunno2(self): del self.__field_dunno2
    dunno2=property(__getfield_dunno2, __setfield_dunno2, __delfield_dunno2, None)
    def __getfield_data(self):
        return self.__field_data.getvalue()
    def __setfield_data(self, value):
        if isinstance(value,DATA):
            self.__field_data=value
        else:
            self.__field_data=DATA(value,)
    def __delfield_data(self): del self.__field_data
    data=property(__getfield_data, __setfield_data, __delfield_data, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('hdr', self.__field_hdr, None)
        yield ('dunno1', self.__field_dunno1, None)
        yield ('index', self.__field_index, None)
        yield ('dunno2', self.__field_dunno2, None)
        yield ('data', self.__field_data, None)
class ss_pb_voicemail_read_req(BaseProtogenClass):
    __fields=['hdr', 'param']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(ss_pb_voicemail_read_req,self).__init__(**dict)
        if self.__class__ is ss_pb_voicemail_read_req:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(ss_pb_voicemail_read_req,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(ss_pb_voicemail_read_req,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_hdr
        except:
            self.__field_hdr=ss_cmd_hdr(**{ 'command': SS_CMD_PB_VOICEMAIL_READ })
        self.__field_hdr.writetobuffer(buf)
        try: self.__field_param
        except:
            self.__field_param=UINT(**{'sizeinbytes': 1,  'constant': SS_CMD_PB_VOICEMAIL_PARAM })
        self.__field_param.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_hdr=ss_cmd_hdr(**{ 'command': SS_CMD_PB_VOICEMAIL_READ })
        self.__field_hdr.readfrombuffer(buf)
        self.__field_param=UINT(**{'sizeinbytes': 1,  'constant': SS_CMD_PB_VOICEMAIL_PARAM })
        self.__field_param.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_hdr(self):
        try: self.__field_hdr
        except:
            self.__field_hdr=ss_cmd_hdr(**{ 'command': SS_CMD_PB_VOICEMAIL_READ })
        return self.__field_hdr.getvalue()
    def __setfield_hdr(self, value):
        if isinstance(value,ss_cmd_hdr):
            self.__field_hdr=value
        else:
            self.__field_hdr=ss_cmd_hdr(value,**{ 'command': SS_CMD_PB_VOICEMAIL_READ })
    def __delfield_hdr(self): del self.__field_hdr
    hdr=property(__getfield_hdr, __setfield_hdr, __delfield_hdr, None)
    def __getfield_param(self):
        try: self.__field_param
        except:
            self.__field_param=UINT(**{'sizeinbytes': 1,  'constant': SS_CMD_PB_VOICEMAIL_PARAM })
        return self.__field_param.getvalue()
    def __setfield_param(self, value):
        if isinstance(value,UINT):
            self.__field_param=value
        else:
            self.__field_param=UINT(value,**{'sizeinbytes': 1,  'constant': SS_CMD_PB_VOICEMAIL_PARAM })
    def __delfield_param(self): del self.__field_param
    param=property(__getfield_param, __setfield_param, __delfield_param, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('hdr', self.__field_hdr, None)
        yield ('param', self.__field_param, None)
class ss_pb_voicemail_resp(BaseProtogenClass):
    __fields=['hdr', 'param', 'number']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(ss_pb_voicemail_resp,self).__init__(**dict)
        if self.__class__ is ss_pb_voicemail_resp:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(ss_pb_voicemail_resp,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(ss_pb_voicemail_resp,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_hdr.writetobuffer(buf)
        self.__field_param.writetobuffer(buf)
        self.__field_number.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_hdr=ss_cmd_hdr()
        self.__field_hdr.readfrombuffer(buf)
        self.__field_param=UINT(**{'sizeinbytes': 1})
        self.__field_param.readfrombuffer(buf)
        self.__field_number=STRING(**{ 'terminator': 0 })
        self.__field_number.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_hdr(self):
        return self.__field_hdr.getvalue()
    def __setfield_hdr(self, value):
        if isinstance(value,ss_cmd_hdr):
            self.__field_hdr=value
        else:
            self.__field_hdr=ss_cmd_hdr(value,)
    def __delfield_hdr(self): del self.__field_hdr
    hdr=property(__getfield_hdr, __setfield_hdr, __delfield_hdr, None)
    def __getfield_param(self):
        return self.__field_param.getvalue()
    def __setfield_param(self, value):
        if isinstance(value,UINT):
            self.__field_param=value
        else:
            self.__field_param=UINT(value,**{'sizeinbytes': 1})
    def __delfield_param(self): del self.__field_param
    param=property(__getfield_param, __setfield_param, __delfield_param, None)
    def __getfield_number(self):
        return self.__field_number.getvalue()
    def __setfield_number(self, value):
        if isinstance(value,STRING):
            self.__field_number=value
        else:
            self.__field_number=STRING(value,**{ 'terminator': 0 })
    def __delfield_number(self): del self.__field_number
    number=property(__getfield_number, __setfield_number, __delfield_number, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('hdr', self.__field_hdr, None)
        yield ('param', self.__field_param, None)
        yield ('number', self.__field_number, None)
class ss_pb_voicemail_write_req(BaseProtogenClass):
    __fields=['hdr', 'param', 'number']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(ss_pb_voicemail_write_req,self).__init__(**dict)
        if self.__class__ is ss_pb_voicemail_write_req:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(ss_pb_voicemail_write_req,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(ss_pb_voicemail_write_req,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_hdr
        except:
            self.__field_hdr=ss_cmd_hdr(**{ 'command': SS_CMD_PB_VOICEMAIL_WRITE })
        self.__field_hdr.writetobuffer(buf)
        try: self.__field_param
        except:
            self.__field_param=UINT(**{'sizeinbytes': 1,  'constant': SS_CMD_PB_VOICEMAIL_PARAM })
        self.__field_param.writetobuffer(buf)
        try: self.__field_number
        except:
            self.__field_number=STRING(**{ 'terminator': 0,               'default': PB_DEFAULT_VOICEMAIL_NUMBER })
        self.__field_number.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_hdr=ss_cmd_hdr(**{ 'command': SS_CMD_PB_VOICEMAIL_WRITE })
        self.__field_hdr.readfrombuffer(buf)
        self.__field_param=UINT(**{'sizeinbytes': 1,  'constant': SS_CMD_PB_VOICEMAIL_PARAM })
        self.__field_param.readfrombuffer(buf)
        self.__field_number=STRING(**{ 'terminator': 0,               'default': PB_DEFAULT_VOICEMAIL_NUMBER })
        self.__field_number.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_hdr(self):
        try: self.__field_hdr
        except:
            self.__field_hdr=ss_cmd_hdr(**{ 'command': SS_CMD_PB_VOICEMAIL_WRITE })
        return self.__field_hdr.getvalue()
    def __setfield_hdr(self, value):
        if isinstance(value,ss_cmd_hdr):
            self.__field_hdr=value
        else:
            self.__field_hdr=ss_cmd_hdr(value,**{ 'command': SS_CMD_PB_VOICEMAIL_WRITE })
    def __delfield_hdr(self): del self.__field_hdr
    hdr=property(__getfield_hdr, __setfield_hdr, __delfield_hdr, None)
    def __getfield_param(self):
        try: self.__field_param
        except:
            self.__field_param=UINT(**{'sizeinbytes': 1,  'constant': SS_CMD_PB_VOICEMAIL_PARAM })
        return self.__field_param.getvalue()
    def __setfield_param(self, value):
        if isinstance(value,UINT):
            self.__field_param=value
        else:
            self.__field_param=UINT(value,**{'sizeinbytes': 1,  'constant': SS_CMD_PB_VOICEMAIL_PARAM })
    def __delfield_param(self): del self.__field_param
    param=property(__getfield_param, __setfield_param, __delfield_param, None)
    def __getfield_number(self):
        try: self.__field_number
        except:
            self.__field_number=STRING(**{ 'terminator': 0,               'default': PB_DEFAULT_VOICEMAIL_NUMBER })
        return self.__field_number.getvalue()
    def __setfield_number(self, value):
        if isinstance(value,STRING):
            self.__field_number=value
        else:
            self.__field_number=STRING(value,**{ 'terminator': 0,               'default': PB_DEFAULT_VOICEMAIL_NUMBER })
    def __delfield_number(self): del self.__field_number
    number=property(__getfield_number, __setfield_number, __delfield_number, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('hdr', self.__field_hdr, None)
        yield ('param', self.__field_param, None)
        yield ('number', self.__field_number, None)
class ss_pb_clear_req(BaseProtogenClass):
    __fields=['hdr']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(ss_pb_clear_req,self).__init__(**dict)
        if self.__class__ is ss_pb_clear_req:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(ss_pb_clear_req,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(ss_pb_clear_req,kwargs)
        if len(args):
            dict2={ 'command': SS_CMD_PB_CLEAR }
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_hdr=ss_cmd_hdr(*args,**dict2)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_hdr
        except:
            self.__field_hdr=ss_cmd_hdr(**{ 'command': SS_CMD_PB_CLEAR })
        self.__field_hdr.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_hdr=ss_cmd_hdr(**{ 'command': SS_CMD_PB_CLEAR })
        self.__field_hdr.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_hdr(self):
        try: self.__field_hdr
        except:
            self.__field_hdr=ss_cmd_hdr(**{ 'command': SS_CMD_PB_CLEAR })
        return self.__field_hdr.getvalue()
    def __setfield_hdr(self, value):
        if isinstance(value,ss_cmd_hdr):
            self.__field_hdr=value
        else:
            self.__field_hdr=ss_cmd_hdr(value,**{ 'command': SS_CMD_PB_CLEAR })
    def __delfield_hdr(self): del self.__field_hdr
    hdr=property(__getfield_hdr, __setfield_hdr, __delfield_hdr, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('hdr', self.__field_hdr, None)
class ss_pb_clear_resp(BaseProtogenClass):
    __fields=['hdr', 'flg']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(ss_pb_clear_resp,self).__init__(**dict)
        if self.__class__ is ss_pb_clear_resp:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(ss_pb_clear_resp,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(ss_pb_clear_resp,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_hdr.writetobuffer(buf)
        self.__field_flg.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_hdr=ss_cmd_hdr()
        self.__field_hdr.readfrombuffer(buf)
        self.__field_flg=UINT(**{'sizeinbytes': 2})
        self.__field_flg.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_hdr(self):
        return self.__field_hdr.getvalue()
    def __setfield_hdr(self, value):
        if isinstance(value,ss_cmd_hdr):
            self.__field_hdr=value
        else:
            self.__field_hdr=ss_cmd_hdr(value,)
    def __delfield_hdr(self): del self.__field_hdr
    hdr=property(__getfield_hdr, __setfield_hdr, __delfield_hdr, None)
    def __getfield_flg(self):
        return self.__field_flg.getvalue()
    def __setfield_flg(self, value):
        if isinstance(value,UINT):
            self.__field_flg=value
        else:
            self.__field_flg=UINT(value,**{'sizeinbytes': 2})
    def __delfield_flg(self): del self.__field_flg
    flg=property(__getfield_flg, __setfield_flg, __delfield_flg, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('hdr', self.__field_hdr, None)
        yield ('flg', self.__field_flg, None)
class ss_number_entry(BaseProtogenClass):
    __fields=['number', 'speeddial', 'primary', 'zero', 'ringtone']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(ss_number_entry,self).__init__(**dict)
        if self.__class__ is ss_number_entry:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(ss_number_entry,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(ss_number_entry,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_number
        except:
            self.__field_number=STRING(**{ 'terminator': 0,               'default': '',               'maxsizeinbytes': PB_MAX_NUMBER_LEN,               'raiseontruncate': False })
        self.__field_number.writetobuffer(buf)
        try: self.__field_speeddial
        except:
            self.__field_speeddial=UINT(**{'sizeinbytes': 2,  'default': 0 })
        self.__field_speeddial.writetobuffer(buf)
        try: self.__field_primary
        except:
            self.__field_primary=UINT(**{'sizeinbytes': 1,  'default': 0 })
        self.__field_primary.writetobuffer(buf)
        try: self.__field_zero
        except:
            self.__field_zero=STRING(**{'sizeinbytes': 8,  'pad': 0,               'default': '' })
        self.__field_zero.writetobuffer(buf)
        try: self.__field_ringtone
        except:
            self.__field_ringtone=STRING(**{ 'terminator': 0,               'default': '' })
        self.__field_ringtone.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_number=STRING(**{ 'terminator': 0,               'default': '',               'maxsizeinbytes': PB_MAX_NUMBER_LEN,               'raiseontruncate': False })
        self.__field_number.readfrombuffer(buf)
        self.__field_speeddial=UINT(**{'sizeinbytes': 2,  'default': 0 })
        self.__field_speeddial.readfrombuffer(buf)
        self.__field_primary=UINT(**{'sizeinbytes': 1,  'default': 0 })
        self.__field_primary.readfrombuffer(buf)
        self.__field_zero=STRING(**{'sizeinbytes': 8,  'pad': 0,               'default': '' })
        self.__field_zero.readfrombuffer(buf)
        self.__field_ringtone=STRING(**{ 'terminator': 0,               'default': '' })
        self.__field_ringtone.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_number(self):
        try: self.__field_number
        except:
            self.__field_number=STRING(**{ 'terminator': 0,               'default': '',               'maxsizeinbytes': PB_MAX_NUMBER_LEN,               'raiseontruncate': False })
        return self.__field_number.getvalue()
    def __setfield_number(self, value):
        if isinstance(value,STRING):
            self.__field_number=value
        else:
            self.__field_number=STRING(value,**{ 'terminator': 0,               'default': '',               'maxsizeinbytes': PB_MAX_NUMBER_LEN,               'raiseontruncate': False })
    def __delfield_number(self): del self.__field_number
    number=property(__getfield_number, __setfield_number, __delfield_number, None)
    def __getfield_speeddial(self):
        try: self.__field_speeddial
        except:
            self.__field_speeddial=UINT(**{'sizeinbytes': 2,  'default': 0 })
        return self.__field_speeddial.getvalue()
    def __setfield_speeddial(self, value):
        if isinstance(value,UINT):
            self.__field_speeddial=value
        else:
            self.__field_speeddial=UINT(value,**{'sizeinbytes': 2,  'default': 0 })
    def __delfield_speeddial(self): del self.__field_speeddial
    speeddial=property(__getfield_speeddial, __setfield_speeddial, __delfield_speeddial, None)
    def __getfield_primary(self):
        try: self.__field_primary
        except:
            self.__field_primary=UINT(**{'sizeinbytes': 1,  'default': 0 })
        return self.__field_primary.getvalue()
    def __setfield_primary(self, value):
        if isinstance(value,UINT):
            self.__field_primary=value
        else:
            self.__field_primary=UINT(value,**{'sizeinbytes': 1,  'default': 0 })
    def __delfield_primary(self): del self.__field_primary
    primary=property(__getfield_primary, __setfield_primary, __delfield_primary, None)
    def __getfield_zero(self):
        try: self.__field_zero
        except:
            self.__field_zero=STRING(**{'sizeinbytes': 8,  'pad': 0,               'default': '' })
        return self.__field_zero.getvalue()
    def __setfield_zero(self, value):
        if isinstance(value,STRING):
            self.__field_zero=value
        else:
            self.__field_zero=STRING(value,**{'sizeinbytes': 8,  'pad': 0,               'default': '' })
    def __delfield_zero(self): del self.__field_zero
    zero=property(__getfield_zero, __setfield_zero, __delfield_zero, None)
    def __getfield_ringtone(self):
        try: self.__field_ringtone
        except:
            self.__field_ringtone=STRING(**{ 'terminator': 0,               'default': '' })
        return self.__field_ringtone.getvalue()
    def __setfield_ringtone(self, value):
        if isinstance(value,STRING):
            self.__field_ringtone=value
        else:
            self.__field_ringtone=STRING(value,**{ 'terminator': 0,               'default': '' })
    def __delfield_ringtone(self): del self.__field_ringtone
    ringtone=property(__getfield_ringtone, __setfield_ringtone, __delfield_ringtone, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('number', self.__field_number, None)
        yield ('speeddial', self.__field_speeddial, None)
        yield ('primary', self.__field_primary, None)
        yield ('zero', self.__field_zero, None)
        yield ('ringtone', self.__field_ringtone, None)
class ss_pb_entry(BaseProtogenClass):
    __fields=['name', 'email', 'email2', 'zero1', 'wallpaper', 'zero2', 'home', 'work', 'cell', 'dummy', 'fax', 'cell2', 'zero3', 'group', 'zero4']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(ss_pb_entry,self).__init__(**dict)
        if self.__class__ is ss_pb_entry:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(ss_pb_entry,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(ss_pb_entry,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_name.writetobuffer(buf)
        try: self.__field_email
        except:
            self.__field_email=STRING(**{ 'terminator': 0,               'default': '',               'maxsizeinbytes': PB_MAX_EMAIL_LEN,               'raiseontruncate': False })
        self.__field_email.writetobuffer(buf)
        try: self.__field_email2
        except:
            self.__field_email2=STRING(**{ 'terminator': 0,               'default': '',               'maxsizeinbytes': PB_MAX_EMAIL_LEN,               'raiseontruncate': False })
        self.__field_email2.writetobuffer(buf)
        try: self.__field_zero1
        except:
            self.__field_zero1=UINT(**{'sizeinbytes': 4,  'default': 0 })
        self.__field_zero1.writetobuffer(buf)
        try: self.__field_wallpaper
        except:
            self.__field_wallpaper=STRING(**{ 'terminator': 0,               'default': '' })
        self.__field_wallpaper.writetobuffer(buf)
        try: self.__field_zero2
        except:
            self.__field_zero2=UINT(**{'sizeinbytes': 1,  'default': 0 })
        self.__field_zero2.writetobuffer(buf)
        try: self.__field_home
        except:
            self.__field_home=ss_number_entry()
        self.__field_home.writetobuffer(buf)
        try: self.__field_work
        except:
            self.__field_work=ss_number_entry()
        self.__field_work.writetobuffer(buf)
        try: self.__field_cell
        except:
            self.__field_cell=ss_number_entry()
        self.__field_cell.writetobuffer(buf)
        try: self.__field_dummy
        except:
            self.__field_dummy=ss_number_entry()
        self.__field_dummy.writetobuffer(buf)
        try: self.__field_fax
        except:
            self.__field_fax=ss_number_entry()
        self.__field_fax.writetobuffer(buf)
        try: self.__field_cell2
        except:
            self.__field_cell2=ss_number_entry()
        self.__field_cell2.writetobuffer(buf)
        try: self.__field_zero3
        except:
            self.__field_zero3=UINT(**{'sizeinbytes': 4,  'default': 0 })
        self.__field_zero3.writetobuffer(buf)
        try: self.__field_group
        except:
            self.__field_group=UINT(**{'sizeinbytes': 1,  'default': 0 })
        self.__field_group.writetobuffer(buf)
        try: self.__field_zero4
        except:
            self.__field_zero4=UINT(**{'sizeinbytes': 2,  'default': 0 })
        self.__field_zero4.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_name=STRING(**{ 'terminator': 0,               'maxsizeinbytes': PB_MAX_NAME_LEN,               'raiseontruncate': False })
        self.__field_name.readfrombuffer(buf)
        self.__field_email=STRING(**{ 'terminator': 0,               'default': '',               'maxsizeinbytes': PB_MAX_EMAIL_LEN,               'raiseontruncate': False })
        self.__field_email.readfrombuffer(buf)
        self.__field_email2=STRING(**{ 'terminator': 0,               'default': '',               'maxsizeinbytes': PB_MAX_EMAIL_LEN,               'raiseontruncate': False })
        self.__field_email2.readfrombuffer(buf)
        self.__field_zero1=UINT(**{'sizeinbytes': 4,  'default': 0 })
        self.__field_zero1.readfrombuffer(buf)
        self.__field_wallpaper=STRING(**{ 'terminator': 0,               'default': '' })
        self.__field_wallpaper.readfrombuffer(buf)
        self.__field_zero2=UINT(**{'sizeinbytes': 1,  'default': 0 })
        self.__field_zero2.readfrombuffer(buf)
        self.__field_home=ss_number_entry()
        self.__field_home.readfrombuffer(buf)
        self.__field_work=ss_number_entry()
        self.__field_work.readfrombuffer(buf)
        self.__field_cell=ss_number_entry()
        self.__field_cell.readfrombuffer(buf)
        self.__field_dummy=ss_number_entry()
        self.__field_dummy.readfrombuffer(buf)
        self.__field_fax=ss_number_entry()
        self.__field_fax.readfrombuffer(buf)
        self.__field_cell2=ss_number_entry()
        self.__field_cell2.readfrombuffer(buf)
        self.__field_zero3=UINT(**{'sizeinbytes': 4,  'default': 0 })
        self.__field_zero3.readfrombuffer(buf)
        self.__field_group=UINT(**{'sizeinbytes': 1,  'default': 0 })
        self.__field_group.readfrombuffer(buf)
        self.__field_zero4=UINT(**{'sizeinbytes': 2,  'default': 0 })
        self.__field_zero4.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_name(self):
        return self.__field_name.getvalue()
    def __setfield_name(self, value):
        if isinstance(value,STRING):
            self.__field_name=value
        else:
            self.__field_name=STRING(value,**{ 'terminator': 0,               'maxsizeinbytes': PB_MAX_NAME_LEN,               'raiseontruncate': False })
    def __delfield_name(self): del self.__field_name
    name=property(__getfield_name, __setfield_name, __delfield_name, None)
    def __getfield_email(self):
        try: self.__field_email
        except:
            self.__field_email=STRING(**{ 'terminator': 0,               'default': '',               'maxsizeinbytes': PB_MAX_EMAIL_LEN,               'raiseontruncate': False })
        return self.__field_email.getvalue()
    def __setfield_email(self, value):
        if isinstance(value,STRING):
            self.__field_email=value
        else:
            self.__field_email=STRING(value,**{ 'terminator': 0,               'default': '',               'maxsizeinbytes': PB_MAX_EMAIL_LEN,               'raiseontruncate': False })
    def __delfield_email(self): del self.__field_email
    email=property(__getfield_email, __setfield_email, __delfield_email, None)
    def __getfield_email2(self):
        try: self.__field_email2
        except:
            self.__field_email2=STRING(**{ 'terminator': 0,               'default': '',               'maxsizeinbytes': PB_MAX_EMAIL_LEN,               'raiseontruncate': False })
        return self.__field_email2.getvalue()
    def __setfield_email2(self, value):
        if isinstance(value,STRING):
            self.__field_email2=value
        else:
            self.__field_email2=STRING(value,**{ 'terminator': 0,               'default': '',               'maxsizeinbytes': PB_MAX_EMAIL_LEN,               'raiseontruncate': False })
    def __delfield_email2(self): del self.__field_email2
    email2=property(__getfield_email2, __setfield_email2, __delfield_email2, None)
    def __getfield_zero1(self):
        try: self.__field_zero1
        except:
            self.__field_zero1=UINT(**{'sizeinbytes': 4,  'default': 0 })
        return self.__field_zero1.getvalue()
    def __setfield_zero1(self, value):
        if isinstance(value,UINT):
            self.__field_zero1=value
        else:
            self.__field_zero1=UINT(value,**{'sizeinbytes': 4,  'default': 0 })
    def __delfield_zero1(self): del self.__field_zero1
    zero1=property(__getfield_zero1, __setfield_zero1, __delfield_zero1, None)
    def __getfield_wallpaper(self):
        try: self.__field_wallpaper
        except:
            self.__field_wallpaper=STRING(**{ 'terminator': 0,               'default': '' })
        return self.__field_wallpaper.getvalue()
    def __setfield_wallpaper(self, value):
        if isinstance(value,STRING):
            self.__field_wallpaper=value
        else:
            self.__field_wallpaper=STRING(value,**{ 'terminator': 0,               'default': '' })
    def __delfield_wallpaper(self): del self.__field_wallpaper
    wallpaper=property(__getfield_wallpaper, __setfield_wallpaper, __delfield_wallpaper, None)
    def __getfield_zero2(self):
        try: self.__field_zero2
        except:
            self.__field_zero2=UINT(**{'sizeinbytes': 1,  'default': 0 })
        return self.__field_zero2.getvalue()
    def __setfield_zero2(self, value):
        if isinstance(value,UINT):
            self.__field_zero2=value
        else:
            self.__field_zero2=UINT(value,**{'sizeinbytes': 1,  'default': 0 })
    def __delfield_zero2(self): del self.__field_zero2
    zero2=property(__getfield_zero2, __setfield_zero2, __delfield_zero2, None)
    def __getfield_home(self):
        try: self.__field_home
        except:
            self.__field_home=ss_number_entry()
        return self.__field_home.getvalue()
    def __setfield_home(self, value):
        if isinstance(value,ss_number_entry):
            self.__field_home=value
        else:
            self.__field_home=ss_number_entry(value,)
    def __delfield_home(self): del self.__field_home
    home=property(__getfield_home, __setfield_home, __delfield_home, None)
    def __getfield_work(self):
        try: self.__field_work
        except:
            self.__field_work=ss_number_entry()
        return self.__field_work.getvalue()
    def __setfield_work(self, value):
        if isinstance(value,ss_number_entry):
            self.__field_work=value
        else:
            self.__field_work=ss_number_entry(value,)
    def __delfield_work(self): del self.__field_work
    work=property(__getfield_work, __setfield_work, __delfield_work, None)
    def __getfield_cell(self):
        try: self.__field_cell
        except:
            self.__field_cell=ss_number_entry()
        return self.__field_cell.getvalue()
    def __setfield_cell(self, value):
        if isinstance(value,ss_number_entry):
            self.__field_cell=value
        else:
            self.__field_cell=ss_number_entry(value,)
    def __delfield_cell(self): del self.__field_cell
    cell=property(__getfield_cell, __setfield_cell, __delfield_cell, None)
    def __getfield_dummy(self):
        try: self.__field_dummy
        except:
            self.__field_dummy=ss_number_entry()
        return self.__field_dummy.getvalue()
    def __setfield_dummy(self, value):
        if isinstance(value,ss_number_entry):
            self.__field_dummy=value
        else:
            self.__field_dummy=ss_number_entry(value,)
    def __delfield_dummy(self): del self.__field_dummy
    dummy=property(__getfield_dummy, __setfield_dummy, __delfield_dummy, None)
    def __getfield_fax(self):
        try: self.__field_fax
        except:
            self.__field_fax=ss_number_entry()
        return self.__field_fax.getvalue()
    def __setfield_fax(self, value):
        if isinstance(value,ss_number_entry):
            self.__field_fax=value
        else:
            self.__field_fax=ss_number_entry(value,)
    def __delfield_fax(self): del self.__field_fax
    fax=property(__getfield_fax, __setfield_fax, __delfield_fax, None)
    def __getfield_cell2(self):
        try: self.__field_cell2
        except:
            self.__field_cell2=ss_number_entry()
        return self.__field_cell2.getvalue()
    def __setfield_cell2(self, value):
        if isinstance(value,ss_number_entry):
            self.__field_cell2=value
        else:
            self.__field_cell2=ss_number_entry(value,)
    def __delfield_cell2(self): del self.__field_cell2
    cell2=property(__getfield_cell2, __setfield_cell2, __delfield_cell2, None)
    def __getfield_zero3(self):
        try: self.__field_zero3
        except:
            self.__field_zero3=UINT(**{'sizeinbytes': 4,  'default': 0 })
        return self.__field_zero3.getvalue()
    def __setfield_zero3(self, value):
        if isinstance(value,UINT):
            self.__field_zero3=value
        else:
            self.__field_zero3=UINT(value,**{'sizeinbytes': 4,  'default': 0 })
    def __delfield_zero3(self): del self.__field_zero3
    zero3=property(__getfield_zero3, __setfield_zero3, __delfield_zero3, None)
    def __getfield_group(self):
        try: self.__field_group
        except:
            self.__field_group=UINT(**{'sizeinbytes': 1,  'default': 0 })
        return self.__field_group.getvalue()
    def __setfield_group(self, value):
        if isinstance(value,UINT):
            self.__field_group=value
        else:
            self.__field_group=UINT(value,**{'sizeinbytes': 1,  'default': 0 })
    def __delfield_group(self): del self.__field_group
    group=property(__getfield_group, __setfield_group, __delfield_group, None)
    def __getfield_zero4(self):
        try: self.__field_zero4
        except:
            self.__field_zero4=UINT(**{'sizeinbytes': 2,  'default': 0 })
        return self.__field_zero4.getvalue()
    def __setfield_zero4(self, value):
        if isinstance(value,UINT):
            self.__field_zero4=value
        else:
            self.__field_zero4=UINT(value,**{'sizeinbytes': 2,  'default': 0 })
    def __delfield_zero4(self): del self.__field_zero4
    zero4=property(__getfield_zero4, __setfield_zero4, __delfield_zero4, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('name', self.__field_name, None)
        yield ('email', self.__field_email, None)
        yield ('email2', self.__field_email2, None)
        yield ('zero1', self.__field_zero1, None)
        yield ('wallpaper', self.__field_wallpaper, None)
        yield ('zero2', self.__field_zero2, None)
        yield ('home', self.__field_home, None)
        yield ('work', self.__field_work, None)
        yield ('cell', self.__field_cell, None)
        yield ('dummy', self.__field_dummy, None)
        yield ('fax', self.__field_fax, None)
        yield ('cell2', self.__field_cell2, None)
        yield ('zero3', self.__field_zero3, None)
        yield ('group', self.__field_group, None)
        yield ('zero4', self.__field_zero4, None)
class ss_pb_write_req(BaseProtogenClass):
    __fields=['hdr', 'zero', 'entry']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(ss_pb_write_req,self).__init__(**dict)
        if self.__class__ is ss_pb_write_req:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(ss_pb_write_req,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(ss_pb_write_req,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_hdr
        except:
            self.__field_hdr=ss_cmd_hdr(**{ 'command': SS_CMD_PB_WRITE })
        self.__field_hdr.writetobuffer(buf)
        try: self.__field_zero
        except:
            self.__field_zero=UINT(**{'sizeinbytes': 1,  'default': 0 })
        self.__field_zero.writetobuffer(buf)
        self.__field_entry.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_hdr=ss_cmd_hdr(**{ 'command': SS_CMD_PB_WRITE })
        self.__field_hdr.readfrombuffer(buf)
        self.__field_zero=UINT(**{'sizeinbytes': 1,  'default': 0 })
        self.__field_zero.readfrombuffer(buf)
        self.__field_entry=ss_pb_entry()
        self.__field_entry.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_hdr(self):
        try: self.__field_hdr
        except:
            self.__field_hdr=ss_cmd_hdr(**{ 'command': SS_CMD_PB_WRITE })
        return self.__field_hdr.getvalue()
    def __setfield_hdr(self, value):
        if isinstance(value,ss_cmd_hdr):
            self.__field_hdr=value
        else:
            self.__field_hdr=ss_cmd_hdr(value,**{ 'command': SS_CMD_PB_WRITE })
    def __delfield_hdr(self): del self.__field_hdr
    hdr=property(__getfield_hdr, __setfield_hdr, __delfield_hdr, None)
    def __getfield_zero(self):
        try: self.__field_zero
        except:
            self.__field_zero=UINT(**{'sizeinbytes': 1,  'default': 0 })
        return self.__field_zero.getvalue()
    def __setfield_zero(self, value):
        if isinstance(value,UINT):
            self.__field_zero=value
        else:
            self.__field_zero=UINT(value,**{'sizeinbytes': 1,  'default': 0 })
    def __delfield_zero(self): del self.__field_zero
    zero=property(__getfield_zero, __setfield_zero, __delfield_zero, None)
    def __getfield_entry(self):
        return self.__field_entry.getvalue()
    def __setfield_entry(self, value):
        if isinstance(value,ss_pb_entry):
            self.__field_entry=value
        else:
            self.__field_entry=ss_pb_entry(value,)
    def __delfield_entry(self): del self.__field_entry
    entry=property(__getfield_entry, __setfield_entry, __delfield_entry, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('hdr', self.__field_hdr, None)
        yield ('zero', self.__field_zero, None)
        yield ('entry', self.__field_entry, None)
class ss_pb_write_resp(BaseProtogenClass):
    __fields=['hdr', 'zero', 'index']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(ss_pb_write_resp,self).__init__(**dict)
        if self.__class__ is ss_pb_write_resp:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(ss_pb_write_resp,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(ss_pb_write_resp,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_hdr.writetobuffer(buf)
        self.__field_zero.writetobuffer(buf)
        self.__field_index.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_hdr=ss_cmd_hdr()
        self.__field_hdr.readfrombuffer(buf)
        self.__field_zero=UINT(**{'sizeinbytes': 1})
        self.__field_zero.readfrombuffer(buf)
        self.__field_index=UINT(**{'sizeinbytes': 2})
        self.__field_index.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_hdr(self):
        return self.__field_hdr.getvalue()
    def __setfield_hdr(self, value):
        if isinstance(value,ss_cmd_hdr):
            self.__field_hdr=value
        else:
            self.__field_hdr=ss_cmd_hdr(value,)
    def __delfield_hdr(self): del self.__field_hdr
    hdr=property(__getfield_hdr, __setfield_hdr, __delfield_hdr, None)
    def __getfield_zero(self):
        return self.__field_zero.getvalue()
    def __setfield_zero(self, value):
        if isinstance(value,UINT):
            self.__field_zero=value
        else:
            self.__field_zero=UINT(value,**{'sizeinbytes': 1})
    def __delfield_zero(self): del self.__field_zero
    zero=property(__getfield_zero, __setfield_zero, __delfield_zero, None)
    def __getfield_index(self):
        return self.__field_index.getvalue()
    def __setfield_index(self, value):
        if isinstance(value,UINT):
            self.__field_index=value
        else:
            self.__field_index=UINT(value,**{'sizeinbytes': 2})
    def __delfield_index(self): del self.__field_index
    index=property(__getfield_index, __setfield_index, __delfield_index, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('hdr', self.__field_hdr, None)
        yield ('zero', self.__field_zero, None)
        yield ('index', self.__field_index, None)
