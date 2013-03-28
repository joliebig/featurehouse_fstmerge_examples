

package edu.rice.cs.plt.reflect;

import java.lang.reflect.InvocationTargetException;


public abstract class ReflectException extends Exception {
  
  protected ReflectException(Throwable cause) { super(cause); }
  public abstract <T> T apply(ReflectExceptionVisitor<T> v);
  
  
  public static class ClassNotFoundReflectException extends ReflectException {
    public ClassNotFoundReflectException(ClassNotFoundException e) { super(e); }
    public <T> T apply(ReflectExceptionVisitor<T> v) {
      return v.forClassNotFound((ClassNotFoundException) getCause());
    }
  }
  
  
  public static class NoSuchFieldReflectException extends ReflectException {
    public NoSuchFieldReflectException(NoSuchFieldException e) { super(e); }
    public <T> T apply(ReflectExceptionVisitor<T> v) {
      return v.forNoSuchField((NoSuchFieldException) getCause());
    }
  }
  
  
  public static class NoSuchMethodReflectException extends ReflectException {
    public NoSuchMethodReflectException(NoSuchMethodException e) { super(e); }
    public <T> T apply(ReflectExceptionVisitor<T> v) {
      return v.forNoSuchMethod((NoSuchMethodException) getCause());
    }
  }
  
  
  public static class NullPointerReflectException extends ReflectException {
    public NullPointerReflectException(NullPointerException e) { super(e); }
    public <T> T apply(ReflectExceptionVisitor<T> v) {
      return v.forNullPointer((NullPointerException) getCause());
    }
  }    
  
  
  public static class IllegalArgumentReflectException extends ReflectException {
    public IllegalArgumentReflectException(IllegalArgumentException e) { super(e); }
    public <T> T apply(ReflectExceptionVisitor<T> v) {
      return v.forIllegalArgument((IllegalArgumentException) getCause());
    }
  }
  
  
  public static class ClassCastReflectException extends ReflectException {
    public ClassCastReflectException(ClassCastException e) { super(e); }
    public <T> T apply(ReflectExceptionVisitor<T> v) {
      return v.forClassCast((ClassCastException) getCause());
    }
  }
  
  
  public static class InvocationTargetReflectException extends ReflectException {
    public InvocationTargetReflectException(InvocationTargetException e) { super(e); }
    public <T> T apply(ReflectExceptionVisitor<T> v) {
      return v.forInvocationTarget((InvocationTargetException) getCause());
    }
  }
  
  
  public static class InstantiationReflectException extends ReflectException {
    public InstantiationReflectException(InstantiationException e) { super(e); }
    public <T> T apply(ReflectExceptionVisitor<T> v) {
      return v.forInstantiation((InstantiationException) getCause());
    }
  }
  
  
  public static class IllegalAccessReflectException extends ReflectException {
    public IllegalAccessReflectException(IllegalAccessException e) { super(e); }
    public <T> T apply(ReflectExceptionVisitor<T> v) {
      return v.forIllegalAccess((IllegalAccessException) getCause());
    }
  }
  
  
  public static class SecurityReflectException extends ReflectException {
    public SecurityReflectException(SecurityException e) { super(e); }
    public <T> T apply(ReflectExceptionVisitor<T> v) {
      return v.forSecurity((SecurityException) getCause());
    }
  }
  
}
