

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.Visitor;



public class ByteTypeName extends PrimitiveTypeName {
  
  public ByteTypeName() {
    this(SourceInfo.NONE);
  }
  
  
  public ByteTypeName(SourceInfo si) {
    super(byte.class, si);
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
  
}
