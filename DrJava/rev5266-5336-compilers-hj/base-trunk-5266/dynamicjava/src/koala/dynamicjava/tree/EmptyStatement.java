

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.*;



public class EmptyStatement extends Statement {
  
  public EmptyStatement() {
    this(SourceInfo.NONE);
  }
  
  
  public EmptyStatement(SourceInfo si) {
    super(si);
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
   
  public String toString() {
    return "("+getClass().getName()+": "+")";
  }
}
