"""A widget for displaying/editting the phone information
The format for a phonebook entry is standardised.  It is a
dict with the following fields.  Each field is a list, most
important first, with each item in the list being a dict.
names:
   - title      ??Job title or salutation??
   - first
   - middle
   - last
   - full       You should specify the fullname or the 4 above
   - nickname   (This could also be an alias)
categories:
  - category    User defined category name
emails:
  - email       Email address
  - type        (optional) 'home' or 'business'
urls:
  - url         URL
  - type        (optional) 'home' or 'business'
ringtones:
  - ringtone    Name of a ringtone
  - use         'call', 'message'
addresses:
  - type        'home' or 'business'
  - company     (only for type of 'business')
  - street      Street part of address
  - street2     Second line of street address
  - city
  - state
  - postalcode
  - country     Can also be the region
wallpapers:
  - wallpaper   Name of wallpaper
  - use         see ringtones.use
flags:
  - secret     Boolean if record is private/secret (if not present - value is false)
  - sim        Boolean if record should be stored on SIM card of GSM phones.
memos:
  - memo       Note
numbers:
  - number     Phone number as ascii string
  - type       'home', 'office', 'cell', 'fax', 'pager', 'data', 'none'  (if you have home2 etc, list
               them without the digits.  The second 'home' is implicitly home2 etc)
  - speeddial  (optional) Speed dial number
serials:
  - sourcetype        identifies source driver in bitpim (eg "lgvx4400", "windowsaddressbook")
  - sourceuniqueid    (optional) identifier for where the serial came from (eg ESN of phone, wab host/username)
                      (imagine having multiple phones of the same model to see why this is needed)
  - *                 other names of use to sourcetype
"""
import os
import cStringIO
import re
import time
import copy
import wx
import wx.grid
import wx.html
import common
import xyaptu
import guihelper
import phonebookentryeditor
import pubsub
import nameparser
import bphtml
import guiwidgets
import phonenumber
import helpids
import database
class phonebookdataobject(database.basedataobject):
    _knownlistproperties=database.basedataobject._knownlistproperties.copy()
    _knownlistproperties.update( {'names': ['title', 'first', 'middle', 'last', 'full', 'nickname'],
                                  'categories': ['category'],
                                  'emails': ['email', 'type'],
                                  'urls': ['url', 'type'],
                                  'ringtones': ['ringtone', 'use'],
                                  'addresses': ['type', 'company', 'street', 'street2', 'city', 'state', 'postalcode', 'country'],
                                  'wallpapers': ['wallpaper', 'use'],
                                  'flags': ['secret', 'sim'],
                                  'memos': ['memo'],
                                  'numbers': ['number', 'type', 'speeddial'],
                                  })
phonebookobjectfactory=database.dataobjectfactory(phonebookdataobject)
class PhoneEntryDetailsView(bphtml.HTMLWindow):
    def __init__(self, parent, id, stylesfile="styles.xy", layoutfile="pblayout.xy"):
        bphtml.HTMLWindow.__init__(self, parent, id)
        self.stylesfile=guihelper.getresourcefile(stylesfile)
        self.pblayoutfile=guihelper.getresourcefile(layoutfile)
        self.xcp=None
        self.xcpstyles=None
        self.ShowEntry({})
    def ShowEntry(self, entry):
        if self.xcp is None:
            template=open(self.pblayoutfile, "rt").read()
            self.xcp=xyaptu.xcopier(None)
            self.xcp.setupxcopy(template)
        if self.xcpstyles is None:
            self.xcpstyles={}
            try:
                execfile(self.stylesfile,  self.xcpstyles, self.xcpstyles)
            except UnicodeError:
                common.unicode_execfile(self.stylesfile,  self.xcpstyles, self.xcpstyles)
        self.xcpstyles['entry']=entry
        text=self.xcp.xcopywithdns(self.xcpstyles)
        try:
            text=bphtml.applyhtmlstyles(text, self.xcpstyles['styles'])
        except:
            if __debug__:
                open("debug.html", "wt").write(common.forceascii(text))
            raise
        self.SetPage(text)
def formatcategories(cats):
    c=[cat['category'] for cat in cats]
    c.sort()
    return "; ".join(c)
def formataddress(address):
    l=[]
    for i in 'company', 'street', 'street2', 'city', 'state', 'postalcode', 'country':
        if i in address:
            l.append(address[i])
    return "; ".join(l)
def formattypenumber(number):
    t=number['type']
    t=t[0].upper()+t[1:]
    sd=number.get("speeddial", None)
    if sd is None:
        return "%s (%s)" % (phonenumber.format(number['number']), t)
    return "%s [%d] (%s)" % (phonenumber.format(number['number']), sd, t)
def formatnumber(number):
    sd=number.get("speeddial", None)
    if sd is None:
        return phonenumber.format(number['number'])
    return "%s [%d]" % (phonenumber.format(number['number']), sd)
def formatstorage(flags):
    for l in flags:
        if l.has_key('sim'):
            if l['sim']:
                return 'SIM'
    return ''
_getdatalist=[
    'Name', ("names", 0, None, nameparser.formatfullname, True),
    'First', ("names", 0, None, nameparser.getfirst, False),
    'Middle', ("names", 0, None, nameparser.getmiddle, False),
    'Last', ("names", 0, None, nameparser.getlast, False),
    'Category', ("categories", 0,  None, "category", False),
    'Category2', ("categories", 1,  None, "category", False),
    'Category3', ("categories", 2,  None, "category", False),
    'Category4', ("categories", 3,  None, "category", False),
    'Category5', ("categories", 4,  None, "category", False),
    'Categories', ("categories", None, None, formatcategories, True),
    "Phone", ("numbers", 0, None, formattypenumber, False),
    "Phone2", ("numbers", 1, None, formattypenumber, False),
    "Phone3", ("numbers", 2, None, formattypenumber, False),
    "Phone4", ("numbers", 3, None, formattypenumber, False),
    "Phone5", ("numbers", 4, None, formattypenumber, False),
    "Phone6", ("numbers", 5, None, formattypenumber, False),
    "Phone7", ("numbers", 6, None, formattypenumber, False),
    "Phone8", ("numbers", 7, None, formattypenumber, False),
    "Phone9", ("numbers", 8, None, formattypenumber, False),
    "Phone10", ("numbers", 9, None, formattypenumber, False),
    'Email', ("emails", 0, None, "email", True),
    'Email2', ("emails", 1, None, "email", True),
    'Email3', ("emails", 2, None, "email", True),
    'Email4', ("emails", 3, None, "email", True),
    'Email5', ("emails", 4, None, "email", True),
    'Business Email', ("emails", 0, ("type", "business"), "email", False),
    'Business Email2', ("emails", 1, ("type", "business"), "email", False),
    'Home Email', ("emails", 0, ("type", "home"), "email", False),
    'Home Email2', ("emails", 1, ("type", "home"), "email", False),
    'URL', ("urls", 0, None, "url", True),
    'URL2', ("urls", 1, None, "url", True),
    'URL3', ("urls", 2, None, "url", True),
    'URL4', ("urls", 3, None, "url", True),
    'URL5', ("urls", 4, None, "url", True),
    'Business URL', ("urls", 0, ("type", "business"), "url", False),
    'Business URL2', ("urls", 1, ("type", "business"), "url", False),
    'Home URL', ("urls", 0, ("type", "home"), "url", False),
    'Home URL2', ("urls", 1, ("type", "home"), "url", False),
    'Ringtone', ("ringtones", 0, ("use", "call"), "ringtone", True),
    'Message Ringtone', ("ringtones", 0, ("use", "message"), "ringtone", True),
    'Address', ("addresses", 0, None, formataddress, True),
    'Address2', ("addresses", 1, None, formataddress, True),
    'Address3', ("addresses", 2, None, formataddress, True),
    'Address4', ("addresses", 3, None, formataddress, True),
    'Address5', ("addresses", 4, None, formataddress, True),
    'Home Address', ("addresses", 0, ("type", "home"), formataddress, False),
    'Home Address2', ("addresses", 1, ("type", "home"), formataddress, False),
    'Business Address', ("addressess", 0, ("type", "business"), formataddress, False),
    'Business Address2', ("addressess", 1, ("type", "business"), formataddress, False),
    "Wallpaper", ("wallpapers", 0, None, "wallpaper", True),
    "Secret", ("flags", 0, ("secret", True), "secret", True),
    "Storage", ("flags", None, None, formatstorage, True),
    "Memo", ("memos", 0, None, "memo", True),
    "Memo2", ("memos", 1, None, "memo", True),
    "Memo3", ("memos", 2, None, "memo", True),
    "Memo4", ("memos", 3, None, "memo", True),
    "Memo5", ("memos", 4, None, "memo", True),
    ]
ll=[]
for pretty, actual in ("Home", "home"), ("Office", "office"), ("Cell", "cell"), ("Fax", "fax"), ("Pager", "pager"), ("Data", "data"):
    for suf,n in ("", 0), ("2", 1), ("3", 2):
        ll.append(pretty+suf)
        ll.append(("numbers", n, ("type", actual), formatnumber, True))
_getdatalist[40:40]=ll
_getdatatable={}
AvailableColumns=[]
DefaultColumns=['Name', 'Phone', 'Phone2', 'Phone3', 'Email', 'Categories', 'Memo', 'Secret']
ImportColumns=[_getdatalist[x*2] for x in range(len(_getdatalist)/2) if _getdatalist[x*2+1][4]]
for n in range(len(_getdatalist)/2):
    AvailableColumns.append(_getdatalist[n*2])
    _getdatatable[_getdatalist[n*2]]=_getdatalist[n*2+1]
del _getdatalist  # so we don't accidentally use it
def getdata(column, entry, default=None):
    """Returns the value in a particular column.
    Note that the data is appropriately formatted.
    @param column: column name
    @param entry: the dict representing a phonebook entry
    @param default: what to return if the entry has no data for that column
    """
    key, count, prereq, formatter, _ =_getdatatable[column]
    if key not in entry:
        return default
    if count is None:
        thevalue=entry[key]
    elif prereq is None:
        if len(entry[key])<=count:
            return default
        thevalue=entry[key][count]
    else:
        ptr=0
        togo=count+1
        l=entry[key]
        k,v=prereq
        while togo:
            if ptr==len(l):
                return default
            if k not in l[ptr]:
                ptr+=1
                continue
            if l[ptr][k]!=v:
                ptr+=1
                continue
            togo-=1
            if togo!=0:
                ptr+=1
                continue
            thevalue=entry[key][ptr]
            break
    if callable(formatter):
        return formatter(thevalue)
    return thevalue.get(formatter, default)
def getdatainfo(column, entry):
    """Similar to L{getdata} except returning higher level information.
    Returns the key name and which index from the list corresponds to
    the column.
    @param column: Column name
    @param entry: The dict representing a phonebook entry
    @returns: (keyname, index) tuple.  index will be None if the entry doesn't
         have the relevant column value and -1 if all of them apply
    """
    key, count, prereq, formatter, _ =_getdatatable[column]
    if key not in entry:
        return (key, None)
    if count is None:
        return (key, -1)
    elif prereq is None:
        if len(entry[key])<=count:
            return (key, None)
        return (key, count)
    else:
        ptr=0
        togo=count+1
        l=entry[key]
        k,v=prereq
        while togo:
            if ptr==len(l):
                return (key,None)
            if k not in l[ptr]:
                ptr+=1
                continue
            if l[ptr][k]!=v:
                ptr+=1
                continue
            togo-=1
            if togo!=0:
                ptr+=1
                continue
            return (key, ptr)
    return (key, None)
class CategoryManager:
    __publisher=pubsub.Publisher
    def __init__(self):
        self.categories=[]
        pubsub.subscribe(self.OnListRequest, pubsub.REQUEST_CATEGORIES)
        pubsub.subscribe(self.OnSetCategories, pubsub.SET_CATEGORIES)
        pubsub.subscribe(self.OnMergeCategories, pubsub.MERGE_CATEGORIES)
        pubsub.subscribe(self.OnAddCategory, pubsub.ADD_CATEGORY)
    def OnListRequest(self, msg=None):
        pubsub.publish(pubsub.ALL_CATEGORIES, self.categories[:])
    def OnAddCategory(self, msg):
        name=msg.data
        if name in self.categories:
            return
        self.categories.append(name)
        self.categories.sort()
        self.OnListRequest()
    def OnSetCategories(self, msg):
        cats=msg.data[:]
        self.categories=cats
        self.categories.sort()
        self.OnListRequest()
    def OnMergeCategories(self, msg):
        cats=msg.data[:]
        newcats=self.categories[:]
        for i in cats:
            if i not in newcats:
                newcats.append(i)
        newcats.sort()
        if newcats!=self.categories:
            self.categories=newcats
            self.OnListRequest()
