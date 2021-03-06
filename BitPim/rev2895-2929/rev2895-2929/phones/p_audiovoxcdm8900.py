"""Various descriptions of data specific to Audiovox CDM8900"""

from prototypes import *

UINT=UINTlsb

BOOL=BOOLlsb

_NUMSLOTS=300

_NUMGROUPS=7

_ALLGROUP=0

_MAXGROUPLEN=16

_MAXPHONENUMBERLEN=32

_MAXNAMELEN=16

_MAXEMAILLEN=48

_MAXMEMOLEN=48

class  readpbslotsrequest (BaseProtogenClass) :
	"Get a list of which slots are used"
	    __fields=['cmd']
	    def __init__(self, *args, **kwargs):

        dict={}

        dict.update(kwargs)

        super(readpbslotsrequest,self).__init__(**dict)

        if self.__class__ is readpbslotsrequest:

            self._update(args,dict)

	def getfields(self):

        return self.__fields

	def _update(self, args, kwargs):

        super(readpbslotsrequest,self)._update(args,kwargs)

        keys=kwargs.keys()

        for key in keys:

            if key in self.__fields:

                setattr(self, key, kwargs[key])

                del kwargs[key]

        if __debug__:

            self._complainaboutunusedargs(readpbslotsrequest,kwargs)

        if len(args):

            dict2={'sizeinbytes': 1, 'constant': 0x85}

            dict2.update(kwargs)

            kwargs=dict2

            self.__field_cmd=UINT(*args,**dict2)

	def writetobuffer(self,buf):

        'Writes this packet to the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        try: self.__field_cmd

        except:

            self.__field_cmd=UINT(**{'sizeinbytes': 1, 'constant': 0x85})

        self.__field_cmd.writetobuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def readfrombuffer(self,buf):

        'Reads this packet from the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_cmd=UINT(**{'sizeinbytes': 1, 'constant': 0x85})

        self.__field_cmd.readfrombuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def __getfield_cmd(self):

        try: self.__field_cmd

        except:

            self.__field_cmd=UINT(**{'sizeinbytes': 1, 'constant': 0x85})

        return self.__field_cmd.getvalue()

	def __setfield_cmd(self, value):

        if isinstance(value,UINT):

            self.__field_cmd=value

        else:

            self.__field_cmd=UINT(value,**{'sizeinbytes': 1, 'constant': 0x85})

	def __delfield_cmd(self): del self.__field_cmd

	    cmd=property(__getfield_cmd, __setfield_cmd, __delfield_cmd, None)
	    def iscontainer(self):

        return True

	def containerelements(self):

        yield ('cmd', self.__field_cmd, None)

	"Get a list of which slots are used"

class  readpbslotsresponse (BaseProtogenClass) :
	__fields=['cmd', 'present']
	    def __init__(self, *args, **kwargs):

        dict={}

        dict.update(kwargs)

        super(readpbslotsresponse,self).__init__(**dict)

        if self.__class__ is readpbslotsresponse:

            self._update(args,dict)

	def getfields(self):

        return self.__fields

	def _update(self, args, kwargs):

        super(readpbslotsresponse,self)._update(args,kwargs)

        keys=kwargs.keys()

        for key in keys:

            if key in self.__fields:

                setattr(self, key, kwargs[key])

                del kwargs[key]

        if __debug__:

            self._complainaboutunusedargs(readpbslotsresponse,kwargs)

        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)

	def writetobuffer(self,buf):

        'Writes this packet to the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_cmd.writetobuffer(buf)

        self.__field_present.writetobuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def readfrombuffer(self,buf):

        'Reads this packet from the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_cmd=UINT(**{'sizeinbytes': 1, 'constant': 0x85})

        self.__field_cmd.readfrombuffer(buf)

        self.__field_present=DATA()

        self.__field_present.readfrombuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def __getfield_cmd(self):

        return self.__field_cmd.getvalue()

	def __setfield_cmd(self, value):

        if isinstance(value,UINT):

            self.__field_cmd=value

        else:

            self.__field_cmd=UINT(value,**{'sizeinbytes': 1, 'constant': 0x85})

	def __delfield_cmd(self): del self.__field_cmd

	    cmd=property(__getfield_cmd, __setfield_cmd, __delfield_cmd, None)
	    def __getfield_present(self):

        return self.__field_present.getvalue()

	def __setfield_present(self, value):

        if isinstance(value,DATA):

            self.__field_present=value

        else:

            self.__field_present=DATA(value,)

	def __delfield_present(self): del self.__field_present

	    present=property(__getfield_present, __setfield_present, __delfield_present, "a non-zero value indicates a slot is present")
	    def iscontainer(self):

        return True

	def containerelements(self):

        yield ('cmd', self.__field_cmd, None)

        yield ('present', self.__field_present, "a non-zero value indicates a slot is present")


class  writepbslotsrequest (BaseProtogenClass) :
	__fields=['cmd', 'present']
	    def __init__(self, *args, **kwargs):

        dict={}

        dict.update(kwargs)

        super(writepbslotsrequest,self).__init__(**dict)

        if self.__class__ is writepbslotsrequest:

            self._update(args,dict)

	def getfields(self):

        return self.__fields

	def _update(self, args, kwargs):

        super(writepbslotsrequest,self)._update(args,kwargs)

        keys=kwargs.keys()

        for key in keys:

            if key in self.__fields:

                setattr(self, key, kwargs[key])

                del kwargs[key]

        if __debug__:

            self._complainaboutunusedargs(writepbslotsrequest,kwargs)

        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)

	def writetobuffer(self,buf):

        'Writes this packet to the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        try: self.__field_cmd

        except:

            self.__field_cmd=UINT(**{'sizeinbytes': 1, 'constant': 0x84})

        self.__field_cmd.writetobuffer(buf)

        self.__field_present.writetobuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def readfrombuffer(self,buf):

        'Reads this packet from the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_cmd=UINT(**{'sizeinbytes': 1, 'constant': 0x84})

        self.__field_cmd.readfrombuffer(buf)

        self.__field_present=DATA()

        self.__field_present.readfrombuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def __getfield_cmd(self):

        try: self.__field_cmd

        except:

            self.__field_cmd=UINT(**{'sizeinbytes': 1, 'constant': 0x84})

        return self.__field_cmd.getvalue()

	def __setfield_cmd(self, value):

        if isinstance(value,UINT):

            self.__field_cmd=value

        else:

            self.__field_cmd=UINT(value,**{'sizeinbytes': 1, 'constant': 0x84})

	def __delfield_cmd(self): del self.__field_cmd

	    cmd=property(__getfield_cmd, __setfield_cmd, __delfield_cmd, None)
	    def __getfield_present(self):

        return self.__field_present.getvalue()

	def __setfield_present(self, value):

        if isinstance(value,DATA):

            self.__field_present=value

        else:

            self.__field_present=DATA(value,)

	def __delfield_present(self): del self.__field_present

	    present=property(__getfield_present, __setfield_present, __delfield_present, "a non-zero value indicates a slot is present")
	    def iscontainer(self):

        return True

	def containerelements(self):

        yield ('cmd', self.__field_cmd, None)

        yield ('present', self.__field_present, "a non-zero value indicates a slot is present")


