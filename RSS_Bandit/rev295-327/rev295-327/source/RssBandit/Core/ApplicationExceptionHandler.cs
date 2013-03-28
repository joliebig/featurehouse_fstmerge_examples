using System; 
using System.Text; 
using System.Threading; 
using System.Windows.Forms; 
using log4net; 
using Microsoft.ApplicationBlocks.ExceptionManagement; 
using RssBandit.Common.Logging; 
using RssBandit.Resources; namespace  RssBandit {
	
    internal class  ApplicationExceptionHandler {
		
        private static readonly  ILog _log = Log.GetLogger(typeof(ApplicationExceptionHandler));
 
        public  void OnAppDomainException(object sender, UnhandledExceptionEventArgs e)
        {
            if (e.ExceptionObject is ThreadAbortException)
            {
                return;
            }
            if (e.ExceptionObject is AccessViolationException)
            {
                string message = e.ExceptionObject.ToString();
                if (message.IndexOf("WSAGetOverlappedResult") >= 0 && message.IndexOf("CompletionPortCallback") >= 0)
                    _log.Debug("Unhandled exception ignored: ", (Exception) e.ExceptionObject);
                return;
            }
            DialogResult result = DialogResult.Cancel;
            try
            {
                Exception ex = (Exception) e.ExceptionObject;
                result = ShowExceptionDialog(ex);
            }
            catch (Exception fatal)
            {
                try
                {
                    Log.Fatal("Exception on publish AppDomainException.", fatal);
                    MessageBox.Show("Fatal Error: " + fatal.Message, "Fatal Error", MessageBoxButtons.OK,
                                    MessageBoxIcon.Stop);
                }
                finally
                {
                    Application.Exit();
                }
            }
            if (result == DialogResult.Abort)
                Application.Exit();
        }
 
        public static  DialogResult ShowExceptionDialog(Exception e)
        {
            return ShowExceptionDialog(e, false);
        }
 
        public static  DialogResult ShowExceptionDialog(Exception e, bool resumable)
        {
            ExceptionManager.Publish(e);
            try
            {
                StringBuilder errorMsg =
                    new StringBuilder(SR.ExceptionGeneralCritical(RssBanditApplication.GetLogFileName()));
                errorMsg.Append("\n" + e.Message);
                if (Application.MessageLoop && e.Source != null)
                    errorMsg.Append("\n@:" + e.Source);
                return
                    MessageBox.Show(errorMsg.ToString(),
                                    SR.GUIErrorMessageBoxCaption,
                                    (resumable ? MessageBoxButtons.AbortRetryIgnore : MessageBoxButtons.OK),
                                    MessageBoxIcon.Stop);
            }
            catch (Exception ex)
            {
                _log.Error("Critical exception in ShowExceptionDialog() ", ex);
            }
            return DialogResult.Abort;
        }

	}

}
