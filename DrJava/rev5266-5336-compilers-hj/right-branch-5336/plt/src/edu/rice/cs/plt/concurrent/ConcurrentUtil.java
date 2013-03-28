

package edu.rice.cs.plt.concurrent;

import java.io.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import edu.rice.cs.plt.lambda.*;
import edu.rice.cs.plt.io.IOUtil;
import edu.rice.cs.plt.io.VoidOutputStream;

import static edu.rice.cs.plt.debug.DebugUtil.error;
import static edu.rice.cs.plt.debug.DebugUtil.debug;


public final class ConcurrentUtil {
  
  private ConcurrentUtil() {}
  
  
  public static final Runnable1<Long> SLEEPING_RUNNABLE = new SleepingRunnable();
  
  private static final class SleepingRunnable implements Runnable1<Long>, Serializable {
    public void run(Long delay) {
      try { Thread.sleep(delay); }
      catch (InterruptedException e) {  }
    }
  }

  
  public static void sleep(long delay) { SLEEPING_RUNNABLE.run(delay); }

  
  public static final Runnable1<Long> WORKING_RUNNABLE = new WorkingRunnable();
  
  private static final class WorkingRunnable implements Runnable1<Long>, Serializable {
    private long junk = 1;
    public void run(Long delay) {
      long finished = System.currentTimeMillis() + delay;
      while (System.currentTimeMillis() < finished) {
        if (Thread.interrupted()) break;
        
        for (int i = 0; i < 10000; i++) { junk = junk * (delay+1); }
      }
    }
  }
  
  
  public static void work(long delay) { WORKING_RUNNABLE.run(delay); }
  
  
  public static long futureTimeNanos(long time, TimeUnit unit) {
    return System.nanoTime() + unit.toNanos(time);
  }
  
  
  public static long futureTimeMillis(long time, TimeUnit unit) {
    return System.currentTimeMillis() + unit.toMillis(time);
  }
  
  
  public static void waitUntilMillis(Object obj, long futureTime) throws InterruptedException, TimeoutException {
    long delta = futureTime - System.currentTimeMillis();
    if (delta > 0) { obj.wait(delta); }
    else { throw new TimeoutException(); }
  }
  
  
  public static void waitUntilNanos(Object obj, long futureTime) throws InterruptedException, TimeoutException {
    long delta = futureTime - System.nanoTime();
    if (delta > 0) { TimeUnit.NANOSECONDS.timedWait(obj, delta); }
    else { throw new TimeoutException(); }
  }
  
  
  public static <T> Callable<T> asCallable(Thunk<? extends T> thunk) {
    return new ThunkCallable<T>(thunk);
  }
  
  private static final class ThunkCallable<T> implements Callable<T>, Serializable {
    private final Thunk<? extends T> _thunk;
    public ThunkCallable(Thunk<? extends T> thunk) { _thunk = thunk; }
    public T call() { return _thunk.value(); }
  }
  
  
  public static <T> TaskController<T> asTaskController(Future<? extends T> future) {
    TaskController<T> result = new FutureTaskController<T>(LambdaUtil.valueLambda(future));
    result.start();
    return result;
  }
  
  
  public static <T> TaskController<T> asTaskController(Thunk<? extends Future<? extends T>> futureThunk) {
    return new FutureTaskController<T>(futureThunk);
  }
  
  
  public static final Executor THREAD_EXECUTOR = new Executor() {
    private int count = 0;
    public void execute(Runnable r) {
      new Thread(r, "THREAD_EXECUTOR-" + (++count)).start();
    }
  };
  
  
  public static final Executor DIRECT_EXECUTOR = new Executor() {
    public void execute(Runnable r) { r.run(); }
  };
  
  
  public static TaskController<Void> runInThread(Runnable task) {
    return computeWithExecutor(LambdaUtil.asThunk(task), THREAD_EXECUTOR, true);
  }
  
  
  public static TaskController<Void> runInThread(Runnable task, boolean start) {
    return computeWithExecutor(LambdaUtil.asThunk(task), THREAD_EXECUTOR, start);
  }
  
  
  public static <R> TaskController<R> computeInThread(Thunk<? extends R> task) {
    return computeWithExecutor(task, THREAD_EXECUTOR, true);
  }
  
  
  public static <R> TaskController<R> computeInThread(Thunk<? extends R> task, boolean start) {
    return computeWithExecutor(task, THREAD_EXECUTOR, start);
  }
  
  
  public static <I, R> IncrementalTaskController<I, R> computeInThread(IncrementalTask<? extends I, ? extends R> task) {
    return computeWithExecutor(task, THREAD_EXECUTOR, true, false);
  }
  
  
  public static <I, R>
      IncrementalTaskController<I, R> computeInThread(IncrementalTask<? extends I, ? extends R> task, boolean start) {
    return computeWithExecutor(task, THREAD_EXECUTOR, start, false);
  }
  
  
  public static <I, R>
    IncrementalTaskController<I, R> computeInThread(IncrementalTask<? extends I, ? extends R> task, 
                                                    boolean start, boolean ignoreIntermediate) {
    return computeWithExecutor(task, THREAD_EXECUTOR, start, ignoreIntermediate);
  }
  
  
  public static <R> TaskController<R> computeWithExecutor(Thunk<? extends R> task, Executor exec) {
    return computeWithExecutor(task, exec, true);
  }
    
  
  public static <R> TaskController<R> computeWithExecutor(Thunk<? extends R> task, Executor exec, boolean start) {
    ExecutorTaskController<R> result = new ExecutorTaskController<R>(exec, task);
    if (start) { result.start(); }
    return result;
  }
  
  
  public static <I, R>
      IncrementalTaskController<I, R> computeWithExecutor(IncrementalTask<? extends I, ? extends R> task,
                                                          Executor exec) {
    return computeWithExecutor(task, exec, true, false);
  }
  
  
  public static <I, R>
      IncrementalTaskController<I, R> computeWithExecutor(IncrementalTask<? extends I, ? extends R> task, 
                                                          Executor exec, boolean start) {
    return computeWithExecutor(task, exec, start, false);
  }
  
  
  public static <I, R>
    IncrementalTaskController<I, R> computeWithExecutor(IncrementalTask<? extends I, ? extends R> task, 
                                                        Executor exec, boolean start, boolean ignoreIntermediate) {
    IncrementalTaskController<I, R> result =
      new ExecutorIncrementalTaskController<I, R>(exec, task, ignoreIntermediate);
    if (start) { result.start(); }
    return result;
  }
  
  
  
