

package edu.rice.cs.util;
import java.util.*;



public class OrderedHashSet<Type> implements Collection<Type> {
  private HashSet<Type> elements = new HashSet<Type>();
  private ArrayList<Type> order = new ArrayList<Type>();
  
  
  
  public boolean add(Type elt) {
    boolean validAdd = elements.add(elt);
    if (validAdd) order.add(elt);
    return validAdd;
  }
  
  public boolean addAll(Collection<? extends Type> c) { 
    throw new UnsupportedOperationException("OrderedHashSet does not support this operation");
  }
  
  public void clear() {
    elements.clear();
    order.clear();
  }
  
  public boolean contains(Object elt) { return elements.contains(elt); }
   
  public boolean containsAll(Collection<?> c) {
    throw new UnsupportedOperationException("OrderedHashSet does not support this operation");
  }
  
  public boolean equals(Object o) { 
    if ((o == null) || o.getClass() != getClass()) return false;
    return order.equals(elements());
  }
  
  public int hashCode() { return order.hashCode(); }
  
  public boolean isEmpty() { return order.isEmpty(); }
  
  public Type get(int i) { return order.get(i); }
  
  public Iterator<Type> iterator() { return new OHMIterator(); }
  
  
  public Type remove(int i) {
    Type elt = order.remove(i); 
    elements.remove(elt);
    return elt;
  }
  
  public boolean remove(Object elt) {
    elements.remove(elt);
    return order.remove(elt);  
  }
  
  public boolean removeAll(Collection<?> elts) {
    throw new UnsupportedOperationException("OrderedHashSet does not support this operation");
  }
  
  public boolean retainAll(Collection<?> elts) {
    throw new UnsupportedOperationException("OrderedHashSet does not support this operation");
  }
  
  public int size() { return order.size(); }
  
  public Object[] toArray() { return order.toArray(); }
  
  public <T> T[] toArray(T[] a) { return order.toArray(a); }
 
  public Collection<Type> elements() { return order; }
  
  public String toString() { return order.toString(); }
  
    
  class OHMIterator implements Iterator<Type> {
    
    Iterator<Type> it = order.iterator();
    
    
    Type lastElt = null;

    public boolean hasNext() { return it.hasNext(); }
    
    public Type next() {
      lastElt = it.next();
      return lastElt;
    }
    
    
    public void remove() {
      it.remove();                 
      elements.remove(lastElt);
    }
  }
}
