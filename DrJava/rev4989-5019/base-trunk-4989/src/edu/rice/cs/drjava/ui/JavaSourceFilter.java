

package edu.rice.cs.drjava.ui;

import java.io.File;
import javax.swing.filechooser.FileFilter;
import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.DrJavaRoot;
import edu.rice.cs.drjava.config.OptionConstants;



public class JavaSourceFilter extends FileFilter {
  
  
  public boolean accept(File f) {
    if (f.isDirectory()) {
      return true;
    }
    String extension = getExtension(f);
    if (extension != null) {
      switch (DrJava.getConfig().getSetting(OptionConstants.LANGUAGE_LEVEL)) {
        case (DrJavaRoot.FULL_JAVA): return (extension.equals("java") || extension.equals("j"));
        case (DrJavaRoot.ELEMENTARY_LEVEL): return extension.equals("dj0");
        case (DrJavaRoot.INTERMEDIATE_LEVEL): return extension.equals("dj1");
        case (DrJavaRoot.ADVANCED_LEVEL): return extension.equals("dj2");
      }
    }
    return false;
  }

  
  public String getDescription() {
    switch (DrJava.getConfig().getSetting(OptionConstants.LANGUAGE_LEVEL)) {
        case (DrJavaRoot.FULL_JAVA): return "Java source files";
        case (DrJavaRoot.ELEMENTARY_LEVEL): return "Elementary source files (.dj0)";
        case (DrJavaRoot.INTERMEDIATE_LEVEL): return "Intermediate source files (.dj1)";
        case (DrJavaRoot.ADVANCED_LEVEL): return "Advanced source files (.dj2)";
      }
    return "Java source files";
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



