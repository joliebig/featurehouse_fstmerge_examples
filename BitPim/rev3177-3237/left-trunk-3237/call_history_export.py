"Deals with Call History import/export stuff"
import wx
import bptime
import phonenumber
class ExportCallHistoryDialog(wx.Dialog):
    def __init__(self, parent, title):
        super(ExportCallHistoryDialog, self).__init__(parent, -1, title)
        self._chwidget=parent.GetActiveCallHistoryWidget()
        self._sel_data=self._chwidget.get_selected_data()
        self._data=self._chwidget.get_data()
        vbs=wx.BoxSizer(wx.VERTICAL)
        hbs=wx.BoxSizer(wx.HORIZONTAL)
        hbs.Add(wx.StaticText(self, -1, "File"), 0, wx.ALL|wx.ALIGN_CENTRE, 5)
        self.filenamectrl=wx.TextCtrl(self, -1, "callhistory_export.csv")
        hbs.Add(self.filenamectrl, 1, wx.ALL|wx.EXPAND, 5)
        self.browsectrl=wx.Button(self, wx.NewId(), "Browse...")
        hbs.Add(self.browsectrl, 0, wx.ALL|wx.EXPAND, 5)
        vbs.Add(hbs, 0, wx.EXPAND|wx.ALL, 5)
        vbs.Add(self.GetSelectionGui(self), 5, wx.EXPAND|wx.ALL, 5)
        vbs.Add(wx.StaticLine(self, -1, style=wx.LI_HORIZONTAL), 0, wx.EXPAND|wx.ALL,5)
        vbs.Add(self.CreateButtonSizer(wx.OK|wx.CANCEL), 0, wx.ALIGN_CENTER|wx.ALL, 5)
        wx.EVT_BUTTON(self, self.browsectrl.GetId(), self.OnBrowse)
        wx.EVT_BUTTON(self, wx.ID_OK, self.OnOk)
        self.SetSizer(vbs)
        self.SetAutoLayout(True)
        vbs.Fit(self)
    def GetSelectionGui(self, parent):
        hbs=wx.BoxSizer(wx.HORIZONTAL)
        rbs=wx.StaticBoxSizer(wx.StaticBox(self, -1, "Call History"), wx.VERTICAL)
        lsel=len(self._sel_data)
        lall=len(self._data)
        self.rows_selected=wx.RadioButton(self, wx.NewId(), "Selected (%d)" % (lsel,), style=wx.RB_GROUP)
        self.rows_all=wx.RadioButton(self, wx.NewId(), "All (%d)" % (lall,))
        if lsel==0:
            self.rows_selected.Enable(False)
            self.rows_selected.SetValue(0)
            self.rows_all.SetValue(1)
        rbs.Add(self.rows_selected, 0, wx.EXPAND|wx.ALL, 2)
        hbs.Add(rbs, 3, wx.EXPAND|wx.ALL, 5)
        rbs.Add(self.rows_all, 0, wx.EXPAND|wx.ALL, 2)
        return hbs
    def OnBrowse(self, _):
        dlg=wx.FileDialog(self, defaultFile=self.filenamectrl.GetValue(),
                          wildcard="CSV files (*.csv)|*.csv", style=wx.SAVE|wx.CHANGE_DIR)
        if dlg.ShowModal()==wx.ID_OK:
            self.filenamectrl.SetValue(dlg.GetPath())
        dlg.Destroy()
    def OnOk(self, _):
        filename=self.filenamectrl.GetValue()
        try:
            _fp=file(filename, 'wt')
        except:
            _fp=None
        if _fp is None:
            dlg=wx.MessageDialog(self, 'Failed to open file ['+filename+']',
                             'Export Error')
            dlg.ShowModal()
            dlg.Destroy()
            self.EndModal(wx.ID_OK)
        if self.rows_all.GetValue():
            _data=self._data
        else:
            _data=self._sel_data
        self._export_csv(_fp, _data)
        _fp.close()
        self.EndModal(wx.ID_OK)
    def _datetime_str(self, v):
        _dt=bptime.BPTime(v)
        return _dt.date_str()+' '+_dt.time_str()
    def _phonenumber_str(self, v):
        return phonenumber.format(v)
    def _hms(self, v):
        if v is None or not isinstance(v, int):
            return ''
        else:
            return '%02d:%02d:%02d'%(v/3600, v/60, v%60)
    _csv_template=(
        ('Date', 'datetime', _datetime_str),
        ('Number', 'number', _phonenumber_str),
        ('Name', 'name', None),
        ('Duration', 'duration', _hms),
        ('Type', 'folder', None))
    def _export_csv(self, fp, ch):
        fp.write(','.join(['"'+e[0]+'"' for e in self._csv_template])+'\n')
        _keys=ch.keys()
        _keys.sort()
        for k in _keys:
            try:
                e=ch[k]
                _l=[]
                for _c in self._csv_template:
                    if _c[2] is None:
                        _s=str(getattr(e, _c[1], ''))
                    else:
                        _s=_c[2](self, getattr(e, _c[1], None))
                    _l.append('"'+_s.replace('"', '')+'"')
                fp.write(','.join(_l)+'\n')
            except:
                if __debug__:
                    raise
