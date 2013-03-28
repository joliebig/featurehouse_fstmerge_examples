"""The various types used in protocol descriptions
To implement a type used for protocol descriptions,
examine the code for UINTlsb in this file.  Of note:
  - Inherit from BaseProtogenClass
  - Call superclass constructors using super()
  - Do not process any of the args or kwargs in the constructor unless
    you need them to alter how you are constructed
  - At the end of the constructor, call _update if you are the most
    derived class
  - In _update, call super()._update, and then delete keyword arguments
    as you process them (consider using L{BaseProtogenClass._consumekw} function)
  - If you are the most derived class, complain about
    unused keyword arguments (consider using
    L{BaseProtogenClass._complainaboutunusedargs} function)
  - set _bufferstartoffset and _bufferendoffset whenever
    you are read or written from a buffer
  - (optionally) define a getvalue() method that returns
    a better type.  For example if your class is integer
    like then this would return a real int.  If string like,
    then this will return a real string.
  - If you are a container, override iscontainer.  You will
    also need to provide a containerelements() method which
    lets you iterate over the entries.
containerelements method:
  - You should return tuples of (fieldname, fieldvalue, descriptionstring or None)
  - fieldvalue should be the actual object, not a pretty version (eg a STRING not str)
"""
import sys
import calendar
import cStringIO
import re
import time
import common
class ProtogenException(Exception):
    """Base class for exceptions encountered with data marshalling"""
    def __init__(self, *args, **kwargs):
        Exception.__init__(self, *args, **kwargs)
class SizeNotKnownException(ProtogenException):
    "Unable to marshal since size isn't known"
    def __init__(self):
        ProtogenException.__init__(self, "The size of this item is not known and hence cannot be en/decoded")
class ValueNotSetException(ProtogenException):
    "Value not been set"
    def __init__(self):
        ProtogenException.__init__(self, "The value for this object has not been set.")
class ValueException(ProtogenException):
    "Some sort of problem with the value"
    def __init__(self, str):
        ProtogenException.__init__(self,str)
class NotTerminatedException(ProtogenException):
    "The value should have been terminated and wasn't"
    def __init__(self):
        ProtogenException.__init__(self,"The value should have been terminated and wasn't")
class ValueLengthException(ProtogenException):
    "The value is the wrong size (too big or too small)"
    def __init__(self, sz, space):
        ProtogenException.__init__(self, "The value (length %d) is the wrong size for space %d" % (sz,space))
class MissingQuotesException(ProtogenException):
    "The value does not have quotes around it"
    def __init__(self):
        ProtogenException.__init__(self, "The value does not have the required quote characters around it")
class BaseProtogenClass(object):
    """All types are derived from this"""
    def packetsize(self):
        "Returns size in bytes that we occupy"
        b=buffer()
        self.writetobuffer(b)
        return len(b.getvalue())
    def writetobuffer(self, buf):
        "Scribble ourselves to the buf"
        raise NotImplementedError("writetobuffer()")
    def readfrombuffer(self, buf):
        "Get our value from the buffer"
        raise NotImplementedError("readfrombuffer()")
    def getvalue(self):
        "Returns our underlying value if sensible (eg an integer, string or list) else returns self"
        return self
    def packetspan(self):
        """Returns tuple of begining,end offsets from last packet we were read or written from.
        Note that in normal Python style, end is one beyond the last byte we
        actually own"""
        return self._bufferstartoffset, self._bufferendoffset
    def _consumekw(self, dict, consumelist):
        """A helper function for easily setting internal values from the dict
        For each name in consumelist, we look for it in the dict and
        set self._name to the value from dict.  The key is then deleted
        from the dict."""
        for name in consumelist:
            if dict.has_key(name):
                setattr(self, "_"+name, dict[name])
                del dict[name]
    def _complainaboutunusedargs(self, klass, dict):
        """A helper function that will raise an exception if there are unused keyword arguments.
        @Note: that we only complain if in the most derived class, so it is safe
        to always call this helper as the last line of your constructor.
        @param klass:  This should be the class you are calling this function from
        @param dict:   The keyword arguments still in play
        """
        if len(dict) and self.__class__.__mro__[0]==klass:
            raise TypeError('Unexpected keyword args supplied: '+`dict`)
    def _ismostderived(self, klass):
        return self.__class__.__mro__[0]==klass
    def _update(self, args, kwargs):
        return
    def iscontainer(self):
        """Do we contain fields?"""
        return False
    def update(self, *args, **kwargs):
        self._update(args, kwargs)
    def autologwrite(self, buf, logtitle="<written data>"):
        f=sys._getframe() # my frame
        f=f.f_back # my caller
        if f:
            f=f.f_back # their caller
            if f:
                caller=f.f_locals.get("self", None)
                if caller:
                    try:
                        caller.logdata(logtitle, buf.getvalue(), self)
                    except:
                        pass
    def autologread(self, buf, logtitle="<read data>"):
        f=sys._getframe() # my frame
        f=f.f_back # my caller
        if f:
            f=f.f_back # their caller
            if f:
                caller=f.f_locals.get("self", None)
                if caller:
                    try:
                        caller.logdata(logtitle, buf.getdata(), self)
                    except:
                        pass
