

package edu.rice.cs.plt.tuple;

import java.io.Serializable;
import java.util.Comparator;

import edu.rice.cs.plt.collect.CollectUtil;
import edu.rice.cs.plt.collect.TotalOrder;
import edu.rice.cs.plt.lambda.Lambda;
import edu.rice.cs.plt.lambda.Lambda4;
import edu.rice.cs.plt.object.ObjectUtil;


public class Quad<T1, T2, T3, T4> extends Tuple {
  
  protected final T1 _first;
  protected final T2 _second;
  protected final T3 _third;
  protected final T4 _fourth;
  
  public Quad(T1 first, T2 second, T3 third, T4 fourth) {
    _first = first;
    _second = second;
    _third = third;
    _fourth = fourth;
  }
  
  public T1 first() { return _first; }
  public T2 second() { return _second; }
  public T3 third() { return _third; }
  public T4 fourth() { return _fourth; }

  public String toString() {
    return "(" + _first + ", " + _second + ", " + _third + ", " + _fourth + ")";
  }
  
  
  public boolean equals(Object o) {
    if (this == o) { return true; }
    else if (o == null || !getClass().equals(o.getClass())) { return false; }
    else {
      Quad<?, ?, ?, ?> cast = (Quad<?, ?, ?, ?>) o;
      return 
        (_first == null ? cast._first == null : _first.equals(cast._first)) &&
        (_second == null ? cast._second == null : _second.equals(cast._second)) &&
        (_third == null ? cast._third == null : _third.equals(cast._third)) &&
        (_fourth == null ? cast._fourth == null : _fourth.equals(cast._fourth));
    }
  }
  
  protected int generateHashCode() {
    return 
      (_first == null ? 0 : _first.hashCode()) ^ 
      (_second == null ? 0 : _second.hashCode() << 1) ^ 
      (_third == null ? 0 : _third.hashCode() << 2) ^ 
      (_fourth == null ? 0 : _fourth.hashCode() << 3) ^ 
      getClass().hashCode();
  }
  
  
  public static <T1, T2, T3, T4> Quad<T1, T2, T3, T4> make(T1 first, T2 second, T3 third, 
                                                           T4 fourth) {
    return new Quad<T1, T2, T3, T4>(first, second, third, fourth);
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
      return new Quad<T1, T2, T3, T4>(first, second, third, fourth);
    }
  }
  
  
  @SuppressWarnings("unchecked") public static <T> Lambda<Quad<? extends T, ?, ?, ?>, T> firstGetter() {
    return (GetFirst<T>) GetFirst.INSTANCE;
  }
  
  private static final class GetFirst<T> implements Lambda<Quad<? extends T, ?, ?, ?>, T>, Serializable {
    public static final GetFirst<Void> INSTANCE = new GetFirst<Void>();
    private GetFirst() {}
    public T value(Quad<? extends T, ?, ?, ?> arg) { return arg.first(); }
  }
      
  
  @SuppressWarnings("unchecked") public static <T> Lambda<Quad<?, ? extends T, ?, ?>, T> secondGetter() {
    return (GetSecond<T>) GetSecond.INSTANCE;
  }
  
  private static final class GetSecond<T> implements Lambda<Quad<?, ? extends T, ?, ?>, T>, Serializable {
    public static final GetSecond<Void> INSTANCE = new GetSecond<Void>();
    private GetSecond() {}
    public T value(Quad<?, ? extends T, ?, ?> arg) { return arg.second(); }
  }

  
  @SuppressWarnings("unchecked") public static <T> Lambda<Quad<?, ?, ? extends T, ?>, T> thirdGetter() {
    return (GetThird<T>) GetThird.INSTANCE;
  }
  
  private static final class GetThird<T> implements Lambda<Quad<?, ?, ? extends T, ?>, T>, Serializable {
    public static final GetThird<Void> INSTANCE = new GetThird<Void>();
    private GetThird() {}
    public T value(Quad<?, ?, ? extends T, ?> arg) { return arg.third(); }
  }

  
  @SuppressWarnings("unchecked") public static <T> Lambda<Quad<?, ?, ?, ? extends T>, T> fourthGetter() {
    return (GetFourth<T>) GetFourth.INSTANCE;
  }
  
  private static final class GetFourth<T> implements Lambda<Quad<?, ?, ?, ? extends T>, T>, Serializable {
    public static final GetFourth<Void> INSTANCE = new GetFourth<Void>();
    private GetFourth() {}
    public T value(Quad<?, ?, ?, ? extends T> arg) { return arg.fourth(); }
  }

  
  public static <T1 extends Comparable<? super T1>, T2 extends Comparable<? super T2>,
                   T3 extends Comparable<? super T3>, T4 extends Comparable<? super T4>>
      TotalOrder<Quad<? extends T1, ? extends T2, ? extends T3, ? extends T4>> comparator() {
    return new QuadComparator<T1, T2, T3, T4>(CollectUtil.<T1>naturalOrder(), CollectUtil.<T2>naturalOrder(),
                                               CollectUtil.<T3>naturalOrder(), CollectUtil.<T4>naturalOrder());
  }
  
  
  public static <T1, T2, T3, T4> TotalOrder<Quad<? extends T1, ? extends T2, ? extends T3, ? extends T4>>
      comparator(Comparator<? super T1> comp1, Comparator<? super T2> comp2,
                 Comparator<? super T3> comp3, Comparator<? super T4> comp4) {
    return new QuadComparator<T1, T2, T3, T4>(comp1, comp2, comp3, comp4);
  }
  
  private static final class QuadComparator<T1, T2, T3, T4>
                                 extends TotalOrder<Quad<? extends T1, ? extends T2,
                                                          ? extends T3, ? extends T4>> {
    private final Comparator<? super T1> _comp1;
    private final Comparator<? super T2> _comp2;
    private final Comparator<? super T3> _comp3;
    private final Comparator<? super T4> _comp4;
    public QuadComparator(Comparator<? super T1> comp1, Comparator<? super T2> comp2,
                           Comparator<? super T3> comp3, Comparator<? super T4> comp4) {
      _comp1 = comp1;
      _comp2 = comp2;
      _comp3 = comp3;
      _comp4 = comp4;
    }
    public int compare(Quad<? extends T1, ? extends T2, ? extends T3, ? extends T4> q1,
                        Quad<? extends T1, ? extends T2, ? extends T3, ? extends T4> q2) {
      return ObjectUtil.compare(_comp1, q1.first(), q2.first(), _comp2, q1.second(), q2.second(),
                                 _comp3, q1.third(), q2.third(), _comp4, q1.fourth(), q2.fourth());
    }
    public boolean equals(Object o) {
      if (this == o) { return true; }
      else if (!(o instanceof QuadComparator<?,?,?,?>)) { return false; }
      else {
        QuadComparator<?,?,?,?> cast = (QuadComparator<?,?,?,?>) o;
        return _comp1.equals(cast._comp1) && _comp2.equals(cast._comp2) && _comp3.equals(cast._comp3);
      }
    }
    public int hashCode() { return ObjectUtil.hash(QuadComparator.class, _comp1, _comp2, _comp3, _comp4); }
  }

}
