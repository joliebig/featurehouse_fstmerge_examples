

package edu.rice.cs.drjava.ui.avail;

import edu.rice.cs.drjava.model.EventNotifier;


public class DefaultGUIAvailabilityNotifier extends GUIAvailabilityNotifier {
  
  public void junit(boolean available) {
    
    availabilityChanged(GUIAvailabilityListener.ComponentType.JUNIT, available);
    availabilityChanged(GUIAvailabilityListener.ComponentType.COMPILER, available);
    
  }

   
  public void junitStarted() { junit(false); }

   
  public void junitFinished() { junit(true); }  

  
  public void javadoc(boolean available) {
    
    availabilityChanged(GUIAvailabilityListener.ComponentType.JAVADOC, available);
    availabilityChanged(GUIAvailabilityListener.ComponentType.COMPILER, available);
  }

   
  public void javadocStarted() { javadoc(false); }

   
  public void javadocFinished() { javadoc(true); }  
}
