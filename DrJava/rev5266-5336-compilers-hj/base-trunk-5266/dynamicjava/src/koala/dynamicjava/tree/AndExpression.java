

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.*;



public class AndExpression extends BinaryExpression {
  
  public AndExpression(Expression lexp, Expression rexp) {
    this(lexp, rexp, SourceInfo.NONE);
  }
  
  
  public AndExpression(Expression lexp, Expression rexp, SourceInfo si) {
    super(lexp, rexp, si);
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
   
  public String toString() {
    return "("+getClass().getName()+": "+getLeftExpression()+" "+getRightExpression()+")";
  }
}
