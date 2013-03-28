

package edu.rice.cs.plt.collect;

import java.util.Set;
import java.io.Serializable;
import edu.rice.cs.plt.tuple.Pair;


public final class EmptyRelation<T1, T2> extends EmptyCollection<Pair<T1, T2>>
                                         implements OneToOneRelation<T1, T2>, Serializable {

  public static final EmptyRelation<Object, Object> INSTANCE = new EmptyRelation<Object, Object>();

  private EmptyRelation() {}
  
  public boolean contains(T1 first, T2 second) { return false; }

  public boolean add(T1 first, T2 second) { throw new UnsupportedOperationException(); }
  public boolean remove(T1 first, T2 second) { throw new UnsupportedOperationException(); }
 
  @SuppressWarnings("unchecked")
  public OneToOneRelation<T2, T1> inverse() { return (EmptyRelation<T2, T1>) INSTANCE; }

  public PredicateSet<T1> firstSet() { return EmptySet.make(); }
  public boolean containsFirst(T1 first) { return false; }
  public PredicateSet<T2> matchFirst(T1 first) { return EmptySet.make(); }
  public PredicateSet<T2> excludeFirsts() { return EmptySet.make(); }

  public PredicateSet<T2> secondSet() { return EmptySet.make(); }
  public boolean containsSecond(T2 second) { return false; }
  public PredicateSet<T1> matchSecond(T2 second) { return EmptySet.make(); }
  public PredicateSet<T1> excludeSeconds() { return EmptySet.make(); }

  public T2 value(T1 first) { return null; }
  public T1 antecedent(T2 second) { return null; }
  public LambdaMap<T1, T2> functionMap() { return EmptyMap.make(); }
  public LambdaMap<T2, T1> injectionMap() { return EmptyMap.make(); }

  public boolean equals(Object o) {
    if (o instanceof Set<?>) { return ((Set<?>) o).isEmpty(); }
    else { return false; }
  }

  public int hashCode() { return 0; }

  
  @SuppressWarnings("unchecked") public static <T1, T2> EmptyRelation<T1, T2> make() {
    return (EmptyRelation<T1, T2>) INSTANCE;
  }

}
