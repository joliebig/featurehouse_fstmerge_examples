"""Talk to the Sanyo SCP-8300 cell phone"""
import re
import time
import sha
import common
import p_brew
import p_sanyo8300
import com_brew
import com_phone
import com_sanyo
import com_sanyomedia
import com_sanyonewer
import prototypes
import bpcalendar
numbertypetab=( 'cell', 'home', 'office', 'pager',
                    'fax', 'data', 'none' )
class Phone(com_sanyonewer.Phone):
    "Talk to the Sanyo PM8300 cell phone"
    desc="PM8300"
    FIRST_MEDIA_DIRECTORY=1
    LAST_MEDIA_DIRECTORY=3
    imagelocations=(
        )    
    protocolclass=p_sanyo8300
    serialsname='mm8300'
    builtinringtones=( 'None', 'Vibrate', 'Ringer & Voice', '', '', '', '', '', '', 
                       'Tone 1', 'Tone 2', 'Tone 3', 'Tone 4', 'Tone 5',
                       'Tone 6', 'Tone 7', 'Tone 8', '', '', '', '', '',
                       '', '', '', '', '', '', '',
                       'Tschaik.Swanlake', 'Satie Gymnop.#1',
                       'Hungarian Dance', 'Beethoven Sym.5', 'Greensleeves',
                       'Foster Ky. Home', 'The Moment', 'Asian Jingle',
                       'Disco')
    calendar_defaultringtone=0
    calendar_defaultcaringtone=0
    def __init__(self, logtarget, commport):
        com_sanyonewer.Phone.__init__(self, logtarget, commport)
        self.mode=self.MODENONE
        self.numbertypetab=numbertypetab
    def _setmodebrew(self):
        req=p_brew.firmwarerequest()
        respc=p_brew.testing0cresponse
        for baud in 0, 38400,115200:
            if baud:
                if not self.comm.setbaudrate(baud):
                    continue
            try:
                self.sendbrewcommand(req, respc, callsetmode=False)
                return True
            except com_phone.modeignoreerrortypes:
                pass
        for baud in (0, 115200, 19200, 230400):
            if baud:
                if not self.comm.setbaudrate(baud):
                    continue
            print "Baud="+`baud`
            try:
                self.comm.write("AT$QCDMG\r\n")
            except:
                self.mode=self.MODENONE
                self.comm.shouldloop=True
                raise
            try:
                if self.comm.readsome().find("OK")>=0:
                    break
            except com_phone.modeignoreerrortypes:
                self.log("No response to setting QCDMG mode")
        for baud in 0,38400,115200:
            if baud:
                if not self.comm.setbaudrate(baud):
                    continue
            try:
                self.sendbrewcommand(req, respc, callsetmode=False)
                return True
            except com_phone.modeignoreerrortypes:
                pass
        return False
    def getfundamentals(self, results):
        """Gets information fundamental to interopating with the phone and UI."""
        req=self.protocolclass.esnrequest()
        res=self.sendpbcommand(req, self.protocolclass.esnresponse)
        results['uniqueserial']=sha.new('%8.8X' % res.esn).hexdigest()
        self.getmediaindices(results)
        self.log("Fundamentals retrieved")
        return results
class Profile(com_sanyonewer.Profile):
    protocolclass=Phone.protocolclass
    serialsname=Phone.serialsname
    phone_manufacturer='SANYO'
    phone_model='SCP-8300/US'
    WALLPAPER_WIDTH=176
    WALLPAPER_HEIGHT=220
    def __init__(self):
        com_sanyonewer.Profile.__init__(self)
        self.numbertypetab=numbertypetab
