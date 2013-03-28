using System; 
using System.Collections; 
using System.Threading; 
using System.ComponentModel; namespace  RssBandit {
	
 public class  ThreadWorkerTaskBase {
		
  protected  ThreadWorkerTaskBase(Enum taskID, ThreadWorkerProgressHandler progressHandler, object[] args) {
   this.TaskID = taskID;
   this.Arguments = args;
   this.ProgressHandler = progressHandler;
  }
 
  public virtual  ThreadWorkerBase GetWorkerInstance() {
   return new ThreadWorkerBase(this);
  }
 
  public  object[] Arguments;
 
  public  Enum TaskID;
 
  protected internal  ThreadWorkerProgressHandler ProgressHandler;
 
  protected internal  Thread WorkerThread;
 
  private  ThreadWorkerTaskBase() {}

	}
	
 public class  ThreadWorkerBase {
		
  public enum  DuplicateTaskQueued  {
   Ignore,
   Allowed,
   Wait,
   Abort,
  } 
  private  Exception LocalRunProcess() {
   if (RaiseBackroundTaskStarted(this.task))
    return null;
   try {
    return DoTaskWork(this.task);
   } catch (ThreadAbortException) {
   } catch (Exception ex) {
    RaiseBackgroundTaskFinished(this.task, 1, 1, ex, null);
    return ex;
   }
   return null;
  }
 
  protected virtual  Exception DoTaskWork(ThreadWorkerTaskBase task) {
   return new NotImplementedException("Inherit the class and override DoTaskWork() to work on tasks in background");
  }
 
  private  ThreadWorkerTaskBase task;
 
  static  ThreadWorkerBase() {
   queuedTasks = Hashtable.Synchronized(new Hashtable());
   taskStartInfos = Queue.Synchronized(new Queue());
   taskResultInfos = Queue.Synchronized(new Queue());
   waitForGlobalThreadResource = false;
   taskTimer = new System.Timers.Timer(100);
   taskTimer.Elapsed += new System.Timers.ElapsedEventHandler(OnTaskTimerElapsed);
   taskTimer.Start();
  }
 
  public  ThreadWorkerBase() { }
 
  public  ThreadWorkerBase(ThreadWorkerTaskBase task) {
   this.task = task;
  }
 
  public static  bool IsTaskQueued(Enum task) {
   return queuedTasks.ContainsKey(task);
  }
 
  public static  bool IsTaskWaitingForGlobalThreadResource(Enum task) {
   IEnumerator myEnumerator = taskStartInfos.GetEnumerator();
   while ( myEnumerator.MoveNext() ) {
    TaskStartInfo info = (TaskStartInfo)myEnumerator.Current;
    if (info.Task.TaskID.Equals(task))
     return true;
   }
   return false;
  }
 
  public static  Thread GetTaskThread(Enum task) {
   if (!queuedTasks.ContainsKey(task))
    return null;
   int its = 0, maxIts = 10;
   ThreadWorkerTaskBase tw = (ThreadWorkerTaskBase)queuedTasks[task];
   Thread t = tw.WorkerThread;
   while (t == null) {
    Thread.Sleep(100);
    if (!queuedTasks.ContainsKey(task))
     return null;
    tw = (ThreadWorkerTaskBase)queuedTasks[task];
    t = tw.WorkerThread;
    if (++its > maxIts)
     break;
   }
   return t;
  }
 
  public static  void AbortTask(Enum task) {
   if (queuedTasks.ContainsKey(task)) {
    Thread t = GetTaskThread(task);
    if (null != t) {
     try {
      t.Abort();
     } catch {}
    }
    queuedTasks.Remove(task);
    RaiseBackroundTasksRunning(queuedTasks.Count);
   }
  }
 
  public static  bool WaitForTask(Enum task) {
   bool terminated = true;
   if (queuedTasks.ContainsKey(task)) {
    Thread t = GetTaskThread(task);
    if (null != t) {
     if (t.IsAlive) {
      try {
       terminated = t.Join(15000);
      } catch {}
     }
    }
    if (terminated) {
     queuedTasks.Remove(task);
     RaiseBackroundTasksRunning(queuedTasks.Count);
    }
   }
   return terminated;
  }
 
  public static  void AbortAllTasks() {
   foreach (ThreadWorkerTaskBase t in queuedTasks.Values) {
    if (null != t && t.WorkerThread != null) {
     try {
      t.WorkerThread.Abort();
     } catch {}
    }
   }
   queuedTasks.Clear();
   RaiseBackroundTasksRunning(0);
  }
 
  public static  void ClearTasksWaitingForGlobalThreadResource() {
   taskStartInfos.Clear();
  }
 
  public static  bool QueueTask(ThreadWorkerTaskBase task) {
   return QueueTask(task , DuplicateTaskQueued.Ignore);
  }
 
  public static  bool QueueTask(ThreadWorkerTaskBase task, DuplicateTaskQueued action) {
   if (waitForGlobalThreadResource) {
    if (IsTaskWaitingForGlobalThreadResource(task.TaskID)) {
     if (action == DuplicateTaskQueued.Ignore)
      return false;
     if (action != DuplicateTaskQueued.Allowed) {
      return false;
     }
    }
    TaskStartInfo startInfo = new TaskStartInfo(TaskStartInfo.StartMethod.ThreadPool, task, action);
    taskStartInfos.Enqueue(startInfo);
    return true;
   }
   if (IsTaskQueued(task.TaskID)) {
    if (action == DuplicateTaskQueued.Ignore)
     return false;
    if (action != DuplicateTaskQueued.Allowed) {
     if (action == DuplicateTaskQueued.Abort) {
      AbortTask(task.TaskID);
     }
     if (action != DuplicateTaskQueued.Wait) {
      WaitForTask(task.TaskID);
     }
    }
   }
   queuedTasks.Add(task.TaskID, task);
   ThreadWorkerBase wc = task.GetWorkerInstance();
   return ThreadPool.QueueUserWorkItem( new WaitCallback (wc.RunProcess));
  }
 
  public static  bool StartTask(ThreadWorkerTaskBase task) {
   return StartTask(task, DuplicateTaskQueued.Ignore);
  }
 
  public static  bool StartTask(ThreadWorkerTaskBase task, DuplicateTaskQueued action) {
   if (waitForGlobalThreadResource) {
    if (IsTaskWaitingForGlobalThreadResource(task.TaskID)) {
     if (action == DuplicateTaskQueued.Ignore)
      return false;
     if (action != DuplicateTaskQueued.Allowed) {
      return false;
     }
    }
    TaskStartInfo startInfo = new TaskStartInfo(TaskStartInfo.StartMethod.ThreadStart, task, action);
    taskStartInfos.Enqueue(startInfo);
    return true;
   }
   if (IsTaskQueued(task.TaskID)) {
    if (action == DuplicateTaskQueued.Ignore)
     return false;
    if (action != DuplicateTaskQueued.Allowed) {
     if (action == DuplicateTaskQueued.Abort) {
      AbortTask(task.TaskID);
     }
     if (action != DuplicateTaskQueued.Wait) {
      WaitForTask(task.TaskID);
     }
    }
   }
   ThreadWorkerBase wc = task.GetWorkerInstance();
   Thread t = new Thread( new ThreadStart(wc.RunProcess));
   t.IsBackground = true;
   task.WorkerThread = t;
   t.Start();
   queuedTasks.Add(task.TaskID, task);
   return true;
  }
 
  public static  Exception RunTaskSynchronized(ThreadWorkerTaskBase task) {
   return RunTaskSynchronized(task, DuplicateTaskQueued.Ignore);
  }
 
  public static  Exception RunTaskSynchronized(ThreadWorkerTaskBase task, DuplicateTaskQueued action) {
   if (IsTaskQueued(task.TaskID)) {
    if (action == DuplicateTaskQueued.Ignore)
     return null;
    if (action != DuplicateTaskQueued.Allowed) {
     if (action == DuplicateTaskQueued.Abort) {
      AbortTask(task.TaskID);
     }
     if (action != DuplicateTaskQueued.Wait) {
      WaitForTask(task.TaskID);
     }
    }
   }
   ThreadWorkerBase wc = task.GetWorkerInstance();
   queuedTasks.Add(task.TaskID, task);
   try {
    return wc.LocalRunProcess();
   } finally {
    queuedTasks.Remove(task);
   }
  }
 
  public  void RunProcess ( object obj ) {
   Thread.CurrentThread.IsBackground = true;
   if (queuedTasks.ContainsKey(task.TaskID)) {
    ThreadWorkerTaskBase tw = (ThreadWorkerTaskBase)queuedTasks[task.TaskID];
    tw.WorkerThread = Thread.CurrentThread;
   }
   RaiseBackroundTasksRunning(queuedTasks.Count);
   try {
    LocalRunProcess();
   } finally {
    queuedTasks.Remove(task.TaskID);
    RaiseBackroundTasksRunning(queuedTasks.Count);
   }
  }
 
  internal  void RunProcess() {
   Thread.CurrentThread.IsBackground = true;
   RaiseBackroundTasksRunning(queuedTasks.Count);
   try {
    LocalRunProcess();
   } finally {
    queuedTasks.Remove(task.TaskID);
    RaiseBackroundTasksRunning(queuedTasks.Count);
   }
  }
 
  private  bool RaiseBackroundTaskStarted(ThreadWorkerTaskBase task) {
   if (OnBackgroundTaskStarted != null) {
    ThreadWorkerProgressArgs args = new ThreadWorkerProgressArgs(task.TaskID, 1, 0, null, false, null);
    OnBackgroundTaskStarted(task.WorkerThread, args);
   }
   return false;
  }
 
  protected  void RaiseBackroundTaskProgress(ThreadWorkerTaskBase task, int total, int current, Exception error, object result) {
   ThreadWorkerProgressArgs args = new ThreadWorkerProgressArgs(task.TaskID, total, current, error, false, result);
   taskResultInfos.Enqueue(new TaskResultInfo(task, args));
  }
 
  protected  void RaiseBackgroundTaskFinished(ThreadWorkerTaskBase task, int total, int current, Exception error, object result) {
   ThreadWorkerProgressArgs args = new ThreadWorkerProgressArgs(task.TaskID, total, current, error, true, result);
   taskResultInfos.Enqueue(new TaskResultInfo(task, args));
  }
 
  private static  void RaiseBackroundTasksRunning(int current) {
   if (OnBackgroundTaskRunning != null) {
    BackgroundTaskRunningArgs args = new BackgroundTaskRunningArgs(current);
    object sender = System.Threading.Thread.CurrentThread;
    try {
     OnBackgroundTaskRunning(sender, args);
    } catch {}
    if (args.Cancel) {
     AbortAllTasks();
    }
   }
  }
 
  public static  event BackgroundTaskRunningHandler OnBackgroundTaskRunning; 
  public static  event ThreadWorkerProgressHandler OnBackgroundTaskStarted; 
  private static  Hashtable queuedTasks;
 
  private static  Queue taskStartInfos;
 
  private static  Queue taskResultInfos;
 
  private static  bool waitForGlobalThreadResource;
 
  public static  bool WaitForGlobalThreadResource {
   get { return waitForGlobalThreadResource; }
   set { waitForGlobalThreadResource = value; }
  }
 
  public static  ISynchronizeInvoke SynchronizingObject {
   get { return synchronizingObject; }
   set { synchronizingObject = value; }
  }
 
  private static  System.Timers.Timer taskTimer;
 
  private static  ISynchronizeInvoke synchronizingObject;
 
  private static  void OnTaskTimerElapsed(object sender, System.Timers.ElapsedEventArgs e) {
   if (taskResultInfos.Count > 0) {
    TaskResultInfo t = (TaskResultInfo)taskResultInfos.Dequeue();
    DispatchInvocationToGuiThread(new MethodInvocation(t.Task.ProgressHandler, new object[]{t.Task, t.Args}));
   }
   if (! waitForGlobalThreadResource && taskStartInfos.Count > 0) {
    TaskStartInfo tInfo = (TaskStartInfo)taskStartInfos.Dequeue();
    if (TaskStartInfo.StartMethod.ThreadStart == tInfo.ThreadStartMethod) {
     StartTask(tInfo.Task, tInfo.Action);
    } else if (TaskStartInfo.StartMethod.ThreadPool == tInfo.ThreadStartMethod ) {
     QueueTask(tInfo.Task, tInfo.Action);
    }
   }
  }
 
  public static  void DispatchInvocationToGuiThread(object methodInvocationAsObject) {
   if (synchronizingObject != null && synchronizingObject.InvokeRequired) {
    try {
     synchronizingObject.Invoke(new WaitCallback(ThreadWorkerBase.DispatchInvocationToGuiThread),
                                new object[] {methodInvocationAsObject});
    } catch (ObjectDisposedException) {
    }
    return ;
   }
   MethodInvocation methodInvocation = (MethodInvocation) methodInvocationAsObject;
   methodInvocation.Invoke();
  }
 
  private class  TaskStartInfo {
			
   public enum  StartMethod  {
    ThreadStart,
    ThreadPool,
   } 
   public  TaskStartInfo(StartMethod startMethod, ThreadWorkerTaskBase task, DuplicateTaskQueued action) {
    this.startMethod = startMethod;
    this.task = task;
    this.action = action;
   }
 
   private readonly  StartMethod startMethod;
 
   public  StartMethod ThreadStartMethod {
    get { return startMethod; }
   }
 
   private readonly  ThreadWorkerTaskBase task;
 
   public  ThreadWorkerTaskBase Task {
    get { return task; }
   }
 
   private readonly  DuplicateTaskQueued action;
 
   public  DuplicateTaskQueued Action {
    get { return action; }
   }

		}
		
  private class  TaskResultInfo {
			
   public  TaskResultInfo(ThreadWorkerTaskBase task, ThreadWorkerProgressArgs args) {
    this.Task = task;
    this.Args = args;
   }
 
   public readonly  ThreadWorkerTaskBase Task;
 
   public readonly  ThreadWorkerProgressArgs Args;

		}
		
  public class  MethodInvocation {
			
   public  MethodInvocation(Delegate d, object[] args) {
    this.Delegate = d;
    this.Args = args;
   }
 
   public  Delegate Delegate;
 
   public  object[] Args;
 
   public  void Invoke() {
    this.Delegate.DynamicInvoke(this.Args);
   }

		}

	}
	
 public class  ThreadWorkerProgressArgs  : CancelEventArgs {
		
  public readonly  Enum TaskID;
 
  public readonly  Exception Exception;
 
  public readonly  int MaxProgress;
 
  public readonly  int CurrentProgress;
 
  public readonly  bool Done;
 
  public readonly  object Result;
 
  public  ThreadWorkerProgressArgs(int total, int current, Exception exception, bool done, object result):
   base(false) {
   this.Exception = exception;
   this.Done = done;
   this.MaxProgress = total;
   this.CurrentProgress = current;
   this.Result = result;
  }
 
  public  ThreadWorkerProgressArgs(Enum taskID, int total, int current):
   this(taskID, total, current, null, false, null) {}
 
  public  ThreadWorkerProgressArgs(Enum taskID, int total, int current, Exception exception):
   this(taskID, total, current, exception, false, null) {}
 
  public  ThreadWorkerProgressArgs(Enum taskID, int total, int current, Exception exception, bool done, object result):
   base(false) {
   this.TaskID = taskID;
   this.Exception = exception;
   this.Done = done;
   this.MaxProgress = total;
   this.CurrentProgress = current;
   this.Result = result;
  }

	}
	
 public delegate  void  ThreadWorkerProgressHandler (object sender, ThreadWorkerProgressArgs e);
	
 public class  BackgroundTaskRunningArgs  : CancelEventArgs {
		
  public  int CurrentRunning;
 
  public  BackgroundTaskRunningArgs():
   this(0) {}
 
  public  BackgroundTaskRunningArgs(int currentRunning):base(false) {
   this.CurrentRunning = currentRunning;
  }

	}
	
 public delegate  void  BackgroundTaskRunningHandler (object sender, BackgroundTaskRunningArgs e);

}
