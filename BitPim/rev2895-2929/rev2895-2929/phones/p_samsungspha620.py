"""Proposed descriptions of data usign AT commands"""

from prototypes import *

from p_samsung_packet import *

UINT=UINTlsb

BOOL=BOOLlsb

NUMPHONEBOOKENTRIES=300

NUMEMAILS=3

NUMPHONENUMBERS=6

MAXNUMBERLEN=32

NUMTODOENTRIES=9

NUMSMSENTRIES=94

NUMGROUPS=4

AMSREGISTRY="ams/AmsRegistry"

ENDTRANSACTION="ams/EndTransaction"

RINGERPREFIX="ams/Ringers/cnts"

WALLPAPERPREFIX="ams/Screen Savers/cnts"

class  pbentry (BaseProtogenClass) :
	__fields=['slot', 'uslot', 'group', 'ringtone', 'name', 'speeddial', 'dunno1', 'numbers', 'dunno3', 'dunno4', 'email', 'url', 'birthday', 'wallpaper', 'timestamp']
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

	def writetobuffer(self,buf):

        'Writes this packet to the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_slot.writetobuffer(buf)

        self.__field_uslot.writetobuffer(buf)

        self.__field_group.writetobuffer(buf)

        try: self.__field_ringtone

        except:

            self.__field_ringtone=CSVINT(**{'default': 20})

        self.__field_ringtone.writetobuffer(buf)

        self.__field_name.writetobuffer(buf)

        self.__field_speeddial.writetobuffer(buf)

        try: self.__field_dunno1

        except:

            self.__field_dunno1=CSVINT(**{'default': 0})

        self.__field_dunno1.writetobuffer(buf)

        try: self.__field_numbers

        except:

            self.__field_numbers=LIST(**{'length': NUMPHONENUMBERS, 'createdefault': True, 'elementclass': phonenumber})

        self.__field_numbers.writetobuffer(buf)

        try: self.__field_dunno3

        except:

            self.__field_dunno3=CSVSTRING(**{'quotechar': None, 'default': ""})

        self.__field_dunno3.writetobuffer(buf)

        try: self.__field_dunno4

        except:

            self.__field_dunno4=CSVSTRING(**{'quotechar': None, 'default': ""})

        self.__field_dunno4.writetobuffer(buf)

        self.__field_email.writetobuffer(buf)

        self.__field_url.writetobuffer(buf)

        try: self.__field_birthday

        except:

            self.__field_birthday=CSVDATE(**{'default': ""})

        self.__field_birthday.writetobuffer(buf)

        try: self.__field_wallpaper

        except:

            self.__field_wallpaper=CSVINT(**{'default': 20})

        self.__field_wallpaper.writetobuffer(buf)

        try: self.__field_timestamp

        except:

            self.__field_timestamp=CSVTIME(**{'terminator': None, 'default': (1980,1,1,12,0,0)})

        self.__field_timestamp.writetobuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def readfrombuffer(self,buf):

        'Reads this packet from the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_slot=CSVINT()

        self.__field_slot.readfrombuffer(buf)

        self.__field_uslot=CSVINT()

        self.__field_uslot.readfrombuffer(buf)

        self.__field_group=CSVINT()

        self.__field_group.readfrombuffer(buf)

        self.__field_ringtone=CSVINT(**{'default': 20})

        self.__field_ringtone.readfrombuffer(buf)

        self.__field_name=CSVSTRING()

        self.__field_name.readfrombuffer(buf)

        self.__field_speeddial=CSVINT()

        self.__field_speeddial.readfrombuffer(buf)

        self.__field_dunno1=CSVINT(**{'default': 0})

        self.__field_dunno1.readfrombuffer(buf)

        self.__field_numbers=LIST(**{'length': NUMPHONENUMBERS, 'createdefault': True, 'elementclass': phonenumber})

        self.__field_numbers.readfrombuffer(buf)

        self.__field_dunno3=CSVSTRING(**{'quotechar': None, 'default': ""})

        self.__field_dunno3.readfrombuffer(buf)

        self.__field_dunno4=CSVSTRING(**{'quotechar': None, 'default': ""})

        self.__field_dunno4.readfrombuffer(buf)

        self.__field_email=CSVSTRING()

        self.__field_email.readfrombuffer(buf)

        self.__field_url=CSVSTRING()

        self.__field_url.readfrombuffer(buf)

        self.__field_birthday=CSVDATE(**{'default': ""})

        self.__field_birthday.readfrombuffer(buf)

        self.__field_wallpaper=CSVINT(**{'default': 20})

        self.__field_wallpaper.readfrombuffer(buf)

        self.__field_timestamp=CSVTIME(**{'terminator': None, 'default': (1980,1,1,12,0,0)})

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

        try: self.__field_ringtone

        except:

            self.__field_ringtone=CSVINT(**{'default': 20})

        return self.__field_ringtone.getvalue()

	def __setfield_ringtone(self, value):

        if isinstance(value,CSVINT):

            self.__field_ringtone=value

        else:

            self.__field_ringtone=CSVINT(value,**{'default': 20})

	def __delfield_ringtone(self): del self.__field_ringtone

	    ringtone=property(__getfield_ringtone, __setfield_ringtone, __delfield_ringtone, None)
	    def __getfield_name(self):

        return self.__field_name.getvalue()

	def __setfield_name(self, value):

        if isinstance(value,CSVSTRING):

            self.__field_name=value

        else:

            self.__field_name=CSVSTRING(value,)

	def __delfield_name(self): del self.__field_name

	    name=property(__getfield_name, __setfield_name, __delfield_name, None)
	    def __getfield_speeddial(self):

        return self.__field_speeddial.getvalue()

	def __setfield_speeddial(self, value):

        if isinstance(value,CSVINT):

            self.__field_speeddial=value

        else:

            self.__field_speeddial=CSVINT(value,)

	def __delfield_speeddial(self): del self.__field_speeddial

	    speeddial=property(__getfield_speeddial, __setfield_speeddial, __delfield_speeddial, "Which phone number assigned to speed dial uslot")
	    def __getfield_dunno1(self):

        try: self.__field_dunno1

        except:

            self.__field_dunno1=CSVINT(**{'default': 0})

        return self.__field_dunno1.getvalue()

	def __setfield_dunno1(self, value):

        if isinstance(value,CSVINT):

            self.__field_dunno1=value

        else:

            self.__field_dunno1=CSVINT(value,**{'default': 0})

	def __delfield_dunno1(self): del self.__field_dunno1

	    dunno1=property(__getfield_dunno1, __setfield_dunno1, __delfield_dunno1, None)
	    def __getfield_numbers(self):

        try: self.__field_numbers

        except:

            self.__field_numbers=LIST(**{'length': NUMPHONENUMBERS, 'createdefault': True, 'elementclass': phonenumber})

        return self.__field_numbers.getvalue()

	def __setfield_numbers(self, value):

        if isinstance(value,LIST):

            self.__field_numbers=value

        else:

            self.__field_numbers=LIST(value,**{'length': NUMPHONENUMBERS, 'createdefault': True, 'elementclass': phonenumber})

	def __delfield_numbers(self): del self.__field_numbers

	    numbers=property(__getfield_numbers, __setfield_numbers, __delfield_numbers, None)
	    def __getfield_dunno3(self):

        try: self.__field_dunno3

        except:

            self.__field_dunno3=CSVSTRING(**{'quotechar': None, 'default': ""})

        return self.__field_dunno3.getvalue()

	def __setfield_dunno3(self, value):

        if isinstance(value,CSVSTRING):

            self.__field_dunno3=value

        else:

            self.__field_dunno3=CSVSTRING(value,**{'quotechar': None, 'default': ""})

	def __delfield_dunno3(self): del self.__field_dunno3

	    dunno3=property(__getfield_dunno3, __setfield_dunno3, __delfield_dunno3, None)
	    def __getfield_dunno4(self):

        try: self.__field_dunno4

        except:

            self.__field_dunno4=CSVSTRING(**{'quotechar': None, 'default': ""})

        return self.__field_dunno4.getvalue()

	def __setfield_dunno4(self, value):

        if isinstance(value,CSVSTRING):

            self.__field_dunno4=value

        else:

            self.__field_dunno4=CSVSTRING(value,**{'quotechar': None, 'default': ""})

	def __delfield_dunno4(self): del self.__field_dunno4

	    dunno4=property(__getfield_dunno4, __setfield_dunno4, __delfield_dunno4, None)
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
	    def __getfield_birthday(self):

        try: self.__field_birthday

        except:

            self.__field_birthday=CSVDATE(**{'default': ""})

        return self.__field_birthday.getvalue()

	def __setfield_birthday(self, value):

        if isinstance(value,CSVDATE):

            self.__field_birthday=value

        else:

            self.__field_birthday=CSVDATE(value,**{'default': ""})

	def __delfield_birthday(self): del self.__field_birthday

	    birthday=property(__getfield_birthday, __setfield_birthday, __delfield_birthday, None)
	    def __getfield_wallpaper(self):

        try: self.__field_wallpaper

        except:

            self.__field_wallpaper=CSVINT(**{'default': 20})

        return self.__field_wallpaper.getvalue()

	def __setfield_wallpaper(self, value):

        if isinstance(value,CSVINT):

            self.__field_wallpaper=value

        else:

            self.__field_wallpaper=CSVINT(value,**{'default': 20})

	def __delfield_wallpaper(self): del self.__field_wallpaper

	    wallpaper=property(__getfield_wallpaper, __setfield_wallpaper, __delfield_wallpaper, None)
	    def __getfield_timestamp(self):

        try: self.__field_timestamp

        except:

            self.__field_timestamp=CSVTIME(**{'terminator': None, 'default': (1980,1,1,12,0,0)})

        return self.__field_timestamp.getvalue()

	def __setfield_timestamp(self, value):

        if isinstance(value,CSVTIME):

            self.__field_timestamp=value

        else:

            self.__field_timestamp=CSVTIME(value,**{'terminator': None, 'default': (1980,1,1,12,0,0)})

	def __delfield_timestamp(self): del self.__field_timestamp

	    timestamp=property(__getfield_timestamp, __setfield_timestamp, __delfield_timestamp, "Use terminator None for last item")
	    def iscontainer(self):

        return True

	def containerelements(self):

        yield ('slot', self.__field_slot, "Internal Slot")

        yield ('uslot', self.__field_uslot, "User Slot, Speed dial")

        yield ('group', self.__field_group, None)

        yield ('ringtone', self.__field_ringtone, None)

        yield ('name', self.__field_name, None)

        yield ('speeddial', self.__field_speeddial, "Which phone number assigned to speed dial uslot")

        yield ('dunno1', self.__field_dunno1, None)

        yield ('numbers', self.__field_numbers, None)

        yield ('dunno3', self.__field_dunno3, None)

        yield ('dunno4', self.__field_dunno4, None)

        yield ('email', self.__field_email, None)

        yield ('url', self.__field_url, None)

        yield ('birthday', self.__field_birthday, None)

        yield ('wallpaper', self.__field_wallpaper, None)

        yield ('timestamp', self.__field_timestamp, "Use terminator None for last item")


