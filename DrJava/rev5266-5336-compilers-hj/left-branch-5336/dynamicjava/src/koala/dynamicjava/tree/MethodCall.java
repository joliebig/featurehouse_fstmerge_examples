

package koala.dynamicjava.tree;

import java.util.*;

import edu.rice.cs.plt.tuple.Option;



public abstract class MethodCall extends PrimaryExpression
  implements StatementExpression {
  
  private Option<List<TypeName>> typeArgs;
  private String methodName;
  private List<Expression> arguments;
  
  
  protected MethodCall(Option<List<TypeName>> targs, String mn, List<? extends Expression> args,
                       SourceInfo si) {
    super(si);
    if (mn == null || targs == null) throw new IllegalArgumentException();
    typeArgs = targs;
    methodName = mn;
    arguments  = (args == null) ? new ArrayList<Expression>(0) : new ArrayList<Expression>(args);
  }
  
  public Option<List<TypeName>> getTypeArgs() { return typeArgs; }
  public void setTypeArgs(List<TypeName> targs) { typeArgs = Option.wrap(targs); }
  public void setTypeArgs(Option<List<TypeName>> targs) {
    if (targs == null) throw new IllegalArgumentException();
    typeArgs = targs;
  }
  
  
  public String getMethodName() {
    return methodName;
  }
  
  
  public void setMethodName(String s) {
    if (s == null) throw new IllegalArgumentException("s == null");
    methodName = s;
  }
  
  
  public List<Expression> getArguments() {
    return arguments;
  }
  
  
  public void setArguments(List<? extends Expression> l) {
    arguments = (l == null) ? new ArrayList<Expression>(0) : new ArrayList<Expression>(l);
  }
}
