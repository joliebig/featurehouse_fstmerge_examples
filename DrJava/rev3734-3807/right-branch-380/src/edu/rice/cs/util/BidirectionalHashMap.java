

package edu.rice.cs.util;
import java.util.*;

public class BidirectionalHashMap<Type1, Type2> {
  HashMap<Type1, Type2> forward = new HashMap<Type1, Type2>();
  HashMap<Type2, Type1> backward = new HashMap<Type2, Type1>();
  
  public void put(Type1 key, Type2 value) {
    if (forward.containsKey(key)) {
      throw new IllegalArgumentException("Key "  + key + " exists in hash already.");
    }
    if (forward.containsValue(value)) {
      throw new IllegalArgumentException("Double hashes must be one to one. " + value + " exists already in hash.");
    }      
    forward.put(key, value);
    backward.put(value, key);
  }
  
  public Type2 getValue(Type1 key) { return forward.get(key); }

  public Type1 getKey(Type2 value) { return backward.get(value); }
  
  public boolean containsKey(Type1 key) { return forward.containsKey(key); }
  
  public boolean containsValue(Type2 value) { return backward.containsKey(value); }
  
  public Iterator<Type2> valuesIterator() { return new BHMIterator(); }
  
  public boolean isEmpty() { return forward.isEmpty(); }
  
  
  public Collection<Type2> values() { return forward.values(); }  
  
  public Object[] valuesArray() { return values().toArray(); }  
  public  Type2[] valuesArray(Type2[] a) { return values().toArray(a); }  
  
  public Type2 removeValue(Type1 key) {
    Type2 tmp = forward.remove(key);
    backward.remove(tmp);
    return tmp;
  }
  
  public Type1 removeKey(Type2 value) {
    Type1 tmp = backward.remove(value);
    forward.remove(tmp);
    return tmp;
  }
  
  public int size() { return forward.size(); }
 
  
  public void clear() {
    forward = new HashMap<Type1, Type2>();
    backward = new HashMap<Type2, Type1>();
  }
  
  public String toString() {
    String ret = "";
    ret = "forward = " + forward.values() + "\nbackward = " + backward.values();
    return ret;
  }
  
  
  class BHMIterator implements Iterator<Type2> {
    
    Iterator<Type2> forwardIt = forward.values().iterator();
    
    
    Type2 lastValue = null;
    
    public boolean hasNext() { 
      return forwardIt.hasNext(); 
    }
    
    public Type2 next() { 
      lastValue = forwardIt.next(); 
      return lastValue;
    }
    
    
    public void remove() {
      forwardIt.remove();          
      backward.remove(lastValue);  
      lastValue = null;
    }
  }
}
