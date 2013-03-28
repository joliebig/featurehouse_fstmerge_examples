

package edu.rice.cs.drjava.model;

import java.io.File;

public class GlobalModelNaming {
    private GlobalModelNaming() { }

  
  public static String getDisplayFilename(OpenDefinitionsDocument doc) {

    String fileName = doc.getFilename();

    
    if (fileName.endsWith(".java")) {
      int extIndex = fileName.lastIndexOf(".java");
      if (extIndex > 0) {
        fileName = fileName.substring(0, extIndex);
      }
    }

    
    if (doc.isModifiedSinceSave()) {
      fileName = fileName + " *";
    }

    return fileName;
  }

  
  public static String getDisplayFullPath(OpenDefinitionsDocument doc) {

    String path = "(Untitled)";
    try {
      File file = doc.getFile();
      if (file != null) path = file.getAbsolutePath();
    }
    catch (FileMovedException fme) {
      
      File file = fme.getFile();
      path = file.getAbsolutePath();
    }

    
    if (doc.isModifiedSinceSave()) path = path + " *";
    return path;
  }
}
