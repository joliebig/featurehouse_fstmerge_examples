

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.*;



public class ExpressionStatement extends Statement implements ExpressionContainer {
  
  
  private Expression expression;
  
  
  private boolean hasSemicolon;
    
  
  public ExpressionStatement(Expression exp, boolean hasSemi) {
    this(exp, hasSemi, SourceInfo.NONE);
  }
  
  
  public ExpressionStatement(Expression exp, boolean hasSemi, SourceInfo si) {
    super(si);
    if (exp == null)  throw new IllegalArgumentException("exp == null");
    expression    = exp;
    hasSemicolon  = hasSemi;
  }
  
  public Expression getExpression() {
    return expression;
  }
  
  public boolean getHasSemicolon() {
    return hasSemicolon;
  }
  
  public void setExpression(Expression e) {
    if (e == null) throw new IllegalArgumentException("e == null");
    expression = e;
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
  
   
  public String toString() {
    return "(" + getClass().getName() + ": " + getExpression() + ", " + getHasSemicolon() + ")";
  }
  
}
