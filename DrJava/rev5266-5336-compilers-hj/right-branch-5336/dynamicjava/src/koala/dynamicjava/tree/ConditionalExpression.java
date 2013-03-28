

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.*;



public class ConditionalExpression extends Expression {
  
  private Expression conditionExpression;
  
  
  private Expression ifTrueExpression;
  
  
  private Expression ifFalseExpression;
  
  
  public ConditionalExpression(Expression cexp, Expression texp, Expression fexp) {
    this(cexp, texp, fexp, SourceInfo.NONE);
  }
  
  
  public ConditionalExpression(Expression cexp, Expression texp, Expression fexp,
                               SourceInfo si) {
    super(si);
    
    if (cexp == null) throw new IllegalArgumentException("cexp == null");
    if (texp == null) throw new IllegalArgumentException("texp == null");
    if (fexp == null) throw new IllegalArgumentException("fexp == null");
    
    conditionExpression  = cexp;
    ifTrueExpression     = texp;
    ifFalseExpression    = fexp;
  }
  
  
  public Expression getConditionExpression() {
    return conditionExpression;
  }
  
  
  public void setConditionExpression(Expression e) {
    if (e == null) throw new IllegalArgumentException("e == null");
    conditionExpression = e;
  }
  
  
  public Expression getIfTrueExpression() {
    return ifTrueExpression;
  }
  
  
  public void setIfTrueExpression(Expression e) {
    if (e == null) throw new IllegalArgumentException("e == null");
    ifTrueExpression = e;
  }
  
  
  public Expression getIfFalseExpression() {
    return ifFalseExpression;
  }
  
  
  public void setIfFalseExpression(Expression e) {
    if (e == null) throw new IllegalArgumentException("e == null");
    ifFalseExpression = e;
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
   
  public String toString() {
    return "("+getClass().getName()+": "+getConditionExpression()+" "+getIfTrueExpression()+" "+getIfFalseExpression()+")";
  }
}
