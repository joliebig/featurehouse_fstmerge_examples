import copy

import webbrowser

import re

import htmlentitydefs

import HTMLParser

import StringIO

import wx

import wx.html

import guihelper

import fixedwxpTag

import common

_basefonts=[wx.html.HTML_FONT_SIZE_1, wx.html.HTML_FONT_SIZE_2,
wx.html.HTML_FONT_SIZE_3, wx.html.HTML_FONT_SIZE_4,
wx.html.HTML_FONT_SIZE_5, wx.html.HTML_FONT_SIZE_6,
wx.html.HTML_FONT_SIZE_7 ]

def getbasefontsizes(scale=1.0):

    return [int(scale*sz) for sz in _basefonts]

class  HTMLWindow (wx.html.HtmlWindow) :
	"""BitPim customised HTML Window
    Some extras on this:
       - You can press Ctrl-Alt-S to get a source view
       - Clicking on a link opens a window in your browser
       - Shift-clicking on a link copies it to the clipboard
    """
	    def __init__(self, parent, id, relsize=0.7):

        wx.html.HtmlWindow.__init__(self, parent, id)

        wx.EVT_KEY_UP(self, self.OnKeyUp)

        self.thetext=""

        self.SetFontScale(relsize)

	def SetFontScale(self, scale):

        self.SetFonts("", "", getbasefontsizes(scale))

        if len(self.thetext):

            wx.html.HtmlWindow.SetPage(self,self.thetext)

	def OnLinkClicked(self, event):

        if event.GetEvent().ShiftDown():

            wx.TheClipboard.Open()

            wx.TheClipboard.SetData(event.GetHref())

            wx.TheClipboard.Close()

        else:

            webbrowser.open(event.GetHref())

	def SetPage(self, text):

        self.thetext=text

        wx.html.HtmlWindow.SetPage(self,text)

	def OnKeyUp(self, evt):

        keycode=evt.GetKeyCode()        

        if keycode==ord('S') and evt.ControlDown() and evt.AltDown():

            vs=ViewSourceFrame(None, self.thetext)

            vs.Show(True)

            evt.Skip()

	"""BitPim customised HTML Window
    Some extras on this:
       - You can press Ctrl-Alt-S to get a source view
       - Clicking on a link opens a window in your browser
       - Shift-clicking on a link copies it to the clipboard
    """

class  ViewSourceFrame (wx.Frame) :
	def __init__(self, parent, text, id=-1):

        wx.Frame.__init__(self, parent, id, "HTML Source")

        stc=wx.TextCtrl(self, -1, "", style=wx.TE_MULTILINE)

        stc.AppendText(text)


class  TreeParser (HTMLParser.HTMLParser) :
	"""Turns the HTML data into a tree structure
    Note that the HTML needs to be well formed (ie closing tags must be present)"""
	class  TreeNode :
		def __init__(self):

            self.tag=""

            self.attrs=[]

            self.children=[]

            self.data=""

            self.styles=[]


	def __init__(self, data):

        HTMLParser.HTMLParser.__init__(self)

        self.rootnode=self.TreeNode()

        self.nodestack=[self.rootnode]

        self.feed(data)

        self.close()

        assert len(self.rootnode.children)==1

        self.rootnode=self.rootnode.children[0]

        assert self.rootnode.tag=="html"

	def handle_starttag(self, tag, attrs):

        node=self.TreeNode()

        node.tag=tag

        node.attrs=attrs

        self.nodestack[-1].children.append(node)

        self.nodestack.append(node)

	def handle_endtag(self, tag):

        if tag==self.nodestack[-1].tag:

            self.nodestack=self.nodestack[:-1]

        else:

            print tag,"doesn't match tos",self.nodestack[-1].tag

            self.printtree()

            assert False, "HTML is not well formed"

	def handle_entityref(self, name):

        data=htmlentitydefs.entitydefs.get(name, None)

        if data is None:

            self.handle_data('&%s'%name)

        elif data=="\xa0": 

            return

        else:

            self.handle_data("&%s;" % (name,))

	def handle_data(self, data):

        if len(data.strip())==0:

            return

        node=self.TreeNode()

        node.data=data

        self.nodestack[-1].children.append(node)

	def printtree(self, node=None, indent=0):

        if node is None:

            node=self.rootnode

        ins="  "*indent

        if len(node.data):

            print ins+`node.data`

            assert len(node.children)==0

            assert len(node.attrs)==0

        else:

            print ins+"<"+node.tag+"> "+`node.attrs`

            for c in node.children:

               self.printtree(c, indent+1)

	def flatten(self):

        io=StringIO.StringIO()

        self._flatten(self.rootnode, io)

        return io.getvalue()

	_nltags=("p", "head", "title", "h1", "h2", "h3", "h4", "h5", "table", "tr")
	    def _flatten(self, node, io):

        if len(node.data):

            io.write(node.data)

            return

        if node.tag in self._nltags:

            io.write("\n")

        io.write("<%s" % (node.tag,))

        for a,v in node.styles:

            io.write(' %s="%s"' % (a,v))

        for a,v in node.attrs:

            io.write(' %s="%s"' % (a,v))

        io.write(">")

        for child in node.children:

            self._flatten(child,io)

        io.write("</%s>" % (node.tag,))

        if node.tag in self._nltags:

            io.write("\n")

	"""Turns the HTML data into a tree structure
    Note that the HTML needs to be well formed (ie closing tags must be present)"""

