

package edu.rice.cs.plt.concurrent;

import java.util.concurrent.Executor;

import edu.rice.cs.plt.lambda.Thunk;
import edu.rice.cs.plt.lambda.WrappedException;


public class ExecutorTaskController<R> extends TaskController<R> {
  
  private Executor _executor;
  private Thunk<? extends R> _task;
  
  private volatile Thread _t;
  
  public ExecutorTaskController(Executor executor, Thunk<? extends R> task) {
    _executor = executor;
    _task = task;
    _t = null; 
  }
  
  protected void doStart() {
    _executor.execute(new Runnable() {
      public void run() {
        _t = Thread.currentThread();
        started();
        try {
          
          if (Thread.interrupted()) { throw new InterruptedException(); }
          finishedCleanly(_task.value());
        }
        catch (InterruptedException e) { stopped(); }
        catch (WrappedException e) {
          if (e.getCause() instanceof InterruptedException) { stopped(); }
          else { finishedWithTaskException(e); }
        }
        catch (RuntimeException e) { finishedWithTaskException(e); }
      }
    });
  }
  
  protected void doStop() { _t.interrupt(); }
  protected void discard() { _executor = null; _task = null; _t = null; }
}
