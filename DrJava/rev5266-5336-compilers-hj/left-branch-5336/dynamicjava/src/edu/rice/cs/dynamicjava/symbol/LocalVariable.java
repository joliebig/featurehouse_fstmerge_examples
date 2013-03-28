package edu.rice.cs.dynamicjava.symbol;

import edu.rice.cs.dynamicjava.symbol.type.Type;


public class LocalVariable implements Variable {
  private final String _name;
  private final Type _type;
  private final boolean _isFinal;
  
  
  public LocalVariable(String name, Type type, boolean isFinal) {
    _name = name;
    _type = type;
    _isFinal = isFinal;
  }
  
  public String declaredName() { return _name; }
  
  public Type type() { return _type; }
  
  public boolean isFinal() { return _isFinal; }
  
  public String toString() {
    return "LocalVariable(" + _name + ": " + _type + ")@" + Integer.toHexString(hashCode());
  }
}
