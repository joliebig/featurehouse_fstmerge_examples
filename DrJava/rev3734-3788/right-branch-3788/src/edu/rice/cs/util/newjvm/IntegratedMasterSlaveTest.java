

package edu.rice.cs.util.newjvm;

import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.drjava.config.FileOption;

import edu.rice.cs.util.swing.Utilities;

import java.rmi.RemoteException;


public class IntegratedMasterSlaveTest extends DrJavaTestCase {
  
  private MasterImpl _testMaster = new MasterImpl();
 
  public void testItAll() throws Exception {
    
    
    
    for (int i = 0; i < 2; i++) _testMaster.runTestSequence();
  }

  public void testImmediateQuit() throws Exception {
    for (int i = 0; i < 5; i++)  _testMaster.runImmediateQuitTest();
  }

  private class MasterImpl extends AbstractMasterJVM implements MasterI {
    
    
    private Object _testLock = new Object();

    private char _letter;
    private boolean _justQuit;
    private boolean _connected; 
    
    private String _currentTest = "";

    public MasterImpl() { super(IntegratedMasterSlaveTest.class.getName() + "$CounterSlave"); }

    
    public void runImmediateQuitTest() throws Exception {
      

      
      _currentTest = "runImmediateQuitTest";
      synchronized (_testLock) { 
        _justQuit = false; 
        _connected = false;
        _letter = 'a';  
      }

      invokeSlave(new String[]{"-Djava.system.class.loader=edu.rice.cs.util.newjvm.CustomSystemClassLoader"}, FileOption.NULL_FILE);


      
      
      quitSlave();
                     


      
      synchronized(_testLock) { 
        while (! _justQuit) _testLock.wait(); 
        _currentTest = "";  
      }
                     

      
      
    }

    public void runTestSequence() throws Exception {
      
      synchronized (_testLock) {
        _currentTest = "runTestSequence";
        _justQuit = false;
        _connected = false;
        _letter = 'a';
      }
      
      invokeSlave(new String[] {"-Djava.system.class.loader=edu.rice.cs.util.newjvm.CustomSystemClassLoader"}, FileOption.NULL_FILE);           

      synchronized (_testLock) { while (! _connected) _testLock.wait();  }

      ((SlaveI)getSlave()).startLetterTest();

      
      synchronized(_testLock) { while (_letter != 'f') { _testLock.wait(); } }

      for (int i = 0; i < 7; i++) {
        int value = ((SlaveI) getSlave()).getNumber();
        assertEquals("value returned by slave", i, value);
      }

      quitSlave();
      synchronized(_testLock) { while (! _justQuit) _testLock.wait(); } 
      _currentTest = "";
    }

    public char getLetter() {
      synchronized(_testLock) {
        char ret = _letter;
        _letter++;
        _testLock.notify();
        return ret;
      }
    }

    protected synchronized void handleSlaveConnected() {
      SlaveI slave = (SlaveI) getSlave();
      assertTrue("slave is set", slave != null);
      assertTrue("startup not in progress", ! isStartupInProgress());
      
      assertEquals("letter value", 'a', _letter);
      synchronized(_testLock) { 
        _connected = true;
        _testLock.notify(); 
      }
    }

    protected void handleSlaveQuit(int status) {
      assertEquals("slave result code", 0, status);
      if (_currentTest.equals("runTestSequence")) {
        
        assertEquals("last letter returned", 'f', _letter);
      }
      assertTrue("slave is not set", getSlave() == null);
      assertTrue("startup not in progress", ! isStartupInProgress());

      
      synchronized(_testLock) {
        _testLock.notify();
        _justQuit = true;
      }
    }

    
    public void errorStartingSlave(Throwable cause) throws RemoteException {
      fail("There was an error starting the slave JVM: " + cause);
    }
  }

  
  public static class CounterSlave extends AbstractSlaveJVM implements SlaveI {
    private int _counter = 0;
    private MasterI _master = null;

    public int getNumber() { return _counter++; }

    protected void handleStart(MasterRemote m) { _master = (MasterI) m; }

    public void startLetterTest() throws RemoteException {
      
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
          catch (RemoteException re) {
            javax.swing.JOptionPane.showMessageDialog(null, re.toString());
            System.exit(3);
          }
          catch (ClassCastException cce) { System.exit(1); }
        }
      };
      thread.start();
    }
  }

  public interface SlaveI extends SlaveRemote {
    public int getNumber() throws RemoteException;
    public void startLetterTest() throws RemoteException;
  }

  public interface MasterI extends MasterRemote {
    public char getLetter() throws RemoteException;
  }
}
