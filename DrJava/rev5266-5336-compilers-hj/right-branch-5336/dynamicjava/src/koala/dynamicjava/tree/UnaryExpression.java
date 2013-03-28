

package koala.dynamicjava.tree;



public abstract class UnaryExpression extends Expression implements ExpressionContainer {
  
  private Expression expression;

  
  protected UnaryExpression(Expression exp,
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
     
  public String toString() {
    return "("+getClass().getName()+": "+getExpression()+")";
  }
}
