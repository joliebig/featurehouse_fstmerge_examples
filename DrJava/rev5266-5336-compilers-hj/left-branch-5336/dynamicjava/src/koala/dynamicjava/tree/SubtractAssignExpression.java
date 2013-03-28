

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.*;



public class SubtractAssignExpression extends AssignExpression {
  
  public SubtractAssignExpression(Expression lexp, Expression rexp) {
    this(lexp, rexp, SourceInfo.NONE);
  }
  
  
  public SubtractAssignExpression(Expression lexp, Expression rexp,
                                  SourceInfo si) {
    super(lexp, rexp, si);
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
  
}
