

package koala.dynamicjava.tree;

import java.util.*;

import edu.rice.cs.plt.tuple.Option;

import koala.dynamicjava.tree.visitor.*;



public class StaticMethodCall extends MethodCall {
  
  private TypeName methodType;

  
  public StaticMethodCall(TypeName typ, Option<List<TypeName>> targs, String mn, List<? extends Expression> args) {
    this(typ, targs, mn, args, SourceInfo.NONE);
  }

  
  public StaticMethodCall(TypeName typ, String mn, List<? extends Expression> args) {
    this(typ, Option.<List<TypeName>>none(), mn, args, SourceInfo.NONE);
  }

  
  public StaticMethodCall(TypeName typ, Option<List<TypeName>> targs, String mn, List<? extends Expression> args,
                          SourceInfo si) {
    super(targs, mn, args, si);
    if (typ == null) throw new IllegalArgumentException("typ == null");
    methodType = typ;
  }

  
  public StaticMethodCall(TypeName typ, String mn, List<? extends Expression> args, SourceInfo si) {
    this(typ, Option.<List<TypeName>>none(), mn, args, si);
  }

  
  public TypeName getMethodType() {
    return methodType;
  }

  
  public void setMethodType(ReferenceTypeName t) {
    if (t == null) throw new IllegalArgumentException("t == null");
    methodType = t;
  }

  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
    
  public String toString() {
    return "("+getClass().getName()+": "+getTypeArgs()+" "+getMethodName()+" "+getArguments()+" "+getMethodType()+")";
  }
}
