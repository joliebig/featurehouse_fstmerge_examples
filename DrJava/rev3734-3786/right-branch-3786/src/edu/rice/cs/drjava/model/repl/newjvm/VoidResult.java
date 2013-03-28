

package edu.rice.cs.drjava.model.repl.newjvm;


public class VoidResult implements InterpretResult {
  public <T> T apply(InterpretResultVisitor<T> v) {
    return v.forVoidResult(this);
  }

  public String toString() { return "(void)"; }
}
