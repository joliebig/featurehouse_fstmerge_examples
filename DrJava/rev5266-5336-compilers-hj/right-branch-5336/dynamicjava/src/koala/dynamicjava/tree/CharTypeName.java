

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.Visitor;



public class CharTypeName extends PrimitiveTypeName {
  
  public CharTypeName() {
    this(SourceInfo.NONE);
  }
  
  
  public CharTypeName(SourceInfo si) {
    super(char.class, si);
  }

  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
  
}
