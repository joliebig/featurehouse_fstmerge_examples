package edu.rice.cs.plt.concurrent;

import java.util.concurrent.Executor;

import edu.rice.cs.plt.lambda.WrappedException;


public class ExecutorIncrementalTaskController<I, R> extends IncrementalTaskController<I, R> {
  
  private Executor _executor;
  private IncrementalTask<? extends I, ? extends R> _task;
  private CompletionMonitor _continueMonitor;
  
  private volatile Thread _t;
  
  public ExecutorIncrementalTaskController(Executor executor, IncrementalTask<? extends I, ? extends R> task,
                                           boolean ignoreIntermediate) {
    super(ignoreIntermediate);
    _executor = executor;
    _task = task;
    _continueMonitor = new CompletionMonitor(false);
    _t = null;
  }
  
  protected void doStart() {
    _continueMonitor.signal();
    _executor.execute(new Runnable() {
      public void run() {
        _t = Thread.currentThread();
        started();
        try {
          while (!_task.isResolved()) {
            authorizeContinue();
            stepped(_task.step());
          }
          authorizeContinue();
          finishedCleanly(_task.value());
        }
        catch (WrappedException e) {
          if (e.getCause() instanceof InterruptedException) { stopped(); }
          else { finishedWithTaskException(e); }
        }
        catch (RuntimeException e) { finishedWithTaskException(e); }
        catch (InterruptedException e) { stopped(); }
        catch (Throwable t) { finishedWithImplementationException(new WrappedException(t)); }
      }
      private void authorizeContinue() throws InterruptedException {
        if (Thread.interrupted()) { throw new InterruptedException(); }
        if (!_continueMonitor.isSignaled()) {
          paused();
          _continueMonitor.ensureSignaled();
          started();
        }
      }
    });
  }

  protected void doPause() { _continueMonitor.reset(); }
  protected void doResume() { _continueMonitor.signal(); }
  protected void doStop() { _t.interrupt(); }
  protected void discard() { _executor = null; _task = null; _continueMonitor = null; _t = null; }
  
}
