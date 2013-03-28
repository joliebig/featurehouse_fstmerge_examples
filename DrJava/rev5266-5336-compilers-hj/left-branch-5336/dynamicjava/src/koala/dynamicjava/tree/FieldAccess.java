

package koala.dynamicjava.tree;



public abstract class FieldAccess extends PrimaryExpression implements LeftHandSide {
  
  private String fieldName;
  
  
  protected FieldAccess(String fln, SourceInfo si) {
    super(si);
    
    if (fln == null) throw new IllegalArgumentException("Null field name in FieldAccess construction");
    
    fieldName  = fln;
  }
  
  
  public String getFieldName() {
    return fieldName;
  }
  
  
  public void setFieldName(String s) {
    if (s == null) throw new IllegalArgumentException("Null field name in FieldAccess update");
    fieldName = s;
  }
}
