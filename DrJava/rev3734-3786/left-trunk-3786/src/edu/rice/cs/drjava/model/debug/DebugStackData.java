

package edu.rice.cs.drjava.model.debug;

import com.sun.jdi.*;


public class DebugStackData {
  private String _method;
  private int _line;
  
  
  public DebugStackData(StackFrame frame) {
    
    
    _method = frame.location().declaringType().name() + "." +
      frame.location().method().name();
    
    _line = frame.location().lineNumber();
  }
  
  
  public String getMethod() {
    return _method;
  }
  
  
  public int getLine() {
    return _line;
  }
}
