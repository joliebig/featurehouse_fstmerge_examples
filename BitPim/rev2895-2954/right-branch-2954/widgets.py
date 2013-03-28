import wx
class BitPimWidget:
    def __init__(self):
        pass
    def InitialiseWidget(self, tree, id, root, config):
        self.id=id
        self._tree=tree
        self.root=root
        self.config=config
        self.OnInit()
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
    def GetRightClickMenuItems(self, node):
        """Default does nothing, override to provide specific functionality.
        node equals value returned from AddNode. 
        Return array of (ID, name) tuples to be used in the popup menu
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
class BlankWidget(wx.Panel, BitPimWidget):
    def __init__(self, parent, id):
        wx.Panel.__init__(self, parent, id)
        self.SetBackgroundColour(wx.WHITE)
