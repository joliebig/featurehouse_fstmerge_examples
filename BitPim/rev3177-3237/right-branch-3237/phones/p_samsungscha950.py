"""Various descriptions of data specific to the Samsung SCH-A950 Phone"""
from prototypes import *
from prototypes_samsung import *
from p_brew import *
UINT=UINTlsb
BOOL=BOOLlsb
RT_PATH='brew/16452/mr'
RT_INDEX_FILE_NAME=RT_PATH+'/MrInfo.db'
RT_EXCLUDED_FILES=('MrInfo.db',)
SND_PATH='brew/16452/ms'
SND_INDEX_FILE_NAME=SND_PATH+'/MsInfo.db'
SND_EXCLUDED_FILES=('MsInfo.db', 'ExInfo.db')
PIC_PATH='brew/16452/mp'
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
class DefaultReponse(BaseProtogenClass):
    __fields=['data']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(DefaultReponse,self).__init__(**dict)
        if self.__class__ is DefaultReponse:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(DefaultReponse,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(DefaultReponse,kwargs)
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
    __fields=['path', 'name', 'eor']
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
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_path
        except:
            self.__field_path=STRING(**{ 'terminator': None,               'default': '/ff/brew/16452/mr/' })
        self.__field_path.writetobuffer(buf)
        self.__field_name.writetobuffer(buf)
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
        self.__field_path=STRING(**{ 'terminator': None,               'default': '/ff/brew/16452/mr/' })
        self.__field_path.readfrombuffer(buf)
        self.__field_name=STRING(**{ 'terminator': None })
        self.__field_name.readfrombuffer(buf)
        self.__field_eor=STRING(**{ 'terminator': None,               'default': '|2\x0A' })
        self.__field_eor.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_path(self):
        try: self.__field_path
        except:
            self.__field_path=STRING(**{ 'terminator': None,               'default': '/ff/brew/16452/mr/' })
        return self.__field_path.getvalue()
    def __setfield_path(self, value):
        if isinstance(value,STRING):
            self.__field_path=value
        else:
            self.__field_path=STRING(value,**{ 'terminator': None,               'default': '/ff/brew/16452/mr/' })
    def __delfield_path(self): del self.__field_path
    path=property(__getfield_path, __setfield_path, __delfield_path, None)
    def __getfield_name(self):
        return self.__field_name.getvalue()
    def __setfield_name(self, value):
        if isinstance(value,STRING):
            self.__field_name=value
        else:
            self.__field_name=STRING(value,**{ 'terminator': None })
    def __delfield_name(self): del self.__field_name
    name=property(__getfield_name, __setfield_name, __delfield_name, None)
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
        yield ('path', self.__field_path, None)
        yield ('name', self.__field_name, None)
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
    __fields=['path', 'name', 'eor']
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
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_path
        except:
            self.__field_path=STRING(**{ 'terminator': None,               'default': '/ff/brew/16452/ms/' })
        self.__field_path.writetobuffer(buf)
        self.__field_name.writetobuffer(buf)
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
        self.__field_path=STRING(**{ 'terminator': None,               'default': '/ff/brew/16452/ms/' })
        self.__field_path.readfrombuffer(buf)
        self.__field_name=STRING(**{ 'terminator': None })
        self.__field_name.readfrombuffer(buf)
        self.__field_eor=STRING(**{ 'terminator': None,               'default': '|0|7\x0A' })
        self.__field_eor.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_path(self):
        try: self.__field_path
        except:
            self.__field_path=STRING(**{ 'terminator': None,               'default': '/ff/brew/16452/ms/' })
        return self.__field_path.getvalue()
    def __setfield_path(self, value):
        if isinstance(value,STRING):
            self.__field_path=value
        else:
            self.__field_path=STRING(value,**{ 'terminator': None,               'default': '/ff/brew/16452/ms/' })
    def __delfield_path(self): del self.__field_path
    path=property(__getfield_path, __setfield_path, __delfield_path, None)
    def __getfield_name(self):
        return self.__field_name.getvalue()
    def __setfield_name(self, value):
        if isinstance(value,STRING):
            self.__field_name=value
        else:
            self.__field_name=STRING(value,**{ 'terminator': None })
    def __delfield_name(self): del self.__field_name
    name=property(__getfield_name, __setfield_name, __delfield_name, None)
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
        yield ('path', self.__field_path, None)
        yield ('name', self.__field_name, None)
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
    __fields=['name', 'path', 'name2', 'eor']
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
        try: self.__field_path
        except:
            self.__field_path=STRING(**{ 'terminator': None,               'default': '|/ff/brew/16452/mp/' })
        self.__field_path.writetobuffer(buf)
        try: self.__field_name2
        except:
            self.__field_name2=STRING(**{ 'terminator': None,               'default': self.name })
        self.__field_name2.writetobuffer(buf)
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
        self.__field_path=STRING(**{ 'terminator': None,               'default': '|/ff/brew/16452/mp/' })
        self.__field_path.readfrombuffer(buf)
        self.__field_name2=STRING(**{ 'terminator': None,               'default': self.name })
        self.__field_name2.readfrombuffer(buf)
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
    def __getfield_path(self):
        try: self.__field_path
        except:
            self.__field_path=STRING(**{ 'terminator': None,               'default': '|/ff/brew/16452/mp/' })
        return self.__field_path.getvalue()
    def __setfield_path(self, value):
        if isinstance(value,STRING):
            self.__field_path=value
        else:
            self.__field_path=STRING(value,**{ 'terminator': None,               'default': '|/ff/brew/16452/mp/' })
    def __delfield_path(self): del self.__field_path
    path=property(__getfield_path, __setfield_path, __delfield_path, None)
    def __getfield_name2(self):
        try: self.__field_name2
        except:
            self.__field_name2=STRING(**{ 'terminator': None,               'default': self.name })
        return self.__field_name2.getvalue()
    def __setfield_name2(self, value):
        if isinstance(value,STRING):
            self.__field_name2=value
        else:
            self.__field_name2=STRING(value,**{ 'terminator': None,               'default': self.name })
    def __delfield_name2(self): del self.__field_name2
    name2=property(__getfield_name2, __setfield_name2, __delfield_name2, None)
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
        yield ('path', self.__field_path, None)
        yield ('name2', self.__field_name2, None)
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
    __fields=['index', 'dunno1', 'name']
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
        self.__field_name.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_index=UINT(**{'sizeinbytes': 1})
        self.__field_index.readfrombuffer(buf)
        self.__field_dunno1=UNKNOWN(**{'sizeinbytes': 8})
        self.__field_dunno1.readfrombuffer(buf)
        self.__field_name=STRING(**{'sizeinbytes': 70,  'terminator': 0 })
        self.__field_name.readfrombuffer(buf)
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
            self.__field_dunno1=UNKNOWN(value,**{'sizeinbytes': 8})
    def __delfield_dunno1(self): del self.__field_dunno1
    dunno1=property(__getfield_dunno1, __setfield_dunno1, __delfield_dunno1, None)
    def __getfield_name(self):
        return self.__field_name.getvalue()
    def __setfield_name(self, value):
        if isinstance(value,STRING):
            self.__field_name=value
        else:
            self.__field_name=STRING(value,**{'sizeinbytes': 70,  'terminator': 0 })
    def __delfield_name(self): del self.__field_name
    name=property(__getfield_name, __setfield_name, __delfield_name, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('index', self.__field_index, None)
        yield ('dunno1', self.__field_dunno1, None)
        yield ('name', self.__field_name, None)
class GroupIndexFile(BaseProtogenClass):
    __fields=['num_of_entries', 'dunno1', 'No_Group', 'items']
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
        self.__field_dunno1.writetobuffer(buf)
        self.__field_No_Group.writetobuffer(buf)
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
        self.__field_dunno1=UNKNOWN(**{'sizeinbytes': 4})
        self.__field_dunno1.readfrombuffer(buf)
        self.__field_No_Group=UNKNOWN(**{'sizeinbytes': 79})
        self.__field_No_Group.readfrombuffer(buf)
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
    def __getfield_dunno1(self):
        return self.__field_dunno1.getvalue()
    def __setfield_dunno1(self, value):
        if isinstance(value,UNKNOWN):
            self.__field_dunno1=value
        else:
            self.__field_dunno1=UNKNOWN(value,**{'sizeinbytes': 4})
    def __delfield_dunno1(self): del self.__field_dunno1
    dunno1=property(__getfield_dunno1, __setfield_dunno1, __delfield_dunno1, None)
    def __getfield_No_Group(self):
        return self.__field_No_Group.getvalue()
    def __setfield_No_Group(self, value):
        if isinstance(value,UNKNOWN):
            self.__field_No_Group=value
        else:
            self.__field_No_Group=UNKNOWN(value,**{'sizeinbytes': 79})
    def __delfield_No_Group(self): del self.__field_No_Group
    No_Group=property(__getfield_No_Group, __setfield_No_Group, __delfield_No_Group, None)
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
        yield ('dunno1', self.__field_dunno1, None)
        yield ('No_Group', self.__field_No_Group, None)
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
