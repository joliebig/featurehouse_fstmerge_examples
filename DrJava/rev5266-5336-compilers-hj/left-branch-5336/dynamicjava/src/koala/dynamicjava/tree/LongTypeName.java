

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.Visitor;



public class LongTypeName extends PrimitiveTypeName {
    
    public LongTypeName() {
 this(SourceInfo.NONE);
    }

    
    public LongTypeName(SourceInfo si) {
 super(long.class, si);
    }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
  
}
