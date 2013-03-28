import wx
import fixedscrolledpanel
import pubsub
import bphtml
import database
import nameparser
import wallpaper
import guihelper
import field_color
"""The dialog for editing a phonebook entry"""
_ringtone_list=None
_wallpaper_list=None
class NavToolBar(wx.ToolBar):
    _id_up=wx.NewId()
    _id_down=wx.NewId()
    _id_del=wx.NewId()
    def __init__(self, parent, horizontal=True):
        self._parent=parent
        self._grandpa=parent.GetParent()
        _style=wx.TB_FLAT
        if horizontal:
            _style|=wx.TB_HORIZONTAL
        else:
            _style|=wx.TB_VERTICAL
        super(NavToolBar, self).__init__(parent, -1, style=_style)
        self.SetToolBitmapSize(wx.Size(16, 16))
        sz=self.GetToolBitmapSize()
        self.AddLabelTool(NavToolBar._id_up, "Up", wx.ArtProvider.GetBitmap(guihelper.ART_ARROW_UP, wx.ART_TOOLBAR, sz), shortHelp="Move field up")
        self.AddLabelTool(NavToolBar._id_down, "Down", wx.ArtProvider.GetBitmap(guihelper.ART_ARROW_DOWN, wx.ART_TOOLBAR, sz), shortHelp="Move field down")
        self.AddLabelTool(NavToolBar._id_del, "Delete", wx.ArtProvider.GetBitmap(guihelper.ART_DEL_FIELD, wx.ART_TOOLBAR, sz), shortHelp="Delete field")
        if hasattr(self._grandpa, 'MoveField'):
            wx.EVT_TOOL(self, NavToolBar._id_up, self.OnMoveUp)
            wx.EVT_TOOL(self, NavToolBar._id_down, self.OnMoveDown)
        if hasattr(self._grandpa, 'DeleteField'):
            wx.EVT_TOOL(self, NavToolBar._id_del, self.OnDelete)
        self.Realize()
    def OnMoveUp(self, _):
        self._grandpa.MoveField(self._parent, -1)
    def OnMoveDown(self, _):
        self._grandpa.MoveField(self._parent, +1)
    def OnDelete(self, _):
        self._grandpa.DeleteField(self._parent)
myEVT_DIRTY_UI=wx.NewEventType()
EVT_DIRTY_UI=wx.PyEventBinder(myEVT_DIRTY_UI, 1)
class DirtyUIBase(wx.Panel):
    """ Base class to add the capability to generate a DirtyUI event"""
    def __init__(self, parent):
        wx.Panel.__init__(self, parent, -1)
        self.dirty=self.ignore_dirty=False
    def OnDirtyUI(self, evt):
        if self.dirty or self.ignore_dirty:
            return
        self.dirty=True
        self.GetEventHandler().ProcessEvent(\
            wx.PyCommandEvent(myEVT_DIRTY_UI, self.GetId()))
    def Clean(self):
        self.dirty=False
        self.ignore_dirty=False
    def Ignore(self, ignore=True):
        self.ignore_dirty=ignore
class RingtoneEditor(DirtyUIBase):
    "Edit a ringtone"
    unnamed="Select:"
    unknownselprefix=": "
    choices=["call", "message", "calendar"]
    ID_LIST=wx.NewId()
    _bordersize=3
    def __init__(self, parent, _, has_type=True, navtoolbar=False):
        DirtyUIBase.__init__(self, parent)
        _box=field_color.build_color_field(self, wx.StaticBox,
                                           (self, -1, "Ringtone"), 'ringtone')
        hs=wx.StaticBoxSizer(_box, wx.HORIZONTAL)
        self.static_box=_box
        vs=wx.BoxSizer(wx.VERTICAL)
        self.preview=bphtml.HTMLWindow(self, -1)
        self.preview.SetBorders(self._bordersize)
        vs.Add(self.preview, 1, wx.EXPAND|wx.ALL, 5)
        self.type=wx.ComboBox(self, -1, "call", choices=self.choices, style=wx.CB_READONLY)
        self.type.SetSelection(0)
        vs.Add(self.type, 0, wx.ALIGN_CENTRE|wx.ALL, 5)
        if not has_type:
            vs.Hide(1)
        hs.Add(vs, 1, wx.EXPAND|wx.ALL, 5)
        self.ringtone=wx.ListBox(self, self.ID_LIST, choices=[self.unnamed], size=(-1,200))
        hs.Add(self.ringtone, 1, wx.EXPAND|wx.ALL, 5)
        if navtoolbar:
            hs.Add(NavToolBar(self, False), 0, wx.EXPAND|wx.BOTTOM, 5)
        self.SetSizer(hs)
        hs.Fit(self)
        pubsub.subscribe(self.OnRingtoneUpdates, pubsub.ALL_RINGTONES)
        wx.CallAfter(pubsub.publish, pubsub.REQUEST_RINGTONES) # make the call once we are onscreen
        wx.EVT_LISTBOX(self, self.ID_LIST, self.OnLBClicked)
        wx.EVT_LISTBOX_DCLICK(self, self.ID_LIST, self.OnLBClicked)
    def __del__(self):
        pubsub.unsubscribe(self.OnRingtoneUpdates)
        super(RingtoneEditor, self).__del__()
    def OnRingtoneUpdates(self, msg):
        "Receives pubsub message with ringtone list"
        tones=msg.data[:]
        cur=self.Get()
        self.ringtone.Clear()
        self.ringtone.Append(self.unnamed)
        for p in tones:
            self.ringtone.Append(p)
        self.Set(cur)
    def OnLBClicked(self, evt=None):
        self.OnDirtyUI(evt)
        self._updaterequested=False
        v=self.Get().get('ringtone', None)
        self.SetPreview(v)
    def SetPreview(self, name):
        if name is None:
            self.preview.SetPage('')
        else:
            pass
            self.preview.SetPage('<img src="bpimage:ringer.png;width=24;height=24"><br>At some point there will be info about the ringtone as well as the ability to play it here')
            pass
    def Set(self, data):
        self.Ignore(True)
        if data is None:
            wp=self.unnamed
            type='call'
        else:
            wp=data.get("ringtone", self.unnamed)
            type=data.get("use", "call")
        self.SetPreview(wp)
        if type=='calendar':
            self.type.SetSelection(2)
        elif type=="message":
            self.type.SetSelection(1)
        else:
            self.type.SetSelection(0)
        if len(wp)==0:
            self.ringtone.SetSelection(0)
            self.Clean()
            return
        try:
            self.ringtone.SetStringSelection(wp)
            self.Clean()
            return
        except:
            pass
        try:
            self.ringtone.SetStringSelection(self.unknownselprefix+wp)
            self.Clean()
            return
        except:
            pass
        self.ringtone.InsertItems([self.unknownselprefix+wp], 1)
        self.ringtone.SetStringSelection(self.unknownselprefix+wp)
        self.Clean()
    def Get(self):
        self.Clean()
        res={}
        rt=self.ringtone.GetStringSelection()
        if rt==self.unnamed:
            return res
        if rt.startswith(self.unknownselprefix):
            rt=rt[len(self.unknownselprefix):]
        if len(rt):
            res['ringtone']=rt
            res['use']=self.type.GetStringSelection()
        return res
