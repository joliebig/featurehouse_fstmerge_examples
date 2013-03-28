"""Communicate with the LG VI5225 cell phone
Also known as the LG-VX5400.  Operates on STI-Mobile, a virtual carrier
reselling Sprint airtime.
"""

import time

import cStringIO

import sha

import common

import copy

import p_lgvi5225

import com_lgvx4400

import com_brew

import com_phone

import com_lg

import prototypes

class  Phone (com_lgvx4400.Phone) :
	"Talk to the LG VI5225 cell phone"
	    desc="LG-VI5225"
	    protocolclass=p_lgvi5225
	    serialsname='lgvi5225'
	    imagelocations=(
        ( 10, "download/dloadindex/brewImageIndex.map", "brew/shared", "images", 30) ,
        ( 0xc8, "download/dloadindex/mmsImageIndex.map", "brew/shared/mms", "mms", 20),
        ( 0xdc, "download/dloadindex/mmsDrmImageIndex.map", "brew/shared/mms/d", "drm", 20), 
        )
	    ringtonelocations=(
        ( 50, "download/dloadindex/brewRingerIndex.map", "user/sound/ringer", "ringers", 30),
        ( 150, "download/dloadindex/mmsRingerIndex.map", "mms/sound", "mms", 20),
        ( 180, "download/dloadindex/mmsDrmRingerIndex.map", "mms/sound/drm", "drm", 20)
        )
	    builtinimages= ('Beach Ball', 'Towerbridge', 'Sunflower', 'Beach', 'Fish', 
                    'Sea', 'Snowman')
	    builtinringtones= ('Ring 1', 'Ring 2', 'Ring 3', 'Ring 4', 'Ring 5', 'Ring 6',
                       'Annen Polka', 'Beethoven Symphony No. 9', 'Pachelbel Canon', 
                       'Hallelujah', 'La Traviata', 'Leichte Kavallerie Overture', 
                       'Mozart Symphony No.40', 'Bach Minuet', 'Farewell', 
                       'Mozart Piano Sonata', 'Sting', 'Trout', 'Pineapple Rag', 
                       'Latin', 'Carol')
	    def __init__(self, logtarget, commport):

        com_lgvx4400.Phone.__init__(self,logtarget,commport)

        self.mode=self.MODENONE

	def eval_detect_data(self, res):

        found=False

        if res.get(self.brew_version_txt_key, None) is not None:

            found=res[self.brew_version_txt_key][:len(self.my_version_txt)]==self.my_version_txt

        if found:

            res['model']=self.my_model

            res['manufacturer']='LG Electronics Inc'

            s=res.get(self.esn_file_key, None)

            if s:

                res['esn']=self.get_esn(s)

	my_version_txt='AX545V'
	    my_model='VI5225'
	"Talk to the LG VI5225 cell phone"
parentprofile=com_lgvx4400.Profile
class  Profile (parentprofile) :
	protocolclass=Phone.protocolclass
	    serialsname=Phone.serialsname
	    phone_manufacturer='LG Electronics Inc'
	    phone_model='LG-LX5400V'
	    usbids_straight=( ( 0x1004, 0x6000, 2), )
	    usbids_usbtoserial=(
        ( 0x067b, 0x2303, None), 
        ( 0x0403, 0x6001, None), 
        ( 0x0731, 0x2003, None), 
        ( 0x6547, 0x0232, None), 
        )
	    usbids=usbids_straight+usbids_usbtoserial
	    WALLPAPER_WIDTH=120
	    WALLPAPER_HEIGHT=131
	    MAX_WALLPAPER_BASENAME_LENGTH=32
	    WALLPAPER_FILENAME_CHARS="_ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ."
	    WALLPAPER_CONVERT_FORMAT="bmp"
	    MAX_RINGTONE_BASENAME_LENGTH=32
	    RINGTONE_FILENAME_CHARS="_ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ."
	    imageorigins={}
	    imageorigins.update(common.getkv(parentprofile.stockimageorigins, "images"))
	    imageorigins.update(common.getkv(parentprofile.stockimageorigins, "mms"))
	    imageorigins.update(common.getkv(parentprofile.stockimageorigins, "drm"))
	    def GetImageOrigins(self):

        return self.imageorigins

	imagetargets={}
	    imagetargets.update(common.getkv(parentprofile.stockimagetargets, "wallpaper",
                                      {'width': 120, 'height': 131, 'format': "BMP"}))
	    def GetTargetsForImageOrigin(self, origin):

        return self.imagetargets

	_supportedsyncs=(
        ('phonebook', 'read', None),  
        ('phonebook', 'write', 'OVERWRITE'),  
       )
	    def __init__(self):

        parentprofile.__init__(self)

	    imageorigins.update(common.getkv(parentprofile.stockimageorigins, "images"))
	    imageorigins.update(common.getkv(parentprofile.stockimageorigins, "mms"))
	    imageorigins.update(common.getkv(parentprofile.stockimageorigins, "drm"))
	    imagetargets.update(common.getkv(parentprofile.stockimagetargets, "wallpaper",
                                      {'width': 120, 'height': 131, 'format': "BMP"}))

