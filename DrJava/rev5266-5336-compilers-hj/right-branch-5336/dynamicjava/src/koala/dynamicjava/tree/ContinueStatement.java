

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.*;



public class ContinueStatement extends Statement {
  
  private String label;
  
  
  public ContinueStatement(String label) {
    this(label, SourceInfo.NONE);
  }
  
  
  public ContinueStatement(String label,
                           SourceInfo si) {
    super(si);
    this.label = label;
  }
  
  
  public String getLabel() {
    return label;
  }
  
  
  public void setLabel(String s) {
    label = s;
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }    
   
  public String toString() {
    return "("+getClass().getName()+": "+getLabel()+")";
  }
}