class  phonebookslotresponse (BaseProtogenClass) :
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

	def writetobuffer(self,buf):

        'Writes this packet to the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_command.writetobuffer(buf)

        self.__field_entry.writetobuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def readfrombuffer(self,buf):

        'Reads this packet from the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

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


class  phonebookslotupdaterequest (BaseProtogenClass) :
	__fields=['command', 'entry']
	    def __init__(self, *args, **kwargs):

        dict={}

        dict.update(kwargs)

        super(phonebookslotupdaterequest,self).__init__(**dict)

        if self.__class__ is phonebookslotupdaterequest:

            self._update(args,dict)

	def getfields(self):

        return self.__fields

	def _update(self, args, kwargs):

        super(phonebookslotupdaterequest,self)._update(args,kwargs)

        keys=kwargs.keys()

        for key in keys:

            if key in self.__fields:

                setattr(self, key, kwargs[key])

                del kwargs[key]

        if __debug__:

            self._complainaboutunusedargs(phonebookslotupdaterequest,kwargs)

        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)

	def writetobuffer(self,buf):

        'Writes this packet to the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        try: self.__field_command

        except:

            self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PBOKW=0,'})

        self.__field_command.writetobuffer(buf)

        self.__field_entry.writetobuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def readfrombuffer(self,buf):

        'Reads this packet from the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PBOKW=0,'})

        self.__field_command.readfrombuffer(buf)

        self.__field_entry=pbentry()

        self.__field_entry.readfrombuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def __getfield_command(self):

        try: self.__field_command

        except:

            self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PBOKW=0,'})

        return self.__field_command.getvalue()

	def __setfield_command(self, value):

        if isinstance(value,CSVSTRING):

            self.__field_command=value

        else:

            self.__field_command=CSVSTRING(value,**{'quotechar': None, 'terminator': None, 'default': '#PBOKW=0,'})

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


class  groupnameresponse (BaseProtogenClass) :
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

	def writetobuffer(self,buf):

        'Writes this packet to the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_command.writetobuffer(buf)

        self.__field_entry.writetobuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def readfrombuffer(self,buf):

        'Reads this packet from the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

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


class  groupnameentry (BaseProtogenClass) :
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

	def writetobuffer(self,buf):

        'Writes this packet to the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_gid.writetobuffer(buf)

        self.__field_groupname.writetobuffer(buf)

        self.__field_ringtone.writetobuffer(buf)

        self.__field_dunno2.writetobuffer(buf)

        self.__field_timestamp.writetobuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def readfrombuffer(self,buf):

        'Reads this packet from the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

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


class  unparsedresponse (BaseProtogenClass) :
	__fields=['pad']
	    def __init__(self, *args, **kwargs):

        dict={}

        dict.update(kwargs)

        super(unparsedresponse,self).__init__(**dict)

        if self.__class__ is unparsedresponse:

            self._update(args,dict)

	def getfields(self):

        return self.__fields

	def _update(self, args, kwargs):

        super(unparsedresponse,self)._update(args,kwargs)

        keys=kwargs.keys()

        for key in keys:

            if key in self.__fields:

                setattr(self, key, kwargs[key])

                del kwargs[key]

        if __debug__:

            self._complainaboutunusedargs(unparsedresponse,kwargs)

        if len(args):

            dict2={}

            dict2.update(kwargs)

            kwargs=dict2

            self.__field_pad=UNKNOWN(*args,**dict2)

	def writetobuffer(self,buf):

        'Writes this packet to the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_pad.writetobuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def readfrombuffer(self,buf):

        'Reads this packet from the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_pad=UNKNOWN()

        self.__field_pad.readfrombuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def __getfield_pad(self):

        return self.__field_pad.getvalue()

	def __setfield_pad(self, value):

        if isinstance(value,UNKNOWN):

            self.__field_pad=value

        else:

            self.__field_pad=UNKNOWN(value,)

	def __delfield_pad(self): del self.__field_pad

	    pad=property(__getfield_pad, __setfield_pad, __delfield_pad, None)
	    def iscontainer(self):

        return True

	def containerelements(self):

        yield ('pad', self.__field_pad, None)