def applyhtmlstyles(html, styles):

    tp=TreeParser(html)

    _applystyles(tp.rootnode, styles)

    return tp.flatten()

def _hasclass(node):

    for a,_ in node.attrs:

        if a=="class":

            return True

    return False

def _applystyles(node, styles):

    if len(node.data):

        return

    if node.tag=="wxp":

        return

    if _hasclass(node):

        newattrs=[]

        for a,v in node.attrs:

            if a!="class":

                newattrs.append( (a,v) )

                continue

            c=styles.get(v,None)

            if c is None:

                continue

            _applystyle(node, c)

        node.attrs=newattrs

    for c in node.children:

        _applystyles(c, styles)

def _applystyle(node, style):

    if len(style)==0: return

    if len(node.data): return

    s=style.get('', None)

    if s is not None:

        assert len(s)&1==0 

        for i in range(len(s)/2):

            node.styles.append( (s[i*2], s[i*2+1]) )

        style=style.copy()

        del style['']

    if len([k for k in style if k[0]=='+']):

        newstyle={}

        for k in style:

            if k[0]!='+':

                newstyle[k]=style[k]

                continue

            kid=TreeParser.TreeNode()

            kid.tag=k[1:]

            kid.children=node.children

            node.children=[kid]

            s=style[k]

            assert len(s)&1==0 

            for i in range(len(s)/2):

                kid.styles.append( (s[i*2], s[i*2+1]) )

        style=newstyle

    if len(style)==0: return

    if node.tag in style:

        s=style[node.tag]

        assert len(s)&1==0 

        for i in range(len(s)/2):

            node.styles.append( (s[i*2], s[i*2+1]) )

    for i in node.children:

        _applystyle(i, style)

class  PrintData (wx.PrintData) :
	""" Similar to wx.PrintData except this one includes copy ctor and
    copy 'operator'
    """
	    _attr_names=("Collate", "Colour", "Duplex", "NoCopies",
                 "Orientation", "PaperId", "PrinterName")
	    def __init__(self, rhs=None):

        super(PrintData, self).__init__()

        if rhs is not None:

            self._copy(rhs, self)

	def _copy(self, src, dest):

        for attr in self._attr_names:

            getattr(dest, 'Set'+attr)(getattr(src, 'Get'+attr)())

	def copy(self):

        return PrintData(self)

	""" Similar to wx.PrintData except this one includes copy ctor and
    copy 'operator'
    """