  public static <R> TaskController<R> computeInProcess(Thunk<? extends R> task) {
    return computeInProcess(task, JVMBuilder.DEFAULT, true);
  }

  
  public static <R> TaskController<R> computeInProcess(Thunk<? extends R> task, boolean start) {
    return computeInProcess(task, JVMBuilder.DEFAULT, start);
  }
  
  
  public static <R> TaskController<R> computeInProcess(Thunk<? extends R> task, JVMBuilder jvmBuilder) {
    return computeInProcess(task, jvmBuilder, true);
  }
  
  
  public static <R> TaskController<R> computeInProcess(Thunk<? extends R> task, JVMBuilder jvmBuilder,
                                                       boolean start) {
    jvmBuilder = jvmBuilder.addDefaultProperties(getProperties("plt."));
    ProcessTaskController<R> controller = new ProcessTaskController<R>(jvmBuilder, THREAD_EXECUTOR, task);
    if (start) { controller.start(); }
    return controller;
  }
   
  
  
  public static <I, R>
      IncrementalTaskController<I, R> computeInProcess(IncrementalTask<? extends I, ? extends R> task) {
    return computeInProcess(task, JVMBuilder.DEFAULT, true, false);
  }

  
  public static <I, R>
      IncrementalTaskController<I, R> computeInProcess(IncrementalTask<? extends I, ? extends R> task, boolean start) {
    return computeInProcess(task, JVMBuilder.DEFAULT, start, false);
  }
  
  
  public static <I, R>
      IncrementalTaskController<I, R> computeInProcess(IncrementalTask<? extends I, ? extends R> task,
                                                       JVMBuilder jvmBuilder) {
    return computeInProcess(task, jvmBuilder, true, false);
  }
  
  
  public static <I, R>
      IncrementalTaskController<I, R> computeInProcess(IncrementalTask<? extends I, ? extends R> task,
                                                       JVMBuilder jvmBuilder, boolean start) {
    return computeInProcess(task, jvmBuilder, start, false);
  }
   
  
  public static <I, R>
      IncrementalTaskController<I, R> computeInProcess(IncrementalTask<? extends I, ? extends R> task,
                                                       JVMBuilder jvmBuilder, boolean start,
                                                       boolean ignoreIntermediate) {
    jvmBuilder = jvmBuilder.addDefaultProperties(getProperties("plt."));
    ProcessIncrementalTaskController<I, R> controller =
      new ProcessIncrementalTaskController<I, R>(jvmBuilder, THREAD_EXECUTOR, task, ignoreIntermediate);
    if (start) { controller.start(); }
    return controller;
  }
   
  
  
  
  public static Remote exportInProcess(Thunk<? extends Remote> factory)
      throws InterruptedException, ExecutionException, IOException {
    return exportInProcess(factory, JVMBuilder.DEFAULT, null);
  }
  
  
  public static Remote exportInProcess(Thunk<? extends Remote> factory, JVMBuilder jvmBuilder)
      throws InterruptedException, ExecutionException, IOException {
    return exportInProcess(factory, jvmBuilder, null);
  }
  
  
  public static Remote exportInProcess(Thunk<? extends Remote> factory, JVMBuilder jvmBuilder,
                                       Runnable1<? super Process> onExit)
      throws InterruptedException, ExecutionException, IOException {
    Thunk<Remote> task = new ExportRemoteTask(factory);
    
    Executor exec = (onExit == null) ? DIRECT_EXECUTOR : THREAD_EXECUTOR;
    
    jvmBuilder = jvmBuilder.addDefaultProperty("java.rmi.server.hostname", "127.0.0.1");
    jvmBuilder = jvmBuilder.addDefaultProperties(getProperties("plt."));
    try { return new ProcessTaskController<Remote>(jvmBuilder, exec, task, onExit).get(); }
    
    catch (CancellationException e) { throw new InterruptedException(); }
    catch (WrappedException e) {
      if (e.getCause() instanceof IOException) { throw (IOException) e.getCause(); }
      else { throw e; }
    }
  }
  
