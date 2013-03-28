"""Talk to the Sanyo SCP-8100 cell phone"""

import common

import p_sanyo8100

import com_sanyomedia

import com_sanyo

import com_brew

import com_phone

import prototypes

import os

class  Phone (com_sanyomedia.SanyoMedia,com_sanyo.Phone) :
	"Talk to the Sanyo SCP-8100 cell phone"
	    desc="SCP-8100"
	    FIRST_MEDIA_DIRECTORY=1
	    LAST_MEDIA_DIRECTORY=3
	    protocolclass=p_sanyo8100
	    serialsname='scp8100'
	    builtinringtones=( 'None', 'Vibrate', 'Ringer & Voice', '', '', '', '', '', '', 
                       'Tone 1', 'Tone 2', 'Tone 3', 'Tone 4', 'Tone 5',
                       'Tone 6', 'Tone 7', 'Tone 8', '', '', '', '', '',
                       '', '', '', '', '', '', '',
                       'Tschaik.Swanlake', 'Satie Gymnop.#1',
                       'Bach Air on the G', 'Beethoven Sym.5', 'Greensleeves',
                       'Johnny Comes..', 'Foster Ky. Home', 'Asian Jingle',
                       'Disco' )
	    calendar_defaultringtone=4
	    calendar_defaultcaringtone=4
	    calendar_tonerange=xrange(18,26)
	    calendar_toneoffset=8
	    def __init__(self, logtarget, commport):

        com_sanyo.Phone.__init__(self, logtarget, commport)

        com_sanyomedia.SanyoMedia.__init__(self)

        self.mode=self.MODENONE

	"Talk to the Sanyo SCP-8100 cell phone"

class  Profile (com_sanyo.Profile) :
	protocolclass=Phone.protocolclass
	    serialsname=Phone.serialsname
	    phone_manufacturer='SANYO'
	    phone_model='SCP-8100/US'
	    WALLPAPER_WIDTH=132
	    WALLPAPER_HEIGHT=144
	    OVERSIZE_PERCENTAGE=100
	    _supportedsyncs=(
        ('phonebook', 'read', None),  
        ('calendar', 'read', None),   
        ('phonebook', 'write', 'OVERWRITE'),  
        ('calendar', 'write', 'OVERWRITE'),   
        ('wallpaper', 'write', 'MERGE'),
        ('ringtone', 'write', 'MERGE'),
        ('wallpaper', 'read', None),  
        ('ringtone', 'read', None),   
        ('call_history', 'read', None),
        ('sms', 'read', None),
    )
	    def __init__(self):

        com_sanyo.Profile.__init__(self)


