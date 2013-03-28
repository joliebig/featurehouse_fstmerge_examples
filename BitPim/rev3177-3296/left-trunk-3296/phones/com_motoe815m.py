"""Communicate with Motorola E815m phones using AT commands"""
import com_motov710m
parentphone=com_motov710m.Phone
class Phone(parentphone):
    desc='Moto-E815m'
    serialsname='motoe815m'
parentprofile=com_motov710m.Profile
class Profile(parentprofile):
    serialsname=Phone.serialsname
    phone_manufacturer='Motorola'
    phone_model='E815M'
    common_model_name='E815'
    generic_phone_model='Motorola CDMA e815 Phone'
