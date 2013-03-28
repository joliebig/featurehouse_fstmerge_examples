

package edu.rice.cs.drjava.model.repl;

import java.io.File;


public class DummyInteractionsListener implements InteractionsListener {
  
  
  public void interactionStarted() { }

  
  public void interactionEnded() { }
  
  
  public void interactionErrorOccurred(int offset, int length) { }

  
  public void interpreterResetting() { }
  
  
  public void interpreterReady(File wd) { }

  
  public void interpreterExited(int status) { }
  
  
  public void interpreterResetFailed(Throwable t) { }
  
  
  public void interpreterChanged(boolean inProgress) { }

  
  public void interactionIncomplete() { }
  
}

