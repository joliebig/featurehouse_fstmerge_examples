

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.Visitor;



public class ShortTypeName extends PrimitiveTypeName {
    
    public ShortTypeName() {
 this(SourceInfo.NONE);
    }

    
    public ShortTypeName(SourceInfo si) {
 super(short.class, si);
    }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
  
}
