

package koala.dynamicjava.tree;

import java.util.*;

import edu.rice.cs.plt.tuple.Option;

import koala.dynamicjava.tree.visitor.*;



public class ThisExpression extends PrimaryExpression {
  
  private Option<String> className;
  
  public ThisExpression(Option<String> cn, SourceInfo si) {
    super(si);
    if (cn == null) throw new IllegalArgumentException("cn == null");
    className = cn;
  }
  
  public ThisExpression(Option<String> cn) { this(cn, SourceInfo.NONE); }
  
  
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
    return "("+getClass().getName()+": "+getClassName()+")";
  }
}
