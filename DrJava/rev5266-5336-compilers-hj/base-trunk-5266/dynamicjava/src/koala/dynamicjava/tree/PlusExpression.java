

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.*;



public class PlusExpression extends UnaryExpression {
  
  public PlusExpression(Expression exp) {
    this(exp, SourceInfo.NONE);
  }
  
  
  public PlusExpression(Expression exp, SourceInfo si) {
    super(exp, si);
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
 
}
