

package edu.rice.cs.plt.concurrent;

import java.io.IOException;
import java.io.Serializable;
import java.io.NotSerializableException;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.lang.reflect.InvocationTargetException;
import junit.framework.TestCase;
import edu.rice.cs.plt.debug.Stopwatch;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.lambda.Thunk;
import edu.rice.cs.plt.lambda.WrappedException;

import static edu.rice.cs.plt.concurrent.ConcurrentUtil.*;
import static edu.rice.cs.plt.debug.DebugUtil.debug;

public class ConcurrentUtilTest extends TestCase {
  
  private static volatile Object _obj; 
  
  private void assertInRange(long lower, long higher, long actual) {
    assertTrue("value is too small (expected: >= " + lower + "; actual: " + actual + ")", lower <= actual);
    assertTrue("value is too large (expected: <= " + higher + "; actual: " + actual + ")", higher >= actual);
  }
  
  public void testSleep() {
    debug.logStart();

    Stopwatch w = new Stopwatch(true);
    sleep(500); 
    long time = w.stop();
    assertInRange(300, 700, time);
    
    new DelayedInterrupter(500);
    w.start();
    sleep(4000);
    time = w.stop();
    assertFalse("Interrupt was not detected", Thread.interrupted());
    assertInRange(300, 700, time);

    debug.logEnd();
  }

  public void testWork() {
    debug.logStart();
    
    Stopwatch w = new Stopwatch(true);
    work(500);
    long time = w.stop();
    assertInRange(300, 700, time);
    
    new DelayedInterrupter(500);
    w.start();
    work(4000);
    time = w.stop();
    assertFalse("Interrupt was not detected", Thread.interrupted());
    assertInRange(300, 700, time);
    
    debug.logEnd();
  }
  
  
  public void testRunInThread() {
    debug.logStart();
    
    _obj = null;
    TaskController<Void> c = runInThread(new Runnable() {
      
      public void run() { sleep(20); _obj = Thread.currentThread(); }
    });
    c.value();
    assertNotNull(_obj);
    assertNotSame(Thread.currentThread(), _obj);
    
    debug.logEnd();
  }

  
  public void testComputeInThread() {
    debug.logStart();
    
    _obj = null;
    final CompletionMonitor started = new CompletionMonitor();
    final CompletionMonitor finish = new CompletionMonitor();
    Thunk<Thread> task = new Thunk<Thread>() {
      public Thread value() {
        started.signal();
        finish.attemptEnsureSignaled();
        _obj = Thread.currentThread();
        return Thread.currentThread();
      }
    };
    TaskController<Thread> c = computeInThread(task, false);
    assertSame(TaskController.Status.PAUSED, c.status());
    c.start();
    started.attemptEnsureSignaled();
    assertSame(TaskController.Status.RUNNING, c.status());
    finish.signal();
    Thread t = c.value();
    assertSame(TaskController.Status.FINISHED, c.status());
    assertNotNull(_obj);
    assertNotSame(Thread.currentThread(), _obj);
    assertSame(t, _obj);
    
    debug.logEnd();
  }
  
  public void testComputeInThreadWithException() {
    debug.logStart();
    
    TaskController<String> c = computeInThread(new Thunk<String>() {
      public String value() { throw new RuntimeException("exception in task"); }
    });
    try { c.value(); fail("expected exception"); }
    catch (WrappedException e) {
      assertTrue(e.getCause() instanceof ExecutionException);
      assertTrue(e.getCause().getCause() instanceof RuntimeException);
      assertEquals("exception in task", e.getCause().getCause().getMessage());
    }
    
    debug.logEnd();
  }
  
  public void testComputeIncrementalInThread() {
    debug.logStart();
    
    TrivialIncrementalTask<Integer, String> task =
      new TrivialIncrementalTask<Integer, String>(IterUtil.make(1, 2, 3, 4, 5), "done");
    IncrementalTaskController<Integer, String> c = computeInThread(task);
    
    task.signal();
    assertTrue(IterUtil.isEqual(IterUtil.make(1), c.intermediateQueue()));
    assertSame(TaskController.Status.RUNNING, c.status());
    task.signal();
    assertTrue(IterUtil.isEqual(IterUtil.make(1, 2), c.intermediateQueue()));
    assertSame(TaskController.Status.RUNNING, c.status());
    c.pause();
    sleep(50); 
    task.ignoreMonitor();
    assertSame(TaskController.Status.PAUSED, c.status());
    assertTrue(IterUtil.isEqual(IterUtil.make(1, 2, 3), c.intermediateQueue()));
    c.start();
    assertEquals("done", c.value());
    assertSame(TaskController.Status.FINISHED, c.status());
    assertTrue(IterUtil.isEqual(IterUtil.make(1, 2, 3, 4, 5), c.intermediateQueue()));
    
    debug.logEnd();
  }
  
