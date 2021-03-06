using System;
using System.Threading;
using System.Windows.Forms;
using RssBandit.WinGui.Forms;
namespace RssBandit.WinGui
{
 public abstract class EntertainmentThreadHandlerBase
 {
  protected AutoResetEvent p_workDone = new AutoResetEvent(false);
  protected Exception p_operationException = null;
  protected IWaitDialog p_waitDialog = null;
  protected Thread p_operationThread = null;
  protected TimeSpan p_operationTimeout = TimeSpan.Zero;
  protected string p_operationMessage = null;
  public EntertainmentThreadHandlerBase() {}
  public DialogResult Start(IWin32Window owner) {
   return this.Start(owner, this.p_operationMessage);
  }
  public DialogResult Start(IWin32Window owner, string waitMessage) {
   return this.Start(owner, waitMessage, true);
  }
  public DialogResult Start(IWin32Window owner, string waitMessage, bool allowCancel) {
   if (owner == null)
    throw new ArgumentNullException("owner");
   DialogResult result = DialogResult.OK;
   p_operationThread = new Thread(new ThreadStart(this.Run));
   p_waitDialog = owner as IWaitDialog;
   Form f = owner as Form;
   if (p_waitDialog == null) {
    p_waitDialog = new EntertainmentDialog();
   }
   p_waitDialog.Initialize(this.p_workDone, this.p_operationTimeout, f != null ? f.Icon: null);
   p_operationThread.Start();
   result = p_waitDialog.StartWaiting(owner, waitMessage, allowCancel);
   if (result != DialogResult.OK) {
    p_operationThread.Abort();
   }
   return result;
  }
  abstract protected void Run();
  public TimeSpan Timeout {
   get { return this.p_operationTimeout; }
   set { this.p_operationTimeout = value; }
  }
  public bool OperationSucceeds {
   get { return this.p_operationException == null; }
  }
  public Exception OperationException {
   get { return this.p_operationException; }
  }
  public string OperationMessage {
   get { return this.p_operationMessage; }
   set { this.p_operationMessage = value; }
  }
  protected AutoResetEvent WorkDone {
   get { return p_workDone; }
  }
 }
}
