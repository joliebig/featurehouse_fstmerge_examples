

package edu.rice.cs.drjava.model.repl;

import java.io.File;


public interface InteractionsListener {
  
  
  public void interactionStarted();
  
  
  public void interactionEnded();
  
  
  public void interactionErrorOccurred(int offset, int length);
  
  
  public void interpreterResetting();
  
  
  public void interpreterReady(File wd);
  
  
  public void interpreterResetFailed(Throwable t);
  
  
  public void interpreterExited(int status);
  
  
  public void interpreterChanged(boolean inProgress);
  
  
  public void interactionIncomplete();
  
}