class UINTlsb(BaseProtogenClass):
    "An integer in Least Significant Byte first order"
    def __init__(self, *args, **kwargs):
        """
        An integer value can be specified in the constructor, or as the value keyword arg.
        @keyword constant:  (Optional) A constant value.  All reads must have this value
        @keyword constantexception: (Optional) Type of exception raised when data doesn't match constant.
        @keyword sizeinbytes: (Mandatory for writing, else Optional) How big we are in bytes
        @keyword default:  (Optional) Our default value
        @keyword value: (Optional) The value
        """
        super(UINTlsb, self).__init__(*args, **kwargs)
        self._constant=None
        self._constantexception=ValueError
        self._sizeinbytes=None
        self._value=None
        self._default=None
        if self._ismostderived(UINTlsb):
            self._update(args,kwargs)
    def _update(self, args, kwargs):
        super(UINTlsb,self)._update(args, kwargs)
        self._consumekw(kwargs, ("constant", "constantexception", "sizeinbytes", "default", "value"))
        self._complainaboutunusedargs(UINTlsb,kwargs)
        if len(args)==0:
            pass
        elif len(args)==1:
            self._value=int(args[0])
        else:
            raise TypeError("Unexpected arguments "+`args`)
        if self._value is None and self._default is not None:
            self._value=self._default
        if self._value is None and self._constant is not None:
            self._value=self._constant
        if self._constant is not None and self._constant!=self._value:
            raise self._constantexception("This field is a constant of %d.  You tried setting it to %d" % (self._constant, self._value))
    def readfrombuffer(self, buf):
        if self._sizeinbytes is None:
            raise SizeNotKnownException()
        self._bufferstartoffset=buf.getcurrentoffset()
        res=0
        shift=0
        for dummy in range(self._sizeinbytes):
            res|=buf.getnextbyte()<<shift
            shift+=8
        self._value=res
        self._bufferendoffset=buf.getcurrentoffset()
        if self._constant is not None and self._value!=self._constant:
            raise self._constantexception("The value read should be a constant of %d, but was %d instead" % (self._constant, self._value))
    def writetobuffer(self, buf):
        if self._sizeinbytes is None:
            raise SizeNotKnownException()
        if self._value is None:
            raise ValueNotSetException()
        self._bufferstartoffset=buf.getcurrentoffset()
        res=self._value
        for dummy in range(self._sizeinbytes):
            buf.appendbyte(res&0xff)
            res>>=8
        self._bufferendoffset=buf.getcurrentoffset()
    def packetsize(self):
        if self._sizeinbytes is None:
            raise SizeNotKnownException()
        return self._sizeinbytes
    def getvalue(self):
        """Returns the integer we are"""
        if self._value is None:
            raise ValueNotSetException()
        return self._value
class BOOLlsb(UINTlsb):
    "An Boolean in Least Significant Byte first order"
    def __init__(self, *args, **kwargs):
        """
        A boolean value can be specified in the constructor.
        Keyword arguments are the same a UINTlsb
        """
        super(BOOLlsb, self).__init__(*args, **kwargs)
        if self._ismostderived(BOOLlsb):
            self._update(args,kwargs)
    def _update(self, args, kwargs):
        super(BOOLlsb,self)._update(args,kwargs)
        self._complainaboutunusedargs(BOOLlsb,kwargs)
        self._boolme()
    def _boolme(self):
        if self._value is not None:
            self._value=bool(self._value)
    def readfrombuffer(self, buf):
        UINTlsb.readfrombuffer(self,buf)
        self._boolme()
class STRING(BaseProtogenClass):
    "A text string DEPRECATED USE USTRING "
    def __init__(self, *args, **kwargs):
        """
        A string value can be specified to this constructor, or in the value keyword arg.
        @keyword constant: (Optional) A constant value.  All reads must have this value
        @keyword terminator: (Default=0) The string terminator (or None).  If set there will
             always be a terminator when writing.  The terminator is not returned when getting
             the value.
        @keyword pad: (Default=0) The padding byte if fixed length when writing, or stripped off
                       when reading
        @keyword sizeinbytes: (Optional) Set if fixed length.
             If not set, then the terminator will be used to find the end of strings on reading.
             If not set and the terminator is None, then reads will be entire rest of buffer.
        @keyword maxsizeinbytes: (Optional) Max string length.  Used together
             with terminator to limit the max length of the value.
             TODO: Need to add this to USTRING also.
        @keyword default: (Optional) Our default value
        @keyword raiseonunterminatedread: (Default True) raise L{NotTerminatedException} if there is
             no terminator on the value being read in.  terminator must also be set.
        @keyword raiseontruncate: (Default True) raise L{ValueLengthException} if the supplied
             value is too large to fit within sizeinbytes.
        @keyword value: (Optional) Value
        @keyword pascal: (Default False) The string is preceded with one byte giving the length
                         of the string (including terminator if there is one)
        """
        super(STRING, self).__init__(*args, **kwargs)
        self._constant=None
        self._terminator=0
        self._pad=0
        self._sizeinbytes=None
        self._default=None
        self._raiseonunterminatedread=True
        self._raiseontruncate=True
        self._value=None
        self._pascal=False
        self._maxsizeinbytes=None
        if self._ismostderived(STRING):
            self._update(args,kwargs)
    def _update(self, args, kwargs):
        super(STRING,self)._update(args, kwargs)
        self._consumekw(kwargs, ("constant", "terminator", "pad", "pascal",
        "sizeinbytes", "default", "raiseonunterminatedread", "value",
                                 "raiseontruncate", "maxsizeinbytes"))
        self._complainaboutunusedargs(STRING,kwargs)
        if len(args)==0:
            pass
        elif len(args)==1:
            self._value=common.forceascii(args[0])
            if self._constant is not None and self._constant!=self._value:
                raise ValueException("This field is a constant of '%s'.  You tried setting it to '%s'" % (self._constant, self._value))
        else:
            raise TypeError("Unexpected arguments "+`args`)
        if self._value is None and self._default is not None:
            self._value=self._default
        if self._value is not None:
            self._value=str(self._value) # no unicode here!
            l=len(self._value)
            if self._sizeinbytes is not None:
                if self._terminator is not None:
                    l+=1
                if l>self._sizeinbytes:
                    if self._raiseontruncate:
                        raise ValueLengthException(l, self._sizeinbytes)
                    self._value=self._value[:self._sizeinbytes]
                    if len(self._value) and self._terminator is not None:
                        self._value=self._value[:-1]
            elif self._maxsizeinbytes is not None:
                if l>self._maxsizeinbytes:
                    if self._raiseontruncate:
                        raise ValueLengthException(l, self._maxsizeinbytes)
                    self._value=self._value[:self._maxsizeinbytes]
    def readfrombuffer(self, buf):
        self._bufferstartoffset=buf.getcurrentoffset()
        flush=0
        if self._pascal: 
            if self._sizeinbytes is None:
                self._sizeinbytes=buf.getnextbyte()
            else:
                temp=self._sizeinbytes-1
                self._sizeinbytes=buf.getnextbyte()
                flush=temp-self._sizeinbytes
                if(temp < 0):
                    raise ValueLengthException()
        if self._sizeinbytes is not None:
            self._value=buf.getnextbytes(self._sizeinbytes)
            if self._terminator is not None:
                pos=self._value.find(chr(self._terminator))
                if pos>=0:
                    self._value=self._value[:pos]
                elif self._raiseonunterminatedread:
                    raise NotTerminatedException()
            elif self._pad is not None:
                while len(self._value) and self._value[-1]==chr(self._pad):
                    self._value=self._value[:-1]
        else:
            if self._terminator is None:
                self._value=buf.getremainingbytes()
            else:
                self._value=""
                while buf.hasmore():
                    self._value+=chr(buf.getnextbyte())
                    if self._value[-1]==chr(self._terminator):
                        break
                if self._value[-1]!=chr(self._terminator):
                    if self._raiseonunterminatedread:
                        raise NotTerminatedException()
                else:
                    self._value=self._value[:-1]
        if self._maxsizeinbytes is not None:
            self._value=self._value[:self._maxsizeinbytes]
        if self._constant is not None and self._value!=self._constant:
            raise ValueException("The value read was not the constant")
        if(flush):
            buf.getnextbytes(flush)
        self._bufferendoffset=buf.getcurrentoffset()
    def writetobuffer(self, buf):
        if self._value is None:
            raise ValueNotSetException()
        self._bufferstartoffset=buf.getcurrentoffset()
        l=len(self._value)
        if self._terminator is not None:
            l+=1
        if self._pascal:
            buf.appendbyte(l)
            l+=1
        buf.appendbytes(self._value)
        if self._terminator is not None:
            buf.appendbyte(self._terminator)
        if self._sizeinbytes is not None:
            if l<self._sizeinbytes:
                buf.appendbytes(chr(self._pad)*(self._sizeinbytes-l))
        self._bufferendoffset=buf.getcurrentoffset()
    def packetsize(self):
        if self._sizeinbytes is not None:
            return self._sizeinbytes
        if self._value is None:
            raise ValueNotSetException()
        l=len(self._value)
        if self._terminator is not None:
            l+=1
        if self._pascal:
            l+=1
        return l
    def getvalue(self):
        """Returns the string we are"""
        if self._value is None:
            raise ValueNotSetException()
        return self._value
