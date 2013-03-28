

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.*;



public class DivideExpression extends BinaryExpression {
  
  public DivideExpression(Expression lexp, Expression rexp) {
    this(lexp, rexp, SourceInfo.NONE);
  }
  
  
  public DivideExpression(Expression lexp, Expression rexp,
                          SourceInfo si) {
    super(lexp, rexp, si);
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
  
}
