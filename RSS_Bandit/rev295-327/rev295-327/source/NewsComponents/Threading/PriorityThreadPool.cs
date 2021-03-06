using System; 
using System.Threading; 
using System.Collections; 
using System.Configuration; 
using NewsComponents.Collections; namespace  NewsComponents.Threading {
	
 public sealed class  PriorityThreadPool {
		
  private static  int _maxWorkerThreads = 4;
 
  static  PriorityQueue _waitingCallbacks;
 
  static  Semaphore _workerThreadNeeded;
 
  static  ArrayList _workerThreads;
 
  static  int _inUseThreads;
 
  private  PriorityThreadPool() {}
 
  static  PriorityThreadPool()
  {
   _waitingCallbacks = new PriorityQueue();
   _workerThreads = new ArrayList();
   _inUseThreads = 0;
   string maxWorkerThreadsFromConfig = ConfigurationSettings.AppSettings["MaxDownloadThreads"];
   if (maxWorkerThreadsFromConfig != null && maxWorkerThreadsFromConfig.Length > 0) {
    try {
     int newMax = Convert.ToInt32(maxWorkerThreadsFromConfig);
     if (newMax > 0) {
      _maxWorkerThreads = newMax;
     }
    } catch {}
   }
   _workerThreadNeeded = new Semaphore(0);
   for(int i=0; i<_maxWorkerThreads; i++)
   {
    Thread newThread = new Thread(new ThreadStart(ProcessQueuedItems));
    newThread.Name = "ManagedPriorityPooledThread #" + i.ToString();
    _workerThreads.Add(newThread);
    newThread.IsBackground = true;
    newThread.TrySetApartmentState(ApartmentState.MTA);
    newThread.Start();
   }
  }
 
  public static  void QueueUserWorkItem(WaitCallback callback, int priority)
  {
   QueueUserWorkItem(callback, null, priority);
  }
 
  public static  void QueueUserWorkItem(WaitCallback callback, object state, int priority)
  {
   WaitingCallback waiting = new WaitingCallback(callback, state);
   lock(_waitingCallbacks.SyncRoot) { _waitingCallbacks.Enqueue(priority, waiting); }
   _workerThreadNeeded.AddOne();
  }
 
  public static  void EmptyQueue()
  {
   lock(_waitingCallbacks.SyncRoot)
   {
    try
    {
     foreach(object obj in _waitingCallbacks)
     {
      ((WaitingCallback)obj).Dispose();
     }
    }
    catch
    {
    }
    _waitingCallbacks.Clear();
    _workerThreadNeeded.Reset(0);
   }
  }
 
  public static  int MaxThreads { get { return _maxWorkerThreads; } }
 
  public static  int ActiveThreads { get { return _inUseThreads; } }
 
  public static  int WaitingCallbacks { get { lock(_waitingCallbacks.SyncRoot) { return _waitingCallbacks.Count; } } }
 
  private static  void ProcessQueuedItems()
  {
   while(true)
   {
    WaitingCallback callback = null;
    while (callback == null)
    {
     lock(_waitingCallbacks.SyncRoot)
     {
      if (_waitingCallbacks.Count > 0)
      {
       callback = (WaitingCallback)_waitingCallbacks.Dequeue();
      }
     }
     if (callback == null) _workerThreadNeeded.WaitOne();
    }
    try
    {
     Interlocked.Increment(ref _inUseThreads);
     callback.Callback(callback.State);
    }
    catch
    {
    }
    finally
    {
     Interlocked.Decrement(ref _inUseThreads);
    }
   }
  }
 
  internal class  WaitingCallback  : IDisposable {
			
   private  WaitCallback _callback;
 
   private  object _state;
 
   public  WaitingCallback(WaitCallback callback, object state)
   {
    _callback = callback;
    _state = state;
   }
 
   public  WaitCallback Callback { get { return _callback; } }
 
   public  object State { get { return _state; } }
 
   public  void Dispose()
   {
    if (State is IDisposable) ((IDisposable)State).Dispose();
   }

		}

	}

}
