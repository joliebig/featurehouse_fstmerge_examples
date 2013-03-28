

package edu.rice.cs.plt.concurrent;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import edu.rice.cs.plt.lambda.Box;
import edu.rice.cs.plt.lambda.Predicate;

 
public class StateMonitor<T> implements Box<T>, Serializable {
  
  private volatile T _state;
  
  public StateMonitor(T state) { _state = state; }
  
  
  public T value() { return _state; }
  
  
  public synchronized void set(T newState) {
    _state = newState;
    this.notifyAll();
  }
  
    
  public synchronized T getAndSet(T newState) {
    T result = _state;
    _state = newState;
    this.notifyAll();
    return result;
  }
  
  
  public synchronized boolean compareAndSet(T expect, T update) {
    if (_state == expect) {
      _state = update;
      this.notifyAll();
      return true;
    }
    else { return false; }
  }
  
  
  public synchronized T ensureState(T expected) throws InterruptedException {
    while (!inState(expected)) { this.wait(); }
    return _state;
  }
  
  
  public T ensureState(T expected, long timeout) throws InterruptedException, TimeoutException {
    return ensureState(expected, timeout, TimeUnit.MILLISECONDS);
  }
  
  
  public synchronized T ensureState(T expected, long timeout, TimeUnit unit)
      throws InterruptedException, TimeoutException {
    if (!inState(expected)) {
      long timeoutTime = ConcurrentUtil.futureTimeNanos(timeout, unit);
      do { ConcurrentUtil.waitUntilNanos(this, timeoutTime); } while (!inState(expected));
    }
    return _state;
  }
  
  
  public synchronized T attemptEnsureState(T expected) {
    try { return ensureState(expected); }
    catch (InterruptedException e) { return _state; }
  }
  
  
  public T attemptEnsureState(T expected, long timeout) {
    try { ensureState(expected, timeout, TimeUnit.MILLISECONDS); return _state; }
    catch (InterruptedException e) { return _state; }
    catch (TimeoutException e) { return _state; }
  }
  
  
  public T attemptEnsureState(T expected, long timeout, TimeUnit unit) {
    try { ensureState(expected, timeout, unit); return _state; }
    catch (InterruptedException e) { return _state; }
    catch (TimeoutException e) { return _state; }
  }

  
  public synchronized T ensureNotState(T wrong) throws InterruptedException {
    while (inState(wrong)) { this.wait(); }
    return _state;
  }
  
  
  public T ensureNotState(T wrong, long timeout) throws InterruptedException, TimeoutException {
    return ensureNotState(wrong, timeout, TimeUnit.MILLISECONDS);
  }
  
  
  public synchronized T ensureNotState(T wrong, long timeout, TimeUnit unit)
      throws InterruptedException, TimeoutException {
    if (inState(wrong)) {
      long timeoutTime = ConcurrentUtil.futureTimeNanos(timeout, unit);
      do { ConcurrentUtil.waitUntilNanos(this, timeoutTime); } while (inState(wrong));
    }
    return _state;
  }
  
  
  public synchronized T attemptEnsureNotState(T wrong) {
    try { return ensureNotState(wrong); }
    catch (InterruptedException e) { return _state; }
  }
  
  
  public T attemptEnsureNotState(T expected, long timeout) {
    try { ensureNotState(expected, timeout, TimeUnit.MILLISECONDS); return _state; }
    catch (InterruptedException e) { return _state; }
    catch (TimeoutException e) { return _state; }
  }
  
  
  public T attemptEnsureNotState(T expected, long timeout, TimeUnit unit) {
    try { ensureNotState(expected, timeout, unit); return _state; }
    catch (InterruptedException e) { return _state; }
    catch (TimeoutException e) { return _state; }
  }


  
  public synchronized T ensurePredicate(Predicate<? super T> predicate) throws InterruptedException {
    while (!predicate.contains(_state)) { this.wait(); }
    return _state;
  }
  
  
  public T ensurePredicate(Predicate<? super T> predicate, long timeout)
      throws InterruptedException, TimeoutException {
    return ensurePredicate(predicate, timeout, TimeUnit.MILLISECONDS);
  }
  
  
  public synchronized T ensurePredicate(Predicate<? super T> predicate, long timeout, TimeUnit unit)
      throws InterruptedException, TimeoutException {
    if (!predicate.contains(_state)) {
      long timeoutTime = ConcurrentUtil.futureTimeNanos(timeout, unit);
      do { ConcurrentUtil.waitUntilNanos(this, timeoutTime); } while (!predicate.contains(_state));
    }
    return _state;
  }

  
  public synchronized T attemptEnsurePredicate(Predicate<? super T> predicate) {
    try { return ensurePredicate(predicate); }
    catch (InterruptedException e) { return _state; }
  }
  
  
  public T attemptEnsurePredicate(Predicate<? super T> predicate, long timeout) {
    try { ensurePredicate(predicate, timeout, TimeUnit.MILLISECONDS); return _state; }
    catch (InterruptedException e) { return _state; }
    catch (TimeoutException e) { return _state; }
  }
  
  
  public T attemptEnsurePredicate(Predicate<? super T> predicate, long timeout, TimeUnit unit) {
    try { ensurePredicate(predicate, timeout, unit); return _state; }
    catch (InterruptedException e) { return _state; }
    catch (TimeoutException e) { return _state; }
  }

  
  private boolean inState(T expected) {
    return (expected == null) ? (_state == null) : expected.equals(_state);
  }
  
}
