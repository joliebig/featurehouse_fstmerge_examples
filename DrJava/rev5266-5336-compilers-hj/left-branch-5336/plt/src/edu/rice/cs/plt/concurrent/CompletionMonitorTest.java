

package edu.rice.cs.plt.concurrent;

import junit.framework.TestCase;

public class CompletionMonitorTest extends TestCase {
  
  volatile boolean _flag; 
  
  public void testDegenerateSignal() throws InterruptedException {
    CompletionMonitor as = new CompletionMonitor(true);
    assertTrue("Flag should start out as true", as.isSignaled());
    
    DelayedInterrupter interrupter = new DelayedInterrupter(50);
    as.ensureSignaled();
    interrupter.abort();
  }
  
  public void testRealSignal() throws InterruptedException {
    final CompletionMonitor as = new CompletionMonitor(false);
    
    DelayedInterrupter interrupter1 = new DelayedInterrupter(500); 
    _flag = false;
    assertFalse(as.isSignaled());
    new Thread() { public void run() { ConcurrentUtil.sleep(100); _flag = true; as.signal(); } }.start();
    as.ensureSignaled();
    assertTrue(_flag);
    interrupter1.abort();
    assertTrue(as.isSignaled());
    
    DelayedInterrupter interrupter2 = new DelayedInterrupter(10);
    assertTrue(as.isSignaled());
    as.ensureSignaled(); 
    interrupter2.abort();
    
    as.reset();
    assertFalse(as.isSignaled());
    @SuppressWarnings("unused") DelayedInterrupter interrupter3 = new DelayedInterrupter(50);
    try { as.ensureSignaled(); fail("Monitor should not be signalled"); }
    catch (InterruptedException e) {  }
  }
  
    
}
