

package koala.dynamicjava.tree;

import java.util.*;

import koala.dynamicjava.tree.visitor.*;



public class ArrayInitializer extends Expression {
  
  private List<Expression> cells;
  
  
  private TypeName elementType;
  
  
  public ArrayInitializer(List<? extends Expression> cells) {
    this(cells, SourceInfo.NONE);
  }
  
  
  public ArrayInitializer(List<? extends Expression> cells,
                          SourceInfo si) {
    super(si);
    
    if (cells == null) throw new IllegalArgumentException("cells == null");
    
    this.cells = new ArrayList<Expression>(cells);
  }
  
  
  public List<Expression> getCells() {
    return cells;
  }
  
  
  public void setCells(List<? extends Expression> l) {
    if (l == null) throw new IllegalArgumentException("l == null");
    cells = new ArrayList<Expression>(l);
  }
  
  
  public TypeName getElementType() {
    return elementType;
  }
  
  
  public void setElementType(TypeName t) {
    if (t == null) throw new IllegalArgumentException("t == null");
    elementType = t;
    if (t instanceof ArrayTypeName) {
      ArrayTypeName at = (ArrayTypeName)t;
      for (Expression init : cells) {
        if (init instanceof ArrayInitializer) {
          ((ArrayInitializer)init).setElementType(at.getElementType());
        }
      }
    }
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
  
     
  public String toString() {
    return "("+getClass().getName()+": "+getCells()+" "+getElementType()+")";
  }
}