class  writepbslotsresponse (BaseProtogenClass) :
	__fields=['cmd']
	    def __init__(self, *args, **kwargs):

        dict={}

        dict.update(kwargs)

        super(writepbslotsresponse,self).__init__(**dict)

        if self.__class__ is writepbslotsresponse:

            self._update(args,dict)

	def getfields(self):

        return self.__fields

	def _update(self, args, kwargs):

        super(writepbslotsresponse,self)._update(args,kwargs)

        keys=kwargs.keys()

        for key in keys:

            if key in self.__fields:

                setattr(self, key, kwargs[key])

                del kwargs[key]

        if __debug__:

            self._complainaboutunusedargs(writepbslotsresponse,kwargs)

        if len(args):

            dict2={'sizeinbytes': 1, 'constant': 0x84}

            dict2.update(kwargs)

            kwargs=dict2

            self.__field_cmd=UINT(*args,**dict2)

	def writetobuffer(self,buf):

        'Writes this packet to the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_cmd.writetobuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def readfrombuffer(self,buf):

        'Reads this packet from the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_cmd=UINT(**{'sizeinbytes': 1, 'constant': 0x84})

        self.__field_cmd.readfrombuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def __getfield_cmd(self):

        return self.__field_cmd.getvalue()

	def __setfield_cmd(self, value):

        if isinstance(value,UINT):

            self.__field_cmd=value

        else:

            self.__field_cmd=UINT(value,**{'sizeinbytes': 1, 'constant': 0x84})

	def __delfield_cmd(self): del self.__field_cmd

	    cmd=property(__getfield_cmd, __setfield_cmd, __delfield_cmd, None)
	    def iscontainer(self):

        return True

	def containerelements(self):

        yield ('cmd', self.__field_cmd, None)


class  readpbentryrequest (BaseProtogenClass) :
	__fields=['cmd', 'slotnumber']
	    def __init__(self, *args, **kwargs):

        dict={}

        dict.update(kwargs)

        super(readpbentryrequest,self).__init__(**dict)

        if self.__class__ is readpbentryrequest:

            self._update(args,dict)

	def getfields(self):

        return self.__fields

	def _update(self, args, kwargs):

        super(readpbentryrequest,self)._update(args,kwargs)

        keys=kwargs.keys()

        for key in keys:

            if key in self.__fields:

                setattr(self, key, kwargs[key])

                del kwargs[key]

        if __debug__:

            self._complainaboutunusedargs(readpbentryrequest,kwargs)

        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)

	def writetobuffer(self,buf):

        'Writes this packet to the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        try: self.__field_cmd

        except:

            self.__field_cmd=UINT(**{'sizeinbytes': 1, 'constant': 0x83})

        self.__field_cmd.writetobuffer(buf)

        self.__field_slotnumber.writetobuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def readfrombuffer(self,buf):

        'Reads this packet from the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_cmd=UINT(**{'sizeinbytes': 1, 'constant': 0x83})

        self.__field_cmd.readfrombuffer(buf)

        self.__field_slotnumber=UINT(**{'sizeinbytes': 2})

        self.__field_slotnumber.readfrombuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def __getfield_cmd(self):

        try: self.__field_cmd

        except:

            self.__field_cmd=UINT(**{'sizeinbytes': 1, 'constant': 0x83})

        return self.__field_cmd.getvalue()

	def __setfield_cmd(self, value):

        if isinstance(value,UINT):

            self.__field_cmd=value

        else:

            self.__field_cmd=UINT(value,**{'sizeinbytes': 1, 'constant': 0x83})

	def __delfield_cmd(self): del self.__field_cmd

	    cmd=property(__getfield_cmd, __setfield_cmd, __delfield_cmd, None)
	    def __getfield_slotnumber(self):

        return self.__field_slotnumber.getvalue()

	def __setfield_slotnumber(self, value):

        if isinstance(value,UINT):

            self.__field_slotnumber=value

        else:

            self.__field_slotnumber=UINT(value,**{'sizeinbytes': 2})

	def __delfield_slotnumber(self): del self.__field_slotnumber

	    slotnumber=property(__getfield_slotnumber, __setfield_slotnumber, __delfield_slotnumber, None)
	    def iscontainer(self):

        return True

	def containerelements(self):

        yield ('cmd', self.__field_cmd, None)

        yield ('slotnumber', self.__field_slotnumber, None)


class  readpbentryresponse (BaseProtogenClass) :
	__fields=['cmd', 'slotnumber', 'entry']
	    def __init__(self, *args, **kwargs):

        dict={}

        dict.update(kwargs)

        super(readpbentryresponse,self).__init__(**dict)

        if self.__class__ is readpbentryresponse:

            self._update(args,dict)

	def getfields(self):

        return self.__fields

	def _update(self, args, kwargs):

        super(readpbentryresponse,self)._update(args,kwargs)

        keys=kwargs.keys()

        for key in keys:

            if key in self.__fields:

                setattr(self, key, kwargs[key])

                del kwargs[key]

        if __debug__:

            self._complainaboutunusedargs(readpbentryresponse,kwargs)

        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)

	def writetobuffer(self,buf):

        'Writes this packet to the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        try: self.__field_cmd

        except:

            self.__field_cmd=UINT(**{'sizeinbytes': 1, 'constant': 0x83})

        self.__field_cmd.writetobuffer(buf)

        self.__field_slotnumber.writetobuffer(buf)

        self.__field_entry.writetobuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def readfrombuffer(self,buf):

        'Reads this packet from the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_cmd=UINT(**{'sizeinbytes': 1, 'constant': 0x83})

        self.__field_cmd.readfrombuffer(buf)

        self.__field_slotnumber=UINT(**{'sizeinbytes': 2})

        self.__field_slotnumber.readfrombuffer(buf)

        self.__field_entry=pbentry()

        self.__field_entry.readfrombuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def __getfield_cmd(self):

        try: self.__field_cmd

        except:

            self.__field_cmd=UINT(**{'sizeinbytes': 1, 'constant': 0x83})

        return self.__field_cmd.getvalue()

	def __setfield_cmd(self, value):

        if isinstance(value,UINT):

            self.__field_cmd=value

        else:

            self.__field_cmd=UINT(value,**{'sizeinbytes': 1, 'constant': 0x83})

	def __delfield_cmd(self): del self.__field_cmd

	    cmd=property(__getfield_cmd, __setfield_cmd, __delfield_cmd, None)
	    def __getfield_slotnumber(self):

        return self.__field_slotnumber.getvalue()

	def __setfield_slotnumber(self, value):

        if isinstance(value,UINT):

            self.__field_slotnumber=value

        else:

            self.__field_slotnumber=UINT(value,**{'sizeinbytes': 2})

	def __delfield_slotnumber(self): del self.__field_slotnumber

	    slotnumber=property(__getfield_slotnumber, __setfield_slotnumber, __delfield_slotnumber, None)
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

        yield ('cmd', self.__field_cmd, None)

        yield ('slotnumber', self.__field_slotnumber, None)

        yield ('entry', self.__field_entry, None)