class  HtmlEasyPrinting :
	"""Similar to wxHtmlEasyPrinting, except this is actually useful.
    The following additions are present:
      - The various settings are saved in a supplied config object
      - You can set the scale for the fonts, otherwise the default
        is way too large (you would get about 80 chars across)
    """
	    def __init__(self, parent=None, config=None, configstr=None):

        self.parent=parent

        self.config=config

        self.configstr=configstr

        self.printData=PrintData()

        self._configtoprintdata(self.printData)

        self._configtopagesetupdata()

	def SetParentFrame(self, parent):

        self.parent=parent

	def PrinterSetup(self):

        printerDialog = wx.PrintDialog(self.parent)

        if self.printData.Ok():

            printerDialog.GetPrintDialogData().SetPrintData(self.printData.copy())

        printerDialog.GetPrintDialogData().SetSetupDialog(True)

        if printerDialog.ShowModal()==wx.ID_OK:

            self.printData = PrintData(printerDialog.GetPrintDialogData().GetPrintData())

            self._printdatatoconfig(self.printData)

        printerDialog.Destroy()

	def PageSetup(self):

        psdd=wx.PageSetupDialogData()

        if self.printData.Ok():

            psdd.SetPrintData(self.printData.copy())

        self._configtopagesetupdata(psdd)

        pageDialog=wx.PageSetupDialog(self.parent, psdd)

        if pageDialog.ShowModal()==wx.ID_OK and \
           pageDialog.GetPageSetupData().Ok() and \
           pageDialog.GetPageSetupData().GetPrintData().Ok():

            self.printData=PrintData(pageDialog.GetPageSetupData().GetPrintData())

            self._printdatatoconfig(self.printData)

            self._pagesetupdatatoconfig(pageDialog.GetPageSetupData())

        pageDialog.Destroy()

	def PreviewText(self, htmltext, basepath="", scale=1.0):

        printout1=self._getprintout(htmltext, basepath, scale)

        printout2=self._getprintout(htmltext, basepath, scale)

        if self.printData.Ok():

            pd=self.printData.copy()

        else:

            pd=PrintData()

        pd.SetOrientation(wx.PORTRAIT)

        preview=wx.PrintPreview(printout1, printout2, pd)

        if not preview.Ok():

            print "preview problem"

            assert False, "preview problem"

            return

        self.frame=wx.PreviewFrame(preview, self.parent, "Print Preview")

        guiwidgets.set_size("PrintPreview", self.frame, screenpct=90, aspect=0.58)

        wx.EVT_CLOSE(self.frame, self.OnPreviewClose)

        self.frame.Initialize()

        self.frame.Show(True)

	def PrintText(self, htmltext, basepath="", scale=1.0):

        pdd=wx.PrintDialogData()

        if self.printData.Ok():

            pdd.SetPrintData(self.printData.copy())

            pdd.GetPrintData().SetOrientation(wx.PORTRAIT)

        printer=wx.Printer(pdd)

        printout=self._getprintout(htmltext, basepath, scale)

        printer.Print(self.parent, printout)

        printout.Destroy()

	def _getprintout(self, htmltext, basepath, scale):

        printout=wx.html.HtmlPrintout()

        basesizes=[7,8,10,12,16,22,30]

        printout.SetFonts("", "", [int(sz*scale) for sz in basesizes])

        printout.SetMargins(*self.margins)

        printout.SetHtmlText(htmltext, basepath)

        return printout

	def _printdatatoconfig(self, pd):

        if self.config is None or self.configstr is None or not pd.Ok():

            print '_printdatatoconfig: bad printData'

            return

        c=self.config

        cstr=self.configstr

        for key,func in [ ("collate", pd.GetCollate),
                          ("colour", pd.GetColour),
                          ("duplex", pd.GetDuplex),
                          ("nocopies", pd.GetNoCopies),
                          ("orientation", pd.GetOrientation),
                          ("paperid", pd.GetPaperId),
                          ]:

            c.WriteInt(cstr+"/"+key, func())

        c.Write(cstr+"/printer", pd.GetPrinterName())

        c.Flush()

	def _configtoprintdata(self, pd):

        if self.config is None or self.configstr is None:

            return

        c=self.config

        cstr=self.configstr

        if not pd.Ok():

            print '_configtoprintdata: bad printData'

            return

        for key,func in [ ("collate", pd.SetCollate),
                          ("colour", pd.SetColour),
                          ("duplex", pd.SetDuplex),
                          ("nocopies", pd.SetNoCopies),
                          ("orientation", pd.SetOrientation),
                          ("paperid", pd.SetPaperId),
                          ]:

            if c.HasEntry(cstr+"/"+key):

                func(c.ReadInt(cstr+"/"+key))

        if not c.HasEntry(cstr+"/paperid"):

            pd.SetPaperId(wx.PAPER_LETTER)

        pd.SetPrinterName(c.Read(cstr+"/printer", ""))

	def _configtopagesetupdata(self, psdd=None):

        v=self.config.Read(self.configstr+"/margins", "")

        if len(v):

            l=[int(x) for x in v.split(',')]

        else:

            l=[15,15,15,15]

        if psdd is not None:

            psdd.SetMarginTopLeft( (l[2], l[0]) )

            psdd.SetMarginBottomRight( (l[3], l[1]) )

        self.margins=l

	def _pagesetupdatatoconfig(self, psdd):

        tl=psdd.GetMarginTopLeft()

        br=psdd.GetMarginBottomRight()

        v="%d,%d,%d,%d" % (tl[1], br[1], tl[0], br[0])

        self.config.Write(self.configstr+"/margins", v)

        self.margins=[tl[1],br[1],tl[0],br[0]]

	def OnPreviewClose(self, event):

        guiwidgets.save_size("PrintPreview", self.frame.GetRect())

        event.Skip()

	"""Similar to wxHtmlEasyPrinting, except this is actually useful.
    The following additions are present:
      - The various settings are saved in a supplied config object
      - You can set the scale for the fonts, otherwise the default
        is way too large (you would get about 80 chars across)
    """
