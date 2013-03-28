

package edu.rice.cs.drjava.model.debug;

import java.util.ArrayList;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;


public class NoDebuggerAvailable implements Debugger {
  
  
  public static final NoDebuggerAvailable ONLY = new NoDebuggerAvailable();
  
  
  private NoDebuggerAvailable() { }
  
  
  public boolean isAvailable() { return false; }
  
  public DebugModelCallback callback() { return new DebugModelCallback() {}; }
  
  
  public void startUp() throws DebugException { }
  
  
  public void shutdown() { }
  
  
  public boolean isReady() { return false; }
  
  
  public void suspend(DebugThreadData d) { }
  
  
  public void suspendAll() { }
  
  
  public void setCurrentThread(DebugThreadData d) { }

  
  public void setAutomaticTraceEnabled(boolean e) { }
  
  
  public boolean isAutomaticTraceEnabled() { return false; } 

  
  public void resume() { }
  
  
  public void resume(DebugThreadData data) { }
  
  
  public void step(StepType type) throws DebugException { }
  
  
  public void clearCurrentStepRequest() { }
  
  
  public void addWatch(String field) { }
  
  
  public void removeWatch(String field) { }
  
  
  public void removeWatch(int index) { }
  
  
  public void removeAllWatches() { }
  
  
  public boolean toggleBreakpoint(OpenDefinitionsDocument doc, int offset, boolean isEnabled) 
    throws DebugException { return false; }
  
  
  public void setBreakpoint(Breakpoint breakpoint) { }
  
  
  public void removeBreakpoint(Breakpoint breakpoint) { }
  
  
  public ArrayList<DebugWatchData> getWatches() { return new ArrayList<DebugWatchData>(); }
  
  
  public ArrayList<DebugThreadData> getCurrentThreadData() { return new ArrayList<DebugThreadData>(); }
  
  
  public ArrayList<DebugStackData> getCurrentStackFrameData() { return new ArrayList<DebugStackData>(); }
  
  
  public void addListener(DebugListener listener) { }
  
  
  public void removeListener(DebugListener listener) { }
  
  
  public boolean hasSuspendedThreads() { return false; }
  
  
  public boolean hasRunningThread() { return false; }
  
  
  public boolean isCurrentThreadSuspended() { return false; }
  
  
  public void scrollToSource(DebugStackData data) { }
  
  
  public void scrollToSource(Breakpoint bp) { }
  
  
  public Breakpoint getBreakpoint(int line, String className) { return null; }
}