class  writepbentryrequest (BaseProtogenClass) :
	__fields=['cmd', 'slotnumber', 'entry']
	    def __init__(self, *args, **kwargs):

        dict={}

        dict.update(kwargs)

        super(writepbentryrequest,self).__init__(**dict)

        if self.__class__ is writepbentryrequest:

            self._update(args,dict)

	def getfields(self):

        return self.__fields

	def _update(self, args, kwargs):

        super(writepbentryrequest,self)._update(args,kwargs)

        keys=kwargs.keys()

        for key in keys:

            if key in self.__fields:

                setattr(self, key, kwargs[key])

                del kwargs[key]

        if __debug__:

            self._complainaboutunusedargs(writepbentryrequest,kwargs)

        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)

	def writetobuffer(self,buf):

        'Writes this packet to the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        try: self.__field_cmd

        except:

            self.__field_cmd=UINT(**{'sizeinbytes': 1, 'constant': 0x82})

        self.__field_cmd.writetobuffer(buf)

        self.__field_slotnumber.writetobuffer(buf)

        self.__field_entry.writetobuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def readfrombuffer(self,buf):

        'Reads this packet from the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_cmd=UINT(**{'sizeinbytes': 1, 'constant': 0x82})

        self.__field_cmd.readfrombuffer(buf)

        self.__field_slotnumber=UINT(**{'sizeinbytes': 2})

        self.__field_slotnumber.readfrombuffer(buf)

        self.__field_entry=pbentry()

        self.__field_entry.readfrombuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def __getfield_cmd(self):

        try: self.__field_cmd

        except:

            self.__field_cmd=UINT(**{'sizeinbytes': 1, 'constant': 0x82})

        return self.__field_cmd.getvalue()

	def __setfield_cmd(self, value):

        if isinstance(value,UINT):

            self.__field_cmd=value

        else:

            self.__field_cmd=UINT(value,**{'sizeinbytes': 1, 'constant': 0x82})

	def __delfield_cmd(self): del self.__field_cmd

	    cmd=property(__getfield_cmd, __setfield_cmd, __delfield_cmd, None)
	    def __getfield_slotnumber(self):

        return self.__field_slotnumber.getvalue()

	def __setfield_slotnumber(self, value):

        if isinstance(value,UINT):

            self.__field_slotnumber=value

        else:

            self.__field_slotnumber=UINT(value,**{'sizeinbytes': 2})

	def __delfield_slotnumber(self): del self.__field_slotnumber

	    slotnumber=property(__getfield_slotnumber, __setfield_slotnumber, __delfield_slotnumber, None)
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

        yield ('cmd', self.__field_cmd, None)

        yield ('slotnumber', self.__field_slotnumber, None)

        yield ('entry', self.__field_entry, None)


class  writepbentryresponse (BaseProtogenClass) :
	__fields=['cmd', 'slotnumber']
	    def __init__(self, *args, **kwargs):

        dict={}

        dict.update(kwargs)

        super(writepbentryresponse,self).__init__(**dict)

        if self.__class__ is writepbentryresponse:

            self._update(args,dict)

	def getfields(self):

        return self.__fields

	def _update(self, args, kwargs):

        super(writepbentryresponse,self)._update(args,kwargs)

        keys=kwargs.keys()

        for key in keys:

            if key in self.__fields:

                setattr(self, key, kwargs[key])

                del kwargs[key]

        if __debug__:

            self._complainaboutunusedargs(writepbentryresponse,kwargs)

        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)

	def writetobuffer(self,buf):

        'Writes this packet to the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_cmd.writetobuffer(buf)

        self.__field_slotnumber.writetobuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def readfrombuffer(self,buf):

        'Reads this packet from the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_cmd=UINT(**{'sizeinbytes': 1, 'constant': 0x82})

        self.__field_cmd.readfrombuffer(buf)

        self.__field_slotnumber=UINT(**{'sizeinbytes': 2})

        self.__field_slotnumber.readfrombuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def __getfield_cmd(self):

        return self.__field_cmd.getvalue()

	def __setfield_cmd(self, value):

        if isinstance(value,UINT):

            self.__field_cmd=value

        else:

            self.__field_cmd=UINT(value,**{'sizeinbytes': 1, 'constant': 0x82})

	def __delfield_cmd(self): del self.__field_cmd

	    cmd=property(__getfield_cmd, __setfield_cmd, __delfield_cmd, None)
	    def __getfield_slotnumber(self):

        return self.__field_slotnumber.getvalue()

	def __setfield_slotnumber(self, value):

        if isinstance(value,UINT):

            self.__field_slotnumber=value

        else:

            self.__field_slotnumber=UINT(value,**{'sizeinbytes': 2})

	def __delfield_slotnumber(self): del self.__field_slotnumber

	    slotnumber=property(__getfield_slotnumber, __setfield_slotnumber, __delfield_slotnumber, None)
	    def iscontainer(self):

        return True

	def containerelements(self):

        yield ('cmd', self.__field_cmd, None)

        yield ('slotnumber', self.__field_slotnumber, None)


