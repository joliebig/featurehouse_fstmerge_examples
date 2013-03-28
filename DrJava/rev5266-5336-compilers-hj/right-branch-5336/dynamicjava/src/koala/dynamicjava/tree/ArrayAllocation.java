

package koala.dynamicjava.tree;

import java.util.*;

import koala.dynamicjava.tree.visitor.*;



public class ArrayAllocation extends PrimaryExpression {
  
  private TypeName elementType;
  
  
  private TypeDescriptor typeDescriptor;
  
  
  public ArrayAllocation(TypeName tp, TypeDescriptor td) { this(tp, td, SourceInfo.NONE); }
  
  
  public ArrayAllocation(TypeName tp, TypeDescriptor td,
                         SourceInfo si) {
    super(si);
    
    if (tp == null) throw new IllegalArgumentException("tp == null");
    if (td == null) throw new IllegalArgumentException("td == null");
    elementType = tp;
    typeDescriptor = td;
    td.initialize(tp);
  }
  
  
  public TypeName getElementType() {
    return elementType;
  }
  
  
  public void setElementType(TypeName t) {
    if (t == null) throw new IllegalArgumentException("t == null");
    elementType = t;
  }

  
  public int getDimension() { return typeDescriptor.dimension; }
  
  
  public void setDimension(int dim) { typeDescriptor.dimension = dim; }
  
  
  public List<Expression> getSizes() {
    return typeDescriptor.sizes;
  }
  
  
  public void setSizes(List<? extends Expression> sz) { 
    typeDescriptor.sizes = (sz == null) ? null : new ArrayList<Expression>(sz);
  }

  
  public ArrayInitializer getInitialization() {
    return typeDescriptor.initialization;
  }
  
  
  public void setInitialization(ArrayInitializer init) { typeDescriptor.initialization = init; }

  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
 }
 
  
  public String toString() {
    return "(" + getClass().getName() + ": " + getElementType() + " " + getDimension() + " " + getSizes() + ")";
  }
  
  
  public static class TypeDescriptor implements SourceInfo.Wrapper {
    
    List<Expression> sizes;
    
    
    int dimension;
    
    
    ArrayInitializer initialization;
    
    SourceInfo sourceInfo;
    
    
    public TypeDescriptor(List<? extends Expression> sizes, int dim, ArrayInitializer init, SourceInfo si) {
      this.sizes     = (sizes == null) ? null : new ArrayList<Expression>(sizes);
      dimension      = dim;
      initialization = init;
      sourceInfo     = si;
    }
    
    public SourceInfo getSourceInfo() { return sourceInfo; }
    
    
    void initialize(TypeName t) {
      if (initialization != null) {
        TypeName et;
        if (dimension > 1)
          et = new ArrayTypeName(t, dimension-1, false, SourceInfo.span(t, this));
        else
          et = t; 

        initialization.setElementType(et);
      }
    }
  }
}
