

package edu.rice.cs.plt.reflect;

import java.lang.reflect.InvocationTargetException;


public abstract class ReflectExceptionVisitor<T> {
  
  protected abstract T defaultCase(Exception e);
  
  
  public T forClassNotFound(ClassNotFoundException e) { return defaultCase(e); }
  
  
  public T forNoSuchField(NoSuchFieldException e) { return defaultCase(e); }

  
  public T forNoSuchMethod(NoSuchMethodException e) { return defaultCase(e); }
  
  
  public T forIllegalArgument(IllegalArgumentException e) { return defaultCase(e); }
  
  
  public T forNullPointer(NullPointerException e) { return defaultCase(e); }
  
  
  public T forClassCast(ClassCastException e) { return defaultCase(e); }
  
  
  public T forInvocationTarget(InvocationTargetException e) { return defaultCase(e); }
  
  
  public T forInstantiation(InstantiationException e) { return defaultCase(e); }
  
  
  public T forIllegalAccess(IllegalAccessException e) { return defaultCase(e); }
  
  
  public T forSecurity(SecurityException e) { return defaultCase(e); }
}
