

package edu.rice.cs.plt.collect;

import java.util.Set;
import java.util.Map;
import edu.rice.cs.plt.tuple.Quad;
import edu.rice.cs.plt.lambda.Predicate4;


public interface Relation4<T1, T2, T3, T4> extends PredicateSet<Quad<T1, T2, T3, T4>>,
                                                   Predicate4<T1, T2, T3, T4> {

  
  public boolean contains(T1 first, T2 second, T3 third, T4 fourth);
  
  public boolean add(T1 first, T2 second, T3 third, T4 fourth);
  
  public boolean remove(T1 first, T2 second, T3 third, T4 fourth);

  
  public Set<T1> firstSet();
  
  public Map<T1, Relation3<T2, T3, T4>> firstMap();
  
  public boolean containsFirst(T1 first);
  
  public Relation3<T2, T3, T4> matchFirst(T1 first);
  
  public Relation3<T2, T3, T4> excludeFirsts();

  
  public Set<T2> secondSet();
  
  public Map<T2, Relation3<T1, T3, T4>> secondMap();
  
  public boolean containsSecond(T2 second);
  
  public Relation3<T1, T3, T4> matchSecond(T2 second);
  
  public Relation3<T1, T3, T4> excludeSeconds();

  
  public Set<T3> thirdSet();
  
  public Map<T3, Relation3<T1, T2, T4>> thirdMap();
  
  public boolean containsThird(T3 third);
  
  public Relation3<T1, T2, T4> matchThird(T3 third);
  
  public Relation3<T1, T2, T4> excludeThirds();

  
  public Set<T4> fourthSet();
  
  public Map<T4, Relation3<T1, T2, T3>> fourthMap();
  
  public boolean containsFourth(T4 fourth);
  
  public Relation3<T1, T2, T3> matchFourth(T4 fourth);
  
  public Relation3<T1, T2, T3> excludeFourths();

}
