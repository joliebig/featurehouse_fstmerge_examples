

package koala.dynamicjava.tree;

import java.util.*;

import edu.rice.cs.plt.tuple.Option;

import koala.dynamicjava.tree.visitor.*;



public class InnerAllocation extends PrimaryExpression implements StatementExpression, ExpressionContainer {
  private Expression expression;
  private Option<List<TypeName>> typeArgs;
  private String className;
  private Option<List<TypeName>> classTypeArgs;
  private List<Expression> arguments;
  
  
  public InnerAllocation(Expression exp, Option<List<TypeName>> targs, String cn,
                          Option<List<TypeName>> ctargs, List<? extends Expression> args) {
    this(exp, targs, cn, ctargs, args, SourceInfo.NONE);
  }
  
  
  public InnerAllocation(Expression exp, String cn, Option<List<TypeName>> ctargs, List<? extends Expression> args) {
    this(exp, Option.<List<TypeName>>none(), cn, ctargs, args, SourceInfo.NONE);
  }
  
  
  public InnerAllocation(Expression exp, String cn, Option<List<TypeName>> ctargs, List<? extends Expression> args,
                         SourceInfo si) {
    this(exp, Option.<List<TypeName>>none(), cn, ctargs, args, si);
  }
  
  
  public InnerAllocation(Expression exp, Option<List<TypeName>> targs, String cn,
                         Option<List<TypeName>> ctargs, List<? extends Expression> args,
                         SourceInfo si) {
    super(si);
    if (targs == null || cn == null || ctargs == null || exp == null) throw new IllegalArgumentException();
    expression = exp;
    typeArgs = targs;
    className = cn;
    classTypeArgs = ctargs;
    arguments  = (args == null) ? new ArrayList<Expression>(0) : new ArrayList<Expression>(args);
  }
  
  
  public Expression getExpression() {
    return expression;
  }
  
  
  public void setExpression(Expression e) {
    if (e == null) throw new IllegalArgumentException("e == null");
    expression = e;
  }
  
  public Option<List<TypeName>> getTypeArgs() { return typeArgs; }
  public void setTypeArgs(List<TypeName> targs) { typeArgs = Option.wrap(targs); }
  public void setTypeArgs(Option<List<TypeName>> targs) {
    if (targs == null) throw new IllegalArgumentException();
    typeArgs = targs;
  }
  
  
  public String getClassName() {
    return className;
  }
  
  
  public void setClassName(String cn) {
    if (cn == null) throw new IllegalArgumentException("cn == null");
    className = cn;
  }
  
  public Option<List<TypeName>> getClassTypeArgs() { return classTypeArgs; }
  public void setClassTypeArgs(List<TypeName> ctargs) { classTypeArgs = Option.wrap(ctargs); }
  public void setClassTypeArgs(Option<List<TypeName>> ctargs) {
    if (ctargs == null) throw new IllegalArgumentException();
    classTypeArgs = ctargs;
  }
  
  
  public List<Expression> getArguments() {
    return arguments;
  }
  
  
  public void setArguments(List<? extends Expression> l) {
    arguments = (l == null) ? new ArrayList<Expression>(0) : new ArrayList<Expression>(l);
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
   
  public String toString() {
    return "("+getClass().getName()+": "+getTypeArgs()+" "+getClassName()+" "+getClassTypeArgs()+" "+
             getExpression()+" "+getArguments()+")";
  }
}
