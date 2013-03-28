"""The main gui code for BitPim"""
import ConfigParser
import thread, threading
import Queue
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
import wx.lib.colourdb
import wx.gizmos
import wx.html
import wx.lib.mixins.listctrl  as  listmix
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
import bitflingscan
import database
import memo
import update
import todo
import sms_tab
import phoneinfo
import call_history
import phone_detect
import phone_media_codec
import hexeditor
import today
import pubsub
import phones.com_brew as com_brew
import auto_sync
import playlist
if guihelper.IsMSWindows():
    import win32api
    import win32con
    import win32gui
mainthreadid=thread.get_ident()
helperthreadid=-1 # set later
if guihelper.IsMSWindows():
    class TaskBarIcon(wx.TaskBarIcon):
        def __init__(self, mw):
            super(TaskBarIcon, self).__init__()
            self.mw=mw
            self._set_icon()
            wx.EVT_TASKBAR_LEFT_DCLICK(self, self.OnRestore)
        def _create_menu(self):
            _menu=wx.Menu()
            _id=wx.NewId()
            if self.mw.IsIconized():
                _menu.Append(_id, 'Restore')
                wx.EVT_MENU(self, _id, self.OnRestore)
            else:
                _menu.Append(_id, 'Minimize')
                wx.EVT_MENU(self, _id, self.OnMinimize)
            _menu.AppendSeparator()
            _id=wx.NewId()
            _menu.Append(_id, 'Close')
            wx.EVT_MENU(self, _id, self.OnClose)
            return _menu
        def _set_icon(self):
            _icon=wx.Icon(guihelper.getresourcefile('bitpim.ico'),
                          wx.BITMAP_TYPE_ICO)
            if _icon.Ok():
                self.SetIcon(_icon, 'BitPim')
        def CreatePopupMenu(self):
            return self._create_menu()
        def OnRestore(self, _):
            self.mw.Iconize(False)
        def OnMinimize(self, _):
            self.mw.Iconize(True)
        def OnClose(self, _):
            self.mw.Close()
class Callback:
    "Callback class.  Extra arguments can be supplied at call time"
    def __init__(self, method, *args, **kwargs):
        if __debug__:
            global mainthreadid
            assert mainthreadid==thread.get_ident()
        self.method=method
        self.args=args
        self.kwargs=kwargs
    def __call__(self, *args, **kwargs):
        if __debug__:
            global mainthreadid
            assert mainthreadid==thread.get_ident()
        d=self.kwargs.copy()
        d.update(kwargs)
        apply(self.method, self.args+args, d)
class Request:
    def __init__(self, method, *args, **kwargs):
        if __debug__:
            global mainthreadid
            assert mainthreadid==thread.get_ident()
        self.method=method
        self.args=args
        self.kwargs=kwargs
    def __call__(self, *args, **kwargs):
        if __debug__:
            global helperthreadid
            assert helperthreadid==thread.get_ident()
        d=self.kwargs.copy()
        d.update(kwargs)
        return apply(self.method, self.args+args, d)
class HelperReturnEvent(wx.PyEvent):
    def __init__(self, callback, *args, **kwargs):
        if __debug__:
            global helperthreadid
            assert helperthreadid==thread.get_ident()
        global EVT_CALLBACK
        wx.PyEvent.__init__(self)
        self.SetEventType(EVT_CALLBACK)
        self.cb=callback
        self.args=args
        self.kwargs=kwargs
    def __call__(self):
        if __debug__:
            global mainthreadid
            assert mainthreadid==thread.get_ident()
        return apply(self.cb, self.args, self.kwargs)
class WorkerThreadFramework(threading.Thread):
    def __init__(self):
        threading.Thread.__init__(self, name="BitPim helper")
        self.q=Queue.Queue()
    def setdispatch(self, dispatchto):
        self.dispatchto=dispatchto
    def checkthread(self):
        global helperthreadid
        assert helperthreadid==thread.get_ident()
    def run(self):
        global helperthreadid
        helperthreadid=thread.get_ident()
        first=1
        while True:
            if not first:
                wx.PostEvent(self.dispatchto, HelperReturnEvent(self.dispatchto.endbusycb))
            else:
                first=0
            item=self.q.get()
            wx.PostEvent(self.dispatchto, HelperReturnEvent(self.dispatchto.startbusycb))
            call=item[0]
            resultcb=item[1]
            ex=None
            res=None
            try:
                res=call()
            except Exception,e:
                ex=e
                if not hasattr(e,"gui_exc_info"):
                    ex.gui_exc_info=sys.exc_info()
            wx.PostEvent(self.dispatchto, HelperReturnEvent(resultcb, ex, res))
            if isinstance(ex, SystemExit):
                raise ex
    def progressminor(self, pos, max, desc=""):
        wx.PostEvent(self.dispatchto, HelperReturnEvent(self.dispatchto.progressminorcb, pos, max, desc))
    def progressmajor(self, pos, max, desc=""):
        wx.PostEvent(self.dispatchto, HelperReturnEvent(self.dispatchto.progressmajorcb, pos, max, desc))
    def progress(self, pos, max, desc=""):
        self.progressminor(pos, max, desc)
    def log(self, str):
        if self.dispatchto.wantlog:
            wx.PostEvent(self.dispatchto, HelperReturnEvent(self.dispatchto.logcb, str))
    def logdata(self, str, data, klass=None):
        if self.dispatchto.wantlog:
            wx.PostEvent(self.dispatchto, HelperReturnEvent(self.dispatchto.logdatacb, str, data, klass))
class Config(ConfigParser.ConfigParser):
    _default_config_filename='.bitpim'
    def __init__(self, config_file_name=None):
        ConfigParser.ConfigParser.__init__(self)
        if config_file_name:
            self._filename=os.path.abspath(config_file_name)
            self._path=os.path.dirname(self._filename)
        else:
            self._path, self._filename=self._getdefaults()
        if self._filename:
            self.read([self._filename])
            self.Write('path', self._path)
            self.Write('config', self._filename)
    def _getdefaults(self):
        if guihelper.IsMSWindows(): # we want subdir of my documents on windows
            from win32com.shell import shell, shellcon
            path=shell.SHGetFolderPath(0, shellcon.CSIDL_PERSONAL, None, 0)
            path=os.path.join(path, "bitpim")
        else:
            path=os.path.expanduser("~/.bitpim-files")
        return path,os.path.join(path, Config._default_config_filename)
    def _expand(self, key):
        _l=key.split('/')
        return (len(_l)>1 and '/'.join(_l[:-1]) or 'DEFAULT', _l[-1])
    def _check_section(self, section):
        if section and section!='DEFAULT' and not self.has_section(section):
            self.add_section(section)
    def Read(self, key, default=''):
        try:
            return self.get(*self._expand(key))
        except:
            return default
    def ReadInt(self, key, default=0):
        _section,_option=self._expand(key)
        try:
            return self.getint(_section, _option)
        except:
            pass
        try:
            return self.getboolean(_section, _option)
        except:
            return default
    def ReadFloat(self, key, default=0.0):
        try:
            return self.getfloat(*self._expand(key))
        except:
            return default
    def Write(self, key, value):
        try:
            _section,_option=self._expand(key)
            if not _section:
                _section='DEFAULT'
            self._check_section(_section)
            self.set(_section, _option, str(value))
            self.write(file(self._filename, 'wb'))
            return True
        except:
            return False
    WriteInt=Write
    WriteFloat=Write
    def HasEntry(self, key):
        return self.has_option(*self._expand(key))
    def Flush(self):
        pass
thesplashscreen=None  # set to non-none if there is one
class MySplashScreen(wx.SplashScreen):
    def __init__(self, app, config):
        self.app=app
        time=config.ReadInt("splashscreentime", 2500)
        if time>0:
            bmp=guihelper.getbitmap("splashscreen")
            self.drawnameandnumber(bmp)
            wx.SplashScreen.__init__(self, bmp, wx.SPLASH_CENTRE_ON_SCREEN|wx.SPLASH_TIMEOUT,
                                    time,
                                    None, -1)
            wx.EVT_CLOSE(self, self.OnClose)
            self.Show()
            app.Yield(True)
            global thesplashscreen
            thesplashscreen=self
            return
        self.goforit()
    def drawnameandnumber(self, bmp):
        dc=wx.MemoryDC()
        dc.SelectObject(bmp)
        x=23 
        y=40
        if False:
            str=version.name
            dc.SetTextForeground( wx.NamedColour("MEDIUMORCHID4") ) 
            dc.SetFont( self._gimmethedamnsizeirequested(25, wx.ROMAN, wx.NORMAL, wx.NORMAL) )
            w,h=dc.GetTextExtent(str)
            dc.DrawText(str, x, y)
            y+=h+0
        x=58
        y=127
        str=version.versionstring+"-"+version.vendor
        dc.SetTextForeground( wx.NamedColour("MEDIUMBLUE") )
        dc.SetFont( self._gimmethedamnsizeirequested(15, wx.ROMAN, wx.NORMAL, wx.NORMAL) )
        w,h=dc.GetTextExtent(str)
        dc.DrawText(str, x+10, y)
        y+=h+0
        dc.SelectObject(wx.NullBitmap)
    def _gimmethedamnsizeirequested(self, ps, family, style, weight):
        if guihelper.IsGtk():
            ps=ps*1.6
        font=wx.TheFontList.FindOrCreateFont(int(ps), family, style, weight)
        return font
    def goforit(self):
        self.app.makemainwindow()
    def OnClose(self, evt):
        self.goforit()
        evt.Skip()
def _notsafefunc(*args, **kwargs):
    raise common.InSafeModeException()
class _NotSafeObject:
    def __getattr__(self, *args):  _notsafefunc()
    def __setattr__(self, *args): _notsafefunc()
_NotSafeObject=_NotSafeObject()
EVT_CALLBACK=None
class MainApp(wx.App):
    def __init__(self, argv, config_filename=None):
        self.frame=None
        self.SAFEMODE=False
        codecs.register(phone_media_codec.search_func)
        self._config_filename=config_filename
        wx.App.__init__(self, redirect=False, useBestVisual=True)
    def OnInit(self):
        self.made=False
        wx.lib.colourdb.updateColourDB()
        global mainthreadid
        mainthreadid=thread.get_ident()
        cfgstr='bitpim'
        self.SetAppName(cfgstr)
        self.SetVendorName(cfgstr)
        self.config=Config(self._config_filename)
        self.wxconfig=wx.Config(cfgstr, style=wx.CONFIG_USE_LOCAL_FILE)
        self.SAFEMODE=self.config.ReadInt("SafeMode", False)
        self.helpcontroller=None
        self.htmlprinter=bphtml.HtmlEasyPrinting(None, self.config, "printing")
        global EVT_CALLBACK
        EVT_CALLBACK=wx.NewEventType()
        com_brew.file_cache=com_brew.FileCache(self.config.Read('path', ''))
        MySplashScreen(self, self.config)
        return True
    def ApplySafeMode(self):
        if not self.SAFEMODE:
            return
        if self.frame is None:
            return
        objects={self.frame:
                    ( "dlgsendphone", "OnDataSendPhone", "OnDataSendPhoneGotFundamentals", "OnDataSendPhoneResults"),
                 self.frame.filesystemwidget:
                    ( "OnFileDelete", "OnFileOverwrite", "OnNewSubdir", "OnNewFile", "OnDirDelete", "OnRestore"),
                 self.frame.wt:
                    ( "senddata", "writewallpaper", "writeringtone", "writephonebook", "writecalendar", "rmfile",
                      "writefile", "mkdir", "rmdir", "rmdirs", "restorefiles" ),
                 self.frame.phoneprofile:
                    ( "convertphonebooktophone", ),
                 self.frame.phonemodule.Phone:
                    ( "mkdir", "mkdirs", "rmdir", "rmfile", "rmdirs", "writefile", "savegroups", "savephonebook",
                      "savecalendar", "savewallpapers", "saveringtones")
                 }
        for obj, names in objects.iteritems():
            if obj is None:
                continue
            for name in names:
                field=getattr(obj, name, None)
                if field is None or field is _notsafefunc or field is _NotSafeObject:
                    continue
                if isinstance(field, (types.MethodType, types.FunctionType)):
                    newval=_notsafefunc
                else: newval=_NotSafeObject
                setattr(obj, name, newval)
        removeids=(guihelper.ID_DATASENDPHONE, guihelper.ID_FV_OVERWRITE, guihelper.ID_FV_NEWSUBDIR,
                   guihelper.ID_FV_NEWFILE, guihelper.ID_FV_DELETE, guihelper.ID_FV_RENAME,
                   guihelper.ID_FV_RESTORE, guihelper.ID_FV_ADD)
        mb=self.frame.GetMenuBar()
        menus=[mb.GetMenu(i) for i in range(mb.GetMenuCount())]
        fsw=self.frame.filesystemwidget
        if  fsw is not None:
            menus.extend( [fsw.list.filemenu, fsw.tree.dirmenu, fsw.list.genericmenu] )
        for menu in menus:
            for id in removeids:
                item=menu.FindItemById(id)
                if item is not None:
                    menu.RemoveItem(item)
    def _setuphelp(self):
        """Does all the nonsense to get help working"""
        wx.FileSystem_AddHandler(wx.ZipFSHandler())
        self.helpcontroller=wx.html.HtmlHelpController()
        self.helpcontroller.AddBook(guihelper.gethelpfilename()+".htb")
        self.helpcontroller.UseConfig(self.wxconfig, "help")
    def displayhelpid(self, id):
        if guihelper.IsMSWindows():
            import win32help
            fname=guihelper.gethelpfilename()+".chm"
            if id is None:
                id=helpids.ID_WELCOME
            win32help.HtmlHelp(self.frame.GetHandle(), fname, win32help.HH_DISPLAY_TOPIC, id)
        else:
            if self.helpcontroller is None:
                self._setuphelp()
            if id is None:
                self.helpcontroller.DisplayContents()
            else:
                self.helpcontroller.Display(id)
    def makemainwindow(self):
        if self.made:
            return # already been called
        self.made=True
        self.frame=MainWindow(None, -1, "BitPim", self.config)
        self.frame.Connect(-1, -1, EVT_CALLBACK, self.frame.OnCallback)
        if guihelper.IsMac():
            self.frame.MacSetMetalAppearance(True)
        wt=WorkerThread()
        wt.setdispatch(self.frame)
        wt.setDaemon(1)
        wt.start()
        self.frame.wt=wt
        self.SetTopWindow(self.frame)
        self.SetExitOnFrameDelete(True)
        self.ApplySafeMode()
        wx.CallAfter(self.CheckDetectPhone)
        wx.CallAfter(self.CheckUpdate)
    update_delta={ 'Daily': 1, 'Weekly': 7, 'Monthly': 30 }
    def CheckUpdate(self):
        if version.isdevelopmentversion():
            return
        if self.frame is None: 
            return
        update_rate=self.config.Read('updaterate', '')
        if not len(update_rate) or update_rate =='Never':
            return
        last_update=self.config.Read('last_update', '')
        try:
            if len(last_update):
                last_date=datetime.date(int(last_update[:4]), int(last_update[4:6]),
                                        int(last_update[6:]))
                next_date=last_date+datetime.timedelta(\
                    self.update_delta.get(update_rate, 7))
            else:
                next_date=last_date=datetime.date.today()
        except ValueError:
            next_date=last_date=datetime.date.today()
        if datetime.date.today()<next_date:
            return
        self.frame.AddPendingEvent(\
            wx.PyCommandEvent(wx.wxEVT_COMMAND_MENU_SELECTED,
                              guihelper.ID_HELP_UPDATE))
    def CheckDetectPhone(self):
        if self.config.ReadInt('autodetectstart', 0) or self.frame.needconfig:
            self.frame.AddPendingEvent(
                wx.PyCommandEvent(wx.wxEVT_COMMAND_MENU_SELECTED,
                                  guihelper.ID_EDITDETECT))
    def OnExit(self): 
        self.config.Flush()
        sys.excepthook=donothingexceptionhandler
