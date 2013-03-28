

package edu.rice.cs.drjava.config;

import edu.rice.cs.util.swing.Utilities;

import java.util.Hashtable;
import java.util.Vector;




public abstract class Option<T> extends OptionParser<T> implements FormatStrategy<T> {

  
  final Hashtable<Configuration,Vector<OptionListener<T>>> listeners =
    new Hashtable<Configuration,Vector<OptionListener<T>>>();

  
  public Option(String name, T def) { super(name,def); }

  
  public String format(T value) { return value.toString(); }

  public String getDefaultString() { return format(getDefault()); }

  
  
  
  

  
  String getString(DefaultOptionMap om) {
    return format(getOption(om));
  }

  
  void notifyListeners(Configuration config, T val) {
    final Vector<OptionListener<T>> v = listeners.get(config);
    if (v == null) return; 
    final OptionEvent<T> e = new OptionEvent<T>(this, val);
    final int size = v.size();
    Utilities.invokeLater(new Runnable() { 
      public void run() {
        for (int i = 0; i < size; i++) v.get(i).optionChanged(e);
      }
    });
  }

  
  void addListener(Configuration c, OptionListener<T> l) {
    Vector<OptionListener<T>> v = listeners.get(c);
    if (v == null) {
      v = new Vector<OptionListener<T>>();
      listeners.put(c,v);
    }
    v.add(l);
  }

  
  void removeListener(Configuration c, OptionListener<T> l) {
    Vector<OptionListener<T>> v = listeners.get(c);
    if (v != null && v.remove(l) && v.size() == 0) listeners.remove(c);  
  }
}




