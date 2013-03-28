

package edu.rice.cs.drjava.model;

import java.io.File;
import java.io.IOException;

import edu.rice.cs.drjava.model.compiler.CompilerErrorModel;
import edu.rice.cs.util.DirectorySelector;


public interface JavadocModel {
  
  public static final String SUGGESTED_DIR_NAME = "doc";
  
  
  public void addListener(JavadocListener listener);

  
  public void removeListener(JavadocListener listener);

  
  public void removeAllListeners();
  
  
  public CompilerErrorModel getJavadocErrorModel();
  
  
  public void resetJavadocErrors();
  
  
  public File suggestJavadocDestination(OpenDefinitionsDocument doc);
  
  
  public void javadocAll(DirectorySelector select, FileSaveSelector saver,
                         String classPath)
    throws IOException;
  
  
  public void javadocDocument(final OpenDefinitionsDocument doc,
                              final FileSaveSelector saver,
                              final String classPath)
    throws IOException;
}
