

package edu.rice.cs.plt.collect;

import java.util.Iterator;
import java.io.Serializable;
import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.plt.tuple.Option;
import edu.rice.cs.plt.iter.ReadOnlyIterator;
import edu.rice.cs.plt.iter.EmptyIterator;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.lambda.Predicate;
import edu.rice.cs.plt.lambda.Lambda;
import edu.rice.cs.plt.object.ObjectUtil;


public class ComposedRelation<T1, T2, T3> extends AbstractRelation<T1, T3> implements Serializable {
  
  private final Relation<T1, T2> _rel1;
  private final Relation<T2, T3> _rel2;
  private final PredicateSet<T1> _firstSet;
  private final PredicateSet<T3> _secondSet;
  
  public ComposedRelation(Relation<T1, T2> rel1,
                          Relation<T2, T3> rel2) {
    _rel1 = rel1;
    _rel2 = rel2;
    _firstSet = new FilteredSet<T1>(rel1.firstSet(), new Predicate<T1>() {
      public boolean contains(T1 first) {
        for (T2 middle : _rel1.matchFirst(first)) {
          if (_rel2.containsFirst(middle)) { return true; }
        }
        return false;
      }
    });
    _secondSet = new FilteredSet<T3>(rel2.secondSet(), new Predicate<T3>() {
      public boolean contains(T3 second) {
        for (T2 middle : _rel2.matchSecond(second)) {
          if (_rel1.containsSecond(middle)) { return true; }
        }
        return false;
      }
    });
  }
  
  public int compositeHeight() { return ObjectUtil.compositeHeight(_rel1, _rel2) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize(_rel1, _rel2) + 1; }
  
  @Override public boolean isEmpty() { return _firstSet.isEmpty(); }
  public boolean isInfinite() { return false; }
  public boolean hasFixedSize() { return false; }
  public boolean isStatic() { return _rel1.isStatic() && _rel2.isStatic(); }
  
  public boolean contains(T1 first, T3 second) {
    return matchFirst(first).contains(second);
  }
  
  public boolean contains(Object o) {
    if (o instanceof Pair<?, ?>) {
      Pair<?, ?> p = (Pair<?, ?>) o;
      Option<T1> first = CollectUtil.castIfContains(_firstSet, p.first());
      return first.isSome() && matchFirst(first.unwrap()).contains(p.second());
    }
    else { return false; }
  }
        
  
  public Iterator<Pair<T1, T3>> iterator() {
    return new ReadOnlyIterator<Pair<T1, T3>>() {
      private final Iterator<T1> _firsts = _firstSet.iterator();
      private T1 _currentFirst = null;
      private Iterator<T2> _currentMiddles = EmptyIterator.<T2>make();
      private T2 _currentMiddle = null;
      private Iterator<? extends T3> _currentSeconds = EmptyIterator.<T3>make();

      public boolean hasNext() {
        return _currentSeconds.hasNext() || _currentMiddles.hasNext() || _firsts.hasNext();
      }
      
      public Pair<T1, T3> next() {
        
        if (!_currentSeconds.hasNext()) {
          if (!_currentMiddles.hasNext()) {
            _currentFirst = _firsts.next();
            _currentMiddles = _rel1.matchFirst(_currentFirst).iterator();
          }
          _currentMiddle = _currentMiddles.next();
          _currentSeconds = _rel2.matchFirst(_currentMiddle).iterator();
        }
        return new Pair<T1, T3>(_currentFirst, _currentSeconds.next());
      }
    };
  }
  
  public PredicateSet<T1> firstSet() { return _firstSet; }

  public PredicateSet<T3> matchFirst(T1 first) {
    Iterable<PredicateSet<T3>> seconds =
      IterUtil.map(_rel1.matchFirst(first), new Lambda<T2, PredicateSet<T3>>() {
      public PredicateSet<T3> value(T2 middle) { return _rel2.matchFirst(middle); }
    });
    return new IterableSet<T3>(IterUtil.collapse(seconds));
  }
  
  public PredicateSet<T3> secondSet() { return _secondSet; }
  
  public PredicateSet<T1> matchSecond(T3 second) {
    Iterable<PredicateSet<T1>> firsts =
      IterUtil.map(_rel2.matchSecond(second), new Lambda<T2, PredicateSet<T1>>() {
      public PredicateSet<T1> value(T2 middle) { return _rel1.matchSecond(middle); }
    });
    return new IterableSet<T1>(IterUtil.collapse(firsts));
  }
  
}
