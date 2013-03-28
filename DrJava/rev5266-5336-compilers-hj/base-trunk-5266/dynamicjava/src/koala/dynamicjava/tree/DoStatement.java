

package koala.dynamicjava.tree;

import java.util.*;

import koala.dynamicjava.tree.visitor.*;



public class DoStatement extends Statement implements ContinueTarget {
  
  private Expression condition;
  
  
  private Node body;
  
  
  private List<String> labels;
  
  
  public DoStatement(Expression cond, Node body) {
    this(cond, body, SourceInfo.NONE);
  }
  
  
  public DoStatement(Expression cond, Node body,
                     SourceInfo si) {
    super(si);
    
    if (cond == null) throw new IllegalArgumentException("cond == null");
    if (body == null) throw new IllegalArgumentException("body == null");
    
    condition = cond;
    this.body = body;
    labels    = new LinkedList<String>();
  }
  
  
  public Expression getCondition() {
    return condition;
  }
  
  
  public void setCondition(Expression e) {
    if (e == null) throw new IllegalArgumentException("e == null");
    condition = e;
  }
  
  
  public Node getBody() {
    return body;
  }
  
  
  public void setBody(Node node) {
    if (node == null) throw new IllegalArgumentException("node == null");
    body = node;
  }
  
  
  public void addLabel(String label) {
    if (label == null) throw new IllegalArgumentException("label == null");
    labels.add(label);
  }
  
  
  public boolean hasLabel(String label) {
    return labels.contains(label);
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }    
   
  public String toString() {
    return "("+getClass().getName()+": "+getCondition()+" "+getBody()+")";
  }
}
