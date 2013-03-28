""" Handle setting phone wizard
"""
import wx
import wx.wizard as wiz
import common
import comscan
import phone_detect
import phones
class MyPage(wiz.WizardPageSimple):
    """My Common Page Setup"""
    def __init__(self, parent, title, instruction=None):
        super(MyPage, self).__init__(parent)
        vs=wx.BoxSizer(wx.VERTICAL)
        _title = wx.StaticText(self, -1, title)
        _title.SetFont(wx.Font(18, wx.SWISS, wx.NORMAL, wx.BOLD))
        vs.Add(_title, 0, wx.ALIGN_CENTRE|wx.ALL, 5)
        vs.Add(wx.StaticLine(self, -1), 0, wx.EXPAND|wx.ALL, 5)
        if instruction:
            _inst=wx.StaticText(self, -1, instruction)
            vs.Add(_inst, 0, wx.ALIGN_LEFT|wx.ALL, 5)
        _my_ctrl=self.GetMyControls()
        if _my_ctrl:
            vs.Add(_my_ctrl, 1, wx.EXPAND|wx.ALL, 5)
        self.SetSizer(vs)
        self.SetAutoLayout(True)
        vs.Fit(self)
    def GetMyControls(self):
        """Build your own controls here, which will be appended to the main
        sizer.  Default to None.
        """
        return None
    def ok(self):
        return True
    def get(self, data):
        pass
    def set(self, data):
        pass
class CommPortPage(MyPage):
    detail_fields=(
        ('name', 'Name', None),
        ('description', 'Description', None),
        ('hardwareinstance', 'Hardware Info', None),
        ('driverprovider', 'Driver Provider', None),
        ('driverversion', 'Driver Version', None),
        ('driverdescription', 'Driver Description', None),
        ('class', 'Class', None),
        ('active', 'Active', None),
        )
    def __init__(self, parent):
        super(CommPortPage, self).__init__(parent, 'Communication Port Setting',
                                           'Set the serial port through which BitPim communicates with the phone.')
        self._ports=[{'name': 'auto', 'description': 'BitPim will try to detect the correct port automatically when accessing your phone'}]+\
                     [x for x in comscan.comscan() if x.get('available', False)]
        self._populate()
        self._set_max_size()
    def GetMyControls(self):
        hs=wx.BoxSizer(wx.HORIZONTAL)
        _sbs1=wx.StaticBoxSizer(wx.StaticBox(self, -1, 'Available Ports'),
                                wx.VERTICAL)
        self._ports_lb=wx.ListBox(self, -1,
                                  style=wx.LB_SINGLE|wx.LB_HSCROLL|wx.LB_NEEDED_SB)
        wx.EVT_LISTBOX(self, self._ports_lb.GetId(),
                       self.OnPortSelected)
        _sbs1.Add(self._ports_lb, 1, wx.EXPAND|wx.ALL, 5)
        hs.Add(_sbs1, 0, wx.EXPAND|wx.ALL, 5)
        _sbs2=wx.StaticBoxSizer(wx.StaticBox(self, -1, 'Port Detail'),
                                wx.VERTICAL)
        fgs=wx.FlexGridSizer(0, 2, 5, 5)
        fgs.AddGrowableCol(1)
        self._w=[]
        for e in CommPortPage.detail_fields:
            fgs.Add(wx.StaticText(self, -1, e[1]), 0, wx.EXPAND|wx.ALL, 0)
            w=wx.StaticText(self, -1, 100*' ')
            fgs.Add(w, 0, wx.EXPAND|wx.LEFT, 5)
            self._w.append(w)
        _sbs2.Add(fgs, 1, wx.EXPAND|wx.ALL, 5)
        self._port_details_bs=_sbs2
        hs.Add(_sbs2, 0, wx.EXPAND|wx.ALL, 5)
        return hs
    def ok(self):
        return self._ports_lb.GetSelection()!=wx.NOT_FOUND
    def get(self, data):
        data['com']=self._ports_lb.GetStringSelection()
    def _set_max_size(self):
        _max_size=wx.Size(0,0)
        for e in self._ports:
            self._populate_each(e)
            _ms=self._port_details_bs.GetMinSize()
            _max_size[0]=max(_max_size[0], _ms[0])
            _max_size[1]=max(_max_size[1], _ms[1])
        self._port_details_bs.SetMinSize(_max_size)
        for w in self._w:
            w.SetLabel('')
    def Clear(self):
        self._ports_lb.Clear()
        for w in self._w:
            w.SetLabel('')
    def _populate(self):
        self.Clear()
        for e in self._ports:
            self._ports_lb.Append(e['name'])
    def _populate_each(self, data):
        for i,e in enumerate(CommPortPage.detail_fields):
            v=data.get(e[0], '')
            self._w[i].SetLabel(str(v))
        self._port_details_bs.Layout()
    def OnPortSelected(self, evt):
        self._populate_each(self._ports[evt.GetInt()])
