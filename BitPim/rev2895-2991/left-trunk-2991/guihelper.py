"Various helper routines for gui based code"
import os
import glob
import sys
import wx
import common  # note we modify the common module contents
ID_FILENEW=1
ID_FILEOPEN=1
ID_FILESAVE=1
ID_FILEIMPORT=1
ID_FILEEXPORT=1
ID_FILEPRINT=1
ID_FILEPRINTPREVIEW=1
ID_FILEEXIT=1
ID_EDITADDENTRY=1
ID_EDITDELETEENTRY=1
ID_EDITSELECTALL=1
ID_EDITSETTINGS=1
ID_EDITPHONEINFO=1
ID_EDITDETECT=1
ID_EDITCOPY=1
ID_EDITPASTE=1
ID_EDITRENAME=1
ID_DATAGETPHONE=1
ID_DATASENDPHONE=1
ID_DATAHISTORICAL=1
ID_AUTOSYNCSETTINGS=1
ID_AUTOSYNCEXECUTE=1
ID_VIEWCOLUMNS=1
ID_VIEWPREVIEW=1
ID_VIEWLOGDATA=1
ID_VIEWCLEARLOGS=1
ID_VIEWFILESYSTEM=1
ID_HELPHELP=1
ID_HELPCONTENTS=1
ID_HELPTOUR=1
ID_HELPSUPPORT=1
ID_HELP_UPDATE=1
ID_HELPABOUT=1
ID_FV_SAVE=1
ID_FV_HEXVIEW=1
ID_FV_OVERWRITE=1
ID_FV_NEWSUBDIR=1
ID_FV_NEWFILE=1
ID_FV_DELETE=1
ID_FV_OPEN=1
ID_FV_RENAME=1
ID_FV_REFRESH=1
ID_FV_PROPERTIES=1
ID_FV_ADD=1
ID_FV_BACKUP=1
ID_FV_BACKUP_TREE=1
ID_FV_RESTORE=1
ID_FV_PASTE=1
ID_FV_TOTAL_REFRESH=1
ID_FV_OFFLINEPHONE=1
ID_FV_REBOOTPHONE=1
ID_FV_MODEMMODE=1
ID_FV_COPY=1
idmap={}
for idmapname in locals().keys():
    if idmapname.startswith('ID_'):
        idnum=wx.NewId()
        exec "%s = %d" % (idmapname, idnum )
        idmap[idnum]=idmapname
_wxmimemapping={
    'bmp': 'image/x-bmp',
    'ico': 'image/x-ico',
    'cur': 'image/x-cur',
    'ani': 'image/x-ani',
    'gif': 'image/gif',
    'iff': 'image/iff',
    'jpg': 'image/jpeg',
    'jpeg': 'image/jpeg',
    'pcx': 'image/pcx',
    'png': 'image/png',
    'pnm': 'image/pnm',
    'xpm': 'image/xpm',
    }
def getwxmimetype(filename):
    "Returns wx's mime type for the extension of filename, or None"
    return _wxmimemapping.get(getextension(filename.lower()), None)
def BusyWrapper(method):
    def _busywrapper(*args, **kwargs):
        wx.BeginBusyCursor()
        try:
            return method(*args, **kwargs)
        finally:
            wx.EndBusyCursor()
    setattr(_busywrapper, "__doc__", getattr(method, "__doc__"))
    return _busywrapper
def getextension(str):
    """Returns the extension of a filename (characters after last period)
    An empty string is returned if the file has no extension.  The period
    character is not returned"""
    str=basename(str)
    if str.rfind('.')>=0:
        return str[str.rfind('.')+1:]
    return ""
def basename(str):
    """Returns the last part of the name (everything after last /)"""
    if str.rfind('/')<0: return str
    return str[str.rfind('/')+1:]
def dirname(str):
    """Returns everything before the last / in the name""" 
    if str.rfind('/')<0: return ""
    return str[:str.rfind('/')]
def IsMSWindows():
    """Are we running on Windows?
    @rtype: Bool"""
    return wx.Platform=='__WXMSW__'
def IsGtk():
    """Are we running on GTK (Linux)
    @rtype: Bool"""
    return wx.Platform=='__WXGTK__'
def IsMac():
    """Are we running on Mac
    @rtype: Bool"""
    return wx.Platform=='__WXMAC__'
def getbitmap(name):
    """Gets a bitmap from the resource directory
    @rtype: wxBitmap
    """
    return getimage(name).ConvertToBitmap()
def getimage(name):
    """Gets an image from the resource directory
    @rtype: wx.Image
    """
    for ext in ("", ".png", ".jpg"):
        if os.path.exists(getresourcefile(name+ext)):
            return wx.Image(getresourcefile(name+ext))
    print "You need to make "+name+".png"
    return getimage('unknown')
def getresourcefile(filename):
    """Returns name of file by adding it to resource directory pathname
    No attempt is made to verify the file exists
    @rtype: string
    """
    return os.path.join(resourcedirectory, filename)
def gethelpfilename():
    """Returns what name we use for the helpfile
    Without trailing extension as wxBestHelpController figures that out"""
    j=os.path.join
    paths=( (helpdirectory, True),
            (resourcedirectory, False) )
    if IsMSWindows():
        name="bitpim.chm"
    else:
        name="bitpim.htb"
    for p,mention in paths:
        if os.path.isfile(j(p, name)):
            if mention:
                print "Using help file from "+p
            return j(p, "bitpim")
    assert False