class WallpaperEditor(DirtyUIBase):
    unnamed="Select:"
    unknownselprefix=": "
    choices=["call", "message", "calendar"]
    ID_LIST=wx.NewId()
    _bordersize=3 # border inside HTML widget
    def __init__(self, parent, _, has_type=True, navtoolbar=False):
        DirtyUIBase.__init__(self, parent)
        _box=field_color.build_color_field(self, wx.StaticBox,
                                           (self, -1, "Wallpaper"),
                                           'wallpaper')
        hs=wx.StaticBoxSizer(_box, wx.HORIZONTAL)
        self.static_box=_box
        vs=wx.BoxSizer(wx.VERTICAL)
        self.preview=wallpaper.WallpaperPreview(self)
        self.type=wx.ComboBox(self, -1, "call", choices=self.choices, style=wx.CB_READONLY)
        self.type.SetSelection(0)
        vs.Add(self.preview, 1, wx.EXPAND|wx.ALL, 5)
        vs.Add(self.type, 0, wx.ALIGN_CENTRE|wx.ALL, 5)
        if not has_type:
            vs.Hide(1)
        hs.Add(vs, 1, wx.EXPAND|wx.ALL, 5)
        self.wallpaper=wx.ListBox(self, self.ID_LIST, choices=[self.unnamed], size=(-1,200), style=wx.LB_SINGLE)
        hs.Add(self.wallpaper, 1, wx.EXPAND|wx.ALL, 5)
        if navtoolbar:
            hs.Add(NavToolBar(self, False), 0, wx.EXPAND|wx.BOTTOM, 5)
        self.SetSizer(hs)
        hs.Fit(self)
        pubsub.subscribe(self.OnWallpaperUpdates, pubsub.ALL_WALLPAPERS)
        wx.CallAfter(pubsub.publish, pubsub.REQUEST_WALLPAPERS) # make the call once we are onscreen
        wx.EVT_LISTBOX(self, self.ID_LIST, self.OnLBClicked)
        wx.EVT_LISTBOX_DCLICK(self, self.ID_LIST, self.OnLBClicked)
    def __del__(self):
        pubsub.unsubscribe(self.OnWallpaperUpdates)
        super(WallpaperEditor, self).__del__()
    def OnWallpaperUpdates(self, msg):
        "Receives pubsub message with wallpaper list"
        papers=msg.data[:]
        cur=self.Get()
        self.wallpaper.Clear()
        self.wallpaper.Append(self.unnamed)
        for p in papers:
            self.wallpaper.Append(p)
        self.Set(cur)
    def OnLBClicked(self, evt=None):
        self.OnDirtyUI(evt)
        v=self.Get().get('wallpaper', None)
        self.SetPreview(v)
    def SetPreview(self, name):
        if name is None or name is self.unnamed:
            self.preview.SetImage(None)
        else:
            self.preview.SetImage(name)        
    def Set(self, data):
        self.Ignore()
        if data is None:
            wp=self.unnamed
            type='call'
        else:
            wp=data.get("wallpaper", self.unnamed)
            type=data.get("use", "call")
        self.SetPreview(wp)
        if type=="message":
            self.type.SetSelection(1)
        elif type=='calendar':
            self.type.SetSelection(2)
        else:
            self.type.SetSelection(0)
        if len(wp)==0:
            self.wallpaper.SetSelection(0)
            self.Clean()
            return
        try:
            self.wallpaper.SetStringSelection(wp)
            self.Clean()
            return
        except:
            pass
        try:
            self.wallpaper.SetStringSelection(self.unknownselprefix+wp)
            self.Clean()
            return
        except:
            pass
        self.wallpaper.InsertItems([self.unknownselprefix+wp], 1)
        self.wallpaper.SetStringSelection(self.unknownselprefix+wp)
        self.Clean()
    def Get(self):
        self.Clean()
        res={}
        wp=self.wallpaper.GetStringSelection()
        if wp==self.unnamed:
            return res
        if wp.startswith(self.unknownselprefix):
            wp=wp[len(self.unknownselprefix):]
        if len(wp):
            res['wallpaper']=wp
            res['use']=self.type.GetStringSelection()
        return res
class CategoryManager(wx.Dialog):
    def __init__(self, parent, title="Manage Categories"):
        wx.Dialog.__init__(self, parent, -1, title, style=wx.CAPTION|wx.SYSTEM_MENU|wx.DEFAULT_DIALOG_STYLE|
                           wx.RESIZE_BORDER)
        vs=wx.BoxSizer(wx.VERTICAL)
        hs=wx.BoxSizer(wx.HORIZONTAL)
        self.delbut=wx.Button(self, wx.NewId(), "Delete")
        self.addbut=wx.Button(self, wx.NewId(), "Add")
        self.add=wx.TextCtrl(self, -1)
        hs.Add(self.delbut,0, wx.EXPAND|wx.ALL, 5)
        hs.Add(self.addbut,0, wx.EXPAND|wx.ALL, 5)
        hs.Add(self.add, 1, wx.EXPAND|wx.ALL, 5)
        vs.Add(hs, 0, wx.EXPAND|wx.ALL, 5)
        self.thelistb=wx.ListBox(self, -1, size=(100, 250), style=wx.LB_SORT)
        self.addlistb=wx.ListBox(self, -1, style=wx.LB_SORT)
        self.dellistb=wx.ListBox(self, -1, style=wx.LB_SORT)
        hs=wx.BoxSizer(wx.HORIZONTAL)
        vs2=wx.BoxSizer(wx.VERTICAL)
        vs2.Add(wx.StaticText(self, -1, "  List"), 0, wx.ALL, 2)
        vs2.Add(self.thelistb, 1, wx.ALL|wx.EXPAND, 5)
        hs.Add(vs2, 1, wx.ALL|wx.EXPAND, 5)
        vs2=wx.BoxSizer(wx.VERTICAL)
        vs2.Add(wx.StaticText(self, -1, "  Added"), 0, wx.ALL, 2)
        vs2.Add(self.addlistb, 1, wx.ALL|wx.EXPAND, 5)
        hs.Add(vs2, 1, wx.ALL|wx.EXPAND, 5)
        vs2=wx.BoxSizer(wx.VERTICAL)
        vs2.Add(wx.StaticText(self, -1, "  Deleted"), 0, wx.ALL, 2)
        vs2.Add(self.dellistb, 1, wx.ALL|wx.EXPAND, 5)
        hs.Add(vs2, 1, wx.ALL|wx.EXPAND, 5)
        vs.Add(hs, 1, wx.EXPAND|wx.ALL, 5)
        vs.Add(wx.StaticLine(self, -1, style=wx.LI_HORIZONTAL), 0, wx.EXPAND|wx.ALL, 5)
        vs.Add(self.CreateButtonSizer(wx.OK|wx.CANCEL|wx.HELP), 0, wx.ALIGN_CENTRE|wx.ALL, 5)
        self.SetSizer(vs)
        vs.Fit(self)
        self.curlist=None
        self.dellist=[]
        self.addlist=[]
        pubsub.subscribe(self.OnUpdateCategories, pubsub.ALL_CATEGORIES)
        pubsub.publish(pubsub.REQUEST_CATEGORIES)
        wx.EVT_BUTTON(self, self.addbut.GetId(), self.OnAdd)
        wx.EVT_BUTTON(self, self.delbut.GetId(), self.OnDelete)
        wx.EVT_BUTTON(self, wx.ID_OK, self.OnOk)
        wx.EVT_BUTTON(self, wx.ID_CANCEL, self.OnCancel)
    def __del__(self):
        pubsub.unsubscribe(self.OnUpdateCategories)
        super(CategoryManager, self).__del__()
    def OnUpdateCategories(self, msg):
        cats=msg.data[:]
        if self.curlist is None:
            self.curlist=cats
        for i in cats:
            if i not in self.curlist and i not in self.dellist:
                self.curlist.append(i)
                self.addlist.append(i)
        self.curlist.sort()
        self.addlist.sort()
        self.UpdateLBs()
    def UpdateLBs(self):
        for lb,l in (self.thelistb, self.curlist), (self.addlistb, self.addlist), (self.dellistb, self.dellist):
            lb.Clear()
            for i in l:
                lb.Append(i)
    def OnOk(self, _):
        pubsub.publish(pubsub.SET_CATEGORIES, self.curlist)
        self.Show(False)
        self.Destroy()
    def OnCancel(self, _):
        self.Show(False)
        self.Destroy()
    def OnAdd(self, _):
        v=self.add.GetValue()
        self.add.SetValue("")
        self.add.SetFocus()
        if len(v)==0:
            return
        if v not in self.curlist:
            self.curlist.append(v)
            self.curlist.sort()
        if v not in self.addlist:
            self.addlist.append(v)
            self.addlist.sort()
        if v in self.dellist:
            i=self.dellist.index(v)
            del self.dellist[i]
        self.UpdateLBs()
    def OnDelete(self,_):
        try:
            v=self.thelistb.GetStringSelection()
            if v is None or len(v)==0: return
        except:
            return
        i=self.curlist.index(v)
        del self.curlist[i]
        if v in self.addlist:
            i=self.addlist.index(v)
            del self.addlist[i]
        self.dellist.append(v)
        self.dellist.sort()
        self.UpdateLBs()
