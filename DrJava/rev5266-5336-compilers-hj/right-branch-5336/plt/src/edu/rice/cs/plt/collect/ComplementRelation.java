

package edu.rice.cs.plt.collect;

import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.lambda.LambdaUtil;
import edu.rice.cs.plt.lambda.Predicate2;


public class ComplementRelation<T1, T2> extends FilteredRelation<T1, T2> {
  
  private final Relation<? super T1, ? super T2> _excluded;
  
  public ComplementRelation(Relation<T1, T2> domain, Relation<? super T1, ? super T2> excluded) {
    super(domain, LambdaUtil.negate((Predicate2<? super T1, ? super T2>)excluded));
    _excluded = excluded;
  }
  
  @Override public PredicateSet<T2> matchFirst(T1 first) {
    return new ComplementSet<T2>(_rel.matchFirst(first), _excluded.matchFirst(first));
  }
  
  @Override public PredicateSet<T1> matchSecond(T2 second) {
    return new ComplementSet<T1>(_rel.matchSecond(second), _excluded.matchSecond(second));
  }
  
  public boolean isInfinite() {
    return IterUtil.isInfinite(_rel) && !_excluded.isInfinite();
  }
  
  public boolean hasFixedSize() {
    return IterUtil.hasFixedSize(_rel) && _excluded.hasFixedSize();
  }
  
  public boolean isStatic() {
    return IterUtil.isStatic(_rel) && _excluded.isStatic();
  }
  
  @Override public boolean isEmpty() {
    if (_rel.isEmpty()) { return true; }
    else if (_excluded.isEmpty()) { return false; }
    else if (_rel == _excluded) { return true; }
    else { return _excluded.containsAll(_rel); }
  }
  
}
