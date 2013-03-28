using System;
using System.Configuration;
using System.Text;
using System.Windows.Forms;
using System.Reflection;
using System.Threading;
using System.Text.RegularExpressions;
using System.Web;
using WikiFunctions.API;
namespace WikiFunctions
{
    public delegate string EventHandlerAddition();
    public partial class ErrorHandler : Form
    {
        public static event EventHandlerAddition AppendToEventHandler;
        public static string CurrentPage;
        public static long CurrentRevision;
        public static string ListMakerText;
        private static bool HandleKnownExceptions(Exception ex)
        {
            if (ex is ArgumentException && ex.StackTrace.Contains("System.Text.RegularExpressions"))
            {
                MessageBox.Show(ex.Message, "Invalid regular expression",
                                MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
            else if (ex is System.Net.WebException || ex.InnerException is System.Net.WebException)
            {
                MessageBox.Show(ex.Message, "Network access error",
                                MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
            else if (ex is OutOfMemoryException)
            {
                MessageBox.Show(ex.Message, "Out of Memory error",
                                MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
            else if (ex is System.IO.IOException || ex is ConfigurationErrorsException
                && (ex.InnerException != null && ex.InnerException.InnerException != null
                && ex.InnerException.InnerException is System.IO.IOException))
            {
                MessageBox.Show(ex.Message, "I/O error",
                                MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
            else
                return false;
            return true;
        }
        new public static void Handle(Exception ex)
        {
            if (ex == null || HandleKnownExceptions(ex)) return;
            //ErrorHandler handler = new ErrorHandler() { txtError = { Text = ex.Message } };
            StringBuilder errorMessage = new StringBuilder("{{AWB bug\r\n | status      = new <!-- when fixed replace with \"fixed\" -->\r\n | description = ");
            var thread = (ex is ApiException) ? (ex as ApiException).ThrowingThread : Thread.CurrentThread;
            if (thread.Name != "Main thread")
                errorMessage.AppendLine("nThread: " + thread.Name);
            errorMessage.Append("<table>");
            FormatException(ex, errorMessage, ExceptionKind.TopLevel);
            errorMessage.AppendLine("</table>");
            if (AppendToEventHandler != null)
            {
                foreach (Delegate d in AppendToEventHandler.GetInvocationList())
                {
                    string retval = d.DynamicInvoke().ToString();
                    if (!string.IsNullOrEmpty(retval))
                        errorMessage.AppendLine(retval);
                }
            }
            errorMessage.AppendLine("~~~~");
            errorMessage.AppendLine(" | OS          = " + Environment.OSVersion);
            errorMessage.Append(" | version     = " + Assembly.GetExecutingAssembly().GetName().Version);
            string revision;
            try
            {
                revision = Variables.Revision;
            }
            catch
            {
                revision = "?";
            }
            if (!revision.Contains("?")) errorMessage.AppendLine(", revision " + revision);
            errorMessage.AppendLine(" | net     = " + Environment.Version);
            if (!string.IsNullOrEmpty(CurrentPage))
            {
                string link = "[" + Variables.URLIndex + "?title=" + HttpUtility.UrlEncode(CurrentPage) + "&oldid=" + CurrentRevision + "]";
                errorMessage.AppendLine(" | duplicate   = [encountered while processing page ''" + link + "'']");
            }
            else if (!string.IsNullOrEmpty(ListMakerText))
                errorMessage.AppendLine(" | duplicate   = '''ListMaker Text:''' " + ListMakerText);
            if (!string.IsNullOrEmpty(Variables.URL))
                errorMessage.AppendLine(" | site    = " + Variables.URL);
            errorMessage.AppendLine(" | workaround     = <!-- Any workaround for the problem -->");
            errorMessage.AppendLine(" | fix_version    = <!-- Version of AWB the fix will be included in; AWB developer will complete when it's fixed -->");
            errorMessage.AppendLine("}}");
            handler.txtDetails.Text = errorMessage.ToString();
            handler.txtSubject.Text = ex.GetType().Name + " in " + Thrower(ex);
            handler.ShowDialog();
        }
        enum ExceptionKind { TopLevel, Inner, LoaderException };
        private static void FormatException(Exception ex, StringBuilder sb, ExceptionKind kind)
        {
            sb.Append("<tr><td>" + KindToString(kind) + ":<td><code>"
                + ex.GetType().Name + "</code><tr><td>Message:<td><code>"
                + ex.Message + "</code><tr><td>Call stack:<td><pre>" + ex.StackTrace + "</pre></tr>\r\n");
            if (ex.InnerException != null)
            {
                FormatException(ex.InnerException, sb, ExceptionKind.Inner);
            }
            if (ex is ReflectionTypeLoadException)
            {
                foreach (Exception e in ((ReflectionTypeLoadException)ex).LoaderExceptions)
                {
                    FormatException(e, sb, ExceptionKind.LoaderException);
                }
            }
        }
        private static string KindToString(ExceptionKind ek)
        {
            switch (ek)
            {
                case ExceptionKind.Inner:
                    return "Inner exception";
                case ExceptionKind.LoaderException:
                    return "Loader exception";
                default:
                    return "Exception";
            }
        }
        public static string[] MethodNames(Exception ex)
        {
            return MethodNames(ex.StackTrace);
        }
        private static readonly Regex StackTrace = new Regex(@"([a-zA-Z_0-9.`]+)(?=\()", RegexOptions.Compiled);
        public static string[] MethodNames(string stackTrace)
        {
            MatchCollection mc = StackTrace.Matches(stackTrace);
            string[] res = new string[mc.Count];
            for (int i = 0; i < res.Length; i++) res[i] = mc[i].Groups[1].Value;
            return res;
        }
        public static string Thrower(Exception ex)
        {
            return Thrower(ex.StackTrace);
        }
        static readonly string[] PresetNamespaces =
            new [] { "System.", "Microsoft.", "Mono." };
        public static string Thrower(string stackTrace)
        {
            string[] trace = MethodNames(stackTrace);
            if (trace.Length == 0) return "unknown function";
            string res = "";
            for (int i = 0; i < trace.Length; i++)
            {
                bool match = false;
                foreach (string ns in PresetNamespaces)
                {
                    if (trace[i].StartsWith(ns)) match = true;
                }
                if (match)
                    res = trace[0];
                else
                {
                    res = trace[i];
                    break;
                }
            }
            var res2 = Regex.Match(res, @"\w+\.{1,2}\w+$").Value;
            if (res2.Length > 0) return res2;
            return res;
        }
        protected ErrorHandler()
        {
            InitializeComponent();
        }
        private void ErrorHandler_Load(object sender, EventArgs e)
        {
            Text = Application.ProductName;
        }
        private void btnCopy_Click(object sender, EventArgs e)
        {
            try
            {
                Clipboard.Clear();
                Thread.Sleep(50);
                Clipboard.SetText(txtDetails.Text);
            }
            catch { }
        }
        private void linkLabel1_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            linkLabel1.LinkVisited = true;
            try
            {
                System.Diagnostics.Process.Start("http://en.wikipedia.org/w/index.php?title=Wikipedia_talk:AutoWikiBrowser/Bugs&action=edit&section=new");
            }
            catch { }
        }
    }
}
