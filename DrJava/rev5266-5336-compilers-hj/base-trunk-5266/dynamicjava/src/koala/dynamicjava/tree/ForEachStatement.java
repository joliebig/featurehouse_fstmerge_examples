



package koala.dynamicjava.tree;

import java.util.*;

import koala.dynamicjava.tree.visitor.*;



public class ForEachStatement extends ForSlashEachStatement implements ContinueTarget {
  
  private FormalParameter parameter;
  
  
  private Expression collection;
  
  
  private Node body;
  
  
  private List<String> labels;
  
  
  
  private List<String> vars;
  
  
  public ForEachStatement(FormalParameter para, Expression collection, Node body) {
    this(para, collection, body, SourceInfo.NONE);
  }
  
  
  public ForEachStatement(FormalParameter para, Expression coll, Node body,
                      SourceInfo si) {
    super(si);
    
    if (body == null) throw new IllegalArgumentException("body == null");
    
    parameter = para;
    collection     = coll;
    this.body      = body;
    labels         = new LinkedList<String>();
    vars = new LinkedList<String>();
  }
  
  
  public void addVar(String s){
    vars.add(s);
  }
  
  public List<String> getVars(){
    return vars;
  }
  
  
  public FormalParameter getParameter() {
    return parameter;
  }
  
  
  public void setParameter(FormalParameter l) {
    parameter = l;
  }
  
  
  public Expression getCollection() {
    return collection;
  }
  
  
  public void setCollection(Expression e) {
    collection = e;
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
    return "("+getClass().getName()+": "+getParameter()+" "+getCollection()+" "+getBody()+")";
  }
}