class CategoryEditor(DirtyUIBase):
    unnamed="Select:"
    def __init__(self, parent, pos, navtoolbar=False):
        DirtyUIBase.__init__(self, parent)
        _box=field_color.build_color_field(self, wx.StaticBox,
                                           (self, -1, "Category"),
                                           'category')
        hs=wx.StaticBoxSizer(_box, wx.HORIZONTAL)
        self.categories=[self.unnamed]
        self.category=wx.ListBox(self, -1, choices=self.categories)
        pubsub.subscribe(self.OnUpdateCategories, pubsub.ALL_CATEGORIES)
        pubsub.publish(pubsub.REQUEST_CATEGORIES)
        hs.Add(self.category, 1, wx.EXPAND|wx.ALL, 5)
        if pos==0:
            self.but=wx.Button(self, wx.NewId(), "Manage Categories")
            hs.Add(self.but, 2, wx.ALIGN_CENTRE|wx.ALL, 5)
            wx.EVT_BUTTON(self, self.but.GetId(), self.OnManageCategories)
        else:
            hs.Add(wx.StaticText(self, -1, ""), 2, wx.ALIGN_CENTRE|wx.ALL, 5)
        wx.EVT_LISTBOX(self, self.category.GetId(), self.OnDirtyUI)
        wx.EVT_LISTBOX_DCLICK(self, self.category.GetId(), self.OnDirtyUI)
        if navtoolbar:
            hs.Add(NavToolBar(self, False), 0, wx.EXPAND|wx.BOTTOM, 5)
        self.SetSizer(hs)
        hs.Fit(self)
    def __del__(self):
        pubsub.unsubscribe(self.OnUpdateCategories)
        super(CategoryEditor, self).__del__()
    def OnManageCategories(self, _):
        dlg=CategoryManager(self)
        dlg.ShowModal()
    def OnUpdateCategories(self, msg):
        cats=msg.data[:]
        cats=[self.unnamed]+cats
        if self.categories!=cats:
            self.categories=cats
            sel=self.category.GetStringSelection()
            self.category.Clear()
            for i in cats:
                self.category.Append(i)
            try:
                self.category.SetStringSelection(sel)
            except:
                self.category.SetStringSelection(self.unnamed)
    def Get(self):
        self.Clean()
        v=self.category.GetStringSelection()
        if len(v) and v!=self.unnamed:
            return {'category': v}
        return {}
    def Set(self, data):
        self.Ignore()
        if data is None:
            v=self.unnamed
        else:
            v=data.get("category", self.unnamed)
        try:
            self.category.SetStringSelection(v)
        except:
            assert v!=self.unnamed
            self.category.SetStringSelection(self.unnamed)
        self.Clean()
class MemoEditor(DirtyUIBase):
    def __init__(self, parent, _, navtoolbar=False):
        DirtyUIBase.__init__(self, parent)
        _box=field_color.build_color_field(self, wx.StaticBox,
                                           (self, -1, "Memo"),
                                           'memo')
        vs=wx.StaticBoxSizer(_box, wx.HORIZONTAL)
        self.static_box=_box
        self.memo=wx.TextCtrl(self, -1, "", style=wx.TE_MULTILINE)
        vs.Add(self.memo, 1, wx.EXPAND|wx.ALL, 5)
        wx.EVT_TEXT(self, self.memo.GetId(), self.OnDirtyUI)
        if navtoolbar:
            vs.Add(NavToolBar(self, horizontal=False), 0, wx.EXPAND|wx.BOTTOM, 5)
        self.SetSizer(vs)
        vs.Fit(self)
    def Set(self, data):
        self.Ignore()
        if data is None:
            s=''
        else:
            s=data.get('memo', '')
        self.memo.SetValue(s)
        self.Clean()
    def Get(self):
        self.Clean()
        if len(self.memo.GetValue()):
            return {'memo': self.memo.GetValue()}
        return {}
class NumberEditor(DirtyUIBase):
    choices=[ ("None", "none"), ("Home", "home"), ("Office",
    "office"), ("Cell", "cell"), ("Fax", "fax"), ("Pager", "pager"),
    ("Data", "data"), ("Main", "main")]
    _None_Value='None'
    def __init__(self, parent, _, navtoolbar=False):
        DirtyUIBase.__init__(self, parent)
        _field_color_dict=field_color.build_field_info(self, 'number')
        hs=wx.StaticBoxSizer(field_color.build_color_field(self,
                                                           wx.StaticBox,
                                                           (self, -1, "Number details"),
                                                           'details',
                                                           _field_color_dict),
                             wx.VERTICAL)
        _hs_top=wx.BoxSizer(wx.HORIZONTAL)
        _txt=field_color.build_color_field(self, wx.StaticText,
                                           (self, -1, "Type"),
                                           'type', _field_color_dict)
        _hs_top.Add(_txt, 0, wx.ALIGN_CENTRE|wx.ALL, 5)
        self.type=wx.ComboBox(self, -1, "Cell", choices=[desc for desc,name in self.choices], style=wx.CB_READONLY)
        _hs_top.Add(self.type, 0, wx.EXPAND|wx.ALL, 5)
        _txt=field_color.build_color_field(self, wx.StaticText,
                                           (self, -1, "SpeedDial"),
                                           'speeddial', _field_color_dict)
        _hs_top.Add(_txt, 0, wx.ALIGN_CENTRE|wx.ALL, 5)
        self.speeddial=wx.TextCtrl(self, -1, "", size=(32,10))
        _hs_top.Add(self.speeddial, 0, wx.EXPAND|wx.ALL, 5)
        _txt=field_color.build_color_field(self, wx.StaticText,
                                           (self, -1, "Number"),
                                           'number', _field_color_dict)
        _hs_top.Add(_txt, 0, wx.ALIGN_CENTRE|wx.ALL, 5)
        self.number=wx.TextCtrl(self, -1, "")
        _hs_top.Add(self.number, 1, wx.EXPAND|wx.ALL, 5)
        if navtoolbar:
            _hs_top.Add(NavToolBar(self), 0, wx.EXPAND|wx.BOTTOM, 5)
        hs.Add(_hs_top, 0, wx.EXPAND|wx.ALL, 0)
        _hs_bot=wx.BoxSizer(wx.HORIZONTAL)
        _txt=field_color.build_color_field(self, wx.StaticText,
                                           (self, -1, "Ringtone"),
                                           'ringtone', _field_color_dict)
        _hs_bot.Add(_txt, 0, wx.ALIGN_CENTRE|wx.ALL, 5)
        self.ringtone=wx.ComboBox(self, -1)
        _hs_bot.Add(self.ringtone, 0, wx.EXPAND|wx.ALL, 5)
        _txt=field_color.build_color_field(self, wx.StaticText,
                                           (self, -1, "Wallpaper"),
                                           'wallpaper', _field_color_dict)
        _hs_bot.Add(_txt, 0, wx.ALIGN_CENTRE|wx.ALL, 5)
        self.wallpaper=wx.ComboBox(self, -1)
        _hs_bot.Add(self.wallpaper, 0, wx.EXPAND|wx.ALL, 5)
        hs.Add(_hs_bot, 0, wx.EXPAND|wx.ALL, 0)
        global _wallpaper_list, _ringtone_list
        pubsub.subscribe(self.OnWallpaperUpdates, pubsub.ALL_WALLPAPERS)
        if _wallpaper_list is None:
            pubsub.publish(pubsub.REQUEST_WALLPAPERS)
        else:
            self._populate_wallpaper()
        pubsub.subscribe(self.OnRingtoneUpdates, pubsub.ALL_RINGTONES)
        if _ringtone_list is None:
            pubsub.publish(pubsub.REQUEST_RINGTONES)
        else:
            self._populate_ringtone()
        wx.EVT_TEXT(self, self.type.GetId(), self.OnDirtyUI)
        wx.EVT_TEXT(self, self.speeddial.GetId(), self.OnDirtyUI)
        wx.EVT_TEXT(self, self.number.GetId(), self.OnDirtyUI)
        wx.EVT_TEXT(self, self.ringtone.GetId(), self.OnDirtyUI)
        wx.EVT_TEXT(self, self.wallpaper.GetId(), self.OnDirtyUI)
        self.SetSizer(hs)
        hs.Fit(self)
    def __del__(self):
        pubsub.unsubscribe(self.OnWallpaperUpdates)
        pubsub.unsubscribe(self.OnRingtoneUpdates)
        super(NumberEditor, self).__del__()
    def _populate_cb(self, cb_widget, data):
        cb_widget.Clear()
        cb_widget.Append(self._None_Value)
        for _entry in data:
            cb_widget.Append(_entry)
    def _set_cb_sel(self, cb_widget, str_sel):
        if str_sel:
            _sel=str_sel
        else:
            _sel=self._None_Value
        try:
            cb_widget.SetStringSelection(_sel)
        except:
            cb_widget.Append(sel)
        cb_widget.SetStringSelection(_sel)
    def _get_cb_sel(self, cb_widget):
        _sel=cb_widget.GetStringSelection()
        if not _sel or _sel==self._None_Value:
            return None
        return _sel
    def _populate_ringtone(self):
        """Populate the combo box with ringtone data"""
        self.Ignore()
        _str_sel=self.ringtone.GetStringSelection()
        global _ringtone_list
        self._populate_cb(self.ringtone, _ringtone_list)
        self._set_cb_sel(self.ringtone, _str_sel)
        self.Clean()
    def _populate_wallpaper(self):
        """Ppulate the combo box with wallpaper data"""
        self.Ignore()
        _str_sel=self.wallpaper.GetStringSelection()
        global _wallpaper_list
        self._populate_cb(self.wallpaper, _wallpaper_list)
        self._set_cb_sel(self.wallpaper, _str_sel)
        self.Clean()
    def OnWallpaperUpdates(self, msg):
        global _wallpaper_list
        _wallpaper_list=msg.data[:]
        self._populate_wallpaper()
    def OnRingtoneUpdates(self, msg):
        global _ringtone_list
        _ringtone_list=msg.data[:]
        self._populate_ringtone()
    def Set(self, data):
        self.Ignore()
        sd=data.get("speeddial", "")
        if isinstance(sd,int):
            sd=`sd`
        self.speeddial.SetValue(sd)
        self.number.SetValue(data.get("number", ""))
        self._set_cb_sel(self.ringtone, data.get('ringtone', None))
        self._set_cb_sel(self.wallpaper, data.get('wallpaper', None))
        v=data.get("type", "cell")
        for i in range(len(self.choices)):
            if self.choices[i][1]==v:
                self.type.SetSelection(i)
                self.Clean()
                return
        self.type.SetSelection(0)
        self.Clean()
    def Get(self):
        self.Clean()
        res={}
        if len(self.number.GetValue())==0:
            return res
        res['number']=self.number.GetValue()
        if len(self.speeddial.GetValue()):
            res['speeddial']=self.speeddial.GetValue()
            try:
                res['speeddial']=int(res['speeddial'])
            except:
                pass
        res['type']=self.choices[self.type.GetSelection()][1]
        _sel=self._get_cb_sel(self.ringtone)
        if _sel:
            res['ringtone']=_sel
        _sel=self._get_cb_sel(self.wallpaper)
        if _sel:
            res['wallpaper']=_sel
        return res
