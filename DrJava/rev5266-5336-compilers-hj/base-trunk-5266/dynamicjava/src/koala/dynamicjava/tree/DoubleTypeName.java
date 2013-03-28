

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.Visitor;



public class DoubleTypeName extends PrimitiveTypeName {
    
    public DoubleTypeName() {
 this(SourceInfo.NONE);
    }

    
    public DoubleTypeName(SourceInfo si) {
 super(double.class, si);
    }
   
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
  
}
