

package edu.rice.cs.drjava.model.debug;

import edu.rice.cs.drjava.model.RegionManagerListener;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;


public interface DebugListener extends RegionManagerListener<Breakpoint> {
  
  
  public void debuggerStarted();
  
  
  public void debuggerShutdown();
  
  
  public void threadLocationUpdated(OpenDefinitionsDocument doc, int lineNumber, boolean shouldHighlight);  
  
  
  public void breakpointReached(Breakpoint bp);
  
  
  public void watchSet(DebugWatchData w);
  
  
  public void watchRemoved(DebugWatchData w);
  
  
  public void stepRequested();
  
  
  public void currThreadSuspended();
  
  
  public void currThreadResumed();
  
  
  public void threadStarted();
  
  
  public void currThreadDied();
  
  
  public void nonCurrThreadDied();
  
  
  public void currThreadSet(DebugThreadData thread);
  
  
  
  
  
}