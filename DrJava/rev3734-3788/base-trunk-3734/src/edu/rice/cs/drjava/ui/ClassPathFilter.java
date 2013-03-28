 

package edu.rice.cs.drjava.ui;

import java.io.File;
import javax.swing.filechooser.FileFilter;



public class ClassPathFilter extends FileFilter {
  public static final ClassPathFilter ONLY = new ClassPathFilter();

  protected ClassPathFilter() { }

  
  public boolean accept(File f) {
    if (f.isDirectory()) return true;
    String extension = getExtension(f);
    if (extension != null) return (extension.equals("jar") || extension.equals("zip"));
    return false;
  }

  
  public String getDescription() { return "Classpath elements"; }

  
  public static String getExtension(File f) {
    String ext = null;
    String s = f.getName();
    int i = s.lastIndexOf('.');
    if (i > 0 && i < s.length() - 1) ext = s.substring(i + 1).toLowerCase();
    return ext;
  }
}
