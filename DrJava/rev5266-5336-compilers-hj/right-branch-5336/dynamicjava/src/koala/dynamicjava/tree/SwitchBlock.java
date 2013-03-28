

package koala.dynamicjava.tree;

import java.util.*;

import koala.dynamicjava.tree.visitor.*;



public class SwitchBlock  extends Node implements ExpressionContainer {
  
  private Expression expression;
  
  
  private List<Node> statements;
  
  
  public SwitchBlock(Expression exp, List<Node> stmts) {
    this(exp, stmts, SourceInfo.NONE);
  }
  
  
  public SwitchBlock(Expression exp, List<Node> stmts,
                     SourceInfo si) {
    super(si);
    
    expression = exp;
    statements = stmts;
  }
  
  
  public Expression getExpression() {
    return expression;
  }
  
  
  public void setExpression(Expression e) {
    expression = e;
  }
  
  
  public List<Node> getStatements() {
    return statements;
  }
  
  
  public void setStatements(List<Node> l) {
    statements = l;
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
    
  public String toString() {
    return "("+getClass().getName()+": "+getExpression()+" "+getStatements()+")";
  }
}
