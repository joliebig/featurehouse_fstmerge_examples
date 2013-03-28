"Deals with SMS import/export stuff"
from email.Generator import Generator
from email.MIMEText import MIMEText
from email.Utils import formatdate
import wx
import bptime
import sms
class ExportSMSDialog(wx.Dialog):
    def __init__(self, parent, title):
        super(ExportSMSDialog, self).__init__(parent, -1, title)
        self._smswidget=parent.GetActiveSMSWidget()
        self._sel_data=self._smswidget.get_selected_data()
        self._data=self._smswidget.get_data()
        vbs=wx.BoxSizer(wx.VERTICAL)
        hbs=wx.BoxSizer(wx.HORIZONTAL)
        hbs.Add(wx.StaticText(self, -1, "File"), 0, wx.ALL|wx.ALIGN_CENTRE, 5)
        self.filenamectrl=wx.TextCtrl(self, -1, "sms_export")
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
    _formats=('mbox', 'CSV')
    def GetSelectionGui(self, parent):
        hbs=wx.BoxSizer(wx.HORIZONTAL)
        rbs=wx.StaticBoxSizer(wx.StaticBox(self, -1, "SMS Messages"), wx.VERTICAL)
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
        vbs=wx.StaticBoxSizer(wx.StaticBox(self, -1, 'Export Format'),
                              wx.VERTICAL)
        self._format=wx.ComboBox(self, wx.NewId(),
                                 value=self._formats[0],
                                 choices=self._formats,
                                 style=wx.CB_READONLY)
        vbs.Add(self._format, 0, wx.EXPAND|wx.ALL, 5)
        hbs.Add(vbs, 3, wx.EXPAND|wx.ALL, 5)        
        return hbs
    def OnBrowse(self, _):
        dlg=wx.FileDialog(self, defaultFile=self.filenamectrl.GetValue(),
                          wildcard="mbox files (*.mbox)|*.mbox", style=wx.SAVE|wx.CHANGE_DIR)
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
            _sms=self._data
        else:
            _sms=self._sel_data
        if self._format.GetValue()==self._formats[0]:
            self._export_mbox(_fp, _sms)
        else:
            self._export_csv(_fp, _sms)
        _fp.close()
        self.EndModal(wx.ID_OK)
    def _export_mbox(self, fp, sms):
        _email_generator=Generator(fp, True)
        _lfs='\n\n'
        _keys=sms.keys()
        _keys.sort()
        for k in _keys:
            e=sms[k]
            try:
                _msg=MIMEText(e.text)
                _msg['From']=e._from or 'self'
                _msg['To']=e._to or 'self'
                _msg['Subject']=e.subject
                _msg['Date']=formatdate(bptime.BPTime(e.datetime).mktime(), True)
                _email_generator.flatten(_msg, True)
                _email_generator.write(_lfs)
            except:
                if __debug__:
                    raise
    def _yesno_str(self, v):
        if v:
            return 'Yes'
        return 'No'
    def _datetime_str(self, v):
        _dt=bptime.BPTime(v)
        return _dt.date_str()+' '+_dt.time_str()
    def _priority_str(self, v):
        return sms.SMSEntry._priority_name.get(v, '')
    _csv_template=(
        ('Date', 'datetime', _datetime_str),
        ('From', '_from', None),
        ('To', '_to', None),
        ('Subj', 'subject', None),
        ('Text', 'text', None),
        ('Priority', 'priority', _priority_str),
        ('Read', 'read', _yesno_str),
        ('Locked', 'locked', _yesno_str),
        ('Callback', 'callback', None),
        ('Folder', 'folder', None))
    def _export_csv(self, fp, sms):
        fp.write(','.join(['"'+e[0]+'"' for e in self._csv_template])+'\n')
        _keys=sms.keys()
        _keys.sort()
        for k in _keys:
            try:
                e=sms[k]
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
