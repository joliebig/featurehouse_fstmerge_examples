   

package edu.rice.cs.drjava.model.repl.newjvm;

import java.io.Serializable;


public interface InterpretResult extends Serializable {
  public <T> T apply(InterpretResultVisitor<T> v);
}
