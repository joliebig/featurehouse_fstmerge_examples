

package edu.rice.cs.util;
import java.util.*;

public class OrderedBidirectionalHashMap<Type1, Type2> extends BidirectionalHashMap<Type1, Type2> {
  private ArrayList<Type2> order = new ArrayList<Type2>();
  
  public OrderedBidirectionalHashMap() { super(); }
  
  public void put(Type1 key, Type2 value) {
    super.put(key, value);
    order.add(value);
  }
   
  public Type2 removeValue(Type1 key) {
    Type2 value = super.removeValue(key);
    order.remove(value);
    return value;
  }
  
  public Type1 removeKey(Type2 value) {
    Type1 key = super.removeKey(value);
    order.remove(value);
    return key;
  }
  
  public Iterator<Type2> valuesIterator() { return new OBHMIterator(); }
  
  public Collection<Type2> values() { return order; }
  
  public void clear() {
    super.clear();
    order.clear();
  }
  
    
  class OBHMIterator implements Iterator<Type2> {
    
    Iterator<Type2> it = order.iterator();

    
    
    Type1 lastKey = null;
    Type2 lastValue = null;

    public boolean hasNext() { return it.hasNext(); }
    
    public Type2 next() {
      lastValue = it.next();
      return lastValue;
    }
    
    
    public void remove() {
      it.remove();                 
      lastKey = backward.get(lastValue);
      forward.remove(lastKey);     
      backward.remove(lastValue);  
      lastValue = null;
    }
  }
}
