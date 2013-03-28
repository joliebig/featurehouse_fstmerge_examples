package edu.rice.cs.dynamicjava.symbol;

import edu.rice.cs.dynamicjava.symbol.type.Type;
import edu.rice.cs.dynamicjava.symbol.type.VariableType;


public interface DJClass extends Access.Limited, Access.Module {
  
  public String packageName();
  
  
  public String fullName();
  
  public boolean isAnonymous();
  
  
  public String declaredName();
  
  public boolean isInterface();
  
  public boolean isStatic();
  
  public boolean isAbstract();
  
  public boolean isFinal();
  
  public Access accessibility();
  
  public boolean hasRuntimeBindingsParams();
  
  
  public DJClass declaringClass();
  
  
  public Iterable<VariableType> declaredTypeParameters();
  
  
  public Iterable<Type> declaredSupertypes();

  public Iterable<DJField> declaredFields();
  
  public Iterable<DJConstructor> declaredConstructors();
  
  public Iterable<DJMethod> declaredMethods();
  
  public Iterable<DJClass> declaredClasses();
  
  
  public Type immediateSuperclass();
  
  
  public Class<?> load();
  
  
  public boolean equals(Object o);
  
  
  public int hashCode();

} 
