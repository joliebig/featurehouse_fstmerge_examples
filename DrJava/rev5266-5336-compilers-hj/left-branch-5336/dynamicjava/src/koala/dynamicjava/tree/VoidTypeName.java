

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.Visitor;



public class VoidTypeName extends PrimitiveTypeName {
    
    public VoidTypeName() {
 this(SourceInfo.NONE);
    }

    
    public VoidTypeName(SourceInfo si) {
 super(void.class, si);
    }
      
  public String toString() {
    return "("+getClass().getName()+": "+")";
  }

  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
  
}
