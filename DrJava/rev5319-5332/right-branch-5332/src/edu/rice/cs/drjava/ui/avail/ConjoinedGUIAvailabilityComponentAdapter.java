

package edu.rice.cs.drjava.ui.avail;

import edu.rice.cs.drjava.model.EventNotifier;

import java.util.HashMap;
import java.awt.Component;


public class ConjoinedGUIAvailabilityComponentAdapter extends ConjoinedGUIAvailabilityListener {
  protected final Component _adaptee;
  
  
  public ConjoinedGUIAvailabilityComponentAdapter(Component adaptee,
                                                  GUIAvailabilityNotifier notifier, ComponentType... components) {
    super(notifier, components);
    _adaptee = adaptee;
  }
  
    
  public void availabilityChanged(boolean available) {
    _adaptee.setEnabled(available);
  }
}
