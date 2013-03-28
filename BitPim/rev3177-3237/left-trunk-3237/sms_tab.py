"""
Code to handle the SMS Tab of the BitPim main display.
The read flag is not required for outbox and saved message, delivery
status is not needed for saved and inbox message, from is not required for
save and outbox, to is not required for inbox etc.
"""
import copy
import wx
import wx.gizmos as gizmos
import wx.lib.scrolledpanel as scrolled
import database
import guiwidgets
import phonebookentryeditor as pb_editor
import phonenumber
import pubsub
import sms
import today
import guihelper
import widgets
class StaticText(wx.StaticText):
    def __init__(self, parent, _=None):
        super(StaticText, self).__init__(parent, -1)
    def SetValue(self, v):
        if v.find('subject') and (v.find('\n')>-1):
            v=v.split('\n')[0]
        if len(v)==10 and v.isdigit():
                v='(%03d)-%03d-%04d'%(int(v[:3]),int(v[3:6]),int(v[6:10]))
        elif len(v)==11 and v.isdigit() and v[0]=='1':
                v='1-(%03d)-%03d-%04d'%(int(v[1:4]),int(v[4:7]),int(v[7:11]))
        self.SetLabel(v)
class TimeStamp(wx.StaticText):
    def __init__(self, parent, _=None):
        super(TimeStamp, self).__init__(parent, -1)
    def SetValue(self, v):
        if v:
            self.SetLabel('%04d-%02d-%02d %02d:%02d:%02d'%(
                int(v[:4]), int(v[4:6]), int(v[6:8]),
                int(v[9:11]), int(v[11:13]), int(v[13:])))
        else:
            self.SetLabel('')
class DeliveryStatus(wx.StaticText):
    def __init__(self, parent, _=None):
        super(DeliveryStatus, self).__init__(parent, -1)
    def SetValue(self, v):
        self.SetLabel('\n'.join(v))
class SMSInfo(pb_editor.DirtyUIBase):
    _dict_key_index=0
    _label_index=1
    _class_index=2
    _get_index=3
    _set_index=4
    _w_index=5
    _flg_index=6
    _not_used_fields={
        sms.SMSEntry.Folder_Inbox: ('delivery_status', '_to'),
        sms.SMSEntry.Folder_Sent: ('read', '_from'),
        sms.SMSEntry.Folder_Saved: ('delivery_status',) }
    def __init__(self, parent, _=None):
        super(SMSInfo, self).__init__(parent)
        self._fields=[
            ['_from', 'From:', StaticText, None, None, None, 0],
            ['_to', 'To:', StaticText, None, None, None, 0],
            ['callback', 'Callback #:', StaticText, None, None, None, 0],
            ['subject', 'Subject:', StaticText, None, None, None, 0],
            ['datetime', 'Date:', TimeStamp, None, None, None, 0],
            ['priority_str', 'Priority:', StaticText, None, None, None, 0],
            ['read', 'Read?:', wx.CheckBox, None, None, None, 0],
            ['locked', 'Locked:', wx.CheckBox, None, None, None, 0],
            ['delivery_status', 'Delivery Status:', DeliveryStatus, None, None,
             None, wx.EXPAND],
            ]
        gs=wx.FlexGridSizer(-1, 2, 5, 5)
        gs.AddGrowableCol(1)
        for n in self._fields:
            gs.Add(wx.StaticText(self, -1, n[self._label_index],
                                 style=wx.ALIGN_LEFT),0, wx.EXPAND|wx.BOTTOM, 0)
            w=n[self._class_index](self, -1)
            gs.Add(w, 0, n[self._flg_index]|wx.BOTTOM, 0)
            n[self._w_index]=w
        self.SetSizer(gs)
        self.SetAutoLayout(True)
        gs.Fit(self)
        self._gs=gs
    def OnMakeDirty(self, evt):
        self.OnDirtyUI(evt)
    def Set(self, data):
        self.ignore_dirty=True
        if data is None:
            for n in self._fields:
                if n[self._class_index]==StaticText or n[self._class_index]==DeliveryStatus or \
                   n[self._class_index]==TimeStamp:
                    w=n[self._w_index]
                    w.SetValue('')
        else:
            _bad_fields=self._not_used_fields.get(data.folder, ())
            for i,n in enumerate(self._fields):
                w=n[self._w_index]
                if n[self._dict_key_index] in _bad_fields:
                    self._gs.Show(i*2, False)
                    self._gs.Show(i*2+1, False)
                else:
                    self._gs.Show(i*2, True)
                    self._gs.Show(i*2+1, True)
                    w.SetValue(getattr(data, n[self._dict_key_index]))
        self._gs.Layout()
        self.ignore_dirty=self.dirty=False
    def Clear(self):
        self.Set(None)
