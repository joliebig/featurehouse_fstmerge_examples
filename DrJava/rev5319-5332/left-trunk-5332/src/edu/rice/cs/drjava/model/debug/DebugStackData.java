

package edu.rice.cs.drjava.model.debug;


public class DebugStackData {
  private final String _method;
  private final int _line;
  
  
  public DebugStackData(String method, int line) {
    _method = method;
    _line = line;
  }
  
  
  public String getMethod() { return _method; }
  
  
  public int getLine() { return _line; }
}
