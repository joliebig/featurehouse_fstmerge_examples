

package koala.dynamicjava.tree;

import java.util.*;

import koala.dynamicjava.tree.visitor.*;



public class BlockStatement extends Statement {
  
  private List<Node> statements;
  
  
  public BlockStatement(List<Node> stmts) {
    this(stmts, SourceInfo.NONE);
  }
  
  
  public BlockStatement(List<Node> stmts, SourceInfo si) {
    super(si);
    
    if (stmts == null) throw new IllegalArgumentException("stmts == null");
    
    statements = stmts;
  }
  
  
  public List<Node> getStatements() {
    return statements;
  }
  
  
  public void setStatements(List<Node> l) {
    if (l == null) throw new IllegalArgumentException("l == null");
    statements = l;
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
     
  public String toString() {
    return "("+getClass().getName()+": "+getStatements()+")";
  }
}
