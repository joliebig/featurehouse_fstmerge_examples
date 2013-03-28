

package edu.rice.cs.plt.collect;

import edu.rice.cs.plt.tuple.Pair;


public interface InjectiveRelation<T1, T2> extends Relation<T1, T2> {

  
  public T1 antecedent(T2 second);
  
  public LambdaMap<T2, T1> injectionMap();

  
  public boolean add(Pair<T1, T2> pair);
  
  public boolean add(T1 first, T2 second);

  
  public FunctionalRelation<T2, T1> inverse();
  
  
  public PredicateSet<T1> matchSecond(T2 second);
  
}
