using System;
using System.Diagnostics;
using System.IO;
using System.Runtime.InteropServices;
using System.Runtime.InteropServices.ComTypes;
using System.Text;
using System.Windows.Forms;
using System.ComponentModel;
using System.Drawing;
using WorldWind;
namespace onlyconnect
{
    public class HtmlSite :
        IDisposable,
        IOleClientSite,
        IOleContainer,
        IDocHostUIHandler,
        IOleInPlaceFrame,
        IOleInPlaceSite,
        IOleInPlaceSiteEx,
        IOleDocumentSite,
        IAdviseSink,
        IHTMLEditDesigner,
        IServiceProvider,
        IDocHostShowUI,
        IOleInPlaceUIWindow,
        HTMLDocumentEvents2,
        IPropertyNotifySink
    {
        HtmlEditor container;
        IOleObject m_document;
        internal IOleDocumentView view;
        IOleInPlaceActiveObject activeObject;
        int iAdviseCookie = 0;
        int iEventsCookie = 0;
        internal int iPropertyNotifyCookie = 0;
        internal IConnectionPoint icp;
        IntPtr m_docHwnd = IntPtr.Zero;
        internal bool mFullyActive = false;
        ~HtmlSite()
        {
            Dispose();
        }
        public void Dispose()
        {
            this.CloseDocument();
        }
        [DispId(dispids.DISPID_AMBIENT_DLCONTROL)]
        public int setFlags()
        {
            if (this.container.mEnableActiveContent)
            {
                return (int)constants.DLCTL_DLIMAGES | (int)constants.DLCTL_VIDEOS
                    | (int)constants.DLCTL_BGSOUNDS | 0;
            }
            else
            {
                return (int)constants.DLCTL_NO_SCRIPTS | (int)constants.DLCTL_NO_JAVA | (int)constants.DLCTL_NO_DLACTIVEXCTLS
                    | (int)constants.DLCTL_NO_RUNACTIVEXCTLS | (int)constants.DLCTL_SILENT | (int)constants.DLCTL_DLIMAGES | 0;
            }
        }
        public HtmlSite(HtmlEditor container)
        {
            if ((container == null) || (container.IsHandleCreated == false)) throw
                                                                                 new ArgumentException();
            this.container = container;
            container.Resize += new EventHandler(this.Container_Resize);
        }
        public Object Document
        {
            get { return m_document; }
        }
        public IntPtr DocumentHandle
        {
            get { return m_docHwnd; }
        }
        public void CreateDocument()
        {
            Debug.Assert(m_document == null, "Must call Close before recreating.");
            Boolean created = false;
            try
            {
                m_document = (IOleObject)new HTMLDocument();
                int iRetval;
                iRetval = win32.OleRun(m_document);
                iRetval = m_document.SetClientSite(this);
                Debug.Assert(iRetval == HRESULT.S_OK, "SetClientSite failed");
                iRetval = win32.OleLockRunning(m_document, true, false);
                m_document.SetHostNames("HtmlEditor", "HtmlEditor");
                m_document.Advise(this, out iAdviseCookie);
                Guid guid = new Guid("3050f613-98b5-11cf-bb82-00aa00bdce0b");
                IConnectionPointContainer icpc = (IConnectionPointContainer)m_document;
                icpc.FindConnectionPoint(ref guid, out icp);
                icp.Advise(this, out iEventsCookie);
                created = true;
            }
            finally
            {
                if (created == false)
                    m_document = null;
            }
        }
        internal void SetPropertyNotifyEvents()
        {
            Guid g = new Guid("9BFBBC02-EFF1-101A-84ED-00AA00341D07");
            IConnectionPointContainer icpc = (IConnectionPointContainer)container.Document;
            icpc.FindConnectionPoint(ref g, out icp);
            icp.Advise(this, out iPropertyNotifyCookie);
        }
        public void ActivateDocument()
        {
            if (m_document == null) return;
            if (!container.Visible) return;
            RECT rect = new RECT();
            win32.GetClientRect(container.Handle, rect);
            int iRetVal = m_document.DoVerb(OLEIVERB.UIACTIVATE, IntPtr.Zero, this, 0,
                container.Handle, rect);
            if (iRetVal == 0)
            {
                this.container.bNeedsActivation = false;
            }
        }
        public void CloseDocument()
        {
            try
            {
                container.releaseWndProc();
                container.Resize -= new EventHandler(this.Container_Resize);
                if (m_document == null) return;
                try
                {
                    if (view != null)
                    {
                        view.Show(-1);
                        view.UIActivate(-1);
                        view.SetInPlaceSite(null);
                        view.CloseView(0);
                    }
                }
                catch (Exception e)
                {
                    Debug.WriteLine("CloseView raised exception: " + e.Message);
                }
                try
                {
                    m_document.Close((int)tagOLECLOSE.OLECLOSE_NOSAVE);
                }
                catch (Exception e)
                {
                    Debug.WriteLine("Close document raised exception: " + e.Message);
                }
                m_document.SetClientSite(null);
                win32.OleLockRunning(m_document, false, false);
                if (this.iAdviseCookie != 0)
                {
                    m_document.Unadvise(this.iAdviseCookie);
                }
                if (this.iEventsCookie != 0)
                {
                    m_document.Unadvise(this.iEventsCookie);
                }
                if (this.iPropertyNotifyCookie != 0)
                {
                    m_document.Unadvise(this.iPropertyNotifyCookie);
                }
                if (container.changeCookie != 0)
                {
                    ((IMarkupContainer2)m_document).UnRegisterForDirtyRange(container.changeCookie);
                    container.changeCookie = 0;
                }
                int RefCount = 0;
                if (m_document != null)
                    do
                    {
                        RefCount = Marshal.ReleaseComObject(m_document);
                    } while (RefCount > 0);
                if (view != null)
                    do
                    {
                        RefCount = Marshal.ReleaseComObject(view);
                    } while (RefCount > 0);
                if (activeObject != null)
                    do
                    {
                        RefCount = Marshal.ReleaseComObject(activeObject);
                    } while (RefCount > 0);
                m_document = null;
                view = null;
                activeObject = null;
                container.mHtmlDoc = null;
                container.mDocHTML = null;
            }
            catch (Exception e)
            {
                Debug.WriteLine("CloseDocument raised exception: " + e.Message);
            }
        }
        void Container_Resize(Object src, EventArgs e)
        {
            if (view == null) return;
            RECT rect = new RECT();
            win32.GetClientRect(container.Handle, rect);
            view.SetRect(rect);
        }
        public Boolean CallTranslateAccelerator(MSG msg)
        {
            if (activeObject != null)
                if (activeObject.TranslateAccelerator(msg) != HRESULT.S_FALSE)
                    return true;
            return false;
        }
        public int SaveObject()
        {
            return HRESULT.S_OK;
        }
        public int GetMoniker(uint dwAssign, uint dwWhichMoniker, out Object ppmk)
        {
            ppmk = null;
            return HRESULT.E_NOTIMPL;
        }
        public int GetContainer(out IOleContainer ppContainer)
        {
            ppContainer = (IOleContainer)this;
            return HRESULT.S_OK;
        }
        public int ShowObject()
        {
            return HRESULT.S_OK;
        }
        public int OnShowWindow(int fShow)
        {
            return HRESULT.S_OK;
        }
        public int RequestNewObjectLayout()
        {
            return HRESULT.S_OK;
        }
        public int ParseDisplayName(Object pbc, String pszDisplayName, int[]
            pchEaten, Object[] ppmkOut)
        {
            return HRESULT.E_NOTIMPL;
        }
        public int EnumObjects(uint grfFlags, Object[] ppenum)
        {
            return HRESULT.E_NOTIMPL;
        }
        public int LockContainer(bool fLock)
        {
            return HRESULT.S_OK;
        }
        public int ActivateMe(IOleDocumentView pViewToActivate)
        {
            Debug.Assert(pViewToActivate != null,
                "The view to activate was null");
            if (pViewToActivate == null) return HRESULT.E_INVALIDARG;
            RECT rect = new RECT();
            win32.GetClientRect(container.Handle, rect);
            view = pViewToActivate;
            int iResult = view.SetInPlaceSite((IOleInPlaceSite)this);
            iResult = view.UIActivate(1);
            iResult = view.SetRect(rect);
            int iShow = 1;
            iResult = view.Show(iShow);
            return HRESULT.S_OK;
        }
        public int GetWindow(ref IntPtr hwnd)
        {
            hwnd = IntPtr.Zero;
            if (this.container != null)
            {
                hwnd = this.container.Handle;
                return HRESULT.S_OK;
            }
            else
            {
                return HRESULT.E_FAIL;
            }
        }
        public int ContextSensitiveHelp(bool fEnterMode)
        {
            return HRESULT.E_NOTIMPL;
        }
        public int CanInPlaceActivate()
        {
            return HRESULT.S_OK;
        }
        public int OnInPlaceActivate()
        {
            return HRESULT.S_OK;
        }
        public int OnUIActivate()
        {
            return HRESULT.S_OK;
        }
        public int GetWindowContext(out IOleInPlaceFrame ppFrame, out IOleInPlaceUIWindow
            ppDoc, RECT lprcPosRect, RECT lprcClipRect, tagOIFI lpFrameInfo)
        {
            ppDoc = null;
            ppFrame = (IOleInPlaceFrame)this;
            if (lprcPosRect != null)
            {
                win32.GetClientRect(container.Handle, lprcPosRect);
            }
            if (lprcClipRect != null)
            {
                win32.GetClientRect(container.Handle, lprcClipRect);
            }
            lpFrameInfo.fMDIApp = 0;
            lpFrameInfo.hwndFrame = container.Handle;
            lpFrameInfo.hAccel = IntPtr.Zero;
            lpFrameInfo.cAccelEntries = 0;
            return HRESULT.S_OK;
        }
        public int Scroll(tagSIZE scrollExtant)
        {
            return HRESULT.E_NOTIMPL;
        }
        public int OnUIDeactivate(int fUndoable)
        {
            return HRESULT.S_OK;
        }
        public int OnInPlaceDeactivate()
        {
            activeObject = null;
            return HRESULT.S_OK;
        }
        public int DiscardUndoState()
        {
            return HRESULT.E_NOTIMPL;
        }
        public int DeactivateAndUndo()
        {
            return HRESULT.S_OK;
        }
        public int OnPosRectChange(RECT lprcPosRect)
        {
            return HRESULT.S_OK;
        }
        public int OnInPlaceActivateEx(out bool pfNoRedraw, int dwFlags)
        {
            pfNoRedraw = false;
            return HRESULT.S_OK;
        }
        public int OnInPlaceDeactivateEx(bool fNoRedraw)
        {
            if (!fNoRedraw)
            {
                this.container.Invalidate();
            }
            return HRESULT.S_OK;
        }
        public int RequestUIActivate()
        {
            if (this.container.mAllowActivation)
            {
                return HRESULT.S_OK;
            }
            else
            {
                return HRESULT.S_FALSE;
            }
        }
        public int GetBorder(RECT lprectBorder)
        {
            return HRESULT.E_NOTIMPL;
        }
        public int RequestBorderSpace(RECT pborderwidths)
        {
            return HRESULT.E_NOTIMPL;
        }
        public int SetBorderSpace(RECT pborderwidths)
        {
            return HRESULT.E_NOTIMPL;
        }
        public int SetActiveObject(IOleInPlaceActiveObject pActiveObject, String
            pszObjName)
        {
            try
            {
                if (pActiveObject == null)
                {
                    container.releaseWndProc();
                    if (this.activeObject != null)
                    {
                        Marshal.ReleaseComObject(this.activeObject);
                    }
                    this.activeObject = null;
                    this.m_docHwnd = IntPtr.Zero;
                    this.mFullyActive = false;
                }
                else
                {
                    this.activeObject = pActiveObject;
                    this.m_docHwnd = new IntPtr();
                    pActiveObject.GetWindow(ref this.m_docHwnd);
                    this.mFullyActive = true;
                    container.setupWndProc();
                }
            }
            catch (Exception e)
            {
                Debug.WriteLine("Exception: " + e.Message + e.StackTrace);
            }
            return HRESULT.S_OK;
        }
        public int InsertMenus(IntPtr hmenuShared, tagOleMenuGroupWidths
            lpMenuWidths)
        {
            return HRESULT.E_NOTIMPL;
        }
        public int SetMenu(IntPtr hmenuShared, IntPtr holemenu, IntPtr
            hwndActiveObject)
        {
            return HRESULT.E_NOTIMPL;
        }
        public int RemoveMenus(IntPtr hmenuShared)
        {
            return HRESULT.E_NOTIMPL;
        }
        public int SetStatusText(String pszStatusText)
        {
            return HRESULT.E_NOTIMPL;
        }
        public int TranslateAccelerator(MSG lpmsg, short wID)
        {
            return HRESULT.S_FALSE;
        }
        public int ShowContextMenu(uint dwID, ref win32POINT ppt,
            [MarshalAs(UnmanagedType.IUnknown)] object pcmdtReserved,
            [MarshalAs(UnmanagedType.IDispatch)]object pdispReserved)
        {
            if (container.IsContextMenuEnabled)
            {
                if (
                    (container.ContextMenu != null)
                    ||
                    (container.ContextMenuStrip != null)
                    )
                {
                    System.Drawing.Point pt = new System.Drawing.Point(ppt.x, ppt.y);
                    pt = container.PointToClient(pt);
                    if (container.ContextMenuStrip != null)
                    {
                        container.ContextMenuStrip.Show(container, pt);
                    }
                    else
                    {
                        container.ContextMenu.Show(container, pt);
                    }
                    return HRESULT.S_OK;
                }
                else
                {
                    Debug.WriteLine("Show context menu");
                    return HRESULT.S_FALSE;
                }
            }
            else
            {
                return HRESULT.S_OK;
            }
        }
        public int GetHostInfo_(DOCHOSTUIINFO info)
        {
            info.cbSize = Marshal.SizeOf(typeof(DOCHOSTUIINFO));
            info.dwDoubleClick = DOCHOSTUIDBLCLICK.DEFAULT;
            info.dwFlags = (int)(DOCHOSTUIFLAG.NO3DBORDER | DOCHOSTUIFLAG.ENABLE_INPLACE_NAVIGATION |
                DOCHOSTUIFLAG.DISABLE_SCRIPT_INACTIVE | DOCHOSTUIFLAG.FLAT_SCROLLBAR);
            info.dwReserved1 = 0;
            info.dwReserved2 = 0;
            return HRESULT.S_OK;
        }
        public int GetHostInfo(DOCHOSTUIINFO info)
        {
            int iFlags = (int)(DOCHOSTUIFLAG.NO3DBORDER | DOCHOSTUIFLAG.ENABLE_INPLACE_NAVIGATION |
                DOCHOSTUIFLAG.DISABLE_SCRIPT_INACTIVE);
            if (container.IsAutoCompleteEnabled)
            {
                iFlags = iFlags | (int)DOCHOSTUIFLAG.ENABLE_FORMS_AUTOCOMPLETE;
                iFlags = iFlags | (int)DOCHOSTUIFLAG.DISABLE_EDIT_NS_FIXUP;
            }
            else
            {
                iFlags = iFlags & ~(int)DOCHOSTUIFLAG.ENABLE_FORMS_AUTOCOMPLETE;
            }
            if (container.IsDivOnEnter)
            {
                iFlags = iFlags | (int)DOCHOSTUIFLAG.DIV_BLOCKDEFAULT;
            }
            else
            {
                iFlags = iFlags & ~(int)DOCHOSTUIFLAG.DIV_BLOCKDEFAULT;
            }
            if (container.IsScrollBarShown)
            {
                iFlags = iFlags & ~(int)DOCHOSTUIFLAG.SCROLL_NO;
                iFlags = iFlags | (int)DOCHOSTUIFLAG.FLAT_SCROLLBAR;
            }
            else
            {
                iFlags = iFlags | (int)DOCHOSTUIFLAG.SCROLL_NO;
                iFlags = iFlags & ~(int)DOCHOSTUIFLAG.FLAT_SCROLLBAR;
            }
            info.cbSize = Marshal.SizeOf(typeof(DOCHOSTUIINFO));
            info.dwDoubleClick = DOCHOSTUIDBLCLICK.DEFAULT;
            info.dwFlags = iFlags;
            info.dwReserved1 = 0;
            info.dwReserved2 = 0;
            return HRESULT.S_OK;
        }
        public int EnableModeless(Boolean fEnable)
        {
            return HRESULT.S_OK;
        }
        public int ShowUI(int dwID, IOleInPlaceActiveObject activeObject,
            IOleCommandTarget commandTarget, IOleInPlaceFrame frame, IOleInPlaceUIWindow doc)
        {
            return HRESULT.S_OK;
        }
        public int HideUI()
        {
            return HRESULT.S_OK;
        }
        public int UpdateUI()
        {
            if (this.mFullyActive && (this.m_document != null) && (this.container.mDesignMode == true))
            {
                try
                {
                    HTMLDocument thisdoc = (HTMLDocument)m_document;
                    IDisplayServices ds = (IDisplayServices)thisdoc;
                    if (ds == null)
                    {
                        return HRESULT.S_OK;
                    }
                    IHTMLCaret caret;
                    int iRetVal = ds.GetCaret(out caret);
                    if (caret == null)
                    {
                        return HRESULT.S_OK;
                    }
                    win32POINT pt = new win32POINT();
                    caret.GetLocation(out pt, true);
                    IHTMLDocument2 htmldoc = (IHTMLDocument2)this.m_document;
                    IHTMLElement el = htmldoc.ElementFromPoint(pt.x, pt.y);
                    if (el == null)
                    {
                        return HRESULT.S_OK;
                    }
                    container.mcurrentElement = el;
                    container.InvokeUpdateUI(el);
                }
                catch (Exception e)
                {
                    Debug.WriteLine(e.Message + e.StackTrace);
                }
            }
            return HRESULT.S_OK;
        }
        public int OnDocWindowActivate(Boolean fActivate)
        {
            return HRESULT.E_NOTIMPL;
        }
        public int OnFrameWindowActivate(Boolean fActivate)
        {
            return HRESULT.E_NOTIMPL;
        }
        public int ResizeBorder(RECT rect, IOleInPlaceUIWindow doc, bool fFrameWindow)
        {
            return HRESULT.E_NOTIMPL;
        }
        public int GetOptionKeyPath(out IntPtr pbstrKey, uint dw)
        {
            Debug.WriteLine("GetOptionKeyPath");
            if ((this.container.OptionKeyPath == null) | (this.container.OptionKeyPath == String.Empty))
            {
                pbstrKey = IntPtr.Zero;
            }
            else
            {
                pbstrKey = Marshal.StringToBSTR(this.container.OptionKeyPath);
            }
            return HRESULT.S_OK;
        }
        public int GetDropTarget(IOleDropTarget pDropTarget, out IOleDropTarget ppDropTarget)
        {
            ppDropTarget = null;
            return HRESULT.E_NOTIMPL;
        }
        public int GetExternal(out Object ppDispatch)
        {
            if (container == null)
            {
                ppDispatch = null;
                return HRESULT.S_FALSE;
            }
            ppDispatch = container.Parent;
            return HRESULT.S_OK;
        }
        public int TranslateAccelerator(MSG msg, ref Guid group, int nCmdID)
        {
            if (nCmdID == commandids.IDM_PASTE)
            {
                BeforePasteArgs e = new BeforePasteArgs();
                container.OnBeforePaste(e);
                if (e.Cancel)
                {
                    return HRESULT.S_OK;
                }
            }
            return HRESULT.S_FALSE;
        }
        public int TranslateUrl(int dwTranslate, String strURLIn, out String
            pstrURLOut)
        {
            pstrURLOut = null;
            BeforeNavigateEventArgs e = new BeforeNavigateEventArgs(strURLIn);
            container.OnBeforeNavigate(e);
            bool translated = false;
            if (e.Cancel)
            {
                if (e.Target.StartsWith("javascript"))
                {
                    pstrURLOut = null;
                }
                else
                {
                    pstrURLOut = " ";
                }
                translated = true;
            }
            else if (e.NewTarget != e.Target)
            {
                pstrURLOut = e.NewTarget;
                translated = true;
            }
            else if ((e.Target.StartsWith("javascript")) & (!container.mEnableActiveContent))
            {
                pstrURLOut = null;
                translated = true;
            }
            else if ((container.mLinksNewWindow))
            {
                pstrURLOut = " ";
                System.Diagnostics.Process p = new System.Diagnostics.Process();
                p.StartInfo.FileName = e.NewTarget;
                p.Start();
                translated = true;
            }
            if (e.Target.StartsWith("res://shdoclc"))
            {
                pstrURLOut = "about:blank";
                translated = true;
            }
            if (translated)
            {
                return HRESULT.S_OK;
            }
            else
            {
                 return HRESULT.S_FALSE;
            }
        }
        public int FilterDataObject(IOleDataObject pDO, out IOleDataObject ppDORet)
        {
            ppDORet = null;
            return HRESULT.E_NOTIMPL;
        }
        public int ShowMessage(IntPtr hwnd, String lpStrText,
            String lpstrCaption, uint dwType, String lpHelpFile,
            uint dwHelpContext, IntPtr lpresult)
        {
            return HRESULT.E_NOTIMPL;
        }
        public int ShowHelp(IntPtr hwnd, String lpHelpFile,
            uint uCommand, uint dwData, win32POINT ptMouse,
            Object pDispatchObjectHit)
        {
            return HRESULT.E_NOTIMPL;
        }
        public bool onhelp(IHTMLEventObj o)
        {
            return true;
        }
        public bool onclick(IHTMLEventObj o)
        {
            return true;
        }
        public bool ondblclick(IHTMLEventObj o)
        {
            return true;
        }
        public void onkeydown(IHTMLEventObj o)
        {
        }
        public void onkeyup(IHTMLEventObj o)
        {
        }
        public bool onkeypress(IHTMLEventObj o)
        {
            return true;
        }
        public void onmousedown(IHTMLEventObj o)
        {
        }
        public void onmousemove(IHTMLEventObj o)
        {
        }
        public void onmouseup(IHTMLEventObj o)
        {
        }
        public void onmouseout(IHTMLEventObj o)
        {
        }
        public void onmouseover(IHTMLEventObj o)
        {
        }
        public void onreadystatechange(IHTMLEventObj o)
        {
            container.ReadyStateChangeActions(o);
        }
        public bool onbeforeupdate(IHTMLEventObj o)
        {
            return true;
        }
        public void onafterupdate(IHTMLEventObj o)
        {
        }
        public bool onrowexit(IHTMLEventObj o)
        {
            return true;
        }
        public void onrowenter(IHTMLEventObj o)
        {
        }
        public bool ondragstart(IHTMLEventObj o)
        {
            return true;
        }
        public bool onselectstart(IHTMLEventObj o)
        {
            return true;
        }
        public bool onerrorupdate(IHTMLEventObj o)
        {
            return true;
        }
        public bool oncontextmenu(IHTMLEventObj o)
        {
            return true;
        }
        public bool onstop(IHTMLEventObj o)
        {
            return true;
        }
        public void onrowsdelete(IHTMLEventObj o)
        {
        }
        public void onrowsinserted(IHTMLEventObj o)
        {
        }
        public void oncellchange(IHTMLEventObj o)
        {
        }
        public void onpropertychange(IHTMLEventObj o)
        {
        }
        public void ondatasetchanged(IHTMLEventObj o)
        {
        }
        public void ondataavailable(IHTMLEventObj o)
        {
        }
        public void ondatasetcomplete(IHTMLEventObj o)
        {
        }
        public void onbeforeeditfocus(IHTMLEventObj o)
        {
        }
        public void onselectionchange(IHTMLEventObj o)
        {
        }
        public bool oncontrolselect(IHTMLEventObj o)
        {
            return true;
        }
        public bool onmousewheel(IHTMLEventObj o)
        {
            return true;
        }
        public void onfocusin(IHTMLEventObj o)
        {
        }
        public void onfocusout(IHTMLEventObj o)
        {
        }
        public void onactivate(IHTMLEventObj o)
        {
        }
        public void ondeactivate(IHTMLEventObj o)
        {
        }
        public bool onbeforeactivate(IHTMLEventObj o)
        {
            return true;
        }
        public bool onbeforedeactivate(IHTMLEventObj o)
        {
            return true;
        }
        public void OnClose()
        {
        }
        public void OnDataChange(object pStgmed, object pFormatEtc)
        {
        }
        public void OnRename(IMoniker pmk)
        {
        }
        public void OnSave()
        {
        }
        public void OnViewChange(int dwAspect, int lindex)
        {
        }
        public int QueryService(ref System.Guid guidservice, ref System.Guid interfacerequested, out IntPtr ppserviceinterface)
        {
            int hr = HRESULT.E_NOINTERFACE;
            System.Guid iid_htmledithost = new System.Guid("3050f6a0-98b5-11cf-bb82-00aa00bdce0b");
            System.Guid sid_shtmledithost = new System.Guid("3050F6A0-98B5-11CF-BB82-00AA00BDCE0B");
            if ((guidservice == sid_shtmledithost) & (interfacerequested == iid_htmledithost))
            {
                CSnap snapper = new CSnap();
                ppserviceinterface = Marshal.GetComInterfaceForObject(snapper, typeof(IHTMLEditHost));
                if (ppserviceinterface != IntPtr.Zero)
                {
                    hr = HRESULT.S_OK;
                }
            }
            else
            {
                ppserviceinterface = IntPtr.Zero;
            }
            return hr;
        }
        public int PreHandleEvent(int inEvtDispID, IHTMLEventObj pIEventObj)
        {
            System.Guid pguidCmdGroup = new Guid("DE4BA900-59CA-11CF-9592-444553540000");
            onlyconnect.IOleCommandTarget ct = (onlyconnect.IOleCommandTarget)this.m_document;
            switch (inEvtDispID)
            {
                case dispids.DISPID_IHTMLELEMENT_ONCLICK:
                    break;
                case dispids.DISPID_IHTMLELEMENT_ONKEYDOWN:
                    break;
                case dispids.DISPID_IHTMLELEMENT_ONKEYUP:
                    break;
                case dispids.DISPID_IHTMLELEMENT_ONKEYPRESS:
                    break;
                case dispids.DISPID_MOUSEMOVE:
                    break;
                case dispids.DISPID_MOUSEDOWN:
                    break;
                case dispids.DISPID_KEYDOWN:
                    if (pIEventObj.keyCode == 46)
                    {
                    }
                    break;
                case dispids.DISPID_KEYPRESS:
                    container.InvokeHtmlKeyPress(ref pIEventObj);
                    break;
                case dispids.DISPID_EVMETH_ONDEACTIVATE:
                    break;
                default:
                    break;
            }
            return HRESULT.S_FALSE;
        }
        public int PostHandleEvent(int inEvtDispID, IHTMLEventObj pIEventObj)
        {
            return HRESULT.S_FALSE;
        }
        public int TranslateAccelerator(int inEvtDispID, IHTMLEventObj pIEventObj)
        {
            return HRESULT.S_FALSE;
        }
        public int PostEditorEventNotify(int inEvtDispID, IHTMLEventObj pIEventObj)
        {
            return HRESULT.S_FALSE;
        }
        public int OnChanged(int iDispID)
        {
            return HRESULT.S_OK;
        }
        public int OnRequestEdit(int iDispID)
        {
            return HRESULT.S_OK;
        }
    }
}
