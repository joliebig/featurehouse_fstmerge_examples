"""Talk to the Sanyo RL-4930 cell phone"""

import sha

import common

import p_sanyo4930

import com_brew

import com_phone

import com_sanyo

import com_sanyomedia

import com_sanyonewer

import prototypes

numbertypetab=( 'cell', 'home', 'office', 'pager',
                    'fax', 'data', 'none' )

class  Phone (com_sanyonewer.Phone) :
	"Talk to the Sanyo RL-4930 cell phone"
	    desc="SCP-4930"
	    FIRST_MEDIA_DIRECTORY=2
	    LAST_MEDIA_DIRECTORY=3
	    imagelocations=(
        )
	    protocolclass=p_sanyo4930
	    serialsname='rl4930'
	    builtinringtones=( 'None', 'Vibrate', 'Ringer & Voice', '', '', '', '', '', '', 
                       'Tone 1', 'Tone 2', 'Tone 3', 'Tone 4', 'Tone 5',
                       'Tone 6', 'Tone 7', 'Tone 8', '', '', '', '', '',
                       '', '', '', '', '', '', '',
                       'Tschaik.Swanlake', 'Satie Gymnop.#1',
                       'Bach Air on the G', 'Beethoven Sym.5', 'Greensleeves',
                       'Johnny Comes..', 'Foster Ky. Home', 'Asian Jingle',
                       'Disco', 'Toy Box', 'Rodeo' )
	    calendar_defaultringtone=4
	    def __init__(self, logtarget, commport):

        com_sanyonewer.Phone.__init__(self, logtarget, commport)

        self.mode=self.MODENONE

        self.numbertypetab=numbertypetab

	"Talk to the Sanyo RL-4930 cell phone"

class  Profile (com_sanyonewer.Profile) :
	protocolclass=Phone.protocolclass
	    serialsname=Phone.serialsname
	    phone_manufacturer='SANYO'
	    phone_model='SCP-4930/US'
	    WALLPAPER_WIDTH=128
	    WALLPAPER_HEIGHT=112
	    OVERSIZE_PERCENTAGE=100
	    def __init__(self):

        com_sanyonewer.Profile.__init__(self)

        self.numbertypetab=numbertypetab


