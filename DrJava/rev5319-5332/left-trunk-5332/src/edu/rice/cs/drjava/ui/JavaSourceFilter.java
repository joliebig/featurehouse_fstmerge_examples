

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
        case (OptionConstants.FULL_JAVA):
          return (extension.equals(OptionConstants.JAVA_FILE_EXTENSION));
        case (OptionConstants.ELEMENTARY_LEVEL):
          return extension.equals(OptionConstants.OLD_DJ0_FILE_EXTENSION);
        case (OptionConstants.INTERMEDIATE_LEVEL):
          return extension.equals(OptionConstants.OLD_DJ1_FILE_EXTENSION);
        case (OptionConstants.ADVANCED_LEVEL):
          return extension.equals(OptionConstants.OLD_DJ2_FILE_EXTENSION);
        case (OptionConstants.FUNCTIONAL_JAVA_LEVEL):
          return extension.equals(OptionConstants.DJ_FILE_EXTENSION);
      }
    }
    return false;
  }

  
  public String getDescription() {
    switch (DrJava.getConfig().getSetting(OptionConstants.LANGUAGE_LEVEL)) {
        case (OptionConstants.FULL_JAVA):
          return "Java source files (*"+OptionConstants.JAVA_FILE_EXTENSION+")";
        case (OptionConstants.ELEMENTARY_LEVEL):
          return "Elementary source files (*"+OptionConstants.OLD_DJ0_FILE_EXTENSION+")";
        case (OptionConstants.INTERMEDIATE_LEVEL):
          return "Intermediate source files (*"+OptionConstants.OLD_DJ1_FILE_EXTENSION+")";
        case (OptionConstants.ADVANCED_LEVEL):
          return "Advanced source files (*"+OptionConstants.OLD_DJ2_FILE_EXTENSION+")";
        case (OptionConstants.FUNCTIONAL_JAVA_LEVEL):
          return "Functional Java source files (*"+OptionConstants.DJ_FILE_EXTENSION+")";
      }
    return "Java source files";
  }

  
  public static String getExtension(File f) {
    String ext = null;
    String s = f.getName();
    int i = s.lastIndexOf('.');
    if (i > 0 && i < s.length() - 1) {
      ext = s.substring(i).toLowerCase();
    }
    return ext;
  }
}
