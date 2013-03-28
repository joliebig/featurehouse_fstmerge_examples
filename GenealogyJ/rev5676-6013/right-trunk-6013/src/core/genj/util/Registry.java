
package genj.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;


public class Registry {
  
  private final static Logger LOG = Logger.getLogger("genj.util");
  
  private String prefix;
  private Properties properties;
  
  private static Map<String, Registry> prefix2registry = new HashMap<String, Registry>();
  private static Map<File, Registry> file2registry = new HashMap<File, Registry>();
  
  
  public Registry(Registry registry, String view) {
    if (registry.prefix.length()>0)
      view = registry.prefix + "." + view;
    this.prefix = view;
    this.properties = registry.properties;
  }
  
  
  private Registry(String rootPrefix) {
    
    this.prefix = rootPrefix;
    
    
    this.properties = new SortingProperties();
    
    
    synchronized (Registry.class) {
      prefix2registry.put(prefix,this);
    }
  }

  
  public Registry(InputStream in) {
    
    prefix = "";
    properties = new SortingProperties();
    try {
      properties.load(in);
    } catch (Exception ex) {
    }
  }
  
  private Registry(File file) {
    
    prefix = "";
    properties = new SortingProperties();
    try {
      properties.load(new FileInputStream(file));
    } catch (Exception ex) {
    }
  }

  
  public static Registry get(File file) {
    synchronized (Registry.class) {
      Registry r = file2registry.get(file);
      if (r==null) {
        r = new Registry(file);
        file2registry.put(file, r);
      }
      return r;
    }
  }

  
  public static Registry get(Object source) {
    return get(source.getClass());
  }
  
  
  public static Registry get(Class<?> source) {
    return get(source.getName());
  }
  
  
  public static Registry get(String pckg) {

    String[] tokens = pckg.split("\\.");
    if (tokens.length==1)
      throw new IllegalArgumentException("default package not allowed");
    
    String prefix = tokens[0];
    
    Registry r;
    synchronized (Registry.class) {
      r = prefix2registry.get(prefix);
      if (r==null) {
        r = new Registry(tokens[0]);
        prefix2registry.put(prefix, r);
      }
    }

    return tokens.length==1 ? r : new Registry(r, pckg.substring(prefix.length()+1));
  }

  
  public void remove(String prefix) {
    List<Object> keys = new ArrayList<Object>(properties.keySet());
    for (int i=0,j=keys.size();i<j;i++) {
      String key = (String)keys.get(i);
      if (key.startsWith(prefix))
        properties.remove(key);
    }
  }
  
  
  @SuppressWarnings("unchecked")
  public <K,V> Map<K,V> get(String key, Map<K,V> def) {
    Map<K,V> result = new HashMap<K,V>();
    
    for (K subkey : def.keySet()) {
      
      V value = def.get(subkey);
      
      try {
        value = (V)getClass().getMethod("get", new Class[]{ String.class, value.getClass() })
          .invoke(this, new Object[]{ key+"."+subkey, value });
      } catch (Throwable t) {
      }
      
      result.put(subkey, value);
    }
    
    return result;
  }

  
  public int[] get(String key, int[] def) {

    
    int size = get(key,-1);
    if (size<0)
      return def;

    
    int result[] = new int[size];
    for (int i=0;i<size;i++) {
      result[i] = get(key+"."+(i+1),-1);
    }

    
    return result;
  }

  
  public Rectangle[] get(String key, Rectangle[] def) {

    
    int size = get(key,-1);
    if (size==-1)
      return def;

    
    Rectangle[] result = new Rectangle[size];
    Rectangle empty = new Rectangle(-1,-1,-1,-1);

    for (int i=0;i<size;i++) {
      result[i] = get(key+"."+(i+1),empty);
    }

    
    return result;
  }

  
  public String[] get(String key, String[] def) {

    
    int size = get(key,-1);
    if (size==-1)
      return def;

    
    String result[] = new String[size];
    for (int i=0;i<size;i++) {
      result[i] = get(key+"."+(i+1),"");
    }

    
    return result;
  }

  
  public float get(String key, float def) {

    
    String result = get(key,(String)null);

    
    if (result==null)
      return def;

    
    try {
      return Float.valueOf(result.trim()).floatValue();
    } catch (NumberFormatException ex) {
    }

    return def;
  }

  
  public int get(String key, int def) {

    
    String result = get(key,(String)null);

    
    if (result==null)
      return def;

    
    try {
      return Integer.parseInt(result.trim());
    } catch (NumberFormatException ex) {
    }

    return def;
  }

  
  public Dimension get(String key, Dimension def) {

    
    int w = get(key+".w", -1);
    int h = get(key+".h", -1);

    
    if ( (w==-1) || (h==-1) )
      return def;

    
    return new Dimension(w,h);
  }

  
  public Font get(String key, Font def) {

    String face = get(key+".name" ,(String)null);
    int style   = get(key+".style",-1);
    int size    = get(key+".size" ,-1);

    if ( (face==null)||(style==-1)||(size==-1) )
      return def;

    return new Font(face,style,size);
  }

  
  public Point get(String key, Point def) {

    
    int x = get(key+".x", Integer.MAX_VALUE);
    int y = get(key+".y", Integer.MAX_VALUE);

    
    if ( x==Integer.MAX_VALUE || y==Integer.MAX_VALUE )
      return def;

    
    return new Point(x,y);
  }

  
  public Point2D get(String key, Point2D def) {

    
    float x = get(key+".x", Float.NaN);
    float y = get(key+".y", Float.NaN);

    
    if ( Float.isNaN(x) || Float.isNaN(y) )
      return def;

    
    return new Point2D.Double(x,y);
  }

  
  public Rectangle get(String key, Rectangle def) {

    
    int x = get(key+".x", Integer.MAX_VALUE);
    int y = get(key+".y", Integer.MAX_VALUE);
    int w = get(key+".w", Integer.MAX_VALUE);
    int h = get(key+".h", Integer.MAX_VALUE);

    
    if ( (x==Integer.MAX_VALUE) || (y==Integer.MAX_VALUE) || (w==Integer.MAX_VALUE) || (h==Integer.MAX_VALUE) )
      return def;

    
    return new Rectangle(x,y,w,h);
  }

  
  public List<String> get(String key, List<String> def) {

    
    int size = get(key,-1);
    if (size==-1)
      return def;

    
    List<String> result = new ArrayList<String>();
    for (int i=0;i<size;i++) 
      result.add(get(key+"."+(i+1),""));

    
    return result;
  }

  
  public boolean get(String key, boolean def) {

    
    String result = get(key,(String)null);

    
    if (result==null)
      return def;

    
    if (result.equals("1"))
      return true;
    if (result.equals("0"))
      return false;

    
    return def;
  }

  
  public Color get(String key, Color def) {

    
    int result = get(key,Integer.MAX_VALUE);

    
    if (result==Integer.MAX_VALUE)
      return def;

    
    return new Color(result);
  }

  
  public String get(String key, String def) {
    
    
    if (prefix.length()>0)
      key = prefix+"."+key;

    
    String result = (String)properties.get(key);

    
    
    
    if (result==null)
      return def;
      
    
    return result;
  }

  
  public void put(String key, String value) {

    
    if (prefix.length()>0)
      key = prefix+"."+key;

    if (value==null)
      properties.remove(key);
    else
      properties.put(key,value);
  }

  
  public void put(String key, Map<String,?> values) {
    
    
    for (String subkey : values.keySet()) {
      
      Object value = values.get(subkey);
      
      try {
        value = getClass().getMethod("put", new Class[]{ String.class, value.getClass() })
          .invoke(this, new Object[]{ key+"."+subkey, value });
      } catch (Throwable t) {        
      }
    }
    
  }
  
  
  public void put(String key, int[] value) {

    
    int l = value.length;
    put(key,l);

    for (int i=0;i<l;i++)
      put(key+"."+(i+1),""+value[i]);

    
  }

  
  public void put(String key, Rectangle[] value) {

    
    int l = value.length;

    put(key,""+l);

    for (int i=0;i<l;i++)
      put(key+"."+(i+1),value[i]);

    
  }

  
  public void put(String key, Object value[]) {
    put(key,value,value.length);
  }

  
  public void put(String key, Object value[], int length) {

    
    int l = Math.min(value.length,length);

    put(key,""+l);

    for (int i=0;i<l;i++) {
      put(key+"."+(i+1),value[i].toString());
    }

    
  }

  
  public void put(String key, float value) {
    put(key,""+value);
  }

  
  public void put(String key, int value) {
    put(key,""+value);
  }

  
  public void put(String key, Dimension value) {

    
    put(key+".w",value.width);
    put(key+".h",value.height);

    
  }

  
  public void put(String key, Font value) {

    
    put(key+".name" ,value.getName() );
    put(key+".style",value.getStyle());
    put(key+".size" ,value.getSize() );

    
  }

  
  public void put(String key, Point value) {

    
    put(key+".x",value.x);
    put(key+".y",value.y);

    
  }


  
  public void put(String key, Point2D value) {

    
    put(key+".x",(float)value.getX());
    put(key+".y",(float)value.getY());

    
  }

  
  public void put(String key, Rectangle value) {

    
    put(key+".x",value.x);
    put(key+".y",value.y);
    put(key+".w",value.width);
    put(key+".h",value.height);

    
  }

  
  public void put(String key, Collection<?> values) {

    
    int l = values.size();
    put(key,l);
    
    Iterator<?> elements = values.iterator();
    for (int i=0;elements.hasNext();i++) {
      put(key+"."+(i+1),elements.next().toString());
    }

    
  }

  
  public void put(String key, boolean value) {

    
    put(key,(value?"1":"0"));

    
  }
  
  
  public void put(String key, Color value) {

    
    put(key,value.getRGB());

    
  }
  
  
  public void setFile(File file) {
    
    synchronized (Registry.class) {
    
      
      try {
        properties.clear();
        LOG.fine("Loading registry "+prefix+" from file "+file.getAbsolutePath());
        FileInputStream in = new FileInputStream(file);
        properties.load(in);
        in.close();
      } catch (Throwable t) {
        LOG.log(Level.FINE, "Failed to read registry from "+file+" ("+t.getMessage()+")");
      }

      file2registry.put(file, this);
    }
  }
  
  
  public static void persist() {
    
    
    for (File file : file2registry.keySet()) {
      Registry registry = file2registry.get(file);
      try {
        LOG.fine("Storing registry in file "+file.getAbsolutePath());
        file.getParentFile().mkdirs();
        FileOutputStream out = new FileOutputStream(file);
        registry.properties.store(out, registry.prefix);
        out.flush();
        out.close();
      } catch (IOException ex) {
        LOG.log(Level.INFO, "Can't store registry in file "+file.getAbsolutePath(), ex);
      }

    }

    
  }

  
  public void put(String key, JFrame frame) {
    Rectangle bounds = frame.getBounds();
    boolean maximized = frame.getExtendedState()==JFrame.MAXIMIZED_BOTH;
    if (bounds!=null&&!maximized)
      put(key, bounds);
    put(key+".maximized", maximized);
  }
  
  public JFrame get(String key, JFrame frame) {
    
    frame.setBounds(get(key, new Rectangle(0,0,640,480)));
    if (get(key+".maximized", true))
      frame.setExtendedState(Frame.MAXIMIZED_BOTH);
    
    return frame;
  }

  private static class SortingProperties extends Properties {
    @SuppressWarnings("unchecked")
    @Override
    public synchronized Enumeration<Object> keys() {
      Vector result = new Vector(super.keySet()); 
      Collections.sort(result);
      return result.elements();
    }
  };

} 