class USTRING(BaseProtogenClass):
    "A text string that supports configurable encodings"
    def __init__(self, *args, **kwargs):
        """
        A string value can be specified to this constructor, or in the value keyword arg.
        @keyword constant: (Optional) A constant value.  All reads must have this value
        @keyword terminator: (Default=0) The string terminator (or None).  If set there will
             always be a terminator when writing.  The terminator is not returned when getting
             the value.
        @keyword terminator_length: (Default=1) (min:1, max:4)The length of the string terminator. 
             This keyword is not used if the terminator is None. Multi-byte terminators are treated 
             as LSB when read from the phone.
        @keyword pad: (Default=0) The padding byte if fixed length when writing, or stripped off
                       when reading
        @keyword sizeinbytes: (Optional) Set if fixed length.
             If not set, then the terminator will be used to find the end of strings on reading.
             If not set and the terminator is None, then reads will be entire rest of buffer.
        @keyword default: (Optional) Our default value
        @keyword raiseonunterminatedread: (Default True) raise L{NotTerminatedException} if there is
             no terminator on the value being read in. Terminator must also be set.
        @keyword raiseontruncate: (Default True) raise L{ValueLengthException} if the supplied
             value is too large to fit within sizeinbytes.
        @keyword value: (Optional) Value
        @keyword pascal: (Default False) The string is preceded with one byte giving the length
                         of the string (including terminator if there is one)
        @keyword encoding: (Default 'ascii') The charset to use when reading/writing to a buffer
        @keyword read_encoding: (Default keyword:encoding) The charset to use when reading from a buffer
        @keyword write_encoding: (Default keyword:encoding) The charset to use when writing to a buffer
        """
        super(USTRING, self).__init__(*args, **kwargs)
        self._constant=None
        self._terminator=0
        self._pad=0
        self._sizeinbytes=None
        self._default=None
        self._raiseonunterminatedread=True
        self._raiseontruncate=True
        self._value=None
        self._pascal=False
        self._encoding='ascii'
        self._read_encoding=None
        self._write_encoding=None
        self._terminator_length=1
        if self._ismostderived(USTRING):
            self._update(args,kwargs)
    def _update(self, args, kwargs):
        super(USTRING,self)._update(args, kwargs)
        self._consumekw(kwargs, ("constant", "terminator", "pad", "pascal",
        "sizeinbytes", "default", "raiseonunterminatedread", "value", "raiseontruncate",
        "encoding", "read_encoding", "write_encoding"))
        self._complainaboutunusedargs(USTRING,kwargs)
        if self._read_encoding==None:
            self._read_encoding=self._encoding
        if self._write_encoding==None:
            self._write_encoding=self._encoding
        if self._terminator_length < 1 or self._terminator_length > 4:
            raise ValueException("Terminator length outside allowed range of 1-4.  You tried setting it to %d" % self._terminator_length)
        if len(args)==0:
            pass
        elif len(args)==1:
            self._value=args[0]
        else:
            raise TypeError("Unexpected arguments "+`args`)
        if self._value is None and self._default is not None:
            self._value=self._default
        if self._value is not None:
            if not isinstance(self._value, (str, unicode)):
                temp=str(self._value)
                self._value=unicode(temp, 'ascii', 'replace')
            elif not isinstance(self._value, unicode):
                self._value=unicode(self._value, 'ascii', 'replace')
        if self._constant is not None and self._constant!=self._value:
            raise ValueException("This field is a constant of '%s'.  You tried setting it to '%s'" % (self._constant, self._value))
        try:
            test=self.convert_for_write()
        except UnicodeEncodeError:
            raise common.PhoneStringEncodeException(self._value, uni_string_codec) 
        if self._value is not None:
            if self._sizeinbytes is not None:
                l=len(test)
                if self._terminator is not None:
                    l+=1
                if l>self._sizeinbytes:
                    if self._raiseontruncate:
                        raise ValueLengthException(l, self._sizeinbytes)
                    self._value=self._value[:self._sizeinbytes]
                    term_len=0
                    if self._terminator!=None:
                        term_len=self._terminator_length
                    while (len(self.convert_for_write())+term_len)>self._sizeinbytes:
                        self._value=self._value[:-1]
    def readfrombuffer(self, buf):
        self._bufferstartoffset=buf.getcurrentoffset()
        flush=0
        _value=''
        if self._pascal: 
            if self._sizeinbytes is None:
                self._sizeinbytes=buf.getnextbyte()
            else:
                temp=self._sizeinbytes-1
                self._sizeinbytes=buf.getnextbyte()
                flush=temp-self._sizeinbytes
                if(temp < 0):
                    raise ValueLengthException()
        if self._sizeinbytes is not None:
            _value=buf.getnextbytes(self._sizeinbytes)
            if self._terminator is not None:
                pos=-1
                for i in range(0, self._sizeinbytes, self._terminator_length):
                    term=0
                    for j in range(self._terminator_length):
                        term+=ord(_value[i+j])<<(j*8)
                    if term==self._terminator:
                        pos=i
                        break
                if pos>=0:
                    _value=_value[:pos]
                elif self._raiseonunterminatedread:
                    raise NotTerminatedException()
            elif self._pad is not None:
                while len(_value) and _value[-1]==chr(self._pad):
                    _value=_value[:-1]
        else:
            if self._terminator is None:
                _value=buf.getremainingbytes()
            else:
                _value=""
                count=0
                term=0
                while buf.hasmore():
                    _value+=chr(buf.getnextbyte())
                    count+=1
                    if (count % self._terminator_length)==0:
                        term=0
                        for j in range(self._terminator_length):
                            term=(term<<8)+ord(_value[count-1-j])
                        if term==self._terminator:
                            break
                if term!=self._terminator and self._raiseonunterminatedread:
                    raise NotTerminatedException()
                else:
                    _value=_value[:-1]
        if self._constant is not None and _value!=self._constant:
            raise ValueException("The value read was not the constant")
        if(flush):
            buf.getnextbytes(flush)
        self._bufferendoffset=buf.getcurrentoffset()
        self._value=_value
    def writetobuffer(self, buf):
        if self._value is None:
            raise ValueNotSetException()
        self._bufferstartoffset=buf.getcurrentoffset()
        temp_str=self.convert_for_write()
        l=len(temp_str)
        if self._terminator is not None:
            l+=1
        if self._pascal:
            buf.appendbyte(l)
            l+=1
        buf.appendbytes(temp_str)
        term=self._terminator
        if self._terminator is not None:
            for j in range(self._terminator_length):
                buf.appendbyte((term & 0xFF))
                term=term>>8
        if self._sizeinbytes is not None:
            if l<self._sizeinbytes:
                buf.appendbytes(chr(self._pad)*(self._sizeinbytes-l))
        self._bufferendoffset=buf.getcurrentoffset()
    def convert_for_write(self):
        if not isinstance(self._value, unicode):
            return self._value
        try:
            temp_str=common.encode_with_degrade(self._value, self._write_encoding)
        except UnicodeError:
            raise common.PhoneStringEncodeException(self._value, self._write_encoding) 
        return temp_str
    def packetsize(self):
        if self._sizeinbytes is not None:
            return self._sizeinbytes
        if self._value is None:
            raise ValueNotSetException()
        l=len(self.convert_for_write())
        if self._terminator is not None:
            l+=1
        return l
    def getvalue(self):
        """Returns the string we are"""
        if self._value is None:
            raise ValueNotSetException()
        if not isinstance(self._value, unicode):
            try:
                self._value=self._value.decode(self._read_encoding)
            except UnicodeDecodeError:
                raise common.PhoneStringDecodeException(self._value, self._read_encoding) 
        return self._value
