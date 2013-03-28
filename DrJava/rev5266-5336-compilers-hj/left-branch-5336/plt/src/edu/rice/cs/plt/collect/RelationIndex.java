

package edu.rice.cs.plt.collect;

import java.util.Iterator;
import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.plt.iter.SizedIterable;


public interface RelationIndex<K, V> extends SizedIterable<Pair<K, V>> {
  
  public boolean contains(Object key, Object value);
  
  public PredicateSet<K> keys();
  
  public PredicateSet<V> match(K key);
  
  public Iterator<Pair<K, V>> iterator();
  
  
  public void added(K key, V value);
  
  public void removed(K key, V value);
  
  public void cleared();
}
