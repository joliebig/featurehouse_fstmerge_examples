"""Proposed descriptions of data usign AT commands"""

from prototypes import *

from p_samsung_packet import *

UINT=UINTlsb

BOOL=BOOLlsb

NUMPHONEBOOKENTRIES=250

NUMPHONENUMBERS=6

NUMCALENDAREVENTS=70

MAXNUMBERLEN=32

NUMTODOENTRIES=20

NUMGROUPS=4

class  pbentry (BaseProtogenClass) :
	__fields=['url', 'birthday', 'slot', 'uslot', 'group', 'ringtone', 'name', 'speeddial', 'dunno1', 'numbers', 'dunno3', 'dunno4', 'email', 'timestamp']
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

        if getattr(self, '__field_url', None) is None:

            self.__field_url=STRING(**{'default': ""})

        if getattr(self, '__field_birthday', None) is None:

            self.__field_birthday=CSVDATE(**{'default': ""})

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

        self.__field_timestamp=CSVTIME(**{'terminator': None, 'default': (1980,1,1,12,0,0)})

        self.__field_timestamp.readfrombuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def __getfield_url(self):

        try: self.__field_url

        except:

            self.__field_url=STRING(**{'default': ""})

        return self.__field_url.getvalue()

	def __setfield_url(self, value):

        if isinstance(value,STRING):

            self.__field_url=value

        else:

            self.__field_url=STRING(value,**{'default': ""})

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

        yield ('url', self.__field_url, None)

        yield ('birthday', self.__field_birthday, None)

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
	__fields=['gid', 'groupname']
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

        self._bufferendoffset=buf.getcurrentoffset()

	def readfrombuffer(self,buf):

        'Reads this packet from the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_gid=CSVINT()

        self.__field_gid.readfrombuffer(buf)

        self.__field_groupname=CSVSTRING(**{'terminator': None})

        self.__field_groupname.readfrombuffer(buf)

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

            self.__field_groupname=CSVSTRING(value,**{'terminator': None})

	def __delfield_groupname(self): del self.__field_groupname

	    groupname=property(__getfield_groupname, __setfield_groupname, __delfield_groupname, None)
	    def iscontainer(self):

        return True

	def containerelements(self):

        yield ('gid', self.__field_gid, None)

        yield ('groupname', self.__field_groupname, None)


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


class  eventrequest (BaseProtogenClass) :
	__fields=['command', 'slot']
	    def __init__(self, *args, **kwargs):

        dict={}

        dict.update(kwargs)

        super(eventrequest,self).__init__(**dict)

        if self.__class__ is eventrequest:

            self._update(args,dict)

	def getfields(self):

        return self.__fields

	def _update(self, args, kwargs):

        super(eventrequest,self)._update(args,kwargs)

        keys=kwargs.keys()

        for key in keys:

            if key in self.__fields:

                setattr(self, key, kwargs[key])

                del kwargs[key]

        if __debug__:

            self._complainaboutunusedargs(eventrequest,kwargs)

        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)

	def writetobuffer(self,buf):

        'Writes this packet to the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        try: self.__field_command

        except:

            self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PISHR='})

        self.__field_command.writetobuffer(buf)

        try: self.__field_slot

        except:

            self.__field_slot=CSVINT(**{'terminator': None})

        self.__field_slot.writetobuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def readfrombuffer(self,buf):

        'Reads this packet from the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PISHR='})

        self.__field_command.readfrombuffer(buf)

        self.__field_slot=CSVINT(**{'terminator': None})

        self.__field_slot.readfrombuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def __getfield_command(self):

        try: self.__field_command

        except:

            self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PISHR='})

        return self.__field_command.getvalue()

	def __setfield_command(self, value):

        if isinstance(value,CSVSTRING):

            self.__field_command=value

        else:

            self.__field_command=CSVSTRING(value,**{'quotechar': None, 'terminator': None, 'default': '#PISHR='})

	def __delfield_command(self): del self.__field_command

	    command=property(__getfield_command, __setfield_command, __delfield_command, None)
	    def __getfield_slot(self):

        try: self.__field_slot

        except:

            self.__field_slot=CSVINT(**{'terminator': None})

        return self.__field_slot.getvalue()

	def __setfield_slot(self, value):

        if isinstance(value,CSVINT):

            self.__field_slot=value

        else:

            self.__field_slot=CSVINT(value,**{'terminator': None})

	def __delfield_slot(self): del self.__field_slot

	    slot=property(__getfield_slot, __setfield_slot, __delfield_slot, None)
	    def iscontainer(self):

        return True

	def containerelements(self):

        yield ('command', self.__field_command, None)

        yield ('slot', self.__field_slot, None)