class EmailEditor(DirtyUIBase):
    ID_TYPE=wx.NewId()
    _None_Value='None'
    def __init__(self, parent, _, navtoolbar=False):
        super(EmailEditor, self).__init__(parent)
        _field_color_dict=field_color.build_field_info(self, 'email_details')
        _box=field_color.build_color_field(self, wx.StaticBox,
                                           (self, -1, 'Email Address'),
                                           'email')
        hs=wx.StaticBoxSizer(_box, wx.VERTICAL)
        _hs_top=wx.BoxSizer(wx.HORIZONTAL)
        self.type=wx.ComboBox(self, self.ID_TYPE, "", choices=["", "Home", "Business"], style=wx.CB_READONLY)
        _hs_top.Add(self.type, 0, wx.EXPAND|wx.ALL, 5)
        self.email=wx.TextCtrl(self, -1, "")
        _hs_top.Add(self.email, 1, wx.EXPAND|wx.ALL, 5)
        if navtoolbar:
            _hs_top.Add(NavToolBar(self), 0, wx.EXPAND|wx.BOTTOM, 5)
        hs.Add(_hs_top, 0, wx.EXPAND|wx.ALL, 0)
        _hs_bot=wx.BoxSizer(wx.HORIZONTAL)
        _txt=field_color.build_color_field(self, wx.StaticText,
                                           (self, -1, "SpeedDial"),
                                           'emailspeeddial', _field_color_dict)
        _hs_bot.Add(_txt, 0, wx.ALIGN_CENTRE|wx.ALL, 5)
        self.speeddial=wx.TextCtrl(self, -1, "", size=(32,10))
        _hs_bot.Add(self.speeddial, 0, wx.EXPAND|wx.ALL, 5)
        _txt=field_color.build_color_field(self, wx.StaticText,
                                           (self, -1, "Ringtone"),
                                           'emailringtone', _field_color_dict)
        _hs_bot.Add(_txt, 0, wx.ALIGN_CENTRE|wx.ALL, 5)
        self.ringtone=wx.ComboBox(self, -1)
        _hs_bot.Add(self.ringtone, 0, wx.EXPAND|wx.ALL, 5)
        _txt=field_color.build_color_field(self, wx.StaticText,
                                           (self, -1, "Wallpaper"),
                                           'emailwallpaper', _field_color_dict)
        _hs_bot.Add(_txt, 0, wx.ALIGN_CENTRE|wx.ALL, 5)
        self.wallpaper=wx.ComboBox(self, -1)
        _hs_bot.Add(self.wallpaper, 0, wx.EXPAND|wx.ALL, 5)
        hs.Add(_hs_bot, 0, wx.EXPAND|wx.ALL, 0)
        global _wallpaper_list, _ringtone_list
        pubsub.subscribe(self.OnWallpaperUpdates, pubsub.ALL_WALLPAPERS)
        if _wallpaper_list is None:
            pubsub.publish(pubsub.REQUEST_WALLPAPERS)
        else:
            self._populate_wallpaper()
        pubsub.subscribe(self.OnRingtoneUpdates, pubsub.ALL_RINGTONES)
        if _ringtone_list is None:
            pubsub.publish(pubsub.REQUEST_RINGTONES)
        else:
            self._populate_ringtone()
        wx.EVT_TEXT(self, self.type.GetId(), self.OnDirtyUI)
        wx.EVT_TEXT(self, self.email.GetId(), self.OnDirtyUI)
        wx.EVT_TEXT(self, self.speeddial.GetId(), self.OnDirtyUI)
        wx.EVT_TEXT(self, self.ringtone.GetId(), self.OnDirtyUI)
        wx.EVT_TEXT(self, self.wallpaper.GetId(), self.OnDirtyUI)
        self.SetSizer(hs)
        hs.Fit(self)
    def __del__(self):
        pubsub.unsubscribe(self.OnWallpaperUpdates)
        pubsub.unsubscribe(self.OnRingtoneUpdates)
        super(NumberEditor, self).__del__()
    def _populate_cb(self, cb_widget, data):
        cb_widget.Clear()
        cb_widget.Append(self._None_Value)
        for _entry in data:
            cb_widget.Append(_entry)
    def _set_cb_sel(self, cb_widget, str_sel):
        if str_sel:
            _sel=str_sel
        else:
            _sel=self._None_Value
        try:
            cb_widget.SetStringSelection(_sel)
        except:
            cb_widget.Append(sel)
        cb_widget.SetStringSelection(_sel)
    def _get_cb_sel(self, cb_widget):
        _sel=cb_widget.GetStringSelection()
        if not _sel or _sel==self._None_Value:
            return None
        return _sel
    def _populate_ringtone(self):
        """Populate the combo box with ringtone data"""
        self.Ignore()
        _str_sel=self.ringtone.GetStringSelection()
        global _ringtone_list
        self._populate_cb(self.ringtone, _ringtone_list)
        self._set_cb_sel(self.ringtone, _str_sel)
        self.Clean()
    def _populate_wallpaper(self):
        """Ppulate the combo box with wallpaper data"""
        self.Ignore()
        _str_sel=self.wallpaper.GetStringSelection()
        global _wallpaper_list
        self._populate_cb(self.wallpaper, _wallpaper_list)
        self._set_cb_sel(self.wallpaper, _str_sel)
        self.Clean()
    def OnWallpaperUpdates(self, msg):
        global _wallpaper_list
        _wallpaper_list=msg.data[:]
        self._populate_wallpaper()
    def OnRingtoneUpdates(self, msg):
        global _ringtone_list
        _ringtone_list=msg.data[:]
        self._populate_ringtone()
    def Set(self, data):
        self.Ignore()
        self.email.SetValue(data.get("email", ""))
        sd=data.get("speeddial", "")
        if isinstance(sd, int):
            sd=`sd`
        self.speeddial.SetValue(sd)
        self._set_cb_sel(self.ringtone, data.get('ringtone', None))
        self._set_cb_sel(self.wallpaper, data.get('wallpaper', None))
        v=data.get("type", "")
        if v=="home":
            self.type.SetSelection(1)
        elif v=="business":
            self.type.SetSelection(2)
        else:
            self.type.SetSelection(0)
        self.Clean()
    def Get(self):
        self.Clean()
        res={}
        if len(self.email.GetValue())==0:
            return res
        res['email']=self.email.GetValue()
        if len(self.speeddial.GetValue()):
            res['speeddial']=self.speeddial.GetValue()
            try:
                res['speeddial']=int(res['speeddial'])
            except:
                pass
        if self.type.GetSelection()==1:
            res['type']='home'
        elif self.type.GetSelection()==2:
            res['type']='business'
        _sel=self._get_cb_sel(self.ringtone)
        if _sel:
            res['ringtone']=_sel
        _sel=self._get_cb_sel(self.wallpaper)
        if _sel:
            res['wallpaper']=_sel
        return res
