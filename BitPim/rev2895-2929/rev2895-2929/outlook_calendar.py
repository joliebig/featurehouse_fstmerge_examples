"Deals with Outlook calendar import stuff"

import datetime

import pywintypes

import sys

import time

import wx

import wx.calendar

import wx.lib.mixins.listctrl as listmix

import bpcalendar

import common

import common_calendar

import guiwidgets

import helpids

import native.outlook

def to_bp_date(dict, v, oc):

    if not isinstance(v, pywintypes.TimeType):

        raise TypeError, 'illegal type'

    if v.year>common_calendar.no_end_date[0]:

        return common_calendar.no_end_date

    return (v.year, v.month, v.day, v.hour, v.minute)

def bp_repeat_str(dict, v):

    if v is None:

        return ''

    elif v==OutlookCalendarImportData.olRecursDaily:

        return 'Daily'

    elif v==OutlookCalendarImportData.olRecursWeekly:

        return 'Weekly'

    elif v==OutlookCalendarImportData.olRecursMonthly or \
         v==OutlookCalendarImportData.olRecursMonthNth:

        return 'Monthly'

    elif v==OutlookCalendarImportData.olRecursYearly:

        return 'Yearly'

    else:

        return '<Unknown Value>'

def convert_categories(dict, v, oc):

    return [x.strip() for x in v.split(",") if len(x)]

def set_recurrence(item, dict, oc):

    oc.update_display()

    if not dict['repeat']:

        dict['repeat']=None

        return True

    return oc.process_repeat(item, dict)

