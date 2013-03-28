"""Communicate with the LG LX325/PM325 (Sprint) cell phone"""
import re
import time
import cStringIO
import sha
import p_lgpm225
import p_lgpm325
import p_brew
import common
import commport
import com_brew
import com_phone
import com_lg
import com_lgpm225
import prototypes
import call_history
import sms
import fileinfo
import memo
class Phone(com_lgpm225.Phone):
    "Talk to the LG LX325/PM325 cell phone"
    desc="LG PM325"
    protocolclass=p_lgpm325
    serialsname='lgpm325'
    imagelocations=(
        (0x600, "setas/dcamIndex.map", "Dcam/Wallet", "camera", 50, 6),
        )
    ringtonelocations=(
        (0x1100, "setas/voicememoRingerIndex.map", "VoiceDB/All/Memos", "voice_memo", 50, 11),
        )
    builtinimages=( )
    builtinringtones=( 'Tone 1', 'Tone 2', 'Tone 3', 'Tone 4', 'Tone 5', 'Tone 6',
                       'Alert 1', 'Alert 2', 'Alert 3', 'Alert 4', 'Alert 5', 'Alert 6',
                       'Jazztic', 'Rock & Roll', 'Grand Waltz', 'Toccata and Fugue',
                       'Sunday Afternoon', 'Bumble Bee', 'Circus Band', 'Cuckoo Waltz',
                       'Latin', 'CanCan', 'Play tag', 'Eine kleine Nachtmusik',
                       'Symphony No.25 in G Minor', 'Capriccio a minor', 'Moonlight',
                       'A Nameless Girl', 'From the New World', 'They Called Me Elvis')
    def __init__(self, logtarget, commport):
        "Calls all the constructors and sets initial modes"
        com_phone.Phone.__init__(self, logtarget, commport)
        com_brew.BrewProtocol.__init__(self)
        com_lg.LGPhonebook.__init__(self)
        self.log("Attempting to contact phone")
        self._cal_has_voice_id=hasattr(self.protocolclass, 'cal_has_voice_id') \
                                and self.protocolclass.cal_has_voice_id
        self.mode=self.MODENONE
    brew_version_file='ams/version.txt'
    brew_version_txt_key='ams_version.txt'
    my_model='LX325'    # AKA the PM325 from Sprint
parentprofile=com_lgpm225.Profile
class Profile(parentprofile):
    protocolclass=Phone.protocolclass
    serialsname=Phone.serialsname
    BP_Calendar_Version=3
    phone_manufacturer='LG Electronics Inc'
    phone_model='LX325'      # aka the PM325 from Sprint
    brew_required=True
    DIALSTRING_CHARS="[^0-9PT#*]"
    _supportedsyncs=(
        ('phonebook', 'read', None),  # all phonebook reading
        ('calendar', 'read', None),   # all calendar reading
        ('wallpaper', 'read', None),  # all wallpaper reading
        ('ringtone', 'read', None),   # all ringtone reading
        ('call_history', 'read', None),# all call history list reading
        ('memo', 'read', None),        # all memo list reading
        ('sms', 'read', None),         # all SMS list reading
        ('phonebook', 'write', 'OVERWRITE'),  # only overwriting phonebook
        ('calendar', 'write', 'OVERWRITE'),   # only overwriting calendar
        ('wallpaper', 'write', 'MERGE'),      # merge and overwrite wallpaper
        ('wallpaper', 'write', 'OVERWRITE'),
        ('ringtone', 'write', 'MERGE'),      # merge and overwrite ringtone
        ('ringtone', 'write', 'OVERWRITE'),
        ('memo', 'write', 'OVERWRITE'),       # all memo list writing
        ('sms', 'write', 'OVERWRITE'),        # all SMS list writing
        )