def donothingexceptionhandler(*args):
    pass
def run(argv, kwargs):
    return MainApp(argv, **kwargs).MainLoop()
class MenuCallback:
    "A wrapper to help with callbacks that ignores arguments when invoked"
    def __init__(self, func, *args, **kwargs):
        self.func=func
        self.args=args
        self.kwargs=kwargs
    def __call__(self, *args):
        return self.func(*self.args, **self.kwargs)
class MainWindow(wx.Frame):
    def __init__(self, parent, id, title, config):
        wx.Frame.__init__(self, parent, id, title,
                         style=wx.DEFAULT_FRAME_STYLE|wx.NO_FULL_REPAINT_ON_RESIZE)
        wx.GetApp().frame=self
        wx.GetApp().htmlprinter.SetParentFrame(self)
        sys.excepthook=Callback(self.excepthook)
        self.wt=None # worker thread
        self.progressminorcb=Callback(self.OnProgressMinor)
        self.progressmajorcb=Callback(self.OnProgressMajor)
        self.logcb=Callback(self.OnLog)
        self.logdatacb=Callback(self.OnLogData)
        self.startbusycb=Callback(self.OnBusyStart)
        self.endbusycb=Callback(self.OnBusyEnd)
        self.queue=Queue.Queue()
        self.exceptiondialog=None
        self.wantlog=1  # do we want to receive log information
        self.config=config
        self.progmajortext=""
        self.lw=None
        self.lwdata=None
        self.filesystemwidget=None
        self.__owner_name=''
        self.database=None
        self._taskbar=None
        self.__phone_detect_at_startup=False
        self._autodetect_delay=0
        sb=guiwidgets.MyStatusBar(self)
        self.SetStatusBar(sb)
        self.SetStatusBarPane(sb.GetHelpPane())
        wx.ArtProvider_PushProvider(guihelper.ArtProvider())
        menuBar = wx.MenuBar()
        menu = wx.Menu()
        menu.Append(guihelper.ID_FILEPRINT, "&Print...", "Print phonebook")
        impmenu=wx.Menu()
        for desc, help, func in importexport.GetPhonebookImports():
            x=wx.NewId()
            impmenu.Append(x, desc, help)
            wx.EVT_MENU(self, x, MenuCallback(func, self) )
        menu.AppendMenu(guihelper.ID_FILEIMPORT, "Import", impmenu)
        expmenu=wx.Menu()
        for desc, help, func in importexport.GetPhonebookExports():
            x=wx.NewId()
            expmenu.Append(x, desc, help)
            wx.EVT_MENU(self, x, MenuCallback(func, self) )
        menu.AppendMenu(guihelper.ID_FILEEXPORT, "Export", expmenu)
        if not guihelper.IsMac():
            menu.AppendSeparator()
            menu.Append(guihelper.ID_FILEEXIT, "E&xit", "Close down this program")
        menuBar.Append(menu, "&File");
        self.__menu_edit=menu=wx.Menu()
        menu.Append(guihelper.ID_EDITSELECTALL, "Select All\tCtrl+A", "Select All")
        menu.AppendSeparator()
        menu.Append(guihelper.ID_EDITADDENTRY, "New...\tCtrl+N", "Add an item")
        menu.Append(guihelper.ID_EDITCOPY, "Copy\tCtrl+C", "Copy to the clipboard")
        menu.Append(guihelper.ID_EDITPASTE,"Paste\tCtrl+V", "Paste from the clipboard")
        menu.Append(guihelper.ID_EDITDELETEENTRY, "Delete\tDel", "Delete currently selected entry")
        menu.Append(guihelper.ID_EDITRENAME, "Rename\tF2", "Rename currently selected entry")
        menu.AppendSeparator()
        menu.Append(guihelper.ID_EDITPHONEINFO,
                    "&Phone Info", "Display Phone Information")
        menu.AppendSeparator()
        menu.Append(guihelper.ID_EDITDETECT,
                    "Detect Phone", "Auto Detect Phone")
        if guihelper.IsMac():
            wx.App_SetMacPreferencesMenuItemId(guihelper.ID_EDITSETTINGS)
            menu.Append(guihelper.ID_EDITSETTINGS, "&Preferences...", "Edit Settings")
        else:
            menu.AppendSeparator()
            menu.Append(guihelper.ID_EDITSETTINGS, "&Settings", "Edit settings")
        menuBar.Append(menu, "&Edit");
        menu=wx.Menu()
        menu.Append(guihelper.ID_DATAGETPHONE, "Get Phone &Data ...", "Loads data from the phone")
        menu.Append(guihelper.ID_DATASENDPHONE, "&Send Phone Data ...", "Sends data to the phone")
        menu.Append(guihelper.ID_DATAHISTORICAL, "Historical Data", "View Current & Historical Data")
        menuBar.Append(menu, "&Data")
        menu=wx.Menu()
        menu.Append(guihelper.ID_AUTOSYNCSETTINGS, "&Configure AutoSync Settings...", "Configures Schedule Auto-Synchronisation")
        menu.Append(guihelper.ID_AUTOSYNCEXECUTE, "&Synchronize Schedule Now", "Synchronize Schedule Now")
        menuBar.Append(menu, "&AutoSync")
        menu=wx.Menu()
        menu.Append(guihelper.ID_VIEWCOLUMNS, "Columns ...", "Which columns to show")
        menu.AppendCheckItem(guihelper.ID_VIEWPREVIEW, "Phonebook Preview", "Toggle Phonebook Preview Pane")
        menu.AppendSeparator()
        menu.AppendCheckItem(guihelper.ID_VIEWLOGDATA, "View protocol logging", "View protocol logging information")
        menu.Append(guihelper.ID_VIEWCLEARLOGS, "Clear logs", "Clears the contents of the log panes")
        menu.AppendSeparator()
        menu.AppendCheckItem(guihelper.ID_VIEWFILESYSTEM, "View filesystem", "View filesystem on the phone")
        menuBar.Append(menu, "&View")
        menu=wx.Menu()
        if guihelper.IsMac():
            menu.Append(guihelper.ID_HELPHELP, "&BitPim Help", "Help for the panel you are looking at")
            menu.AppendSeparator()
        else:
            menu.Append(guihelper.ID_HELPHELP, "&Help", "Help for the panel you are looking at")
        menu.Append(guihelper.ID_HELPTOUR, "&Tour", "Tour of BitPim")
        menu.Append(guihelper.ID_HELPCONTENTS, "&Contents", "Table of contents for the online help")
        menu.Append(guihelper.ID_HELPSUPPORT, "&Support", "Getting support for BitPim")
        if version.vendor=='official':
            menu.AppendSeparator()
            menu.Append(guihelper.ID_HELP_UPDATE, "&Check for Update", "Check for any BitPim Update")
        if guihelper.IsMac():
            wx.App_SetMacAboutMenuItemId(guihelper.ID_HELPABOUT)
            menu.Append(guihelper.ID_HELPABOUT, "&About BitPim", "Display program information")
            wx.App_SetMacHelpMenuTitleName("&Help")
            wx.App_SetMacExitMenuItemId(guihelper.ID_FILEEXIT)
        else:
            menu.AppendSeparator()
            menu.Append(guihelper.ID_HELPABOUT, "&About", "Display program information")
        menuBar.Append(menu, "&Help");
        self.SetMenuBar(menuBar)
        self.tb=self.CreateToolBar(wx.TB_HORIZONTAL)
        self.tb.SetToolBitmapSize(wx.Size(32,32))
        sz=self.tb.GetToolBitmapSize()
        self.getphonedata=self.tb.AddSimpleTool(guihelper.ID_DATAGETPHONE, wx.ArtProvider.GetBitmap(guihelper.ART_DATAGETPHONE, wx.ART_TOOLBAR, sz),
                                                "Get Phone Data", "Synchronize BitPim with Phone")
        self.sendphonedata=self.tb.AddLabelTool(guihelper.ID_DATASENDPHONE, "Send Phone Data", wx.ArtProvider.GetBitmap(guihelper.ART_DATASENDPHONE, wx.ART_TOOLBAR, sz),
                                          shortHelp="Send Phone Data", longHelp="Synchronize Phone with BitPim")
        self.tb.AddSeparator()
        self.tooladd=self.tb.AddLabelTool(guihelper.ID_EDITADDENTRY, "Add", wx.ArtProvider.GetBitmap(wx.ART_ADD_BOOKMARK, wx.ART_TOOLBAR, sz),
                                          shortHelp="Add", longHelp="Add an item")
        self.tooldelete=self.tb.AddLabelTool(guihelper.ID_EDITDELETEENTRY, "Delete", wx.ArtProvider.GetBitmap(wx.ART_DEL_BOOKMARK, wx.ART_TOOLBAR, sz),
                                             shortHelp="Delete", longHelp="Delete item")
        self.editphoneinfo=self.tb.AddLabelTool(guihelper.ID_EDITPHONEINFO, "Phone Info", wx.ArtProvider.GetBitmap(guihelper.ART_EDITPHONEINFO, wx.ART_TOOLBAR, sz),
                                          shortHelp="Phone Info", longHelp="Show Phone Info")
        self.editdetectphone=self.tb.AddLabelTool(guihelper.ID_EDITDETECT, "Find Phone", wx.ArtProvider.GetBitmap(guihelper.ART_EDITDETECT, wx.ART_TOOLBAR, sz),
                                          shortHelp="Find Phone", longHelp="Find Phone")
        self.editsettings=self.tb.AddLabelTool(guihelper.ID_EDITSETTINGS, "Edit Settings", wx.ArtProvider.GetBitmap(guihelper.ART_EDITSETTINGS, wx.ART_TOOLBAR, sz),
                                          shortHelp="Edit Settings", longHelp="Edit BitPim Settings")
        self.tb.AddSeparator()
        self.autosync=self.tb.AddSimpleTool(guihelper.ID_AUTOSYNCEXECUTE, wx.ArtProvider.GetBitmap(guihelper.ART_AUTOSYNCEXECUTE, wx.ART_TOOLBAR, sz),
                                            "Autosync Calendar", "Synchronize Phone Calendar with PC")
        self.tb.AddSeparator()
        self.help=self.tb.AddLabelTool(guihelper.ID_HELPHELP, "BitPim Help", wx.ArtProvider.GetBitmap(guihelper.ART_HELPHELP, wx.ART_TOOLBAR, sz),
                                             shortHelp="BitPim Help", longHelp="BitPim Help")
        self.tb.Realize()
        self.dlggetphone=guiwidgets.GetPhoneDialog(self, "Get Data from Phone")
        self.dlgsendphone=guiwidgets.SendPhoneDialog(self, "Send Data to Phone")
        wx.EVT_MENU(self, guihelper.ID_FILEPRINT, self.OnFilePrint)
        wx.EVT_MENU(self, guihelper.ID_FILEEXIT, self.OnExit)
        wx.EVT_MENU(self, guihelper.ID_EDITSETTINGS, self.OnEditSettings)
        wx.EVT_MENU(self, guihelper.ID_DATAGETPHONE, self.OnDataGetPhone)
        wx.EVT_MENU(self, guihelper.ID_DATASENDPHONE, self.OnDataSendPhone)
        wx.EVT_MENU(self, guihelper.ID_DATAHISTORICAL, self.OnDataHistorical)
        wx.EVT_MENU(self, guihelper.ID_VIEWCOLUMNS, self.OnViewColumns)
        wx.EVT_MENU(self, guihelper.ID_VIEWPREVIEW, self.OnViewPreview)
        wx.EVT_MENU(self, guihelper.ID_VIEWCLEARLOGS, self.OnViewClearLogs)
        wx.EVT_MENU(self, guihelper.ID_VIEWLOGDATA, self.OnViewLogData)
        wx.EVT_MENU(self, guihelper.ID_VIEWFILESYSTEM, self.OnViewFilesystem)
        wx.EVT_MENU(self, guihelper.ID_EDITADDENTRY, self.OnEditAddEntry)
        wx.EVT_MENU(self, guihelper.ID_EDITDELETEENTRY, self.OnEditDeleteEntry)
        wx.EVT_MENU(self, guihelper.ID_EDITSELECTALL, self.OnEditSelectAll)
        wx.EVT_MENU(self, guihelper.ID_EDITCOPY, self.OnCopyEntry)
        wx.EVT_MENU(self, guihelper.ID_EDITPASTE, self.OnPasteEntry)
        wx.EVT_MENU(self, guihelper.ID_EDITRENAME, self.OnRenameEntry)
        wx.EVT_MENU(self, guihelper.ID_HELPABOUT, self.OnHelpAbout)
        wx.EVT_MENU(self, guihelper.ID_HELPHELP, self.OnHelpHelp)
        wx.EVT_MENU(self, guihelper.ID_HELPCONTENTS, self.OnHelpContents)
        wx.EVT_MENU(self, guihelper.ID_HELPSUPPORT, self.OnHelpSupport)
        wx.EVT_MENU(self, guihelper.ID_HELPTOUR, self.OnHelpTour)
        wx.EVT_MENU(self, guihelper.ID_HELP_UPDATE, self.OnCheckUpdate)
        wx.EVT_MENU(self, guihelper.ID_EDITPHONEINFO, self.OnPhoneInfo)
        wx.EVT_MENU(self, guihelper.ID_EDITDETECT, self.OnDetectPhone)
        wx.EVT_MENU(self, guihelper.ID_AUTOSYNCSETTINGS, self.OnAutoSyncSettings)
        wx.EVT_MENU(self, guihelper.ID_AUTOSYNCEXECUTE, self.OnAutoSyncExecute)
        wx.EVT_CLOSE(self, self.OnClose)
        wx.EVT_UPDATE_UI(self, guihelper.ID_AUTOSYNCEXECUTE, self.AutosyncUpdateUIEvent)
        wx.EVT_UPDATE_UI(self, guihelper.ID_DATASENDPHONE, self.DataSendPhoneUpdateUIEvent)
        wx.EVT_UPDATE_UI(self, guihelper.ID_EDITDELETEENTRY, self.DataDeleteItemUpdateUIEvent)
        wx.EVT_UPDATE_UI(self, guihelper.ID_EDITADDENTRY, self.DataAddItemUpdateUIEvent)
        wx.EVT_UPDATE_UI(self, guihelper.ID_DATAHISTORICAL, self.HistoricalDataUpdateUIEvent)
        wx.EVT_UPDATE_UI(self, guihelper.ID_VIEWCOLUMNS, self.ViewColumnsandPreviewDataUpdateUIEvent)
        wx.EVT_UPDATE_UI(self, guihelper.ID_VIEWPREVIEW, self.ViewColumnsandPreviewDataUpdateUIEvent)
        wx.EVT_UPDATE_UI(self, guihelper.ID_FILEPRINT, self.FilePrintDataUpdateUIEvent)
        wx.EVT_UPDATE_UI(self, guihelper.ID_EDITSELECTALL, self.SelectAllDataUpdateUIEvent)
        wx.EVT_UPDATE_UI(self, guihelper.ID_EDITCOPY, self.EditCopyUpdateUIEvent)
        wx.EVT_UPDATE_UI(self, guihelper.ID_EDITPASTE, self.EditPasteUpdateUIEvent)
        wx.EVT_UPDATE_UI(self, guihelper.ID_EDITRENAME, self.EditRenameUpdateUIEvent)
        if min(self.GetSize())<250:
            self.SetSize( (640, 480) )
        self.configdlg=guiwidgets.ConfigDialog(self, self)
        self.needconfig=self.configdlg.needconfig()
        self.configdlg.updatevariables()
        self.calenders=importexport.GetCalenderAutoSyncImports()
        print "cal " +`self.calenders`
        self.autosyncsetting=auto_sync.AutoSyncSettingsDialog(self, self)
        self.autosyncsetting.updatevariables()
        self.nb=wx.Notebook(self,-1, style=wx.NO_FULL_REPAINT_ON_RESIZE|wx.CLIP_CHILDREN)
        if self.config.ReadInt("console", 0):
            import developer
            self.nb.AddPage(developer.DeveloperPanel(self.nb, {'mw': self, 'db': self.database} ), "Console")
        self.widget=self.todaywidget=today.TodayWidget(self, self.nb)
        self.nb.AddPage(self.todaywidget, "Today")
        self.phonewidget=phonebook.PhoneWidget(self, self.nb, self.config)
        self.nb.AddPage(self.phonewidget, "PhoneBook")
        self.wallpaperwidget=wallpaper.WallpaperView(self, self.nb)
        self.nb.AddPage(self.wallpaperwidget, "Wallpaper")
        self.ringerwidget=ringers.RingerView(self, self.nb)
        self.nb.AddPage(self.ringerwidget, "Ringers")
        self.calendarwidget=bpcalendar.Calendar(self, self.nb)
        self.nb.AddPage(self.calendarwidget, "Calendar")
        self.memowidget=memo.MemoWidget(self, self.nb)
        self.nb.AddPage(self.memowidget, "Memo")
        self.todowidget=todo.TodoWidget(self, self.nb)
        self.nb.AddPage(self.todowidget, 'Todo')
        self.smswidget=sms_tab.SMSWidget(self, self.nb)
        self.nb.AddPage(self.smswidget, 'SMS')
        self.callhistorywidget=call_history.CallHistoryWidget(self, self.nb)
        self.nb.AddPage(self.callhistorywidget, 'Call History')
        self.playlistwidget=playlist.PlaylistWidget(self, self.nb)
        self.nb.AddPage(self.playlistwidget, 'Play List')
        self.lw=guiwidgets.LogWindow(self.nb)
        self.nb.AddPage(self.lw, "Log")
        lv=self.config.ReadInt("viewlogdata", 0)
        if lv:
            menuBar.Check(guihelper.ID_VIEWLOGDATA, 1)
            self.OnViewLogData(None)
        fv=self.config.ReadInt("viewfilesystem", 0)
        if fv:
            menuBar.Check(guihelper.ID_VIEWFILESYSTEM, 1)
            self.OnViewFilesystem(None)
            wx.Yield()
        if self.config.ReadInt("viewpreview", 1):
            menuBar.Check(guihelper.ID_VIEWPREVIEW, 1)
        else:
            self.phonewidget.OnViewPreview(False)
        self.SetPhoneModelStatus()
        self.SetVersionsStatus()
        wx.EVT_NOTEBOOK_PAGE_CHANGED(self, -1, self.OnNotebookPageChanged)
        if self.config.ReadInt('startwithtoday', 0):
            pg='Today'
        else:
            pg=self.config.Read("viewnotebookpage", "")
        sel=0
        if len(pg):
            for i in range(self.nb.GetPageCount()):
                if pg==self.nb.GetPageText(i):
                    sel=i
                    break
        if sel==self.nb.GetSelection():
            self.OnNotebookPageChanged()
        else:
            self.nb.SetSelection(sel)
        guiwidgets.set_size("MainWin", self, screenpct=90)
        self.Show()
        if self.config.ReadInt("firstrun", True):
            self.config.WriteInt("firstrun", False)
            self.config.Flush()
            wx.CallAfter(self.OnHelpTour)
        wx.CallAfter(self.OnPopulateEverythingFromDisk)
        if guihelper.IsMSWindows():
            if self.config.ReadInt('taskbaricon', 0):
                self._taskbar=TaskBarIcon(self)
            self.oldwndproc = win32gui.SetWindowLong(self.GetHandle(),
                                                     win32con.GWL_WNDPROC,
                                                     self.MyWndProc)
        if self._taskbar:
            wx.EVT_ICONIZE(self, self.OnIconize)
        pubsub.subscribe(self.OnReqChangeTab, pubsub.REQUEST_TAB_CHANGED)
        self._setup_midnight_timer()
    def AutosyncUpdateUIEvent(self, event):
        event.Enable(self.autosyncsetting.IsConfigured())
    def DataSendPhoneUpdateUIEvent(self, event):
        event.Enable(not wx.GetApp().SAFEMODE)
    def EditCopyUpdateUIEvent(self, event):
        enable_copy=hasattr(self.widget, "OnCopy") and \
                     hasattr(self.widget, "CanCopy") and \
                     self.widget.CanCopy()
        event.Enable(enable_copy)
    def EditPasteUpdateUIEvent(self, event):
        enable_paste=hasattr(self.widget, "OnPaste") and \
                      hasattr(self.widget, "CanPaste") and \
                      self.widget.CanPaste()
        event.Enable(enable_paste)
    def EditRenameUpdateUIEvent(self, event):
        enable_rename=hasattr(self.widget, "OnRename") and \
                       hasattr(self.widget, "CanRename") and \
                       self.widget.CanRename()
        event.Enable(enable_rename)
    def DataDeleteItemUpdateUIEvent(self, event):
        enable_del=hasattr(self.widget, "OnDelete")
        event.Enable(enable_del)
    def DataAddItemUpdateUIEvent(self, event):
        enable_add=hasattr(self.widget, "OnAdd")
        event.Enable(enable_add)
    def HistoricalDataUpdateUIEvent(self, event):
        enable_historical_data=hasattr(self.widget, 'OnHistoricalData')
        event.Enable(enable_historical_data)
    def ViewColumnsandPreviewDataUpdateUIEvent(self, event):
        is_phone_widget=self.widget is self.phonewidget
        event.Enable(is_phone_widget)
    def FilePrintDataUpdateUIEvent(self, event):
        enable_print=hasattr(self.widget, "OnPrintDialog")
        event.Enable(enable_print)
    def SelectAllDataUpdateUIEvent(self, event):
        enable_select_all=hasattr(self.widget, "OnSelectAll")
        event.Enable(enable_select_all)
    def CloseSplashScreen(self):
        global thesplashscreen
        if thesplashscreen is not None:
            try:
                thesplashscreen.Show(False)
            except:
                pass
            thesplashscreen=None
            wx.SafeYield(onlyIfNeeded=True)
    def OnExit(self,_=None):
        self.Close()
    def OnClose(self, event):
        if not self.IsIconized():
            self.saveSize()
        if not self.wt:
            self.Destroy()
            return
        if event.CanVeto():
            pass # yup close for now
        self.MakeCall( Request(self.wt.exit), Callback(self.OnCloseResults) )
    def OnCloseResults(self, exception, _):
        assert isinstance(exception, SystemExit)
        if self._taskbar:
            self._taskbar.Destroy()
        self.Destroy()
        wx.GetApp().ExitMainLoop()
    def OnIconize(self, evt):
        if evt.Iconized():
            self.Show(False)
        else:
            self.Show(True)
    def OnHelpAbout(self,_):
        import version
        str="BitPim Version "+version.versionstring+" - "+version.vendor
        d=wx.MessageDialog(self, str, "About BitPim", wx.OK|wx.ICON_INFORMATION)
        d.ShowModal()
        d.Destroy()
    def OnHelpHelp(self, _):
        text=re.sub("[^A-Za-z]", "", self.nb.GetPageText(self.nb.GetSelection()))
        wx.GetApp().displayhelpid(getattr(helpids, "ID_TAB_"+text.upper()))
    def OnHelpContents(self, _):
        wx.GetApp().displayhelpid(None)
    def OnHelpSupport(self, _):
        wx.GetApp().displayhelpid(helpids.ID_HELPSUPPORT)
    def OnHelpTour(self, _=None):
        wx.GetApp().displayhelpid(helpids.ID_TOUR)
    def DoCheckUpdate(self):
        s=update.check_update()
        if not len(s):
            return
        self.config.Write('latest_version', s)
        self.config.Write('last_update',
                          time.strftime('%Y%m%d', time.localtime()))
        self.SetVersionsStatus()
    def OnCheckUpdate(self, _):
        self.DoCheckUpdate()
    def SetPhoneModelStatus(self):
        phone=self.config.Read('phonetype', 'None')
        port=self.config.Read('lgvx4400port', 'None')
        self.GetStatusBar().set_phone_model(
            '%s %s/%s'%(self.__owner_name, phone, port))
    def OnPhoneInfo(self, _):
        self.MakeCall(Request(self.wt.getphoneinfo),
                      Callback(self.OnDisplayPhoneInfo))
    def OnDisplayPhoneInfo(self, exception, phone_info):
        if self.HandleException(exception): return
        if phone_info is None:
            dlg=wx.MessageDialog(self, "Phone Info not available",
                             "Phone Info Error", style=wx.OK)
        else:
            dlg=phoneinfo.PhoneInfoDialog(self, phone_info)
        dlg.ShowModal()
        dlg.Destroy()
    def OnDetectPhone(self, _=None):
        if wx.IsBusy():
            self.queue.put((self.OnDetectPhone, (), {}), False)
            return
        self.__detect_phone()
    def __detect_phone(self, using_port=None, check_auto_sync=0, delay=0):
        self.OnBusyStart()
        self.GetStatusBar().progressminor(0, 100, 'Phone detection in progress ...')
        self.MakeCall(Request(self.wt.detectphone, using_port, delay),
                      Callback(self.OnDetectPhoneReturn, check_auto_sync))
    def __get_owner_name(self, esn, style=wx.DEFAULT_DIALOG_STYLE):
        """ retrieve or ask user for the owner's name of this phone
        """
        if esn is None or not len(esn):
            return None
        phone_id='phones/'+sha.new(esn).hexdigest()
        phone_name=self.config.Read(phone_id, '<None/>')
        s=None
        if phone_name=='<None/>':
            dlg=guiwidgets.AskPhoneNameDialog(
                self, 'A new phone has been detected,\n'
                "Would you like to enter the owner's name:", style=style)
            r=dlg.ShowModal()
            if r==wx.ID_OK:
                s=dlg.GetValue()
            elif r==wx.ID_CANCEL:
                s=''
            if s is not None:
                self.config.Write(phone_id, s)
            dlg.Destroy()
            return s
        return phone_name
    def OnDetectPhoneReturn(self, check_auto_sync, exception, r):
        self._autodetect_delay=0
        self.OnBusyEnd()
        if self.HandleException(exception): return
        if r is None:
            self.__owner_name=''
            _dlg=wx.MessageDialog(self, 'No phone detected/recognized.\nRun Settings?',
                                  'Phone Detection Failed', wx.YES_NO)
            if _dlg.ShowModal()==wx.ID_YES:
                wx.CallAfter(self.OnEditSettings)
            _dlg.Destroy()
        else:
            self.__owner_name=self.__get_owner_name(r.get('phone_esn', None))
            if self.__owner_name is None:
                self.__owner_name=''
            else:
                self.__owner_name+="'s"
            self.config.Write("phonetype", r['phone_name'])
            self.commportsetting=str(r['port'])
            self.wt.clearcomm()
            self.config.Write("lgvx4400port", r['port'])
            self.phonemodule=common.importas(r['phone_module'])
            self.phoneprofile=self.phonemodule.Profile()
            pubsub.publish(pubsub.PHONE_MODEL_CHANGED, self.phonemodule)
            self.SetPhoneModelStatus()
            wx.MessageBox('Found %s %s on %s'%(self.__owner_name,
                                               r['phone_name'],
                                               r['port']),
                          'Phone Detection', wx.OK)
            if check_auto_sync:
                self.__autosync_phone(silent=1)
    def WindowsOnDeviceChanged(self, type, name="", drives=[], flag=None):
        if not name.lower().startswith("com"):
            return
        if type=='DBT_DEVICEREMOVECOMPLETE':
            print "Device remove", name
            if name==self.config.Read('lgvx4400port', '') and \
               self.wt is not None:
                self.wt.clearcomm()
            return
        if type!='DBT_DEVICEARRIVAL':
            return
        print 'New device on port:',name
        if wx.IsBusy():
            return
        check_auto_sync=auto_sync.UpdateOnConnect(self)
        self.__detect_phone(name, check_auto_sync, self._autodetect_delay)
    def MyWndProc(self, hwnd, msg, wparam, lparam):
        if msg==win32con.WM_DEVICECHANGE:
            type,params=DeviceChanged(wparam, lparam).GetEventInfo()
            self.OnDeviceChanged(type, **params)
            return True
        if msg == win32con.WM_DESTROY:
            win32api.SetWindowLong(self.GetHandle(),
                                   win32con.GWL_WNDPROC,
                                   self.oldwndproc)
        return win32gui.CallWindowProc(self.oldwndproc,
                                       hwnd, msg, wparam, lparam)
    if guihelper.IsMSWindows():
        OnDeviceChanged=WindowsOnDeviceChanged
    def SetVersionsStatus(self):
        current_v=version.version
        latest_v=self.config.Read('latest_version')
        self.GetStatusBar().set_versions(current_v, latest_v)
    def OnViewColumns(self, _):
        dlg=phonebook.ColumnSelectorDialog(self, self.config, self.phonewidget)
        dlg.ShowModal()
        dlg.Destroy()
    def OnViewPreview(self, evt):
        if evt.IsChecked():
            config=1
            preview_on=True
        else:
            config=0
            preview_on=False
        self.config.WriteInt('viewpreview', config)
        self.phonewidget.OnViewPreview(preview_on)
    def OnViewLogData(self, _):
        logdatatitle="Protocol Log"
        if self.lwdata is None:
            self.lwdata=guiwidgets.LogWindow(self.nb)
            self.nb.AddPage(self.lwdata, logdatatitle)
            self.config.WriteInt("viewlogdata", 1)
        else:
            self.lwdata=None
            for i in range(0,self.nb.GetPageCount()):
                if self.nb.GetPageText(i)==logdatatitle:
                    self.nb.DeletePage(i)
                    break
            self.config.WriteInt("viewlogdata", 0)
    def OnViewFilesystem(self,_):
        logtitle="Log"
        fstitle="Filesystem"
        if self.filesystemwidget is None:
            for i in range(0, self.nb.GetPageCount()):
                if self.nb.GetPageText(i)==logtitle:
                    self.filesystemwidget=FileSystemView(self, self.nb, id=97)
                    self.nb.InsertPage(i, self.filesystemwidget, fstitle, True)
                    self.config.WriteInt("viewfilesystem", True)
                    return
            assert False, "log page is missing!"
            return
        self.filesystemwidget=None
        for i in range(0, self.nb.GetPageCount()):
            if self.nb.GetPageText(i)==fstitle:
                self.nb.DeletePage(i)
                self.config.WriteInt("viewfilesystem", False)
                return
        assert False, "filesytem view page is missing!"
    def update_cache_path(self):
        com_brew.file_cache.set_path(self.configpath)
    def OnFilePrint(self,_):
        self.nb.GetPage(self.nb.GetSelection()).OnPrintDialog(self, self.config)
    def OnDataHistorical(self, _):
        self.nb.GetPage(self.nb.GetSelection()).OnHistoricalData()
    def OnDataGetPhone(self,_):
        todo=[]
        dlg=self.dlggetphone
        dlg.UpdateWithProfile(self.phoneprofile)
        if dlg.ShowModal()!=wx.ID_OK:
            return
        self._autodetect_delay=self.phoneprofile.autodetect_delay
        todo.append((self.wt.rebootcheck, "Phone Reboot"))
        self.MakeCall(Request(self.wt.getdata, dlg, todo),
                      Callback(self.OnDataGetPhoneResults))
    def OnDataGetPhoneResults(self, exception, results):
        if self.HandleException(exception): return
        self.OnLog(`results.keys()`)
        self.OnLog(`results['sync']`)
        if results['sync'].has_key('phonebook'):
            v=results['sync']['phonebook']
            print "phonebookmergesetting is",v
            if v=='MERGE': 
                merge=True
            else:
                merge=False
            self.phonewidget.importdata(results['phonebook'], results.get('categories', []), merge)
        updwp=False # did we update the wallpaper
        if results['sync'].has_key('wallpaper'):
            v=results['sync']['wallpaper']
            if v=='MERGE': raise Exception("Not implemented")
            updwp=True
            self.wallpaperwidget.populatefs(results)
            self.wallpaperwidget.populate(results)
        if not updwp and results.has_key('wallpaper-index'):
            self.wallpaperwidget.updateindex(results['wallpaper-index'])
        updrng=False # did we update ringtones
        if results['sync'].has_key('ringtone'):
            v=results['sync']['ringtone']
            if v=='MERGE': raise Exception("Not implemented")
            updrng=True
            self.ringerwidget.populatefs(results)
            self.ringerwidget.populate(results)
        if not updrng and results.has_key('ringtone-index'):
            self.ringerwidget.updateindex(results['ringtone-index'])            
        if results['sync'].has_key('calendar'):
            v=results['sync']['calendar']
            if v=='MERGE': raise Exception("Not implemented")
            results['calendar_version']=self.phoneprofile.BP_Calendar_Version
            self.calendarwidget.populatefs(results)
            self.calendarwidget.populate(results)
        if results['sync'].has_key('memo'):
            v=results['sync']['memo']
            if v=='MERGE': raise Exception("Not implemented")
            self.memowidget.populatefs(results)
            self.memowidget.populate(results)
        if results['sync'].has_key('todo'):
            v=results['sync']['todo']
            if v=='MERGE': raise NotImplementedError
            self.todowidget.populatefs(results)
            self.todowidget.populate(results)
        if results['sync'].has_key('sms'):
            v=results['sync']['sms']
            if v=='MERGE':
                self.smswidget.merge(results)
            else:
                self.smswidget.populatefs(results)
                self.smswidget.populate(results)
        if results['sync'].has_key('call_history'):
            v=results['sync']['call_history']
            if v=='MERGE':
                self.callhistorywidget.merge(results)
            else:
                self.callhistorywidget.populatefs(results)
                self.callhistorywidget.populate(results)
        if results['sync'].has_key(playlist.playlist_key):
            if results['sync'][playlist.playlist_key]=='MERGE':
                raise NotImplementedError
            self.playlistwidget.populatefs(results)
            self.playlistwidget.populate(results)
    def OnDataSendPhone(self, _):
        dlg=self.dlgsendphone
        print self.phoneprofile
        dlg.UpdateWithProfile(self.phoneprofile)
        if dlg.ShowModal()!=wx.ID_OK:
            return
        data={}
        convertors=[]
        todo=[]
        funcscb=[]
        v=dlg.GetWallpaperSetting()
        if v!=dlg.NOTREQUESTED:
            merge=True
            if v==dlg.OVERWRITE: merge=False
            if merge:
                want=self.wallpaperwidget.SELECTED
            else:
                want=self.wallpaperwidget.ALL
            self.wallpaperwidget.getdata(data, want)
            todo.append( (self.wt.writewallpaper, "Wallpaper", merge) )
        v=dlg.GetRingtoneSetting()
        if v!=dlg.NOTREQUESTED:
            merge=True
            if v==dlg.OVERWRITE: merge=False
            if merge:
                want=self.ringerwidget.SELECTED
            else:
                want=self.ringerwidget.ALL
            self.ringerwidget.getdata(data, want)
            todo.append( (self.wt.writeringtone, "Ringtone", merge) )
        v=dlg.GetCalendarSetting()
        if v!=dlg.NOTREQUESTED:
            merge=True
            if v==dlg.OVERWRITE: merge=False
            data['calendar_version']=self.phoneprofile.BP_Calendar_Version
            self.calendarwidget.getdata(data)
            todo.append( (self.wt.writecalendar, "Calendar", merge) )
        v=dlg.GetPhoneBookSetting()
        if v!=dlg.NOTREQUESTED:
            if v==dlg.OVERWRITE: 
                self.phonewidget.getdata(data)
                todo.append( (self.wt.writephonebook, "Phonebook") )
            convertors.append(self.phonewidget.converttophone)
            funcscb.append(self.phonewidget.updateserials)
        v=dlg.GetMemoSetting()
        if v!=dlg.NOTREQUESTED:
            merge=v!=dlg.OVERWRITE
            self.memowidget.getdata(data)
            todo.append((self.wt.writememo, "Memo", merge))
        v=dlg.GetTodoSetting()
        if v!=dlg.NOTREQUESTED:
            merge=v!=dlg.OVERWRITE
            self.todowidget.getdata(data)
            todo.append((self.wt.writetodo, "Todo", merge))
        v=dlg.GetSMSSetting()
        if v!=dlg.NOTREQUESTED:
            merge=v!=dlg.OVERWRITE
            self.smswidget.getdata(data)
            todo.append((self.wt.writesms, "SMS", merge))
        v=dlg.GetPlaylistSetting()
        if v!=dlg.NOTREQUESTED:
            merge=v!=dlg.OVERWRITE
            self.playlistwidget.getdata(data)
            todo.append((self.wt.writeplaylist, "Playlist", merge))
        self._autodetect_delay=self.phoneprofile.autodetect_delay
        todo.append((self.wt.rebootcheck, "Phone Reboot"))
        self.MakeCall(Request(self.wt.getfundamentals),
                      Callback(self.OnDataSendPhoneGotFundamentals, data, todo, convertors, funcscb))
    def OnDataSendPhoneGotFundamentals(self,data,todo,convertors, funcscb, exception, results):
        if self.HandleException(exception): return
        data.update(results)
        for f in convertors:
            f(data)
        self.MakeCall(Request(self.wt.senddata, data, todo),
                      Callback(self.OnDataSendPhoneResults, funcscb))
    def OnDataSendPhoneResults(self, funcscb, exception, results):
        if self.HandleException(exception): return
        print results.keys()
        for f in funcscb:
            f(results)
    def GetCalendarData(self):
        d={}
        return self.calendarwidget.getdata(d).get('calendar', {})
    def OnPopulateEverythingFromDisk(self,_=None):
        self.OnBusyStart()
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
            self.CloseSplashScreen()
        finally:
            self.OnBusyEnd()
    def OnEditSettings(self, _=None):
        if wx.IsBusy():
            wx.MessageBox("BitPim is busy.  You can't change settings until it has finished talking to your phone.",
                         "BitPim is busy.", wx.OK|wx.ICON_EXCLAMATION)
        else:
            self.__owner_name=''
            self.configdlg.ShowModal()
    def OnAutoSyncSettings(self, _=None):
        if wx.IsBusy():
            wx.MessageBox("BitPim is busy.  You can't change settings until it has finished talking to your phone.",
                         "BitPim is busy.", wx.OK|wx.ICON_EXCLAMATION)
        else:
            self.__owner_name=''
            self.autosyncsetting.ShowModal()
    def OnAutoSyncExecute(self, _=None):
        if wx.IsBusy():
            wx.MessageBox("BitPim is busy.  You can't run autosync until it has finished talking to your phone.",
                         "BitPim is busy.", wx.OK|wx.ICON_EXCLAMATION)
            return
        self.__autosync_phone()
    def __autosync_phone(self, silent=0):
        r=auto_sync.SyncSchedule(self).sync(self, silent)
    def OnReqChangeTab(self, msg=None):
        if msg is None:
            return
        data=msg.data
        if not isinstance(data, int):
            if __debug__:
                raise TypeError
            return
        self.nb.SetSelection(data)
    def OnNotebookPageChanged(self, _=None):
        text=self.nb.GetPageText(self.nb.GetSelection())
        if text is not None:
            self.config.Write("viewnotebookpage", text)
        self.widget=self.nb.GetPage(self.nb.GetSelection())
        self.widget.SetFocus()
        sz=self.tb.GetToolBitmapSize()
        mapbmpadd={id(self.ringerwidget): guihelper.ART_ADD_RINGER,
                   id(self.wallpaperwidget): guihelper.ART_ADD_WALLPAPER,
                   id(self.phonewidget): guihelper.ART_ADD_CONTACT,
                   id(self.memowidget): guihelper.ART_ADD_MEMO,
                   id(self.todowidget): guihelper.ART_ADD_TODO,
                   id(self.smswidget): guihelper.ART_ADD_SMS,
                   id(self.playlistwidget): guihelper.ART_ADD_TODO,
                   }
        mapbmpdelete={id(self.ringerwidget): guihelper.ART_DEL_RINGER,
                      id(self.wallpaperwidget): guihelper.ART_DEL_WALLPAPER,
                      id(self.phonewidget): guihelper.ART_DEL_CONTACT,
                      id(self.memowidget): guihelper.ART_DEL_MEMO,
                      id(self.todowidget): guihelper.ART_DEL_TODO,
                      id(self.smswidget): guihelper.ART_DEL_SMS,
                      id(self.playlistwidget): guihelper.ART_DEL_TODO,
                      }
        shorthelp={id(self.ringerwidget): ("Add Ringer", "Delete Ringer"),
                   id(self.wallpaperwidget): ("Add Wallpaper","Delete Wallpaper"),
                   id(self.phonewidget): ("Add Contact","Delete Contact"),
                   id(self.memowidget): ("Add Memo","Delete Memo"),
                   id(self.todowidget): ("Add Todo Item","Delete Todo Item"),
                   id(self.smswidget): ("Add SMS","Delete SMS"),
                   id(self.playlistwidget): ("Add Playlist","Delete Playlist"),
                   id(self.callhistorywidget): ("Add","Delete Call"),
                   }
        if id(self.widget) in shorthelp:
            short_help_add, short_help_delete=shorthelp[id(self.widget)]
        else:
            short_help_add, short_help_delete=("Add", "Delete")
        pos=self.GetToolBar().GetToolPos(guihelper.ID_EDITADDENTRY)
        self.GetToolBar().DeleteTool(guihelper.ID_EDITADDENTRY)
        self.tooladd=self.tb.InsertLabelTool(pos, guihelper.ID_EDITADDENTRY, short_help_add, 
                                             wx.ArtProvider.GetBitmap(mapbmpadd.get(id(self.widget), wx.ART_ADD_BOOKMARK), wx.ART_TOOLBAR, sz),
                                             shortHelp=short_help_add, longHelp="Add an item")
        pos=self.GetToolBar().GetToolPos(guihelper.ID_EDITDELETEENTRY)
        self.GetToolBar().DeleteTool(guihelper.ID_EDITDELETEENTRY)
        self.tooldelete=self.tb.InsertLabelTool(pos, guihelper.ID_EDITDELETEENTRY, short_help_delete, 
                                                wx.ArtProvider.GetBitmap(mapbmpdelete.get(id(self.widget), wx.ART_DEL_BOOKMARK), wx.ART_TOOLBAR, sz),
                                                shortHelp=short_help_delete, longHelp="Delete item")
        self.tb.Realize()
    def OnEditAddEntry(self, evt):
        self.nb.GetPage(self.nb.GetSelection()).OnAdd(evt)
    def OnEditDeleteEntry(self, evt):
        self.nb.GetPage(self.nb.GetSelection()).OnDelete(evt)
    def OnEditSelectAll(self, evt):
        self.nb.GetPage(self.nb.GetSelection()).OnSelectAll(evt)
    def OnCopyEntry(self, evt):
        self.nb.GetPage(self.nb.GetSelection()).OnCopy(evt)
    def OnPasteEntry(self, evt):
        self.nb.GetPage(self.nb.GetSelection()).OnPaste(evt)
    def OnRenameEntry(self, evt):
        self.nb.GetPage(self.nb.GetSelection()).OnRename(evt)
    def OnBusyStart(self):
        self.GetStatusBar().set_app_status("BUSY")
        wx.BeginBusyCursor(wx.StockCursor(wx.CURSOR_ARROWWAIT))
    def OnBusyEnd(self):
        wx.EndBusyCursor()
        self.GetStatusBar().set_app_status("Ready")
        self.OnProgressMajor(0,1)
        if not self.queue.empty():
            _q=self.queue.get(False)
            wx.CallAfter(_q[0], *_q[1], **_q[2])
    def OnViewClearLogs(self, _):
        self.lw.Clear()
        if self.lwdata is not None:
            self.lwdata.Clear()
    def OnProgressMinor(self, pos, max, desc=""):
        self.GetStatusBar().progressminor(pos, max, desc)
    def OnProgressMajor(self, pos, max, desc=""):
        self.GetStatusBar().progressmajor(pos, max, desc)
    def OnLog(self, str):
        if self.__phone_detect_at_startup:
            return
        str=common.strorunicode(str)
        self.lw.log(str)
        if self.lwdata is not None:
            self.lwdata.log(str)
        if str.startswith("<!= "):
            p=str.index("=!>")+3
            dlg=wx.MessageDialog(self, str[p:], "Alert", style=wx.OK|wx.ICON_EXCLAMATION)
            dlg.ShowModal()
            dlg.Destroy()
            self.OnLog("Alert dialog closed")
    log=OnLog
    def OnLogData(self, str, data, klass=None):
        if self.lwdata is not None:
            self.lwdata.logdata(str,data, klass)
    def excepthook(self, type, value, traceback):
        if not hasattr(value, "gui_exc_info"):
            value.gui_exc_info=(type,value,traceback)
        self.HandleException(value)
    def HandleException(self, exception):
        """returns true if this function handled the exception
        and the caller should not do any further processing"""
        if exception is None: return False
        assert isinstance(exception, Exception)
        self.CloseSplashScreen()
        self.wt.clearcomm()
        text=None
        title=None
        style=None
        if isinstance(exception, common.CommsDeviceNeedsAttention):
            text="%s: %s" % (exception.device, exception.message)
            title="Device needs attention - "+exception.device
            style=wx.OK|wx.ICON_INFORMATION
            help=lambda _: wx.GetApp().displayhelpid(helpids.ID_DEVICE_NEEDS_ATTENTION)
        elif isinstance(exception, common.CommsOpenFailure):
            text="%s: %s" % (exception.device, exception.message)
            title="Failed to open communications - "+exception.device
            style=wx.OK|wx.ICON_INFORMATION
            help=lambda _: wx.GetApp().displayhelpid(helpids.ID_FAILED_TO_OPEN_DEVICE)
        elif isinstance(exception, common.AutoPortsFailure):
            text=exception.message
            title="Failed to automatically detect port"
            style=wx.OK|wx.ICON_INFORMATION
            help=lambda _: wx.GetApp().displayhelpid(helpids.ID_FAILED_TO_AUTODETECT_PORT)
        elif isinstance(exception, common.HelperBinaryNotFound) and exception.basename=="pvconv":
            text="The Qualcomm PureVoice converter program (%s) was not found.\nPlease see the help. Directories looked in are:\n\n " +\
                  "\n ".join(exception.paths)
            text=text % (exception.fullname,)
            title="Failed to find PureVoice converter"
            style=wx.OK|wx.ICON_INFORMATION
            help=lambda _: wx.GetApp().displayhelpid(helpids.ID_NO_PVCONV)
        elif isinstance(exception, common.PhoneBookBusyException):
            text="The phonebook is busy on your phone.\nExit back to the main screen and then repeat the operation."
            title="Phonebook busy on phone"
            style=wx.OK|wx.ICON_INFORMATION
            help=lambda _: wx.GetApp().displayhelpid(helpids.ID_PHONEBOOKBUSY)
        elif isinstance(exception, common.IntegrityCheckFailed):
            text="The phonebook on your phone is partially corrupt.  Please read the\nhelp for more details on the cause and fix"
            title="IntegrityCheckFailed"
            style=wx.OK|wx.ICON_EXCLAMATION
            help=lambda _: wx.GetApp().displayhelpid(helpids.ID_LG_INTEGRITYCHECKFAILED)
        elif isinstance(exception, common.CommsDataCorruption):
            text=exception.message+"\nPlease see the help."
            title="Communications Error - "+exception.device
            style=wx.OK|wx.ICON_EXCLAMATION
            help=lambda _: wx.GetApp().displayhelpid(helpids.ID_COMMSDATAERROR)
        if text is not None:
            self.OnLog("Error: "+title+"\n"+text)
            dlg=guiwidgets.AlertDialogWithHelp(self,text, title, help, style=style)
            dlg.ShowModal()
            dlg.Destroy()
            return True
        if self.exceptiondialog is None:
            self.excepttime=time.time()
            self.exceptcount=0
            self.exceptiondialog=guiwidgets.ExceptionDialog(self, exception)
            try:
                self.OnLog("Exception: "+self.exceptiondialog.getexceptiontext())
            except AttributeError:
                pass
        else:
            self.exceptcount+=1
            if self.exceptcount<10:
                print "Ignoring an exception as the exception dialog is already up"
                self.OnLog("Exception during exception swallowed")
            return True
        self.exceptiondialog.ShowModal()
        self.exceptiondialog.Destroy()
        self.exceptiondialog=None
        return True
    def _OnTimer(self, _):
        self.MakeCall(Request(self._pub_timer),
                      Callback(self._OnTimerReturn))
    def _pub_timer(self):
        pubsub.publish(pubsub.MIDNIGHT)
    def _OnTimerReturn(self, exceptions, result):
        self._timer.Start(((3600*24)+1)*1000, True)
    def _setup_midnight_timer(self):
        _today=datetime.datetime.now()
        _timer_val=24*3600-_today.hour*3600-_today.minute*60-_today.second+1
        self._timer=wx.Timer(self)
        wx.EVT_TIMER(self, self._timer.GetId(), self._OnTimer)
        self._timer.Start(_timer_val*1000, True)
        print _timer_val,'seconds till midnight'
    def OnCallback(self, event):
        assert isinstance(event, HelperReturnEvent)
        event()
    def MakeCall(self, request, cbresult):
        assert isinstance(request, Request)
        assert isinstance(cbresult, Callback)
        self.wt.q.put( (request, cbresult) )
    def saveSize(self):
        guiwidgets.save_size("MainWin", self.GetRect())
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
class WorkerThread(WorkerThreadFramework):
    def __init__(self):
        WorkerThreadFramework.__init__(self)
        self.commphone=None
    def exit(self):
        if __debug__: self.checkthread()
        for i in range(0,0):
            self.progressmajor(i, 2, "Shutting down helper thread")
            time.sleep(1)
        self.log("helper thread shut down")
        raise SystemExit("helper thread shutdown")
    def clearcomm(self):
        if self.commphone is None:
            return
        self.commphone.close()
        self.commphone=None
    def setupcomm(self):
        if __debug__: self.checkthread()
        if self.commphone is None:
            import commport
            if self.dispatchto.commportsetting is None or \
               len(self.dispatchto.commportsetting)==0:
                raise common.CommsNeedConfiguring("Comm port not configured", "DEVICE")
            if self.dispatchto.commportsetting=="auto":
                autofunc=comdiagnose.autoguessports
            else:
                autofunc=None
            comcfg=self.dispatchto.commparams
            name=self.dispatchto.commportsetting
            if name.startswith("bitfling::"):
                klass=bitflingscan.CommConnection
            else:
                klass=commport.CommConnection
            comport=klass(self, self.dispatchto.commportsetting, autolistfunc=autofunc,
                          autolistargs=(self.dispatchto.phonemodule,),
                          baud=comcfg['baud'], timeout=comcfg['timeout'],
                          hardwareflow=comcfg['hardwareflow'],
                          softwareflow=comcfg['softwareflow'],
                          configparameters=comcfg)
            try:
                self.commphone=self.dispatchto.phonemodule.Phone(self, comport)
            except:
                comport.close()
                raise
    def getfundamentals(self):
        if __debug__: self.checkthread()
        self.setupcomm()
        results={}
        self.commphone.getfundamentals(results)
        return results
    def getdata(self, req, todo):
        if __debug__: self.checkthread()
        self.setupcomm()
        results=self.getfundamentals()
        com_brew.file_cache.esn=results.get('uniqueserial', None)
        willcall=[]
        sync={}
        for i in (
            (req.GetPhoneBookSetting, self.commphone.getphonebook, "Phone Book", "phonebook"),
            (req.GetCalendarSetting, self.commphone.getcalendar, "Calendar", "calendar",),
            (req.GetWallpaperSetting, self.commphone.getwallpapers, "Wallpaper", "wallpaper"),
            (req.GetRingtoneSetting, self.commphone.getringtones, "Ringtones", "ringtone"),
            (req.GetMemoSetting, self.commphone.getmemo, "Memo", "memo"),
            (req.GetTodoSetting, self.commphone.gettodo, "Todo", "todo"),
            (req.GetSMSSetting, self.commphone.getsms, "SMS", "sms"),
            (req.GetCallHistorySetting, self.commphone.getcallhistory, 'Call History', 'call_history'),
            (req.GetPlaylistSetting, self.commphone.getplaylist, 'Play List', 'playlist')):
            st=i[0]()
            if st==req.MERGE:
                sync[i[3]]="MERGE"
                willcall.append(i)
            elif st==req.OVERWRITE:
                sync[i[3]]="OVERWRITE"
                willcall.append(i)
        results['sync']=sync
        count=0
        for i in willcall:
            self.progressmajor(count, len(willcall), i[2])
            count+=1
            i[1](results)
        for xx in todo:
            func=xx[0]
            desc=xx[1]
            args=[results]
            if len(xx)>2:
                args.extend(xx[2:])
            apply(func, args)
        return results
    def senddata(self, dict, todo):
        count=0
        for xx in todo:
            func=xx[0]
            desc=xx[1]
            args=[dict]
            if len(xx)>2:
                args.extend(xx[2:])
            self.progressmajor(count,len(todo),desc)
            apply(func, args)
            count+=1
        return dict
    def writewallpaper(self, data, merge):
        if __debug__: self.checkthread()
        self.setupcomm()
        return self.commphone.savewallpapers(data, merge)
    def writeringtone(self, data, merge):
        if __debug__: self.checkthread()
        self.setupcomm()
        return self.commphone.saveringtones(data, merge)
    def writephonebook(self, data):
        if __debug__: self.checkthread()
        self.setupcomm()
        return self.commphone.savephonebook(data)
    def rebootcheck(self, results):
        if __debug__: self.checkthread()
        if results.has_key('rebootphone'):
            self.log("BitPim is rebooting your phone for changes to take effect")
            self.phonerebootrequest()
            self.clearcomm()
    def writecalendar(self, data, merge):
        if __debug__: self.checkthread()
        self.setupcomm()
        return self.commphone.savecalendar(data, merge)
    def writememo(self, data, merge):
        if __debug__: self.checkthread()
        self.setupcomm()
        return self.commphone.savememo(data, merge)
    def writetodo(self, data, merge):
        if __debug__: self.checkthread()
        self.setupcomm()
        return self.commphone.savetodo(data, merge)
    def writesms(self, data, merge):
        if __debug__: self.checkthread()
        self.setupcomm()
        return self.commphone.savesms(data, merge)
    def writeplaylist(self, data, merge):
        if __debug__: self.checkthread()
        self.setupcomm()
        return self.commphone.saveplaylist(data, merge)
    def getphoneinfo(self):
        if __debug__: self.checkthread()
        self.setupcomm()
        if hasattr(self.commphone, 'getphoneinfo'):
            phone_info=phoneinfo.PhoneInfo()
            getattr(self.commphone, 'getphoneinfo')(phone_info)
            return phone_info
    def detectphone(self, using_port=None, delay=0):
        self.clearcomm()
        print 'detectphone:sleeping',delay
        time.sleep(delay)
        return phone_detect.DetectPhone(self).detect(using_port)
    def dirlisting(self, path, recurse=0):
        if __debug__: self.checkthread()
        self.setupcomm()
        try:
            return self.commphone.getfilesystem(path, recurse)
        except:
            self.log('Failed to read dir: '+path)
            return {}
    def getfileonlylist(self, path):
        if __debug__: self.checkthread()
        self.setupcomm()
        try:
            return self.commphone.listfiles(path)
        except:
            self.log('Failed to read filesystem')
            return {}
    def getdironlylist(self, path, recurse):
        results=self.commphone.listsubdirs(path)
        subdir_list=[x['name'] for k,x in results.items()]
        if recurse:
            for _subdir in subdir_list:
                try:
                    results.update(self.getdironlylist(_subdir, recurse))
                except:
                    self.log('Failed to list directories in ' +_subdir)
        return results
    def fulldirlisting(self):
        if __debug__: self.checkthread()
        self.setupcomm()
        try:
            return self.getdironlylist("", True)
        except:
            self.log('Failed to read filesystem')
            return {}
    def singledirlisting(self, path):
        if __debug__: self.checkthread()
        self.setupcomm()
        try:
            return self.getdironlylist(path, False)
        except:
            self.log('Failed to read filesystem')
            return {}
    def getfile(self, path):
        if __debug__: self.checkthread()
        self.setupcomm()
        return self.commphone.getfilecontents(path)
    def rmfile(self,path):
        if __debug__: self.checkthread()
        self.setupcomm()
        return self.commphone.rmfile(path)
    def writefile(self,path,contents):
        if __debug__: self.checkthread()
        self.setupcomm()
        return self.commphone.writefile(path, contents)
    def mkdir(self,path):
        if __debug__: self.checkthread()
        self.setupcomm()
        return self.commphone.mkdir(path)
    def rmdir(self,path):
        if __debug__: self.checkthread()
        self.setupcomm()
        return self.commphone.rmdir(path)
    def rmdirs(self,path):
        if __debug__: self.checkthread()
        self.setupcomm()
        return self.commphone.rmdirs(path)
    def phonerebootrequest(self):
        if __debug__: self.checkthread()
        self.setupcomm()
        return self.commphone.offlinerequest(reset=True)
    def phoneofflinerequest(self):
        if __debug__: self.checkthread()
        self.setupcomm()
        return self.commphone.offlinerequest()
    def modemmoderequest(self):
        if __debug__: self.checkthread()
        self.setupcomm()
        return self.commphone.modemmoderequest()
    def getbackup(self,path,recurse=0):
        if __debug__: self.checkthread()
        self.setupcomm()
        self.progressmajor(0,0,"Listing files")
        files=self.dirlisting(path, recurse)
        if path=="/" or path=="":
            strip=0 # root dir
        else:
            strip=len(path)+1 # child
        keys=files.keys()
        keys.sort()
        op=cStringIO.StringIO()
        zip=zipfile.ZipFile(op, "w", zipfile.ZIP_DEFLATED)
        count=0
        for k in keys:
            try:
                count+=1
                if files[k]['type']!='file':
                    continue
                self.progressmajor(count, len(keys)+1, "Getting files")
                contents=self.getfile(k)
                time.sleep(0.3)
                zi=zipfile.ZipInfo()
                zi.filename=k[strip:]
                if files[k]['date'][0]==0:
                    zi.date_time=(0,0,0,0,0,0)
                else:
                    zi.date_time=time.gmtime(files[k]['date'][0])[:6]
                zi.compress_type=zipfile.ZIP_DEFLATED
                zip.writestr(zi, contents)
            except:
                self.log('Failed to read file: '+k)
        zip.close()
        return op.getvalue()
    def restorefiles(self, files):
        if __debug__: self.checkthread()
        self.setupcomm()
        results=[]
        seendirs=[]
        count=0
        for name, contents in files:
            self.progressmajor(count, len(files), "Restoring files")
            count+=1
            d=guihelper.dirname(name)
            if d not in seendirs:
                seendirs.append(d)
                self.commphone.mkdirs(d)
            self.writefile(name, contents)
            results.append( (True, name) )
            time.sleep(0.3)
        return results
