

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.*;



public class PreIncrement extends UnaryExpression implements StatementExpression {
  
  public PreIncrement(Expression exp) {
    this(exp, SourceInfo.NONE);
  }
  
  
  public PreIncrement(Expression exp, SourceInfo si) {
    super(exp, si);
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
  
}
