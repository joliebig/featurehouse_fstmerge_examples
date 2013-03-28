

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.*;



public class PostIncrement extends UnaryExpression implements StatementExpression {
  
  public PostIncrement(Expression exp) {
    this(exp, SourceInfo.NONE);
  }
  
  
  public PostIncrement(Expression exp, SourceInfo si) {
    super(exp, si);
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
 
}