CategoryManager=CategoryManager() # shadow out class name
class PhoneDataTable(wx.grid.PyGridTableBase):
    def __init__(self, widget, columns):
        self.main=widget
        self.rowkeys=self.main._data.keys()
        wx.grid.PyGridTableBase.__init__(self)
        self.oddattr=wx.grid.GridCellAttr()
        self.oddattr.SetBackgroundColour("OLDLACE")
        self.evenattr=wx.grid.GridCellAttr()
        self.evenattr.SetBackgroundColour("ALICE BLUE")
        self.columns=columns
        assert len(self.rowkeys)==0  # we can't sort here, and it isn't necessary because list is zero length
    def GetColLabelValue(self, col):
        return self.columns[col]
    def OnDataUpdated(self):
        newkeys=self.main._data.keys()
        newkeys.sort()
        oldrows=self.rowkeys
        self.rowkeys=newkeys
        lo=len(oldrows)
        ln=len(self.rowkeys)
        if ln>lo:
            msg=wx.grid.GridTableMessage(self, wx.grid.GRIDTABLE_NOTIFY_ROWS_APPENDED, ln-lo)
        elif lo>ln:
            msg=wx.grid.GridTableMessage(self, wx.grid.GRIDTABLE_NOTIFY_ROWS_DELETED, 0, lo-ln)
        else:
            msg=None
        if msg is not None:
            self.GetView().ProcessTableMessage(msg)
        self.Sort()
        msg=wx.grid.GridTableMessage(self, wx.grid.GRIDTABLE_REQUEST_VIEW_GET_VALUES)
        self.GetView().ProcessTableMessage(msg)
        self.GetView().AutoSizeColumns()
    def SetColumns(self, columns):
        oldcols=self.columns
        self.columns=columns
        lo=len(oldcols)
        ln=len(self.columns)
        if ln>lo:
            msg=wx.grid.GridTableMessage(self, wx.grid.GRIDTABLE_NOTIFY_COLS_APPENDED, ln-lo)
        elif lo>ln:
            msg=wx.grid.GridTableMessage(self, wx.grid.GRIDTABLE_NOTIFY_COLS_DELETED, 0, lo-ln)
        else:
            msg=None
        if msg is not None:
            self.GetView().ProcessTableMessage(msg)
        msg=wx.grid.GridTableMessage(self, wx.grid.GRIDTABLE_REQUEST_VIEW_GET_VALUES)
        self.GetView().ProcessTableMessage(msg)
        self.GetView().AutoSizeColumns()
    def Sort(self):
        bycol=self.main.sortedColumn
        descending=self.main.sortedColumnDescending
        l=[ (getdata(self.columns[bycol], self.main._data[key]), key) for key in self.rowkeys]
        l.sort()
        if descending:
            l.reverse()
        self.rowkeys=[key for val,key in l]
        msg=wx.grid.GridTableMessage(self, wx.grid.GRIDTABLE_REQUEST_VIEW_GET_VALUES)
        self.GetView().ProcessTableMessage(msg)
    def IsEmptyCell(self, row, col):
        return False
    def GetNumberRows(self):
        return len(self.rowkeys)
    def GetNumberCols(self):
        return len(self.columns)
    def GetValue(self, row, col):
        try:
            entry=self.main._data[self.rowkeys[row]]
        except:
            print "bad row", row
            return "<error>"
        return getdata(self.columns[col], entry, "")
    def GetAttr(self, row, col, _):
        r=[self.evenattr, self.oddattr][row%2]
        r.IncRef()
        return r
