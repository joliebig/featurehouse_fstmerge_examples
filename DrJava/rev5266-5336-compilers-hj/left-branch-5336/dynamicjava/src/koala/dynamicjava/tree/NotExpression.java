

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.*;



public class NotExpression extends UnaryExpression {
  
  public NotExpression(Expression exp) {
    this(exp, SourceInfo.NONE);
  }
  
  
  public NotExpression(Expression exp, SourceInfo si) {
    super(exp, si);
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }

}
