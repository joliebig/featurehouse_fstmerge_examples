

package edu.rice.cs.util.swing;

import java.io.File;


public interface FileDisplayManager extends DisplayManager<File> {
  
  
  public FileDisplay makeFileDisplay(File f);
  
  
  public FileDisplay makeFileDisplay(File parent, String child);
  
  
  public FileDisplay makeNewFolderDisplay(File parent);
  
  
  public void update();
  
}