class  OutlookCalendarImportData :
	_non_auto_sync_calendar_keys=[
        ('Subject', 'description', None),
        ('Location', 'location', None),
        ('Start', 'start', to_bp_date),
        ('End', 'end', to_bp_date),
        ('Categories', 'categories', convert_categories),
        ('IsRecurring', 'repeat', None),
        ('ReminderSet', 'alarm', None),
        ('ReminderMinutesBeforeStart', 'alarm_value', None),
        ('Importance', 'priority', None),
        ('Body', 'notes', None),
        ('AllDayEvent', 'allday', None)
        ]
	    _auto_sync_calendar_keys=[
        ('Subject', 'description', None),
        ('Location', 'location', None),
        ('Start', 'start', to_bp_date),
        ('End', 'end', to_bp_date),
        ('Categories', 'categories', convert_categories),
        ('IsRecurring', 'repeat', None),
        ('ReminderSet', 'alarm', None),
        ('ReminderMinutesBeforeStart', 'alarm_value', None),
        ('Importance', 'priority', None),
        ('AllDayEvent', 'allday', None)
        ]
	    _recurrence_keys=[
        ('NoEndDate', 'NoEndDate', None),
        ('PatternStartDate', 'PatternStartDate', to_bp_date),
        ('PatternEndDate', 'PatternEndDate', to_bp_date),
        ('Instance', 'Instance', None),
        ('DayOfWeekMask', 'DayOfWeekMask', None),
        ('Interval', 'Interval', None),
        ('Occurrences', 'Occurrences', None),
        ('RecurrenceType', 'RecurrenceType', None)
        ]
	    _exception_keys=[
        ('OriginalDate', 'exception_date', to_bp_date),
        ('Deleted', 'deleted', None)
        ]
	    _default_filter={
        'start': None,
        'end': None,
        'categories': None,
        'rpt_events': False,
        'no_alarm': False,
        'ringtone': None,
        'alarm_override':False,
        'vibrate':False,
        'alarm_value':0
        }
	    olRecursDaily    = native.outlook.outlook_com.constants.olRecursDaily
	    olRecursMonthNth = native.outlook.outlook_com.constants.olRecursMonthNth
	    olRecursMonthly  = native.outlook.outlook_com.constants.olRecursMonthly
	    olRecursWeekly   = native.outlook.outlook_com.constants.olRecursWeekly
	    olRecursYearNth  = native.outlook.outlook_com.constants.olRecursYearNth
	    olRecursYearly   = native.outlook.outlook_com.constants.olRecursYearly
	    olImportanceHigh = native.outlook.outlook_com.constants.olImportanceHigh
	    olImportanceLow  = native.outlook.outlook_com.constants.olImportanceLow
	    olImportanceNormal = native.outlook.outlook_com.constants.olImportanceNormal
	    def __init__(self, outlook, auto_sync_only=0):

        self._outlook=outlook

        self._data=[]

        self._error_list=[]

        self._single_data=[]

        self._folder=None

        self._filter=self._default_filter

        self._total_count=0

        self._current_count=0

        self._update_dlg=None

        self._exception_list=[]

        if auto_sync_only:

            self._calendar_keys=self._auto_sync_calendar_keys

        else:

            self._calendar_keys=self._non_auto_sync_calendar_keys

	def _accept(self, entry):

        s_date=entry['start'][:3]

        e_date=entry['end'][:3]

        if entry.get('repeat', False):

            if self._filter['start'] is not None and \
               e_date<self._filter['start'][:3]:

                return False

            if self._filter['end'] is not None and \
               s_date>self._filter['end'][:3]:

                return False

        else:

            if self._filter['start'] is not None and \
               e_date<self._filter['start'][:3]:

                return False

            if self._filter['end'] is not None and \
               e_date>self._filter['end'][:3]:

                return False

        c=self._filter.get('categories', None)

        if c is None or not len(c):

            return True

        if len([x for x in entry['categories'] if x in c]):

            return True

        return False

	def _populate_entry(self, e, ce):

        ce.description=e.get('description', None)

        ce.location=e.get('location', None)

        v=e.get('priority', None)

        if v is not None:

            if v==self.olImportanceNormal:

                ce.priority=ce.priority_normal

            elif v==self.olImportanceLow:

                ce.priority=ce.priority_low

            elif v==self.olImportanceHigh:

                ce.priority=ce.priority_high

        if not self._filter.get('no_alarm', False) and \
               not self._filter.get('alarm_override', False) and \
               e.get('alarm', False):

            ce.alarm=e.get('alarm_value', 0)

            ce.ringtone=self._filter.get('ringtone', "")

            ce.vibrate=self._filter.get('vibrate', False)

        elif not self._filter.get('no_alarm', False) and \
               self._filter.get('alarm_override', False):

            ce.alarm=self._filter.get('alarm_value', 0)

            ce.ringtone=self._filter.get('ringtone', "")

            ce.vibrate=self._filter.get('vibrate', False)

        ce.allday=e.get('allday', False)

        if ce.allday:

            if not e.get('repeat', False):

                ce.start=e['start'][:3]+(0,0)

                ce.end=e['start'][:3]+(23,59)

            else:

                ce.start=e['start'][:3]+(0,0)

                ce.end=e['end'][:3]+(23,59)

        else:

            ce.start=e['start']

            ce.end=e['end']

        ce.notes=e.get('notes', None)

        v=[]

        for k in e.get('categories', []):

            v.append({ 'category': k })

        ce.categories=v

        if not e.get('repeat', False):

            return

        rp=bpcalendar.RepeatEntry()

        rt=e['repeat_type']

        r_interval=e.get('repeat_interval', 0)

        r_interval2=e.get('repeat_interval2', 1)

        r_dow=e.get('repeat_dow', 0)

        if rt==self.olRecursDaily:

            rp.repeat_type=rp.daily

        elif rt==self.olRecursWeekly:

            if r_interval:

                rp.repeat_type=rp.weekly

            else:

                rp.repeat_type=rp.daily

        elif rt==self.olRecursMonthly or rt==self.olRecursMonthNth:

            rp.repeat_type=rp.monthly

        else:

            rp.repeat_type=rp.yearly

        if rp.repeat_type==rp.daily:

            rp.interval=r_interval

        elif rp.repeat_type==rp.weekly or rp.repeat_type==rp.monthly:

            rp.interval=r_interval

            rp.interval2=r_interval2

            rp.dow=r_dow

        for k in e.get('exceptions', []):

            rp.add_suppressed(*k[:3])

        ce.repeat=rp

	def _generate_repeat_events(self, e):

        ce=bpcalendar.CalendarEntry()

        self._populate_entry(e, ce)

        l=[]

        new_e=e.copy()

        new_e['repeat']=False

        for k in ('repeat_type', 'repeat_interval', 'repeat_dow'):

            if new_e.has_key(k):

                del new_e[k]

        s_date=datetime.datetime(*self._filter['start'])

        e_date=datetime.datetime(*self._filter['end'])

        one_day=datetime.timedelta(1)

        this_date=s_date

        while this_date<=e_date:

            date_l=(this_date.year, this_date.month, this_date.day)

            if ce.is_active(*date_l):

                new_e['start']=date_l+new_e['start'][3:]

                new_e['end']=date_l+new_e['end'][3:]

                l.append(new_e.copy())

            this_date+=one_day

        return l

	def get(self):

        res={}

        single_rpt=self._filter.get('rpt_events', False)

        for k in self._data:

            if self._accept(k):

                if k.get('repeat', False) and single_rpt:

                    d=self._generate_repeat_events(k)

                else:

                    d=[k]

                for n in d:

                    ce=bpcalendar.CalendarEntry()

                    self._populate_entry(n, ce)

                    res[ce.id]=ce

        return res

	def get_display_data(self):

        cnt=0

        res={}

        single_rpt=self._filter.get('rpt_events', False)

        no_alarm=self._filter.get('no_alarm', False)

        for k in self._data:

            if self._accept(k):

                if k.get('repeat', False) and single_rpt:

                    d=self._generate_repeat_events(k)

                else:

                    d=[k.copy()]

                for n in d:

                    if no_alarm:

                        n['alarm']=False

                    res[cnt]=n

                    cnt+=1

        return res

	def get_category_list(self):

        l=[]

        for e in self._data:

            l+=[x for x in e.get('categories', []) if x not in l]

        return l

	def pick_folder(self):

        return self._outlook.pickfolder()

	def set_folder(self, f):

        if f is None:

            self._folder=self._outlook.getfolderfromid('', True, 'calendar')

        else:

            self._folder=f

	def get_folder_id(self):

        return self._outlook.getfolderid(self._folder)

	def set_folder_id(self, id):

        if id is None or id=="":

            self.set_folder(None)

        else:

            self._folder=self._outlook.getfolderfromid(id)

	def set_filter(self, filter):

        self._filter=filter

	def get_filter(self):

        return self._filter

	def get_folder_name(self):

        if self._folder is None:

            return '<None>'

        return self._outlook.getfoldername(self._folder)

	def read(self, folder=None, update_dlg=None):

        if folder is not None:

            self._folder=folder

        if self._folder is None:

            self._folder=self._outlook.getfolderfromid('', True, 'calendar')

        self._update_dlg=update_dlg

        self._total_count=self._folder.Items.Count

        self._current_count=0

        self._exception_list=[]

        self._data, self._error_list=self._outlook.getdata(self._folder,
                                                           self._calendar_keys,
                                                           {}, self,
                                                           set_recurrence)

        self._data+=self._exception_list

	def _set_repeat_dates(self, dict, r):

        dict['start']=r['PatternStartDate'][:3]+dict['start'][3:]

        dict['end']=r['PatternEndDate'][:3]+dict['end'][3:]

        dict['repeat_type']=r['RecurrenceType']

	def _is_daily_or_weekly(self, dict, r):

        if r['RecurrenceType']==self.olRecursDaily or \
           r['RecurrenceType']==self.olRecursWeekly:

            self._set_repeat_dates(dict, r)

            dict['repeat_interval']=r['Interval']

            dict['repeat_dow']=r['DayOfWeekMask']

            return True

        return False

	def _is_monthly(self, dict, r):

        if r['RecurrenceType']==self.olRecursMonthly or \
           r['RecurrenceType']==self.olRecursMonthNth:

            self._set_repeat_dates(dict, r)

            dict['repeat_interval2']=r['Interval']

            if r['RecurrenceType']==self.olRecursMonthNth:

                dict['repeat_interval']=r['Instance']

                dict['repeat_dow']=r['DayOfWeekMask']

            return True

        return False

	def _is_yearly(self, dict, r):

        if r['RecurrenceType']==self.olRecursYearly and \
           r['Interval']==12:

            self._set_repeat_dates(dict, r)

            return True

        return False

	def _process_exceptions(self, dict, r):

        r_ex=r.Exceptions

        if not r_ex.Count:

            return

        for i in range(1, r_ex.Count+1):

            ex=self._outlook.getitemdata(r_ex.Item(i), {},
                                          self._exception_keys, self)

            dict.setdefault('exceptions', []).append(ex['exception_date'])

            if not ex['deleted']:

                appt=self._outlook.getitemdata(r_ex.Item(i).AppointmentItem,
                                                {}, self._calendar_keys, self)

                appt['repeat']=False

                appt['end']=appt['start'][:3]+appt['end'][3:]

                self._exception_list.append(appt)

	def process_repeat(self, item, dict):

        rec_pat=item.GetRecurrencePattern()

        r=self._outlook.getitemdata(rec_pat, {},
                                     self._recurrence_keys, self)

        if self._is_daily_or_weekly(dict, r) or \
           self._is_monthly(dict, r) or \
           self._is_yearly(dict, r):

            self._process_exceptions(dict, rec_pat)

            return True

        dict['repeat']=False

        dict['end']=dict['start'][:3]+dict['end'][3:]

        dict['notes']+=' [BITPIM: Unrecognized repeat event, repeat event discarded]'

        return True

	def update_display(self):

        self._current_count += 1

        if self._update_dlg is not None:

            self._update_dlg.Update(100*self._current_count/self._total_count)

	def has_errors(self):

        return bool(self._error_list)

	def get_error_list(self):

        res=[]

        for d in self._error_list:

            _start=d.get('start', None)

            s=''

            if _start:

                if len(_start)>4:

                    s='%02d-%02d-%02d %02d:%02d '%_start[:5]

                elif len(_start)>2:

                    s='%02d-%02d-%02d '%_start[:3]

            _desc=d.get('description', None)

            if _desc:

                s+=_desc

            if not s:

                s='<Unknown>'

            res.append(s)

        return res


