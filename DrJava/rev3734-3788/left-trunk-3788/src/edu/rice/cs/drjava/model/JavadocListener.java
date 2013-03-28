

package edu.rice.cs.drjava.model;

import java.io.File;


public interface JavadocListener {
  
  
  public void saveBeforeJavadoc();
  
  
  public void javadocStarted();
  
  
  public void javadocEnded(boolean success, File destDir, boolean allDocs);
}