class SEVENBITSTRING(BaseProtogenClass):
    """A text string where ASCII characters are stored as packed 7 bit characters.  This is
    typically used in SMS messages."""
    def __init__(self, *args, **kwargs):
        """
        @keyword terminator: (Default=\x00) The termination character
        @keyword sizeinbytes: Amount of space the string sits in
        """
        super(SEVENBITSTRING, self).__init__(*args, **kwargs)
        self._value=None
        self._terminator='\x00'
        self._sizeinbytes=None
        if self._ismostderived(SEVENBITSTRING):
            self._update(args,kwargs)
    def _update(self, args, kwargs):
        super(SEVENBITSTRING,self)._update(args, kwargs)
        self._consumekw(kwargs, ("terminator", "value", "sizeinbytes"))
        self._complainaboutunusedargs(SEVENBITSTRING, kwargs)
        if len(args):
            raise TypeError("Unexpected arguments "+`args`)
        if self._sizeinbytes is None:
            raise ValueException("You must specify a size in bytes")
    def readfrombuffer(self, buf):
        self._bufferstartoffset=buf.getcurrentoffset()
        bytes=buf.getnextbytes(self._sizeinbytes)
        self._value=common.decodecharacterbits(bytes, bitsperchar=7, charconv=chr, terminator=self._terminator)
        self._bufferendoffset=buf.getcurrentoffset()
    def getvalue(self):
        """Returns the string we are"""
        if self._value is None:
            raise ValueNotSetException()
        return self._value        
