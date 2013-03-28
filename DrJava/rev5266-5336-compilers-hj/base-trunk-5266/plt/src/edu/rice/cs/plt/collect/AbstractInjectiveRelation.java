

package edu.rice.cs.plt.collect;

import java.util.Iterator;
import java.util.Map;
import java.io.Serializable;
import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.plt.lambda.Lambda;
import edu.rice.cs.plt.lambda.Runnable1;
import edu.rice.cs.plt.iter.MappedIterator;
import edu.rice.cs.plt.iter.EmptyIterator;
import edu.rice.cs.plt.iter.MutableSingletonIterator;
import edu.rice.cs.plt.object.ObjectUtil;


public abstract class AbstractInjectiveRelation<T1, T2> extends AbstractRelation<T1, T2>
                                                        implements InjectiveRelation<T1, T2> {
  
  public abstract boolean isStatic();
  public abstract LambdaMap<T2, T1> injectionMap();
  public abstract PredicateSet<T1> firstSet();
  public abstract PredicateSet<T2> matchFirst(T1 first);

  
  @Override public boolean isEmpty() { return injectionMap().isEmpty(); }
  
  @Override public int size() { return injectionMap().size(); }
  
  @Override public int size(int bound) { return injectionMap().keySet().size(bound); }
  
  public boolean isInfinite() { return injectionMap().keySet().isInfinite(); }
  
  public boolean hasFixedSize() { return injectionMap().keySet().hasFixedSize(); }
  
  
  public boolean contains(T1 first, T2 second) {
    LambdaMap<T2, T1> map = injectionMap();
    return map.containsKey(second) && ObjectUtil.equal(map.get(second), first);
  }
  
  
  public boolean contains(Object obj) {
    if (obj instanceof Pair<?, ?>) {
      Pair<?, ?> p = (Pair<?, ?>) obj;
      LambdaMap<T2, T1> map = injectionMap();
      return map.containsKey(p.second()) && ObjectUtil.equal(map.get(p.second()), p.first());
    }
    else { return false; }
  }
  
  
  public Iterator<Pair<T1, T2>> iterator() {
    return MappedIterator.make(injectionMap().entrySet().iterator(),
                               new Lambda<Map.Entry<T2, T1>, Pair<T1, T2>>() {
      public Pair<T1, T2> value(Map.Entry<T2, T1> entry) {
        return new Pair<T1, T2>(entry.getValue(), entry.getKey());
      }
    });
  }

  
  public PredicateSet<T2> secondSet() { return injectionMap().keySet(); }

  
  @Override public boolean containsSecond(T2 second) { return injectionMap().containsKey(second); }

  
  public PredicateSet<T1> matchSecond(T2 second) { return new MatchSecondSet(second); }
  
  
  public T1 antecedent(T2 second) { return injectionMap().get(second); }
  
  
  @Override public FunctionalRelation<T2, T1> inverse() { return new InverseInjectiveRelation(); }
  
  
  private final class MatchSecondSet extends AbstractPredicateSet<T1> implements Serializable {
    private final T2 _key;

    public MatchSecondSet(T2 second) { _key = second; }

    @Override public boolean isEmpty() { return !injectionMap().containsKey(_key); }
    @Override public int size() { return injectionMap().containsKey(_key) ? 1 : 0; }
    @Override public int size(int bound) { return (bound == 0) ? 0 : size(); }
    public boolean isInfinite() { return false; }
    public boolean hasFixedSize() { return AbstractInjectiveRelation.this.isStatic(); }
    public boolean isStatic() { return AbstractInjectiveRelation.this.isStatic(); }

    public boolean contains(Object val) {
      return AbstractInjectiveRelation.this.contains(Pair.make(val, _key));
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
      boolean result = !AbstractInjectiveRelation.this.contains(val, _key);
      if (result) { injectionMap().put(_key, val); }
      return result;
    }
    
    @Override public boolean remove(Object val) {
      boolean result = AbstractInjectiveRelation.this.contains(Pair.make(val, _key));
      if (result) { injectionMap().remove(_key); }
      return result;
    }
    
    @Override public void clear() { injectionMap().remove(_key); }
  }
  
  
  protected class InverseInjectiveRelation extends InverseRelation implements FunctionalRelation<T2, T1> {
    public T1 value(T2 first) { return AbstractInjectiveRelation.this.antecedent(first); }
    public LambdaMap<T2, T1> functionMap() { return AbstractInjectiveRelation.this.injectionMap(); }
    @Override public Relation<T1, T2> inverse() { return AbstractInjectiveRelation.this; }
  }
  
}
