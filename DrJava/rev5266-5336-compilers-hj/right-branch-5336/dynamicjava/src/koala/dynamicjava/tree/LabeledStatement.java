

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.*;



public class LabeledStatement extends Statement {
  
  private String label;
  
  
  private Node statement;
  
  
  public LabeledStatement(String label, Node stat) {
    this(label, stat, SourceInfo.NONE);
  }
  
  
  public LabeledStatement(String label, Node stat,
                          SourceInfo si) {
    super(si);
    
    if (label == null) throw new IllegalArgumentException("label == null");
    if (stat == null)  throw new IllegalArgumentException("stat == null");
    
    this.label = label;
    statement  = stat;
  }
  
  
  public String getLabel() {
    return label;
  }
  
  
  public void setLabel(String s) {
    if (s == null) throw new IllegalArgumentException("s == null");
    label = s;
  }
  
  
  public Node getStatement() {
    return statement;
  }
  
  
  public void setStatement(Node n) {
    if (n == null) throw new IllegalArgumentException("n == null");
    statement = n;
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }    
     
  public String toString() {
    return "("+getClass().getName()+": "+getLabel()+" "+getStatement()+")";
  }
}
