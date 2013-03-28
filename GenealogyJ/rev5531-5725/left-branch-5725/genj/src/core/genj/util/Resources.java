
package genj.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;


public class Resources {
  
  
  private static Map instances = new HashMap();

  
  private Map key2string;
  private List keys;

  
  private String pkg;

  
  private WeakHashMap msgFormats = new WeakHashMap();
  
  
  public Resources(InputStream in) {
    
    key2string = new HashMap();
    keys = new ArrayList(1000);
    
    try {
      load(in);
    } catch (IOException e) {
      
    }
  }
  
  
  public static Resources get(Object packgeMember) {
    return get(calcPackage(packgeMember));
  }

  
  public static Resources get(String packge) {
    synchronized (instances) {
      Resources result = (Resources)instances.get(packge);
      if (result==null) {
        result = new Resources(packge);
        instances.put(packge, result);
      }
      return result;
    }
  }
  
  
  private static String calcPackage(Object object) {
    Class clazz = object instanceof Class ? (Class)object : object.getClass();
    String name = clazz.getName();
    int last = name.lastIndexOf('.');
    return last<0 ? "" : name.substring(0, last);
  }
  
  
  private String calcFile(String pkg, String lang, String country) {

    
    String file = '/'+pkg.replace('.','/')+"/resources";
    
    
    if (lang!=null) {
      file += '_'+lang;
      if (country!=null) {
        file += '_'+country;
      }
    }
    
    return file+".properties";   
  }

  
  private Resources(String pkg) {
    
    this.pkg=pkg;
  }
  
  
  public void load(InputStream in) throws IOException {
    load(in, keys, key2string);
  }
  
  private static String trim(String s) {
    
    
    int start = 0;
    for (int len=s.length(); start<len; start++) {
      if (!Character.isWhitespace(s.charAt(start)))
        break;
    }
    int end = s.length();
    
    
    int comment = s.indexOf("*/", start);
    if (comment>=0)
      start = comment+2;
    
    
    comment = s.indexOf("/*", start);
    if (comment>=0)
      end = comment;
    
    return s.substring(start,end);
  }
  
  
  private static void load(InputStream in, List keys, Map key2string) throws IOException {
    
    if (in==null)
      throw new IOException("can't load resources from null");
    
    try {
      BufferedReader lines = new BufferedReader(new InputStreamReader(in, "UTF-8"));
      
      String key, val, last = null;
      while (true) {
        
        String line = lines.readLine();
        if (line==null) 
          break;
        String trimmed = trim(line);
        if (trimmed.length()==0) {
          last = null;
          continue;
        }
        
        if (last!=null) {
          
          if (trimmed.charAt(0)=='+') {
            key2string.put(last, key2string.get(last)+"\n"+breakify(trimmed.substring(1)));
            continue;
          }
          
          if (trimmed.charAt(0)=='&') {
            key2string.put(last, key2string.get(last)+breakify(trimmed.substring(1)));
            continue;
          }
          
          if (line.charAt(0)==' ') {
            String appendto = (String)key2string.get(last);
            if (!(appendto.endsWith(" ")||appendto.endsWith("\n"))) appendto += " ";
            key2string.put(last, appendto + breakify(trimmed));
            continue;
          }
        } 
          
        
        if (!Character.isLetter(line.charAt(0)))
          continue;
        
        
        int i = trimmed.indexOf('=');
        if (i<0) 
          continue;
        key = trimmed.substring(0, i).trim();
        if (key.indexOf(' ')>0)
          continue;
        val = trim(trimmed.substring(i+1));
        keys.add(key);
        
        
        key = key.toLowerCase();
        key2string.put(key, breakify(val));
        
        
        last = key;
        
      }

    } catch (UnsupportedEncodingException e) {
      throw new IOException(e.getMessage());
    }
  }

  private static String breakify(String string) {
    while (true) {
      int i = string.indexOf("\\n");
      if (i<0) break;
      string = string.substring(0,i) + '\n' + string.substring(i+2);
    }
    return string;
  }
  
  
  private Map getKey2String() {
    
    
    if (key2string!=null)
      return key2string;
    
    
    synchronized (this) {
      
      
      if (key2string!=null)
        return key2string;
      
      
      Locale locale = Locale.getDefault();
      Map tmpKey2Val = new HashMap();    
      List tmpKeys = new ArrayList(100);

      
      try {
        load(getClass().getResourceAsStream(calcFile(pkg, null, null)), tmpKeys, tmpKey2Val);
      } catch (Throwable t) {
      }
      
      
      try {
        load(getClass().getResourceAsStream(calcFile(pkg, locale.getLanguage(), null)), tmpKeys, tmpKey2Val);
      } catch (Throwable t) {
      }
  
      
      try {
        load(getClass().getResourceAsStream(calcFile(pkg, locale.getLanguage(), locale.getCountry())), tmpKeys, tmpKey2Val);
      } catch (Throwable t) {
      }

      
      key2string = tmpKey2Val;
      keys = tmpKeys;
    }
    
    
    return key2string;
  }
  
  
  public boolean contains(String key) {
    return getString(key, false) != null;
  }
  
  
  public String getString(String key, boolean notNull) {
    String result = (String)getKey2String().get(key.toLowerCase());
    if (result==null&&notNull) result = key;
    return result;
  }
  
  
  public String getString(String key) {
    return getString(key, true);
  }

  
  public String getString(String key, Object substitute) {
    return getString(key, new Object[]{ substitute });
  }

  
  public String getString(String key, Object[] substitutes) {
    return getString(key, substitutes, true);
  }
  
  
  public String getString(String key, Object[] substitutes, boolean notNull) {

    
    MessageFormat format = (MessageFormat)msgFormats.get(key);
    if (format==null) {
      String string = getString(key, false);
      if (string==null)
        return notNull ? key : null;
      format = getMessageFormat(string);
      msgFormats.put(key, format);
    }

    
    return format.format(substitutes);
  }
  
  
  public static MessageFormat getMessageFormat(String pattern) {
    
    
    if (pattern.indexOf('\'')>=0) {
      StringBuffer buffer = new StringBuffer(pattern.length()+8);
      for (int i=0,j=pattern.length();i<j;i++) {
        char c = pattern.charAt(i);
        buffer.append(c);
        if (c=='\'') buffer.append('\'');
      }
      pattern = buffer.toString();
    }
    
    return new MessageFormat(pattern);
  }

  
  public List getKeys() {
    
    getKey2String();
    return keys;
  }
  
} 
