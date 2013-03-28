

package edu.rice.cs.plt.concurrent;

import java.util.LinkedList;
import java.util.List;

import edu.rice.cs.plt.lambda.Condition;

import junit.framework.TestCase;

public class ConditionMonitorTest extends TestCase {
  
  public void test() throws InterruptedException {
    final List<String> l = new LinkedList<String>();
    final ConditionMonitor m = new ConditionMonitor(new Condition() {
      public boolean isTrue() { return l.isEmpty(); }
    });
    assertTrue(m.isTrue());
    
    DelayedInterrupter interrupter1 = new DelayedInterrupter(50);
    m.ensureTrue();
    assertEquals(0, l.size());
    interrupter1.abort();
    
    l.add("x");
    assertEquals(1, l.size());
    assertFalse(m.isTrue());
    DelayedInterrupter interrupter2 = new DelayedInterrupter(300);
    new Thread() { public void run() { ConcurrentUtil.sleep(100); l.remove("x"); m.check(); } }.start();
    m.ensureTrue();
    assertTrue(m.isTrue());
    assertEquals(0, l.size());
    interrupter2.abort();

    l.add("y");
    @SuppressWarnings("unused") DelayedInterrupter interrupter4 = new DelayedInterrupter(50);
    try { m.ensureTrue(); fail("Monitor should block until interrupted"); }
    catch (InterruptedException e) {  }
  }
      
}
