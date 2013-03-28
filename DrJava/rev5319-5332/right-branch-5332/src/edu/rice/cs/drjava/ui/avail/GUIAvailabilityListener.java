

package edu.rice.cs.drjava.ui.avail;


public interface GUIAvailabilityListener {
  public static enum ComponentType {
    COMPILER, INTERACTIONS, DEBUGGER, DEBUGGER_SUSPENDED, JUNIT, JAVADOC,
      PROJECT, PROJECT_MAIN_CLASS, PROJECT_BUILD_DIR;
  }
  
    
  public void availabilityChanged(ComponentType component, boolean available);
}