class  pbentry (BaseProtogenClass) :
	__fields=['secret', 'group', 'previous', 'next', 'mobile', 'home', 'office', 'pager', 'fax', 'name', 'email', 'wireless', 'memo', 'ringtone', 'msgringtone', 'wallpaper']
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

        self.__field_secret.writetobuffer(buf)

        self.__field_group.writetobuffer(buf)

        self.__field_previous.writetobuffer(buf)

        self.__field_next.writetobuffer(buf)

        self.__field_mobile.writetobuffer(buf)

        self.__field_home.writetobuffer(buf)

        self.__field_office.writetobuffer(buf)

        self.__field_pager.writetobuffer(buf)

        self.__field_fax.writetobuffer(buf)

        self.__field_name.writetobuffer(buf)

        self.__field_email.writetobuffer(buf)

        self.__field_wireless.writetobuffer(buf)

        self.__field_memo.writetobuffer(buf)

        self.__field_ringtone.writetobuffer(buf)

        self.__field_msgringtone.writetobuffer(buf)

        self.__field_wallpaper.writetobuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def readfrombuffer(self,buf):

        'Reads this packet from the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_secret=UINT(**{'sizeinbytes': 1})

        self.__field_secret.readfrombuffer(buf)

        self.__field_group=UINT(**{'sizeinbytes': 1})

        self.__field_group.readfrombuffer(buf)

        self.__field_previous=UINT(**{'sizeinbytes': 2})

        self.__field_previous.readfrombuffer(buf)

        self.__field_next=UINT(**{'sizeinbytes': 2})

        self.__field_next.readfrombuffer(buf)

        self.__field_mobile=COUNTEDBUFFEREDSTRING(**{'sizeinbytes': 33})

        self.__field_mobile.readfrombuffer(buf)

        self.__field_home=COUNTEDBUFFEREDSTRING(**{'sizeinbytes': 33})

        self.__field_home.readfrombuffer(buf)

        self.__field_office=COUNTEDBUFFEREDSTRING(**{'sizeinbytes': 33})

        self.__field_office.readfrombuffer(buf)

        self.__field_pager=COUNTEDBUFFEREDSTRING(**{'sizeinbytes': 33})

        self.__field_pager.readfrombuffer(buf)

        self.__field_fax=COUNTEDBUFFEREDSTRING(**{'sizeinbytes': 33})

        self.__field_fax.readfrombuffer(buf)

        self.__field_name=STRING(**{'sizeinbytes': 17})

        self.__field_name.readfrombuffer(buf)

        self.__field_email=STRING(**{'sizeinbytes': 49})

        self.__field_email.readfrombuffer(buf)

        self.__field_wireless=STRING(**{'sizeinbytes': 49})

        self.__field_wireless.readfrombuffer(buf)

        self.__field_memo=STRING(**{'sizeinbytes': 49})

        self.__field_memo.readfrombuffer(buf)

        self.__field_ringtone=UINT(**{'sizeinbytes': 2})

        self.__field_ringtone.readfrombuffer(buf)

        self.__field_msgringtone=UINT(**{'sizeinbytes': 2})

        self.__field_msgringtone.readfrombuffer(buf)

        self.__field_wallpaper=UINT(**{'sizeinbytes': 2})

        self.__field_wallpaper.readfrombuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def __getfield_secret(self):

        return self.__field_secret.getvalue()

	def __setfield_secret(self, value):

        if isinstance(value,UINT):

            self.__field_secret=value

        else:

            self.__field_secret=UINT(value,**{'sizeinbytes': 1})

	def __delfield_secret(self): del self.__field_secret

	    secret=property(__getfield_secret, __setfield_secret, __delfield_secret, "non-zero if entry is secret/locked")
	    def __getfield_group(self):

        return self.__field_group.getvalue()

	def __setfield_group(self, value):

        if isinstance(value,UINT):

            self.__field_group=value

        else:

            self.__field_group=UINT(value,**{'sizeinbytes': 1})

	def __delfield_group(self): del self.__field_group

	    group=property(__getfield_group, __setfield_group, __delfield_group, None)
	    def __getfield_previous(self):

        return self.__field_previous.getvalue()

	def __setfield_previous(self, value):

        if isinstance(value,UINT):

            self.__field_previous=value

        else:

            self.__field_previous=UINT(value,**{'sizeinbytes': 2})

	def __delfield_previous(self): del self.__field_previous

	    previous=property(__getfield_previous, __setfield_previous, __delfield_previous, "?index number for previous entry")
	    def __getfield_next(self):

        return self.__field_next.getvalue()

	def __setfield_next(self, value):

        if isinstance(value,UINT):

            self.__field_next=value

        else:

            self.__field_next=UINT(value,**{'sizeinbytes': 2})

	def __delfield_next(self): del self.__field_next

	    next=property(__getfield_next, __setfield_next, __delfield_next, "?index number for next entry")
	    def __getfield_mobile(self):

        return self.__field_mobile.getvalue()

	def __setfield_mobile(self, value):

        if isinstance(value,COUNTEDBUFFEREDSTRING):

            self.__field_mobile=value

        else:

            self.__field_mobile=COUNTEDBUFFEREDSTRING(value,**{'sizeinbytes': 33})

	def __delfield_mobile(self): del self.__field_mobile

	    mobile=property(__getfield_mobile, __setfield_mobile, __delfield_mobile, None)
	    def __getfield_home(self):

        return self.__field_home.getvalue()

	def __setfield_home(self, value):

        if isinstance(value,COUNTEDBUFFEREDSTRING):

            self.__field_home=value

        else:

            self.__field_home=COUNTEDBUFFEREDSTRING(value,**{'sizeinbytes': 33})

	def __delfield_home(self): del self.__field_home

	    home=property(__getfield_home, __setfield_home, __delfield_home, None)
	    def __getfield_office(self):

        return self.__field_office.getvalue()

	def __setfield_office(self, value):

        if isinstance(value,COUNTEDBUFFEREDSTRING):

            self.__field_office=value

        else:

            self.__field_office=COUNTEDBUFFEREDSTRING(value,**{'sizeinbytes': 33})

	def __delfield_office(self): del self.__field_office

	    office=property(__getfield_office, __setfield_office, __delfield_office, None)
	    def __getfield_pager(self):

        return self.__field_pager.getvalue()

	def __setfield_pager(self, value):

        if isinstance(value,COUNTEDBUFFEREDSTRING):

            self.__field_pager=value

        else:

            self.__field_pager=COUNTEDBUFFEREDSTRING(value,**{'sizeinbytes': 33})

	def __delfield_pager(self): del self.__field_pager

	    pager=property(__getfield_pager, __setfield_pager, __delfield_pager, None)
	    def __getfield_fax(self):

        return self.__field_fax.getvalue()

	def __setfield_fax(self, value):

        if isinstance(value,COUNTEDBUFFEREDSTRING):

            self.__field_fax=value

        else:

            self.__field_fax=COUNTEDBUFFEREDSTRING(value,**{'sizeinbytes': 33})

	def __delfield_fax(self): del self.__field_fax

	    fax=property(__getfield_fax, __setfield_fax, __delfield_fax, None)
	    def __getfield_name(self):

        return self.__field_name.getvalue()

	def __setfield_name(self, value):

        if isinstance(value,STRING):

            self.__field_name=value

        else:

            self.__field_name=STRING(value,**{'sizeinbytes': 17})

	def __delfield_name(self): del self.__field_name

	    name=property(__getfield_name, __setfield_name, __delfield_name, None)
	    def __getfield_email(self):

        return self.__field_email.getvalue()

	def __setfield_email(self, value):

        if isinstance(value,STRING):

            self.__field_email=value

        else:

            self.__field_email=STRING(value,**{'sizeinbytes': 49})

	def __delfield_email(self): del self.__field_email

	    email=property(__getfield_email, __setfield_email, __delfield_email, None)
	    def __getfield_wireless(self):

        return self.__field_wireless.getvalue()

	def __setfield_wireless(self, value):

        if isinstance(value,STRING):

            self.__field_wireless=value

        else:

            self.__field_wireless=STRING(value,**{'sizeinbytes': 49})

	def __delfield_wireless(self): del self.__field_wireless

	    wireless=property(__getfield_wireless, __setfield_wireless, __delfield_wireless, None)
	    def __getfield_memo(self):

        return self.__field_memo.getvalue()

	def __setfield_memo(self, value):

        if isinstance(value,STRING):

            self.__field_memo=value

        else:

            self.__field_memo=STRING(value,**{'sizeinbytes': 49})

	def __delfield_memo(self): del self.__field_memo

	    memo=property(__getfield_memo, __setfield_memo, __delfield_memo, None)
	    def __getfield_ringtone(self):

        return self.__field_ringtone.getvalue()

	def __setfield_ringtone(self, value):

        if isinstance(value,UINT):

            self.__field_ringtone=value

        else:

            self.__field_ringtone=UINT(value,**{'sizeinbytes': 2})

	def __delfield_ringtone(self): del self.__field_ringtone

	    ringtone=property(__getfield_ringtone, __setfield_ringtone, __delfield_ringtone, None)
	    def __getfield_msgringtone(self):

        return self.__field_msgringtone.getvalue()

	def __setfield_msgringtone(self, value):

        if isinstance(value,UINT):

            self.__field_msgringtone=value

        else:

            self.__field_msgringtone=UINT(value,**{'sizeinbytes': 2})

	def __delfield_msgringtone(self): del self.__field_msgringtone

	    msgringtone=property(__getfield_msgringtone, __setfield_msgringtone, __delfield_msgringtone, None)
	    def __getfield_wallpaper(self):

        return self.__field_wallpaper.getvalue()

	def __setfield_wallpaper(self, value):

        if isinstance(value,UINT):

            self.__field_wallpaper=value

        else:

            self.__field_wallpaper=UINT(value,**{'sizeinbytes': 2})

	def __delfield_wallpaper(self): del self.__field_wallpaper

	    wallpaper=property(__getfield_wallpaper, __setfield_wallpaper, __delfield_wallpaper, None)
	    def iscontainer(self):

        return True

	def containerelements(self):

        yield ('secret', self.__field_secret, "non-zero if entry is secret/locked")

        yield ('group', self.__field_group, None)

        yield ('previous', self.__field_previous, "?index number for previous entry")

        yield ('next', self.__field_next, "?index number for next entry")

        yield ('mobile', self.__field_mobile, None)

        yield ('home', self.__field_home, None)

        yield ('office', self.__field_office, None)

        yield ('pager', self.__field_pager, None)

        yield ('fax', self.__field_fax, None)

        yield ('name', self.__field_name, None)

        yield ('email', self.__field_email, None)

        yield ('wireless', self.__field_wireless, None)

        yield ('memo', self.__field_memo, None)

        yield ('ringtone', self.__field_ringtone, None)

        yield ('msgringtone', self.__field_msgringtone, None)

        yield ('wallpaper', self.__field_wallpaper, None)


