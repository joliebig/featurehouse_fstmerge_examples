

package edu.rice.cs.plt.collect;

import java.util.Set;
import java.util.Iterator;
import java.io.Serializable;
import edu.rice.cs.plt.iter.ImmutableIterator;
import edu.rice.cs.plt.iter.ComposedIterator;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;


public class UnionSet<E> extends AbstractPredicateSet<E> implements Composite, Serializable {
  private final Set<? extends E> _set1;
  private final Set<? extends E> _set2;
  private final Set<? extends E> _set2Extras;
  
  
  public UnionSet(Set<? extends E> set1, Set<? extends E> set2) {
    _set1 = set1;
    _set2 = set2;
    _set2Extras = new ComplementSet<E>(set2, set1);
  }
  
  public int compositeHeight() { return ObjectUtil.compositeHeight(_set1, _set2) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize(_set1, _set2) + 1; }
  
  public boolean contains(Object o) {
    return _set1.contains(o) || _set2.contains(o);
  }
  
  public Iterator<E> iterator() {
    return new ImmutableIterator<E>(new ComposedIterator<E>(_set1.iterator(), _set2Extras.iterator()));
  }
  
  public boolean isInfinite() { return IterUtil.isInfinite(_set1) || IterUtil.isInfinite(_set2); }
  public boolean hasFixedSize() { return IterUtil.hasFixedSize(_set1) && IterUtil.hasFixedSize(_set2); }
  public boolean isStatic() { return IterUtil.isStatic(_set1) && IterUtil.isStatic(_set2); }
  
  
  
  @Override public int size() {
    return _set1.size() + _set2Extras.size();
  }
  
  @Override public int size(int bound) {
    int size1 = IterUtil.sizeOf(_set1, bound);
    int bound2 = bound - size1;
    int size2 = (bound2 > 0) ? IterUtil.sizeOf(_set2Extras, bound) : 0;
    return size1 + size2;
  }
  
  @Override public boolean isEmpty() {
    return _set1.isEmpty() && _set2.isEmpty();
  }
  
}
