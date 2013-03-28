

package edu.rice.cs.drjava.config;
import java.util.Vector;
import java.util.Iterator;

public class DefaultOptionMap implements OptionMap {
  
  private final Vector<OptionParser<?>> keys = new Vector<OptionParser<?>>();
  
  public <T> T getOption(OptionParser<T> o) { return o.getOption(this); }
  
  public <T> T setOption(Option<T> o, T val) {
    setOption(o);
    return o.setOption(this,val);
  }
  
  private <T> void setOption(OptionParser<T> o) { if (keys.indexOf(o) == -1) keys.add(o); }
  
  public <T> String getString(OptionParser<T> o) { return o.getString(this); }
  
  public <T> void setString(OptionParser<T> o, String s) {
    setOption(o);
    o.setString(this,s);
  }
  
  public <T> T removeOption(OptionParser<T> o) {
    keys.remove(o);
    return o.remove(this);
  }
  
  public Iterator<OptionParser<?>> keys() { return keys.iterator(); }
  
  public String toString() {
    StringBuffer result = new StringBuffer("\n{ ");
    
    for (OptionParser<?> key: keys) {
      result.append(key.name).append(" = ").append(getString(key)).append('\n');
    }
    
    result.append('}');
    return result.toString();
  }
}