class URLEditor(DirtyUIBase):
    ID_TYPE=wx.NewId()
    def __init__(self, parent, _, navtoolbar=False):
        super(URLEditor, self).__init__(parent)
        _box=field_color.build_color_field(self, wx.StaticBox,
                                           (self, -1, "URL"), 'url')
        hs=wx.StaticBoxSizer(_box, wx.HORIZONTAL)
        self.type=wx.ComboBox(self, self.ID_TYPE, "", choices=["", "Home", "Business"], style=wx.CB_READONLY)
        hs.Add(self.type, 0, wx.EXPAND|wx.ALL, 5)
        self.url=wx.TextCtrl(self, -1, "")
        hs.Add(self.url, 1, wx.EXPAND|wx.ALL, 5)
        if navtoolbar:
            hs.Add(NavToolBar(self), 0, wx.EXPAND|wx.BOTTOM, 5)
        wx.EVT_TEXT(self, self.type.GetId(), self.OnDirtyUI)
        wx.EVT_TEXT(self, self.url.GetId(), self.OnDirtyUI)
        self.SetSizer(hs)
        hs.Fit(self)
    def Set(self, data):
        self.Ignore()
        self.url.SetValue(data.get("url", ""))
        v=data.get("type", "")
        if v=="home":
            self.type.SetSelection(1)
        elif v=="business":
            self.type.SetSelection(2)
        else:
            self.type.SetSelection(0)
        self.Clean()
    def Get(self):
        self.Clean()
        res={}
        if len(self.url.GetValue())==0:
            return res
        res['url']=self.url.GetValue()
        if self.type.GetSelection()==1:
            res['type']='home'
        elif self.type.GetSelection()==2:
            res['type']='business'
        return res
class AddressEditor(DirtyUIBase):
    ID_TYPE=wx.NewId()
    fieldinfos=("street", "Street"), ("street2", "Street2"), ("city", "City"), \
            ("state", "State"), ("postalcode", "Postal/Zipcode"), ("country", "Country/Region")
    def __init__(self, parent, _, navtoolbar=False):
        super(AddressEditor, self).__init__(parent)
        _fc_dict=field_color.build_field_info(self, 'address')
        _hs=wx.StaticBoxSizer(field_color.build_color_field(self, wx.StaticBox,
                                          (self, -1, "Address Details"),
                                           'details', _fc_dict),
                              wx.HORIZONTAL)
        vs=wx.BoxSizer(wx.VERTICAL)
        hs=wx.BoxSizer(wx.HORIZONTAL)
        hs.Add(field_color.build_color_field(self, wx.StaticText,
                                             (self, -1, "Type"),
                                             'type', _fc_dict),
               0, wx.ALIGN_CENTRE|wx.ALL, 5)
        self.type=wx.ComboBox(self, self.ID_TYPE, "Home", choices=["Home", "Business"], style=wx.CB_READONLY)
        hs.Add(self.type, 0, wx.EXPAND|wx.ALL, 5)
        hs.Add(field_color.build_color_field(self, wx.StaticText,
                                             (self, -1, "Company"),
                                             'company', _fc_dict),
               0, wx.ALIGN_CENTRE|wx.ALL, 5)
        self.company=wx.TextCtrl(self, -1, "")
        hs.Add(self.company, 1, wx.EXPAND|wx.ALL, 5)
        gs=wx.FlexGridSizer(6,2,2,5)
        for name,desc in self.fieldinfos:
            gs.Add(field_color.build_color_field(self, wx.StaticText,
                                                 (self, -1, desc),
                                                 name, _fc_dict),
                   0, wx.ALIGN_CENTRE)
            setattr(self, name, wx.TextCtrl(self, -1, ""))
            gs.Add(getattr(self,name), 1, wx.EXPAND)
        gs.AddGrowableCol(1)
        vs.Add(hs,0,wx.EXPAND|wx.ALL, 5)
        vs.Add(gs,0,wx.EXPAND|wx.ALL, 5)
        _hs.Add(vs, 0, wx.EXPAND|wx.ALL, 5)
        if navtoolbar:
            _hs.Add(NavToolBar(self, horizontal=False), 0, wx.EXPAND|wx.BOTTOM, 5)
        wx.EVT_TEXT(self, self.type.GetId(), self.OnDirtyUI)
        wx.EVT_TEXT(self, self.company.GetId(), self.OnDirtyUI)
        for name,_ in self.fieldinfos:
            wx.EVT_TEXT(self, getattr(self, name).GetId(), self.OnDirtyUI)
        self.SetSizer(_hs)
        vs.Fit(self)
    def Set(self, data):
        self.Ignore()
        for name,ignore in self.fieldinfos:
            getattr(self, name).SetValue(data.get(name, ""))
        self.company.SetValue(data.get("company", ""))
        if data.get("type", "home")=="home":
            self.type.SetValue("Home")
        else:
            self.type.SetValue("Business")
        self.Clean()
    def Get(self):
        self.Clean()
        res={}
        for name,ignore in self.fieldinfos:
            w=getattr(self, name)
            if len(w.GetValue()):
                res[name]=w.GetValue()
        if self.type.GetSelection()==1:
            if len(self.company.GetValue()):
                res['company']=self.company.GetValue()
        if len(res):
            res['type']=['home', 'business'][self.type.GetSelection()]
        return res