class  filepbentry (BaseProtogenClass) :
	__fields=['dunno1', 'dunno2', 'dunno3', 'dunno4', 'dunno5', 'dunno6', 'dunno7', 'dunno8', 'dunno9', 'slot', 'dunno10', 'dunno11', 'dunno12', 'dunno13', 'dunno14', 'dunno15', 'dunno16', 'dunno17', 'dunno18', 'dunno19', 'dunno20', 'dunno21', 'name_len', 'name', 'birthday', 'group_num']
	    def __init__(self, *args, **kwargs):

        dict={}

        dict.update(kwargs)

        super(filepbentry,self).__init__(**dict)

        if self.__class__ is filepbentry:

            self._update(args,dict)

	def getfields(self):

        return self.__fields

	def _update(self, args, kwargs):

        super(filepbentry,self)._update(args,kwargs)

        keys=kwargs.keys()

        for key in keys:

            if key in self.__fields:

                setattr(self, key, kwargs[key])

                del kwargs[key]

        if __debug__:

            self._complainaboutunusedargs(filepbentry,kwargs)

        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)

	def writetobuffer(self,buf):

        'Writes this packet to the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_dunno1.writetobuffer(buf)

        self.__field_dunno2.writetobuffer(buf)

        self.__field_dunno3.writetobuffer(buf)

        self.__field_dunno4.writetobuffer(buf)

        self.__field_dunno5.writetobuffer(buf)

        self.__field_dunno6.writetobuffer(buf)

        self.__field_dunno7.writetobuffer(buf)

        self.__field_dunno8.writetobuffer(buf)

        self.__field_dunno9.writetobuffer(buf)

        self.__field_slot.writetobuffer(buf)

        self.__field_dunno10.writetobuffer(buf)

        self.__field_dunno11.writetobuffer(buf)

        self.__field_dunno12.writetobuffer(buf)

        self.__field_dunno13.writetobuffer(buf)

        self.__field_dunno14.writetobuffer(buf)

        self.__field_dunno15.writetobuffer(buf)

        self.__field_dunno16.writetobuffer(buf)

        self.__field_dunno17.writetobuffer(buf)

        self.__field_dunno18.writetobuffer(buf)

        self.__field_dunno19.writetobuffer(buf)

        self.__field_dunno20.writetobuffer(buf)

        self.__field_dunno21.writetobuffer(buf)

        self.__field_name_len.writetobuffer(buf)

        self.__field_name.writetobuffer(buf)

        self.__field_birthday.writetobuffer(buf)

        self.__field_group_num.writetobuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def readfrombuffer(self,buf):

        'Reads this packet from the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_dunno1=UINT(**{'sizeinbytes': 1})

        self.__field_dunno1.readfrombuffer(buf)

        self.__field_dunno2=UINT(**{'sizeinbytes': 1})

        self.__field_dunno2.readfrombuffer(buf)

        self.__field_dunno3=UINT(**{'sizeinbytes': 1})

        self.__field_dunno3.readfrombuffer(buf)

        self.__field_dunno4=UINT(**{'sizeinbytes': 1})

        self.__field_dunno4.readfrombuffer(buf)

        self.__field_dunno5=UINT(**{'sizeinbytes': 1})

        self.__field_dunno5.readfrombuffer(buf)

        self.__field_dunno6=UINT(**{'sizeinbytes': 1})

        self.__field_dunno6.readfrombuffer(buf)

        self.__field_dunno7=UINT(**{'sizeinbytes': 1})

        self.__field_dunno7.readfrombuffer(buf)

        self.__field_dunno8=UINT(**{'sizeinbytes': 1})

        self.__field_dunno8.readfrombuffer(buf)

        self.__field_dunno9=UINT(**{'sizeinbytes': 1})

        self.__field_dunno9.readfrombuffer(buf)

        self.__field_slot=UINT(**{'sizeinbytes': 2})

        self.__field_slot.readfrombuffer(buf)

        self.__field_dunno10=UINT(**{'sizeinbytes': 1})

        self.__field_dunno10.readfrombuffer(buf)

        self.__field_dunno11=UINT(**{'sizeinbytes': 1})

        self.__field_dunno11.readfrombuffer(buf)

        self.__field_dunno12=UINT(**{'sizeinbytes': 1})

        self.__field_dunno12.readfrombuffer(buf)

        self.__field_dunno13=UINT(**{'sizeinbytes': 1})

        self.__field_dunno13.readfrombuffer(buf)

        self.__field_dunno14=UINT(**{'sizeinbytes': 1})

        self.__field_dunno14.readfrombuffer(buf)

        self.__field_dunno15=UINT(**{'sizeinbytes': 1})

        self.__field_dunno15.readfrombuffer(buf)

        self.__field_dunno16=UINT(**{'sizeinbytes': 1})

        self.__field_dunno16.readfrombuffer(buf)

        self.__field_dunno17=UINT(**{'sizeinbytes': 1})

        self.__field_dunno17.readfrombuffer(buf)

        self.__field_dunno18=UINT(**{'sizeinbytes': 1})

        self.__field_dunno18.readfrombuffer(buf)

        self.__field_dunno19=UINT(**{'sizeinbytes': 1})

        self.__field_dunno19.readfrombuffer(buf)

        self.__field_dunno20=UINT(**{'sizeinbytes': 1})

        self.__field_dunno20.readfrombuffer(buf)

        self.__field_dunno21=UINT(**{'sizeinbytes': 1})

        self.__field_dunno21.readfrombuffer(buf)

        self.__field_name_len=UINT(**{'sizeinbytes': 1})

        self.__field_name_len.readfrombuffer(buf)

        self.__field_name=STRING(**{'sizeinbytes': 21, 'raiseonunterminatedread': False })

        self.__field_name.readfrombuffer(buf)

        self.__field_birthday=STRING(**{'sizeinbytes': 11})

        self.__field_birthday.readfrombuffer(buf)

        self.__field_group_num=UINT(**{'sizeinbytes': 1})

        self.__field_group_num.readfrombuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def __getfield_dunno1(self):

        return self.__field_dunno1.getvalue()

	def __setfield_dunno1(self, value):

        if isinstance(value,UINT):

            self.__field_dunno1=value

        else:

            self.__field_dunno1=UINT(value,**{'sizeinbytes': 1})

	def __delfield_dunno1(self): del self.__field_dunno1

	    dunno1=property(__getfield_dunno1, __setfield_dunno1, __delfield_dunno1, None)
	    def __getfield_dunno2(self):

        return self.__field_dunno2.getvalue()

	def __setfield_dunno2(self, value):

        if isinstance(value,UINT):

            self.__field_dunno2=value

        else:

            self.__field_dunno2=UINT(value,**{'sizeinbytes': 1})

	def __delfield_dunno2(self): del self.__field_dunno2

	    dunno2=property(__getfield_dunno2, __setfield_dunno2, __delfield_dunno2, None)
	    def __getfield_dunno3(self):

        return self.__field_dunno3.getvalue()

	def __setfield_dunno3(self, value):

        if isinstance(value,UINT):

            self.__field_dunno3=value

        else:

            self.__field_dunno3=UINT(value,**{'sizeinbytes': 1})

	def __delfield_dunno3(self): del self.__field_dunno3

	    dunno3=property(__getfield_dunno3, __setfield_dunno3, __delfield_dunno3, None)
	    def __getfield_dunno4(self):

        return self.__field_dunno4.getvalue()

	def __setfield_dunno4(self, value):

        if isinstance(value,UINT):

            self.__field_dunno4=value

        else:

            self.__field_dunno4=UINT(value,**{'sizeinbytes': 1})

	def __delfield_dunno4(self): del self.__field_dunno4

	    dunno4=property(__getfield_dunno4, __setfield_dunno4, __delfield_dunno4, None)
	    def __getfield_dunno5(self):

        return self.__field_dunno5.getvalue()

	def __setfield_dunno5(self, value):

        if isinstance(value,UINT):

            self.__field_dunno5=value

        else:

            self.__field_dunno5=UINT(value,**{'sizeinbytes': 1})

	def __delfield_dunno5(self): del self.__field_dunno5

	    dunno5=property(__getfield_dunno5, __setfield_dunno5, __delfield_dunno5, None)
	    def __getfield_dunno6(self):

        return self.__field_dunno6.getvalue()

	def __setfield_dunno6(self, value):

        if isinstance(value,UINT):

            self.__field_dunno6=value

        else:

            self.__field_dunno6=UINT(value,**{'sizeinbytes': 1})

	def __delfield_dunno6(self): del self.__field_dunno6

	    dunno6=property(__getfield_dunno6, __setfield_dunno6, __delfield_dunno6, None)
	    def __getfield_dunno7(self):

        return self.__field_dunno7.getvalue()

	def __setfield_dunno7(self, value):

        if isinstance(value,UINT):

            self.__field_dunno7=value

        else:

            self.__field_dunno7=UINT(value,**{'sizeinbytes': 1})

	def __delfield_dunno7(self): del self.__field_dunno7

	    dunno7=property(__getfield_dunno7, __setfield_dunno7, __delfield_dunno7, None)
	    def __getfield_dunno8(self):

        return self.__field_dunno8.getvalue()

	def __setfield_dunno8(self, value):

        if isinstance(value,UINT):

            self.__field_dunno8=value

        else:

            self.__field_dunno8=UINT(value,**{'sizeinbytes': 1})

	def __delfield_dunno8(self): del self.__field_dunno8

	    dunno8=property(__getfield_dunno8, __setfield_dunno8, __delfield_dunno8, None)
	    def __getfield_dunno9(self):

        return self.__field_dunno9.getvalue()

	def __setfield_dunno9(self, value):

        if isinstance(value,UINT):

            self.__field_dunno9=value

        else:

            self.__field_dunno9=UINT(value,**{'sizeinbytes': 1})

	def __delfield_dunno9(self): del self.__field_dunno9

	    dunno9=property(__getfield_dunno9, __setfield_dunno9, __delfield_dunno9, None)
	    def __getfield_slot(self):

        return self.__field_slot.getvalue()

	def __setfield_slot(self, value):

        if isinstance(value,UINT):

            self.__field_slot=value

        else:

            self.__field_slot=UINT(value,**{'sizeinbytes': 2})

	def __delfield_slot(self): del self.__field_slot

	    slot=property(__getfield_slot, __setfield_slot, __delfield_slot, None)
	    def __getfield_dunno10(self):

        return self.__field_dunno10.getvalue()

	def __setfield_dunno10(self, value):

        if isinstance(value,UINT):

            self.__field_dunno10=value

        else:

            self.__field_dunno10=UINT(value,**{'sizeinbytes': 1})

	def __delfield_dunno10(self): del self.__field_dunno10

	    dunno10=property(__getfield_dunno10, __setfield_dunno10, __delfield_dunno10, None)
	    def __getfield_dunno11(self):

        return self.__field_dunno11.getvalue()

	def __setfield_dunno11(self, value):

        if isinstance(value,UINT):

            self.__field_dunno11=value

        else:

            self.__field_dunno11=UINT(value,**{'sizeinbytes': 1})

	def __delfield_dunno11(self): del self.__field_dunno11

	    dunno11=property(__getfield_dunno11, __setfield_dunno11, __delfield_dunno11, None)
	    def __getfield_dunno12(self):

        return self.__field_dunno12.getvalue()

	def __setfield_dunno12(self, value):

        if isinstance(value,UINT):

            self.__field_dunno12=value

        else:

            self.__field_dunno12=UINT(value,**{'sizeinbytes': 1})

	def __delfield_dunno12(self): del self.__field_dunno12

	    dunno12=property(__getfield_dunno12, __setfield_dunno12, __delfield_dunno12, None)
	    def __getfield_dunno13(self):

        return self.__field_dunno13.getvalue()

	def __setfield_dunno13(self, value):

        if isinstance(value,UINT):

            self.__field_dunno13=value

        else:

            self.__field_dunno13=UINT(value,**{'sizeinbytes': 1})

	def __delfield_dunno13(self): del self.__field_dunno13

	    dunno13=property(__getfield_dunno13, __setfield_dunno13, __delfield_dunno13, None)
	    def __getfield_dunno14(self):

        return self.__field_dunno14.getvalue()

	def __setfield_dunno14(self, value):

        if isinstance(value,UINT):

            self.__field_dunno14=value

        else:

            self.__field_dunno14=UINT(value,**{'sizeinbytes': 1})

	def __delfield_dunno14(self): del self.__field_dunno14

	    dunno14=property(__getfield_dunno14, __setfield_dunno14, __delfield_dunno14, None)
	    def __getfield_dunno15(self):

        return self.__field_dunno15.getvalue()

	def __setfield_dunno15(self, value):

        if isinstance(value,UINT):

            self.__field_dunno15=value

        else:

            self.__field_dunno15=UINT(value,**{'sizeinbytes': 1})

	def __delfield_dunno15(self): del self.__field_dunno15

	    dunno15=property(__getfield_dunno15, __setfield_dunno15, __delfield_dunno15, None)
	    def __getfield_dunno16(self):

        return self.__field_dunno16.getvalue()

	def __setfield_dunno16(self, value):

        if isinstance(value,UINT):

            self.__field_dunno16=value

        else:

            self.__field_dunno16=UINT(value,**{'sizeinbytes': 1})

	def __delfield_dunno16(self): del self.__field_dunno16

	    dunno16=property(__getfield_dunno16, __setfield_dunno16, __delfield_dunno16, None)
	    def __getfield_dunno17(self):

        return self.__field_dunno17.getvalue()

	def __setfield_dunno17(self, value):

        if isinstance(value,UINT):

            self.__field_dunno17=value

        else:

            self.__field_dunno17=UINT(value,**{'sizeinbytes': 1})

	def __delfield_dunno17(self): del self.__field_dunno17

	    dunno17=property(__getfield_dunno17, __setfield_dunno17, __delfield_dunno17, None)
	    def __getfield_dunno18(self):

        return self.__field_dunno18.getvalue()

	def __setfield_dunno18(self, value):

        if isinstance(value,UINT):

            self.__field_dunno18=value

        else:

            self.__field_dunno18=UINT(value,**{'sizeinbytes': 1})

	def __delfield_dunno18(self): del self.__field_dunno18

	    dunno18=property(__getfield_dunno18, __setfield_dunno18, __delfield_dunno18, None)
	    def __getfield_dunno19(self):

        return self.__field_dunno19.getvalue()

	def __setfield_dunno19(self, value):

        if isinstance(value,UINT):

            self.__field_dunno19=value

        else:

            self.__field_dunno19=UINT(value,**{'sizeinbytes': 1})

	def __delfield_dunno19(self): del self.__field_dunno19

	    dunno19=property(__getfield_dunno19, __setfield_dunno19, __delfield_dunno19, None)
	    def __getfield_dunno20(self):

        return self.__field_dunno20.getvalue()

	def __setfield_dunno20(self, value):

        if isinstance(value,UINT):

            self.__field_dunno20=value

        else:

            self.__field_dunno20=UINT(value,**{'sizeinbytes': 1})

	def __delfield_dunno20(self): del self.__field_dunno20

	    dunno20=property(__getfield_dunno20, __setfield_dunno20, __delfield_dunno20, None)
	    def __getfield_dunno21(self):

        return self.__field_dunno21.getvalue()

	def __setfield_dunno21(self, value):

        if isinstance(value,UINT):

            self.__field_dunno21=value

        else:

            self.__field_dunno21=UINT(value,**{'sizeinbytes': 1})

	def __delfield_dunno21(self): del self.__field_dunno21

	    dunno21=property(__getfield_dunno21, __setfield_dunno21, __delfield_dunno21, None)
	    def __getfield_name_len(self):

        return self.__field_name_len.getvalue()

	def __setfield_name_len(self, value):

        if isinstance(value,UINT):

            self.__field_name_len=value

        else:

            self.__field_name_len=UINT(value,**{'sizeinbytes': 1})

	def __delfield_name_len(self): del self.__field_name_len

	    name_len=property(__getfield_name_len, __setfield_name_len, __delfield_name_len, None)
	    def __getfield_name(self):

        return self.__field_name.getvalue()

	def __setfield_name(self, value):

        if isinstance(value,STRING):

            self.__field_name=value

        else:

            self.__field_name=STRING(value,**{'sizeinbytes': 21, 'raiseonunterminatedread': False })

	def __delfield_name(self): del self.__field_name

	    name=property(__getfield_name, __setfield_name, __delfield_name, None)
	    def __getfield_birthday(self):

        return self.__field_birthday.getvalue()

	def __setfield_birthday(self, value):

        if isinstance(value,STRING):

            self.__field_birthday=value

        else:

            self.__field_birthday=STRING(value,**{'sizeinbytes': 11})

	def __delfield_birthday(self): del self.__field_birthday

	    birthday=property(__getfield_birthday, __setfield_birthday, __delfield_birthday, None)
	    def __getfield_group_num(self):

        return self.__field_group_num.getvalue()

	def __setfield_group_num(self, value):

        if isinstance(value,UINT):

            self.__field_group_num=value

        else:

            self.__field_group_num=UINT(value,**{'sizeinbytes': 1})

	def __delfield_group_num(self): del self.__field_group_num

	    group_num=property(__getfield_group_num, __setfield_group_num, __delfield_group_num, None)
	    def iscontainer(self):

        return True

	def containerelements(self):

        yield ('dunno1', self.__field_dunno1, None)

        yield ('dunno2', self.__field_dunno2, None)

        yield ('dunno3', self.__field_dunno3, None)

        yield ('dunno4', self.__field_dunno4, None)

        yield ('dunno5', self.__field_dunno5, None)

        yield ('dunno6', self.__field_dunno6, None)

        yield ('dunno7', self.__field_dunno7, None)

        yield ('dunno8', self.__field_dunno8, None)

        yield ('dunno9', self.__field_dunno9, None)

        yield ('slot', self.__field_slot, None)

        yield ('dunno10', self.__field_dunno10, None)

        yield ('dunno11', self.__field_dunno11, None)

        yield ('dunno12', self.__field_dunno12, None)

        yield ('dunno13', self.__field_dunno13, None)

        yield ('dunno14', self.__field_dunno14, None)

        yield ('dunno15', self.__field_dunno15, None)

        yield ('dunno16', self.__field_dunno16, None)

        yield ('dunno17', self.__field_dunno17, None)

        yield ('dunno18', self.__field_dunno18, None)

        yield ('dunno19', self.__field_dunno19, None)

        yield ('dunno20', self.__field_dunno20, None)

        yield ('dunno21', self.__field_dunno21, None)

        yield ('name_len', self.__field_name_len, None)

        yield ('name', self.__field_name, None)

        yield ('birthday', self.__field_birthday, None)

        yield ('group_num', self.__field_group_num, None)


