

package edu.rice.cs.drjava.model.debug;

import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;

import java.util.Vector;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.Position;

import com.sun.jdi.*;
import com.sun.jdi.request.*;

import java.io.*;


public class Breakpoint extends DocumentDebugAction<BreakpointRequest> implements DebugBreakpointData {

   private Position _startPos;
   private Position _endPos;

  
  public Breakpoint(OpenDefinitionsDocument doc, int offset, int lineNumber, boolean enabled, JPDADebugger manager)
    throws DebugException {

    super(manager, doc, offset);
    _suspendPolicy = EventRequest.SUSPEND_EVENT_THREAD;
    _lineNumber = lineNumber;
    _enabled = enabled;

    try {
      _startPos = doc.createPosition(doc.getLineStartPos(offset));
      _endPos = doc.createPosition( doc.getLineEndPos(offset));
    }
    catch (BadLocationException ble) {
      throw new UnexpectedException(ble);
    }

    if ((_manager!=null) && (_manager.isReady())) {
      
      
      Vector<ReferenceType> refTypes = _manager.getReferenceTypes(_className, _lineNumber);
      _initializeRequests(refTypes);
      setEnabled(enabled);
    }
  }
  
  
  protected void _createRequests(Vector<ReferenceType> refTypes) throws DebugException {
    try {
        for (ReferenceType rt : refTypes) {
            if (!rt.isPrepared()) {
                
                continue;
            }

            
            List lines = rt.locationsOfLine(_lineNumber);
            if (lines.size() == 0) {
                
                setEnabled(false);
                throw new DebugException("Could not find line number: " + _lineNumber);
            }
            Location loc = (Location) lines.get(0);

            BreakpointRequest request = _manager.getEventRequestManager().createBreakpointRequest(loc);
            request.setEnabled(_enabled);
            _requests.add(request);
        }
    }
    catch (AbsentInformationException aie) {
      throw new DebugException("Could not find line number: " + aie);
    }
  }

  
  public int getStartOffset() {
    return _startPos.getOffset();
  }

  
  public int getEndOffset() {
    return _endPos.getOffset();
  }
  
  
  public void setEnabled(boolean enabled) {
    boolean old = _enabled;
    super.setEnabled(enabled);
    try {
      for(BreakpointRequest bpr: _requests) {
        bpr.setEnabled(enabled);
      }
    }
    catch(VMDisconnectedException vmde) {  }
    if (_enabled!=old) _manager._notifier.breakpointChanged(this);
  }

  public String toString() {
    if (_requests.size() > 0) {
      
      
      return "Breakpoint[class: " + getClassName() +
        ", lineNumber: " + getLineNumber() +
        ", method: " + _requests.get(0).location().method() +
        ", codeIndex: " + _requests.get(0).location().codeIndex() +
        ", numRefTypes: " + _requests.size() + "]";
    }
    else {
      return "Breakpoint[class: " + getClassName() +
        ", lineNumber: " + getLineNumber() + "]";
    }
  }
}
