

package edu.rice.cs.plt.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import edu.rice.cs.plt.collect.ListenerSet;
import edu.rice.cs.plt.lambda.LazyRunnable;


public abstract class PollingTaskController<R> extends TaskController<R> {
  
  private final LazyRunnable _startDaemon;
  
  protected PollingTaskController() {
    
    
    _startDaemon = new LazyRunnable(new Runnable() {
      public void run() {
        Thread t = new Thread("PollingTaskController daemon") {
          public void run() {
            try { finish(); }
            catch (InterruptedException e) {  }
          }
        };
        t.setDaemon(true);
        t.start();
      }
    });
  }
  
  
  public ListenerSet<R>.Sink finishListeners() {
    _startDaemon.run();
    return super.finishListeners();
  }
  
 
  
  
  protected abstract void update();
  
  
  protected abstract void finish() throws InterruptedException;
  
  
  protected abstract void finish(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException;
  
  protected RunningState runningState() { return new PollingRunningState(); }
  
  
  protected class PollingRunningState extends RunningState {
    public Status status() {
      update();
      return (state.get() == this) ? Status.RUNNING : state.get().status();
    }
    public void start() {
      update();
      if (state.get() != this) { state.get().start(); }
    }
    public R get() throws InterruptedException, ExecutionException {
      finish();
      if (!isDone()) { throw new IllegalStateException("PollingTaskController returned without finishing"); }
      return state.get().get();
    }
    public R get(long timeout, TimeUnit u) throws InterruptedException, ExecutionException, TimeoutException  {
      finish(timeout, u);
      if (!isDone()) { throw new IllegalStateException("PollingTaskController returned without finishing"); }
      return state.get().get();
    }
    public boolean cancel(boolean stopRunning) {
      update();
      if (state.get() == this) {
        if (stopRunning) {
          if (state.compareAndSet(this, new PollingCancelingState())) { doStop(); return true; }
          else { return state.get().cancel(stopRunning); }
        }
        else { return false; }
      }
      else { return state.get().cancel(stopRunning); }
    }
  }
  
  
  protected class PollingCancelingState extends CancelingState {
    public void start() {
      update();
      if (state.get() != this) { state.get().start(); }
    }
    public Status status() {
      update();
      return (state.get() == this) ? Status.RUNNING : state.get().status();
    }
    public boolean cancel(boolean stopRunning) {
      update();
      return (state.get() == this) ? stopRunning : state.get().cancel(stopRunning);
    }
    public R get() throws InterruptedException, ExecutionException {
      finish();
      if (!isDone()) { throw new IllegalStateException("PollingRunner returned without finishing"); }
      return state.get().get();
    }
    public R get(long timeout, TimeUnit u) throws InterruptedException, ExecutionException, TimeoutException  {
      finish(timeout, u);
      if (!isDone()) { throw new IllegalStateException("PollingRunner returned without finishing"); }
      return state.get().get();
    }
  }
  
}
