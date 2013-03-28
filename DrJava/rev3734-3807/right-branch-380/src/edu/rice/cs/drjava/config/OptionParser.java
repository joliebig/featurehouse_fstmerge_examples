

package edu.rice.cs.drjava.config;
import java.util.Hashtable;

public abstract class OptionParser<T> implements ParseStrategy<T> {
    
    
    public final String name;
    private final T defaultValue;

    
    final Hashtable<DefaultOptionMap,T> map = new Hashtable<DefaultOptionMap,T>();

    
    public OptionParser(String name, T def) { this.name = name; defaultValue = def; }
    
    
    public String getName() { return name; }

    
    public T getDefault() { return defaultValue; }

    
    public abstract String getDefaultString();
  
    
    public abstract T parse(String value);
     
    
    
    
    

    abstract String getString(DefaultOptionMap om);
    
    
    T setString(DefaultOptionMap om, String val) { return setOption(om,parse(val)); }
    
    
    T getOption(DefaultOptionMap om) { return map.get(om); }

    
    T setOption(DefaultOptionMap om, T val) { return map.put(om,val); }
    
    
    T remove(DefaultOptionMap om) { return map.remove(om); }
}











