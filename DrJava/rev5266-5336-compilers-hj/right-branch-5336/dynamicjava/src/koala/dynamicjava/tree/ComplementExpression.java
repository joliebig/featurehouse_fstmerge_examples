

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.*;



public class ComplementExpression extends UnaryExpression {
  
  public ComplementExpression(Expression exp) {
    this(exp, SourceInfo.NONE);
  }
  
  
  public ComplementExpression(Expression exp,
                              SourceInfo si) {
    super(exp, si);
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }

}
