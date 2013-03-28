

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.*;



public class MinusExpression extends UnaryExpression {
  
  public MinusExpression(Expression exp) {
    this(exp, SourceInfo.NONE);
  }
  
  
  public MinusExpression(Expression exp, SourceInfo si) {
    super(exp, si);
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
 
}
