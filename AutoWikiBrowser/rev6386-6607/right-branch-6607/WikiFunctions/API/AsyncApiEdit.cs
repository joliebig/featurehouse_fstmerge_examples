using System;
using System.Threading;
using System.Windows.Forms;
using System.Reflection;
namespace WikiFunctions.API
{
    public delegate void AsyncEventHandler(AsyncApiEdit sender);
    public delegate void AsyncOpenEditHandler(AsyncApiEdit sender, PageInfo pageInfo);
    public delegate void AsyncSaveEventHandler(AsyncApiEdit sender, SaveInfo saveInfo);
    public delegate void AsyncStringEventHandler(AsyncApiEdit sender, string result);
    public delegate void AsyncExceptionEventHandler(AsyncApiEdit sender, Exception ex);
    public delegate void AsyncMaxlagEventHandler(AsyncApiEdit sender, int maxlag, int retryAfter);
    public class AsyncApiEdit
    {
        private Thread TheThread;
        private readonly Control ParentControl;
        private bool InCrossThreadCall;
        public AsyncApiEdit(string url)
            : this(url, null, false)
        {
        }
        public AsyncApiEdit(string url, bool php5)
            : this(url, null, php5)
        {
        }
        public AsyncApiEdit(string url, Control parentControl)
            : this(url, parentControl, false)
        {
        }
        public AsyncApiEdit(string url, Control parentControl, bool php5)
            : this(new ApiEdit(url, php5), parentControl)
        {
        }
        private AsyncApiEdit(ApiEdit editor, Control parentControl)
        {
            SynchronousEditor = editor;
            ParentControl = parentControl;
            State = EditState.Ready;
        }
        public AsyncApiEdit Clone()
        {
            return new AsyncApiEdit((ApiEdit)SynchronousEditor.Clone(), ParentControl);
        }
        public ApiEdit SynchronousEditor { get; private set; }
        public enum EditState
        {
            Ready,
            Working,
            Aborted,
            Failed
        }
        private EditState mState = EditState.Ready;
        public EditState State
        {
            get
            {
                return mState;
            }
            protected set
            {
                CallEvent(StateChanged, this);
                mState = value;
            }
        }
        public bool IsActive
        {
            get
            {
                return State == EditState.Working;
            }
        }
        public void Wait()
        {
            if (TheThread != null)
            {
                if (ParentControl != null && !ParentControl.InvokeRequired)
                {
                    while (IsActive) Application.DoEvents();
                }
                else
                {
                    TheThread.Join();
                }
            }
        }
        public event AsyncOpenEditHandler OpenComplete;
        public event AsyncSaveEventHandler SaveComplete;
        public event AsyncStringEventHandler PreviewComplete;
        public event AsyncExceptionEventHandler ExceptionCaught;
        public event AsyncMaxlagEventHandler MaxlagExceeded;
        public event AsyncEventHandler LoggedOff;
        public event AsyncEventHandler StateChanged;
        public event AsyncEventHandler Aborted;
        delegate void OperationEndedInternal(string operation, object result);
        delegate void OperationFailedInternal(string operation, Exception ex);
        delegate void ExceptionCaughtInternal(Exception ex);
        protected virtual void OnOperationComplete(string operation, object result)
        {
            switch (operation)
            {
                case "Open":
                    if (OpenComplete != null) OpenComplete(this, Page);
                    break;
                case "Save":
                    if (SaveComplete != null) SaveComplete(this, (SaveInfo)result);
                    break;
                case "Preview":
                    if (PreviewComplete != null) PreviewComplete(this, (string)result);
                    break;
            }
        }
        protected virtual void OnOperationFailed(string operation, Exception ex)
        {
            Tools.WriteDebug("ApiEdit", ex.Message);
            if (ex is MaxlagException)
            {
                var exm = (MaxlagException)ex;
                if (MaxlagExceeded != null) MaxlagExceeded(this, exm.Maxlag, exm.RetryAfter);
            }
            else if (ex is LoggedOffException)
            {
                if (LoggedOff != null) LoggedOff(this);
            }
            else
                OnExceptionCaught(ex);
        }
        protected virtual void OnExceptionCaught(Exception ex)
        {
            if (ExceptionCaught != null) ExceptionCaught(this, ex);
        }
        private void CallEvent(Delegate method, params object[] args)
        {
            if (method == null) return;
            if (ParentControl == null)
            {
                method.DynamicInvoke(args);
            }
            else
            {
                InCrossThreadCall = true;
                ParentControl.Invoke(method, args);
                InCrossThreadCall = false;
            }
        }
        private class InvokeArgs
        {
            public readonly string Function;
            public readonly object[] Arguments;
            public InvokeArgs(string func, params object[] args)
            {
                Function = func;
                Arguments = args;
            }
        }
        private void InvokerThread(object genericArgs)
        {
            string operation = null;
            try
            {
                InvokeArgs args = (InvokeArgs)genericArgs;
                operation = args.Function;
                Thread.CurrentThread.Name = string.Format("InvokerThread ({0})", args.Function);
                Type t = SynchronousEditor.GetType();
                object result = t.InvokeMember(
                    args.Function,
                    BindingFlags.InvokeMethod,
                    null,
                    SynchronousEditor,
                    args.Arguments
                    );
                TheThread = null;
                State = EditState.Ready;
                CallEvent(new OperationEndedInternal(OnOperationComplete), args.Function, result);
            }
            catch (ThreadAbortException)
            {
                SynchronousEditor.Reset();
            }
            catch (Exception ex)
            {
                TheThread = null;
                SynchronousEditor.Reset();
                if (ex is TargetInvocationException) ex = ex.InnerException;
                State = EditState.Failed;
                if (operation != null && ex is ApiException)
                {
                    CallEvent(new OperationFailedInternal(OnOperationFailed), operation, ex);
                }
                else
                {
                    CallEvent(new ExceptionCaughtInternal(OnExceptionCaught), ex);
                }
            }
            finally
            {
                TheThread = null;
            }
        }
        private void InvokeFunction(InvokeArgs args)
        {
            if (TheThread != null && TheThread.IsAlive)
                throw new InvocationException("An asynchronous call is already being performed");
            State = EditState.Working;
            TheThread = new Thread(InvokerThread);
            TheThread.Start(args);
        }
        private void InvokeFunction(string name, params object[] args)
        {
            InvokeFunction(new InvokeArgs(name, args));
        }
        public string URL
        {
            get { return SynchronousEditor.URL; }
        }
        public string ApiURL
        {
            get { return SynchronousEditor.ApiURL; }
        }
        public bool PHP5
        {
            get { return SynchronousEditor.PHP5; }
        }
        public int Maxlag
        {
            get { return SynchronousEditor.Maxlag; }
            set { SynchronousEditor.Maxlag = value; }
        }
        public bool NewMessageThrows
        {
            get { return SynchronousEditor.NewMessageThrows; }
            set { SynchronousEditor.NewMessageThrows = value; }
        }
        public string Action
        {
            get { return SynchronousEditor.Action; }
        }
        public string HtmlHeaders
        {
            get { return SynchronousEditor.HtmlHeaders; }
        }
        public PageInfo Page
        {
            get { return SynchronousEditor.Page; }
        }
        public void Reset()
        {
            Abort();
            SynchronousEditor.Reset();
        }
        public void HttpGet(string url)
        {
            InvokeFunction("HttpGet", url);
        }
        public void Login(string username, string password)
        {
            InvokeFunction("Login", username, password);
        }
        public void Logout()
        {
            InvokeFunction("Logout");
        }
        public void Open(string title, bool resolveRedirects)
        {
            InvokeFunction("Open", title, resolveRedirects);
        }
        public void Save(string pageText, string summary, bool minor, WatchOptions watch)
        {
            InvokeFunction("Save", pageText, summary, minor, watch);
        }
        public void Watch(string title)
        {
            InvokeFunction("Watch", title);
        }
        public void Unwatch(string title)
        {
            InvokeFunction("Unwatch", title);
        }
        public void Delete(string title, string reason)
        {
            Delete(title, reason, false);
        }
        public void Delete(string title, string reason, bool watch)
        {
            InvokeFunction("Delete", title, reason, watch);
        }
        public void Protect(string title, string reason, string expiry, string edit, string move, bool cascade, bool watch)
        {
            InvokeFunction("Protect", title, reason, expiry, edit, move, cascade, watch);
        }
        public void Protect(string title, string reason, TimeSpan expiry, string edit, string move, bool cascade, bool watch)
        {
            Protect(title, reason, expiry.ToString(), edit, move, cascade, watch);
        }
        public void Protect(string title, string reason, string expiry, string edit, string move)
        {
            Protect(title, reason, expiry, edit, move, false, false);
        }
        public void Protect(string title, string reason, TimeSpan expiry, string edit, string move)
        {
            Protect(title, reason, expiry.ToString(), edit, move, false, false);
        }
        public void Move(string title, string newTitle, string reason)
        {
            Move(title, newTitle, reason, true, false, false);
        }
        public void Move(string title, string newTitle, string reason, bool moveTalk, bool noRedirect)
        {
            Move(title, newTitle, reason, moveTalk, noRedirect, false);
        }
        public void Move(string title, string newTitle, string reason, bool moveTalk, bool noRedirect, bool watch)
        {
            InvokeFunction("Move", title, newTitle, reason, moveTalk, noRedirect, watch);
        }
        public void Preview(string title, string text)
        {
            InvokeFunction("Preview", title, text);
        }
        public void QueryApi(string queryParameters)
        {
            InvokeFunction("QueryApi", queryParameters);
        }
        public void Rollback(string title, string user)
        {
            InvokeFunction("Rollback", title, user);
        }
        public void ExpandTemplates(string title, string text)
        {
            InvokeFunction("ExpandTemplates", title, text);
        }
        public void Abort()
        {
            if (InCrossThreadCall) return;
            if (TheThread != null)
                TheThread.Abort();
            if (TheThread != null)
                TheThread.Join();
            TheThread = null;
            if (Aborted != null)
                Aborted(this);
            State = EditState.Aborted;
        }
        public UserInfo User
        {
            get { return SynchronousEditor.User; }
        }
        public void RefreshUserInfo()
        {
            InvokeFunction("RefreshUserInfo");
        }
    }
}
