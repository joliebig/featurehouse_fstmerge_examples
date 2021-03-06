import os
class BaseDhcpClient(object):
    def __init__(self, interface_name):
        self.interface_name = interface_name
    def start(self):
        raise NotImplementedError('start is not implemented in this class')
    def stop(self):
        raise NotImplementedError('stop is not implemented in this class')
    def status(self):
        return True
    def check(self):
        return True
    def __del__(self):
        raise NotImplementedError('__del__ is not implemented in this class')
