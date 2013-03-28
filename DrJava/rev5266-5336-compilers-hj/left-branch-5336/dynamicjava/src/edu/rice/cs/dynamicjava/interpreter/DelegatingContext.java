package edu.rice.cs.dynamicjava.interpreter;

import edu.rice.cs.plt.iter.IterUtil;

import edu.rice.cs.dynamicjava.symbol.*;
import edu.rice.cs.dynamicjava.symbol.type.Type;
import edu.rice.cs.dynamicjava.symbol.type.ClassType;
import edu.rice.cs.dynamicjava.symbol.type.VariableType;

import static edu.rice.cs.plt.debug.DebugUtil.debug;


public abstract class DelegatingContext implements TypeContext {
  
  private TypeContext _next;
  
  protected DelegatingContext(TypeContext next) {
    _next = next;
  }
  
  
  protected abstract TypeContext duplicate(TypeContext next);

  
  
  
  
  public TypeContext setPackage(String name) {
    return duplicate(_next.setPackage(name));
  }
  
  
  public TypeContext importTopLevelClasses(String pkg) {
    return duplicate(_next.importTopLevelClasses(pkg));
  }
  
  
  public TypeContext importMemberClasses(DJClass outer) {
    return duplicate(_next.importMemberClasses(outer));
  }
  
  
  public TypeContext importStaticMembers(DJClass c) {
    return duplicate(_next.importStaticMembers(c));
  }
  
  
  public TypeContext importTopLevelClass(DJClass c) {
    return duplicate(_next.importTopLevelClass(c));
  }
  
  
  public TypeContext importMemberClass(DJClass outer, String name) {
    return duplicate(_next.importMemberClass(outer, name));
  }
  
  
  public TypeContext importField(DJClass c, String name) {
    return duplicate(_next.importField(c, name));
  }
  
  
  public TypeContext importMethod(DJClass c, String name) {
    return duplicate(_next.importMethod(c, name));
  }
  
  
  
  
  
  public boolean typeExists(String name, TypeSystem ts) {
    return _next.typeExists(name, ts);
  }
  
  
  public boolean topLevelClassExists(String name, TypeSystem ts) {
    return _next.topLevelClassExists(name, ts);
  }
  
  
  public DJClass getTopLevelClass(String name, TypeSystem ts) throws AmbiguousNameException {
    return _next.getTopLevelClass(name, ts);
  }
  
  
  public boolean memberClassExists(String name, TypeSystem ts) {
    return _next.memberClassExists(name, ts);
  }
  
  
  public ClassType typeContainingMemberClass(String name, TypeSystem ts) throws AmbiguousNameException {
    return _next.typeContainingMemberClass(name, ts);
  }
  
  
  public boolean typeVariableExists(String name, TypeSystem ts) {
    return _next.typeVariableExists(name, ts);
  }
  
  
  public VariableType getTypeVariable(String name, TypeSystem ts) {
    return _next.getTypeVariable(name, ts);
  }
  

    
  
  
  public boolean variableExists(String name, TypeSystem ts) {
    return _next.variableExists(name, ts);
  }
  
  
  public boolean fieldExists(String name, TypeSystem ts) {
    return _next.fieldExists(name, ts);
  }
  
  
  public ClassType typeContainingField(String name, TypeSystem ts) throws AmbiguousNameException {
    return _next.typeContainingField(name, ts);
  }
  
  
  public boolean localVariableExists(String name, TypeSystem ts) {
    return _next.localVariableExists(name, ts);
  }
  
  
  public LocalVariable getLocalVariable(String name, TypeSystem ts) {
    return _next.getLocalVariable(name, ts);
  }
  
  
  
  
  
  public boolean functionExists(String name, TypeSystem ts) {
    return _next.functionExists(name, ts);
  }
  
  
  public boolean methodExists(String name, TypeSystem ts) {
    return _next.methodExists(name, ts);
  }
  
  
  public Type typeContainingMethod(String name, TypeSystem ts) {
    return _next.typeContainingMethod(name, ts);
  }
  
  
  public boolean localFunctionExists(String name, TypeSystem ts) {
    return _next.localFunctionExists(name, ts);
  }
  
  
  public final Iterable<LocalFunction> getLocalFunctions(String name, TypeSystem ts) {
    return getLocalFunctions(name, ts, IterUtil.<LocalFunction>empty());
  }
  
  public Iterable<LocalFunction> getLocalFunctions(String name, TypeSystem ts, Iterable<LocalFunction> partial) {
    return _next.getLocalFunctions(name, ts, partial);
  }
    
  
  
  
  public Access.Module accessModule() {
    return _next.accessModule();
  }
  
  
  public String makeClassName(String n) {
    return _next.makeClassName(n);
  }
  
  
  public String makeAnonymousClassName() {
    return _next.makeAnonymousClassName();
  }
  
  
  public DJClass getThis() {
    return _next.getThis();
  }
  
  
  public DJClass getThis(String className) {
    return _next.getThis(className);
  }
  
  public DJClass getThis(Type expected, TypeSystem ts) { return _next.getThis(expected, ts); }
  
  public DJClass initializingClass() { return _next.initializingClass(); }
  
  
  public Type getReturnType() {
    return _next.getReturnType();
  }
  
  
  public Iterable<Type> getDeclaredThrownTypes() {
    return _next.getDeclaredThrownTypes();
  }
  
  public ClassLoader getClassLoader() {
    return _next.getClassLoader();
  }
  
}
