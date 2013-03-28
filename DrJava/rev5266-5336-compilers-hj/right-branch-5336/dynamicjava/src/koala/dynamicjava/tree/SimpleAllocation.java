

package koala.dynamicjava.tree;

import java.util.*;

import edu.rice.cs.plt.tuple.Option;

import koala.dynamicjava.tree.visitor.*;



public class SimpleAllocation extends PrimaryExpression implements StatementExpression {
  private Option<List<TypeName>> typeArgs;
  private ReferenceTypeName creationType;
  private List<Expression> arguments;
  
  
  public SimpleAllocation(Option<List<TypeName>> targs, ReferenceTypeName tp, List<? extends Expression> args) {
    this(targs, tp, args, SourceInfo.NONE);
  }
  
  
  public SimpleAllocation(ReferenceTypeName tp, List<? extends Expression> args) {
    this(Option.<List<TypeName>>none(), tp, args, SourceInfo.NONE);
  }
  
  
  public SimpleAllocation(ReferenceTypeName tp, List<? extends Expression> args, SourceInfo si) {
    this(Option.<List<TypeName>>none(), tp, args, si);
  }
  
  
  public SimpleAllocation(Option<List<TypeName>> targs, ReferenceTypeName tp, List<? extends Expression> args,
                           SourceInfo si) {
    super(si);
    if (tp == null || targs == null) throw new IllegalArgumentException();
    typeArgs = targs;
    creationType = tp;
    arguments = (args == null) ? new ArrayList<Expression>(0) : new ArrayList<Expression>(args);
  }
  
  public Option<List<TypeName>> getTypeArgs() { return typeArgs; }
  public void setTypeArgs(List<TypeName> targs) { typeArgs = Option.wrap(targs); }
  public void setTypeArgs(Option<List<TypeName>> targs) {
    if (targs == null) throw new IllegalArgumentException();
    typeArgs = targs;
  }
  
  
  public ReferenceTypeName getCreationType() {
    return creationType;
  }
  
  
  public void setCreationType(ReferenceTypeName t) {
    if (t == null) throw new IllegalArgumentException("t == null");
    creationType = t;
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
    return "("+getTypeArgs()+" "+getClass().getName()+": "+getCreationType()+" "+getArguments()+")";
  }
}
