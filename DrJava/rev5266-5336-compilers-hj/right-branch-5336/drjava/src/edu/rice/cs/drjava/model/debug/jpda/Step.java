

package edu.rice.cs.drjava.model.debug.jpda;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.model.debug.DebugException;

import java.util.StringTokenizer;

import com.sun.jdi.*;
import com.sun.jdi.request.*;


public class Step extends DebugAction<StepRequest> implements OptionConstants {
  private final ThreadReference _thread;
  private final int _size;
  private final int _depth;

  
  private final String[] _javaExcludes = {"java.*", "javax.*", "sun.*", "com.sun.*", "com.apple.eawt.*", "com.apple.eio.*" };

  
  public Step(JPDADebugger manager, int size, int depth)
    throws DebugException, IllegalStateException {
     super (manager);
    _suspendPolicy = EventRequest.SUSPEND_EVENT_THREAD;
    _thread = _manager.getCurrentThread();
    _size = size;
    _depth = depth;
    _countFilter = 1; 
    _initializeRequests();
  }

  
  
  

  
  protected void _createRequests() throws DebugException {
    boolean stepJava = DrJava.getConfig().getSetting(DEBUG_STEP_JAVA).booleanValue();
    boolean stepInterpreter = DrJava.getConfig().getSetting(DEBUG_STEP_INTERPRETER).booleanValue();
    boolean stepDrJava = DrJava.getConfig().getSetting(DEBUG_STEP_DRJAVA).booleanValue();

    StepRequest request = _manager.getEventRequestManager().
      createStepRequest(_thread, _size, _depth);
    if (!stepJava) {
      for (int i = 0; i < _javaExcludes.length; i++) {
        request.addClassExclusionFilter(_javaExcludes[i]);
      }
    }
    if (!stepInterpreter) {
      request.addClassExclusionFilter("koala.*");
      request.addClassExclusionFilter("edu.rice.cs.dynamicjava.*");
    }
    if (!stepDrJava) {
      request.addClassExclusionFilter("edu.rice.cs.drjava.*");
      request.addClassExclusionFilter("edu.rice.cs.util.*");
      request.addClassExclusionFilter("edu.rice.cs.plt.*");
    }
    for(String s: DrJava.getConfig().getSetting(DEBUG_STEP_EXCLUDE)) {
      request.addClassExclusionFilter(s.trim());
    }

    
    _requests.add(request);
  }

  public String toString() { return "Step[thread: " + _thread +  "]"; }
}
