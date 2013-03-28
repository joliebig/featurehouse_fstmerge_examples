

package koala.dynamicjava.tree;



public abstract class BinaryExpression extends Expression {
  
  private Expression leftExpression;
  
  
  private Expression rightExpression;
  
  
  protected BinaryExpression(Expression lexp, Expression rexp,
                             SourceInfo si) {
    super(si);
    
    if (lexp == null) throw new IllegalArgumentException("lexp == null");
    if (rexp == null) throw new IllegalArgumentException("rexp == null");
    
    leftExpression  = lexp;
    rightExpression = rexp;
  }
  
  
  public Expression getLeftExpression() {
    return leftExpression;
  }
  
  
  public void setLeftExpression(Expression exp) {
    if (exp == null) throw new IllegalArgumentException("exp == null");
    leftExpression = exp;
  }
  
  
  public Expression getRightExpression() {
    return rightExpression;
  }
  
  
  public void setRightExpression(Expression exp) {
    if (exp == null) throw new IllegalArgumentException("exp == null");
    rightExpression = exp;
  }
   
  public String toString() {
    return "("+getClass().getName()+": "+getLeftExpression()+" "+getRightExpression()+")";
  }
}