_scales=[0.7, 0.9, 1, 1.1, 1.2, 1.4, 1.6]
class  Renderer :
	_lastsize=None
	    _lastfont=None
	    _mdc=None
	    _bmp=None
	    _hdc=None
	    def getbestsize(html, basepath, font, size):

        if Renderer._bmp is None:

            Renderer._bmp=wx.EmptyBitmap(1,1)

            Renderer._mdc=wx.MemoryDC()

            Renderer._mdc.SelectObject(Renderer._bmp)

            Renderer._hdc=wx.html.HtmlDCRenderer()

            Renderer._hdc.SetDC(Renderer._mdc, 1)

            Renderer._hdc.SetSize(99999,99999)

        Renderer._mdc.ResetBoundingBox()

        if Renderer._lastsize!=size or Renderer._lastfont!=font:

            Renderer._hdc.SetFonts(font, "", [int(x*size) for x in _scales])

        Renderer._hdc.SetHtmlText(html, basepath)

        Renderer._hdc.Render(0,0,0,False)

        return (Renderer._mdc.MaxX(), Renderer._mdc.MaxY())

	getbestsize=staticmethod(getbestsize)

def getbestsize(dc, html, basepath="", font="", size=10):

    return Renderer.getbestsize(html, basepath, font, size)

def drawhtml(dc, rect, html, basepath="", font="", size=10):

    """Draw html into supplied dc and rect"""

    if html is None or html=="":

        return

    origscale=dc.GetUserScale()

    hdc=wx.html.HtmlDCRenderer()

    hdc.SetFonts(font, "", [int(x*size) for x in _scales])

    hdc.SetDC(dc, 1)

    hdc.SetSize(rect.width, rect.height)

    hdc.SetHtmlText(html, basepath)

    hdc.Render(rect.x, rect.y, 0, False)

    dc.SetUserScale(*origscale)

import guiwidgets

if __name__=='__main__':

    src="""
<HTML>
<head><title>A title</title></head>
<body>
<h1 cLaSs="gaudy">Heading 1</h1>
<p>This is my sentence <span class=hilite>with some hilite</span></p>
<p><table><tr><th>one</th><th>two</th></tr>
<tr><td class="orange">orange</td><td>Normal</td></tr>
<tr class="orange"><td>whole row is</td><td>orange</td></tr>
</table></p>
</body>
</html>
"""

    styles={
        'gaudy': 
        {
        '+font': ('color', '#123456'),
        '': ('bgcolor', 'grey'),
        },
        'orange':
        {
        '+font': ('color', '#001122'),
        'tr': ('bgcolor', '#cc1122'),
        'td': ('bgcolor', '#cc1122'),
        },
        'hilite':
        {
        '+b': (),
        '+font': ('color', '#564bef'),
        }
        }

    tp=TreeParser(src)

    _applystyles(tp.rootnode, styles)

    tp.printtree()

    print tp.flatten()

    app=wx.PySimpleApp()

    f=wx.Frame(None, -1, "HTML Test")

    h=HTMLWindow(f, -1)

    f.Show(True)

    h.SetPage(tp.flatten())

    app.MainLoop()

    sys.exit(0)