thephonewidget=None  # track the instance
class PhoneWidget(wx.Panel):
    """Main phone editing/displaying widget"""
    CURRENTFILEVERSION=2
    _Current_Data=0
    _Historic_Data=1
    def __init__(self, mainwindow, parent, config):
        global thephonewidget
        thephonewidget=self
        wx.Panel.__init__(self, parent,-1)
        self.categorymanager=CategoryManager
        split=wx.SplitterWindow(self, -1, style=wx.SP_3D|wx.SP_LIVE_UPDATE)
        split.SetMinimumPaneSize(20)
        self.mainwindow=mainwindow
        self._data={}
        self.categories=[]
        self.modified=False
        self.table_panel=wx.Panel(split)
        self.table=wx.grid.Grid(self.table_panel, wx.NewId())
        self.table.EnableGridLines(False)
        self.error_log=guihelper.MultiMessageBox(self.mainwindow , "Contact Export Errors", 
                   "Bitpim is unable to send the following contacts to your phone")
        cur=config.Read("phonebookcolumns", "")
        if len(cur):
            cur=cur.split(",")
            cur=[c for c in cur if c in AvailableColumns]
        else:
            cur=DefaultColumns
        self.sortedColumn=0
        self.sortedColumnDescending=False
        self.dt=PhoneDataTable(self, cur)
        self.table.SetTable(self.dt, False, wx.grid.Grid.wxGridSelectRows)
        self.table.SetSelectionMode(wx.grid.Grid.wxGridSelectRows)
        self.table.SetRowLabelSize(0)
        self.table.EnableEditing(False)
        self.table.EnableDragRowSize(False)
        self.table.SetMargins(1,0)
        hbs=wx.BoxSizer(wx.HORIZONTAL)
        self.read_only=False
        self.historical_date=None
        static_bs=wx.StaticBoxSizer(wx.StaticBox(self.table_panel, -1,
                                                 'Historical Data Status:'),
                                    wx.VERTICAL)
        self.historical_data_label=wx.StaticText(self, -1, 'Current Data')
        static_bs.Add(self.historical_data_label, 1, wx.EXPAND|wx.ALL, 5)
        hbs.Add(static_bs, 1, wx.EXPAND|wx.ALL, 5)
        vbs=wx.BoxSizer(wx.VERTICAL)
        vbs.Add(hbs, 0, wx.EXPAND|wx.ALL, 5)
        vbs.Add(self.table, 1, wx.EXPAND, 0)
        self.table_panel.SetSizer(vbs)
        self.table_panel.SetAutoLayout(True)
        vbs.Fit(self.table_panel)
        self.preview=PhoneEntryDetailsView(split, -1, "styles.xy", "pblayout.xy")
        wx.CallAfter(self.preview.ShowEntry, {})
        self.sash_pos=None
        split.SplitVertically(self.table_panel, self.preview, -300)
        self.split=split
        bs=wx.BoxSizer(wx.VERTICAL)
        bs.Add(split, 1, wx.EXPAND)
        self.SetSizer(bs)
        self.SetAutoLayout(True)
        wx.EVT_IDLE(self, self.OnIdle)
        wx.grid.EVT_GRID_SELECT_CELL(self, self.OnCellSelect)
        wx.grid.EVT_GRID_CELL_LEFT_DCLICK(self, self.OnCellDClick)
        wx.grid.EVT_GRID_CELL_RIGHT_CLICK(self, self.OnCellRightClick)
        wx.EVT_LEFT_DCLICK(self.preview, self.OnPreviewDClick)
        pubsub.subscribe(self.OnCategoriesUpdate, pubsub.ALL_CATEGORIES)
        pubsub.subscribe(self.OnPBLookup, pubsub.REQUEST_PB_LOOKUP)
        pubsub.subscribe(self.OnMediaNameChanged, pubsub.MEDIA_NAME_CHANGED)
        wx.EVT_PAINT(self.table.GetGridColLabelWindow(), self.OnColumnHeaderPaint)
        wx.grid.EVT_GRID_LABEL_LEFT_CLICK(self.table, self.OnGridLabelLeftClick)
        wx.grid.EVT_GRID_LABEL_LEFT_DCLICK(self.table, self.OnGridLabelLeftClick)
        self.context_menu=wx.Menu()
        id=wx.NewId()
        self.context_menu.Append(id, 'Set to current',
                                 'Set the selected item to current data')
        wx.EVT_MENU(self, id, self.OnSetToCurrent)
    def OnColumnHeaderPaint(self, evt):
        w = self.table.GetGridColLabelWindow()
        dc = wx.PaintDC(w)
        font = dc.GetFont()
        dc.SetTextForeground(wx.BLACK)
        totColSize = -self.table.GetViewStart()[0]*self.table.GetScrollPixelsPerUnit()[0]
        for col in range(self.table.GetNumberCols()):
            dc.SetBrush(wx.Brush("WHEAT", wx.TRANSPARENT))
            colSize = self.table.GetColSize(col)
            rect = (totColSize,0,colSize,32)
            dc.DrawRectangle(rect[0] - (col!=0 and 1 or 0), rect[1], rect[2] + (col!=0 and 1 or 0), rect[3])
            totColSize += colSize
            if col == self.sortedColumn:
                font.SetWeight(wx.BOLD)
                left = rect[0] + 3
                top = rect[1] + 3
                dc.SetBrush(wx.Brush("WHEAT", wx.SOLID))
                if self.sortedColumnDescending:
                    dc.DrawPolygon([(left,top), (left+6,top), (left+3,top+4)])
                else:
                    dc.DrawPolygon([(left+3,top), (left+6, top+4), (left, top+4)])
            else:
                font.SetWeight(wx.NORMAL)
            dc.SetFont(font)
            dc.DrawLabel("%s" % self.table.GetTable().columns[col],
                     rect, wx.ALIGN_CENTER | wx.ALIGN_TOP)
    def OnGridLabelLeftClick(self, evt):
        col=evt.GetCol()
        if col==self.sortedColumn:
            self.sortedColumnDescending=not self.sortedColumnDescending
        else:
            self.sortedColumn=col
            self.sortedColumnDescending=False
        self.dt.Sort()
        self.table.Refresh()
    def SetColumns(self, columns):
        c=self.GetColumns()[self.sortedColumn]
        self.dt.SetColumns(columns)
        if c in columns:
            self.sortedColumn=columns.index(c)
        else:
            self.sortedColumn=0
            self.sortedColumnDescending=False
        self.dt.Sort()
        self.table.Refresh()
    def GetColumns(self):
        return self.dt.columns
    def OnCategoriesUpdate(self, msg):
        if self.categories!=msg.data:
            self.categories=msg.data[:]
            self.modified=True
    def OnPBLookup(self, msg):
        d=msg.data
        s=d.get('item', '')
        if not len(s):
            return
        d['name']=None
        for k,e in self._data.items():
            for n in e.get('numbers', []):
                if s==n.get('number', None):
                    d['name']=nameparser.getfullname(e['names'][0])+'('+\
                               n.get('type', '')+')'
                    pubsub.publish(pubsub.RESPONSE_PB_LOOKUP, d)
                    return
            for n in e.get('emails', []):
                if s==n.get('email', None):
                    d['name']=nameparser.getfullname(e['names'][0])+'(email)'
                    pubsub.publish(pubsub.RESPONSE_PB_LOOKUP, d)
                    return
        pubsub.publish(pubsub.RESPONSE_PB_LOOKUP, d)
    def OnMediaNameChanged(self, msg):
        d=msg.data
        _type=d.get(pubsub.media_change_type, None)
        _old_name=d.get(pubsub.media_old_name, None)
        _new_name=d.get(pubsub.media_new_name, None)
        if _type is None or _old_name is None or _new_name is None:
            return
        if _type!=pubsub.wallpaper_type and \
           _type!=pubsub.ringtone_type:
            return
        _old_name=common.basename(_old_name)
        _new_name=common.basename(_new_name)
        if _type==pubsub.wallpaper_type:
            main_key='wallpapers'
            element_key='wallpaper'
        else:
            main_key='ringtones'
            element_key='ringtone'
        for k,e in self._data.items():
            for i,n in enumerate(e.get(main_key, [])):
                if _old_name==n.get(element_key, None):
                    self._data[k][main_key][i][element_key]=_new_name
                    self.modified=True
    def OnViewPreview(self, preview_on):
        if preview_on:
            self.split.SplitVertically(self.table_panel, self.preview,
                                       self.sash_pos)
        else:
            if self.sash_pos is None:
                self.sash_pos=-300
            else:
                self.sash_pos=self.split.GetSashPosition()
            self.split.Unsplit(self.preview)
        self.dt.GetView().AutoSizeColumns()
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
                                            self.mainwindow.database.getchangescount('phonebook'))
        if dlg.ShowModal()==wx.ID_OK:
            self.mainwindow.OnBusyStart()
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
            self.populate(r, False)
            self.historical_data_label.SetLabel(msg_str)
            self.mainwindow.OnBusyEnd()
        dlg.Destroy()
    def OnIdle(self, _):
        "We save out changed data"
        if self.modified:
            self.modified=False
            self.populatefs(self.getdata({}))
    def updateserials(self, results):
        "update the serial numbers after having written to the phone"
        if not results.has_key('serialupdates'):
            return
        for bpserial,updserial in results['serialupdates']:
            for k in self._data:
                entry=self._data[k]
                if not entry.has_key('serials'):
                    assert False, "serials have gone horribly wrong"
                    continue
                found=False
                for serial in entry['serials']:
                    if bpserial==serial:
                        found=True
                        break
                if not found:
                    continue
                st=updserial['sourcetype']
                remove=None
                for serial in entry['serials']:
                    if serial['sourcetype']!=st:
                        continue
                    if updserial.has_key("sourceuniqueid"):
                        if updserial["sourceuniqueid"]!=serial.get("sourceuniqueid", None):
                            continue
                    remove=serial
                    break
                if remove is not None:
                    for count,serial in enumerate(entry['serials']):
                        if remove==serial:
                            break
                    del entry['serials'][count]
                entry['serials'].append(updserial)
        self.modified=True
    def OnSelectAll(self, _):
        self.table.SelectAll()
    def OnCellSelect(self, event):
        event.Skip()
        row=event.GetRow()
        self.SetPreview(self._data[self.dt.rowkeys[row]]) # bad breaking of abstraction referencing dt!
    def OnPreviewDClick(self, _):
        self.EditEntries(self.table.GetGridCursorRow(), self.table.GetGridCursorCol())
    def OnCellDClick(self, event):
        self.EditEntries(event.GetRow(), event.GetCol())
    def OnCellRightClick(self, evt):
        if not self.read_only or not self.GetSelectedRowKeys():
            return
        self.table.PopupMenu(self.context_menu, evt.GetPosition())
    def OnSetToCurrent(self, _):
        r={}
        for k in self.GetSelectedRowKeys():
            r[k]=self._data[k]
        if r:
            dict={}
            self.getfromfs(dict)
            dict['phonebook'].update(r)
            c=[e for e in self.categories if e not in dict['categories']]
            dict['categories']+=c
            self._save_db(dict)
    def EditEntries(self, row, column):
        key=self.dt.rowkeys[row]
        data=self._data[key]
        datakey,dataindex=getdatainfo(self.GetColumns()[column], data)
        _keys=self.GetSelectedRowKeys()
        if datakey in ('categories', 'ringtones', 'wallpapers') and \
           len(_keys)>1 and not self.read_only:
            dlg=phonebookentryeditor.SingleFieldEditor(self, datakey)
            if dlg.ShowModal()==wx.ID_OK:
                _data=dlg.GetData()
                if _data:
                    for r in _keys:
                        self._data[r][datakey]=_data
                else:
                    for r in _keys:
                        del self._data[r][datakey]
                self.SetPreview(self._data[_keys[0]])
                self.dt.OnDataUpdated()
                self.modified=True
        else:
            dlg=phonebookentryeditor.Editor(self, data,
                                            factory=phonebookobjectfactory,
                                            keytoopenon=datakey,
                                            dataindex=dataindex,
                                            readonly=self.read_only,
                                            datakey=key,
                                            movement=True)
            if dlg.ShowModal()==wx.ID_OK:
                self.SaveData(dlg.GetData(), dlg.GetDataKey())
        dlg.Destroy()
    def SaveData(self, data, key):
        self._data[key]=data
        self.dt.OnDataUpdated()
        self.SetPreview(data)
        self.modified=True
    def EditEntry(self, row, column):
        key=self.dt.rowkeys[row]
        data=self._data[key]
        datakey,dataindex=getdatainfo(self.GetColumns()[column], data)
        dlg=phonebookentryeditor.Editor(self, data,
                                        factory=phonebookobjectfactory,
                                        keytoopenon=datakey,
                                        dataindex=dataindex,
                                        readonly=self.read_only)
        if dlg.ShowModal()==wx.ID_OK:
            data=dlg.GetData()
            self._data[key]=data
            self.dt.OnDataUpdated()
            self.SetPreview(data)
            self.modified=True
        dlg.Destroy()
    def GetNextEntry(self, next=True):
        _sel_rows=self.GetSelectedRows()
        if not _sel_rows:
            return None
        try:
            row=_sel_rows[0]
            if next:
                _new_row=row+1
            else:
                _new_row=row-1
            _num_rows=self.table.GetNumberRows()
            if _new_row>=_num_rows:
                _new_row=0
            elif _new_row<0:
                _new_row=_num_rows-1
            self.table.SetGridCursor(_new_row, self.table.GetGridCursorCol())
            self.table.SelectRow(_new_row)
            _key=self.dt.rowkeys[_new_row]
            return (_key,self._data[_key])
        except:
            if __debug__:
                raise
            return None
    def OnAdd(self, _):
        if self.read_only:
            return
        dlg=phonebookentryeditor.Editor(self, {'names': [{'full': 'New Entry'}]}, keytoopenon="names", dataindex=0)
        if dlg.ShowModal()==wx.ID_OK:
            data=phonebookobjectfactory.newdataobject(dlg.GetData())
            data.EnsureBitPimSerial()
            while True:
                key=int(time.time())
                if key in self._data:
                    continue
                break
            self._data[key]=data
            self.dt.OnDataUpdated()
            self.SetPreview(data)
            self.modified=True
        dlg.Destroy()
    def GetSelectedRows(self):
        rows=[]
        if len(self._data)==0:
            return rows
        gcr=self.table.GetGridCursorRow()
        set1=self.table.GetSelectionBlockTopLeft()
        set2=self.table.GetSelectionBlockBottomRight()
        if len(set1):
            assert len(set1)==len(set2)
            for i in range(len(set1)):
                for row in range(set1[i][0], set2[i][0]+1): # range in wx is inclusive of last element
                    if row not in rows:
                        rows.append(row)
        else:
            if gcr>=0:
                rows.append(gcr)
        return rows
    def GetSelectedRowKeys(self):
        return [self.dt.rowkeys[r] for r in self.GetSelectedRows()]
    def OnDelete(self,_):
        if self.read_only:
            return
        for r in self.GetSelectedRowKeys():
            del self._data[r]
        self.table.ClearSelection()
        self.dt.OnDataUpdated()
        self.modified=True
    def SetPreview(self, entry):
        self.preview.ShowEntry(entry)
    def OnPrintDialog(self, mainwindow, config):
        dlg=PhonebookPrintDialog(self, mainwindow, config)
        dlg.ShowModal()
        dlg.Destroy()
    def getdata(self, dict):
        dict['phonebook']=self._data.copy()
        dict['categories']=self.categories[:]
        return dict
    def DeleteBySerial(self, bpserial):
        for k in self._data:
            entry=self._data[k]
            for serial in entry['serials']:
                if serial==bpserial:
                    del self._data[k]
                    self.dt.OnDataUpdated()
                    self.modified=True
                    return
        raise ValueError("No such entry with serial "+`bpserial`)
    def UpdateSerial(self, bpserial, otherserial):
        try:
            for k in self._data:
                entry=self._data[k]
                for serial in entry['serials']:
                    if serial==bpserial:
                        for i,serial in enumerate(entry['serials']):
                            if serial["sourcetype"]==otherserial["sourcetype"]:
                                if otherserial.has_key("sourceuniqueid") and \
                                   serial["sourceuniqueid"]==otherserial["sourceuniqueid"]:
                                    entry['serials'][i]=otherserial
                                    return
                                elif not otherserial.has_key("sourceuniqueid"):
                                    entry['serials'][i]=otherserial
                                    return
                        entry['serials'].append(otherserial)
                        return
            raise ValueError("No such entry with serial "+`bpserial`)
        finally:
            self.modified=True
    def versionupgrade(self, dict, version):
        """Upgrade old data format read from disk
        @param dict:  The dict that was read in
        @param version: version number of the data on disk
        """
        if version==0:
            version=1  # they are the same
        if version==1:
            wx.MessageBox("BitPim can't upgrade your old phone data stored on disk, and has discarded it.  Please re-read your phonebook from the phone.  If you downgrade, please delete the phonebook directory in the BitPim data directory first", "Phonebook file format not supported", wx.OK|wx.ICON_EXCLAMATION)
            version=2
            dict['result']['phonebook']={}
            dict['result']['categories']=[]
    def clear(self):
        self._data={}
        self.dt.OnDataUpdated()
    def getfromfs(self, dict, timestamp=None):
        self.thedir=self.mainwindow.phonebookpath
        if os.path.exists(os.path.join(self.thedir, "index.idx")):
            d={'result': {'phonebook': {}, 'categories': []}}
            common.readversionedindexfile(os.path.join(self.thedir, "index.idx"), d, self.versionupgrade, self.CURRENTFILEVERSION)
            pb=d['result']['phonebook']
            database.ensurerecordtype(pb, phonebookobjectfactory)
            pb=database.extractbitpimserials(pb)
            self.mainwindow.database.savemajordict("phonebook", pb)
            self.mainwindow.database.savelist("categories", d['result']['categories'])
            os.rename(os.path.join(self.thedir, "index.idx"), os.path.join(self.thedir, "index-is-now-in-database.bak"))
        dict['phonebook']=self.mainwindow.database.getmajordictvalues(
            "phonebook", phonebookobjectfactory, at_time=timestamp)
        dict['categories']=self.mainwindow.database.loadlist("categories")
    def populate(self, dict, savetodb=True):
        if self.read_only and savetodb:
            wx.MessageBox('You are viewing historical data which cannot be changed or saved',
                             'Cannot Save Phonebook Data',
                             style=wx.OK|wx.ICON_ERROR)
            return            
        self.clear()
        pubsub.publish(pubsub.MERGE_CATEGORIES, dict['categories'])
        pb=dict['phonebook']
        cats=[]
        for i in pb:
            for cat in pb[i].get('categories', []):
                cats.append(cat['category'])
        pubsub.publish(pubsub.MERGE_CATEGORIES, cats)                
        k=pb.keys()
        k.sort()
        self.clear()
        self._data=pb.copy()
        self.dt.OnDataUpdated()
        self.modified=savetodb
    def _save_db(self, dict):
        self.mainwindow.database.savemajordict("phonebook", database.extractbitpimserials(dict["phonebook"]))
        self.mainwindow.database.savelist("categories", dict["categories"])
    def populatefs(self, dict):
        if self.read_only:
            wx.MessageBox('You are viewing historical data which cannot be changed or saved',
                             'Cannot Save Phonebook Data',
                             style=wx.OK|wx.ICON_ERROR)
        else:
            self._save_db(dict)
        return dict
    def importdata(self, importdata, categoriesinfo=[], merge=True):
        if self.read_only:
            wx.MessageBox('You are viewing historical data which cannot be changed or saved',
                             'Cannot Save Phonebook Data',
                             style=wx.OK|wx.ICON_ERROR)
            return
        if merge:
            d=self._data
        else:
            d={}
        normalise_data(importdata)
        dlg=ImportDialog(self, d, importdata)
        result=None
        if dlg.ShowModal()==wx.ID_OK:
            result=dlg.resultdata
        guiwidgets.save_size("PhoneImportMergeDialog", dlg.GetRect())
        dlg.Destroy()
        if result is not None:
            d={}
            database.ensurerecordtype(result, phonebookobjectfactory)
            database.ensurebitpimserials(result)
            d['phonebook']=result
            d['categories']=categoriesinfo
            self.populatefs(d)
            self.populate(d, False)
    def converttophone(self, data):
        self.error_log.ClearMessages()
        self.mainwindow.phoneprofile.convertphonebooktophone(self, data)
        if self.error_log.MsgCount():
            self.error_log.ShowMessages()
        return
    def add_error_message(self, msg, priority=99):
        self.error_log.AddMessage(msg, priority)
    class ConversionFailed(Exception):
        pass
    def _getentries(self, list, min, max, name):
        candidates=[]
        for i in list:
            candidates.append(i)
        if len(candidates)<min:
            raise self.ConversionFailed("Too few %s.  Need at least %d but there were only %d" % (name,min,len(candidates)))
        if len(candidates)>max:
            candidates=candidates[:max]
        return candidates
    def _getfield(self,list,name):
        res=[]
        for i in list:
            res.append(i[name])
        return res
    def _truncatefields(self, list, truncateat):
        if truncateat is None:
            return list
        res=[]
        for i in list:
            if len(i)>truncateat:
                res.append(i[:truncateat])
            else:
                res.append(i)
        return res
    def _findfirst(self, candidates, required, key, default):
        """Find first match in candidates that meets required and return value of key
        @param candidates: list of dictionaries to search through
        @param required: a dict of what key/value pairs must exist in an entry
        @param key: for a matching entry, which key's value to return
        @param default: what value to return if there is no match
        """
        for dict in candidates:
            ok=True
            for k in required:
                if dict[k]!=required[k]:
                   ok=False
                   break # really want break 2
            if not ok:
                continue
            return dict[key]
        return default
    def getfullname(self, names, min, max, truncateat=None):
        "Return at least min and at most max fullnames from the names list"
        if wx.GetApp().config.ReadInt("lastnamefirst", False):
            n=[nameparser.formatsimplelastfirst(nn) for nn in names]
        else:
            n=[nameparser.formatsimplename(nn) for nn in names]
        if len(n)<min:
            raise self.ConversionFailed("Too few names.  Need at least %d but there were only %d" % (min, len(n)))
        if len(n)>max:
            n=n[:max]
        return self._truncatefields(n, truncateat)
    def getcategory(self, categories, min, max, truncateat=None):
        "Return at least min and at most max categories from the categories list"
        return self._truncatefields(self._getfield(self._getentries(categories, min, max, "categories"), "category"), truncateat)
    def getemails(self, emails, min, max, truncateat=None):
        "Return at least min and at most max emails from the emails list"
        return self._truncatefields(self._getfield(self._getentries(emails, min, max, "emails"), "email"), truncateat)
    def geturls(self, urls, min, max, truncateat=None):
        "Return at least min and at most max urls from the urls list"
        return self._truncatefields(self._getfield(self._getentries(urls, min, max, "urls"), "url"), truncateat)
    def getmemos(self, memos, min, max, truncateat=None):
        "Return at least min and at most max memos from the memos list"
        return self._truncatefields(self._getfield(self._getentries(memos, min, max, "memos"), "memo"), truncateat)
    def getnumbers(self, numbers, min, max):
        "Return at least min and at most max numbers from the numbers list"
        return self._getentries(numbers, min, max, "numbers")
    def getnumber(self, numbers, type, count=1, default=""):
        """Returns phone numbers of the type
        @param numbers: The list of numbers
        @param type: The type, such as cell, home, office
        @param count: Which number to return (eg with type=home, count=2 the second
                    home number is returned)
        @param default: What is returned if there is no such number"""
        for n in numbers:
            if n['type']==type:
                if count==1:
                    return n['number']
                count-=1
        return default
    def getserial(self, serials, sourcetype, id, key, default):
        "Gets a serial if it exists"
        return self._findfirst(serials, {'sourcetype': sourcetype, 'sourceuniqueid': id}, key, default)
    def getringtone(self, ringtones, use, default):
        "Gets a ringtone of type use"
        return self._findfirst(ringtones, {'use': use}, 'ringtone', default)
    def getwallpaper(self, wallpapers, use, default):
        "Gets a wallpaper of type use"
        return self._findfirst(wallpapers, {'use': use}, 'wallpaper', default)
    def getwallpaperindex(self, wallpapers, use, default):
        "Gets a wallpaper index of type use"
        return self._findfirst(wallpapers, {'use': use}, 'index', default)
    def getflag(self, flags, name, default):
        "Gets value of flag named name"
        for i in flags:
            if i.has_key(name):
                return i[name]
        return default
    def getmostpopularcategories(self, howmany, entries, reserved=[], truncateat=None, padnames=[]):
        """Returns the most popular categories
        @param howmany:  How many to return, including the reserved ones
        @param entries:  A dict of the entries
        @param reserved: A list of reserved entries (ie must be present, no matter
                         how popular)
        @param truncateat: How long to truncate the category names at
        @param padnames: if the list is less than howmany long, then add these on the end providing
                         they are not already in the list
        @return: A list of the group names.  The list starts with the members of
               reserved followed by the most popular groups
        """
        freq={}
        for entry in entries:
            e=entries[entry]
            for cat in e.get('categories', []):
               n=cat['category']
               if truncateat: n=n[:truncateat] # truncate
               freq[n]=1+freq.get(n,0)
        freq=[(count,value) for value,count in freq.items()]
        freq.sort()
        freq.reverse() # most popular first
        newl=reserved[:]
        for _, group in freq:
            if len(newl)==howmany:
                break
            if group not in newl:
                newl.append(group)
        for p in padnames:
            if len(newl)==howmany:
                break
            if p not in newl:
                newl.append(p)
        return newl
    def makeone(self, list, default):
        "Returns one item long list"
        if len(list)==0:
            return default
        assert len(list)==1
        return list[0]   
    def filllist(self, list, numitems, blank):
        "makes list numitems long appending blank to get there"
        l=list[:]
        for dummy in range(len(l),numitems):
            l.append(blank)
        return l
