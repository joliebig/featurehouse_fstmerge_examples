

package edu.rice.cs.drjava.config;


public abstract class EagerProperty extends DrJavaProperty {
  
  public EagerProperty(String name, String help) {
    super(name, help);
  }

  
  public EagerProperty(String name, String value, String help) {
    super(name, value, help);
  }

  
  public String getLazy(PropertyMaps pm) {
    return getCurrent(pm);
  }
  
  
  public boolean isCurrent() { return false; }
} 