class NameEditor(DirtyUIBase):
    def __init__(self, parent, _, navtoolbar=False):
        super(NameEditor, self).__init__(parent)
        _fc_dict=field_color.build_field_info(self, 'name')
        _hs=wx.StaticBoxSizer(field_color.build_color_field(self,
                                                            wx.StaticBox,
                                                            (self, -1, 'Name Details'),
                                                            'details', _fc_dict),
                              wx.HORIZONTAL)
        vs=wx.BoxSizer(wx.VERTICAL)
        hstop=wx.BoxSizer(wx.HORIZONTAL)
        hsbot=wx.BoxSizer(wx.HORIZONTAL)
        hstop.Add(field_color.build_color_field(self, wx.StaticText,
                                                (self, -1, "First"),
                                                'first', _fc_dict),
                  0, wx.ALIGN_CENTRE|wx.ALL,5)
        self.first=wx.TextCtrl(self, -1, "")
        hstop.Add(self.first, 1, wx.EXPAND|wx.ALL, 5)
        hstop.Add(field_color.build_color_field(self, wx.StaticText,
                                                (self, -1, "Middle"),
                                                'middle', _fc_dict),
                  0, wx.ALIGN_CENTRE|wx.ALL,5)
        self.middle=wx.TextCtrl(self, -1, "")
        hstop.Add(self.middle, 1, wx.EXPAND|wx.ALL, 5)
        hstop.Add(field_color.build_color_field(self, wx.StaticText,
                                                (self, -1, "Last"),
                                                'last', _fc_dict),
                  0, wx.ALIGN_CENTRE|wx.ALL,5)
        self.last=wx.TextCtrl(self, -1, "")
        hstop.Add(self.last, 1, wx.EXPAND|wx.ALL, 5)
        hsbot.Add(field_color.build_color_field(self, wx.StaticText,
                                                (self, -1, "Full"),
                                                'full', _fc_dict),
                  0, wx.ALIGN_CENTRE|wx.ALL,5)
        self.full=wx.TextCtrl(self, -1, "")
        hsbot.Add(self.full, 4, wx.EXPAND|wx.ALL, 5)
        hsbot.Add(field_color.build_color_field(self, wx.StaticText,
                                                (self, -1, "Nickname"),
                                                'nickname', _fc_dict),
                  0, wx.ALIGN_CENTRE|wx.ALL,5)
        self.nickname=wx.TextCtrl(self, -1, "")
        hsbot.Add(self.nickname, 1, wx.EXPAND|wx.ALL, 5)
        vs.Add(hstop, 0, wx.EXPAND|wx.ALL, 5)
        vs.Add(hsbot, 0, wx.EXPAND|wx.ALL, 5)
        _hs.Add(vs, 0, wx.EXPAND|wx.ALL, 5)
        if navtoolbar:
            _hs.Add(NavToolBar(self, horizontal=False), 0, wx.EXPAND, 0)
        for _name in ('first', 'middle', 'last', 'full', 'nickname'):
            wx.EVT_TEXT(self, getattr(self, _name).GetId(), self.OnDirtyUI)
        self.SetSizer(_hs)
        vs.Fit(self)
    def Set(self, data):
        self.Ignore()
        self.first.SetValue(data.get("first", ""))
        self.middle.SetValue(data.get("middle", ""))
        self.last.SetValue(data.get("last", ""))
        self.full.SetValue(data.get("full", ""))
        self.nickname.SetValue(data.get("nickname", ""))
        self.Clean()
    def Get(self):
        self.Clean()
        res={}
        for name,widget in ( "first", self.first), ("middle", self.middle), ("last", self.last), \
            ("full", self.full), ("nickname", self.nickname):
            if len(widget.GetValue()):
                res[name]=widget.GetValue()
        return res
class StorageEditor(DirtyUIBase):
    def __init__(self, parent, _, navtoolbar=False):
        super(StorageEditor, self).__init__(parent)
        vs=wx.StaticBoxSizer(field_color.build_color_field(self,
                                                           wx.StaticBox,
                                                           (self, -1, "Storage Details"),
                                                           'storage'),
                             wx.VERTICAL)
        hs=wx.BoxSizer(wx.HORIZONTAL)
        hs.Add(field_color.build_color_field(self, wx.StaticText,
                                             (self, -1, "Storage Option:"),
                                             'storage'),
               0, wx.ALIGN_CENTRE|wx.ALL, 5)
        self._storage=wx.ComboBox(self, -1, 'Phone', choices=["Phone", "SIM"],
                                  style=wx.CB_READONLY)
        hs.Add(self._storage, 0, wx.EXPAND|wx.LEFT, 5)
        vs.Add(hs, 0, wx.EXPAND|wx.ALL, 5)
        self.SetSizer(vs)
        vs.Fit(self)
    def Set(self, data):
        _value=False
        for d in data.get('flags', []):
            if d.has_key('sim'):
                _value=d['sim']
                break
        if _value:
            _choice='SIM'
        else:
            _choice='Phone'
        self._storage.SetValue(_choice)
    def Get(self):
        return { 'flags': [{'sim': self._storage.GetValue()=='SIM' }]}
class EditorManager(fixedscrolledpanel.wxScrolledPanel):
    ID_DOWN=wx.NewId()
    ID_UP=wx.NewId()
    ID_ADD=wx.NewId()
    ID_DELETE=wx.NewId()
    instruction_text="""
\n\nPress Add above to add a field.  Press Delete to remove the field your
cursor is on.
You can use Up and Down to change the priority of items.  For example, some
phones store the first five numbers in the numbers tab, and treat the first
number as the default to call.  Other phones can only store one email address
so only the first one would be stored.
"""
    def __init__(self, parent, childclass):
        """Constructor
        @param parent: Parent window
        @param childclass: One of the *Editor classes which is used as a factory for making the
               widgets that correspond to each value"""
        fixedscrolledpanel.wxScrolledPanel.__init__(self, parent)
        self.dirty_ui_handler=getattr(parent, 'OnDirtyUI', None)
        self.sizer=wx.BoxSizer(wx.VERTICAL)
        self.SetSizer(self.sizer)
        self.widgets=[]
        self.childclass=childclass
        self.instructions=wx.StaticText(self, -1, EditorManager.instruction_text)
        self.sizer.Add(self.instructions, 0, wx.ALIGN_CENTER )
        self.SetupScrolling()
    def Get(self):
        """Returns a list of dicts corresponding to the values"""
        res=[]
        for i in self.widgets:
            g=i.Get()
            if len(g):
                res.append(g)
        return res
    def Populate(self, data):
        """Fills in the editors according to the list of dicts in data
        The editor widgets are created and destroyed as needed"""
        callsus=False
        while len(data)>len(self.widgets):
            callsus=True
            _w=self.childclass(self, len(self.widgets), navtoolbar=True)
            if self.dirty_ui_handler:
                EVT_DIRTY_UI(self, _w.GetId(), self.dirty_ui_handler)
            self.widgets.append(_w)
            self.sizer.Add(_w, 0, wx.EXPAND|wx.ALL, 10)
        while len(self.widgets)>len(data):
            callsus=True
            self.sizer.Remove(self.widgets[-1])
            self.widgets[-1].Destroy()
            del self.widgets[-1]
        for num in range(len(data)):
            self.widgets[num].Clean()
            self.widgets[num].Set(data[num])
        callsus=self.DoInstructionsLayout() or callsus
        if callsus:
            self.sizer.Layout()
            self.SetupScrolling()
    def DoInstructionsLayout(self):
        "Returns True if Layout should be called"
        if len(self.widgets):
            if self.instructions.IsShown():
                self.sizer.Remove(self.instructions)
                self.instructions.Show(False)
                return True
        else:
            if not self.instructions.IsShown():
                self.sizer.Add(self.instructions, 0, wx.ALIGN_CENTER )
                self.instructions.Show(True)
                return True
        return False
    def GetCurrentWidgetIndex(self):
        """Returns the index of the currently selected editor widget
        @raise IndexError: if there is no selected one"""
        focuswin=wx.Window.FindFocus()
        win=focuswin
        while win is not None and win not in self.widgets:
            win=win.GetParent()
        if win is None:
            raise IndexError("no idea who is selected")
        if win not in self.widgets:
            raise IndexError("no idea what that thing is")
        pos=self.widgets.index(win)
        return pos
    def Add(self):
        """Adds a new widget at the currently selected location"""
        gets=[x.Get() for x in self.widgets]
        try:
            pos=self.GetCurrentWidgetIndex()
        except IndexError:
            pos=len(gets)-1
        _w=self.childclass(self, len(self.widgets), navtoolbar=True)
        if self.dirty_ui_handler:
            EVT_DIRTY_UI(self, _w.GetId(), self.dirty_ui_handler)
            self.dirty_ui_handler(None)
        self.widgets.append(_w)
        self.sizer.Add(_w, 0, wx.EXPAND|wx.ALL, 10)
        self.DoInstructionsLayout() 
        self.sizer.Layout()
        self.SetupScrolling()
        if len(self.widgets)>1:
            for num,value in zip( range(pos+2, len(self.widgets)), gets[pos+1:]):
                self.widgets[num].Set(value)
            self.widgets[pos+1].Set({})
            self.widgets[pos+1].SetFocus()
        else:
            self.widgets[0].SetFocus()
    def MoveField(self, field, delta):
        try:
            pos=self.widgets.index(field)
        except IndexError:
            wx.Bell()
            return
        if pos+delta<0:
            print "that would go off top"
            return
        if pos+delta>=len(self.widgets):
            print "that would go off bottom"
            return
        if self.dirty_ui_handler:
            self.dirty_ui_handler(None)
        gets=[x.Get() for x in self.widgets]
        path,settings=self.GetWidgetPathAndSettings(self.widgets[pos], field)
        self.widgets[pos+delta].Set(gets[pos])
        self.widgets[pos].Set(gets[pos+delta])
        self.SetWidgetPathAndSettings(self.widgets[pos+delta], path, settings)
    def DeleteField(self, field):
        """Deletes the currently select widget"""
        if len(self.widgets)==0:
            return
        gets=[x.Get() for x in self.widgets]
        try:
            pos=self.widgets.index(field)
        except IndexError:
            wx.Bell()
            return
        if self.dirty_ui_handler:
            self.dirty_ui_handler(None)
        self.sizer.Remove(self.widgets[-1])
        self.widgets[-1].Destroy()
        del self.widgets[-1]
        if len(self.widgets):
            if pos==len(self.widgets):
                self.widgets[pos-1].SetFocus()
        self.DoInstructionsLayout() 
        self.sizer.Layout()
        self.SetupScrolling()
        for i in range(pos, len(self.widgets)):
            self.widgets[i].Set(gets[i+1])
        if len(self.widgets):
            if pos<len(self.widgets):
                self.widgets[pos].SetFocus()
    def Delete(self):
        """Deletes the currently select widget"""
        if len(self.widgets)==0:
            return
        gets=[x.Get() for x in self.widgets]
        try:
            pos=self.GetCurrentWidgetIndex()
        except IndexError:
            wx.Bell()
            return
        self.sizer.Remove(self.widgets[-1])
        self.widgets[-1].Destroy()
        del self.widgets[-1]
        if len(self.widgets):
            if pos==len(self.widgets):
                self.widgets[pos-1].SetFocus()
        self.DoInstructionsLayout() 
        self.sizer.Layout()
        self.SetupScrolling()
        for i in range(pos, len(self.widgets)):
            self.widgets[i].Set(gets[i+1])
        if len(self.widgets):
            if pos<len(self.widgets):
                self.widgets[pos].SetFocus()
    def Move(self, delta):
        """Moves the currently selected widget
        @param delta: positive to move down, negative to move up
        """
        focuswin=wx.Window_FindFocus()
        try:
            pos=self.GetCurrentWidgetIndex()
        except IndexError:
            wx.Bell()
            return
        if pos+delta<0:
            print "that would go off top"
            return
        if pos+delta>=len(self.widgets):
            print "that would go off bottom"
            return
        gets=[x.Get() for x in self.widgets]
        path,settings=self.GetWidgetPathAndSettings(self.widgets[pos], focuswin)
        self.widgets[pos+delta].Set(gets[pos])
        self.widgets[pos].Set(gets[pos+delta])
        self.SetWidgetPathAndSettings(self.widgets[pos+delta], path, settings)
    def GetWidgetPathAndSettings(self, widgetfrom, controlfrom):
        """Finds the specified control within the editor widgetfrom.
        The values are for calling L{SetWidgetPathAndSettings}.
        Returns a tuple of (path, settings).  path corresponds
        to the hierarchy with an editor (eg a panel contains a
        radiobox contains the radio button widget).  settings
        means something to L{SetWidgetPathAndSettings}.  For example,
        if the widget is a text widget it contains the current insertion
        point and selection."""
        path=[]
        win=controlfrom
        while win is not widgetfrom:
            p=win.GetParent()
            kiddies=p.GetChildren()
            found=False
            for kid in range(len(kiddies)):
                if kiddies[kid] is win:
                    path=[kid]+path
                    win=p
                    found=True
                    break
            if found:
                continue
            print "i don't appear to be my parent's child!!!"
            return
        settings=[]
        if isinstance(controlfrom, wx.TextCtrl):
            settings=[controlfrom.GetInsertionPoint(), controlfrom.GetSelection()]
        return path,settings
    def SetWidgetPathAndSettings(self,widgetto,path,settings):
        """See L{GetWidgetPathAndSettings}"""
        print path
        win=widgetto
        for p in path:
            kids=win.GetChildren()
            win=kids[p]
        controlto=win
        controlto.SetFocus()
        if isinstance(controlto, wx.TextCtrl):
            controlto.SetInsertionPoint(settings[0])
            controlto.SetSelection(settings[1][0], settings[1][1])
    def SetFocusOnValue(self, index):
        """Sets focus to the editor widget corresponding to the supplied index"""
        wx.CallAfter(self.widgets[index].SetFocus)
