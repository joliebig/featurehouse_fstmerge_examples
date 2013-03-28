

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.*;



public class SynchronizedStatement extends Statement {
  
  private Expression lock;
  
  
  private Node body;
  
  
  public SynchronizedStatement(Expression lock, Node body,
                               SourceInfo si) {
    super(si);
    
    if (lock == null) throw new IllegalArgumentException("lock == null");
    if (body == null) throw new IllegalArgumentException("body == null");
    
    this.lock = lock;
    this.body = body;
  }
  
  
  public Expression getLock() {
    return lock;
  }
  
  
  public void setLock(Expression e) {
    if (e == null) throw new IllegalArgumentException("e == null");
    lock = e;
  }
  
  
  public Node getBody() {
    return body;
  }
  
  
  public void setBody(Node node) {
    if (node == null) throw new IllegalArgumentException("node == null");
    body = node;
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }    
    
  public String toString() {
    return "("+getClass().getName()+": "+getLock()+" "+getBody()+")";
  }
}
