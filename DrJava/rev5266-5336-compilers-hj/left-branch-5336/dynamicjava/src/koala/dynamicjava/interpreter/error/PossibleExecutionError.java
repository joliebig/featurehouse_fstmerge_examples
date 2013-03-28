

package koala.dynamicjava.interpreter.error;

import koala.dynamicjava.tree.*;


public class PossibleExecutionError extends ExecutionError {
  private ReferenceTypeName referenceType;
  
  
  
  public PossibleExecutionError(String s, Node n, ReferenceTypeName rt) {
    super(s, n);
    referenceType = rt;
  }
  
  public ReferenceTypeName getReferenceType() { return referenceType; }
}