class  readgroupentryrequest (BaseProtogenClass) :
	__fields=['cmd', 'number']
	    def __init__(self, *args, **kwargs):

        dict={}

        dict.update(kwargs)

        super(readgroupentryrequest,self).__init__(**dict)

        if self.__class__ is readgroupentryrequest:

            self._update(args,dict)

	def getfields(self):

        return self.__fields

	def _update(self, args, kwargs):

        super(readgroupentryrequest,self)._update(args,kwargs)

        keys=kwargs.keys()

        for key in keys:

            if key in self.__fields:

                setattr(self, key, kwargs[key])

                del kwargs[key]

        if __debug__:

            self._complainaboutunusedargs(readgroupentryrequest,kwargs)

        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)

	def writetobuffer(self,buf):

        'Writes this packet to the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        try: self.__field_cmd

        except:

            self.__field_cmd=UINT(**{'sizeinbytes': 1, 'constant': 0x81})

        self.__field_cmd.writetobuffer(buf)

        self.__field_number.writetobuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def readfrombuffer(self,buf):

        'Reads this packet from the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_cmd=UINT(**{'sizeinbytes': 1, 'constant': 0x81})

        self.__field_cmd.readfrombuffer(buf)

        self.__field_number=UINT(**{'sizeinbytes': 1})

        self.__field_number.readfrombuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def __getfield_cmd(self):

        try: self.__field_cmd

        except:

            self.__field_cmd=UINT(**{'sizeinbytes': 1, 'constant': 0x81})

        return self.__field_cmd.getvalue()

	def __setfield_cmd(self, value):

        if isinstance(value,UINT):

            self.__field_cmd=value

        else:

            self.__field_cmd=UINT(value,**{'sizeinbytes': 1, 'constant': 0x81})

	def __delfield_cmd(self): del self.__field_cmd

	    cmd=property(__getfield_cmd, __setfield_cmd, __delfield_cmd, None)
	    def __getfield_number(self):

        return self.__field_number.getvalue()

	def __setfield_number(self, value):

        if isinstance(value,UINT):

            self.__field_number=value

        else:

            self.__field_number=UINT(value,**{'sizeinbytes': 1})

	def __delfield_number(self): del self.__field_number

	    number=property(__getfield_number, __setfield_number, __delfield_number, None)
	    def iscontainer(self):

        return True

	def containerelements(self):

        yield ('cmd', self.__field_cmd, None)

        yield ('number', self.__field_number, None)


class  readgroupentryresponse (BaseProtogenClass) :
	__fields=['cmd', 'number', 'anothergroupnum', 'dunno', 'name', 'nummembers']
	    def __init__(self, *args, **kwargs):

        dict={}

        dict.update(kwargs)

        super(readgroupentryresponse,self).__init__(**dict)

        if self.__class__ is readgroupentryresponse:

            self._update(args,dict)

	def getfields(self):

        return self.__fields

	def _update(self, args, kwargs):

        super(readgroupentryresponse,self)._update(args,kwargs)

        keys=kwargs.keys()

        for key in keys:

            if key in self.__fields:

                setattr(self, key, kwargs[key])

                del kwargs[key]

        if __debug__:

            self._complainaboutunusedargs(readgroupentryresponse,kwargs)

        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)

	def writetobuffer(self,buf):

        'Writes this packet to the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_cmd.writetobuffer(buf)

        self.__field_number.writetobuffer(buf)

        self.__field_anothergroupnum.writetobuffer(buf)

        self.__field_dunno.writetobuffer(buf)

        self.__field_name.writetobuffer(buf)

        self.__field_nummembers.writetobuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def readfrombuffer(self,buf):

        'Reads this packet from the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_cmd=UINT(**{'sizeinbytes': 1, 'constant': 0x81})

        self.__field_cmd.readfrombuffer(buf)

        self.__field_number=UINT(**{'sizeinbytes': 1})

        self.__field_number.readfrombuffer(buf)

        self.__field_anothergroupnum=UINT(**{'sizeinbytes': 1})

        self.__field_anothergroupnum.readfrombuffer(buf)

        self.__field_dunno=UINT(**{'sizeinbytes': 2})

        self.__field_dunno.readfrombuffer(buf)

        self.__field_name=STRING(**{'sizeinbytes': 17})

        self.__field_name.readfrombuffer(buf)

        self.__field_nummembers=UINT(**{'sizeinbytes': 2})

        self.__field_nummembers.readfrombuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def __getfield_cmd(self):

        return self.__field_cmd.getvalue()

	def __setfield_cmd(self, value):

        if isinstance(value,UINT):

            self.__field_cmd=value

        else:

            self.__field_cmd=UINT(value,**{'sizeinbytes': 1, 'constant': 0x81})

	def __delfield_cmd(self): del self.__field_cmd

	    cmd=property(__getfield_cmd, __setfield_cmd, __delfield_cmd, None)
	    def __getfield_number(self):

        return self.__field_number.getvalue()

	def __setfield_number(self, value):

        if isinstance(value,UINT):

            self.__field_number=value

        else:

            self.__field_number=UINT(value,**{'sizeinbytes': 1})

	def __delfield_number(self): del self.__field_number

	    number=property(__getfield_number, __setfield_number, __delfield_number, None)
	    def __getfield_anothergroupnum(self):

        return self.__field_anothergroupnum.getvalue()

	def __setfield_anothergroupnum(self, value):

        if isinstance(value,UINT):

            self.__field_anothergroupnum=value

        else:

            self.__field_anothergroupnum=UINT(value,**{'sizeinbytes': 1})

	def __delfield_anothergroupnum(self): del self.__field_anothergroupnum

	    anothergroupnum=property(__getfield_anothergroupnum, __setfield_anothergroupnum, __delfield_anothergroupnum, None)
	    def __getfield_dunno(self):

        return self.__field_dunno.getvalue()

	def __setfield_dunno(self, value):

        if isinstance(value,UINT):

            self.__field_dunno=value

        else:

            self.__field_dunno=UINT(value,**{'sizeinbytes': 2})

	def __delfield_dunno(self): del self.__field_dunno

	    dunno=property(__getfield_dunno, __setfield_dunno, __delfield_dunno, "first member?")
	    def __getfield_name(self):

        return self.__field_name.getvalue()

	def __setfield_name(self, value):

        if isinstance(value,STRING):

            self.__field_name=value

        else:

            self.__field_name=STRING(value,**{'sizeinbytes': 17})

	def __delfield_name(self): del self.__field_name

	    name=property(__getfield_name, __setfield_name, __delfield_name, None)
	    def __getfield_nummembers(self):

        return self.__field_nummembers.getvalue()

	def __setfield_nummembers(self, value):

        if isinstance(value,UINT):

            self.__field_nummembers=value

        else:

            self.__field_nummembers=UINT(value,**{'sizeinbytes': 2})

	def __delfield_nummembers(self): del self.__field_nummembers

	    nummembers=property(__getfield_nummembers, __setfield_nummembers, __delfield_nummembers, "how many members of the group")
	    def iscontainer(self):

        return True

	def containerelements(self):

        yield ('cmd', self.__field_cmd, None)

        yield ('number', self.__field_number, None)

        yield ('anothergroupnum', self.__field_anothergroupnum, None)

        yield ('dunno', self.__field_dunno, "first member?")

        yield ('name', self.__field_name, None)

        yield ('nummembers', self.__field_nummembers, "how many members of the group")


