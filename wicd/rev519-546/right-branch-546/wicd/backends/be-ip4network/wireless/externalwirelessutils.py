import re
import misc
from dhcpmanager import DhcpManager
class WirelessRegexPatterns:
    essid       = re.compile('.*ESSID:"(.*?)"\n', re.I | re.M  | re.S)
    ap_mac      = re.compile('.*Address: (.*?)\n', re.I | re.M  | re.S)
    channel     = re.compile('.*Channel:? ?(\d\d?)', re.I | re.M  | re.S)
    strength    = re.compile('.*Quality:?=? ?(\d+)\s*/?\s*(\d*)', re.I | re.M  | re.S)
    altstrength = re.compile('.*Signal level:?=? ?(\d\d*)', re.I | re.M | re.S)
    signaldbm   = re.compile('.*Signal level:?=? ?(-\d\d*)', re.I | re.M | re.S)
    mode        = re.compile('.*Mode:(.*?)\n', re.I | re.M  | re.S)
    freq        = re.compile('.*Frequency:(.*?)\n', re.I | re.M  | re.S)
    ip          = re.compile(r'inet [Aa]d?dr[^.]*:([^.]*\.[^.]*\.[^.]*\.[0-9]*)', re.S)
    bssid       = re.compile('.*Access Point: (([0-9A-Z]{2}:){5}[0-9A-Z]{2})', re.I | re.M | re.S)
    wep         = re.compile('.*Encryption key:(.*?)\n', re.I | re.M  | re.S)
    altwpa      = re.compile('(wpa_ie)', re.I | re.M | re.S)
    wpa1        = re.compile('(WPA Version 1)', re.I | re.M  | re.S)
    wpa2        = re.compile('(WPA2)', re.I | re.M  | re.S)
    auth        = re.compile('.*wpa_state=(.*?)\n', re.I | re.M  | re.S)    
