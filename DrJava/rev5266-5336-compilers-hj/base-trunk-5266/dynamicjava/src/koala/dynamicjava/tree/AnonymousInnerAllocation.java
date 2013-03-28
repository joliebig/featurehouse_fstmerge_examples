

package koala.dynamicjava.tree;

import java.util.*;

import edu.rice.cs.plt.tuple.Option;

import koala.dynamicjava.tree.visitor.*;



public class AnonymousInnerAllocation extends InnerAllocation {

  
  public final static String MEMBERS = "members";
  
  
  private List<Node> members;
  
  
  public AnonymousInnerAllocation(Expression exp, Option<List<TypeName>> targs, String cn,
                                   Option<List<TypeName>> ctargs, List<? extends Expression> args, List<Node> memb) {
    this(exp, targs, cn, ctargs, args, memb, SourceInfo.NONE);
  }
  
  
  public AnonymousInnerAllocation(Expression exp, Option<List<TypeName>> targs, String cn,
                                   Option<List<TypeName>> ctargs, List<? extends Expression> args, List<Node> memb,
                                   SourceInfo si) {
    super(exp, targs, cn, ctargs, args, si);
    if (memb == null) throw new IllegalArgumentException("memb == null");
    members = memb;
  }
  
  
  public List<Node> getMembers() { return members; }
  
  
  public void setMembers(List<Node> l) {
    if (l == null) throw new IllegalArgumentException("l == null");
    members = l;
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
   
  public String toString() {
    return "("+getClass().getName()+": "+getTypeArgs()+" "+getClassName()+" "+getClassTypeArgs()+" "+getExpression()+
            " "+getArguments()+" "+getMembers()+")";
  }
}
