

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.*;



public abstract class Literal extends PrimaryExpression {
  
  private String representation;
  
  
  private Object value;
  
  
  private Class<?> type;
  
  
  protected Literal(String rep, Object val, Class<?> typ, SourceInfo si) {
    super(si);
    
    if (rep == null) throw new IllegalArgumentException("rep == null");
    
    representation = rep;
    value          = val;
    type           = typ;
  }
  
  
  public String getRepresentation() {
    return representation;
  }
  
  
  public void setRepresentation(String s) {
    if (s == null) throw new IllegalArgumentException("s == null");
    representation = s;
  }
  
  
  public Object getValue() {
    return value;
  }
  
  
  public void setValue(Object o) {
    value = o;
  }
  
  
  public Class<?> getType() {
    return type;
  }
  
  
  public void setType(Class<?> c) {
    type = c;
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
       
  public String toString() {
    return "("+getClass().getName()+": "+getRepresentation()+" "+getValue()+" "+getType()+")";
  }
}