def getresourcefiles(wildcard):
    "Returns a list of filenames matching the wildcard in the resource directory"
    l=glob.glob(os.path.join(resourcedirectory, wildcard))
    l.sort()
    return l
resourcedirectory=os.path.join(common.get_main_dir(), 'resources')
helpdirectory=os.path.join(common.get_main_dir(), 'help')
if wx.USE_UNICODE:
    def strorunicode(s):
        if s is None: return s
        if isinstance(s, unicode): return s
        return str(s)
    common.strorunicode=strorunicode
    del strorunicode
else:
    def strorunicode(s):
        if s is None: return s
        try:
            return str(s)
        except UnicodeEncodeError:
            return s.encode("ascii", "replace")
    common.strorunicode=strorunicode
    del strorunicode
def GetOpenCommand(mimetypes, filename):
    for mt in mimetypes:
        ft=wx.TheMimeTypesManager.GetFileTypeFromMimeType(mt)
        if ft is None:
            continue
        cmd=ft.GetOpenCommand(filename)
        if cmd is not None and len(cmd):
            return cmd
    return None
_ourart={
    "ART_ADD_WALLPAPER": "add_picture",
    "ART_DEL_WALLPAPER": "delete_picture",
    "ART_ARROW_UP": "arrow_up",
    "ART_ARROW_DOWN": "arrow_down",
    "ART_ARROW_LEFT": "arrow_left",
    "ART_ARROW_RIGHT": "arrow_right",
    "ART_ADD_FIELD": "add_field",
    "ART_DEL_FIELD": "delete_field",
    "ART_ADD_CONTACT": "add_contact",
    "ART_DEL_CONTACT": "delete_contact",
    "ART_ADD_WALLPAPER": "add_picture",
    "ART_ADD_RINGER": "add_ringer",
    "ART_DEL_RINGER": "delete_ringer",
    "ART_ADD_MEMO": "add_memo",
    "ART_DEL_MEMO": "delete_memo",
    "ART_ADD_TODO": "add_todo",
    "ART_DEL_TODO": "delete_todo",
    "ART_ADD_SMS": "add_sms",
    "ART_DEL_SMS": "delete_sms",
    "ART_DATAGETPHONE": "datagetphone",
    "ART_DATASENDPHONE": "datasendphone",
    "ART_AUTOSYNCEXECUTE": "autosyncexecute",
    "ART_HELPHELP": "helphelp",
    "ART_EDITPHONEINFO": "editphoneinfo",
    "ART_EDITDETECT": "editdetect",
    "ART_EDITSETTINGS": "editsettings"
    }
for s in _ourart: globals()[s]=s
class ArtProvider(wx.ArtProvider):
    """ArtProvider manages the art for the application"""
    def CreateBitmap(self, artid, client, size):
        """Loads a bitmap and returns it depending on the parameters
        """
        if artid in _ourart:
            return getbitmap(_ourart[artid])
        print "not returning a bitmap for",artid
        return wx.NullBitmap
class MultiMessageBox:
        def __init__(self, parent, title="Bitpim", dlg_msg=""):
            self.__title=title
            self.__dlg_msg=dlg_msg
            self.__parent=parent
            self.__msgs={}
        def AddMessage(self, msg, priority=99):
            """
            Add a message to the list of messages to be displayed
            Each message appears on it's own line
            """
            loop=0
            key="%d.%05d" % (priority, loop)
            while self.__msgs.has_key(key):
                loop=loop+1
                key="%d.%05d" % (priority, loop)
            self.__msgs[key]={'msg': msg}
            return
        def ShowMessages(self, max_rows=0, max_columns=0):
            """
            Displays the messages in a list in a dialog
            max_rows: Max visible messages, if number of messages exceed
                    this a scroll bar will appear
            max_columns: Max visible width in characters
            returns: button pressed to exit dialog 
            """
            keys=self.__msgs.keys()
            keys.sort()
            out_list=[]
            for k in keys:
                msg=self.__msgs[k]['msg']
                out_list.append(msg)
            msg_dlg=wx.Dialog(self.__parent, -1, self.__title, 
                    style=wx.DEFAULT_DIALOG_STYLE|wx.RESIZE_BORDER|wx.SYSTEM_MENU|wx.MAXIMIZE_BOX|wx.MINIMIZE_BOX)
            main_bs=wx.BoxSizer(wx.VERTICAL)
            main_bs.Add(wx.StaticText(msg_dlg, -1, self.__dlg_msg), 0, wx.ALL|wx.ALIGN_LEFT, 5)
            msgs=wx.ListBox(msg_dlg)
            msgs.Set(out_list)
            main_bs.Add(msgs, 0, wx.ALL|wx.EXPAND, 5)
            main_bs.Add(wx.Button(msg_dlg, wx.ID_OK, 'OK'), 0, wx.ALIGN_CENTRE|wx.ALL, 5)
            msg_dlg.SetSizer(main_bs)
            msg_dlg.SetAutoLayout(True)
            main_bs.Fit(msg_dlg)
            res=msg_dlg.ShowModal()
            print "multi "+`res`
            msg_dlg.Destroy()
            return res
        def MsgCount(self):
            return len(self.__msgs)
        def ClearMessages(self):
            self.__msgs.clear()