class ImportCellRenderer(wx.grid.PyGridCellRenderer):
    SCALE=0.8
    COLOURS=["HONEYDEW", "WHITE", "LEMON CHIFFON", "ROSYBROWN1"]
    def __init__(self, table, grid):
        wx.grid.PyGridCellRenderer.__init__(self)
        self.calc=False
        self.table=table
    def _calcattrs(self):
        grid=self.table.GetView()
        self.font=grid.GetDefaultCellFont()
        self.facename=self.font.GetFaceName()
        self.facesize=self.font.GetPointSize()
        self.textcolour=grid.GetDefaultCellTextColour()
        self.brushes=[wx.Brush(wx.NamedColour(c)) for c in self.COLOURS]
        self.pens=[wx.Pen(wx.NamedColour(c),1 , wx.SOLID) for c in self.COLOURS]
        self.selbrush=wx.Brush(grid.GetSelectionBackground(), wx.SOLID)
        self.selpen=wx.Pen(grid.GetSelectionBackground(), 1, wx.SOLID)
        self.selfg=grid.GetSelectionForeground()
        self.calc=True
    def Draw(self, grid, attr, dc, rect, row, col, isSelected):
        if not self.calc: self._calcattrs()
        rowtype=self.table.GetRowType(row)
        dc.SetClippingRect(rect)
        dc.SetBackgroundMode(wx.SOLID)
        if isSelected:
            dc.SetBrush(self.selbrush)
            dc.SetPen(self.selpen)
            colour=self.selfg
        else:
            dc.SetBrush(self.brushes[rowtype])
            dc.SetPen(self.pens[rowtype])
            colour=self.textcolour
        dc.DrawRectangle(rect.x, rect.y, rect.width, rect.height)
        dc.SetBackgroundMode(wx.TRANSPARENT)
        dc.SetFont(self.font)
        text = grid.GetTable().GetHtmlCellValue(row, col, colour)
        if len(text):
            bphtml.drawhtml(dc,
                            wx.Rect(rect.x+2, rect.y+1, rect.width-4, rect.height-2),
                            text, font=self.facename, size=self.facesize)
        dc.DestroyClippingRegion()
    def GetBestSize(self, grid, attr, dc, row, col):
        if not self.calc: self._calcattrs()
        text = grid.GetTable().GetHtmlCellValue(row, col)
        if not len(text): return (5,5)
        return bphtml.getbestsize(dc, text, font=self.facename, size=self.facesize)
    def Clone(self):
        return ImportCellRenderer()
class ImportDataTable(wx.grid.PyGridTableBase):
    ADDED=0
    UNALTERED=1
    CHANGED=2
    DELETED=3
    htmltemplate=["Not set - "+`i` for i in range(15)]
    def __init__(self, widget):
        self.main=widget
        self.rowkeys=[]
        wx.grid.PyGridTableBase.__init__(self)
        self.columns=['Confidence']+ImportColumns
    def GetRowData(self, row):
        """Returns a 4 part tuple as defined in ImportDialog.rowdata
        for the numbered row"""
        return self.main.rowdata[self.rowkeys[row]]
    def GetColLabelValue(self, col):
        "Returns the label for the numbered column"
        return self.columns[col]
    def IsEmptyCell(self, row, col):
        return False
    def GetNumberCols(self):
        return len(self.columns)
    def GetNumberRows(self):
        return len(self.rowkeys)
    def GetRowType(self, row):
        """Returns what type the row is from DELETED, CHANGED, ADDED and UNALTERED"""
        row=self.GetRowData(row)
        if row[3] is None:
            return self.DELETED
        if row[1] is not None and row[2] is not None:
            return self.CHANGED
        if row[1] is not None and row[2] is None:
            return self.ADDED
        return self.UNALTERED
    def GetValueWithNamedColumn(self, row, columnname):
        row=self.main.rowdata[self.rowkeys[row]]
        if columnname=='Confidence':
            return row[0]
        for i,ptr in (3,self.main.resultdata), (1,self.main.importdata), (2, self.main.existingdata):
            if row[i] is not None:
                return getdata(columnname, ptr[row[i]], "")
        assert False, "Can't get here"
        return ""
    def ShouldColumnBeShown(self, columnname, row):
        confidence, importedkey, existingkey,  resultkey=self.GetRowData(row)
        if columnname=="Confidence": return True
        return (resultkey is not None and getdata(columnname, self.main.resultdata[resultkey], None) is not None) \
               or (existingkey is not None and getdata(columnname, self.main.existingdata[existingkey], None) is not None) \
               or (importedkey is not None and getdata(columnname, self.main.importdata[importedkey], None) is not None)
    def GetHtmlCellValue(self, row, col, colour=None):
        try:
            row=self.GetRowData(row)
        except:
            print "bad row", row
            return "&gt;error&lt;"
        if colour is None:
            colour="#000000" # black
        else:
            colour="#%02X%02X%02X" % (colour.Red(), colour.Green(), colour.Blue())
        if self.columns[col]=='Confidence':
            if row[0]=="": return ""
            return '<font color="%s">%d</font>' % (colour, row[0])
        imported,existing,result=None,None,None
        if row[1] is not None:
            imported=getdata(self.columns[col], self.main.importdata[row[1]], None)
            if imported is not None: imported=common.strorunicode(imported)
        if row[2] is not None:
            existing=getdata(self.columns[col], self.main.existingdata[row[2]], None)
            if existing is not None: existing=common.strorunicode(existing)
        if row[3] is not None:
            result=getdata(self.columns[col], self.main.resultdata[row[3]], None)
            if result is not None: result=common.strorunicode(result)
        if imported is None and existing is None and result is None:
            return ""  # idx=9 - shortcut
        matchfn=lambda x,y: x==y
        if result is None:
            assert  imported is not None or existing is not None
            if imported is not None and existing is not None:
                if matchfn(imported, existing):
                    idx=14
                else:
                    idx=13
            else:
                if imported is None:
                    assert existing is not None
                    idx=11
                else:
                    assert existing is None
                    idx=12
        else:
            if imported is None and existing is None:
                idx=10
            else:
                if imported is not None:
                    imported_eq_result= matchfn(imported,result)
                if existing is not None:
                    existing_eq_result= matchfn(existing,result)
                if      imported is None          and    existing_eq_result:
                    idx=0
                elif    imported is None          and    not existing_eq_result:
                    idx=1
                elif    imported_eq_result        and    existing is None:
                    idx=2
                elif    not imported_eq_result    and    existing is None:
                    idx=3
                elif    imported_eq_result        and    existing_eq_result:
                    idx=4
                elif    imported_eq_result        and    not existing_eq_result:
                    idx=5
                elif    not imported_eq_result    and    existing_eq_result:
                    idx=6
                elif    not imported_eq_result    and    not existing_eq_result:
                    if matchfn(imported, existing):
                        idx=7
                    else:
                        idx=8
                else:
                    assert False, "This is unpossible!"
                    return "FAILED"
        if False: # set to true to debug this
            return `idx`+" "+self.htmltemplate[idx] % { 'imported': _htmlfixup(imported),
                                          'existing': _htmlfixup(existing),
                                          'result': _htmlfixup(result),
                                          'colour': colour}
        return self.htmltemplate[idx] % { 'imported': _htmlfixup(imported),
                                          'existing': _htmlfixup(existing),
                                          'result': _htmlfixup(result),
                                          'colour': colour}
    def OnDataUpdated(self):
        newkeys=self.main.rowdata.keys()
        oldrows=self.rowkeys
        self.rowkeys=[k for k in oldrows if k in newkeys]+[k for k in newkeys if k not in oldrows]
        self.rowkeys=[self.rowkeys[n] for n in range(len(self.rowkeys)) if self.GetRowType(n) in self.main.show]
        colsavail=ImportColumns
        colsused=[]
        for row in range(len(self.rowkeys)):
            can=[] # cols available now
            for col in colsavail:
                if self.ShouldColumnBeShown(col, row):
                    colsused.append(col)
                else:
                    can.append(col)
            colsavail=can
        colsused=[c for c in ImportColumns if c in colsused]
        colsused=["Confidence"]+colsused
        lo=len(self.columns)
        ln=len(colsused)
        try:
            sortcolumn=self.columns[self.main.sortedColumn]
        except IndexError:
            sortcolumn=0
        self.columns=colsused
        if ln>lo:
            msg=wx.grid.GridTableMessage(self, wx.grid.GRIDTABLE_NOTIFY_COLS_APPENDED, ln-lo)
        elif lo>ln:
            msg=wx.grid.GridTableMessage(self, wx.grid.GRIDTABLE_NOTIFY_COLS_DELETED, 0, lo-ln)
        else:
            msg=None
        if msg is not None:
            self.GetView().ProcessTableMessage(msg)
        if sortcolumn not in self.columns:
            sortcolumn=1
        else:
            sortcolumn=self.columns.index(sortcolumn)
        items=[]
        for row in range(len(self.rowkeys)):
            v=self.GetValue(row,sortcolumn)
            try:
                items.append((v.lower(), row))
            except:
                items.append((v, row))
        items.sort()
        if self.main.sortedColumnDescending:
            items.reverse()
        self.rowkeys=[self.rowkeys[n] for _,n in items]
        lo=len(oldrows)
        ln=len(self.rowkeys)
        if ln>lo:
            msg=wx.grid.GridTableMessage(self, wx.grid.GRIDTABLE_NOTIFY_ROWS_APPENDED, ln-lo)
        elif lo>ln:
            msg=wx.grid.GridTableMessage(self, wx.grid.GRIDTABLE_NOTIFY_ROWS_DELETED, 0, lo-ln)
        else:
            msg=None
        if msg is not None:
            self.GetView().ProcessTableMessage(msg)
        msg=wx.grid.GridTableMessage(self, wx.grid.GRIDTABLE_REQUEST_VIEW_GET_VALUES)
        self.GetView().ProcessTableMessage(msg)
        self.GetView().ClearSelection()
        self.main.OnCellSelect()
        self.GetView().Refresh()
    OnDataUpdated=guihelper.BusyWrapper(OnDataUpdated)
