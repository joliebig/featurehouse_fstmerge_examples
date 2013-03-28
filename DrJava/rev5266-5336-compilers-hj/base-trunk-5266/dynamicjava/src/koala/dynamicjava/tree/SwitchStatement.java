

package koala.dynamicjava.tree;

import java.util.*;

import koala.dynamicjava.tree.visitor.*;



public class SwitchStatement extends Statement {
  
  private Expression selector;
  
  
  private List<SwitchBlock> bindings;
  
  
  public SwitchStatement(Expression sel, List<SwitchBlock> cases,
                         SourceInfo si) {
    super(si);
    
    if (sel == null)   throw new IllegalArgumentException("sel == null");
    if (cases == null) throw new IllegalArgumentException("cases == null");
    
    selector          = sel;
    bindings          = cases;
  }
  
  
  public Expression getSelector() {
    return selector;
  }
  
  
  public void setSelector(Expression e) {
    if (e == null) throw new IllegalArgumentException("e == null");
    selector = e;
  }
  
  
  public List<SwitchBlock> getBindings() {
    return bindings;
  }
  
  
  public void setBindings(List<SwitchBlock> l) {
    if (l == null) throw new IllegalArgumentException("l == null");
    bindings = l;
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
    
  public String toString() {
    return "("+getClass().getName()+": "+getSelector()+" "+getBindings()+")";
  }
}