class  writegroupentryrequest (BaseProtogenClass) :
	__fields=['cmd', 'number', 'anothernumber', 'dunno', 'name', 'nummembers']
	    def __init__(self, *args, **kwargs):

        dict={}

        dict.update(kwargs)

        super(writegroupentryrequest,self).__init__(**dict)

        if self.__class__ is writegroupentryrequest:

            self._update(args,dict)

	def getfields(self):

        return self.__fields

	def _update(self, args, kwargs):

        super(writegroupentryrequest,self)._update(args,kwargs)

        keys=kwargs.keys()

        for key in keys:

            if key in self.__fields:

                setattr(self, key, kwargs[key])

                del kwargs[key]

        if __debug__:

            self._complainaboutunusedargs(writegroupentryrequest,kwargs)

        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)

	def writetobuffer(self,buf):

        'Writes this packet to the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        try: self.__field_cmd

        except:

            self.__field_cmd=UINT(**{'sizeinbytes': 1, 'constant': 0x80})

        self.__field_cmd.writetobuffer(buf)

        self.__field_number.writetobuffer(buf)

        self.__field_anothernumber.writetobuffer(buf)

        try: self.__field_dunno

        except:

            self.__field_dunno=UINT(**{'sizeinbytes': 2, 'constant': 0xffff})

        self.__field_dunno.writetobuffer(buf)

        self.__field_name.writetobuffer(buf)

        self.__field_nummembers.writetobuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def readfrombuffer(self,buf):

        'Reads this packet from the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_cmd=UINT(**{'sizeinbytes': 1, 'constant': 0x80})

        self.__field_cmd.readfrombuffer(buf)

        self.__field_number=UINT(**{'sizeinbytes': 1})

        self.__field_number.readfrombuffer(buf)

        self.__field_anothernumber=UINT(**{'sizeinbytes': 1})

        self.__field_anothernumber.readfrombuffer(buf)

        self.__field_dunno=UINT(**{'sizeinbytes': 2, 'constant': 0xffff})

        self.__field_dunno.readfrombuffer(buf)

        self.__field_name=STRING(**{'sizeinbytes': 17})

        self.__field_name.readfrombuffer(buf)

        self.__field_nummembers=UINT(**{'sizeinbytes': 2})

        self.__field_nummembers.readfrombuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def __getfield_cmd(self):

        try: self.__field_cmd

        except:

            self.__field_cmd=UINT(**{'sizeinbytes': 1, 'constant': 0x80})

        return self.__field_cmd.getvalue()

	def __setfield_cmd(self, value):

        if isinstance(value,UINT):

            self.__field_cmd=value

        else:

            self.__field_cmd=UINT(value,**{'sizeinbytes': 1, 'constant': 0x80})

	def __delfield_cmd(self): del self.__field_cmd

	    cmd=property(__getfield_cmd, __setfield_cmd, __delfield_cmd, None)
	    def __getfield_number(self):

        return self.__field_number.getvalue()

	def __setfield_number(self, value):

        if isinstance(value,UINT):

            self.__field_number=value

        else:

            self.__field_number=UINT(value,**{'sizeinbytes': 1})

	def __delfield_number(self): del self.__field_number

	    number=property(__getfield_number, __setfield_number, __delfield_number, None)
	    def __getfield_anothernumber(self):

        return self.__field_anothernumber.getvalue()

	def __setfield_anothernumber(self, value):

        if isinstance(value,UINT):

            self.__field_anothernumber=value

        else:

            self.__field_anothernumber=UINT(value,**{'sizeinbytes': 1})

	def __delfield_anothernumber(self): del self.__field_anothernumber

	    anothernumber=property(__getfield_anothernumber, __setfield_anothernumber, __delfield_anothernumber, "same as number")
	    def __getfield_dunno(self):

        try: self.__field_dunno

        except:

            self.__field_dunno=UINT(**{'sizeinbytes': 2, 'constant': 0xffff})

        return self.__field_dunno.getvalue()

	def __setfield_dunno(self, value):

        if isinstance(value,UINT):

            self.__field_dunno=value

        else:

            self.__field_dunno=UINT(value,**{'sizeinbytes': 2, 'constant': 0xffff})

	def __delfield_dunno(self): del self.__field_dunno

	    dunno=property(__getfield_dunno, __setfield_dunno, __delfield_dunno, "?first member of the group")
	    def __getfield_name(self):

        return self.__field_name.getvalue()

	def __setfield_name(self, value):

        if isinstance(value,STRING):

            self.__field_name=value

        else:

            self.__field_name=STRING(value,**{'sizeinbytes': 17})

	def __delfield_name(self): del self.__field_name

	    name=property(__getfield_name, __setfield_name, __delfield_name, None)
	    def __getfield_nummembers(self):

        return self.__field_nummembers.getvalue()

	def __setfield_nummembers(self, value):

        if isinstance(value,UINT):

            self.__field_nummembers=value

        else:

            self.__field_nummembers=UINT(value,**{'sizeinbytes': 2})

	def __delfield_nummembers(self): del self.__field_nummembers

	    nummembers=property(__getfield_nummembers, __setfield_nummembers, __delfield_nummembers, None)
	    def iscontainer(self):

        return True

	def containerelements(self):

        yield ('cmd', self.__field_cmd, None)

        yield ('number', self.__field_number, None)

        yield ('anothernumber', self.__field_anothernumber, "same as number")

        yield ('dunno', self.__field_dunno, "?first member of the group")

        yield ('name', self.__field_name, None)

        yield ('nummembers', self.__field_nummembers, None)


class  writegroupentryresponse (BaseProtogenClass) :
	__fields=['cmd']
	    def __init__(self, *args, **kwargs):

        dict={}

        dict.update(kwargs)

        super(writegroupentryresponse,self).__init__(**dict)

        if self.__class__ is writegroupentryresponse:

            self._update(args,dict)

	def getfields(self):

        return self.__fields

	def _update(self, args, kwargs):

        super(writegroupentryresponse,self)._update(args,kwargs)

        keys=kwargs.keys()

        for key in keys:

            if key in self.__fields:

                setattr(self, key, kwargs[key])

                del kwargs[key]

        if __debug__:

            self._complainaboutunusedargs(writegroupentryresponse,kwargs)

        if len(args):

            dict2={'sizeinbytes': 1, 'constant': 0x80}

            dict2.update(kwargs)

            kwargs=dict2

            self.__field_cmd=UINT(*args,**dict2)

	def writetobuffer(self,buf):

        'Writes this packet to the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_cmd.writetobuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def readfrombuffer(self,buf):

        'Reads this packet from the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_cmd=UINT(**{'sizeinbytes': 1, 'constant': 0x80})

        self.__field_cmd.readfrombuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def __getfield_cmd(self):

        return self.__field_cmd.getvalue()

	def __setfield_cmd(self, value):

        if isinstance(value,UINT):

            self.__field_cmd=value

        else:

            self.__field_cmd=UINT(value,**{'sizeinbytes': 1, 'constant': 0x80})

	def __delfield_cmd(self): del self.__field_cmd

	    cmd=property(__getfield_cmd, __setfield_cmd, __delfield_cmd, None)
	    def iscontainer(self):

        return True

	def containerelements(self):

        yield ('cmd', self.__field_cmd, None)


