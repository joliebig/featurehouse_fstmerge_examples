

package edu.rice.cs.util.newjvm;

import edu.rice.cs.plt.concurrent.ConcurrentUtil;

import java.rmi.*;

import static edu.rice.cs.plt.debug.DebugUtil.error;
import static edu.rice.cs.plt.debug.DebugUtil.debug;


public abstract class AbstractSlaveJVM implements SlaveRemote {
  public static final int CHECK_MAIN_VM_ALIVE_SECONDS = 1;
  
  
  private final String _quitSlaveThreadName;
  
  private final String _pollMasterThreadName;
  private boolean _started;
  
  public AbstractSlaveJVM() {
    this("Quit SlaveJVM Thread", "Poll MasterJVM Thread");
  }
  
  public AbstractSlaveJVM(String quitSlaveThreadName, String pollMasterThreadName) {
    _quitSlaveThreadName = quitSlaveThreadName;
    _pollMasterThreadName = pollMasterThreadName;
    _started = false;
  }
  
  
  public final synchronized void quit() {
    beforeQuit();
    
    new Thread(_quitSlaveThreadName) {
      public void run() {
        
        synchronized (AbstractSlaveJVM.this) {
          try { System.exit(0); }
          catch (RuntimeException e) { error.log("Can't invoke System.exit", e); }
        }
      }
    }.start();
  }
  
  
  public final synchronized void start(final MasterRemote master) throws RemoteException {
    if (_started) { throw new IllegalArgumentException("start() has already been invoked"); }
    master.checkStillAlive(); 

    Thread checkMaster = new Thread(_pollMasterThreadName) {
      public void run() {
        while (true) {
          ConcurrentUtil.sleep(CHECK_MAIN_VM_ALIVE_SECONDS*1000);
          try { master.checkStillAlive(); }
          catch (RemoteException e) {
            
            
            
            quit();
          }
        }
      }
    };
    checkMaster.setDaemon(true);
    checkMaster.start();
    handleStart(master);
  }
  
  
  protected void beforeQuit() { }
  
  
  protected abstract void handleStart(MasterRemote master);
  
}