class  pbbook (BaseProtogenClass) :
	__fields=['dummy', 'entry']
	    def __init__(self, *args, **kwargs):

        dict={}

        dict.update(kwargs)

        super(pbbook,self).__init__(**dict)

        if self.__class__ is pbbook:

            self._update(args,dict)

	def getfields(self):

        return self.__fields

	def _update(self, args, kwargs):

        super(pbbook,self)._update(args,kwargs)

        keys=kwargs.keys()

        for key in keys:

            if key in self.__fields:

                setattr(self, key, kwargs[key])

                del kwargs[key]

        if __debug__:

            self._complainaboutunusedargs(pbbook,kwargs)

        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)

	def writetobuffer(self,buf):

        'Writes this packet to the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_dummy.writetobuffer(buf)

        try: self.__field_entry

        except:

            self.__field_entry=LIST(**{ 'length': 300, 'elementclass': filepbentry })

        self.__field_entry.writetobuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def readfrombuffer(self,buf):

        'Reads this packet from the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_dummy=pbentry()

        self.__field_dummy.readfrombuffer(buf)

        self.__field_entry=LIST(**{ 'length': 300, 'elementclass': filepbentry })

        self.__field_entry.readfrombuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def __getfield_dummy(self):

        return self.__field_dummy.getvalue()

	def __setfield_dummy(self, value):

        if isinstance(value,pbentry):

            self.__field_dummy=value

        else:

            self.__field_dummy=pbentry(value,)

	def __delfield_dummy(self): del self.__field_dummy

	    dummy=property(__getfield_dummy, __setfield_dummy, __delfield_dummy, None)
	    def __getfield_entry(self):

        try: self.__field_entry

        except:

            self.__field_entry=LIST(**{ 'length': 300, 'elementclass': filepbentry })

        return self.__field_entry.getvalue()

	def __setfield_entry(self, value):

        if isinstance(value,LIST):

            self.__field_entry=value

        else:

            self.__field_entry=LIST(value,**{ 'length': 300, 'elementclass': filepbentry })

	def __delfield_entry(self): del self.__field_entry

	    entry=property(__getfield_entry, __setfield_entry, __delfield_entry, None)
	    def iscontainer(self):

        return True

	def containerelements(self):

        yield ('dummy', self.__field_dummy, None)

        yield ('entry', self.__field_entry, None)