class SMSDATE(BaseProtogenClass):
    """A date as used in SMS messages.  It is six bytes long with the
    bytes being year month day hour minute second.  From stuff on the
    web, it appears GSM phones swap each nybble."""
    def __init__(self, *args, **kwargs):
        """@keyword sizeinbytes: (optional) Must be six"""
        super(SMSDATE, self).__init__(*args, **kwargs)
        self._values=None
        self._sizeinbytes=6
        if self._ismostderived(SMSDATE):
            self._update(args, kwargs)
    def _update(self, args, kwargs):
        super(SMSDATE, self)._update(args, kwargs)
        self._consumekw(kwargs, ("sizeinbytes",))
        self._complainaboutunusedargs(SMSDATE, kwargs)
        if len(args):
            raise TypeError("Unexpected arguments "+`args`)
        if self._sizeinbytes != 6:
            raise ValueNotSetException("You can only specify 6 as the size in bytes")
    def readfrombuffer(self, buf):
        self._bufferstartoffset=buf.getcurrentoffset()
        year=buf.getnextbyte()
        if year<0:
            year+=1900
        else:
            year+=2000
        month=buf.getnextbyte()
        day=buf.getnextbyte()
        hour=buf.getnextbyte()
        minute=buf.getnextbyte()
        second=buf.getnextbyte()
        self._value=year,month,day, hour,minute,second
        self._bufferendoffset=buf.getcurrentoffset()
    def getvalue(self):
         """Returns the  ISO date time string we are"""
         if self._value is None:
             raise ValueNotSetException()
         return "%d%02d%02dT%02d%02d%02d" % self._value
class CSVSTRING(BaseProtogenClass):
    """A text string enclosed in quotes, with a way to escape quotes that a supposed
    to be part of the string.  Typical of Samsung phones."""
    def __init__(self, *args, **kwargs):
        """
        A string value can be specified to this constructor, or in the value keyword arg.
        @keyword constant: (Optional) A constant value.  All reads must have this value
        @keyword terminator: (Default=,) The string terminator (or None).  If set there will
             always be a terminator when writing.  The terminator is not returned when getting
             the value.
        @keyword quotechar: (Default=Double Quote) Quote character that surrounds string
        @keyword readescape: (Default=True) Interpret PPP escape char (0x7d)
        @keywors writeescape: (Default=False) Escape quotechar.  If false, drop quotechar in string.
        @keyword maxsizeinbytes: (Optional) On writing, truncate strings longer than this (length is before
                       any escaping and quoting
        @keyword default: (Optional) Our default value
        @keyword raiseonunterminatedread: (Default True) raise L{NotTerminatedException} if there is
             no terminator on the value being read in.  terminator must also be set.
        @keyword raiseontruncate: (Default True) raise L{ValueLengthException} if the supplied
             value is too large to fit within sizeinbytes.
        @keyword raiseonmissingquotes: (Default True) raise L{MissingQuotesException} if the string does
             not have quote characters around it
        @keyword value: (Optional) Value
        @keyword invalidchars: (Default=quotechar) A string containing invalid
             characters which would be removed before writing to buffer.
        """
        super(CSVSTRING, self).__init__(*args, **kwargs)
        self._constant=None
        self._terminator=ord(',')
        self._quotechar=ord('"')
        self._readescape=True
        self._writeescape=False
        self._maxsizeinbytes=None
        self._default=None
        self._raiseonunterminatedread=True
        self._raiseontruncate=True
        self._raiseonmissingquotes=True
        self._invalidchars=chr(self._quotechar)
        self._value=None
        if self._ismostderived(CSVSTRING):
            self._update(args,kwargs)
    def _update(self, args, kwargs):
        super(CSVSTRING,self)._update(args, kwargs)
        self._consumekw(kwargs, ("constant", "terminator", "quotechar", "readescape",
                                 "writeescape", "maxsizeinbytes", "default",
                                 "raiseonunterminatedread", "value",
                                 "raiseontruncate", "raiseonmissingquotes",
                                 "invalidchars"))
        self._complainaboutunusedargs(CSVSTRING,kwargs)
        if len(args)==0:
            pass
        elif len(args)==1:
            self._value=common.forceascii(args[0])
            if self._constant is not None and self._constant!=self._value:
                raise ValueException("This field is a constant of '%s'.  You tried setting it to '%s'" % (self._constant, self._value))
        else:
            raise TypeError("Unexpected arguments "+`args`)
        if self._value is None and self._default is not None:
            self._value=self._default
        if self._value is not None:
            self._value=str(self._value) # no unicode here!
            if self._invalidchars:
                self._value=re.sub(r'[%s]'%self._invalidchars, r'', self._value)
            if self._maxsizeinbytes is not None:
                l=len(self._value)
                if l>self._maxsizeinbytes:
                    if self._raiseontruncate:
                        raise ValueLengthException(l, self._maxsizeinbytes)
                    self._value=self._value[:self._maxsizeinbytes]
    def readfrombuffer(self, buf):
        self._bufferstartoffset=buf.getcurrentoffset()
        if self._terminator is None:
            self._value=buf.getremainingbytes()
        else:
            self._value=chr(buf.getnextbyte())
            if self._value == ',':
                self._value = ''
            else:
                inquotes=False
                if self._quotechar is not None:
                    if self._value[0]==chr(self._quotechar):
                        inquotes=True
                while buf.hasmore():
                    self._value+=chr(buf.getnextbyte())
                    if inquotes:
                        if self._value[-1]==chr(self._quotechar):
                            inquotes=False
                    else:
                        if self._value[-1]==chr(self._terminator):
                            break
                if self._value[-1]==self._terminator:
                    if self._raiseonunterminatedread:
                        raise NotTerminatedException()
                else:
                    self._value=self._value[:-1]
        if self._quotechar is not None and self._value:
            if self._value[0]==chr(self._quotechar) and self._value[-1]==chr(self._quotechar):
                self._value=self._value[1:-1]
            else:
                raise MissingQuotesException()
        if self._readescape:
            self._value=common.pppunescape(self._value)
        if self._constant is not None and self._value!=self._constant:
            raise ValueException("The value read was not the constant")
        self._bufferendoffset=buf.getcurrentoffset()
    def writetobuffer(self, buf):
        if self._value is None:
            raise ValueNotSetException()
        self._bufferstartoffset=buf.getcurrentoffset()
        if self._quotechar is not None:
            buf.appendbyte(self._quotechar)
        buf.appendbytes(self._value)
        if self._quotechar is not None:
            buf.appendbyte(self._quotechar)
        if self._terminator is not None:
            buf.appendbyte(self._terminator)
        self._bufferendoffset=buf.getcurrentoffset()
    def packetsize(self):
        if self._sizeinbytes is not None:
            return self._sizeinbytes
        if self._value is None:
            raise ValueNotSetException()
        l=len(self._value)
        if self._terminator is not None:
            l+=1
        return l
    def getvalue(self):
        """Returns the string we are"""
        if self._value is None:
            raise ValueNotSetException()
        return self._value
