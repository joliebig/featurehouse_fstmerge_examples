

package edu.rice.cs.drjava.ui;

import java.io.File;
import javax.swing.filechooser.FileFilter;



public class DirectoryFilter extends FileFilter {
  
  private String _description;

  
  public DirectoryFilter() {
    this("Directories");
  }
  
  
  public DirectoryFilter(String description) {
    _description = description;
  }

  
  public boolean accept(File f) {
    return f.isDirectory();
  }

  
  public String getDescription() {
    return _description;
  }
}
