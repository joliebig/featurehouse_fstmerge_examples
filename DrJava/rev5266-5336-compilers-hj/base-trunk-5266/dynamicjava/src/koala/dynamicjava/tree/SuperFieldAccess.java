

package koala.dynamicjava.tree;

import edu.rice.cs.plt.tuple.Option;

import koala.dynamicjava.tree.visitor.*;



public class SuperFieldAccess extends FieldAccess {
  
  private Option<String> className;
  
  public SuperFieldAccess(Option<String> cn, String fln, SourceInfo si) {
    super(fln, si);
    if (cn == null) throw new IllegalArgumentException("cn == null");
    className = cn;
  }
  
  public SuperFieldAccess(Option<String> cn, String fln) { this(cn, fln, SourceInfo.NONE); }
  
  
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
    return "("+getClass().getName()+": "+getClassName()+" "+getFieldName()+")";
  }
}
