using System;
namespace WorldWind.Net
{
 public abstract class DownloadRequest : IDisposable
 {
  internal static DownloadQueue Queue;
  object m_owner;
  protected DownloadRequest(object owner)
  {
   m_owner = owner;
  }
  public abstract string Key
  {
   get;
  }
  public object Owner
  {
   get
   {
    return m_owner;
   }
   set
   {
    m_owner = value;
   }
  }
  public abstract float Progress
  {
   get;
  }
  public abstract bool IsDownloading
  {
   get;
  }
  public abstract void Start();
  public virtual float CalculateScore()
  {
   return 0;
  }
  public virtual void OnComplete()
  {
   Queue.OnComplete(this);
  }
  public virtual void Dispose()
  {
  }
 }
}
