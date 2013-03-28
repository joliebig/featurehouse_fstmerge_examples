"""Common code for newer SCP-5500 style phones"""

import time

import cStringIO

import common

import p_sanyonewer

import com_brew

import com_phone

import com_sanyo

import com_sanyomedia

import prototypes

class  Phone (com_sanyomedia.SanyoMedia,com_sanyo.Phone) :
	"Talk to a Sanyo SCP-5500 style cell phone"
	    builtinringtones=( 'None', 'Vibrate', 'Ringer & Voice', '', '', '', '', '', '', 
                       'Tone 1', 'Tone 2', 'Tone 3', 'Tone 4', 'Tone 5',
                       'Tone 6', 'Tone 7', 'Tone 8', '', '', '', '', '',
                       '', '', '', '', '', '', '',
                       'Tschaik.Swanlake', 'Satie Gymnop.#1',
                       'Bach Air on the G', 'Beethoven Sym.5', 'Greensleeves',
                       'Johnny Comes..', 'Foster Ky. Home', 'Asian Jingle',
                       'Disco', 'Toy Box', 'Rodeo' )
	    calendar_defaultringtone=4
	    calendar_defaultcaringtone=4
	    calendar_tonerange=xrange(18,26)
	    calendar_toneoffset=8
	    def __init__(self, logtarget, commport):

        com_sanyo.Phone.__init__(self, logtarget, commport)

        com_sanyomedia.SanyoMedia.__init__(self)

        self.mode=self.MODENONE

	def sendpbcommand(self, request, responseclass, callsetmode=True, writemode=False, numsendretry=2, returnerror=False):

        res=com_sanyo.Phone.sendpbcommand(self, request, responseclass, callsetmode=callsetmode, writemode=False, numsendretry=numsendretry, returnerror=returnerror)

        return res

	def savecalendar(self, dict, merge):

        req=self.protocolclass.beginendupdaterequest()

        req.beginend=1 

        res=self.sendpbcommand(req, self.protocolclass.beginendupdateresponse, writemode=True)

        self.writewait()

        result = com_sanyo.Phone.savecalendar(self, dict, merge)

	"Talk to a Sanyo SCP-5500 style cell phone"

class  Profile (com_sanyo.Profile) :
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
        ('todo', 'read', None), 
    )
	    def __init__(self):

        com_sanyo.Profile.__init__(self)


