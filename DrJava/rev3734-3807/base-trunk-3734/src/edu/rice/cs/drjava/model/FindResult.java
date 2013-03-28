

package edu.rice.cs.drjava.model;

import edu.rice.cs.util.text.AbstractDocumentInterface;


public class FindResult {
  private AbstractDocumentInterface _document;
  private int _foundoffset;
  private boolean _wrapped;
  private boolean _allDocsWrapped;
  
  
  public FindResult(AbstractDocumentInterface document, int foundoffset, boolean wrapped, boolean allDocsWrapped) {
    _document = document;
    _foundoffset = foundoffset;
    _wrapped = wrapped;
    _allDocsWrapped = allDocsWrapped;
  }
  
  
  public AbstractDocumentInterface getDocument() { return _document; }
  
   
  public int getFoundOffset() { return _foundoffset; }
  
  
  public boolean getWrapped() { return _wrapped; }
  
  
  public boolean getAllDocsWrapped() { return _allDocsWrapped; }
}
