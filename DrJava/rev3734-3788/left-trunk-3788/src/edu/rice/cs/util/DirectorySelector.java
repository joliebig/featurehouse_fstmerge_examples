

package edu.rice.cs.util;

import java.io.File;

public interface DirectorySelector {
  
  
  public File getDirectory(File start) throws OperationCanceledException;
  
  
  public boolean askUser(String message, String title);
  
  
  public void warnUser(String message, String title);
  
  
  public boolean isRecursive();
  
}
