"""Communicate with the LG VX7000 cell phone
The VX7000 is substantially similar to the VX6000 but also supports video.
The code in this file mainly inherits from VX4400 code and then extends where
the 6000 has extra functionality
"""
import time
import cStringIO
import sha
import common
import copy
import p_lgvx7000
import com_lgvx4400
import com_brew
import com_phone
import com_lg
import prototypes
class Phone(com_lg.LGNewIndexedMedia,com_lgvx4400.Phone):
    "Talk to the LG VX7000 cell phone"
    desc="LG-VX7000"
    protocolclass=p_lgvx7000
    serialsname='lgvx7000'
    builtinringtones= ('Low Beep Once', 'Low Beeps', 'Loud Beep Once', 'Loud Beeps') + \
                      tuple(['Ringtone '+`n` for n in range(1,11)]) + \
                      ('No Ring',)
    ringtonelocations= (
        ( 'ringers', 'dload/sound.dat', 'dload/soundsize.dat', 'dload/snd', 100, 50, 1),
        )
    builtinwallpapers = () # none
    wallpaperlocations= (
        ( 'images', 'dload/image.dat', 'dload/imagesize.dat', 'dload/img', 100, 50, 0),
        )
    def __init__(self, logtarget, commport):
        com_lgvx4400.Phone.__init__(self,logtarget,commport)
        com_lg.LGNewIndexedMedia.__init__(self)
        self.mode=self.MODENONE
    my_model='VX7000'
parentprofile=com_lgvx4400.Profile
class Profile(parentprofile):
    protocolclass=Phone.protocolclass
    serialsname=Phone.serialsname
    phone_manufacturer='LG Electronics Inc'
    phone_model='VX7000'
    WALLPAPER_WIDTH=176
    WALLPAPER_HEIGHT=184
    MAX_WALLPAPER_BASENAME_LENGTH=32
    WALLPAPER_FILENAME_CHARS="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ."
    WALLPAPER_CONVERT_FORMAT="jpg"
    MAX_RINGTONE_BASENAME_LENGTH=32
    RINGTONE_FILENAME_CHARS="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ."
    imageorigins={}
    imageorigins.update(common.getkv(parentprofile.stockimageorigins, "images"))
    def GetImageOrigins(self):
        return self.imageorigins
    imagetargets={}
    imagetargets.update(common.getkv(parentprofile.stockimagetargets, "wallpaper",
                                      {'width': 176, 'height': 184, 'format': "JPEG"}))
    imagetargets.update(common.getkv(parentprofile.stockimagetargets, "pictureid",
                                      {'width': 176, 'height': 184, 'format': "JPEG"}))
    imagetargets.update(common.getkv(parentprofile.stockimagetargets, "outsidelcd",
                                      {'width': 96, 'height': 80, 'format': "JPEG"}))
    imagetargets.update(common.getkv(parentprofile.stockimagetargets, "fullscreen",
                                      {'width': 176, 'height': 220, 'format': "JPEG"}))
    def GetTargetsForImageOrigin(self, origin):
        return self.imagetargets
    def __init__(self):
        parentprofile.__init__(self)
    _supportedsyncs=(
        ('phonebook', 'read', None),  # all phonebook reading
        ('calendar', 'read', None),   # all calendar reading
        ('wallpaper', 'read', None),  # all wallpaper reading
        ('ringtone', 'read', None),   # all ringtone reading
        ('call_history', 'read', None),# all call history list reading
        ('sms', 'read', None),         # all SMS list reading
        ('phonebook', 'write', 'OVERWRITE'),  # only overwriting phonebook
        ('calendar', 'write', 'OVERWRITE'),   # only overwriting calendar
        ('wallpaper', 'write', 'MERGE'),      # merge and overwrite wallpaper
        ('wallpaper', 'write', 'OVERWRITE'),
        ('ringtone', 'write', 'MERGE'),      # merge and overwrite ringtone
        ('ringtone', 'write', 'OVERWRITE'),
        ('sms', 'write', 'OVERWRITE'),        # all SMS list writing
        )
