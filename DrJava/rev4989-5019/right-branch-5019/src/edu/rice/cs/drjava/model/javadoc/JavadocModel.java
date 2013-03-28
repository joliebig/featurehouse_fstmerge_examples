

package edu.rice.cs.drjava.model.javadoc;

import java.io.File;
import java.io.IOException;

import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.drjava.model.FileSaveSelector;
import edu.rice.cs.drjava.model.compiler.CompilerErrorModel;
import edu.rice.cs.util.DirectorySelector;


public interface JavadocModel {
  
  public static final String SUGGESTED_DIR_NAME = "doc";
  
  
  public boolean isAvailable();
  
  
  public void addListener(JavadocListener listener);
  
  
  public void removeListener(JavadocListener listener);
  
  
  public void removeAllListeners();
  
  
  public CompilerErrorModel getJavadocErrorModel();
  
  
  public void resetJavadocErrors();
  
  
  public File suggestJavadocDestination(OpenDefinitionsDocument doc);
  
  
  public void javadocAll(DirectorySelector select, FileSaveSelector saver) throws IOException;
  
  
  public void javadocDocument(OpenDefinitionsDocument doc, FileSaveSelector saver) throws IOException;
}