class CSVINT(CSVSTRING):
    """Integers in CSV lines"""
    def __init__(self, *args, **kwargs):
        super(CSVINT,self).__init__(*args, **kwargs)
        self._quotechar=None
        if self._ismostderived(CSVINT):
            self._update(args,kwargs)
    def _update(self, args, kwargs):
        for k in 'constant', 'default', 'value':
            if kwargs.has_key(k):
                kwargs[k]=str(kwargs[k])
        if len(args)==0:
            pass
        elif len(args)==1:
            args=(str(args[0]),)
        else:
            raise TypeError("expected integer as arg")
        super(CSVINT,self)._update(args,kwargs)
        self._complainaboutunusedargs(CSVINT,kwargs)
    def getvalue(self):
        """Convert the string into an integer
        @rtype: integer
        """
        val=super(CSVINT,self).getvalue()
        try:
            ival=int(val)
        except:
            try:
                ival=int(self._default)
            except:
                raise ValueException("The field '%s' is not an integer" % (val))
        return ival
class CSVDATE(CSVSTRING):
    """Dates in CSV lines"""
    def __init__(self, *args, **kwargs):
        super(CSVDATE,self).__init__(*args, **kwargs)
        self._valuedate=(0,0,0) # Year month day
        self._quotechar=None
        if self._ismostderived(CSVDATE):
            self._update(args,kwargs)
    def _update(self, args, kwargs):
        for k in 'constant', 'default', 'value':
            if kwargs.has_key(k):
                kwargs[k]=self._converttostring(kwargs[k])
        if len(args)==0:
            pass
        elif len(args)==1:
            args=(self._converttostring(args[0]),)
        else:
            raise TypeError("expected (year,month,day) as arg")
        super(CSVDATE,self)._update(args, kwargs) # we want the args
        self._complainaboutunusedargs(CSVDATE,kwargs)
    def getvalue(self):
        """Unpack the string into the date
        @rtype: tuple
        @return: (year, month, day)
        """
        s=super(CSVDATE,self).getvalue()
        val=s.split("/") # List of of Month, day, year
        if len(val)<2:
            year = 0
            month = 0
            day = 0
        else:
            year=int(val[2])
            month=int(val[0])
            day=int(val[1])
        return (year, month, day)
    def _converttostring(self, date):
        if len(date)>=3:
            year,month,day=date[:3]
            if month>0 or day>0 or year>0:
                s='%2.2d/%2.2d/%4.4d'%(month, day, year)
            else:
                s=""
        else:
            s=""
        return s
class CSVTIME(CSVSTRING):
    """Timestamp in CSV lines"""
    def __init__(self, *args, **kwargs):
        super(CSVTIME,self).__init__(*args, **kwargs)
        self._valuetime=(0,0,0,0,0,0) # Year month day, hour, minute, second
        self._quotechar=None
        if self._ismostderived(CSVTIME):
            self._update(args,kwargs)
    def _update(self, args, kwargs):
        for k in 'constant', 'default', 'value':
            if kwargs.has_key(k):
                kwargs[k]=self._converttostring(kwargs[k])
        if len(args)==0:
            pass
        elif len(args)==1:
            args=(self._converttostring(args[0]),)
        else:
            raise TypeError("expected (year,month,day) as arg")
        super(CSVTIME,self)._update(args, kwargs) # we want the args
        self._complainaboutunusedargs(CSVTIME,kwargs)
    def getvalue(self):
        """Unpack the string into the date
        @rtype: tuple
        @return: (year, month, day)
        """
        s=super(CSVTIME,self).getvalue()
        year=int(s[0:4])
        month=int(s[4:6])
        day=int(s[6:8])
        hour=int(s[9:11])
        minute=int(s[11:13])
        second=int(s[13:15])
        return (year, month, day, hour, minute, second)
    def _converttostring(self, time):
        if len(time)>=6:
            year,month,day,hour,minute,second=time[:6]
            s='%4.4d%2.2d%2.2dT%2.2d%2.2d%2.2d'%(year, month, day, hour, minute, second)
        else:
            s=""
        return s
