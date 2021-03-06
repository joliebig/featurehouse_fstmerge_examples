from misc import WicdError
import os
from logfile import log
from globalconfig import global_config
class NetUtilsManager(object):
    def __init__(self):
        preference = global_config.get('netutils', 'preference',
                                       'ioctlnetutils.py')
        available = self.find_available_netutils()
        if preference in available:
            self.recommended_netutils = self.load_netutils(preference)
        else:
            netutils = self.find_available_netutils()
            self.recommended_netutils = self.load_netutils(netutils[0])
    def find_available_netutils(self):
        available_netutils = []
        for item in os.listdir(os.path.join(
            os.path.dirname(os.path.realpath(__file__)),
            'netutilsbackends')):
            if item.endswith('netutils.py') and \
               not item.index('netutils.py') == 0 \
               and not item == 'basenetutils.py':
                available_netutils.append(item)
        return available_netutils
    def load_netutils(self, name):
        if name in self.find_available_netutils():
            log('loading netutils %s' % name)
            if name.endswith('.py'):
                name = name[:-3]
            module = __import__('netutilsbackends.' + str(name))
            module = getattr(module, name)
            return module
        else:
            raise WicdError('Netutils %s was not found' % name)
    def get_netutils(self):
        return self.recommended_netutils
