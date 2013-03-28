

package koala.dynamicjava.tree;

import java.util.*;

import edu.rice.cs.plt.tuple.Option;

import koala.dynamicjava.tree.tiger.TypeParameter;
import koala.dynamicjava.tree.visitor.*;



public class ConstructorDeclaration extends Declaration {
  private Option<List<TypeParameter>> typeParams;
  private String name;
  private List<FormalParameter> parameters;
  private List<? extends ReferenceTypeName> exceptions;
  private ConstructorCall constructorInvocation; 
  private List<Node> statements;

  private boolean varargs;

  
  public ConstructorDeclaration(ModifierSet mods, String name,
                                List<FormalParameter> params, List<? extends ReferenceTypeName> excepts,
                                ConstructorCall eci, List<Node> stmts) {
    this(mods, Option.<List<TypeParameter>>none(), name, params, excepts, eci, stmts, SourceInfo.NONE);
  }

  
  public ConstructorDeclaration(ModifierSet mods, Option<List<TypeParameter>> tparams, String name,
                                List<FormalParameter> params, List<? extends ReferenceTypeName> excepts,
                                ConstructorCall eci, List<Node> stmts) {
    this(mods, tparams, name, params, excepts, eci, stmts, SourceInfo.NONE);
  }

  
  public ConstructorDeclaration(ModifierSet mods, String name,
                                List<FormalParameter> params, List<? extends ReferenceTypeName> excepts,
                                ConstructorCall eci, List<Node> stmts,
                                SourceInfo si) {
    this(mods, Option.<List<TypeParameter>>none(), name, params, excepts, eci, stmts, si);
  }
  
  public ConstructorDeclaration(ModifierSet mods, Option<List<TypeParameter>> tparams, String name,
                                List<FormalParameter> params, List<? extends ReferenceTypeName> excepts,
                                ConstructorCall eci, List<Node> stmts,
                                SourceInfo si) {
    super(mods, si);

    if (tparams == null || name == null || params == null || excepts == null || stmts == null) {
      throw new IllegalArgumentException();
    }
    typeParams            = tparams;
    this.name             = name;
    parameters            = params;
    constructorInvocation = eci;
    statements            = stmts;
    exceptions            = excepts;
  }

  public Option<List<TypeParameter>> getTypeParams() { return typeParams; }
  public void setTypeArgs(List<TypeParameter> tparams) { typeParams = Option.wrap(tparams); }
  public void setTypeArgs(Option<List<TypeParameter>> tparams) {
    if (tparams == null) throw new IllegalArgumentException();
    typeParams = tparams;
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
    parameters = l;
  }

  
  public List<? extends ReferenceTypeName> getExceptions() {
    return exceptions;
  }

  
  public void setExceptions(List<? extends ReferenceTypeName> l) {
    if (l == null) throw new IllegalArgumentException("l == null");
    exceptions = l;
  }

  
  public ConstructorCall getConstructorCall() {
    return constructorInvocation;
  }

  
  public void setConstructorCall(ConstructorCall ci) {
    constructorInvocation = ci;
  }

  
  public List<Node> getStatements() {
    return statements;
  }

  
  public void setStatements(List<Node> l) {
    if (l == null) throw new IllegalArgumentException("l == null");
    statements = l;
  }

  public boolean isVarArgs(){
    return varargs;
  }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
   
  public String toString() {
    return "("+getClass().getName()+": "+toStringHelper()+")";
  }
  
  public String toStringHelper() {
    return getModifiers()+" "+getName()+" "+getParameters()+" "+getExceptions()+" "+getConstructorCall()+" "+getStatements();
  }
}