class FileSystemView(wx.SplitterWindow):
    def __init__(self, mainwindow, parent, id=-1):
        self.mainwindow=mainwindow
        wx.SplitterWindow.__init__(self, parent, id, style=wx.SP_BORDER|wx.SP_LIVE_UPDATE)
        self.tree=FileSystemDirectoryView(mainwindow, self, wx.NewId(), style=(wx.TR_DEFAULT_STYLE|wx.TR_NO_LINES)&~wx.TR_TWIST_BUTTONS)
        self.list=FileSystemFileView(mainwindow, self, wx.NewId())
        pos=mainwindow.config.ReadInt("filesystemsplitterpos", 200)
        self.SplitVertically(self.tree, self.list, pos)
        self.SetMinimumPaneSize(20)
        wx.EVT_SPLITTER_SASH_POS_CHANGED(self, id, self.OnSplitterPosChanged)
        pubsub.subscribe(self.OnPhoneModelChanged, pubsub.PHONE_MODEL_CHANGED)
    def __del__(self):
        pubsub.unsubscribe(self.OnPhoneModelChanged)
    def OnPhoneModelChanged(self, msg):
        self.list.ResetView()
        self.tree.ResetView()
    def OnSplitterPosChanged(self,_):
        pos=self.GetSashPosition()
        self.mainwindow.config.WriteInt("filesystemsplitterpos", pos)        
    def OnPhoneReboot(self,_):
        mw=self.mainwindow
        mw.MakeCall( Request(mw.wt.phonerebootrequest),
                     Callback(self.OnPhoneRebootResults) )
    def OnPhoneRebootResults(self, exception, _):
        mw=self.mainwindow
        mw.wt.clearcomm()
        if mw.HandleException(exception): return
    def OnPhoneOffline(self,_):
        mw=self.mainwindow
        mw.MakeCall( Request(mw.wt.phoneofflinerequest),
                     Callback(self.OnPhoneOfflineResults) )
    def OnPhoneOfflineResults(self, exception, _):
        mw=self.mainwindow
        if mw.HandleException(exception): return
    def OnModemMode(self,_):
        mw=self.mainwindow
        mw.MakeCall( Request(mw.wt.modemmoderequest),
                     Callback(self.OnModemModeResults) )
    def OnModemModeResults(self, exception, _):
        mw=self.mainwindow
        if mw.HandleException(exception): return
    def ShowFiles(self, dir, refresh=False):
        self.list.ShowFiles(dir, refresh)
    def OnNewFileResults(self, parentdir, exception, _):
        mw=self.mainwindow
        if mw.HandleException(exception): return
        self.ShowFiles(parentdir, True)
