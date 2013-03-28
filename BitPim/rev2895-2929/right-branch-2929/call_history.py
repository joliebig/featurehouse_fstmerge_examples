"""
Code to handle Call History data storage and display.
The format of the Call History is standardized.  It is an object with the
following attributes:
folder: string (where this item belongs)
datetime: string 'YYYYMMDDThhmmss' or (y,m,d,h,m,s)
number: string (the phone number of this call)
name: string (optional name associated with this number)
duration: int (optional duration of the call in minutes)
To implement Call History feature for a phone module:
  Add an entry into Profile._supportedsyncs:
  ('call_history', 'read', None),
  Implement the following method in your Phone class:
  def getcallhistory(self, result, merge):
     ...
     return result
The result dict key is 'call_history'.
"""
import copy
import sha
import time
import wx
import wx.lib.scrolledpanel as scrolled
import database
import guiwidgets
import phonenumber
import pubsub
import today
class CallHistoryDataobject(database.basedataobject):
    _knownproperties=['folder', 'datetime', 'number', 'name', 'duration' ]
    _knownlistproperties=database.basedataobject._knownlistproperties.copy()
    def __init__(self, data=None):
        if data is None or not isinstance(data, CallHistoryEntry):
            return;
        self.update(data.get_db_dict())
callhistoryobjectfactory=database.dataobjectfactory(CallHistoryDataobject)
class CallHistoryEntry(object):
    Folder_Incoming='Incoming'
    Folder_Outgoing='Outgoing'
    Folder_Missed='Missed'
    Folder_Data='Data'
    Valid_Folders=(Folder_Incoming, Folder_Outgoing, Folder_Missed, Folder_Data)
    _folder_key='folder'
    _datetime_key='datetime'
    _number_key='number'
    _name_key='name'
    _duration_key='duration'
    _unknown_datetime='YYYY-MM-DD hh:mm:ss'
    _id_index=0
    _max_id_index=999
    def __init__(self):
        self._data={ 'serials': [] }
        self._create_id()
    def __eq__(self, rhs):
        return self.folder==rhs.folder and self.datetime==rhs.datetime and\
               self.number==rhs.number
    def __ne__(self, rhs):
        return self.folder!=rhs.folder or self.datetime!=rhs.datetime or\
               self.number!=rhs.number
    def get(self):
        return copy.deepcopy(self._data, {})
    def set(self, d):
        self._data={}
        self._data.update(d)
    def get_db_dict(self):
        return self.get()
    def set_db_dict(self, d):
        self.set(d)
    def _create_id(self):
        "Create a BitPim serial for this entry"
        self._data.setdefault("serials", []).append(\
            {"sourcetype": "bitpim",
             "id": '%.3f%03d'%(time.time(), CallHistoryEntry._id_index) })
        if CallHistoryEntry._id_index<CallHistoryEntry._max_id_index:
            CallHistoryEntry._id_index+=1
        else:
            CallHistoryEntry._id_index=0
    def _get_id(self):
        s=self._data.get('serials', [])
        for n in s:
            if n.get('sourcetype', None)=='bitpim':
                return n.get('id', None)
        return None
    def _set_id(self, id):
        s=self._data.get('serials', [])
        for n in s:
            if n.get('sourcetype', None)=='bitpim':
                n['id']=id
                return
        self._data['serials'].append({'sourcetype': 'bitpim', 'id': id } )
    id=property(fget=_get_id, fset=_set_id)
    def _set_or_del(self, key, v, v_list=[]):
        if v is None or v in v_list:
            if self._data.has_key(key):
                del self._data[key]
        else:
            self._data[key]=v
    def _get_folder(self):
        return self._data.get(self._folder_key, '')
    def _set_folder(self, v):
        if v is None:
            if self._data.has_key(self._folder_key):
                del self._data[self._folder_key]
                return
        if not isinstance(v, (str, unicode)):
            raise TypeError,'not a string or unicode type'
        if v not in self.Valid_Folders:
            raise ValueError,'not a valid folder'
        self._data[self._folder_key]=v
    folder=property(fget=_get_folder, fset=_set_folder)
    def _get_number(self):
        return self._data.get(self._number_key, '')
    def _set_number(self, v):
        self._set_or_del(self._number_key, v, [''])
    number=property(fget=_get_number, fset=_set_number)
    def _get_name(self):
        return self._data.get(self._name_key, '')
    def _set_name(self, v):
        self._set_or_del(self._name_key, v, ('',))
    name=property(fget=_get_name, fset=_set_name)
    def _get_duration(self):
        return self._data.get(self._duration_key, None)
    def _set_duration(self, v):
        if v is not None and not isinstance(v, int):
            raise TypeError('duration property is an int arg')
        self._set_or_del(self._duration_key, v)
    duration=property(fget=_get_duration, fset=_set_duration)
    def _get_datetime(self):
        return self._data.get(self._datetime_key, '')
    def _set_datetime(self, v):
        if v is None:
            if self._data.has_key(self._datetime_key):
                del self._data[self._datetime_key]
            return
        if isinstance(v, (tuple, list)):
            if len(v)!=6:
                raise ValueError,'(y, m, d, h, m, s)'
            s='%04d%02d%02dT%02d%02d%02d'%tuple(v)
        elif isinstance(v, (str, unicode)):
            if len(v)!=15 or v[8]!='T':
                raise ValueError,'value must be in format YYYYMMDDThhmmss'
            s=v
        else:
            raise TypeError
        self._data[self._datetime_key]=s
    datetime=property(fget=_get_datetime, fset=_set_datetime)
    def get_repr(self, name=None):
        f=self.folder[0].upper()
        s=self.datetime
        if self.duration is None:
            _duration=''
        else:
            _duration='<%dm%ds>'%(self.duration/60, self.duration%60)
        if not len(s):
            s=f+'['+self._unknown_datetime+_duration+']'
        else:
            s=f+'['+s[:4]+'-'+s[4:6]+'-'+s[6:8]+' '+s[9:11]+':'+s[11:13]+\
               ':'+s[13:]+_duration+']  -  '
        if name:
            s+=name
        elif self.name:
            s+=self.name
        else:
            s+=phonenumber.format(self.number)
        return s
    def summary(self, name=None):
        s=self.datetime
        if s:
            s=s[4:6]+'/'+s[6:8]+' '+s[9:11]+':'+s[11:13]+' '
        else:
            s='**/** **:** '
        if name:
            s+=name
        elif self.name:
            s+=self.name
        else:
            s+=phonenumber.format(self.number)
        return s
    def _get_date_str(self):
        s=self.datetime
        if not len(s):
            return '****-**-**'
        else:
            return s[:4]+'-'+s[4:6]+'-'+s[6:8]
    date_str=property(fget=_get_date_str)
