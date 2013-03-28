

package koala.dynamicjava.tree;

import java.util.*;

import koala.dynamicjava.tree.tiger.TypeParameter;

import edu.rice.cs.plt.tuple.Option;



public abstract class TypeDeclaration extends Declaration {
  private String name;
  private Option<List<TypeParameter>> typeParams;
  private List<? extends ReferenceTypeName> interfaces; 
  private List<Node> members;

  
  protected TypeDeclaration(ModifierSet mods, String name, Option<List<TypeParameter>> tparams,
                             List<? extends ReferenceTypeName> impl, List<Node> body,
                             SourceInfo si) {
    super(mods, si);
    if (name == null || tparams == null || body == null) throw new IllegalArgumentException();
    this.name = name;
    typeParams = tparams;
    interfaces = impl;
    members = body;
  }

  
  public String getName() {
    return name;
  }

  
  public void setName(String s) {
    if (s == null) throw new IllegalArgumentException("s == null");
    name = s;
  }

  public Option<List<TypeParameter>> getTypeParams() { return typeParams; }
  public void setTypeArgs(List<TypeParameter> tparams) { typeParams = Option.wrap(tparams); }
  public void setTypeArgs(Option<List<TypeParameter>> tparams) {
    if (tparams == null) throw new IllegalArgumentException();
    typeParams = tparams;
  }
  
  
  public List<? extends ReferenceTypeName> getInterfaces() {
    return interfaces;
  }

  
  public void setInterfaces(List<? extends ReferenceTypeName> l) {
    interfaces = l;
  }

  
  public List<Node> getMembers() {
    return members;
  }

  
  public void setMembers(List<Node> l) {
    if (l == null) throw new IllegalArgumentException("l == null");
    members = l;
  }
}
