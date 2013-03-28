

package edu.rice.cs.drjava.ui;

import  java.io.File;
import  javax.swing.filechooser.FileFilter;



public class InteractionsHistoryFilter extends FileFilter {

  
  public static final String HIST_EXTENSION = "hist";
  
  
  public boolean accept(File f) {
    if (f.isDirectory()) {
      return true;
    }
    String ext = getExtension(f);
    if ((ext != null) && (ext.equals(HIST_EXTENSION))) {
      return true;
    }
    return false;
  }

  
  public String getDescription() {
    return "Interaction History Files";
  }

  
  public static String getExtension(File f) {
    String ext = null;
    String s = f.getName();
    int i = s.lastIndexOf('.');
    if (i > 0 && i < s.length() - 1) {
      ext = s.substring(i + 1).toLowerCase();
    }
    return ext;
  }
}



