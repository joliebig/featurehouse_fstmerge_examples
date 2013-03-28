

package koala.dynamicjava.interpreter.error;

import koala.dynamicjava.tree.*;



public class ReturnException extends ExecutionError {
  
  private boolean withValue;
  
  
  private Object value;
  
  
  public ReturnException(String s, Node n) {
    super(s, n);
    withValue = false;
  }
  
  
  public ReturnException(String s, Object o, Node n) {
    super(s, n);
    withValue = true;
    value = o;
  }
  
  
  public Object getValue() {
    return value;
  }
  
  
  public boolean hasValue() {
    return withValue;
  }
}