def _htmlfixup(txt):
    if txt is None: return ""
    return txt.replace("&", "&amp;").replace("<", "&gt;").replace(">", "&lt;") \
           .replace("\r\n", "<br>").replace("\r", "<br>").replace("\n", "<br>")
def workaroundyetanotherwxpythonbug(method, *args):
    try:
        return method(*args)
    except TypeError:
        print "swallowed a type error in workaroundyetanotherwxpythonbug"
        pass
ImportDataTable.htmltemplate[0]='<font color="%(colour)s">%(result)s</font>'
ImportDataTable.htmltemplate[1]='<font color="%(colour)s"><strike>%(result)s</strike><br><b><font size=-1>Existing</font></b> %(existing)s</font>'
ImportDataTable.htmltemplate[2]='<font color="%(colour)s"><strike>%(result)s</strike></font>'
ImportDataTable.htmltemplate[3]='<font color="%(colour)s"><strike>%(result)s</strike><br><b><font size=-1>Imported</font></b> %(imported)s</font>'
ImportDataTable.htmltemplate[4]=ImportDataTable.htmltemplate[0]  # just display result
ImportDataTable.htmltemplate[5]='<font color="%(colour)s"><strike><font color="#00aa00">%(result)s</font></strike><br><b><font size=-1>Existing</font></b> %(existing)s</font>'
ImportDataTable.htmltemplate[6]='<font color="%(colour)s">%(result)s<br><b><font size=-1>Imported</font></b> %(imported)s</font>'
ImportDataTable.htmltemplate[7]='<font color="%(colour)s"><strike>%(result)s</strike><br><b><font size=-1>Imported/Existing</font></b> %(imported)s</font>'
ImportDataTable.htmltemplate[8]='<font color="%(colour)s"><strike>%(result)s</strike><br><b><font size=-1>Imported</font></b> %(imported)s<br><b><font size=-1>Existing</font></b> %(existing)s</font>'
ImportDataTable.htmltemplate[9]=""
ImportDataTable.htmltemplate[10]='<font color="%(colour)s"><strike>%(result)s</strike></b></font>'
ImportDataTable.htmltemplate[11]='<font color="#aa0000">%(existing)s</font>'
ImportDataTable.htmltemplate[12]='<font color="#aa0000"><font size=-1>%(imported)s</font></font>' # slightly smaller
ImportDataTable.htmltemplate[13]='<font color="%(colour)s"><b><font size=-1>Existing</font></b> <font color="#aa0000">%(existing)s</font><br><b><font size=-1>Imported</font></b> <font color="#888888">%(imported)s</font></font>'
ImportDataTable.htmltemplate[14]='<font color="#aa0000">%(existing)s</font>'
class ImportDialog(wx.Dialog):
    "The dialog for mixing new (imported) data with existing data"
    def __init__(self, parent, existingdata, importdata):
        wx.Dialog.__init__(self, parent, id=-1, title="Import Phonebook data", style=wx.CAPTION|
             wx.SYSTEM_MENU|wx.DEFAULT_DIALOG_STYLE|wx.RESIZE_BORDER)
        self.existingdata=existingdata
        self.importdata=importdata
        self.resultdata={}
        self.rowdata={}
        vbs=wx.BoxSizer(wx.VERTICAL)
        bg=self.GetBackgroundColour()
        w=wx.html.HtmlWindow(self, -1, size=wx.Size(600,50), style=wx.html.HW_SCROLLBAR_NEVER)
        w.SetPage('<html><body BGCOLOR="#%02X%02X%02X">Your data is being imported and BitPim is showing what will happen below so you can confirm its actions.</body></html>' % (bg.Red(), bg.Green(), bg.Blue()))
        vbs.Add(w, 0, wx.EXPAND|wx.ALL, 5)
        hbs=wx.BoxSizer(wx.HORIZONTAL)
        hbs.Add(wx.StaticText(self, -1, "Show entries"), 0, wx.EXPAND|wx.ALL,3)
        self.cbunaltered=wx.CheckBox(self, wx.NewId(), "Unaltered")
        self.cbadded=wx.CheckBox(self, wx.NewId(), "Added")
        self.cbchanged=wx.CheckBox(self, wx.NewId(), "Merged")
        self.cbdeleted=wx.CheckBox(self, wx.NewId(), "Deleted")
        wx.EVT_CHECKBOX(self, self.cbunaltered.GetId(), self.OnCheckbox)
        wx.EVT_CHECKBOX(self, self.cbadded.GetId(), self.OnCheckbox)
        wx.EVT_CHECKBOX(self, self.cbchanged.GetId(), self.OnCheckbox)
        wx.EVT_CHECKBOX(self, self.cbdeleted.GetId(), self.OnCheckbox)
        for i in self.cbunaltered, self.cbadded, self.cbchanged, self.cbdeleted:
            i.SetValue(True)
            hbs.Add(i, 0, wx.ALIGN_CENTRE|wx.LEFT|wx.RIGHT, 7)
        t=ImportDataTable
        self.show=[t.ADDED, t.UNALTERED, t.CHANGED, t.DELETED]
        hbs.Add(wx.StaticText(self, -1, " "), 0, wx.EXPAND|wx.LEFT, 10)
        vbs.Add(hbs, 0, wx.EXPAND|wx.ALL, 5)
        splitter=wx.SplitterWindow(self,-1, style=wx.SP_3D|wx.SP_LIVE_UPDATE)
        splitter.SetMinimumPaneSize(20)
        self.grid=wx.grid.Grid(splitter, wx.NewId())
        self.table=ImportDataTable(self)
        cr=ImportCellRenderer(self.table, self.grid)
        cr.IncRef()  # wxPython bug
        self.grid.RegisterDataType("string", cr, None) # wxWidgets bug - it uses the string renderer rather than DefaultCellRenderer
        self.grid.SetTable(self.table, False, wx.grid.Grid.wxGridSelectRows)
        self.grid.SetSelectionMode(wx.grid.Grid.wxGridSelectRows)
        self.grid.SetRowLabelSize(0)
        self.grid.EnableDragRowSize(True)
        self.grid.EnableEditing(False)
        self.grid.SetMargins(1,0)
        self.grid.EnableGridLines(False)
        wx.grid.EVT_GRID_CELL_RIGHT_CLICK(self.grid, self.OnRightGridClick)
        wx.grid.EVT_GRID_SELECT_CELL(self.grid, self.OnCellSelect)
        wx.grid.EVT_GRID_CELL_LEFT_DCLICK(self.grid, self.OnCellDClick)
        wx.EVT_PAINT(self.grid.GetGridColLabelWindow(), self.OnColumnHeaderPaint)
        wx.grid.EVT_GRID_LABEL_LEFT_CLICK(self.grid, self.OnGridLabelLeftClick)
        wx.grid.EVT_GRID_LABEL_LEFT_DCLICK(self.grid, self.OnGridLabelLeftClick)
        self.resultpreview=PhoneEntryDetailsView(splitter, -1, "styles.xy", "pblayout.xy")
        splitter.SplitVertically(self.grid, self.resultpreview)
        vbs.Add(splitter, 1, wx.EXPAND|wx.ALL,5)
        vbs.Add(wx.StaticLine(self, -1, style=wx.LI_HORIZONTAL), 0, wx.EXPAND|wx.ALL, 5)
        vbs.Add(self.CreateButtonSizer(wx.OK|wx.CANCEL|wx.HELP), 0, wx.ALIGN_CENTRE|wx.ALL, 5)
        self.SetSizer(vbs)
        self.SetAutoLayout(True)
        self.config = parent.mainwindow.config
        guiwidgets.set_size("PhoneImportMergeDialog", self, screenpct=95,  aspect=1.10)
        self.MakeMenus()
        self.sortedColumn=1
        self.sortedColumnDescending=False
        wx.EVT_BUTTON(self, wx.ID_HELP, lambda _: wx.GetApp().displayhelpid(helpids.ID_DLG_PBMERGEENTRIES))
        self.splitter=splitter
        wx.CallAfter(self._setthedamnsplittersizeinsteadofbeingsostupid_thewindowisnot20pixelswide_isetthesizenolessthan3times_argggh)
        wx.CallAfter(self.DoMerge)
    def _setthedamnsplittersizeinsteadofbeingsostupid_thewindowisnot20pixelswide_isetthesizenolessthan3times_argggh(self):
        splitter=self.splitter
        w,_=splitter.GetSize()
        splitter.SetSashPosition(max(w/2, w-200))
    def OnColumnHeaderPaint(self, evt):
        w = self.grid.GetGridColLabelWindow()
        dc = wx.PaintDC(w)
        font = dc.GetFont()
        dc.SetTextForeground(wx.BLACK)
        totColSize = -self.grid.GetViewStart()[0]*self.grid.GetScrollPixelsPerUnit()[0]
        for col in range(self.grid.GetNumberCols()):
            dc.SetBrush(wx.Brush("WHEAT", wx.TRANSPARENT))
            colSize = self.grid.GetColSize(col)
            rect = (totColSize,0,colSize,32)
            dc.DrawRectangle(rect[0] - (col!=0), rect[1], rect[2] + (col!=0), rect[3])
            totColSize += colSize
            if col == self.sortedColumn:
                font.SetWeight(wx.BOLD)
                left = rect[0] + 3
                top = rect[1] + 3
                dc.SetBrush(wx.Brush("WHEAT", wx.SOLID))
                if self.sortedColumnDescending:
                    dc.DrawPolygon([(left,top), (left+6,top), (left+3,top+4)])
                else:
                    dc.DrawPolygon([(left+3,top), (left+6, top+4), (left, top+4)])
            else:
                font.SetWeight(wx.NORMAL)
            dc.SetFont(font)
            dc.DrawLabel("%s" % self.grid.GetTable().GetColLabelValue(col),
                     rect, wx.ALIGN_CENTER | wx.ALIGN_TOP)
    def OnGridLabelLeftClick(self, evt):
        col=evt.GetCol()
        if col==self.sortedColumn:
            self.sortedColumnDescending=not self.sortedColumnDescending
        else:
            self.sortedColumn=col
            self.sortedColumnDescending=False
        self.table.OnDataUpdated()
    def OnCheckbox(self, _):
        t=ImportDataTable
        vclist=((t.ADDED, self.cbadded), (t.UNALTERED, self.cbunaltered),
                (t.CHANGED, self.cbchanged), (t.DELETED, self.cbdeleted))
        self.show=[v for v,c in vclist if c.GetValue()]
        if len(self.show)==0:
            for v,c in vclist:
                self.show.append(v)
                c.SetValue(True)
        self.table.OnDataUpdated()
    def DoMerge(self):
        if len(self.existingdata)*len(self.importdata)>200:
            progdlg=wx.ProgressDialog("Merging entries", "BitPim is merging the new information into the existing information",
                                      len(self.existingdata), parent=self, style=wx.PD_APP_MODAL|wx.PD_CAN_ABORT|wx.PD_REMAINING_TIME)
        else:
            progdlg=None
        try:
            self._DoMerge(progdlg)
        finally:
            if progdlg:
                progdlg.Destroy()
            del progdlg
    DoMerge=guihelper.BusyWrapper(DoMerge)
    def _DoMerge(self, progdlg):
        """Merges all the importdata with existing data
        This can take quite a while!
        """
        count=0
        row={}
        results={}
        em=EntryMatcher(self.existingdata, self.importdata)
        usedimportkeys=[]
        for progress,existingid in enumerate(self.existingdata.keys()):
            if progdlg:
                if not progdlg.Update(progress):
                    wx.CallAfter(self.EndModal, wx.ID_CANCEL)
                    return
            merged=False
            for confidence, importid in em.bestmatches(existingid, limit=1):
                if confidence>90:
                    if importid in usedimportkeys:
                        for i in row:
                            if row[i][1]==importid:
                                break
                        if confidence<row[i][0]:
                            break # they beat us so this existing passed on an importmatch
                        assert i==row[i][3]
                        row[i]=("", None, row[i][2], row[i][3])
                        results[i]=copy.deepcopy(self.existingdata[row[i][2]])
                    results[count]=self.MergeEntries(copy.deepcopy(self.existingdata[existingid]),
                                                     copy.deepcopy(self.importdata[importid]))
                    row[count]=(confidence, importid, existingid, count)
                    count+=1
                    usedimportkeys.append(importid)
                    merged=True
                    break # we are happy with this match
            if not merged:
                results[count]=copy.deepcopy(self.existingdata[existingid])
                row[count]=("", None, existingid, count)
                count+=1
        for importid in self.importdata:
            if importid in usedimportkeys: continue
            results[count]=copy.deepcopy(self.importdata[importid])
            row[count]=("", importid, None, count)
            count+=1
        for r in row:
            _, importid, existingid, resid=row[r]
            if importid is not None and existingid is not None:
                checkresult=copy.deepcopy(results[resid])
                checkexisting=copy.deepcopy(self.existingdata[existingid])
                if "serials" in checkresult: del checkresult["serials"]
                if "serials" in checkexisting: del checkexisting["serials"]
                if checkresult == checkexisting:
                    row[r]=("", None, existingid, resid)
        self.rowdata=row
        self.resultdata=results
        self.table.OnDataUpdated()
    def MergeEntries(self, originalentry, importentry):
        "Take an original and a merge entry and join them together return a dict of the result"
        o=originalentry
        i=importentry
        result={}
        intersect=dictintersection(o,i)
        for dict in i,o:
            for k in dict.keys():
                if k not in intersect:
                    result[k]=dict[k][:]
        for key in intersect:
            if key=="names":
                r=i["names"][0]
                for k in o["names"][0]:
                    r[k]=o["names"][0][k]
                result["names"]=[r]
            elif key=="numbers":
                result['numbers']=mergenumberlists(o['numbers'], i['numbers'])
            elif key=="urls":
                result['urls']=mergefields(o['urls'], i['urls'], 'url', cleaner=cleanurl)
            elif key=="emails":
                result['emails']=mergefields(o['emails'], i['emails'], 'email', cleaner=cleanemail)
            else:
                result[key]=common.list_union(o[key], i[key])
        return result
    def OnCellSelect(self, event=None):
        if event is not None:
            event.Skip()
            row=self.table.GetRowData(event.GetRow())
        else:
            gcr=self.grid.GetGridCursorRow()
            if gcr>=0:
                row=self.table.GetRowData(gcr)
            else: # table is empty
                row=None,None,None,None
        confidence,importid,existingid,resultid=row
        if resultid is not None:
            self.resultpreview.ShowEntry(self.resultdata[resultid])
        else:
            self.resultpreview.ShowEntry({})
    ID_EDIT_ITEM=wx.NewId()
    ID_REVERT_TO_IMPORTED=wx.NewId()
    ID_REVERT_TO_EXISTING=wx.NewId()
    ID_CLEAR_FIELD=wx.NewId()
    ID_IMPORTED_MISMATCH=wx.NewId()
    def MakeMenus(self):
        menu=wx.Menu()
        menu.Append(self.ID_EDIT_ITEM, "Edit...")
        menu.Append(self.ID_REVERT_TO_EXISTING, "Revert field to existing value")
        menu.Append(self.ID_REVERT_TO_IMPORTED, "Revert field to imported value")
        menu.Append(self.ID_CLEAR_FIELD, "Clear field")
        menu.AppendSeparator()
        menu.Append(self.ID_IMPORTED_MISMATCH, "Imported entry mismatch...")
        self.menu=menu
        wx.EVT_MENU(menu, self.ID_EDIT_ITEM, self.OnEditItem)
        wx.EVT_MENU(menu, self.ID_REVERT_TO_EXISTING, self.OnRevertFieldToExisting)
        wx.EVT_MENU(menu, self.ID_REVERT_TO_IMPORTED, self.OnRevertFieldToImported)
        wx.EVT_MENU(menu, self.ID_CLEAR_FIELD, self.OnClearField)
        wx.EVT_MENU(menu, self.ID_IMPORTED_MISMATCH, self.OnImportedMismatch)
    def OnRightGridClick(self, event):
        row,col=event.GetRow(), event.GetCol()
        self.grid.SetGridCursor(row,col)
        self.grid.ClearSelection()
        columnname=self.table.GetColLabelValue(col)
        _, importkey, existingkey, resultkey=self.table.GetRowData(row)
        if columnname=="Confidence":
            self.menu.Enable(self.ID_REVERT_TO_EXISTING, False)
            self.menu.Enable(self.ID_REVERT_TO_IMPORTED, False)
            self.menu.Enable(self.ID_CLEAR_FIELD, False)
        else:
            resultvalue=None
            if resultkey is not None:
                resultvalue=getdata(columnname, self.resultdata[resultkey], None)
            self.menu.Enable(self.ID_REVERT_TO_EXISTING, existingkey is not None 
                                 and getdata(columnname, self.existingdata[existingkey], None)!= resultvalue)
            self.menu.Enable(self.ID_REVERT_TO_IMPORTED, importkey is not None
                             and getdata(columnname, self.importdata[importkey], None) != resultvalue)
            self.menu.Enable(self.ID_CLEAR_FIELD, True)
        self.menu.Enable(self.ID_IMPORTED_MISMATCH, importkey is not None)
        pos=event.GetPosition()
        self.grid.PopupMenu(self.menu, pos)
    def OnEditItem(self,_):
        self.EditEntry(self.grid.GetGridCursorRow(), self.grid.GetGridCursorCol())
    def OnRevertFieldToExisting(self, _):
        row,col=self.grid.GetGridCursorRow(), self.grid.GetGridCursorCol()
        columnname=self.table.GetColLabelValue(col)
        row=self.table.GetRowData(row)
        reskey,resindex=getdatainfo(columnname, self.resultdata[row[3]])
        exkey,exindex=getdatainfo(columnname, self.existingdata[row[2]])
        if exindex is None:
            self.OnClearField(None)
            return
        if resindex is None:
            self.resultdata[row[3]][reskey].append(copy.deepcopy(self.existingdata[row[2]][exkey][exindex]))
        elif resindex<0:
            self.resultdata[row[3]][reskey]=copy.deepcopy(self.existingdata[row[2]][exkey])
        else:
            self.resultdata[row[3]][reskey][resindex]=copy.deepcopy(self.existingdata[row[2]][exkey][exindex])
        self.table.OnDataUpdated()
    def OnRevertFieldToImported(self, _):
        row,col=self.grid.GetGridCursorRow(), self.grid.GetGridCursorCol()
        columnname=self.table.GetColLabelValue(col)
        row=self.table.GetRowData(row)
        reskey,resindex=getdatainfo(columnname, self.resultdata[row[3]])
        imkey,imindex=getdatainfo(columnname, self.importdata[row[1]])
        assert imindex is not None
        if resindex is None:
             self.resultdata[row[3]][reskey].append(copy.deepcopy(self.importdata[row[1]][imkey][imindex]))
        elif resindex<0:
            self.resultdata[row[3]][reskey]=copy.deepcopy(self.importdata[row[1]][imkey])
        else:
            self.resultdata[row[3]][reskey][resindex]=copy.deepcopy(self.importdata[row[1]][imkey][imindex])
        self.table.OnDataUpdated()
    def OnClearField(self, _):
        row,col=self.grid.GetGridCursorRow(), self.grid.GetGridCursorCol()
        columnname=self.table.GetColLabelValue(col)
        row=self.table.GetRowData(row)
        reskey,resindex=getdatainfo(columnname, self.resultdata[row[3]])
        assert resindex is not None
        if resindex<0:
            del self.resultdata[row[3]][reskey]
        else:
            del self.resultdata[row[3]][reskey][resindex]
        self.table.OnDataUpdated()
    def OnImportedMismatch(self,_):
        row=self.grid.GetGridCursorRow()
        _,ourimportkey,existingmatchkey,resultkey=self.table.GetRowData(row)
        match=None
        choices=[]
        for row in range(self.table.GetNumberRows()):
            _,_,existingkey,_=self.table.GetRowData(row)
            if existingkey is not None:
                if existingmatchkey==existingkey:
                    match=len(choices)
                choices.append( (getdata("Name", self.existingdata[existingkey], "<blank>"), existingkey) )
        dlg=ImportedEntryMatchDialog(self, choices, match)
        try:
            if dlg.ShowModal()==wx.ID_OK:
                confidence,importkey,existingkey,resultkey=self.table.GetRowData(self.grid.GetGridCursorRow())
                assert importkey is not None
                match=dlg.GetMatch()
                if match is None:
                    if existingkey is None:
                        wx.MessageBox("It is already a new entry!", wx.OK|wx.ICON_EXCLAMATION)
                        return
                    for rowdatakey in xrange(100000):
                        if rowdatakey not in self.rowdata:
                            for resultdatakey in xrange(100000):
                                if resultdatakey not in self.resultdata:
                                    self.rowdata[rowdatakey]=("", importkey, None, resultdatakey)
                                    self.resultdata[resultdatakey]=copy.deepcopy(self.importdata[importkey])
                                    self.resultdata[resultkey]=copy.deepcopy(self.existingdata[existingkey])
                                    self.rowdata[self.table.rowkeys[self.grid.GetGridCursorRow()]]=("", None, existingkey, resultkey)
                                    self.table.OnDataUpdated()
                                    return
                    assert False, "You really can't get here!"
                ekey=choices[match][1]
                if ekey==existingkey:
                    wx.MessageBox("That is already the entry matched!", wx.OK|wx.ICON_EXCLAMATION)
                    return
                for r in range(self.table.GetNumberRows()):
                    if r==self.grid.GetGridCursorRow(): continue
                    confidence,importkey,existingkey,resultkey=self.table.GetRowData(r)
                    if existingkey==ekey:
                        if importkey is not None:
                            wx.MessageBox("The new match already has an imported entry matching it!", "Already matched", wx.OK|wx.ICON_EXCLAMATION, self)
                            return
                        del self.rowdata[self.table.rowkeys[self.grid.GetGridCursorRow()]]
                        self.rowdata[self.table.rowkeys[r]]=(confidence, ourimportkey, ekey, resultkey)
                        self.resultdata[resultkey]=self.MergeEntries(
                            copy.deepcopy(self.existingdata[ekey]),
                            copy.deepcopy(self.importdata[ourimportkey]))
                        self.table.OnDataUpdated()
                        return
                assert False, "Can't get here"
        finally:
            dlg.Destroy()
    def OnCellDClick(self, event):
        self.EditEntry(event.GetRow(), event.GetCol())
    def EditEntry(self, row, col=None):
        row=self.table.GetRowData(row)
        k=row[3]
        assert k is not None
        data=self.resultdata[k]
        if col is not None:
            columnname=self.table.GetColLabelValue(col)
            if columnname=="Confidence":
                columnname="Name"
        else:
            columnname="Name"
        datakey, dataindex=getdatainfo(columnname, data)
        dlg=phonebookentryeditor.Editor(self, data, keytoopenon=datakey, dataindex=dataindex)
        if dlg.ShowModal()==wx.ID_OK:
            data=dlg.GetData()
            self.resultdata[k]=data
            self.table.OnDataUpdated()
        dlg.Destroy()
