

package edu.rice.cs.plt.collect;

import java.util.Set;
import java.util.Map;
import java.util.Iterator;
import java.io.Serializable;
import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.lambda.Thunk;


public class IndexedRelation<T1, T2> extends AbstractRelation<T1, T2> implements Serializable {
  private final RelationIndex<T1, T2> _firstIndex;
  private final RelationIndex<T2, T1> _secondIndex;
  
  
  public IndexedRelation() {
    this(CollectUtil.<T1, PredicateSet<T2>>hashMapFactory(),
         CollectUtil.<T2>hashSetFactory(4),
         CollectUtil.<T2, PredicateSet<T1>>hashMapFactory(),
         CollectUtil.<T1>hashSetFactory(4));
  }
  
  
  public IndexedRelation(boolean indexSecond) {
    if (indexSecond) {
      _firstIndex = makeFirstIndex(CollectUtil.<T1, PredicateSet<T2>>hashMapFactory(),
                                   CollectUtil.<T2>hashSetFactory(4));
      _secondIndex = makeSecondIndex(CollectUtil.<T2, PredicateSet<T1>>hashMapFactory(),
                                     CollectUtil.<T1>hashSetFactory(4));
    }
    else {
      _firstIndex = makeFirstIndex(CollectUtil.<T1, PredicateSet<T2>>hashMapFactory(),
                                   CollectUtil.<T2>hashSetFactory(4));
      _secondIndex = new LazyRelationIndex<T2, T1>(IterUtil.map(_firstIndex, Pair.<T1, T2>inverter()));
    }
  }
  
  
  public IndexedRelation(Thunk<Map<T1, PredicateSet<T2>>> firstIndexFactory,
                         Thunk<Set<T2>> firstIndexEntryFactory,
                         Thunk<Map<T2, PredicateSet<T1>>> secondIndexFactory,
                         Thunk<Set<T1>> secondIndexEntryFactory) {
    _firstIndex = makeFirstIndex(firstIndexFactory, firstIndexEntryFactory);
    _secondIndex = makeSecondIndex(secondIndexFactory, secondIndexEntryFactory);
  }
  
  
  public IndexedRelation(Thunk<Map<T1, PredicateSet<T2>>> firstIndexFactory,
                         Thunk<Set<T2>> firstIndexEntryFactory) {
    _firstIndex = makeFirstIndex(firstIndexFactory, firstIndexEntryFactory);
    _secondIndex = new LazyRelationIndex<T2, T1>(IterUtil.map(_firstIndex, Pair.<T1, T2>inverter()));
  }
  
  private RelationIndex<T1, T2> makeFirstIndex(Thunk<Map<T1, PredicateSet<T2>>> mapFactory,
                                               Thunk<Set<T2>> setFactory) {
    return new ConcreteRelationIndex<T1, T2>(mapFactory, setFactory) {
      public void addToRelation(T1 first, T2 second) { _secondIndex.added(second, first); }
      public void removeFromRelation(T1 first, T2 second) { _secondIndex.removed(second, first); }
      public void clearRelation() { _secondIndex.cleared(); }
    };
  }
  
  private RelationIndex<T2, T1> makeSecondIndex(Thunk<Map<T2, PredicateSet<T1>>> mapFactory,
                                                Thunk<Set<T1>> setFactory) {
    return new ConcreteRelationIndex<T2, T1>(mapFactory, setFactory) {
      public void addToRelation(T2 second, T1 first) { _firstIndex.added(first, second); }
      public void removeFromRelation(T2 second, T1 first) { _firstIndex.removed(first, second); }
      public void clearRelation() { _firstIndex.cleared(); }
    };
  }
  
  @Override public boolean isEmpty() { return _firstIndex.isEmpty(); }
  @Override public int size() { return _firstIndex.size(); }
  @Override public int size(int bound) { return _firstIndex.size(bound); }
  public boolean isInfinite() { return false; }
  public boolean hasFixedSize() { return false; }
  public boolean isStatic() { return false; }
  
  public boolean contains(T1 first, T2 second) {
    return _firstIndex.contains(first, second);
  }
  
  public boolean contains(Object obj) {
    if (obj instanceof Pair<?, ?>) {
      Pair<?, ?> p = (Pair<?, ?>) obj;
      return _firstIndex.contains(p.first(), p.second());
    }
    else { return false; }
  }
  
  public Iterator<Pair<T1, T2>> iterator() { return _firstIndex.iterator(); }
  
  public PredicateSet<T1> firstSet() { return _firstIndex.keys(); }
  public PredicateSet<T2> matchFirst(T1 first) { return _firstIndex.match(first); }
  public PredicateSet<T2> secondSet() { return _secondIndex.keys(); }
  public PredicateSet<T1> matchSecond(T2 second) { return _secondIndex.match(second); }
  
  @Override public boolean add(T1 first, T2 second) {
    boolean result = !_firstIndex.contains(first, second);
    if (result) {
      _firstIndex.added(first, second);
      _secondIndex.added(second, first);
    }
    return result;
  }
  
  @Override public boolean remove(T1 first, T2 second) {
    boolean result = _firstIndex.contains(first, second);
    if (result) {
      _firstIndex.removed(first, second);
      _secondIndex.removed(second, first);
    }
    return result;
  }
  
  @Override public void clear() {
    if (!_firstIndex.isEmpty()) {
      _firstIndex.cleared();
      _secondIndex.cleared();
    }
  }
  
  
  public static <T1, T2> IndexedRelation<T1, T2> makeHashBased() {
    return new IndexedRelation<T1, T2>(CollectUtil.<T1, PredicateSet<T2>>hashMapFactory(),
                                       CollectUtil.<T2>hashSetFactory(4),
                                       CollectUtil.<T2, PredicateSet<T1>>hashMapFactory(),
                                       CollectUtil.<T1>hashSetFactory(4));
  }
  
  
  public static <T1, T2> IndexedRelation<T1, T2> makeLinkedHashBased() {
    return new IndexedRelation<T1, T2>(CollectUtil.<T1, PredicateSet<T2>>linkedHashMapFactory(),
                                       CollectUtil.<T2>linkedHashSetFactory(4),
                                       CollectUtil.<T2, PredicateSet<T1>>linkedHashMapFactory(),
                                       CollectUtil.<T1>linkedHashSetFactory(4));
  }
  
  
  public static <T1 extends Comparable<? super T1>, T2 extends Comparable<? super T2>>
    IndexedRelation<T1, T2> makeTreeBased() {
    return new IndexedRelation<T1, T2>(CollectUtil.<T1, PredicateSet<T2>>treeMapFactory(),
                                       CollectUtil.<T2>treeSetFactory(),
                                       CollectUtil.<T2, PredicateSet<T1>>treeMapFactory(),
                                       CollectUtil.<T1>treeSetFactory());
  }
  
}
