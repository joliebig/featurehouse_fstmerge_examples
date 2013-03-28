

package edu.rice.cs.util.swing;

import edu.rice.cs.drjava.model.OpenDefinitionsDocument;   

import edu.rice.cs.util.text.AbstractDocumentInterface;


public interface DocumentIterator {
  
  OpenDefinitionsDocument getNextDocument(OpenDefinitionsDocument doc);
  
  
  OpenDefinitionsDocument getPrevDocument(OpenDefinitionsDocument doc);
  
  
  int getDocumentCount();
}