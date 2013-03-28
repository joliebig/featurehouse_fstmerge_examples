package genj.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ReferenceSet<KEY,REF> {

  
  private Map<KEY,Set<REF>> key2references = new HashMap<KEY,Set<REF>>();
  
  
  private int size = 0;
  
  
  public ReferenceSet() {
  }
  
  
  public Set<REF> getReferences(KEY key) {
    
    if (key==null) 
      return new HashSet<REF>();
    
    Set<REF> references = key2references.get(key);
    if (references==null) 
      return new HashSet<REF>();
    
    return references;
  }
  
  
  public int getSize() {
    return size;
  }
  
  
  public int getSize(KEY key) {
    
    if (key==null) 
      return 0;
    
    Set<REF> references = key2references.get(key);
    if (references==null) 
      return 0;
    
    return references.size();
  }

  
  public boolean add(KEY key) {
    return add(key, null);
  }

  
  public boolean add(KEY key, REF reference) {
    
    if (key==null) 
      return false;
    
    Set<REF> references = key2references.get(key);
    if (references==null) {
      references = new HashSet<REF>();
      key2references.put(key, references);
    }
    
    
    if (reference==null)
      return false;
    
    if (!references.add(reference)) 
      return false;
    
    size++;      
    
    return true;
  }
  
  
  public boolean remove(KEY key, REF reference) {
    
    if (key==null) 
      return false;
    
    Set<REF> references = key2references.get(key);
    if (references==null) 
      return false;
    
    if (!references.remove(reference))
      return false;
    
    size--;
    
    if (references.isEmpty())
      key2references.remove(key);
    
    return true; 
  }
  
  
  public List<KEY> getKeys() {
    return getKeys(null);
  }
  
  
  public List<KEY> getKeys(Comparator<Object> comparator) {
    ArrayList<KEY> result = new ArrayList<KEY>(key2references.keySet()); 
    if (comparator!=null) 
      Collections.sort(result, comparator);
    else 
      Collections.sort(result, new Comparator<KEY>() {
        public int compare(KEY o1, KEY o2) {
          return getSize(o1) - getSize(o2);
        }
      });
      
    return result;
  }

} 
