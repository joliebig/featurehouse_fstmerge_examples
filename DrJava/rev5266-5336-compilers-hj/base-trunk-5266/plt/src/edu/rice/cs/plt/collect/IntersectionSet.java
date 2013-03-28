

package edu.rice.cs.plt.collect;

import java.util.Set;
import edu.rice.cs.plt.iter.IterUtil;


public class IntersectionSet<E> extends FilteredSet<E> {
  
  
  public IntersectionSet(Set<?> set1, Set<? extends E> set2) {
    super(set2, CollectUtil.asPredicateSet(set1));
  }
  
  public boolean isInfinite() {
    return ((PredicateSet<?>) _pred).isInfinite() && IterUtil.isInfinite(_set);
  }
  
  public boolean hasFixedSize() {
    return ((PredicateSet<?>) _pred).hasFixedSize() && IterUtil.hasFixedSize(_set);
  }
  
  public boolean isStatic() {
    return ((PredicateSet<?>) _pred).isStatic() && IterUtil.isStatic(_set);
  }
  
  @Override public boolean isEmpty() {
    return ((Set<?>) _pred).isEmpty() || (_set != _pred && super.isEmpty());
  }
  
}