class  image (BaseProtogenClass) :
	__fields=['inuse', 'pic_type', 'pic_id']
	    def __init__(self, *args, **kwargs):

        dict={}

        dict.update(kwargs)

        super(image,self).__init__(**dict)

        if self.__class__ is image:

            self._update(args,dict)

	def getfields(self):

        return self.__fields

	def _update(self, args, kwargs):

        super(image,self)._update(args,kwargs)

        keys=kwargs.keys()

        for key in keys:

            if key in self.__fields:

                setattr(self, key, kwargs[key])

                del kwargs[key]

        if __debug__:

            self._complainaboutunusedargs(image,kwargs)

        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)

	def writetobuffer(self,buf):

        'Writes this packet to the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_inuse.writetobuffer(buf)

        self.__field_pic_type.writetobuffer(buf)

        self.__field_pic_id.writetobuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def readfrombuffer(self,buf):

        'Reads this packet from the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_inuse=UINT(**{'sizeinbytes': 1})

        self.__field_inuse.readfrombuffer(buf)

        self.__field_pic_type=UINT(**{'sizeinbytes': 1})

        self.__field_pic_type.readfrombuffer(buf)

        self.__field_pic_id=UINT(**{'sizeinbytes': 1})

        self.__field_pic_id.readfrombuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def __getfield_inuse(self):

        return self.__field_inuse.getvalue()

	def __setfield_inuse(self, value):

        if isinstance(value,UINT):

            self.__field_inuse=value

        else:

            self.__field_inuse=UINT(value,**{'sizeinbytes': 1})

	def __delfield_inuse(self): del self.__field_inuse

	    inuse=property(__getfield_inuse, __setfield_inuse, __delfield_inuse, None)
	    def __getfield_pic_type(self):

        return self.__field_pic_type.getvalue()

	def __setfield_pic_type(self, value):

        if isinstance(value,UINT):

            self.__field_pic_type=value

        else:

            self.__field_pic_type=UINT(value,**{'sizeinbytes': 1})

	def __delfield_pic_type(self): del self.__field_pic_type

	    pic_type=property(__getfield_pic_type, __setfield_pic_type, __delfield_pic_type, "1: Man, 2: Animals, 3: Other, 4: Downloads, 5: Pic Wallet")
	    def __getfield_pic_id(self):

        return self.__field_pic_id.getvalue()

	def __setfield_pic_id(self, value):

        if isinstance(value,UINT):

            self.__field_pic_id=value

        else:

            self.__field_pic_id=UINT(value,**{'sizeinbytes': 1})

	def __delfield_pic_id(self): del self.__field_pic_id

	    pic_id=property(__getfield_pic_id, __setfield_pic_id, __delfield_pic_id, None)
	    def iscontainer(self):

        return True

	def containerelements(self):

        yield ('inuse', self.__field_inuse, None)

        yield ('pic_type', self.__field_pic_type, "1: Man, 2: Animals, 3: Other, 4: Downloads, 5: Pic Wallet")

        yield ('pic_id', self.__field_pic_id, None)


class  avatars (BaseProtogenClass) :
	__fields=['dummy', 'entry']
	    def __init__(self, *args, **kwargs):

        dict={}

        dict.update(kwargs)

        super(avatars,self).__init__(**dict)

        if self.__class__ is avatars:

            self._update(args,dict)

	def getfields(self):

        return self.__fields

	def _update(self, args, kwargs):

        super(avatars,self)._update(args,kwargs)

        keys=kwargs.keys()

        for key in keys:

            if key in self.__fields:

                setattr(self, key, kwargs[key])

                del kwargs[key]

        if __debug__:

            self._complainaboutunusedargs(avatars,kwargs)

        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)

	def writetobuffer(self,buf):

        'Writes this packet to the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_dummy.writetobuffer(buf)

        try: self.__field_entry

        except:

            self.__field_entry=LIST(**{'length': NUMPHONEBOOKENTRIES, 'elementclass': image})

        self.__field_entry.writetobuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def readfrombuffer(self,buf):

        'Reads this packet from the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_dummy=image()

        self.__field_dummy.readfrombuffer(buf)

        self.__field_entry=LIST(**{'length': NUMPHONEBOOKENTRIES, 'elementclass': image})

        self.__field_entry.readfrombuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def __getfield_dummy(self):

        return self.__field_dummy.getvalue()

	def __setfield_dummy(self, value):

        if isinstance(value,image):

            self.__field_dummy=value

        else:

            self.__field_dummy=image(value,)

	def __delfield_dummy(self): del self.__field_dummy

	    dummy=property(__getfield_dummy, __setfield_dummy, __delfield_dummy, None)
	    def __getfield_entry(self):

        try: self.__field_entry

        except:

            self.__field_entry=LIST(**{'length': NUMPHONEBOOKENTRIES, 'elementclass': image})

        return self.__field_entry.getvalue()

	def __setfield_entry(self, value):

        if isinstance(value,LIST):

            self.__field_entry=value

        else:

            self.__field_entry=LIST(value,**{'length': NUMPHONEBOOKENTRIES, 'elementclass': image})

	def __delfield_entry(self): del self.__field_entry

	    entry=property(__getfield_entry, __setfield_entry, __delfield_entry, None)
	    def iscontainer(self):

        return True

	def containerelements(self):

        yield ('dummy', self.__field_dummy, None)

        yield ('entry', self.__field_entry, None)


class  ringer (BaseProtogenClass) :
	__fields=['inuse', 'ring_type', 'ring_id']
	    def __init__(self, *args, **kwargs):

        dict={}

        dict.update(kwargs)

        super(ringer,self).__init__(**dict)

        if self.__class__ is ringer:

            self._update(args,dict)

	def getfields(self):

        return self.__fields

	def _update(self, args, kwargs):

        super(ringer,self)._update(args,kwargs)

        keys=kwargs.keys()

        for key in keys:

            if key in self.__fields:

                setattr(self, key, kwargs[key])

                del kwargs[key]

        if __debug__:

            self._complainaboutunusedargs(ringer,kwargs)

        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)

	def writetobuffer(self,buf):

        'Writes this packet to the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_inuse.writetobuffer(buf)

        self.__field_ring_type.writetobuffer(buf)

        self.__field_ring_id.writetobuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def readfrombuffer(self,buf):

        'Reads this packet from the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_inuse=UINT(**{'sizeinbytes': 1})

        self.__field_inuse.readfrombuffer(buf)

        self.__field_ring_type=UINT(**{'sizeinbytes': 1})

        self.__field_ring_type.readfrombuffer(buf)

        self.__field_ring_id=UINT(**{'sizeinbytes': 1})

        self.__field_ring_id.readfrombuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def __getfield_inuse(self):

        return self.__field_inuse.getvalue()

	def __setfield_inuse(self, value):

        if isinstance(value,UINT):

            self.__field_inuse=value

        else:

            self.__field_inuse=UINT(value,**{'sizeinbytes': 1})

	def __delfield_inuse(self): del self.__field_inuse

	    inuse=property(__getfield_inuse, __setfield_inuse, __delfield_inuse, None)
	    def __getfield_ring_type(self):

        return self.__field_ring_type.getvalue()

	def __setfield_ring_type(self, value):

        if isinstance(value,UINT):

            self.__field_ring_type=value

        else:

            self.__field_ring_type=UINT(value,**{'sizeinbytes': 1})

	def __delfield_ring_type(self): del self.__field_ring_type

	    ring_type=property(__getfield_ring_type, __setfield_ring_type, __delfield_ring_type, "0: Default: 1: Ringtones, 2: Melodies, 3: Downloads, 4: Single Tone")
	    def __getfield_ring_id(self):

        return self.__field_ring_id.getvalue()

	def __setfield_ring_id(self, value):

        if isinstance(value,UINT):

            self.__field_ring_id=value

        else:

            self.__field_ring_id=UINT(value,**{'sizeinbytes': 1})

	def __delfield_ring_id(self): del self.__field_ring_id

	    ring_id=property(__getfield_ring_id, __setfield_ring_id, __delfield_ring_id, "0x45 Tone 1, 0x4a = Tone 6, 0x51=Ringtone 1, 5b=Fur Elise")
	    def iscontainer(self):

        return True

	def containerelements(self):

        yield ('inuse', self.__field_inuse, None)

        yield ('ring_type', self.__field_ring_type, "0: Default: 1: Ringtones, 2: Melodies, 3: Downloads, 4: Single Tone")

        yield ('ring_id', self.__field_ring_id, "0x45 Tone 1, 0x4a = Tone 6, 0x51=Ringtone 1, 5b=Fur Elise")