class PhoneModelPage(MyPage):
    def __init__(self, parent):
        super(PhoneModelPage, self).__init__(parent, 'Phone Model Setting',
                                        'Set your phone model.')
        self._populate()
    def GetMyControls(self):
        hs=wx.BoxSizer(wx.HORIZONTAL)
        _sbs=wx.StaticBoxSizer(wx.StaticBox(self, -1, 'Carriers'),
                              wx.VERTICAL)
        self._carriers_lb=wx.ListBox(self, -1,
                                     style=wx.LB_SINGLE|wx.LB_HSCROLL|wx.LB_NEEDED_SB)
        wx.EVT_LISTBOX(self, self._carriers_lb.GetId(), self.OnCarriersLB)
        _sbs.Add(self._carriers_lb, 1, wx.EXPAND|wx.ALL, 5)
        hs.Add(_sbs, 0, wx.EXPAND|wx.ALL, 5)
        _sbs=wx.StaticBoxSizer(wx.StaticBox(self, -1, 'Manufacturers'),
                               wx.VERTICAL)
        self._manuf_lb=wx.ListBox(self, -1,
                                  style=wx.LB_SINGLE|wx.LB_HSCROLL|wx.LB_NEEDED_SB)
        wx.EVT_LISTBOX(self, self._manuf_lb.GetId(), self.OnCarriersLB)
        _sbs.Add(self._manuf_lb, 1, wx.EXPAND|wx.ALL, 5)
        hs.Add(_sbs, 0, wx.EXPAND|wx.ALL, 5)
        _sbs=wx.StaticBoxSizer(wx.StaticBox(self, -1, 'Models'),
                               wx.VERTICAL)
        self._models_lb=wx.ListBox(self, -1,
                                   style=wx.LB_SINGLE|wx.LB_HSCROLL|wx.LB_NEEDED_SB)
        wx.EVT_LISTBOX(self, self._models_lb.GetId(), self.OnModelsLB)
        _sbs.Add(self._models_lb, 1, wx.EXPAND|wx.ALL, 5)
        hs.Add(_sbs, 0, wx.EXPAND|wx.ALL, 5)
        return hs
    def _populate_models(self, carrier=None, brand=None):
        self._models_lb.Clear()
        for e in phones.phoneslist(brand, carrier):
            self._models_lb.Append(e)
    def _populate(self):
        self._carriers_lb.Clear()
        self._manuf_lb.Clear()
        self._models_lb.Clear()
        for e in ['All']+phones.phonecarriers:
            self._carriers_lb.Append(e)
        for e in ['All']+phones.phonemanufacturers:
            self._manuf_lb.Append(e)
        self._carriers_lb.SetSelection(0)
        self._manuf_lb.SetSelection(0)
        self._populate_models()
    def ok(self):
        return self._models_lb.GetSelection()!=wx.NOT_FOUND
    def get(self, data):
        data['phone']=self._models_lb.GetStringSelection()
    def OnCarriersLB(self, evt):
        _s=self._carriers_lb.GetStringSelection()
        if not _s or _s=='All':
            _carrier=None
        else:
            _carrier=_s
        _s=self._manuf_lb.GetStringSelection()
        if not _s or _s=='All':
            _brand=None
        else:
            _brand=_s
        self._populate_models(_carrier, _brand)
    def OnModelsLB(self, evt):
        _model=evt.GetString()
        self._carriers_lb.SetStringSelection(phones.carrier(_model))
        self._manuf_lb.SetStringSelection(phones.manufacturer(_model))
