

package edu.rice.cs.plt.collect;

import java.util.Map;
import java.io.Serializable;
import edu.rice.cs.plt.lambda.Thunk;


public class IndexedOneToOneRelation<T1, T2> extends AbstractOneToOneRelation<T1, T2> implements Serializable {
  
  private Map<T1, T2> _firstMap;
  private LambdaMap<T1, T2> _functionMap;
  private Map<T2, T1> _secondMap;
  private LambdaMap<T2, T1> _injectionMap;
  
  
  public IndexedOneToOneRelation() {
    this(CollectUtil.<T1, T2>hashMapFactory(), CollectUtil.<T2, T1>hashMapFactory());
  }
  
  
  public IndexedOneToOneRelation(Thunk<Map<T1, T2>> firstIndexFactory,
                                 Thunk<Map<T2, T1>> secondIndexFactory) {
    _firstMap = firstIndexFactory.value();
    
    _functionMap = new ImmutableMap<T1, T2>(_firstMap);
    _secondMap = secondIndexFactory.value();
    
    _injectionMap = new ImmutableMap<T2, T1>(_secondMap);
  }
  
  public boolean isStatic() { return false; }
  public LambdaMap<T1, T2> functionMap() { return _functionMap; }
  public LambdaMap<T2, T1> injectionMap() { return _injectionMap; }
  
  @Override public boolean add(T1 first, T2 second) {
    boolean result = validateAdd(first, second);
    if (result) {
      _firstMap.put(first, second);
      _secondMap.put(second, first);
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
    else if (_secondMap.containsKey(second)) {
      throw new IllegalArgumentException("Relation already contains an entry for " + second);
    }
    else { return true; }
  }
  
  @Override public boolean remove(T1 first, T2 second) {
    boolean result = contains(first, second);
    if (result) {
      _firstMap.remove(first);
      _secondMap.remove(second);
    }
    return result;
  }
  
  @Override public void clear() {
    _firstMap.clear();
    _secondMap.clear();
  }
  
  
  public static <T1, T2> IndexedOneToOneRelation<T1, T2> makeHashBased() {
    return new IndexedOneToOneRelation<T1, T2>(CollectUtil.<T1, T2>hashMapFactory(),
                                               CollectUtil.<T2, T1>hashMapFactory());
  }
  
  
  public static <T1, T2> IndexedOneToOneRelation<T1, T2> makeLinkedHashBased() {
    return new IndexedOneToOneRelation<T1, T2>(CollectUtil.<T1, T2>linkedHashMapFactory(),
                                               CollectUtil.<T2, T1>linkedHashMapFactory());
  }
  
  
  public static <T1 extends Comparable<? super T1>, T2 extends Comparable<? super T2>>
    IndexedOneToOneRelation<T1, T2> makeTreeBased() {
    return new IndexedOneToOneRelation<T1, T2>(CollectUtil.<T1, T2>treeMapFactory(),
                                               CollectUtil.<T2, T1>treeMapFactory());
  }
  
}