class FileSystemFileView(wx.ListCtrl, listmix.ColumnSorterMixin):
    def __init__(self, mainwindow, parent, id, style=wx.LC_REPORT|wx.LC_VIRTUAL|wx.LC_SINGLE_SEL):
        wx.ListCtrl.__init__(self, parent, id, style=style)
        self.parent=parent
        self.mainwindow=mainwindow
        self.datacolumn=False # used for debugging and inspection of values
        self.InsertColumn(0, "Name", width=300)
        self.InsertColumn(1, "Size", format=wx.LIST_FORMAT_RIGHT)
        self.InsertColumn(2, "Date", width=200)
        self.font=wx.TheFontList.FindOrCreateFont(10, family=wx.SWISS, style=wx.NORMAL, weight=wx.NORMAL)
        self.ResetView()        
        if self.datacolumn:
            self.InsertColumn(3, "Extra Stuff", width=400)
            listmix.ColumnSorterMixin.__init__(self, 4)
        else:
            listmix.ColumnSorterMixin.__init__(self, 3)
        self.filemenu=wx.Menu()
        self.filemenu.Append(guihelper.ID_FV_SAVE, "Save ...")
        self.filemenu.Append(guihelper.ID_FV_HEXVIEW, "Hexdump")
        self.filemenu.AppendSeparator()
        self.filemenu.Append(guihelper.ID_FV_DELETE, "Delete")
        self.filemenu.Append(guihelper.ID_FV_OVERWRITE, "Overwrite ...")
        self.genericmenu=wx.Menu()
        self.genericmenu.Append(guihelper.ID_FV_NEWFILE, "New File ...")
        self.genericmenu.AppendSeparator()
        self.genericmenu.Append(guihelper.ID_FV_OFFLINEPHONE, "Offline Phone")
        self.genericmenu.Append(guihelper.ID_FV_REBOOTPHONE, "Reboot Phone")
        self.genericmenu.Append(guihelper.ID_FV_MODEMMODE, "Go to modem mode")
        wx.EVT_MENU(self.genericmenu, guihelper.ID_FV_NEWFILE, self.OnNewFile)
        wx.EVT_MENU(self.genericmenu, guihelper.ID_FV_OFFLINEPHONE, parent.OnPhoneOffline)
        wx.EVT_MENU(self.genericmenu, guihelper.ID_FV_REBOOTPHONE, parent.OnPhoneReboot)
        wx.EVT_MENU(self.genericmenu, guihelper.ID_FV_MODEMMODE, parent.OnModemMode)
        wx.EVT_MENU(self.filemenu, guihelper.ID_FV_SAVE, self.OnFileSave)
        wx.EVT_MENU(self.filemenu, guihelper.ID_FV_HEXVIEW, self.OnHexView)
        wx.EVT_MENU(self.filemenu, guihelper.ID_FV_DELETE, self.OnFileDelete)
        wx.EVT_MENU(self.filemenu, guihelper.ID_FV_OVERWRITE, self.OnFileOverwrite)
        wx.EVT_RIGHT_DOWN(self.GetMainWindow(), self.OnRightDown)
        wx.EVT_RIGHT_UP(self.GetMainWindow(), self.OnRightUp)
        wx.EVT_LIST_ITEM_ACTIVATED(self,id, self.OnItemActivated)
        self.image_list=wx.ImageList(16, 16)
        a={"sm_up":"GO_UP","sm_dn":"GO_DOWN","w_idx":"WARNING","e_idx":"ERROR","i_idx":"QUESTION"}
        for k,v in a.items():
            s="self.%s= self.image_list.Add(wx.ArtProvider_GetBitmap(wx.ART_%s,wx.ART_TOOLBAR,(16,16)))" % (k,v)
            exec(s)
        self.img_file=self.image_list.Add(wx.ArtProvider_GetBitmap(wx.ART_NORMAL_FILE,
                                                             wx.ART_OTHER,
                                                             (16, 16)))
        self.SetImageList(self.image_list, wx.IMAGE_LIST_SMALL)
        self.__dragging=False
        self.add_files=[]
        self.droptarget=guiwidgets.MyFileDropTarget(self, True)
        self.SetDropTarget(self.droptarget)
    def OnPaint(self, evt):
        w,h=self.GetSize()
        self.Refresh()
        dc=wx.PaintDC(self)
        dc.BeginDrawing()
        dc.SetFont(self.font)
        x,y= dc.GetTextExtent("There are no items to show in this view")
        xx=(w-x)/2
        if xx<0:
            xx=0
        dc.DrawText("There are no items to show in this view", xx, h/3)
        dc.EndDrawing()
    def OnDropFiles(self, _, dummy, filenames):
        if self.__dragging:
            return
        target=self # fallback
        t=self.mainwindow.nb.GetPage(self.mainwindow.nb.GetSelection())
        if isinstance(t, FileSystemFileView):
            target=t
        self.add_files=filenames
        target.OnAddFiles()
    def OnDragOver(self, x, y, d):
        return wx._misc.DragCopy
    def OnAddFiles(self):
        mw=self.mainwindow
        if not len(self.add_files):
            return
        for file in self.add_files:
            if file is None:
                continue
            if len(self.path):
                path=self.path+"/"+os.path.basename(file)
            else:
                path=os.path.basename(file) # you can't create files in root but I won't stop you
            contents=open(file, "rb").read()
            mw.MakeCall( Request(mw.wt.writefile, path, contents),
                         Callback(self.OnAddFilesResults, self.path) )
            self.add_files.remove(file)
            break
    def OnAddFilesResults(self, parentdir, exception, _):
        mw=self.mainwindow
        if mw.HandleException(exception): return
        if not len(self.add_files):
            self.ShowFiles(parentdir, True)
        else:
            self.OnAddFiles()
    if guihelper.IsMSWindows():
        def OnStartDrag(self, evt):
            evt.Skip()
            if not evt.LeftIsDown():
                return
            path=self.itemtopath(self.GetFirstSelected())
            drag_source=wx.DropSource(self)
            file_names=wx.FileDataObject()
            file_names.AddFile(path)
            drag_source.SetData(file_names)
            self.__dragging=True
            res=drag_source.DoDragDrop(wx.Drag_CopyOnly)
            self.__dragging=False
    def OnRightUp(self, event):
        pt = event.GetPosition()
        item, flags = self.HitTest(pt)
        if item is not -1:
            self.Select(item)
            self.PopupMenu(self.filemenu, pt)
        else:
            self.PopupMenu(self.genericmenu, pt)
    def OnRightDown(self,event):
        pt = event.GetPosition();
        item, flags = self.HitTest(pt)
        try:
            self.Select(item)
        except:
            pass
    def OnNewFile(self,_):
        dlg=wx.FileDialog(self, style=wx.OPEN|wx.HIDE_READONLY|wx.CHANGE_DIR)
        if dlg.ShowModal()!=wx.ID_OK:
            dlg.Destroy()
            return
        infile=dlg.GetPath()
        contents=open(infile, "rb").read()
        if len(self.path):
            path=self.path+"/"+os.path.basename(dlg.GetPath())
        else:
            path=os.path.basename(dlg.GetPath()) # you can't create files in root but I won't stop you
        mw=self.mainwindow
        mw.MakeCall( Request(mw.wt.writefile, path, contents),
                     Callback(self.parent.OnNewFileResults, self.path) )
        dlg.Destroy()
    def OnFileSave(self, _):
        path=self.itemtopath(self.GetFirstSelected())
        mw=self.mainwindow
        mw.MakeCall( Request(mw.wt.getfile, path),
                     Callback(self.OnFileSaveResults, path) )
    def OnFileSaveResults(self, path, exception, contents):
        mw=self.mainwindow
        if mw.HandleException(exception): return
        bn=guihelper.basename(path)
        ext=guihelper.getextension(bn)
        if len(ext):
            ext="%s files (*.%s)|*.%s" % (ext.upper(), ext, ext)
        else:
            ext="All files|*"
        dlg=wx.FileDialog(self, "Save File As", defaultFile=bn, wildcard=ext,
                             style=wx.SAVE|wx.OVERWRITE_PROMPT|wx.CHANGE_DIR)
        if dlg.ShowModal()==wx.ID_OK:
            open(dlg.GetPath(), "wb").write(contents)
        dlg.Destroy()
    def OnItemActivated(self,_):
        self.OnHexView(self)
    def OnHexView(self, _):
        path=self.itemtopath(self.GetFirstSelected())
        mw=self.mainwindow
        mw.MakeCall( Request(mw.wt.getfile, path),
                     Callback(self.OnHexViewResults, path) )
    def OnHexViewResults(self, path, exception, result):
        mw=self.mainwindow
        if mw.HandleException(exception): return
        dlg=hexeditor.HexEditorDialog(self, result, path+" Contents")
        dlg.Show()
    def OnFileDelete(self, _):
        path=self.itemtopath(self.GetFirstSelected())
        mw=self.mainwindow
        mw.MakeCall( Request(mw.wt.rmfile, path),
                     Callback(self.OnFileDeleteResults, guihelper.dirname(path)) )
    def OnFileDeleteResults(self, parentdir, exception, _):
        mw=self.mainwindow
        if mw.HandleException(exception): return
        self.ShowFiles(parentdir, True)
    def OnFileOverwrite(self,_):
        path=self.itemtopath(self.GetFirstSelected())
        dlg=wx.FileDialog(self, style=wx.OPEN|wx.HIDE_READONLY|wx.CHANGE_DIR)
        if dlg.ShowModal()!=wx.ID_OK:
            dlg.Destroy()
            return
        infile=dlg.GetPath()
        contents=open(infile, "rb").read()
        mw=self.mainwindow
        mw.MakeCall( Request(mw.wt.writefile, path, contents),
                     Callback(self.OnFileOverwriteResults, guihelper.dirname(path)) )
        dlg.Destroy()
    def OnFileOverwriteResults(self, parentdir, exception, _):
        mw=self.mainwindow
        if mw.HandleException(exception): return
        self.ShowFiles(parentdir, True)
    def ResetView(self):
        self.DeleteAllItems()
        self.files={}
        self.path=None
        self.itemDataMap = self.files
        self.itemIndexMap = self.files.keys()
        self.SetItemCount(0)
    def ShowFiles(self, path, refresh=False):
        mw=self.mainwindow
        if path == self.path and not refresh:
            return
        self.path=None
        mw.MakeCall( Request(mw.wt.getfileonlylist, path),
                     Callback(self.OnShowFilesResults, path) )
    def OnShowFilesResults(self, path, exception, result):
        mw=self.mainwindow
        if mw.HandleException(exception): return
        count=self.GetItemCount()
        self.path=path
        self.DeleteAllItems()
        self.files={}
        index=0
        for file in result:
            index=index+1
            f=guihelper.basename(file)
            if self.datacolumn:
                self.files[index]=(f, `result[file]['size']`, result[file]['date'][1], result[file]['data'], file)
            else:
                self.files[index]=(f, `result[file]['size']`, result[file]['date'][1], file)
        self.itemDataMap = self.files
        self.itemIndexMap = self.files.keys()
        self.SetItemCount(index)
        self.SortListItems()
        if count!=0 and index==0:
            wx.EVT_PAINT(self, self.OnPaint)
        elif count==0 and index!=0:
            self.Unbind(wx.EVT_PAINT)
    def itemtopath(self, item):
        index=self.itemIndexMap[item]
        if self.datacolumn:
            return self.itemDataMap[index][4]
        return self.itemDataMap[index][3]
    def SortItems(self,sorter=None):
        col=self._col
        sf=self._colSortFlag[col]
        items=[]
        for k,v in self.itemDataMap.items():
            if col==1:
                items.append([int(v[col]),k])
            else:
                items.append([v[col],k])
        items.sort()
        k=[key for value, key in items]
        if sf==False:
            k.reverse()
        self.itemIndexMap=k
        self.Refresh()
    def GetListCtrl(self):
        return self
    def GetSortImages(self):
        return (self.sm_dn, self.sm_up)
    def OnGetItemText(self, item, col):
        index=self.itemIndexMap[item]
        s = self.itemDataMap[index][col]
        return s
    def OnGetItemImage(self, item):
        return self.img_file
    def OnGetItemAttr(self, item):
        return None
