

package edu.rice.cs.plt.tuple;

import java.io.Serializable;
import edu.rice.cs.plt.lambda.Lambda;


public class IdentityWrapper<T> extends Wrapper<T> {
  
  public IdentityWrapper(T value) { super(value); }
  
  
  public boolean equals(Object o) {
    if (this == o) { return true; }
    else if (o == null || !getClass().equals(o.getClass())) { return false; }
    else {
      Wrapper<?> cast = (Wrapper<?>) o;
      return _value == cast._value;
    }
  }
  
  protected int generateHashCode() { 
    return System.identityHashCode(_value) ^ getClass().hashCode();
  }
  
  
  public static <T> IdentityWrapper<T> make(T value) { return new IdentityWrapper<T>(value); }
  
  
  @SuppressWarnings("unchecked") public static <T> Lambda<T, Wrapper<T>> factory() {
    return (Factory<T>) Factory.INSTANCE;
  }
  
  private static final class Factory<T> implements Lambda<T, Wrapper<T>>, Serializable {
    public static final Factory<Object> INSTANCE = new Factory<Object>();
    private Factory() {}
    public Wrapper<T> value(T val) { return new IdentityWrapper<T>(val); }
  }
  
}
