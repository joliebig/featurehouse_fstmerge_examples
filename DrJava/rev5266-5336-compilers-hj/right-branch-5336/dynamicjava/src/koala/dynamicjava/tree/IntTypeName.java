

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.Visitor;



public class IntTypeName extends PrimitiveTypeName {
    
    public IntTypeName() {
 this(SourceInfo.NONE);
    }

    
    public IntTypeName(SourceInfo si) {
 super(int.class, si);
    }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
  
}