class  amsregistry (BaseProtogenClass) :
	__fields=['items', 'info', 'info2', 'strings', 'num1', 'num2', 'nfiles', 'num4']
	    def __init__(self, *args, **kwargs):

        dict={}

        dict.update(kwargs)

        super(amsregistry,self).__init__(**dict)

        if self.__class__ is amsregistry:

            self._update(args,dict)

	def getfields(self):

        return self.__fields

	def _update(self, args, kwargs):

        super(amsregistry,self)._update(args,kwargs)

        keys=kwargs.keys()

        for key in keys:

            if key in self.__fields:

                setattr(self, key, kwargs[key])

                del kwargs[key]

        if __debug__:

            self._complainaboutunusedargs(amsregistry,kwargs)

        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)

	def writetobuffer(self,buf):

        'Writes this packet to the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_items.writetobuffer(buf)

        self.__field_info.writetobuffer(buf)

        self.__field_info2.writetobuffer(buf)

        self.__field_strings.writetobuffer(buf)

        self.__field_num1.writetobuffer(buf)

        self.__field_num2.writetobuffer(buf)

        self.__field_nfiles.writetobuffer(buf)

        self.__field_num4.writetobuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def readfrombuffer(self,buf):

        'Reads this packet from the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_items=LIST(**{'elementclass': _gen_p_samsungspha620_134, 'length': 150})

        self.__field_items.readfrombuffer(buf)

        self.__field_info=LIST(**{'elementclass': _gen_p_samsungspha620_137, 'length': 320})

        self.__field_info.readfrombuffer(buf)

        self.__field_info2=LIST(**{'elementclass': _gen_p_samsungspha620_155, 'length': 100})

        self.__field_info2.readfrombuffer(buf)

        self.__field_strings=DATA(**{'sizeinbytes': 23000})

        self.__field_strings.readfrombuffer(buf)

        self.__field_num1=UINT(**{'sizeinbytes': 2})

        self.__field_num1.readfrombuffer(buf)

        self.__field_num2=UINT(**{'sizeinbytes': 2})

        self.__field_num2.readfrombuffer(buf)

        self.__field_nfiles=UINT(**{'sizeinbytes': 2})

        self.__field_nfiles.readfrombuffer(buf)

        self.__field_num4=UINT(**{'sizeinbytes': 2})

        self.__field_num4.readfrombuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def __getfield_items(self):

        return self.__field_items.getvalue()

	def __setfield_items(self, value):

        if isinstance(value,LIST):

            self.__field_items=value

        else:

            self.__field_items=LIST(value,**{'elementclass': _gen_p_samsungspha620_134, 'length': 150})

	def __delfield_items(self): del self.__field_items

	    items=property(__getfield_items, __setfield_items, __delfield_items, None)
	    def __getfield_info(self):

        return self.__field_info.getvalue()

	def __setfield_info(self, value):

        if isinstance(value,LIST):

            self.__field_info=value

        else:

            self.__field_info=LIST(value,**{'elementclass': _gen_p_samsungspha620_137, 'length': 320})

	def __delfield_info(self): del self.__field_info

	    info=property(__getfield_info, __setfield_info, __delfield_info, None)
	    def __getfield_info2(self):

        return self.__field_info2.getvalue()

	def __setfield_info2(self, value):

        if isinstance(value,LIST):

            self.__field_info2=value

        else:

            self.__field_info2=LIST(value,**{'elementclass': _gen_p_samsungspha620_155, 'length': 100})

	def __delfield_info2(self): del self.__field_info2

	    info2=property(__getfield_info2, __setfield_info2, __delfield_info2, None)
	    def __getfield_strings(self):

        return self.__field_strings.getvalue()

	def __setfield_strings(self, value):

        if isinstance(value,DATA):

            self.__field_strings=value

        else:

            self.__field_strings=DATA(value,**{'sizeinbytes': 23000})

	def __delfield_strings(self): del self.__field_strings

	    strings=property(__getfield_strings, __setfield_strings, __delfield_strings, None)
	    def __getfield_num1(self):

        return self.__field_num1.getvalue()

	def __setfield_num1(self, value):

        if isinstance(value,UINT):

            self.__field_num1=value

        else:

            self.__field_num1=UINT(value,**{'sizeinbytes': 2})

	def __delfield_num1(self): del self.__field_num1

	    num1=property(__getfield_num1, __setfield_num1, __delfield_num1, None)
	    def __getfield_num2(self):

        return self.__field_num2.getvalue()

	def __setfield_num2(self, value):

        if isinstance(value,UINT):

            self.__field_num2=value

        else:

            self.__field_num2=UINT(value,**{'sizeinbytes': 2})

	def __delfield_num2(self): del self.__field_num2

	    num2=property(__getfield_num2, __setfield_num2, __delfield_num2, None)
	    def __getfield_nfiles(self):

        return self.__field_nfiles.getvalue()

	def __setfield_nfiles(self, value):

        if isinstance(value,UINT):

            self.__field_nfiles=value

        else:

            self.__field_nfiles=UINT(value,**{'sizeinbytes': 2})

	def __delfield_nfiles(self): del self.__field_nfiles

	    nfiles=property(__getfield_nfiles, __setfield_nfiles, __delfield_nfiles, None)
	    def __getfield_num4(self):

        return self.__field_num4.getvalue()

	def __setfield_num4(self, value):

        if isinstance(value,UINT):

            self.__field_num4=value

        else:

            self.__field_num4=UINT(value,**{'sizeinbytes': 2})

	def __delfield_num4(self): del self.__field_num4

	    num4=property(__getfield_num4, __setfield_num4, __delfield_num4, None)
	    def iscontainer(self):

        return True

	def containerelements(self):

        yield ('items', self.__field_items, None)

        yield ('info', self.__field_info, None)

        yield ('info2', self.__field_info2, None)

        yield ('strings', self.__field_strings, None)

        yield ('num1', self.__field_num1, None)

        yield ('num2', self.__field_num2, None)

        yield ('nfiles', self.__field_nfiles, None)

        yield ('num4', self.__field_num4, None)


class  _gen_p_samsungspha620_134 (BaseProtogenClass) :
	'Anonymous inner class'
	    __fields=['zeros', 'sixtyfour']
	    def __init__(self, *args, **kwargs):

        dict={}

        dict.update(kwargs)

        super(_gen_p_samsungspha620_134,self).__init__(**dict)

        if self.__class__ is _gen_p_samsungspha620_134:

            self._update(args,dict)

	def getfields(self):

        return self.__fields

	def _update(self, args, kwargs):

        super(_gen_p_samsungspha620_134,self)._update(args,kwargs)

        keys=kwargs.keys()

        for key in keys:

            if key in self.__fields:

                setattr(self, key, kwargs[key])

                del kwargs[key]

        if __debug__:

            self._complainaboutunusedargs(_gen_p_samsungspha620_134,kwargs)

        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)

	def writetobuffer(self,buf):

        'Writes this packet to the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_zeros.writetobuffer(buf)

        self.__field_sixtyfour.writetobuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def readfrombuffer(self,buf):

        'Reads this packet from the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_zeros=UINT(**{'sizeinbytes': 4})

        self.__field_zeros.readfrombuffer(buf)

        self.__field_sixtyfour=UINT(**{'sizeinbytes': 2})

        self.__field_sixtyfour.readfrombuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def __getfield_zeros(self):

        return self.__field_zeros.getvalue()

	def __setfield_zeros(self, value):

        if isinstance(value,UINT):

            self.__field_zeros=value

        else:

            self.__field_zeros=UINT(value,**{'sizeinbytes': 4})

	def __delfield_zeros(self): del self.__field_zeros

	    zeros=property(__getfield_zeros, __setfield_zeros, __delfield_zeros, None)
	    def __getfield_sixtyfour(self):

        return self.__field_sixtyfour.getvalue()

	def __setfield_sixtyfour(self, value):

        if isinstance(value,UINT):

            self.__field_sixtyfour=value

        else:

            self.__field_sixtyfour=UINT(value,**{'sizeinbytes': 2})

	def __delfield_sixtyfour(self): del self.__field_sixtyfour

	    sixtyfour=property(__getfield_sixtyfour, __setfield_sixtyfour, __delfield_sixtyfour, None)
	    def iscontainer(self):

        return True

	def containerelements(self):

        yield ('zeros', self.__field_zeros, None)

        yield ('sixtyfour', self.__field_sixtyfour, None)

	'Anonymous inner class'

