

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.*;



public class ArrayTypeName extends TypeName {
  
  private TypeName elementType;
  
  private boolean vararg;
  
  
  public ArrayTypeName(TypeName et, int dim, boolean varg) {
    this(et, dim, varg, SourceInfo.NONE);
  }
  
  
  public ArrayTypeName(TypeName et, int dim, boolean varg, SourceInfo si) {
    super(si);
    
    if (et == null) throw new IllegalArgumentException("et == null");
    if (dim < 1)    throw new IllegalArgumentException("dim < 1");
    
    elementType = (dim > 1) ? new ArrayTypeName(et, dim - 1, false, si) : et;
    vararg = varg;
  }
  
  
  public TypeName getElementType() {
    return elementType;
  }
  
  
  public void setElementType(TypeName t) {
    if (t == null) throw new IllegalArgumentException("t == null");
    elementType = t;
  }
  
  public boolean isVararg() { return vararg; }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
  
  public String toString() {
    return "("+getClass().getName()+": "+getElementType()+")";
  }
}
