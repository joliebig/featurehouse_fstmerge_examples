

package edu.rice.cs.drjava.model.debug;

import java.util.ArrayList;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;


public interface Debugger {
  
  public static enum StepType { STEP_INTO, STEP_OVER, STEP_OUT; }
  
  
  public void addListener(DebugListener listener);
  
  
  public void removeListener(DebugListener listener);
  
  
  public boolean isAvailable();
  
  public DebugModelCallback callback();
  
  
  public void startUp() throws DebugException;
  
  
  public void shutdown();
  
  
  public boolean isReady();
  


  


  
  
  public void setCurrentThread(DebugThreadData d) throws DebugException;
  
  
  public void setAutomaticTraceEnabled(boolean e);
    
  
  public boolean isAutomaticTraceEnabled(); 
  
  
  public void resume() throws DebugException;
  
  
  public void resume(DebugThreadData data) throws DebugException;
  
  
  public void step(StepType type) throws DebugException;
  
  
  public void addWatch(String field) throws DebugException;
  
  
  public void removeWatch(String field) throws DebugException;
  
  
  public void removeWatch(int index) throws DebugException;
  
  
  public void removeAllWatches() throws DebugException;
  
  
  
  public boolean toggleBreakpoint(OpenDefinitionsDocument doc, int offset, boolean isEnabled) throws DebugException;
  
  
  public void setBreakpoint(final Breakpoint breakpoint) throws DebugException;
  
  
  public void removeBreakpoint(final Breakpoint breakpoint) throws DebugException;
  
  
  public ArrayList<DebugWatchData> getWatches() throws DebugException;
  
  
  public ArrayList<DebugThreadData> getCurrentThreadData() throws DebugException;
  
  
  public ArrayList<DebugStackData> getCurrentStackFrameData() throws DebugException;
  
  
  public boolean hasSuspendedThreads() throws DebugException;
  
  
  public boolean hasRunningThread() throws DebugException;
  
  
  public boolean isCurrentThreadSuspended() throws DebugException;
  
  
  public void scrollToSource(DebugStackData data) throws DebugException;
  
  
  public void scrollToSource(Breakpoint bp);
  
  
  public Breakpoint getBreakpoint(int line, String className) throws DebugException;
}
