

package edu.rice.cs.plt.concurrent;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import edu.rice.cs.plt.collect.ListenerSet;
import edu.rice.cs.plt.collect.SnapshotSynchronizedSet;
import edu.rice.cs.plt.lambda.ResolvingThunk;
import edu.rice.cs.plt.lambda.Runnable1;
import edu.rice.cs.plt.lambda.WrappedException;
import edu.rice.cs.plt.tuple.Option;


public abstract class TaskController<R> implements ResolvingThunk<R>, Future<R> {
  
  
  protected final AtomicReference<State> state;
  private final CompletionMonitor _done; 
  private volatile ListenerSet<R> _finishListeners;
  
  protected TaskController() {
    state = new AtomicReference<State>(new FreshState());
    _done = new CompletionMonitor();
    
    _finishListeners = new ListenerSet<R>(SnapshotSynchronizedSet.<Runnable1<? super R>>makeLinkedHash());
  }
  
  
  public Status status() { return state.get().status(); }
  
  
  public boolean isDone() { Status s = status(); return s == Status.FINISHED || s == Status.CANCELED; }
  
  public boolean hasValue() { return status() == Status.FINISHED; }
  
  public boolean isCanceled() { return status() == Status.CANCELED; }
  
  public boolean isCancelled() { return status() == Status.CANCELED; }
  
  
  public boolean isResolved() {
    
    return ((Object) state.get()) instanceof TaskController.CleanlyFinishedState;
  }
  
  
  public void start() { state.get().start(); }
  
  
  public boolean cancel() { return state.get().cancel(true); }
  
  
  public boolean cancel(boolean stopRunning) { return state.get().cancel(stopRunning); }
  
  
  public R value() {
    try { return state.get().get(); }
    catch (Exception e) { throw new WrappedException(e); }
  }
  
  
  public R get() throws InterruptedException, ExecutionException { return state.get().get(); }
  
  
  public R get(long timeout) throws InterruptedException, ExecutionException, TimeoutException {
    return get(timeout, TimeUnit.MILLISECONDS);
  }
  
  
  public R get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
    return state.get().get(timeout, unit);
  }
  
  
  public Option<R> attemptGet(long timeout) throws ExecutionException {
    try { return Option.some(get(timeout, TimeUnit.MILLISECONDS)); }
    catch (InterruptedException e) { return Option.none(); }
    catch (TimeoutException e) { return Option.none(); }
  }
  
  
  public Option<R> attemptGet(long timeout, TimeUnit unit) throws ExecutionException {
    try { return Option.some(get(timeout, unit)); }
    catch (InterruptedException e) { return Option.none(); }
    catch (TimeoutException e) { return Option.none(); }
  }

  
  public ListenerSet<R>.Sink finishListeners() { return _finishListeners.sink(); }
  
  
  protected abstract void doStart();

  
  protected abstract void doStop();
  
  
  protected void discard() {}
  
  
  protected final void started() {
    boolean kept = false;
    State current = state.get();
    State next = runningState();
    
    while (((Object) current) instanceof TaskController.StartingState && !kept) {
      
      
      kept = state.weakCompareAndSet(current, next);
      if (kept) { ((StartingState) current).started(); }
      else { current = state.get(); }
    }
  }
  
  
  protected final void stopped() { finished(new CanceledState()); }
  
  
  protected final void finishedCleanly(R result) {
    finished(new CleanlyFinishedState(result));
    _finishListeners.run(result);
    _finishListeners.clear(); 
  }
  
  
  protected final void finishedWithTaskException(Exception e) {
    finished(new ExecutionExceptionState(new ExecutionException(e)));
  }
  
  
  protected final void finishedWithImplementationException(RuntimeException e) {
    finished(new InternalExceptionState(e));
  }
  
  private final void finished(State finishedState) {
    State current = state.get();
    boolean changed = false;
    while (!changed && current.status() != Status.CANCELED && current.status() != Status.FINISHED) {
      
      changed = state.weakCompareAndSet(current, finishedState);
      if (changed) { _done.signal(); discard(); }
      else { current = state.get(); }
    }
  }

  
  protected RunningState runningState() { return new RunningState(); }
  
  
  public static enum Status {
    
    PAUSED,
    
    RUNNING,
    
    FINISHED,
    
    CANCELED;
  }
  
  
  
  protected abstract class State {
    public abstract Status status();
    public abstract void start();
    public abstract boolean cancel(boolean stopRunning);
    public abstract R get() throws InterruptedException, ExecutionException;
    public abstract R get(long timeout, TimeUnit u) throws InterruptedException, ExecutionException, TimeoutException;
  }
  
  
  protected abstract class WaitingState extends State {
    public Status status() { return Status.PAUSED; }
    public boolean cancel(boolean stopRunning) {
      if (state.compareAndSet(this, new CanceledState())) {
        _done.signal();
        discard();
        return true;
      }
      else { return state.get().cancel(stopRunning); }
    }
    public R get() throws InterruptedException, ExecutionException {
      start(); return state.get().get();
    }
    public R get(long timeout, TimeUnit u) throws InterruptedException, ExecutionException, TimeoutException {
      start(); return state.get().get(timeout, u);
    }
  }
  
  
  protected abstract class ComputingState extends State {
    public R get() throws InterruptedException, ExecutionException {
      _done.ensureSignaled();
      return state.get().get();
    }
    public R get(long timeout, TimeUnit u) throws InterruptedException, ExecutionException, TimeoutException {
      _done.ensureSignaled(timeout, u);
      return state.get().get(); 
    }
  }
  
  
  protected class FreshState extends WaitingState {
    public void start() {
      if (state.compareAndSet(this, new FreshStartingState())) { doStart(); }
      else { state.get().start(); }
    }
  }
  
   
  protected abstract class StartingState extends ComputingState {
    public Status status() { return Status.PAUSED; }
    public void start() {}
    public boolean cancel(boolean stopRunning) {
      if (stopRunning) {
        if (state.compareAndSet(this, new CanceledStartingState())) { return true; }
        else { return state.get().cancel(stopRunning); }
      }
      else { return false; }
    }
    
    public abstract void started();
  }
  
  
  protected class FreshStartingState extends StartingState {
    public void started() {}
  }
  
  
  protected class CanceledStartingState extends StartingState {
    public boolean cancel(boolean stopRunning) { return stopRunning; }
    public void started() { state.get().cancel(true); }
  }
  
  
  protected class RunningState extends ComputingState {
    public Status status() { return Status.RUNNING; }
    public void start() {}
    public boolean cancel(boolean stopRunning) {
      if (stopRunning) {
        if (state.compareAndSet(this, new CancelingState())) { doStop(); return true; }
        else { return state.get().cancel(stopRunning); }
      }
      else { return false; }
    }
  }
  
  
  protected class CancelingState extends ComputingState {
    public Status status() { return Status.RUNNING; }
    public void start() {}
    public boolean cancel(boolean stopRunning) { return stopRunning; }
  }
  
  
  protected abstract class FinishedState extends State {
    public Status status() { return Status.FINISHED; }
    public void start() {}
    public boolean cancel(boolean stopRunning) { return false; }
  }
  
  
  protected class CleanlyFinishedState extends FinishedState {
    private R _result;
    public CleanlyFinishedState(R result) { _result = result; }
    public R get() { return _result; }
    public R get(long timeout, TimeUnit u) { return _result; }
  }
  
  
  protected class ExecutionExceptionState extends FinishedState {
    private ExecutionException _e;
    public ExecutionExceptionState(ExecutionException e) { _e = e; }
    public R get() throws ExecutionException { throw _e; }
    public R get(long timeout, TimeUnit u) throws ExecutionException { throw _e; }
  }
  
  
  protected class InternalExceptionState extends FinishedState {
    private RuntimeException _e;
    public InternalExceptionState(RuntimeException e) { _e = e; }
    public R get() { throw _e; }
    public R get(long timeout, TimeUnit u) throws ExecutionException { throw _e; }
  }
  
  
  protected class CanceledState extends State {
    public Status status() { return Status.CANCELED; }
    public void start() { throw new CancellationException("Task is canceled"); }
    public boolean cancel(boolean stopRunning) { return false; }
    public R get() { throw new CancellationException("Task is canceled"); }
    public R get(long timeout, TimeUnit u) { throw new CancellationException("Task is canceled"); }
  }
  
}
