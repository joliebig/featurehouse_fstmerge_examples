

package edu.rice.cs.drjava.model;


public class DocumentClosedException extends RuntimeException {
  private OpenDefinitionsDocument _document;
  
  
  public DocumentClosedException(OpenDefinitionsDocument d, String s) {
    super(s);
    _document = d;
  }
  
  
  public OpenDefinitionsDocument getDocument() {
    return _document;
  }
}