

package edu.rice.cs.plt.tuple;

import java.io.Serializable;

import edu.rice.cs.plt.lambda.Thunk;
import edu.rice.cs.plt.lambda.Lambda;


public class Wrapper<T> extends Option<T> implements Thunk<T> {
  
  protected final T _value;
  
  public Wrapper(T value) { _value = value; }
  
  public T value() { return _value; }
  
  public T unwrap() { return _value; }
  
  public T unwrap(T forNone) { return _value; }
  
  public <Ret> Ret apply(OptionVisitor<? super T, ? extends Ret> visitor) {
    return visitor.forSome(_value);
  }
  
  public boolean isSome() { return true; }
  
  public String toString() { return "(" + _value + ")"; }
  
  
  public boolean equals(Object o) {
    if (this == o) { return true; }
    else if (o == null || !getClass().equals(o.getClass())) { return false; }
    else {
      Wrapper<?> cast = (Wrapper<?>) o;
      return _value == null ? cast._value == null : _value.equals(cast._value);
    }
  }
  
  protected int generateHashCode() {
    return (_value == null ? 0 : _value.hashCode()) ^ getClass().hashCode();
  }
  
  
  public static <T> Wrapper<T> make(T value) { return new Wrapper<T>(value); }
  
  
  @SuppressWarnings("unchecked") public static <T> Lambda<T, Wrapper<T>> factory() {
    return (Factory<T>) Factory.INSTANCE;
  }
  
  private static final class Factory<T> implements Lambda<T, Wrapper<T>>, Serializable {
    public static final Factory<Object> INSTANCE = new Factory<Object>();
    private Factory() {}
    public Wrapper<T> value(T val) { return new Wrapper<T>(val); }
  }
  
}
