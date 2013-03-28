

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.Visitor;



public class FloatTypeName extends PrimitiveTypeName {
    
    public FloatTypeName() {
 this(SourceInfo.NONE);
    }

    
    public FloatTypeName(SourceInfo si) {
 super(float.class, si);
    }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
  
}
