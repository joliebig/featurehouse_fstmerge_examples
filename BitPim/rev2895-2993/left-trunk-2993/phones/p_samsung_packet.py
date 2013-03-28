"""Various descriptions of data specific to Sanyo phones"""
from prototypes import *
UINT=UINTlsb
BOOL=BOOLlsb
NUMCALENDAREVENTS=70
NUMTODOENTRIES=9
NUMMEMOENTRIES=9
DEFAULT_RINGTONE=20
DEFAULT_WALLPAPER=20
class phonenumber(BaseProtogenClass):
    __fields=['number', 'secret']
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
        try: self.__field_number
        except:
            self.__field_number=CSVSTRING(**{'quotechar': None, 'default': ""})
        self.__field_number.writetobuffer(buf)
        try: self.__field_secret
        except:
            self.__field_secret=CSVINT(**{'default': 0})
        self.__field_secret.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def readfrombuffer(self,buf):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_number=CSVSTRING(**{'quotechar': None, 'default': ""})
        self.__field_number.readfrombuffer(buf)
        self.__field_secret=CSVINT(**{'default': 0})
        self.__field_secret.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_number(self):
        try: self.__field_number
        except:
            self.__field_number=CSVSTRING(**{'quotechar': None, 'default': ""})
        return self.__field_number.getvalue()
    def __setfield_number(self, value):
        if isinstance(value,CSVSTRING):
            self.__field_number=value
        else:
            self.__field_number=CSVSTRING(value,**{'quotechar': None, 'default': ""})
    def __delfield_number(self): del self.__field_number
    number=property(__getfield_number, __setfield_number, __delfield_number, None)
    def __getfield_secret(self):
        try: self.__field_secret
        except:
            self.__field_secret=CSVINT(**{'default': 0})
        return self.__field_secret.getvalue()
    def __setfield_secret(self, value):
        if isinstance(value,CSVINT):
            self.__field_secret=value
        else:
            self.__field_secret=CSVINT(value,**{'default': 0})
    def __delfield_secret(self): del self.__field_secret
    secret=property(__getfield_secret, __setfield_secret, __delfield_secret, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('number', self.__field_number, None)
        yield ('secret', self.__field_secret, None)
class phonebookslotrequest(BaseProtogenClass):
    __fields=['command', 'slot']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(phonebookslotrequest,self).__init__(**dict)
        if self.__class__ is phonebookslotrequest:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(phonebookslotrequest,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(phonebookslotrequest,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_command
        except:
            self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PBOKR='})
        self.__field_command.writetobuffer(buf)
        try: self.__field_slot
        except:
            self.__field_slot=CSVINT(**{'terminator': None})
        self.__field_slot.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def readfrombuffer(self,buf):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PBOKR='})
        self.__field_command.readfrombuffer(buf)
        self.__field_slot=CSVINT(**{'terminator': None})
        self.__field_slot.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_command(self):
        try: self.__field_command
        except:
            self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PBOKR='})
        return self.__field_command.getvalue()
    def __setfield_command(self, value):
        if isinstance(value,CSVSTRING):
            self.__field_command=value
        else:
            self.__field_command=CSVSTRING(value,**{'quotechar': None, 'terminator': None, 'default': '#PBOKR='})
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
    slot=property(__getfield_slot, __setfield_slot, __delfield_slot, "Internal Slot")
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('command', self.__field_command, None)
        yield ('slot', self.__field_slot, "Internal Slot")
class phonebooksloterase(BaseProtogenClass):
    __fields=['command', 'slot']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(phonebooksloterase,self).__init__(**dict)
        if self.__class__ is phonebooksloterase:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(phonebooksloterase,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(phonebooksloterase,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_command
        except:
            self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PBOKW='})
        self.__field_command.writetobuffer(buf)
        try: self.__field_slot
        except:
            self.__field_slot=CSVINT(**{'terminator': None})
        self.__field_slot.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def readfrombuffer(self,buf):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PBOKW='})
        self.__field_command.readfrombuffer(buf)
        self.__field_slot=CSVINT(**{'terminator': None})
        self.__field_slot.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_command(self):
        try: self.__field_command
        except:
            self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PBOKW='})
        return self.__field_command.getvalue()
    def __setfield_command(self, value):
        if isinstance(value,CSVSTRING):
            self.__field_command=value
        else:
            self.__field_command=CSVSTRING(value,**{'quotechar': None, 'terminator': None, 'default': '#PBOKW='})
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
    slot=property(__getfield_slot, __setfield_slot, __delfield_slot, "Internal Slot")
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('command', self.__field_command, None)
        yield ('slot', self.__field_slot, "Internal Slot")
class phonebookslotupdateresponse(BaseProtogenClass):
    __fields=['pad']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(phonebookslotupdateresponse,self).__init__(**dict)
        if self.__class__ is phonebookslotupdateresponse:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(phonebookslotupdateresponse,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(phonebookslotupdateresponse,kwargs)
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
class groupnamerequest(BaseProtogenClass):
    __fields=['command', 'gid']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(groupnamerequest,self).__init__(**dict)
        if self.__class__ is groupnamerequest:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(groupnamerequest,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(groupnamerequest,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_command
        except:
            self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PBGRR='})
        self.__field_command.writetobuffer(buf)
        try: self.__field_gid
        except:
            self.__field_gid=CSVINT(**{'terminator': None})
        self.__field_gid.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def readfrombuffer(self,buf):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PBGRR='})
        self.__field_command.readfrombuffer(buf)
        self.__field_gid=CSVINT(**{'terminator': None})
        self.__field_gid.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_command(self):
        try: self.__field_command
        except:
            self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PBGRR='})
        return self.__field_command.getvalue()
    def __setfield_command(self, value):
        if isinstance(value,CSVSTRING):
            self.__field_command=value
        else:
            self.__field_command=CSVSTRING(value,**{'quotechar': None, 'terminator': None, 'default': '#PBGRR='})
    def __delfield_command(self): del self.__field_command
    command=property(__getfield_command, __setfield_command, __delfield_command, None)
    def __getfield_gid(self):
        try: self.__field_gid
        except:
            self.__field_gid=CSVINT(**{'terminator': None})
        return self.__field_gid.getvalue()
    def __setfield_gid(self, value):
        if isinstance(value,CSVINT):
            self.__field_gid=value
        else:
            self.__field_gid=CSVINT(value,**{'terminator': None})
    def __delfield_gid(self): del self.__field_gid
    gid=property(__getfield_gid, __setfield_gid, __delfield_gid, "Group #")
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('command', self.__field_command, None)
        yield ('gid', self.__field_gid, "Group #")
class groupnamesetrequest(BaseProtogenClass):
    __fields=['command', 'gid', 'groupname', 'ringtone']
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
    def writetobuffer(self,buf):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_command
        except:
            self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PBGRW='})
        self.__field_command.writetobuffer(buf)
        try: self.__field_gid
        except:
            self.__field_gid=CSVINT()
        self.__field_gid.writetobuffer(buf)
        try: self.__field_groupname
        except:
            self.__field_groupname=CSVSTRING()
        self.__field_groupname.writetobuffer(buf)
        try: self.__field_ringtone
        except:
            self.__field_ringtone=CSVINT(**{'terminator': None, 'default': 0})
        self.__field_ringtone.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def readfrombuffer(self,buf):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PBGRW='})
        self.__field_command.readfrombuffer(buf)
        self.__field_gid=CSVINT()
        self.__field_gid.readfrombuffer(buf)
        self.__field_groupname=CSVSTRING()
        self.__field_groupname.readfrombuffer(buf)
        self.__field_ringtone=CSVINT(**{'terminator': None, 'default': 0})
        self.__field_ringtone.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_command(self):
        try: self.__field_command
        except:
            self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PBGRW='})
        return self.__field_command.getvalue()
    def __setfield_command(self, value):
        if isinstance(value,CSVSTRING):
            self.__field_command=value
        else:
            self.__field_command=CSVSTRING(value,**{'quotechar': None, 'terminator': None, 'default': '#PBGRW='})
    def __delfield_command(self): del self.__field_command
    command=property(__getfield_command, __setfield_command, __delfield_command, None)
    def __getfield_gid(self):
        try: self.__field_gid
        except:
            self.__field_gid=CSVINT()
        return self.__field_gid.getvalue()
    def __setfield_gid(self, value):
        if isinstance(value,CSVINT):
            self.__field_gid=value
        else:
            self.__field_gid=CSVINT(value,)
    def __delfield_gid(self): del self.__field_gid
    gid=property(__getfield_gid, __setfield_gid, __delfield_gid, "Group #")
    def __getfield_groupname(self):
        try: self.__field_groupname
        except:
            self.__field_groupname=CSVSTRING()
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
            self.__field_ringtone=CSVINT(**{'terminator': None, 'default': 0})
        return self.__field_ringtone.getvalue()
    def __setfield_ringtone(self, value):
        if isinstance(value,CSVINT):
            self.__field_ringtone=value
        else:
            self.__field_ringtone=CSVINT(value,**{'terminator': None, 'default': 0})
    def __delfield_ringtone(self): del self.__field_ringtone
    ringtone=property(__getfield_ringtone, __setfield_ringtone, __delfield_ringtone, "Ringtone assignment")
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('command', self.__field_command, None)
        yield ('gid', self.__field_gid, "Group #")
        yield ('groupname', self.__field_groupname, None)
        yield ('ringtone', self.__field_ringtone, "Ringtone assignment")
class groupnamesetrequest(BaseProtogenClass):
    __fields=['command', 'gid', 'groupname', 'ringtone']
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
    def writetobuffer(self,buf):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_command
        except:
            self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PBGRW='})
        self.__field_command.writetobuffer(buf)
        try: self.__field_gid
        except:
            self.__field_gid=CSVINT()
        self.__field_gid.writetobuffer(buf)
        try: self.__field_groupname
        except:
            self.__field_groupname=CSVSTRING()
        self.__field_groupname.writetobuffer(buf)
        try: self.__field_ringtone
        except:
            self.__field_ringtone=CSVINT(**{'terminator': None, 'default': 0})
        self.__field_ringtone.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def readfrombuffer(self,buf):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PBGRW='})
        self.__field_command.readfrombuffer(buf)
        self.__field_gid=CSVINT()
        self.__field_gid.readfrombuffer(buf)
        self.__field_groupname=CSVSTRING()
        self.__field_groupname.readfrombuffer(buf)
        self.__field_ringtone=CSVINT(**{'terminator': None, 'default': 0})
        self.__field_ringtone.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_command(self):
        try: self.__field_command
        except:
            self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PBGRW='})
        return self.__field_command.getvalue()
    def __setfield_command(self, value):
        if isinstance(value,CSVSTRING):
            self.__field_command=value
        else:
            self.__field_command=CSVSTRING(value,**{'quotechar': None, 'terminator': None, 'default': '#PBGRW='})
    def __delfield_command(self): del self.__field_command
    command=property(__getfield_command, __setfield_command, __delfield_command, None)
    def __getfield_gid(self):
        try: self.__field_gid
        except:
            self.__field_gid=CSVINT()
        return self.__field_gid.getvalue()
    def __setfield_gid(self, value):
        if isinstance(value,CSVINT):
            self.__field_gid=value
        else:
            self.__field_gid=CSVINT(value,)
    def __delfield_gid(self): del self.__field_gid
    gid=property(__getfield_gid, __setfield_gid, __delfield_gid, "Group #")
    def __getfield_groupname(self):
        try: self.__field_groupname
        except:
            self.__field_groupname=CSVSTRING()
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
            self.__field_ringtone=CSVINT(**{'terminator': None, 'default': 0})
        return self.__field_ringtone.getvalue()
    def __setfield_ringtone(self, value):
        if isinstance(value,CSVINT):
            self.__field_ringtone=value
        else:
            self.__field_ringtone=CSVINT(value,**{'terminator': None, 'default': 0})
    def __delfield_ringtone(self): del self.__field_ringtone
    ringtone=property(__getfield_ringtone, __setfield_ringtone, __delfield_ringtone, "Ringtone assignment")
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('command', self.__field_command, None)
        yield ('gid', self.__field_gid, "Group #")
        yield ('groupname', self.__field_groupname, None)
        yield ('ringtone', self.__field_ringtone, "Ringtone assignment")
class eventrequest(BaseProtogenClass):
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
class eventresponse(BaseProtogenClass):
    __fields=['command', 'slot', 'start', 'end', 'timestamp', 'alarm', 'dunno', 'eventname']
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
        self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': ord(' '), 'constant': '#PISHR:'})
        self.__field_command.readfrombuffer(buf)
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
    def __getfield_command(self):
        return self.__field_command.getvalue()
    def __setfield_command(self, value):
        if isinstance(value,CSVSTRING):
            self.__field_command=value
        else:
            self.__field_command=CSVSTRING(value,**{'quotechar': None, 'terminator': ord(' '), 'constant': '#PISHR:'})
    def __delfield_command(self): del self.__field_command
    command=property(__getfield_command, __setfield_command, __delfield_command, None)
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
    alarm=property(__getfield_alarm, __setfield_alarm, __delfield_alarm, "0: 10 minutes, 1: 30 minutes, 2: 60 minutes, 3: No Alarm, 4: On Time")
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
        yield ('command', self.__field_command, None)
        yield ('slot', self.__field_slot, None)
        yield ('start', self.__field_start, None)
        yield ('end', self.__field_end, None)
        yield ('timestamp', self.__field_timestamp, None)
        yield ('alarm', self.__field_alarm, "0: 10 minutes, 1: 30 minutes, 2: 60 minutes, 3: No Alarm, 4: On Time")
        yield ('dunno', self.__field_dunno, None)
        yield ('eventname', self.__field_eventname, None)
class eventupdaterequest(BaseProtogenClass):
    __fields=['command', 'slot', 'start', 'end', 'timestamp', 'alarm', 'eventname']
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
        try: self.__field_command
        except:
            self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PISHW='})
        self.__field_command.writetobuffer(buf)
        self.__field_slot.writetobuffer(buf)
        self.__field_start.writetobuffer(buf)
        self.__field_end.writetobuffer(buf)
        self.__field_timestamp.writetobuffer(buf)
        self.__field_alarm.writetobuffer(buf)
        self.__field_eventname.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def readfrombuffer(self,buf):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PISHW='})
        self.__field_command.readfrombuffer(buf)
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
        self.__field_eventname=CSVSTRING(**{'terminator': None})
        self.__field_eventname.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_command(self):
        try: self.__field_command
        except:
            self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PISHW='})
        return self.__field_command.getvalue()
    def __setfield_command(self, value):
        if isinstance(value,CSVSTRING):
            self.__field_command=value
        else:
            self.__field_command=CSVSTRING(value,**{'quotechar': None, 'terminator': None, 'default': '#PISHW='})
    def __delfield_command(self): del self.__field_command
    command=property(__getfield_command, __setfield_command, __delfield_command, None)
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
    alarm=property(__getfield_alarm, __setfield_alarm, __delfield_alarm, "0: 10 minutes, 1: 30 minutes, 2: 60 minutes, 3: No Alarm, 4: On Time")
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
        yield ('command', self.__field_command, None)
        yield ('slot', self.__field_slot, None)
        yield ('start', self.__field_start, None)
        yield ('end', self.__field_end, None)
        yield ('timestamp', self.__field_timestamp, None)
        yield ('alarm', self.__field_alarm, "0: 10 minutes, 1: 30 minutes, 2: 60 minutes, 3: No Alarm, 4: On Time")
        yield ('eventname', self.__field_eventname, None)
class eventsloterase(BaseProtogenClass):
    __fields=['command', 'slot']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(eventsloterase,self).__init__(**dict)
        if self.__class__ is eventsloterase:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(eventsloterase,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(eventsloterase,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_command
        except:
            self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PISHW='})
        self.__field_command.writetobuffer(buf)
        try: self.__field_slot
        except:
            self.__field_slot=CSVINT(**{'terminator': None})
        self.__field_slot.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def readfrombuffer(self,buf):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PISHW='})
        self.__field_command.readfrombuffer(buf)
        self.__field_slot=CSVINT(**{'terminator': None})
        self.__field_slot.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_command(self):
        try: self.__field_command
        except:
            self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PISHW='})
        return self.__field_command.getvalue()
    def __setfield_command(self, value):
        if isinstance(value,CSVSTRING):
            self.__field_command=value
        else:
            self.__field_command=CSVSTRING(value,**{'quotechar': None, 'terminator': None, 'default': '#PISHW='})
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
class eventupdateresponse(BaseProtogenClass):
    __fields=['pad']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(eventupdateresponse,self).__init__(**dict)
        if self.__class__ is eventupdateresponse:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(eventupdateresponse,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(eventupdateresponse,kwargs)
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
class todorequest(BaseProtogenClass):
    __fields=['command', 'slot']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(todorequest,self).__init__(**dict)
        if self.__class__ is todorequest:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(todorequest,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(todorequest,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_command
        except:
            self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PITDR='})
        self.__field_command.writetobuffer(buf)
        try: self.__field_slot
        except:
            self.__field_slot=CSVINT(**{'terminator': None})
        self.__field_slot.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def readfrombuffer(self,buf):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PITDR='})
        self.__field_command.readfrombuffer(buf)
        self.__field_slot=CSVINT(**{'terminator': None})
        self.__field_slot.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_command(self):
        try: self.__field_command
        except:
            self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PITDR='})
        return self.__field_command.getvalue()
    def __setfield_command(self, value):
        if isinstance(value,CSVSTRING):
            self.__field_command=value
        else:
            self.__field_command=CSVSTRING(value,**{'quotechar': None, 'terminator': None, 'default': '#PITDR='})
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
class todoresponse(BaseProtogenClass):
    __fields=['command', 'slot', 'priority', 'duedate', 'timestamp', 'status', 'subject']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(todoresponse,self).__init__(**dict)
        if self.__class__ is todoresponse:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(todoresponse,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(todoresponse,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_command.writetobuffer(buf)
        self.__field_slot.writetobuffer(buf)
        self.__field_priority.writetobuffer(buf)
        self.__field_duedate.writetobuffer(buf)
        self.__field_timestamp.writetobuffer(buf)
        self.__field_status.writetobuffer(buf)
        self.__field_subject.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def readfrombuffer(self,buf):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': ord(' '), 'default': '#PITDR:'})
        self.__field_command.readfrombuffer(buf)
        self.__field_slot=CSVINT()
        self.__field_slot.readfrombuffer(buf)
        self.__field_priority=CSVINT()
        self.__field_priority.readfrombuffer(buf)
        self.__field_duedate=CSVTIME()
        self.__field_duedate.readfrombuffer(buf)
        self.__field_timestamp=CSVTIME()
        self.__field_timestamp.readfrombuffer(buf)
        self.__field_status=CSVSTRING(**{'quotechar': None})
        self.__field_status.readfrombuffer(buf)
        self.__field_subject=CSVSTRING(**{'terminator': None})
        self.__field_subject.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_command(self):
        return self.__field_command.getvalue()
    def __setfield_command(self, value):
        if isinstance(value,CSVSTRING):
            self.__field_command=value
        else:
            self.__field_command=CSVSTRING(value,**{'quotechar': None, 'terminator': ord(' '), 'default': '#PITDR:'})
    def __delfield_command(self): del self.__field_command
    command=property(__getfield_command, __setfield_command, __delfield_command, None)
    def __getfield_slot(self):
        return self.__field_slot.getvalue()
    def __setfield_slot(self, value):
        if isinstance(value,CSVINT):
            self.__field_slot=value
        else:
            self.__field_slot=CSVINT(value,)
    def __delfield_slot(self): del self.__field_slot
    slot=property(__getfield_slot, __setfield_slot, __delfield_slot, None)
    def __getfield_priority(self):
        return self.__field_priority.getvalue()
    def __setfield_priority(self, value):
        if isinstance(value,CSVINT):
            self.__field_priority=value
        else:
            self.__field_priority=CSVINT(value,)
    def __delfield_priority(self): del self.__field_priority
    priority=property(__getfield_priority, __setfield_priority, __delfield_priority, None)
    def __getfield_duedate(self):
        return self.__field_duedate.getvalue()
    def __setfield_duedate(self, value):
        if isinstance(value,CSVTIME):
            self.__field_duedate=value
        else:
            self.__field_duedate=CSVTIME(value,)
    def __delfield_duedate(self): del self.__field_duedate
    duedate=property(__getfield_duedate, __setfield_duedate, __delfield_duedate, None)
    def __getfield_timestamp(self):
        return self.__field_timestamp.getvalue()
    def __setfield_timestamp(self, value):
        if isinstance(value,CSVTIME):
            self.__field_timestamp=value
        else:
            self.__field_timestamp=CSVTIME(value,)
    def __delfield_timestamp(self): del self.__field_timestamp
    timestamp=property(__getfield_timestamp, __setfield_timestamp, __delfield_timestamp, None)
    def __getfield_status(self):
        return self.__field_status.getvalue()
    def __setfield_status(self, value):
        if isinstance(value,CSVSTRING):
            self.__field_status=value
        else:
            self.__field_status=CSVSTRING(value,**{'quotechar': None})
    def __delfield_status(self): del self.__field_status
    status=property(__getfield_status, __setfield_status, __delfield_status, None)
    def __getfield_subject(self):
        return self.__field_subject.getvalue()
    def __setfield_subject(self, value):
        if isinstance(value,CSVSTRING):
            self.__field_subject=value
        else:
            self.__field_subject=CSVSTRING(value,**{'terminator': None})
    def __delfield_subject(self): del self.__field_subject
    subject=property(__getfield_subject, __setfield_subject, __delfield_subject, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('command', self.__field_command, None)
        yield ('slot', self.__field_slot, None)
        yield ('priority', self.__field_priority, None)
        yield ('duedate', self.__field_duedate, None)
        yield ('timestamp', self.__field_timestamp, None)
        yield ('status', self.__field_status, None)
        yield ('subject', self.__field_subject, None)
class todoupdaterequest(BaseProtogenClass):
    __fields=['command', 'slot', 'priority', 'duedate', 'timestamp', 'subject']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(todoupdaterequest,self).__init__(**dict)
        if self.__class__ is todoupdaterequest:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(todoupdaterequest,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(todoupdaterequest,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_command
        except:
            self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PITDW='})
        self.__field_command.writetobuffer(buf)
        self.__field_slot.writetobuffer(buf)
        self.__field_priority.writetobuffer(buf)
        self.__field_duedate.writetobuffer(buf)
        self.__field_timestamp.writetobuffer(buf)
        self.__field_subject.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def readfrombuffer(self,buf):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PITDW='})
        self.__field_command.readfrombuffer(buf)
        self.__field_slot=CSVINT()
        self.__field_slot.readfrombuffer(buf)
        self.__field_priority=CSVINT()
        self.__field_priority.readfrombuffer(buf)
        self.__field_duedate=CSVTIME()
        self.__field_duedate.readfrombuffer(buf)
        self.__field_timestamp=CSVTIME()
        self.__field_timestamp.readfrombuffer(buf)
        self.__field_subject=CSVSTRING(**{'terminator': None})
        self.__field_subject.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_command(self):
        try: self.__field_command
        except:
            self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PITDW='})
        return self.__field_command.getvalue()
    def __setfield_command(self, value):
        if isinstance(value,CSVSTRING):
            self.__field_command=value
        else:
            self.__field_command=CSVSTRING(value,**{'quotechar': None, 'terminator': None, 'default': '#PITDW='})
    def __delfield_command(self): del self.__field_command
    command=property(__getfield_command, __setfield_command, __delfield_command, None)
    def __getfield_slot(self):
        return self.__field_slot.getvalue()
    def __setfield_slot(self, value):
        if isinstance(value,CSVINT):
            self.__field_slot=value
        else:
            self.__field_slot=CSVINT(value,)
    def __delfield_slot(self): del self.__field_slot
    slot=property(__getfield_slot, __setfield_slot, __delfield_slot, None)
    def __getfield_priority(self):
        return self.__field_priority.getvalue()
    def __setfield_priority(self, value):
        if isinstance(value,CSVINT):
            self.__field_priority=value
        else:
            self.__field_priority=CSVINT(value,)
    def __delfield_priority(self): del self.__field_priority
    priority=property(__getfield_priority, __setfield_priority, __delfield_priority, None)
    def __getfield_duedate(self):
        return self.__field_duedate.getvalue()
    def __setfield_duedate(self, value):
        if isinstance(value,CSVTIME):
            self.__field_duedate=value
        else:
            self.__field_duedate=CSVTIME(value,)
    def __delfield_duedate(self): del self.__field_duedate
    duedate=property(__getfield_duedate, __setfield_duedate, __delfield_duedate, None)
    def __getfield_timestamp(self):
        return self.__field_timestamp.getvalue()
    def __setfield_timestamp(self, value):
        if isinstance(value,CSVTIME):
            self.__field_timestamp=value
        else:
            self.__field_timestamp=CSVTIME(value,)
    def __delfield_timestamp(self): del self.__field_timestamp
    timestamp=property(__getfield_timestamp, __setfield_timestamp, __delfield_timestamp, None)
    def __getfield_subject(self):
        return self.__field_subject.getvalue()
    def __setfield_subject(self, value):
        if isinstance(value,CSVSTRING):
            self.__field_subject=value
        else:
            self.__field_subject=CSVSTRING(value,**{'terminator': None})
    def __delfield_subject(self): del self.__field_subject
    subject=property(__getfield_subject, __setfield_subject, __delfield_subject, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('command', self.__field_command, None)
        yield ('slot', self.__field_slot, None)
        yield ('priority', self.__field_priority, None)
        yield ('duedate', self.__field_duedate, None)
        yield ('timestamp', self.__field_timestamp, None)
        yield ('subject', self.__field_subject, None)
class todoerase(BaseProtogenClass):
    __fields=['command', 'slot']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(todoerase,self).__init__(**dict)
        if self.__class__ is todoerase:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(todoerase,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(todoerase,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_command
        except:
            self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PITDW='})
        self.__field_command.writetobuffer(buf)
        self.__field_slot.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def readfrombuffer(self,buf):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PITDW='})
        self.__field_command.readfrombuffer(buf)
        self.__field_slot=CSVINT(**{'terminator': None})
        self.__field_slot.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_command(self):
        try: self.__field_command
        except:
            self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PITDW='})
        return self.__field_command.getvalue()
    def __setfield_command(self, value):
        if isinstance(value,CSVSTRING):
            self.__field_command=value
        else:
            self.__field_command=CSVSTRING(value,**{'quotechar': None, 'terminator': None, 'default': '#PITDW='})
    def __delfield_command(self): del self.__field_command
    command=property(__getfield_command, __setfield_command, __delfield_command, None)
    def __getfield_slot(self):
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
class todoupdateresponse(BaseProtogenClass):
    __fields=['pad']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(todoupdateresponse,self).__init__(**dict)
        if self.__class__ is todoupdateresponse:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(todoupdateresponse,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(todoupdateresponse,kwargs)
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
class memorequest(BaseProtogenClass):
    __fields=['command', 'slot']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(memorequest,self).__init__(**dict)
        if self.__class__ is memorequest:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(memorequest,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(memorequest,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_command
        except:
            self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PIMMR='})
        self.__field_command.writetobuffer(buf)
        try: self.__field_slot
        except:
            self.__field_slot=CSVINT(**{'terminator': None})
        self.__field_slot.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def readfrombuffer(self,buf):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PIMMR='})
        self.__field_command.readfrombuffer(buf)
        self.__field_slot=CSVINT(**{'terminator': None})
        self.__field_slot.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_command(self):
        try: self.__field_command
        except:
            self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PIMMR='})
        return self.__field_command.getvalue()
    def __setfield_command(self, value):
        if isinstance(value,CSVSTRING):
            self.__field_command=value
        else:
            self.__field_command=CSVSTRING(value,**{'quotechar': None, 'terminator': None, 'default': '#PIMMR='})
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
class memoresponse(BaseProtogenClass):
    __fields=['command', 'slot', 'timestamp', 'status', 'text']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(memoresponse,self).__init__(**dict)
        if self.__class__ is memoresponse:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(memoresponse,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(memoresponse,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_command.writetobuffer(buf)
        self.__field_slot.writetobuffer(buf)
        self.__field_timestamp.writetobuffer(buf)
        self.__field_status.writetobuffer(buf)
        self.__field_text.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def readfrombuffer(self,buf):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': ord(' '), 'default': '#PIMMR:'})
        self.__field_command.readfrombuffer(buf)
        self.__field_slot=CSVINT()
        self.__field_slot.readfrombuffer(buf)
        self.__field_timestamp=CSVTIME()
        self.__field_timestamp.readfrombuffer(buf)
        self.__field_status=CSVSTRING(**{'quotechar': None})
        self.__field_status.readfrombuffer(buf)
        self.__field_text=CSVSTRING(**{'terminator': None})
        self.__field_text.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_command(self):
        return self.__field_command.getvalue()
    def __setfield_command(self, value):
        if isinstance(value,CSVSTRING):
            self.__field_command=value
        else:
            self.__field_command=CSVSTRING(value,**{'quotechar': None, 'terminator': ord(' '), 'default': '#PIMMR:'})
    def __delfield_command(self): del self.__field_command
    command=property(__getfield_command, __setfield_command, __delfield_command, None)
    def __getfield_slot(self):
        return self.__field_slot.getvalue()
    def __setfield_slot(self, value):
        if isinstance(value,CSVINT):
            self.__field_slot=value
        else:
            self.__field_slot=CSVINT(value,)
    def __delfield_slot(self): del self.__field_slot
    slot=property(__getfield_slot, __setfield_slot, __delfield_slot, None)
    def __getfield_timestamp(self):
        return self.__field_timestamp.getvalue()
    def __setfield_timestamp(self, value):
        if isinstance(value,CSVTIME):
            self.__field_timestamp=value
        else:
            self.__field_timestamp=CSVTIME(value,)
    def __delfield_timestamp(self): del self.__field_timestamp
    timestamp=property(__getfield_timestamp, __setfield_timestamp, __delfield_timestamp, None)
    def __getfield_status(self):
        return self.__field_status.getvalue()
    def __setfield_status(self, value):
        if isinstance(value,CSVSTRING):
            self.__field_status=value
        else:
            self.__field_status=CSVSTRING(value,**{'quotechar': None})
    def __delfield_status(self): del self.__field_status
    status=property(__getfield_status, __setfield_status, __delfield_status, None)
    def __getfield_text(self):
        return self.__field_text.getvalue()
    def __setfield_text(self, value):
        if isinstance(value,CSVSTRING):
            self.__field_text=value
        else:
            self.__field_text=CSVSTRING(value,**{'terminator': None})
    def __delfield_text(self): del self.__field_text
    text=property(__getfield_text, __setfield_text, __delfield_text, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('command', self.__field_command, None)
        yield ('slot', self.__field_slot, None)
        yield ('timestamp', self.__field_timestamp, None)
        yield ('status', self.__field_status, None)
        yield ('text', self.__field_text, None)
class memoupdaterequest(BaseProtogenClass):
    __fields=['command', 'slot', 'timestamp', 'text']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(memoupdaterequest,self).__init__(**dict)
        if self.__class__ is memoupdaterequest:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(memoupdaterequest,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(memoupdaterequest,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_command
        except:
            self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PIMMW='})
        self.__field_command.writetobuffer(buf)
        self.__field_slot.writetobuffer(buf)
        self.__field_timestamp.writetobuffer(buf)
        self.__field_text.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def readfrombuffer(self,buf):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PIMMW='})
        self.__field_command.readfrombuffer(buf)
        self.__field_slot=CSVINT()
        self.__field_slot.readfrombuffer(buf)
        self.__field_timestamp=CSVTIME()
        self.__field_timestamp.readfrombuffer(buf)
        self.__field_text=CSVSTRING(**{'terminator': None})
        self.__field_text.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_command(self):
        try: self.__field_command
        except:
            self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PIMMW='})
        return self.__field_command.getvalue()
    def __setfield_command(self, value):
        if isinstance(value,CSVSTRING):
            self.__field_command=value
        else:
            self.__field_command=CSVSTRING(value,**{'quotechar': None, 'terminator': None, 'default': '#PIMMW='})
    def __delfield_command(self): del self.__field_command
    command=property(__getfield_command, __setfield_command, __delfield_command, None)
    def __getfield_slot(self):
        return self.__field_slot.getvalue()
    def __setfield_slot(self, value):
        if isinstance(value,CSVINT):
            self.__field_slot=value
        else:
            self.__field_slot=CSVINT(value,)
    def __delfield_slot(self): del self.__field_slot
    slot=property(__getfield_slot, __setfield_slot, __delfield_slot, None)
    def __getfield_timestamp(self):
        return self.__field_timestamp.getvalue()
    def __setfield_timestamp(self, value):
        if isinstance(value,CSVTIME):
            self.__field_timestamp=value
        else:
            self.__field_timestamp=CSVTIME(value,)
    def __delfield_timestamp(self): del self.__field_timestamp
    timestamp=property(__getfield_timestamp, __setfield_timestamp, __delfield_timestamp, None)
    def __getfield_text(self):
        return self.__field_text.getvalue()
    def __setfield_text(self, value):
        if isinstance(value,CSVSTRING):
            self.__field_text=value
        else:
            self.__field_text=CSVSTRING(value,**{'terminator': None})
    def __delfield_text(self): del self.__field_text
    text=property(__getfield_text, __setfield_text, __delfield_text, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('command', self.__field_command, None)
        yield ('slot', self.__field_slot, None)
        yield ('timestamp', self.__field_timestamp, None)
        yield ('text', self.__field_text, None)
class memoerase(BaseProtogenClass):
    __fields=['command', 'slot']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(memoerase,self).__init__(**dict)
        if self.__class__ is memoerase:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(memoerase,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(memoerase,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_command
        except:
            self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PIMMW='})
        self.__field_command.writetobuffer(buf)
        self.__field_slot.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def readfrombuffer(self,buf):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PIMMW='})
        self.__field_command.readfrombuffer(buf)
        self.__field_slot=CSVINT(**{'terminator': None})
        self.__field_slot.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_command(self):
        try: self.__field_command
        except:
            self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '#PIMMW='})
        return self.__field_command.getvalue()
    def __setfield_command(self, value):
        if isinstance(value,CSVSTRING):
            self.__field_command=value
        else:
            self.__field_command=CSVSTRING(value,**{'quotechar': None, 'terminator': None, 'default': '#PIMMW='})
    def __delfield_command(self): del self.__field_command
    command=property(__getfield_command, __setfield_command, __delfield_command, None)
    def __getfield_slot(self):
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
class memoupdateresponse(BaseProtogenClass):
    __fields=['pad']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(memoupdateresponse,self).__init__(**dict)
        if self.__class__ is memoupdateresponse:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(memoupdateresponse,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(memoupdateresponse,kwargs)
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
class esnrequest(BaseProtogenClass):
    __fields=['command']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(esnrequest,self).__init__(**dict)
        if self.__class__ is esnrequest:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(esnrequest,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(esnrequest,kwargs)
        if len(args):
            dict2={'quotechar': None, 'terminator': None, 'default': '+GSN'}
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_command=CSVSTRING(*args,**dict2)
    def writetobuffer(self,buf):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_command
        except:
            self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '+GSN'})
        self.__field_command.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def readfrombuffer(self,buf):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '+GSN'})
        self.__field_command.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_command(self):
        try: self.__field_command
        except:
            self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': None, 'default': '+GSN'})
        return self.__field_command.getvalue()
    def __setfield_command(self, value):
        if isinstance(value,CSVSTRING):
            self.__field_command=value
        else:
            self.__field_command=CSVSTRING(value,**{'quotechar': None, 'terminator': None, 'default': '+GSN'})
    def __delfield_command(self): del self.__field_command
    command=property(__getfield_command, __setfield_command, __delfield_command, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('command', self.__field_command, None)
class esnresponse(BaseProtogenClass):
    __fields=['command', 'esn']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(esnresponse,self).__init__(**dict)
        if self.__class__ is esnresponse:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(esnresponse,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(esnresponse,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_command.writetobuffer(buf)
        self.__field_esn.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def readfrombuffer(self,buf):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_command=CSVSTRING(**{'quotechar': None, 'terminator': ord(' '), 'default': '+GSN'})
        self.__field_command.readfrombuffer(buf)
        self.__field_esn=CSVSTRING(**{'quotechar': None, 'terminator': None})
        self.__field_esn.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_command(self):
        return self.__field_command.getvalue()
    def __setfield_command(self, value):
        if isinstance(value,CSVSTRING):
            self.__field_command=value
        else:
            self.__field_command=CSVSTRING(value,**{'quotechar': None, 'terminator': ord(' '), 'default': '+GSN'})
    def __delfield_command(self): del self.__field_command
    command=property(__getfield_command, __setfield_command, __delfield_command, None)
    def __getfield_esn(self):
        return self.__field_esn.getvalue()
    def __setfield_esn(self, value):
        if isinstance(value,CSVSTRING):
            self.__field_esn=value
        else:
            self.__field_esn=CSVSTRING(value,**{'quotechar': None, 'terminator': None})
    def __delfield_esn(self): del self.__field_esn
    esn=property(__getfield_esn, __setfield_esn, __delfield_esn, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('command', self.__field_command, None)
        yield ('esn', self.__field_esn, None)
