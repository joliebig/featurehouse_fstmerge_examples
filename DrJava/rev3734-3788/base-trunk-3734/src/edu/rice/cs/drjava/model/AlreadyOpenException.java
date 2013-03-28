

package edu.rice.cs.drjava.model;


public class AlreadyOpenException extends Exception {
  private OpenDefinitionsDocument _openDoc;

  
  public AlreadyOpenException(OpenDefinitionsDocument doc) { _openDoc = doc; }

  
  public OpenDefinitionsDocument getOpenDocument() { return _openDoc; }

}