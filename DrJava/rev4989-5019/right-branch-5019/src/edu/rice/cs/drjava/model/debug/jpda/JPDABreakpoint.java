

package edu.rice.cs.drjava.model.debug.jpda;

import edu.rice.cs.drjava.model.OrderedDocumentRegion;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.drjava.model.IDocumentRegion;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.drjava.model.debug.Breakpoint;
import edu.rice.cs.drjava.model.debug.DebugException;

import java.awt.EventQueue;
import java.util.List;
import java.util.Vector;
import javax.swing.text.BadLocationException;
import javax.swing.text.Position;

import com.sun.jdi.*;
import com.sun.jdi.request.*;


public class JPDABreakpoint extends DocumentDebugAction<BreakpointRequest> implements Breakpoint {
  
  private volatile Position _position;
  private volatile Position _startPos;
  private volatile Position _endPos;
  
  
  private volatile OpenDefinitionsDocument _doc;
  
  
  public JPDABreakpoint(OpenDefinitionsDocument doc, int offset, boolean isEnabled, JPDADebugger manager)
    throws DebugException {
    
    super(manager, doc, offset);
    
    assert EventQueue.isDispatchThread();
    _doc = doc;
    try { _position = doc.createPosition(offset); }
    catch(BadLocationException e) { throw new UnexpectedException(e); }
    
    _suspendPolicy = EventRequest.SUSPEND_EVENT_THREAD;
    _isEnabled = isEnabled;
    update();
    
    if (_manager != null && _manager.isReady()) {
      
      
      Vector<ReferenceType> refTypes = _manager.getReferenceTypes(_className, _manager.LLBreakpointLineNum(this));
      _initializeRequests(refTypes);
      setEnabled(isEnabled);
    }
  }
  
  
  public String getString() {
    try {
      int start = _startPos.getOffset();
      int end = _endPos.getOffset();
      int length = end - start;
      if (length <= 120) return _doc.getText(start, length);
      StringBuilder sb = new StringBuilder(124);
      sb.append(_doc.getText(start, 120)).append(" ...");
      return sb.toString();
    }
    catch(BadLocationException e) { throw new UnexpectedException(e); }
  }
  







  
  
  protected void _createRequests(Vector<ReferenceType> refTypes) throws DebugException {
    try {
      for (int i = 0; i < refTypes.size(); i++) {
        ReferenceType rt = refTypes.get(i);
        
        if (!rt.isPrepared()) {
          
          continue;
        }
        
        
        List<Location> lines = rt.locationsOfLine(_manager.LLBreakpointLineNum(this));
        if (lines.size() == 0) {
          
          setEnabled(false);          
          throw new DebugException("Could not find line number: " + _lineNumber);
        }
        Location loc = lines.get(0);
        
        BreakpointRequest request = _manager.getEventRequestManager().createBreakpointRequest(loc);
        request.setEnabled(_isEnabled);
        _requests.add(request);
      }
    }
    catch (AbsentInformationException aie) { throw new DebugException("Could not find line number: " + aie); }
  }
  
  
  public int getStartOffset() { return _startPos.getOffset(); }
  
  
  public int getEndOffset() { return _endPos.getOffset(); }
  
  
  public int getLineStartOffset() { return _startPos.getOffset(); }
  
  
  public int getLineEndOffset() { return _endPos.getOffset(); }
  
  
  public void update() {
    try {  
      int offset = _position.getOffset();
      _startPos = _doc.createPosition(_doc._getLineStartPos(offset));
      _endPos = _doc.createPosition(_doc._getLineEndPos(offset));
      _lineNumber = _doc.getLineOfOffset(offset)+1; 
    }
    catch (BadLocationException ble) { throw new UnexpectedException(ble); }  
  }
  
  public boolean isEmpty() { update(); return getStartOffset() == getEndOffset(); }
    




  




  
  
  public final boolean equals(Object o) {
    if (o == null || ! (o instanceof IDocumentRegion)) return false;
    update(); 
    IDocumentRegion r = (IDocumentRegion) o;
    return getDocument() == r.getDocument() && getStartOffset() == r.getStartOffset() && getEndOffset() == r.getEndOffset();
  }
  
  
  public int compareTo(OrderedDocumentRegion r) {
    int docRel = getDocument().compareTo(r.getDocument());
    if (docRel != 0) return docRel;
    
    
    assert getDocument() == r.getDocument();  
    int end1 = getEndOffset();
    int end2 = r.getEndOffset();
    int endDiff = end1 - end2;
    if (endDiff != 0) return endDiff;
    
    int start1 = getStartOffset();
    int start2 = r.getStartOffset();
    return start1 - start2;
  }
  
  
  public int getLineNumber() {
    update();
    return _lineNumber;
  }
  
  

  
  
  public void setEnabled(boolean isEnabled) {
    assert EventQueue.isDispatchThread();
    boolean old = _isEnabled;
    super.setEnabled(isEnabled);
    try {
      for(BreakpointRequest bpr: _requests) {
        bpr.setEnabled(isEnabled);
      }
    }
    catch(VMDisconnectedException vmde) {  }
    if (_isEnabled!=old) _manager.notifyBreakpointChange(this);
  }
  
  public String toString() {
    String cn = getClassName();
    if (_exactClassName != null) { cn = _exactClassName.replace('$', '.'); }
    if (_requests.size() > 0) {
      
      
      return "Breakpoint[class: " + cn +
        ", lineNumber: " + getLineNumber() +
        ", method: " + _requests.get(0).location().method() +
        ", codeIndex: " + _requests.get(0).location().codeIndex() +
        ", numRefTypes: " + _requests.size() + "]";
    }
    else {
      return "Breakpoint[class: " + cn +
        ", lineNumber: " + getLineNumber() + "]";
    }
  }
}
