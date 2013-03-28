

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.*;


public class CatchStatement extends Statement {
  
  private FormalParameter exception;
  
  
  private Node block;
  
  
  public CatchStatement(FormalParameter fp, Node blk,
                        SourceInfo si) {
    super(si);
    exception = fp;
    block     = blk;
  }
  
  
  public FormalParameter getException() {
    return exception;
  }
  
  
  public Node getBlock() {
    return block;
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
   
  public String toString() {
    return "("+getClass().getName()+": "+getException()+" "+getBlock()+")";
  }
}
