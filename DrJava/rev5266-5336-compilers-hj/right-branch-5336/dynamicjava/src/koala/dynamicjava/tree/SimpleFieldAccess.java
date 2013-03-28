

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.*;


public class SimpleFieldAccess extends FieldAccess {
  
  public SimpleFieldAccess(String fln) {
    this(fln, SourceInfo.NONE);
  }
  
  
  public SimpleFieldAccess(String fln, SourceInfo si) {
    super(fln, si);
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
    
  public String toString() {
    return "("+getClass().getName()+": "+getFieldName()+")";
  }
}