class COUNTEDBUFFEREDSTRING(BaseProtogenClass):
    """A string as used on Audiovox.  There is a one byte header saying how long the string
    is, followed by the string in a fixed sized buffer"""
    def __init__(self, *args, **kwargs):
        """
        A string value can be specified to this constructor, or in the value keyword arg.
        @keyword constant: (Optional) A constant value.  All reads must have this value
        @keyword pad: (Default=32 - space) When writing, what to pad the rest of the buffer with
        @keyword default: (Optional) Our default value
        @keyword raiseontruncate: (Default True) raise L{ValueLengthException} if the supplied
             value is too large to fit within the buffer.
        @keyword value: (Optional) Value
        @keyword sizeinbytes: (Mandatory) Size of the buffer, including the count byte
        """
        super(COUNTEDBUFFEREDSTRING,self).__init__(*args, **kwargs)
        self._constant=None
        self._pad=32
        self._sizeinbytes=None
        self._default=None
        self._raiseontruncate=True
        self._value=None
        if self._ismostderived(COUNTEDBUFFEREDSTRING):
            self._update(args, kwargs)
    def _update(self, args, kwargs):
        super(COUNTEDBUFFEREDSTRING,self)._update(args, kwargs)
        self._consumekw(kwargs, ("constant", "pad", "sizeinbytes", "default", "raiseontruncate", "value"))
        self._complainaboutunusedargs(COUNTEDBUFFEREDSTRING,kwargs)
        if len(args)==0:
            pass
        elif len(args)==1:
            self._value=str(args[0])
            if self._constant is not None and self._constant!=self._value:
                raise ValueException("This field is a constant of '%s'.  You tried setting it to '%s'" % (self._constant, self._value))
        else:
            raise TypeError("Unexpected arguments "+`args`)
        if self._value is None and self._default is not None:
            self._value=self._default
        if self._sizeinbytes is None:
            raise ValueException("sizeinbytes must be specified for COUNTEDBUFFEREDSTRING")
        if self._value is not None:
            l=len(self._value)
            if l>self._sizeinbytes-1:
                if self._raiseontruncate:
                    raise ValueLengthException(l, self._sizeinbytes-1)
                self._value=self._value[:self._sizeinbytes-1]
    def readfrombuffer(self, buf):
        assert self._sizeinbytes is not None
        self._bufferstartoffset=buf.getcurrentoffset()
        strlen=buf.getnextbyte()
        if strlen>self._sizeinbytes-1:
            raise ValueException("counter specifies size of %d which is greater than remaining stringbuffer size of %d!" % (strlen, self._sizeinbytes-1))
        self._value=buf.getnextbytes(self._sizeinbytes-1) # -1 due to counter byte
        self._value=self._value[:strlen]
        if self._constant is not None and self._value!=self._constant:
            raise ValueException("The value read was not the constant")
        self._bufferendoffset=buf.getcurrentoffset()
    def writetobuffer(self, buf):
        assert self._sizeinbytes is not None
        if self._value is None:
            raise ValueNotSetException()
        self._bufferstartoffset=buf.getcurrentoffset()
        buf.appendbyte(len(self._value))
        buf.appendbytes(self._value)
        if len(self._value)+1<self._sizeinbytes:
            buf.appendbytes(chr(self._pad)*(self._sizeinbytes-1-len(self._value)))
        self._bufferendoffset=buf.getcurrentoffset()
    def packetsize(self):
        assert self._sizeinbytes is not None
        return self._sizeinbytes
    def getvalue(self):
        """Returns the string we are"""
        if self._value is None:
            raise ValueNotSetException()
        return self._value
class DATA(BaseProtogenClass):
    "A block of bytes"
    def __init__(self, *args, **kwargs):
        """
        A data value can be specified to this constructor or in the value keyword arg
        @keyword constant: (Optional) A constant value.  All reads must have this value
        @keyword pad: (Default=0) The padding byte if fixed length when writing and the
             value isn't long enough
        @keyword sizeinbytes: (Optional) Set if fixed length.
             If not set, then the rest of the packet will be consumed on reads.
        @keyword default: (Optional) Our default value
        @keyword raiseonwrongsize: (Default True) raise L{ValueLengthException} if the supplied
             value is too large to fit within sizeinbytes.
        """
        super(DATA, self).__init__(*args, **kwargs)
        self._constant=None
        self._pad=0
        self._sizeinbytes=None
        self._default=None
        self._raiseonwrongsize=True
        self._value=None
        if self._ismostderived(DATA):
            self._update(args,kwargs)
    def _update(self, args, kwargs):
        super(DATA,self)._update(args, kwargs)
        self._consumekw(kwargs, ("constant", "pad", "sizeinbytes", "default", "raiseonwrongsize", "value"))
        self._complainaboutunusedargs(DATA,kwargs)
        if len(args)==0:
            pass
        elif len(args)==1:
            self._value=args[0]
            if self._constant is not None and self._constant!=self._value:
                raise ValueException("This field is a constant and you set it to a different value")
        else:
            raise TypeError("Unexpected arguments "+`args`)
        if self._value is None and self._default is not None:
            self._value=self._default
        if self._value is not None:
            if self._sizeinbytes is not None:
                l=len(self._value)
                if l<self._sizeinbytes:
                    if self._pad is not None:
                        self._value+=chr(self._pad)*(self._sizeinbytes-l)
                l=len(self._value)
                if l!=self._sizeinbytes:
                    if self._raiseonwrongsize:
                        raise ValueLengthException(l, self._sizeinbytes)
                    else:
                        self._value=self._value[:self._sizeinbytes]
    def readfrombuffer(self, buf):
        self._bufferstartoffset=buf.getcurrentoffset()
        if self._sizeinbytes is not None:
            self._value=buf.getnextbytes(self._sizeinbytes)
        else:
            self._value=buf.getremainingbytes()
        if self._constant is not None and self._value!=self._constant:
            raise ValueException("The value read was not the constant")
        self._bufferendoffset=buf.getcurrentoffset()
    def writetobuffer(self, buf):
        if self._value is None:
            raise ValueNotSetException()
        self._bufferstartoffset=buf.getcurrentoffset()
        buf.appendbytes(self._value)
        self._bufferendoffset=buf.getcurrentoffset()
    def packetsize(self):
        if self._sizeinbytes is not None:
            return self._sizeinbytes
        if self._value is None:
            raise ValueNotSetException()
        l=len(self._value)
        return l
    def getvalue(self):
        """Returns the bytes we are"""
        if self._value is None:
            raise ValueNotSetException()
        return self._value
class UNKNOWN(DATA):
    "A block of bytes whose purpose we don't know"
    def __init__(self, *args, **kwargs):
        """
        Same arguments as L{DATA.__init__}.  We default to a block
        of pad chars (usually \x00)
        """
        dict={'pad':0 , 'default': ""}
        dict.update(kwargs)
        super(UNKNOWN,self).__init__(*args, **dict)
        if self._ismostderived(UNKNOWN):
            self._update(args,dict)
    def _update(self, args, kwargs):
        super(UNKNOWN,self)._update(args, kwargs)
        self._complainaboutunusedargs(UNKNOWN,kwargs)
        if len(args)==1:
            self._value=args[0]
        elif len(args)>1:
            raise TypeError("Unexpected arguments "+`args`)
