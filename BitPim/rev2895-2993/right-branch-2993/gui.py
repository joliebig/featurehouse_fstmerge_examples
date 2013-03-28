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
import wx.html
import guiwidgets
import common
import version
import helpids
import comdiagnose
import phonebook
import importexport
import guihelper
import bphtml
import bitflingscan
import update
import phoneinfo
import phone_detect
import phone_media_codec
import pubsub
import phones.com_brew as com_brew
import auto_sync
import phone_root
import playlist
import fileview
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
        if guihelper.IsMac():
            from Carbon import AH
            path=os.path.abspath(os.path.join(guihelper.resourcedirectory, "..", "..", ".."))
            if  os.path.exists(path) and path.endswith(".app"):
                print "registering help book for bundle",path
                res=AH.AHRegisterHelpBook(path)
                print "result was",res
                self.helpcontroller=True
                return
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
            return
        elif guihelper.IsMac():
            if self.helpcontroller is None:
                self._setuphelp()
            if self.helpcontroller is True:
                from Carbon import AH
                res=AH.AHGotoPage('BitPim Help', id, None)
                print "gotopage",id,"returned",res
                return
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
        self.__owner_name=''
        self._taskbar=None
        self.__phone_detect_at_startup=False
        self._autodetect_delay=0
        sb=guiwidgets.MyStatusBar(self)
        self.SetStatusBar(sb)
        self.SetStatusBarPane(sb.GetHelpPane())
        wx.ArtProvider_PushProvider(guihelper.ArtProvider())
        ib=wx.IconBundle()
        ib.AddIconFromFile(guihelper.getresourcefile("bitpim.ico"), wx.BITMAP_TYPE_ANY)
        self.SetIcons(ib)
        menuBar = wx.MenuBar()
        menu = wx.Menu()
        menu.Append(guihelper.ID_FILEPRINT, "&Print...", "Print phonebook")
        impmenu=wx.Menu()
        for x, desc, help, func in importexport.GetPhonebookImports():
            impmenu.Append(x, desc, help)
            wx.EVT_MENU(self, x, MenuCallback(func, self) )
        menu.AppendMenu(guihelper.ID_FILEIMPORT, "Import", impmenu)
        expmenu=wx.Menu()
        for x, desc, help, func in importexport.GetPhonebookExports():
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
        menu.Append(guihelper.ID_DATAHISTORICAL, "Historical Data ...", "View Current & Historical Data")
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
            menu.Append(guihelper.ID_HELPHELP, "&Help on this panel", "Help for the panel you are looking at")
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
        self.tb.AddSimpleTool(guihelper.ID_DATAGETPHONE, wx.ArtProvider.GetBitmap(guihelper.ART_DATAGETPHONE, wx.ART_TOOLBAR, sz),
                                                "Get Phone Data", "Synchronize BitPim with Phone")
        self.tb.AddLabelTool(guihelper.ID_DATASENDPHONE, "Send Phone Data", wx.ArtProvider.GetBitmap(guihelper.ART_DATASENDPHONE, wx.ART_TOOLBAR, sz),
                                          shortHelp="Send Phone Data", longHelp="Synchronize Phone with BitPim")
        self.tb.AddLabelTool(guihelper.ID_DATAHISTORICAL, "BitPim Help", wx.ArtProvider.GetBitmap(guihelper.ART_DATAHISTORICAL, wx.ART_TOOLBAR, sz),
                                             shortHelp="Historical Data", longHelp="Show Historical Data")
        self.tb.AddSeparator()
        self.tb.AddLabelTool(guihelper.ID_EDITADDENTRY, "Add", wx.ArtProvider.GetBitmap(wx.ART_ADD_BOOKMARK, wx.ART_TOOLBAR, sz),
                                          shortHelp="Add", longHelp="Add an item")
        self.tb.AddLabelTool(guihelper.ID_EDITDELETEENTRY, "Delete", wx.ArtProvider.GetBitmap(wx.ART_DEL_BOOKMARK, wx.ART_TOOLBAR, sz),
                                             shortHelp="Delete", longHelp="Delete item")
        self.tb.AddLabelTool(guihelper.ID_EDITPHONEINFO, "Phone Info", wx.ArtProvider.GetBitmap(guihelper.ART_EDITPHONEINFO, wx.ART_TOOLBAR, sz),
                                          shortHelp="Phone Info", longHelp="Show Phone Info")
        self.tb.AddLabelTool(guihelper.ID_EDITDETECT, "Find Phone", wx.ArtProvider.GetBitmap(guihelper.ART_EDITDETECT, wx.ART_TOOLBAR, sz),
                                          shortHelp="Find Phone", longHelp="Find Phone")
        self.tb.AddLabelTool(guihelper.ID_EDITSETTINGS, "Edit Settings", wx.ArtProvider.GetBitmap(guihelper.ART_EDITSETTINGS, wx.ART_TOOLBAR, sz),
                                          shortHelp="Edit Settings", longHelp="Edit BitPim Settings")
        self.tb.AddSeparator()
        self.tb.AddSimpleTool(guihelper.ID_AUTOSYNCEXECUTE, wx.ArtProvider.GetBitmap(guihelper.ART_AUTOSYNCEXECUTE, wx.ART_TOOLBAR, sz),
                                            "Autosync Calendar", "Synchronize Phone Calendar with PC")
        self.tb.AddSeparator()
        self.tb.AddLabelTool(guihelper.ID_HELPHELP, "BitPim Help", wx.ArtProvider.GetBitmap(guihelper.ART_HELPHELP, wx.ART_TOOLBAR, sz),
                                             shortHelp="BitPim Help", longHelp="BitPim Help")
        self.tb.Realize()
        self.dlggetphone=guiwidgets.GetPhoneDialog(self, "Get Data from Phone")
        self.dlgsendphone=guiwidgets.SendPhoneDialog(self, "Send Data to Phone")
        self.sw=wx.SplitterWindow(self, wx.NewId(), style=wx.SP_3D|wx.SP_NO_XP_THEME)
        self.tree = phone_root.PhoneTree(self.sw, self, wx.NewId())
        wx.EVT_MENU(self, guihelper.ID_FILEPRINT, self.tree.OnFilePrint)
        wx.EVT_MENU(self, guihelper.ID_FILEEXIT, self.OnExit)
        wx.EVT_MENU(self, guihelper.ID_EDITSETTINGS, self.OnEditSettings)
        wx.EVT_MENU(self, guihelper.ID_DATAGETPHONE, self.OnDataGetPhone)
        wx.EVT_MENU(self, guihelper.ID_DATASENDPHONE, self.OnDataSendPhone)
        wx.EVT_MENU(self, guihelper.ID_DATAHISTORICAL, self.tree.OnDataHistorical)
        wx.EVT_MENU(self, guihelper.ID_VIEWCOLUMNS, self.tree.OnViewColumns)
        wx.EVT_MENU(self, guihelper.ID_VIEWPREVIEW, self.tree.OnViewPreview)
        wx.EVT_MENU(self, guihelper.ID_VIEWCLEARLOGS, self.tree.OnViewClearLogs)
        wx.EVT_MENU(self, guihelper.ID_VIEWLOGDATA, self.tree.OnViewLogData)
        wx.EVT_MENU(self, guihelper.ID_VIEWFILESYSTEM, self.tree.OnViewFilesystem)
        wx.EVT_MENU(self, guihelper.ID_EDITADDENTRY, self.tree.OnEditAddEntry)
        wx.EVT_MENU(self, guihelper.ID_EDITDELETEENTRY, self.tree.OnEditDeleteEntry)
        wx.EVT_MENU(self, guihelper.ID_EDITSELECTALL, self.tree.OnEditSelectAll)
        wx.EVT_MENU(self, guihelper.ID_EDITCOPY, self.tree.OnCopyEntry)
        wx.EVT_MENU(self, guihelper.ID_EDITPASTE, self.tree.OnPasteEntry)
        wx.EVT_MENU(self, guihelper.ID_EDITRENAME, self.tree.OnRenameEntry)
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
        if min(self.GetSize())<250:
            self.SetSize( (640, 480) )
        self.configdlg=guiwidgets.ConfigDialog(self, self)
        self.needconfig=self.configdlg.needconfig()
        self.configdlg.updatevariables()
        pos=self.config.ReadInt("mainwindowsplitterpos", 200)
        self.sw.SplitVertically(self.tree, self.tree.active_panel, pos)
        self.sw.SetMinimumPaneSize(50)
        wx.EVT_SPLITTER_SASH_POS_CHANGED(self, id, self.OnSplitterPosChanged)
        self.tree.Expand(self.tree.root)
        self.tree.CreatePhone("Phone", self.config, self.configpath)
        self.calenders=importexport.GetCalenderAutoSyncImports()
        self.autosyncsetting=auto_sync.AutoSyncSettingsDialog(self, self)
        self.autosyncsetting.updatevariables()
        self.CloseSplashScreen()
        wx.EVT_UPDATE_UI(self, guihelper.ID_AUTOSYNCEXECUTE, self.AutosyncUpdateUIEvent)
        wx.EVT_UPDATE_UI(self, guihelper.ID_DATASENDPHONE, self.tree.DataSendPhoneUpdateUIEvent)
        wx.EVT_UPDATE_UI(self, guihelper.ID_EDITDELETEENTRY, self.tree.DataDeleteItemUpdateUIEvent)
        wx.EVT_UPDATE_UI(self, guihelper.ID_EDITADDENTRY, self.tree.DataAddItemUpdateUIEvent)
        wx.EVT_UPDATE_UI(self, guihelper.ID_DATAHISTORICAL, self.tree.HistoricalDataUpdateUIEvent)
        wx.EVT_UPDATE_UI(self, guihelper.ID_VIEWCOLUMNS, self.tree.ViewColumnsandPreviewDataUpdateUIEvent)
        wx.EVT_UPDATE_UI(self, guihelper.ID_VIEWPREVIEW, self.tree.ViewColumnsandPreviewDataUpdateUIEvent)
        wx.EVT_UPDATE_UI(self, guihelper.ID_FILEPRINT, self.tree.FilePrintDataUpdateUIEvent)
        wx.EVT_UPDATE_UI(self, guihelper.ID_EDITSELECTALL, self.tree.SelectAllDataUpdateUIEvent)
        wx.EVT_UPDATE_UI(self, guihelper.ID_EDITCOPY, self.tree.EditCopyUpdateUIEvent)
        wx.EVT_UPDATE_UI(self, guihelper.ID_EDITPASTE, self.tree.EditPasteUpdateUIEvent)
        wx.EVT_UPDATE_UI(self, guihelper.ID_EDITRENAME, self.tree.EditRenameUpdateUIEvent)
        wx.EVT_UPDATE_UI(self, guihelper.ID_VIEWLOGDATA, self.tree.ViewLogDataUIEvent)
        wx.EVT_UPDATE_UI(self, guihelper.ID_VIEWFILESYSTEM, self.tree.ViewFileSystemUIEvent)
        guiwidgets.set_size("MainWin", self, screenpct=90)
        self.Show()
        if self.config.ReadInt("firstrun", True):
            self.config.WriteInt("firstrun", False)
            self.config.Flush()
            wx.CallAfter(self.OnHelpTour)
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
    def OnSplitterPosChanged(self,_):
        pos=self.sw.GetSashPosition()
        self.config.WriteInt("mainwindowsplitterpos", pos)        
    def SetActivePanel(self, panel):
        w2=self.sw.GetWindow2()
        if w2==None: # still in startup
            return
        w2.Show(False)
        self.sw.ReplaceWindow(w2, panel)
        panel.Show(True)
        panel.SetFocus()
    def GetActiveMemoWidget(self):
        return self.tree.GetActivePhone().memowidget
    def GetActiveRingerWidget(self):
        return self.tree.GetActivePhone().ringerwidget
    def GetActiveWallpaperWidget(self):
        return self.tree.GetActivePhone().wallpaperwidget
    def GetActiveTodoWidget(self):
        return self.tree.GetActivePhone().todowidget
    def GetActiveCalendarWidget(self):
        return self.tree.GetActivePhone().calendarwidget
    def GetActivePlaylistWidget(self):
        return self.tree.GetActivePhone().playlistwidget
    def GetActivePhonebookWidget(self):
        return self.tree.GetActivePhone().phonewidget
    def GetActiveCallHistoryWidget(self):
        return self.tree.GetActivePhone().callhistorywidget
    def GetActiveSMSWidget(self):
        return self.tree.GetActivePhone().smswidget
    def GetCurrentActiveWidget(self):
        return self.tree.GetActiveWidget()
    def UpdateToolbarOnPanelChange(self, add_image, add_help, delete_image, delete_help):
        sz=self.tb.GetToolBitmapSize()
        pos=self.GetToolBar().GetToolPos(guihelper.ID_EDITADDENTRY)
        self.GetToolBar().DeleteTool(guihelper.ID_EDITADDENTRY)
        self.tooladd=self.tb.InsertLabelTool(pos, guihelper.ID_EDITADDENTRY, add_help, 
                                             wx.ArtProvider.GetBitmap(add_image, wx.ART_TOOLBAR, sz),
                                             shortHelp=add_help, longHelp="Add an item")
        pos=self.GetToolBar().GetToolPos(guihelper.ID_EDITDELETEENTRY)
        self.GetToolBar().DeleteTool(guihelper.ID_EDITDELETEENTRY)
        self.tooldelete=self.tb.InsertLabelTool(pos, guihelper.ID_EDITDELETEENTRY, delete_help, 
                                                wx.ArtProvider.GetBitmap(delete_image, wx.ART_TOOLBAR, sz),
                                                shortHelp=delete_help, longHelp="Delete item")
        self.tb.Realize()
    def CloseSplashScreen(self):
        global thesplashscreen
        if thesplashscreen is not None:
            try:
                thesplashscreen.Show(False)
            except:
                pass
            thesplashscreen=None
            wx.SafeYield(onlyIfNeeded=True)
    def AutosyncUpdateUIEvent(self, event):
        event.Enable(self.autosyncsetting.IsConfigured())
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
    def OnEditSettings(self, _=None):
        if wx.IsBusy():
            wx.MessageBox("BitPim is busy.  You can't change settings until it has finished talking to your phone.",
                         "BitPim is busy.", wx.OK|wx.ICON_EXCLAMATION)
        else:
            self.__owner_name=''
            self.configdlg.ShowModal()
    def OnHelpAbout(self,_):
        import version
        str="BitPim Version "+version.versionstring+" - "+version.vendor
        d=wx.MessageDialog(self, str, "About BitPim", wx.OK|wx.ICON_INFORMATION)
        d.ShowModal()
        d.Destroy()
    def OnHelpHelp(self, _):
        wx.GetApp().displayhelpid(self.GetCurrentActiveWidget().GetHelpID())
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
    def update_cache_path(self):
        com_brew.file_cache.set_path(self.configpath)
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
            self.GetActivePhonebookWidget().importdata(results['phonebook'], results.get('categories', []), merge)
        updwp=False # did we update the wallpaper
        if results['sync'].has_key('wallpaper'):
            v=results['sync']['wallpaper']
            if v=='MERGE': raise Exception("Not implemented")
            updwp=True
            self.GetActiveWallpaperWidget().populatefs(results)
            self.GetActiveWallpaperWidget().populate(results)
        if not updwp and results.has_key('wallpaper-index'):
            self.GetActiveWallpaperWidget().updateindex(results['wallpaper-index'])
        updrng=False # did we update ringtones
        if results['sync'].has_key('ringtone'):
            v=results['sync']['ringtone']
            if v=='MERGE': raise Exception("Not implemented")
            updrng=True
            self.GetActiveRingerWidget().populatefs(results)
            self.GetActiveRingerWidget().populate(results)
        if not updrng and results.has_key('ringtone-index'):
            self.GetActiveRingerWidget().updateindex(results['ringtone-index'])            
        if results['sync'].has_key('calendar'):
            v=results['sync']['calendar']
            if v=='MERGE': raise Exception("Not implemented")
            results['calendar_version']=self.phoneprofile.BP_Calendar_Version
            self.GetActiveCalendarWidget().populatefs(results)
            self.GetActiveCalendarWidget().populate(results)
        if results['sync'].has_key('memo'):
            v=results['sync']['memo']
            if v=='MERGE': raise Exception("Not implemented")
            self.GetActiveMemoWidget().populatefs(results)
            self.GetActiveMemoWidget().populate(results)
        if results['sync'].has_key('todo'):
            v=results['sync']['todo']
            if v=='MERGE': raise NotImplementedError
            self.tree.GetActiveTodoWidget().populatefs(results)
            self.tree.GetActiveTodoWidget().populate(results)
        if results['sync'].has_key('sms'):
            v=results['sync']['sms']
            if v=='MERGE':
                self.GetActiveSMSWidget().merge(results)
            else:
                self.GetActiveSMSWidget().populatefs(results)
                self.GetActiveSMSWidget().populate(results)
        if results['sync'].has_key('call_history'):
            v=results['sync']['call_history']
            if v=='MERGE':
                self.GetActiveCallHistoryWidget().merge(results)
            else:
                self.GetActiveCallHistoryWidget().populatefs(results)
                self.GetActiveCallHistoryWidget().populate(results)
        if results['sync'].has_key(playlist.playlist_key):
            if results['sync'][playlist.playlist_key]=='MERGE':
                raise NotImplementedError
            self.GetActivePlaylistWidget().populatefs(results)
            self.GetActivePlaylistWidget().populate(results)
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
                want=self.GetActiveWallpaperWidget().SELECTED
            else:
                want=self.GetActiveWallpaperWidget().ALL
            self.GetActiveWallpaperWidget().getdata(data, want)
            todo.append( (self.wt.writewallpaper, "Wallpaper", merge) )
        v=dlg.GetRingtoneSetting()
        if v!=dlg.NOTREQUESTED:
            merge=True
            if v==dlg.OVERWRITE: merge=False
            if merge:
                want=self.GetActiveRingerWidget().SELECTED
            else:
                want=self.GetActiveRingerWidget().ALL
            self.GetActiveRingerWidget().getdata(data, want)
            todo.append( (self.wt.writeringtone, "Ringtone", merge) )
        v=dlg.GetCalendarSetting()
        if v!=dlg.NOTREQUESTED:
            merge=True
            if v==dlg.OVERWRITE: merge=False
            data['calendar_version']=self.phoneprofile.BP_Calendar_Version
            self.GetActiveCalendarWidget().getdata(data)
            todo.append( (self.wt.writecalendar, "Calendar", merge) )
        v=dlg.GetPhoneBookSetting()
        if v!=dlg.NOTREQUESTED:
            if v==dlg.OVERWRITE: 
                self.GetActivePhonebookWidget().getdata(data)
                todo.append( (self.wt.writephonebook, "Phonebook") )
            convertors.append(self.GetActivePhonebookWidget().converttophone)
            funcscb.append(self.GetActivePhonebookWidget().updateserials)
        v=dlg.GetMemoSetting()
        if v!=dlg.NOTREQUESTED:
            merge=v!=dlg.OVERWRITE
            self.GetActiveMemoWidget().getdata(data)
            todo.append((self.wt.writememo, "Memo", merge))
        v=dlg.GetTodoSetting()
        if v!=dlg.NOTREQUESTED:
            merge=v!=dlg.OVERWRITE
            self.tree.GetActiveTodoWidget().getdata(data)
            todo.append((self.wt.writetodo, "Todo", merge))
        v=dlg.GetSMSSetting()
        if v!=dlg.NOTREQUESTED:
            merge=v!=dlg.OVERWRITE
            self.GetActiveSMSWidget().getdata(data)
            todo.append((self.wt.writesms, "SMS", merge))
        v=dlg.GetPlaylistSetting()
        if v!=dlg.NOTREQUESTED:
            merge=v!=dlg.OVERWRITE
            self.GetActivePlaylistWidget().getdata(data)
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
        return self.GetActiveCalendarWidget().getdata(d).get('calendar', {})
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
    def OnProgressMinor(self, pos, max, desc=""):
        self.GetStatusBar().progressminor(pos, max, desc)
    def OnProgressMajor(self, pos, max, desc=""):
        self.GetStatusBar().progressmajor(pos, max, desc)
    def OnLog(self, str):
        if self.__phone_detect_at_startup:
            return
        str=common.strorunicode(str)
        self.tree.lw.log(str)
        if self.tree.lwdata is not None:
            self.tree.lwdata.log(str)
        if str.startswith("<!= "):
            p=str.index("=!>")+3
            dlg=wx.MessageDialog(self, str[p:], "Alert", style=wx.OK|wx.ICON_EXCLAMATION)
            dlg.ShowModal()
            dlg.Destroy()
            self.OnLog("Alert dialog closed")
    log=OnLog
    def OnLogData(self, str, data, klass=None):
        if self.tree.lwdata is not None:
            self.tree.lwdata.logdata(str,data, klass)
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
        if self.wt is not None:
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
                try:
                    self.OnLog("Exception during exception swallowed")
                except AttributeError:
                    pass
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