class  OutlookImportCalDialog (common_calendar.PreviewDialog) :
	_column_labels=[
        ('description', 'Description', 400, None),
        ('start', 'Start', 150, common_calendar.bp_date_str),
        ('end', 'End', 150, common_calendar.bp_date_str),
        ('repeat_type', 'Repeat', 80, bp_repeat_str),
        ('alarm', 'Alarm', 80, common_calendar.bp_alarm_str),
        ('categories', 'Category', 150, common_calendar.category_str)
        ]
	    ID_ADD=wx.NewId()
	    _config_name='import/calendar/outlookdialog'
	    _browse_label='Outlook Calendar Folder:'
	    _progress_dlg_title='Outlook Calendar Import'
	    _error_dlg_title='Outlook Calendar Import Error'
	    _error_dlg_text='Outlook Calendar Items that failed to import:'
	    _data_class=OutlookCalendarImportData
	    _filter_dlg_class=common_calendar.FilterDialog
	    def __init__(self, parent, id, title):

        self._oc=self._data_class(native.outlook)

        self._oc.set_folder(None)

        common_calendar.PreviewDialog.__init__(self, parent, id, title,
                               self._column_labels,
                               self._oc.get_display_data(),
                               config_name=self._config_name)

	def getcontrols(self, main_bs):

        hbs=wx.BoxSizer(wx.HORIZONTAL)

        hbs.Add(wx.StaticText(self, -1, self._browse_label), 0, wx.ALL|wx.ALIGN_CENTRE, 2)

        self.folderctrl=wx.TextCtrl(self, -1, "", style=wx.TE_READONLY)

        self.folderctrl.SetValue(self._oc.get_folder_name())

        hbs.Add(self.folderctrl, 1, wx.EXPAND|wx.ALL, 2)

        id_browse=wx.NewId()

        hbs.Add(wx.Button(self, id_browse, 'Browse ...'), 0, wx.EXPAND|wx.ALL, 2)

        main_bs.Add(hbs, 0, wx.EXPAND|wx.ALL, 5)

        main_bs.Add(wx.StaticLine(self, -1), 0, wx.EXPAND|wx.TOP|wx.BOTTOM, 5)

        wx.EVT_BUTTON(self, id_browse, self.OnBrowseFolder)

	def getpostcontrols(self, main_bs):

        main_bs.Add(wx.StaticLine(self, -1), 0, wx.EXPAND|wx.TOP|wx.BOTTOM, 5)

        hbs=wx.BoxSizer(wx.HORIZONTAL)

        id_import=wx.NewId()

        hbs.Add(wx.Button(self, id_import, 'Import'), 0, wx.ALIGN_CENTRE|wx.ALL, 5)

        hbs.Add(wx.Button(self, wx.ID_OK, 'Replace All'), 0, wx.ALIGN_CENTRE|wx.ALL, 5)

        hbs.Add(wx.Button(self, self.ID_ADD, 'Add'), 0, wx.ALIGN_CENTRE|wx.ALL, 5)

        hbs.Add(wx.Button(self, wx.ID_CANCEL, 'Cancel'), 0, wx.ALIGN_CENTRE|wx.ALL, 5)

        id_filter=wx.NewId()

        hbs.Add(wx.Button(self, id_filter, 'Filter'), 0, wx.ALIGN_CENTRE|wx.ALL, 5)

        hbs.Add(wx.Button(self, wx.ID_HELP, 'Help'), 0,  wx.ALIGN_CENTRE|wx.ALL, 5)

        main_bs.Add(hbs, 0, wx.ALIGN_CENTRE|wx.ALL, 5)

        wx.EVT_BUTTON(self, id_import, self.OnImport)

        wx.EVT_BUTTON(self, id_filter, self.OnFilter)

        wx.EVT_BUTTON(self, self.ID_ADD, self.OnAdd)

        wx.EVT_BUTTON(self, wx.ID_HELP, lambda *_: wx.GetApp().displayhelpid(helpids.ID_DLG_CALENDAR_IMPORT))

	def OnImport(self, evt):

        wx.BeginBusyCursor()

        dlg=wx.ProgressDialog(self._progress_dlg_title,
                              'Importing Outlook Data, please wait ...\n(Please also watch out for the Outlook Permission Request dialog)',
                              parent=self)

        self._oc.read(None, dlg)

        self.populate(self._oc.get_display_data())

        dlg.Destroy()

        if self._oc.has_errors():

            error_dlg=wx.SingleChoiceDialog(self,
                                            self._error_dlg_text,
                                            self._error_dlg_title,
                                            self._oc.get_error_list())

            error_dlg.ShowModal()

            error_dlg.Destroy()

        wx.EndBusyCursor()

	def OnBrowseFolder(self, evt):

        f=self._oc.pick_folder()

        if f is None:

            return 

        self._oc.set_folder(f)

        self.folderctrl.SetValue(self._oc.get_folder_name())

	def OnFilter(self, evt):

        cat_list=self._oc.get_category_list()

        dlg=self._filter_dlg_class(self, -1, 'Filtering Parameters', cat_list)

        if dlg.ShowModal()==wx.ID_OK:

            self._oc.set_filter(dlg.get())

            self.populate(self._oc.get_display_data())

	def OnAdd(self, evt):

        self.EndModal(self.ID_ADD)

	def get(self):

        return self._oc.get()

	def get_categories(self):

        return self._oc.get_category_list()


