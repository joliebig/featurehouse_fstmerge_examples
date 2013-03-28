

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


public abstract class AbstractFunctionalRelation<T1, T2> extends AbstractRelation<T1, T2>
                                                         implements FunctionalRelation<T1, T2> {
  
  public abstract boolean isStatic();
  public abstract LambdaMap<T1, T2> functionMap();
  public abstract PredicateSet<T2> secondSet();
  public abstract PredicateSet<T1> matchSecond(T2 second);

  
  @Override public boolean isEmpty() { return functionMap().isEmpty(); }
  
  @Override public int size() { return functionMap().size(); }
  
  @Override public int size(int bound) { return functionMap().keySet().size(bound); }
  
  public boolean isInfinite() { return functionMap().keySet().isInfinite(); }
  
  public boolean hasFixedSize() { return functionMap().keySet().hasFixedSize(); }
  
  
  public boolean contains(T1 first, T2 second) {
    LambdaMap<T1, T2> map = functionMap();
    return map.containsKey(first) && ObjectUtil.equal(map.get(first), second);
  }
  
  
  public boolean contains(Object obj) {
    if (obj instanceof Pair<?, ?>) {
      Pair<?, ?> p = (Pair<?, ?>) obj;
      LambdaMap<T1, T2> map = functionMap();
      return map.containsKey(p.first()) && ObjectUtil.equal(map.get(p.first()), p.second());
    }
    else { return false; }
  }
  
  
  public Iterator<Pair<T1, T2>> iterator() {
    return MappedIterator.make(functionMap().entrySet().iterator(),
                               new Lambda<Map.Entry<T1, T2>, Pair<T1, T2>>() {
      public Pair<T1, T2> value(Map.Entry<T1, T2> entry) {
        return new Pair<T1, T2>(entry.getKey(), entry.getValue());
      }
    });
  }

  
  public PredicateSet<T1> firstSet() { return functionMap().keySet(); }

  
  @Override public boolean containsFirst(T1 first) { return functionMap().containsKey(first); }

  
  public PredicateSet<T2> matchFirst(T1 first) { return new MatchFirstSet(first); }
  
  
  public T2 value(T1 first) { return functionMap().get(first); }
  
  
  @Override public Relation<T2, T1> inverse() { return new InverseFunctionalRelation(); }
  
  
  private final class MatchFirstSet extends AbstractPredicateSet<T2> implements Serializable {
    private final T1 _key;

    public MatchFirstSet(T1 first) { _key = first; }

    @Override public boolean isEmpty() { return !functionMap().containsKey(_key); }
    @Override public int size() { return functionMap().containsKey(_key) ? 1 : 0; }
    @Override public int size(int bound) { return (bound == 0) ? 0 : size(); }
    public boolean isInfinite() { return false; }
    public boolean hasFixedSize() { return AbstractFunctionalRelation.this.isStatic(); }
    public boolean isStatic() { return AbstractFunctionalRelation.this.isStatic(); }

    public boolean contains(Object val) {
      return AbstractFunctionalRelation.this.contains(Pair.make(_key, val));
    }

    public Iterator<T2> iterator() {
      final LambdaMap<T1, T2> map = functionMap();
      if (map.containsKey(_key)) {
        return new MutableSingletonIterator<T2>(map.get(_key), new Runnable1<T2>() {
          public void run(T2 val) { map.remove(_key); }
        });
      }
      else { return EmptyIterator.make(); }
    }
    
    @Override public boolean add(T2 val) {
      boolean result = !AbstractFunctionalRelation.this.contains(_key, val);
      if (result) { functionMap().put(_key, val); }
      return result;
    }
    
    @Override public boolean remove(Object val) {
      boolean result = AbstractFunctionalRelation.this.contains(Pair.make(_key, val));
      if (result) { functionMap().remove(_key); }
      return result;
    }
    
    @Override public void clear() { functionMap().remove(_key); }
  }
  
  
  protected class InverseFunctionalRelation extends InverseRelation implements InjectiveRelation<T2, T1> {
    public T2 antecedent(T1 second) { return AbstractFunctionalRelation.this.value(second); }
    public LambdaMap<T1, T2> injectionMap() { return AbstractFunctionalRelation.this.functionMap(); }
    @Override public FunctionalRelation<T1, T2> inverse() { return AbstractFunctionalRelation.this; }
  }
  
}
