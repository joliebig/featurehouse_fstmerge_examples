

package edu.rice.cs.drjava.config;
import edu.rice.cs.util.UnexpectedException;

import java.util.Properties;
import java.io.*;
import java.lang.reflect.*;
public class OptionMapLoader implements OptionConstants {
  
  
  private static final DefaultOptionMap DEFAULTS = new DefaultOptionMap();
  private static final Properties DEFAULT_STRINGS = new Properties();
  private static volatile Field[] fields = OptionConstants.class.getDeclaredFields();
  
  static {
    
    for (int i = 0; i < fields.length; i++) {
      Field field = fields[i];
      int mods = field.getModifiers();
      if (Modifier.isStatic(mods) && Modifier.isPublic(mods) && Modifier.isFinal(mods)) {
        
        Option<?> option;
        try {
          Object o = field.get(null); 
          
          if (o == null || !(o instanceof Option<?>)) {
            continue; 
          }
          
          option = (Option<?>) o;
        }
        catch(IllegalAccessException e) {
          
          throw new UnexpectedException(e);
        }
        
        String sval = option.getDefaultString();
        DEFAULT_STRINGS.setProperty(option.name, sval);
        DEFAULTS.setString(option, sval);
      }
    }
  }
  
  
  public static final OptionMapLoader DEFAULT = new OptionMapLoader(DEFAULT_STRINGS);
  
  
  public OptionMapLoader(InputStream is) throws IOException {
    this(new Properties(DEFAULT_STRINGS));
    try { prop.load(is); }
    finally { is.close(); }
  }
  
  private final Properties prop;
  
  private OptionMapLoader(Properties prop) {
    this.prop = prop;
  }
  
  public void loadInto(OptionMap map) {
    java.util.ArrayList<OptionParseException> es = new java.util.ArrayList<OptionParseException>();
    for (OptionParser<?> option : DEFAULTS.keys()) {
      try {
        String val = prop.getProperty(option.name);
        map.setString(option, val);
      }
      catch(OptionParseException ope) {
        es.add(ope);
        map.setString(option, DEFAULT.prop.getProperty(option.name));
      }
    }
    if (es.size() > 0) throw new OptionParseException(es.toArray(new OptionParseException[0]));
  }
}
