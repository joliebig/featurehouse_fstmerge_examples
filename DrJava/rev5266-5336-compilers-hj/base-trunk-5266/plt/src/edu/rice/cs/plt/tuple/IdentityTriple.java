

package edu.rice.cs.plt.tuple;

import java.io.Serializable;
import edu.rice.cs.plt.lambda.Lambda3;


public class IdentityTriple<T1, T2, T3> extends Triple<T1, T2, T3> {
  
  public IdentityTriple(T1 first, T2 second, T3 third) { super(first, second, third); }
  
  
  public boolean equals(Object o) {
    if (this == o) { return true; }
    else if (o == null || !getClass().equals(o.getClass())) { return false; }
    else {
      Triple<?, ?, ?> cast = (Triple<?, ?, ?>) o;
      return 
        _first == cast._first &&
        _second == cast._second &&
        _third == cast._third;
    }
  }
  
  protected int generateHashCode() {
    return 
      System.identityHashCode(_first) ^ 
      (System.identityHashCode(_second) << 1) ^ 
      (System.identityHashCode(_third) << 2) ^
      getClass().hashCode();
  }
  
  
  public static <T1, T2, T3> IdentityTriple<T1, T2, T3> make(T1 first, T2 second, T3 third) {
    return new IdentityTriple<T1, T2, T3>(first, second, third);
  }
  
  
  @SuppressWarnings("unchecked") public static <T1, T2, T3> Lambda3<T1, T2, T3, Triple<T1, T2, T3>> factory() {
    return (Factory<T1, T2, T3>) Factory.INSTANCE;
  }
  
  private static final class Factory<T1, T2, T3> implements Lambda3<T1, T2, T3, Triple<T1, T2, T3>>, Serializable {
    public static final Factory<Object, Object, Object> INSTANCE = new Factory<Object, Object, Object>();
    private Factory() {}
    public Triple<T1, T2, T3> value(T1 first, T2 second, T3 third) {
      return new IdentityTriple<T1, T2, T3>(first, second, third);
    }
  }
  
}
