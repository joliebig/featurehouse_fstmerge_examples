

package edu.rice.cs.drjava.model.debug.jpda;

import com.sun.jdi.*;
import com.sun.jdi.request.*;

import java.util.Vector;
import java.util.List;
import java.io.File;

import edu.rice.cs.drjava.model.*;
import edu.rice.cs.drjava.model.definitions.ClassNameNotFoundException;
import edu.rice.cs.drjava.model.debug.DebugException;
import edu.rice.cs.drjava.model.debug.LineNotExecutableException;
import javax.swing.text.BadLocationException;


public abstract class DocumentDebugAction<T extends EventRequest> extends DebugAction<T> {
  
  protected volatile String _className;
  protected volatile String _exactClassName;
  protected volatile File _file;
  protected volatile OpenDefinitionsDocument _doc;
  protected int _offset;
  
  public final int SHORT_DOC_MAX_LENGTH = 20000;
  
  
  
  public DocumentDebugAction (JPDADebugger manager, OpenDefinitionsDocument doc, int offset) throws DebugException {
    super(manager);
    _exactClassName = null;
    try {
      if (offset >= 0) {
        if (doc.getLength() < SHORT_DOC_MAX_LENGTH) {
          
          
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
  
  
  public File getFile() { return _file; }
  
  
  public OpenDefinitionsDocument getDocument() { return _doc; }
  
  
  public int getOffset() { return _offset; }
  
  
  public String getExactClassName() { return _exactClassName; }
  
  
  public boolean createRequests(Vector<ReferenceType> refTypes) throws DebugException {
    _createRequests(refTypes);
    if (_requests.size() > 0) {
      _prepareRequests(_requests);
      return true;
    }
    else return false;
  }
  
  
  protected void _initializeRequests(Vector<ReferenceType> refTypes) throws DebugException {
    if (refTypes.size() > 0) createRequests(refTypes);
    else {
      if (_exactClassName != null) {
        List<ReferenceType> referenceTypes = _manager.getVM().classesByName(_exactClassName);
        if (referenceTypes.size() > 0) {
          
          throw new LineNotExecutableException("Cannot set breakpoint, line " + getLineNumber() + 
                                               " is not an executable line.\nYou may have to recompile.");
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
