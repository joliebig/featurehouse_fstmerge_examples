from prototypes import *
from p_samsung_packet import *
UINT=UINTlsb
BOOL=BOOLlsb
NUMPHONEBOOKENTRIES=500
NUMEMAILS=1
NUMPHONENUMBERS=5
MAXNUMBERLEN=32
NUMGROUPS=7
MAXNAMELEN=32
NUMSPEEDDIALS=100
WP_CAMERA_PATH='digital_cam/jpeg'
NUMTODOENTRIES=9
class pbentry(BaseProtogenClass):
    __fields=['slot', 'uslot', 'name', 'wallpaper', 'primary', 'numbers', 'email', 'url', 'nick', 'memo', 'group', 'ringtone', 'timestamp']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(pbentry,self).__init__(**dict)
        if self.__class__ is pbentry:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(pbentry,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(pbentry,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_slot.writetobuffer(buf)
        self.__field_uslot.writetobuffer(buf)
        self.__field_name.writetobuffer(buf)
        self.__field_wallpaper.writetobuffer(buf)
        self.__field_primary.writetobuffer(buf)
        self.__field_numbers.writetobuffer(buf)
        self.__field_email.writetobuffer(buf)
        self.__field_url.writetobuffer(buf)
        self.__field_nick.writetobuffer(buf)
        self.__field_memo.writetobuffer(buf)
        self.__field_group.writetobuffer(buf)
        self.__field_ringtone.writetobuffer(buf)
        self.__field_timestamp.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_slot=CSVINT()
        self.__field_slot.readfrombuffer(buf)
        self.__field_uslot=CSVINT()
        self.__field_uslot.readfrombuffer(buf)
        self.__field_name=CSVSTRING()
        self.__field_name.readfrombuffer(buf)
        self.__field_wallpaper=CSVINT()
        self.__field_wallpaper.readfrombuffer(buf)
        self.__field_primary=CSVINT()
        self.__field_primary.readfrombuffer(buf)
        self.__field_numbers=LIST(**{'length': NUMPHONENUMBERS, 'createdefault': True, 'elementclass': phonenumber})
        self.__field_numbers.readfrombuffer(buf)
        self.__field_email=CSVSTRING()
        self.__field_email.readfrombuffer(buf)
        self.__field_url=CSVSTRING()
        self.__field_url.readfrombuffer(buf)
        self.__field_nick=CSVSTRING()
        self.__field_nick.readfrombuffer(buf)
        self.__field_memo=CSVSTRING()
        self.__field_memo.readfrombuffer(buf)
        self.__field_group=CSVINT()
        self.__field_group.readfrombuffer(buf)
        self.__field_ringtone=CSVINT()
        self.__field_ringtone.readfrombuffer(buf)
        self.__field_timestamp=CSVTIME(**{'terminator': None })
        self.__field_timestamp.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_slot(self):
        return self.__field_slot.getvalue()
    def __setfield_slot(self, value):
        if isinstance(value,CSVINT):
            self.__field_slot=value
        else:
            self.__field_slot=CSVINT(value,)
    def __delfield_slot(self): del self.__field_slot
    slot=property(__getfield_slot, __setfield_slot, __delfield_slot, "Internal Slot")
    def __getfield_uslot(self):
        return self.__field_uslot.getvalue()
    def __setfield_uslot(self, value):
        if isinstance(value,CSVINT):
            self.__field_uslot=value
        else:
            self.__field_uslot=CSVINT(value,)
    def __delfield_uslot(self): del self.__field_uslot
    uslot=property(__getfield_uslot, __setfield_uslot, __delfield_uslot, "User Slot, Speed dial")
    def __getfield_name(self):
        return self.__field_name.getvalue()
    def __setfield_name(self, value):
        if isinstance(value,CSVSTRING):
            self.__field_name=value
        else:
            self.__field_name=CSVSTRING(value,)
    def __delfield_name(self): del self.__field_name
    name=property(__getfield_name, __setfield_name, __delfield_name, None)
    def __getfield_wallpaper(self):
        return self.__field_wallpaper.getvalue()
    def __setfield_wallpaper(self, value):
        if isinstance(value,CSVINT):
            self.__field_wallpaper=value
        else:
            self.__field_wallpaper=CSVINT(value,)
    def __delfield_wallpaper(self): del self.__field_wallpaper
    wallpaper=property(__getfield_wallpaper, __setfield_wallpaper, __delfield_wallpaper, None)
    def __getfield_primary(self):
        return self.__field_primary.getvalue()
    def __setfield_primary(self, value):
        if isinstance(value,CSVINT):
            self.__field_primary=value
        else:
            self.__field_primary=CSVINT(value,)
    def __delfield_primary(self): del self.__field_primary
    primary=property(__getfield_primary, __setfield_primary, __delfield_primary, None)
    def __getfield_numbers(self):
        return self.__field_numbers.getvalue()
    def __setfield_numbers(self, value):
        if isinstance(value,LIST):
            self.__field_numbers=value
        else:
            self.__field_numbers=LIST(value,**{'length': NUMPHONENUMBERS, 'createdefault': True, 'elementclass': phonenumber})
    def __delfield_numbers(self): del self.__field_numbers
    numbers=property(__getfield_numbers, __setfield_numbers, __delfield_numbers, None)
    def __getfield_email(self):
        return self.__field_email.getvalue()
    def __setfield_email(self, value):
        if isinstance(value,CSVSTRING):
            self.__field_email=value
        else:
            self.__field_email=CSVSTRING(value,)
    def __delfield_email(self): del self.__field_email
    email=property(__getfield_email, __setfield_email, __delfield_email, None)
    def __getfield_url(self):
        return self.__field_url.getvalue()
    def __setfield_url(self, value):
        if isinstance(value,CSVSTRING):
            self.__field_url=value
        else:
            self.__field_url=CSVSTRING(value,)
    def __delfield_url(self): del self.__field_url
    url=property(__getfield_url, __setfield_url, __delfield_url, None)
    def __getfield_nick(self):
        return self.__field_nick.getvalue()
    def __setfield_nick(self, value):
        if isinstance(value,CSVSTRING):
            self.__field_nick=value
        else:
            self.__field_nick=CSVSTRING(value,)
    def __delfield_nick(self): del self.__field_nick
    nick=property(__getfield_nick, __setfield_nick, __delfield_nick, None)
    def __getfield_memo(self):
        return self.__field_memo.getvalue()
    def __setfield_memo(self, value):
        if isinstance(value,CSVSTRING):
            self.__field_memo=value
        else:
            self.__field_memo=CSVSTRING(value,)
    def __delfield_memo(self): del self.__field_memo
    memo=property(__getfield_memo, __setfield_memo, __delfield_memo, None)
    def __getfield_group(self):
        return self.__field_group.getvalue()
    def __setfield_group(self, value):
        if isinstance(value,CSVINT):
            self.__field_group=value
        else:
            self.__field_group=CSVINT(value,)
    def __delfield_group(self): del self.__field_group
    group=property(__getfield_group, __setfield_group, __delfield_group, None)
    def __getfield_ringtone(self):
        return self.__field_ringtone.getvalue()
    def __setfield_ringtone(self, value):
        if isinstance(value,CSVINT):
            self.__field_ringtone=value
        else:
            self.__field_ringtone=CSVINT(value,)
    def __delfield_ringtone(self): del self.__field_ringtone
    ringtone=property(__getfield_ringtone, __setfield_ringtone, __delfield_ringtone, None)
    def __getfield_timestamp(self):
        return self.__field_timestamp.getvalue()
    def __setfield_timestamp(self, value):
        if isinstance(value,CSVTIME):
            self.__field_timestamp=value
        else:
            self.__field_timestamp=CSVTIME(value,**{'terminator': None })
    def __delfield_timestamp(self): del self.__field_timestamp
    timestamp=property(__getfield_timestamp, __setfield_timestamp, __delfield_timestamp, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('slot', self.__field_slot, "Internal Slot")
        yield ('uslot', self.__field_uslot, "User Slot, Speed dial")
        yield ('name', self.__field_name, None)
        yield ('wallpaper', self.__field_wallpaper, None)
        yield ('primary', self.__field_primary, None)
        yield ('numbers', self.__field_numbers, None)
        yield ('email', self.__field_email, None)
        yield ('url', self.__field_url, None)
        yield ('nick', self.__field_nick, None)
        yield ('memo', self.__field_memo, None)
        yield ('group', self.__field_group, None)
        yield ('ringtone', self.__field_ringtone, None)
        yield ('timestamp', self.__field_timestamp, None)
class phonebookslotresponse(BaseProtogenClass):
    __fields=['command', 'entry']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(phonebookslotresponse,self).__init__(**dict)
        if self.__class__ is phonebookslotresponse:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(phonebookslotresponse,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(phonebookslotresponse,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_command.writetobuffer(buf)
        self.__field_entry.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': ord(' '), 'constant': '#PBOKR:'})
        self.__field_command.readfrombuffer(buf)
        self.__field_entry=pbentry()
        self.__field_entry.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_command(self):
        return self.__field_command.getvalue()
    def __setfield_command(self, value):
        if isinstance(value,CSVSTRING):
            self.__field_command=value
        else:
            self.__field_command=CSVSTRING(value,**{'quotechar': None, 'terminator': ord(' '), 'constant': '#PBOKR:'})
    def __delfield_command(self): del self.__field_command
    command=property(__getfield_command, __setfield_command, __delfield_command, None)
    def __getfield_entry(self):
        return self.__field_entry.getvalue()
    def __setfield_entry(self, value):
        if isinstance(value,pbentry):
            self.__field_entry=value
        else:
            self.__field_entry=pbentry(value,)
    def __delfield_entry(self): del self.__field_entry
    entry=property(__getfield_entry, __setfield_entry, __delfield_entry, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('command', self.__field_command, None)
        yield ('entry', self.__field_entry, None)
class speeddial_entry(BaseProtogenClass):
    __fields=['on_flg1', 'on_flg2', 'uslot', 'which']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(speeddial_entry,self).__init__(**dict)
        if self.__class__ is speeddial_entry:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(speeddial_entry,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(speeddial_entry,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_on_flg1.writetobuffer(buf)
        self.__field_on_flg2.writetobuffer(buf)
        self.__field_uslot.writetobuffer(buf)
        self.__field_which.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_on_flg1=UINT(**{'sizeinbytes': 1})
        self.__field_on_flg1.readfrombuffer(buf)
        self.__field_on_flg2=UINT(**{'sizeinbytes': 1})
        self.__field_on_flg2.readfrombuffer(buf)
        self.__field_uslot=UINT(**{'sizeinbytes': 2})
        self.__field_uslot.readfrombuffer(buf)
        self.__field_which=UINT(**{'sizeinbytes': 1})
        self.__field_which.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_on_flg1(self):
        return self.__field_on_flg1.getvalue()
    def __setfield_on_flg1(self, value):
        if isinstance(value,UINT):
            self.__field_on_flg1=value
        else:
            self.__field_on_flg1=UINT(value,**{'sizeinbytes': 1})
    def __delfield_on_flg1(self): del self.__field_on_flg1
    on_flg1=property(__getfield_on_flg1, __setfield_on_flg1, __delfield_on_flg1, None)
    def __getfield_on_flg2(self):
        return self.__field_on_flg2.getvalue()
    def __setfield_on_flg2(self, value):
        if isinstance(value,UINT):
            self.__field_on_flg2=value
        else:
            self.__field_on_flg2=UINT(value,**{'sizeinbytes': 1})
    def __delfield_on_flg2(self): del self.__field_on_flg2
    on_flg2=property(__getfield_on_flg2, __setfield_on_flg2, __delfield_on_flg2, None)
    def __getfield_uslot(self):
        return self.__field_uslot.getvalue()
    def __setfield_uslot(self, value):
        if isinstance(value,UINT):
            self.__field_uslot=value
        else:
            self.__field_uslot=UINT(value,**{'sizeinbytes': 2})
    def __delfield_uslot(self): del self.__field_uslot
    uslot=property(__getfield_uslot, __setfield_uslot, __delfield_uslot, None)
    def __getfield_which(self):
        return self.__field_which.getvalue()
    def __setfield_which(self, value):
        if isinstance(value,UINT):
            self.__field_which=value
        else:
            self.__field_which=UINT(value,**{'sizeinbytes': 1})
    def __delfield_which(self): del self.__field_which
    which=property(__getfield_which, __setfield_which, __delfield_which, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('on_flg1', self.__field_on_flg1, None)
        yield ('on_flg2', self.__field_on_flg2, None)
        yield ('uslot', self.__field_uslot, None)
        yield ('which', self.__field_which, None)
class speeddial_file(BaseProtogenClass):
    __fields=['items']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(speeddial_file,self).__init__(**dict)
        if self.__class__ is speeddial_file:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(speeddial_file,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(speeddial_file,kwargs)
        if len(args):
            dict2={ 'elementclass': speeddial_entry }
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_items=LIST(*args,**dict2)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_items.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_items=LIST(**{ 'elementclass': speeddial_entry })
        self.__field_items.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_items(self):
        return self.__field_items.getvalue()
    def __setfield_items(self, value):
        if isinstance(value,LIST):
            self.__field_items=value
        else:
            self.__field_items=LIST(value,**{ 'elementclass': speeddial_entry })
    def __delfield_items(self): del self.__field_items
    items=property(__getfield_items, __setfield_items, __delfield_items, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('items', self.__field_items, None)
class groupnameresponse(BaseProtogenClass):
    __fields=['command', 'entry']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(groupnameresponse,self).__init__(**dict)
        if self.__class__ is groupnameresponse:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(groupnameresponse,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(groupnameresponse,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_command.writetobuffer(buf)
        self.__field_entry.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': ord(' '), 'constant': '#PBGRR:'})
        self.__field_command.readfrombuffer(buf)
        self.__field_entry=groupnameentry()
        self.__field_entry.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_command(self):
        return self.__field_command.getvalue()
    def __setfield_command(self, value):
        if isinstance(value,CSVSTRING):
            self.__field_command=value
        else:
            self.__field_command=CSVSTRING(value,**{'quotechar': None, 'terminator': ord(' '), 'constant': '#PBGRR:'})
    def __delfield_command(self): del self.__field_command
    command=property(__getfield_command, __setfield_command, __delfield_command, None)
    def __getfield_entry(self):
        return self.__field_entry.getvalue()
    def __setfield_entry(self, value):
        if isinstance(value,groupnameentry):
            self.__field_entry=value
        else:
            self.__field_entry=groupnameentry(value,)
    def __delfield_entry(self): del self.__field_entry
    entry=property(__getfield_entry, __setfield_entry, __delfield_entry, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('command', self.__field_command, None)
        yield ('entry', self.__field_entry, None)
class groupnameentry(BaseProtogenClass):
    __fields=['gid', 'groupname', 'ringtone', 'dunno2', 'timestamp']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(groupnameentry,self).__init__(**dict)
        if self.__class__ is groupnameentry:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(groupnameentry,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(groupnameentry,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_gid.writetobuffer(buf)
        self.__field_groupname.writetobuffer(buf)
        self.__field_ringtone.writetobuffer(buf)
        self.__field_dunno2.writetobuffer(buf)
        self.__field_timestamp.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_gid=CSVINT()
        self.__field_gid.readfrombuffer(buf)
        self.__field_groupname=CSVSTRING()
        self.__field_groupname.readfrombuffer(buf)
        self.__field_ringtone=CSVINT()
        self.__field_ringtone.readfrombuffer(buf)
        self.__field_dunno2=CSVSTRING(**{'quotechar': None})
        self.__field_dunno2.readfrombuffer(buf)
        self.__field_timestamp=CSVTIME(**{'terminator': None})
        self.__field_timestamp.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_gid(self):
        return self.__field_gid.getvalue()
    def __setfield_gid(self, value):
        if isinstance(value,CSVINT):
            self.__field_gid=value
        else:
            self.__field_gid=CSVINT(value,)
    def __delfield_gid(self): del self.__field_gid
    gid=property(__getfield_gid, __setfield_gid, __delfield_gid, None)
    def __getfield_groupname(self):
        return self.__field_groupname.getvalue()
    def __setfield_groupname(self, value):
        if isinstance(value,CSVSTRING):
            self.__field_groupname=value
        else:
            self.__field_groupname=CSVSTRING(value,)
    def __delfield_groupname(self): del self.__field_groupname
    groupname=property(__getfield_groupname, __setfield_groupname, __delfield_groupname, None)
    def __getfield_ringtone(self):
        return self.__field_ringtone.getvalue()
    def __setfield_ringtone(self, value):
        if isinstance(value,CSVINT):
            self.__field_ringtone=value
        else:
            self.__field_ringtone=CSVINT(value,)
    def __delfield_ringtone(self): del self.__field_ringtone
    ringtone=property(__getfield_ringtone, __setfield_ringtone, __delfield_ringtone, "Ringtone assignment?")
    def __getfield_dunno2(self):
        return self.__field_dunno2.getvalue()
    def __setfield_dunno2(self, value):
        if isinstance(value,CSVSTRING):
            self.__field_dunno2=value
        else:
            self.__field_dunno2=CSVSTRING(value,**{'quotechar': None})
    def __delfield_dunno2(self): del self.__field_dunno2
    dunno2=property(__getfield_dunno2, __setfield_dunno2, __delfield_dunno2, "A single character C or S")
    def __getfield_timestamp(self):
        return self.__field_timestamp.getvalue()
    def __setfield_timestamp(self, value):
        if isinstance(value,CSVTIME):
            self.__field_timestamp=value
        else:
            self.__field_timestamp=CSVTIME(value,**{'terminator': None})
    def __delfield_timestamp(self): del self.__field_timestamp
    timestamp=property(__getfield_timestamp, __setfield_timestamp, __delfield_timestamp, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('gid', self.__field_gid, None)
        yield ('groupname', self.__field_groupname, None)
        yield ('ringtone', self.__field_ringtone, "Ringtone assignment?")
        yield ('dunno2', self.__field_dunno2, "A single character C or S")
        yield ('timestamp', self.__field_timestamp, None)
class groupnamesetrequest(BaseProtogenClass):
    __fields=['command', 'gid', 'groupname', 'ringtone', 'timestamp']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(groupnamesetrequest,self).__init__(**dict)
        if self.__class__ is groupnamesetrequest:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(groupnamesetrequest,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(groupnamesetrequest,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_command
        except:
            self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None,                 'default': '#PBGRW='})
        self.__field_command.writetobuffer(buf)
        self.__field_gid.writetobuffer(buf)
        self.__field_groupname.writetobuffer(buf)
        try: self.__field_ringtone
        except:
            self.__field_ringtone=CSVINT(**{ 'default': 0 })
        self.__field_ringtone.writetobuffer(buf)
        try: self.__field_timestamp
        except:
            self.__field_timestamp=CSVTIME(**{'terminator': None,               'default': (1980,1,1,12,0,0) })
        self.__field_timestamp.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None,                 'default': '#PBGRW='})
        self.__field_command.readfrombuffer(buf)
        self.__field_gid=CSVINT()
        self.__field_gid.readfrombuffer(buf)
        self.__field_groupname=CSVSTRING()
        self.__field_groupname.readfrombuffer(buf)
        self.__field_ringtone=CSVINT(**{ 'default': 0 })
        self.__field_ringtone.readfrombuffer(buf)
        self.__field_timestamp=CSVTIME(**{'terminator': None,               'default': (1980,1,1,12,0,0) })
        self.__field_timestamp.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_command(self):
        try: self.__field_command
        except:
            self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None,                 'default': '#PBGRW='})
        return self.__field_command.getvalue()
    def __setfield_command(self, value):
        if isinstance(value,CSVSTRING):
            self.__field_command=value
        else:
            self.__field_command=CSVSTRING(value,**{'quotechar': None, 'terminator': None,                 'default': '#PBGRW='})
    def __delfield_command(self): del self.__field_command
    command=property(__getfield_command, __setfield_command, __delfield_command, None)
    def __getfield_gid(self):
        return self.__field_gid.getvalue()
    def __setfield_gid(self, value):
        if isinstance(value,CSVINT):
            self.__field_gid=value
        else:
            self.__field_gid=CSVINT(value,)
    def __delfield_gid(self): del self.__field_gid
    gid=property(__getfield_gid, __setfield_gid, __delfield_gid, "Group #")
    def __getfield_groupname(self):
        return self.__field_groupname.getvalue()
    def __setfield_groupname(self, value):
        if isinstance(value,CSVSTRING):
            self.__field_groupname=value
        else:
            self.__field_groupname=CSVSTRING(value,)
    def __delfield_groupname(self): del self.__field_groupname
    groupname=property(__getfield_groupname, __setfield_groupname, __delfield_groupname, None)
    def __getfield_ringtone(self):
        try: self.__field_ringtone
        except:
            self.__field_ringtone=CSVINT(**{ 'default': 0 })
        return self.__field_ringtone.getvalue()
    def __setfield_ringtone(self, value):
        if isinstance(value,CSVINT):
            self.__field_ringtone=value
        else:
            self.__field_ringtone=CSVINT(value,**{ 'default': 0 })
    def __delfield_ringtone(self): del self.__field_ringtone
    ringtone=property(__getfield_ringtone, __setfield_ringtone, __delfield_ringtone, None)
    def __getfield_timestamp(self):
        try: self.__field_timestamp
        except:
            self.__field_timestamp=CSVTIME(**{'terminator': None,               'default': (1980,1,1,12,0,0) })
        return self.__field_timestamp.getvalue()
    def __setfield_timestamp(self, value):
        if isinstance(value,CSVTIME):
            self.__field_timestamp=value
        else:
            self.__field_timestamp=CSVTIME(value,**{'terminator': None,               'default': (1980,1,1,12,0,0) })
    def __delfield_timestamp(self): del self.__field_timestamp
    timestamp=property(__getfield_timestamp, __setfield_timestamp, __delfield_timestamp, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('command', self.__field_command, None)
        yield ('gid', self.__field_gid, "Group #")
        yield ('groupname', self.__field_groupname, None)
        yield ('ringtone', self.__field_ringtone, None)
        yield ('timestamp', self.__field_timestamp, None)
