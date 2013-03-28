

package edu.rice.cs.drjava.config;

import edu.rice.cs.util.swing.Utilities;


public class Configuration {  
  
  
  protected volatile OptionMap map;
  
  
  protected volatile Exception _startupException;
  
  
  public Configuration(OptionMap om) {
    map = om;
    _startupException = null;
  }
  
  
  public <T> T setSetting(final Option<T> op, final T value) {
    T ret = map.setOption(op, value);

    Utilities.invokeLater(new Runnable() { public void run() { op.notifyListeners(Configuration.this, value); } });
    return ret;
  }
  
  
  public <T> T getSetting(Option<T> op) { return map.getOption(op); }
  
  
  public <T> void addOptionListener(Option<T> op, OptionListener<T> l) { op.addListener(this,l); }
  
  
  public <T> void removeOptionListener(Option<T> op, OptionListener<T> l) { op.removeListener(this,l); }
  
  
  public void resetToDefaults() { OptionMapLoader.DEFAULT.loadInto(map); }
  
  
  public boolean hadStartupException() { return _startupException != null; }
  
  
  public Exception getStartupException() { return _startupException; }
  
  
  public void storeStartupException(Exception e) { _startupException = e; }
  
  
  public String toString() { return map.toString(); }
  
  
  public OptionMap getOptionMap() { return map; }
}
