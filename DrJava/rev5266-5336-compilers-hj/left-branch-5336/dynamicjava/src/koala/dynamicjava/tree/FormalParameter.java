

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.*;



public class FormalParameter extends Declaration {
  
  private TypeName type;
  
  
  private String name;
  
  
  public FormalParameter(ModifierSet mods, TypeName t, String n) {
    this(mods, t, n, SourceInfo.NONE);
  }
  
  
  public FormalParameter(ModifierSet mods, TypeName t, String n,
                         SourceInfo si) {
    super(mods, si);
    
    if (t == null) throw new IllegalArgumentException("t == null");
    if (n == null) throw new IllegalArgumentException("n == null");
    type           = t;
    name           = n;
  }
  
  
  public TypeName getType() {
    return type;
  }
  
  
  public void setType(TypeName t) {
    if (t == null) throw new IllegalArgumentException("t == null");
    type = t;
  }
  
  
  public String getName() {
    return name;
  }
  
  
  public void setName(String s) {
    if (s == null) throw new IllegalArgumentException("s == null");
    name = s;
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
   
  public String toString() {
    return "("+getClass().getName()+": "+getModifiers()+" "+getType()+" "+getName()+")";
  }
}
