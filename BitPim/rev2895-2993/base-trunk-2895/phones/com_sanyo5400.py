"""Talk to the Sanyo SCP-5400 (RL2500) cell phone"""
import common
import p_sanyo5400
import com_brew
import com_phone
import com_sanyo
import com_sanyonewer
import prototypes
class Phone(com_sanyonewer.Phone):
    "Talk to the Sanyo SCP-5400 cell phone"
    desc="SCP-5400"
    FIRST_MEDIA_DIRECTORY=2
    LAST_MEDIA_DIRECTORY=3
    imagelocations=(
        )    
    protocolclass=p_sanyo5400
    serialsname='scp5400'
    calendar_defaultringtone=0
    def __init__(self, logtarget, commport):
        com_sanyonewer.Phone.__init__(self, logtarget, commport)
        self.mode=self.MODENONE
class Profile(com_sanyonewer.Profile):
    protocolclass=Phone.protocolclass
    serialsname=Phone.serialsname
    phone_manufacturer='SANYO'
    phone_model='SCP-5400/US'
    def __init__(self):
        com_sanyonewer.Profile.__init__(self)
