

package edu.rice.cs.plt.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import edu.rice.cs.plt.lambda.Condition;


public class CompletionMonitor implements Condition {
  private volatile boolean _signal;
  
  
  public CompletionMonitor() { _signal = false; }
  
  
  public CompletionMonitor(boolean signaled) { _signal = signaled; }
  
  
  public boolean isSignaled() { return _signal; }
  
  
  public boolean isTrue() { return _signal; }
  
  
  public void reset() { _signal = false; }
  
  
  public void signal() {
    if (!_signal) {
      synchronized (this) {
        if (!_signal) {
          _signal = true;
          this.notifyAll();
        }
      }
    }
  }
  
  
  public void ensureSignaled() throws InterruptedException {
    if (!_signal) {
      synchronized (this) {
        while (!_signal) { this.wait(); }
      }
    }
  }
  
  
  public void ensureSignaled(long timeout) throws InterruptedException, TimeoutException {
    ensureSignaled(timeout, TimeUnit.MILLISECONDS);
  }
  
  
  public void ensureSignaled(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
    if (!_signal) {
      long timeoutTime = ConcurrentUtil.futureTimeNanos(timeout, unit);
      synchronized (this) {
        while (!_signal) { ConcurrentUtil.waitUntilNanos(this, timeoutTime); }
      }
    }
  }
  
  
  public boolean attemptEnsureSignaled() {
    try { ensureSignaled(); return true; }
    catch (InterruptedException e) { return _signal; }
  }
  
  
  public boolean attemptEnsureSignaled(long timeout) {
    try { ensureSignaled(timeout, TimeUnit.MILLISECONDS); return true; }
    catch (InterruptedException e) { return _signal; }
    catch (TimeoutException e) { return _signal; }
  }
  
  
  public boolean attemptEnsureSignaled(long timeout, TimeUnit unit) {
    try { ensureSignaled(timeout, unit); return true; }
    catch (InterruptedException e) { return _signal; }
    catch (TimeoutException e) { return _signal; }
  }
  
}
