

package edu.rice.cs.drjava.model.cache;

import java.io.IOException;

import edu.rice.cs.drjava.model.FileMovedException;
import edu.rice.cs.drjava.model.definitions.DefinitionsDocument;


public interface DCacheAdapter {
  
  
  public DefinitionsDocument getDocument() throws IOException, FileMovedException;
  
  
  public boolean isReady();
  
  
  public void close();
  
  public DDReconstructor getReconstructor();
  
  
  public void documentSaved(String fileName);
  
  
  public void documentModified();
  
  
  public void documentReset();
  
}