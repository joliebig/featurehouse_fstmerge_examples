

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.*;



public class StaticFieldAccess extends FieldAccess {
  
  private ReferenceTypeName fieldType;
  
  
  public StaticFieldAccess(ReferenceTypeName typ, String fln) {
    this(typ, fln, SourceInfo.NONE);
  }
  
  
  public StaticFieldAccess(ReferenceTypeName typ, String fln,
                           SourceInfo si) {
    super(fln, si);
    
    if (typ == null) throw new IllegalArgumentException("typ == null");
    
    fieldType = typ;
  }
  
  
  public ReferenceTypeName getFieldType() {
    return fieldType;
  }
  
  
  public void setFieldType(ReferenceTypeName t) {
    if (t == null) throw new IllegalArgumentException("t == null");
    fieldType = t;
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
    
  public String toString() {
    return "("+getClass().getName()+": "+getFieldName()+" "+getFieldType()+")";
  }
}
