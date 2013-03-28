

package koala.dynamicjava.tree;

import java.util.*;

import koala.dynamicjava.tree.visitor.*;



public class ConstructorCall extends PrimaryExpression implements ExpressionContainer, StatementExpression {
  
  private Expression expression;
  
  
  private List<Expression> arguments;
  
  
  private boolean superCall;
  
  
  public ConstructorCall(Expression exp, List<? extends Expression> args, boolean sup) {
    this(exp, args, sup, SourceInfo.NONE);
  }
  
  
  public ConstructorCall(Expression exp, List<? extends Expression> args, boolean sup,
                               SourceInfo si) {
    super(si);
    
    expression = exp;
    arguments  = (args == null) ? new ArrayList<Expression>(0) : new ArrayList<Expression>(args);
    superCall  = sup;
  }
  
  
  public Expression getExpression() {
    return expression;
  }
  
  
  public void setExpression(Expression e) {
    expression = e;
  }
  
  
  public List<Expression> getArguments() {
    return arguments;
  }
  
  
  public void setArguments(List<? extends Expression> l) {
    arguments = (l == null) ? new ArrayList<Expression>(0) : new ArrayList<Expression>(l);
  }
  
  
  public boolean isSuper() {
    return superCall;
  }
  
  
  public void setSuper(boolean b) {
    superCall = b;
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
   
  public String toString() {
    return "("+getClass().getName()+": "+getExpression()+" "+getArguments()+" "+isSuper()+")";
  }
}
