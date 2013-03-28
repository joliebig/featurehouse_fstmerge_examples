

package edu.rice.cs.drjava.model.junit;

import java.io.IOException;
import java.io.File;
import java.util.List;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.util.text.SwingDocument;

public interface JUnitModel {
  
  
  
  
  
  public void setForceTestSuffix(boolean b);

  

  
  public void addListener(JUnitListener listener);

  
  public void removeListener(JUnitListener listener);

  
  public void removeAllListeners();

  

  
  public SwingDocument getJUnitDocument();

  
  public void junitAll();

  
  public void junitProject();

  
  public void junitDocs(List<OpenDefinitionsDocument> lod);
  
  
  public void junit(OpenDefinitionsDocument doc) throws ClassNotFoundException, IOException;

  
  public void junitClasses(List<String> qualifiedClassnames, List<File> files);
  
  

  
  public JUnitErrorModel getJUnitErrorModel();

  
  public void resetJUnitErrors();
  
}