if __name__=='__main__':

    import sys

    def OnSlider(evt):

        global scale

        scale=szlist[evt.GetPosition()]

    f=common.opentextfile(sys.argv[1])

    html=f.read()

    f.close()

    app=wx.PySimpleApp()

    f=wx.Frame(None, -1, "Print Test")

    butsetup=wx.Button(f, wx.NewId(), "Setup")

    butpreview=wx.Button(f, wx.NewId(), "Preview")

    butprint=wx.Button(f, wx.NewId(), "Print")

    slider=wx.Slider(f, wx.NewId(), 5, 0, 10, style=wx.SL_HORIZONTAL)

    szlist=[0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, 1.1, 1.2, 1.3, 1.4]

    scale=1.0

    bs=wx.BoxSizer(wx.HORIZONTAL)

    bs.Add(slider, 1, wx.EXPAND|wx.ALL, 5)

    for i in (butsetup,butpreview, butprint):

        bs.Add(i, 0, wx.EXPAND|wx.ALL, 5)

    f.SetSizer(bs)

    bs.Fit(f)

    hep=HtmlEasyPrinting(f, None, None)

    wx.EVT_BUTTON(f, butsetup.GetId(), lambda _: hep.PrinterSetup())

    wx.EVT_BUTTON(f, butpreview.GetId(), lambda _: hep.PreviewText(html, scale=scale))

    wx.EVT_BUTTON(f, butprint.GetId(), lambda _: hep.PrintText(html, scale=scale))

    wx.EVT_COMMAND_SCROLL(f, slider.GetId(), OnSlider)

    f.Show(True)

    app.MainLoop()


if __name__=='__main__':

    src="""
<HTML>
<head><title>A title</title></head>
<body>
<h1 cLaSs="gaudy">Heading 1</h1>
<p>This is my sentence <span class=hilite>with some hilite</span></p>
<p><table><tr><th>one</th><th>two</th></tr>
<tr><td class="orange">orange</td><td>Normal</td></tr>
<tr class="orange"><td>whole row is</td><td>orange</td></tr>
</table></p>
</body>
</html>
"""

    styles={
        'gaudy': 
        {
        '+font': ('color', '#123456'),
        '': ('bgcolor', 'grey'),
        },
        'orange':
        {
        '+font': ('color', '#001122'),
        'tr': ('bgcolor', '#cc1122'),
        'td': ('bgcolor', '#cc1122'),
        },
        'hilite':
        {
        '+b': (),
        '+font': ('color', '#564bef'),
        }
        }

    tp=TreeParser(src)

    _applystyles(tp.rootnode, styles)

    tp.printtree()

    print tp.flatten()

    app=wx.PySimpleApp()

    f=wx.Frame(None, -1, "HTML Test")

    h=HTMLWindow(f, -1)

    f.Show(True)

    h.SetPage(tp.flatten())

    app.MainLoop()

    sys.exit(0)


if __name__=='__main__':

    import sys

    def OnSlider(evt):

        global scale

        scale=szlist[evt.GetPosition()]

    f=common.opentextfile(sys.argv[1])

    html=f.read()

    f.close()

    app=wx.PySimpleApp()

    f=wx.Frame(None, -1, "Print Test")

    butsetup=wx.Button(f, wx.NewId(), "Setup")

    butpreview=wx.Button(f, wx.NewId(), "Preview")

    butprint=wx.Button(f, wx.NewId(), "Print")

    slider=wx.Slider(f, wx.NewId(), 5, 0, 10, style=wx.SL_HORIZONTAL)

    szlist=[0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, 1.1, 1.2, 1.3, 1.4]

    scale=1.0

    bs=wx.BoxSizer(wx.HORIZONTAL)

    bs.Add(slider, 1, wx.EXPAND|wx.ALL, 5)

    for i in (butsetup,butpreview, butprint):

        bs.Add(i, 0, wx.EXPAND|wx.ALL, 5)

    f.SetSizer(bs)

    bs.Fit(f)

    hep=HtmlEasyPrinting(f, None, None)

    wx.EVT_BUTTON(f, butsetup.GetId(), lambda _: hep.PrinterSetup())

    wx.EVT_BUTTON(f, butpreview.GetId(), lambda _: hep.PreviewText(html, scale=scale))

    wx.EVT_BUTTON(f, butprint.GetId(), lambda _: hep.PrintText(html, scale=scale))

    wx.EVT_COMMAND_SCROLL(f, slider.GetId(), OnSlider)

    f.Show(True)

    app.MainLoop()