class Editor(wx.Dialog):
    "The Editor Dialog itself.  It contains panes for the various field types."
    ID_DOWN=wx.NewId()
    ID_UP=wx.NewId()
    ID_ADD=wx.NewId()
    ID_DELETE=wx.NewId()
    color_field_name='phonebook'
    tabsfactory=[
        ("Names", "names", NameEditor),
        ("Numbers", "numbers", NumberEditor),
        ("Emails",  "emails", EmailEditor),
        ("Addresses", "addresses", AddressEditor),
        ("URLs", "urls", URLEditor),
        ("Memos", "memos", MemoEditor),
        ("Categories", "categories", CategoryEditor),
        ("Wallpapers", "wallpapers", WallpaperEditor),
        ("Ringtones", "ringtones", RingtoneEditor),
        ("Storage", None, StorageEditor),
        ]
    def __init__(self, parent, data, title="Edit PhoneBook Entry",
                 keytoopenon=None, dataindex=None,
                 factory=database.dictdataobjectfactory, readonly=False,
                 datakey=None, movement=False):
        """Constructor for phonebookentryeditor dialog
        @param parent: parent window
        @param data: dict of values to edit
        @param title: window title
        @param keytoopenon: The key to open on. This is the key as stored in the data such as "names", "numbers"
        @param dataindex: Which value within the tab specified by keytoopenon to set focus to
        @param readonly: Indicates read-only data.
        """
        global _ringtone_list, _wallpaper_list        
        wx.Dialog.__init__(self, parent, -1, title, size=(740,580), style=wx.DEFAULT_DIALOG_STYLE|wx.RESIZE_BORDER)
        field_color.get_color_info_from_profile(self)
        _ringtone_list=None
        _wallpaper_list=None
        self._data_key=datakey
        if movement and datakey is None and __debug__:
            self.log('Movement and datakey is None')
            raise ValueError
        self.dirty_widgets={ True: [], False: [] }
        self.data=factory.newdataobject(data)
        vs=wx.BoxSizer(wx.VERTICAL)
        _hbs=wx.BoxSizer(wx.HORIZONTAL)
        self._title=wx.StaticText(self, -1, "Name here", style=wx.ALIGN_CENTRE|wx.ST_NO_AUTORESIZE)
        _add_btn=wx.BitmapButton(self, wx.NewId(),
                                 wx.ArtProvider.GetBitmap(guihelper.ART_ADD_FIELD), name="Prev Item")
        if movement:
            _prev_btn=wx.BitmapButton(self, wx.NewId(), wx.ArtProvider.GetBitmap(guihelper.ART_ARROW_LEFT), name="Prev Item")
            _next_btn=wx.BitmapButton(self, wx.NewId(), wx.ArtProvider.GetBitmap(guihelper.ART_ARROW_RIGHT), name="Next Item")
            self.dirty_widgets[False].append(_prev_btn)
            self.dirty_widgets[False].append(_next_btn)
            _hbs.Add(_prev_btn, 0, wx.EXPAND, 0)
            _hbs.Add(_add_btn, 0, wx.EXPAND|wx.LEFT, 10)
            _hbs.Add(self._title, 1, wx.EXPAND, 0)
            _hbs.Add(_next_btn, 0, wx.EXPAND, 0)
            wx.EVT_BUTTON(self, _prev_btn.GetId(), self.OnMovePrev)
            wx.EVT_BUTTON(self, _next_btn.GetId(), self.OnMoveNext)
        else:
            _hbs.Add(_add_btn, 0, wx.EXPAND|wx.LEFT, 10)
            _hbs.Add(self._title, 1, wx.EXPAND, 0)
        wx.EVT_BUTTON(self, _add_btn.GetId(), self.Add)
        vs.Add(_hbs, 0, wx.ALL|wx.EXPAND, 5)
        nb=wx.Notebook(self, -1)
        self.nb=nb
        self.nb.OnDirtyUI=self.OnDirtyUI
        vs.Add(nb,1,wx.EXPAND|wx.ALL,5)
        self.tabs=[]
        for name,key,klass in self.tabsfactory:
            widget=EditorManager(self.nb, klass)
            nb.AddPage(widget,name)
            if key==keytoopenon:
                nb.SetSelection(len(self.tabs))
            self.tabs.append(widget)
        self.Populate()
        for _idx, (name,key,klass) in enumerate(self.tabsfactory):
            if key and key==keytoopenon and dataindex is not None:
                self.tabs[_idx].SetFocusOnValue(dataindex)
        vs.Add(wx.StaticLine(self, -1, style=wx.LI_HORIZONTAL), 0, wx.EXPAND|wx.ALL, 5)
        _btn_sizer=wx.StdDialogButtonSizer()
        if not readonly:
            _btn_sizer.AddButton(wx.Button(self, wx.ID_OK))
            if self._data_key is not None:
                _w=wx.Button(self, wx.ID_APPLY)
                self.dirty_widgets[True].append(_w)
                _btn_sizer.AddButton(_w)
                wx.EVT_BUTTON(self, wx.ID_APPLY, self.OnApply)
        _btn_sizer.AddButton(wx.Button(self, wx.ID_CANCEL))
        _w=wx.Button(self, wx.ID_REVERT_TO_SAVED)
        self.dirty_widgets[True].append(_w)
        _btn_sizer.SetNegativeButton(_w)
        _btn_sizer.Realize()
        vs.Add(_btn_sizer, 0, wx.ALIGN_CENTRE|wx.ALL, 5)
        self.SetSizer(vs)
        wx.EVT_BUTTON(self, wx.ID_REVERT_TO_SAVED, self.Revert)
        wx.EVT_TOOL(self, self.ID_UP, self.MoveUp)
        wx.EVT_TOOL(self, self.ID_DOWN, self.MoveDown)
        wx.EVT_TOOL(self, self.ID_ADD, self.Add)
        wx.EVT_TOOL(self, self.ID_DELETE, self.Delete)
        self.ignore_dirty=False
        self.dirty=False
        self.setdirty(False)
    def Revert(self, _):
        self.Populate()
        self.setdirty(False)
    def OnDirtyUI(self, _):
        self.setdirty()
    def setdirty(self, flg=True):
        if self.ignore_dirty:
            return
        self.dirty=flg
        for w in self.dirty_widgets[self.dirty]:
            w.Enable(True)
        for w in self.dirty_widgets[not self.dirty]:
            w.Enable(False)
    def OnApply(self, _):
        self.GetParent().SaveData(self.GetData(), self.GetDataKey())
        self.setdirty(False)
    def Populate(self):
        self._set_title()
        for _idx, (name,key,klass) in enumerate(self.tabsfactory):
            if key is None: 
                self.tabs[_idx].Populate([self.data])
            else:
                self.tabs[_idx].Populate(self.data.get(key, {}))
    def GetData(self):
        res=self.data
        for i in range(len(self.tabsfactory)):
            widget=self.nb.GetPage(i)
            data=widget.Get()
            key=self.tabsfactory[i][1]
            if len(data):
                if key is None:
                    res.update(data[0])
                else:
                    res[key]=data
            else:
                try:
                    if key is not None:
                        del res[key]
                except KeyError:
                    pass
        return res
    def GetDataKey(self):
        return self._data_key
    def MoveUp(self, _):
        self.nb.GetPage(self.nb.GetSelection()).Move(-1)
        self.setdirty()
    def MoveDown(self, _):
        self.nb.GetPage(self.nb.GetSelection()).Move(+1)
        self.setdirty()
    def Add(self, _):
        self.nb.GetPage(self.nb.GetSelection()).Add()
    def Delete(self, _):
        self.nb.GetPage(self.nb.GetSelection()).Delete()
        self.setdirty()
    def _set_title(self):
        if hasattr(self, '_title'):
            self._title.SetLabel(nameparser.getfullname(self.data['names'][0]))
    def OnMoveNext(self, _):
        _key,_data=self.GetParent().GetNextEntry(True)
        if _data:
            self.data=_data
            self._data_key=_key
            self.Populate()
    def OnMovePrev(self, _):
        _key,_data=self.GetParent().GetNextEntry(False)
        if _data:
            self.data=_data
            self._data_key=_key
            self.Populate()
