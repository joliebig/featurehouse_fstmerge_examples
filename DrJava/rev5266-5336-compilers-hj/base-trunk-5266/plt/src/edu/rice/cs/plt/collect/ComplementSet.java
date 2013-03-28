

package edu.rice.cs.plt.collect;

import java.util.Set;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.lambda.LambdaUtil;


public class ComplementSet<E> extends FilteredSet<E> {
  
  private final PredicateSet<?> _excluded;
  
  public ComplementSet(Set<? extends E> domain, Set<?> excluded) {
    this(domain, CollectUtil.asPredicateSet(excluded));
  }
  
  private ComplementSet(Set<? extends E> domain, PredicateSet<?> excluded) {
    super(domain, LambdaUtil.negate(excluded));
    _excluded = excluded;
  }
  
  public boolean isInfinite() {
    return IterUtil.isInfinite(_set) && !_excluded.isInfinite();
  }
  
  public boolean hasFixedSize() {
    return IterUtil.hasFixedSize(_set) && _excluded.hasFixedSize();
  }
  
  public boolean isStatic() {
    return IterUtil.isStatic(_set) && _excluded.isStatic();
  }
  
  @Override public boolean isEmpty() {
    if (_set.isEmpty()) { return true; }
    else if (_excluded.isEmpty()) { return false; }
    else if (_set == _excluded) { return true; }
    else { return _excluded.containsAll(_set); }
  }
  
}