class ImportedEntryMatchDialog(wx.Dialog):
    "The dialog shown to select how an imported entry should match"
    def __init__(self, parent, choices, match):
        wx.Dialog.__init__(self, parent, id=-1, title="Select Import Entry Match", style=wx.CAPTION|
             wx.SYSTEM_MENU|wx.DEFAULT_DIALOG_STYLE|wx.RESIZE_BORDER)
        self.choices=choices
        self.importdialog=parent
        vbs=wx.BoxSizer(wx.VERTICAL)
        hbs=wx.BoxSizer(wx.HORIZONTAL)
        self.matchexisting=wx.RadioButton(self, wx.NewId(), "Matches an existing entry below", style=wx.RB_GROUP)
        self.matchnew=wx.RadioButton(self, wx.NewId(), "Is a new entry")
        hbs.Add(self.matchexisting, wx.NewId(), wx.ALIGN_CENTRE|wx.ALL, 5)
        hbs.Add(self.matchnew, wx.NewId(), wx.ALIGN_CENTRE|wx.ALL, 5)
        vbs.Add(hbs, 0, wx.EXPAND|wx.ALL, 5)
        wx.EVT_RADIOBUTTON(self, self.matchexisting.GetId(), self.OnRBClicked)
        wx.EVT_RADIOBUTTON(self, self.matchnew.GetId(), self.OnRBClicked)
        splitter=wx.SplitterWindow(self, -1, style=wx.SP_3D|wx.SP_LIVE_UPDATE)
        self.nameslb=wx.ListBox(splitter, wx.NewId(), choices=[name for name,id in choices], style=wx.LB_SINGLE|wx.LB_NEEDED_SB)
        self.preview=PhoneEntryDetailsView(splitter, -1)
        splitter.SplitVertically(self.nameslb, self.preview)
        vbs.Add(splitter, 1, wx.EXPAND|wx.ALL, 5)
        vbs.Add(wx.StaticLine(self, -1, style=wx.LI_HORIZONTAL), 0, wx.EXPAND|wx.ALL, 5)
        vbs.Add(self.CreateButtonSizer(wx.OK|wx.CANCEL|wx.HELP), 0, wx.ALIGN_CENTRE|wx.ALL, 5)
        wx.EVT_LISTBOX(self, self.nameslb.GetId(), self.OnLbClicked)
        wx.EVT_LISTBOX_DCLICK(self, self.nameslb.GetId(), self.OnLbDClicked)
        if match is None:
            self.matchexisting.SetValue(False)
            self.matchnew.SetValue(True)
            self.nameslb.Enable(False)
        else:
            self.matchexisting.SetValue(True)
            self.matchnew.SetValue(False)
            self.nameslb.Enable(True)
            self.nameslb.SetSelection(match)
            self.preview.ShowEntry(self.importdialog.existingdata[choices[match][1]])
        self.SetSizer(vbs)
        self.SetAutoLayout(True)
        guiwidgets.set_size("PhonebookImportEntryMatcher", self, screenpct=75, aspect=0.58)
        wx.EVT_MENU(self, wx.ID_OK, self.SaveSize)
        wx.EVT_MENU(self, wx.ID_CANCEL, self.SaveSize)
    def SaveSize(self, evt=None):
        if evt is not None:
            evt.Skip()
        guiwidgets.save_size("PhonebookImportEntryMatcher", self.GetRect())
    def OnRBClicked(self, _):
        self.nameslb.Enable(self.matchexisting.GetValue())
    def OnLbClicked(self,_=None):
        existingid=self.choices[self.nameslb.GetSelection()][1]
        self.preview.ShowEntry(self.importdialog.existingdata[existingid])
    def OnLbDClicked(self,_):
        self.OnLbClicked()
        self.SaveSize()
        self.EndModal(wx.ID_OK)
    def GetMatch(self):
        if self.matchnew.GetValue():
            return None # new entry
        return self.nameslb.GetSelection() 
