import wx
import re
import helpids
class BitPimWidget:
    MENU_NORMAL=wx.ITEM_NORMAL
    MENU_SPACER=wx.ITEM_SEPARATOR
    MENU_CHECK=wx.ITEM_CHECK
    def __init__(self):
        pass
    def InitialiseWidget(self, tree, id, root, config, help_id=None):
        self.id=id
        self._tree=tree
        self.root=root
        self.config=config
        self.OnInit()
        if help_id==None:
            try:
                id_name=re.sub("[^A-Za-z]", "",self.GetWidgetName().upper())
                self.help_id=getattr(helpids, "ID_TAB_"+id_name)
            except:
                self.help_id=helpids.ID_WELCOME
        else:
            self.help_id=help_id
    def OnInit(self):
        pass
    def AddSubPage(self, page, name, image=None, after=None):
        return self._tree.AddPage(self.id, page, name, image, after)
    def AddNode(self, name, image=None):
        return self._tree.AddNode(self, name, image)
    def OnSelected(self, node):
        """Default does nothing, override to provide specific functionality.
        node equals value returned from AddNode.
        """
        pass
    def OnPopupMenu(self, parent, node, pt):
        menu=self.GetRightClickMenuItems(node)
        if len(menu):
            popup_menu=wx.Menu()
            for menu_item in menu:
                type, id, name, tooltip=menu_item
                if type==self.MENU_SPACER:
                    popup_menu.AppendSeparator()
                else:
                    popup_menu.Append(id, name, tooltip, type)
            parent.PopupMenu(popup_menu, pt)
    def GetWidgetName(self):
        return self._tree.GetItemText(self.id)
    def GetHelpID(self):
        return self.help_id
    def GetRightClickMenuItems(self, node):
        """Default does nothing, override to provide specific functionality.
        node equals value returned from AddNode. 
        Return array of (type, ID, name, tootltip) tuples to be used in the popup menu
        Valid types are "menu",
        """
        result=[]
        return result
    def CanCopy(self):
        return False
    def OnCopy(self, evt):
        pass
    def CanPaste(self):
        return False
    def OnPaste(self, evt):
        pass
    def CanRename(self):
        return False
    def OnRename(self, evt):
        pass
    def CanDelete(self):
        return False
    def GetDeleteInfo(self):
        return wx.ART_DEL_BOOKMARK, "Delete"
    def OnDelete(self, evt):
        pass
    def CanAdd(self):
        return False
    def GetAddInfo(self):
        return wx.ART_ADD_BOOKMARK, "Add"
    def OnAdd(self, evt):
        pass
    def CanPrint(self):
        return False
    def OnPrintDialog(self, mainwindow, config):
        pass
    def CanSelectAll(self):
        return False
    def OnSelectAll(self, evt):
        pass
    def HasHistoricalData(self):
        return False
    def OnHistoricalData(self):
        pass
    def HasPreviewPane(self):
        return False
    def IsPreviewPaneEnabled(self):
        return False
    def OnViewPreview(self, on):
        pass
class RootWidget(wx.Panel, BitPimWidget):
    def __init__(self, parent, id):
        wx.Panel.__init__(self, parent, id)
        self.SetBackgroundColour(wx.WHITE)
        vbs=wx.BoxSizer(wx.VERTICAL)
        vbs.Add(wx.StaticText(self, -1, 'Welcome to BitPim'), 0, wx.ALIGN_LEFT|wx.ALL, 2)
        self.SetSizer(vbs)
        self.SetAutoLayout(True)
        vbs.Fit(self)
