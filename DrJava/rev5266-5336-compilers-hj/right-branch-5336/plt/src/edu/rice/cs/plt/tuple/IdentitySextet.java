

package edu.rice.cs.plt.tuple;


public class IdentitySextet<T1, T2, T3, T4, T5, T6> extends Sextet<T1, T2, T3, T4, T5, T6> {
  
  public IdentitySextet(T1 first, T2 second, T3 third, T4 fourth, T5 fifth, T6 sixth) {
    super(first, second, third, fourth, fifth, sixth);
  }
  
  
  public boolean equals(Object o) {
    if (this == o) { return true; }
    else if (o == null || !getClass().equals(o.getClass())) { return false; }
    else {
      Sextet<?, ?, ?, ?, ?, ?> cast = (Sextet<?, ?, ?, ?, ?, ?>) o;
      return 
        _first == cast._first &&
        _second == cast._second &&
        _third == cast._third &&
        _fourth == cast._fourth &&
        _fifth == cast._fifth &&
        _sixth == cast._sixth;
    }
  }
  
  protected int generateHashCode() {
    return 
      System.identityHashCode(_first) ^ 
      (System.identityHashCode(_second) << 1) ^ 
      (System.identityHashCode(_third) << 2) ^ 
      (System.identityHashCode(_fourth) << 3) ^ 
      (System.identityHashCode(_fifth) << 4) ^ 
      (System.identityHashCode(_sixth) << 5) ^ 
      getClass().hashCode();
  }
    
  
  public static <T1, T2, T3, T4, T5, T6> 
    IdentitySextet<T1, T2, T3, T4, T5, T6> make(T1 first, T2 second, T3 third, T4 fourth, 
                                                T5 fifth, T6 sixth) {
    return new IdentitySextet<T1, T2, T3, T4, T5, T6>(first, second, third, fourth, fifth, sixth);
  }
  
}
