

package edu.rice.cs.drjava.config;

import java.util.HashSet;


public class ConstantProperty extends EagerProperty {
  
  public ConstantProperty(String name, String value, String help) {
    super(name, help);
    if (value == null) { throw new IllegalArgumentException("DrJavaProperty value is null"); }
    _value = value;
    _isCurrent = true;
    resetAttributes();
  }
      
  
  public void update(PropertyMaps pm) { }
  
  
  public String getCurrent(PropertyMaps pm) { return _value; }

  
  public String toString() { return _value; }
  
  
  public boolean isCurrent() { return true; }
  
  
  public void invalidate() {
    
    invalidateOthers(new HashSet<DrJavaProperty>());
  }
} 
