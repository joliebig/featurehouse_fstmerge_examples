

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.*;



public class TypeExpression extends PrimaryExpression {
  
  private TypeName type;
  
  
  public TypeExpression(TypeName t) {
    this(t, SourceInfo.NONE);
  }
  
  
  public TypeExpression(TypeName t, SourceInfo si) {
    super(si);
    
    if (t == null) throw new IllegalArgumentException("t == null");
    
    type = t;
  }
  
  
  public TypeName getType() {
    return type;
  }
  
  
  public void setType(ReferenceTypeName t) {
    if (t == null) throw new IllegalArgumentException("t == null");
    type = t;
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
    
  public String toString() {
    return "("+getClass().getName()+": "+getType()+")";
  }
}
