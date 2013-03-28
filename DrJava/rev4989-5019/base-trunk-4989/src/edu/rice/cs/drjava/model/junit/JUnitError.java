

package edu.rice.cs.drjava.model.junit;

import edu.rice.cs.drjava.model.DJError;

import java.io.File;
import java.io.Serializable;


public class JUnitError extends DJError implements Serializable {
  private String _test;
  private String _className;
  private String _exception;
  private StackTraceElement[] _stackTrace;
  
  
  public JUnitError(File file, int lineNumber, int startColumn, String message, boolean isWarning, String test, 
                    String className, String exception, StackTraceElement[] stackTrace) {
    super(file, lineNumber, startColumn, message, isWarning);
    _test = test;
    _className = className;
    _exception = exception;
    _stackTrace = stackTrace;
  }

  
  public JUnitError(String message, boolean isWarning, String test) {
    this(null, -1, -1, message, isWarning, test, "", "No associated stack trace", new StackTraceElement[0]);
  }

  
  public String testName() { return _test; }

  
  public String className() { return _className; }

  
  public String exception() { return _exception; }
  
  
  public StackTraceElement[] stackTrace() { return _stackTrace; }
  
  
  public void setStackTrace(StackTraceElement[] stes) { _stackTrace = stes; }
  
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(_exception);
    
    for(StackTraceElement s: _stackTrace) {
      sb.append('\n');
      sb.append("\tat ");
      sb.append(s);
    }
    return sb.toString();
  }
}