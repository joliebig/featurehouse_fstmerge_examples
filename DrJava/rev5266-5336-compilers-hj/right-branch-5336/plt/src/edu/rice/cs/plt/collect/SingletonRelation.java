

package edu.rice.cs.plt.collect;

import java.io.Serializable;
import java.util.Iterator;
import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.plt.iter.SingletonIterator;
import edu.rice.cs.plt.object.ObjectUtil;


public class SingletonRelation<T1, T2> extends AbstractOneToOneRelation<T1, T2> implements Serializable {
  
  private final T1 _first;
  private final T2 _second;
  
  public SingletonRelation(T1 first, T2 second) { _first = first; _second = second; }
  
  public SingletonRelation(Pair<? extends T1, ? extends T2> pair) {
    _first = pair.first();
    _second = pair.second();
  }
  
  @Override public boolean isEmpty() { return false; }
  @Override public int size() { return 1; }
  @Override public int size(int bound) { return (bound < 1) ? bound : 1; }
  public boolean isInfinite() { return false; }
  public boolean hasFixedSize() { return true; }
  public boolean isStatic() { return true; }
  
  public boolean contains(T1 candidate1, T2 candidate2) {
    return ObjectUtil.equal(candidate1, _first) && ObjectUtil.equal(candidate2, _second);
  }
  
  public boolean contains(Object obj) {
    if (obj instanceof Pair<?, ?>) {
      Pair<?, ?> p = (Pair<?, ?>) obj;
      return ObjectUtil.equal(p.first(), _first) && ObjectUtil.equal(p.second(), _second);
    }
    else { return false; }
  }
  
  @Override public Iterator<Pair<T1, T2>> iterator() {
    return new SingletonIterator<Pair<T1, T2>>(Pair.make(_first, _second));
  }
  
  public LambdaMap<T1, T2> functionMap() { return new SingletonMap<T1, T2>(_first, _second); }
  public LambdaMap<T2, T1> injectionMap() { return new SingletonMap<T2, T1>(_second, _first); }
  
  @Override public PredicateSet<T1> firstSet() { return new SingletonSet<T1>(_first); }
  @Override public PredicateSet<T2> matchFirst(T1 match) {
    if ((_first == null) ? (match == null) : _first.equals(match)) {
      return new SingletonSet<T2>(_second);
    }
    else { return EmptySet.make(); }
  }
  
  @Override public PredicateSet<T2> secondSet() { return new SingletonSet<T2>(_second); }
  @Override public PredicateSet<T1> matchSecond(T2 match) {
    if ((_second == null) ? (match == null) : _second.equals(match)) {
      return new SingletonSet<T1>(_first);
    }
    else { return EmptySet.make(); }
  }
  
  @Override public OneToOneRelation<T2, T1> inverse() {
    return new SingletonRelation<T2, T1>(_second, _first);
  }
  
  
  public static <T1, T2> SingletonRelation<T1, T2> make(T1 first, T2 second) { 
    return new SingletonRelation<T1, T2>(first, second);
  }
  
  
  public static <T1, T2> SingletonRelation<T1, T2> make(Pair<? extends T1, ? extends T2> pair) { 
    return new SingletonRelation<T1, T2>(pair);
  }
  
}
