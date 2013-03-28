

package edu.rice.cs.plt.tuple;

import java.io.Serializable;
import edu.rice.cs.plt.lambda.Lambda4;


public class IdentityQuad<T1, T2, T3, T4> extends Quad<T1, T2, T3, T4> {
  
  public IdentityQuad(T1 first, T2 second, T3 third, T4 fourth) { 
    super(first, second, third, fourth);
  }
  
  
  public boolean equals(Object o) {
    if (this == o) { return true; }
    else if (o == null || !getClass().equals(o.getClass())) { return false; }
    else {
      Quad<?, ?, ?, ?> cast = (Quad<?, ?, ?, ?>) o;
      return 
        _first == cast._first &&
        _second == cast._second &&
        _third == cast._third &&
        _fourth == cast._fourth;
    }
  }
  
  protected int generateHashCode() {
    return 
      System.identityHashCode(_first) ^ 
      (System.identityHashCode(_second) << 1) ^ 
      (System.identityHashCode(_third) << 2) ^ 
      (System.identityHashCode(_fourth) << 3) ^ 
      getClass().hashCode();
  }
  
  
  public static <T1, T2, T3, T4> IdentityQuad<T1, T2, T3, T4> make(T1 first, T2 second, T3 third, 
                                                                   T4 fourth) {
    return new IdentityQuad<T1, T2, T3, T4>(first, second, third, fourth);
  }
  
  
  @SuppressWarnings("unchecked") public static 
    <T1, T2, T3, T4> Lambda4<T1, T2, T3, T4, Quad<T1, T2, T3, T4>> factory() {
    return (Factory<T1, T2, T3, T4>) Factory.INSTANCE;
  }
  
  
  private static final class Factory<T1, T2, T3, T4> 
    implements Lambda4<T1, T2, T3, T4, Quad<T1, T2, T3, T4>>, Serializable {
    public static final Factory<Object, Object, Object, Object> INSTANCE = 
      new Factory<Object, Object, Object, Object>();
    private Factory() {}
    public Quad<T1, T2, T3, T4> value(T1 first, T2 second, T3 third, T4 fourth) {
      return new IdentityQuad<T1, T2, T3, T4>(first, second, third, fourth);
    }
  }
  
}
