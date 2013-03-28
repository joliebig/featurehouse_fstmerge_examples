

package edu.rice.cs.drjava.ui.avail;

import edu.rice.cs.drjava.model.EventNotifier;
import java.util.HashSet;


public abstract class ConjoinedGUIAvailabilityListener implements GUIAvailabilityListener {
  protected final HashSet<ComponentType> _components = new HashSet<ComponentType>();
  protected final GUIAvailabilityNotifier _notifier;
  protected volatile boolean _lastValue = true;
  
  
  public ConjoinedGUIAvailabilityListener(GUIAvailabilityNotifier notifier, ComponentType... components) {
    _notifier = notifier;
    for(ComponentType c: components) {
      _components.add(c);
      _lastValue &= _notifier.isAvailable(c);
    }
  }
  
  
  public boolean isAvailable() {
    for(ComponentType c: _components) {
      if (!_notifier.isAvailable(c)) return false;
    }
    return true;
  }
  
    
  public void availabilityChanged(ComponentType component, boolean available) {
    if (_components.contains(component)) {
      boolean newValue = isAvailable();
      if (_lastValue != newValue) {
        _lastValue = newValue;
        availabilityChanged(newValue);
      }
    }
  }
  
    
  public abstract void availabilityChanged(boolean available);
}
