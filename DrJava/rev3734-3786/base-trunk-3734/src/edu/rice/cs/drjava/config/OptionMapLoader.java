

package edu.rice.cs.drjava.config;
import edu.rice.cs.util.UnexpectedException;

import java.util.Properties;
import java.util.Iterator;
import java.io.*;
import java.lang.reflect.*;
public class OptionMapLoader implements OptionConstants {

  
  private static DefaultOptionMap DEFAULTS = new DefaultOptionMap();
  private static Properties DEFAULT_STRINGS = new Properties();

  static {
    
    Field[] fields = OptionConstants.class.getDeclaredFields();
    for (int i = 0; i < fields.length; i++) {
      Field field = fields[i];
      int mods = field.getModifiers();
      if (Modifier.isStatic(mods) && Modifier.isPublic(mods) && Modifier.isFinal(mods)) {
        
        Option<?> option;
        try {
          Object o = field.get(null); 
          
          if (o == null || !(o instanceof Option)) {
            continue; 
          }

          option = (Option<?>) o;
        }
        catch(IllegalAccessException e) {
          
          throw new UnexpectedException(e);
        }

        String sval = option.getDefaultString();
        DEFAULT_STRINGS.setProperty(option.name,sval);
        DEFAULTS.setString(option,sval);
      }
    }
  }

  
  public static final OptionMapLoader DEFAULT = new OptionMapLoader(DEFAULT_STRINGS);

  
  public OptionMapLoader(InputStream is) throws IOException {
    this(new Properties(DEFAULT_STRINGS));
    try {
      prop.load(is);
    }
    finally {
      is.close();
    }
  }

  private final Properties prop;

  private OptionMapLoader(Properties prop) {
    this.prop = prop;
  }

  public void loadInto(OptionMap map) {
    Iterator<OptionParser<?>> options = DEFAULTS.keys();
    while(options.hasNext()) {
      OptionParser<?> option = options.next();
      String val = prop.getProperty(option.name);
      map.setString(option,val);
    }
  }
}
