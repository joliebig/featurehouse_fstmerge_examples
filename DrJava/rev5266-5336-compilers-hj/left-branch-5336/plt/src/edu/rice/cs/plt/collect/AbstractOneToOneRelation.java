

package edu.rice.cs.plt.collect;

import java.util.Iterator;
import java.io.Serializable;
import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.plt.lambda.Runnable1;
import edu.rice.cs.plt.iter.EmptyIterator;
import edu.rice.cs.plt.iter.MutableSingletonIterator;


public abstract class AbstractOneToOneRelation<T1, T2> extends AbstractFunctionalRelation<T1, T2>
                                                       implements OneToOneRelation<T1, T2> {
  
  public abstract boolean isStatic();
  public abstract LambdaMap<T1, T2> functionMap();
  public abstract LambdaMap<T2, T1> injectionMap();
  
  
  
  
  public PredicateSet<T2> secondSet() { return injectionMap().keySet(); }

  
  @Override public boolean containsSecond(T2 second) { return injectionMap().containsKey(second); }

  
  public PredicateSet<T1> matchSecond(T2 second) { return new MatchSecondSet(second); }
  
  
  public T1 antecedent(T2 second) { return injectionMap().get(second); }
  
  
  @Override public OneToOneRelation<T2, T1> inverse() { return new InverseOneToOneRelation(); }
  
  
  private final class MatchSecondSet extends AbstractPredicateSet<T1> implements Serializable {
    private final T2 _key;

    public MatchSecondSet(T2 second) { _key = second; }

    @Override public boolean isEmpty() { return !injectionMap().containsKey(_key); }
    @Override public int size() { return injectionMap().containsKey(_key) ? 1 : 0; }
    @Override public int size(int bound) { return (bound == 0) ? 0 : size(); }
    public boolean isInfinite() { return false; }
    public boolean hasFixedSize() { return AbstractOneToOneRelation.this.isStatic(); }
    public boolean isStatic() { return AbstractOneToOneRelation.this.isStatic(); }

    public boolean contains(Object val) {
      return AbstractOneToOneRelation.this.contains(Pair.make(val, _key));
    }

    public Iterator<T1> iterator() {
      final LambdaMap<T2, T1> map = injectionMap();
      if (map.containsKey(_key)) {
        return new MutableSingletonIterator<T1>(map.get(_key), new Runnable1<T1>() {
          public void run(T1 val) { map.remove(_key); }
        });
      }
      else { return EmptyIterator.make(); }
    }
    
    @Override public boolean add(T1 val) {
      boolean result = !AbstractOneToOneRelation.this.contains(val, _key);
      if (result) { injectionMap().put(_key, val); }
      return result;
    }
    
    @Override public boolean remove(Object val) {
      boolean result = AbstractOneToOneRelation.this.contains(Pair.make(val, _key));
      if (result) { injectionMap().remove(_key); }
      return result;
    }
    
    @Override public void clear() { injectionMap().remove(_key); }
  }
  
  
  protected class InverseOneToOneRelation extends InverseFunctionalRelation implements OneToOneRelation<T2, T1> {
    public T1 value(T2 first) { return AbstractOneToOneRelation.this.antecedent(first); }
    public LambdaMap<T2, T1> functionMap() { return AbstractOneToOneRelation.this.injectionMap(); }
    @Override public OneToOneRelation<T1, T2> inverse() { return AbstractOneToOneRelation.this; }
  }
  
}
