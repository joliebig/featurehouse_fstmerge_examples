using System;
using System.Threading;
using System.Net;
using System.Configuration;
using System.Diagnostics;
using NewsComponents.Collections;
namespace NewsComponents.Net
{
 internal class RequestThread {
  private static volatile int _maxRequests = 8;
  private readonly log4net.ILog _log = RssBandit.Common.Logging.Log.GetLogger(typeof(RequestThread));
  private PriorityQueue _waitingRequests;
  private volatile int _runningRequests;
  private AsyncWebRequest myAsyncWebRequest = null;
  internal RequestThread(AsyncWebRequest asyncWebRequest) {
   myAsyncWebRequest = asyncWebRequest;
   _waitingRequests = new PriorityQueue();
   _runningRequests = 0;
   string maxWorkerThreadsFromConfig = ConfigurationSettings.AppSettings["MaxDownloadThreads"];
   if (maxWorkerThreadsFromConfig != null && maxWorkerThreadsFromConfig.Length > 0) {
    try {
     int newMax = Convert.ToInt32(maxWorkerThreadsFromConfig);
     if (newMax > 0 && newMax < 50) {
      _maxRequests = newMax;
     }
    } catch {}
   }
   Thread thread = new Thread(new ThreadStart(Run));
   thread.IsBackground = true;
   thread.Priority = ThreadPriority.Normal;
   thread.Start();
  }
  public int RunningRequests {
   get {
    lock(_waitingRequests) {
     return _runningRequests;
    }
   }
  }
  public void QueueRequest(RequestState state, int priority) {
   lock(_waitingRequests) {
    _waitingRequests.Enqueue(priority, state);
    if (_runningRequests < _maxRequests)
     Monitor.Pulse(_waitingRequests);
   }
  }
  public void EndRequest(RequestState state) {
   lock(_waitingRequests) {
    _runningRequests--;
    if (_runningRequests < _maxRequests)
     Monitor.Pulse(_waitingRequests);
   }
  }
  private void Run() {
   lock(_waitingRequests) {
   _wait001:
    try {
     while ((_waitingRequests.Count == 0) || (_runningRequests >= _maxRequests)) {
      Monitor.Wait(_waitingRequests);
     }
     RequestState state = (RequestState) _waitingRequests.Dequeue();
     try {
      if (state.OnRequestStart()) {
       myAsyncWebRequest.RequestStartCancelled(state);
       goto _wait001;
      }
     }
     catch (Exception signalException) {
      _log.Error("Error during dispatch of StartDownloadCallBack()", signalException);
     }
     state.StartTime = DateTime.Now;
     _runningRequests++;
     try {
      _log.Debug("calling BeginGetResponse for " + state.Request.RequestUri);
      IAsyncResult result = state.Request.BeginGetResponse(new AsyncCallback(myAsyncWebRequest.ResponseCallback), state);
      ThreadPool.RegisterWaitForSingleObject (result.AsyncWaitHandle, new WaitOrTimerCallback(myAsyncWebRequest.TimeoutCallback), state, state.Request.Timeout, true);
     }
     catch (Exception responseException) {
      state.OnRequestException(responseException);
      myAsyncWebRequest.FinalizeWebRequest(state);
     }
     goto _wait001;
    } catch (Exception ex) {
     _log.Fatal("Critical exception caught in RequestThread.Run()!", ex);
    }
    goto _wait001;
   }
  }
 }
 internal class RequestThread2
 {
  private static int _maxRequests = 8;
  private static readonly log4net.ILog _log = RssBandit.Common.Logging.Log.GetLogger(typeof(RequestThread));
  static PriorityQueue _waitingRequests;
  static int _runningRequests;
  private static AsyncWebRequest myAsyncWebRequest = null;
  static RequestThread2()
  {
   _waitingRequests = new PriorityQueue();
   _runningRequests = 0;
   string maxWorkerThreadsFromConfig = ConfigurationSettings.AppSettings["MaxDownloadThreads"];
   if (maxWorkerThreadsFromConfig != null && maxWorkerThreadsFromConfig.Length > 0) {
    try {
     int newMax = Convert.ToInt32(maxWorkerThreadsFromConfig);
     if (newMax > 0 && newMax < 50) {
      _maxRequests = newMax;
     }
    } catch {}
   }
   Thread thread = new Thread(Run);
            thread.TrySetApartmentState(ApartmentState.MTA);
   thread.IsBackground = true;
   thread.Priority = ThreadPriority.Normal;
   thread.Start();
  }
  public static int RunningRequests {
   get {
    lock(_waitingRequests) {
     return _runningRequests;
    }
   }
  }
  public static void QueueRequest(RequestState state, int priority) {
   lock(_waitingRequests) {
    _waitingRequests.Enqueue(priority, state);
    Monitor.Pulse(_waitingRequests);
   }
  }
  public static void TryActivateNext() {
   lock(_waitingRequests) {
    Monitor.Pulse(_waitingRequests);
    }
  }
  public static void EndRequest(RequestState state) {
   lock(_waitingRequests) {
    Interlocked.Decrement(ref _runningRequests);
    Monitor.Pulse(_waitingRequests);
   }
  }
  private static void Run() {
   RequestState state = null;
   bool requestsQueued = false;
   try {
    goto waitOrNextRequest;
waitForNextPulse:
    lock(_waitingRequests) {
     Monitor.Wait(_waitingRequests);
    }
waitOrNextRequest:
    if (_runningRequests >= _maxRequests) {
     goto waitForNextPulse;
    }
    lock(_waitingRequests) {
     state = null;
     requestsQueued = (_waitingRequests.Count > 0);
     if (requestsQueued) {
      state = ((RequestState)_waitingRequests.Dequeue());
     }
    }
    if (!requestsQueued) {
     goto waitForNextPulse;
    }
    try {
     if (state == null )
      goto waitOrNextRequest;
     if (state.OnRequestStart()) {
      myAsyncWebRequest.RequestStartCancelled(state);
      goto waitOrNextRequest;
     }
    }
    catch (Exception e) {
     Trace.WriteLine("Error during dispatch of OnRequestStart() callback", e.ToString());
    }
    try {
     IAsyncResult result = state.Request.BeginGetResponse(new AsyncCallback(myAsyncWebRequest.ResponseCallback), state);
     ThreadPool.RegisterWaitForSingleObject (result.AsyncWaitHandle, new WaitOrTimerCallback(myAsyncWebRequest.TimeoutCallback), state, state.Request.Timeout, true);
     state.StartTime = DateTime.Now;
     Interlocked.Increment(ref _runningRequests);
    }
    catch (WebException we) {
     state.OnRequestException(we);
     myAsyncWebRequest.FinalizeWebRequest(state);
    }
    catch (Exception e) {
     state.OnRequestException(e);
     myAsyncWebRequest.FinalizeWebRequest(state);
    }
    goto waitOrNextRequest;
   }
   catch (Exception ex) {
    Trace.WriteLine("Critical exception caught in RequestThread.Run():" + ex.ToString());
   }
  }
 }
}
