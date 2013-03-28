

package edu.rice.cs.plt.collect;

import java.util.Collection;
import java.util.Iterator;
import edu.rice.cs.plt.iter.SizedIterable;


public interface Multiset<T> extends Collection<T>, SizedIterable<T> {
  
  
  public int size();
  
  
  public int size(int bound);
  
  
  public boolean isInfinite();
  
  
  public boolean hasFixedSize();
  
  
  public boolean isStatic();

  
  public boolean isEmpty();
  
  
  public boolean contains(Object obj);
  
  
  public int count(Object value);
  
  
  public PredicateSet<T> asSet();
  
  
  public Iterator<T> iterator();
  
  
  public Object[] toArray();
  
  
  public <S> S[] toArray(S[] fill);
  
  
  public boolean add(T val);
  
  
  public boolean add(T val, int instances);
  
  
  public boolean remove(Object obj);
  
  
  public boolean remove(Object obj, int instances);
  
  
  public boolean removeAllInstances(Object obj);

  
  public boolean containsAll(Collection<?> c);
  
  
  public boolean isSupersetOf(Multiset<?> s);
  
  
  public boolean addAll(Collection<? extends T> coll);
  
  
  public boolean removeAll(Collection<?> coll);

  
  public boolean retainAll(Collection<?> coll);
  
  
  public void clear();
  
  
  public boolean equals(Object obj);
  
  
  public int hashCode();
  
}
