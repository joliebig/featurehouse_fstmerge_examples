

package edu.rice.cs.drjava.ui;

import java.io.File;
import javax.swing.filechooser.FileFilter;
import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.DrJavaRoot;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.model.DrJavaFileUtils;


public class SmartSourceFilter extends JavaSourceFilter {
  
  
  public boolean accept(File f) {
    if (f.isDirectory()) {
      return true;
    }
    
    String name = f.getName();
    if (DrJavaFileUtils.isLLFile(name)) { return true; }
    if (!name.endsWith(OptionConstants.JAVA_FILE_EXTENSION)) { return false; }

    
    File parent = f.getParentFile();
    if (parent==null) {
      
      return true;
    }
    
    if (new File(parent,DrJavaFileUtils.getDJForJavaFile(name)).exists()) return false;
    if (new File(parent,DrJavaFileUtils.getDJ0ForJavaFile(name)).exists()) return false;
    if (new File(parent,DrJavaFileUtils.getDJ1ForJavaFile(name)).exists()) return false;
    if (new File(parent,DrJavaFileUtils.getDJ2ForJavaFile(name)).exists()) return false;

    return true; 
  }

  
  public String getDescription() {
    return "DrJava source files (*"+OptionConstants.JAVA_FILE_EXTENSION+", *"+
      OptionConstants.DJ_FILE_EXTENSION+", *"+OptionConstants.OLD_DJ0_FILE_EXTENSION+", *"+
      OptionConstants.OLD_DJ1_FILE_EXTENSION+", *"+OptionConstants.OLD_DJ2_FILE_EXTENSION+")";
  }
}
