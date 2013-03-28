

package edu.rice.cs.drjava.model.compiler;

import java.io.File;

import edu.rice.cs.drjava.model.DJError;



public class CompilerError extends DJError {
  
  
  public CompilerError(File file, int lineNumber, int startColumn, String message, boolean isWarning) { 
    super(file, lineNumber, startColumn, message, isWarning);
  }
  
  
  public CompilerError(File file, String message, boolean isWarning) { super(file, message, isWarning); }
  
  
  public CompilerError(String message, boolean isWarning) { super(message, isWarning); }
}
  