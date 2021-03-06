"""Various descriptions of data specific to LG VX4600"""
from prototypes import *
from p_lg import *
from p_lgvx4400 import *
UINT=UINTlsb
BOOL=BOOLlsb
NUMSPEEDDIALS=100
FIRSTSPEEDDIAL=1
LASTSPEEDDIAL=99
NUMPHONEBOOKENTRIES=500
MAXCALENDARDESCRIPTION=38
NUMEMAILS=1
NUMPHONENUMBERS=5
MEMOLENGTH=49
class mediadesc(BaseProtogenClass):
    __fields=['totalsize', 'dunno1', 'index', 'magic1', 'magic2', 'magic3', 'dunno2', 'filename', 'whoknows', 'mimetype', 'whoknows2']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(mediadesc,self).__init__(**dict)
        if self.__class__ is mediadesc:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(mediadesc,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(mediadesc,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_totalsize.writetobuffer(buf)
        try: self.__field_dunno1
        except:
            self.__field_dunno1=UINT(**{'sizeinbytes': 4, 'constant': 0})
        self.__field_dunno1.writetobuffer(buf)
        self.__field_index.writetobuffer(buf)
        try: self.__field_magic1
        except:
            self.__field_magic1=UINT(**{'sizeinbytes': 4, 'default': 0x7824c97a})
        self.__field_magic1.writetobuffer(buf)
        try: self.__field_magic2
        except:
            self.__field_magic2=UINT(**{'sizeinbytes': 4, 'default': 0x7824c97a})
        self.__field_magic2.writetobuffer(buf)
        try: self.__field_magic3
        except:
            self.__field_magic3=UINT(**{'sizeinbytes': 4, 'default': 0x7824c97a})
        self.__field_magic3.writetobuffer(buf)
        try: self.__field_dunno2
        except:
            self.__field_dunno2=UINT(**{'sizeinbytes': 4, 'constant': 0})
        self.__field_dunno2.writetobuffer(buf)
        self.__field_filename.writetobuffer(buf)
        try: self.__field_whoknows
        except:
            self.__field_whoknows=STRING(**{'sizeinbytes': 32, 'default': 'identity'})
        self.__field_whoknows.writetobuffer(buf)
        self.__field_mimetype.writetobuffer(buf)
        try: self.__field_whoknows2
        except:
            self.__field_whoknows2=STRING(**{'sizeinbytes': 32, 'default': ""})
        self.__field_whoknows2.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def readfrombuffer(self,buf):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_totalsize=UINT(**{'sizeinbytes': 4})
        self.__field_totalsize.readfrombuffer(buf)
        self.__field_dunno1=UINT(**{'sizeinbytes': 4, 'constant': 0})
        self.__field_dunno1.readfrombuffer(buf)
        self.__field_index=UINT(**{'sizeinbytes': 4})
        self.__field_index.readfrombuffer(buf)
        self.__field_magic1=UINT(**{'sizeinbytes': 4, 'default': 0x7824c97a})
        self.__field_magic1.readfrombuffer(buf)
        self.__field_magic2=UINT(**{'sizeinbytes': 4, 'default': 0x7824c97a})
        self.__field_magic2.readfrombuffer(buf)
        self.__field_magic3=UINT(**{'sizeinbytes': 4, 'default': 0x7824c97a})
        self.__field_magic3.readfrombuffer(buf)
        self.__field_dunno2=UINT(**{'sizeinbytes': 4, 'constant': 0})
        self.__field_dunno2.readfrombuffer(buf)
        self.__field_filename=STRING(**{'sizeinbytes': 32, 'default': 'body'})
        self.__field_filename.readfrombuffer(buf)
        self.__field_whoknows=STRING(**{'sizeinbytes': 32, 'default': 'identity'})
        self.__field_whoknows.readfrombuffer(buf)
        self.__field_mimetype=STRING(**{'sizeinbytes': 32})
        self.__field_mimetype.readfrombuffer(buf)
        self.__field_whoknows2=STRING(**{'sizeinbytes': 32, 'default': ""})
        self.__field_whoknows2.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_totalsize(self):
        return self.__field_totalsize.getvalue()
    def __setfield_totalsize(self, value):
        if isinstance(value,UINT):
            self.__field_totalsize=value
        else:
            self.__field_totalsize=UINT(value,**{'sizeinbytes': 4})
    def __delfield_totalsize(self): del self.__field_totalsize
    totalsize=property(__getfield_totalsize, __setfield_totalsize, __delfield_totalsize, "media file size with size of this file (156 bytes) added")
    def __getfield_dunno1(self):
        try: self.__field_dunno1
        except:
            self.__field_dunno1=UINT(**{'sizeinbytes': 4, 'constant': 0})
        return self.__field_dunno1.getvalue()
    def __setfield_dunno1(self, value):
        if isinstance(value,UINT):
            self.__field_dunno1=value
        else:
            self.__field_dunno1=UINT(value,**{'sizeinbytes': 4, 'constant': 0})
    def __delfield_dunno1(self): del self.__field_dunno1
    dunno1=property(__getfield_dunno1, __setfield_dunno1, __delfield_dunno1, None)
    def __getfield_index(self):
        return self.__field_index.getvalue()
    def __setfield_index(self, value):
        if isinstance(value,UINT):
            self.__field_index=value
        else:
            self.__field_index=UINT(value,**{'sizeinbytes': 4})
    def __delfield_index(self): del self.__field_index
    index=property(__getfield_index, __setfield_index, __delfield_index, "index number")
    def __getfield_magic1(self):
        try: self.__field_magic1
        except:
            self.__field_magic1=UINT(**{'sizeinbytes': 4, 'default': 0x7824c97a})
        return self.__field_magic1.getvalue()
    def __setfield_magic1(self, value):
        if isinstance(value,UINT):
            self.__field_magic1=value
        else:
            self.__field_magic1=UINT(value,**{'sizeinbytes': 4, 'default': 0x7824c97a})
    def __delfield_magic1(self): del self.__field_magic1
    magic1=property(__getfield_magic1, __setfield_magic1, __delfield_magic1, "probably a date")
    def __getfield_magic2(self):
        try: self.__field_magic2
        except:
            self.__field_magic2=UINT(**{'sizeinbytes': 4, 'default': 0x7824c97a})
        return self.__field_magic2.getvalue()
    def __setfield_magic2(self, value):
        if isinstance(value,UINT):
            self.__field_magic2=value
        else:
            self.__field_magic2=UINT(value,**{'sizeinbytes': 4, 'default': 0x7824c97a})
    def __delfield_magic2(self): del self.__field_magic2
    magic2=property(__getfield_magic2, __setfield_magic2, __delfield_magic2, "probably a date")
    def __getfield_magic3(self):
        try: self.__field_magic3
        except:
            self.__field_magic3=UINT(**{'sizeinbytes': 4, 'default': 0x7824c97a})
        return self.__field_magic3.getvalue()
    def __setfield_magic3(self, value):
        if isinstance(value,UINT):
            self.__field_magic3=value
        else:
            self.__field_magic3=UINT(value,**{'sizeinbytes': 4, 'default': 0x7824c97a})
    def __delfield_magic3(self): del self.__field_magic3
    magic3=property(__getfield_magic3, __setfield_magic3, __delfield_magic3, "probably a date")
    def __getfield_dunno2(self):
        try: self.__field_dunno2
        except:
            self.__field_dunno2=UINT(**{'sizeinbytes': 4, 'constant': 0})
        return self.__field_dunno2.getvalue()
    def __setfield_dunno2(self, value):
        if isinstance(value,UINT):
            self.__field_dunno2=value
        else:
            self.__field_dunno2=UINT(value,**{'sizeinbytes': 4, 'constant': 0})
    def __delfield_dunno2(self): del self.__field_dunno2
    dunno2=property(__getfield_dunno2, __setfield_dunno2, __delfield_dunno2, None)
    def __getfield_filename(self):
        return self.__field_filename.getvalue()
    def __setfield_filename(self, value):
        if isinstance(value,STRING):
            self.__field_filename=value
        else:
            self.__field_filename=STRING(value,**{'sizeinbytes': 32, 'default': 'body'})
    def __delfield_filename(self): del self.__field_filename
    filename=property(__getfield_filename, __setfield_filename, __delfield_filename, None)
    def __getfield_whoknows(self):
        try: self.__field_whoknows
        except:
            self.__field_whoknows=STRING(**{'sizeinbytes': 32, 'default': 'identity'})
        return self.__field_whoknows.getvalue()
    def __setfield_whoknows(self, value):
        if isinstance(value,STRING):
            self.__field_whoknows=value
        else:
            self.__field_whoknows=STRING(value,**{'sizeinbytes': 32, 'default': 'identity'})
    def __delfield_whoknows(self): del self.__field_whoknows
    whoknows=property(__getfield_whoknows, __setfield_whoknows, __delfield_whoknows, None)
    def __getfield_mimetype(self):
        return self.__field_mimetype.getvalue()
    def __setfield_mimetype(self, value):
        if isinstance(value,STRING):
            self.__field_mimetype=value
        else:
            self.__field_mimetype=STRING(value,**{'sizeinbytes': 32})
    def __delfield_mimetype(self): del self.__field_mimetype
    mimetype=property(__getfield_mimetype, __setfield_mimetype, __delfield_mimetype, None)
    def __getfield_whoknows2(self):
        try: self.__field_whoknows2
        except:
            self.__field_whoknows2=STRING(**{'sizeinbytes': 32, 'default': ""})
        return self.__field_whoknows2.getvalue()
    def __setfield_whoknows2(self, value):
        if isinstance(value,STRING):
            self.__field_whoknows2=value
        else:
            self.__field_whoknows2=STRING(value,**{'sizeinbytes': 32, 'default': ""})
    def __delfield_whoknows2(self): del self.__field_whoknows2
    whoknows2=property(__getfield_whoknows2, __setfield_whoknows2, __delfield_whoknows2, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('totalsize', self.__field_totalsize, "media file size with size of this file (156 bytes) added")
        yield ('dunno1', self.__field_dunno1, None)
        yield ('index', self.__field_index, "index number")
        yield ('magic1', self.__field_magic1, "probably a date")
        yield ('magic2', self.__field_magic2, "probably a date")
        yield ('magic3', self.__field_magic3, "probably a date")
        yield ('dunno2', self.__field_dunno2, None)
        yield ('filename', self.__field_filename, None)
        yield ('whoknows', self.__field_whoknows, None)
        yield ('mimetype', self.__field_mimetype, None)
        yield ('whoknows2', self.__field_whoknows2, None)
class pbentry(BaseProtogenClass):
    __fields=['serial1', 'entrysize', 'serial2', 'entrynumber', 'name', 'group', 'emails', 'url', 'ringtone', 'secret', 'memo', 'wallpaper', 'numbertypes', 'numbers', 'unknown20c']
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
        self.__field_serial1.writetobuffer(buf)
        try: self.__field_entrysize
        except:
            self.__field_entrysize=UINT(**{'sizeinbytes': 2, 'constant': 0x0190})
        self.__field_entrysize.writetobuffer(buf)
        self.__field_serial2.writetobuffer(buf)
        self.__field_entrynumber.writetobuffer(buf)
        self.__field_name.writetobuffer(buf)
        self.__field_group.writetobuffer(buf)
        try: self.__field_emails
        except:
            self.__field_emails=LIST(**{'elementclass': _gen_p_lgvx4600_66, 'length': NUMEMAILS})
        self.__field_emails.writetobuffer(buf)
        self.__field_url.writetobuffer(buf)
        self.__field_ringtone.writetobuffer(buf)
        self.__field_secret.writetobuffer(buf)
        self.__field_memo.writetobuffer(buf)
        self.__field_wallpaper.writetobuffer(buf)
        try: self.__field_numbertypes
        except:
            self.__field_numbertypes=LIST(**{'elementclass': _gen_p_lgvx4600_73, 'length': NUMPHONENUMBERS})
        self.__field_numbertypes.writetobuffer(buf)
        try: self.__field_numbers
        except:
            self.__field_numbers=LIST(**{'elementclass': _gen_p_lgvx4600_75, 'length': NUMPHONENUMBERS})
        self.__field_numbers.writetobuffer(buf)
        try: self.__field_unknown20c
        except:
            self.__field_unknown20c=UNKNOWN()
        self.__field_unknown20c.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def readfrombuffer(self,buf):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_serial1=UINT(**{'sizeinbytes': 4})
        self.__field_serial1.readfrombuffer(buf)
        self.__field_entrysize=UINT(**{'sizeinbytes': 2, 'constant': 0x0190})
        self.__field_entrysize.readfrombuffer(buf)
        self.__field_serial2=UINT(**{'sizeinbytes': 4})
        self.__field_serial2.readfrombuffer(buf)
        self.__field_entrynumber=UINT(**{'sizeinbytes': 2})
        self.__field_entrynumber.readfrombuffer(buf)
        self.__field_name=STRING(**{'sizeinbytes': 23, 'raiseonunterminatedread': False})
        self.__field_name.readfrombuffer(buf)
        self.__field_group=UINT(**{'sizeinbytes': 2})
        self.__field_group.readfrombuffer(buf)
        self.__field_emails=LIST(**{'elementclass': _gen_p_lgvx4600_66, 'length': NUMEMAILS})
        self.__field_emails.readfrombuffer(buf)
        self.__field_url=STRING(**{'sizeinbytes': 72, 'raiseonunterminatedread': False})
        self.__field_url.readfrombuffer(buf)
        self.__field_ringtone=UINT(**{'sizeinbytes': 2})
        self.__field_ringtone.readfrombuffer(buf)
        self.__field_secret=BOOL(**{'sizeinbytes': 1})
        self.__field_secret.readfrombuffer(buf)
        self.__field_memo=STRING(**{'raiseonunterminatedread': False, 'sizeinbytes': MEMOLENGTH})
        self.__field_memo.readfrombuffer(buf)
        self.__field_wallpaper=UINT(**{'sizeinbytes': 2})
        self.__field_wallpaper.readfrombuffer(buf)
        self.__field_numbertypes=LIST(**{'elementclass': _gen_p_lgvx4600_73, 'length': NUMPHONENUMBERS})
        self.__field_numbertypes.readfrombuffer(buf)
        self.__field_numbers=LIST(**{'elementclass': _gen_p_lgvx4600_75, 'length': NUMPHONENUMBERS})
        self.__field_numbers.readfrombuffer(buf)
        self.__field_unknown20c=UNKNOWN()
        self.__field_unknown20c.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_serial1(self):
        return self.__field_serial1.getvalue()
    def __setfield_serial1(self, value):
        if isinstance(value,UINT):
            self.__field_serial1=value
        else:
            self.__field_serial1=UINT(value,**{'sizeinbytes': 4})
    def __delfield_serial1(self): del self.__field_serial1
    serial1=property(__getfield_serial1, __setfield_serial1, __delfield_serial1, None)
    def __getfield_entrysize(self):
        try: self.__field_entrysize
        except:
            self.__field_entrysize=UINT(**{'sizeinbytes': 2, 'constant': 0x0190})
        return self.__field_entrysize.getvalue()
    def __setfield_entrysize(self, value):
        if isinstance(value,UINT):
            self.__field_entrysize=value
        else:
            self.__field_entrysize=UINT(value,**{'sizeinbytes': 2, 'constant': 0x0190})
    def __delfield_entrysize(self): del self.__field_entrysize
    entrysize=property(__getfield_entrysize, __setfield_entrysize, __delfield_entrysize, None)
    def __getfield_serial2(self):
        return self.__field_serial2.getvalue()
    def __setfield_serial2(self, value):
        if isinstance(value,UINT):
            self.__field_serial2=value
        else:
            self.__field_serial2=UINT(value,**{'sizeinbytes': 4})
    def __delfield_serial2(self): del self.__field_serial2
    serial2=property(__getfield_serial2, __setfield_serial2, __delfield_serial2, None)
    def __getfield_entrynumber(self):
        return self.__field_entrynumber.getvalue()
    def __setfield_entrynumber(self, value):
        if isinstance(value,UINT):
            self.__field_entrynumber=value
        else:
            self.__field_entrynumber=UINT(value,**{'sizeinbytes': 2})
    def __delfield_entrynumber(self): del self.__field_entrynumber
    entrynumber=property(__getfield_entrynumber, __setfield_entrynumber, __delfield_entrynumber, None)
    def __getfield_name(self):
        return self.__field_name.getvalue()
    def __setfield_name(self, value):
        if isinstance(value,STRING):
            self.__field_name=value
        else:
            self.__field_name=STRING(value,**{'sizeinbytes': 23, 'raiseonunterminatedread': False})
    def __delfield_name(self): del self.__field_name
    name=property(__getfield_name, __setfield_name, __delfield_name, None)
    def __getfield_group(self):
        return self.__field_group.getvalue()
    def __setfield_group(self, value):
        if isinstance(value,UINT):
            self.__field_group=value
        else:
            self.__field_group=UINT(value,**{'sizeinbytes': 2})
    def __delfield_group(self): del self.__field_group
    group=property(__getfield_group, __setfield_group, __delfield_group, None)
    def __getfield_emails(self):
        try: self.__field_emails
        except:
            self.__field_emails=LIST(**{'elementclass': _gen_p_lgvx4600_66, 'length': NUMEMAILS})
        return self.__field_emails.getvalue()
    def __setfield_emails(self, value):
        if isinstance(value,LIST):
            self.__field_emails=value
        else:
            self.__field_emails=LIST(value,**{'elementclass': _gen_p_lgvx4600_66, 'length': NUMEMAILS})
    def __delfield_emails(self): del self.__field_emails
    emails=property(__getfield_emails, __setfield_emails, __delfield_emails, None)
    def __getfield_url(self):
        return self.__field_url.getvalue()
    def __setfield_url(self, value):
        if isinstance(value,STRING):
            self.__field_url=value
        else:
            self.__field_url=STRING(value,**{'sizeinbytes': 72, 'raiseonunterminatedread': False})
    def __delfield_url(self): del self.__field_url
    url=property(__getfield_url, __setfield_url, __delfield_url, None)
    def __getfield_ringtone(self):
        return self.__field_ringtone.getvalue()
    def __setfield_ringtone(self, value):
        if isinstance(value,UINT):
            self.__field_ringtone=value
        else:
            self.__field_ringtone=UINT(value,**{'sizeinbytes': 2})
    def __delfield_ringtone(self): del self.__field_ringtone
    ringtone=property(__getfield_ringtone, __setfield_ringtone, __delfield_ringtone, "ringtone index for a call")
    def __getfield_secret(self):
        return self.__field_secret.getvalue()
    def __setfield_secret(self, value):
        if isinstance(value,BOOL):
            self.__field_secret=value
        else:
            self.__field_secret=BOOL(value,**{'sizeinbytes': 1})
    def __delfield_secret(self): del self.__field_secret
    secret=property(__getfield_secret, __setfield_secret, __delfield_secret, None)
    def __getfield_memo(self):
        return self.__field_memo.getvalue()
    def __setfield_memo(self, value):
        if isinstance(value,STRING):
            self.__field_memo=value
        else:
            self.__field_memo=STRING(value,**{'raiseonunterminatedread': False, 'sizeinbytes': MEMOLENGTH})
    def __delfield_memo(self): del self.__field_memo
    memo=property(__getfield_memo, __setfield_memo, __delfield_memo, None)
    def __getfield_wallpaper(self):
        return self.__field_wallpaper.getvalue()
    def __setfield_wallpaper(self, value):
        if isinstance(value,UINT):
            self.__field_wallpaper=value
        else:
            self.__field_wallpaper=UINT(value,**{'sizeinbytes': 2})
    def __delfield_wallpaper(self): del self.__field_wallpaper
    wallpaper=property(__getfield_wallpaper, __setfield_wallpaper, __delfield_wallpaper, None)
    def __getfield_numbertypes(self):
        try: self.__field_numbertypes
        except:
            self.__field_numbertypes=LIST(**{'elementclass': _gen_p_lgvx4600_73, 'length': NUMPHONENUMBERS})
        return self.__field_numbertypes.getvalue()
    def __setfield_numbertypes(self, value):
        if isinstance(value,LIST):
            self.__field_numbertypes=value
        else:
            self.__field_numbertypes=LIST(value,**{'elementclass': _gen_p_lgvx4600_73, 'length': NUMPHONENUMBERS})
    def __delfield_numbertypes(self): del self.__field_numbertypes
    numbertypes=property(__getfield_numbertypes, __setfield_numbertypes, __delfield_numbertypes, None)
    def __getfield_numbers(self):
        try: self.__field_numbers
        except:
            self.__field_numbers=LIST(**{'elementclass': _gen_p_lgvx4600_75, 'length': NUMPHONENUMBERS})
        return self.__field_numbers.getvalue()
    def __setfield_numbers(self, value):
        if isinstance(value,LIST):
            self.__field_numbers=value
        else:
            self.__field_numbers=LIST(value,**{'elementclass': _gen_p_lgvx4600_75, 'length': NUMPHONENUMBERS})
    def __delfield_numbers(self): del self.__field_numbers
    numbers=property(__getfield_numbers, __setfield_numbers, __delfield_numbers, None)
    def __getfield_unknown20c(self):
        try: self.__field_unknown20c
        except:
            self.__field_unknown20c=UNKNOWN()
        return self.__field_unknown20c.getvalue()
    def __setfield_unknown20c(self, value):
        if isinstance(value,UNKNOWN):
            self.__field_unknown20c=value
        else:
            self.__field_unknown20c=UNKNOWN(value,)
    def __delfield_unknown20c(self): del self.__field_unknown20c
    unknown20c=property(__getfield_unknown20c, __setfield_unknown20c, __delfield_unknown20c, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('serial1', self.__field_serial1, None)
        yield ('entrysize', self.__field_entrysize, None)
        yield ('serial2', self.__field_serial2, None)
        yield ('entrynumber', self.__field_entrynumber, None)
        yield ('name', self.__field_name, None)
        yield ('group', self.__field_group, None)
        yield ('emails', self.__field_emails, None)
        yield ('url', self.__field_url, None)
        yield ('ringtone', self.__field_ringtone, "ringtone index for a call")
        yield ('secret', self.__field_secret, None)
        yield ('memo', self.__field_memo, None)
        yield ('wallpaper', self.__field_wallpaper, None)
        yield ('numbertypes', self.__field_numbertypes, None)
        yield ('numbers', self.__field_numbers, None)
        yield ('unknown20c', self.__field_unknown20c, None)
class _gen_p_lgvx4600_66(BaseProtogenClass):
    'Anonymous inner class'
    __fields=['email']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(_gen_p_lgvx4600_66,self).__init__(**dict)
        if self.__class__ is _gen_p_lgvx4600_66:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(_gen_p_lgvx4600_66,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(_gen_p_lgvx4600_66,kwargs)
        if len(args):
            dict2={'sizeinbytes': 73, 'raiseonunterminatedread': False}
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_email=STRING(*args,**dict2)
    def writetobuffer(self,buf):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_email.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def readfrombuffer(self,buf):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_email=STRING(**{'sizeinbytes': 73, 'raiseonunterminatedread': False})
        self.__field_email.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_email(self):
        return self.__field_email.getvalue()
    def __setfield_email(self, value):
        if isinstance(value,STRING):
            self.__field_email=value
        else:
            self.__field_email=STRING(value,**{'sizeinbytes': 73, 'raiseonunterminatedread': False})
    def __delfield_email(self): del self.__field_email
    email=property(__getfield_email, __setfield_email, __delfield_email, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('email', self.__field_email, None)
class _gen_p_lgvx4600_73(BaseProtogenClass):
    'Anonymous inner class'
    __fields=['numbertype']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(_gen_p_lgvx4600_73,self).__init__(**dict)
        if self.__class__ is _gen_p_lgvx4600_73:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(_gen_p_lgvx4600_73,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(_gen_p_lgvx4600_73,kwargs)
        if len(args):
            dict2={'sizeinbytes': 1}
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_numbertype=UINT(*args,**dict2)
    def writetobuffer(self,buf):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_numbertype.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def readfrombuffer(self,buf):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_numbertype=UINT(**{'sizeinbytes': 1})
        self.__field_numbertype.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_numbertype(self):
        return self.__field_numbertype.getvalue()
    def __setfield_numbertype(self, value):
        if isinstance(value,UINT):
            self.__field_numbertype=value
        else:
            self.__field_numbertype=UINT(value,**{'sizeinbytes': 1})
    def __delfield_numbertype(self): del self.__field_numbertype
    numbertype=property(__getfield_numbertype, __setfield_numbertype, __delfield_numbertype, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('numbertype', self.__field_numbertype, None)
class _gen_p_lgvx4600_75(BaseProtogenClass):
    'Anonymous inner class'
    __fields=['number']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(_gen_p_lgvx4600_75,self).__init__(**dict)
        if self.__class__ is _gen_p_lgvx4600_75:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(_gen_p_lgvx4600_75,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(_gen_p_lgvx4600_75,kwargs)
        if len(args):
            dict2={'sizeinbytes': 33, 'raiseonunterminatedread': False}
            dict2.update(kwargs)
            kwargs=dict2
            self.__field_number=STRING(*args,**dict2)
    def writetobuffer(self,buf):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_number.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def readfrombuffer(self,buf):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_number=STRING(**{'sizeinbytes': 33, 'raiseonunterminatedread': False})
        self.__field_number.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_number(self):
        return self.__field_number.getvalue()
    def __setfield_number(self, value):
        if isinstance(value,STRING):
            self.__field_number=value
        else:
            self.__field_number=STRING(value,**{'sizeinbytes': 33, 'raiseonunterminatedread': False})
    def __delfield_number(self): del self.__field_number
    number=property(__getfield_number, __setfield_number, __delfield_number, None)
    def iscontainer(self):
        return True
    def containerelements(self):
        yield ('number', self.__field_number, None)
class pbreadentryresponse(BaseProtogenClass):
    "Results of reading one entry"
    __fields=['header', 'entry']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(pbreadentryresponse,self).__init__(**dict)
        if self.__class__ is pbreadentryresponse:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(pbreadentryresponse,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(pbreadentryresponse,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_header.writetobuffer(buf)
        self.__field_entry.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def readfrombuffer(self,buf):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_header=pbheader()
        self.__field_header.readfrombuffer(buf)
        self.__field_entry=pbentry()
        self.__field_entry.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_header(self):
        return self.__field_header.getvalue()
    def __setfield_header(self, value):
        if isinstance(value,pbheader):
            self.__field_header=value
        else:
            self.__field_header=pbheader(value,)
    def __delfield_header(self): del self.__field_header
    header=property(__getfield_header, __setfield_header, __delfield_header, None)
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
        yield ('header', self.__field_header, None)
        yield ('entry', self.__field_entry, None)
class pbupdateentryrequest(BaseProtogenClass):
    __fields=['header', 'entry']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(pbupdateentryrequest,self).__init__(**dict)
        if self.__class__ is pbupdateentryrequest:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(pbupdateentryrequest,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(pbupdateentryrequest,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_header
        except:
            self.__field_header=pbheader(**{'command': 0x04, 'flag': 0x01})
        self.__field_header.writetobuffer(buf)
        self.__field_entry.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def readfrombuffer(self,buf):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_header=pbheader(**{'command': 0x04, 'flag': 0x01})
        self.__field_header.readfrombuffer(buf)
        self.__field_entry=pbentry()
        self.__field_entry.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_header(self):
        try: self.__field_header
        except:
            self.__field_header=pbheader(**{'command': 0x04, 'flag': 0x01})
        return self.__field_header.getvalue()
    def __setfield_header(self, value):
        if isinstance(value,pbheader):
            self.__field_header=value
        else:
            self.__field_header=pbheader(value,**{'command': 0x04, 'flag': 0x01})
    def __delfield_header(self): del self.__field_header
    header=property(__getfield_header, __setfield_header, __delfield_header, None)
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
        yield ('header', self.__field_header, None)
        yield ('entry', self.__field_entry, None)
class pbappendentryrequest(BaseProtogenClass):
    __fields=['header', 'entry']
    def __init__(self, *args, **kwargs):
        dict={}
        dict.update(kwargs)
        super(pbappendentryrequest,self).__init__(**dict)
        if self.__class__ is pbappendentryrequest:
            self._update(args,dict)
    def getfields(self):
        return self.__fields
    def _update(self, args, kwargs):
        super(pbappendentryrequest,self)._update(args,kwargs)
        keys=kwargs.keys()
        for key in keys:
            if key in self.__fields:
                setattr(self, key, kwargs[key])
                del kwargs[key]
        if __debug__:
            self._complainaboutunusedargs(pbappendentryrequest,kwargs)
        if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)
    def writetobuffer(self,buf):
        'Writes this packet to the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        try: self.__field_header
        except:
            self.__field_header=pbheader(**{'command': 0x03, 'flag': 0x01})
        self.__field_header.writetobuffer(buf)
        self.__field_entry.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def readfrombuffer(self,buf):
        'Reads this packet from the supplied buffer'
        self._bufferstartoffset=buf.getcurrentoffset()
        self.__field_header=pbheader(**{'command': 0x03, 'flag': 0x01})
        self.__field_header.readfrombuffer(buf)
        self.__field_entry=pbentry()
        self.__field_entry.readfrombuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def __getfield_header(self):
        try: self.__field_header
        except:
            self.__field_header=pbheader(**{'command': 0x03, 'flag': 0x01})
        return self.__field_header.getvalue()
    def __setfield_header(self, value):
        if isinstance(value,pbheader):
            self.__field_header=value
        else:
            self.__field_header=pbheader(value,**{'command': 0x03, 'flag': 0x01})
    def __delfield_header(self): del self.__field_header
    header=property(__getfield_header, __setfield_header, __delfield_header, None)
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
        yield ('header', self.__field_header, None)
        yield ('entry', self.__field_entry, None)