class  dunnorequest (BaseProtogenClass) :
	__fields=['cmd', 'cmd2', 'cmd3', 'which']
	    def __init__(self, *args, **kwargs):

        dict={}

        dict.update(kwargs)

        super(dunnorequest,self).__init__(**dict)

        if self.__class__ is dunnorequest:

            self._update(args,dict)

	def getfields(self):

        return self.__fields

	def _update(self, args, kwargs):

        super(dunnorequest,self)._update(args,kwargs)

        keys=kwargs.keys()

        for key in keys:

            if key in self.__fields:

                setattr(self, key, kwargs[key])

                del kwargs[key]

        if __debug__:

            self._complainaboutunusedargs(dunnorequest,kwargs)

        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)

	def writetobuffer(self,buf):

        'Writes this packet to the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        try: self.__field_cmd

        except:

            self.__field_cmd=UINT(**{'sizeinbytes': 1, 'constant': 0x26})

        self.__field_cmd.writetobuffer(buf)

        try: self.__field_cmd2

        except:

            self.__field_cmd2=UINT(**{'sizeinbytes': 1, 'constant': 0xf7})

        self.__field_cmd2.writetobuffer(buf)

        try: self.__field_cmd3

        except:

            self.__field_cmd3=UINT(**{'sizeinbytes': 1, 'constant': 0x03})

        self.__field_cmd3.writetobuffer(buf)

        self.__field_which.writetobuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def readfrombuffer(self,buf):

        'Reads this packet from the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_cmd=UINT(**{'sizeinbytes': 1, 'constant': 0x26})

        self.__field_cmd.readfrombuffer(buf)

        self.__field_cmd2=UINT(**{'sizeinbytes': 1, 'constant': 0xf7})

        self.__field_cmd2.readfrombuffer(buf)

        self.__field_cmd3=UINT(**{'sizeinbytes': 1, 'constant': 0x03})

        self.__field_cmd3.readfrombuffer(buf)

        self.__field_which=UINT(**{'sizeinbytes': 1})

        self.__field_which.readfrombuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def __getfield_cmd(self):

        try: self.__field_cmd

        except:

            self.__field_cmd=UINT(**{'sizeinbytes': 1, 'constant': 0x26})

        return self.__field_cmd.getvalue()

	def __setfield_cmd(self, value):

        if isinstance(value,UINT):

            self.__field_cmd=value

        else:

            self.__field_cmd=UINT(value,**{'sizeinbytes': 1, 'constant': 0x26})

	def __delfield_cmd(self): del self.__field_cmd

	    cmd=property(__getfield_cmd, __setfield_cmd, __delfield_cmd, None)
	    def __getfield_cmd2(self):

        try: self.__field_cmd2

        except:

            self.__field_cmd2=UINT(**{'sizeinbytes': 1, 'constant': 0xf7})

        return self.__field_cmd2.getvalue()

	def __setfield_cmd2(self, value):

        if isinstance(value,UINT):

            self.__field_cmd2=value

        else:

            self.__field_cmd2=UINT(value,**{'sizeinbytes': 1, 'constant': 0xf7})

	def __delfield_cmd2(self): del self.__field_cmd2

	    cmd2=property(__getfield_cmd2, __setfield_cmd2, __delfield_cmd2, None)
	    def __getfield_cmd3(self):

        try: self.__field_cmd3

        except:

            self.__field_cmd3=UINT(**{'sizeinbytes': 1, 'constant': 0x03})

        return self.__field_cmd3.getvalue()

	def __setfield_cmd3(self, value):

        if isinstance(value,UINT):

            self.__field_cmd3=value

        else:

            self.__field_cmd3=UINT(value,**{'sizeinbytes': 1, 'constant': 0x03})

	def __delfield_cmd3(self): del self.__field_cmd3

	    cmd3=property(__getfield_cmd3, __setfield_cmd3, __delfield_cmd3, None)
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

        yield ('cmd', self.__field_cmd, None)

        yield ('cmd2', self.__field_cmd2, None)

        yield ('cmd3', self.__field_cmd3, None)

        yield ('which', self.__field_which, None)


class  dunnoresponse (BaseProtogenClass) :
	__fields=['stuff']
	    def __init__(self, *args, **kwargs):

        dict={}

        dict.update(kwargs)

        super(dunnoresponse,self).__init__(**dict)

        if self.__class__ is dunnoresponse:

            self._update(args,dict)

	def getfields(self):

        return self.__fields

	def _update(self, args, kwargs):

        super(dunnoresponse,self)._update(args,kwargs)

        keys=kwargs.keys()

        for key in keys:

            if key in self.__fields:

                setattr(self, key, kwargs[key])

                del kwargs[key]

        if __debug__:

            self._complainaboutunusedargs(dunnoresponse,kwargs)

        if len(args):

            dict2={}

            dict2.update(kwargs)

            kwargs=dict2

            self.__field_stuff=DATA(*args,**dict2)

	def writetobuffer(self,buf):

        'Writes this packet to the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_stuff.writetobuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def readfrombuffer(self,buf):

        'Reads this packet from the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_stuff=DATA()

        self.__field_stuff.readfrombuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def __getfield_stuff(self):

        return self.__field_stuff.getvalue()

	def __setfield_stuff(self, value):

        if isinstance(value,DATA):

            self.__field_stuff=value

        else:

            self.__field_stuff=DATA(value,)

	def __delfield_stuff(self): del self.__field_stuff

	    stuff=property(__getfield_stuff, __setfield_stuff, __delfield_stuff, None)
	    def iscontainer(self):

        return True

	def containerelements(self):

        yield ('stuff', self.__field_stuff, None)


