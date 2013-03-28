

package edu.rice.cs.plt.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import edu.rice.cs.plt.collect.ListenerSet;
import edu.rice.cs.plt.lambda.WrappedException;


public abstract class IncrementalTaskController<I, R> extends TaskController<R> {
  
  private final boolean _ignoreIntermediate;
  private final AtomicInteger _steps;
  private final BlockingQueue<I> _intermediateQueue;
  private final ListenerSet<I> _intermediateListeners;
  
  
  protected IncrementalTaskController() { this(false); }

  
  protected IncrementalTaskController(boolean ignoreIntermediate) {
    _ignoreIntermediate = ignoreIntermediate;
    _steps = new AtomicInteger(0);
    _intermediateQueue = _ignoreIntermediate ? null : new LinkedBlockingQueue<I>();
    _intermediateListeners = new ListenerSet<I>();
  }
  
  
  public int steps() { return _steps.get(); }
  
  
  public BlockingQueue<I> intermediateQueue() {
    if (_ignoreIntermediate) { throw new IllegalStateException("No queue is maintained"); }
    else { return _intermediateQueue; }
  }
  
  
  public ListenerSet<I>.Sink intermediateListeners() {
    return _intermediateListeners.sink();
  }
  
  
  public void pause() {
    
    
    boolean success = false;
    do {
      State s = state.get();
      Object sObj = s; 
      if (sObj instanceof TaskController.RunningState) {
        success = state.compareAndSet(s, new FreshPausingState());
        if (success) { doPause(); }
      }
      else if (sObj instanceof TaskController.FreshStartingState) {
        success = state.compareAndSet(s, new PausedStartingState());
      }
      else if (sObj instanceof IncrementalTaskController.StartedPausingState) {
        success = state.compareAndSet(s, new FreshPausingState());
      }
      else if (sObj instanceof TaskController.CanceledState) {
        throw new CancellationException("Task is canceled");
      }
      else { 
        success = true;
      }
    } while (!success);
  }
  
  
  protected abstract void doPause();
  
  
  protected abstract void doResume();
  
  protected void paused() {
    boolean kept = false;
    State current = state.get();
    State next = new PausedState();
    
    while (((Object) current) instanceof IncrementalTaskController.PausingState && !kept) {
      
      
      kept = state.weakCompareAndSet(current, next);
      if (kept) { ((PausingState) current).paused(); }
      else { current = state.get(); }
    }
  }
  
  
  protected void stepped(I intermediateResult) {
    if (!_ignoreIntermediate) {
      try { _intermediateQueue.put(intermediateResult); }
      
      
      catch (InterruptedException e) { throw new WrappedException(e); }
    }
    _steps.incrementAndGet();
    _intermediateListeners.run(intermediateResult);
  }
  
  
  protected class PausedState extends WaitingState {
    public final void start() {
      if (state.compareAndSet(this, new FreshStartingState())) { doResume(); }
      else { state.get().start(); }
    }
  }
  
  
  protected abstract class PausingState extends ComputingState {
    public Status status() { return Status.RUNNING; }
    public boolean cancel(boolean stopRunning) {
      if (stopRunning) {
        if (state.compareAndSet(this, new CanceledPausingState())) { doStop(); return true; }
        else { return state.get().cancel(stopRunning); }
      }
      else { return false; }
    }
    
    public abstract void paused();
  }
  
  
  protected class FreshPausingState extends PausingState {
    public void start() {
      if (!state.compareAndSet(this, new StartedPausingState())) { state.get().start(); }
    }
    public R get() throws InterruptedException, ExecutionException {
      start(); return state.get().get();
    }
    public R get(long timeout, TimeUnit u) throws InterruptedException, ExecutionException, TimeoutException {
      start(); return state.get().get(timeout, u);
    }
    public void paused() {}
  }
  
  
  protected class CanceledPausingState extends PausingState {
    public void start() {} 
    public boolean cancel(boolean stopRunning) { return stopRunning; }
    public void paused() { state.get().cancel(true); }
  }
  
  
  protected class StartedPausingState extends PausingState {
    public void start() {}
    public void paused() { state.get().start(); }
  }
  
  
  protected class PausedStartingState extends StartingState {
    public void start() {
      if (!state.compareAndSet(this, new FreshStartingState())) { state.get().start(); }
    }
    public void started() { IncrementalTaskController.this.pause(); }
  }
  
}
