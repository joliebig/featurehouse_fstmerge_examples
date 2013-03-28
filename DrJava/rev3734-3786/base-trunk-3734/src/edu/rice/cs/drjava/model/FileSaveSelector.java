

package edu.rice.cs.drjava.model;

import java.io.File;

import edu.rice.cs.util.OperationCanceledException;


public interface FileSaveSelector {

  
  public File getFile() throws OperationCanceledException;
  
  
  public boolean warnFileOpen(File f);
  
  
  public boolean verifyOverwrite();
  
  
  public boolean shouldSaveAfterFileMoved(OpenDefinitionsDocument doc, File oldFile);

}
