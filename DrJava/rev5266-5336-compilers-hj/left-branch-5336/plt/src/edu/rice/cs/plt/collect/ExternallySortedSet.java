

package edu.rice.cs.plt.collect;

import java.util.*;
import edu.rice.cs.plt.iter.SizedIterable;


public class ExternallySortedSet<T, C extends Comparable<? super C>> implements SizedIterable<T> {

  
  private final SortedSet<T> _set;
  
  
  private final Map<T, C> _orderByMap;
  
  
  private final C _lowerBound;
  
  
  private final C _upperBound;
  
  
  public ExternallySortedSet() {
    _set = new TreeSet<T>(new Comparator<T>() {
      
      public int compare(T t1, T t2) { 
        return _orderByMap.get(t1).compareTo(_orderByMap.get(t2));
      }
    });
    
    _orderByMap = new HashMap<T, C>();
    _lowerBound = null;
    _upperBound = null;
  }
  
  
  private ExternallySortedSet(SortedSet<T> set, Map<T, C> orderByMap, C lowerBound, 
                              C upperBound) {
    _set = set;
    _orderByMap = orderByMap;
    _lowerBound = lowerBound;
    _upperBound = upperBound;
  }
  
  public boolean isEmpty() { return _set.isEmpty(); }
  
  public int size() { return _set.size(); }
  
  public int size(int bound) {
    int result = _set.size();
    return result <= bound ? result : bound;
  }
 
  public boolean isInfinite() { return false; }
  
  public boolean hasFixedSize() { return false; }
  
  public boolean isStatic() { return false; }
  
  public boolean contains(Object element) {
    
    return _orderByMap.containsKey(element) && _set.contains(element);
  }
  
  
  public Iterator<T> iterator() {
    final Iterator<T> setI = _set.iterator();
    return new Iterator<T>() {
      private T _last = null;
      
      public boolean hasNext() { return setI.hasNext(); }
      
      public T next() { 
        _last = setI.next();
        return _last;
      }
      
      public void remove() { 
        if (_last == null) { throw new IllegalStateException(); }
        else {
          setI.remove();
          _orderByMap.remove(_last);
          _last = null;
        }
      }
      
    };
  }
  
  
  public Object[] toArray() { return _set.toArray(); }
  
  
  public <S> S[] toArray(S[] a) { return _set.toArray(a); }
  
  
  public boolean add(T element, C orderBy) {
    if (element == null) { throw new NullPointerException(); }
    else {
      assertInBounds(orderBy);
      if (contains(element)) { return false; }
      else {
        _orderByMap.put(element, orderBy);
        _set.add(element);
        return true;
      }
    }
  }
  
  
  public boolean remove(Object element) {
    if (contains(element)) {
      _set.remove(element);
      _orderByMap.remove(element);
      return true;
    }
    else { return false; }
  }
  
  
  public boolean containsAll(Iterable<?> i) {
    for (Object o : i) { if (! contains(o)) { return false; } }
    return true;
  }
  
  
  public boolean addAll(ExternallySortedSet<? extends T, ? extends C> s) {
    if ((_lowerBound == null || 
         (s._lowerBound != null && _lowerBound.compareTo(s._lowerBound) <= 0)) &&
        (_upperBound == null ||
         (s._upperBound != null && _upperBound.compareTo(s._upperBound) >= 0))) {
      
      return uncheckedAddAll(s);
    }
    else {
      return checkedAddAll(s);
    }
  }
  
  
  private boolean checkedAddAll(ExternallySortedSet<? extends T, ? extends C> s) {
    boolean result = false;
    for (T t : s) {
      
      result = result | add(t, s._orderByMap.get(t));
    }
    return result;
  }
  
  
  private boolean uncheckedAddAll(ExternallySortedSet<? extends T, ? extends C> s) {
    for (Map.Entry<? extends T, ? extends C> e : s._orderByMap.entrySet()) {
      if (! _orderByMap.containsKey(e.getKey())) { _orderByMap.put(e.getKey(), e.getValue()); }
    }
    boolean result = _set.addAll(s._set);
    return result;
  }
  
  
  public boolean retainAll(Collection<?> c) {
    boolean result = false;
    for (Iterator<T> i = iterator(); i.hasNext(); ) {
      T t = i.next();
      if (! c.contains(t)) { i.remove(); result = true; }
    }
    return result;
  }
  
  
  public boolean retainAll(ExternallySortedSet<?, ?> s) {
    boolean result = false;
    for (Iterator<T> i = iterator(); i.hasNext(); ) {
      T t = i.next();
      if (! s.contains(t)) { i.remove(); result = true; }
    }
    return result;
  }
    
  
  public boolean removeAll(Iterable<?> i) {
    boolean result = false;
    
    for (Object o : i) { result = result | remove(o); }
    return result;
  }
  
  
  public void clear() {
    
    for (T t : _set) { _orderByMap.remove(t); }
    _set.clear();
  }
  
  
  public ExternallySortedSet<T, C> subSet(C from, C to) {
    assertInBounds(from);
    assertInBounds(to);
    T fromElement = firstAt(from);
    T toElement = firstAt(to);

    SortedSet<T> subSet;
    if (fromElement == null) {
      if (toElement == null) { subSet = _set; }
      else { subSet = _set.headSet(toElement); }
    }
    else if (toElement == null) { subSet = _set.tailSet(fromElement); }
    else { subSet = _set.subSet(fromElement, toElement); }
      
    return new ExternallySortedSet<T, C>(subSet, _orderByMap, from, to);
  }
  
  
  public ExternallySortedSet<T, C> headSet(C to) {
    assertInBounds(to);
    T toElement = firstAt(to);
    SortedSet<T> subSet;
    if (toElement == null) { subSet = _set; }
    else { subSet = _set.headSet(toElement); }
    return new ExternallySortedSet<T, C>(subSet, _orderByMap, null, to);
  }
  
  
  public ExternallySortedSet<T, C> tailSet(C from) {
    assertInBounds(from);
    T fromElement = firstAt(from);
    SortedSet<T> subSet;
    if (fromElement == null) { subSet = _set; }
    else { subSet = _set.tailSet(fromElement); }
    return new ExternallySortedSet<T, C>(subSet, _orderByMap, from, null);
  }
  
  public T first() { return _set.first(); }
  
  public T last() { return _set.last(); }
  
  
  private T firstAt(C c) {
    _orderByMap.put(null, c); 
    SortedSet<T> resultSet = _set.tailSet(null);
    T result = null;
    if (! resultSet.isEmpty()) { result = resultSet.first(); }
    _orderByMap.remove(null);
    return result;
  }
  
  private void assertInBounds(C c) {
    if (_lowerBound != null && c.compareTo(_lowerBound) < 0) {
      throw new IllegalArgumentException(c + " is < this set's lower bound: " + _lowerBound);
    }
    if (_upperBound != null && c.compareTo(_upperBound) >= 0) {
      throw new IllegalArgumentException(c + " is >= this set's upper bound: " + _upperBound);
    }
  }
  
}
