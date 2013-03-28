

package edu.rice.cs.util.swing;

import edu.rice.cs.util.text.AbstractDocumentInterface;


public interface DocumentIterator {
  
  AbstractDocumentInterface getNextDocument(AbstractDocumentInterface doc);
  
  
  AbstractDocumentInterface getPrevDocument(AbstractDocumentInterface doc);
  
  
  int getDocumentCount();
}