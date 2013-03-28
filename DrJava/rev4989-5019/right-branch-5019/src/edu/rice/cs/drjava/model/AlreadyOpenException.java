

package edu.rice.cs.drjava.model;
import edu.rice.cs.util.UnexpectedException;


public class AlreadyOpenException extends Exception {
  
  private OpenDefinitionsDocument[] _openDocs;
  
  public AlreadyOpenException(OpenDefinitionsDocument[] docs) {
    if (docs.length == 0) {
      throw new UnexpectedException("This exception can't be constructed with an empty array"); 
    }
    _openDocs = docs;
  }

  
  public AlreadyOpenException(OpenDefinitionsDocument doc) { _openDocs = new OpenDefinitionsDocument[] { doc }; }

  
  public OpenDefinitionsDocument getOpenDocument() { return _openDocs[0]; }
  
  public OpenDefinitionsDocument[] getOpenDocuments() { return _openDocs; }

}