class  _gen_p_samsungspha620_137 (BaseProtogenClass) :
	'Anonymous inner class'
	    __fields=['dir_ptr', 'num2', 'name_ptr', 'version_ptr', 'vendor_ptr', 'downloaddomain_ptr', 'num7', 'num8', 'num9', 'filetype', 'mimetype_ptr', 'num12', 'num13', 'num14', 'num15', 'num16', 'num17']
	    def __init__(self, *args, **kwargs):

        dict={}

        dict.update(kwargs)

        super(_gen_p_samsungspha620_137,self).__init__(**dict)

        if self.__class__ is _gen_p_samsungspha620_137:

            self._update(args,dict)

	def getfields(self):

        return self.__fields

	def _update(self, args, kwargs):

        super(_gen_p_samsungspha620_137,self)._update(args,kwargs)

        keys=kwargs.keys()

        for key in keys:

            if key in self.__fields:

                setattr(self, key, kwargs[key])

                del kwargs[key]

        if __debug__:

            self._complainaboutunusedargs(_gen_p_samsungspha620_137,kwargs)

        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)

	def writetobuffer(self,buf):

        'Writes this packet to the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_dir_ptr.writetobuffer(buf)

        self.__field_num2.writetobuffer(buf)

        self.__field_name_ptr.writetobuffer(buf)

        self.__field_version_ptr.writetobuffer(buf)

        self.__field_vendor_ptr.writetobuffer(buf)

        self.__field_downloaddomain_ptr.writetobuffer(buf)

        self.__field_num7.writetobuffer(buf)

        self.__field_num8.writetobuffer(buf)

        self.__field_num9.writetobuffer(buf)

        self.__field_filetype.writetobuffer(buf)

        self.__field_mimetype_ptr.writetobuffer(buf)

        self.__field_num12.writetobuffer(buf)

        self.__field_num13.writetobuffer(buf)

        self.__field_num14.writetobuffer(buf)

        self.__field_num15.writetobuffer(buf)

        self.__field_num16.writetobuffer(buf)

        self.__field_num17.writetobuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def readfrombuffer(self,buf):

        'Reads this packet from the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_dir_ptr=UINT(**{'sizeinbytes': 2})

        self.__field_dir_ptr.readfrombuffer(buf)

        self.__field_num2=UINT(**{'sizeinbytes': 2})

        self.__field_num2.readfrombuffer(buf)

        self.__field_name_ptr=UINT(**{'sizeinbytes': 2})

        self.__field_name_ptr.readfrombuffer(buf)

        self.__field_version_ptr=UINT(**{'sizeinbytes': 2})

        self.__field_version_ptr.readfrombuffer(buf)

        self.__field_vendor_ptr=UINT(**{'sizeinbytes': 2})

        self.__field_vendor_ptr.readfrombuffer(buf)

        self.__field_downloaddomain_ptr=UINT(**{'sizeinbytes': 2})

        self.__field_downloaddomain_ptr.readfrombuffer(buf)

        self.__field_num7=UINT(**{'sizeinbytes': 2})

        self.__field_num7.readfrombuffer(buf)

        self.__field_num8=UINT(**{'sizeinbytes': 2})

        self.__field_num8.readfrombuffer(buf)

        self.__field_num9=UINT(**{'sizeinbytes': 4})

        self.__field_num9.readfrombuffer(buf)

        self.__field_filetype=UINT(**{'sizeinbytes': 2})

        self.__field_filetype.readfrombuffer(buf)

        self.__field_mimetype_ptr=UINT(**{'sizeinbytes': 2})

        self.__field_mimetype_ptr.readfrombuffer(buf)

        self.__field_num12=UINT(**{'sizeinbytes': 2})

        self.__field_num12.readfrombuffer(buf)

        self.__field_num13=UINT(**{'sizeinbytes': 2})

        self.__field_num13.readfrombuffer(buf)

        self.__field_num14=UINT(**{'sizeinbytes': 2})

        self.__field_num14.readfrombuffer(buf)

        self.__field_num15=UINT(**{'sizeinbytes': 2})

        self.__field_num15.readfrombuffer(buf)

        self.__field_num16=UINT(**{'sizeinbytes': 2})

        self.__field_num16.readfrombuffer(buf)

        self.__field_num17=UINT(**{'sizeinbytes': 2})

        self.__field_num17.readfrombuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def __getfield_dir_ptr(self):

        return self.__field_dir_ptr.getvalue()

	def __setfield_dir_ptr(self, value):

        if isinstance(value,UINT):

            self.__field_dir_ptr=value

        else:

            self.__field_dir_ptr=UINT(value,**{'sizeinbytes': 2})

	def __delfield_dir_ptr(self): del self.__field_dir_ptr

	    dir_ptr=property(__getfield_dir_ptr, __setfield_dir_ptr, __delfield_dir_ptr, None)
	    def __getfield_num2(self):

        return self.__field_num2.getvalue()

	def __setfield_num2(self, value):

        if isinstance(value,UINT):

            self.__field_num2=value

        else:

            self.__field_num2=UINT(value,**{'sizeinbytes': 2})

	def __delfield_num2(self): del self.__field_num2

	    num2=property(__getfield_num2, __setfield_num2, __delfield_num2, None)
	    def __getfield_name_ptr(self):

        return self.__field_name_ptr.getvalue()

	def __setfield_name_ptr(self, value):

        if isinstance(value,UINT):

            self.__field_name_ptr=value

        else:

            self.__field_name_ptr=UINT(value,**{'sizeinbytes': 2})

	def __delfield_name_ptr(self): del self.__field_name_ptr

	    name_ptr=property(__getfield_name_ptr, __setfield_name_ptr, __delfield_name_ptr, None)
	    def __getfield_version_ptr(self):

        return self.__field_version_ptr.getvalue()

	def __setfield_version_ptr(self, value):

        if isinstance(value,UINT):

            self.__field_version_ptr=value

        else:

            self.__field_version_ptr=UINT(value,**{'sizeinbytes': 2})

	def __delfield_version_ptr(self): del self.__field_version_ptr

	    version_ptr=property(__getfield_version_ptr, __setfield_version_ptr, __delfield_version_ptr, None)
	    def __getfield_vendor_ptr(self):

        return self.__field_vendor_ptr.getvalue()

	def __setfield_vendor_ptr(self, value):

        if isinstance(value,UINT):

            self.__field_vendor_ptr=value

        else:

            self.__field_vendor_ptr=UINT(value,**{'sizeinbytes': 2})

	def __delfield_vendor_ptr(self): del self.__field_vendor_ptr

	    vendor_ptr=property(__getfield_vendor_ptr, __setfield_vendor_ptr, __delfield_vendor_ptr, None)
	    def __getfield_downloaddomain_ptr(self):

        return self.__field_downloaddomain_ptr.getvalue()

	def __setfield_downloaddomain_ptr(self, value):

        if isinstance(value,UINT):

            self.__field_downloaddomain_ptr=value

        else:

            self.__field_downloaddomain_ptr=UINT(value,**{'sizeinbytes': 2})

	def __delfield_downloaddomain_ptr(self): del self.__field_downloaddomain_ptr

	    downloaddomain_ptr=property(__getfield_downloaddomain_ptr, __setfield_downloaddomain_ptr, __delfield_downloaddomain_ptr, None)
	    def __getfield_num7(self):

        return self.__field_num7.getvalue()

	def __setfield_num7(self, value):

        if isinstance(value,UINT):

            self.__field_num7=value

        else:

            self.__field_num7=UINT(value,**{'sizeinbytes': 2})

	def __delfield_num7(self): del self.__field_num7

	    num7=property(__getfield_num7, __setfield_num7, __delfield_num7, None)
	    def __getfield_num8(self):

        return self.__field_num8.getvalue()

	def __setfield_num8(self, value):

        if isinstance(value,UINT):

            self.__field_num8=value

        else:

            self.__field_num8=UINT(value,**{'sizeinbytes': 2})

	def __delfield_num8(self): del self.__field_num8

	    num8=property(__getfield_num8, __setfield_num8, __delfield_num8, None)
	    def __getfield_num9(self):

        return self.__field_num9.getvalue()

	def __setfield_num9(self, value):

        if isinstance(value,UINT):

            self.__field_num9=value

        else:

            self.__field_num9=UINT(value,**{'sizeinbytes': 4})

	def __delfield_num9(self): del self.__field_num9

	    num9=property(__getfield_num9, __setfield_num9, __delfield_num9, None)
	    def __getfield_filetype(self):

        return self.__field_filetype.getvalue()

	def __setfield_filetype(self, value):

        if isinstance(value,UINT):

            self.__field_filetype=value

        else:

            self.__field_filetype=UINT(value,**{'sizeinbytes': 2})

	def __delfield_filetype(self): del self.__field_filetype

	    filetype=property(__getfield_filetype, __setfield_filetype, __delfield_filetype, "12: Ringer, 13 Screen Saver, 15 Apps")
	    def __getfield_mimetype_ptr(self):

        return self.__field_mimetype_ptr.getvalue()

	def __setfield_mimetype_ptr(self, value):

        if isinstance(value,UINT):

            self.__field_mimetype_ptr=value

        else:

            self.__field_mimetype_ptr=UINT(value,**{'sizeinbytes': 2})

	def __delfield_mimetype_ptr(self): del self.__field_mimetype_ptr

	    mimetype_ptr=property(__getfield_mimetype_ptr, __setfield_mimetype_ptr, __delfield_mimetype_ptr, None)
	    def __getfield_num12(self):

        return self.__field_num12.getvalue()

	def __setfield_num12(self, value):

        if isinstance(value,UINT):

            self.__field_num12=value

        else:

            self.__field_num12=UINT(value,**{'sizeinbytes': 2})

	def __delfield_num12(self): del self.__field_num12

	    num12=property(__getfield_num12, __setfield_num12, __delfield_num12, None)
	    def __getfield_num13(self):

        return self.__field_num13.getvalue()

	def __setfield_num13(self, value):

        if isinstance(value,UINT):

            self.__field_num13=value

        else:

            self.__field_num13=UINT(value,**{'sizeinbytes': 2})

	def __delfield_num13(self): del self.__field_num13

	    num13=property(__getfield_num13, __setfield_num13, __delfield_num13, None)
	    def __getfield_num14(self):

        return self.__field_num14.getvalue()

	def __setfield_num14(self, value):

        if isinstance(value,UINT):

            self.__field_num14=value

        else:

            self.__field_num14=UINT(value,**{'sizeinbytes': 2})

	def __delfield_num14(self): del self.__field_num14

	    num14=property(__getfield_num14, __setfield_num14, __delfield_num14, None)
	    def __getfield_num15(self):

        return self.__field_num15.getvalue()

	def __setfield_num15(self, value):

        if isinstance(value,UINT):

            self.__field_num15=value

        else:

            self.__field_num15=UINT(value,**{'sizeinbytes': 2})

	def __delfield_num15(self): del self.__field_num15

	    num15=property(__getfield_num15, __setfield_num15, __delfield_num15, None)
	    def __getfield_num16(self):

        return self.__field_num16.getvalue()

	def __setfield_num16(self, value):

        if isinstance(value,UINT):

            self.__field_num16=value

        else:

            self.__field_num16=UINT(value,**{'sizeinbytes': 2})

	def __delfield_num16(self): del self.__field_num16

	    num16=property(__getfield_num16, __setfield_num16, __delfield_num16, None)
	    def __getfield_num17(self):

        return self.__field_num17.getvalue()

	def __setfield_num17(self, value):

        if isinstance(value,UINT):

            self.__field_num17=value

        else:

            self.__field_num17=UINT(value,**{'sizeinbytes': 2})

	def __delfield_num17(self): del self.__field_num17

	    num17=property(__getfield_num17, __setfield_num17, __delfield_num17, None)
	    def iscontainer(self):

        return True

	def containerelements(self):

        yield ('dir_ptr', self.__field_dir_ptr, None)

        yield ('num2', self.__field_num2, None)

        yield ('name_ptr', self.__field_name_ptr, None)

        yield ('version_ptr', self.__field_version_ptr, None)

        yield ('vendor_ptr', self.__field_vendor_ptr, None)

        yield ('downloaddomain_ptr', self.__field_downloaddomain_ptr, None)

        yield ('num7', self.__field_num7, None)

        yield ('num8', self.__field_num8, None)

        yield ('num9', self.__field_num9, None)

        yield ('filetype', self.__field_filetype, "12: Ringer, 13 Screen Saver, 15 Apps")

        yield ('mimetype_ptr', self.__field_mimetype_ptr, None)

        yield ('num12', self.__field_num12, None)

        yield ('num13', self.__field_num13, None)

        yield ('num14', self.__field_num14, None)

        yield ('num15', self.__field_num15, None)

        yield ('num16', self.__field_num16, None)

        yield ('num17', self.__field_num17, None)

	'Anonymous inner class'

