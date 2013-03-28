

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.*;



public class PostDecrement extends UnaryExpression implements StatementExpression {
  
  public PostDecrement(Expression exp) {
    this(exp, SourceInfo.NONE);
  }
  
  
  public PostDecrement(Expression exp, SourceInfo si) {
    super(exp, si);
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
  
}