class  eventresponse (BaseProtogenClass) :
	__fields=['command', 'entry']
	    def __init__(self, *args, **kwargs):

        dict={}

        dict.update(kwargs)

        super(eventresponse,self).__init__(**dict)

        if self.__class__ is eventresponse:

            self._update(args,dict)

	def getfields(self):

        return self.__fields

	def _update(self, args, kwargs):

        super(eventresponse,self)._update(args,kwargs)

        keys=kwargs.keys()

        for key in keys:

            if key in self.__fields:

                setattr(self, key, kwargs[key])

                del kwargs[key]

        if __debug__:

            self._complainaboutunusedargs(eventresponse,kwargs)

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

        self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': ord(' '), 'constant': '#PISHR:'})

        self.__field_command.readfrombuffer(buf)

        self.__field_entry=evententry()

        self.__field_entry.readfrombuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def __getfield_command(self):

        return self.__field_command.getvalue()

	def __setfield_command(self, value):

        if isinstance(value,CSVSTRING):

            self.__field_command=value

        else:

            self.__field_command=CSVSTRING(value,**{'quotechar': None, 'terminator': ord(' '), 'constant': '#PISHR:'})

	def __delfield_command(self): del self.__field_command

	    command=property(__getfield_command, __setfield_command, __delfield_command, None)
	    def __getfield_entry(self):

        return self.__field_entry.getvalue()

	def __setfield_entry(self, value):

        if isinstance(value,evententry):

            self.__field_entry=value

        else:

            self.__field_entry=evententry(value,)

	def __delfield_entry(self): del self.__field_entry

	    entry=property(__getfield_entry, __setfield_entry, __delfield_entry, None)
	    def iscontainer(self):

        return True

	def containerelements(self):

        yield ('command', self.__field_command, None)

        yield ('entry', self.__field_entry, None)


