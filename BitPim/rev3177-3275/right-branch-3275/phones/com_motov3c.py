"""Communicate with Motorola V3c phones using AT commands"""
import com_motov710
parentphone=com_motov710.Phone
class Phone(parentphone):
    desc='Moto-V3c'
    serialsname='motov3c'
    def __init__(self, logtarget, commport):
        parentphone.__init__(self, logtarget, commport)
    def _detectphone(coms, likely_ports, res, _module, _log):
        pass
    detectphone=staticmethod(_detectphone)
parentprofile=com_motov710.Profile
class Profile(parentprofile):
    serialsname=Phone.serialsname
    phone_model='Motorola CDMA V3c Phone'
