"""Communicate with Motorola V3cm phones using AT commands"""
import com_motov710m
parentphone=com_motov710m.Phone
class Phone(parentphone):
    desc='Moto-V3cm'
    serialsname='motov3cm'
    def __init__(self, logtarget, commport):
        parentphone.__init__(self, logtarget, commport)
    def _detectphone(coms, likely_ports, res, _module, _log):
        pass
    detectphone=staticmethod(_detectphone)
parentprofile=com_motov710m.Profile
class Profile(parentprofile):
    serialsname=Phone.serialsname
    phone_model='Motorola CDMA V3cM Phone'
