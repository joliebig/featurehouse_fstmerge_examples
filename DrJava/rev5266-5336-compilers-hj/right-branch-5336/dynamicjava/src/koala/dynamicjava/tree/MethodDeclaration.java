

package koala.dynamicjava.tree;

import java.util.*;

import edu.rice.cs.plt.tuple.Option;

import koala.dynamicjava.tree.tiger.TypeParameter;
import koala.dynamicjava.tree.visitor.*;



public class MethodDeclaration extends Declaration {
  
  private Option<List<TypeParameter>> typeParams;
  private TypeName returnType;
  private String name;
  private List<FormalParameter> parameters;
  private List<? extends ReferenceTypeName> exceptions;
  private BlockStatement body;

  
  public MethodDeclaration(ModifierSet mods, TypeName type, String name,
                           List<FormalParameter> params, List<? extends ReferenceTypeName> excepts, BlockStatement body) {
    this(mods, Option.<List<TypeParameter>>none(), type, name, params, excepts, body, SourceInfo.NONE);
  }

  
  public MethodDeclaration(ModifierSet mods, Option<List<TypeParameter>> tparams, TypeName type, String name,
                           List<FormalParameter> params, List<? extends ReferenceTypeName> excepts, BlockStatement body) {
    this(mods, tparams, type, name, params, excepts, body, SourceInfo.NONE);
  }

  
  public MethodDeclaration(ModifierSet mods, TypeName type, String name,
                           List<FormalParameter> params, List<? extends ReferenceTypeName> excepts, BlockStatement body,
                           SourceInfo si) {
    this(mods, Option.<List<TypeParameter>>none(), type, name, params, excepts, body, si);
  }
  
  public MethodDeclaration(ModifierSet mods, Option<List<TypeParameter>> tparams, TypeName type, String name,
                           List<FormalParameter> params, List<? extends ReferenceTypeName> excepts, BlockStatement body,
                           SourceInfo si) {
    super(mods, si);

    if (tparams == null) throw new IllegalArgumentException("tparams == null");
    if (type == null)    throw new IllegalArgumentException("type == null");
    if (name == null)    throw new IllegalArgumentException("name == null");
    if (params == null)  throw new IllegalArgumentException("params == null");
    if (excepts == null) throw new IllegalArgumentException("excepts == null");

    typeParams  = tparams;
    returnType  = type;
    this.name   = name;
    parameters  = params;
    this.body   = body;
    exceptions = excepts;
  }

  public Option<List<TypeParameter>> getTypeParams() { return typeParams; }
  public void setTypeArgs(List<TypeParameter> tparams) { typeParams = Option.wrap(tparams); }
  public void setTypeArgs(Option<List<TypeParameter>> tparams) {
    if (tparams == null) throw new IllegalArgumentException();
    typeParams = tparams;
  }
  
  
  public TypeName getReturnType() {
    return returnType;
  }

  
  public void setReturnType(TypeName t) {
    if (t == null) throw new IllegalArgumentException("t == null");
    returnType = t;
  }

  
  public String getName() {
    return name;
  }

  
  public void setName(String s) {
    if (s == null) throw new IllegalArgumentException("s == null");
    name = s;
  }

  
  public List<FormalParameter> getParameters() {
    return parameters;
  }

  
  public void setParameters(List<FormalParameter> l) {
    if (l == null) throw new IllegalArgumentException("l == null");
    parameters = l;
  }

  
  public List<? extends ReferenceTypeName> getExceptions() {
    return exceptions;
  }

  
  public void setExceptions(List<? extends ReferenceTypeName> l) {
    if (l == null) throw new IllegalArgumentException("l == null");
    exceptions = l;
  }

  
  public BlockStatement getBody() {
    return body;
  }

  
  public void setBody(BlockStatement bs) {
    body = bs;
  }


  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
    
  public String toString() {
    return "("+getClass().getName()+": "+toStringHelper()+")";
  }

  public String toStringHelper() {
 return getModifiers()+" "+getTypeParams()+" "+getReturnType()+" "+getName()+" "+getParameters()+" "+getExceptions()+" "+getBody();
  }
}