class SingleFieldEditor(wx.Dialog):
    "Edit a single field for a groups of entries"
    ID_DOWN=wx.NewId()
    ID_UP=wx.NewId()
    ID_ADD=wx.NewId()
    ID_DELETE=wx.NewId()
    tabsfactory={
        'categories': ("Categories", "categories", CategoryEditor),
        'wallpapers': ("Wallpapers", "wallpapers", WallpaperEditor),
        'ringtones': ("Ringtones", "ringtones", RingtoneEditor) }
    color_field_name='phonebook'
    def __init__(self, parent, key):
        if not self.tabsfactory.has_key(key):
            raise KeyError
        super(SingleFieldEditor, self).__init__(parent, -1,
                                                "Edit PhoneBook Entry",
                                                size=(740,580),
                                                style=wx.DEFAULT_DIALOG_STYLE|wx.RESIZE_BORDER)
        field_color.get_color_info_from_profile(self)
        self._key=key
        vs=wx.BoxSizer(wx.VERTICAL)
        _hbs=wx.BoxSizer(wx.HORIZONTAL)
        _add_btn=wx.BitmapButton(self, wx.NewId(),
                                 wx.ArtProvider.GetBitmap(guihelper.ART_ADD_FIELD), name="Prev Item")
        _hbs.Add(_add_btn, 0, wx.EXPAND|wx.LEFT, 10)
        wx.EVT_BUTTON(self, _add_btn.GetId(), self.Add)
        vs.Add(_hbs, 0, wx.ALL|wx.EXPAND, 5)
        self.nb=wx.Notebook(self, -1)
        vs.Add(self.nb,1,wx.EXPAND|wx.ALL,5)
        name,key,klass=self.tabsfactory[key]
        widget=EditorManager(self.nb, klass)
        self.nb.AddPage(widget,name)
        vs.Add(wx.StaticLine(self, -1, style=wx.LI_HORIZONTAL), 0, wx.EXPAND|wx.ALL, 5)
        _btn_sizer=self.CreateButtonSizer(wx.OK|wx.CANCEL)
        vs.Add(_btn_sizer, 0, wx.ALIGN_CENTRE|wx.ALL, 5)
        self.SetSizer(vs)
        wx.EVT_TOOL(self, self.ID_UP, self.MoveUp)
        wx.EVT_TOOL(self, self.ID_DOWN, self.MoveDown)
        wx.EVT_TOOL(self, self.ID_ADD, self.Add)
        wx.EVT_TOOL(self, self.ID_DELETE, self.Delete)
    def MoveUp(self, _):
        self.nb.GetPage(0).Move(-1)
    def MoveDown(self, _):
        self.nb.GetPage(0).Move(+1)
    def Add(self, _):
        self.nb.GetPage(0).Add()
    def Delete(self, _):
        self.nb.GetPage(0).Delete()
    def GetData(self):
        return self.nb.GetPage(0).Get()
if __name__=='__main__':
    data={ 'names': [ { 'full': 'John Smith'}, { 'nickname': 'I Love Testing'} ],
           'categories': [ {'category': 'business'}, {'category': 'friend' } ],
           'urls': [ {'url': 'www.example.com'}, {'url': 'http://www.example.net', 'type': 'home'} ],
           'ringtones': [ {'ringtone': 'mi2.mid', 'use': 'call'}, {'ringtone': 'dots.mid', 'use': 'message'}],
           'addresses': [ {'type': 'home', 'street': '123 Main Street', 'city': 'Main Town', 'state': 'CA', 'postalcode': '12345'},
                          {'type': 'business', 'company': 'Acme Widgets Inc', 'street': '444 Industrial Way', 'street2': 'Square Business Park',
                           'city': 'City Of Quality', 'state': 'Northern', 'postalcode': 'GHGJJ-12324', 'country': 'Nations United'}
                          ],
           'wallpapers': [{'wallpaper': 'pic1.bmp', 'use': 'call'}, {'wallpaper': 'alert.jpg', 'use': 'message'}],
           'flags': [ {'secret': True}, {'wierd': 'orange'} ],
           'memos': [ {'memo': 'Some stuff about this person " is usually welcome'}, {'memo': 'A second note'}],
           'numbers': [ {'number': '123-432-2342', 'type': 'home', 'speeddial': 3}, {'number': '121=+4321/4', 'type': 'fax'}]
           }
    app=wx.PySimpleApp()
    dlg=Editor(None,data)
    dlg.ShowModal()
