

package edu.rice.cs.plt.collect;

import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.plt.lambda.Lambda;


public interface FunctionalRelation<T1, T2> extends Relation<T1, T2>, Lambda<T1, T2> {

  
  public T2 value(T1 first);
  
  public LambdaMap<T1, T2> functionMap();

  
  public boolean add(Pair<T1, T2> pair);
  
  public boolean add(T1 first, T2 second);

  
  public Relation<T2, T1> inverse();

  
  public PredicateSet<T2> matchFirst(T1 first);
  
}
