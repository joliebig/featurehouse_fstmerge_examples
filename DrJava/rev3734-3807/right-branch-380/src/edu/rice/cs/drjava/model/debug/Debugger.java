

package edu.rice.cs.drjava.model.debug;

import java.util.Vector;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;

import com.sun.jdi.request.StepRequest;


public interface Debugger {
  public static final int STEP_INTO = StepRequest.STEP_INTO;
  public static final int STEP_OVER = StepRequest.STEP_OVER;
  public static final int STEP_OUT = StepRequest.STEP_OUT;

  
  public void addListener(DebugListener listener);

  
  public void removeListener(DebugListener listener);

  
  public boolean isAvailable();

  
  public void startup() throws DebugException;

  
  public void shutdown();

  
  public boolean isReady();
  
  
  public boolean inDebugMode();







  
  public void setCurrentThread(DebugThreadData d) throws DebugException;

  
  public void resume() throws DebugException;

  
  public void resume(DebugThreadData data) throws DebugException;

  
  public void step(int flag) throws DebugException;

  
  
  

  
  public void addWatch(String field) throws DebugException;

  
  public void removeWatch(String field) throws DebugException;

  
  public void removeWatch(int index) throws DebugException;

  
  public void removeAllWatches() throws DebugException;


  
  public void toggleBreakpoint(OpenDefinitionsDocument doc, int offset, int lineNum, boolean isEnabled) throws DebugException;

  
  public void setBreakpoint(final Breakpoint breakpoint) throws DebugException;

 
  public void removeBreakpoint(final Breakpoint breakpoint) throws DebugException;

  
  public void removeAllBreakpoints() throws DebugException;

  
  public Vector<Breakpoint> getBreakpoints() throws DebugException;

  
  public void printBreakpoints() throws DebugException;

  
  public Vector<DebugWatchData> getWatches() throws DebugException;

  
  public Vector<DebugThreadData> getCurrentThreadData() throws DebugException;

  
  public Vector<DebugStackData> getCurrentStackFrameData() throws DebugException;

  
  public boolean hasSuspendedThreads() throws DebugException;

  
  public boolean hasRunningThread() throws DebugException;

  
  public boolean isCurrentThreadSuspended() throws DebugException;

  
  public void scrollToSource(DebugStackData data) throws DebugException;

  
  public void scrollToSource(Breakpoint bp);

  
  public Breakpoint getBreakpoint(int line, String className) throws DebugException;
}
