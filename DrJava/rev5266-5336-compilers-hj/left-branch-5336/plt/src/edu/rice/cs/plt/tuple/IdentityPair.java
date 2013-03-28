

package edu.rice.cs.plt.tuple;

import java.io.Serializable;
import edu.rice.cs.plt.lambda.Lambda2;


public class IdentityPair<T1, T2> extends Pair<T1, T2> {
  
  public IdentityPair(T1 first, T2 second) { super(first, second); }
  
  @Override public IdentityPair<T2, T1> inverse() { return new IdentityPair<T2, T1>(_second, _first); }
  
  
  public boolean equals(Object o) {
    if (this == o) { return true; }
    else if (o == null || !getClass().equals(o.getClass())) { return false; }
    else {
      Pair<?, ?> cast = (Pair<?, ?>) o;
      return 
        _first == cast._first &&
        _second == cast._second;
    }
  }
  
  protected int generateHashCode() {
    return
      System.identityHashCode(_first) ^ 
      (System.identityHashCode(_second) << 1) ^ 
      getClass().hashCode();
  }
  
  
  public static <T1, T2> IdentityPair<T1, T2> make(T1 first, T2 second) {
    return new IdentityPair<T1, T2>(first, second);
  }
  
  
  @SuppressWarnings("unchecked") public static <T1, T2> Lambda2<T1, T2, Pair<T1, T2>> factory() {
    return (Factory<T1, T2>) Factory.INSTANCE;
  }
  
  private static final class Factory<T1, T2> implements Lambda2<T1, T2, Pair<T1, T2>>, Serializable {
    public static final Factory<Object, Object> INSTANCE = new Factory<Object, Object>();
    private Factory() {}
    public Pair<T1, T2> value(T1 first, T2 second) { return new IdentityPair<T1, T2>(first, second); }
  }
  
}
