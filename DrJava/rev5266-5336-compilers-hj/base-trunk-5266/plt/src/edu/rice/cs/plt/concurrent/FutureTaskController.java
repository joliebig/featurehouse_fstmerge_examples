

package edu.rice.cs.plt.concurrent;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import edu.rice.cs.plt.lambda.Thunk;
import edu.rice.cs.plt.lambda.WrappedException;


public class FutureTaskController<T> extends PollingTaskController<T> {
  
  private volatile Thunk<? extends Future<? extends T>> _futureThunk;
  private volatile Future<? extends T> _future;
  
  public FutureTaskController(Thunk<? extends Future<? extends T>> futureThunk) {
    _futureThunk = futureThunk;
    _future = null;
  }

  protected void doStart() { _future = _futureThunk.value(); started(); }
  protected void doStop() { _future.cancel(true); }
  protected void discard() { _futureThunk = null; _future = null; }
  
  protected void update() {
    if (_future.isDone()) {
      if (_future.isCancelled()) { stopped(); }
      else {
        try { finish(); }
        catch (InterruptedException e) {  }
      }
    }
  }
  
  protected void finish() throws InterruptedException {
    try { finishedCleanly(_future.get()); }
    catch (CancellationException e) { stopped(); }
    catch (ExecutionException e) { handleExecutionException(e); }
    catch (RuntimeException e) { finishedWithImplementationException(e); }
  }
  
  protected void finish(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
    try { finishedCleanly(_future.get(timeout, unit)); }
    catch (CancellationException e) { stopped(); }
    catch (ExecutionException e) { handleExecutionException(e); }
    catch (RuntimeException e) { finishedWithImplementationException(e); }
  }
  
  private void handleExecutionException(ExecutionException e) {
    Throwable cause = e.getCause(); 
    if (cause instanceof Exception) { finishedWithTaskException((Exception) cause); }
    else { finishedWithImplementationException(new WrappedException(e)); }
  }
      
}