  private static class TrivialIncrementalTask<I, R> implements IncrementalTask<I, R> {
    private final Iterator<? extends I> _intermediates;
    private final R _finalResult;
    private final CompletionMonitor _monitor;
    private volatile boolean _useMonitor;
    public TrivialIncrementalTask(Iterable<? extends I> intermediates, R finalResult) {
      _intermediates = intermediates.iterator();
      _finalResult = finalResult;
      _monitor = new CompletionMonitor();
      _useMonitor = true;
    }
    
    
    public void ignoreMonitor() { _useMonitor = false; _monitor.signal(); sleep(50); }
    public void useMonitor() { _monitor.reset(); _useMonitor = true; sleep(50); }
    public void signal() { _monitor.signal(); sleep(50); }
    
    public boolean isResolved() { return !_intermediates.hasNext(); }
    public I step() {
      if (_useMonitor) { _monitor.attemptEnsureSignaled(); _monitor.reset(); }
      return _intermediates.next();
    }
    public R value() {
      if (_useMonitor) { _monitor.attemptEnsureSignaled(); _monitor.reset(); }
      return _finalResult;
    }
  }
  
  
  public void testComputeInProcess() {
    debug.logStart();
    
    _obj = null;
    TaskController<String> c1 = computeInProcess(new ProcessTask1());
    assertEquals("done", c1.value());
    assertNull(_obj);
    
    _obj = null;
    TaskController<String> c2 = computeInProcess(new ProcessTask2());
    try { c2.value(); fail("expected exception"); }
    catch (WrappedException e) {
      assertTrue(e.getCause() instanceof WrappedException); 
      assertTrue(e.getCause().getCause() instanceof NotSerializableException);
    }
    assertNull(_obj);
    
    _obj = null;
    TaskController<String> c3 = computeInProcess(new ProcessTask3());
    try { c3.value(); fail("expected exception"); }
    catch (WrappedException e) {
      assertTrue(e.getCause() instanceof ExecutionException);
      assertTrue(e.getCause().getCause() instanceof RuntimeException);
      assertEquals("done", e.getCause().getCause().getMessage());
    }
    assertNull(_obj);
    
    debug.logEnd();
  }
  
  private static final class ProcessTask1 implements Thunk<String>, Serializable {
    public String value() {
      _obj = "should not be visible in parent process";
      return "done";
    }
  }
  
  private static final class ProcessTask2 implements Thunk<String>  {
    public String value() {
      _obj = "should not be visible in parent process";
      return "done";
    }
  }
  
  private static final class ProcessTask3 implements Thunk<String>, Serializable {
    public String value() {
      _obj = "should not be visible in parent process";
      throw new RuntimeException("done");
    }
  }
  
  public void testComputeIncrementalInProcess() {
    debug.logStart();
    
    _obj = null;
    IncrementalTaskController<Integer, String> c = computeInProcess(new IncrementalProcessTask1());
    assertEquals("done", c.value());
    assertTrue(IterUtil.isEqual(IterUtil.make(1, 2, 3, 4, 5), c.intermediateQueue()));
    assertEquals(5, c.steps());
    assertNull(_obj);
    
    debug.logEnd();
  }
    
    
  private static final class IncrementalProcessTask1 implements IncrementalTask<Integer, String>, Serializable {
    private int _steps = 0;
    public boolean isResolved() { return _steps >= 5; }
    public Integer step() { return ++_steps; }
    public String value() {
      _obj = "should not be visible in parent process";
      return "done";
    }
  }
  
  public void testExportInProcess() throws InterruptedException, IOException, ExecutionException {
    debug.logStart();
    
    RemoteCounter c = (RemoteCounter) exportInProcess(new CounterFactory());
    debug.logValue("exportInProcess result", c);
    assertEquals(0, c.current());
    assertEquals(0, Counter.TOTAL);
    c.increment();
    c.increment();
    assertEquals(2, c.current());
    assertEquals(0, Counter.TOTAL); 
    
    Counter c2 = new Counter();
    c2.increment();
    assertEquals(2, c.current());
    assertEquals(1, c2.current());
    assertEquals(1, Counter.TOTAL); 
    
    c.exit();
    
    debug.logEnd();
  }
  
  private interface RemoteCounter extends Remote {
    public void increment() throws RemoteException;
    public int current() throws RemoteException;
    public void exit() throws RemoteException;
  }
  
  private static class Counter implements RemoteCounter {
    public static int TOTAL = 0;
    private int _x = 0;
    public void increment() { _x++; TOTAL++; }
    public int current() { return _x; }
    public void exit() throws NoSuchObjectException {
      debug.log();
      UnicastRemoteObject.unexportObject(this, true);
    }
  }
  
  private static class CounterFactory implements Thunk<Counter>, Serializable {
    public Counter value() { debug.log(); return new Counter(); }
  }
  
  
}