class LIST(BaseProtogenClass):
    """A list of items
    You can generally treat this class as though it is a list.  Note that some
    list like methods haven't been implemented (there are so darn many!)  If you
    are missing one you want to use, please add it to this class.
    """
    def __init__(self, *args, **kwargs):
        """
        You can pass objects to start the list with, or to the value keyword arg
        @keyword createdefault:  (Default False) Creates default members of the list if enough
            were not supplied before writing.
        @keyword length:  (Optional) How many items there are in the list
        @keyword raiseonbadlength: (Default True) raises L{ValueLengthException} if there are
            the wrong number of items in the list.  Note that this checking is only done
            when writing or reading from a buffer.  length must be set for this to have any
            effect.  If you have createdefault set then having less than length elements will
            not cause the exception.
        @keyword elementclass: (Mandatory) The class of each element
        @keyword elementinitkwargs: (Optional) KWargs for the constructor of each element
        @keyword value: (Optional) Value
        """
        self._thelist=[]
        super(LIST, self).__init__(*args, **kwargs)
        self._createdefault=False
        self._length=None
        self._raiseonbadlength=True
        self._elementclass=None
        self._elementinitkwargs={}
        if self._ismostderived(LIST):
            self._update(args,kwargs)
    def _update(self, args, kwargs):
        super(LIST,self)._update(args, kwargs)
        self._consumekw(kwargs, ("createdefault","length","raiseonbadlength","elementclass","elementinitkwargs"))
        if kwargs.has_key("value"):
            self._thelist=list(kwargs['value'])
            del kwargs['value']
        self._complainaboutunusedargs(LIST,kwargs)
        if self._elementclass is None:
            raise TypeError("elementclass argument was not supplied")
        if len(args):
            self.extend(args)
    def readfrombuffer(self,buf):
        self._bufferstartoffset=buf.getcurrentoffset()
        self._thelist=[]
        if self._length is None:
            while buf.hasmore():
                x=self._makeitem()
                x.readfrombuffer(buf)
                self._thelist.append(x)
        else:
            for dummy in range(self._length):
                x=self._makeitem()
                x.readfrombuffer(buf)
                self._thelist.append(x)
        self._bufferendoffset=buf.getcurrentoffset()
    def writetobuffer(self, buf):
        self._bufferstartoffset=buf.getcurrentoffset()
        self._ensurelength()
        for i in self:
            i.writetobuffer(buf)
        self._bufferendoffset=buf.getcurrentoffset()
    def packetsize(self):
        self._ensurelength()
        sz=0
        for item in self:
            sz+=item.packetsize()
        return sz
    def iscontainer(self):
        return True
    def containerelements(self):
        self._ensurelength()
        for i,v in enumerate(self._thelist):
            yield "["+`i`+"]",v,None
    def append(self, item):
        self._thelist.append(self._makeitem(item))
    def extend(self, items):
        self._thelist.extend(map(self._makeitem, items))
    def insert(self, index, item):
        self._thelist.insert(index, self._makeitem(item))
    def __getitem__(self, index):
        return self._thelist[index]
    def __iter__(self):
        try:
            return self._thelist.__iter__()
        except:
            return self.__fallbackiter()
    def __fallbackiter(self):
        for item in self._thelist:
            yield item
    def __len__(self):
        return self._thelist.__len__()
    def __setitem__(self, index, value):
        self._thelist.__setitem__(index, self._makeitem(value))
    def __delitem__(self, index):
        self._thelist.__delitem__(index)
    def _makeitem(self, *args, **kwargs):
        "Creates a child element"
        if len(args)==1 and isinstance(args[0], self._elementclass):
            return args[0]
        d={}
        d.update(self._elementinitkwargs)
        d.update(kwargs)
        return self._elementclass(*args, **d)
    def _ensurelength(self):
        "Ensures we are the correct length"
        if self._createdefault and self._length is not None and len(self._thelist)<self._length:
            while len(self._thelist)<self._length:
                x=self._makeitem()
                self._thelist.append(x)
            return
        if self._length is not None and self._raiseonbadlength and len(self._thelist)!=self._length:
            raise ValueLengthException(len(self), self._length)
class buffer:
    "This is used for reading and writing byte data"
    def __init__(self, data=None):
        "Call with data to read from it, or with None to write to it"
        if data is not None:
            self._data=data
        else:
            self._buffer=cStringIO.StringIO()
        self._offset=0
    def getcurrentoffset(self):
        "Returns distance into data we are"
        return self._offset
    def peeknextbyte(self, howmuch=0):
        "Returns value of next byte, but doesn't advance position"
        if self._offset+howmuch>=len(self._data):
            return None
        return ord(self._data[self._offset+howmuch]) 
    def getnextbyte(self):
        "Returns next byte"
        if self._offset>=len(self._data):
            raise IndexError("trying to read one byte beyond end of "+`len(self._data)`+" byte buffer")
        res=ord(self._data[self._offset])
        self._offset+=1
        return res
    def getnextbytes(self, howmany):
        "Returns howmany bytes"
        assert howmany>=0
        if self._offset+howmany>len(self._data):
            raise IndexError("Trying to read "+`howmany`+" bytes starting at "+`self._offset`+" which will go beyond end of "+`len(self._data)`+" byte buffer")
        res=self._data[self._offset:self._offset+howmany]
        self._offset+=howmany
        return res
    def peeknextbytes(self, howmany):
        if self._offset+howmany>len(self._data):
            return None
        return self._data[self._offset:self._offset+howmany]
    def getremainingbytes(self):
        "Returns rest of buffer"
        sz=len(self._data)-self._offset
        return self.getnextbytes(sz)
    def hasmore(self):
        "Is there any data left?"
        return self._offset<len(self._data)
    def howmuchmore(self):
        "Returns how many bytes left"
        return len(self._data)-self._offset
    def appendbyte(self, val):
        """Appends byte to data.
        @param val: a number 0 <= val <=255
        """
        assert val>=0 and val<=255
        self._buffer.write(chr(val))
        self._offset+=1
        assert self._offset==len(self._buffer.getvalue())
    def appendbytes(self, bytes):
        "Adds bytes to end"
        self._buffer.write(bytes)
        self._offset+=len(bytes)
        assert self._offset==len(self._buffer.getvalue())
    def getvalue(self):
        "Returns the buffer being built"
        return self._buffer.getvalue()
    def getdata(self):
        "Returns the data passed in"
        return self._data
