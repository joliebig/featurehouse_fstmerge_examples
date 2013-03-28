

package koala.dynamicjava.util;

import java.lang.reflect.Method;


public class AmbiguousMethodException extends RuntimeException {
  
  @SuppressWarnings("unused") private Method[] _methods;
  
  
  public AmbiguousMethodException(String e) {
    super(e);
    _methods = new Method[0];
  }
  public AmbiguousMethodException(Method m1, Method m2) {
    super("Both methods match:" + m1 + ", and " + m2);
    _methods = new Method[]{m1,m2};
  }
}