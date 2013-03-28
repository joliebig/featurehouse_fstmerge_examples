

package koala.dynamicjava.tree;

import java.util.*;

import edu.rice.cs.plt.tuple.Option;

import koala.dynamicjava.tree.visitor.*;



public class ObjectMethodCall extends MethodCall implements ExpressionContainer {
  
  private Expression expression;

  
  public ObjectMethodCall(Expression exp, Option<List<TypeName>> targs, String mn,
                           List<? extends Expression> args, SourceInfo si) {
    super(targs, mn, args, si);
    if (exp == null) { throw new IllegalArgumentException("exp == null"); }
    expression = exp;
  }

  
  public ObjectMethodCall(Expression exp, String mn, List<? extends Expression> args, SourceInfo si) {
    this(exp, Option.<List<TypeName>>none(), mn, args, si);
  }

  
  public ObjectMethodCall(Expression exp, Option<List<TypeName>> targs, String mn,
                           List<? extends Expression> args) {
    this(exp, targs, mn, args, SourceInfo.NONE);
  }

  
  public ObjectMethodCall(Expression exp, String mn, List<? extends Expression> args) {
    this(exp, Option.<List<TypeName>>none(), mn, args, SourceInfo.NONE);
  }

  
  public Expression getExpression() {
    return expression;
  }

  
  public void setExpression(Expression e) {
    if (e == null) { throw new IllegalArgumentException("e == null"); }
    expression = e;
  }

  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }

  public String toString() {
    return "("+getClass().getName()+": "+toStringHelper()+")";
  }

  
  public String toStringHelper() {
    return getTypeArgs()+" "+getMethodName()+" "+getArguments()+" "+getExpression();
  }
}
