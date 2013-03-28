

package edu.rice.cs.drjava.config;


public abstract class DrJavaActionProperty extends DrJavaProperty {  
  
  public DrJavaActionProperty(String name, String help) { super(name,help); }

  
  public DrJavaActionProperty(String name, String value, String help) { super(name, value, help); }
  
  
  public String getCurrent(PropertyMaps pm) {
    invalidate();
    _value = "";
    return super.getCurrent(pm);
  }
} 
