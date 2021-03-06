"""Various descriptions of data specific to LG VX5200"""
from common import PhoneBookBusyException
from prototypes import *
from p_lg import *
from p_lgvx8100 import *
UINT=UINTlsb
BOOL=BOOLlsb
BREW_FILE_SYSTEM=0
PHONE_ENCODING='iso-8859-1'
class indexentry(BaseProtogenClass):
    __fields=['index', 'type', 'filename', 'icon', 'date', 'dunno']
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
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('index', self.__field_index, None)
        yield ('type', self.__field_type, None)
        yield ('filename', self.__field_filename, "includes full pathname")
        yield ('icon', self.__field_icon, None)
        yield ('date', self.__field_date, "i think this is bitfield of the date")
        yield ('dunno', self.__field_dunno, None)
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
            self.__field_msg=LIST(**{'elementclass': _gen_p_lgvx5200_87, 'length': 219})
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
        self.__field_msg=LIST(**{'elementclass': _gen_p_lgvx5200_87, 'length': 219})
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
            self.__field_msg=LIST(**{'elementclass': _gen_p_lgvx5200_87, 'length': 219})
        return self.__field_msg.getvalue()
    def __setfield_msg(self, value):
        if isinstance(value,LIST):
            self.__field_msg=value
        else:
            self.__field_msg=LIST(value,**{'elementclass': _gen_p_lgvx5200_87, 'length': 219})
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
class _gen_p_lgvx5200_87(BaseProtogenClass):
    'Anonymous inner class'
    __fields=['byte']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(_gen_p_lgvx5200_87,self).__init__(**dict)
        if self.__class__ is _gen_p_lgvx5200_87:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(_gen_p_lgvx5200_87,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(_gen_p_lgvx5200_87,kwargs)
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
        self.__field_unknown1=DATA(**{'sizeinbytes': 33})
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
            self.__field_unknown1=DATA(value,**{'sizeinbytes': 33})
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
    __fields=['index', 'unknown1', 'locked', 'timesent', 'unknown2', 'GPStime', 'subject', 'unknown4', 'num_msg_elements', 'messages', 'unknown1', 'priority', 'unknown5', 'callback', 'recipients', 'pad']
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
        self.__field_unknown1.writetobuffer(buf)
        self.__field_priority.writetobuffer(buf)
        self.__field_unknown5.writetobuffer(buf)
        self.__field_callback.writetobuffer(buf)
        try: self.__field_recipients
        except:
            self.__field_recipients=LIST(**{'elementclass': recipient_record,'length': 9})
        self.__field_recipients.writetobuffer(buf)
        self.__field_pad.writetobuffer(buf)
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
        self.__field_unknown4=UNKNOWN(**{'sizeinbytes': 1})
        self.__field_unknown4.readfrombuffer(buf)
        self.__field_num_msg_elements=UINT(**{'sizeinbytes': 1})
        self.__field_num_msg_elements.readfrombuffer(buf)
        self.__field_messages=LIST(**{'elementclass': msg_record, 'length': 7})
        self.__field_messages.readfrombuffer(buf)
        self.__field_unknown1=UNKNOWN(**{'sizeinbytes': 15})
        self.__field_unknown1.readfrombuffer(buf)
        self.__field_priority=UINT(**{'sizeinbytes': 1})
        self.__field_priority.readfrombuffer(buf)
        self.__field_unknown5=UNKNOWN(**{'sizeinbytes': 1})
        self.__field_unknown5.readfrombuffer(buf)
        self.__field_callback=USTRING(**{'sizeinbytes': 35})
        self.__field_callback.readfrombuffer(buf)
        self.__field_recipients=LIST(**{'elementclass': recipient_record,'length': 9})
        self.__field_recipients.readfrombuffer(buf)
        self.__field_pad=UNKNOWN()
        self.__field_pad.readfrombuffer(buf)
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
        if isinstance(value,UNKNOWN):
            self.__field_unknown4=value
        else:
            self.__field_unknown4=UNKNOWN(value,**{'sizeinbytes': 1})
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
    def __getfield_unknown1(self):
        return self.__field_unknown1.getvalue()
    def __setfield_unknown1(self, value):
        if isinstance(value,UNKNOWN):
            self.__field_unknown1=value
        else:
            self.__field_unknown1=UNKNOWN(value,**{'sizeinbytes': 15})
    def __delfield_unknown1(self): del self.__field_unknown1
    unknown1=property(__getfield_unknown1, __setfield_unknown1, __delfield_unknown1, None)
    def __getfield_priority(self):
        return self.__field_priority.getvalue()
    def __setfield_priority(self, value):
        if isinstance(value,UINT):
            self.__field_priority=value
        else:
            self.__field_priority=UINT(value,**{'sizeinbytes': 1})
    def __delfield_priority(self): del self.__field_priority
    priority=property(__getfield_priority, __setfield_priority, __delfield_priority, None)
    def __getfield_unknown5(self):
        return self.__field_unknown5.getvalue()
    def __setfield_unknown5(self, value):
        if isinstance(value,UNKNOWN):
            self.__field_unknown5=value
        else:
            self.__field_unknown5=UNKNOWN(value,**{'sizeinbytes': 1})
    def __delfield_unknown5(self): del self.__field_unknown5
    unknown5=property(__getfield_unknown5, __setfield_unknown5, __delfield_unknown5, None)
    def __getfield_callback(self):
        return self.__field_callback.getvalue()
    def __setfield_callback(self, value):
        if isinstance(value,USTRING):
            self.__field_callback=value
        else:
            self.__field_callback=USTRING(value,**{'sizeinbytes': 35})
    def __delfield_callback(self): del self.__field_callback
    callback=property(__getfield_callback, __setfield_callback, __delfield_callback, None)
    def __getfield_recipients(self):
        try: self.__field_recipients
        except:
            self.__field_recipients=LIST(**{'elementclass': recipient_record,'length': 9})
        return self.__field_recipients.getvalue()
    def __setfield_recipients(self, value):
        if isinstance(value,LIST):
            self.__field_recipients=value
        else:
            self.__field_recipients=LIST(value,**{'elementclass': recipient_record,'length': 9})
    def __delfield_recipients(self): del self.__field_recipients
    recipients=property(__getfield_recipients, __setfield_recipients, __delfield_recipients, None)
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
        yield ('unknown1', self.__field_unknown1, None)
        yield ('priority', self.__field_priority, None)
        yield ('unknown5', self.__field_unknown5, None)
        yield ('callback', self.__field_callback, None)
        yield ('recipients', self.__field_recipients, None)
        yield ('pad', self.__field_pad, None)
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
            dict2={'elementclass': _gen_p_lgvx5200_126, 'length': 181}
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_msg=LIST(*args,**dict2)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_msg
        except:
            self.__field_msg=LIST(**{'elementclass': _gen_p_lgvx5200_126, 'length': 181})
        self.__field_msg.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_msg=LIST(**{'elementclass': _gen_p_lgvx5200_126, 'length': 181})
        self.__field_msg.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_msg(self):
        try: self.__field_msg
        except:
            self.__field_msg=LIST(**{'elementclass': _gen_p_lgvx5200_126, 'length': 181})
        return self.__field_msg.getvalue()
    def __setfield_msg(self, value):
        if isinstance(value,LIST):
            self.__field_msg=value
        else:
            self.__field_msg=LIST(value,**{'elementclass': _gen_p_lgvx5200_126, 'length': 181})
    def __delfield_msg(self): del self.__field_msg
    msg=property(__getfield_msg, __setfield_msg, __delfield_msg, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('msg', self.__field_msg, None)
class _gen_p_lgvx5200_126(BaseProtogenClass):
    'Anonymous inner class'
    __fields=['byte']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(_gen_p_lgvx5200_126,self).__init__(**dict)
        if self.__class__ is _gen_p_lgvx5200_126:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(_gen_p_lgvx5200_126,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(_gen_p_lgvx5200_126,kwargs)
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
    __fields=['unknown1', 'timesent', 'unknown2', 'callback_length', 'callback', 'sender_length', 'sender', 'unknown3', 'lg_time', 'unknown4', 'GPStime', 'unknown5', 'read', 'locked', 'unknown6', 'priority', 'subject', 'bin_header1', 'bin_header2', 'unknown7', 'multipartID', 'bin_header3', 'num_msg_elements', 'msglengths', 'unknown8', 'msgs', 'unknown9']
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
        self.__field_unknown1.writetobuffer(buf)
        self.__field_timesent.writetobuffer(buf)
        self.__field_unknown2.writetobuffer(buf)
        self.__field_callback_length.writetobuffer(buf)
        self.__field_callback.writetobuffer(buf)
        self.__field_sender_length.writetobuffer(buf)
        try: self.__field_sender
        except:
            self.__field_sender=LIST(**{'elementclass': _gen_p_lgvx5200_136, 'length': 38})
        self.__field_sender.writetobuffer(buf)
        self.__field_unknown3.writetobuffer(buf)
        self.__field_lg_time.writetobuffer(buf)
        self.__field_unknown4.writetobuffer(buf)
        self.__field_GPStime.writetobuffer(buf)
        self.__field_unknown5.writetobuffer(buf)
        self.__field_read.writetobuffer(buf)
        self.__field_locked.writetobuffer(buf)
        self.__field_unknown6.writetobuffer(buf)
        self.__field_priority.writetobuffer(buf)
        self.__field_subject.writetobuffer(buf)
        self.__field_bin_header1.writetobuffer(buf)
        self.__field_bin_header2.writetobuffer(buf)
        self.__field_unknown7.writetobuffer(buf)
        self.__field_multipartID.writetobuffer(buf)
        self.__field_bin_header3.writetobuffer(buf)
        self.__field_num_msg_elements.writetobuffer(buf)
        try: self.__field_msglengths
        except:
            self.__field_msglengths=LIST(**{'elementclass': _gen_p_lgvx5200_154, 'length': 10})
        self.__field_msglengths.writetobuffer(buf)
        self.__field_unknown8.writetobuffer(buf)
        try: self.__field_msgs
        except:
            self.__field_msgs=LIST(**{'length': 10, 'elementclass': SMSINBOXMSGFRAGMENT})
        self.__field_msgs.writetobuffer(buf)
        self.__field_unknown9.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_unknown1=UNKNOWN(**{'sizeinbytes': 10})
        self.__field_unknown1.readfrombuffer(buf)
        self.__field_timesent=SMSDATE(**{'sizeinbytes': 6})
        self.__field_timesent.readfrombuffer(buf)
        self.__field_unknown2=UINT(**{'sizeinbytes': 3})
        self.__field_unknown2.readfrombuffer(buf)
        self.__field_callback_length=UINT(**{'sizeinbytes': 1})
        self.__field_callback_length.readfrombuffer(buf)
        self.__field_callback=USTRING(**{'sizeinbytes': 38})
        self.__field_callback.readfrombuffer(buf)
        self.__field_sender_length=UINT(**{'sizeinbytes': 1})
        self.__field_sender_length.readfrombuffer(buf)
        self.__field_sender=LIST(**{'elementclass': _gen_p_lgvx5200_136, 'length': 38})
        self.__field_sender.readfrombuffer(buf)
        self.__field_unknown3=DATA(**{'sizeinbytes': 12})
        self.__field_unknown3.readfrombuffer(buf)
        self.__field_lg_time=LGCALDATE(**{'sizeinbytes': 4})
        self.__field_lg_time.readfrombuffer(buf)
        self.__field_unknown4=UNKNOWN(**{'sizeinbytes': 3})
        self.__field_unknown4.readfrombuffer(buf)
        self.__field_GPStime=GPSDATE(**{'sizeinbytes': 4})
        self.__field_GPStime.readfrombuffer(buf)
        self.__field_unknown5=UINT(**{'sizeinbytes': 4})
        self.__field_unknown5.readfrombuffer(buf)
        self.__field_read=UINT(**{'sizeinbytes': 1})
        self.__field_read.readfrombuffer(buf)
        self.__field_locked=UINT(**{'sizeinbytes': 1})
        self.__field_locked.readfrombuffer(buf)
        self.__field_unknown6=UINT(**{'sizeinbytes': 8})
        self.__field_unknown6.readfrombuffer(buf)
        self.__field_priority=UINT(**{'sizeinbytes': 1})
        self.__field_priority.readfrombuffer(buf)
        self.__field_subject=USTRING(**{'sizeinbytes': 21, 'encoding': PHONE_ENCODING})
        self.__field_subject.readfrombuffer(buf)
        self.__field_bin_header1=UINT(**{'sizeinbytes': 1})
        self.__field_bin_header1.readfrombuffer(buf)
        self.__field_bin_header2=UINT(**{'sizeinbytes': 1})
        self.__field_bin_header2.readfrombuffer(buf)
        self.__field_unknown7=UINT(**{'sizeinbytes': 4})
        self.__field_unknown7.readfrombuffer(buf)
        self.__field_multipartID=UINT(**{'sizeinbytes': 2})
        self.__field_multipartID.readfrombuffer(buf)
        self.__field_bin_header3=UINT(**{'sizeinbytes': 1})
        self.__field_bin_header3.readfrombuffer(buf)
        self.__field_num_msg_elements=UINT(**{'sizeinbytes': 1})
        self.__field_num_msg_elements.readfrombuffer(buf)
        self.__field_msglengths=LIST(**{'elementclass': _gen_p_lgvx5200_154, 'length': 10})
        self.__field_msglengths.readfrombuffer(buf)
        self.__field_unknown8=UNKNOWN(**{'sizeinbytes': 10})
        self.__field_unknown8.readfrombuffer(buf)
        self.__field_msgs=LIST(**{'length': 10, 'elementclass': SMSINBOXMSGFRAGMENT})
        self.__field_msgs.readfrombuffer(buf)
        self.__field_unknown9=UNKNOWN()
        self.__field_unknown9.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_unknown1(self):
        return self.__field_unknown1.getvalue()
    def __setfield_unknown1(self, value):
        if isinstance(value,UNKNOWN):
            self.__field_unknown1=value
        else:
            self.__field_unknown1=UNKNOWN(value,**{'sizeinbytes': 10})
    def __delfield_unknown1(self): del self.__field_unknown1
    unknown1=property(__getfield_unknown1, __setfield_unknown1, __delfield_unknown1, None)
    def __getfield_timesent(self):
        return self.__field_timesent.getvalue()
    def __setfield_timesent(self, value):
        if isinstance(value,SMSDATE):
            self.__field_timesent=value
        else:
            self.__field_timesent=SMSDATE(value,**{'sizeinbytes': 6})
    def __delfield_timesent(self): del self.__field_timesent
    timesent=property(__getfield_timesent, __setfield_timesent, __delfield_timesent, None)
    def __getfield_unknown2(self):
        return self.__field_unknown2.getvalue()
    def __setfield_unknown2(self, value):
        if isinstance(value,UINT):
            self.__field_unknown2=value
        else:
            self.__field_unknown2=UINT(value,**{'sizeinbytes': 3})
    def __delfield_unknown2(self): del self.__field_unknown2
    unknown2=property(__getfield_unknown2, __setfield_unknown2, __delfield_unknown2, None)
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
            self.__field_sender=LIST(**{'elementclass': _gen_p_lgvx5200_136, 'length': 38})
        return self.__field_sender.getvalue()
    def __setfield_sender(self, value):
        if isinstance(value,LIST):
            self.__field_sender=value
        else:
            self.__field_sender=LIST(value,**{'elementclass': _gen_p_lgvx5200_136, 'length': 38})
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
        if isinstance(value,UNKNOWN):
            self.__field_unknown4=value
        else:
            self.__field_unknown4=UNKNOWN(value,**{'sizeinbytes': 3})
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
    def __getfield_unknown6(self):
        return self.__field_unknown6.getvalue()
    def __setfield_unknown6(self, value):
        if isinstance(value,UINT):
            self.__field_unknown6=value
        else:
            self.__field_unknown6=UINT(value,**{'sizeinbytes': 8})
    def __delfield_unknown6(self): del self.__field_unknown6
    unknown6=property(__getfield_unknown6, __setfield_unknown6, __delfield_unknown6, None)
    def __getfield_priority(self):
        return self.__field_priority.getvalue()
    def __setfield_priority(self, value):
        if isinstance(value,UINT):
            self.__field_priority=value
        else:
            self.__field_priority=UINT(value,**{'sizeinbytes': 1})
    def __delfield_priority(self): del self.__field_priority
    priority=property(__getfield_priority, __setfield_priority, __delfield_priority, None)
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
    def __getfield_unknown7(self):
        return self.__field_unknown7.getvalue()
    def __setfield_unknown7(self, value):
        if isinstance(value,UINT):
            self.__field_unknown7=value
        else:
            self.__field_unknown7=UINT(value,**{'sizeinbytes': 4})
    def __delfield_unknown7(self): del self.__field_unknown7
    unknown7=property(__getfield_unknown7, __setfield_unknown7, __delfield_unknown7, None)
    def __getfield_multipartID(self):
        return self.__field_multipartID.getvalue()
    def __setfield_multipartID(self, value):
        if isinstance(value,UINT):
            self.__field_multipartID=value
        else:
            self.__field_multipartID=UINT(value,**{'sizeinbytes': 2})
    def __delfield_multipartID(self): del self.__field_multipartID
    multipartID=property(__getfield_multipartID, __setfield_multipartID, __delfield_multipartID, None)
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
            self.__field_msglengths=LIST(**{'elementclass': _gen_p_lgvx5200_154, 'length': 10})
        return self.__field_msglengths.getvalue()
    def __setfield_msglengths(self, value):
        if isinstance(value,LIST):
            self.__field_msglengths=value
        else:
            self.__field_msglengths=LIST(value,**{'elementclass': _gen_p_lgvx5200_154, 'length': 10})
    def __delfield_msglengths(self): del self.__field_msglengths
    msglengths=property(__getfield_msglengths, __setfield_msglengths, __delfield_msglengths, None)
    def __getfield_unknown8(self):
        return self.__field_unknown8.getvalue()
    def __setfield_unknown8(self, value):
        if isinstance(value,UNKNOWN):
            self.__field_unknown8=value
        else:
            self.__field_unknown8=UNKNOWN(value,**{'sizeinbytes': 10})
    def __delfield_unknown8(self): del self.__field_unknown8
    unknown8=property(__getfield_unknown8, __setfield_unknown8, __delfield_unknown8, None)
    def __getfield_msgs(self):
        try: self.__field_msgs
        except:
            self.__field_msgs=LIST(**{'length': 10, 'elementclass': SMSINBOXMSGFRAGMENT})
        return self.__field_msgs.getvalue()
    def __setfield_msgs(self, value):
        if isinstance(value,LIST):
            self.__field_msgs=value
        else:
            self.__field_msgs=LIST(value,**{'length': 10, 'elementclass': SMSINBOXMSGFRAGMENT})
    def __delfield_msgs(self): del self.__field_msgs
    msgs=property(__getfield_msgs, __setfield_msgs, __delfield_msgs, None)
    def __getfield_unknown9(self):
        return self.__field_unknown9.getvalue()
    def __setfield_unknown9(self, value):
        if isinstance(value,UNKNOWN):
            self.__field_unknown9=value
        else:
            self.__field_unknown9=UNKNOWN(value,)
    def __delfield_unknown9(self): del self.__field_unknown9
    unknown9=property(__getfield_unknown9, __setfield_unknown9, __delfield_unknown9, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('unknown1', self.__field_unknown1, None)
        yield ('timesent', self.__field_timesent, None)
        yield ('unknown2', self.__field_unknown2, None)
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
        yield ('unknown6', self.__field_unknown6, None)
        yield ('priority', self.__field_priority, None)
        yield ('subject', self.__field_subject, None)
        yield ('bin_header1', self.__field_bin_header1, None)
        yield ('bin_header2', self.__field_bin_header2, None)
        yield ('unknown7', self.__field_unknown7, None)
        yield ('multipartID', self.__field_multipartID, None)
        yield ('bin_header3', self.__field_bin_header3, None)
        yield ('num_msg_elements', self.__field_num_msg_elements, None)
        yield ('msglengths', self.__field_msglengths, None)
        yield ('unknown8', self.__field_unknown8, None)
        yield ('msgs', self.__field_msgs, None)
        yield ('unknown9', self.__field_unknown9, None)
class _gen_p_lgvx5200_136(BaseProtogenClass):
    'Anonymous inner class'
    __fields=['byte']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(_gen_p_lgvx5200_136,self).__init__(**dict)
        if self.__class__ is _gen_p_lgvx5200_136:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(_gen_p_lgvx5200_136,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(_gen_p_lgvx5200_136,kwargs)
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
class _gen_p_lgvx5200_154(BaseProtogenClass):
    'Anonymous inner class'
    __fields=['msglength']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(_gen_p_lgvx5200_154,self).__init__(**dict)
        if self.__class__ is _gen_p_lgvx5200_154:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(_gen_p_lgvx5200_154,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(_gen_p_lgvx5200_154,kwargs)
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
            dict2={'elementclass': _gen_p_lgvx5200_167,  'length': SMS_CANNED_MAX_ITEMS, 'createdefault': True}
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_msgs=LIST(*args,**dict2)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_msgs
        except:
            self.__field_msgs=LIST(**{'elementclass': _gen_p_lgvx5200_167,  'length': SMS_CANNED_MAX_ITEMS, 'createdefault': True})
        self.__field_msgs.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_msgs=LIST(**{'elementclass': _gen_p_lgvx5200_167,  'length': SMS_CANNED_MAX_ITEMS, 'createdefault': True})
        self.__field_msgs.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_msgs(self):
        try: self.__field_msgs
        except:
            self.__field_msgs=LIST(**{'elementclass': _gen_p_lgvx5200_167,  'length': SMS_CANNED_MAX_ITEMS, 'createdefault': True})
        return self.__field_msgs.getvalue()
    def __setfield_msgs(self, value):
        if isinstance(value,LIST):
            self.__field_msgs=value
        else:
            self.__field_msgs=LIST(value,**{'elementclass': _gen_p_lgvx5200_167,  'length': SMS_CANNED_MAX_ITEMS, 'createdefault': True})
    def __delfield_msgs(self): del self.__field_msgs
    msgs=property(__getfield_msgs, __setfield_msgs, __delfield_msgs, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('msgs', self.__field_msgs, None)
class _gen_p_lgvx5200_167(BaseProtogenClass):
    'Anonymous inner class'
    __fields=['msg']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(_gen_p_lgvx5200_167,self).__init__(**dict)
        if self.__class__ is _gen_p_lgvx5200_167:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(_gen_p_lgvx5200_167,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(_gen_p_lgvx5200_167,kwargs)
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
    __fields=['dunno', 'GPStime', 'memotime', 'text']
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
        try: self.__field_dunno
        except:
            self.__field_dunno=UINT(**{'sizeinbytes': 4,  'constant':1 })
        self.__field_dunno.writetobuffer(buf)
        self.__field_GPStime.writetobuffer(buf)
        self.__field_memotime.writetobuffer(buf)
        self.__field_text.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_dunno=UINT(**{'sizeinbytes': 4,  'constant':1 })
        self.__field_dunno.readfrombuffer(buf)
        self.__field_GPStime=GPSDATE(**{'sizeinbytes': 4})
        self.__field_GPStime.readfrombuffer(buf)
        self.__field_memotime=LGCALDATE(**{'sizeinbytes': 4})
        self.__field_memotime.readfrombuffer(buf)
        self.__field_text=USTRING(**{'sizeinbytes': 152, 'encoding': PHONE_ENCODING, 'raiseonunterminatedread': False, 'raiseontruncate': False })
        self.__field_text.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_dunno(self):
        try: self.__field_dunno
        except:
            self.__field_dunno=UINT(**{'sizeinbytes': 4,  'constant':1 })
        return self.__field_dunno.getvalue()
    def __setfield_dunno(self, value):
        if isinstance(value,UINT):
            self.__field_dunno=value
        else:
            self.__field_dunno=UINT(value,**{'sizeinbytes': 4,  'constant':1 })
    def __delfield_dunno(self): del self.__field_dunno
    dunno=property(__getfield_dunno, __setfield_dunno, __delfield_dunno, None)
    def __getfield_GPStime(self):
        return self.__field_GPStime.getvalue()
    def __setfield_GPStime(self, value):
        if isinstance(value,GPSDATE):
            self.__field_GPStime=value
        else:
            self.__field_GPStime=GPSDATE(value,**{'sizeinbytes': 4})
    def __delfield_GPStime(self): del self.__field_GPStime
    GPStime=property(__getfield_GPStime, __setfield_GPStime, __delfield_GPStime, None)
    def __getfield_memotime(self):
        return self.__field_memotime.getvalue()
    def __setfield_memotime(self, value):
        if isinstance(value,LGCALDATE):
            self.__field_memotime=value
        else:
            self.__field_memotime=LGCALDATE(value,**{'sizeinbytes': 4})
    def __delfield_memotime(self): del self.__field_memotime
    memotime=property(__getfield_memotime, __setfield_memotime, __delfield_memotime, None)
    def __getfield_text(self):
        return self.__field_text.getvalue()
    def __setfield_text(self, value):
        if isinstance(value,USTRING):
            self.__field_text=value
        else:
            self.__field_text=USTRING(value,**{'sizeinbytes': 152, 'encoding': PHONE_ENCODING, 'raiseonunterminatedread': False, 'raiseontruncate': False })
    def __delfield_text(self): del self.__field_text
    text=property(__getfield_text, __setfield_text, __delfield_text, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('dunno', self.__field_dunno, None)
        yield ('GPStime', self.__field_GPStime, None)
        yield ('memotime', self.__field_memotime, None)
        yield ('text', self.__field_text, None)
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