def dictintersection(one,two):
    return filter(two.has_key, one.keys())
class EntryMatcher:
    "Implements matching phonebook entries"
    def __init__(self, sources, against):
        self.sources=sources
        self.against=against
    def bestmatches(self, sourceid, limit=5):
        """Gives best matches out of against list
        @return: list of tuples of (percent match, againstid)
        """
        res=[]
        source=self.sources[sourceid]
        for i in self.against:
            against=self.against[i]
            intersect=dictintersection(source,against)
            score=0
            count=0
            for key in intersect:
                s=source[key]
                a=against[key]
                count+=1
                if s==a:
                    score+=40*len(s)
                    continue
                if key=="names":
                    score+=comparenames(s,a)
                elif key=="numbers":
                    score+=comparenumbers(s,a)
                elif key=="urls":
                    score+=comparefields(s,a,"url")
                elif key=="emails":
                    score+=comparefields(s,a,"email")
                elif key=="addresses":
                    score+=compareallfields(s,a, ("company", "street", "street2", "city", "state", "postalcode", "country"))
                else:
                    count-=1
            if count:
                res.append( ( int(score*100/count), i ) )
        res.sort()
        res.reverse()
        if len(res)>limit:
            return res[:limit]
        return res
def comparenames(s,a):
    "Give a score on two names"
    return (jarowinkler(nameparser.formatsimplename(s[0]), nameparser.formatsimplename(a[0]))-0.6)*10
def cleanurl(url, mode="compare"):
    """Returns lowercase url with the "http://" prefix removed and in lower case
    @param mode: If the value is compare (default), it removes ""http://www.""
                 in preparation for comparing entries. Otherwise, if the value
                 is pb, the result is formatted for writing to the phonebook.
    """
    if mode == "compare":
        urlprefix=re.compile("^(http://)?(www.)?")
    else: urlprefix=re.compile("^(http://)?")
    return default_cleaner(re.sub(urlprefix, "", url).lower())
def cleanemail(email, mode="compare"):
    """Returns lowercase email 
    """
    return default_cleaner(email.lower())
nondigits=re.compile("[^0-9]")
def cleannumber(num):
    "Returns num (a phone number) with all non-digits removed"
    return re.sub(nondigits, "", num)
def comparenumbers(s,a):
    """Give a score on two phone numbers
    """
    ss=[cleannumber(x['number']) for x in s]
    aa=[cleannumber(x['number']) for x in a]
    candidates=[]
    for snum in ss:
        for anum in aa:
            candidates.append( (jarowinkler(snum, anum), snum, anum) )
    candidates.sort()
    candidates.reverse()
    if len(candidates)>3:
        candidates=candidates[:3]
    score=0
    for ratio,snum,anum in candidates:
        if ratio>0.9:
            score+=(ratio-0.9)*10
    return score
def comparefields(s,a,valuekey,threshold=0.8,lookat=3):
    """Compares the valuekey field in source and against lists returning a score for closeness of match"""
    ss=[x[valuekey] for x in s if x.has_key(valuekey)]
    aa=[x[valuekey] for x in a if x.has_key(valuekey)]
    candidates=[]
    for sval in ss:
        for aval in aa:
            candidates.append( (jarowinkler(sval, aval), sval, aval) )
    candidates.sort()
    candidates.reverse()
    if len(candidates)>lookat:
        candidates=candidates[:lookat]
    score=0
    for ratio,sval,aval in candidates:
        if ratio>threshold:
            score+=(ratio-threshold)*10/(1-threshold)
    return score
def compareallfields(s,a,fields,threshold=0.8,lookat=3):
    """Like comparefields, but for source and against lists where multiple keys have values in each item
    @param fields: This should be a list of keys from the entries that are in the order the human
                   would write them down."""
    args=[]
    for d in s,a:
        str=""
        list=[]
        for entry in d:
            for f in fields:
                if entry.has_key(f):
                    str+=entry.get(f)+"  "
            list.append( {'value': str} )
        args.append( list )
    args.extend( ['value', threshold, lookat] )
    return comparefields(*args)
def mergenumberlists(orig, imp):
    """Return the results of merging two lists of numbers
    We compare the sanitised numbers (ie after punctuation etc is stripped
    out).  If they are the same, then the original is kept (since the number
    is the same, and the original most likely has the correct punctuation).
    Otherwise the imported entries overwrite the originals
    """
    res=[]
    res.extend(orig)
    for i in imp:
        num=cleannumber(i['number'])
        found=False
        for r in res:
            if num==cleannumber(r['number']):
                found=True
                if i.has_key('speeddial'):
                    r['speeddial']=i['speeddial']
                break
        if found:
            continue
        found=False
        for r in res:
            if i['type']==r['type']:
                r['number']=i['number']
                if i.has_key('speeddial'):
                    r['speeddial']=i['speeddial']
                found=True
                break
        if found:
            continue
        res.append(i)
    return res
default_cleaner=lambda x: x
def mergefields(orig, imp, field, threshold=0.88, cleaner=default_cleaner):
    """Return the results of merging two lists of fields
    We compare the fields. If they are the same, then the original is kept
    (since the name is the same, and the original most likely has the 
    correct punctuation).
    Otherwise the imported entries overwrite the originals
    """
    res=[]
    res.extend(orig)
    for i in imp:
        impfield=cleaner(i[field])
        found=False
        for r in res:
            resfield=cleaner(r[field])
            if (comparestrings(resfield, impfield) > threshold):
                found=True
                if i.has_key('type'):
                    r['type'] = i['type']
                break
        if found:
            continue
        found=False
        for r in res:
            if (i.has_key('type') and r.has_key('type')):
                if i['type']==r['type']:
                    r[field]=cleaner(i[field], "pb")
                    found=True
                    break
        if found:
            continue
        i[field] = cleaner(i[field], "pb")
        res.append(i)
    return res
import native.strings
jarowinkler=native.strings.jarow
def comparestrings(origfield, impfield):
    """ Compares two strings and returns the score using 
    winkler routine from Febrl (stringcmp.py)
    Return value is between 0.0 and 1.0, where 0.0 means no similarity
    whatsoever, and 1.0 means the strings match exactly."""
    return jarowinkler(origfield, impfield, 16)
def normalise_data(entries):
    for k in entries:
        for n in entries[k].get("numbers", []):
            n["number"]=phonenumber.normalise(n["number"])
class ColumnSelectorDialog(wx.Dialog):
    "The dialog for selecting what columns you want to view"
    ID_SHOW=wx.NewId()
    ID_AVAILABLE=wx.NewId()
    ID_UP=wx.NewId()
    ID_DOWN=wx.NewId()
    ID_ADD=wx.NewId()
    ID_REMOVE=wx.NewId()
    ID_DEFAULT=wx.NewId()
    def __init__(self, parent, config, phonewidget):
        wx.Dialog.__init__(self, parent, id=-1, title="Select Columns to view", style=wx.CAPTION|
                 wx.SYSTEM_MENU|wx.DEFAULT_DIALOG_STYLE|wx.RESIZE_BORDER)
        self.config=config
        self.phonewidget=phonewidget
        hbs=wx.BoxSizer(wx.HORIZONTAL)
        bs=wx.BoxSizer(wx.VERTICAL)
        bs.Add(wx.StaticText(self, -1, "Showing"), 0, wx.ALIGN_CENTRE|wx.ALL, 5)
        self.show=wx.ListBox(self, self.ID_SHOW, style=wx.LB_SINGLE|wx.LB_NEEDED_SB, size=(250, 300))
        bs.Add(self.show, 1, wx.EXPAND|wx.ALL, 5)
        hbs.Add(bs, 1, wx.EXPAND|wx.ALL, 5)
        bs=wx.BoxSizer(wx.VERTICAL)
        self.up=wx.Button(self, self.ID_UP, "Move Up")
        self.down=wx.Button(self, self.ID_DOWN, "Move Down")
        self.add=wx.Button(self, self.ID_ADD, "Show")
        self.remove=wx.Button(self, self.ID_REMOVE, "Don't Show")
        self.default=wx.Button(self, self.ID_DEFAULT, "Default")
        for b in self.up, self.down, self.add, self.remove, self.default:
            bs.Add(b, 0, wx.ALL|wx.ALIGN_CENTRE, 10)
        hbs.Add(bs, 0, wx.ALL|wx.ALIGN_CENTRE, 5)
        bs=wx.BoxSizer(wx.VERTICAL)
        bs.Add(wx.StaticText(self, -1, "Available"), 0, wx.ALIGN_CENTRE|wx.ALL, 5)
        self.available=wx.ListBox(self, self.ID_AVAILABLE, style=wx.LB_EXTENDED|wx.LB_NEEDED_SB, choices=AvailableColumns)
        bs.Add(self.available, 1, wx.EXPAND|wx.ALL, 5)
        hbs.Add(bs, 1, wx.EXPAND|wx.ALL, 5)
        vbs=wx.BoxSizer(wx.VERTICAL)
        vbs.Add(hbs, 1, wx.EXPAND|wx.ALL, 5)
        vbs.Add(wx.StaticLine(self, -1, style=wx.LI_HORIZONTAL), 0, wx.EXPAND|wx.ALL, 5)
        vbs.Add(self.CreateButtonSizer(wx.OK|wx.CANCEL|wx.HELP), 0, wx.ALIGN_CENTRE|wx.ALL, 5)
        self.SetSizer(vbs)
        vbs.Fit(self)
        cur=self.config.Read("phonebookcolumns", "")
        if len(cur):
            cur=cur.split(",")
            cur=[c for c in cur if c in AvailableColumns]
        else:
            cur=DefaultColumns
        self.show.Set(cur)
        self.up.Disable()
        self.down.Disable()
        self.add.Disable()
        self.remove.Disable()
        wx.EVT_LISTBOX(self, self.ID_SHOW, self.OnShowClicked)
        wx.EVT_LISTBOX_DCLICK(self, self.ID_SHOW, self.OnShowClicked)
        wx.EVT_LISTBOX(self, self.ID_AVAILABLE, self.OnAvailableClicked)
        wx.EVT_LISTBOX_DCLICK(self, self.ID_AVAILABLE, self.OnAvailableDClicked)
        wx.EVT_BUTTON(self, self.ID_ADD, self.OnAdd)
        wx.EVT_BUTTON(self, self.ID_REMOVE, self.OnRemove)
        wx.EVT_BUTTON(self, self.ID_UP, self.OnUp)
        wx.EVT_BUTTON(self, self.ID_DOWN, self.OnDown)
        wx.EVT_BUTTON(self, self.ID_DEFAULT, self.OnDefault)
        wx.EVT_BUTTON(self, wx.ID_OK, self.OnOk)
    def OnShowClicked(self, _=None):
        self.up.Enable(self.show.GetSelection()>0)
        self.down.Enable(self.show.GetSelection()<self.show.GetCount()-1)
        self.remove.Enable(self.show.GetCount()>0)
        self.FindWindowById(wx.ID_OK).Enable(self.show.GetCount()>0)
    def OnAvailableClicked(self, _):
        self.add.Enable(True)
    def OnAvailableDClicked(self, _):
        self.OnAdd()
    def OnAdd(self, _=None):
        items=[AvailableColumns[i] for i in self.available.GetSelections()]
        for i in self.available.GetSelections():
            self.available.Deselect(i)
        self.add.Disable()
        it=self.show.GetSelection()
        if it>=0:
            self.show.Deselect(it)
            it+=1
        else:
            it=self.show.GetCount()
        self.show.InsertItems(items, it)
        self.remove.Disable()
        self.up.Disable()
        self.down.Disable()
        self.show.SetSelection(it)
        self.OnShowClicked()
    def OnRemove(self, _):
        it=self.show.GetSelection()
        assert it>=0
        self.show.Delete(it)
        if self.show.GetCount():
            if it==self.show.GetCount():
                self.show.SetSelection(it-1)
            else:
                self.show.SetSelection(it)
        self.OnShowClicked()
    def OnDefault(self,_):
        self.show.Set(DefaultColumns)
        self.show.SetSelection(0)
        self.OnShowClicked()
    def OnUp(self, _):
        it=self.show.GetSelection()
        assert it>=1
        self.show.InsertItems([self.show.GetString(it)], it-1)
        self.show.Delete(it+1)
        self.show.SetSelection(it-1)
        self.OnShowClicked()
    def OnDown(self, _):
        it=self.show.GetSelection()
        assert it<self.show.GetCount()-1
        self.show.InsertItems([self.show.GetString(it)], it+2)
        self.show.Delete(it)
        self.show.SetSelection(it+1)
        self.OnShowClicked()
    def OnOk(self, event):
        cur=[self.show.GetString(i) for i in range(self.show.GetCount())]
        self.config.Write("phonebookcolumns", ",".join(cur))
        self.config.Flush()
        self.phonewidget.SetColumns(cur)
        event.Skip()
