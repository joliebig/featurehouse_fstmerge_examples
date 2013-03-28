

package edu.rice.cs.drjava.model.debug;

import java.util.Vector;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;


public class NoDebuggerAvailable implements Debugger {
  
  
  public static final NoDebuggerAvailable ONLY = new NoDebuggerAvailable();

  
  private NoDebuggerAvailable() { }

  
  public boolean isAvailable() { return false; }

  
  public void startup() throws DebugException {
    throw new IllegalStateException("No debugger is available");
  }

  
  public void shutdown() {
    throw new IllegalStateException("No debugger is available");
  }

  
  public boolean isReady() {
    throw new IllegalStateException("No debugger is available");
  }
  
  public boolean inDebugMode() { return false; }

  
  public void suspend(DebugThreadData d) {
    throw new IllegalStateException("No debugger is available");
  }

  
  public void suspendAll() {
    throw new IllegalStateException("No debugger is available");
  }

  
  public void setCurrentThread(DebugThreadData d) {
    throw new IllegalStateException("No debugger is available");
  }

  
  public void resume() {
    throw new IllegalStateException("No debugger is available");
  }

  
  public void resume(DebugThreadData data) {
    throw new IllegalStateException("No debugger is available");
  }

  
  public void step(int flag) throws DebugException {
    throw new IllegalStateException("No debugger is available");
  }

  
  public void clearCurrentStepRequest() {
    throw new IllegalStateException("No debugger is available");
  }

  
  public void addWatch(String field) {
    throw new IllegalStateException("No debugger is available");
  }

  
  public void removeWatch(String field) {
    throw new IllegalStateException("No debugger is available");
  }

  
  public void removeWatch(int index) {
    throw new IllegalStateException("No debugger is available");
  }

  
  public void removeAllWatches() {
    throw new IllegalStateException("No debugger is available");
  }

  
  public void toggleBreakpoint(OpenDefinitionsDocument doc,
                               int offset, int lineNum, boolean isEnabled)
    throws DebugException
  {
    throw new IllegalStateException("No debugger is available");
  }

  
  public void setBreakpoint(Breakpoint breakpoint) {
    throw new IllegalStateException("No debugger is available");
  }

 
  public void removeBreakpoint(Breakpoint breakpoint) {
    throw new IllegalStateException("No debugger is available");
  }

  
  public void removeAllBreakpoints() {
    throw new IllegalStateException("No debugger is available");
  }

  
  public Vector<Breakpoint> getBreakpoints() {
    throw new IllegalStateException("No debugger is available");
  }

  
  public void printBreakpoints() {
    throw new IllegalStateException("No debugger is available");
  }

  
  public Vector<DebugWatchData> getWatches() {
    throw new IllegalStateException("No debugger is available");
  }

  
  public Vector<DebugThreadData> getCurrentThreadData() {
    throw new IllegalStateException("No debugger is available");
  }

  
  public Vector<DebugStackData> getCurrentStackFrameData() {
    throw new IllegalStateException("No debugger is available");
  }

  
  public void addListener(DebugListener listener) {
    throw new IllegalStateException("No debugger is available");
  }

  
  public void removeListener(DebugListener listener) {
    throw new IllegalStateException("No debugger is available");
  }

   
  public boolean hasSuspendedThreads() {
    throw new IllegalStateException("No debugger is available");
  }

  
  public boolean hasRunningThread() {
    throw new IllegalStateException("No Debugger is available");
  }

  
  public boolean isCurrentThreadSuspended() {
    throw new IllegalStateException("No debugger is available");
  }

  
  public void scrollToSource(DebugStackData data) {
    throw new IllegalStateException("No debugger is available");
  }

  
  public void scrollToSource(Breakpoint bp) {
    throw new IllegalStateException("No debugger is available");
  }

  
  public Breakpoint getBreakpoint(int line, String className) {
    throw new IllegalStateException("No debugger is available");
  }
}
