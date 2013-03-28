

package edu.rice.cs.plt.collect;

import java.util.Set;
import java.util.Collection;
import java.util.Iterator;
import java.io.Serializable;
import edu.rice.cs.plt.lambda.Predicate;
import edu.rice.cs.plt.tuple.Option;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.iter.FilteredIterator;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;


public class FilteredSet<T> extends AbstractPredicateSet<T> implements Composite, Serializable {

  protected final Set<? extends T> _set;
  protected final Predicate<? super T> _pred;
  
  public FilteredSet(Set<? extends T> set, Predicate<? super T> predicate) {
    _set = set;
    _pred = predicate;
  }
  
  public int compositeHeight() { return ObjectUtil.compositeHeight(_set, _pred) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize(_set, _pred) + 1; }
  
  public boolean contains(Object o) {
    Option<T> cast = CollectUtil.castIfContains(_set, o);
    if (cast.isSome()) { return _pred.contains(cast.unwrap()); }
    else { return false; }
  }
  
  @Override public boolean containsAll(Collection<?> objs) {
    if (_set.containsAll(objs)) {
      
      
      
      @SuppressWarnings("unchecked") Iterable<? extends T> iter = (Iterable<? extends T>) objs;
      return IterUtil.and(iter, _pred);
    }
    else { return false; }
  }
  
  
  public Iterator<T> iterator() { return new FilteredIterator<T>(_set.iterator(), _pred); }
  
  @Override public boolean isEmpty() { return _set.isEmpty() || super.isEmpty(); }
  public boolean isInfinite() { return false; }
  public boolean hasFixedSize() { return false; }
  public boolean isStatic() { return false; }
  
}
