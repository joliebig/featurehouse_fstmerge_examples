

package edu.rice.cs.util.newjvm;



import java.rmi.*;



public abstract class AbstractSlaveJVM implements SlaveRemote {
  public static final int CHECK_MAIN_VM_ALIVE_MINUTES = 1;

  
  protected String _quitSlaveThreadName = "Quit SlaveJVM Thread";

  
  protected String _pollMasterThreadName = "Poll MasterJVM Thread";

  private Object _slaveJVMLock = new Object();
  private boolean _slaveExited = false;
  
  
  public final void quit() {
    beforeQuit();
    
    _slaveExited = false;


    
    Thread t = new Thread(_quitSlaveThreadName) {
      public void run() {
        try {
          
          synchronized(_slaveJVMLock) { while (! _slaveExited) _slaveJVMLock.wait(); }







          System.exit(0);
        }
        catch (Throwable th) { 

          quitFailed(th); 
        }
      }
    };

    t.start();
    synchronized(_slaveJVMLock) { 
      _slaveExited = true; 
      _slaveJVMLock.notify();
    } 
  }

  
  protected void beforeQuit() { }

  
  protected void quitFailed(Throwable th) { }

  
  public final void start(final MasterRemote master) throws RemoteException {
    Thread thread = new Thread(_pollMasterThreadName) {
      public void run() {

        while (true) {
          try { Thread.sleep(CHECK_MAIN_VM_ALIVE_MINUTES*60*1000); }
          catch (InterruptedException ie) { }

          try { master.checkStillAlive(); }
          catch (RemoteException re) {
            quit(); 
          }
        }
      }
    };

    thread.setDaemon(true);
    thread.start();

    handleStart(master);
  }

  
  protected abstract void handleStart(MasterRemote master);
}
