

package edu.rice.cs.plt.tuple;

import java.io.Serializable;
import java.util.Comparator;

import edu.rice.cs.plt.collect.CollectUtil;
import edu.rice.cs.plt.collect.TotalOrder;
import edu.rice.cs.plt.lambda.Lambda;
import edu.rice.cs.plt.lambda.Lambda3;
import edu.rice.cs.plt.object.ObjectUtil;


public class Triple<T1, T2, T3> extends Tuple {
  
  protected final T1 _first;
  protected final T2 _second;
  protected final T3 _third;
  
  public Triple(T1 first, T2 second, T3 third) { 
    _first = first;
    _second = second;
    _third = third;
  }
  
  public T1 first() { return _first; }
  public T2 second() { return _second; }
  public T3 third() { return _third; }

  public String toString() {
    return "(" + _first + ", " + _second + ", " + _third + ")";
  }
  
  
  public boolean equals(Object o) {
    if (this == o) { return true; }
    else if (o == null || !getClass().equals(o.getClass())) { return false; }
    else {
      Triple<?, ?, ?> cast = (Triple<?, ?, ?>) o;
      return 
        (_first == null ? cast._first == null : _first.equals(cast._first)) &&
        (_second == null ? cast._second == null : _second.equals(cast._second)) &&
        (_third == null ? cast._third == null : _third.equals(cast._third));
    }
  }
  
  protected int generateHashCode() {
    return 
      (_first == null ? 0 : _first.hashCode()) ^ 
      (_second == null ? 0 : _second.hashCode() << 1) ^ 
      (_third == null ? 0 : _third.hashCode() << 2) ^
      getClass().hashCode();
  }
  
  
  public static <T1, T2, T3> Triple<T1, T2, T3> make(T1 first, T2 second, T3 third) {
    return new Triple<T1, T2, T3>(first, second, third);
  }
  
  
  @SuppressWarnings("unchecked") public static <T1, T2, T3> Lambda3<T1, T2, T3, Triple<T1, T2, T3>> factory() {
    return (Factory<T1, T2, T3>) Factory.INSTANCE;
  }
  
  private static final class Factory<T1, T2, T3> implements Lambda3<T1, T2, T3, Triple<T1, T2, T3>>, Serializable {
    public static final Factory<Object, Object, Object> INSTANCE = new Factory<Object, Object, Object>();
    private Factory() {}
    public Triple<T1, T2, T3> value(T1 first, T2 second, T3 third) {
      return new Triple<T1, T2, T3>(first, second, third);
    }
  }

  
  @SuppressWarnings("unchecked") public static <T> Lambda<Triple<? extends T, ?, ?>, T> firstGetter() {
    return (GetFirst<T>) GetFirst.INSTANCE;
  }
  
  private static final class GetFirst<T> implements Lambda<Triple<? extends T, ?, ?>, T>, Serializable {
    public static final GetFirst<Void> INSTANCE = new GetFirst<Void>();
    private GetFirst() {}
    public T value(Triple<? extends T, ?, ?> arg) { return arg.first(); }
  }
      
  
  @SuppressWarnings("unchecked") public static <T> Lambda<Triple<?, ? extends T, ?>, T> secondGetter() {
    return (GetSecond<T>) GetSecond.INSTANCE;
  }
  
  private static final class GetSecond<T> implements Lambda<Triple<?, ? extends T, ?>, T>, Serializable {
    public static final GetSecond<Void> INSTANCE = new GetSecond<Void>();
    private GetSecond() {}
    public T value(Triple<?, ? extends T, ?> arg) { return arg.second(); }
  }

  
  @SuppressWarnings("unchecked") public static <T> Lambda<Triple<?, ?, ? extends T>, T> thirdGetter() {
    return (GetThird<T>) GetThird.INSTANCE;
  }
  
  private static final class GetThird<T> implements Lambda<Triple<?, ?, ? extends T>, T>, Serializable {
    public static final GetThird<Void> INSTANCE = new GetThird<Void>();
    private GetThird() {}
    public T value(Triple<?, ?, ? extends T> arg) { return arg.third(); }
  }

  
  public static <T1 extends Comparable<? super T1>, T2 extends Comparable<? super T2>,
                   T3 extends Comparable<? super T3>>
      TotalOrder<Triple<? extends T1, ? extends T2, ? extends T3>> comparator() {
    return new TripleComparator<T1, T2, T3>(CollectUtil.<T1>naturalOrder(), CollectUtil.<T2>naturalOrder(),
                                             CollectUtil.<T3>naturalOrder());
  }
  
  
  public static <T1, T2, T3> TotalOrder<Triple<? extends T1, ? extends T2, ? extends T3>>
      comparator(Comparator<? super T1> comp1, Comparator<? super T2> comp2, Comparator<? super T3> comp3) {
    return new TripleComparator<T1, T2, T3>(comp1, comp2, comp3);
  }
  
  private static final class TripleComparator<T1, T2, T3>
                                 extends TotalOrder<Triple<? extends T1, ? extends T2, ? extends T3>> {
    private final Comparator<? super T1> _comp1;
    private final Comparator<? super T2> _comp2;
    private final Comparator<? super T3> _comp3;
    public TripleComparator(Comparator<? super T1> comp1, Comparator<? super T2> comp2,
                             Comparator<? super T3> comp3) {
      _comp1 = comp1;
      _comp2 = comp2;
      _comp3 = comp3;
    }
    public int compare(Triple<? extends T1, ? extends T2, ? extends T3> t1,
                        Triple<? extends T1, ? extends T2, ? extends T3> t2) {
      return ObjectUtil.compare(_comp1, t1.first(), t2.first(), _comp2, t1.second(), t2.second(),
                                 _comp3, t1.third(), t2.third());
    }
    public boolean equals(Object o) {
      if (this == o) { return true; }
      else if (!(o instanceof TripleComparator<?,?,?>)) { return false; }
      else {
        TripleComparator<?,?,?> cast = (TripleComparator<?,?,?>) o;
        return _comp1.equals(cast._comp1) && _comp2.equals(cast._comp2) && _comp3.equals(cast._comp3);
      }
    }
    public int hashCode() { return ObjectUtil.hash(TripleComparator.class, _comp1, _comp2, _comp3); }
  }

}
