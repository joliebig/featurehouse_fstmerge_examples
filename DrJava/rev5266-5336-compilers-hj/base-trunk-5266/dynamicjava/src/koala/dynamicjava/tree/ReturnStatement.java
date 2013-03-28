

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.*;



public class ReturnStatement extends Statement implements ExpressionContainer {
  
  private Expression expression;
  
  
  public ReturnStatement(Expression exp) {
    this(exp, SourceInfo.NONE);
  }
  
  
  public ReturnStatement(Expression exp,
                         SourceInfo si) {
    super(si);
    expression = exp;
  }
  
  
  public Expression getExpression() {
    return expression;
  }
  
  
  public void setExpression(Expression e) {
    expression = e;
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }  
    
  public String toString() {
    return "("+getClass().getName()+": "+getExpression()+")";
  }
}