def ImportCal(folder, filters):

    _oc=OutlookCalendarImportData(native.outlook, auto_sync_only=1)

    _oc.set_folder_id(folder)

    _oc.set_filter(filters)

    _oc.read()

    res={ 'calendar':_oc.get() }

    return res

class  OutlookAutoConfCalDialog (wx.Dialog) :
	def __init__(self, parent, id, title, folder, filters,
                 style=wx.CAPTION|wx.MAXIMIZE_BOX| \
                 wx.SYSTEM_MENU|wx.DEFAULT_DIALOG_STYLE|wx.RESIZE_BORDER):

        self._oc=OutlookCalendarImportData(native.outlook, auto_sync_only=1)

        self._oc.set_folder_id(folder)

        self._oc.set_filter(filters)

        self.__read=False

        wx.Dialog.__init__(self, parent, id=id, title=title, style=style)

        main_bs=wx.BoxSizer(wx.VERTICAL)

        hbs=wx.BoxSizer(wx.HORIZONTAL)

        hbs.Add(wx.StaticText(self, -1, "Outlook Calendar Folder:"), 0, wx.ALL|wx.ALIGN_CENTRE, 2)

        self.folderctrl=wx.TextCtrl(self, -1, "", style=wx.TE_READONLY)

        self.folderctrl.SetValue(self._oc.get_folder_name())

        hbs.Add(self.folderctrl, 1, wx.EXPAND|wx.ALL, 2)

        id_browse=wx.NewId()

        hbs.Add(wx.Button(self, id_browse, 'Browse ...'), 0, wx.EXPAND|wx.ALL, 2)

        main_bs.Add(hbs, 0, wx.EXPAND|wx.ALL, 5)

        main_bs.Add(wx.StaticLine(self, -1), 0, wx.EXPAND|wx.TOP|wx.BOTTOM, 5)

        wx.EVT_BUTTON(self, id_browse, self.OnBrowseFolder)

        hbs=wx.BoxSizer(wx.HORIZONTAL)

        hbs.Add(wx.Button(self, wx.ID_OK, 'OK'), 0, wx.ALIGN_CENTRE|wx.ALL, 5)

        hbs.Add(wx.Button(self, wx.ID_CANCEL, 'Cancel'), 0, wx.ALIGN_CENTRE|wx.ALL, 5)

        id_filter=wx.NewId()

        hbs.Add(wx.Button(self, id_filter, 'Filter'), 0, wx.ALIGN_CENTRE|wx.ALL, 5)

        hbs.Add(wx.Button(self, wx.ID_HELP, 'Help'), 0,  wx.ALIGN_CENTRE|wx.ALL, 5)

        main_bs.Add(hbs, 0, wx.ALIGN_CENTRE|wx.ALL, 5)

        wx.EVT_BUTTON(self, id_filter, self.OnFilter)

        wx.EVT_BUTTON(self, wx.ID_HELP, lambda *_: wx.GetApp().displayhelpid(helpids.ID_DLG_CALENDAR_IMPORT))

        self.SetSizer(main_bs)

        self.SetAutoLayout(True)

        main_bs.Fit(self)

	def OnBrowseFolder(self, evt):

        f=self._oc.pick_folder()

        if f is None:

            return 

        self._oc.set_folder(f)

        self.folderctrl.SetValue(self._oc.get_folder_name())

        self.__read=False

	def OnFilter(self, evt):

        if not self.__read:

            self._oc.read()

            self.__read=True

        cat_list=self._oc.get_category_list()

        dlg=common_calendar.AutoSyncFilterDialog(self, -1, 'Filtering Parameters', cat_list)

        dlg.set(self._oc.get_filter())

        if dlg.ShowModal()==wx.ID_OK:

            self._oc.set_filter(dlg.get())

	def GetFolder(self):

        return self._oc.get_folder_id()

	def GetFilter(self):

        return self._oc.get_filter()


