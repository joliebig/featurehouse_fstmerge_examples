

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.*;



public class ObjectFieldAccess extends FieldAccess implements ExpressionContainer {
  
  private Expression expression;
  
  
  public ObjectFieldAccess(Expression exp, String fln) {
    this(exp, fln, SourceInfo.NONE);
  }
  
  
  public ObjectFieldAccess(Expression exp, String fln, 
                           SourceInfo si) {
    super(fln, si);
    
    if (exp == null) throw new IllegalArgumentException("exp == null");
    
    expression = exp;
  }
  
  
  public Expression getExpression() {
    return expression;
  }
  
  
  public void setExpression(Expression e) {
    if (e == null) throw new IllegalArgumentException("e == null");
    expression = e;
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
    
  public String toString() {
    return "("+getClass().getName()+": "+getFieldName()+" "+getExpression()+")";
  }
}
