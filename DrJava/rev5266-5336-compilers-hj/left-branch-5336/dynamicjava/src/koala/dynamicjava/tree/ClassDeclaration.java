

package koala.dynamicjava.tree;

import java.util.*;

import edu.rice.cs.plt.tuple.Option;

import koala.dynamicjava.tree.tiger.TypeParameter;
import koala.dynamicjava.tree.visitor.*;



public class ClassDeclaration extends TypeDeclaration {
  
  private static final ReferenceTypeName OBJECT = new ReferenceTypeName("java", "lang", "Object");

  
  private ReferenceTypeName superclass;

  
  public ClassDeclaration(ModifierSet mods, String name, ReferenceTypeName ext, List<? extends ReferenceTypeName> impl, List<Node> body) {
    this(mods, name, Option.<List<TypeParameter>>none(), ext, impl, body, SourceInfo.NONE);
  }

  
  public ClassDeclaration(ModifierSet mods, String name, Option<List<TypeParameter>> tparams, ReferenceTypeName ext,
                           List<? extends ReferenceTypeName> impl, List<Node> body) {
    this(mods, name, tparams, ext, impl, body, SourceInfo.NONE);
  }

  
  public ClassDeclaration(ModifierSet mods, String name, ReferenceTypeName ext,
                           List<? extends ReferenceTypeName> impl, List<Node> body, SourceInfo si) {
    this(mods, name, Option.<List<TypeParameter>>none(), ext, impl, body, si);
  }

  
  public ClassDeclaration(ModifierSet mods, String name, Option<List<TypeParameter>> tparams, ReferenceTypeName ext,
                           List<? extends ReferenceTypeName> impl, List<Node> body, SourceInfo si) {
    super(mods, name, tparams, impl, body, si);
    superclass = (ext == null) ? OBJECT : ext;
  }
  

  
  public ReferenceTypeName getSuperclass() {
    return superclass;
  }

  
  public void setSuperclass(ReferenceTypeName s) {
    if (s == null) throw new IllegalArgumentException("s == null");
    superclass = s;
  }

  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
   
  public String toString() {
    return "("+getClass().getName()+": "+toStringHelper()+")";
  }

  protected String toStringHelper() {
    return getModifiers()+" "+getName()+" "+getTypeParams()+" "+getSuperclass()+" "+getInterfaces()+" "+getMembers();
  }
}
