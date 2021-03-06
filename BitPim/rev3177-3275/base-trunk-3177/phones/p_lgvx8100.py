"""Various descriptions of data specific to LG VX8000"""
from prototypes import *
from prototypeslg import *
from p_lg import *
from p_lgvx7000 import *
UINT=UINTlsb
BOOL=BOOLlsb
SPEEDDIALINDEX=1 
MAXCALENDARDESCRIPTION=32
SMS_CANNED_MAX_ITEMS=18
SMS_CANNED_MAX_LENGTH=101
BREW_FILE_SYSTEM=2
MEDIA_TYPE_RINGTONE=0x0201
MEDIA_TYPE_IMAGE=0x0100
MEDIA_TYPE_SOUND=0x0402
MEDIA_TYPE_SDIMAGE=0x0008
MEDIA_TYPE_SDSOUND=0x000C
MEDIA_TYPE_VIDEO=0x0304
MEDIA_RINGTONE_DEFAULT_ICON=1
MEDIA_IMAGE_DEFAULT_ICON=0
MEDIA_VIDEO_DEFAULT_ICON=0
PHONE_ENCODING='iso-8859-1'
class indexentry(BaseProtogenClass):
    __fields=['index', 'type', 'filename', 'icon', 'date', 'dunno', 'size']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(indexentry,self).__init__(**dict)
        if self.__class__ is indexentry:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(indexentry,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(indexentry,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_index.writetobuffer(buf)
        self.__field_type.writetobuffer(buf)
        self.__field_filename.writetobuffer(buf)
        try: self.__field_icon
        except:
            self.__field_icon=UINT(**{'sizeinbytes': 4, 'default':0})
        self.__field_icon.writetobuffer(buf)
        try: self.__field_date
        except:
            self.__field_date=UINT(**{'sizeinbytes': 4, 'default': 0})
        self.__field_date.writetobuffer(buf)
        self.__field_dunno.writetobuffer(buf)
        try: self.__field_size
        except:
            self.__field_size=UINT(**{'sizeinbytes': 4, 'default': 0})
        self.__field_size.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_index=UINT(**{'sizeinbytes': 2})
        self.__field_index.readfrombuffer(buf)
        self.__field_type=UINT(**{'sizeinbytes': 2})
        self.__field_type.readfrombuffer(buf)
        self.__field_filename=USTRING(**{'sizeinbytes': 60, 'raiseonunterminatedread': False, 'raiseontruncate': False })
        self.__field_filename.readfrombuffer(buf)
        self.__field_icon=UINT(**{'sizeinbytes': 4, 'default':0})
        self.__field_icon.readfrombuffer(buf)
        self.__field_date=UINT(**{'sizeinbytes': 4, 'default': 0})
        self.__field_date.readfrombuffer(buf)
        self.__field_dunno=UINT(**{'sizeinbytes': 4})
        self.__field_dunno.readfrombuffer(buf)
        self.__field_size=UINT(**{'sizeinbytes': 4, 'default': 0})
        self.__field_size.readfrombuffer(buf)
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
    def __getfield_type(self):
        return self.__field_type.getvalue()
    def __setfield_type(self, value):
        if isinstance(value,UINT):
            self.__field_type=value
        else:
            self.__field_type=UINT(value,**{'sizeinbytes': 2})
    def __delfield_type(self): del self.__field_type
    type=property(__getfield_type, __setfield_type, __delfield_type, None)
    def __getfield_filename(self):
        return self.__field_filename.getvalue()
    def __setfield_filename(self, value):
        if isinstance(value,USTRING):
            self.__field_filename=value
        else:
            self.__field_filename=USTRING(value,**{'sizeinbytes': 60, 'raiseonunterminatedread': False, 'raiseontruncate': False })
    def __delfield_filename(self): del self.__field_filename
    filename=property(__getfield_filename, __setfield_filename, __delfield_filename, "includes full pathname")
    def __getfield_icon(self):
        try: self.__field_icon
        except:
            self.__field_icon=UINT(**{'sizeinbytes': 4, 'default':0})
        return self.__field_icon.getvalue()
    def __setfield_icon(self, value):
        if isinstance(value,UINT):
            self.__field_icon=value
        else:
            self.__field_icon=UINT(value,**{'sizeinbytes': 4, 'default':0})
    def __delfield_icon(self): del self.__field_icon
    icon=property(__getfield_icon, __setfield_icon, __delfield_icon, None)
    def __getfield_date(self):
        try: self.__field_date
        except:
            self.__field_date=UINT(**{'sizeinbytes': 4, 'default': 0})
        return self.__field_date.getvalue()
    def __setfield_date(self, value):
        if isinstance(value,UINT):
            self.__field_date=value
        else:
            self.__field_date=UINT(value,**{'sizeinbytes': 4, 'default': 0})
    def __delfield_date(self): del self.__field_date
    date=property(__getfield_date, __setfield_date, __delfield_date, "i think this is bitfield of the date")
    def __getfield_dunno(self):
        return self.__field_dunno.getvalue()
    def __setfield_dunno(self, value):
        if isinstance(value,UINT):
            self.__field_dunno=value
        else:
            self.__field_dunno=UINT(value,**{'sizeinbytes': 4})
    def __delfield_dunno(self): del self.__field_dunno
    dunno=property(__getfield_dunno, __setfield_dunno, __delfield_dunno, None)
    def __getfield_size(self):
        try: self.__field_size
        except:
            self.__field_size=UINT(**{'sizeinbytes': 4, 'default': 0})
        return self.__field_size.getvalue()
    def __setfield_size(self, value):
        if isinstance(value,UINT):
            self.__field_size=value
        else:
            self.__field_size=UINT(value,**{'sizeinbytes': 4, 'default': 0})
    def __delfield_size(self): del self.__field_size
    size=property(__getfield_size, __setfield_size, __delfield_size, "size of the file, can be set to zero")
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('index', self.__field_index, None)
        yield ('type', self.__field_type, None)
        yield ('filename', self.__field_filename, "includes full pathname")
        yield ('icon', self.__field_icon, None)
        yield ('date', self.__field_date, "i think this is bitfield of the date")
        yield ('dunno', self.__field_dunno, None)
        yield ('size', self.__field_size, "size of the file, can be set to zero")
class indexfile(BaseProtogenClass):
    "Used for tracking wallpaper and ringtones"
    __fields=['items']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(indexfile,self).__init__(**dict)
        if self.__class__ is indexfile:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(indexfile,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(indexfile,kwargs)
        if len(args):
            dict2={'elementclass': indexentry, 'createdefault': True}
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_items=LIST(*args,**dict2)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_items
        except:
            self.__field_items=LIST(**{'elementclass': indexentry, 'createdefault': True})
        self.__field_items.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_items=LIST(**{'elementclass': indexentry, 'createdefault': True})
        self.__field_items.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_items(self):
        try: self.__field_items
        except:
            self.__field_items=LIST(**{'elementclass': indexentry, 'createdefault': True})
        return self.__field_items.getvalue()
    def __setfield_items(self, value):
        if isinstance(value,LIST):
            self.__field_items=value
        else:
            self.__field_items=LIST(value,**{'elementclass': indexentry, 'createdefault': True})
    def __delfield_items(self): del self.__field_items
    items=property(__getfield_items, __setfield_items, __delfield_items, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('items', self.__field_items, None)
class pbgroup(BaseProtogenClass):
    "A single group"
    __fields=['name']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(pbgroup,self).__init__(**dict)
        if self.__class__ is pbgroup:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(pbgroup,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(pbgroup,kwargs)
        if len(args):
            dict2={'sizeinbytes': 23, 'encoding': PHONE_ENCODING, 'raiseonunterminatedread': False, 'raiseontruncate': False }
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_name=USTRING(*args,**dict2)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_name.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_name=USTRING(**{'sizeinbytes': 23, 'encoding': PHONE_ENCODING, 'raiseonunterminatedread': False, 'raiseontruncate': False })
        self.__field_name.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_name(self):
        return self.__field_name.getvalue()
    def __setfield_name(self, value):
        if isinstance(value,USTRING):
            self.__field_name=value
        else:
            self.__field_name=USTRING(value,**{'sizeinbytes': 23, 'encoding': PHONE_ENCODING, 'raiseonunterminatedread': False, 'raiseontruncate': False })
    def __delfield_name(self): del self.__field_name
    name=property(__getfield_name, __setfield_name, __delfield_name, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('name', self.__field_name, None)
class pbgroups(BaseProtogenClass):
    "Phonebook groups"
    __fields=['groups']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(pbgroups,self).__init__(**dict)
        if self.__class__ is pbgroups:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(pbgroups,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(pbgroups,kwargs)
        if len(args):
            dict2={'elementclass': pbgroup}
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_groups=LIST(*args,**dict2)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_groups
        except:
            self.__field_groups=LIST(**{'elementclass': pbgroup})
        self.__field_groups.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_groups=LIST(**{'elementclass': pbgroup})
        self.__field_groups.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_groups(self):
        try: self.__field_groups
        except:
            self.__field_groups=LIST(**{'elementclass': pbgroup})
        return self.__field_groups.getvalue()
    def __setfield_groups(self, value):
        if isinstance(value,LIST):
            self.__field_groups=value
        else:
            self.__field_groups=LIST(value,**{'elementclass': pbgroup})
    def __delfield_groups(self): del self.__field_groups
    groups=property(__getfield_groups, __setfield_groups, __delfield_groups, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('groups', self.__field_groups, None)
class scheduleexception(BaseProtogenClass):
    __fields=['pos', 'day', 'month', 'year']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(scheduleexception,self).__init__(**dict)
        if self.__class__ is scheduleexception:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(scheduleexception,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(scheduleexception,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_pos.writetobuffer(buf)
        self.__field_day.writetobuffer(buf)
        self.__field_month.writetobuffer(buf)
        self.__field_year.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_pos=UINT(**{'sizeinbytes': 4})
        self.__field_pos.readfrombuffer(buf)
        self.__field_day=UINT(**{'sizeinbytes': 1})
        self.__field_day.readfrombuffer(buf)
        self.__field_month=UINT(**{'sizeinbytes': 1})
        self.__field_month.readfrombuffer(buf)
        self.__field_year=UINT(**{'sizeinbytes': 2})
        self.__field_year.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_pos(self):
        return self.__field_pos.getvalue()
    def __setfield_pos(self, value):
        if isinstance(value,UINT):
            self.__field_pos=value
        else:
            self.__field_pos=UINT(value,**{'sizeinbytes': 4})
    def __delfield_pos(self): del self.__field_pos
    pos=property(__getfield_pos, __setfield_pos, __delfield_pos, "Refers to event id (position in schedule file) that this suppresses")
    def __getfield_day(self):
        return self.__field_day.getvalue()
    def __setfield_day(self, value):
        if isinstance(value,UINT):
            self.__field_day=value
        else:
            self.__field_day=UINT(value,**{'sizeinbytes': 1})
    def __delfield_day(self): del self.__field_day
    day=property(__getfield_day, __setfield_day, __delfield_day, None)
    def __getfield_month(self):
        return self.__field_month.getvalue()
    def __setfield_month(self, value):
        if isinstance(value,UINT):
            self.__field_month=value
        else:
            self.__field_month=UINT(value,**{'sizeinbytes': 1})
    def __delfield_month(self): del self.__field_month
    month=property(__getfield_month, __setfield_month, __delfield_month, None)
    def __getfield_year(self):
        return self.__field_year.getvalue()
    def __setfield_year(self, value):
        if isinstance(value,UINT):
            self.__field_year=value
        else:
            self.__field_year=UINT(value,**{'sizeinbytes': 2})
    def __delfield_year(self): del self.__field_year
    year=property(__getfield_year, __setfield_year, __delfield_year, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('pos', self.__field_pos, "Refers to event id (position in schedule file) that this suppresses")
        yield ('day', self.__field_day, None)
        yield ('month', self.__field_month, None)
        yield ('year', self.__field_year, None)
class scheduleexceptionfile(BaseProtogenClass):
    __fields=['items']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(scheduleexceptionfile,self).__init__(**dict)
        if self.__class__ is scheduleexceptionfile:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(scheduleexceptionfile,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(scheduleexceptionfile,kwargs)
        if len(args):
            dict2={'elementclass': scheduleexception}
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_items=LIST(*args,**dict2)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_items
        except:
            self.__field_items=LIST(**{'elementclass': scheduleexception})
        self.__field_items.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_items=LIST(**{'elementclass': scheduleexception})
        self.__field_items.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_items(self):
        try: self.__field_items
        except:
            self.__field_items=LIST(**{'elementclass': scheduleexception})
        return self.__field_items.getvalue()
    def __setfield_items(self, value):
        if isinstance(value,LIST):
            self.__field_items=value
        else:
            self.__field_items=LIST(value,**{'elementclass': scheduleexception})
    def __delfield_items(self): del self.__field_items
    items=property(__getfield_items, __setfield_items, __delfield_items, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('items', self.__field_items, None)
class scheduleevent(BaseProtogenClass):
    __fields=['pos', 'description', 'start', 'end', 'repeat', 'alarmindex_vibrate', 'ringtone', 'unknown1', 'alarmminutes', 'alarmhours', 'unknown2']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(scheduleevent,self).__init__(**dict)
        if self.__class__ is scheduleevent:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(scheduleevent,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(scheduleevent,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_pos.writetobuffer(buf)
        self.__field_description.writetobuffer(buf)
        self.__field_start.writetobuffer(buf)
        self.__field_end.writetobuffer(buf)
        self.__field_repeat.writetobuffer(buf)
        self.__field_alarmindex_vibrate.writetobuffer(buf)
        self.__field_ringtone.writetobuffer(buf)
        self.__field_unknown1.writetobuffer(buf)
        self.__field_alarmminutes.writetobuffer(buf)
        self.__field_alarmhours.writetobuffer(buf)
        self.__field_unknown2.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_pos=UINT(**{'sizeinbytes': 4})
        self.__field_pos.readfrombuffer(buf)
        self.__field_description=USTRING(**{'sizeinbytes': 33, 'encoding': PHONE_ENCODING, 'raiseonunterminatedread': False, 'raiseontruncate': False })
        self.__field_description.readfrombuffer(buf)
        self.__field_start=LGCALDATE(**{'sizeinbytes': 4})
        self.__field_start.readfrombuffer(buf)
        self.__field_end=LGCALDATE(**{'sizeinbytes': 4})
        self.__field_end.readfrombuffer(buf)
        self.__field_repeat=LGCALREPEAT(**{'sizeinbytes': 4})
        self.__field_repeat.readfrombuffer(buf)
        self.__field_alarmindex_vibrate=UINT(**{'sizeinbytes': 1})
        self.__field_alarmindex_vibrate.readfrombuffer(buf)
        self.__field_ringtone=UINT(**{'sizeinbytes': 1})
        self.__field_ringtone.readfrombuffer(buf)
        self.__field_unknown1=UINT(**{'sizeinbytes': 1})
        self.__field_unknown1.readfrombuffer(buf)
        self.__field_alarmminutes=UINT(**{'sizeinbytes': 1})
        self.__field_alarmminutes.readfrombuffer(buf)
        self.__field_alarmhours=UINT(**{'sizeinbytes': 1})
        self.__field_alarmhours.readfrombuffer(buf)
        self.__field_unknown2=UINT(**{'sizeinbytes': 1})
        self.__field_unknown2.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_pos(self):
        return self.__field_pos.getvalue()
    def __setfield_pos(self, value):
        if isinstance(value,UINT):
            self.__field_pos=value
        else:
            self.__field_pos=UINT(value,**{'sizeinbytes': 4})
    def __delfield_pos(self): del self.__field_pos
    pos=property(__getfield_pos, __setfield_pos, __delfield_pos, "position within file, used as an event id")
    def __getfield_description(self):
        return self.__field_description.getvalue()
    def __setfield_description(self, value):
        if isinstance(value,USTRING):
            self.__field_description=value
        else:
            self.__field_description=USTRING(value,**{'sizeinbytes': 33, 'encoding': PHONE_ENCODING, 'raiseonunterminatedread': False, 'raiseontruncate': False })
    def __delfield_description(self): del self.__field_description
    description=property(__getfield_description, __setfield_description, __delfield_description, None)
    def __getfield_start(self):
        return self.__field_start.getvalue()
    def __setfield_start(self, value):
        if isinstance(value,LGCALDATE):
            self.__field_start=value
        else:
            self.__field_start=LGCALDATE(value,**{'sizeinbytes': 4})
    def __delfield_start(self): del self.__field_start
    start=property(__getfield_start, __setfield_start, __delfield_start, None)
    def __getfield_end(self):
        return self.__field_end.getvalue()
    def __setfield_end(self, value):
        if isinstance(value,LGCALDATE):
            self.__field_end=value
        else:
            self.__field_end=LGCALDATE(value,**{'sizeinbytes': 4})
    def __delfield_end(self): del self.__field_end
    end=property(__getfield_end, __setfield_end, __delfield_end, None)
    def __getfield_repeat(self):
        return self.__field_repeat.getvalue()
    def __setfield_repeat(self, value):
        if isinstance(value,LGCALREPEAT):
            self.__field_repeat=value
        else:
            self.__field_repeat=LGCALREPEAT(value,**{'sizeinbytes': 4})
    def __delfield_repeat(self): del self.__field_repeat
    repeat=property(__getfield_repeat, __setfield_repeat, __delfield_repeat, None)
    def __getfield_alarmindex_vibrate(self):
        return self.__field_alarmindex_vibrate.getvalue()
    def __setfield_alarmindex_vibrate(self, value):
        if isinstance(value,UINT):
            self.__field_alarmindex_vibrate=value
        else:
            self.__field_alarmindex_vibrate=UINT(value,**{'sizeinbytes': 1})
    def __delfield_alarmindex_vibrate(self): del self.__field_alarmindex_vibrate
    alarmindex_vibrate=property(__getfield_alarmindex_vibrate, __setfield_alarmindex_vibrate, __delfield_alarmindex_vibrate, None)
    def __getfield_ringtone(self):
        return self.__field_ringtone.getvalue()
    def __setfield_ringtone(self, value):
        if isinstance(value,UINT):
            self.__field_ringtone=value
        else:
            self.__field_ringtone=UINT(value,**{'sizeinbytes': 1})
    def __delfield_ringtone(self): del self.__field_ringtone
    ringtone=property(__getfield_ringtone, __setfield_ringtone, __delfield_ringtone, None)
    def __getfield_unknown1(self):
        return self.__field_unknown1.getvalue()
    def __setfield_unknown1(self, value):
        if isinstance(value,UINT):
            self.__field_unknown1=value
        else:
            self.__field_unknown1=UINT(value,**{'sizeinbytes': 1})
    def __delfield_unknown1(self): del self.__field_unknown1
    unknown1=property(__getfield_unknown1, __setfield_unknown1, __delfield_unknown1, None)
    def __getfield_alarmminutes(self):
        return self.__field_alarmminutes.getvalue()
    def __setfield_alarmminutes(self, value):
        if isinstance(value,UINT):
            self.__field_alarmminutes=value
        else:
            self.__field_alarmminutes=UINT(value,**{'sizeinbytes': 1})
    def __delfield_alarmminutes(self): del self.__field_alarmminutes
    alarmminutes=property(__getfield_alarmminutes, __setfield_alarmminutes, __delfield_alarmminutes, "a value of 0xFF indicates not set")
    def __getfield_alarmhours(self):
        return self.__field_alarmhours.getvalue()
    def __setfield_alarmhours(self, value):
        if isinstance(value,UINT):
            self.__field_alarmhours=value
        else:
            self.__field_alarmhours=UINT(value,**{'sizeinbytes': 1})
    def __delfield_alarmhours(self): del self.__field_alarmhours
    alarmhours=property(__getfield_alarmhours, __setfield_alarmhours, __delfield_alarmhours, "a value of 0xFF indicates not set")
    def __getfield_unknown2(self):
        return self.__field_unknown2.getvalue()
    def __setfield_unknown2(self, value):
        if isinstance(value,UINT):
            self.__field_unknown2=value
        else:
            self.__field_unknown2=UINT(value,**{'sizeinbytes': 1})
    def __delfield_unknown2(self): del self.__field_unknown2
    unknown2=property(__getfield_unknown2, __setfield_unknown2, __delfield_unknown2, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('pos', self.__field_pos, "position within file, used as an event id")
        yield ('description', self.__field_description, None)
        yield ('start', self.__field_start, None)
        yield ('end', self.__field_end, None)
        yield ('repeat', self.__field_repeat, None)
        yield ('alarmindex_vibrate', self.__field_alarmindex_vibrate, None)
        yield ('ringtone', self.__field_ringtone, None)
        yield ('unknown1', self.__field_unknown1, None)
        yield ('alarmminutes', self.__field_alarmminutes, "a value of 0xFF indicates not set")
        yield ('alarmhours', self.__field_alarmhours, "a value of 0xFF indicates not set")
        yield ('unknown2', self.__field_unknown2, None)
class schedulefile(BaseProtogenClass):
    __fields=['numactiveitems', 'events']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(schedulefile,self).__init__(**dict)
        if self.__class__ is schedulefile:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(schedulefile,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(schedulefile,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_numactiveitems.writetobuffer(buf)
        try: self.__field_events
        except:
            self.__field_events=LIST(**{'elementclass': scheduleevent})
        self.__field_events.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_numactiveitems=UINT(**{'sizeinbytes': 2})
        self.__field_numactiveitems.readfrombuffer(buf)
        self.__field_events=LIST(**{'elementclass': scheduleevent})
        self.__field_events.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_numactiveitems(self):
        return self.__field_numactiveitems.getvalue()
    def __setfield_numactiveitems(self, value):
        if isinstance(value,UINT):
            self.__field_numactiveitems=value
        else:
            self.__field_numactiveitems=UINT(value,**{'sizeinbytes': 2})
    def __delfield_numactiveitems(self): del self.__field_numactiveitems
    numactiveitems=property(__getfield_numactiveitems, __setfield_numactiveitems, __delfield_numactiveitems, None)
    def __getfield_events(self):
        try: self.__field_events
        except:
            self.__field_events=LIST(**{'elementclass': scheduleevent})
        return self.__field_events.getvalue()
    def __setfield_events(self, value):
        if isinstance(value,LIST):
            self.__field_events=value
        else:
            self.__field_events=LIST(value,**{'elementclass': scheduleevent})
    def __delfield_events(self): del self.__field_events
    events=property(__getfield_events, __setfield_events, __delfield_events, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('numactiveitems', self.__field_numactiveitems, None)
        yield ('events', self.__field_events, None)
class call(BaseProtogenClass):
    __fields=['GPStime', 'unknown2', 'duration', 'number', 'name', 'numberlength', 'pbnumbertype', 'unknown2', 'pbentrynum']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(call,self).__init__(**dict)
        if self.__class__ is call:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(call,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(call,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_GPStime.writetobuffer(buf)
        self.__field_unknown2.writetobuffer(buf)
        self.__field_duration.writetobuffer(buf)
        self.__field_number.writetobuffer(buf)
        self.__field_name.writetobuffer(buf)
        self.__field_numberlength.writetobuffer(buf)
        self.__field_pbnumbertype.writetobuffer(buf)
        self.__field_unknown2.writetobuffer(buf)
        self.__field_pbentrynum.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_GPStime=GPSDATE(**{'sizeinbytes': 4})
        self.__field_GPStime.readfrombuffer(buf)
        self.__field_unknown2=UINT(**{'sizeinbytes': 4})
        self.__field_unknown2.readfrombuffer(buf)
        self.__field_duration=UINT(**{'sizeinbytes': 4})
        self.__field_duration.readfrombuffer(buf)
        self.__field_number=USTRING(**{'sizeinbytes': 49, 'raiseonunterminatedread': False})
        self.__field_number.readfrombuffer(buf)
        self.__field_name=USTRING(**{'sizeinbytes': 36, 'encoding': PHONE_ENCODING, 'raiseonunterminatedread': False})
        self.__field_name.readfrombuffer(buf)
        self.__field_numberlength=UINT(**{'sizeinbytes': 2})
        self.__field_numberlength.readfrombuffer(buf)
        self.__field_pbnumbertype=UINT(**{'sizeinbytes': 1})
        self.__field_pbnumbertype.readfrombuffer(buf)
        self.__field_unknown2=UINT(**{'sizeinbytes': 3})
        self.__field_unknown2.readfrombuffer(buf)
        self.__field_pbentrynum=UINT(**{'sizeinbytes': 2})
        self.__field_pbentrynum.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_GPStime(self):
        return self.__field_GPStime.getvalue()
    def __setfield_GPStime(self, value):
        if isinstance(value,GPSDATE):
            self.__field_GPStime=value
        else:
            self.__field_GPStime=GPSDATE(value,**{'sizeinbytes': 4})
    def __delfield_GPStime(self): del self.__field_GPStime
    GPStime=property(__getfield_GPStime, __setfield_GPStime, __delfield_GPStime, None)
    def __getfield_unknown2(self):
        return self.__field_unknown2.getvalue()
    def __setfield_unknown2(self, value):
        if isinstance(value,UINT):
            self.__field_unknown2=value
        else:
            self.__field_unknown2=UINT(value,**{'sizeinbytes': 4})
    def __delfield_unknown2(self): del self.__field_unknown2
    unknown2=property(__getfield_unknown2, __setfield_unknown2, __delfield_unknown2, None)
    def __getfield_duration(self):
        return self.__field_duration.getvalue()
    def __setfield_duration(self, value):
        if isinstance(value,UINT):
            self.__field_duration=value
        else:
            self.__field_duration=UINT(value,**{'sizeinbytes': 4})
    def __delfield_duration(self): del self.__field_duration
    duration=property(__getfield_duration, __setfield_duration, __delfield_duration, None)
    def __getfield_number(self):
        return self.__field_number.getvalue()
    def __setfield_number(self, value):
        if isinstance(value,USTRING):
            self.__field_number=value
        else:
            self.__field_number=USTRING(value,**{'sizeinbytes': 49, 'raiseonunterminatedread': False})
    def __delfield_number(self): del self.__field_number
    number=property(__getfield_number, __setfield_number, __delfield_number, None)
    def __getfield_name(self):
        return self.__field_name.getvalue()
    def __setfield_name(self, value):
        if isinstance(value,USTRING):
            self.__field_name=value
        else:
            self.__field_name=USTRING(value,**{'sizeinbytes': 36, 'encoding': PHONE_ENCODING, 'raiseonunterminatedread': False})
    def __delfield_name(self): del self.__field_name
    name=property(__getfield_name, __setfield_name, __delfield_name, None)
    def __getfield_numberlength(self):
        return self.__field_numberlength.getvalue()
    def __setfield_numberlength(self, value):
        if isinstance(value,UINT):
            self.__field_numberlength=value
        else:
            self.__field_numberlength=UINT(value,**{'sizeinbytes': 2})
    def __delfield_numberlength(self): del self.__field_numberlength
    numberlength=property(__getfield_numberlength, __setfield_numberlength, __delfield_numberlength, None)
    def __getfield_pbnumbertype(self):
        return self.__field_pbnumbertype.getvalue()
    def __setfield_pbnumbertype(self, value):
        if isinstance(value,UINT):
            self.__field_pbnumbertype=value
        else:
            self.__field_pbnumbertype=UINT(value,**{'sizeinbytes': 1})
    def __delfield_pbnumbertype(self): del self.__field_pbnumbertype
    pbnumbertype=property(__getfield_pbnumbertype, __setfield_pbnumbertype, __delfield_pbnumbertype, None)
    def __getfield_unknown2(self):
        return self.__field_unknown2.getvalue()
    def __setfield_unknown2(self, value):
        if isinstance(value,UINT):
            self.__field_unknown2=value
        else:
            self.__field_unknown2=UINT(value,**{'sizeinbytes': 3})
    def __delfield_unknown2(self): del self.__field_unknown2
    unknown2=property(__getfield_unknown2, __setfield_unknown2, __delfield_unknown2, None)
    def __getfield_pbentrynum(self):
        return self.__field_pbentrynum.getvalue()
    def __setfield_pbentrynum(self, value):
        if isinstance(value,UINT):
            self.__field_pbentrynum=value
        else:
            self.__field_pbentrynum=UINT(value,**{'sizeinbytes': 2})
    def __delfield_pbentrynum(self): del self.__field_pbentrynum
    pbentrynum=property(__getfield_pbentrynum, __setfield_pbentrynum, __delfield_pbentrynum, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('GPStime', self.__field_GPStime, None)
        yield ('unknown2', self.__field_unknown2, None)
        yield ('duration', self.__field_duration, None)
        yield ('number', self.__field_number, None)
        yield ('name', self.__field_name, None)
        yield ('numberlength', self.__field_numberlength, None)
        yield ('pbnumbertype', self.__field_pbnumbertype, None)
        yield ('unknown2', self.__field_unknown2, None)
        yield ('pbentrynum', self.__field_pbentrynum, None)
class callhistory(BaseProtogenClass):
    __fields=['numcalls', 'unknown1', 'calls']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(callhistory,self).__init__(**dict)
        if self.__class__ is callhistory:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(callhistory,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(callhistory,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_numcalls.writetobuffer(buf)
        self.__field_unknown1.writetobuffer(buf)
        try: self.__field_calls
        except:
            self.__field_calls=LIST(**{'elementclass': call})
        self.__field_calls.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_numcalls=UINT(**{'sizeinbytes': 4})
        self.__field_numcalls.readfrombuffer(buf)
        self.__field_unknown1=UINT(**{'sizeinbytes': 1})
        self.__field_unknown1.readfrombuffer(buf)
        self.__field_calls=LIST(**{'elementclass': call})
        self.__field_calls.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_numcalls(self):
        return self.__field_numcalls.getvalue()
    def __setfield_numcalls(self, value):
        if isinstance(value,UINT):
            self.__field_numcalls=value
        else:
            self.__field_numcalls=UINT(value,**{'sizeinbytes': 4})
    def __delfield_numcalls(self): del self.__field_numcalls
    numcalls=property(__getfield_numcalls, __setfield_numcalls, __delfield_numcalls, None)
    def __getfield_unknown1(self):
        return self.__field_unknown1.getvalue()
    def __setfield_unknown1(self, value):
        if isinstance(value,UINT):
            self.__field_unknown1=value
        else:
            self.__field_unknown1=UINT(value,**{'sizeinbytes': 1})
    def __delfield_unknown1(self): del self.__field_unknown1
    unknown1=property(__getfield_unknown1, __setfield_unknown1, __delfield_unknown1, None)
    def __getfield_calls(self):
        try: self.__field_calls
        except:
            self.__field_calls=LIST(**{'elementclass': call})
        return self.__field_calls.getvalue()
    def __setfield_calls(self, value):
        if isinstance(value,LIST):
            self.__field_calls=value
        else:
            self.__field_calls=LIST(value,**{'elementclass': call})
    def __delfield_calls(self): del self.__field_calls
    calls=property(__getfield_calls, __setfield_calls, __delfield_calls, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('numcalls', self.__field_numcalls, None)
        yield ('unknown1', self.__field_unknown1, None)
        yield ('calls', self.__field_calls, None)
class msg_record(BaseProtogenClass):
    __fields=['unknown1', 'binary', 'unknown3', 'unknown4', 'unknown6', 'length', 'msg']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(msg_record,self).__init__(**dict)
        if self.__class__ is msg_record:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(msg_record,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(msg_record,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_unknown1.writetobuffer(buf)
        self.__field_binary.writetobuffer(buf)
        self.__field_unknown3.writetobuffer(buf)
        self.__field_unknown4.writetobuffer(buf)
        self.__field_unknown6.writetobuffer(buf)
        self.__field_length.writetobuffer(buf)
        try: self.__field_msg
        except:
            self.__field_msg=LIST(**{'elementclass': _gen_p_lgvx8100_174, 'length': 219})
        self.__field_msg.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_unknown1=UINT(**{'sizeinbytes': 1})
        self.__field_unknown1.readfrombuffer(buf)
        self.__field_binary=UINT(**{'sizeinbytes': 1})
        self.__field_binary.readfrombuffer(buf)
        self.__field_unknown3=UINT(**{'sizeinbytes': 1})
        self.__field_unknown3.readfrombuffer(buf)
        self.__field_unknown4=UINT(**{'sizeinbytes': 1})
        self.__field_unknown4.readfrombuffer(buf)
        self.__field_unknown6=UINT(**{'sizeinbytes': 1})
        self.__field_unknown6.readfrombuffer(buf)
        self.__field_length=UINT(**{'sizeinbytes': 1})
        self.__field_length.readfrombuffer(buf)
        self.__field_msg=LIST(**{'elementclass': _gen_p_lgvx8100_174, 'length': 219})
        self.__field_msg.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_unknown1(self):
        return self.__field_unknown1.getvalue()
    def __setfield_unknown1(self, value):
        if isinstance(value,UINT):
            self.__field_unknown1=value
        else:
            self.__field_unknown1=UINT(value,**{'sizeinbytes': 1})
    def __delfield_unknown1(self): del self.__field_unknown1
    unknown1=property(__getfield_unknown1, __setfield_unknown1, __delfield_unknown1, None)
    def __getfield_binary(self):
        return self.__field_binary.getvalue()
    def __setfield_binary(self, value):
        if isinstance(value,UINT):
            self.__field_binary=value
        else:
            self.__field_binary=UINT(value,**{'sizeinbytes': 1})
    def __delfield_binary(self): del self.__field_binary
    binary=property(__getfield_binary, __setfield_binary, __delfield_binary, None)
    def __getfield_unknown3(self):
        return self.__field_unknown3.getvalue()
    def __setfield_unknown3(self, value):
        if isinstance(value,UINT):
            self.__field_unknown3=value
        else:
            self.__field_unknown3=UINT(value,**{'sizeinbytes': 1})
    def __delfield_unknown3(self): del self.__field_unknown3
    unknown3=property(__getfield_unknown3, __setfield_unknown3, __delfield_unknown3, None)
    def __getfield_unknown4(self):
        return self.__field_unknown4.getvalue()
    def __setfield_unknown4(self, value):
        if isinstance(value,UINT):
            self.__field_unknown4=value
        else:
            self.__field_unknown4=UINT(value,**{'sizeinbytes': 1})
    def __delfield_unknown4(self): del self.__field_unknown4
    unknown4=property(__getfield_unknown4, __setfield_unknown4, __delfield_unknown4, None)
    def __getfield_unknown6(self):
        return self.__field_unknown6.getvalue()
    def __setfield_unknown6(self, value):
        if isinstance(value,UINT):
            self.__field_unknown6=value
        else:
            self.__field_unknown6=UINT(value,**{'sizeinbytes': 1})
    def __delfield_unknown6(self): del self.__field_unknown6
    unknown6=property(__getfield_unknown6, __setfield_unknown6, __delfield_unknown6, None)
    def __getfield_length(self):
        return self.__field_length.getvalue()
    def __setfield_length(self, value):
        if isinstance(value,UINT):
            self.__field_length=value
        else:
            self.__field_length=UINT(value,**{'sizeinbytes': 1})
    def __delfield_length(self): del self.__field_length
    length=property(__getfield_length, __setfield_length, __delfield_length, None)
    def __getfield_msg(self):
        try: self.__field_msg
        except:
            self.__field_msg=LIST(**{'elementclass': _gen_p_lgvx8100_174, 'length': 219})
        return self.__field_msg.getvalue()
    def __setfield_msg(self, value):
        if isinstance(value,LIST):
            self.__field_msg=value
        else:
            self.__field_msg=LIST(value,**{'elementclass': _gen_p_lgvx8100_174, 'length': 219})
    def __delfield_msg(self): del self.__field_msg
    msg=property(__getfield_msg, __setfield_msg, __delfield_msg, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('unknown1', self.__field_unknown1, None)
        yield ('binary', self.__field_binary, None)
        yield ('unknown3', self.__field_unknown3, None)
        yield ('unknown4', self.__field_unknown4, None)
        yield ('unknown6', self.__field_unknown6, None)
        yield ('length', self.__field_length, None)
        yield ('msg', self.__field_msg, None)
class _gen_p_lgvx8100_174(BaseProtogenClass):
    'Anonymous inner class'
    __fields=['byte']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(_gen_p_lgvx8100_174,self).__init__(**dict)
        if self.__class__ is _gen_p_lgvx8100_174:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(_gen_p_lgvx8100_174,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(_gen_p_lgvx8100_174,kwargs)
        if len(args):
            dict2={'sizeinbytes': 1}
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_byte=UINT(*args,**dict2)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_byte.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_byte=UINT(**{'sizeinbytes': 1})
        self.__field_byte.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_byte(self):
        return self.__field_byte.getvalue()
    def __setfield_byte(self, value):
        if isinstance(value,UINT):
            self.__field_byte=value
        else:
            self.__field_byte=UINT(value,**{'sizeinbytes': 1})
    def __delfield_byte(self): del self.__field_byte
    byte=property(__getfield_byte, __setfield_byte, __delfield_byte, "individual byte of message")
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('byte', self.__field_byte, "individual byte of message")
class recipient_record(BaseProtogenClass):
    __fields=['unknown1', 'number', 'status', 'timesent', 'timereceived', 'unknown2', 'unknown3']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(recipient_record,self).__init__(**dict)
        if self.__class__ is recipient_record:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(recipient_record,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(recipient_record,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_unknown1.writetobuffer(buf)
        self.__field_number.writetobuffer(buf)
        self.__field_status.writetobuffer(buf)
        self.__field_timesent.writetobuffer(buf)
        self.__field_timereceived.writetobuffer(buf)
        self.__field_unknown2.writetobuffer(buf)
        self.__field_unknown3.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_unknown1=DATA(**{'sizeinbytes': 45})
        self.__field_unknown1.readfrombuffer(buf)
        self.__field_number=USTRING(**{'sizeinbytes': 49})
        self.__field_number.readfrombuffer(buf)
        self.__field_status=UINT(**{'sizeinbytes': 1})
        self.__field_status.readfrombuffer(buf)
        self.__field_timesent=LGCALDATE(**{'sizeinbytes': 4})
        self.__field_timesent.readfrombuffer(buf)
        self.__field_timereceived=LGCALDATE(**{'sizeinbytes': 4})
        self.__field_timereceived.readfrombuffer(buf)
        self.__field_unknown2=UINT(**{'sizeinbytes': 1})
        self.__field_unknown2.readfrombuffer(buf)
        self.__field_unknown3=DATA(**{'sizeinbytes': 40})
        self.__field_unknown3.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_unknown1(self):
        return self.__field_unknown1.getvalue()
    def __setfield_unknown1(self, value):
        if isinstance(value,DATA):
            self.__field_unknown1=value
        else:
            self.__field_unknown1=DATA(value,**{'sizeinbytes': 45})
    def __delfield_unknown1(self): del self.__field_unknown1
    unknown1=property(__getfield_unknown1, __setfield_unknown1, __delfield_unknown1, None)
    def __getfield_number(self):
        return self.__field_number.getvalue()
    def __setfield_number(self, value):
        if isinstance(value,USTRING):
            self.__field_number=value
        else:
            self.__field_number=USTRING(value,**{'sizeinbytes': 49})
    def __delfield_number(self): del self.__field_number
    number=property(__getfield_number, __setfield_number, __delfield_number, None)
    def __getfield_status(self):
        return self.__field_status.getvalue()
    def __setfield_status(self, value):
        if isinstance(value,UINT):
            self.__field_status=value
        else:
            self.__field_status=UINT(value,**{'sizeinbytes': 1})
    def __delfield_status(self): del self.__field_status
    status=property(__getfield_status, __setfield_status, __delfield_status, None)
    def __getfield_timesent(self):
        return self.__field_timesent.getvalue()
    def __setfield_timesent(self, value):
        if isinstance(value,LGCALDATE):
            self.__field_timesent=value
        else:
            self.__field_timesent=LGCALDATE(value,**{'sizeinbytes': 4})
    def __delfield_timesent(self): del self.__field_timesent
    timesent=property(__getfield_timesent, __setfield_timesent, __delfield_timesent, None)
    def __getfield_timereceived(self):
        return self.__field_timereceived.getvalue()
    def __setfield_timereceived(self, value):
        if isinstance(value,LGCALDATE):
            self.__field_timereceived=value
        else:
            self.__field_timereceived=LGCALDATE(value,**{'sizeinbytes': 4})
    def __delfield_timereceived(self): del self.__field_timereceived
    timereceived=property(__getfield_timereceived, __setfield_timereceived, __delfield_timereceived, None)
    def __getfield_unknown2(self):
        return self.__field_unknown2.getvalue()
    def __setfield_unknown2(self, value):
        if isinstance(value,UINT):
            self.__field_unknown2=value
        else:
            self.__field_unknown2=UINT(value,**{'sizeinbytes': 1})
    def __delfield_unknown2(self): del self.__field_unknown2
    unknown2=property(__getfield_unknown2, __setfield_unknown2, __delfield_unknown2, None)
    def __getfield_unknown3(self):
        return self.__field_unknown3.getvalue()
    def __setfield_unknown3(self, value):
        if isinstance(value,DATA):
            self.__field_unknown3=value
        else:
            self.__field_unknown3=DATA(value,**{'sizeinbytes': 40})
    def __delfield_unknown3(self): del self.__field_unknown3
    unknown3=property(__getfield_unknown3, __setfield_unknown3, __delfield_unknown3, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('unknown1', self.__field_unknown1, None)
        yield ('number', self.__field_number, None)
        yield ('status', self.__field_status, None)
        yield ('timesent', self.__field_timesent, None)
        yield ('timereceived', self.__field_timereceived, None)
        yield ('unknown2', self.__field_unknown2, None)
        yield ('unknown3', self.__field_unknown3, None)
class sms_saved(BaseProtogenClass):
    __fields=['outboxmsg', 'GPStime', 'outbox', 'inbox']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(sms_saved,self).__init__(**dict)
        if self.__class__ is sms_saved:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(sms_saved,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(sms_saved,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_outboxmsg.writetobuffer(buf)
        self.__field_GPStime.writetobuffer(buf)
        if self.outboxmsg:
            self.__field_outbox.writetobuffer(buf)
        if not self.outboxmsg:
            self.__field_inbox.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_outboxmsg=UINT(**{'sizeinbytes': 4})
        self.__field_outboxmsg.readfrombuffer(buf)
        self.__field_GPStime=GPSDATE(**{'sizeinbytes': 4})
        self.__field_GPStime.readfrombuffer(buf)
        if self.outboxmsg:
            self.__field_outbox=sms_out()
            self.__field_outbox.readfrombuffer(buf)
        if not self.outboxmsg:
            self.__field_inbox=sms_in()
            self.__field_inbox.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_outboxmsg(self):
        return self.__field_outboxmsg.getvalue()
    def __setfield_outboxmsg(self, value):
        if isinstance(value,UINT):
            self.__field_outboxmsg=value
        else:
            self.__field_outboxmsg=UINT(value,**{'sizeinbytes': 4})
    def __delfield_outboxmsg(self): del self.__field_outboxmsg
    outboxmsg=property(__getfield_outboxmsg, __setfield_outboxmsg, __delfield_outboxmsg, None)
    def __getfield_GPStime(self):
        return self.__field_GPStime.getvalue()
    def __setfield_GPStime(self, value):
        if isinstance(value,GPSDATE):
            self.__field_GPStime=value
        else:
            self.__field_GPStime=GPSDATE(value,**{'sizeinbytes': 4})
    def __delfield_GPStime(self): del self.__field_GPStime
    GPStime=property(__getfield_GPStime, __setfield_GPStime, __delfield_GPStime, None)
    def __getfield_outbox(self):
        return self.__field_outbox.getvalue()
    def __setfield_outbox(self, value):
        if isinstance(value,sms_out):
            self.__field_outbox=value
        else:
            self.__field_outbox=sms_out(value,)
    def __delfield_outbox(self): del self.__field_outbox
    outbox=property(__getfield_outbox, __setfield_outbox, __delfield_outbox, None)
    def __getfield_inbox(self):
        return self.__field_inbox.getvalue()
    def __setfield_inbox(self, value):
        if isinstance(value,sms_in):
            self.__field_inbox=value
        else:
            self.__field_inbox=sms_in(value,)
    def __delfield_inbox(self): del self.__field_inbox
    inbox=property(__getfield_inbox, __setfield_inbox, __delfield_inbox, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('outboxmsg', self.__field_outboxmsg, None)
        yield ('GPStime', self.__field_GPStime, None)
        if self.outboxmsg:
            yield ('outbox', self.__field_outbox, None)
        if not self.outboxmsg:
            yield ('inbox', self.__field_inbox, None)
class sms_out(BaseProtogenClass):
    __fields=['index', 'unknown1', 'locked', 'timesent', 'unknown2', 'GPStime', 'subject', 'unknown4', 'num_msg_elements', 'messages', 'unknown5', 'priority', 'unknown7', 'unknown8', 'callback', 'recipients']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(sms_out,self).__init__(**dict)
        if self.__class__ is sms_out:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(sms_out,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(sms_out,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_index.writetobuffer(buf)
        self.__field_unknown1.writetobuffer(buf)
        self.__field_locked.writetobuffer(buf)
        self.__field_timesent.writetobuffer(buf)
        self.__field_unknown2.writetobuffer(buf)
        self.__field_GPStime.writetobuffer(buf)
        self.__field_subject.writetobuffer(buf)
        self.__field_unknown4.writetobuffer(buf)
        self.__field_num_msg_elements.writetobuffer(buf)
        try: self.__field_messages
        except:
            self.__field_messages=LIST(**{'elementclass': msg_record, 'length': 7})
        self.__field_messages.writetobuffer(buf)
        self.__field_unknown5.writetobuffer(buf)
        self.__field_priority.writetobuffer(buf)
        self.__field_unknown7.writetobuffer(buf)
        self.__field_unknown8.writetobuffer(buf)
        self.__field_callback.writetobuffer(buf)
        try: self.__field_recipients
        except:
            self.__field_recipients=LIST(**{'elementclass': recipient_record,'length': 10})
        self.__field_recipients.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_index=UINT(**{'sizeinbytes': 4})
        self.__field_index.readfrombuffer(buf)
        self.__field_unknown1=UINT(**{'sizeinbytes': 1})
        self.__field_unknown1.readfrombuffer(buf)
        self.__field_locked=UINT(**{'sizeinbytes': 1})
        self.__field_locked.readfrombuffer(buf)
        self.__field_timesent=LGCALDATE(**{'sizeinbytes': 4})
        self.__field_timesent.readfrombuffer(buf)
        self.__field_unknown2=UINT(**{'sizeinbytes': 2})
        self.__field_unknown2.readfrombuffer(buf)
        self.__field_GPStime=GPSDATE(**{'sizeinbytes': 4})
        self.__field_GPStime.readfrombuffer(buf)
        self.__field_subject=USTRING(**{'sizeinbytes': 21, 'encoding': PHONE_ENCODING})
        self.__field_subject.readfrombuffer(buf)
        self.__field_unknown4=UINT(**{'sizeinbytes': 1})
        self.__field_unknown4.readfrombuffer(buf)
        self.__field_num_msg_elements=UINT(**{'sizeinbytes': 1})
        self.__field_num_msg_elements.readfrombuffer(buf)
        self.__field_messages=LIST(**{'elementclass': msg_record, 'length': 7})
        self.__field_messages.readfrombuffer(buf)
        self.__field_unknown5=UINT(**{'sizeinbytes': 1})
        self.__field_unknown5.readfrombuffer(buf)
        self.__field_priority=UINT(**{'sizeinbytes': 1})
        self.__field_priority.readfrombuffer(buf)
        self.__field_unknown7=DATA(**{'sizeinbytes': 12})
        self.__field_unknown7.readfrombuffer(buf)
        self.__field_unknown8=DATA(**{'sizeinbytes': 3})
        self.__field_unknown8.readfrombuffer(buf)
        self.__field_callback=USTRING(**{'sizeinbytes': 23})
        self.__field_callback.readfrombuffer(buf)
        self.__field_recipients=LIST(**{'elementclass': recipient_record,'length': 10})
        self.__field_recipients.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_index(self):
        return self.__field_index.getvalue()
    def __setfield_index(self, value):
        if isinstance(value,UINT):
            self.__field_index=value
        else:
            self.__field_index=UINT(value,**{'sizeinbytes': 4})
    def __delfield_index(self): del self.__field_index
    index=property(__getfield_index, __setfield_index, __delfield_index, None)
    def __getfield_unknown1(self):
        return self.__field_unknown1.getvalue()
    def __setfield_unknown1(self, value):
        if isinstance(value,UINT):
            self.__field_unknown1=value
        else:
            self.__field_unknown1=UINT(value,**{'sizeinbytes': 1})
    def __delfield_unknown1(self): del self.__field_unknown1
    unknown1=property(__getfield_unknown1, __setfield_unknown1, __delfield_unknown1, None)
    def __getfield_locked(self):
        return self.__field_locked.getvalue()
    def __setfield_locked(self, value):
        if isinstance(value,UINT):
            self.__field_locked=value
        else:
            self.__field_locked=UINT(value,**{'sizeinbytes': 1})
    def __delfield_locked(self): del self.__field_locked
    locked=property(__getfield_locked, __setfield_locked, __delfield_locked, None)
    def __getfield_timesent(self):
        return self.__field_timesent.getvalue()
    def __setfield_timesent(self, value):
        if isinstance(value,LGCALDATE):
            self.__field_timesent=value
        else:
            self.__field_timesent=LGCALDATE(value,**{'sizeinbytes': 4})
    def __delfield_timesent(self): del self.__field_timesent
    timesent=property(__getfield_timesent, __setfield_timesent, __delfield_timesent, None)
    def __getfield_unknown2(self):
        return self.__field_unknown2.getvalue()
    def __setfield_unknown2(self, value):
        if isinstance(value,UINT):
            self.__field_unknown2=value
        else:
            self.__field_unknown2=UINT(value,**{'sizeinbytes': 2})
    def __delfield_unknown2(self): del self.__field_unknown2
    unknown2=property(__getfield_unknown2, __setfield_unknown2, __delfield_unknown2, None)
    def __getfield_GPStime(self):
        return self.__field_GPStime.getvalue()
    def __setfield_GPStime(self, value):
        if isinstance(value,GPSDATE):
            self.__field_GPStime=value
        else:
            self.__field_GPStime=GPSDATE(value,**{'sizeinbytes': 4})
    def __delfield_GPStime(self): del self.__field_GPStime
    GPStime=property(__getfield_GPStime, __setfield_GPStime, __delfield_GPStime, None)
    def __getfield_subject(self):
        return self.__field_subject.getvalue()
    def __setfield_subject(self, value):
        if isinstance(value,USTRING):
            self.__field_subject=value
        else:
            self.__field_subject=USTRING(value,**{'sizeinbytes': 21, 'encoding': PHONE_ENCODING})
    def __delfield_subject(self): del self.__field_subject
    subject=property(__getfield_subject, __setfield_subject, __delfield_subject, None)
    def __getfield_unknown4(self):
        return self.__field_unknown4.getvalue()
    def __setfield_unknown4(self, value):
        if isinstance(value,UINT):
            self.__field_unknown4=value
        else:
            self.__field_unknown4=UINT(value,**{'sizeinbytes': 1})
    def __delfield_unknown4(self): del self.__field_unknown4
    unknown4=property(__getfield_unknown4, __setfield_unknown4, __delfield_unknown4, None)
    def __getfield_num_msg_elements(self):
        return self.__field_num_msg_elements.getvalue()
    def __setfield_num_msg_elements(self, value):
        if isinstance(value,UINT):
            self.__field_num_msg_elements=value
        else:
            self.__field_num_msg_elements=UINT(value,**{'sizeinbytes': 1})
    def __delfield_num_msg_elements(self): del self.__field_num_msg_elements
    num_msg_elements=property(__getfield_num_msg_elements, __setfield_num_msg_elements, __delfield_num_msg_elements, None)
    def __getfield_messages(self):
        try: self.__field_messages
        except:
            self.__field_messages=LIST(**{'elementclass': msg_record, 'length': 7})
        return self.__field_messages.getvalue()
    def __setfield_messages(self, value):
        if isinstance(value,LIST):
            self.__field_messages=value
        else:
            self.__field_messages=LIST(value,**{'elementclass': msg_record, 'length': 7})
    def __delfield_messages(self): del self.__field_messages
    messages=property(__getfield_messages, __setfield_messages, __delfield_messages, None)
    def __getfield_unknown5(self):
        return self.__field_unknown5.getvalue()
    def __setfield_unknown5(self, value):
        if isinstance(value,UINT):
            self.__field_unknown5=value
        else:
            self.__field_unknown5=UINT(value,**{'sizeinbytes': 1})
    def __delfield_unknown5(self): del self.__field_unknown5
    unknown5=property(__getfield_unknown5, __setfield_unknown5, __delfield_unknown5, None)
    def __getfield_priority(self):
        return self.__field_priority.getvalue()
    def __setfield_priority(self, value):
        if isinstance(value,UINT):
            self.__field_priority=value
        else:
            self.__field_priority=UINT(value,**{'sizeinbytes': 1})
    def __delfield_priority(self): del self.__field_priority
    priority=property(__getfield_priority, __setfield_priority, __delfield_priority, None)
    def __getfield_unknown7(self):
        return self.__field_unknown7.getvalue()
    def __setfield_unknown7(self, value):
        if isinstance(value,DATA):
            self.__field_unknown7=value
        else:
            self.__field_unknown7=DATA(value,**{'sizeinbytes': 12})
    def __delfield_unknown7(self): del self.__field_unknown7
    unknown7=property(__getfield_unknown7, __setfield_unknown7, __delfield_unknown7, None)
    def __getfield_unknown8(self):
        return self.__field_unknown8.getvalue()
    def __setfield_unknown8(self, value):
        if isinstance(value,DATA):
            self.__field_unknown8=value
        else:
            self.__field_unknown8=DATA(value,**{'sizeinbytes': 3})
    def __delfield_unknown8(self): del self.__field_unknown8
    unknown8=property(__getfield_unknown8, __setfield_unknown8, __delfield_unknown8, None)
    def __getfield_callback(self):
        return self.__field_callback.getvalue()
    def __setfield_callback(self, value):
        if isinstance(value,USTRING):
            self.__field_callback=value
        else:
            self.__field_callback=USTRING(value,**{'sizeinbytes': 23})
    def __delfield_callback(self): del self.__field_callback
    callback=property(__getfield_callback, __setfield_callback, __delfield_callback, None)
    def __getfield_recipients(self):
        try: self.__field_recipients
        except:
            self.__field_recipients=LIST(**{'elementclass': recipient_record,'length': 10})
        return self.__field_recipients.getvalue()
    def __setfield_recipients(self, value):
        if isinstance(value,LIST):
            self.__field_recipients=value
        else:
            self.__field_recipients=LIST(value,**{'elementclass': recipient_record,'length': 10})
    def __delfield_recipients(self): del self.__field_recipients
    recipients=property(__getfield_recipients, __setfield_recipients, __delfield_recipients, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('index', self.__field_index, None)
        yield ('unknown1', self.__field_unknown1, None)
        yield ('locked', self.__field_locked, None)
        yield ('timesent', self.__field_timesent, None)
        yield ('unknown2', self.__field_unknown2, None)
        yield ('GPStime', self.__field_GPStime, None)
        yield ('subject', self.__field_subject, None)
        yield ('unknown4', self.__field_unknown4, None)
        yield ('num_msg_elements', self.__field_num_msg_elements, None)
        yield ('messages', self.__field_messages, None)
        yield ('unknown5', self.__field_unknown5, None)
        yield ('priority', self.__field_priority, None)
        yield ('unknown7', self.__field_unknown7, None)
        yield ('unknown8', self.__field_unknown8, None)
        yield ('callback', self.__field_callback, None)
        yield ('recipients', self.__field_recipients, None)
class SMSINBOXMSGFRAGMENT(BaseProtogenClass):
    __fields=['msg']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(SMSINBOXMSGFRAGMENT,self).__init__(**dict)
        if self.__class__ is SMSINBOXMSGFRAGMENT:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(SMSINBOXMSGFRAGMENT,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(SMSINBOXMSGFRAGMENT,kwargs)
        if len(args):
            dict2={'elementclass': _gen_p_lgvx8100_213, 'length': 181}
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_msg=LIST(*args,**dict2)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_msg
        except:
            self.__field_msg=LIST(**{'elementclass': _gen_p_lgvx8100_213, 'length': 181})
        self.__field_msg.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_msg=LIST(**{'elementclass': _gen_p_lgvx8100_213, 'length': 181})
        self.__field_msg.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_msg(self):
        try: self.__field_msg
        except:
            self.__field_msg=LIST(**{'elementclass': _gen_p_lgvx8100_213, 'length': 181})
        return self.__field_msg.getvalue()
    def __setfield_msg(self, value):
        if isinstance(value,LIST):
            self.__field_msg=value
        else:
            self.__field_msg=LIST(value,**{'elementclass': _gen_p_lgvx8100_213, 'length': 181})
    def __delfield_msg(self): del self.__field_msg
    msg=property(__getfield_msg, __setfield_msg, __delfield_msg, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('msg', self.__field_msg, None)
class _gen_p_lgvx8100_213(BaseProtogenClass):
    'Anonymous inner class'
    __fields=['byte']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(_gen_p_lgvx8100_213,self).__init__(**dict)
        if self.__class__ is _gen_p_lgvx8100_213:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(_gen_p_lgvx8100_213,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(_gen_p_lgvx8100_213,kwargs)
        if len(args):
            dict2={'sizeinbytes': 1}
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_byte=UINT(*args,**dict2)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_byte.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_byte=UINT(**{'sizeinbytes': 1})
        self.__field_byte.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_byte(self):
        return self.__field_byte.getvalue()
    def __setfield_byte(self, value):
        if isinstance(value,UINT):
            self.__field_byte=value
        else:
            self.__field_byte=UINT(value,**{'sizeinbytes': 1})
    def __delfield_byte(self): del self.__field_byte
    byte=property(__getfield_byte, __setfield_byte, __delfield_byte, "individual byte of message")
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('byte', self.__field_byte, "individual byte of message")
class sms_in(BaseProtogenClass):
    __fields=['msg_index1', 'msg_index2', 'unknown2', 'timesent', 'unknown', 'callback_length', 'callback', 'sender_length', 'sender', 'unknown3', 'lg_time', 'unknown4', 'GPStime', 'unknown5', 'read', 'locked', 'unknown8', 'priority', 'unknown11', 'subject', 'bin_header1', 'bin_header2', 'unknown6', 'multipartID', 'unknown14', 'bin_header3', 'num_msg_elements', 'msglengths', 'msgs', 'unknown12', 'senders_name', 'unknown9']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(sms_in,self).__init__(**dict)
        if self.__class__ is sms_in:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(sms_in,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(sms_in,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_msg_index1.writetobuffer(buf)
        self.__field_msg_index2.writetobuffer(buf)
        self.__field_unknown2.writetobuffer(buf)
        self.__field_timesent.writetobuffer(buf)
        self.__field_unknown.writetobuffer(buf)
        self.__field_callback_length.writetobuffer(buf)
        self.__field_callback.writetobuffer(buf)
        self.__field_sender_length.writetobuffer(buf)
        try: self.__field_sender
        except:
            self.__field_sender=LIST(**{'elementclass': _gen_p_lgvx8100_225, 'length': 38})
        self.__field_sender.writetobuffer(buf)
        self.__field_unknown3.writetobuffer(buf)
        self.__field_lg_time.writetobuffer(buf)
        self.__field_unknown4.writetobuffer(buf)
        self.__field_GPStime.writetobuffer(buf)
        self.__field_unknown5.writetobuffer(buf)
        self.__field_read.writetobuffer(buf)
        self.__field_locked.writetobuffer(buf)
        self.__field_unknown8.writetobuffer(buf)
        self.__field_priority.writetobuffer(buf)
        self.__field_unknown11.writetobuffer(buf)
        self.__field_subject.writetobuffer(buf)
        self.__field_bin_header1.writetobuffer(buf)
        self.__field_bin_header2.writetobuffer(buf)
        self.__field_unknown6.writetobuffer(buf)
        self.__field_multipartID.writetobuffer(buf)
        self.__field_unknown14.writetobuffer(buf)
        self.__field_bin_header3.writetobuffer(buf)
        self.__field_num_msg_elements.writetobuffer(buf)
        try: self.__field_msglengths
        except:
            self.__field_msglengths=LIST(**{'elementclass': _gen_p_lgvx8100_245, 'length': 20})
        self.__field_msglengths.writetobuffer(buf)
        try: self.__field_msgs
        except:
            self.__field_msgs=LIST(**{'length': 20, 'elementclass': SMSINBOXMSGFRAGMENT})
        self.__field_msgs.writetobuffer(buf)
        self.__field_unknown12.writetobuffer(buf)
        self.__field_senders_name.writetobuffer(buf)
        self.__field_unknown9.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_msg_index1=UINT(**{'sizeinbytes': 4})
        self.__field_msg_index1.readfrombuffer(buf)
        self.__field_msg_index2=UINT(**{'sizeinbytes': 4})
        self.__field_msg_index2.readfrombuffer(buf)
        self.__field_unknown2=UINT(**{'sizeinbytes': 2})
        self.__field_unknown2.readfrombuffer(buf)
        self.__field_timesent=SMSDATE(**{'sizeinbytes': 6})
        self.__field_timesent.readfrombuffer(buf)
        self.__field_unknown=UINT(**{'sizeinbytes': 3})
        self.__field_unknown.readfrombuffer(buf)
        self.__field_callback_length=UINT(**{'sizeinbytes': 1})
        self.__field_callback_length.readfrombuffer(buf)
        self.__field_callback=USTRING(**{'sizeinbytes': 38})
        self.__field_callback.readfrombuffer(buf)
        self.__field_sender_length=UINT(**{'sizeinbytes': 1})
        self.__field_sender_length.readfrombuffer(buf)
        self.__field_sender=LIST(**{'elementclass': _gen_p_lgvx8100_225, 'length': 38})
        self.__field_sender.readfrombuffer(buf)
        self.__field_unknown3=DATA(**{'sizeinbytes': 12})
        self.__field_unknown3.readfrombuffer(buf)
        self.__field_lg_time=LGCALDATE(**{'sizeinbytes': 4})
        self.__field_lg_time.readfrombuffer(buf)
        self.__field_unknown4=UINT(**{'sizeinbytes': 3})
        self.__field_unknown4.readfrombuffer(buf)
        self.__field_GPStime=GPSDATE(**{'sizeinbytes': 4})
        self.__field_GPStime.readfrombuffer(buf)
        self.__field_unknown5=UINT(**{'sizeinbytes': 4})
        self.__field_unknown5.readfrombuffer(buf)
        self.__field_read=UINT(**{'sizeinbytes': 1})
        self.__field_read.readfrombuffer(buf)
        self.__field_locked=UINT(**{'sizeinbytes': 1})
        self.__field_locked.readfrombuffer(buf)
        self.__field_unknown8=UINT(**{'sizeinbytes': 2})
        self.__field_unknown8.readfrombuffer(buf)
        self.__field_priority=UINT(**{'sizeinbytes': 1})
        self.__field_priority.readfrombuffer(buf)
        self.__field_unknown11=DATA(**{'sizeinbytes': 6})
        self.__field_unknown11.readfrombuffer(buf)
        self.__field_subject=USTRING(**{'sizeinbytes': 21, 'encoding': PHONE_ENCODING})
        self.__field_subject.readfrombuffer(buf)
        self.__field_bin_header1=UINT(**{'sizeinbytes': 1})
        self.__field_bin_header1.readfrombuffer(buf)
        self.__field_bin_header2=UINT(**{'sizeinbytes': 1})
        self.__field_bin_header2.readfrombuffer(buf)
        self.__field_unknown6=UINT(**{'sizeinbytes': 2})
        self.__field_unknown6.readfrombuffer(buf)
        self.__field_multipartID=UINT(**{'sizeinbytes': 2})
        self.__field_multipartID.readfrombuffer(buf)
        self.__field_unknown14=UINT(**{'sizeinbytes': 2})
        self.__field_unknown14.readfrombuffer(buf)
        self.__field_bin_header3=UINT(**{'sizeinbytes': 1})
        self.__field_bin_header3.readfrombuffer(buf)
        self.__field_num_msg_elements=UINT(**{'sizeinbytes': 1})
        self.__field_num_msg_elements.readfrombuffer(buf)
        self.__field_msglengths=LIST(**{'elementclass': _gen_p_lgvx8100_245, 'length': 20})
        self.__field_msglengths.readfrombuffer(buf)
        self.__field_msgs=LIST(**{'length': 20, 'elementclass': SMSINBOXMSGFRAGMENT})
        self.__field_msgs.readfrombuffer(buf)
        self.__field_unknown12=DATA(**{'sizeinbytes': 60})
        self.__field_unknown12.readfrombuffer(buf)
        self.__field_senders_name=USTRING(**{'sizeinbytes': 33, 'encoding': PHONE_ENCODING})
        self.__field_senders_name.readfrombuffer(buf)
        self.__field_unknown9=DATA(**{'sizeinbytes': 169})
        self.__field_unknown9.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_msg_index1(self):
        return self.__field_msg_index1.getvalue()
    def __setfield_msg_index1(self, value):
        if isinstance(value,UINT):
            self.__field_msg_index1=value
        else:
            self.__field_msg_index1=UINT(value,**{'sizeinbytes': 4})
    def __delfield_msg_index1(self): del self.__field_msg_index1
    msg_index1=property(__getfield_msg_index1, __setfield_msg_index1, __delfield_msg_index1, None)
    def __getfield_msg_index2(self):
        return self.__field_msg_index2.getvalue()
    def __setfield_msg_index2(self, value):
        if isinstance(value,UINT):
            self.__field_msg_index2=value
        else:
            self.__field_msg_index2=UINT(value,**{'sizeinbytes': 4})
    def __delfield_msg_index2(self): del self.__field_msg_index2
    msg_index2=property(__getfield_msg_index2, __setfield_msg_index2, __delfield_msg_index2, None)
    def __getfield_unknown2(self):
        return self.__field_unknown2.getvalue()
    def __setfield_unknown2(self, value):
        if isinstance(value,UINT):
            self.__field_unknown2=value
        else:
            self.__field_unknown2=UINT(value,**{'sizeinbytes': 2})
    def __delfield_unknown2(self): del self.__field_unknown2
    unknown2=property(__getfield_unknown2, __setfield_unknown2, __delfield_unknown2, None)
    def __getfield_timesent(self):
        return self.__field_timesent.getvalue()
    def __setfield_timesent(self, value):
        if isinstance(value,SMSDATE):
            self.__field_timesent=value
        else:
            self.__field_timesent=SMSDATE(value,**{'sizeinbytes': 6})
    def __delfield_timesent(self): del self.__field_timesent
    timesent=property(__getfield_timesent, __setfield_timesent, __delfield_timesent, None)
    def __getfield_unknown(self):
        return self.__field_unknown.getvalue()
    def __setfield_unknown(self, value):
        if isinstance(value,UINT):
            self.__field_unknown=value
        else:
            self.__field_unknown=UINT(value,**{'sizeinbytes': 3})
    def __delfield_unknown(self): del self.__field_unknown
    unknown=property(__getfield_unknown, __setfield_unknown, __delfield_unknown, None)
    def __getfield_callback_length(self):
        return self.__field_callback_length.getvalue()
    def __setfield_callback_length(self, value):
        if isinstance(value,UINT):
            self.__field_callback_length=value
        else:
            self.__field_callback_length=UINT(value,**{'sizeinbytes': 1})
    def __delfield_callback_length(self): del self.__field_callback_length
    callback_length=property(__getfield_callback_length, __setfield_callback_length, __delfield_callback_length, None)
    def __getfield_callback(self):
        return self.__field_callback.getvalue()
    def __setfield_callback(self, value):
        if isinstance(value,USTRING):
            self.__field_callback=value
        else:
            self.__field_callback=USTRING(value,**{'sizeinbytes': 38})
    def __delfield_callback(self): del self.__field_callback
    callback=property(__getfield_callback, __setfield_callback, __delfield_callback, None)
    def __getfield_sender_length(self):
        return self.__field_sender_length.getvalue()
    def __setfield_sender_length(self, value):
        if isinstance(value,UINT):
            self.__field_sender_length=value
        else:
            self.__field_sender_length=UINT(value,**{'sizeinbytes': 1})
    def __delfield_sender_length(self): del self.__field_sender_length
    sender_length=property(__getfield_sender_length, __setfield_sender_length, __delfield_sender_length, None)
    def __getfield_sender(self):
        try: self.__field_sender
        except:
            self.__field_sender=LIST(**{'elementclass': _gen_p_lgvx8100_225, 'length': 38})
        return self.__field_sender.getvalue()
    def __setfield_sender(self, value):
        if isinstance(value,LIST):
            self.__field_sender=value
        else:
            self.__field_sender=LIST(value,**{'elementclass': _gen_p_lgvx8100_225, 'length': 38})
    def __delfield_sender(self): del self.__field_sender
    sender=property(__getfield_sender, __setfield_sender, __delfield_sender, None)
    def __getfield_unknown3(self):
        return self.__field_unknown3.getvalue()
    def __setfield_unknown3(self, value):
        if isinstance(value,DATA):
            self.__field_unknown3=value
        else:
            self.__field_unknown3=DATA(value,**{'sizeinbytes': 12})
    def __delfield_unknown3(self): del self.__field_unknown3
    unknown3=property(__getfield_unknown3, __setfield_unknown3, __delfield_unknown3, None)
    def __getfield_lg_time(self):
        return self.__field_lg_time.getvalue()
    def __setfield_lg_time(self, value):
        if isinstance(value,LGCALDATE):
            self.__field_lg_time=value
        else:
            self.__field_lg_time=LGCALDATE(value,**{'sizeinbytes': 4})
    def __delfield_lg_time(self): del self.__field_lg_time
    lg_time=property(__getfield_lg_time, __setfield_lg_time, __delfield_lg_time, None)
    def __getfield_unknown4(self):
        return self.__field_unknown4.getvalue()
    def __setfield_unknown4(self, value):
        if isinstance(value,UINT):
            self.__field_unknown4=value
        else:
            self.__field_unknown4=UINT(value,**{'sizeinbytes': 3})
    def __delfield_unknown4(self): del self.__field_unknown4
    unknown4=property(__getfield_unknown4, __setfield_unknown4, __delfield_unknown4, None)
    def __getfield_GPStime(self):
        return self.__field_GPStime.getvalue()
    def __setfield_GPStime(self, value):
        if isinstance(value,GPSDATE):
            self.__field_GPStime=value
        else:
            self.__field_GPStime=GPSDATE(value,**{'sizeinbytes': 4})
    def __delfield_GPStime(self): del self.__field_GPStime
    GPStime=property(__getfield_GPStime, __setfield_GPStime, __delfield_GPStime, None)
    def __getfield_unknown5(self):
        return self.__field_unknown5.getvalue()
    def __setfield_unknown5(self, value):
        if isinstance(value,UINT):
            self.__field_unknown5=value
        else:
            self.__field_unknown5=UINT(value,**{'sizeinbytes': 4})
    def __delfield_unknown5(self): del self.__field_unknown5
    unknown5=property(__getfield_unknown5, __setfield_unknown5, __delfield_unknown5, None)
    def __getfield_read(self):
        return self.__field_read.getvalue()
    def __setfield_read(self, value):
        if isinstance(value,UINT):
            self.__field_read=value
        else:
            self.__field_read=UINT(value,**{'sizeinbytes': 1})
    def __delfield_read(self): del self.__field_read
    read=property(__getfield_read, __setfield_read, __delfield_read, None)
    def __getfield_locked(self):
        return self.__field_locked.getvalue()
    def __setfield_locked(self, value):
        if isinstance(value,UINT):
            self.__field_locked=value
        else:
            self.__field_locked=UINT(value,**{'sizeinbytes': 1})
    def __delfield_locked(self): del self.__field_locked
    locked=property(__getfield_locked, __setfield_locked, __delfield_locked, None)
    def __getfield_unknown8(self):
        return self.__field_unknown8.getvalue()
    def __setfield_unknown8(self, value):
        if isinstance(value,UINT):
            self.__field_unknown8=value
        else:
            self.__field_unknown8=UINT(value,**{'sizeinbytes': 2})
    def __delfield_unknown8(self): del self.__field_unknown8
    unknown8=property(__getfield_unknown8, __setfield_unknown8, __delfield_unknown8, None)
    def __getfield_priority(self):
        return self.__field_priority.getvalue()
    def __setfield_priority(self, value):
        if isinstance(value,UINT):
            self.__field_priority=value
        else:
            self.__field_priority=UINT(value,**{'sizeinbytes': 1})
    def __delfield_priority(self): del self.__field_priority
    priority=property(__getfield_priority, __setfield_priority, __delfield_priority, None)
    def __getfield_unknown11(self):
        return self.__field_unknown11.getvalue()
    def __setfield_unknown11(self, value):
        if isinstance(value,DATA):
            self.__field_unknown11=value
        else:
            self.__field_unknown11=DATA(value,**{'sizeinbytes': 6})
    def __delfield_unknown11(self): del self.__field_unknown11
    unknown11=property(__getfield_unknown11, __setfield_unknown11, __delfield_unknown11, None)
    def __getfield_subject(self):
        return self.__field_subject.getvalue()
    def __setfield_subject(self, value):
        if isinstance(value,USTRING):
            self.__field_subject=value
        else:
            self.__field_subject=USTRING(value,**{'sizeinbytes': 21, 'encoding': PHONE_ENCODING})
    def __delfield_subject(self): del self.__field_subject
    subject=property(__getfield_subject, __setfield_subject, __delfield_subject, None)
    def __getfield_bin_header1(self):
        return self.__field_bin_header1.getvalue()
    def __setfield_bin_header1(self, value):
        if isinstance(value,UINT):
            self.__field_bin_header1=value
        else:
            self.__field_bin_header1=UINT(value,**{'sizeinbytes': 1})
    def __delfield_bin_header1(self): del self.__field_bin_header1
    bin_header1=property(__getfield_bin_header1, __setfield_bin_header1, __delfield_bin_header1, None)
    def __getfield_bin_header2(self):
        return self.__field_bin_header2.getvalue()
    def __setfield_bin_header2(self, value):
        if isinstance(value,UINT):
            self.__field_bin_header2=value
        else:
            self.__field_bin_header2=UINT(value,**{'sizeinbytes': 1})
    def __delfield_bin_header2(self): del self.__field_bin_header2
    bin_header2=property(__getfield_bin_header2, __setfield_bin_header2, __delfield_bin_header2, None)
    def __getfield_unknown6(self):
        return self.__field_unknown6.getvalue()
    def __setfield_unknown6(self, value):
        if isinstance(value,UINT):
            self.__field_unknown6=value
        else:
            self.__field_unknown6=UINT(value,**{'sizeinbytes': 2})
    def __delfield_unknown6(self): del self.__field_unknown6
    unknown6=property(__getfield_unknown6, __setfield_unknown6, __delfield_unknown6, None)
    def __getfield_multipartID(self):
        return self.__field_multipartID.getvalue()
    def __setfield_multipartID(self, value):
        if isinstance(value,UINT):
            self.__field_multipartID=value
        else:
            self.__field_multipartID=UINT(value,**{'sizeinbytes': 2})
    def __delfield_multipartID(self): del self.__field_multipartID
    multipartID=property(__getfield_multipartID, __setfield_multipartID, __delfield_multipartID, None)
    def __getfield_unknown14(self):
        return self.__field_unknown14.getvalue()
    def __setfield_unknown14(self, value):
        if isinstance(value,UINT):
            self.__field_unknown14=value
        else:
            self.__field_unknown14=UINT(value,**{'sizeinbytes': 2})
    def __delfield_unknown14(self): del self.__field_unknown14
    unknown14=property(__getfield_unknown14, __setfield_unknown14, __delfield_unknown14, None)
    def __getfield_bin_header3(self):
        return self.__field_bin_header3.getvalue()
    def __setfield_bin_header3(self, value):
        if isinstance(value,UINT):
            self.__field_bin_header3=value
        else:
            self.__field_bin_header3=UINT(value,**{'sizeinbytes': 1})
    def __delfield_bin_header3(self): del self.__field_bin_header3
    bin_header3=property(__getfield_bin_header3, __setfield_bin_header3, __delfield_bin_header3, None)
    def __getfield_num_msg_elements(self):
        return self.__field_num_msg_elements.getvalue()
    def __setfield_num_msg_elements(self, value):
        if isinstance(value,UINT):
            self.__field_num_msg_elements=value
        else:
            self.__field_num_msg_elements=UINT(value,**{'sizeinbytes': 1})
    def __delfield_num_msg_elements(self): del self.__field_num_msg_elements
    num_msg_elements=property(__getfield_num_msg_elements, __setfield_num_msg_elements, __delfield_num_msg_elements, None)
    def __getfield_msglengths(self):
        try: self.__field_msglengths
        except:
            self.__field_msglengths=LIST(**{'elementclass': _gen_p_lgvx8100_245, 'length': 20})
        return self.__field_msglengths.getvalue()
    def __setfield_msglengths(self, value):
        if isinstance(value,LIST):
            self.__field_msglengths=value
        else:
            self.__field_msglengths=LIST(value,**{'elementclass': _gen_p_lgvx8100_245, 'length': 20})
    def __delfield_msglengths(self): del self.__field_msglengths
    msglengths=property(__getfield_msglengths, __setfield_msglengths, __delfield_msglengths, None)
    def __getfield_msgs(self):
        try: self.__field_msgs
        except:
            self.__field_msgs=LIST(**{'length': 20, 'elementclass': SMSINBOXMSGFRAGMENT})
        return self.__field_msgs.getvalue()
    def __setfield_msgs(self, value):
        if isinstance(value,LIST):
            self.__field_msgs=value
        else:
            self.__field_msgs=LIST(value,**{'length': 20, 'elementclass': SMSINBOXMSGFRAGMENT})
    def __delfield_msgs(self): del self.__field_msgs
    msgs=property(__getfield_msgs, __setfield_msgs, __delfield_msgs, None)
    def __getfield_unknown12(self):
        return self.__field_unknown12.getvalue()
    def __setfield_unknown12(self, value):
        if isinstance(value,DATA):
            self.__field_unknown12=value
        else:
            self.__field_unknown12=DATA(value,**{'sizeinbytes': 60})
    def __delfield_unknown12(self): del self.__field_unknown12
    unknown12=property(__getfield_unknown12, __setfield_unknown12, __delfield_unknown12, None)
    def __getfield_senders_name(self):
        return self.__field_senders_name.getvalue()
    def __setfield_senders_name(self, value):
        if isinstance(value,USTRING):
            self.__field_senders_name=value
        else:
            self.__field_senders_name=USTRING(value,**{'sizeinbytes': 33, 'encoding': PHONE_ENCODING})
    def __delfield_senders_name(self): del self.__field_senders_name
    senders_name=property(__getfield_senders_name, __setfield_senders_name, __delfield_senders_name, None)
    def __getfield_unknown9(self):
        return self.__field_unknown9.getvalue()
    def __setfield_unknown9(self, value):
        if isinstance(value,DATA):
            self.__field_unknown9=value
        else:
            self.__field_unknown9=DATA(value,**{'sizeinbytes': 169})
    def __delfield_unknown9(self): del self.__field_unknown9
    unknown9=property(__getfield_unknown9, __setfield_unknown9, __delfield_unknown9, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('msg_index1', self.__field_msg_index1, None)
        yield ('msg_index2', self.__field_msg_index2, None)
        yield ('unknown2', self.__field_unknown2, None)
        yield ('timesent', self.__field_timesent, None)
        yield ('unknown', self.__field_unknown, None)
        yield ('callback_length', self.__field_callback_length, None)
        yield ('callback', self.__field_callback, None)
        yield ('sender_length', self.__field_sender_length, None)
        yield ('sender', self.__field_sender, None)
        yield ('unknown3', self.__field_unknown3, None)
        yield ('lg_time', self.__field_lg_time, None)
        yield ('unknown4', self.__field_unknown4, None)
        yield ('GPStime', self.__field_GPStime, None)
        yield ('unknown5', self.__field_unknown5, None)
        yield ('read', self.__field_read, None)
        yield ('locked', self.__field_locked, None)
        yield ('unknown8', self.__field_unknown8, None)
        yield ('priority', self.__field_priority, None)
        yield ('unknown11', self.__field_unknown11, None)
        yield ('subject', self.__field_subject, None)
        yield ('bin_header1', self.__field_bin_header1, None)
        yield ('bin_header2', self.__field_bin_header2, None)
        yield ('unknown6', self.__field_unknown6, None)
        yield ('multipartID', self.__field_multipartID, None)
        yield ('unknown14', self.__field_unknown14, None)
        yield ('bin_header3', self.__field_bin_header3, None)
        yield ('num_msg_elements', self.__field_num_msg_elements, None)
        yield ('msglengths', self.__field_msglengths, None)
        yield ('msgs', self.__field_msgs, None)
        yield ('unknown12', self.__field_unknown12, None)
        yield ('senders_name', self.__field_senders_name, None)
        yield ('unknown9', self.__field_unknown9, None)
class _gen_p_lgvx8100_225(BaseProtogenClass):
    'Anonymous inner class'
    __fields=['byte']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(_gen_p_lgvx8100_225,self).__init__(**dict)
        if self.__class__ is _gen_p_lgvx8100_225:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(_gen_p_lgvx8100_225,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(_gen_p_lgvx8100_225,kwargs)
        if len(args):
            dict2={'sizeinbytes': 1}
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_byte=UINT(*args,**dict2)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_byte.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_byte=UINT(**{'sizeinbytes': 1})
        self.__field_byte.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_byte(self):
        return self.__field_byte.getvalue()
    def __setfield_byte(self, value):
        if isinstance(value,UINT):
            self.__field_byte=value
        else:
            self.__field_byte=UINT(value,**{'sizeinbytes': 1})
    def __delfield_byte(self): del self.__field_byte
    byte=property(__getfield_byte, __setfield_byte, __delfield_byte, "individual byte of senders phone number")
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('byte', self.__field_byte, "individual byte of senders phone number")
class _gen_p_lgvx8100_245(BaseProtogenClass):
    'Anonymous inner class'
    __fields=['msglength']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(_gen_p_lgvx8100_245,self).__init__(**dict)
        if self.__class__ is _gen_p_lgvx8100_245:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(_gen_p_lgvx8100_245,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(_gen_p_lgvx8100_245,kwargs)
        if len(args):
            dict2={'sizeinbytes': 1}
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_msglength=UINT(*args,**dict2)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_msglength.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_msglength=UINT(**{'sizeinbytes': 1})
        self.__field_msglength.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_msglength(self):
        return self.__field_msglength.getvalue()
    def __setfield_msglength(self, value):
        if isinstance(value,UINT):
            self.__field_msglength=value
        else:
            self.__field_msglength=UINT(value,**{'sizeinbytes': 1})
    def __delfield_msglength(self): del self.__field_msglength
    msglength=property(__getfield_msglength, __setfield_msglength, __delfield_msglength, "lengths of individual messages in septets")
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('msglength', self.__field_msglength, "lengths of individual messages in septets")
class sms_quick_text(BaseProtogenClass):
    __fields=['msgs']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(sms_quick_text,self).__init__(**dict)
        if self.__class__ is sms_quick_text:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(sms_quick_text,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(sms_quick_text,kwargs)
        if len(args):
            dict2={'elementclass': _gen_p_lgvx8100_259, 'length': SMS_CANNED_MAX_ITEMS, 'createdefault': True}
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_msgs=LIST(*args,**dict2)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_msgs
        except:
            self.__field_msgs=LIST(**{'elementclass': _gen_p_lgvx8100_259, 'length': SMS_CANNED_MAX_ITEMS, 'createdefault': True})
        self.__field_msgs.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_msgs=LIST(**{'elementclass': _gen_p_lgvx8100_259, 'length': SMS_CANNED_MAX_ITEMS, 'createdefault': True})
        self.__field_msgs.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_msgs(self):
        try: self.__field_msgs
        except:
            self.__field_msgs=LIST(**{'elementclass': _gen_p_lgvx8100_259, 'length': SMS_CANNED_MAX_ITEMS, 'createdefault': True})
        return self.__field_msgs.getvalue()
    def __setfield_msgs(self, value):
        if isinstance(value,LIST):
            self.__field_msgs=value
        else:
            self.__field_msgs=LIST(value,**{'elementclass': _gen_p_lgvx8100_259, 'length': SMS_CANNED_MAX_ITEMS, 'createdefault': True})
    def __delfield_msgs(self): del self.__field_msgs
    msgs=property(__getfield_msgs, __setfield_msgs, __delfield_msgs, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('msgs', self.__field_msgs, None)
class _gen_p_lgvx8100_259(BaseProtogenClass):
    'Anonymous inner class'
    __fields=['msg']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(_gen_p_lgvx8100_259,self).__init__(**dict)
        if self.__class__ is _gen_p_lgvx8100_259:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(_gen_p_lgvx8100_259,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(_gen_p_lgvx8100_259,kwargs)
        if len(args):
            dict2={'sizeinbytes': 101, 'encoding': PHONE_ENCODING, 'default': ""}
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_msg=USTRING(*args,**dict2)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_msg
        except:
            self.__field_msg=USTRING(**{'sizeinbytes': 101, 'encoding': PHONE_ENCODING, 'default': ""})
        self.__field_msg.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_msg=USTRING(**{'sizeinbytes': 101, 'encoding': PHONE_ENCODING, 'default': ""})
        self.__field_msg.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_msg(self):
        try: self.__field_msg
        except:
            self.__field_msg=USTRING(**{'sizeinbytes': 101, 'encoding': PHONE_ENCODING, 'default': ""})
        return self.__field_msg.getvalue()
    def __setfield_msg(self, value):
        if isinstance(value,USTRING):
            self.__field_msg=value
        else:
            self.__field_msg=USTRING(value,**{'sizeinbytes': 101, 'encoding': PHONE_ENCODING, 'default': ""})
    def __delfield_msg(self): del self.__field_msg
    msg=property(__getfield_msg, __setfield_msg, __delfield_msg, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('msg', self.__field_msg, None)
class textmemo(BaseProtogenClass):
    __fields=['text', 'memotime']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(textmemo,self).__init__(**dict)
        if self.__class__ is textmemo:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(textmemo,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(textmemo,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_text.writetobuffer(buf)
        self.__field_memotime.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_text=USTRING(**{'sizeinbytes': 151,  'encoding': PHONE_ENCODING, 'raiseonunterminatedread': False, 'raiseontruncate': False })
        self.__field_text.readfrombuffer(buf)
        self.__field_memotime=LGCALDATE(**{'sizeinbytes': 4})
        self.__field_memotime.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_text(self):
        return self.__field_text.getvalue()
    def __setfield_text(self, value):
        if isinstance(value,USTRING):
            self.__field_text=value
        else:
            self.__field_text=USTRING(value,**{'sizeinbytes': 151,  'encoding': PHONE_ENCODING, 'raiseonunterminatedread': False, 'raiseontruncate': False })
    def __delfield_text(self): del self.__field_text
    text=property(__getfield_text, __setfield_text, __delfield_text, None)
    def __getfield_memotime(self):
        return self.__field_memotime.getvalue()
    def __setfield_memotime(self, value):
        if isinstance(value,LGCALDATE):
            self.__field_memotime=value
        else:
            self.__field_memotime=LGCALDATE(value,**{'sizeinbytes': 4})
    def __delfield_memotime(self): del self.__field_memotime
    memotime=property(__getfield_memotime, __setfield_memotime, __delfield_memotime, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('text', self.__field_text, None)
        yield ('memotime', self.__field_memotime, None)
class textmemofile(BaseProtogenClass):
    __fields=['itemcount', 'items']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(textmemofile,self).__init__(**dict)
        if self.__class__ is textmemofile:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(textmemofile,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(textmemofile,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_itemcount.writetobuffer(buf)
        try: self.__field_items
        except:
            self.__field_items=LIST(**{ 'elementclass': textmemo })
        self.__field_items.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_itemcount=UINT(**{'sizeinbytes': 4})
        self.__field_itemcount.readfrombuffer(buf)
        self.__field_items=LIST(**{ 'elementclass': textmemo })
        self.__field_items.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_itemcount(self):
        return self.__field_itemcount.getvalue()
    def __setfield_itemcount(self, value):
        if isinstance(value,UINT):
            self.__field_itemcount=value
        else:
            self.__field_itemcount=UINT(value,**{'sizeinbytes': 4})
    def __delfield_itemcount(self): del self.__field_itemcount
    itemcount=property(__getfield_itemcount, __setfield_itemcount, __delfield_itemcount, None)
    def __getfield_items(self):
        try: self.__field_items
        except:
            self.__field_items=LIST(**{ 'elementclass': textmemo })
        return self.__field_items.getvalue()
    def __setfield_items(self, value):
        if isinstance(value,LIST):
            self.__field_items=value
        else:
            self.__field_items=LIST(value,**{ 'elementclass': textmemo })
    def __delfield_items(self): del self.__field_items
    items=property(__getfield_items, __setfield_items, __delfield_items, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('itemcount', self.__field_itemcount, None)
        yield ('items', self.__field_items, None)
class firmwareresponse(BaseProtogenClass):
    __fields=['command', 'date1', 'time1', 'date2', 'time2', 'firmware']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(firmwareresponse,self).__init__(**dict)
        if self.__class__ is firmwareresponse:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(firmwareresponse,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(firmwareresponse,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_command.writetobuffer(buf)
        self.__field_date1.writetobuffer(buf)
        self.__field_time1.writetobuffer(buf)
        self.__field_date2.writetobuffer(buf)
        self.__field_time2.writetobuffer(buf)
        self.__field_firmware.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_command=UINT(**{'sizeinbytes': 1})
        self.__field_command.readfrombuffer(buf)
        self.__field_date1=USTRING(**{'sizeinbytes': 11, 'terminator': None})
        self.__field_date1.readfrombuffer(buf)
        self.__field_time1=USTRING(**{'sizeinbytes': 8, 'terminator': None})
        self.__field_time1.readfrombuffer(buf)
        self.__field_date2=USTRING(**{'sizeinbytes': 11, 'terminator': None})
        self.__field_date2.readfrombuffer(buf)
        self.__field_time2=USTRING(**{'sizeinbytes': 8, 'terminator': None})
        self.__field_time2.readfrombuffer(buf)
        self.__field_firmware=USTRING(**{'sizeinbytes': 8, 'terminator': None})
        self.__field_firmware.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_command(self):
        return self.__field_command.getvalue()
    def __setfield_command(self, value):
        if isinstance(value,UINT):
            self.__field_command=value
        else:
            self.__field_command=UINT(value,**{'sizeinbytes': 1})
    def __delfield_command(self): del self.__field_command
    command=property(__getfield_command, __setfield_command, __delfield_command, None)
    def __getfield_date1(self):
        return self.__field_date1.getvalue()
    def __setfield_date1(self, value):
        if isinstance(value,USTRING):
            self.__field_date1=value
        else:
            self.__field_date1=USTRING(value,**{'sizeinbytes': 11, 'terminator': None})
    def __delfield_date1(self): del self.__field_date1
    date1=property(__getfield_date1, __setfield_date1, __delfield_date1, None)
    def __getfield_time1(self):
        return self.__field_time1.getvalue()
    def __setfield_time1(self, value):
        if isinstance(value,USTRING):
            self.__field_time1=value
        else:
            self.__field_time1=USTRING(value,**{'sizeinbytes': 8, 'terminator': None})
    def __delfield_time1(self): del self.__field_time1
    time1=property(__getfield_time1, __setfield_time1, __delfield_time1, None)
    def __getfield_date2(self):
        return self.__field_date2.getvalue()
    def __setfield_date2(self, value):
        if isinstance(value,USTRING):
            self.__field_date2=value
        else:
            self.__field_date2=USTRING(value,**{'sizeinbytes': 11, 'terminator': None})
    def __delfield_date2(self): del self.__field_date2
    date2=property(__getfield_date2, __setfield_date2, __delfield_date2, None)
    def __getfield_time2(self):
        return self.__field_time2.getvalue()
    def __setfield_time2(self, value):
        if isinstance(value,USTRING):
            self.__field_time2=value
        else:
            self.__field_time2=USTRING(value,**{'sizeinbytes': 8, 'terminator': None})
    def __delfield_time2(self): del self.__field_time2
    time2=property(__getfield_time2, __setfield_time2, __delfield_time2, None)
    def __getfield_firmware(self):
        return self.__field_firmware.getvalue()
    def __setfield_firmware(self, value):
        if isinstance(value,USTRING):
            self.__field_firmware=value
        else:
            self.__field_firmware=USTRING(value,**{'sizeinbytes': 8, 'terminator': None})
    def __delfield_firmware(self): del self.__field_firmware
    firmware=property(__getfield_firmware, __setfield_firmware, __delfield_firmware, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('command', self.__field_command, None)
        yield ('date1', self.__field_date1, None)
        yield ('time1', self.__field_time1, None)
        yield ('date2', self.__field_date2, None)
        yield ('time2', self.__field_time2, None)
        yield ('firmware', self.__field_firmware, None)
