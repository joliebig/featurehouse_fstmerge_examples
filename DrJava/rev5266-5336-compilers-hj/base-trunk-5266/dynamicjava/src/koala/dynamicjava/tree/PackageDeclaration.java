

package koala.dynamicjava.tree;

import java.util.*;

import koala.dynamicjava.tree.visitor.*;



public class PackageDeclaration extends Declaration {
  private String name;
  
  
  public PackageDeclaration(ModifierSet mods, List<IdentifierToken> ident) {
    this(mods, ident, SourceInfo.NONE);
  }
  
  
  public PackageDeclaration(ModifierSet mods, List<IdentifierToken> ident, SourceInfo si) {
    super(mods, si);
    name = TreeUtilities.listToName(ident);
  }
  
  
  public PackageDeclaration(ModifierSet mods, String nm, SourceInfo si) {
    super(mods, si);
    name = nm;
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
    return "("+getClass().getName()+": "+getName()+")";
  }
}
