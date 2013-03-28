

package edu.rice.cs.drjava.model.debug;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.OptionConstants;
import java.util.StringTokenizer;

import com.sun.jdi.*;
import com.sun.jdi.request.*;


public class Step extends DebugAction<StepRequest> implements OptionConstants {
  private ThreadReference _thread;
  private int _size;
  private int _depth;

  
  private String[] javaExcludes = {"java.*", "javax.*", "sun.*", "com.sun.*", "com.apple.mrj.*"};

  
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
    String exclude = DrJava.getConfig().getSetting(DEBUG_STEP_EXCLUDE);

    StepRequest request = _manager.getEventRequestManager().
      createStepRequest(_thread, _size, _depth);
    if (!stepJava) {
      for (int i=0; i < javaExcludes.length; i++) {
        request.addClassExclusionFilter(javaExcludes[i]);
      }
    }
    if (!stepInterpreter) {
      request.addClassExclusionFilter("koala.*");
    }
    if (!stepDrJava) {
      request.addClassExclusionFilter("edu.rice.cs.drjava.*");
      request.addClassExclusionFilter("edu.rice.cs.util.*");
    }
    StringTokenizer st = new StringTokenizer(exclude, ",");
    while (st.hasMoreTokens()) {
      request.addClassExclusionFilter(st.nextToken().trim());
    }


    
    _requests.add(request);
  }

  public String toString() {
    return "Step[thread: " + _thread +  "]";
  }
}