class SMSWidget(wx.Panel, widgets.BitPimWidget):
    _data_key='sms'
    _canned_data_key='canned_msg'
    msg_type_list=(sms.SMSEntry.Folder_Saved, sms.SMSEntry.Folder_Sent, sms.SMSEntry.Folder_Inbox)
    def __init__(self, mainwindow, parent):
        super(SMSWidget, self).__init__(parent, -1)
        self._main_window=mainwindow
        self._data={}
        self._parent=parent
        self.sms_tree_nodes={}
        vbs=wx.BoxSizer(wx.VERTICAL)
        hbs=wx.BoxSizer(wx.HORIZONTAL)
        self.read_only=False
        self.historical_date=None
        static_bs=wx.StaticBoxSizer(wx.StaticBox(self, -1, 'Historical Data Status:'),
                                    wx.VERTICAL)
        self.historical_data_label=wx.StaticText(self, -1, 'Current Data')
        static_bs.Add(self.historical_data_label, 1, wx.EXPAND|wx.ALL, 5)
        hbs.Add(static_bs, 1, wx.EXPAND|wx.ALL, 5)
        vbs.Add(hbs, 0, wx.EXPAND|wx.ALL, 5)
        static_bs1=wx.StaticBoxSizer(wx.StaticBox(self, -1, 'Canned Messages:'),
                                    wx.HORIZONTAL)
        self.canned_list=gizmos.EditableListBox(self, -1, 'User Defined Canned Messages:')
        static_bs1.Add(self.canned_list, 1, wx.EXPAND|wx.ALL, 5)
        vbs1=wx.BoxSizer(wx.VERTICAL)
        vbs1.Add(wx.StaticText(self, -1, '  Built-in Canned Messages:'), 0, wx.ALL, 0)
        self.builtin_canned_list=wx.ListBox(self, -1)
        vbs1.Add(self.builtin_canned_list, 1, wx.EXPAND|wx.ALL, 5)
        static_bs1.Add(vbs1, 1, wx.EXPAND|wx.ALL, 5)
        vbs.Add(static_bs1, 1, wx.EXPAND|wx.ALL, 5)
        self.save_btn=wx.Button(self, wx.NewId(), 'Save')
        vbs.Add(self.save_btn, 0, wx.ALIGN_CENTRE|wx.ALL, 5)
        self.list_widget=SMSList(self._main_window, self._parent, self)
        wx.EVT_BUTTON(self, self.save_btn.GetId(), self.OnSaveCannedMsg)
        self.SetSizer(vbs)
        self.SetAutoLayout(True)
        vbs.Fit(self)
    def OnInit(self):
        for stat in self.msg_type_list:
            self.sms_tree_nodes[stat]=self.AddSubPage(self.list_widget, stat, self._tree.message)
    def GetRightClickMenuItems(self, node):
        result=[]
        result.append((widgets.BitPimWidget.MENU_NORMAL, guihelper.ID_EXPORT_SMS, "Export SMS ...", "Export the SMS"))
        result.append((widgets.BitPimWidget.MENU_NORMAL, guihelper.ID_DATAHISTORICAL, "Historical Data ...", "Display Historical Data"))
        return result
    def _populate(self):
        self.list_widget.populate()
        self.canned_list.SetStrings(
            self._canned_data.user_list)
        self.builtin_canned_list.Set(self._canned_data.builtin_list)
    def OnSaveCannedMsg(self, _):
        if self.read_only:
            wx.MessageBox('You are viewing historical data which cannot be changed or saved',
                          'Cannot Save SMS Data',
                          style=wx.OK|wx.ICON_ERROR)
            return
        self._canned_data.user_list=self.canned_list.GetStrings()
        self._save_to_db(canned_msg_dict=self._canned_data)
    def GetDeleteInfo(self):
        return guihelper.ART_DEL_SMS, "Delete SMS"
    def GetAddInfo(self):
        return guihelper.ART_ADD_SMS, "Add SMS"
    def getdata(self,dict,want=None):
        dict[self._data_key]=copy.deepcopy(self._data, {})
        dict[self._canned_data_key]=self._canned_data.get().get(
            self._canned_data_key, {})
    def get_selected_data(self):
        res={}
        for sel_idx in self.list_widget._item_list.GetSelections():
            k=self.list_widget._item_list.GetItemData(sel_idx)
            if k:
                res[k]=self._data[k]
        return res
    def get_data(self):
        return self._data
    def populate(self, dict, force=False):
        if self.read_only and not force:
            return
        if not self.read_only:
            self._canned_data=sms.CannedMsgEntry()
            self._canned_data.set({ self._canned_data_key: dict.get(self._canned_data_key, [])})
        self._data=dict.get(self._data_key, {})
        self._populate()
    def _save_to_db(self, sms_dict=None, canned_msg_dict=None):
        if self.read_only:
            return
        if sms_dict is not None:
            db_rr={}
            for k, e in sms_dict.items():
                db_rr[k]=sms.SMSDataObject(e)
            database.ensurerecordtype(db_rr, sms.smsobjectfactory)
            self._main_window.database.savemajordict(self._data_key, db_rr)
        if canned_msg_dict is not None:
            db_rr={}
            db_rr[self._canned_data_key]=sms.CannedMsgDataObject(
                canned_msg_dict)
            database.ensurerecordtype(db_rr, sms.cannedmsgobjectfactory)
            self._main_window.database.savemajordict(self._canned_data_key,
                                                      db_rr)
    def populatefs(self, dict):
        if self.read_only:
            wx.MessageBox('You are viewing historical data which cannot be changed or saved',
                          'Cannot Save SMS Data',
                          style=wx.OK|wx.ICON_ERROR)
            return
        canned_msg=sms.CannedMsgEntry()
        canned_msg.set({ self._canned_data_key: dict.get(self._canned_data_key, [])})
        self._save_to_db(sms_dict=dict.get(self._data_key, []),
                          canned_msg_dict=canned_msg)
        return dict
    def getfromfs(self, result, timestamp=None):
        sms_dict=self._main_window.database.\
                   getmajordictvalues(self._data_key, sms.smsobjectfactory,
                                      at_time=timestamp)
        r={}
        for k,e in sms_dict.items():
            ce=sms.SMSEntry()
            ce.set_db_dict(e)
            r[ce.id]=ce
        result.update({ self._data_key: r })
        canned_msg_dict=self._main_window.database.\
                         getmajordictvalues(self._canned_data_key,
                                            sms.cannedmsgobjectfactory)
        for k,e in canned_msg_dict.items():
            ce=sms.CannedMsgEntry()
            ce.set_db_dict(e)
            result.update(ce.get())
        return result
    def merge(self, dict):
        if self.read_only:
            wx.MessageBox('You are viewing historical data which cannot be changed or saved',
                          'Cannot Save SMS Data',
                          style=wx.OK|wx.ICON_ERROR)
            return
        existing_id=[e.msg_id for k,e in self._data.items()]
        d=dict.get(self._data_key, {})
        for k,e in d.items():
            if e.msg_id not in existing_id:
                self._data[e.id]=e
        self._canned_data=sms.CannedMsgEntry()
        self._canned_data.set({ self._canned_data_key: dict.get(self._canned_data_key, []) } )
        self._populate()
        self._save_to_db(sms_dict=self._data,
                         canned_msg_dict=self._canned_data)
    def HasHistoricalData(self):
        return True
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
            self.list_widget.historical_data_label.SetLabel(msg_str)
            self._main_window.OnBusyEnd()
        dlg.Destroy()
