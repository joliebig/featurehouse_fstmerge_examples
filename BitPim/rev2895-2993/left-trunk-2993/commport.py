"""Encapsulates the serial port device"""
import serial
import sys
import common
import time
import threading
try:
    import native.usb as usb
except:
    usb=None
try:
    import pywintypes
except:
    pywintypes=None
class CommTimeout(Exception):
    def __init__(self, str=None, partial=None):
        Exception.__init__(self, str)
        self.partial=partial
class ATError(Exception):
    def __init__(self, str=None, partial=None):
        Exception.__init__(self, str)
        self.partial=partial
class CommConnection:
    usbwhine=0
    def __init__(self, logtarget, port, baud=115200, timeout=3, hardwareflow=0,
                 softwareflow=0, autolistfunc=None, autolistargs=None, configparameters=None):
        self._brokennotifications=0
        self.ser=None
        self.port=port
        self.logtarget=logtarget
        self.clearcounters()
        if usb is None and self.usbwhine<1:
            self.log("USB support is not available")
        self.success=False
        self.shouldloop=False
        self.ports=None
        self.autolistfunc=autolistfunc
        self.autolistargs=autolistargs
        self.configparameters=configparameters
        self.params=(baud,timeout,hardwareflow,softwareflow)
        self.readahead=""
        assert port!="auto" or (port=="auto" and autolistfunc is not None)
        if autolistfunc is not None:
            self._isauto=True
        else:
            self._isauto=False
        if port=="auto":
            self.log("Auto detected port requested")
            self.NextAutoPort()
        else:
            self._openport(self.port, *self.params)
    def IsAuto(self):
        return self._isauto
    def close(self):
        if self.ser is not None:
            try:
                self.ser.close()
            except:
                pass
            self.ser=None
    def _openport(self, port, baud, timeout, hardwareflow, softwareflow, description=None):
        self.close()
        self.log("Opening port %s, %d baud, timeout %f, hardwareflow %d, softwareflow %d" %
                 (port, baud, float(timeout), hardwareflow, softwareflow) )
        if description is not None:
            self.log(description)
        for dummy in range(2):
            try:
                self.close()
                if port.startswith("usb::"):
                    self.ser=self._openusb(port, timeout)
                else:
                    useport=port
                    if sys.platform=='win32' and port.lower().startswith("com"): useport="\\\\?\\"+port
                    self.ser=serial.Serial(useport, baud, timeout=timeout, rtscts=hardwareflow, xonxoff=softwareflow)
                self.log("Open of comm port suceeded")
                self.port=port
                self.clearcounters()
                return
            except serial.serialutil.SerialException,e:
                if dummy:
                    self.log('Open of comm port failed')
                    raise common.CommsOpenFailure(e.__str__(), port)
                time.sleep(2)
    def _openusb(self, name, timeout):
        self.close()
        if usb is None:
            self.log("USB module not available - unable to open "+name)
            raise Exception("USB module not available - unable to open "+name)
        _,wantedbus,wanteddev,wantediface=name.split("::")
        wantediface=int(wantediface)
        usb.UpdateLists()
        for bus in usb.AllBusses():
            if bus.name()!=wantedbus:
                continue
            for device in bus.devices():
                if device.name()!=wanteddev:
                    continue
                for iface in device.interfaces():
                    if iface.number()!=wantediface:
                        continue
                    return _usbdevicewrapper(iface.openbulk(), timeout)
        self.log("Failed to find "+name+".  You may need to rescan.")
        raise Exception("Failed to find usb device "+name)
    def reset(self):
        self._openport(self.port, *self.params)
    def _refreshautoports(self):
        self.close()
        self.ports=self.autolistfunc(*self.autolistargs)
        assert self.ports is not None
        self.success=False
        self.portstried=self.ports
    def NextAutoPort(self):
        if (self.ports is None and self.autolistfunc is not None) or \
           ( len(self.ports)==0 and (self.success or self.shouldloop)):
            self._refreshautoports()
            self.shouldloop=False
        if len(self.ports)==0:
            self.ports=None # so user can retry
            raise common.AutoPortsFailure(map(lambda x: x[0], self.portstried))
        self.log("Trying next auto port")
        description=self.ports[0][1]['description']
        self.port=self.ports[0][0]
        self.ports=self.ports[1:]
        try:
            self._openport(self.port, *(self.params+(description,)))
        except common.CommsOpenFailure:
            self.NextAutoPort()
    def clearcounters(self):
        self.readbytes=0
        self.readrequests=0
        self.writebytes=0
        self.writerequests=0
    def log(self, str):
        if self.logtarget:
            self.logtarget.log(self.port+": "+str)
    def logdata(self, str, data):
        if self.logtarget:
            self.logtarget.logdata(self.port+": "+str, data)
    def setbaudrate(self, rate):
        """Change to the specified baud rate
        @rtype: Boolean
        @returns: True on success, False on failure
        """
        try:
            self.ser.setBaudrate(rate)
            self.log("Changed port speed to "+`rate`)
            time.sleep(.5)
            return True
        except SilentException:
            return False
        except Exception,e:
            self.log("Port speed "+`rate`+" not supported")
            return False
    def setdtr(self, dtr):
        """Set or Clear DTR
        @rtype: Boolean
        @returns: True on success, False on failure
        """
        try:
            self.ser.setDTR(dtr)
            self.log("DTR set to "+`dtr`)
            return True
        except SilentException:
            return False
    def setrts(self, rts):
        """Set or Clear RTS
        @rtype: Boolean
        @returns: True on success, False on failure
        """
        try:
            self.ser.setRTS(rts)
            self.log("RTS set to "+`rts`)
            return True
        except SilentException:
            return False
    def _write(self, data, log=True):
        self.writerequests+=1
        if log:
            self.logdata("Writing", data)
        self.ser.write(data)
        self.writebytes+=len(data)
    def write_thread(self, data, log):
        try:
            self._write(data, log)
            self._write_res=True
        except Exception,e:
            self.log('Write Exception: '+str(e))
    def write(self, data, log=True):
        _t=threading.Thread(target=self.write_thread, args=(data, log))
        self._write_res=False
        _t.start()
        _t.join(self.params[1]+1)
        if _t.isAlive():
            _t._Thread__stop()
        if not self._write_res:
            raise CommTimeout()
    def sendatcommand(self, atcommand, ignoreerror=False):
        b=self.ser.inWaiting()
        if b:
            self.read(b,0)
        fullline="AT"+atcommand
        self.write(str(fullline+"\r\n"))
        self.readatresponse(ignoreerror)
        res=[]
        line=self.getcleanline()
        if line==fullline:
            line=self.getcleanline()
        while line!="OK" and line:
            if line=="ERROR":
                if not ignoreerror:
                    raise ATError
            else:
                res.append(line)
            line=self.getcleanline()
        return res
    def peekline(self):
        return self.getcleanline(peek=True)
    def getcleanline(self, peek=False):
        i=0
        sbuf=self.readahead
        if len(sbuf)==0:
            return ""
        while sbuf[i]!='\n' and sbuf[i]!='\r':
            i+=1
        firstline=sbuf[0:i]
        if not peek:
            i+=1
            while i<len(sbuf):
                if sbuf[i]!='\n' and sbuf[i]!='\r':
                    break
                i+=1
            self.readahead=self.readahead[i:]
        return firstline
    def readatresponse(self, ignoreerror, log=True):
        """Read until OK, ERROR or a timeout"""
        self.readrequests+=1
        res=""
        while True:
            b=self.ser.inWaiting()
            if b:
                res=res+self.read(b,0)
                if res.find("OK\r")>=0 or (res.find("ERROR\r")>=0 and not ignoreerror):
                    break
                continue
            r=self.read(1,0)
            if len(r):
                res=res+r
                continue
            break
        while len(res)>0 and (res[0]=='\n' or res[0]=='\r'):
            res=res[1:]
        if len(res)==0:
            raise CommTimeout()
        self.readbytes+=len(res)
        if log:
            self.logdata("Reading remaining data", res)
        self.readahead=res
        return
    def readline(self):
        return self.getcleanline(peek=False)
    def _isbrokendriver(self, e):
        if pywintypes is None or not isinstance(e, pywintypes.error) or e[0]!=121:
            return False
        return True
    def _brokendriverread(self, numchars):
        self._brokennotifications+=1
        if self._brokennotifications==3:
            self.log("This driver is broken - enabling workaround")
        basetime=time.time()
        while time.time()-basetime<self.params[1]:
            try:
                res=self.ser.read(numchars)
                return res
            except Exception,e:
                if not self._isbrokendriver(e):
                    raise
        raise CommTimeout()
    def _read(self, numchars=1, log=True):
        self.readrequests+=1
        try:
            res=self.ser.read(numchars)
        except Exception,e:
            if not self._isbrokendriver(e):
                raise
            res=self._brokendriverread(numchars)
        if log:
            self.logdata("Reading exact data - requested "+`numchars`, res)
        self.readbytes+=len(res)
        return res
    def read_thread(self, numchars=1, log=True):
        try:
            self._read_res=self._read(numchars, log)
        except Exception,e:
            self.log('Read Exception: '+str(e))
    def read(self, numchars=1, log=True):
        _t=threading.Thread(target=self.read_thread, args=(numchars, log))
        self._read_res=None
        _t.start()
        _t.join(self.params[1]+1)
        if _t.isAlive():
            _t._Thread__stop()
        if self._read_res is None:
            raise CommTimeout()
        return self._read_res
    def readsome(self, log=True, numchars=None):
        self.readrequests+=1
        res=""
        while True:
            if numchars is not None and len(res)>= numchars:
                break
            b=self.ser.inWaiting()
            if b:
                res=res+self.read(b,0)
                continue
            r=self.read(1,0)
            if len(r):
                res=res+r
                continue
            break
        if len(res)==0:
            raise CommTimeout()
        self.readbytes+=len(res)
        if log:
            self.logdata("Reading remaining data", res)
        return res
    def readuntil(self, char, log=True, logsuccess=True, numfailures=0):
        self.readrequests+=1
        if False: # don't log this anymore
            self.logdata("Begin reading until 0x%02x" % (ord(char),), None)
        res=''
        while len(res)==0 or res[-1]!=char:
            if hasattr(self.ser, 'readuntil'):
                res2=self.ser.readuntil(char)
                b=-99999
            else:
                b=self.ser.inWaiting()
                if b<1: b=1
                res2=self.read(b,0)
            if len(res2)<1:
                if numfailures==0:
                    if log:
                        self.log("Timed out waiting for %02x, requested bytes %d  - %d bytes read" % 
                                 (ord(char), b, len(res)))
                        self.logdata("Incomplete read was", res)
                    self.readbytes+=len(res)
                    raise CommTimeout(partial=res)
                else:
                    numfailures-=1
                    self.ser.flushInput()
                    self.log("Timed out - flushing and trying again")
            res=res+res2
        self.readbytes+=len(res)
        if logsuccess:
            self.logdata("Read completed", res)
        return res
    def writethenreaduntil(self, data, logwrite, char, logreaduntil=True, logreaduntilsuccess=True, numfailures=0):
        self.write(data, logwrite)
        return self.readuntil(char, logreaduntil, logreaduntilsuccess, numfailures)
class SilentException(Exception): pass
class _usbdevicewrapper:
    def __init__(self, dev, timeout):
        self.dev=dev
        self.timeout=int(timeout*1000)
    def inWaiting(self):
        return 0
    def read(self, numchars=1):
        return self.dev.read(numchars, self.timeout)
    def flushInput(self):
        self.dev.resetep()
    def write(self, data):
        self.dev.write(data, self.timeout)
    def close(self):
        self.dev.close()
    def readuntil(self, char):
        try:
            data=self.dev.read(999999, self.timeout)
            if len(data) and data[-1]==char:
                return data
        except usb.USBException:
            self.dev.resetep()
            data=""
        basetime=time.time()
        while 1000*(time.time()-basetime)<self.timeout:
            try:
                more=self.dev.read(99999, max(100,self.timeout-1000*(time.time()-basetime)))
                data+=more
                if len(data) and data[-1]==char:
                    return data
            except usb.USBException:
                pass
        return data
