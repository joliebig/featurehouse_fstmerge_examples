

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.*;



public class AssertStatement extends Statement {
  
  private Expression condition, failedString;
    
  
  public AssertStatement(Expression cond, Expression falseString) {
    this(cond, falseString, SourceInfo.NONE);
  }
  
  
  public AssertStatement(Expression cond, Expression falseString, SourceInfo si) {
    super(si);
    
    if (cond == null)  throw new IllegalArgumentException("cond == null");
    
    condition     = cond;
    failedString  = falseString;
  }
  
  
  public Expression getCondition() {
    return condition;
  }
  
  
  public Expression getFailString() {
    return failedString;
  }
  
  
  public void setCondition(Expression e) {
    if (e == null) throw new IllegalArgumentException("e == null");
    condition = e;
  }
  
  
  public void setFailString(Expression e) {
    failedString = e;
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }    
   
  public String toString() {
    return "("+getClass().getName()+": "+getCondition()+ ((getFailString() != null)? getFailString() : "" ) + ")";
  }
}
