

package edu.rice.cs.drjava.config;

import edu.rice.cs.util.swing.Utilities;

import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.io.*;
import java.util.Enumeration;


public class ResourceBundleConfiguration extends FileConfiguration {
  
  protected final String _resourceBundleName;
  
  
  protected final ResourceBundle _bundle;
  
  
  protected final FileConfiguration _shadowed;
  
  
  public ResourceBundleConfiguration(String resourceBundleName, FileConfiguration shadowed) {
    super(shadowed.getFile());
    _resourceBundleName = resourceBundleName;
    _bundle = ResourceBundle.getBundle(resourceBundleName);
    _shadowed = shadowed;
    map = new OptionMap() {
      public <T> T getOption(OptionParser<T> o) {
        if (o==null) return _shadowed.getOptionMap().getOption(o);
        try {
          String str = _bundle.getString(o.getName());
          return o.parse(str); 
        }
        catch(MissingResourceException mre) {
          
          return _shadowed.getOptionMap().getOption(o);
        }
      }
      
      public <T> T setOption(Option<T> o, T val) {
        if (o==null) return _shadowed.getOptionMap().setOption(o, val);
        try {
          String str = _bundle.getString(o.getName());
          return null; 
        }
        catch(MissingResourceException mre) {
          
          return _shadowed.getOptionMap().setOption(o, val);
        }
      }
      
      public <T> String getString(OptionParser<T> o) {
        if (o==null) return _shadowed.getOptionMap().getString(o);
        try {
          String str = _bundle.getString(o.getName());
          return str; 
        }
        catch(MissingResourceException mre) {
          
          return _shadowed.getOptionMap().getString(o);
        }
      }
      
      public <T> void setString(OptionParser<T> o, String s) {
        if (o==null) _shadowed.getOptionMap().setString(o, s);
        try {
          String str = _bundle.getString(o.getName());
          return; 
        }
        catch(MissingResourceException mre) {
          
          _shadowed.getOptionMap().setString(o, s);
        }
      }
      
      public <T> T removeOption(OptionParser<T> o) {
        if (o==null) return _shadowed.getOptionMap().removeOption(o);
        try {
          String str = _bundle.getString(o.getName());
          return null; 
        }
        catch(MissingResourceException mre) {
          
          return _shadowed.getOptionMap().removeOption(o);
        }
      }
      
      public Iterable<OptionParser<?>> keys() {
        
        Iterable<OptionParser<?>> shadowedKeys = _shadowed.getOptionMap().keys();






        return shadowedKeys;
      }
    };
  }
  
  
  public <T> T setSetting(final Option<T> op, final T value) {
    if (op==null) return _shadowed.setSetting(op, value);
    try {
      String str = _bundle.getString(op.getName());
      return null; 
    }
    catch(MissingResourceException mre) {
      
      return _shadowed.setSetting(op, value);
    }
  }
  
  
  public <T> T getSetting(Option<T> op) {
    if (op==null) return _shadowed.getSetting(op);
    try {
      String str = _bundle.getString(op.getName());
      return op.parse(str); 
    }
    catch(MissingResourceException mre) {
      
      return _shadowed.getSetting(op);
    }
  }

  
  public <T> boolean isEditable(Option<T> op) {
    if (op==null) return _shadowed.isEditable(op);
    try {
      String str = _bundle.getString(op.getName());
      return false; 
    }
    catch(MissingResourceException mre) {
      
      return _shadowed.isEditable(op);
    }
  }
  
  
  public void resetToDefaults() {
    
    _shadowed.resetToDefaults();
  }
  
  
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("In resource bundle ");
    sb.append(_resourceBundleName);
    sb.append(":\n");
    boolean empty = true;
    Enumeration<String> keyEn = _bundle.getKeys();
    while(keyEn.hasMoreElements()) {
      String key = keyEn.nextElement();
      sb.append(key);
      sb.append(" = ");
      sb.append(_bundle.getString(key));
      sb.append("\n");
      empty = false;
    }
    if (empty) sb.append("\tnothing\n");
    sb.append("\nIn shadowed configuration:\n");
    sb.append(_shadowed);
    return sb.toString();
  }
  
  
  public void loadConfiguration() throws IOException {
    _shadowed.loadConfiguration(); 
  }
  
  
  public void saveConfiguration() throws IOException {
    _shadowed.saveConfiguration();
  }
  
  
  public void saveConfiguration(final String header) throws IOException {
    _shadowed.saveConfiguration(header);
  }
  
  
  public void loadConfiguration(InputStream is) throws IOException {
    _shadowed.loadConfiguration(is); 
  }

  
  public void saveConfiguration(OutputStream os, String header) throws IOException {
    _shadowed.saveConfiguration(os,header);
  }
  
  
  public <T> void addOptionListener(Option<T> op, OptionListener<T> l) {
    op.addListener(this,l);
    op.addListener(_shadowed,l);
  }
  
  
  public <T> void removeOptionListener(Option<T> op, OptionListener<T> l) {
    op.removeListener(this,l);
    op.removeListener(_shadowed,l);
  }
}
