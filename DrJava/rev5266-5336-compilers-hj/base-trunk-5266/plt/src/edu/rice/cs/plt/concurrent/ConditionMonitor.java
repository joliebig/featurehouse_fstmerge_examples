

package edu.rice.cs.plt.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import edu.rice.cs.plt.lambda.Condition;

 
public class ConditionMonitor implements Condition {
  private final Condition _condition;
  
  
  public ConditionMonitor(Condition condition) { _condition = condition; }
  
  
  public boolean isTrue() { return _condition.isTrue(); }
  
  
  synchronized public void check() {
    
    
    if (_condition.isTrue()) { this.notifyAll(); }
  }
  
  
  synchronized public void ensureTrue() throws InterruptedException {
    while (!_condition.isTrue()) { this.wait(); }
  }
  
  
  public void ensureTrue(long timeout) throws InterruptedException, TimeoutException {
    ensureTrue(timeout, TimeUnit.MILLISECONDS);
  }
  
  
  public synchronized void ensureTrue(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
    if (!_condition.isTrue()) {
      long timeoutTime = ConcurrentUtil.futureTimeNanos(timeout, unit);
      do { ConcurrentUtil.waitUntilNanos(this, timeoutTime); } while (!_condition.isTrue());
    }
  }
  
  
  public boolean attemptEnsureTrue() {
    try { ensureTrue(); return true; }
    catch (InterruptedException e) { return _condition.isTrue(); }
  }
  
  
  public boolean attemptEnsureTrue(long timeout) {
    try { ensureTrue(timeout, TimeUnit.MILLISECONDS); return true; }
    catch (InterruptedException e) { return _condition.isTrue(); }
    catch (TimeoutException e) { return _condition.isTrue(); }
  }
  
  
  public boolean attemptEnsureTrue(long timeout, TimeUnit unit) {
    try { ensureTrue(timeout, unit); return true; }
    catch (InterruptedException e) { return _condition.isTrue(); }
    catch (TimeoutException e) { return _condition.isTrue(); }
  }
  
}
