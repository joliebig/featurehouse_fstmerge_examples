

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.*;



public class InstanceInitializer extends Initializer {
  
  public InstanceInitializer(BlockStatement block) {
    this(block, SourceInfo.NONE);
  }
  
  
  public InstanceInitializer(BlockStatement block,
                             SourceInfo si) {
    super(block, si);
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }    
   
  public String toString() {
    return "("+getClass().getName()+": "+getBlock()+")";
  }
}
