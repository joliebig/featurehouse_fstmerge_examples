

package koala.dynamicjava.util;

import koala.dynamicjava.tree.MethodDeclaration;


public class AmbiguousFunctionException extends RuntimeException {
  
  @SuppressWarnings("unused") private MethodDeclaration[] _methods;
  
  
  public AmbiguousFunctionException(String e) {
    super(e);
    _methods = new MethodDeclaration[0];
  }
  public AmbiguousFunctionException(MethodDeclaration m1, MethodDeclaration m2) {
    super("Both functions match:" + m1 + ", and " + m2);
    _methods = new MethodDeclaration[]{m1,m2};
  }
}