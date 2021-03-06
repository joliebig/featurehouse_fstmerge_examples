import socket
import fcntl
import struct
import time
import array
import misc
import externalnetutils
from logfile import log
SIOCGIWESSID = 0x8B1B
SIOCGIWRANGE = 0x8B0B
SIOCGIWAP = 0x8B15
SIOCGIWSTATS = 0x8B0F
SIOCGIFADDR = 0x8915
SIOCGIFHWADDR = 0x8927
SIOCGMIIPHY = 0x8947
SIOCETHTOOL = 0x8946
SIOCGIFFLAGS = 0x8913
class NetworkInterface(externalnetutils.NetworkInterface):
    ''' Represents a network interface. '''
    def __init__(self, interface_name):
        externalnetutils.NetworkInterface.__init__(self, interface_name)
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    def get_ip(self):
        """ Get the IP address of the interface.
        Returns:
        The IP address of the interface in dotted quad form.
        """
        ifstruct = struct.pack('256s', self.interface_name)
        try:
            raw_ip = fcntl.ioctl(self.sock.fileno(), SIOCGIFADDR, ifstruct)
        except IOError:
            return None
        except OSError:
            return None
        return socket.inet_ntoa(raw_ip[20:24])
    def check_link(self):
        return self._eth_get_plugged_in()
    def _eth_get_plugged_in(self):
        """ Use ethtool to determine the physical connection state.
        Returns:
        True if a link is detected, False otherwise.
        """
        if not self.is_up():
            self.up()
            time.sleep(1)
        buff = array.array('i', [0x0000000a, 0x00000000])
        addr, length = buff.buffer_info()
        arg = struct.pack('Pi', addr, length)
        data = (self.interface_name + '\0' * 16)[:16] + arg
        try:
            fcntl.ioctl(self.sock.fileno(), SIOCETHTOOL, data)
        except IOError, e:
            print 'SIOCETHTOOL failed: ' + str(e)
            return False
        return bool(buff.tolist()[1])
    def _mii_get_plugged_in(self):
        """ Use mii-tool to determine the physical connection state. 
        Returns:
        True if a link is detected, False otherwise.
        """
        if not self.is_up():
            self.up()
            time.sleep(1)
        buff = struct.pack('16shhhh', (self.interface_name + '\0' * 16)[:16], 0, 
                           1, 0x0004, 0)
        try:
            result = fcntl.ioctl(self.sock.fileno(), SIOCGMIIPHY, buff)
        except IOError, e:
            print 'SIOCGMIIPHY failed: ' + str(e)
            return False
        reg = struct.unpack('16shhhh', result)[-1]
        return bool(reg & 0x0004)
    def is_up(self):
        """ Determines if the interface is up.
        Returns:
        True if the interface is up, False otherwise.
        """
        data = (self.interface_name + '\0' * 16)[:18]
        try:
            result = fcntl.ioctl(self.sock.fileno(), SIOCGIFFLAGS, data)
        except IOError, e:
            print "SIOCGIFFLAGS failed: " + str(e)
            return False
        flags, = struct.unpack('H', result[16:18])
        return bool(flags & 1)
