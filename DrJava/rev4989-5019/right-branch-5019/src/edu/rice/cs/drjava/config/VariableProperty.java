

package edu.rice.cs.drjava.config;


public class VariableProperty extends ConstantProperty {
  
  public VariableProperty(String name, String value) {
    super(name, value, "User-defined variable.");
  }
  
  
  public void setValue(String value) {
    _value = value;
  }
} 
