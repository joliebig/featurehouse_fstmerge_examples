

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.*;



public class IfThenStatement extends Statement {
  
  private Expression condition;
  
  
  private Node thenStatement;
  
  
  public IfThenStatement(Expression cond, Node tstmt) {
    this(cond, tstmt, SourceInfo.NONE);
  }
  
  
  public IfThenStatement(Expression cond, Node tstmt,
                         SourceInfo si) {
    super(si);
    
    if (cond == null)  throw new IllegalArgumentException("cond == null");
    if (tstmt == null) throw new IllegalArgumentException("tstmt == null");
    
    condition     = cond;
    thenStatement = tstmt;
  }
  
  
  public Expression getCondition() {
    return condition;
  }
  
  
  public void setCondition(Expression e) {
    if (e == null) throw new IllegalArgumentException("e == null");
    condition = e;
  }
  
  
  public Node getThenStatement() {
    return thenStatement;
  }
  
  
  public void setThenStatement(Node node) {
    if (node == null) throw new IllegalArgumentException("node == null");
    thenStatement = node;
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }    
   
  public String toString() {
    return "("+getClass().getName()+": "+getCondition()+" "+getThenStatement()+")";
  }
}
