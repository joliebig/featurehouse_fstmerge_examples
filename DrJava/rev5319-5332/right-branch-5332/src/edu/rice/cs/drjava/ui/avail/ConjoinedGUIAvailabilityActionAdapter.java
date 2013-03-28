

package edu.rice.cs.drjava.ui.avail;

import edu.rice.cs.drjava.model.EventNotifier;

import java.util.HashMap;
import javax.swing.Action;


public class ConjoinedGUIAvailabilityActionAdapter extends ConjoinedGUIAvailabilityListener {
  protected final Action _adaptee;
  
  
  public ConjoinedGUIAvailabilityActionAdapter(Action adaptee,
                                               GUIAvailabilityNotifier notifier, ComponentType... components) {
    super(notifier, components);
    _adaptee = adaptee;
  }
  
    
  public void availabilityChanged(boolean available) {
    _adaptee.setEnabled(available);
  }
}
