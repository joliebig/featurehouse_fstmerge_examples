"""Talk to the Sanyo SCP-8100 Bell Mobility (Canada) cell phone"""

import common

import p_sanyo8100_bell

import com_brew

import com_phone

import com_sanyo

import com_sanyomedia

import com_sanyonewer

import prototypes

import os

class  Phone (com_sanyonewer.Phone) :
	"Talk to the Sanyo SCP-8100 Bell Mobility (Canada) cell phone"
	    desc="SCP-8100-Bell"
	    FIRST_MEDIA_DIRECTORY=1
	    LAST_MEDIA_DIRECTORY=3
	    protocolclass=p_sanyo8100_bell
	    serialsname='scp8100bell'
	    builtinringtones=( 'None', 'Vibrate', 'Ringer & Voice', '', '', '', '', '', '', 
                       'Tone 1', 'Tone 2', 'Tone 3', 'Tone 4', 'Tone 5',
                       'Tone 6', 'Tone 7', 'Tone 8', '', '', '', '', '',
                       '', '', '', '', '', '', '',
                       'Tschaik.Swanlake', 'Satie Gymnop.#1',
                       'Bach Air on the G', 'Beethoven Sym.5', 'Greensleeves',
                       'Johnny Comes..', 'Foster Ky. Home', 'Asian Jingle',
                       'Disco' )
	    calendar_defaultringtone=4
	    def __init__(self, logtarget, commport):

        com_sanyonewer.Phone.__init__(self, logtarget, commport)

        self.mode=self.MODENONE

	"Talk to the Sanyo SCP-8100 Bell Mobility (Canada) cell phone"

class  Profile (com_sanyonewer.Profile) :
	protocolclass=p_sanyo8100_bell
	    serialsname='scp8100bell'
	    WALLPAPER_WIDTH=132
	    WALLPAPER_HEIGHT=144
	    OVERSIZE_PERCENTAGE=100
	    _supportedsyncs=(
        ('phonebook', 'read', None),  
        ('calendar', 'read', None),   
        ('phonebook', 'write', 'OVERWRITE'),  
        ('calendar', 'write', 'OVERWRITE'),   
        ('wallpaper', 'read', None),  
        ('ringtone', 'read', None),   
    )
	    def __init__(self):

        com_sanyonewer.Profile.__init__(self)


