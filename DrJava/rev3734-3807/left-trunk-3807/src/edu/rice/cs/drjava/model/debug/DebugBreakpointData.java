

package edu.rice.cs.drjava.model.debug;

import java.io.File;


public interface DebugBreakpointData {
  
  public File getFile();

  
  public int getOffset();
  
  
  public int getLineNumber();
  
  
  public boolean isEnabled();
}
