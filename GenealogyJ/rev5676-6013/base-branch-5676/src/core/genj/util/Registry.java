
package genj.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Registry {
  
  private final static Logger LOG = Logger.getLogger("genj.util");

  private String view;
  private Properties properties;
  private Registry parent;

  private static Hashtable registries = new Hashtable();

  
  public Registry() {
    
    view       ="";
    
    properties = new Properties() {
      
      public synchronized Enumeration keys() {
        Vector result = new Vector(super.keySet()); 
        Collections.sort(result);
        return result.elements();
      }
    };
    
  }

  
  public Registry(InputStream in) {
    this();
    
    try {
      properties.load(in);
    } catch (Exception ex) {
    }
  }

  
  public Registry(String name) {
    this(name, (Origin)null);
  }
  
  
  public Registry(String name, Origin origin) {
    
    this();

    
    if (origin!=null) {
      
      LOG.fine("Loading registry '"+name+".properties' from origin "+origin);
      try {
        InputStream in = origin.open(name+".properties");
        properties.load(in);
        in.close();
      } catch (Throwable t) {
        LOG.log(Level.INFO, "Failed to read registry "+name+" from "+origin+" ("+t.getMessage()+")");
      }
    }
    
    
    File file = getFile(name);
    try {
      LOG.fine("Loading registry '"+name+"' from file "+file.getAbsolutePath());
      FileInputStream in = new FileInputStream(file);
      properties.load(in);
      in.close();
    } catch (Throwable t) {
      LOG.log(Level.INFO, "Failed to read registry "+name+" from "+file+" ("+t.getMessage()+")");
    }
    
    
    registries.put(name,this);
    
  }

  
  public Registry(Registry registry, String view) {

    
    if ( (view==null) || ((view = view.trim()).length()==0) ) {
      throw new IllegalArgumentException("View can't be empty");
    }

    
    this.view       = view;
    this.parent     = registry;

    
  }
  
  
  public void set(Registry registry) {
    this.properties = (Properties)registry.properties.clone();
  }
  
  
  public void remove(String prefix) {
    List keys = new ArrayList(properties.keySet());
    for (int i=0,j=keys.size();i<j;i++) {
      String key = (String)keys.get(i);
      if (key.startsWith(prefix))
        properties.remove(key);
    }
  }
  
  
  public Registry getRoot() {
    if (parent==null)
      return this;
    return parent.getRoot();
  }

  
  public Registry getParent() {
    return parent;
  }

  
  public static Registry lookup(String name, Origin origin) {
    Registry result = (Registry)registries.get(name);
    if (result!=null)
      return result;
    return new Registry(name, origin);
  }
  
  
  public String getView() {

    
    if (parent==null)
      return "";

    
    String s = parent.getView();
    return (s.length()==0 ? "" : s+".")+view;
  }

  
  public String getViewSuffix() {

    String v = getView();

    int pos = v.lastIndexOf('.');
    if (pos==-1)
      return v;

    return v.substring(pos+1);
  }
  
  
  public Map get(String prefix, Map def) {
    Map result = new HashMap();
    
    Iterator keys = def.keySet().iterator();
    while (keys.hasNext()) {
      
      Object key = keys.next();
      Object value = def.get(key);
      
      try {
        value = getClass().getMethod("get", new Class[]{ String.class, value.getClass() })
          .invoke(this, new Object[]{ prefix+"."+key, value });
      } catch (Throwable t) {
        
      }
      
      result.put(key, value);
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

  
  

  
  public Collection get(String key, Collection def) {

    
    int size = get(key,-1);
    if (size==-1)
      return def;

    
    Collection result;
    try {
      result = (Collection)def.getClass().newInstance();
    } catch (Throwable t) {
      return def;
    }
    
    
    for (int i=0;i<size;i++) {
      result.add(get(key+"."+(i+1),""));
    }

    
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

    
    String result;
    if (parent==null)
      result = properties.getProperty(key);
    else
      result = parent.get(view+"."+key,def);

    
    
    
    if (result==null)
      return def;
      
    
    return result;
  }

  
  public void put(String key, String value) {

    
    if (parent==null) {
      
      if (value==null)
        properties.remove(key);
      else
        properties.put(key,value);
    } else {
      parent.put(view+"."+key,value);
    }
  }

  
  public void put(String prefix, Map values) {
    
    
    Iterator keys = values.keySet().iterator();
    while (keys.hasNext()) {
      
      Object key = keys.next();
      Object value = values.get(key);
      
      try {
        value = getClass().getMethod("put", new Class[]{ String.class, value.getClass() })
          .invoke(this, new Object[]{ prefix+"."+key, value });
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

  
  public void put(String key, Collection values) {

    
    int l = values.size();
    put(key,l);
    
    Iterator elements = values.iterator();
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
  
  
  private static File getFile(String name) {
    
    name = name+".properties";
    
    String dir = EnvironmentChecker.getProperty(
      Registry.class,
      new String[]{ "user.home.genj" },
      ".",
      "calculate dir for registry file "+name
    );
    
    return new File(dir,name);
  }

  
  public static void persist() {
    
    
    Enumeration keys = registries.keys();
    while (keys.hasMoreElements()) {

      
      String key = keys.nextElement().toString();
      Registry registry = (Registry)registries.get(key);

      
      try {
        File file = getFile(key);
        
        LOG.fine("Storing registry in file "+file.getAbsolutePath());
        file.getParentFile().mkdirs();
        FileOutputStream out = new FileOutputStream(file);
        registry.properties.store(out,key);
        out.flush();
        out.close();
      } catch (IOException ex) {
      }

    }

    
  }

} 
