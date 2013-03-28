

package edu.rice.cs.drjava.model.debug;

import com.sun.jdi.*;
import com.sun.jdi.request.*;

import java.util.Vector;
import java.util.List;
import java.io.File;

import edu.rice.cs.drjava.model.*;
import edu.rice.cs.drjava.model.definitions.ClassNameNotFoundException;
import javax.swing.text.BadLocationException;


public abstract class DocumentDebugAction<T extends EventRequest> extends DebugAction<T> {
  
  protected String _className;
  protected String _exactClassName;
  protected File _file;
  protected OpenDefinitionsDocument _doc;
  protected int _offset;
  
  
  
  public DocumentDebugAction (JPDADebugger manager,
                              OpenDefinitionsDocument doc,
                              int offset) throws DebugException {
    super(manager);
    _exactClassName = null;
    try {
      if (offset >= 0) {
        if (doc.getNumberOfLines()<500) {
          
          
          _exactClassName = doc.getEnclosingClassName(offset, true);
        }
      }
    }
    catch(ClassNameNotFoundException cnnfe) {  }
    catch(BadLocationException ble) {  }
    try {
      if (offset >= 0) {
        _className = doc.getQualifiedClassName(offset);
      }
    }
    catch (ClassNameNotFoundException cnnfe) {
      
      _className = "";
    }
    
    
    try {
      _file = doc.getFile();
      if (_file == null) throw new DebugException("This document has no source file.");
    }
    catch (FileMovedException fme) {
      throw new DebugException("This document's file no longer exists: " + fme.getMessage());
    }
    _doc = doc;
    _offset = offset;
  }
  
  
  public String getClassName() { return _className; }
  
  
  public File getFile() {
    return _file;
  }
  
  
  public OpenDefinitionsDocument getDocument() {
    return _doc;
  }
  
  
  public int getOffset() { return _offset; }
  
  
  public String getExactClassName() { return _exactClassName; }
  
  
  public boolean createRequests(Vector<ReferenceType> refTypes) throws DebugException {
    _createRequests(refTypes);
    if (_requests.size() > 0) {
      _prepareRequests(_requests);
      return true;
    }
    else {
      return false;
    }
  }
  
  
  protected void _initializeRequests(Vector<ReferenceType> refTypes) throws DebugException {
    if (refTypes.size() > 0) {
      createRequests(refTypes);
    }
    else {
      if (_exactClassName!=null) {
        List<ReferenceType> referenceTypes = _manager.getVM().classesByName(_exactClassName);
        if (referenceTypes.size()>0) {
          
          throw new LineNotExecutableException(toString()+" not on an executable line; not set.");
        }
      }
    }
    
    
    
    
    _manager.getPendingRequestManager().addPendingRequest(this);
    
  }
  
  
  protected abstract void _createRequests(Vector<ReferenceType> refTypes) throws DebugException;
  
  
  protected void _prepareRequest(T request) {
    super._prepareRequest(request);
    request.putProperty("document", _doc);
  }
}
