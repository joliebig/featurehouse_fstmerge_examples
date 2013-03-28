

package edu.rice.cs.drjava.ui.avail;

import edu.rice.cs.drjava.model.EventNotifier;
import java.util.HashMap;


public class GUIAvailabilityNotifier extends EventNotifier<GUIAvailabilityListener> implements GUIAvailabilityListener {
  
  protected final HashMap<ComponentType,Integer> _values = new HashMap<ComponentType,Integer>();
 
  
  
  
  public GUIAvailabilityNotifier() {
    for(ComponentType component: ComponentType.values()) {
      _values.put(component, 0);
    }
  }
  
  
  public boolean isAvailable(ComponentType component) {
    return (_values.get(component)==0);
  }

  
  public int getCount(ComponentType component) {
    return _values.get(component);
  }
  
  
  public void ensureUnavailable(ComponentType component) {
    if (isAvailable(component)) { availabilityChanged(component, false); }
  }
  
  
  public void unavailable(ComponentType component) {
    availabilityChanged(component, false);
  }
  
  
  public void ensureAvailable(ComponentType component) {
    
    if (!isAvailable(component)) {
      _values.put(component, 0);
      notifyListeners(component);
    }
  }
  
  
  public void available(ComponentType component) {
    availabilityChanged(component, true);
  }

  
  public void ensureAvailabilityIs(ComponentType component, boolean available) {
    if (available) { ensureAvailable(component); } else { ensureUnavailable(component); }
  }
  
    
  public void availabilityChanged(ComponentType component, boolean available) {
    
    int value = _values.get(component);
    boolean changed = false;
    if (available) {
      
      if (value==1) {
        _values.put(component, 0);
        changed = true;
      }
      else if (value>1) {
        _values.put(component, value-1);
      }
    }
    else {
      
      _values.put(component, value+1);
      changed = true;
    }
    if (changed) { notifyListeners(component); }
  }
  
  
  protected void notifyListeners(ComponentType component) {
    _lock.startRead();
    try { for (GUIAvailabilityListener cl : _listeners) {
      cl.availabilityChanged(component, isAvailable(component));
    } }
    finally { _lock.endRead(); }
  }
}
