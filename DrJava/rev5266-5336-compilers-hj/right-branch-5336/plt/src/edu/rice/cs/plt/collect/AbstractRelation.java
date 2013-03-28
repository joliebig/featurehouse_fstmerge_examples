

package edu.rice.cs.plt.collect;

import java.util.Iterator;
import java.io.Serializable;
import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.plt.tuple.Option;
import edu.rice.cs.plt.iter.MappedIterator;


public abstract class AbstractRelation<T1, T2> extends AbstractPredicateSet<Pair<T1, T2>>
                                               implements Relation<T1, T2> {
  
  public abstract boolean isInfinite();
  public abstract boolean hasFixedSize();
  public abstract boolean isStatic();
  
  
  public abstract boolean contains(T1 first, T2 second);
  public abstract boolean contains(Object obj);
  public abstract Iterator<Pair<T1, T2>> iterator();
  public abstract PredicateSet<T1> firstSet();
  public abstract PredicateSet<T2> matchFirst(T1 first);
  public abstract PredicateSet<T2> secondSet();
  public abstract PredicateSet<T1> matchSecond(T2 second);
  
  public boolean add(T1 first, T2 second) { throw new UnsupportedOperationException(); }
  public boolean remove(T1 first, T2 second) { throw new UnsupportedOperationException(); }
    
  
  public boolean add(Pair<T1, T2> p) { return add(p.first(), p.second()); }
  
  
  public boolean remove(Object o) {
    Option<Pair<T1, T2>> cast = CollectUtil.castIfContains(this, o);
    if (cast.isSome()) { return remove(cast.unwrap().first(), cast.unwrap().second()); }
    else { return false; }
  }
  
  
  public Relation<T2, T1> inverse() { return new InverseRelation(); }
  
  
  public boolean containsFirst(T1 first) { return firstSet().contains(first); }
  
  
  public PredicateSet<T2> excludeFirsts() { return secondSet(); }
  
  
  public boolean containsSecond(T2 second) { return secondSet().contains(second); }
  
  
  public PredicateSet<T1> excludeSeconds() { return firstSet(); }
  
  
  protected class InverseRelation extends AbstractPredicateSet<Pair<T2, T1>>
                                  implements Relation<T2, T1>, Serializable {
    
    public int size() { return AbstractRelation.this.size(); }
    public int size(int bound) { return AbstractRelation.this.size(bound); }
    @Override public boolean isEmpty() { return AbstractRelation.this.isEmpty(); }
    public boolean isInfinite() { return AbstractRelation.this.isInfinite(); }
    public boolean hasFixedSize() { return AbstractRelation.this.hasFixedSize(); }
    public boolean isStatic() { return AbstractRelation.this.isStatic(); }
    
    @Override public boolean contains(Object o) {
      return (o instanceof Pair<?, ?>) &&
             AbstractRelation.this.contains(((Pair<?, ?>) o).inverse());
    }
    
    public Iterator<Pair<T2, T1>> iterator() {
      return new MappedIterator<Pair<T1, T2>, Pair<T2, T1>>(AbstractRelation.this.iterator(),
                                                            Pair.<T1, T2>inverter());
    }
    
    @Override public boolean add(Pair<T2, T1> pair) {
      return AbstractRelation.this.add(pair.inverse());
    }
    
    @Override public boolean remove(Object o) {
      return (o instanceof Pair<?, ?>) &&
             AbstractRelation.this.remove(((Pair<?, ?>) o).inverse());
    }
    
    @Override public void clear() { AbstractRelation.this.clear(); }
    
    public boolean contains(T2 f, T1 s) { return AbstractRelation.this.contains(s, f); }
    public boolean add(T2 f, T1 s) { return AbstractRelation.this.add(s, f); }
    public boolean remove(T2 f, T1 s) { return AbstractRelation.this.remove(s, f); }
    public Relation<T1, T2> inverse() { return AbstractRelation.this; }
    
    public PredicateSet<T2> firstSet() { return AbstractRelation.this.secondSet(); }
    public boolean containsFirst(T2 f) { return AbstractRelation.this.containsSecond(f); }
    public PredicateSet<T1> matchFirst(T2 f) { return AbstractRelation.this.matchSecond(f); }
    public PredicateSet<T1> excludeFirsts() { return AbstractRelation.this.excludeSeconds(); }
    
    public PredicateSet<T1> secondSet() { return AbstractRelation.this.firstSet(); }
    public boolean containsSecond(T1 s) { return AbstractRelation.this.containsFirst(s); }
    public PredicateSet<T2> matchSecond(T1 s) { return AbstractRelation.this.matchFirst(s); }
    public PredicateSet<T2> excludeSeconds() { return AbstractRelation.this.excludeFirsts(); }
  }
  
}
