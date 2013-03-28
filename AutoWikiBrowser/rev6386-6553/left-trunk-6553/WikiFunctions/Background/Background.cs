using System;
using System.Collections.Specialized;
using System.Threading;
using System.Text.RegularExpressions;
using System.Windows.Forms;
using System.Collections.Generic;
using System.Web;
using WikiFunctions.API;
using WikiFunctions.Lists.Providers;
namespace WikiFunctions.Background
{
    public delegate void BackgroundRequestComplete(BackgroundRequest req);
    public delegate void BackgroundRequestErrored(BackgroundRequest req);
    public delegate void ExecuteFunctionDelegate();
    public class BackgroundRequest
    {
        public object Result;
        public bool Done
        {
            get
            {
                bool res = (BgThread != null && (BgThread.ThreadState == ThreadState.Stopped ||
                    BgThread.ThreadState == ThreadState.Aborted));
                try
                {
                    if (res && UI != null) UI.Close();
                }
                catch
                {
                }
                return res;
            }
        }
        public bool HasUI = true;
        public Exception ErrorException { get; private set; }
        PleaseWait UI;
        Thread BgThread;
        public event BackgroundRequestComplete Complete;
        public event BackgroundRequestErrored Errored;
        public BackgroundRequest()
        { }
        public BackgroundRequest(BackgroundRequestComplete handler)
        {
            Complete += handler;
        }
        public BackgroundRequest(BackgroundRequestComplete completeHandler, BackgroundRequestErrored errorHandler)
            : this (completeHandler)
        {
            Errored += errorHandler;
        }
        public void Wait()
        {
            while (!Done) Application.DoEvents();
        }
        public void Abort()
        {
            if (UI != null) UI.Close();
            UI = null;
            if (BgThread != null) BgThread.Abort();
            Wait();
            Result = null;
        }
        protected string StrParam;
        protected object ObjParam1, ObjParam2, ObjParam3;
        private void InitThread(ThreadStart start)
        {
            BgThread = new Thread(start)
                           {
                               IsBackground = true,
                               Name =
                                   string.Format(
                                   "BackgroundRequest (StrParam = {0}, ObjParam1 = {1}, ObjParam2 = {2}, ObjParam3 = {3})",
                                   StrParam, ObjParam1, ObjParam2, ObjParam3)
                           };
            BgThread.Start();
        }
        private void InvokeOnComplete()
        {
            if (Complete != null) Complete(this);
        }
        private void InvokeOnError()
        {
            if (Errored != null) Errored(this);
        }
        public void GetHTML(string url)
        {
            StrParam = url;
            InitThread(GetHTMLFunc);
        }
        private void GetHTMLFunc()
        {
            try
            {
                Result = Tools.GetHTML(StrParam);
                InvokeOnComplete();
            }
            catch (Exception e)
            {
                ErrorException = e;
                InvokeOnError();
            }
        }
        public void PostData(string url, NameValueCollection postvars)
        {
            StrParam = url;
            ObjParam1 = postvars;
            InitThread(PostDataFunc);
        }
        private void PostDataFunc()
        {
            try
            {
                Result = Tools.PostData((NameValueCollection)ObjParam1, StrParam);
                InvokeOnComplete();
            }
            catch (Exception e)
            {
                ErrorException = e;
                InvokeOnError();
            }
        }
        public void Execute(ExecuteFunctionDelegate d)
        {
            BgThread = new Thread(ExecuteFunc) {Name = "BackgroundThread", IsBackground = true};
            BgThread.Start(d);
        }
        private void ExecuteFunc(object d)
        {
            try
            {
                ((ExecuteFunctionDelegate)d)();
                InvokeOnComplete();
            }
            catch (Exception e)
            {
                ErrorException = e;
                InvokeOnError();
            }
        }
        public void BypassRedirects(string article, IApiEdit editor)
        {
            Result = StrParam = article;
            ObjParam1 = editor;
            if (HasUI)
            {
                UI = new PleaseWait();
                UI.Show(Variables.MainForm as Form);
            }
            InitThread(BypassRedirectsFunc);
        }
        private void BypassRedirectsFunc()
        {
            Dictionary<string, string> knownLinks = new Dictionary<string, string>();
            if (HasUI) UI.Worker = Thread.CurrentThread;
            IApiEdit editor = ObjParam1 as IApiEdit;
            if (editor == null)
            {
                Result = "";
                InvokeOnError();
                return;
            }
            try
            {
                if (HasUI) UI.Status = "Loading links";
                MatchCollection links = WikiRegexes.WikiLinksOnlyPossiblePipe.Matches(StrParam);
                if (HasUI)
                {
                    UI.Status = "Processing links";
                    UI.SetProgress(0, links.Count);
                }
                int n = 0;
                foreach (Match m in links)
                {
                    string link = m.Value;
                    string article = m.Groups[1].Value.TrimStart(new[] {':'});
                    string linkText = (!string.IsNullOrEmpty(m.Groups[2].Value)) ? m.Groups[2].Value : article;
                    string ftu = Tools.TurnFirstToUpper(article);
                    string value;
                    if (!knownLinks.TryGetValue(ftu, out value))
                    {
                        string text;
                        try
                        {
                            text = editor.Open(article, false);
                        }
                        catch
                        {
                            continue;
                        }
                        string dest = article;
                        if (Tools.IsRedirect(text))
                        {
                            dest = HttpUtility.UrlDecode(Tools.RedirectTarget(text).Replace("_", " "));
                            string directLink = "[[" + dest + "|" + linkText + "]]";
                            StrParam = StrParam.Replace(link, directLink);
                        }
                        knownLinks.Add(ftu, Tools.TurnFirstToUpper(dest));
                    }
                    else if (value != ftu)
                    {
                        string directLink = "[[" + value + "|" + linkText + "]]";
                        StrParam = StrParam.Replace(link, directLink);
                    }
                    n++;
                    if (HasUI) UI.SetProgress(n, links.Count);
                }
                Result = StrParam;
                InvokeOnComplete();
            }
            catch (Exception e)
            {
                ErrorException = e;
                InvokeOnError();
            }
        }
        public void GetList(IListProvider what, params string[] params1)
        {
            ObjParam1 = what;
            ObjParam2 = params1;
            if (HasUI) UI = new PleaseWait();
            if (HasUI) UI.Show(Variables.MainForm as Form);
            InitThread(GetListFunc);
        }
        private void GetListFunc()
        {
            if (HasUI)
            {
                UI.Worker = Thread.CurrentThread;
                UI.Status = "Getting list of pages";
            }
            try
            {
                Result = ((IListProvider)ObjParam1).MakeList((string[])ObjParam2);
                InvokeOnComplete();
            }
            catch (Exception e)
            {
                ErrorException = e;
                InvokeOnError();
            }
        }
    }
    public class CrossThreadQueue<T>
    {
        private readonly Queue<T> Queue = new Queue<T>();
        public void Add(T value)
        {
            lock (Queue)
            {
                Queue.Enqueue(value);
            }
        }
        public T Remove()
        {
            lock (Queue)
            {
                return Queue.Dequeue();
            }
        }
        public int Count
        {
            get
            {
                lock (Queue)
                {
                    return Queue.Count;
                }
            }
        }
    }
}
