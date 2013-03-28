

package edu.rice.cs.plt.collect;

import java.io.Serializable;
import java.util.Iterator;
import edu.rice.cs.plt.iter.NoDuplicatesIterator;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;


public class IterableSet<E> extends AbstractPredicateSet<E> implements Composite, Serializable {
  
  private final Iterable<? extends E> _iter;
  
  public IterableSet(Iterable<? extends E> iter) { _iter = iter; }
  
  public boolean contains(Object o) {
    return IterUtil.contains(_iter, o);
  }
  
  public int compositeHeight() { return ObjectUtil.compositeHeight(_iter) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize(_iter) + 1; }
  
  
  public Iterator<E> iterator() { return new NoDuplicatesIterator<E>(_iter.iterator()); }
  
  public boolean isInfinite() { return IterUtil.isInfinite(_iter); }
  
  public boolean hasFixedSize() {
    
    return IterUtil.isStatic(_iter) ||
           IterUtil.isInfinite(_iter) && IterUtil.hasFixedSize(_iter);
  }
  
  public boolean isStatic() { return IterUtil.isStatic(_iter); }
  
  @Override public boolean isEmpty() { return IterUtil.isEmpty(_iter); }

}