class FileSystemDirectoryView(wx.TreeCtrl):
    def __init__(self, mainwindow, parent, id, style):
        wx.TreeCtrl.__init__(self, parent, id, style=style)
        self.parent=parent
        self.mainwindow=mainwindow
        wx.EVT_TREE_ITEM_EXPANDED(self, id, self.OnItemExpanded)
        wx.EVT_TREE_SEL_CHANGED(self,id, self.OnItemSelected)
        self.dirmenu=wx.Menu()
        self.dirmenu.Append(guihelper.ID_FV_NEWSUBDIR, "Make subdirectory ...")
        self.dirmenu.Append(guihelper.ID_FV_NEWFILE, "New File ...")
        self.dirmenu.AppendSeparator()
        self.dirmenu.Append(guihelper.ID_FV_BACKUP, "Backup directory ...")
        self.dirmenu.Append(guihelper.ID_FV_BACKUP_TREE, "Backup entire tree ...")
        self.dirmenu.Append(guihelper.ID_FV_RESTORE, "Restore ...")
        self.dirmenu.AppendSeparator()
        self.dirmenu.Append(guihelper.ID_FV_REFRESH, "Refresh")
        self.dirmenu.AppendSeparator()
        self.dirmenu.Append(guihelper.ID_FV_DELETE, "Delete")
        self.dirmenu.AppendSeparator()
        self.dirmenu.Append(guihelper.ID_FV_TOTAL_REFRESH, "Refresh Filesystem")
        self.dirmenu.Append(guihelper.ID_FV_OFFLINEPHONE, "Offline Phone")
        self.dirmenu.Append(guihelper.ID_FV_REBOOTPHONE, "Reboot Phone")
        self.dirmenu.Append(guihelper.ID_FV_MODEMMODE, "Go to modem mode")
        self.genericmenu=wx.Menu()
        self.genericmenu.Append(guihelper.ID_FV_TOTAL_REFRESH, "Refresh Filesystem")
        self.genericmenu.Append(guihelper.ID_FV_OFFLINEPHONE, "Offline Phone")
        self.genericmenu.Append(guihelper.ID_FV_REBOOTPHONE, "Reboot Phone")
        self.genericmenu.Append(guihelper.ID_FV_MODEMMODE, "Go to modem mode")
        wx.EVT_MENU(self.genericmenu, guihelper.ID_FV_TOTAL_REFRESH, self.OnRefresh)
        wx.EVT_MENU(self.genericmenu, guihelper.ID_FV_OFFLINEPHONE, parent.OnPhoneOffline)
        wx.EVT_MENU(self.genericmenu, guihelper.ID_FV_REBOOTPHONE, parent.OnPhoneReboot)
        wx.EVT_MENU(self.genericmenu, guihelper.ID_FV_MODEMMODE, parent.OnModemMode)
        wx.EVT_MENU(self.dirmenu, guihelper.ID_FV_NEWSUBDIR, self.OnNewSubdir)
        wx.EVT_MENU(self.dirmenu, guihelper.ID_FV_NEWFILE, self.OnNewFile)
        wx.EVT_MENU(self.dirmenu, guihelper.ID_FV_DELETE, self.OnDirDelete)
        wx.EVT_MENU(self.dirmenu, guihelper.ID_FV_BACKUP, self.OnBackupDirectory)
        wx.EVT_MENU(self.dirmenu, guihelper.ID_FV_BACKUP_TREE, self.OnBackupTree)
        wx.EVT_MENU(self.dirmenu, guihelper.ID_FV_RESTORE, self.OnRestore)
        wx.EVT_MENU(self.dirmenu, guihelper.ID_FV_REFRESH, self.OnDirRefresh)
        wx.EVT_MENU(self.dirmenu, guihelper.ID_FV_TOTAL_REFRESH, self.OnRefresh)
        wx.EVT_MENU(self.dirmenu, guihelper.ID_FV_OFFLINEPHONE, parent.OnPhoneOffline)
        wx.EVT_MENU(self.dirmenu, guihelper.ID_FV_REBOOTPHONE, parent.OnPhoneReboot)
        wx.EVT_MENU(self.dirmenu, guihelper.ID_FV_MODEMMODE, parent.OnModemMode)
        wx.EVT_RIGHT_UP(self, self.OnRightUp)
        self.image_list=wx.ImageList(16, 16)
        self.img_dir=self.image_list.Add(wx.ArtProvider_GetBitmap(wx.ART_FOLDER,
                                                             wx.ART_OTHER,
                                                             (16, 16)))
        self.img_dir_open=self.image_list.Add(wx.ArtProvider_GetBitmap(wx.ART_FOLDER_OPEN,
                                                             wx.ART_OTHER,
                                                             (16, 16)))
        self.SetImageList(self.image_list)
        self.add_files=[]
        self.add_target=""
        self.droptarget=guiwidgets.MyFileDropTarget(self, True, True)
        self.SetDropTarget(self.droptarget)
        self.ResetView()
    def ResetView(self):
        self.first_time=True
        self.DeleteAllItems()
        self.root=self.AddRoot("/")
        self.item=self.root
        self.SetPyData(self.root, None)
        self.SetItemHasChildren(self.root, True)
        self.SetItemImage(self.root, self.img_dir)
        self.SetItemImage(self.root, self.img_dir_open, which=wx.TreeItemIcon_Expanded)
        self.SetPyData(self.AppendItem(self.root, "Retrieving..."), None)
        self.selections=[]
        self.dragging=False
        self.skip_dir_list=0
    def OnDropFiles(self, x, y, filenames):
        target=self
        t=self.mainwindow.nb.GetPage(self.mainwindow.nb.GetSelection())
        if isinstance(t, FileSystemDirectoryView):
            target=t
        item, flags = self.HitTest((x, y))
        if item.IsOk():
            self.SelectItem(item)
            self.add_target=self.itemtopath(item)
            self.add_files=filenames
            target.OnAddFiles()
        self.dragging=False
    def OnDragOver(self, x, y, d):
        target=self
        t=self.mainwindow.nb.GetPage(self.mainwindow.nb.GetSelection())
        if isinstance(t, FileSystemDirectoryView):
            target=t
        item, flags = self.HitTest((x, y))
        selections = self.GetSelections()
        if item.IsOk():
            if selections != [item]:
                self.UnselectAll()
                self.SelectItem(item)
            return wx._misc.DragCopy
        elif selections:
            self.UnselectAll()
        return wx._misc.DragNone
    def _saveSelection(self):
        self.selections = self.GetSelections()
        self.UnselectAll()
    def _restoreSelection(self):
        self.UnselectAll()
        for i in self.selections:
            self.SelectItem(i)
        self.selections=[]
    def OnEnter(self, x, y, d):
        self._saveSelection()
        self.dragging=True
        return d
    def OnLeave(self):
        self.dragging=False
        self._restoreSelection()
    def OnAddFiles(self):
        mw=self.mainwindow
        if not len(self.add_files):
            return
        for file in self.add_files:
            if file is None:
                continue
            if len(self.add_target):
                path=self.add_target+"/"+os.path.basename(file)
            else:
                path=os.path.basename(file) # you can't create files in root but I won't stop you
            contents=open(file, "rb").read()
            mw.MakeCall( Request(mw.wt.writefile, path, contents),
                         Callback(self.OnAddFilesResults, self.add_target) )
            self.add_files.remove(file)
            break
    def OnAddFilesResults(self, parentdir, exception, _):
        mw=self.mainwindow
        if mw.HandleException(exception): return
        if not len(self.add_files):
            self.parent.ShowFiles(parentdir, True)
        else:
            self.OnAddFiles()
    def OnRightUp(self, event):
        pt = event.GetPosition();
        item, flags = self.HitTest(pt)
        if item.IsOk():
            self.SelectItem(item)
            self.PopupMenu(self.dirmenu, pt)
        else:
            self.SelectItem(self.item)
            self.PopupMenu(self.genericmenu, pt)
    def OnItemSelected(self,_):
        if not self.dragging and not self.first_time:
            item=self.GetSelection()
            if item.IsOk() and item != self.item:
                path=self.itemtopath(item)
                self.parent.ShowFiles(path)
                if not self.skip_dir_list:
                    self.OnDirListing(path)
                self.item=item
    def OnItemExpanded(self, event):
        if not self.skip_dir_list:
            item=event.GetItem()
            if self.first_time:
                self.GetFullFS()
            else:
                path=self.itemtopath(item)
                self.OnDirListing(path)
    def AddDirectory(self, location, name):
        new_item=self.AppendItem(location, name)
        self.SetPyData(new_item, None)
        self.SetItemImage(new_item, self.img_dir)
        self.SetItemImage(new_item, self.img_dir_open, which=wx.TreeItemIcon_Expanded)
        if self.GetChildrenCount(location, False) == 1 and not self.IsExpanded(location):
            self.skip_dir_list+=1
            self.Expand(location)
            self.Collapse(location)
            self.skip_dir_list-=1
        return new_item
    def RemoveDirectory(self, parent, item):
        if self.GetChildrenCount(parent, False) == 1:
            self.Collapse(parent)
        self.Delete(item)
    def GetFullFS(self):
        mw=self.mainwindow
        mw.OnBusyStart()
        mw.GetStatusBar().progressminor(0, 100, 'Reading Phone File System ...')
        mw.MakeCall( Request(mw.wt.fulldirlisting),
                     Callback(self.OnFullDirListingResults) )
    def OnFullDirListingResults(self, exception, result):
        mw=self.mainwindow
        mw.OnBusyEnd()
        if mw.HandleException(exception):
            self.Collapse(self.root)
            return
        self.first_time=False
        self.skip_dir_list+=1
        self.SelectItem(self.root)
        self.DeleteChildren(self.root)
        keys=result.keys()
        keys.sort()
        for k in keys:
            path, dir=os.path.split(k)
            item=self.pathtoitem(path)
            self.AddDirectory(item, dir)
        self.skip_dir_list-=1
        self.parent.ShowFiles("")
    def OnDirListing(self, path):
        mw=self.mainwindow
        mw.MakeCall( Request(mw.wt.singledirlisting, path),
                     Callback(self.OnDirListingResults, path) )
    def OnDirListingResults(self, path, exception, result):
        mw=self.mainwindow
        if mw.HandleException(exception): return
        item=self.pathtoitem(path)
        l=[]
        child,cookie=self.GetFirstChild(item)
        for dummy in range(0,self.GetChildrenCount(item,False)):
            l.append(child)
            child,cookie=self.GetNextChild(item,cookie)
        sort=False
        for file in result:
            children=True
            f=guihelper.basename(file)
            found=None
            for i in l:
                if self.GetItemText(i)==f:
                    found=i
                    break
            if found is None:
                found=self.AddDirectory(item, f)
                self.OnDirListing(file)
                sort=True
        for i in l: # remove all children not present in result
            if not result.has_key(self.itemtopath(i)):
                self.RemoveDirectory(item, i)
        if sort:
            self.SortChildren(item)
    def OnNewSubdir(self, _):
        dlg=wx.TextEntryDialog(self, "Subdirectory name?", "Create Subdirectory", "newfolder")
        if dlg.ShowModal()!=wx.ID_OK:
            dlg.Destroy()
            return
        item=self.GetSelection()
        parent=self.itemtopath(item)
        if len(parent):
            path=parent+"/"+dlg.GetValue()
        else:
            path=dlg.GetValue()
        mw=self.mainwindow
        mw.MakeCall( Request(mw.wt.mkdir, path),
                     Callback(self.OnNewSubdirResults, path) )
        dlg.Destroy()
    def OnNewSubdirResults(self, new_path, exception, _):
        mw=self.mainwindow
        if mw.HandleException(exception): return
        path, dir=os.path.split(new_path)
        item=self.pathtoitem(path)
        self.AddDirectory(item, dir)
        self.SortChildren(item)
        self.Expand(item)
        self.OnDirListing(path)
    def OnNewFile(self,_):
        parent=self.itemtopath(self.GetSelection())
        dlg=wx.FileDialog(self, style=wx.OPEN|wx.HIDE_READONLY|wx.CHANGE_DIR)
        if dlg.ShowModal()!=wx.ID_OK:
            dlg.Destroy()
            return
        infile=dlg.GetPath()
        contents=open(infile, "rb").read()
        if len(parent):
            path=parent+"/"+os.path.basename(dlg.GetPath())
        else:
            path=os.path.basename(dlg.GetPath()) # you can't create files in root but I won't stop you
        mw=self.mainwindow
        mw.MakeCall( Request(mw.wt.writefile, path, contents),
                     Callback(self.OnNewFileResults, parent) )
        dlg.Destroy()
    def OnNewFileResults(self, parentdir, exception, _):
        mw=self.mainwindow
        if mw.HandleException(exception): return
        self.parent.ShowFiles(parentdir, True)
    def OnDirDelete(self, _):
        path=self.itemtopath(self.GetSelection())
        mw=self.mainwindow
        mw.MakeCall( Request(mw.wt.rmdirs, path),
                     Callback(self.OnDirDeleteResults, path) )
    def OnDirDeleteResults(self, path, exception, _):
        mw=self.mainwindow
        if mw.HandleException(exception): return
        parent, dir=os.path.split(path)
        parent_item=self.pathtoitem(parent)
        del_item=self.pathtoitem(path)
        self.RemoveDirectory(parent_item, del_item)
        self.OnDirListing(parent)
    def OnBackupTree(self, _):
        self.OnBackup(recurse=100)
    def OnBackupDirectory(self, _):
        self.OnBackup()
    def OnBackup(self, recurse=0):
        path=self.itemtopath(self.GetSelection())
        mw=self.mainwindow
        mw.MakeCall( Request(mw.wt.getbackup, path, recurse),
                     Callback(self.OnBackupResults, path) )
    def OnBackupResults(self, path, exception, backup):
        mw=self.mainwindow
        if mw.HandleException(exception): return
        bn=guihelper.basename(path)
        if len(bn)<1:
            bn="root"
        bn+=".zip"
        ext="Zip files|*.zip|All Files|*"
        dlg=wx.FileDialog(self, "Save File As", defaultFile=bn, wildcard=ext,
                             style=wx.SAVE|wx.OVERWRITE_PROMPT|wx.CHANGE_DIR)
        if dlg.ShowModal()==wx.ID_OK:
            open(dlg.GetPath(), "wb").write(backup)
        dlg.Destroy()
    def OnRestore(self, _):
        ext="Zip files|*.zip|All Files|*"
        path=self.itemtopath(self.GetSelection())
        bn=guihelper.basename(path)
        if len(bn)<1:
            bn="root"
        bn+=".zip"
        ext="Zip files|*.zip|All Files|*"
        dlg=wx.FileDialog(self, "Open backup file", defaultFile=bn, wildcard=ext,
                             style=wx.OPEN|wx.HIDE_READONLY|wx.CHANGE_DIR)
        if dlg.ShowModal()!=wx.ID_OK:
            return
        name=dlg.GetPath()
        if not zipfile.is_zipfile(name):
            dlg=guiwidgets.AlertDialogWithHelp(self.mainwindow, name+" is not a valid zipfile.", "Zip file required",
                                               lambda _: wx.GetApp().displayhelpid(helpids.ID_NOT_A_ZIPFILE),
                                               style=wx.OK|wx.ICON_ERROR)
            dlg.ShowModal()
            dlg.Destroy()
            return
        zipf=zipfile.ZipFile(name, "r")
        xx=zipf.testzip()
        if xx is not None:
            dlg=guiwidgets.AlertDialogWithHelp(self.mainwindow, name+" has corrupted contents.  Use a repair utility to fix it",
                                               "Zip file corrupted",
                                               lambda _: wx.GetApp().displayhelpid(helpids.ID_ZIPFILE_CORRUPTED),
                                               style=wx.OK|wx.ICON_ERROR)
            dlg.ShowModal()
            dlg.Destroy()
            return
        dlg=RestoreDialog(self.mainwindow, "Restore files", zipf, path, self.OnRestoreOK)
        dlg.Show(True)
    def OnRestoreOK(self, zipf, names, parentdir):
        if len(names)==0:
            wx.MessageBox("You didn't select any files to restore!", "No files selected",
                         wx.OK|wx.ICON_EXCLAMATION)
            return
        l=[]
        for zipname, fsname in names:
            l.append( (fsname, zipf.read(zipname)) )
        mw=self.mainwindow
        mw.MakeCall( Request(mw.wt.restorefiles, l),
                     Callback(self.OnRestoreResults, parentdir) )
    def OnRestoreResults(self, parentdir, exception, results):
        mw=self.mainwindow
        if mw.HandleException(exception): return
        ok=filter(lambda s: s[0], results)
        fail=filter(lambda s: not s[0], results)
        if len(parentdir):
            dirs=[]
            for _, name in results:
                while(len(name)>len(parentdir)):
                    name=guihelper.dirname(name)
                    if name not in dirs:
                        dirs.append(name)
            dirs.sort()
            for d in dirs:
                self.OnDirListing(d)
        self.OnDirListing(parentdir)
        if len(ok) and len(fail)==0:
            dlg=wx.MessageDialog(mw, "All files restored ok", "All files restored",
                                wx.OK|wx.ICON_INFORMATION)
            dlg.Show(True)
            return
        if len(fail) and len(ok)==0:
            wx.MessageBox("All files failed to restore", "No files restored",
                         wx.OK|wx.ICON_ERROR)
            return
        op="Failed to restore some files.  Check the log for reasons.:\n\n"
        for s,n in fail:
            op+="   "+n+"\n"
        wx.MessageBox(op, "Some restores failed", wx.OK|wx.ICON_ERROR)
    def OnDirRefresh(self, _):
        path=self.itemtopath(self.GetSelection())
        self.parent.ShowFiles(path, True)
        self.OnDirListing(path)
    def OnRefresh(self, _):
        self.GetFullFS()
    def itemtopath(self, item):
        if item==self.root: return ""
        res=self.GetItemText(item)
        while True:
            parent=self.GetItemParent(item)
            if parent==self.root:
                return res
            item=parent
            res=self.GetItemText(item)+"/"+res
        assert False
        return ""
    def pathtoitem(self, path):
        if path=="": return self.root
        dirs=path.split('/')
        node=self.root
        for n in range(0, len(dirs)):
            foundnode=None
            child,cookie=self.GetFirstChild(node)
            for dummy in range(0, self.GetChildrenCount(node, False)):
                d=self.GetItemText(child)
                if d==dirs[n]:
                    node=child
                    foundnode=node
                    break
                child,cookie=self.GetNextChild(node,cookie)
            if foundnode is not None:
                continue
            node=self.AppendItem(node, dirs[n])
            self.SetPyData(node, None)
        return node
