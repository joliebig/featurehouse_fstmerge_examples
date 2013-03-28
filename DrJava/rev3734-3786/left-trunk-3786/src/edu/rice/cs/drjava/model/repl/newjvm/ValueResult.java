

package edu.rice.cs.drjava.model.repl.newjvm;


public class ValueResult implements InterpretResult {
  private final String _valueStr;
  private final String _style;

  public ValueResult(final String valueStr, final String style) {
    _valueStr = valueStr;
    _style = style;
  }
    
  public String getValueStr() {
    return _valueStr;
  }
  
  public String getStyle() {
    return _style;
  }

  public <T> T apply(InterpretResultVisitor<T> v) {
    return v.forValueResult(this);
  }

  public String toString() { return "(value: " + _valueStr + "; style: " + _style + ")"; }
}
