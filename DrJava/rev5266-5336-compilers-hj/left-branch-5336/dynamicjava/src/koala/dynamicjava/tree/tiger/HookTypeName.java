

package koala.dynamicjava.tree.tiger;

import edu.rice.cs.plt.tuple.Option;

import koala.dynamicjava.tree.*;
import koala.dynamicjava.tree.visitor.Visitor;



public class HookTypeName extends ReferenceTypeName {
  
  private final Option<TypeName> upperBound;
  private final Option<TypeName> lowerBound;
  
  
  public HookTypeName(Option<TypeName> up, Option<TypeName> low) {
    this(up, low, SourceInfo.NONE);
  }

  
  public HookTypeName(Option<TypeName> up, Option<TypeName> low,
                      SourceInfo si) {
    super(new String[]{"?"}, si);

    if (up == null) throw new IllegalArgumentException("up == null");
    if (low == null) throw new IllegalArgumentException("low == null");
    upperBound = up;
    lowerBound = low;
  }

  public Option<TypeName> getUpperBound() { return upperBound; }
  public Option<TypeName> getLowerBound() { return lowerBound; }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
    
  public String toString() {
    return "("+getClass().getName()+": "+toStringHelper()+")";
  }

  protected String toStringHelper() {
   return upperBound + " " + lowerBound;
  }
}