class PhonebookPrintDialog(wx.Dialog):
    ID_SELECTED=wx.NewId()
    ID_ALL=wx.NewId()
    ID_LAYOUT=wx.NewId()
    ID_STYLES=wx.NewId()
    ID_PRINT=wx.NewId()
    ID_PAGESETUP=wx.NewId()
    ID_PRINTPREVIEW=wx.NewId()
    ID_CLOSE=wx.ID_CANCEL
    ID_HELP=wx.NewId()
    ID_TEXTSCALE=wx.NewId()
    textscales=[ (0.4, "Teeny"), (0.6, "Tiny"), (0.8, "Small"), (1.0, "Normal"), (1.2, "Large"), (1.4, "Ginormous") ]
    textscales.reverse()
    def __init__(self, phonewidget, mainwindow, config):
        wx.Dialog.__init__(self, mainwindow, id=-1, title="Print PhoneBook", style=wx.CAPTION|
                 wx.SYSTEM_MENU|wx.DEFAULT_DIALOG_STYLE|wx.RESIZE_BORDER)
        self.config=config
        self.phonewidget=phonewidget
        self.layoutfiles={}
        for file in guihelper.getresourcefiles("pbpl-*.xy"):
            f=open(file, "rt")
            desc=f.readline().strip()
            self.layoutfiles[desc]=f.read()
            f.close()
        self.stylefiles={}
        for file in guihelper.getresourcefiles("pbps-*.xy"):
            f=open(file, "rt")
            desc=f.readline().strip()
            self.stylefiles[desc]=f.read()
            f.close()
        vbs=wx.BoxSizer(wx.VERTICAL)  # main vertical sizer
        hbs=wx.BoxSizer(wx.HORIZONTAL) # first row
        numselected=len(phonewidget.GetSelectedRows())
        numtotal=len(phonewidget._data)
        vbs2=wx.BoxSizer(wx.VERTICAL)
        bs=wx.StaticBoxSizer(wx.StaticBox(self, -1, "Rows"), wx.VERTICAL)
        self.selected=wx.RadioButton(self, self.ID_SELECTED, "Selected (%d)" % (numselected,), style=wx.RB_GROUP)
        self.all=wx.RadioButton(self, self.ID_SELECTED, "All (%d)" % (numtotal,) )
        bs.Add(self.selected, 0, wx.EXPAND|wx.ALL, 2)
        bs.Add(self.all, 0, wx.EXPAND|wx.ALL, 2)
        self.selected.SetValue(numselected>1)
        self.all.SetValue(not (numselected>1))
        vbs2.Add(bs, 0, wx.EXPAND|wx.ALL, 2)
        bs=wx.StaticBoxSizer(wx.StaticBox(self, -1, "Text Scale"), wx.HORIZONTAL)
        for i in range(len(self.textscales)):
            if self.textscales[i][0]==1.0:
                sv=i
                break
        self.textscaleslider=wx.Slider(self, self.ID_TEXTSCALE, sv, 0, len(self.textscales)-1, style=wx.SL_VERTICAL|wx.SL_AUTOTICKS)
        self.scale=1
        bs.Add(self.textscaleslider, 0, wx.EXPAND|wx.ALL, 2)
        self.textscalelabel=wx.StaticText(self, -1, "Normal")
        bs.Add(self.textscalelabel, 0, wx.ALIGN_CENTRE)
        vbs2.Add(bs, 1, wx.EXPAND|wx.ALL, 2)
        hbs.Add(vbs2, 0, wx.EXPAND|wx.ALL, 2)
        self.sortkeyscb=[]
        bs=wx.StaticBoxSizer(wx.StaticBox(self, -1, "Sorting"), wx.VERTICAL)
        choices=["<None>"]+AvailableColumns
        for i in range(3):
            bs.Add(wx.StaticText(self, -1, ("Sort by", "Then")[i!=0]), 0, wx.EXPAND|wx.ALL, 2)
            self.sortkeyscb.append(wx.ComboBox(self, wx.NewId(), "<None>", choices=choices, style=wx.CB_READONLY))
            self.sortkeyscb[-1].SetSelection(0)
            bs.Add(self.sortkeyscb[-1], 0, wx.EXPAND|wx.ALL, 2)
        hbs.Add(bs, 0, wx.EXPAND|wx.ALL, 4)
        vbs2=wx.BoxSizer(wx.VERTICAL) # they are on top of each other
        bs=wx.StaticBoxSizer(wx.StaticBox(self, -1, "Layout"), wx.VERTICAL)
        k=self.layoutfiles.keys()
        k.sort()
        self.layout=wx.ListBox(self, self.ID_LAYOUT, style=wx.LB_SINGLE|wx.LB_NEEDED_SB|wx.LB_HSCROLL, choices=k, size=(150,-1))
        self.layout.SetSelection(0)
        bs.Add(self.layout, 1, wx.EXPAND|wx.ALL, 2)
        vbs2.Add(bs, 1, wx.EXPAND|wx.ALL, 2)
        bs=wx.StaticBoxSizer(wx.StaticBox(self, -1, "Styles"), wx.VERTICAL)
        k=self.stylefiles.keys()
        self.styles=wx.CheckListBox(self, self.ID_STYLES, choices=k)
        bs.Add(self.styles, 1, wx.EXPAND|wx.ALL, 2)
        vbs2.Add(bs, 1, wx.EXPAND|wx.ALL, 2)
        hbs.Add(vbs2, 1, wx.EXPAND|wx.ALL, 2)
        vbs2=wx.BoxSizer(wx.VERTICAL)
        vbs2.Add(wx.Button(self, self.ID_PRINT, "Print"), 0, wx.EXPAND|wx.ALL, 2)
        vbs2.Add(wx.Button(self, self.ID_PAGESETUP, "Page Setup..."), 0, wx.EXPAND|wx.ALL, 2)
        vbs2.Add(wx.Button(self, self.ID_PRINTPREVIEW, "Print Preview"), 0, wx.EXPAND|wx.ALL, 2)
        vbs2.Add(wx.Button(self, self.ID_HELP, "Help"), 0, wx.EXPAND|wx.ALL, 2)
        vbs2.Add(wx.Button(self, self.ID_CLOSE, "Close"), 0, wx.EXPAND|wx.ALL, 2)
        hbs.Add(vbs2, 0, wx.EXPAND|wx.ALL, 2)
        vbs.Add(hbs, 1, wx.EXPAND|wx.ALL, 2)
        bs=wx.StaticBoxSizer(wx.StaticBox(self, -1, "Content Preview"), wx.VERTICAL)
        self.preview=bphtml.HTMLWindow(self, -1)
        bs.Add(self.preview, 1, wx.EXPAND|wx.ALL, 2)
        vbs.Add(bs, 2, wx.EXPAND|wx.ALL, 2)
        self.SetSizer(vbs)
        vbs.Fit(self)
        wx.EVT_BUTTON(self, self.ID_PRINTPREVIEW, self.OnPrintPreview)
        wx.EVT_BUTTON(self, self.ID_PRINT, self.OnPrint)
        wx.EVT_BUTTON(self, self.ID_PAGESETUP, self.OnPageSetup)
        wx.EVT_RADIOBUTTON(self, self.selected.GetId(), self.UpdateHtml)
        wx.EVT_RADIOBUTTON(self, self.all.GetId(), self.UpdateHtml)
        for i in self.sortkeyscb:
            wx.EVT_COMBOBOX(self, i.GetId(), self.UpdateHtml)
        wx.EVT_LISTBOX(self, self.layout.GetId(), self.UpdateHtml)
        wx.EVT_CHECKLISTBOX(self, self.styles.GetId(), self.UpdateHtml)
        wx.EVT_COMMAND_SCROLL(self, self.textscaleslider.GetId(), self.UpdateSlider)
        self.UpdateHtml()
    def UpdateSlider(self, evt):
        pos=evt.GetPosition()
        if self.textscales[pos][0]!=self.scale:
            self.scale=self.textscales[pos][0]
            self.textscalelabel.SetLabel(self.textscales[pos][1])
            self.preview.SetFontScale(self.scale)
    def UpdateHtml(self,_=None):
        wx.CallAfter(self._UpdateHtml)
    def _UpdateHtml(self):
        self.html=self.GetCurrentHTML()
        self.preview.SetPage(self.html)
    def GetCurrentHTML(self):
        vars={'phonebook': __import__(__name__) }
        if self.all.GetValue():
            rowkeys=self.phonewidget._data.keys()
        else:
            rowkeys=self.phonewidget.GetSelectedRowKeys()
        for keycb in (-1, -2, -3):
            sortkey=self.sortkeyscb[keycb].GetValue()
            if sortkey=="<None>": continue
            l=[(getdata(sortkey, self.phonewidget._data[key]), key) for key in rowkeys]
            l.sort()
            rowkeys=[key for val,key in l]
        vars['rowkeys']=rowkeys
        vars['currentcolumns']=self.phonewidget.GetColumns()
        vars['data']=self.phonewidget._data
        xcp=xyaptu.xcopier(None)
        xcp.setupxcopy(self.layoutfiles[self.layout.GetStringSelection()])
        html=xcp.xcopywithdns(vars)
        sd={'styles': {}, '__builtins__': __builtins__ }
        for i in range(self.styles.GetCount()):
            if self.styles.IsChecked(i):
                exec self.stylefiles[self.styles.GetString(i)] in sd,sd
        try:
            html=bphtml.applyhtmlstyles(html, sd['styles'])
        except:
            if __debug__:
                f=open("debug.html", "wt")
                f.write(html)
                f.close()
            raise
        return html
    GetCurrentHTML=guihelper.BusyWrapper(GetCurrentHTML)
    def OnPrintPreview(self, _):
        wx.GetApp().htmlprinter.PreviewText(self.html, scale=self.scale)
    def OnPrint(self, _):
        wx.GetApp().htmlprinter.PrintText(self.html, scale=self.scale)
    def OnPrinterSetup(self, _):
        wx.GetApp().htmlprinter.PrinterSetup()
    def OnPageSetup(self, _):
        wx.GetApp().htmlprinter.PageSetup()
def htmlify(string):
    return common.strorunicode(string).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br/>")
