"""Communicate with an unsupported Brew phone"""
import com_phone
import com_brew
import commport
class Phone(com_phone.Phone, com_brew.BrewProtocol):
    "Talk to an unsupported CDMA phone"
    desc="Other CDMA Phone"
    protocolclass=None
    def __init__(self, logtarget, commport):
        com_phone.Phone.__init__(self, logtarget, commport)
        com_brew.BrewProtocol.__init__(self)
class Profile(com_phone.Profile):
    pass
