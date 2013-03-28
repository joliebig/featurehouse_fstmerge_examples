

package edu.rice.cs.plt.collect;

import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.plt.lambda.Predicate2;


public interface Relation<T1, T2> extends PredicateSet<Pair<T1, T2>>, Predicate2<T1, T2> {

  
  public boolean contains(Object o);
  
  public boolean contains(T1 first, T2 second);
  
  public boolean add(Pair<T1, T2> pair);
  
  public boolean add(T1 first, T2 second);
  
  public boolean remove(Object o);
  
  public boolean remove(T1 first, T2 second);
  
  
  public Relation<T2, T1> inverse();

  
  public PredicateSet<T1> firstSet();
  
  public boolean containsFirst(T1 first);
  
  public PredicateSet<T2> matchFirst(T1 first);
  
  public PredicateSet<T2> excludeFirsts();

  
  public PredicateSet<T2> secondSet();
  
  public boolean containsSecond(T2 second);
  
  public PredicateSet<T1> matchSecond(T2 second);
  
  public PredicateSet<T1> excludeSeconds();
  
}
