

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.*;



public class InstanceOfExpression extends Expression implements ExpressionContainer {
  
  private Expression expression;
  
  
  private TypeName referenceType;
  
  
  public InstanceOfExpression(Expression exp, TypeName t) {
    this(exp, t, SourceInfo.NONE);
  }
  
  
  public InstanceOfExpression(Expression exp, TypeName t,
                              SourceInfo si) {
    super(si);
    
    if (exp == null) throw new IllegalArgumentException("exp == null");
    if (t == null)   throw new IllegalArgumentException("t == null");
    
    expression    = exp;
    referenceType = t;
  }
  
  
  public Expression getExpression() {
    return expression;
  }
  
  
  public void setExpression(Expression e) {
    if (e == null) throw new IllegalArgumentException("e == null");
    expression = e;
  }
  
  
  public TypeName getReferenceType() {
    return referenceType;
  }
  
  
  public void setReferenceType(TypeName t) {
    if (t == null) throw new IllegalArgumentException("t == null");
    referenceType = t;
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
   
  public String toString() {
    return "("+getClass().getName()+": "+getExpression()+" "+getReferenceType()+")";
  }
}
