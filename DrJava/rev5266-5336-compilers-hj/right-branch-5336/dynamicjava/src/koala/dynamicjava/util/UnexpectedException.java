

package koala.dynamicjava.util;


public class UnexpectedException extends RuntimeException {

  private Throwable _value;

   
  public UnexpectedException(Throwable value) {
    super(value.toString());
    _value = value;
  }

  
  public UnexpectedException(Throwable value, String msg) {
    super(msg + ": " + value.toString());
    _value = value;
  }
  
  
  public UnexpectedException() {
    this(new RuntimeException("Unreachable point in code has been reached!"));
  }

  
  public Throwable getCause() { return _value; }
}
