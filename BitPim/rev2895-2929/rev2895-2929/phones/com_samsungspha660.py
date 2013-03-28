"""Communicate with a Samsung SPH-A620"""

import sha

import re

import struct

import common

import commport

import p_brew

import p_samsungspha620

import p_samsungspha660

import com_brew

import com_phone

import com_samsung_packet

import com_samsungspha620

import prototypes

numbertypetab=('home','office','cell','pager','fax','none')

class  Phone (com_samsungspha620.Phone) :
	"Talk to a Samsung SPH-A660 phone"
	    desc="SPH-A660"
	    protocolclass=p_samsungspha660
	    serialsname='spha660'
	    imagelocations=(
        )
	    ringtonelocations=(
        )
	    def __init__(self, logtarget, commport):

        com_samsungspha620.Phone.__init__(self, logtarget, commport)

        self.numbertypetab=numbertypetab

        self.mode=self.MODENONE

	"Talk to a Samsung SPH-A660 phone"
parentprofile=com_samsungspha620.Profile
class  Profile (parentprofile) :
	deviceclasses=("modem",)
	    protocolclass=Phone.protocolclass
	    serialsname=Phone.serialsname
	    phone_manufacturer='SAMSUNG'
	    phone_model='SPH-A660/152'
	    def __init__(self):

        parentprofile.__init__(self)

        self.numbertypetab=numbertypetab


