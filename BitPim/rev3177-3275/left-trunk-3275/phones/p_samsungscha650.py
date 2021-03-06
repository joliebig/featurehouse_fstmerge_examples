from prototypes import *
max_pb_slots=501
max_pb_entries=534
user_pb_entry_range=xrange(1, 501)
max_number_entries=501
max_ringtone_entries=20
max_image_entries=10
slot_file_name='nvm/nvm/pclink_tbl'
pb_file_name='nvm/nvm/dial_tbl'
number_file_name='nvm/nvm/dial'
ringtone_index_file_name='nvm/nvm/brew_melody'
ringtone_file_path='user/sound/ringer'
image_index_file_name='nvm/nvm/brew_image'
image_file_path='nvm/brew/shared'
UINT=UINTlsb
BOOL=BOOLlsb
class pbslot(BaseProtogenClass):
    __fields=['c0', 'pbbook_index', 'status', 'timestamp']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(pbslot,self).__init__(**dict)
        if self.__class__ is pbslot:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(pbslot,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(pbslot,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_c0.writetobuffer(buf)
        self.__field_pbbook_index.writetobuffer(buf)
        self.__field_status.writetobuffer(buf)
        self.__field_timestamp.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_c0=UINT(**{'sizeinbytes': 1})
        self.__field_c0.readfrombuffer(buf)
        self.__field_pbbook_index=UINT(**{'sizeinbytes': 2})
        self.__field_pbbook_index.readfrombuffer(buf)
        self.__field_status=UINT(**{'sizeinbytes': 1})
        self.__field_status.readfrombuffer(buf)
        self.__field_timestamp=LIST(**{'elementclass': _gen_p_samsungscha650_41,  'length': 4 })
        self.__field_timestamp.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_c0(self):
        return self.__field_c0.getvalue()
    def __setfield_c0(self, value):
        if isinstance(value,UINT):
            self.__field_c0=value
        else:
            self.__field_c0=UINT(value,**{'sizeinbytes': 1})
    def __delfield_c0(self): del self.__field_c0
    c0=property(__getfield_c0, __setfield_c0, __delfield_c0, None)
    def __getfield_pbbook_index(self):
        return self.__field_pbbook_index.getvalue()
    def __setfield_pbbook_index(self, value):
        if isinstance(value,UINT):
            self.__field_pbbook_index=value
        else:
            self.__field_pbbook_index=UINT(value,**{'sizeinbytes': 2})
    def __delfield_pbbook_index(self): del self.__field_pbbook_index
    pbbook_index=property(__getfield_pbbook_index, __setfield_pbbook_index, __delfield_pbbook_index, None)
    def __getfield_status(self):
        return self.__field_status.getvalue()
    def __setfield_status(self, value):
        if isinstance(value,UINT):
            self.__field_status=value
        else:
            self.__field_status=UINT(value,**{'sizeinbytes': 1})
    def __delfield_status(self): del self.__field_status
    status=property(__getfield_status, __setfield_status, __delfield_status, None)
    def __getfield_timestamp(self):
        return self.__field_timestamp.getvalue()
    def __setfield_timestamp(self, value):
        if isinstance(value,LIST):
            self.__field_timestamp=value
        else:
            self.__field_timestamp=LIST(value,**{'elementclass': _gen_p_samsungscha650_41,  'length': 4 })
    def __delfield_timestamp(self): del self.__field_timestamp
    timestamp=property(__getfield_timestamp, __setfield_timestamp, __delfield_timestamp, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('c0', self.__field_c0, None)
        yield ('pbbook_index', self.__field_pbbook_index, None)
        yield ('status', self.__field_status, None)
        yield ('timestamp', self.__field_timestamp, None)
class _gen_p_samsungscha650_41(BaseProtogenClass):
    'Anonymous inner class'
    __fields=['t']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(_gen_p_samsungscha650_41,self).__init__(**dict)
        if self.__class__ is _gen_p_samsungscha650_41:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(_gen_p_samsungscha650_41,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(_gen_p_samsungscha650_41,kwargs)
        if len(args):
            dict2={'sizeinbytes': 1}
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_t=UINT(*args,**dict2)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_t.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_t=UINT(**{'sizeinbytes': 1})
        self.__field_t.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_t(self):
        return self.__field_t.getvalue()
    def __setfield_t(self, value):
        if isinstance(value,UINT):
            self.__field_t=value
        else:
            self.__field_t=UINT(value,**{'sizeinbytes': 1})
    def __delfield_t(self): del self.__field_t
    t=property(__getfield_t, __setfield_t, __delfield_t, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('t', self.__field_t, None)
class pbslots(BaseProtogenClass):
    __fields=['slot']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(pbslots,self).__init__(**dict)
        if self.__class__ is pbslots:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(pbslots,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(pbslots,kwargs)
        if len(args):
            dict2={ 'length': max_pb_slots, 'elementclass': pbslot }
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_slot=LIST(*args,**dict2)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_slot
        except:
            self.__field_slot=LIST(**{ 'length': max_pb_slots, 'elementclass': pbslot })
        self.__field_slot.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_slot=LIST(**{ 'length': max_pb_slots, 'elementclass': pbslot })
        self.__field_slot.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_slot(self):
        try: self.__field_slot
        except:
            self.__field_slot=LIST(**{ 'length': max_pb_slots, 'elementclass': pbslot })
        return self.__field_slot.getvalue()
    def __setfield_slot(self, value):
        if isinstance(value,LIST):
            self.__field_slot=value
        else:
            self.__field_slot=LIST(value,**{ 'length': max_pb_slots, 'elementclass': pbslot })
    def __delfield_slot(self): del self.__field_slot
    slot=property(__getfield_slot, __setfield_slot, __delfield_slot, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('slot', self.__field_slot, None)
class pbentry(BaseProtogenClass):
    __fields=['c0', 'mem_index', 'c3', 'speed_dial_index', 'home_num_index', 'office_num_index', 'mobile_num_index', 'pager_num_index', 'fax_num_index', 'alias_num_index', 'unused_index', 'email_index', 'name', 'c4', 'ringer_type', 'group_num', 'c5']
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
        self.__field_c0.writetobuffer(buf)
        self.__field_mem_index.writetobuffer(buf)
        self.__field_c3.writetobuffer(buf)
        self.__field_speed_dial_index.writetobuffer(buf)
        self.__field_home_num_index.writetobuffer(buf)
        self.__field_office_num_index.writetobuffer(buf)
        self.__field_mobile_num_index.writetobuffer(buf)
        self.__field_pager_num_index.writetobuffer(buf)
        self.__field_fax_num_index.writetobuffer(buf)
        self.__field_alias_num_index.writetobuffer(buf)
        self.__field_unused_index.writetobuffer(buf)
        self.__field_email_index.writetobuffer(buf)
        self.__field_name.writetobuffer(buf)
        self.__field_c4.writetobuffer(buf)
        self.__field_ringer_type.writetobuffer(buf)
        self.__field_group_num.writetobuffer(buf)
        try: self.__field_c5
        except:
            self.__field_c5=LIST(**{'elementclass': _gen_p_samsungscha650_64,  'length': 7 })
        self.__field_c5.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_c0=UINT(**{'sizeinbytes': 1})
        self.__field_c0.readfrombuffer(buf)
        self.__field_mem_index=UINT(**{'sizeinbytes': 2})
        self.__field_mem_index.readfrombuffer(buf)
        self.__field_c3=UINT(**{'sizeinbytes': 1})
        self.__field_c3.readfrombuffer(buf)
        self.__field_speed_dial_index=UINT(**{'sizeinbytes': 2})
        self.__field_speed_dial_index.readfrombuffer(buf)
        self.__field_home_num_index=UINT(**{'sizeinbytes': 2})
        self.__field_home_num_index.readfrombuffer(buf)
        self.__field_office_num_index=UINT(**{'sizeinbytes': 2})
        self.__field_office_num_index.readfrombuffer(buf)
        self.__field_mobile_num_index=UINT(**{'sizeinbytes': 2})
        self.__field_mobile_num_index.readfrombuffer(buf)
        self.__field_pager_num_index=UINT(**{'sizeinbytes': 2})
        self.__field_pager_num_index.readfrombuffer(buf)
        self.__field_fax_num_index=UINT(**{'sizeinbytes': 2})
        self.__field_fax_num_index.readfrombuffer(buf)
        self.__field_alias_num_index=UINT(**{'sizeinbytes': 2})
        self.__field_alias_num_index.readfrombuffer(buf)
        self.__field_unused_index=UINT(**{'sizeinbytes': 2})
        self.__field_unused_index.readfrombuffer(buf)
        self.__field_email_index=UINT(**{'sizeinbytes': 2})
        self.__field_email_index.readfrombuffer(buf)
        self.__field_name=STRING(**{'sizeinbytes': 22, 'raiseonunterminatedread': False })
        self.__field_name.readfrombuffer(buf)
        self.__field_c4=UINT(**{'sizeinbytes': 1})
        self.__field_c4.readfrombuffer(buf)
        self.__field_ringer_type=UINT(**{'sizeinbytes': 1})
        self.__field_ringer_type.readfrombuffer(buf)
        self.__field_group_num=UINT(**{'sizeinbytes': 1})
        self.__field_group_num.readfrombuffer(buf)
        self.__field_c5=LIST(**{'elementclass': _gen_p_samsungscha650_64,  'length': 7 })
        self.__field_c5.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_c0(self):
        return self.__field_c0.getvalue()
    def __setfield_c0(self, value):
        if isinstance(value,UINT):
            self.__field_c0=value
        else:
            self.__field_c0=UINT(value,**{'sizeinbytes': 1})
    def __delfield_c0(self): del self.__field_c0
    c0=property(__getfield_c0, __setfield_c0, __delfield_c0, None)
    def __getfield_mem_index(self):
        return self.__field_mem_index.getvalue()
    def __setfield_mem_index(self, value):
        if isinstance(value,UINT):
            self.__field_mem_index=value
        else:
            self.__field_mem_index=UINT(value,**{'sizeinbytes': 2})
    def __delfield_mem_index(self): del self.__field_mem_index
    mem_index=property(__getfield_mem_index, __setfield_mem_index, __delfield_mem_index, None)
    def __getfield_c3(self):
        return self.__field_c3.getvalue()
    def __setfield_c3(self, value):
        if isinstance(value,UINT):
            self.__field_c3=value
        else:
            self.__field_c3=UINT(value,**{'sizeinbytes': 1})
    def __delfield_c3(self): del self.__field_c3
    c3=property(__getfield_c3, __setfield_c3, __delfield_c3, None)
    def __getfield_speed_dial_index(self):
        return self.__field_speed_dial_index.getvalue()
    def __setfield_speed_dial_index(self, value):
        if isinstance(value,UINT):
            self.__field_speed_dial_index=value
        else:
            self.__field_speed_dial_index=UINT(value,**{'sizeinbytes': 2})
    def __delfield_speed_dial_index(self): del self.__field_speed_dial_index
    speed_dial_index=property(__getfield_speed_dial_index, __setfield_speed_dial_index, __delfield_speed_dial_index, None)
    def __getfield_home_num_index(self):
        return self.__field_home_num_index.getvalue()
    def __setfield_home_num_index(self, value):
        if isinstance(value,UINT):
            self.__field_home_num_index=value
        else:
            self.__field_home_num_index=UINT(value,**{'sizeinbytes': 2})
    def __delfield_home_num_index(self): del self.__field_home_num_index
    home_num_index=property(__getfield_home_num_index, __setfield_home_num_index, __delfield_home_num_index, None)
    def __getfield_office_num_index(self):
        return self.__field_office_num_index.getvalue()
    def __setfield_office_num_index(self, value):
        if isinstance(value,UINT):
            self.__field_office_num_index=value
        else:
            self.__field_office_num_index=UINT(value,**{'sizeinbytes': 2})
    def __delfield_office_num_index(self): del self.__field_office_num_index
    office_num_index=property(__getfield_office_num_index, __setfield_office_num_index, __delfield_office_num_index, None)
    def __getfield_mobile_num_index(self):
        return self.__field_mobile_num_index.getvalue()
    def __setfield_mobile_num_index(self, value):
        if isinstance(value,UINT):
            self.__field_mobile_num_index=value
        else:
            self.__field_mobile_num_index=UINT(value,**{'sizeinbytes': 2})
    def __delfield_mobile_num_index(self): del self.__field_mobile_num_index
    mobile_num_index=property(__getfield_mobile_num_index, __setfield_mobile_num_index, __delfield_mobile_num_index, None)
    def __getfield_pager_num_index(self):
        return self.__field_pager_num_index.getvalue()
    def __setfield_pager_num_index(self, value):
        if isinstance(value,UINT):
            self.__field_pager_num_index=value
        else:
            self.__field_pager_num_index=UINT(value,**{'sizeinbytes': 2})
    def __delfield_pager_num_index(self): del self.__field_pager_num_index
    pager_num_index=property(__getfield_pager_num_index, __setfield_pager_num_index, __delfield_pager_num_index, None)
    def __getfield_fax_num_index(self):
        return self.__field_fax_num_index.getvalue()
    def __setfield_fax_num_index(self, value):
        if isinstance(value,UINT):
            self.__field_fax_num_index=value
        else:
            self.__field_fax_num_index=UINT(value,**{'sizeinbytes': 2})
    def __delfield_fax_num_index(self): del self.__field_fax_num_index
    fax_num_index=property(__getfield_fax_num_index, __setfield_fax_num_index, __delfield_fax_num_index, None)
    def __getfield_alias_num_index(self):
        return self.__field_alias_num_index.getvalue()
    def __setfield_alias_num_index(self, value):
        if isinstance(value,UINT):
            self.__field_alias_num_index=value
        else:
            self.__field_alias_num_index=UINT(value,**{'sizeinbytes': 2})
    def __delfield_alias_num_index(self): del self.__field_alias_num_index
    alias_num_index=property(__getfield_alias_num_index, __setfield_alias_num_index, __delfield_alias_num_index, None)
    def __getfield_unused_index(self):
        return self.__field_unused_index.getvalue()
    def __setfield_unused_index(self, value):
        if isinstance(value,UINT):
            self.__field_unused_index=value
        else:
            self.__field_unused_index=UINT(value,**{'sizeinbytes': 2})
    def __delfield_unused_index(self): del self.__field_unused_index
    unused_index=property(__getfield_unused_index, __setfield_unused_index, __delfield_unused_index, None)
    def __getfield_email_index(self):
        return self.__field_email_index.getvalue()
    def __setfield_email_index(self, value):
        if isinstance(value,UINT):
            self.__field_email_index=value
        else:
            self.__field_email_index=UINT(value,**{'sizeinbytes': 2})
    def __delfield_email_index(self): del self.__field_email_index
    email_index=property(__getfield_email_index, __setfield_email_index, __delfield_email_index, None)
    def __getfield_name(self):
        return self.__field_name.getvalue()
    def __setfield_name(self, value):
        if isinstance(value,STRING):
            self.__field_name=value
        else:
            self.__field_name=STRING(value,**{'sizeinbytes': 22, 'raiseonunterminatedread': False })
    def __delfield_name(self): del self.__field_name
    name=property(__getfield_name, __setfield_name, __delfield_name, None)
    def __getfield_c4(self):
        return self.__field_c4.getvalue()
    def __setfield_c4(self, value):
        if isinstance(value,UINT):
            self.__field_c4=value
        else:
            self.__field_c4=UINT(value,**{'sizeinbytes': 1})
    def __delfield_c4(self): del self.__field_c4
    c4=property(__getfield_c4, __setfield_c4, __delfield_c4, None)
    def __getfield_ringer_type(self):
        return self.__field_ringer_type.getvalue()
    def __setfield_ringer_type(self, value):
        if isinstance(value,UINT):
            self.__field_ringer_type=value
        else:
            self.__field_ringer_type=UINT(value,**{'sizeinbytes': 1})
    def __delfield_ringer_type(self): del self.__field_ringer_type
    ringer_type=property(__getfield_ringer_type, __setfield_ringer_type, __delfield_ringer_type, None)
    def __getfield_group_num(self):
        return self.__field_group_num.getvalue()
    def __setfield_group_num(self, value):
        if isinstance(value,UINT):
            self.__field_group_num=value
        else:
            self.__field_group_num=UINT(value,**{'sizeinbytes': 1})
    def __delfield_group_num(self): del self.__field_group_num
    group_num=property(__getfield_group_num, __setfield_group_num, __delfield_group_num, None)
    def __getfield_c5(self):
        try: self.__field_c5
        except:
            self.__field_c5=LIST(**{'elementclass': _gen_p_samsungscha650_64,  'length': 7 })
        return self.__field_c5.getvalue()
    def __setfield_c5(self, value):
        if isinstance(value,LIST):
            self.__field_c5=value
        else:
            self.__field_c5=LIST(value,**{'elementclass': _gen_p_samsungscha650_64,  'length': 7 })
    def __delfield_c5(self): del self.__field_c5
    c5=property(__getfield_c5, __setfield_c5, __delfield_c5, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('c0', self.__field_c0, None)
        yield ('mem_index', self.__field_mem_index, None)
        yield ('c3', self.__field_c3, None)
        yield ('speed_dial_index', self.__field_speed_dial_index, None)
        yield ('home_num_index', self.__field_home_num_index, None)
        yield ('office_num_index', self.__field_office_num_index, None)
        yield ('mobile_num_index', self.__field_mobile_num_index, None)
        yield ('pager_num_index', self.__field_pager_num_index, None)
        yield ('fax_num_index', self.__field_fax_num_index, None)
        yield ('alias_num_index', self.__field_alias_num_index, None)
        yield ('unused_index', self.__field_unused_index, None)
        yield ('email_index', self.__field_email_index, None)
        yield ('name', self.__field_name, None)
        yield ('c4', self.__field_c4, None)
        yield ('ringer_type', self.__field_ringer_type, None)
        yield ('group_num', self.__field_group_num, None)
        yield ('c5', self.__field_c5, None)
class _gen_p_samsungscha650_64(BaseProtogenClass):
    'Anonymous inner class'
    __fields=['c5']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(_gen_p_samsungscha650_64,self).__init__(**dict)
        if self.__class__ is _gen_p_samsungscha650_64:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(_gen_p_samsungscha650_64,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(_gen_p_samsungscha650_64,kwargs)
        if len(args):
            dict2={'sizeinbytes': 1}
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_c5=UINT(*args,**dict2)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_c5.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_c5=UINT(**{'sizeinbytes': 1})
        self.__field_c5.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_c5(self):
        return self.__field_c5.getvalue()
    def __setfield_c5(self, value):
        if isinstance(value,UINT):
            self.__field_c5=value
        else:
            self.__field_c5=UINT(value,**{'sizeinbytes': 1})
    def __delfield_c5(self): del self.__field_c5
    c5=property(__getfield_c5, __setfield_c5, __delfield_c5, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('c5', self.__field_c5, None)
class pbbook(BaseProtogenClass):
    __fields=['entry']
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
        if len(args):
            dict2={ 'length': max_pb_entries, 'elementclass': pbentry }
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_entry=LIST(*args,**dict2)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_entry
        except:
            self.__field_entry=LIST(**{ 'length': max_pb_entries, 'elementclass': pbentry })
        self.__field_entry.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_entry=LIST(**{ 'length': max_pb_entries, 'elementclass': pbentry })
        self.__field_entry.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_entry(self):
        try: self.__field_entry
        except:
            self.__field_entry=LIST(**{ 'length': max_pb_entries, 'elementclass': pbentry })
        return self.__field_entry.getvalue()
    def __setfield_entry(self, value):
        if isinstance(value,LIST):
            self.__field_entry=value
        else:
            self.__field_entry=LIST(value,**{ 'length': max_pb_entries, 'elementclass': pbentry })
    def __delfield_entry(self): del self.__field_entry
    entry=property(__getfield_entry, __setfield_entry, __delfield_entry, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('entry', self.__field_entry, None)
class number(BaseProtogenClass):
    __fields=['valid', 'type', 'length', 'name', 'pb_index']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(number,self).__init__(**dict)
        if self.__class__ is number:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(number,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(number,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_valid.writetobuffer(buf)
        self.__field_type.writetobuffer(buf)
        self.__field_length.writetobuffer(buf)
        self.__field_name.writetobuffer(buf)
        self.__field_pb_index.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_valid=UINT(**{'sizeinbytes': 2})
        self.__field_valid.readfrombuffer(buf)
        self.__field_type=UINT(**{'sizeinbytes': 2})
        self.__field_type.readfrombuffer(buf)
        self.__field_length=UINT(**{'sizeinbytes': 1})
        self.__field_length.readfrombuffer(buf)
        self.__field_name=STRING(**{'sizeinbytes': 49,  'raiseonunterminatedread': False })
        self.__field_name.readfrombuffer(buf)
        self.__field_pb_index=UINT(**{'sizeinbytes': 2})
        self.__field_pb_index.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_valid(self):
        return self.__field_valid.getvalue()
    def __setfield_valid(self, value):
        if isinstance(value,UINT):
            self.__field_valid=value
        else:
            self.__field_valid=UINT(value,**{'sizeinbytes': 2})
    def __delfield_valid(self): del self.__field_valid
    valid=property(__getfield_valid, __setfield_valid, __delfield_valid, None)
    def __getfield_type(self):
        return self.__field_type.getvalue()
    def __setfield_type(self, value):
        if isinstance(value,UINT):
            self.__field_type=value
        else:
            self.__field_type=UINT(value,**{'sizeinbytes': 2})
    def __delfield_type(self): del self.__field_type
    type=property(__getfield_type, __setfield_type, __delfield_type, None)
    def __getfield_length(self):
        return self.__field_length.getvalue()
    def __setfield_length(self, value):
        if isinstance(value,UINT):
            self.__field_length=value
        else:
            self.__field_length=UINT(value,**{'sizeinbytes': 1})
    def __delfield_length(self): del self.__field_length
    length=property(__getfield_length, __setfield_length, __delfield_length, None)
    def __getfield_name(self):
        return self.__field_name.getvalue()
    def __setfield_name(self, value):
        if isinstance(value,STRING):
            self.__field_name=value
        else:
            self.__field_name=STRING(value,**{'sizeinbytes': 49,  'raiseonunterminatedread': False })
    def __delfield_name(self): del self.__field_name
    name=property(__getfield_name, __setfield_name, __delfield_name, None)
    def __getfield_pb_index(self):
        return self.__field_pb_index.getvalue()
    def __setfield_pb_index(self, value):
        if isinstance(value,UINT):
            self.__field_pb_index=value
        else:
            self.__field_pb_index=UINT(value,**{'sizeinbytes': 2})
    def __delfield_pb_index(self): del self.__field_pb_index
    pb_index=property(__getfield_pb_index, __setfield_pb_index, __delfield_pb_index, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('valid', self.__field_valid, None)
        yield ('type', self.__field_type, None)
        yield ('length', self.__field_length, None)
        yield ('name', self.__field_name, None)
        yield ('pb_index', self.__field_pb_index, None)
class numbers(BaseProtogenClass):
    __fields=['entry']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(numbers,self).__init__(**dict)
        if self.__class__ is numbers:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(numbers,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(numbers,kwargs)
        if len(args):
            dict2={ 'length': max_number_entries, 'elementclass': number }
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_entry=LIST(*args,**dict2)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_entry
        except:
            self.__field_entry=LIST(**{ 'length': max_number_entries, 'elementclass': number })
        self.__field_entry.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_entry=LIST(**{ 'length': max_number_entries, 'elementclass': number })
        self.__field_entry.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_entry(self):
        try: self.__field_entry
        except:
            self.__field_entry=LIST(**{ 'length': max_number_entries, 'elementclass': number })
        return self.__field_entry.getvalue()
    def __setfield_entry(self, value):
        if isinstance(value,LIST):
            self.__field_entry=value
        else:
            self.__field_entry=LIST(value,**{ 'length': max_number_entries, 'elementclass': number })
    def __delfield_entry(self): del self.__field_entry
    entry=property(__getfield_entry, __setfield_entry, __delfield_entry, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('entry', self.__field_entry, None)
class ringtone(BaseProtogenClass):
    __fields=['c0', 'index', 'c1', 'assignment', 'c2', 'name', 'name_len', 'file_name', 'file_name_len', 'c3']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(ringtone,self).__init__(**dict)
        if self.__class__ is ringtone:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(ringtone,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(ringtone,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_c0.writetobuffer(buf)
        self.__field_index.writetobuffer(buf)
        self.__field_c1.writetobuffer(buf)
        self.__field_assignment.writetobuffer(buf)
        self.__field_c2.writetobuffer(buf)
        self.__field_name.writetobuffer(buf)
        self.__field_name_len.writetobuffer(buf)
        self.__field_file_name.writetobuffer(buf)
        self.__field_file_name_len.writetobuffer(buf)
        self.__field_c3.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_c0=UINT(**{'sizeinbytes': 1})
        self.__field_c0.readfrombuffer(buf)
        self.__field_index=UINT(**{'sizeinbytes': 1})
        self.__field_index.readfrombuffer(buf)
        self.__field_c1=UINT(**{'sizeinbytes': 1})
        self.__field_c1.readfrombuffer(buf)
        self.__field_assignment=UINT(**{'sizeinbytes': 1})
        self.__field_assignment.readfrombuffer(buf)
        self.__field_c2=UINT(**{'sizeinbytes': 1})
        self.__field_c2.readfrombuffer(buf)
        self.__field_name=STRING(**{'sizeinbytes': 17,  'raiseonunterminatedread': False })
        self.__field_name.readfrombuffer(buf)
        self.__field_name_len=UINT(**{'sizeinbytes': 1})
        self.__field_name_len.readfrombuffer(buf)
        self.__field_file_name=STRING(**{'sizeinbytes': 46,  'raiseonunterminatedread': False })
        self.__field_file_name.readfrombuffer(buf)
        self.__field_file_name_len=UINT(**{'sizeinbytes': 1})
        self.__field_file_name_len.readfrombuffer(buf)
        self.__field_c3=UINT(**{'sizeinbytes': 2})
        self.__field_c3.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_c0(self):
        return self.__field_c0.getvalue()
    def __setfield_c0(self, value):
        if isinstance(value,UINT):
            self.__field_c0=value
        else:
            self.__field_c0=UINT(value,**{'sizeinbytes': 1})
    def __delfield_c0(self): del self.__field_c0
    c0=property(__getfield_c0, __setfield_c0, __delfield_c0, None)
    def __getfield_index(self):
        return self.__field_index.getvalue()
    def __setfield_index(self, value):
        if isinstance(value,UINT):
            self.__field_index=value
        else:
            self.__field_index=UINT(value,**{'sizeinbytes': 1})
    def __delfield_index(self): del self.__field_index
    index=property(__getfield_index, __setfield_index, __delfield_index, None)
    def __getfield_c1(self):
        return self.__field_c1.getvalue()
    def __setfield_c1(self, value):
        if isinstance(value,UINT):
            self.__field_c1=value
        else:
            self.__field_c1=UINT(value,**{'sizeinbytes': 1})
    def __delfield_c1(self): del self.__field_c1
    c1=property(__getfield_c1, __setfield_c1, __delfield_c1, None)
    def __getfield_assignment(self):
        return self.__field_assignment.getvalue()
    def __setfield_assignment(self, value):
        if isinstance(value,UINT):
            self.__field_assignment=value
        else:
            self.__field_assignment=UINT(value,**{'sizeinbytes': 1})
    def __delfield_assignment(self): del self.__field_assignment
    assignment=property(__getfield_assignment, __setfield_assignment, __delfield_assignment, None)
    def __getfield_c2(self):
        return self.__field_c2.getvalue()
    def __setfield_c2(self, value):
        if isinstance(value,UINT):
            self.__field_c2=value
        else:
            self.__field_c2=UINT(value,**{'sizeinbytes': 1})
    def __delfield_c2(self): del self.__field_c2
    c2=property(__getfield_c2, __setfield_c2, __delfield_c2, None)
    def __getfield_name(self):
        return self.__field_name.getvalue()
    def __setfield_name(self, value):
        if isinstance(value,STRING):
            self.__field_name=value
        else:
            self.__field_name=STRING(value,**{'sizeinbytes': 17,  'raiseonunterminatedread': False })
    def __delfield_name(self): del self.__field_name
    name=property(__getfield_name, __setfield_name, __delfield_name, None)
    def __getfield_name_len(self):
        return self.__field_name_len.getvalue()
    def __setfield_name_len(self, value):
        if isinstance(value,UINT):
            self.__field_name_len=value
        else:
            self.__field_name_len=UINT(value,**{'sizeinbytes': 1})
    def __delfield_name_len(self): del self.__field_name_len
    name_len=property(__getfield_name_len, __setfield_name_len, __delfield_name_len, None)
    def __getfield_file_name(self):
        return self.__field_file_name.getvalue()
    def __setfield_file_name(self, value):
        if isinstance(value,STRING):
            self.__field_file_name=value
        else:
            self.__field_file_name=STRING(value,**{'sizeinbytes': 46,  'raiseonunterminatedread': False })
    def __delfield_file_name(self): del self.__field_file_name
    file_name=property(__getfield_file_name, __setfield_file_name, __delfield_file_name, None)
    def __getfield_file_name_len(self):
        return self.__field_file_name_len.getvalue()
    def __setfield_file_name_len(self, value):
        if isinstance(value,UINT):
            self.__field_file_name_len=value
        else:
            self.__field_file_name_len=UINT(value,**{'sizeinbytes': 1})
    def __delfield_file_name_len(self): del self.__field_file_name_len
    file_name_len=property(__getfield_file_name_len, __setfield_file_name_len, __delfield_file_name_len, None)
    def __getfield_c3(self):
        return self.__field_c3.getvalue()
    def __setfield_c3(self, value):
        if isinstance(value,UINT):
            self.__field_c3=value
        else:
            self.__field_c3=UINT(value,**{'sizeinbytes': 2})
    def __delfield_c3(self): del self.__field_c3
    c3=property(__getfield_c3, __setfield_c3, __delfield_c3, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('c0', self.__field_c0, None)
        yield ('index', self.__field_index, None)
        yield ('c1', self.__field_c1, None)
        yield ('assignment', self.__field_assignment, None)
        yield ('c2', self.__field_c2, None)
        yield ('name', self.__field_name, None)
        yield ('name_len', self.__field_name_len, None)
        yield ('file_name', self.__field_file_name, None)
        yield ('file_name_len', self.__field_file_name_len, None)
        yield ('c3', self.__field_c3, None)
class ringtones(BaseProtogenClass):
    __fields=['entry']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(ringtones,self).__init__(**dict)
        if self.__class__ is ringtones:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(ringtones,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(ringtones,kwargs)
        if len(args):
            dict2={ 'length': max_ringtone_entries, 'elementclass': ringtone }
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_entry=LIST(*args,**dict2)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_entry.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_entry=LIST(**{ 'length': max_ringtone_entries, 'elementclass': ringtone })
        self.__field_entry.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_entry(self):
        return self.__field_entry.getvalue()
    def __setfield_entry(self, value):
        if isinstance(value,LIST):
            self.__field_entry=value
        else:
            self.__field_entry=LIST(value,**{ 'length': max_ringtone_entries, 'elementclass': ringtone })
    def __delfield_entry(self): del self.__field_entry
    entry=property(__getfield_entry, __setfield_entry, __delfield_entry, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('entry', self.__field_entry, None)
class image(BaseProtogenClass):
    __fields=['c0', 'index', 'c1', 'assignment', 'name', 'name_len', 'file_name', 'file_name_len', 'c2']
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
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_c0.writetobuffer(buf)
        self.__field_index.writetobuffer(buf)
        self.__field_c1.writetobuffer(buf)
        self.__field_assignment.writetobuffer(buf)
        self.__field_name.writetobuffer(buf)
        self.__field_name_len.writetobuffer(buf)
        self.__field_file_name.writetobuffer(buf)
        self.__field_file_name_len.writetobuffer(buf)
        self.__field_c2.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_c0=UINT(**{'sizeinbytes': 1})
        self.__field_c0.readfrombuffer(buf)
        self.__field_index=UINT(**{'sizeinbytes': 1})
        self.__field_index.readfrombuffer(buf)
        self.__field_c1=UINT(**{'sizeinbytes': 1})
        self.__field_c1.readfrombuffer(buf)
        self.__field_assignment=UINT(**{'sizeinbytes': 1})
        self.__field_assignment.readfrombuffer(buf)
        self.__field_name=STRING(**{'sizeinbytes': 17,  'raiseonunterminatedread': False })
        self.__field_name.readfrombuffer(buf)
        self.__field_name_len=UINT(**{'sizeinbytes': 1})
        self.__field_name_len.readfrombuffer(buf)
        self.__field_file_name=STRING(**{'sizeinbytes': 46,  'raiseonunterminatedread': False })
        self.__field_file_name.readfrombuffer(buf)
        self.__field_file_name_len=UINT(**{'sizeinbytes': 1})
        self.__field_file_name_len.readfrombuffer(buf)
        self.__field_c2=UINT(**{'sizeinbytes': 2})
        self.__field_c2.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_c0(self):
        return self.__field_c0.getvalue()
    def __setfield_c0(self, value):
        if isinstance(value,UINT):
            self.__field_c0=value
        else:
            self.__field_c0=UINT(value,**{'sizeinbytes': 1})
    def __delfield_c0(self): del self.__field_c0
    c0=property(__getfield_c0, __setfield_c0, __delfield_c0, None)
    def __getfield_index(self):
        return self.__field_index.getvalue()
    def __setfield_index(self, value):
        if isinstance(value,UINT):
            self.__field_index=value
        else:
            self.__field_index=UINT(value,**{'sizeinbytes': 1})
    def __delfield_index(self): del self.__field_index
    index=property(__getfield_index, __setfield_index, __delfield_index, None)
    def __getfield_c1(self):
        return self.__field_c1.getvalue()
    def __setfield_c1(self, value):
        if isinstance(value,UINT):
            self.__field_c1=value
        else:
            self.__field_c1=UINT(value,**{'sizeinbytes': 1})
    def __delfield_c1(self): del self.__field_c1
    c1=property(__getfield_c1, __setfield_c1, __delfield_c1, None)
    def __getfield_assignment(self):
        return self.__field_assignment.getvalue()
    def __setfield_assignment(self, value):
        if isinstance(value,UINT):
            self.__field_assignment=value
        else:
            self.__field_assignment=UINT(value,**{'sizeinbytes': 1})
    def __delfield_assignment(self): del self.__field_assignment
    assignment=property(__getfield_assignment, __setfield_assignment, __delfield_assignment, None)
    def __getfield_name(self):
        return self.__field_name.getvalue()
    def __setfield_name(self, value):
        if isinstance(value,STRING):
            self.__field_name=value
        else:
            self.__field_name=STRING(value,**{'sizeinbytes': 17,  'raiseonunterminatedread': False })
    def __delfield_name(self): del self.__field_name
    name=property(__getfield_name, __setfield_name, __delfield_name, None)
    def __getfield_name_len(self):
        return self.__field_name_len.getvalue()
    def __setfield_name_len(self, value):
        if isinstance(value,UINT):
            self.__field_name_len=value
        else:
            self.__field_name_len=UINT(value,**{'sizeinbytes': 1})
    def __delfield_name_len(self): del self.__field_name_len
    name_len=property(__getfield_name_len, __setfield_name_len, __delfield_name_len, None)
    def __getfield_file_name(self):
        return self.__field_file_name.getvalue()
    def __setfield_file_name(self, value):
        if isinstance(value,STRING):
            self.__field_file_name=value
        else:
            self.__field_file_name=STRING(value,**{'sizeinbytes': 46,  'raiseonunterminatedread': False })
    def __delfield_file_name(self): del self.__field_file_name
    file_name=property(__getfield_file_name, __setfield_file_name, __delfield_file_name, None)
    def __getfield_file_name_len(self):
        return self.__field_file_name_len.getvalue()
    def __setfield_file_name_len(self, value):
        if isinstance(value,UINT):
            self.__field_file_name_len=value
        else:
            self.__field_file_name_len=UINT(value,**{'sizeinbytes': 1})
    def __delfield_file_name_len(self): del self.__field_file_name_len
    file_name_len=property(__getfield_file_name_len, __setfield_file_name_len, __delfield_file_name_len, None)
    def __getfield_c2(self):
        return self.__field_c2.getvalue()
    def __setfield_c2(self, value):
        if isinstance(value,UINT):
            self.__field_c2=value
        else:
            self.__field_c2=UINT(value,**{'sizeinbytes': 2})
    def __delfield_c2(self): del self.__field_c2
    c2=property(__getfield_c2, __setfield_c2, __delfield_c2, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('c0', self.__field_c0, None)
        yield ('index', self.__field_index, None)
        yield ('c1', self.__field_c1, None)
        yield ('assignment', self.__field_assignment, None)
        yield ('name', self.__field_name, None)
        yield ('name_len', self.__field_name_len, None)
        yield ('file_name', self.__field_file_name, None)
        yield ('file_name_len', self.__field_file_name_len, None)
        yield ('c2', self.__field_c2, None)
class images(BaseProtogenClass):
    __fields=['entry']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(images,self).__init__(**dict)
        if self.__class__ is images:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(images,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(images,kwargs)
        if len(args):
            dict2={ 'length': max_image_entries, 'elementclass': image }
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_entry=LIST(*args,**dict2)
    def writetobuffer(self,buf,autolog=True,logtitle="<written data>"):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_entry.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologwrite(buf, logtitle=logtitle)
    def readfrombuffer(self,buf,autolog=True,logtitle="<read data>"):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        if autolog and self._bufferstartoffset==0: self.autologread(buf, logtitle=logtitle)
        self.__field_entry=LIST(**{ 'length': max_image_entries, 'elementclass': image })
        self.__field_entry.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_entry(self):
        return self.__field_entry.getvalue()
    def __setfield_entry(self, value):
        if isinstance(value,LIST):
            self.__field_entry=value
        else:
            self.__field_entry=LIST(value,**{ 'length': max_image_entries, 'elementclass': image })
    def __delfield_entry(self): del self.__field_entry
    entry=property(__getfield_entry, __setfield_entry, __delfield_entry, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('entry', self.__field_entry, None)
