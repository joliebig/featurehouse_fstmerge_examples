"""Various descriptions of data specific to Sanyo SCP-8100"""
from prototypes import *
from p_sanyo import *
from p_sanyomedia import *
from p_sanyonewer import *
UINT=UINTlsb
BOOL=BOOLlsb
_NUMPBSLOTS=300
_NUMSPEEDDIALS=8
_NUMLONGNUMBERS=5
_LONGPHONENUMBERLEN=30
_NUMEVENTSLOTS=100
_NUMCALLALARMSLOTS=15
_NUMCALLHISTORY=20
_MAXNUMBERLEN=48
_MAXEMAILLEN=48
class phonenumber(BaseProtogenClass):
    __fields=['number_len', 'number']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(phonenumber,self).__init__(**dict)
        if self.__class__ is phonenumber:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(phonenumber,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(phonenumber,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_number_len
        except:
            self.__field_number_len=UINT(**{'sizeinbytes': 1, 'default': 0})
        self.__field_number_len.writetobuffer(buf)
        try: self.__field_number
        except:
            self.__field_number=STRING(**{'sizeinbytes': 49, 'default': ""})
        self.__field_number.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def readfrombuffer(self,buf):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_number_len=UINT(**{'sizeinbytes': 1, 'default': 0})
        self.__field_number_len.readfrombuffer(buf)
        self.__field_number=STRING(**{'sizeinbytes': 49, 'default': ""})
        self.__field_number.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_number_len(self):
        try: self.__field_number_len
        except:
            self.__field_number_len=UINT(**{'sizeinbytes': 1, 'default': 0})
        return self.__field_number_len.getvalue()
    def __setfield_number_len(self, value):
        if isinstance(value,UINT):
            self.__field_number_len=value
        else:
            self.__field_number_len=UINT(value,**{'sizeinbytes': 1, 'default': 0})
    def __delfield_number_len(self): del self.__field_number_len
    number_len=property(__getfield_number_len, __setfield_number_len, __delfield_number_len, None)
    def __getfield_number(self):
        try: self.__field_number
        except:
            self.__field_number=STRING(**{'sizeinbytes': 49, 'default': ""})
        return self.__field_number.getvalue()
    def __setfield_number(self, value):
        if isinstance(value,STRING):
            self.__field_number=value
        else:
            self.__field_number=STRING(value,**{'sizeinbytes': 49, 'default': ""})
    def __delfield_number(self): del self.__field_number
    number=property(__getfield_number, __setfield_number, __delfield_number, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('number_len', self.__field_number_len, None)
        yield ('number', self.__field_number, None)
class phonebookentry(BaseProtogenClass):
    __fields=['slot', 'slotdup', 'name', 'numbers', 'email_len', 'email', 'url_len', 'url', 'secret', 'name_len']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(phonebookentry,self).__init__(**dict)
        if self.__class__ is phonebookentry:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(phonebookentry,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(phonebookentry,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_slot.writetobuffer(buf)
        self.__field_slotdup.writetobuffer(buf)
        self.__field_name.writetobuffer(buf)
        try: self.__field_numbers
        except:
            self.__field_numbers=LIST(**{'length': 7, 'createdefault': True, 'elementclass': phonenumber})
        self.__field_numbers.writetobuffer(buf)
        try: self.__field_email_len
        except:
            self.__field_email_len=UINT(**{'sizeinbytes': 1})
        self.__field_email_len.writetobuffer(buf)
        try: self.__field_email
        except:
            self.__field_email=STRING(**{'sizeinbytes': 49, 'default': ""})
        self.__field_email.writetobuffer(buf)
        try: self.__field_url_len
        except:
            self.__field_url_len=UINT(**{'sizeinbytes': 1})
        self.__field_url_len.writetobuffer(buf)
        try: self.__field_url
        except:
            self.__field_url=STRING(**{'sizeinbytes': 49, 'default': ""})
        self.__field_url.writetobuffer(buf)
        try: self.__field_secret
        except:
            self.__field_secret=BOOL(**{'sizeinbytes': 1})
        self.__field_secret.writetobuffer(buf)
        self.__field_name_len.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def readfrombuffer(self,buf):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_slot=UINT(**{'sizeinbytes': 2})
        self.__field_slot.readfrombuffer(buf)
        self.__field_slotdup=UINT(**{'sizeinbytes': 2})
        self.__field_slotdup.readfrombuffer(buf)
        self.__field_name=STRING(**{'sizeinbytes': 16, 'raiseonunterminatedread': False, 'raiseontruncate': False, 'terminator': None})
        self.__field_name.readfrombuffer(buf)
        self.__field_numbers=LIST(**{'length': 7, 'createdefault': True, 'elementclass': phonenumber})
        self.__field_numbers.readfrombuffer(buf)
        self.__field_email_len=UINT(**{'sizeinbytes': 1})
        self.__field_email_len.readfrombuffer(buf)
        self.__field_email=STRING(**{'sizeinbytes': 49, 'default': ""})
        self.__field_email.readfrombuffer(buf)
        self.__field_url_len=UINT(**{'sizeinbytes': 1})
        self.__field_url_len.readfrombuffer(buf)
        self.__field_url=STRING(**{'sizeinbytes': 49, 'default': ""})
        self.__field_url.readfrombuffer(buf)
        self.__field_secret=BOOL(**{'sizeinbytes': 1})
        self.__field_secret.readfrombuffer(buf)
        self.__field_name_len=UINT(**{'sizeinbytes': 1})
        self.__field_name_len.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_slot(self):
        return self.__field_slot.getvalue()
    def __setfield_slot(self, value):
        if isinstance(value,UINT):
            self.__field_slot=value
        else:
            self.__field_slot=UINT(value,**{'sizeinbytes': 2})
    def __delfield_slot(self): del self.__field_slot
    slot=property(__getfield_slot, __setfield_slot, __delfield_slot, None)
    def __getfield_slotdup(self):
        return self.__field_slotdup.getvalue()
    def __setfield_slotdup(self, value):
        if isinstance(value,UINT):
            self.__field_slotdup=value
        else:
            self.__field_slotdup=UINT(value,**{'sizeinbytes': 2})
    def __delfield_slotdup(self): del self.__field_slotdup
    slotdup=property(__getfield_slotdup, __setfield_slotdup, __delfield_slotdup, None)
    def __getfield_name(self):
        return self.__field_name.getvalue()
    def __setfield_name(self, value):
        if isinstance(value,STRING):
            self.__field_name=value
        else:
            self.__field_name=STRING(value,**{'sizeinbytes': 16, 'raiseonunterminatedread': False, 'raiseontruncate': False, 'terminator': None})
    def __delfield_name(self): del self.__field_name
    name=property(__getfield_name, __setfield_name, __delfield_name, None)
    def __getfield_numbers(self):
        try: self.__field_numbers
        except:
            self.__field_numbers=LIST(**{'length': 7, 'createdefault': True, 'elementclass': phonenumber})
        return self.__field_numbers.getvalue()
    def __setfield_numbers(self, value):
        if isinstance(value,LIST):
            self.__field_numbers=value
        else:
            self.__field_numbers=LIST(value,**{'length': 7, 'createdefault': True, 'elementclass': phonenumber})
    def __delfield_numbers(self): del self.__field_numbers
    numbers=property(__getfield_numbers, __setfield_numbers, __delfield_numbers, None)
    def __getfield_email_len(self):
        try: self.__field_email_len
        except:
            self.__field_email_len=UINT(**{'sizeinbytes': 1})
        return self.__field_email_len.getvalue()
    def __setfield_email_len(self, value):
        if isinstance(value,UINT):
            self.__field_email_len=value
        else:
            self.__field_email_len=UINT(value,**{'sizeinbytes': 1})
    def __delfield_email_len(self): del self.__field_email_len
    email_len=property(__getfield_email_len, __setfield_email_len, __delfield_email_len, None)
    def __getfield_email(self):
        try: self.__field_email
        except:
            self.__field_email=STRING(**{'sizeinbytes': 49, 'default': ""})
        return self.__field_email.getvalue()
    def __setfield_email(self, value):
        if isinstance(value,STRING):
            self.__field_email=value
        else:
            self.__field_email=STRING(value,**{'sizeinbytes': 49, 'default': ""})
    def __delfield_email(self): del self.__field_email
    email=property(__getfield_email, __setfield_email, __delfield_email, None)
    def __getfield_url_len(self):
        try: self.__field_url_len
        except:
            self.__field_url_len=UINT(**{'sizeinbytes': 1})
        return self.__field_url_len.getvalue()
    def __setfield_url_len(self, value):
        if isinstance(value,UINT):
            self.__field_url_len=value
        else:
            self.__field_url_len=UINT(value,**{'sizeinbytes': 1})
    def __delfield_url_len(self): del self.__field_url_len
    url_len=property(__getfield_url_len, __setfield_url_len, __delfield_url_len, None)
    def __getfield_url(self):
        try: self.__field_url
        except:
            self.__field_url=STRING(**{'sizeinbytes': 49, 'default': ""})
        return self.__field_url.getvalue()
    def __setfield_url(self, value):
        if isinstance(value,STRING):
            self.__field_url=value
        else:
            self.__field_url=STRING(value,**{'sizeinbytes': 49, 'default': ""})
    def __delfield_url(self): del self.__field_url
    url=property(__getfield_url, __setfield_url, __delfield_url, None)
    def __getfield_secret(self):
        try: self.__field_secret
        except:
            self.__field_secret=BOOL(**{'sizeinbytes': 1})
        return self.__field_secret.getvalue()
    def __setfield_secret(self, value):
        if isinstance(value,BOOL):
            self.__field_secret=value
        else:
            self.__field_secret=BOOL(value,**{'sizeinbytes': 1})
    def __delfield_secret(self): del self.__field_secret
    secret=property(__getfield_secret, __setfield_secret, __delfield_secret, None)
    def __getfield_name_len(self):
        return self.__field_name_len.getvalue()
    def __setfield_name_len(self, value):
        if isinstance(value,UINT):
            self.__field_name_len=value
        else:
            self.__field_name_len=UINT(value,**{'sizeinbytes': 1})
    def __delfield_name_len(self): del self.__field_name_len
    name_len=property(__getfield_name_len, __setfield_name_len, __delfield_name_len, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('slot', self.__field_slot, None)
        yield ('slotdup', self.__field_slotdup, None)
        yield ('name', self.__field_name, None)
        yield ('numbers', self.__field_numbers, None)
        yield ('email_len', self.__field_email_len, None)
        yield ('email', self.__field_email, None)
        yield ('url_len', self.__field_url_len, None)
        yield ('url', self.__field_url, None)
        yield ('secret', self.__field_secret, None)
        yield ('name_len', self.__field_name_len, None)
class phonebookslotresponse(BaseProtogenClass):
    __fields=['header', 'entry', 'pad']
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
        self.__field_header.writetobuffer(buf)
        self.__field_entry.writetobuffer(buf)
        self.__field_pad.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def readfrombuffer(self,buf):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_header=sanyoheader()
        self.__field_header.readfrombuffer(buf)
        self.__field_entry=phonebookentry()
        self.__field_entry.readfrombuffer(buf)
        self.__field_pad=UNKNOWN()
        self.__field_pad.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_header(self):
        return self.__field_header.getvalue()
    def __setfield_header(self, value):
        if isinstance(value,sanyoheader):
            self.__field_header=value
        else:
            self.__field_header=sanyoheader(value,)
    def __delfield_header(self): del self.__field_header
    header=property(__getfield_header, __setfield_header, __delfield_header, None)
    def __getfield_entry(self):
        return self.__field_entry.getvalue()
    def __setfield_entry(self, value):
        if isinstance(value,phonebookentry):
            self.__field_entry=value
        else:
            self.__field_entry=phonebookentry(value,)
    def __delfield_entry(self): del self.__field_entry
    entry=property(__getfield_entry, __setfield_entry, __delfield_entry, None)
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
        yield ('header', self.__field_header, None)
        yield ('entry', self.__field_entry, None)
        yield ('pad', self.__field_pad, None)
class phonebookslotupdaterequest(BaseProtogenClass):
    __fields=['header', 'entry', 'pad']
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
        try: self.__field_header
        except:
            self.__field_header=sanyowriteheader(**{'packettype': 0x0c, 'command': 0x28})
        self.__field_header.writetobuffer(buf)
        self.__field_entry.writetobuffer(buf)
        try: self.__field_pad
        except:
            self.__field_pad=UNKNOWN(**{'sizeinbytes': 500})
        self.__field_pad.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def readfrombuffer(self,buf):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_header=sanyowriteheader(**{'packettype': 0x0c, 'command': 0x28})
        self.__field_header.readfrombuffer(buf)
        self.__field_entry=phonebookentry()
        self.__field_entry.readfrombuffer(buf)
        self.__field_pad=UNKNOWN(**{'sizeinbytes': 500})
        self.__field_pad.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_header(self):
        try: self.__field_header
        except:
            self.__field_header=sanyowriteheader(**{'packettype': 0x0c, 'command': 0x28})
        return self.__field_header.getvalue()
    def __setfield_header(self, value):
        if isinstance(value,sanyowriteheader):
            self.__field_header=value
        else:
            self.__field_header=sanyowriteheader(value,**{'packettype': 0x0c, 'command': 0x28})
    def __delfield_header(self): del self.__field_header
    header=property(__getfield_header, __setfield_header, __delfield_header, None)
    def __getfield_entry(self):
        return self.__field_entry.getvalue()
    def __setfield_entry(self, value):
        if isinstance(value,phonebookentry):
            self.__field_entry=value
        else:
            self.__field_entry=phonebookentry(value,)
    def __delfield_entry(self): del self.__field_entry
    entry=property(__getfield_entry, __setfield_entry, __delfield_entry, None)
    def __getfield_pad(self):
        try: self.__field_pad
        except:
            self.__field_pad=UNKNOWN(**{'sizeinbytes': 500})
        return self.__field_pad.getvalue()
    def __setfield_pad(self, value):
        if isinstance(value,UNKNOWN):
            self.__field_pad=value
        else:
            self.__field_pad=UNKNOWN(value,**{'sizeinbytes': 500})
    def __delfield_pad(self): del self.__field_pad
    pad=property(__getfield_pad, __setfield_pad, __delfield_pad, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('header', self.__field_header, None)
        yield ('entry', self.__field_entry, None)
        yield ('pad', self.__field_pad, None)
class evententry(BaseProtogenClass):
    __fields=['slot', 'flag', 'eventname', 'pad1', 'eventname_len', 'start', 'end', 'location', 'pad2', 'location_len', 'ringtone', 'alarmdiff', 'period', 'dom', 'alarm', 'serial']
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
        self.__field_flag.writetobuffer(buf)
        self.__field_eventname.writetobuffer(buf)
        try: self.__field_pad1
        except:
            self.__field_pad1=UNKNOWN(**{'sizeinbytes': 7})
        self.__field_pad1.writetobuffer(buf)
        self.__field_eventname_len.writetobuffer(buf)
        self.__field_start.writetobuffer(buf)
        self.__field_end.writetobuffer(buf)
        self.__field_location.writetobuffer(buf)
        try: self.__field_pad2
        except:
            self.__field_pad2=UNKNOWN(**{'sizeinbytes': 7})
        self.__field_pad2.writetobuffer(buf)
        self.__field_location_len.writetobuffer(buf)
        self.__field_ringtone.writetobuffer(buf)
        self.__field_alarmdiff.writetobuffer(buf)
        self.__field_period.writetobuffer(buf)
        self.__field_dom.writetobuffer(buf)
        self.__field_alarm.writetobuffer(buf)
        try: self.__field_serial
        except:
            self.__field_serial=UINT(**{'sizeinbytes': 1, 'default': 0})
        self.__field_serial.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def readfrombuffer(self,buf):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_slot=UINT(**{'sizeinbytes': 1})
        self.__field_slot.readfrombuffer(buf)
        self.__field_flag=UINT(**{'sizeinbytes': 1})
        self.__field_flag.readfrombuffer(buf)
        self.__field_eventname=STRING(**{'sizeinbytes': 14, 'raiseonunterminatedread': False, 'raiseontruncate': False, 'terminator': None})
        self.__field_eventname.readfrombuffer(buf)
        self.__field_pad1=UNKNOWN(**{'sizeinbytes': 7})
        self.__field_pad1.readfrombuffer(buf)
        self.__field_eventname_len=UINT(**{'sizeinbytes': 1})
        self.__field_eventname_len.readfrombuffer(buf)
        self.__field_start=UINT(**{'sizeinbytes': 4})
        self.__field_start.readfrombuffer(buf)
        self.__field_end=UINT(**{'sizeinbytes': 4})
        self.__field_end.readfrombuffer(buf)
        self.__field_location=STRING(**{'sizeinbytes': 14, 'raiseonunterminatedread': False, 'raiseontruncate': False, 'terminator': None})
        self.__field_location.readfrombuffer(buf)
        self.__field_pad2=UNKNOWN(**{'sizeinbytes': 7})
        self.__field_pad2.readfrombuffer(buf)
        self.__field_location_len=UINT(**{'sizeinbytes': 1})
        self.__field_location_len.readfrombuffer(buf)
        self.__field_ringtone=UINT(**{'sizeinbytes': 1})
        self.__field_ringtone.readfrombuffer(buf)
        self.__field_alarmdiff=UINT(**{'sizeinbytes': 4})
        self.__field_alarmdiff.readfrombuffer(buf)
        self.__field_period=UINT(**{'sizeinbytes': 1})
        self.__field_period.readfrombuffer(buf)
        self.__field_dom=UINT(**{'sizeinbytes': 1})
        self.__field_dom.readfrombuffer(buf)
        self.__field_alarm=UINT(**{'sizeinbytes': 4})
        self.__field_alarm.readfrombuffer(buf)
        self.__field_serial=UINT(**{'sizeinbytes': 1, 'default': 0})
        self.__field_serial.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_slot(self):
        return self.__field_slot.getvalue()
    def __setfield_slot(self, value):
        if isinstance(value,UINT):
            self.__field_slot=value
        else:
            self.__field_slot=UINT(value,**{'sizeinbytes': 1})
    def __delfield_slot(self): del self.__field_slot
    slot=property(__getfield_slot, __setfield_slot, __delfield_slot, None)
    def __getfield_flag(self):
        return self.__field_flag.getvalue()
    def __setfield_flag(self, value):
        if isinstance(value,UINT):
            self.__field_flag=value
        else:
            self.__field_flag=UINT(value,**{'sizeinbytes': 1})
    def __delfield_flag(self): del self.__field_flag
    flag=property(__getfield_flag, __setfield_flag, __delfield_flag, "0: Not used, 1: Scheduled, 2: Already Happened")
    def __getfield_eventname(self):
        return self.__field_eventname.getvalue()
    def __setfield_eventname(self, value):
        if isinstance(value,STRING):
            self.__field_eventname=value
        else:
            self.__field_eventname=STRING(value,**{'sizeinbytes': 14, 'raiseonunterminatedread': False, 'raiseontruncate': False, 'terminator': None})
    def __delfield_eventname(self): del self.__field_eventname
    eventname=property(__getfield_eventname, __setfield_eventname, __delfield_eventname, None)
    def __getfield_pad1(self):
        try: self.__field_pad1
        except:
            self.__field_pad1=UNKNOWN(**{'sizeinbytes': 7})
        return self.__field_pad1.getvalue()
    def __setfield_pad1(self, value):
        if isinstance(value,UNKNOWN):
            self.__field_pad1=value
        else:
            self.__field_pad1=UNKNOWN(value,**{'sizeinbytes': 7})
    def __delfield_pad1(self): del self.__field_pad1
    pad1=property(__getfield_pad1, __setfield_pad1, __delfield_pad1, None)
    def __getfield_eventname_len(self):
        return self.__field_eventname_len.getvalue()
    def __setfield_eventname_len(self, value):
        if isinstance(value,UINT):
            self.__field_eventname_len=value
        else:
            self.__field_eventname_len=UINT(value,**{'sizeinbytes': 1})
    def __delfield_eventname_len(self): del self.__field_eventname_len
    eventname_len=property(__getfield_eventname_len, __setfield_eventname_len, __delfield_eventname_len, None)
    def __getfield_start(self):
        return self.__field_start.getvalue()
    def __setfield_start(self, value):
        if isinstance(value,UINT):
            self.__field_start=value
        else:
            self.__field_start=UINT(value,**{'sizeinbytes': 4})
    def __delfield_start(self): del self.__field_start
    start=property(__getfield_start, __setfield_start, __delfield_start, "# seconds since Jan 1, 1980 approximately")
    def __getfield_end(self):
        return self.__field_end.getvalue()
    def __setfield_end(self, value):
        if isinstance(value,UINT):
            self.__field_end=value
        else:
            self.__field_end=UINT(value,**{'sizeinbytes': 4})
    def __delfield_end(self): del self.__field_end
    end=property(__getfield_end, __setfield_end, __delfield_end, None)
    def __getfield_location(self):
        return self.__field_location.getvalue()
    def __setfield_location(self, value):
        if isinstance(value,STRING):
            self.__field_location=value
        else:
            self.__field_location=STRING(value,**{'sizeinbytes': 14, 'raiseonunterminatedread': False, 'raiseontruncate': False, 'terminator': None})
    def __delfield_location(self): del self.__field_location
    location=property(__getfield_location, __setfield_location, __delfield_location, None)
    def __getfield_pad2(self):
        try: self.__field_pad2
        except:
            self.__field_pad2=UNKNOWN(**{'sizeinbytes': 7})
        return self.__field_pad2.getvalue()
    def __setfield_pad2(self, value):
        if isinstance(value,UNKNOWN):
            self.__field_pad2=value
        else:
            self.__field_pad2=UNKNOWN(value,**{'sizeinbytes': 7})
    def __delfield_pad2(self): del self.__field_pad2
    pad2=property(__getfield_pad2, __setfield_pad2, __delfield_pad2, None)
    def __getfield_location_len(self):
        return self.__field_location_len.getvalue()
    def __setfield_location_len(self, value):
        if isinstance(value,UINT):
            self.__field_location_len=value
        else:
            self.__field_location_len=UINT(value,**{'sizeinbytes': 1})
    def __delfield_location_len(self): del self.__field_location_len
    location_len=property(__getfield_location_len, __setfield_location_len, __delfield_location_len, None)
    def __getfield_ringtone(self):
        return self.__field_ringtone.getvalue()
    def __setfield_ringtone(self, value):
        if isinstance(value,UINT):
            self.__field_ringtone=value
        else:
            self.__field_ringtone=UINT(value,**{'sizeinbytes': 1})
    def __delfield_ringtone(self): del self.__field_ringtone
    ringtone=property(__getfield_ringtone, __setfield_ringtone, __delfield_ringtone, "0: Beep, 1: Voice, 2: Silent")
    def __getfield_alarmdiff(self):
        return self.__field_alarmdiff.getvalue()
    def __setfield_alarmdiff(self, value):
        if isinstance(value,UINT):
            self.__field_alarmdiff=value
        else:
            self.__field_alarmdiff=UINT(value,**{'sizeinbytes': 4})
    def __delfield_alarmdiff(self): del self.__field_alarmdiff
    alarmdiff=property(__getfield_alarmdiff, __setfield_alarmdiff, __delfield_alarmdiff, "Displayed alarm time")
    def __getfield_period(self):
        return self.__field_period.getvalue()
    def __setfield_period(self, value):
        if isinstance(value,UINT):
            self.__field_period=value
        else:
            self.__field_period=UINT(value,**{'sizeinbytes': 1})
    def __delfield_period(self): del self.__field_period
    period=property(__getfield_period, __setfield_period, __delfield_period, "No, Daily, Weekly, Monthly, Yearly")
    def __getfield_dom(self):
        return self.__field_dom.getvalue()
    def __setfield_dom(self, value):
        if isinstance(value,UINT):
            self.__field_dom=value
        else:
            self.__field_dom=UINT(value,**{'sizeinbytes': 1})
    def __delfield_dom(self): del self.__field_dom
    dom=property(__getfield_dom, __setfield_dom, __delfield_dom, "Day of month for the event")
    def __getfield_alarm(self):
        return self.__field_alarm.getvalue()
    def __setfield_alarm(self, value):
        if isinstance(value,UINT):
            self.__field_alarm=value
        else:
            self.__field_alarm=UINT(value,**{'sizeinbytes': 4})
    def __delfield_alarm(self): del self.__field_alarm
    alarm=property(__getfield_alarm, __setfield_alarm, __delfield_alarm, None)
    def __getfield_serial(self):
        try: self.__field_serial
        except:
            self.__field_serial=UINT(**{'sizeinbytes': 1, 'default': 0})
        return self.__field_serial.getvalue()
    def __setfield_serial(self, value):
        if isinstance(value,UINT):
            self.__field_serial=value
        else:
            self.__field_serial=UINT(value,**{'sizeinbytes': 1, 'default': 0})
    def __delfield_serial(self): del self.__field_serial
    serial=property(__getfield_serial, __setfield_serial, __delfield_serial, "Some kind of serial number")
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('slot', self.__field_slot, None)
        yield ('flag', self.__field_flag, "0: Not used, 1: Scheduled, 2: Already Happened")
        yield ('eventname', self.__field_eventname, None)
        yield ('pad1', self.__field_pad1, None)
        yield ('eventname_len', self.__field_eventname_len, None)
        yield ('start', self.__field_start, "# seconds since Jan 1, 1980 approximately")
        yield ('end', self.__field_end, None)
        yield ('location', self.__field_location, None)
        yield ('pad2', self.__field_pad2, None)
        yield ('location_len', self.__field_location_len, None)
        yield ('ringtone', self.__field_ringtone, "0: Beep, 1: Voice, 2: Silent")
        yield ('alarmdiff', self.__field_alarmdiff, "Displayed alarm time")
        yield ('period', self.__field_period, "No, Daily, Weekly, Monthly, Yearly")
        yield ('dom', self.__field_dom, "Day of month for the event")
        yield ('alarm', self.__field_alarm, None)
        yield ('serial', self.__field_serial, "Some kind of serial number")
class eventresponse(BaseProtogenClass):
    __fields=['header', 'entry', 'pad']
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
        self.__field_header.writetobuffer(buf)
        self.__field_entry.writetobuffer(buf)
        self.__field_pad.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def readfrombuffer(self,buf):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_header=sanyoheader()
        self.__field_header.readfrombuffer(buf)
        self.__field_entry=evententry()
        self.__field_entry.readfrombuffer(buf)
        self.__field_pad=UNKNOWN()
        self.__field_pad.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_header(self):
        return self.__field_header.getvalue()
    def __setfield_header(self, value):
        if isinstance(value,sanyoheader):
            self.__field_header=value
        else:
            self.__field_header=sanyoheader(value,)
    def __delfield_header(self): del self.__field_header
    header=property(__getfield_header, __setfield_header, __delfield_header, None)
    def __getfield_entry(self):
        return self.__field_entry.getvalue()
    def __setfield_entry(self, value):
        if isinstance(value,evententry):
            self.__field_entry=value
        else:
            self.__field_entry=evententry(value,)
    def __delfield_entry(self): del self.__field_entry
    entry=property(__getfield_entry, __setfield_entry, __delfield_entry, None)
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
        yield ('header', self.__field_header, None)
        yield ('entry', self.__field_entry, None)
        yield ('pad', self.__field_pad, None)
class eventupdaterequest(BaseProtogenClass):
    __fields=['header', 'entry', 'pad']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(eventupdaterequest,self).__init__(**dict)
        if self.__class__ is eventupdaterequest:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(eventupdaterequest,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(eventupdaterequest,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_header
        except:
            self.__field_header=sanyoheader(**{'packettype': 0x0c, 'command':0x23})
        self.__field_header.writetobuffer(buf)
        self.__field_entry.writetobuffer(buf)
        try: self.__field_pad
        except:
            self.__field_pad=UNKNOWN(**{'sizeinbytes': 400})
        self.__field_pad.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def readfrombuffer(self,buf):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_header=sanyoheader(**{'packettype': 0x0c, 'command':0x23})
        self.__field_header.readfrombuffer(buf)
        self.__field_entry=evententry()
        self.__field_entry.readfrombuffer(buf)
        self.__field_pad=UNKNOWN(**{'sizeinbytes': 400})
        self.__field_pad.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_header(self):
        try: self.__field_header
        except:
            self.__field_header=sanyoheader(**{'packettype': 0x0c, 'command':0x23})
        return self.__field_header.getvalue()
    def __setfield_header(self, value):
        if isinstance(value,sanyoheader):
            self.__field_header=value
        else:
            self.__field_header=sanyoheader(value,**{'packettype': 0x0c, 'command':0x23})
    def __delfield_header(self): del self.__field_header
    header=property(__getfield_header, __setfield_header, __delfield_header, None)
    def __getfield_entry(self):
        return self.__field_entry.getvalue()
    def __setfield_entry(self, value):
        if isinstance(value,evententry):
            self.__field_entry=value
        else:
            self.__field_entry=evententry(value,)
    def __delfield_entry(self): del self.__field_entry
    entry=property(__getfield_entry, __setfield_entry, __delfield_entry, None)
    def __getfield_pad(self):
        try: self.__field_pad
        except:
            self.__field_pad=UNKNOWN(**{'sizeinbytes': 400})
        return self.__field_pad.getvalue()
    def __setfield_pad(self, value):
        if isinstance(value,UNKNOWN):
            self.__field_pad=value
        else:
            self.__field_pad=UNKNOWN(value,**{'sizeinbytes': 400})
    def __delfield_pad(self): del self.__field_pad
    pad=property(__getfield_pad, __setfield_pad, __delfield_pad, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('header', self.__field_header, None)
        yield ('entry', self.__field_entry, None)
        yield ('pad', self.__field_pad, None)
class callalarmentry(BaseProtogenClass):
    __fields=['ringtone', 'slot', 'flag', 'dunno1', 'phonenum', 'phonenum_len', 'date', 'period', 'dom', 'datedup', 'name', 'pad1', 'name_len', 'phonenumbertype', 'phonenumberslot', 'serial']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(callalarmentry,self).__init__(**dict)
        if self.__class__ is callalarmentry:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(callalarmentry,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(callalarmentry,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
        if getattr(self, '__field_ringtone', None) is None:
            self.__field_ringtone=UINT(**{'constant': 0})
    def writetobuffer(self,buf):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_slot.writetobuffer(buf)
        self.__field_flag.writetobuffer(buf)
        try: self.__field_dunno1
        except:
            self.__field_dunno1=UINT(**{'sizeinbytes': 1, 'default': 0})
        self.__field_dunno1.writetobuffer(buf)
        self.__field_phonenum.writetobuffer(buf)
        self.__field_phonenum_len.writetobuffer(buf)
        self.__field_date.writetobuffer(buf)
        self.__field_period.writetobuffer(buf)
        self.__field_dom.writetobuffer(buf)
        self.__field_datedup.writetobuffer(buf)
        self.__field_name.writetobuffer(buf)
        try: self.__field_pad1
        except:
            self.__field_pad1=UNKNOWN(**{'sizeinbytes': 1})
        self.__field_pad1.writetobuffer(buf)
        self.__field_name_len.writetobuffer(buf)
        self.__field_phonenumbertype.writetobuffer(buf)
        self.__field_phonenumberslot.writetobuffer(buf)
        try: self.__field_serial
        except:
            self.__field_serial=UINT(**{'sizeinbytes': 1, 'default': 0})
        self.__field_serial.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def readfrombuffer(self,buf):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_slot=UINT(**{'sizeinbytes': 1})
        self.__field_slot.readfrombuffer(buf)
        self.__field_flag=UINT(**{'sizeinbytes': 1})
        self.__field_flag.readfrombuffer(buf)
        self.__field_dunno1=UINT(**{'sizeinbytes': 1, 'default': 0})
        self.__field_dunno1.readfrombuffer(buf)
        self.__field_phonenum=STRING(**{'sizeinbytes': 49, 'raiseonunterminatedread': False})
        self.__field_phonenum.readfrombuffer(buf)
        self.__field_phonenum_len=UINT(**{'sizeinbytes': 1})
        self.__field_phonenum_len.readfrombuffer(buf)
        self.__field_date=UINT(**{'sizeinbytes': 4})
        self.__field_date.readfrombuffer(buf)
        self.__field_period=UINT(**{'sizeinbytes': 1})
        self.__field_period.readfrombuffer(buf)
        self.__field_dom=UINT(**{'sizeinbytes': 1})
        self.__field_dom.readfrombuffer(buf)
        self.__field_datedup=UINT(**{'sizeinbytes': 4})
        self.__field_datedup.readfrombuffer(buf)
        self.__field_name=STRING(**{'sizeinbytes': 16, 'raiseonunterminatedread': False, 'raiseontruncate': False, 'terminator': None})
        self.__field_name.readfrombuffer(buf)
        self.__field_pad1=UNKNOWN(**{'sizeinbytes': 1})
        self.__field_pad1.readfrombuffer(buf)
        self.__field_name_len=UINT(**{'sizeinbytes': 1})
        self.__field_name_len.readfrombuffer(buf)
        self.__field_phonenumbertype=UINT(**{'sizeinbytes': 1})
        self.__field_phonenumbertype.readfrombuffer(buf)
        self.__field_phonenumberslot=UINT(**{'sizeinbytes': 2})
        self.__field_phonenumberslot.readfrombuffer(buf)
        self.__field_serial=UINT(**{'sizeinbytes': 1, 'default': 0})
        self.__field_serial.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_ringtone(self):
        return self.__field_ringtone.getvalue()
    def __setfield_ringtone(self, value):
        if isinstance(value,UINT):
            self.__field_ringtone=value
        else:
            self.__field_ringtone=UINT(value,**{'constant': 0})
    def __delfield_ringtone(self): del self.__field_ringtone
    ringtone=property(__getfield_ringtone, __setfield_ringtone, __delfield_ringtone, None)
    def __getfield_slot(self):
        return self.__field_slot.getvalue()
    def __setfield_slot(self, value):
        if isinstance(value,UINT):
            self.__field_slot=value
        else:
            self.__field_slot=UINT(value,**{'sizeinbytes': 1})
    def __delfield_slot(self): del self.__field_slot
    slot=property(__getfield_slot, __setfield_slot, __delfield_slot, None)
    def __getfield_flag(self):
        return self.__field_flag.getvalue()
    def __setfield_flag(self, value):
        if isinstance(value,UINT):
            self.__field_flag=value
        else:
            self.__field_flag=UINT(value,**{'sizeinbytes': 1})
    def __delfield_flag(self): del self.__field_flag
    flag=property(__getfield_flag, __setfield_flag, __delfield_flag, "0: Not used, 1: Scheduled, 2: Already Happened")
    def __getfield_dunno1(self):
        try: self.__field_dunno1
        except:
            self.__field_dunno1=UINT(**{'sizeinbytes': 1, 'default': 0})
        return self.__field_dunno1.getvalue()
    def __setfield_dunno1(self, value):
        if isinstance(value,UINT):
            self.__field_dunno1=value
        else:
            self.__field_dunno1=UINT(value,**{'sizeinbytes': 1, 'default': 0})
    def __delfield_dunno1(self): del self.__field_dunno1
    dunno1=property(__getfield_dunno1, __setfield_dunno1, __delfield_dunno1, "Related to Snooze?")
    def __getfield_phonenum(self):
        return self.__field_phonenum.getvalue()
    def __setfield_phonenum(self, value):
        if isinstance(value,STRING):
            self.__field_phonenum=value
        else:
            self.__field_phonenum=STRING(value,**{'sizeinbytes': 49, 'raiseonunterminatedread': False})
    def __delfield_phonenum(self): del self.__field_phonenum
    phonenum=property(__getfield_phonenum, __setfield_phonenum, __delfield_phonenum, None)
    def __getfield_phonenum_len(self):
        return self.__field_phonenum_len.getvalue()
    def __setfield_phonenum_len(self, value):
        if isinstance(value,UINT):
            self.__field_phonenum_len=value
        else:
            self.__field_phonenum_len=UINT(value,**{'sizeinbytes': 1})
    def __delfield_phonenum_len(self): del self.__field_phonenum_len
    phonenum_len=property(__getfield_phonenum_len, __setfield_phonenum_len, __delfield_phonenum_len, None)
    def __getfield_date(self):
        return self.__field_date.getvalue()
    def __setfield_date(self, value):
        if isinstance(value,UINT):
            self.__field_date=value
        else:
            self.__field_date=UINT(value,**{'sizeinbytes': 4})
    def __delfield_date(self): del self.__field_date
    date=property(__getfield_date, __setfield_date, __delfield_date, "# seconds since Jan 1, 1980 approximately")
    def __getfield_period(self):
        return self.__field_period.getvalue()
    def __setfield_period(self, value):
        if isinstance(value,UINT):
            self.__field_period=value
        else:
            self.__field_period=UINT(value,**{'sizeinbytes': 1})
    def __delfield_period(self): del self.__field_period
    period=property(__getfield_period, __setfield_period, __delfield_period, "No, Daily, Weekly, Monthly, Yearly")
    def __getfield_dom(self):
        return self.__field_dom.getvalue()
    def __setfield_dom(self, value):
        if isinstance(value,UINT):
            self.__field_dom=value
        else:
            self.__field_dom=UINT(value,**{'sizeinbytes': 1})
    def __delfield_dom(self): del self.__field_dom
    dom=property(__getfield_dom, __setfield_dom, __delfield_dom, "Day of month for the event")
    def __getfield_datedup(self):
        return self.__field_datedup.getvalue()
    def __setfield_datedup(self, value):
        if isinstance(value,UINT):
            self.__field_datedup=value
        else:
            self.__field_datedup=UINT(value,**{'sizeinbytes': 4})
    def __delfield_datedup(self): del self.__field_datedup
    datedup=property(__getfield_datedup, __setfield_datedup, __delfield_datedup, "Copy of the date.  Always the same???")
    def __getfield_name(self):
        return self.__field_name.getvalue()
    def __setfield_name(self, value):
        if isinstance(value,STRING):
            self.__field_name=value
        else:
            self.__field_name=STRING(value,**{'sizeinbytes': 16, 'raiseonunterminatedread': False, 'raiseontruncate': False, 'terminator': None})
    def __delfield_name(self): del self.__field_name
    name=property(__getfield_name, __setfield_name, __delfield_name, None)
    def __getfield_pad1(self):
        try: self.__field_pad1
        except:
            self.__field_pad1=UNKNOWN(**{'sizeinbytes': 1})
        return self.__field_pad1.getvalue()
    def __setfield_pad1(self, value):
        if isinstance(value,UNKNOWN):
            self.__field_pad1=value
        else:
            self.__field_pad1=UNKNOWN(value,**{'sizeinbytes': 1})
    def __delfield_pad1(self): del self.__field_pad1
    pad1=property(__getfield_pad1, __setfield_pad1, __delfield_pad1, None)
    def __getfield_name_len(self):
        return self.__field_name_len.getvalue()
    def __setfield_name_len(self, value):
        if isinstance(value,UINT):
            self.__field_name_len=value
        else:
            self.__field_name_len=UINT(value,**{'sizeinbytes': 1})
    def __delfield_name_len(self): del self.__field_name_len
    name_len=property(__getfield_name_len, __setfield_name_len, __delfield_name_len, None)
    def __getfield_phonenumbertype(self):
        return self.__field_phonenumbertype.getvalue()
    def __setfield_phonenumbertype(self, value):
        if isinstance(value,UINT):
            self.__field_phonenumbertype=value
        else:
            self.__field_phonenumbertype=UINT(value,**{'sizeinbytes': 1})
    def __delfield_phonenumbertype(self): del self.__field_phonenumbertype
    phonenumbertype=property(__getfield_phonenumbertype, __setfield_phonenumbertype, __delfield_phonenumbertype, "1: Home, 2: Work, ...")
    def __getfield_phonenumberslot(self):
        return self.__field_phonenumberslot.getvalue()
    def __setfield_phonenumberslot(self, value):
        if isinstance(value,UINT):
            self.__field_phonenumberslot=value
        else:
            self.__field_phonenumberslot=UINT(value,**{'sizeinbytes': 2})
    def __delfield_phonenumberslot(self): del self.__field_phonenumberslot
    phonenumberslot=property(__getfield_phonenumberslot, __setfield_phonenumberslot, __delfield_phonenumberslot, None)
    def __getfield_serial(self):
        try: self.__field_serial
        except:
            self.__field_serial=UINT(**{'sizeinbytes': 1, 'default': 0})
        return self.__field_serial.getvalue()
    def __setfield_serial(self, value):
        if isinstance(value,UINT):
            self.__field_serial=value
        else:
            self.__field_serial=UINT(value,**{'sizeinbytes': 1, 'default': 0})
    def __delfield_serial(self): del self.__field_serial
    serial=property(__getfield_serial, __setfield_serial, __delfield_serial, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('ringtone', self.__field_ringtone, None)
        yield ('slot', self.__field_slot, None)
        yield ('flag', self.__field_flag, "0: Not used, 1: Scheduled, 2: Already Happened")
        yield ('dunno1', self.__field_dunno1, "Related to Snooze?")
        yield ('phonenum', self.__field_phonenum, None)
        yield ('phonenum_len', self.__field_phonenum_len, None)
        yield ('date', self.__field_date, "# seconds since Jan 1, 1980 approximately")
        yield ('period', self.__field_period, "No, Daily, Weekly, Monthly, Yearly")
        yield ('dom', self.__field_dom, "Day of month for the event")
        yield ('datedup', self.__field_datedup, "Copy of the date.  Always the same???")
        yield ('name', self.__field_name, None)
        yield ('pad1', self.__field_pad1, None)
        yield ('name_len', self.__field_name_len, None)
        yield ('phonenumbertype', self.__field_phonenumbertype, "1: Home, 2: Work, ...")
        yield ('phonenumberslot', self.__field_phonenumberslot, None)
        yield ('serial', self.__field_serial, None)
class callalarmresponse(BaseProtogenClass):
    __fields=['header', 'entry', 'pad']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(callalarmresponse,self).__init__(**dict)
        if self.__class__ is callalarmresponse:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(callalarmresponse,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(callalarmresponse,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_header.writetobuffer(buf)
        self.__field_entry.writetobuffer(buf)
        self.__field_pad.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def readfrombuffer(self,buf):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_header=sanyoheader()
        self.__field_header.readfrombuffer(buf)
        self.__field_entry=callalarmentry()
        self.__field_entry.readfrombuffer(buf)
        self.__field_pad=UNKNOWN()
        self.__field_pad.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_header(self):
        return self.__field_header.getvalue()
    def __setfield_header(self, value):
        if isinstance(value,sanyoheader):
            self.__field_header=value
        else:
            self.__field_header=sanyoheader(value,)
    def __delfield_header(self): del self.__field_header
    header=property(__getfield_header, __setfield_header, __delfield_header, None)
    def __getfield_entry(self):
        return self.__field_entry.getvalue()
    def __setfield_entry(self, value):
        if isinstance(value,callalarmentry):
            self.__field_entry=value
        else:
            self.__field_entry=callalarmentry(value,)
    def __delfield_entry(self): del self.__field_entry
    entry=property(__getfield_entry, __setfield_entry, __delfield_entry, None)
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
        yield ('header', self.__field_header, None)
        yield ('entry', self.__field_entry, None)
        yield ('pad', self.__field_pad, None)
class callalarmupdaterequest(BaseProtogenClass):
    __fields=['header', 'entry', 'pad']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(callalarmupdaterequest,self).__init__(**dict)
        if self.__class__ is callalarmupdaterequest:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(callalarmupdaterequest,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(callalarmupdaterequest,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_header
        except:
            self.__field_header=sanyoheader(**{'packettype': 0x0c, 'command':0x24})
        self.__field_header.writetobuffer(buf)
        self.__field_entry.writetobuffer(buf)
        try: self.__field_pad
        except:
            self.__field_pad=UNKNOWN(**{'sizeinbytes': 400})
        self.__field_pad.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def readfrombuffer(self,buf):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_header=sanyoheader(**{'packettype': 0x0c, 'command':0x24})
        self.__field_header.readfrombuffer(buf)
        self.__field_entry=callalarmentry()
        self.__field_entry.readfrombuffer(buf)
        self.__field_pad=UNKNOWN(**{'sizeinbytes': 400})
        self.__field_pad.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_header(self):
        try: self.__field_header
        except:
            self.__field_header=sanyoheader(**{'packettype': 0x0c, 'command':0x24})
        return self.__field_header.getvalue()
    def __setfield_header(self, value):
        if isinstance(value,sanyoheader):
            self.__field_header=value
        else:
            self.__field_header=sanyoheader(value,**{'packettype': 0x0c, 'command':0x24})
    def __delfield_header(self): del self.__field_header
    header=property(__getfield_header, __setfield_header, __delfield_header, None)
    def __getfield_entry(self):
        return self.__field_entry.getvalue()
    def __setfield_entry(self, value):
        if isinstance(value,callalarmentry):
            self.__field_entry=value
        else:
            self.__field_entry=callalarmentry(value,)
    def __delfield_entry(self): del self.__field_entry
    entry=property(__getfield_entry, __setfield_entry, __delfield_entry, None)
    def __getfield_pad(self):
        try: self.__field_pad
        except:
            self.__field_pad=UNKNOWN(**{'sizeinbytes': 400})
        return self.__field_pad.getvalue()
    def __setfield_pad(self, value):
        if isinstance(value,UNKNOWN):
            self.__field_pad=value
        else:
            self.__field_pad=UNKNOWN(value,**{'sizeinbytes': 400})
    def __delfield_pad(self): del self.__field_pad
    pad=property(__getfield_pad, __setfield_pad, __delfield_pad, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('header', self.__field_header, None)
        yield ('entry', self.__field_entry, None)
        yield ('pad', self.__field_pad, None)
