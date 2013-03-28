

package koala.dynamicjava.tree;

import java.util.*;

import edu.rice.cs.plt.tuple.Option;

import koala.dynamicjava.tree.visitor.*;



public class SimpleMethodCall extends MethodCall {
  
  public SimpleMethodCall(String mn, List<Expression> args, SourceInfo si) {
    this(Option.<List<TypeName>>none(), mn, args, si);
  }

  
  public SimpleMethodCall(Option<List<TypeName>> targs, String mn, List<Expression> args,
                      SourceInfo si) {
    super(targs, mn, args, si);
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
   
  public String toString() {
    return "("+getClass().getName()+": "+getTypeArgs()+" "+getMethodName()+" "+getArguments()+")";
  }
}