class  evententry (BaseProtogenClass) :
	__fields=['slot', 'start', 'end', 'timestamp', 'alarm', 'dunno', 'eventname']
	    def __init__(self, *args, **kwargs):

        dict={}

        dict.update(kwargs)

        super(evententry,self).__init__(**dict)

        if self.__class__ is evententry:

            self._update(args,dict)

	def getfields(self):

        return self.__fields

	def _update(self, args, kwargs):

        super(evententry,self)._update(args,kwargs)

        keys=kwargs.keys()

        for key in keys:

            if key in self.__fields:

                setattr(self, key, kwargs[key])

                del kwargs[key]

        if __debug__:

            self._complainaboutunusedargs(evententry,kwargs)

        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)

	def writetobuffer(self,buf):

        'Writes this packet to the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_slot.writetobuffer(buf)

        self.__field_start.writetobuffer(buf)

        self.__field_end.writetobuffer(buf)

        self.__field_timestamp.writetobuffer(buf)

        self.__field_alarm.writetobuffer(buf)

        self.__field_dunno.writetobuffer(buf)

        self.__field_eventname.writetobuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def readfrombuffer(self,buf):

        'Reads this packet from the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_slot=CSVINT()

        self.__field_slot.readfrombuffer(buf)

        self.__field_start=CSVTIME()

        self.__field_start.readfrombuffer(buf)

        self.__field_end=CSVTIME()

        self.__field_end.readfrombuffer(buf)

        self.__field_timestamp=CSVTIME()

        self.__field_timestamp.readfrombuffer(buf)

        self.__field_alarm=CSVINT()

        self.__field_alarm.readfrombuffer(buf)

        self.__field_dunno=CSVSTRING(**{'quotechar': None})

        self.__field_dunno.readfrombuffer(buf)

        self.__field_eventname=CSVSTRING(**{'terminator': None})

        self.__field_eventname.readfrombuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def __getfield_slot(self):

        return self.__field_slot.getvalue()

	def __setfield_slot(self, value):

        if isinstance(value,CSVINT):

            self.__field_slot=value

        else:

            self.__field_slot=CSVINT(value,)

	def __delfield_slot(self): del self.__field_slot

	    slot=property(__getfield_slot, __setfield_slot, __delfield_slot, None)
	    def __getfield_start(self):

        return self.__field_start.getvalue()

	def __setfield_start(self, value):

        if isinstance(value,CSVTIME):

            self.__field_start=value

        else:

            self.__field_start=CSVTIME(value,)

	def __delfield_start(self): del self.__field_start

	    start=property(__getfield_start, __setfield_start, __delfield_start, None)
	    def __getfield_end(self):

        return self.__field_end.getvalue()

	def __setfield_end(self, value):

        if isinstance(value,CSVTIME):

            self.__field_end=value

        else:

            self.__field_end=CSVTIME(value,)

	def __delfield_end(self): del self.__field_end

	    end=property(__getfield_end, __setfield_end, __delfield_end, None)
	    def __getfield_timestamp(self):

        return self.__field_timestamp.getvalue()

	def __setfield_timestamp(self, value):

        if isinstance(value,CSVTIME):

            self.__field_timestamp=value

        else:

            self.__field_timestamp=CSVTIME(value,)

	def __delfield_timestamp(self): del self.__field_timestamp

	    timestamp=property(__getfield_timestamp, __setfield_timestamp, __delfield_timestamp, None)
	    def __getfield_alarm(self):

        return self.__field_alarm.getvalue()

	def __setfield_alarm(self, value):

        if isinstance(value,CSVINT):

            self.__field_alarm=value

        else:

            self.__field_alarm=CSVINT(value,)

	def __delfield_alarm(self): del self.__field_alarm

	    alarm=property(__getfield_alarm, __setfield_alarm, __delfield_alarm, "0: No Alarm, 1: On Time, 2: 10 minutes, 3: 30 minutes, 4: 60 minutes")
	    def __getfield_dunno(self):

        return self.__field_dunno.getvalue()

	def __setfield_dunno(self, value):

        if isinstance(value,CSVSTRING):

            self.__field_dunno=value

        else:

            self.__field_dunno=CSVSTRING(value,**{'quotechar': None})

	def __delfield_dunno(self): del self.__field_dunno

	    dunno=property(__getfield_dunno, __setfield_dunno, __delfield_dunno, None)
	    def __getfield_eventname(self):

        return self.__field_eventname.getvalue()

	def __setfield_eventname(self, value):

        if isinstance(value,CSVSTRING):

            self.__field_eventname=value

        else:

            self.__field_eventname=CSVSTRING(value,**{'terminator': None})

	def __delfield_eventname(self): del self.__field_eventname

	    eventname=property(__getfield_eventname, __setfield_eventname, __delfield_eventname, None)
	    def iscontainer(self):

        return True

	def containerelements(self):

        yield ('slot', self.__field_slot, None)

        yield ('start', self.__field_start, None)

        yield ('end', self.__field_end, None)

        yield ('timestamp', self.__field_timestamp, None)

        yield ('alarm', self.__field_alarm, "0: No Alarm, 1: On Time, 2: 10 minutes, 3: 30 minutes, 4: 60 minutes")

        yield ('dunno', self.__field_dunno, None)

        yield ('eventname', self.__field_eventname, None)


