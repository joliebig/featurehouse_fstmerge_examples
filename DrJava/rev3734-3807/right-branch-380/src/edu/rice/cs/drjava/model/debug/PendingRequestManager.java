

package edu.rice.cs.drjava.model.debug;

import com.sun.jdi.*;
import com.sun.jdi.request.*;
import com.sun.jdi.event.*;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import java.io.*;



public class PendingRequestManager {
  private JPDADebugger _manager;
  private Hashtable<String, Vector<DocumentDebugAction<?>>> _pendingActions;

  public PendingRequestManager(JPDADebugger manager) {
    _manager = manager;
    _pendingActions = new Hashtable<String, Vector<DocumentDebugAction<?>>>();
  }

  
  public void addPendingRequest (DocumentDebugAction<?> action) {
    String className = action.getClassName();
    Vector<DocumentDebugAction<?>> actions = _pendingActions.get(className);
    if (actions == null) {
      actions = new Vector<DocumentDebugAction<?>>();

      
      ClassPrepareRequest request =
        _manager.getEventRequestManager().createClassPrepareRequest();
      
      request.addClassFilter(className + "*");
      request.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
      request.enable();
      
    }
    actions.add(action);
    _pendingActions.put(className, actions);
  }

  
  public void removePendingRequest (DocumentDebugAction<?> action) {
    String className = action.getClassName();
    Vector<DocumentDebugAction<?>> actions = _pendingActions.get(className);
    if (actions == null) {
      return;
    }
    actions.remove(action);
    
    if (actions.size() == 0) {
      _pendingActions.remove(className);
    }
  }

  
  private boolean recursiveFindLineNumber(int lineNumber, ReferenceType rt) {
    try {
      for(Location l: rt.allLineLocations()) {
        if (l.lineNumber()==lineNumber) { return true; }
      }
      for(ReferenceType nested: rt.nestedTypes()) {
        if (recursiveFindLineNumber(lineNumber, nested)==true) { return true; }
      }
    }
    catch (AbsentInformationException aie) {
      
    }
    
    return false;
  }
  
  
  public void classPrepared (ClassPrepareEvent event) throws DebugException {
    ReferenceType rt = event.referenceType();
    
    
    
    String className = rt.name();

    
    int indexOfDollar = className.indexOf('$');
    if (indexOfDollar > 1) {
      className = className.substring(0, indexOfDollar);
    }

    
    Vector<DocumentDebugAction<?>> actions = _pendingActions.get(className);
    Vector<DocumentDebugAction<?>> failedActions =
      new Vector<DocumentDebugAction<?>>();
    
    if (actions == null) {
      
      
      return;
    }
    else if (actions.isEmpty()) {
      
      
      _manager.getEventRequestManager().deleteEventRequest(event.request());
      return;
    }
    for (int i = 0; i < actions.size(); i++) {
      DocumentDebugAction<?> a = actions.get(i);
      int lineNumber = a.getLineNumber();
      if (lineNumber != DebugAction.ANY_LINE) {
        try {
          List lines = rt.locationsOfLine(lineNumber);
          if (lines.size() == 0) {
            
            String exactClassName = a.getExactClassName();
            if ((exactClassName!=null) && (exactClassName.equals(rt.name()))) {
              _manager.printMessage(actions.get(i).toString()+" not on an executable line; disabled.");
              actions.get(i).setEnabled(false);
            }

            
            continue;
          }
        }
        catch (AbsentInformationException aie) {
          
          continue;
        }
      }
      
      try {
        Vector<ReferenceType> refTypes = new Vector<ReferenceType>();
        refTypes.add(rt);
        a.createRequests(refTypes);  
      }
      catch (DebugException e) {
        failedActions.add(a);
        
      }
    }

    
    
    if (failedActions.size() > 0) {
      
      throw new DebugException("Failed actions: " + failedActions);
    }
  }
}
