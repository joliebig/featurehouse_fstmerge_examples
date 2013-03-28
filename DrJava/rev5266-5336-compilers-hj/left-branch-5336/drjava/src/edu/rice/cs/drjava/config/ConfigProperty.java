

package edu.rice.cs.drjava.config;

import edu.rice.cs.drjava.DrJava;

import java.util.Vector;


public class ConfigProperty extends EagerProperty {
  
  protected boolean _isList = false;
  
  
  public ConfigProperty(String name) {
    super(name, "Help not available.");
    resetAttributes();
  }

  
  public void update(PropertyMaps pm) {
    OptionMap om = DrJava.getConfig().getOptionMap();
    for (OptionParser<?> op : om.keys()) {
      String key = op.getName();
      String value = om.getString(op);
      if (_name.equals("config." + key)) {
        if (op instanceof VectorOption<?>) {
          @SuppressWarnings("unchecked")
          Vector<?> vec = ((VectorOption)op).parse(value);
          StringBuilder sb = new StringBuilder();
          for(Object o: vec) {
            sb.append(_attributes.get("sep"));
            sb.append(o.toString());
          }
          _value = sb.toString();
          if (_value.startsWith(_attributes.get("sep"))) {
            _value= _value.substring(_attributes.get("sep").length());
          }
        }
        else if (_name.equals("config.debug.step.exclude")) {
          java.util.StringTokenizer tok = new java.util.StringTokenizer(value);
          StringBuilder sb = new StringBuilder();
          while(tok.hasMoreTokens()) {
            sb.append(_attributes.get("sep"));
            sb.append(tok.nextToken());
          }
          _value = sb.toString();
          if (_value.startsWith(_attributes.get("sep"))) {
            _value= _value.substring(_attributes.get("sep").length());
          }
        }
        else {
          _value = value;
        }
        return;
      }
    }
    _value = "--unknown--";
  }

  
  public void resetAttributes() {
    _attributes.clear();
    OptionMap om = DrJava.getConfig().getOptionMap();
    for (OptionParser<?> op : om.keys()) {
      String key = op.getName();
      if (_name.equals("config." + key)) {
        if (op instanceof VectorOption<?>) {
          _isList = true;
          _attributes.put("sep", java.io.File.pathSeparator);
        }
        else if (_name.equals("config.debug.step.exclude")) {
          _isList = true;
          _attributes.put("sep", ",");
        }
        else _isList = false;
        return;
      }
    }
  }

  
  public String toString() { return _value; }
} 
