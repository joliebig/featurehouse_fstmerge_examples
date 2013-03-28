

package koala.dynamicjava.tree;

import java.util.*;

import koala.dynamicjava.tree.visitor.*;



public class TryStatement extends Statement {
  
  private Node tryBlock;
  
  
  private List<CatchStatement> catchStatements;
  
  
  private Node finallyBlock;
  
  
  public TryStatement(Node tryB, List<CatchStatement> catchL, Node fin,
                      SourceInfo si) {
    super(si);
    tryBlock        = tryB;
    catchStatements = catchL;
    finallyBlock    = fin;
  }
  
  
  public Node getTryBlock() {
    return tryBlock;
  }
  
  
  public List<CatchStatement> getCatchStatements() {
    return catchStatements;
  }
  
  
  public Node getFinallyBlock() {
    return finallyBlock;
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
    
  public String toString() {
    return "("+getClass().getName()+": "+getTryBlock()+" "+getCatchStatements()+" "+getFinallyBlock()+")";
  }
}
