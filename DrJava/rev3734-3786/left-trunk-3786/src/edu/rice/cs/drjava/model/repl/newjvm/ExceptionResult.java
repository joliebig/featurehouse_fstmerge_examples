

package edu.rice.cs.drjava.model.repl.newjvm;


public class ExceptionResult implements InterpretResult {
  private final String _exceptionClass;
  private final String _exceptionMessage;
  private final String _stackTrace;
  private final String _specialMessage;

  public ExceptionResult(final String exceptionClass,
                         final String exceptionMessage,
                         final String stackTrace,
                         final String specialMessage)
  {
    _exceptionClass = exceptionClass;
    _exceptionMessage = exceptionMessage;
    _stackTrace = stackTrace;
    _specialMessage = specialMessage;
  }

  public String getExceptionClass() {
    return _exceptionClass;
  }

  public String getExceptionMessage() {
    return _exceptionMessage;
  }

  public String getStackTrace() {
    return _stackTrace;
  }
  
  public String getSpecialMessage() {
    return _specialMessage;
  }

  public <T> T apply(InterpretResultVisitor<T> v) {
    return v.forExceptionResult(this);
  }

  public String toString() { return "(exception: " + _exceptionClass + ")"; }
}
