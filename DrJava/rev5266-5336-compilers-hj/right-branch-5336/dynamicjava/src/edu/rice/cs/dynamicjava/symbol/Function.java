package edu.rice.cs.dynamicjava.symbol;

import edu.rice.cs.dynamicjava.symbol.type.Type;
import edu.rice.cs.dynamicjava.symbol.type.VariableType;


public interface Function {
  public String declaredName();
  public Iterable<VariableType> typeParameters();
  public Iterable<LocalVariable> parameters();
  public Type returnType();
  public Iterable<Type> thrownTypes();
}
