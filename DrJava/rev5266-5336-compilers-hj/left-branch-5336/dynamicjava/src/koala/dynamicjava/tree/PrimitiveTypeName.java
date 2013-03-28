

package koala.dynamicjava.tree;



public abstract class PrimitiveTypeName extends TypeName {
  
  private Class<?> value;
  
  
  protected PrimitiveTypeName(Class<?> val, SourceInfo si) {
    super(si);
    
    if (val == null) throw new IllegalArgumentException("val == null");
    
    value = val;
  }
  
  
  @Deprecated public Class<?> getValue() {
    return value;
  }
  
  
  public void setValue(Class<?> c) {
    if (c == null) throw new IllegalArgumentException("c == null");
    value = c;
  }
  
  
  public String toString() {
    return "("+getClass().getName()+": "+getValue()+")";
  }
}
