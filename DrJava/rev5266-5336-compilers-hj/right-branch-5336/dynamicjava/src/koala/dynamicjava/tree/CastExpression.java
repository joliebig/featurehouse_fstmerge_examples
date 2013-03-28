

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.*;



public class CastExpression extends UnaryExpression {
  public final static String TARGET_TYPE = "targetType";
  
  
  private TypeName targetType;
  
  
  public CastExpression(TypeName tt, Expression exp) {
    this(tt, exp, SourceInfo.NONE);
  }
  
  
  public CastExpression(TypeName tt, Expression exp,
                        SourceInfo si) {
    super(exp, si);
    
    
    
    targetType = tt;
  }
  
  
  public TypeName getTargetType() {
    return targetType;
  }
  
  
  public void setTargetType(TypeName t) {
    
    targetType = t;
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
     
  public String toString() {
    return "("+getClass().getName()+": "+getExpression()+" "+getTargetType()+")";
  }
}
