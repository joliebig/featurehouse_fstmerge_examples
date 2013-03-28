

package edu.rice.cs.plt.collect;

import edu.rice.cs.plt.iter.IterUtil;


public class IntersectionRelation<T1, T2> extends FilteredRelation<T1, T2> {
  
  
  public IntersectionRelation(Relation<? super T1, ? super T2> rel1, Relation<T1, T2> rel2) {
    super(rel2, rel1);
  }
  
  @Override public PredicateSet<T2> matchFirst(T1 first) {
    
    @SuppressWarnings("unchecked")
    Relation<? super T1, ? super T2> rel1 = (Relation<? super T1, ? super T2>) _pred;
    return new IntersectionSet<T2>(rel1.matchFirst(first), _rel.matchFirst(first));
  }
  
  @Override public PredicateSet<T1> matchSecond(T2 second) {
    
    @SuppressWarnings("unchecked")
    Relation<? super T1, ? super T2> rel1 = (Relation<? super T1, ? super T2>) _pred;
    return new IntersectionSet<T1>(rel1.matchSecond(second), _rel.matchSecond(second));
  }
  
  public boolean isInfinite() {
    return ((Relation<?, ?>) _pred).isInfinite() && IterUtil.isInfinite(_rel);
  }
  
  public boolean hasFixedSize() {
    return ((Relation<?, ?>) _pred).hasFixedSize() && IterUtil.hasFixedSize(_rel);
  }
  
  public boolean isStatic() {
    return ((Relation<?, ?>) _pred).isStatic() && IterUtil.isStatic(_rel);
  }
  
  @Override public boolean isEmpty() {
    return ((Relation<?, ?>) _pred).isEmpty() || (_rel != _pred && super.isEmpty());
  }
  
}
