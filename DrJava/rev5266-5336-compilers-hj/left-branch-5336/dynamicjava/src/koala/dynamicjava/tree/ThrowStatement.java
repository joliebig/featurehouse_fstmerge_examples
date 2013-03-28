

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.*;



public class ThrowStatement extends Statement implements ExpressionContainer {
  
  private Expression expression;
  
  
  public ThrowStatement(Expression exp) {
    this(exp, SourceInfo.NONE);
  }
  
  
  public ThrowStatement(Expression exp,
                        SourceInfo si) {
    super(si);
    
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
    return "("+getClass().getName()+": "+getExpression()+")";
  }
}
