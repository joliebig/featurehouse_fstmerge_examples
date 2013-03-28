

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.*;



public class ClassInitializer extends Initializer {
  
  public ClassInitializer(BlockStatement block) {
    this(block, SourceInfo.NONE);
  }
  
  
  public ClassInitializer(BlockStatement block,
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
