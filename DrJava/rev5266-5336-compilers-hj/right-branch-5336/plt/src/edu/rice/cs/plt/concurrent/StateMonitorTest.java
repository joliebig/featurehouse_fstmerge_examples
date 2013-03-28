

package edu.rice.cs.plt.concurrent;

import junit.framework.TestCase;

public class StateMonitorTest extends TestCase {
  
  volatile boolean _flag; 
  
  public void test() throws InterruptedException {
    final StateMonitor<Integer> m = new StateMonitor<Integer>(0);
    assertEquals((Integer) 0, m.value());
    
    DelayedInterrupter interrupter1 = new DelayedInterrupter(50);
    m.ensureState(0);
    interrupter1.abort();
    
    DelayedInterrupter interrupter2 = new DelayedInterrupter(500); 
    _flag = false;
    new Thread() { public void run() { ConcurrentUtil.sleep(100); _flag = true; m.set(1); } }.start();
    m.ensureState(1);
    assertTrue(_flag);
    interrupter2.abort();
    assertEquals((Integer) 1, m.value());
    
    DelayedInterrupter interrupter3 = new DelayedInterrupter(10);
    m.ensureState(1); 
    interrupter3.abort();
  
    @SuppressWarnings("unused") DelayedInterrupter interrupter4 = new DelayedInterrupter(50);
    try { m.ensureState(2); fail("Monitor should block until interrupted"); }
    catch (InterruptedException e) {  }
  }
      
}