class  _gen_p_samsungspha620_155 (BaseProtogenClass) :
	'Anonymous inner class'
	    __fields=['val1', 'val2', 'val3', 'val4', 'val5', 'val6', 'val7', 'val8', 'val9', 'val10']
	    def __init__(self, *args, **kwargs):

        dict={}

        dict.update(kwargs)

        super(_gen_p_samsungspha620_155,self).__init__(**dict)

        if self.__class__ is _gen_p_samsungspha620_155:

            self._update(args,dict)

	def getfields(self):

        return self.__fields

	def _update(self, args, kwargs):

        super(_gen_p_samsungspha620_155,self)._update(args,kwargs)

        keys=kwargs.keys()

        for key in keys:

            if key in self.__fields:

                setattr(self, key, kwargs[key])

                del kwargs[key]

        if __debug__:

            self._complainaboutunusedargs(_gen_p_samsungspha620_155,kwargs)

        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)

	def writetobuffer(self,buf):

        'Writes this packet to the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_val1.writetobuffer(buf)

        self.__field_val2.writetobuffer(buf)

        self.__field_val3.writetobuffer(buf)

        self.__field_val4.writetobuffer(buf)

        self.__field_val5.writetobuffer(buf)

        self.__field_val6.writetobuffer(buf)

        self.__field_val7.writetobuffer(buf)

        self.__field_val8.writetobuffer(buf)

        self.__field_val9.writetobuffer(buf)

        self.__field_val10.writetobuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def readfrombuffer(self,buf):

        'Reads this packet from the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_val1=UINT(**{'sizeinbytes': 2})

        self.__field_val1.readfrombuffer(buf)

        self.__field_val2=UINT(**{'sizeinbytes': 2})

        self.__field_val2.readfrombuffer(buf)

        self.__field_val3=UINT(**{'sizeinbytes': 2})

        self.__field_val3.readfrombuffer(buf)

        self.__field_val4=UINT(**{'sizeinbytes': 2})

        self.__field_val4.readfrombuffer(buf)

        self.__field_val5=UINT(**{'sizeinbytes': 2})

        self.__field_val5.readfrombuffer(buf)

        self.__field_val6=UINT(**{'sizeinbytes': 2})

        self.__field_val6.readfrombuffer(buf)

        self.__field_val7=UINT(**{'sizeinbytes': 2})

        self.__field_val7.readfrombuffer(buf)

        self.__field_val8=UINT(**{'sizeinbytes': 2})

        self.__field_val8.readfrombuffer(buf)

        self.__field_val9=UINT(**{'sizeinbytes': 2})

        self.__field_val9.readfrombuffer(buf)

        self.__field_val10=UINT(**{'sizeinbytes': 2})

        self.__field_val10.readfrombuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def __getfield_val1(self):

        return self.__field_val1.getvalue()

	def __setfield_val1(self, value):

        if isinstance(value,UINT):

            self.__field_val1=value

        else:

            self.__field_val1=UINT(value,**{'sizeinbytes': 2})

	def __delfield_val1(self): del self.__field_val1

	    val1=property(__getfield_val1, __setfield_val1, __delfield_val1, None)
	    def __getfield_val2(self):

        return self.__field_val2.getvalue()

	def __setfield_val2(self, value):

        if isinstance(value,UINT):

            self.__field_val2=value

        else:

            self.__field_val2=UINT(value,**{'sizeinbytes': 2})

	def __delfield_val2(self): del self.__field_val2

	    val2=property(__getfield_val2, __setfield_val2, __delfield_val2, None)
	    def __getfield_val3(self):

        return self.__field_val3.getvalue()

	def __setfield_val3(self, value):

        if isinstance(value,UINT):

            self.__field_val3=value

        else:

            self.__field_val3=UINT(value,**{'sizeinbytes': 2})

	def __delfield_val3(self): del self.__field_val3

	    val3=property(__getfield_val3, __setfield_val3, __delfield_val3, None)
	    def __getfield_val4(self):

        return self.__field_val4.getvalue()

	def __setfield_val4(self, value):

        if isinstance(value,UINT):

            self.__field_val4=value

        else:

            self.__field_val4=UINT(value,**{'sizeinbytes': 2})

	def __delfield_val4(self): del self.__field_val4

	    val4=property(__getfield_val4, __setfield_val4, __delfield_val4, None)
	    def __getfield_val5(self):

        return self.__field_val5.getvalue()

	def __setfield_val5(self, value):

        if isinstance(value,UINT):

            self.__field_val5=value

        else:

            self.__field_val5=UINT(value,**{'sizeinbytes': 2})

	def __delfield_val5(self): del self.__field_val5

	    val5=property(__getfield_val5, __setfield_val5, __delfield_val5, None)
	    def __getfield_val6(self):

        return self.__field_val6.getvalue()

	def __setfield_val6(self, value):

        if isinstance(value,UINT):

            self.__field_val6=value

        else:

            self.__field_val6=UINT(value,**{'sizeinbytes': 2})

	def __delfield_val6(self): del self.__field_val6

	    val6=property(__getfield_val6, __setfield_val6, __delfield_val6, None)
	    def __getfield_val7(self):

        return self.__field_val7.getvalue()

	def __setfield_val7(self, value):

        if isinstance(value,UINT):

            self.__field_val7=value

        else:

            self.__field_val7=UINT(value,**{'sizeinbytes': 2})

	def __delfield_val7(self): del self.__field_val7

	    val7=property(__getfield_val7, __setfield_val7, __delfield_val7, None)
	    def __getfield_val8(self):

        return self.__field_val8.getvalue()

	def __setfield_val8(self, value):

        if isinstance(value,UINT):

            self.__field_val8=value

        else:

            self.__field_val8=UINT(value,**{'sizeinbytes': 2})

	def __delfield_val8(self): del self.__field_val8

	    val8=property(__getfield_val8, __setfield_val8, __delfield_val8, None)
	    def __getfield_val9(self):

        return self.__field_val9.getvalue()

	def __setfield_val9(self, value):

        if isinstance(value,UINT):

            self.__field_val9=value

        else:

            self.__field_val9=UINT(value,**{'sizeinbytes': 2})

	def __delfield_val9(self): del self.__field_val9

	    val9=property(__getfield_val9, __setfield_val9, __delfield_val9, None)
	    def __getfield_val10(self):

        return self.__field_val10.getvalue()

	def __setfield_val10(self, value):

        if isinstance(value,UINT):

            self.__field_val10=value

        else:

            self.__field_val10=UINT(value,**{'sizeinbytes': 2})

	def __delfield_val10(self): del self.__field_val10

	    val10=property(__getfield_val10, __setfield_val10, __delfield_val10, None)
	    def iscontainer(self):

        return True

	def containerelements(self):

        yield ('val1', self.__field_val1, None)

        yield ('val2', self.__field_val2, None)

        yield ('val3', self.__field_val3, None)

        yield ('val4', self.__field_val4, None)

        yield ('val5', self.__field_val5, None)

        yield ('val6', self.__field_val6, None)

        yield ('val7', self.__field_val7, None)

        yield ('val8', self.__field_val8, None)

        yield ('val9', self.__field_val9, None)

        yield ('val10', self.__field_val10, None)

	'Anonymous inner class'

