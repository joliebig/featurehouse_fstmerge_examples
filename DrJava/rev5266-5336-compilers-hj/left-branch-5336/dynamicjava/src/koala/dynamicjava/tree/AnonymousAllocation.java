

package koala.dynamicjava.tree;

import java.util.*;

import edu.rice.cs.plt.tuple.Option;

import koala.dynamicjava.tree.visitor.*;



public class AnonymousAllocation extends SimpleAllocation implements StatementExpression {
  
  private List<Node> members;
  
  
  public AnonymousAllocation(ReferenceTypeName tp, List<? extends Expression> args, List<Node> memb) {
    this(Option.<List<TypeName>>none(), tp, args, memb, SourceInfo.NONE);
  }
  
  
  public AnonymousAllocation(Option<List<TypeName>> targs, ReferenceTypeName tp, List<? extends Expression> args,
                              List<Node> memb) {
    this(targs, tp, args, memb, SourceInfo.NONE);
  }
  
  
  public AnonymousAllocation(ReferenceTypeName tp, List<? extends Expression> args, List<Node> memb, SourceInfo si) {
    this(Option.<List<TypeName>>none(), tp, args, memb, si);
  }
  
  
  public AnonymousAllocation(Option<List<TypeName>> targs, ReferenceTypeName tp, List<? extends Expression> args,
                              List<Node> memb, SourceInfo si) {
    super(targs, tp, args, si);
    if (memb == null) throw new IllegalArgumentException("memb == null");
    members = memb;
  }
  
  
  public List<Node> getMembers() {
    return members;
  }
  
  
  public void setMembers(List<Node> l) {
    if (l == null) throw new IllegalArgumentException("l == null");
    members = l;
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
   
  public String toString() {
    return "("+getTypeArgs()+" "+getClass().getName()+": "+getCreationType()+" "+getArguments()+" "+getMembers()+")";
  }
}