class RestoreDialog(wx.Dialog):
    """A dialog that lists all the files that will be restored"""
    def __init__(self, parent, title, zipf, path, okcb):
        """Constructor
        @param path: Placed before names in the archive.  Should not include a
                       trailing slash.
        """
        wx.Dialog.__init__(self, parent, -1, title, style=wx.DEFAULT_DIALOG_STYLE|wx.RESIZE_BORDER)
        vbs=wx.BoxSizer(wx.VERTICAL)
        vbs.Add( wx.StaticText(self, -1, "Choose files to restore"), 0, wx.ALIGN_CENTRE|wx.ALL, 5)
        nl=zipf.namelist()
        nl.sort()
        prefix=path
        if len(prefix)=="/" or prefix=="":
            prefix=""
        else:
            prefix+="/"
        nnl=map(lambda i: prefix+i, nl)
        self.clb=wx.CheckListBox(self, -1, choices=nnl, style=wx.LB_SINGLE|wx.LB_HSCROLL|wx.LB_NEEDED_SB, size=wx.Size(200,300))
        for i in range(len(nnl)):
            self.clb.Check(i, True)
        vbs.Add( self.clb, 1, wx.EXPAND|wx.ALL, 5)
        vbs.Add(wx.StaticLine(self, -1, style=wx.LI_HORIZONTAL), 0, wx.EXPAND|wx.ALL, 5)
        vbs.Add(self.CreateButtonSizer(wx.OK|wx.CANCEL|wx.HELP), 0, wx.ALIGN_CENTER|wx.ALL, 5)
        self.SetSizer(vbs)
        self.SetAutoLayout(True)
        vbs.Fit(self)
        wx.EVT_BUTTON(self, wx.ID_HELP, lambda _: wx.GetApp().displayhelpid(helpids.ID_RESTOREDIALOG))
        wx.EVT_BUTTON(self, wx.ID_OK, self.OnOK)
        self.okcb=okcb
        self.zipf=zipf
        self.nl=zip(nl, nnl)
        self.path=path
    def OnOK(self, _):
        names=[]
        for i in range(len(self.nl)):
            if self.clb.IsChecked(i):
                names.append(self.nl[i])
        self.okcb(self.zipf, names, self.path)
        self.Show(False)
        self.Destroy()
