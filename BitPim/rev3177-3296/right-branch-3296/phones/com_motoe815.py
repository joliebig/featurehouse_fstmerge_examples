"""Communicate with Motorola E815 phones using AT commands"""
import com_motov710
parentphone=com_motov710.Phone
class Phone(parentphone):
    desc='Moto-E815'
    serialsname='motoe815'
parentprofile=com_motov710.Profile
class Profile(parentprofile):
    serialsname=Phone.serialsname
    phone_manufacturer='Motorola'
    phone_model='E815 '
    common_model_name='E815'
    generic_phone_model='Motorola CDMA e815'
