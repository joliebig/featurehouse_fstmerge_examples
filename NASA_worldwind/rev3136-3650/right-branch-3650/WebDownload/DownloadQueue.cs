using System;
using System.Collections;
namespace WorldWind.Net
{
 public class DownloadQueue : IDisposable
 {
  public static int MaxQueueLength = 200;
  public static int MaxConcurrentDownloads = 2;
  private ArrayList m_requests = new ArrayList();
  private ArrayList m_activeDownloads = new ArrayList();
  public DownloadQueue()
  {
   DownloadRequest.Queue = this;
  }
  public ArrayList Requests
  {
   get
   {
    return m_requests;
   }
  }
  public ArrayList ActiveDownloads
  {
   get
   {
    return m_activeDownloads;
   }
  }
  public virtual void Clear(object owner)
  {
   lock(m_requests.SyncRoot)
   {
    for(int i=m_requests.Count-1; i>=0; i--)
    {
     DownloadRequest request = (DownloadRequest)m_requests[i];
     if(request.Owner == owner)
     {
      m_requests.RemoveAt(i);
      request.Dispose();
     }
    }
   }
   ServiceDownloadQueue();
  }
  protected virtual DownloadRequest GetNextDownloadRequest()
  {
   DownloadRequest bestRequest = null;
   float highestScore = float.MinValue;
   lock(m_requests.SyncRoot)
   {
    for (int i = m_requests.Count-1; i>=0; i--)
    {
     DownloadRequest request = (DownloadRequest) m_requests[i];
     if(request.IsDownloading)
      continue;
     float score = request.CalculateScore();
     if(float.IsNegativeInfinity(score))
     {
      m_requests.RemoveAt(i);
      request.Dispose();
      continue;
     }
     if( score > highestScore )
     {
      highestScore = score;
      bestRequest = request;
     }
    }
   }
   return bestRequest;
  }
  public virtual void Add(DownloadRequest newRequest)
  {
   if(newRequest==null)
    throw new NullReferenceException();
   lock(m_requests.SyncRoot)
   {
    foreach(DownloadRequest request in m_requests)
    {
     if(request.Key == newRequest.Key)
     {
      newRequest.Dispose();
      return;
     }
    }
    m_requests.Add(newRequest);
    if(m_requests.Count > MaxQueueLength)
    {
     DownloadRequest leastImportantRequest = null;
     float lowestScore = float.MinValue;
     for (int i = m_requests.Count-1; i>=0; i--)
     {
      DownloadRequest request = (DownloadRequest) m_requests[i];
      if(request.IsDownloading)
       continue;
      float score = request.CalculateScore();
      if(score == float.MinValue)
      {
       m_requests.Remove(request);
       request.Dispose();
       return;
      }
      if( score < lowestScore )
      {
       lowestScore = score;
       leastImportantRequest = request;
      }
     }
     if(leastImportantRequest != null)
     {
      m_requests.Remove(leastImportantRequest);
      leastImportantRequest.Dispose();
     }
    }
   }
   ServiceDownloadQueue();
  }
  public virtual void Remove( DownloadRequest request )
  {
   lock(m_requests.SyncRoot)
   {
    for (int i = m_activeDownloads.Count-1; i>=0; i--)
     if(request == m_activeDownloads[i])
      return;
     m_requests.Remove(request);
   }
   request.Dispose();
   ServiceDownloadQueue();
  }
  protected virtual void ServiceDownloadQueue()
  {
   lock(m_requests.SyncRoot)
   {
    for (int i = m_activeDownloads.Count-1; i>=0; i--)
    {
     DownloadRequest request = (DownloadRequest) m_activeDownloads[i];
     if(!request.IsDownloading)
      m_activeDownloads.RemoveAt(i);
    }
    while(m_activeDownloads.Count < MaxConcurrentDownloads)
    {
     DownloadRequest request = GetNextDownloadRequest();
     if(request == null)
      break;
     m_activeDownloads.Add(request);
     request.Start();
    }
   }
  }
  internal void OnComplete( DownloadRequest request )
  {
   lock(m_requests.SyncRoot)
   {
    m_requests.Remove(request);
    request.Dispose();
   }
   ServiceDownloadQueue();
  }
  public void Dispose()
  {
   lock(m_requests.SyncRoot)
   {
    foreach(DownloadRequest request in m_requests)
     request.Dispose();
    m_requests.Clear();
    m_activeDownloads.Clear();
   }
   GC.SuppressFinalize(this);
  }
 }
}