if guihelper.IsMSWindows():
    import struct
    class DeviceChanged:
        DBT_DEVICEARRIVAL = 0x8000
        DBT_DEVICEQUERYREMOVE = 0x8001
        DBT_DEVICEQUERYREMOVEFAILED = 0x8002
        DBT_DEVICEREMOVEPENDING =  0x8003
        DBT_DEVICEREMOVECOMPLETE = 0x8004
        DBT_DEVICETYPESPECIFIC = 0x8005    
        DBT_DEVNODES_CHANGED = 7
        DBT_CONFIGCHANGED = 0x18
        DBT_DEVTYP_OEM = 0
        DBT_DEVTYP_DEVNODE = 1
        DBT_DEVTYP_VOLUME = 2
        DBT_DEVTYP_PORT = 3
        DBT_DEVTYP_NET = 4
        DBTF_MEDIA   =   0x0001
        DBTF_NET    =    0x0002
        def __init__(self, wparam, lparam):
            self._info=None
            for name in dir(self):
                if name.startswith("DBT") and \
                   not name.startswith("DBT_DEVTYP") and \
                   getattr(self,name)==wparam:
                    self._info=(name, dict(self._decode_struct(lparam)))
        def GetEventInfo(self):
            return self._info
        def _decode_struct(self, lparam):
            if lparam==0: return ()
            format = "iii"
            buf = win32gui.PyMakeBuffer(struct.calcsize(format), lparam)
            dbch_size, dbch_devicetype, dbch_reserved = struct.unpack(format, buf)
            buf = win32gui.PyMakeBuffer(dbch_size, lparam) # we know true size now
            if dbch_devicetype==self.DBT_DEVTYP_PORT:
                name=""
                for b in buf[struct.calcsize(format):]:
                    if b!="\x00":
                        name+=b
                        continue
                    break
                return ("name", name),
            if dbch_devicetype==self.DBT_DEVTYP_VOLUME:
                format="iiiih0i"
                dbcv_size, dbcv_devicetype, dbcv_reserved, dbcv_unitmask, dbcv_flags = struct.unpack(format, buf)
                units=[chr(ord('A')+x) for x in range(26) if dbcv_unitmask&(2**x)]
                flag=""
                for name in dir(self):
                    if name.startswith("DBTF_") and getattr(self, name)==dbcv_flags:
                        flag=name
                        break
                return ("drives", units), ("flag", flag)
            print "unhandled devicetype struct", dbch_devicetype
            return ()
