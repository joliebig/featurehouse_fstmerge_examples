
package genj.print;

import genj.util.Registry;
import genj.util.WordBuffer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.logging.Level;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.TextSyntax;
import javax.print.attribute.URISyntax;
import javax.print.attribute.standard.MediaPrintableArea;


public class PrintRegistry extends Registry {
  
  public static PrintRegistry get(Object source) {
    
    Registry r = Registry.get(source);
    
    return new PrintRegistry(r);
  }

  
  private PrintRegistry(Registry registry) {
    super(registry, "");
  }
  
  
  public void put(PrintService service) {
    super.put("service", service.getName());
  }
  
  
  public PrintService get(PrintService def) {
    String name = super.get("service", "");
    PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
    for (int i = 0; i < services.length; i++) {
      if (services[i].getName().equals(name))
        return services[i];
    } 
    return def;
  }
  
  
  public void put(AttributeSet set) {
    WordBuffer track = new WordBuffer();
    Attribute[] attrs = set.toArray();
    for (int i = 0; i < attrs.length; i++) {
      String key = put(attrs[i]);
      if (key!=null)
        track.append(key);
    }
    super.put("attributes", track.toString());
  }
  
  
  @SuppressWarnings("unchecked")
  public void get(AttributeSet set) {
    
    StringTokenizer attributes = new StringTokenizer(super.get("attributes", ""));
    while (attributes.hasMoreTokens()) {
      String attribute = attributes.nextToken();
      try {
        Attribute a = get((Class<Attribute>)Class.forName(attribute), null);
        if (a!=null)
          set.add(a);
        else
          PrintTask.LOG.log(Level.INFO, "Couldn't restore print attribute "+attribute);
      } catch (Throwable t) {
        PrintTask.LOG.log(Level.WARNING, "Error restoring print attribute "+attribute, t);
      }
    }
    
  }
  
  
  public String put(Attribute attr) {
    
    if (attr instanceof EnumSyntax)
      return put((EnumSyntax)attr);
    if (attr instanceof IntegerSyntax)
      return put((IntegerSyntax)attr);
    if (attr instanceof URISyntax)
      return put((URISyntax)attr);
    if (attr instanceof MediaPrintableArea)
      return put((MediaPrintableArea)attr);
    if (attr instanceof TextSyntax)
      return put((TextSyntax)attr);
    
    return null;
  }

  
  public Attribute get(Class<Attribute> type, Attribute def) {

    
    if (!Attribute.class.isAssignableFrom(type))
      throw new IllegalArgumentException("only Attribute types allowed");
    if (def!=null&&!type.isAssignableFrom(def.getClass()))
      throw new IllegalArgumentException("def/Attribute types mismatch");
    
    if (EnumSyntax.class.isAssignableFrom(type))
      return getEnumSyntax(type, def);
    
    if (IntegerSyntax.class.isAssignableFrom(type))
      return getIntegerSyntax(type, def);
    
    if (URISyntax.class.isAssignableFrom(type))
      return getURISyntax(type, def);
    
    if (MediaPrintableArea.class.isAssignableFrom(type))
      return getMediaPrintableArea(def);
    
    if (TextSyntax.class.isAssignableFrom(type))
      return getTextSyntax(type, def);
    
    return null;
  }

  
  private String put(TextSyntax syntax) {
    String key = syntax.getClass().getName();
    super.put(key, syntax.getValue());
    return key;
  }

  
  private Attribute getTextSyntax(Class<Attribute> type, Attribute def) {
    
    String txt = super.get(type.getName(),(String)null);
    if (txt==null)
      return def;
    
    try {
      return type.getConstructor(new Class[]{String.class, Locale.class}).newInstance(new Object[]{txt, null});
    } catch (Throwable t) {
      return def;
    }
  }

  
  private String put(URISyntax syntax) {
    String key = syntax.getClass().getName();
    super.put(key, ""+syntax.getURI());
    return key;
  }

  
  private Attribute getURISyntax(Class<Attribute> type, Attribute def) {
    
    String uri = super.get(type.getName(),(String)null);
    if (uri==null)
      return def;
    
    try {
      return type.getConstructor(new Class[]{URI.class}).newInstance(new Object[]{new URI(uri)});
    } catch (Throwable t) {
      return def;
    }
  }

  
  private String put(IntegerSyntax syntax) {
    String key = syntax.getClass().getName();
    super.put(key, syntax.getValue());
    return key;
  }

  
  private Attribute getIntegerSyntax(Class<Attribute> type, Attribute def) {
    
    int i = super.get(type.getName(),(int)-1);
    if (i<0)
      return def;
    
    try {
      return type.getConstructor(new Class[]{Integer.TYPE}).newInstance(new Object[]{new Integer(i)});
    } catch (Throwable t) {
      return def;
    }
  }

  
  private String put(EnumSyntax syntax) {
    String key = syntax.getClass().getName();
    super.put(key, syntax.getValue());
    return key;
  }
  
  
  private Attribute getEnumSyntax(Class<Attribute> type, Attribute def) {
    
    int i = super.get(type.getName(),(int)-1);
    if (i<0)
      return def;
    
    try {
      Field[] fields = type.getFields();
      for (int f = 0; f < fields.length; f++) {
        Field field = fields[f];
        if (Modifier.isPublic(field.getModifiers())) {
          if (Modifier.isStatic(field.getModifiers())) {
            if (field.getType()==type) {
              EnumSyntax e = (EnumSyntax)field.get(null);
              if (e.getValue()==i) {
                return (Attribute)e;
              }
            }
          }
        }
      }
    } catch (Throwable t) {
    }
    return def;
  }

  
  private String put(MediaPrintableArea area) {
    String key = area.getClass().getName();
    super.put(key+".x", area.getX(MediaPrintableArea.INCH));
    super.put(key+".y", area.getY(MediaPrintableArea.INCH));
    super.put(key+".w", area.getWidth(MediaPrintableArea.INCH));
    super.put(key+".h", area.getHeight(MediaPrintableArea.INCH));
    return key;
  }

  
  private Attribute getMediaPrintableArea(Attribute def) {
    String prefix = MediaPrintableArea.class.getName()+".";
    float
     x = super.get(prefix+'x', -1F),
     y = super.get(prefix+'y', -1F),
     w = super.get(prefix+'w', -1F),
     h = super.get(prefix+'h', -1F);
    if (x<0||y<0||w<0||h<0)
      return def;
    try {
      return new MediaPrintableArea(x,y,w,h,MediaPrintableArea.INCH);
    } catch (IllegalArgumentException e) {
      return def;
    }
  }
  
} 
