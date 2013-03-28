

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.Visitor;

public class VariableAccess extends PrimaryExpression implements LeftHandSide {
  
  private String variableName;
  
  
  public VariableAccess(String varName) {
    this(varName, SourceInfo.NONE);
  }
  
  
  public VariableAccess(String varName, SourceInfo si) {
    super(si);
    
    if (varName == null) throw new IllegalArgumentException("Null variable name in VariableAccess construction");
    
    variableName  = varName;
  }
  
  
  public String getVariableName() {
    return variableName;
  }
  
  
  public void setVariableName(String s) {
    if (s == null) throw new IllegalArgumentException("Null variable name in VariableAccess update");
    variableName = s;
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
  
    
  public String toString() {
    return "("+getClass().getName()+": "+getVariableName()+")";
  }
  
}
