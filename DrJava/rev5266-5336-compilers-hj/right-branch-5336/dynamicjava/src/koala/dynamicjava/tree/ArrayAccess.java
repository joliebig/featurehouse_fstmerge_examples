

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.*;



public class ArrayAccess extends PrimaryExpression implements LeftHandSide,
  ExpressionContainer {
  
  
  private Expression expression;
  
  
  private Expression cellNumber;
  
  
  public ArrayAccess(Expression exp, Expression cell) {
    this(exp, cell, SourceInfo.NONE);
  }
  
  
  public ArrayAccess(Expression exp, Expression cell,
                     SourceInfo si) {
    super(si);
    
    if (exp == null)  throw new IllegalArgumentException("exp == null");
    if (cell == null) throw new IllegalArgumentException("cell == null");
    
    expression = exp;
    cellNumber = cell;
  }
  
  
  public Expression getExpression() {
    return expression;
  }
  
  
  public void setExpression(Expression e) {
    if (e == null)  throw new IllegalArgumentException("e == null");
    expression = e;
  }
  
  
  public Expression getCellNumber() {
    return cellNumber;
  }
  
  
  public void setCellNumber(Expression e) {
    if (e == null)  throw new IllegalArgumentException("e == null");
    cellNumber = e;
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
  
   
  public String toString() {
    return "("+getClass().getName()+": "+getExpression()+" "+getCellNumber()+")";
  }
}
