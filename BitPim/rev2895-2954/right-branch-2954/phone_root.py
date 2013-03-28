"""The main gui code for BitPim"""
import ConfigParser
import time
import os
import cStringIO
import zipfile
import re
import sys
import shutil
import types
import datetime
import sha
import codecs
import locale
import wx
import guiwidgets
import common
import version
import helpids
import comdiagnose
import phonebook
import importexport
import wallpaper
import ringers
import guihelper
import bpcalendar
import bphtml
import database
import memo
import update
import todo
import sms_tab
import call_history
import phone_media_codec
import today
import pubsub
import playlist
import filesystem
import widgets
if guihelper.IsMSWindows():
    import win32api
    import win32con
    import win32gui
class PhoneTree(wx.TreeCtrl):
    def __init__(self, parent, mw, id):
        wx.TreeCtrl.__init__(self, parent, id, style=wx.TR_HAS_BUTTONS)
        self.parent=parent
        self.mw=mw
        self.config=mw.config
        bmsize=(22,22)
        wx.EVT_TREE_SEL_CHANGED(self,id, self.OnItemSelected)
        self.image_list=wx.ImageList(22, 22)
        self.img_dir=self.image_list.Add(wx.ArtProvider_GetBitmap(wx.ART_FOLDER,
                                                             wx.ART_OTHER,
                                                             bmsize))
        self.phonebook=self.image_list.Add(wx.ArtProvider.GetBitmap(guihelper.ART_SEL_PHONEBOOK,
                                                             wx.ART_TOOLBAR,
                                                             bmsize))
        self.wallpaper=self.image_list.Add(wx.ArtProvider.GetBitmap(guihelper.ART_SEL_WALLPAPER,
                                                             wx.ART_TOOLBAR,
                                                             bmsize))
        self.ringers=self.image_list.Add(wx.ArtProvider.GetBitmap(guihelper.ART_SEL_RINGERS,
                                                             wx.ART_TOOLBAR,
                                                             bmsize))
        self.calendar=self.image_list.Add(wx.ArtProvider.GetBitmap(guihelper.ART_SEL_CALENDAR,
                                                             wx.ART_TOOLBAR,
                                                             bmsize))
        self.call_history=self.image_list.Add(wx.ArtProvider.GetBitmap(guihelper.ART_SEL_CALLHISTORY,
                                                             wx.ART_TOOLBAR,
                                                             bmsize))
        self.sms=self.image_list.Add(wx.ArtProvider.GetBitmap(guihelper.ART_SEL_SMS,
                                                             wx.ART_TOOLBAR,
                                                             bmsize))
        self.memo=self.image_list.Add(wx.ArtProvider.GetBitmap(guihelper.ART_SEL_MEMO,
                                                             wx.ART_TOOLBAR,
                                                             bmsize))
        self.file=self.image_list.Add(wx.ArtProvider.GetBitmap(guihelper.ART_SEL_FILE,
                                                             wx.ART_TOOLBAR,
                                                             bmsize))
        self.log=self.image_list.Add(wx.ArtProvider.GetBitmap(guihelper.ART_SEL_LOG,
                                                             wx.ART_TOOLBAR,
                                                             bmsize))
        self.todo=self.image_list.Add(wx.ArtProvider.GetBitmap(guihelper.ART_SEL_TODO,
                                                             wx.ART_TOOLBAR,
                                                             bmsize))
        self.playlist=self.image_list.Add(wx.ArtProvider.GetBitmap(guihelper.ART_SEL_PLAYLIST,
                                                             wx.ART_TOOLBAR,
                                                             bmsize))
        self.protocol=self.image_list.Add(wx.ArtProvider.GetBitmap(guihelper.ART_SEL_PROTOCOL,
                                                             wx.ART_TOOLBAR,
                                                             bmsize))
        self.console=self.image_list.Add(wx.ArtProvider.GetBitmap(guihelper.ART_SEL_CONSOLE,
                                                             wx.ART_TOOLBAR,
                                                             bmsize))
        self.phone_root=self.image_list.Add(wx.ArtProvider.GetBitmap(guihelper.ART_PHONE_ROOT,
                                                             wx.ART_TOOLBAR,
                                                             bmsize))
        self.phone=self.image_list.Add(wx.ArtProvider.GetBitmap(guihelper.ART_PHONE,
                                                             wx.ART_TOOLBAR,
                                                             bmsize))
        self.root_image=self.image_list.Add(wx.ArtProvider.GetBitmap(guihelper.ART_SEL_ROOT,
                                                             wx.ART_TOOLBAR,
                                                             bmsize))
        self.SetImageList(self.image_list)
        self.DeleteAllItems()
        self.root=self.AddRoot("BitPim")
        self.SetPyData(self.root, None)
        self.SetItemImage(self.root, self.root_image)
        self.active_phone=None
        self.null_panel=widgets.BlankWidget(self.parent, wx.NewId())
        self.del_bmp, self.short_help_delete=self.null_panel.GetDeleteInfo()
        self.add_bmp, self.short_help_add=self.null_panel.GetAddInfo()
        self.active_panel=self.null_panel
        self.lw=None
        self.lwdata=None
        self.filesystemwidget=None
        self.lw=guiwidgets.LogWindow(self.parent)
        self.lw_id=self.AddPage(self.root, self.lw, "Log", self.log)
        lv=self.config.ReadInt("viewlogdata", 0)
        if lv:
            self.OnViewLogData(None)
        fv=self.config.ReadInt("viewfilesystem", 0)
        if fv:
            self.OnViewFilesystem(None)
            wx.Yield()
    def CreatePhone(self, name, config, database):
        phone=Phone(self.parent)
        phone_id=self.AddPage(self.root, phone, name, self.phone)
        phone.Initialise(self, self.mw, config, database, phone_id)
        if self.active_phone==None:
            self.Expand(phone_id)
            self.active_phone=phone
    def GetActivePhone(self):
        return self.active_phone
    def AddPage(self, phone, panel, name, image=None, after=None):
        if image==None:
            image=self.img_dir
        if after==None:
            item=self.PrependItem(phone, name)
        else:
            item=self.InsertItem(phone, after, name)
        self.SetPyData(item, panel)
        self.SetItemImage(item, image)
        panel.Show(False)
        panel.InitialiseWidget(self, item, phone, self.config)
        return item
    def AddNode(self, panel, name, image=None):
        if image==None:
            image=self.img_dir
        item=self.AppendItem(panel.id, name)
        self.SetPyData(item, panel)
        self.SetItemImage(item, image)
        return item
    def DeletePage(self, id):
        self.Delete(id)
    def OnItemSelected(self, _):
        item=self.GetSelection()
        self.active_panel=self.null_panel
        if item.IsOk(): 
            if self.GetItemPyData(item):
                self.active_panel=self.GetItemPyData(item)
                self.active_panel.OnSelected(item)
        self.mw.SetActivePanel(self.active_panel)
        del_bmp, short_help_delete=self.active_panel.GetDeleteInfo()
        add_bmp, short_help_add=self.active_panel.GetAddInfo()
        if del_bmp!=self.del_bmp or add_bmp!=self.add_bmp or self.short_help_delete!=short_help_delete:
            self.mw.UpdateToolbarOnPanelChange(add_bmp, short_help_add, del_bmp, short_help_delete)
            self.add_bmp=add_bmp
            self.del_bmp=del_bmp
            self.short_help_delete=short_help_delete
            self.short_help_add=short_help_add
        return
    def ViewLogDataUIEvent(self, event):
        event.Check(self.lwdata != None)
    def ViewFileSystemUIEvent(self, event):
        event.Check(self.filesystemwidget != None)
    def DataSendPhoneUpdateUIEvent(self, event):
        event.Enable(not wx.GetApp().SAFEMODE)
    def EditCopyUpdateUIEvent(self, event):
        event.Enable(self.active_panel.CanCopy())
    def EditPasteUpdateUIEvent(self, event):
        event.Enable(self.active_panel.CanPaste())
    def EditRenameUpdateUIEvent(self, event):
        event.Enable(self.active_panel.CanRename())
    def DataDeleteItemUpdateUIEvent(self, event):
        event.Enable(self.active_panel.CanDelete())
    def DataAddItemUpdateUIEvent(self, event):
        event.Enable(self.active_panel.CanAdd())
    def HistoricalDataUpdateUIEvent(self, event):
        event.Enable(self.active_panel.HasHistoricalData())
    def ViewColumnsandPreviewDataUpdateUIEvent(self, event):
        event.Enable(self.active_panel.HasPreviewPane())
        event.Check(self.active_panel.IsPreviewPaneEnabled())
    def FilePrintDataUpdateUIEvent(self, event):
        enable_print=hasattr(self.active_panel, "OnPrintDialog")
        event.Enable(self.active_panel.CanPrint())
    def SelectAllDataUpdateUIEvent(self, event):
        event.Enable(self.active_panel.CanSelectAll())
    def OnFilePrint(self,_):
        self.active_panel.OnPrintDialog(self, self.config)
    def OnDataHistorical(self, _):
        self.active_panel.OnHistoricalData()
    def OnEditAddEntry(self, evt):
        self.active_panel.OnAdd(evt)
    def OnEditDeleteEntry(self, evt):
        self.active_panel.OnDelete(evt)
    def OnEditSelectAll(self, evt):
        self.active_panel.OnSelectAll(evt)
    def OnCopyEntry(self, evt):
        self.active_panel.OnCopy(evt)
    def OnPasteEntry(self, evt):
        self.active_panel.OnPaste(evt)
    def OnRenameEntry(self, evt):
        self.active_panel.OnRename(evt)
    def OnViewClearLogs(self, _):
        self.lw.Clear()
        if self.lwdata is not None:
            self.lwdata.Clear()
    def OnViewColumns(self, _):
        dlg=phonebook.ColumnSelectorDialog(self, self.config, self.phonewidget)
        dlg.ShowModal()
        dlg.Destroy()
    def OnViewPreview(self, evt):
        if not self.active_panel.IsPreviewPaneEnabled():
            config=1
            preview_on=True
        else:
            config=0
            preview_on=False
        self.active_panel.OnViewPreview(preview_on)
    def OnViewLogData(self, _):
        logdatatitle="Protocol Log"
        if self.lwdata is None:
            self.lwdata=guiwidgets.LogWindow(self.parent)
            self.lwdata_id=self.AddPage(self.root, self.lwdata, logdatatitle, self.log, self.lw_id)
            self.config.WriteInt("viewlogdata", 1)
        else:
            self.lwdata=None
            self.DeletePage(self.lwdata_id)
            self.lwdata_id=0
            self.config.WriteInt("viewlogdata", 0)
    def OnViewFilesystem(self,_):
        logtitle="Log"
        fstitle="Filesystem"
        if self.filesystemwidget is None:
            self.filesystemwidget=filesystem.FileSystemView(self.mw, self.parent, id=97)
            pos=self.GetPrevSibling(self.lw_id)
            self.filesystemwidget_id=self.AddPage(self.root, self.filesystemwidget, fstitle, self.file, pos)
            self.config.WriteInt("viewfilesystem", 1)
        else:
            self.filesystemwidget=None
            self.DeletePage(self.filesystemwidget_id)
            self.config.WriteInt("viewfilesystem", 0)
    def OnBusyStart(self):
        return self.mw.OnBusyStart()
    def OnBusyEnd(self):
        return self.mw.OnBusyEnd()