  private static class ExportRemoteTask implements Thunk<Remote>, Serializable {
    private final Thunk<? extends Remote> _factory;
    
    
    private static final List<Remote> _cache = new ArrayList<Remote>(1);
    public ExportRemoteTask(Thunk<? extends Remote> factory) { _factory = factory; }
    public Remote value() {
      Remote server = _factory.value();
      _cache.add(server);
      try { return UnicastRemoteObject.exportObject(server, 0); }
      catch (RemoteException e) { throw new WrappedException(e); }
    }
  }


  
  public static boolean processIsTerminated(Process p) {
    try { p.exitValue(); return true; }
    catch (IllegalThreadStateException e) { return false; }
  }
  
  
  public static void onProcessExit(final Process p, final Runnable1<? super Process> listener) {
    Thread t = new Thread("ConcurrentUtil.onProcessExit") {
      public void run() {
        try { p.waitFor(); listener.run(p); }
        catch (InterruptedException e) {  }
      }
    };
    t.setDaemon(true);
    t.start();
  }
  
  
  public static void discardProcessOutput(Process p) {
    copyProcessOut(p, VoidOutputStream.INSTANCE);
    copyProcessErr(p, VoidOutputStream.INSTANCE);
  }
  
  
  public static void copyProcessOutput(Process p, OutputStream out, OutputStream err) {
    copyProcessOut(p, out);
    copyProcessErr(p, err);
  }
  
  
  public static Thread discardProcessOut(Process p) { return copyProcessOut(p, VoidOutputStream.INSTANCE); }
  
  
  public static Thread copyProcessOut(Process p, OutputStream out) { return copyProcessOut(p, out, true); }
  
  
  public static Thread copyProcessOut(Process p, OutputStream out, boolean close) {
    Thread result = new Thread(new CopyStream(p.getInputStream(), out, close), "ConcurrentUtil.copyProcessOut");
    result.setDaemon(true); 
    result.start();
    return result;
  }

  
  public static TaskController<String> processOutAsString(Process p) {
    return computeInThread(new StreamToString(p.getInputStream()));
  }
  
  
  public static TaskController<String> processOutAsString(Process p, Executor exec) {
    return computeWithExecutor(new StreamToString(p.getInputStream()), exec);
  }
  
  
  public static Thread discardProcessErr(Process p) { return copyProcessErr(p, VoidOutputStream.INSTANCE); }
  
  
  public static Thread copyProcessErr(Process p, OutputStream err) { return copyProcessErr(p, err, false); }
  
  
  public static Thread copyProcessErr(Process p, OutputStream err, boolean close) {
    Thread result = new Thread(new CopyStream(p.getErrorStream(), err, close), "ConcurrentUtil.copyProcessErr");
    result.setDaemon(true); 
    result.start();
    return result;
  }
  
  
  public static TaskController<String> processErrAsString(Process p) {
    return computeInThread(new StreamToString(p.getErrorStream()));
  }
  
  
  public static TaskController<String> processErrAsString(Process p, Executor exec) {
    return computeWithExecutor(new StreamToString(p.getErrorStream()), exec);
  }
  
  
  private static final class CopyStream implements Runnable, Serializable {
    private final InputStream _in;
    private final OutputStream _out;
    private final boolean _close;
    public CopyStream(InputStream in, OutputStream out, boolean close) { _in = in; _out = out; _close = close; }
    public void run() {
      try {
        try { IOUtil.copyInputStream(_in, _out); }
        finally { if (_close) _out.close(); }
      }
      catch (IOException e) { error.log(e); }
    }
  }

  
  private static final class StreamToString implements Thunk<String> {
    private final InputStream _stream;
    public StreamToString(InputStream stream) { _stream = stream; }
    public String value() {
      try { return IOUtil.toString(new InputStreamReader(_stream)); }
      catch (IOException e) { throw new WrappedException(e); }
    }
  }
    
    
  
  public static Properties getProperties(String... prefixes) {
    Properties result = new Properties();
    
    
    for (Map.Entry<Object, Object> entry : System.getProperties().entrySet()) {
      for (String prefix : prefixes) {
        if (entry.getKey() instanceof String && ((String) entry.getKey()).startsWith(prefix)) {
          result.put(entry.getKey(), entry.getValue());
          break;
        }
      }
    }
    return result;
  }
  
  
  public static Map<String, String> getPropertiesAsMap(String... prefixes) {
    Map<String, String> result = new HashMap<String, String>();
    for (Map.Entry<Object, Object> entry : System.getProperties().entrySet()) {
      for (String prefix : prefixes) {
        if (entry.getKey() instanceof String && ((String) entry.getKey()).startsWith(prefix)) {
          result.put((String) entry.getKey(), entry.getValue().toString());
          break;
        }
      }
    }
    return result;
  }
  
}
