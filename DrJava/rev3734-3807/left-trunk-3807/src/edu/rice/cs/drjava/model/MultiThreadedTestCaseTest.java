

package edu.rice.cs.drjava.model;

import edu.rice.cs.drjava.DrJavaTestCase;
import junit.framework.*;


public class MultiThreadedTestCaseTest extends DrJavaTestCase {
  public MultiThreadedTestCaseTest() { super(); }
  public MultiThreadedTestCaseTest(String name) { super(name); }
  
  public static class Uncaught extends MultiThreadedTestCase {
    public Uncaught() { super(); }
    public Uncaught(String name) { super(name); }
    public void testUncaught() {
      Thread t = new Thread(new Runnable() {
        public void run() {
          throw new RuntimeException();
        }
      });
      t.start();
      join(t);
    }
  }
  
  
  
  public void testUncaught() {
    TestResult tr = new Uncaught().run();
    assertEquals(1, tr.runCount());
    assertEquals(1, tr.failureCount());
  }    
  
  public static void main(String[] args) {
    (new MultiThreadedTestCaseTest()).testUncaught();
  }
}
