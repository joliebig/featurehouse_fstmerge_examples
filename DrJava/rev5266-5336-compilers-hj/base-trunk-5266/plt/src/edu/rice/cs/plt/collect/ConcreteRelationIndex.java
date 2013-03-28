

package edu.rice.cs.plt.collect;

import java.io.Serializable;
import java.util.*;
import java.lang.ref.WeakReference;
import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.plt.tuple.Option;
import edu.rice.cs.plt.lambda.Thunk;
import edu.rice.cs.plt.iter.IterUtil;


public class ConcreteRelationIndex<K, V> implements RelationIndex<K, V>, Serializable {
  
  
  private final Map<K, WeakReference<ValueSet>> _valueSets;
  private final Map<K, PredicateSet<V>> _nonEmptyValueSets;
  private final Thunk<? extends Set<V>> _setFactory;
  private int _size;
  
  
  public ConcreteRelationIndex(Thunk<? extends Map<K, PredicateSet<V>>> mapFactory,
                               Thunk<? extends Set<V>> setFactory) {
    _valueSets = new HashMap<K, WeakReference<ValueSet>>();
    _nonEmptyValueSets = mapFactory.value();
    _setFactory = setFactory;
    _size = 0;
  }
  
  
  protected void validateAdd(K key, V value) {}
  
  
  protected void addToRelation(K key, V value) {}
  
  
  protected void validateRemove(K key, V value) {}
  
  
  protected void validateRemoveKey(K key, PredicateSet<V> vals) {}
  
  
  protected void removeFromRelation(K key, V value) {}
  
  
  protected void validateClear() {}
  
  
  protected void clearRelation() {}
  
  public boolean contains(Object key, Object value) {
    PredicateSet<V> vals = _nonEmptyValueSets.get(key);
    return (vals != null) && vals.contains(value);
  }
  
  public PredicateSet<K> keys() { return new KeySet(); }
  public PredicateSet<V> match(K key) { return findMatch(key); }
  public Iterator<Pair<K, V>> iterator() { return new EntryIterator(); }
  
  public boolean isEmpty() { return _nonEmptyValueSets.isEmpty(); }
  public int size() { return _size; }
  public int size(int bound) { return _size < bound ? _size : bound; }
  public boolean isInfinite() { return false; }
  public boolean hasFixedSize() { return false; }
  public boolean isStatic() { return false; }
  
  public void added(K key, V value) { findMatch(key).doAdd(value); }
  public void removed(K key, V value) { findMatch(key).doRemove(value); }
  
  public void cleared() {
    for (K key : _nonEmptyValueSets.keySet()) {
      
      _valueSets.get(key).get().doClear(false);
    }
    _nonEmptyValueSets.clear();
  }
  
  
  private ValueSet findMatch(K key) {
    WeakReference<ValueSet> ref = _valueSets.get(key);
    if (ref != null) {
      ValueSet result = ref.get();
      if (result != null) { return result; }
    }
    return new ValueSet(key);
  }
  
  
  private class KeySet extends DelegatingSet<K> {
    public KeySet() { super(_nonEmptyValueSets.keySet()); }
    
    @Override public boolean add(K obj) { throw new UnsupportedOperationException(); }
    @Override public boolean addAll(Collection<? extends K> c) {
      throw new UnsupportedOperationException();
    }
    
    @Override public Iterator<K> iterator() {
      return new Iterator<K>() {
        final Iterator<K> _i = _delegate.iterator();
        K _last = null;
        public boolean hasNext() { return _i.hasNext(); }
        public K next() { _last = _i.next(); return _last; }
        public void remove() {
          if (_last == null) { throw new IllegalStateException(); }
          
          ValueSet vals = _valueSets.get(_last).get();
          validateRemoveKey(_last, new ImmutableSet<V>(vals));
          _i.remove();
          vals.clearAndNotifyAfterMapRemoval();
        }
      };
    }
    
    @Override public boolean remove(Object o) {
      Option<K> cast = CollectUtil.castIfContains(this, o);
      if (cast.isSome()) {
        K key = cast.unwrap();
        
        ValueSet vals = _valueSets.get(key).get();
        validateRemoveKey(key, new ImmutableSet<V>(vals));
        _delegate.remove(key);
        vals.clearAndNotifyAfterMapRemoval();
        return true;
      }
      else { return false; }
    }
    
    @Override public void clear() {
      validateClear();
      
      List<ValueSet> toClear = new ArrayList<ValueSet>(_delegate.size());
      for (K key : _delegate) { toClear.add(_valueSets.get(key).get()); }
      
      _delegate.clear();
      for (ValueSet v : toClear) { v.clearAfterMapRemoval(); }
      
      clearRelation();
    }
    
    @Override public boolean removeAll(Collection<?> c) {
      return abstractCollectionRemoveAll(c);
    }
    
    @Override public boolean retainAll(Collection<?> c) {
      return abstractCollectionRetainAll(c);
    }
  }
  
  
  private class ValueSet extends DelegatingSet<V> implements Serializable {
    private final K _key;
    private int _size;
    
    public ValueSet(K key) {
      super(_setFactory.value());
      _key = key;
      _size = _delegate.size();
      _valueSets.put(key, new WeakReference<ValueSet>(this));
      
      if (!_delegate.isEmpty()) { _nonEmptyValueSets.put(_key, this); }
    }
    
    @Override protected void finalize() throws Throwable {
      try { _valueSets.remove(_key); }
      finally { super.finalize(); }
    }
    