class SMSList(wx.Panel, widgets.BitPimWidget):
    _by_type=0
    _by_date=1
    _by_number=2
    def __init__(self, mainwindow, parent, stats):
        super(SMSList, self).__init__(parent, -1)
        self._main_window=mainwindow
        self._stats=stats
        self.nodes={}
        self.nodes_keys={}
        self._display_filter="All"
        self._name_map={}
        self._data_map={}
        vbs=wx.BoxSizer(wx.VERTICAL)
        hbs=wx.BoxSizer(wx.HORIZONTAL)
        static_bs=wx.StaticBoxSizer(wx.StaticBox(self, -1, 'Historical Data Status:'),
                                    wx.VERTICAL)
        self.historical_data_label=wx.StaticText(self, -1, 'Current Data')
        static_bs.Add(self.historical_data_label, 1, wx.EXPAND|wx.ALL, 5)
        hbs.Add(static_bs, 1, wx.EXPAND|wx.ALL, 5)
        vbs.Add(hbs, 0, wx.EXPAND|wx.ALL, 5)
        hbmessage=wx.BoxSizer(wx.HORIZONTAL)
        column_info=[]
        column_info.append(("From/To", 105))
        column_info.append(("Date", 120))
        column_info.append(("Subject", 180))
        self._item_list=guiwidgets.BitPimListCtrl(self, column_info)
        self._item_list.ResetView(self.nodes, self.nodes_keys)
        vbs0=wx.BoxSizer(wx.VERTICAL)
        vbs0.Add(self._item_list, 1, wx.EXPAND|wx.ALL, 5)
        vbs0.Add(wx.StaticText(self, -1, '  Note: Click column headings to sort data'), 0, wx.ALIGN_CENTRE|wx.BOTTOM, 10)
        hbmessage.Add(vbs0, 1, wx.EXPAND|wx.ALL, 5)
        vbs1=wx.BoxSizer(wx.VERTICAL)
        self._item_info=SMSInfo(self)
        vbs1.Add(self._item_info, 0, wx.EXPAND|wx.ALL, 5)
        self._item_text=pb_editor.MemoEditor(self, -1)
        vbs1.Add(self._item_text, 1, wx.EXPAND|wx.ALL, 5)
        hbmessage.Add(vbs1, 0, wx.EXPAND|wx.ALL, 5)
        hbmessage.SetItemMinSize(1, (350, 20))
        vbs.Add(hbmessage, 1, wx.EXPAND|wx.ALL, 5)
        wx.EVT_LIST_ITEM_SELECTED(self, self._item_list.GetId(), self._OnSelChanged)
        pubsub.subscribe(self._OnPBLookup, pubsub.RESPONSE_PB_LOOKUP)
        self.today_data=None
        today.bind_notification_event(self.OnTodaySelection,
                                      today.Today_Group_IncomingSMS)
        self.SetSizer(vbs)
        self.SetAutoLayout(True)
        vbs.Fit(self)
    def OnSelected(self, node):
        for stat in self._stats.msg_type_list:
            if self._stats.sms_tree_nodes[stat]==node:
                if self._display_filter!=stat:
                    self._display_filter=stat
                    if self._item_list.GetItemCount():
                        item=self._item_list.GetTopItem()
                        while item!=-1:
                            self._item_list.Select(item, 0)
                            item=self._item_list.GetNextItem(item)
                    self._item_info.Clear()
                    self._item_text.Set(None)
                    self.populate()
                self._on_today_selection()
                return
    def GetRightClickMenuItems(self, node):
        result=[]
        result.append((widgets.BitPimWidget.MENU_NORMAL, guihelper.ID_EDITSELECTALL, "Select All", "Select All Items"))
        result.append((widgets.BitPimWidget.MENU_NORMAL, guihelper.ID_EDITDELETEENTRY, "Delete Selected", "Delete Selected Items"))
        result.append((widgets.BitPimWidget.MENU_SPACER, 0, "", ""))
        result.append((widgets.BitPimWidget.MENU_NORMAL, guihelper.ID_EXPORT_SMS, "Export SMS ...", "Export the SMS"))
        result.append((widgets.BitPimWidget.MENU_NORMAL, guihelper.ID_DATAHISTORICAL, "Historical Data ...", "Display Historical Data"))
        return result
    def CanSelectAll(self):
        if self._item_list.GetItemCount():
            return True
        return False
    def _OnPBLookup(self, msg):
        d=msg.data
        k=d.get('item', None)
        name=d.get('name', None)
        if k is None:
            return
        self._name_map[k]=name
    def publish_today_data(self):
        keys=[(x.datetime,k) for k,x in self._stats._data.items()]
        keys.sort()
        keys.reverse()
        today_event=today.TodaySMSEvent()
        for _,k in keys:
            if self._stats._data[k].folder==sms.SMSEntry.Folder_Inbox:
                today_event.append(self._stats._data[k].text,
                                   { 'id': k } ) 
        today_event.broadcast()
    def OnTodaySelection(self, evt):
        inbox_node=self._stats.sms_tree_nodes[sms.SMSEntry.Folder_Inbox]
        self.today_data=evt.data
        self.ActivateSelf(inbox_node)
    def _on_today_selection(self):
        if self.today_data and self._item_list.GetItemCount():
            item=self._item_list.GetTopItem()
            while item>=0:
                if self.today_data['id']==self._item_list.GetItemData(item):
                    self._item_list.Select(item, 1)
                    self._item_list.EnsureVisible(item)
                else:
                    self._item_list.Select(item, 0)
                item=self._item_list.GetNextItem(item)
        self.today_data=None
    def OnSelectAll(self, _):
        item=self._item_list.GetTopItem()
        while item!=-1:
            self._item_list.Select(item)
            item=self._item_list.GetNextItem(item)
    def _OnSelChanged(self, evt):
        item=evt.GetIndex()
        k=self._item_list.GetItemData(item)
        if k is None:
            self._item_info.Clear()
            self._item_text.Set(None)
            return
        entry=self._stats._data.get(k, None)
        if entry is None:
            return
        e=copy.deepcopy(entry)
        s=self._name_map.get(e._from, None)
        if s is None:
            e._from=phonenumber.format(e._from)
        else:
            e._from=s
        s=self._name_map.get(e._to, None)
        if s is None:
            e._to=phonenumber.format(e._to)
        else:
            e._to=s
        s=self._name_map.get(e.callback, None)
        if s is None:
            e.callback=phonenumber.format(e.callback)
        else:
            e.callback=s
        self._item_info.Set(e)
        self._item_text.Set({'memo': e.text})
    def HasHistoricalData(self):
        return self._stats.HasHistoricalData()
    def OnHistoricalData(self):
        return self._stats.OnHistoricalData()
    def populate(self):
        self.nodes={}
        self.nodes_keys={}
        index=0
        for k,e in self._stats._data.items():
            if len(e._from) and not self._name_map.has_key(e._from):
                pubsub.publish(pubsub.REQUEST_PB_LOOKUP,
                               { 'item': e._from } )
            if len(e._to) and not self._name_map.has_key(e._to):
                pubsub.publish(pubsub.REQUEST_PB_LOOKUP,
                               { 'item': e._to } )
            if len(e.callback) and not self._name_map.has_key(e.callback):
                pubsub.publish(pubsub.REQUEST_PB_LOOKUP,
                               { 'item': e.callback } )
            if e.folder==self._display_filter:
                col=self._item_list.GetColumn(0)
                col.SetMask(wx.LIST_MASK_TEXT)
                if e.folder==sms.SMSEntry.Folder_Inbox:
                    col.SetText("From")
                    name=e._from
                else:
                    col.SetText("To")
                    name=e._to
                self._item_list.SetColumn(0, col)
                if name!=None and name!="":
                    temp=self._name_map.get(name, None)
                    if temp !=None:
                        name=temp
                self.nodes[index]=(name, e.get_date_time_str(), e.subject)
                self.nodes_keys[index]=k
                self._data_map[k]=index
                index+=1
        self._item_list.ResetView(self.nodes, self.nodes_keys)
        self.publish_today_data()
    def CanDelete(self):
        if self._stats.read_only:
            return False
        sels_idx=self._item_list.GetFirstSelected()
        if sels_idx==-1:
            return False
        return True
    def GetDeleteInfo(self):
        return guihelper.ART_DEL_SMS, "Delete Message"
    def OnDelete(self, _):
        if self._stats.read_only:
            return
        sels_idx=self._item_list.GetSelections()
        if len(sels_idx):
            for i,item in sels_idx.items():
                del self._stats._data[self._item_list.GetItemData(item)]
                self._item_list.Select(item, 0)
            self.populate()
            self._stats._save_to_db(self._stats._data)
