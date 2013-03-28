

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.*;



public class IfThenElseStatement extends IfThenStatement {
  
  private Node elseStatement;
  
  
  public IfThenElseStatement(Expression cond, Node tstmt, Node estmt) {
    this(cond, tstmt, estmt, SourceInfo.NONE);
  }
  
  
  public IfThenElseStatement(Expression cond, Node tstmt, Node estmt,
                             SourceInfo si) {
    super(cond, tstmt, si);
    
    if (estmt == null) throw new IllegalArgumentException("estmt == null");
    
    elseStatement = estmt;
  }
  
  
  public Node getElseStatement() {
    return elseStatement;
  }
  
  
  public void setElseStatement(Node node) {
    if (node == null) throw new IllegalArgumentException("node == null");
    elseStatement = node;
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }    
   
  public String toString() {
    return "("+getClass().getName()+": "+getCondition()+" "+getThenStatement()+" "+getElseStatement()+")";
  }
}
