

package edu.rice.cs.util.newjvm;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.io.Serializable;
import java.util.Map;

import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.plt.collect.CollectUtil;
import edu.rice.cs.plt.concurrent.ConcurrentUtil;
import edu.rice.cs.plt.concurrent.JVMBuilder;
import edu.rice.cs.plt.concurrent.StateMonitor;
import edu.rice.cs.plt.lambda.LazyThunk;
import edu.rice.cs.plt.lambda.Runnable1;
import edu.rice.cs.plt.lambda.Thunk;
import edu.rice.cs.plt.lambda.WrappedException;
import edu.rice.cs.plt.reflect.ReflectException;
import edu.rice.cs.plt.reflect.ReflectUtil;

import static edu.rice.cs.plt.debug.DebugUtil.debug;
import static edu.rice.cs.plt.debug.DebugUtil.error;


public abstract class AbstractMasterJVM implements MasterRemote {
  
  
  private enum State { FRESH, STARTING, RUNNING, QUITTING, DISPOSED };
  
  
  private static class SlaveFactory implements Thunk<AbstractSlaveJVM>, Serializable {
    private final String _className;
    public SlaveFactory(String className) { _className = className; }
    public AbstractSlaveJVM value() {
      try { return (AbstractSlaveJVM) ReflectUtil.getStaticField(_className, "ONLY"); }
      catch (ReflectException e) {
        try { return (AbstractSlaveJVM) ReflectUtil.loadObject(_className); }
        catch (ReflectException e2) { throw new WrappedException(e2); }
     }
    }
  }
  
  private final StateMonitor<State> _monitor;
  private final SlaveFactory _slaveFactory;
  private final LazyThunk<MasterRemote> _masterStub;
  
  private volatile SlaveRemote _slave;
  
  
  protected AbstractMasterJVM(String slaveClassName) {
    _monitor = new StateMonitor<State>(State.FRESH);
    _slaveFactory = new SlaveFactory(slaveClassName);
    _masterStub = new LazyThunk<MasterRemote>(new Thunk<MasterRemote>() {
      public MasterRemote value() {
        try { return (MasterRemote) UnicastRemoteObject.exportObject(AbstractMasterJVM.this, 0); }
        catch (RemoteException re) {
          error.log(re);
          throw new UnexpectedException(re);
        }
      }
    });
    _slave = null;
    
    System.setProperty("java.rmi.server.hostname", "127.0.0.1");
  }
  
  
  protected abstract void handleSlaveConnected(SlaveRemote newSlave);
  
  
  protected abstract void handleSlaveQuit(int status);
  
  
  protected abstract void handleSlaveWontStart(Exception e);
  
  
  protected final void invokeSlave(JVMBuilder jvmBuilder) {
    transition(State.FRESH, State.STARTING);

    
    Map<String, String> props = ConcurrentUtil.getPropertiesAsMap("plt.", "drjava.", "edu.rice.cs.");
    props.put("java.rmi.server.hostname", "127.0.0.1"); 
    if (!props.containsKey("plt.log.working.dir") && 
        (props.containsKey("plt.debug.log") || props.containsKey("plt.error.log") || 
            props.containsKey("plt.log.factory"))) {
      props.put("plt.log.working.dir", System.getProperty("user.dir", ""));
    }
    
    final JVMBuilder tweakedJVMBuilder = jvmBuilder.properties(CollectUtil.union(props, jvmBuilder.properties()));

    SlaveRemote newSlave = null;
    try {
      debug.logStart("invoking remote JVM process");
      newSlave =
        (SlaveRemote) ConcurrentUtil.exportInProcess(_slaveFactory, tweakedJVMBuilder, new Runnable1<Process>() {
          public void run(Process p) {
            debug.log("Remote JVM quit");
            _monitor.set(State.FRESH);
            
            debug.logStart("handleSlaveQuit");
            handleSlaveQuit(p.exitValue());
            debug.logEnd("handleSlaveQuit");
          }
        });
      debug.logEnd("invoking remote JVM process");
    }
    catch (Exception e) {
      debug.log(e);
      debug.logEnd("invoking remote JVM process (failed)");
      _monitor.set(State.FRESH);
      
      handleSlaveWontStart(e);
    }

    if (newSlave != null) {
      try { newSlave.start(_masterStub.value()); }
      catch (RemoteException e) {
        debug.log(e);
        attemptQuit(newSlave);
        _monitor.set(State.FRESH);
        
        handleSlaveWontStart(e);
        return;
      }
      
      handleSlaveConnected(newSlave);
      _slave = newSlave;
      _monitor.set(State.RUNNING);
      
    }
  }
  
  
  protected final void quitSlave() {
    transition(State.RUNNING, State.QUITTING);
    attemptQuit(_slave);
    _slave = null;
    _monitor.set(State.FRESH);
    
  }
    
  
  private static void attemptQuit(SlaveRemote slave) {
    try { slave.quit(); }
    catch (RemoteException e) { error.log("Unable to complete slave.quit()", e); }
  }
  
  
  protected void dispose() {
    transition(State.FRESH, State.DISPOSED);
    if (_masterStub.isResolved()) { 
      try { UnicastRemoteObject.unexportObject(this, true); }
      catch (NoSuchObjectException e) { error.log(e); }
    }
  }
  
  
  private void transition(State from, State to) {
    State s = _monitor.value();
    
    while (!(s.equals(from) && _monitor.compareAndSet(from, to))) {
      if (s.equals(State.DISPOSED)) { throw new IllegalStateException("In disposed state"); }
      debug.log("Waiting for transition from " + s + " to " + from);
      try { s = _monitor.ensureNotState(s); }
      catch (InterruptedException e) { throw new UnexpectedException(e); }
    }
    
  }
  
  protected boolean isDisposed() { return _monitor.value().equals(State.DISPOSED); }
  
  
  public void checkStillAlive() { }
  
}

