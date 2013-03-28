

package edu.rice.cs.drjava.config;


public class JavaSystemProperty extends EagerProperty {
  
  public JavaSystemProperty(String name) {
    super(name, "Help not available.");
    resetAttributes();
  }

  
  public void update(PropertyMaps pm) {
    _value = System.getProperty(_name);
    if (_value == null) { _value = "--unknown--"; }
  }
} 
