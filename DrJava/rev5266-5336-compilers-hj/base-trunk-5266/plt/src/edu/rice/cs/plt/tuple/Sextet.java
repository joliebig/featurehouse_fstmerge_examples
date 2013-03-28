

package edu.rice.cs.plt.tuple;


public class Sextet<T1, T2, T3, T4, T5, T6> extends Tuple {
  
  protected final T1 _first;
  protected final T2 _second;
  protected final T3 _third;
  protected final T4 _fourth;
  protected final T5 _fifth;
  protected final T6 _sixth;
  
  public Sextet(T1 first, T2 second, T3 third, T4 fourth, T5 fifth, T6 sixth) {
    _first = first;
    _second = second;
    _third = third;
    _fourth = fourth;
    _fifth = fifth;
    _sixth = sixth;
  }
  
  public T1 first() { return _first; }
  public T2 second() { return _second; }
  public T3 third() { return _third; }
  public T4 fourth() { return _fourth; }
  public T5 fifth() { return _fifth; }
  public T6 sixth() { return _sixth; }

  public String toString() {
    return "(" + _first + ", " + _second + ", " + _third + ", " + _fourth + ", " + _fifth + ", " +
             _sixth + ")";
  }
  
  
  public boolean equals(Object o) {
    if (this == o) { return true; }
    else if (o == null || !getClass().equals(o.getClass())) { return false; }
    else {
      Sextet<?, ?, ?, ?, ?, ?> cast = (Sextet<?, ?, ?, ?, ?, ?>) o;
      return 
        (_first == null ? cast._first == null : _first.equals(cast._first)) &&
        (_second == null ? cast._second == null : _second.equals(cast._second)) &&
        (_third == null ? cast._third == null : _third.equals(cast._third)) &&
        (_fourth == null ? cast._fourth == null : _fourth.equals(cast._fourth)) &&
        (_fifth == null ? cast._fifth == null : _fifth.equals(cast._fifth)) &&
        (_sixth == null ? cast._sixth == null : _sixth.equals(cast._sixth));
    }
  }
  
  protected int generateHashCode() {
    return 
      (_first == null ? 0 : _first.hashCode()) ^ 
      (_second == null ? 0 : _second.hashCode() << 1) ^ 
      (_third == null ? 0 : _third.hashCode() << 2) ^ 
      (_fourth == null ? 0 : _fourth.hashCode() << 3) ^ 
      (_fifth == null ? 0 : _fifth.hashCode() << 4) ^ 
      (_sixth == null ? 0 : _sixth.hashCode() << 5) ^ 
      getClass().hashCode();
  }
    
  
  public static <T1, T2, T3, T4, T5, T6> Sextet<T1, T2, T3, T4, T5, T6> make(T1 first, T2 second, 
                                                                             T3 third, T4 fourth, 
                                                                             T5 fifth, T6 sixth) {
    return new Sextet<T1, T2, T3, T4, T5, T6>(first, second, third, fourth, fifth, sixth);
  }
  
}
