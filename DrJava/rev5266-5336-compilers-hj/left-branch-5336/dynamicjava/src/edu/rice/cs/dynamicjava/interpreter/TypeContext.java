package edu.rice.cs.dynamicjava.interpreter;

import edu.rice.cs.dynamicjava.symbol.*;
import edu.rice.cs.dynamicjava.symbol.type.Type;
import edu.rice.cs.dynamicjava.symbol.type.ClassType;
import edu.rice.cs.dynamicjava.symbol.type.VariableType;


public interface TypeContext {
  
  
  
  
  public TypeContext setPackage(String name);
  
  
  public TypeContext importTopLevelClasses(String pkg);
  
  
  public TypeContext importMemberClasses(DJClass outer);
  
  
  public TypeContext importStaticMembers(DJClass c);
  
  
  public TypeContext importTopLevelClass(DJClass c);
  
  
  public TypeContext importMemberClass(DJClass outer, String name);
  
  
  public TypeContext importField(DJClass c, String name);
  
  
  public TypeContext importMethod(DJClass c, String name);   
  
  
  
  
  
  public boolean typeExists(String name, TypeSystem ts);
  
  
  public boolean topLevelClassExists(String name, TypeSystem ts);
  
  
  public DJClass getTopLevelClass(String name, TypeSystem ts) throws AmbiguousNameException;
  
  
  public boolean memberClassExists(String name, TypeSystem ts);
  
  
  public ClassType typeContainingMemberClass(String name, TypeSystem ts) throws AmbiguousNameException;
  
  
  public boolean typeVariableExists(String name, TypeSystem ts);
  
  
  public VariableType getTypeVariable(String name, TypeSystem ts);
  

    
  
  
  public boolean variableExists(String name, TypeSystem ts);
  
  
  public boolean fieldExists(String name, TypeSystem ts);
  
  
  public ClassType typeContainingField(String name, TypeSystem ts) throws AmbiguousNameException;
  
  
  public boolean localVariableExists(String name, TypeSystem ts);
  
  
  public LocalVariable getLocalVariable(String name, TypeSystem ts);
  
  
  
  
  
  public boolean functionExists(String name, TypeSystem ts);
  
  
  public boolean methodExists(String name, TypeSystem ts);
  
  
  public Type typeContainingMethod(String name, TypeSystem ts);
  
  
  public boolean localFunctionExists(String name, TypeSystem ts);
  
  
  public Iterable<LocalFunction> getLocalFunctions(String name, TypeSystem ts);
  
  
  public Iterable<LocalFunction> getLocalFunctions(String name, TypeSystem ts, Iterable<LocalFunction> partial);
  
  
  
  
  
  public Access.Module accessModule();
  
  
  public String makeClassName(String declaredName);
  
  
  public String makeAnonymousClassName();
    
  
  public DJClass getThis();
  
  
  public DJClass getThis(String className);
  
  
  public DJClass getThis(Type expected, TypeSystem ts);
  
  
  public DJClass initializingClass();
  
  
  public Type getReturnType();
  
  
  public Iterable<Type> getDeclaredThrownTypes();
  
  
  public ClassLoader getClassLoader();
  
}
