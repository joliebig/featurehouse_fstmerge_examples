

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.*;



public class PreDecrement extends UnaryExpression implements StatementExpression {
  
  public PreDecrement(Expression exp) {
    this(exp, SourceInfo.NONE);
  }
  
  
  public PreDecrement(Expression exp, SourceInfo si) {
    super(exp, si);
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
 
}
