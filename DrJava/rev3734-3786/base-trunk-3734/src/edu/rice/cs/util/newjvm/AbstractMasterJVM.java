

package edu.rice.cs.util.newjvm;

import edu.rice.cs.drjava.config.FileOption;

import java.rmi.*;
import java.rmi.server.*;
import java.io.*;


public abstract class AbstractMasterJVM
  implements MasterRemote {
  
  
  protected String _waitForQuitThreadName = "Wait for SlaveJVM Exit Thread";
  
  
  protected String _exportMasterThreadName = "Export MasterJVM Thread";
  
  
  protected Object _masterJVMLock = new Object();
  
  private static final String RUNNER = SlaveJVMRunner.class.getName();
  
  
  private volatile SlaveRemote _slave = null;

  
  private volatile boolean _startupInProgress = false;

  
  private boolean _quitOnStartup = false;
  
  
  final static Object _exportLock = new Object();

  
  private Remote _stub;
  
  
  private File _stubFile;

  
  private Remote _classLoaderStub;

  
  File _classLoaderStubFile;
  
  
  private final String _slaveClassName;

  
  protected AbstractMasterJVM(String slaveClassName) {
    _slaveClassName = slaveClassName;
    
    
    System.setProperty("java.rmi.server.hostname", "127.0.0.1");
  }

  
  protected abstract void handleSlaveConnected();
  
  
  protected abstract void handleSlaveQuit(int status);
  
  
  protected final void invokeSlave() throws IOException, RemoteException {
    invokeSlave(new String[0], FileOption.NULL_FILE);
  }
  
  
  protected final void invokeSlave(String[] jvmArgs, File workDir) throws IOException, RemoteException {
    invokeSlave(jvmArgs, System.getProperty("java.class.path"), workDir);
  }
 
  
  protected final void invokeSlave(String[] jvmArgs, String cp, File workDir) throws IOException, RemoteException {
    
    synchronized(_masterJVMLock) {
      if (_startupInProgress) throw new IllegalStateException("startup is in progress in invokeSlave");
      
      if (_slave != null) throw new IllegalStateException("slave nonnull in invoke: " + _slave);
      _startupInProgress = true;
      
      
      
      Thread t = new Thread(_exportMasterThreadName) {
        public void run() {
          synchronized(_exportLock) {
            try {
              _stub = UnicastRemoteObject.exportObject(AbstractMasterJVM.this);
              
              
              
            }
            catch (RemoteException re) {
              
              throw new edu.rice.cs.util.UnexpectedException(re);
            }
            _exportLock.notify();
          }
        }
      };
      synchronized(_exportLock) {
        t.start();
        try { while (_stub == null) { _exportLock.wait(); } }
        catch (InterruptedException ie) { throw new edu.rice.cs.util.UnexpectedException(ie); }
      }
      _stubFile = File.createTempFile("DrJava-remote-stub", ".tmp");
      _stubFile.deleteOnExit();
      
      FileOutputStream fstream = new FileOutputStream(_stubFile);
      ObjectOutputStream ostream = new ObjectOutputStream(fstream);
      ostream.writeObject(_stub);
      ostream.flush();
      fstream.close();
      
      
      
      final RemoteClassLoader rClassLoader = new RemoteClassLoader(getClass().getClassLoader());
      t = new Thread(_exportMasterThreadName) {
        public void run() {
          synchronized(_exportLock) {
            try {
              _classLoaderStub = UnicastRemoteObject.exportObject(rClassLoader);
              
              
              
            }
            catch (RemoteException re) {
              
              throw new edu.rice.cs.util.UnexpectedException(re);
            }
            _exportLock.notify();
          }
        }
      };
      synchronized(_exportLock) {
        t.start();
        try { while (_classLoaderStub == null) { _exportLock.wait(); } }
        catch (InterruptedException ie) { throw new edu.rice.cs.util.UnexpectedException(ie); }
      }
      _classLoaderStubFile = File.createTempFile("DrJava-remote-stub", ".tmp");
      _classLoaderStubFile.deleteOnExit();
      
      fstream = new FileOutputStream(_classLoaderStubFile);
      ostream = new ObjectOutputStream(fstream);
      ostream.writeObject(_classLoaderStub);
      ostream.flush();
      fstream.close();
      
      String[] args = 
        new String[] { _stubFile.getAbsolutePath(), _slaveClassName, _classLoaderStubFile.getAbsolutePath() };
      
            
      final Process process = ExecJVM.runJVM(RUNNER, args, cp, jvmArgs, workDir);
      
      
      Thread thread = new Thread(_waitForQuitThreadName) {
        public void run() {
          try {
            int status = process.waitFor();
            
            synchronized(AbstractMasterJVM.this) {
              if (_startupInProgress) {
                
                
                
                
                
                
                
                
                slaveQuitDuringStartup(status);
              }
              _slave = null;
              UnicastRemoteObject.unexportObject(AbstractMasterJVM.this, true);

              handleSlaveQuit(status);
            }
          }
          catch (NoSuchObjectException e) {
            throw new edu.rice.cs.util.UnexpectedException(e);
          }
          catch (InterruptedException ie) {
            throw new edu.rice.cs.util.UnexpectedException(ie);
          }
        }
      };
      thread.start();
    }
  }
  
  
  protected void slaveQuitDuringStartup(int status) {
    String msg = "SlaveJVM quit before registering!  Status: " + status;
    throw new IllegalStateException(msg);
  }
  
  
  public abstract void errorStartingSlave(Throwable cause) throws RemoteException;
  
  
  public void checkStillAlive() { }

  public void registerSlave(SlaveRemote slave) throws RemoteException {
    synchronized(_masterJVMLock) {
      _slave = slave;
      _startupInProgress = false;
      _stubFile.delete();
      _stub = null;
      _classLoaderStub = null;
      _classLoaderStubFile.delete();
      
      handleSlaveConnected();
      
      if (_quitOnStartup) {
        
        _quitOnStartup = false;
        quitSlave();
      }
    }
  }
  
  protected final void quitSlave() throws RemoteException {

    synchronized(_masterJVMLock) {
      if (isStartupInProgress())
        
        
        
        _quitOnStartup = true;
      
      else if (_slave == null)  System.out.println("slave JVM quit invoked when no slave running");

      else _slave.quit();
    }
  }
  
  
  protected final SlaveRemote getSlave() {  return _slave; }
  
  
  protected boolean isStartupInProgress() { return _startupInProgress; }
}
