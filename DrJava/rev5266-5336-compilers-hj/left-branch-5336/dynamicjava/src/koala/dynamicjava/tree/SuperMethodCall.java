

package koala.dynamicjava.tree;

import java.util.*;

import edu.rice.cs.plt.tuple.Option;

import koala.dynamicjava.tree.visitor.*;



public class SuperMethodCall extends MethodCall {
  
  private Option<String> className;
  
  public SuperMethodCall(Option<String> cn, Option<List<TypeName>> targs, String mn,
                          List<? extends Expression> args, SourceInfo si) {
    super(targs, mn, args, si);
    if (cn == null) throw new IllegalArgumentException("cn == null");
    className = cn;
  }
  
  public SuperMethodCall(Option<String> cn, String mn, List<? extends Expression> args, SourceInfo si) {
    this(cn, Option.<List<TypeName>>none(), mn, args, si);
  }
  
  public SuperMethodCall(Option<String> cn, Option<List<TypeName>> targs, String mn,
                          List<? extends Expression> args) {
    this(cn, targs, mn, args, SourceInfo.NONE);
  }
  
  public SuperMethodCall(Option<String> cn, String mn, List<? extends Expression> args) {
    this(cn, Option.<List<TypeName>>none(), mn, args, SourceInfo.NONE);
  }
  
  
  public Option<String> getClassName() {
    return className;
  }
  
  
  public void setClassName(Option<String> cn) {
    if (cn == null) throw new IllegalArgumentException("cn == null");
    className = cn;
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
    
  public String toString() {
    return "("+getClass().getName()+": "+toStringHelper()+")";
  }
    
  public String toStringHelper() {
    return getClassName()+" "+getTypeArgs()+" "+getMethodName()+" "+getArguments();
  }
}
