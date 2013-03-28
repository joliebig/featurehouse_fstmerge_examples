

package edu.rice.cs.plt.collect;

import java.util.Iterator;
import java.io.Serializable;
import edu.rice.cs.plt.lambda.Predicate;
import edu.rice.cs.plt.lambda.Predicate2;
import edu.rice.cs.plt.lambda.LambdaUtil;
import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.plt.tuple.Option;
import edu.rice.cs.plt.iter.FilteredIterator;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;


public class FilteredRelation<T1, T2> extends AbstractRelation<T1, T2> implements Composite, Serializable {
  
  protected final Relation<T1, T2> _rel;
  protected final Predicate2<? super T1, ? super T2> _pred;
  private final PredicateSet<T1> _firstSet;
  private final PredicateSet<T2> _secondSet;
  
  public FilteredRelation(Relation<T1, T2> relation, Predicate2<? super T1, ? super T2> predicate) {
    _rel = relation;
    _pred = predicate;
    _firstSet = new FilteredSet<T1>(_rel.firstSet(), new Predicate<T1>() {
      public boolean contains(T1 first) {
        for (T2 second : _rel.matchFirst(first)) {
          if (_pred.contains(first, second)) { return true; }
        }
        return false;
      }
    });
    _secondSet = new FilteredSet<T2>(_rel.secondSet(), new Predicate<T2>() {
      public boolean contains(T2 second) {
        for (T1 first : _rel.matchSecond(second)) {
          if (_pred.contains(first, second)) { return true; }
        }
        return false;
      }
    });
  }
  
  public int compositeHeight() { return ObjectUtil.compositeHeight(_rel, _pred) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize(_rel, _pred) + 1; }
  
  @Override public boolean isEmpty() { return _rel.isEmpty() || super.isEmpty(); }
  public boolean isInfinite() { return false; }
  public boolean hasFixedSize() { return false; }
  public boolean isStatic() { return false; }
  
  public boolean contains(T1 first, T2 second) {
    return _rel.contains(first, second) && _pred.contains(first, second);
  }
  
  public boolean contains(Object obj) {
    Option<Pair<T1, T2>> cast = CollectUtil.castIfContains(_rel, obj);
    if (cast.isSome()) {
      Pair<T1, T2> p = cast.unwrap();
      return _pred.contains(p.first(), p.second());
    }
    else { return false; }
  }
  
  public Iterator<Pair<T1, T2>> iterator() {
    return FilteredIterator.make(_rel.iterator(), LambdaUtil.<T1, T2>unary(_pred));
  }
  
  public PredicateSet<T1> firstSet() { return _firstSet; }
  
  public PredicateSet<T2> matchFirst(final T1 first) {
    return new FilteredSet<T2>(_rel.matchFirst(first), new Predicate<T2>() {
      public boolean contains(T2 second) {
        return _pred.contains(first, second);
      }
    });
  }
  
  public PredicateSet<T2> secondSet() { return _secondSet; }
  
  public PredicateSet<T1> matchSecond(final T2 second) {
    return new FilteredSet<T1>(_rel.matchSecond(second), new Predicate<T1>() {
      public boolean contains(T1 first) {
        return _pred.contains(first, second);
      }
    });
  }

}
