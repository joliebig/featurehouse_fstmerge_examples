

package edu.rice.cs.plt.collect;

import java.util.Iterator;
import java.io.Serializable;
import edu.rice.cs.plt.iter.ImmutableIterator;
import edu.rice.cs.plt.iter.ComposedIterator;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;


public class UnionRelation<T1, T2> extends AbstractRelation<T1, T2> implements Composite, Serializable {
  private final Relation<T1, T2> _rel1;
  private final Relation<T1, T2> _rel2;
  private final Relation<T1, T2> _rel2Extras;
  
  
  public UnionRelation(Relation<T1, T2> rel1, Relation<T1, T2> rel2) {
    _rel1 = rel1;
    _rel2 = rel2;
    _rel2Extras = new ComplementRelation<T1, T2>(rel2, rel1);
  }
  
  public int compositeHeight() { return ObjectUtil.compositeHeight(_rel1, _rel2) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize(_rel1, _rel2) + 1; }
  
  public boolean contains(T1 first, T2 second) {
    return _rel1.contains(first, second) || _rel2.contains(first, second);
  }
  
  public boolean contains(Object o) {
    return _rel1.contains(o) || _rel2.contains(o);
  }
  
  public Iterator<Pair<T1, T2>> iterator() {
    return ImmutableIterator.make(ComposedIterator.make(_rel1.iterator(), _rel2Extras.iterator()));
  }
  
  public PredicateSet<T1> firstSet() {
    return new UnionSet<T1>(_rel1.firstSet(), _rel2.firstSet());
  }
  
  public PredicateSet<T2> matchFirst(T1 first) {
    return new UnionSet<T2>(_rel1.matchFirst(first), _rel2.matchFirst(first));
  }
  
  public PredicateSet<T2> secondSet() {
    return new UnionSet<T2>(_rel1.secondSet(), _rel2.secondSet());
  }
  
  public PredicateSet<T1> matchSecond(T2 second) {
    return new UnionSet<T1>(_rel1.matchSecond(second), _rel2.matchSecond(second));
  }
  
  public boolean isInfinite() { return IterUtil.isInfinite(_rel1) || IterUtil.isInfinite(_rel2); }
  public boolean hasFixedSize() { return IterUtil.hasFixedSize(_rel1) && IterUtil.hasFixedSize(_rel2); }
  public boolean isStatic() { return IterUtil.isStatic(_rel1) && IterUtil.isStatic(_rel2); }
  
  
  
  @Override public int size() {
    return _rel1.size() + _rel2Extras.size();
  }
  
  @Override public int size(int bound) {
    int size1 = IterUtil.sizeOf(_rel1, bound);
    int bound2 = bound - size1;
    int size2 = (bound2 > 0) ? IterUtil.sizeOf(_rel2Extras, bound) : 0;
    return size1 + size2;
  }
  
  @Override public boolean isEmpty() {
    return _rel1.isEmpty() && _rel2.isEmpty();
  }
  
}