class  readlockcoderequest (BaseProtogenClass) :
	__fields=['cmd', 'cmd2', 'cmd3', 'padding']
	    def __init__(self, *args, **kwargs):

        dict={}

        dict.update(kwargs)

        super(readlockcoderequest,self).__init__(**dict)

        if self.__class__ is readlockcoderequest:

            self._update(args,dict)

	def getfields(self):

        return self.__fields

	def _update(self, args, kwargs):

        super(readlockcoderequest,self)._update(args,kwargs)

        keys=kwargs.keys()

        for key in keys:

            if key in self.__fields:

                setattr(self, key, kwargs[key])

                del kwargs[key]

        if __debug__:

            self._complainaboutunusedargs(readlockcoderequest,kwargs)

        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)

	def writetobuffer(self,buf):

        'Writes this packet to the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        try: self.__field_cmd

        except:

            self.__field_cmd=UINT(**{'sizeinbytes': 1, 'constant': 0x26})

        self.__field_cmd.writetobuffer(buf)

        try: self.__field_cmd2

        except:

            self.__field_cmd2=UINT(**{'sizeinbytes': 1, 'constant': 0x52})

        self.__field_cmd2.writetobuffer(buf)

        try: self.__field_cmd3

        except:

            self.__field_cmd3=UINT(**{'sizeinbytes': 1, 'constant': 0x00})

        self.__field_cmd3.writetobuffer(buf)

        try: self.__field_padding

        except:

            self.__field_padding=DATA(**{'sizeinbytes': 130})

        self.__field_padding.writetobuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def readfrombuffer(self,buf):

        'Reads this packet from the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_cmd=UINT(**{'sizeinbytes': 1, 'constant': 0x26})

        self.__field_cmd.readfrombuffer(buf)

        self.__field_cmd2=UINT(**{'sizeinbytes': 1, 'constant': 0x52})

        self.__field_cmd2.readfrombuffer(buf)

        self.__field_cmd3=UINT(**{'sizeinbytes': 1, 'constant': 0x00})

        self.__field_cmd3.readfrombuffer(buf)

        self.__field_padding=DATA(**{'sizeinbytes': 130})

        self.__field_padding.readfrombuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def __getfield_cmd(self):

        try: self.__field_cmd

        except:

            self.__field_cmd=UINT(**{'sizeinbytes': 1, 'constant': 0x26})

        return self.__field_cmd.getvalue()

	def __setfield_cmd(self, value):

        if isinstance(value,UINT):

            self.__field_cmd=value

        else:

            self.__field_cmd=UINT(value,**{'sizeinbytes': 1, 'constant': 0x26})

	def __delfield_cmd(self): del self.__field_cmd

	    cmd=property(__getfield_cmd, __setfield_cmd, __delfield_cmd, None)
	    def __getfield_cmd2(self):

        try: self.__field_cmd2

        except:

            self.__field_cmd2=UINT(**{'sizeinbytes': 1, 'constant': 0x52})

        return self.__field_cmd2.getvalue()

	def __setfield_cmd2(self, value):

        if isinstance(value,UINT):

            self.__field_cmd2=value

        else:

            self.__field_cmd2=UINT(value,**{'sizeinbytes': 1, 'constant': 0x52})

	def __delfield_cmd2(self): del self.__field_cmd2

	    cmd2=property(__getfield_cmd2, __setfield_cmd2, __delfield_cmd2, None)
	    def __getfield_cmd3(self):

        try: self.__field_cmd3

        except:

            self.__field_cmd3=UINT(**{'sizeinbytes': 1, 'constant': 0x00})

        return self.__field_cmd3.getvalue()

	def __setfield_cmd3(self, value):

        if isinstance(value,UINT):

            self.__field_cmd3=value

        else:

            self.__field_cmd3=UINT(value,**{'sizeinbytes': 1, 'constant': 0x00})

	def __delfield_cmd3(self): del self.__field_cmd3

	    cmd3=property(__getfield_cmd3, __setfield_cmd3, __delfield_cmd3, None)
	    def __getfield_padding(self):

        try: self.__field_padding

        except:

            self.__field_padding=DATA(**{'sizeinbytes': 130})

        return self.__field_padding.getvalue()

	def __setfield_padding(self, value):

        if isinstance(value,DATA):

            self.__field_padding=value

        else:

            self.__field_padding=DATA(value,**{'sizeinbytes': 130})

	def __delfield_padding(self): del self.__field_padding

	    padding=property(__getfield_padding, __setfield_padding, __delfield_padding, None)
	    def iscontainer(self):

        return True

	def containerelements(self):

        yield ('cmd', self.__field_cmd, None)

        yield ('cmd2', self.__field_cmd2, None)

        yield ('cmd3', self.__field_cmd3, None)

        yield ('padding', self.__field_padding, None)


class  readlockcoderesponse (BaseProtogenClass) :
	__fields=['cmd', 'cmd2', 'cmd3', 'lockcode']
	    def __init__(self, *args, **kwargs):

        dict={}

        dict.update(kwargs)

        super(readlockcoderesponse,self).__init__(**dict)

        if self.__class__ is readlockcoderesponse:

            self._update(args,dict)

	def getfields(self):

        return self.__fields

	def _update(self, args, kwargs):

        super(readlockcoderesponse,self)._update(args,kwargs)

        keys=kwargs.keys()

        for key in keys:

            if key in self.__fields:

                setattr(self, key, kwargs[key])

                del kwargs[key]

        if __debug__:

            self._complainaboutunusedargs(readlockcoderesponse,kwargs)

        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)

	def writetobuffer(self,buf):

        'Writes this packet to the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_cmd.writetobuffer(buf)

        self.__field_cmd2.writetobuffer(buf)

        self.__field_cmd3.writetobuffer(buf)

        self.__field_lockcode.writetobuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def readfrombuffer(self,buf):

        'Reads this packet from the supplied buffer'

        self._bufferstartoffset=buf.getcurrentoffset()

        self.__field_cmd=UINT(**{'sizeinbytes': 1, 'constant': 0x26})

        self.__field_cmd.readfrombuffer(buf)

        self.__field_cmd2=UINT(**{'sizeinbytes': 1, 'constant': 0x52})

        self.__field_cmd2.readfrombuffer(buf)

        self.__field_cmd3=UINT(**{'sizeinbytes': 1, 'constant': 0x00})

        self.__field_cmd3.readfrombuffer(buf)

        self.__field_lockcode=STRING()

        self.__field_lockcode.readfrombuffer(buf)

        self._bufferendoffset=buf.getcurrentoffset()

	def __getfield_cmd(self):

        return self.__field_cmd.getvalue()

	def __setfield_cmd(self, value):

        if isinstance(value,UINT):

            self.__field_cmd=value

        else:

            self.__field_cmd=UINT(value,**{'sizeinbytes': 1, 'constant': 0x26})

	def __delfield_cmd(self): del self.__field_cmd

	    cmd=property(__getfield_cmd, __setfield_cmd, __delfield_cmd, None)
	    def __getfield_cmd2(self):

        return self.__field_cmd2.getvalue()

	def __setfield_cmd2(self, value):

        if isinstance(value,UINT):

            self.__field_cmd2=value

        else:

            self.__field_cmd2=UINT(value,**{'sizeinbytes': 1, 'constant': 0x52})

	def __delfield_cmd2(self): del self.__field_cmd2

	    cmd2=property(__getfield_cmd2, __setfield_cmd2, __delfield_cmd2, None)
	    def __getfield_cmd3(self):

        return self.__field_cmd3.getvalue()

	def __setfield_cmd3(self, value):

        if isinstance(value,UINT):

            self.__field_cmd3=value

        else:

            self.__field_cmd3=UINT(value,**{'sizeinbytes': 1, 'constant': 0x00})

	def __delfield_cmd3(self): del self.__field_cmd3

	    cmd3=property(__getfield_cmd3, __setfield_cmd3, __delfield_cmd3, None)
	    def __getfield_lockcode(self):

        return self.__field_lockcode.getvalue()

	def __setfield_lockcode(self, value):

        if isinstance(value,STRING):

            self.__field_lockcode=value

        else:

            self.__field_lockcode=STRING(value,)

	def __delfield_lockcode(self): del self.__field_lockcode

	    lockcode=property(__getfield_lockcode, __setfield_lockcode, __delfield_lockcode, None)
	    def iscontainer(self):

        return True

	def containerelements(self):

        yield ('cmd', self.__field_cmd, None)

        yield ('cmd2', self.__field_cmd2, None)

        yield ('cmd3', self.__field_cmd3, None)

        yield ('lockcode', self.__field_lockcode, None)


