

package edu.rice.cs.drjava.model.repl;


public class ExceptionReturnedException extends Exception {
  private final Throwable _contained;

  public ExceptionReturnedException(Throwable t) {
    super(t.toString());
    _contained = t;
  }

  public Throwable getContainedException() { return _contained; }
}
