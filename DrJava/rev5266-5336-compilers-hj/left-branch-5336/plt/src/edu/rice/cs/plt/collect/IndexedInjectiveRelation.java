

package edu.rice.cs.plt.collect;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.io.Serializable;
import edu.rice.cs.plt.lambda.Thunk;


public class IndexedInjectiveRelation<T1, T2> extends AbstractInjectiveRelation<T1, T2> implements Serializable {
  
  private RelationIndex<T1, T2> _firstIndex;
  private Map<T2, T1> _secondMap;
  private LambdaMap<T2, T1> _injectionMap;
  
  
  public IndexedInjectiveRelation() {
    this(CollectUtil.<T2, T1>hashMapFactory(),
         CollectUtil.<T1, PredicateSet<T2>>hashMapFactory(),
         CollectUtil.<T2>hashSetFactory(4));
  }
  
  
  public IndexedInjectiveRelation(boolean indexFirst) {
    if (indexFirst) {
      _secondMap = new HashMap<T2, T1>();
      _injectionMap = new ImmutableMap<T2, T1>(_secondMap);
      _firstIndex = makeFirstIndex(CollectUtil.<T1, PredicateSet<T2>>hashMapFactory(),
                                   CollectUtil.<T2>hashSetFactory(4));
    }
    else {
      _secondMap = new HashMap<T2, T1>();
      _injectionMap = new ImmutableMap<T2, T1>(_secondMap);
      _firstIndex = new LazyRelationIndex<T1, T2>(this);
    }
  }
  
  
  public IndexedInjectiveRelation(Thunk<Map<T2, T1>> secondIndexFactory,
                                   Thunk<Map<T1, PredicateSet<T2>>> firstIndexFactory,
                                   Thunk<Set<T2>> firstIndexEntryFactory) {
    _secondMap = secondIndexFactory.value();
    _injectionMap = new ImmutableMap<T2, T1>(_secondMap);
    _firstIndex = makeFirstIndex(firstIndexFactory, firstIndexEntryFactory);
  }
  
  
  public IndexedInjectiveRelation(Thunk<Map<T2, T1>> secondIndexFactory) {
    _secondMap = secondIndexFactory.value();
    _injectionMap = new ImmutableMap<T2, T1>(_secondMap);
    _firstIndex = new LazyRelationIndex<T1, T2>(this);
  }
  
  private RelationIndex<T1, T2> makeFirstIndex(Thunk<Map<T1, PredicateSet<T2>>> mapFactory,
                                               Thunk<Set<T2>> setFactory) {
    return new ConcreteRelationIndex<T1, T2>(mapFactory, setFactory) {
      public void validateAdd(T1 first, T2 second) { IndexedInjectiveRelation.this.validateAdd(first, second); }
      public void addToRelation(T1 first, T2 second) { _secondMap.put(second, first); }
      public void removeFromRelation(T1 first, T2 second) { _secondMap.remove(second); }
      public void clearRelation() { _secondMap.clear(); }
    };
  }

  public boolean isStatic() { return false; }
  public LambdaMap<T2, T1> injectionMap() { return _injectionMap; }
  public PredicateSet<T1> firstSet() { return _firstIndex.keys(); }
  public PredicateSet<T2> matchFirst(T1 first) { return _firstIndex.match(first); }

  @Override public boolean add(T1 first, T2 second) {
    boolean result = validateAdd(first, second);
    if (result) {
      _secondMap.put(second, first);
      _firstIndex.added(first, second);
    }
    return result;
  }
  
  
  private boolean validateAdd(T1 first, T2 second) {
    if (_secondMap.containsKey(second)) {
      T1 current = _secondMap.get(second);
      if ((current == null) ? (first == null) : current.equals(first)) {
        return false;
      }
      else {
        throw new IllegalArgumentException("Relation already contains an entry for " + second);
      }
    }
    else { return true; }
  }
  
  @Override public boolean remove(T1 first, T2 second) {
    boolean result = contains(first, second);
    if (result) {
      _secondMap.remove(second);
      _firstIndex.removed(first, second);
    }
    return result;
  }
  
  @Override public void clear() {
    _secondMap.clear();
    _firstIndex.cleared();
  }
    
  
  public static <T1, T2> IndexedInjectiveRelation<T1, T2> makeHashBased() {
    return new IndexedInjectiveRelation<T1, T2>(CollectUtil.<T2, T1>hashMapFactory(),
                                                CollectUtil.<T1, PredicateSet<T2>>hashMapFactory(),
                                                CollectUtil.<T2>hashSetFactory(4));
  }
  
  
  public static <T1, T2> IndexedInjectiveRelation<T1, T2> makeLinkedHashBased() {
    return new IndexedInjectiveRelation<T1, T2>(CollectUtil.<T2, T1>linkedHashMapFactory(),
                                                CollectUtil.<T1, PredicateSet<T2>>linkedHashMapFactory(),
                                                CollectUtil.<T2>linkedHashSetFactory(4));
  }
  
  
  public static <T1 extends Comparable<? super T1>, T2 extends Comparable<? super T2>>
    IndexedInjectiveRelation<T1, T2> makeTreeBased() {
    return new IndexedInjectiveRelation<T1, T2>(CollectUtil.<T2, T1>treeMapFactory(),
                                                CollectUtil.<T1, PredicateSet<T2>>treeMapFactory(),
                                                CollectUtil.<T2>treeSetFactory());
  }
  
}
