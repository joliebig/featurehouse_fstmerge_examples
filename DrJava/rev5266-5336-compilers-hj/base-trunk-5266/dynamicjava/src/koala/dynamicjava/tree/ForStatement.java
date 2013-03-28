

package koala.dynamicjava.tree;

import java.util.*;

import koala.dynamicjava.tree.visitor.*;



public class ForStatement extends ForSlashEachStatement implements ContinueTarget {
  
  private List<Node> initialization;
  
  
  private Expression condition;
  
  
  private List<Node> update;
  
  
  private Node body;
  
  
  private List<String> labels;
  
  
  public ForStatement(List<Node> init, Expression cond, List<Node> updt, Node body) {
    this(init, cond, updt, body, SourceInfo.NONE);
  }
  
  
  public ForStatement(List<Node> init, Expression cond, List<Node> updt, Node body,
                      SourceInfo si) {
    super(si);
    
    if (body == null) throw new IllegalArgumentException("body == null");
    
    initialization = init;
    condition      = cond;
    update         = updt;
    this.body      = body;
    labels         = new LinkedList<String>();
  }
  
  
  public List<Node> getInitialization() {
    return initialization;
  }
  
  
  public void setInitialization(List<Node> l) {
    initialization = l;
  }
  
  
  public Expression getCondition() {
    return condition;
  }
  
  
  public void setCondition(Expression e) {
    condition = e;
  }
  
  
  public List<Node> getUpdate() {
    return update;
  }
  
  
  public void setUpdate(List<Node> l) {
    update = l;
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
  
  public String toString(){
    return "("+getClass().getName()+": "+getInitialization()+" "+getCondition()+" "+getUpdate()+" "+getBody()+")";
  }
}
