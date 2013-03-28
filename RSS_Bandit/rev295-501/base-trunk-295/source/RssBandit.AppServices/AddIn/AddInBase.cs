using System;
namespace RssBandit.UIServices
{
 public abstract class AddInBase: MarshalByRefObject, IDisposable
 {
  private bool _disposed = false;
  public AddInBase(){}
  public override object InitializeLifetimeService()
  {
   return null;
  }
  public void Dispose() {
   Dispose(true);
   GC.SuppressFinalize(this);
  }
  protected virtual void Dispose(bool disposing) {
   if (!this._disposed) {
    if (disposing) {
    }
    this._disposed = true;
   }
  }
  ~AddInBase() {
   Dispose(false);
  }
 }
}
