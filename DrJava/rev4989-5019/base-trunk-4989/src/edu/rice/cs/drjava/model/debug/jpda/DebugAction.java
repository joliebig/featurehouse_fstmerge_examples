

package edu.rice.cs.drjava.model.debug.jpda;

import com.sun.jdi.request.*;
import java.util.Vector;
import edu.rice.cs.drjava.model.debug.DebugException;


public abstract class DebugAction<T extends EventRequest> {
  public static final int ANY_LINE = -1;

  protected final JPDADebugger _manager;

  

  
  protected final Vector<T> _requests;
  protected volatile int _suspendPolicy = EventRequest.SUSPEND_NONE;
  protected volatile boolean _isEnabled = true;
  protected volatile int _countFilter = -1;
  protected volatile int _lineNumber = ANY_LINE;

  
  public DebugAction(JPDADebugger manager) {
    _manager = manager;
    _requests = new Vector<T>();
  }

  
  public Vector<T> getRequests() { return _requests; }

  
  public int getLineNumber() { return _lineNumber; }

  
  

  public boolean createRequests() throws DebugException {
    _createRequests();
    if (_requests.size() > 0) {
      _prepareRequests(_requests);
      return true;
    }
    else return false;
  }

  
  protected void _initializeRequests() throws DebugException {
    createRequests();
    if (_requests.size() == 0) {
      throw new DebugException("Could not create EventRequests for this action!");
    }
  }

  
  protected void _createRequests() throws DebugException { }

  
  protected void _prepareRequests(Vector<T> requests) {
    for (int i=0; i < requests.size(); i++) {
      _prepareRequest(requests.get(i));
    }
  }

  
  protected void _prepareRequest(T request) {
    
    request.setEnabled(false);

    if (_countFilter != -1) {
      request.addCountFilter(_countFilter);
    }
    request.setSuspendPolicy(_suspendPolicy);
    request.setEnabled(_isEnabled);

    
    request.putProperty("debugAction", this);
  }
  
  
  public boolean isEnabled() { return _isEnabled; }
  
  
  
  public void setEnabled(boolean isEnabled) { _isEnabled = isEnabled; }
}
