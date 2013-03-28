

package edu.rice.cs.drjava.model;

import java.io.File;
import java.util.List;


public interface SingleDisplayModel extends GlobalModel {
  
  public OpenDefinitionsDocument getActiveDocument();

  
  public void setActiveDocument(OpenDefinitionsDocument doc);
  
  
  public void refreshActiveDocument();

  
  public java.awt.Container getDocCollectionWidget();

  
  public void setActiveNextDocument();

  
  public void setActivePreviousDocument();

  
  public boolean closeFiles(List<OpenDefinitionsDocument> docList);  
  
  public void setActiveFirstDocument();
  
  public void dispose();
  
  
  public void disposeExternalResources();

  public boolean closeAllFilesOnQuit();
  
  
  public File[] getExclFiles();
  
  
  public void setExcludedFiles(File[] fs);




  
  
  public void ensureJVMStarterFinished();
}
