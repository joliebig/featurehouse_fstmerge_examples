

package edu.rice.cs.util.swing;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import java.io.File;


public class DefaultFileDisplayManager implements FileDisplayManager {
  
  private JFileChooser _jfc;
  
  public DefaultFileDisplayManager() { _jfc = new JFileChooser(); }
  
  
  public Icon getIcon(File f) {
    
    if (f != null && ! f.exists()) f = null; 
    
    return _jfc.getIcon(f);
  }
  
  
  public String getName(File f) { return _jfc.getName(f); }
  
  
  public FileDisplay makeFileDisplay(File f) { return new FileDisplay(f, this); }
  
  
  public FileDisplay makeFileDisplay(File parent, String child) { return new FileDisplay(parent, child, this); }
  
  
  public FileDisplay makeNewFolderDisplay(File parent) { return FileDisplay.newFile(parent, this); }
  
  
  public void update() { _jfc.updateUI(); }
}