

package edu.rice.cs.drjava.model.junit;

import edu.rice.cs.drjava.model.compiler.CompilerError;


import java.io.File;
import java.io.Serializable;


public class JUnitError extends CompilerError implements Comparable, Serializable {
  private String _test;
  private String _className;
  private String _stackTrace;
  
  
  public JUnitError(File file, int lineNumber, int startColumn, String message,
                    boolean isWarning, String test, String className, String stackTrace) {
    super(file, lineNumber, startColumn, message, isWarning);
    _test = test;
    _className = className;
    _stackTrace = stackTrace;
  }

  
  public JUnitError(String message, boolean isWarning, String test) {
    this(null, -1, -1, message, isWarning, test, "", "No associated stack trace");
  }

  
  public String testName() {
    return _test;
  }

  
  public String className() {
    return _className;
  }

  
  public String stackTrace() {
    return _stackTrace;
  }
  
}