class CallHistoryWidget(scrolled.ScrolledPanel, guiwidgets.BitPimWidget):
    _data_key='call_history'
    _by_type=0
    _by_date=1
    _by_number=2
    def __init__(self, mainwindow, parent):
        super(CallHistoryWidget, self).__init__(parent, -1)
        self._main_window=mainwindow
        self._data={}
        self._node_dict={}
        self._name_map={}
        self._by_mode=self._by_type
        self._display_func=(self._display_by_type, self._display_by_date,
                             self._display_by_number)
        vbs=wx.BoxSizer(wx.VERTICAL)
        hbs=wx.BoxSizer(wx.HORIZONTAL)
        self.read_only=False
        self.historical_date=None
        static_bs=wx.StaticBoxSizer(wx.StaticBox(self, -1,
                                                 'Historical Data Status:'),
                                    wx.VERTICAL)
        self.historical_data_label=wx.StaticText(self, -1, 'Current Data')
        static_bs.Add(self.historical_data_label, 1, wx.EXPAND|wx.ALL, 5)
        hbs.Add(static_bs, 1, wx.EXPAND|wx.ALL, 5)
        vbs.Add(hbs, 0, wx.EXPAND|wx.ALL, 5)
        self._item_list=wx.TreeCtrl(self, wx.NewId(),
                                    style=wx.TR_MULTIPLE|wx.TR_HAS_BUTTONS)
        vbs.Add(self._item_list, 1, wx.EXPAND|wx.ALL, 5)
        self._root=self._item_list.AddRoot('Call History')
        self._nodes={}
        organize_menu=wx.Menu()
        organize_menu_data=(
            ('Type', self._OnOrganizedByType),
            ('Date', self._OnOrganizedByDate),
            ('Number', self._OnOrganizedByNumber))
        for e in organize_menu_data:
            id=wx.NewId()
            organize_menu.AppendRadioItem(id, e[0])
            wx.EVT_MENU(self, id, e[1])
        context_menu_data=(
            ('Expand All', self._OnExpandAll),
            ('Collapse All', self._OnCollapseAll))
        self._bgmenu=wx.Menu()
        self._bgmenu.AppendMenu(wx.NewId(), 'Organize Items by', organize_menu)
        for e in context_menu_data:
            id=wx.NewId()
            self._bgmenu.Append(id, e[0])
            wx.EVT_MENU(self, id, e[1])
        pubsub.subscribe(self._OnPBLookup, pubsub.RESPONSE_PB_LOOKUP)
        wx.EVT_RIGHT_UP(self._item_list, self._OnRightClick)
        self.SetSizer(vbs)
        self.SetAutoLayout(True)
        vbs.Fit(self)
        self.SetupScrolling()
        self._populate()
    def _OnPBLookup(self, msg):
        d=msg.data
        k=d.get('item', None)
        name=d.get('name', None)
        if k is None:
            return
        self._name_map[k]=name
    def _OnRightClick(self, evt):
        self._item_list.PopupMenu(self._bgmenu, evt.GetPosition())
    def _OnOrganizedByType(self, evt):
        evt.GetEventObject().Check(evt.GetId(), True)
        if self._by_mode!=self._by_type:
            self._by_mode=self._by_type
            self._display_func[self._by_type]()
            self._expand_all()
    def _OnOrganizedByDate(self, evt):
        evt.GetEventObject().Check(evt.GetId(), True)
        if self._by_mode!=self._by_date:
            self._by_mode=self._by_date
            self._display_func[self._by_date]()
            self._expand_all()
    def _OnOrganizedByNumber(self, evt):
        evt.GetEventObject().Check(evt.GetId(), True)
        if self._by_mode!=self._by_number:
            self._by_mode=self._by_number
            self._display_func[self._by_number]()
            self._expand_all()
    def _expand_all(self, sel_id=None):
        if sel_id is None:
            sel_id=self._root
        self._item_list.Expand(sel_id)
        id, cookie=self._item_list.GetFirstChild(sel_id)
        while id.IsOk():
            self._item_list.Expand(id)
            id, cookie=self._item_list.GetNextChild(sel_id, cookie)
    def _OnExpandAll(self, _):
        sel_ids=self._item_list.GetSelections()
        if not sel_ids:
            sel_ids=[self._root]
        for sel_id in sel_ids:
            if not sel_id.IsOk():
                sel_id=self._root
            self._expand_all(sel_id)
    def _OnCollapseAll(self, _):
        sel_ids=self._item_list.GetSelections()
        if not sel_ids:
            sel_ids=[self._root]
        for sel_id in sel_ids:
            if not sel_id.IsOk():
                sel_id=self._root
            self._item_list.Collapse(sel_id)
            id, cookie=self._item_list.GetFirstChild(sel_id)
            while id.IsOk():
                self._item_list.Collapse(id)
                id, cookie=self._item_list.GetNextChild(sel_id, cookie)
    def _clear(self):
        self._item_list.Collapse(self._root)
        for k,e in self._nodes.items():
            self._item_list.DeleteChildren(e)
    def _display_by_date(self):
        self._item_list.CollapseAndReset(self._root)
        self._nodes={}
        date_list=[]
        for k,e in self._data.items():
            if e.date_str not in date_list:
                date_list.append(e.date_str)
        date_list.sort()
        for s in date_list:
            self._nodes[s]=self._item_list.AppendItem(self._root, s)
        for k,e in self._data.items():
            i=self._item_list.AppendItem(self._nodes[e.date_str],
                                          e.get_repr(self._name_map.get(e.number, None)))
            self._item_list.SetItemPyData(i, k)
    def _display_by_number(self):
        self._item_list.CollapseAndReset(self._root)
        self._nodes={}
        number_list=[]
        for k,e in self._data.items():
            s=phonenumber.format(e.number)
            if s not in number_list:
                number_list.append(s)
        number_list.sort()
        for s in number_list:
            self._nodes[s]=self._item_list.AppendItem(self._root, s)
        for k,e in self._data.items():
            i=self._item_list.AppendItem(self._nodes[phonenumber.format(e.number)],
                                          e.get_repr(self._name_map.get(e.number, None)))
            self._item_list.SetItemPyData(i, k)
    def _display_by_type(self):
        self._item_list.CollapseAndReset(self._root)
        self._nodes={}
        for s in CallHistoryEntry.Valid_Folders:
            self._nodes[s]=self._item_list.AppendItem(self._root, s)
        node_dict={}
        for k,e in self._data.items():
            node_dict[e.get_repr(self._name_map.get(e.number, None))]=k
        keys=node_dict.keys()
        keys.sort()
        for k in keys:
            data_key=node_dict[k]
            n=self._data[data_key]
            i=self._item_list.AppendItem(self._nodes[n.folder], k)
            self._item_list.SetItemPyData(i, data_key)
    def _publish_today_data(self):
        keys=[(x.datetime, k) for k,x in self._data.items()]
        keys.sort()
        keys.reverse()
        today_event=today.TodayIncomingCallsEvent()
        today_event.names=[self._data[k].summary(self._name_map.get(self._data[k].number, None))\
                                    for _,k in keys \
                                    if self._data[k].folder==CallHistoryEntry.Folder_Incoming]
        today_event.broadcast()
        today_event=today.TodayMissedCallsEvent()
        today_event.names=[self._data[k].summary(self._name_map.get(self._data[k].number, None))\
                                    for _,k in keys \
                                    if self._data[k].folder==CallHistoryEntry.Folder_Missed]
        today_event.broadcast()
    def _populate(self):
        self._clear()
        self._node_dict={}
        for k,e in self._data.items():
            if e.name:
                if not self._name_map.has_key(e.number):
                    self._name_map[e.number]=e.name
            else:
                if not self._name_map.has_key(e.number):
                    pubsub.publish(pubsub.REQUEST_PB_LOOKUP,
                                   { 'item': e.number } )
        self._display_func[self._by_mode]()
        self._OnExpandAll(None)
        self._publish_today_data()
    def OnDelete(self, _):
        if self.read_only:
            return
        sels_idx=self._item_list.GetSelections()
        if not sels_idx:
            return
        for sel_idx in sels_idx:
            if not sel_idx.Ok():
                continue
            k=self._item_list.GetPyData(sel_idx)
            if k is None:
                continue
            self._item_list.Delete(sel_idx)
            del self._data[k]
        self._save_to_db(self._data)
    def getdata(self, dict, want=None):
        dict[self._data_key]=copy.deepcopy(self._data)
    def populate(self, dict, force=False):
        if self.read_only and not force:
            return
        self._data=dict.get(self._data_key, {})
        self._populate()
    def _save_to_db(self, dict):
        if self.read_only:
            return
        db_rr={}
        for k,e in dict.items():
            db_rr[k]=CallHistoryDataobject(e)
        database.ensurerecordtype(db_rr, callhistoryobjectfactory)
        self._main_window.database.savemajordict(self._data_key, db_rr)
    def populatefs(self, dict):
        if self.read_only:
            wx.MessageBox('You are viewing historical data which cannot be changed or saved',
                             'Cannot Save Call History Data',
                             style=wx.OK|wx.ICON_ERROR)
        else:
            self._save_to_db(dict.get(self._data_key, {}))
        return dict
    def getfromfs(self, result, timestamp=None):
        dict=self._main_window.database.\
                   getmajordictvalues(self._data_key,
                                      callhistoryobjectfactory,
                                      at_time=timestamp)
        r={}
        for k,e in dict.items():
            ce=CallHistoryEntry()
            ce.set_db_dict(e)
            r[ce.id]=ce
        result.update({ self._data_key: r})
        return result
    def merge(self, dict):
        if self.read_only:
            wx.MessageBox('You are viewing historical data which cannot be changed or saved',
                             'Cannot Save Call History Data',
                             style=wx.OK|wx.ICON_ERROR)
            return
        d=dict.get(self._data_key, {})
        l=[e for k,e in self._data.items()]
        for k,e in d.items():
            if e not in l:
                self._data[e.id]=e
        self._save_to_db(self._data)
        self._populate()
    def OnHistoricalData(self):
        """Display current or historical data"""
        if self.read_only:
            current_choice=guiwidgets.HistoricalDataDialog.Historical_Data
        else:
            current_choice=guiwidgets.HistoricalDataDialog.Current_Data
        dlg=guiwidgets.HistoricalDataDialog(self,
                                            current_choice=current_choice,
                                            historical_date=self.historical_date,
                                            historical_events=\
                                            self._main_window.database.getchangescount(self._data_key))
        if dlg.ShowModal()==wx.ID_OK:
            self._main_window.OnBusyStart()
            current_choice, self.historical_date=dlg.GetValue()
            r={}
            if current_choice==guiwidgets.HistoricalDataDialog.Current_Data:
                self.read_only=False
                msg_str='Current Data'
                self.getfromfs(r)
            else:
                self.read_only=True
                msg_str='Historical Data as of %s'%\
                         str(wx.DateTimeFromTimeT(self.historical_date))
                self.getfromfs(r, self.historical_date)
            self.populate(r, True)
            self.historical_data_label.SetLabel(msg_str)
            self._main_window.OnBusyEnd()
        dlg.Destroy()
    def get_selected_data(self):
        res={}
        for sel_idx in self._item_list.GetSelections():
            k=self._item_list.GetPyData(sel_idx)
            if k:
                res[k]=self._data[k]
        return res
    def get_data(self):
        return self._data
