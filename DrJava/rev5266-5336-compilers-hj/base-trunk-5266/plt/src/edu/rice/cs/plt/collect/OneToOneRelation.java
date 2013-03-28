

package edu.rice.cs.plt.collect;

import edu.rice.cs.plt.tuple.Pair;


public interface OneToOneRelation<T1, T2> extends FunctionalRelation<T1, T2>, InjectiveRelation<T1, T2> {
  
  
  public boolean add(Pair<T1, T2> pair);
  
  
  public boolean add(T1 first, T2 second);

  
  public OneToOneRelation<T2, T1> inverse();

}
