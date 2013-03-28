

package edu.rice.cs.drjava.model.debug.jpda;

import com.sun.jdi.StackFrame;
import edu.rice.cs.drjava.model.debug.DebugStackData;


public class JPDAStackData extends DebugStackData {
  
  public JPDAStackData(StackFrame frame) {
    super(methodName(frame), frame.location().lineNumber());
  }

  
  public JPDAStackData(String method, int lineNum) {
    super(method, lineNum);
  }
  
  
  public static String methodName(StackFrame frame) {
    return frame.location().declaringType().name() + "." + frame.location().method().name();
  }
}
