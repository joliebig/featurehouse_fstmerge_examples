

package edu.rice.cs.drjava.config;

import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.MissingResourceException;


public abstract class OptionParser<T> implements ParseStrategy<T> {
  
  
  public final String name;
  protected final T defaultValue;
  
  
  final HashMap<DefaultOptionMap,T> map = new HashMap<DefaultOptionMap,T>();
  
  
  public OptionParser(String name, T def) {
      this.name = name; defaultValue = def;
  }
  
  
  public String getName() { return name; }
  
  
  public T getDefault() { return defaultValue; }
  
  
  public abstract String getDefaultString();
  
  
  public abstract T parse(String value);
  
  
  public String toString() { return "Option<" + name + ", " + defaultValue + ">"; }
  
  
  
  abstract String getString(DefaultOptionMap om);
  
  
  T setString(DefaultOptionMap om, String val) { return setOption(om,parse(val)); }
  
  
  T getOption(DefaultOptionMap om) { return map.get(om); }
  
  
  T setOption(DefaultOptionMap om, T val) { return map.put(om,val); }
  
  
  T remove(DefaultOptionMap om) { return map.remove(om); }
}
