

package koala.dynamicjava.tree;

import java.util.*;

import edu.rice.cs.plt.tuple.Option;

import koala.dynamicjava.tree.tiger.TypeParameter;
import koala.dynamicjava.tree.visitor.*;



public class InterfaceDeclaration extends TypeDeclaration {

  private boolean annotation;
  
  
  public InterfaceDeclaration(ModifierSet mods, boolean ann, String name, Option<List<TypeParameter>> tparams,
                               List<? extends ReferenceTypeName> impl, List<Node> body) {
    this(mods, ann, name, tparams, impl, body, SourceInfo.NONE);
  }

  
  public InterfaceDeclaration(ModifierSet mods, boolean ann, String name,
                               List<? extends ReferenceTypeName> impl, List<Node> body) {
    this(mods, ann, name, Option.<List<TypeParameter>>none(), impl, body, SourceInfo.NONE);
  }

  
  public InterfaceDeclaration(ModifierSet mods, boolean ann, String name, Option<List<TypeParameter>> tparams,
                               List<? extends ReferenceTypeName> impl, List<Node> body, SourceInfo si) {
    super(mods, name, tparams, ann ? addAnnotationType(impl, si) : impl, body, si);
    annotation = ann;
  }
  
  private static List<ReferenceTypeName> addAnnotationType(List<? extends ReferenceTypeName> impl, SourceInfo si) {
    List<ReferenceTypeName> result = new ArrayList<ReferenceTypeName>();
    result.add(new ReferenceTypeName(new String[]{ "java", "lang", "annotation", "Annotation" }, si));
    if (impl != null) { result.addAll(impl); }
    return result;
  }

  
  public InterfaceDeclaration(ModifierSet mods, boolean ann, String name,
                               List<? extends ReferenceTypeName> impl, List<Node> body, SourceInfo si) {
    this(mods, ann, name, Option.<List<TypeParameter>>none(), impl, body, si);
  }
  
  
  public boolean isAnnotation() {
    return annotation;
  }

  
  public void setIsAnnotation(boolean ann) {
    annotation = ann;
  }


  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
     
  public String toString() {
    return "("+getClass().getName()+": "+toStringHelper()+")";
  }

  protected String toStringHelper() {
    return getModifiers()+" "+isAnnotation()+" "+getName()+" "+getTypeParams()+" "+getInterfaces()+" "+getMembers();
  }
}
