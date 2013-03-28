

package edu.rice.cs.drjava.model.debug;


public class DebugException extends Exception {
  public DebugException() {
    super();
  }
  
  public DebugException(String desc) {
    super(desc);
  }
}