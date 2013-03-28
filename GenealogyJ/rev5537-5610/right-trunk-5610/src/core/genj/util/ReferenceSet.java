package genj.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ReferenceSet {

  
  private Map key2references = new HashMap();
  
  
  private int size = 0;
  
  
  public ReferenceSet() {
  }
  
  
  public Set getReferences(Object key) {
    
    if (key==null) 
      return Collections.EMPTY_SET;
    
    Set references = (Set)key2references.get(key);
    if (references==null) 
      return Collections.EMPTY_SET;
    
    return references;
  }
  
  
  public int getSize() {
    return size;
  }
  
  
  public int getSize(Object key) {
    
    if (key==null) 
      return 0;
    
    Set references = (Set)key2references.get(key);
    if (references==null) 
      return 0;
    
    return references.size();
  }

  
  public boolean add(Object key) {
    return add(key, null);
  }

  
  public boolean add(Object key, Object reference) {
    
    if (key==null) 
      return false;
    
    Set references = (Set)key2references.get(key);
    if (references==null) {
      references = new HashSet();
      key2references.put(key, references);
    }
    
    
    if (reference==null)
      return false;
    
    if (!references.add(reference)) 
      return false;
    
    size++;      
    
    return true;
  }
  
  
  public boolean remove(Object key, Object reference) {
    
    if (key==null) 
      return false;
    
    Set references = (Set)key2references.get(key);
    if (references==null) 
      return false;
    
    if (!references.remove(reference))
      return false;
    
    size--;
    
    if (references.isEmpty())
      key2references.remove(key);
    
    return true; 
  }
  
  
  public List getKeys() {
    return getKeys(null);
  }
  
  
  public List getKeys(Comparator comparator) {
    ArrayList result = new ArrayList(key2references.keySet()); 
    if (comparator!=null) 
      Collections.sort(result, comparator);
    else 
      Collections.sort(result, new Comparator() {
        public int compare(Object o1, Object o2) {
          return getSize(o1) - getSize(o2);
        }
      });
      
    return result;
  }


} 
