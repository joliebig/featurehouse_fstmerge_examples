"""The publish subscribe mechanism used to maintain lists of stuff.
This helps different pieces of code maintain lists of things (eg
wallpapers, categories) and other to express and interest and be
notified when it changes (eg field editors).  The wxPython pubsub
module is the base.  The enhancements are a list of standard topics in
this file.
This code also used to be larger as the wxPython pubsub didn't use
weak references.  It does now, so a whole bunch of code could be
deleted.
"""

from wx.lib.pubsub import Publisher

REQUEST_CATEGORIES=( 'request', 'categories' )

ALL_CATEGORIES=( 'response', 'categories')

SET_CATEGORIES=( 'request', 'setcategories')

ADD_CATEGORY=( 'request', 'addcategory')

MERGE_CATEGORIES=( 'request', 'mergecategories')

ALL_WALLPAPERS=( 'response', 'wallpapers')

REQUEST_WALLPAPERS=( 'request', 'wallpapers')

ALL_RINGTONES=( 'response', 'ringtones' )

REQUEST_RINGTONES=( 'request', 'ringtones')

PHONE_MODEL_CHANGED=( 'notification', 'phonemodelchanged')

REQUEST_RINGTONE_INDEX=('request', 'ringtone-index')

ALL_RINGTONE_INDEX=('response', 'ringtone-index')

REQUEST_PB_LOOKUP=('request', 'phonebook')

RESPONSE_PB_LOOKUP=('response', 'phonebook')

MEDIA_NAME_CHANGED=('notificaion', 'medianamechanged')

REQUEST_TAB_CHANGED=('notification', 'tabchanges')

TODAY_ITEM_SELECTED=('notification', 'todayitemselected')

REQUEST_TODAY_DATA=('request', 'todaydata')

RESPONSE_TODAY_DATA=('response', 'todaydata')

NEW_DATA_AVAILABLE=('notification', 'dataavailable')

MIDNIGHT=('notification', 'midnight')

media_change_type='type'

wallpaper_type='wallpaper'

ringtone_type='ringtone'

media_old_name='old_name'

media_new_name='new_name'

def subscribe(listener, topic):

    Publisher.subscribe(listener, topic)

def unsubscribe(listener):

    Publisher.unsubscribe(listener)

def publish(topic, data=None):

    Publisher.sendMessage(topic, data)

