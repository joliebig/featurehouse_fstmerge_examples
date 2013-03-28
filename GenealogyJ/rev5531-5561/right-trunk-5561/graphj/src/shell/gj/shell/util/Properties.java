
package gj.shell.util;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class Properties {

  
  private java.util.Properties content;

  
  public Properties() {
    content = new java.util.Properties();
  }

  
  public Properties(File file) {

    
    try {
      FileInputStream in = new FileInputStream(file);
      content.load(in);
      in.close();
    } catch (Exception ex) {
    }

    
  }

  
  public Properties(InputStream in) {
    this();
    
    
    try {
      content.load(in);
    } catch (Exception ex) {
    }

    
  }
  
  
  public Properties(Class<?> type, String name) {
    this();
    
    
    try {
      content.load(type.getResourceAsStream(name));
    } catch (Exception ex) {
    }

    
  }

  
  public int[] get(String key, int[] def) {

    
    int size = get(key,-1);
    if (size==-1)
      return def;

    
    int result[] = new int[size];
    for (int i=0;i<size;i++) {
      result[i] = get(key+"."+i,-1);
    }

    
    return result;
  }

  
  public Rectangle2D[] get(String key, Rectangle2D[] def) {

    
    int size = get(key,-1);
    if (size==-1)
      return def;

    
    Rectangle2D[] result = new Rectangle2D[size];
    Rectangle2D empty = new Rectangle2D.Double(-1,-1,-1,-1);

    for (int i=0;i<size;i++) {
      result[i] = get(key+"."+i,empty);
    }

    
    return result;
  }

  
  public Object[] get(String key, Object[] def) {
    
    
    String[] types = get(key,new String[0]);
    if (types.length==0)
      return def;
    
    
    Class<?> target = def.getClass().getComponentType();  
    
    
    List<Object> result = new ArrayList<Object>();
    
    try {
	    for (int i = 0; i<types.length; i++) {
	      Object instance = ReflectHelper.getInstance(types[i], target);
	      if (instance!=null) 
	        result.add(instance);
	    }
    } catch (Throwable t) {
      
    }
    
    
    return result.toArray((Object[])Array.newInstance(target,result.size()));
  }

  
  public String[] get(String key, String[] def) {

    
    int size = get(key,-1);
    if (size==-1)
      return def;

    
    String result[] = new String[size];
    for (int i=0;i<size;i++) {
      result[i] = get(key+"."+i,"");
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

  
  public Rectangle2D get(String key, Rectangle2D def) {

    
    int x = get(key+".x", Integer.MAX_VALUE);
    int y = get(key+".y", Integer.MAX_VALUE);
    int w = get(key+".w", Integer.MAX_VALUE);
    int h = get(key+".h", Integer.MAX_VALUE);

    
    if ( (x==Integer.MAX_VALUE) || (y==Integer.MAX_VALUE) || (w==Integer.MAX_VALUE) || (h==Integer.MAX_VALUE) )
      return def;

    
    return new Rectangle2D.Double(x,y,w,h);
  }

  
  public String get(String key, String def) {

    
    String result = content.getProperty(key);

    
    if (result==null)
      return def;

    
    result = result.trim();
    if (result.length()==0)
      return def;

    
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

  
  @Override
  public String toString() {
    return content.toString();
  }
}
