

package edu.rice.cs.plt.collect;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.io.Serializable;
import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.lambda.Thunk;


public class IndexedFunctionalRelation<T1, T2> extends AbstractFunctionalRelation<T1, T2> implements Serializable {
  
  private Map<T1, T2> _firstMap;
  private LambdaMap<T1, T2> _functionMap;
  private RelationIndex<T2, T1> _secondIndex;
  
  
  public IndexedFunctionalRelation() {
    this(CollectUtil.<T1, T2>hashMapFactory(),
         CollectUtil.<T2, PredicateSet<T1>>hashMapFactory(),
         CollectUtil.<T1>hashSetFactory(4));
  }
  
  
  public IndexedFunctionalRelation(boolean indexSecond) {
    if (indexSecond) {
      _firstMap = new HashMap<T1, T2>();
      _functionMap = new ImmutableMap<T1, T2>(_firstMap);
      _secondIndex = makeSecondIndex(CollectUtil.<T2, PredicateSet<T1>>hashMapFactory(),
                                     CollectUtil.<T1>hashSetFactory(4));
    }
    else {
      _firstMap = new HashMap<T1, T2>();
      _functionMap = new ImmutableMap<T1, T2>(_firstMap);
      _secondIndex = new LazyRelationIndex<T2, T1>(IterUtil.map(this, Pair.<T1, T2>inverter()));
    }
  }
  
  
  public IndexedFunctionalRelation(Thunk<Map<T1, T2>> firstIndexFactory,
                                   Thunk<Map<T2, PredicateSet<T1>>> secondIndexFactory,
                                   Thunk<Set<T1>> secondIndexEntryFactory) {
    _firstMap = firstIndexFactory.value();
    _functionMap = new ImmutableMap<T1, T2>(_firstMap);
    _secondIndex = makeSecondIndex(secondIndexFactory, secondIndexEntryFactory);
  }
  
  
  public IndexedFunctionalRelation(Thunk<Map<T1, T2>> firstIndexFactory) {
    _firstMap = firstIndexFactory.value();
    _functionMap = new ImmutableMap<T1, T2>(_firstMap);
    _secondIndex = new LazyRelationIndex<T2, T1>(IterUtil.map(this, Pair.<T1, T2>inverter()));
  }
    
  private RelationIndex<T2, T1> makeSecondIndex(Thunk<Map<T2, PredicateSet<T1>>> mapFactory,
                                                Thunk<Set<T1>> setFactory) {
    return new ConcreteRelationIndex<T2, T1>(mapFactory, setFactory) {
      public void validateAdd(T2 second, T1 first) { IndexedFunctionalRelation.this.validateAdd(first, second); }
      public void addToRelation(T2 second, T1 first) { _firstMap.put(first, second); }
      public void removeFromRelation(T2 second, T1 first) { _firstMap.remove(first); }
      public void clearRelation() { _firstMap.clear(); }
    };
  }
  
  public boolean isStatic() { return false; }
  public LambdaMap<T1, T2> functionMap() { return _functionMap; }
  public PredicateSet<T2> secondSet() { return _secondIndex.keys(); }
  public PredicateSet<T1> matchSecond(T2 second) { return _secondIndex.match(second); }

  @Override public boolean add(T1 first, T2 second) {
    boolean result = validateAdd(first, second);
    if (result) {
      _firstMap.put(first, second);
      _secondIndex.added(second, first);
    }
    return result;
  }
  
  
  private boolean validateAdd(T1 first, T2 second) {
    if (_firstMap.containsKey(first)) {
      T2 current = _firstMap.get(first);
      if ((current == null) ? (second == null) : current.equals(second)) {
        return false;
      }
      else {
        throw new IllegalArgumentException("Relation already contains an entry for " + first);
      }
    }
    else { return true; }
  }
  
  @Override public boolean remove(T1 first, T2 second) {
    boolean result = contains(first, second);
    if (result) {
      _firstMap.remove(first);
      _secondIndex.removed(second, first);
    }
    return result;
  }
  
  @Override public void clear() {
    _firstMap.clear();
    _secondIndex.cleared();
  }
    
  
  public static <T1, T2> IndexedFunctionalRelation<T1, T2> makeHashBased() {
    return new IndexedFunctionalRelation<T1, T2>(CollectUtil.<T1, T2>hashMapFactory(),
                                                 CollectUtil.<T2, PredicateSet<T1>>hashMapFactory(),
                                                 CollectUtil.<T1>hashSetFactory(4));
  }
  
  
  public static <T1, T2> IndexedFunctionalRelation<T1, T2> makeLinkedHashBased() {
    return new IndexedFunctionalRelation<T1, T2>(CollectUtil.<T1, T2>linkedHashMapFactory(),
                                                 CollectUtil.<T2, PredicateSet<T1>>linkedHashMapFactory(),
                                                 CollectUtil.<T1>linkedHashSetFactory(4));
  }
  
  
  public static <T1 extends Comparable<? super T1>, T2 extends Comparable<? super T2>>
    IndexedFunctionalRelation<T1, T2> makeTreeBased() {
    return new IndexedFunctionalRelation<T1, T2>(CollectUtil.<T1, T2>treeMapFactory(),
                                                 CollectUtil.<T2, PredicateSet<T1>>treeMapFactory(),
                                                 CollectUtil.<T1>treeSetFactory());
  }
  
}
