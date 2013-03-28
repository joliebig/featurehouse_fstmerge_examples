

package edu.rice.cs.plt.collect;

import edu.rice.cs.plt.tuple.Triple;
import edu.rice.cs.plt.lambda.Predicate3;


public interface Relation3<T1, T2, T3> extends PredicateSet<Triple<T1, T2, T3>>, Predicate3<T1, T2, T3> {

  
  public boolean contains(T1 first, T2 second, T3 third);
  
  public boolean add(T1 first, T2 second, T3 third);
  
  public boolean remove(T1 first, T2 second, T3 third);

  
  public PredicateSet<T1> firstSet();
  
  public LambdaMap<T1, Relation<T2, T3>> firstMap();
  
  public boolean containsFirst(T1 first);
  
  public Relation<T2, T3> matchFirst(T1 first);
  
  public Relation<T2, T3> excludeFirsts();

  
  public PredicateSet<T2> secondSet();
  
  public LambdaMap<T2, Relation<T1, T3>> secondMap();
  
  public boolean containsSecond(T2 second);
  
  public Relation<T1, T3> matchSecond(T2 second);
  
  public Relation<T1, T3> excludeSeconds();

  
  public PredicateSet<T3> thirdSet();
  
  public LambdaMap<T3, Relation<T1, T2>> thirdMap();
  
  public boolean containsThird(T3 third);
  
  public Relation<T1, T2> matchThird(T3 third);
  
  public Relation<T1, T2> excludeThirds();

}
