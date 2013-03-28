

package edu.rice.cs.util.newjvm;

import edu.rice.cs.drjava.DrJavaTestCase;

import edu.rice.cs.plt.concurrent.CompletionMonitor;
import edu.rice.cs.plt.concurrent.JVMBuilder;

import java.rmi.RemoteException;


public class IntegratedMasterSlaveTest extends DrJavaTestCase {
  
  volatile TestMasterJVM _testMaster;
  
  public void setUp() throws Exception {
    super.setUp();
    _testMaster = new TestMasterJVM();
  }
  
  public void tearDown() throws Exception {
    _testMaster.dispose();
    super.tearDown();
  }
  
  public void testItAll() throws Exception {
    
    
    
    for (int i = 0; i < 2; i++) _testMaster.runTestSequence();
  }
  
  public void testImmediateQuit() throws Exception {
    for (int i = 0; i < 5; i++)  _testMaster.runImmediateQuitTest();
  }
  
  
  private static class TestMasterJVM extends AbstractMasterJVM implements TestMasterRemote {
    
    private static final int WAIT_TIMEOUT = 10000; 
    
    
    private final CompletionMonitor _justQuit = new CompletionMonitor(); 
    
    
    private volatile TestSlaveRemote _slave;                    
    private final Object _slaveLock = new Object();
    
    
    private volatile char _letter;
    private final Object _letterLock = new Object();
    
    private volatile String _currentTest = "";
    
    public TestMasterJVM() { super(CounterSlave.class.getName()); }
    
    
    public void runImmediateQuitTest() throws Exception {
      _currentTest = "runImmediateQuitTest";
      _justQuit.reset(); 
      _slave = null;
      _letter = 'a';  
      
      new Thread() {
        public void run() { invokeSlave(JVMBuilder.DEFAULT); }
      }.start();
      
      
      quitSlave();
      assertTrue(_justQuit.attemptEnsureSignaled(WAIT_TIMEOUT));
      _currentTest = "";  
      
    }
    
    public void runTestSequence() throws Exception {
      _currentTest = "runTestSequence";
      _justQuit.reset();
      _slave = null;
      _letter = 'a';
      
      invokeSlave(JVMBuilder.DEFAULT);           
      _slave.startLetterTest();
      
      synchronized(_letterLock) { while (_letter != 'f') { _letterLock.wait(); } }
      for (int i = 0; i < 7; i++) {
        int value = _slave.getNumber();
        assertEquals("value returned by slave", i, value);
      }
      
      quitSlave();
      assertTrue(_justQuit.attemptEnsureSignaled(WAIT_TIMEOUT));
      _currentTest = "";
    }
    
    public char getLetter() {
      synchronized(_letterLock) {
        char ret = _letter;
        _letter++;
        _letterLock.notify();
        return ret;
      }
    }
    
    @Override protected void handleSlaveConnected(SlaveRemote slave) {
      
      assertEquals("letter value", 'a', _letter);
      synchronized(_slaveLock) {
        _slave = (TestSlaveRemote) slave;
        _slaveLock.notify();
      }
    }
    
    @Override protected void handleSlaveQuit(int status) {
      assertEquals("slave result code", 0, status);
      if (_currentTest.equals("runTestSequence")) {
        
        assertEquals("last letter returned", 'f', _letter);
      }
      
      _justQuit.signal();
    }
    
    @Override protected void handleSlaveWontStart(Exception e) {
      fail("There was an error starting the slave JVM: " + e);
    }
  }
  
  
  public static class CounterSlave extends AbstractSlaveJVM implements TestSlaveRemote {
    
    public static final CounterSlave ONLY = new CounterSlave();
    
    private volatile int _counter = 0;
    private volatile TestMasterRemote _master = null;
    
    private CounterSlave() { }
    
    public synchronized int getNumber() { return _counter++; }
    
    protected void handleStart(MasterRemote m) { _master = (TestMasterRemote) m; }
    
    public void startLetterTest() {
      
      Thread thread = new Thread() {
        public void run() {
          try {
            for (char c = 'a'; c <= 'e'; c++) {
              char got = _master.getLetter();
              if (c != got) System.exit(2);
            }
            
            
            Thread.sleep(15000);
            System.exit(4);
          }
          catch (InterruptedException e) { System.exit(5); }
          catch (RemoteException re) { System.exit(3); }
          catch (ClassCastException cce) { System.exit(1); }
        }
      };
      thread.start();
    }
  }
  
  public interface TestSlaveRemote extends SlaveRemote {
    public int getNumber() throws RemoteException;
    public void startLetterTest() throws RemoteException;
  }
  
  public interface TestMasterRemote extends MasterRemote {
    public char getLetter() throws RemoteException;
  }
}