class SummaryPage(MyPage):
    def __init__(self, parent):
        super(SummaryPage, self).__init__(parent, 'Summary')
    def GetMyControls(self):
        vs=wx.BoxSizer(wx.VERTICAL)
        _sbs=wx.StaticBoxSizer(wx.StaticBox(self, -1, 'User Selection'),
                               wx.VERTICAL)
        _fgs=wx.FlexGridSizer(0, 2, 5, 5)
        _fgs.Add(wx.StaticText(self, -1, 'Phone Model:'),
                 0, wx.EXPAND|wx.ALL, 0)
        self._model=wx.StaticText(self, -1, '')
        _fgs.Add(self._model, 0, wx.EXPAND|wx.ALL, 0)
        _fgs.Add(wx.StaticText(self, -1, 'Port:'),
                 0, wx.EXPAND|wx.ALL, 0)
        self._port=wx.StaticText(self, -1, '')
        _fgs.Add(self._port, 0, wx.EXPAND|wx.ALL, 0)
        _fgs.Add(wx.StaticText(self, -1, 'Detection Status:'),
                 0, wx.EXPAND|wx.ALL, 0)
        self._status=wx.StaticText(self, -1, '')
        _fgs.Add(self._status, 0, wx.EXPAND|wx.ALL, 0)
        _sbs.Add(_fgs, 1, wx.EXPAND, 0)
        vs.Add(_sbs, 0, wx.EXPAND|wx.ALL, 5)
        self._det_btn=wx.Button(self, -1, 'Detect Phone')
        vs.Add(self._det_btn, 0, wx.ALL, 5)
        wx.EVT_BUTTON(self, self._det_btn.GetId(), self.OnDetect)
        return vs
    def set(self, data):
        self._status.SetLabel('')
        self._model_name=data.get('phone', '')
        self._model.SetLabel(self._model_name)
        self._com_port=data.get('com', '')
        self._port.SetLabel(self._com_port)
        _module=common.importas(phones.module(self._model_name))
        self._det_btn.Enable(bool(self._model_name and \
                                  hasattr(_module.Phone,
                                         'detectphone'))) 
    def OnDetect(self, _):
        if self._com_port=='auto':
            _port_name=None
        else:
            _port_name=str(self._com_port)
        _res=phone_detect.DetectPhone().detect(using_port=_port_name,
                                               using_model=self._model_name)
        _status='FAILED'
        if _res and _res.get('phone_name', '')==self._model_name:
            _status='PASSED'
        self._status.SetLabel(_status)
class SetPhoneWizard(wiz.Wizard):
    def __init__(self, parent):
        super(SetPhoneWizard, self).__init__(parent, -1,
                                             'Phone Setting Wizard')
        self._data={}
        commport_page=CommPortPage(self)
        phonemodel_page=PhoneModelPage(self)
        summary_page=SummaryPage(self)
        wiz.WizardPageSimple_Chain(phonemodel_page, commport_page)
        wiz.WizardPageSimple_Chain(commport_page, summary_page)
        self.first_page=phonemodel_page
        self.GetPageAreaSizer().Add(phonemodel_page, 1, wx.EXPAND|wx.ALL, 5)
        wiz.EVT_WIZARD_PAGE_CHANGING(self, self.GetId(), self.OnPageChanging)
        wiz.EVT_WIZARD_PAGE_CHANGED(self, self.GetId(), self.OnPageChanged)
    def RunWizard(self, firstPage=None):
        return super(SetPhoneWizard, self).RunWizard(firstPage or self.first_page)
    def OnPageChanging(self, evt):
        pg=evt.GetPage()
        if not evt.GetDirection() or pg.ok():
            pg.get(self._data)
        else:
            evt.Veto()
    def OnPageChanged(self, evt):
        evt.GetPage().set(self._data)
    def get(self):
        return self._data
if __name__=="__main__":
    app=wx.PySimpleApp()
    f=wx.Frame(None, title='setphone_wizard')
    w=SetPhoneWizard(f)
    print w.RunWizard()
    print w._data
    w.Destroy()
