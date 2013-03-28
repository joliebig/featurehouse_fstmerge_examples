

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.Visitor;



public class BooleanTypeName extends PrimitiveTypeName {
  
  public BooleanTypeName() {
    this(SourceInfo.NONE);
  }
  
  
  public BooleanTypeName(SourceInfo si) {
    super(boolean.class, si);
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
  
}
