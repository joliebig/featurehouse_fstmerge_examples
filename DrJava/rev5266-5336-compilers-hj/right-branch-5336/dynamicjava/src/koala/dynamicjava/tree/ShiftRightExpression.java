

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.*;



public class ShiftRightExpression extends BinaryExpression {
  
  public ShiftRightExpression(Expression lexp, Expression rexp) {
    this(lexp, rexp, SourceInfo.NONE);
  }
  
  
  public ShiftRightExpression(Expression lexp, Expression rexp,
                              SourceInfo si) {
    super(lexp, rexp, si);
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }

}