    @Override public boolean isEmpty() { return _size == 0; }
    @Override public int size() { return _size; }
    @Override public int size(int bound) { return _size < bound ? _size : bound; }
    
    @Override public ValueSetIterator iterator() { return new ValueSetIterator(); }
    
    @Override public boolean add(V val) {
      boolean result = !_delegate.contains(val);
      if (result) {
        validateAdd(_key, val);
        _delegate.add(val);
        finishAdd();
        addToRelation(_key, val);
      }
      return result;
    }
    
    @Override public boolean remove(Object o) {
      Option<V> cast = CollectUtil.castIfContains(_delegate, o);
      if (cast.isSome()) {
        V val = cast.unwrap();
        validateRemove(_key, val);
        _delegate.remove(val);
        finishRemove();
        removeFromRelation(_key, val);
        return true;
      }
      else { return false; }
    }
    
    @Override public void clear() {
      if (_size != 0) {
        validateRemoveKey(_key, new ImmutableSet<V>(this));
        Iterable<V> vals = IterUtil.snapshot(this);
        _delegate.clear();
        finishClear(true);
        for (V val : vals) { removeFromRelation(_key, val); }
      }
    }
    
    @Override public boolean addAll(Collection<? extends V> c) {
      return abstractCollectionAddAll(c);
    }
    @Override public boolean retainAll(Collection<?> c) {
      return abstractCollectionRetainAll(c);
    }
    @Override public boolean removeAll(Collection<?> c) {
      return abstractCollectionRemoveAll(c);
    }
    
    
    public void clearAndNotifyAfterMapRemoval() {
      if (_size != 0) {
        Iterable<V> vals = IterUtil.snapshot(this);
        try { _delegate.clear(); }
        catch (RuntimeException e) {
          
          _nonEmptyValueSets.put(_key, this);
          throw e;
        }
        finishClear(false);
        for (V val : vals) { removeFromRelation(_key, val); }
      }
    }
    
    
    public void clearAfterMapRemoval() {
      if (_size != 0) {
        try { _delegate.clear(); }
        catch (RuntimeException e) {
          
          _nonEmptyValueSets.put(_key, this);
          throw e;
        }
        finishClear(false);
      }
    }
    
    
    public void doAdd(V val) {
      boolean changed = _delegate.add(val);
      
      if (changed) { finishAdd(); }
    }
    
    
    public void doRemove(V val) {
      boolean changed = _delegate.remove(val);
      
      if (changed) { finishRemove(); }
    }
    
    
    public void doClear(boolean removeFromMap) {
      if (_size != 0) {
        _delegate.clear();
        finishClear(removeFromMap);
      }
    }
    
    
    private void finishAdd() {
      if (_size == 0) { _nonEmptyValueSets.put(_key, this); }
      _size++;
      ConcreteRelationIndex.this._size++;
    }
    
    
    private void finishRemove() {
      _size--;
      ConcreteRelationIndex.this._size--;
      if (_size == 0) { _nonEmptyValueSets.remove(_key); }
    }
    
    
    private void finishRemove(Iterator<Map.Entry<K, PredicateSet<V>>> nonEmptyMapIterator) {
      _size--;
      ConcreteRelationIndex.this._size--;
      if (_size == 0) { nonEmptyMapIterator.remove(); }
    }
    
    
    private void finishClear(boolean removeFromMap) {
      ConcreteRelationIndex.this._size -= _size;
      _size = 0;
      if (removeFromMap) { _nonEmptyValueSets.remove(_key); }
    }
    
    
    public final class ValueSetIterator implements Iterator<V> {
      private final Iterator<? extends V> _i;
      private V _last; 
      public ValueSetIterator() { _i = _delegate.iterator(); _last = null; }
      public boolean hasNext() { return _i.hasNext(); }
      public V next() { _last = _i.next(); return _last; }
      public void remove() {
        if (_last == null) { throw new IllegalStateException(); }
        validateRemove(_key, _last);
        _i.remove();
        finishRemove();
        removeFromRelation(_key, _last);
      }
      public void remove(Iterator<Map.Entry<K, PredicateSet<V>>> nonEmptyMapIterator) {
        if (_last == null) { throw new IllegalStateException(); }
        validateRemove(_key, _last);
        _i.remove();
        finishRemove(nonEmptyMapIterator);
        removeFromRelation(_key, _last);
      }
    }
  }
  
  private class EntryIterator implements Iterator<Pair<K, V>> {
    private final Iterator<Map.Entry<K, PredicateSet<V>>> _entries;
    private K _currentKey;
    private PredicateSet<V> _currentValues;
    private Iterator<V> _valuesIter;
    
    public EntryIterator() {
      _entries = _nonEmptyValueSets.entrySet().iterator();
      _currentKey = null;
      _currentValues = null;
      _valuesIter = null;
    }
    
    public boolean hasNext() {
      return (_valuesIter != null && _valuesIter.hasNext()) || _entries.hasNext();
    }
    
    public Pair<K, V> next() {
      if (_valuesIter == null || !_valuesIter.hasNext()) {
        Map.Entry<K, PredicateSet<V>> entry = _entries.next();
        _currentKey = entry.getKey();
        _currentValues = entry.getValue();
        _valuesIter = _currentValues.iterator();
      }
      return new Pair<K, V>(_currentKey, _valuesIter.next());
    }
    
    public void remove() {
      
      
      if (_valuesIter instanceof ConcreteRelationIndex.ValueSet.ValueSetIterator) {
        ((ValueSet.ValueSetIterator) _valuesIter).remove(_entries);
      }
      else { throw new UnsupportedOperationException(); }
    }
  }
  
}