class WirelessNetwork: pass
class WirelessInterface:
    ''' Represents a hardware wireless interface. '''
    def __init__(self, interface_name):
        self.interface_name = interface_name
        self.current_network = None
        self.dhcp_manager = DhcpManager(interface_name)
    def up(self):
        ''' Put the interface up. '''
        cmd =[misc.find_program_in_path('ifconfig'), self.interface_name, 'up']
        misc.run(cmd)
    def down(self):
        ''' Put the interface down. '''
        cmd = [misc.find_program_in_path('ifconfig'), 
               self.interface_name, 'down']
        misc.run(cmd)
    def do_ip_address(self, network):
        if 'use_static_ip' in network.profile and \
           network.profile['use_static_ip']:
            print 'do static ip'
            ip_addresses = ['static_ip', 'static_netmask', 'static_dns_1']
            optional = ['static_gateway', 'static_dns_2', 'static_dns_3']
            for field in ip_addresses + optional:
                if not field in network.profile:
                    raise misc.WicdError('Missing required profile item %s' %
                                         field)
            for field in ip_addresses:
                if not misc.is_valid_ip(network.profile[field]):
                    raise misc.WicdError('Invalid IP address: %s' 
                                         % network.profile[field])
            for field in optional:
                if not network.profile[field] is None:
                    if not misc.is_valid_ip(network.profile[field]):
                        raise misc.WicdError('Invalid IP address: %s' 
                                             % network.profile[field])
            self.set_ip(network.profile['static_ip'])
            self.set_netmask(network.profile['static_netmask'])
            if network.profile['static_gateway']:
                self.set_gateway(network.profile['static_gateway'])
            return True
        else:
            print 'using dhcp'
            self.dhcp_manager.start()
            return self.dhcp_manager.status()
    def reset(self):
        self.set_ip('0.0.0.0')
        self.down()
    def scan(self):
        ''' Scan for new networks. '''
        self.current_network = None
        self.networks = self._do_scan()
        return self.networks
    def _do_scan(self):
        self.up()
        cmd = [misc.find_program_in_path('iwlist'),
                              self.interface_name,
                              'scan']
        results = misc.run(cmd)
        networks = results.split( '   Cell ' )
        access_points = []
        for cell in networks:
            if 'ESSID:' in cell:
                entry = self._parse_access_point(cell)
                if entry is not None:
                    access_points.append(entry)
        return access_points
    def _parse_access_point(self, cell):
        """ Parse a single cell from the output of iwlist.
        Keyword arguments:
        cell -- string containing the cell information
        Returns:
        A dictionary containing the cell networks properties.
        """
        ap = WirelessNetwork()
        ap.essid = misc.RunRegex(WirelessRegexPatterns.essid, cell)
        try:
            ap.essid = misc.to_unicode(ap.essid)
        except (UnicodeDecodeError, UnicodeEncodeError):
            print 'Unicode problem with current network essid, ignoring!!'
            return None
        if ap.essid in ['<hidden>', ""]:
            ap.essid = 'Hidden'
            ap.hidden = True
        else:
            ap.hidden = False
        ap.channel = misc.RunRegex(WirelessRegexPatterns.channel, cell)
        if ap.channel == None:
            freq = misc.RunRegex(WirelessRegexPatterns.freq, cell)
            ap.channel = self._freq_to_channel(freq)
        ap.bssid = misc.RunRegex(WirelessRegexPatterns.ap_mac, cell)
        ap.mode = misc.RunRegex(WirelessRegexPatterns.mode, cell)
        if (WirelessRegexPatterns.strength.match(cell)):
            [(strength, max_strength)] = WirelessRegexPatterns.strength.findall(cell)
            if max_strength:
                ap.quality = 100 * int(strength) // int(max_strength)
            else:
                ap.quality = int(strength)
        elif misc.RunRegex(WirelessRegexPatterns.altstrength,cell):
            ap.quality = misc.RunRegex(WirelessRegexPatterns.altstrength, cell)
        else:
            ap.quality = -1
        if misc.RunRegex(WirelessRegexPatterns.signaldbm, cell):
            ap.strength = misc.RunRegex(WirelessRegexPatterns.signaldbm, cell)
        return ap
    def _freq_to_channel(self, freq):
        """ Translate the specified frequency to a channel.
        Note: This function is simply a lookup dict and therefore the
        freq argument must be in the dict to provide a valid channel.
        Keyword arguments:
        freq -- string containing the specified frequency
        Returns:
        The channel number, or None if not found.
        """
        ret = None
        freq_dict = {'2.412 GHz': 1, '2.417 GHz': 2, '2.422 GHz': 3,
                         '2.427 GHz': 4, '2.432 GHz': 5, '2.437 GHz': 6,
                         '2.442 GHz': 7, '2.447 GHz': 8, '2.452 GHz': 9,
                         '2.457 GHz': 10, '2.462 GHz': 11, '2.467 GHz': 12,
                         '2.472 GHz': 13, '2.484 GHz': 14 }
        try:
            ret = freq_dict[freq]
        except KeyError:
            print "Couldn't determine channel number for frequency: " + str(freq)
        return ret
    def get_ip(self):
        """ Get the IP address of the interface.
        Returns:
        The IP address of the interface in dotted quad form.
        """
        cmd = 'ifconfig %s' + self.interface_name
        output = misc.Run(cmd)
        return misc.RunRegex(WirelessRegexPatterns.ip, output)
    def set_ip(self, new_ip):
        ''' Sets the IP of the current network interface. '''
        cmd = '%s %s %s' % (misc.find_program_in_path('ifconfig'), 
                            self.interface_name, new_ip)
        misc.run(cmd)
    def set_netmask(self, new_ip):
        ''' Sets the netmask of the current network interface. '''
        cmd = '%s %s netmask %s' % (misc.find_program_in_path('ifconfig'), 
                            self.interface_name, new_ip)
        misc.run(cmd)
    def set_gateway(self, new_ip):
        ''' Sets the netmask of the current network interface. '''
        cmd = '%s add default gateway %s %s' % (misc.find_program_in_path('route'), 
                            new_ip, self.interface_name)
        misc.run(cmd)
    def do_dhcp(self):
        pass
    def connect(self, finished_callback):
        ''' Connects to the network in self.current_network. '''
        raise NotImplementedError('connect is not implemented in this class.')
    def cancel_connection_attempt(self):
        raise NotImplementedError(
            'cancel_connection_attempt is not implemented in this class.'
        )
