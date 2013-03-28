

package edu.rice.cs.drjava.model;

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

  public void jarAll();
  
  public void dispose();




}
