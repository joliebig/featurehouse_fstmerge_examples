

package edu.rice.cs.plt.tuple;


public class IdentityOctet<T1, T2, T3, T4, T5, T6, T7, T8> 
  extends Octet<T1, T2, T3, T4, T5, T6, T7, T8> {
  
  public IdentityOctet(T1 first, T2 second, T3 third, T4 fourth, T5 fifth, T6 sixth, T7 seventh, 
                       T8 eighth) {
    super(first, second, third, fourth, fifth, sixth, seventh, eighth);
  }
  
  
  public boolean equals(Object o) {
    if (this == o) { return true; }
    else if (o == null || !getClass().equals(o.getClass())) { return false; }
    else {
      Octet<?, ?, ?, ?, ?, ?, ?, ?> cast = (Octet<?, ?, ?, ?, ?, ?, ?, ?>) o;
      return 
        _first == cast._first &&
        _second == cast._second &&
        _third == cast._third &&
        _fourth == cast._fourth &&
        _fifth == cast._fifth &&
        _sixth == cast._sixth &&
        _seventh == cast._seventh &&
        _eighth == cast._eighth;
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
      (System.identityHashCode(_seventh) << 6) ^ 
      (System.identityHashCode(_eighth) << 7) ^ 
      getClass().hashCode();
  }
  
  
  public static <T1, T2, T3, T4, T5, T6, T7, T8> 
    IdentityOctet<T1, T2, T3, T4, T5, T6, T7, T8> make(T1 first, T2 second, T3 third, T4 fourth, 
                                                       T5 fifth, T6 sixth, T7 seventh, T8 eighth) {
    return new IdentityOctet<T1, T2, T3, T4, T5, T6, T7, T8>(first, second, third, fourth, fifth, 
                                                             sixth, seventh, eighth);
  }
  
}