class Phone(today.TodayWidget):
    def __init__(self, parent):
        self.parent=parent
        today.TodayWidget.__init__(self, self, self.parent)
    def Initialise(self, tree, mw, config, path, phone_id):
        self.tree=tree
        self.mw=mw
        self.phone_id=phone_id
        self.config=config
        self.path=path
        self.phoneprofile=self.mw.phoneprofile
        id=None
        self.EnsureDatabase(self.path, self.path)
        if self.config.ReadInt("console", 0):
            import developer
            id=self.tree.AddPage(self.phone_id, developer.DeveloperPanel(self.parent, {'mw': self.mw, 'db': self.database} ), "Console", self.tree.console)
        self.phonewidget=phonebook.PhoneWidget(self, self.parent, self.config)
        id=self.tree.AddPage(self.phone_id, self.phonewidget, "PhoneBook", self.tree.phonebook,id)
        self.wallpaperwidget=wallpaper.WallpaperView(self, self.parent)
        id=self.tree.AddPage(self.phone_id, self.wallpaperwidget, "Wallpaper", self.tree.wallpaper,id)
        self.ringerwidget=ringers.RingerView(self, self.parent)
        id=self.tree.AddPage(self.phone_id, self.ringerwidget, "Ringers", self.tree.ringers,id)
        self.calendarwidget=bpcalendar.Calendar(self, self.parent)
        id=self.tree.AddPage(self.phone_id, self.calendarwidget, "Calendar", self.tree.calendar,id)
        self.memowidget=memo.MemoWidget(self, self.parent)
        id=self.tree.AddPage(self.phone_id, self.memowidget, "Memo", self.tree.memo,id)
        self.todowidget=todo.TodoWidget(self, self.parent)
        id=self.tree.AddPage(self.phone_id, self.todowidget, 'Todo', self.tree.todo,id)
        self.smswidget=sms_tab.SMSWidget(self, self.parent)
        id=self.tree.AddPage(self.phone_id, self.smswidget, 'SMS', self.tree.sms,id)
        self.callhistorywidget=call_history.CallHistoryWidget(self, self.parent)
        id=self.tree.AddPage(self.phone_id, self.callhistorywidget, 'Call History', self.tree.call_history,id)
        self.playlistwidget=playlist.PlaylistWidget(self, self.parent)
        id=self.tree.AddPage(self.phone_id, self.playlistwidget, 'Play List', self.tree.playlist,id)
        self.mw.SetPhoneModelStatus()
        self.mw.SetVersionsStatus()
        self.ringerpath=self._fixup(os.path.join(self.path, "ringer"))
        self.wallpaperpath=self._fixup(os.path.join(self.path, "wallpaper"))
        self.phonebookpath=self._fixup(os.path.join(self.path, "phonebook"))
        self.calendarpath=self._fixup(os.path.join(self.path, "calendar"))
        try:
            results={}
            self.phonewidget.getfromfs(results)
            self.wallpaperwidget.getfromfs(results)
            self.ringerwidget.getfromfs(results)
            self.calendarwidget.getfromfs(results)
            self.memowidget.getfromfs(results)
            self.todowidget.getfromfs(results)
            self.smswidget.getfromfs(results)
            self.callhistorywidget.getfromfs(results)
            self.playlistwidget.getfromfs(results)
            wx.SafeYield(onlyIfNeeded=True)
            self.phonewidget.populate(results)
            wx.SafeYield(onlyIfNeeded=True)
            self.wallpaperwidget.populate(results)
            wx.SafeYield(onlyIfNeeded=True)
            self.ringerwidget.populate(results)
            wx.SafeYield(onlyIfNeeded=True)
            self.calendarwidget.populate(results)
            wx.SafeYield(onlyIfNeeded=True)
            self.memowidget.populate(results)
            wx.SafeYield(onlyIfNeeded=True)
            self.todowidget.populate(results)
            wx.SafeYield(onlyIfNeeded=True)
            self.smswidget.populate(results)
            wx.SafeYield(onlyIfNeeded=True)
            self.callhistorywidget.populate(results)
            wx.SafeYield(onlyIfNeeded=True)
            self.playlistwidget.populate(results)
        except Exception, e:
            if __debug__:
                raise Exception, e 
            pass
    def EnsureDatabase(self, newpath, oldpath):
        newdbpath=os.path.abspath(os.path.join(newpath, "bitpim.db"))
        if oldpath is not None and len(oldpath) and oldpath!=newpath:
            if self.database:
                self.database=None # cause it to be closed
            olddbpath=os.path.abspath(os.path.join(oldpath, "bitpim.db"))
            if os.path.exists(olddbpath) and not os.path.exists(newdbpath):
                shutil.copyfile(olddbpath, newdbpath)
        self.database=None # allow gc
        self.database=database.Database(newdbpath)
    def GetDatabase(self):
        return self.database
    def _fixup(self, path):
        if len(path)>=3:
            if path[1]==':' and path[2]=='\\' and path[3]=='\\':
                return path[0:2]+path[3:]
        return path
    def OnBusyStart(self):
        return self.mw.OnBusyStart()
    def OnBusyEnd(self):
        return self.mw.OnBusyEnd()
