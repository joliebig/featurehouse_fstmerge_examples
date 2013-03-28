
package genj.io;

import genj.gedcom.GedcomException;
import genj.gedcom.MultiLineProperty;
import genj.gedcom.Property;
import genj.gedcom.PropertyXRef;
import genj.util.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;


public class PropertyReader {

  protected final static Resources RESOURCES = Resources.get("genj.io");

  protected boolean useIndents = false;
  protected int lines = 0;
  protected String line = null;
  protected Collection collectXRefs;
  protected boolean isMerge = false;
  
  
  protected int level;
  protected String tag;
  protected String xref;
  protected String value;
  
  
  protected BufferedReader in;
  
  
  public PropertyReader(Reader in, Collection collectXRefs, boolean useIndents) {
    this(new BufferedReader(in), collectXRefs, useIndents);
  }
  
  
  public PropertyReader(BufferedReader in, Collection collectXRefs, boolean useIndents) {
    this.in = in;
    this.useIndents = useIndents;
    this.collectXRefs = collectXRefs;
  }
  
  
  public int getLines() {
    return lines;
  }
  
  
  public void read(Property prop) throws IOException {
    read(prop, -1);
  }
  
  
  public void read(Property prop, int index) throws IOException {
    
    readProperties(prop, 0, index);
    
    if (line!=null) {
      line = null;
      in.reset();
    }
    
  }
  
  
  public void setMerge(boolean set) {
    isMerge = set;
  }
  
  
  protected void readProperties(Property prop, int currentLevel, int pos) throws IOException {
    
    
    if (prop instanceof MultiLineProperty) {
      
      MultiLineProperty.Collector collector = ((MultiLineProperty)prop).getLineCollector();
      while (true) {
        
        if (!readLine(false))
          break;
        
        if (level<currentLevel+1 || !collector.append(level-currentLevel, tag, value))
          break;
        
        line = null;
        
      } 
      
      prop.setValue(collector.getValue());
    }
  
    
    while (true) {
      
      
      if (!readLine(false))
        return;
      
      
      if (level<currentLevel+1) 
        return;
      
      
      line = null;
      
      
      
      
      
      
      if (level>currentLevel+1) {
        trackBadLevel(level, prop);
        while (level-1>currentLevel++) 
          prop = prop.addProperty("_TAG", "");
      }
    
      
      int lineNoForChild = lines;

      
      Property child = addProperty(prop, tag, value, pos);
      
      
      readProperties(child, currentLevel+1, 0);
        
      
      
      if (child instanceof PropertyXRef)
        link((PropertyXRef)child, lineNoForChild);
        
      
      if (pos>=0) pos++;
    }
    
    
  }

  
  protected Property addProperty(Property prop, String tag, String value, int pos) {
    if (isMerge) {
      
      Property child = prop.getProperty(tag, false);
      if (child!=null&&prop.getMetaProperty().getNested(tag, false).isSingleton()&&!(child instanceof PropertyXRef)) {
        child.setValue(value);
        return child;
      }
    }
    
    try {
      return prop.addProperty(tag, value, pos);
    } catch (GedcomException e) {
      Property fallback = prop.addSimpleProperty(tag, value, pos);
      trackBadProperty(fallback, e.getMessage());
      return fallback;
    }
    
  }
  
  
  protected boolean readLine(boolean consume) throws IOException {
    
    
    if (line==null) {
      
      
      in.mark(256);
      
      
      while (line==null) {
        line = in.readLine();
        if (line==null) 
          return false;
        lines ++;
        if (line.trim().length()==0) {
          trackEmptyLine();
          line = null;
        }
      }
      
      
      StringTokenizer tokens = new StringTokenizer(line," \t");
  
      try {
        
        
        try {
          if (useIndents) {
            level = 0;
            while (line.charAt(level)==' ') level++;
            level++;
          } else {
            level = Integer.parseInt(tokens.nextToken(),10);
          }
        } catch (StringIndexOutOfBoundsException sioobe) {
          throw new GedcomFormatException(RESOURCES.getString("read.error.emptyline"), lines);
        } catch (NumberFormatException nfe) {
          throw new GedcomFormatException(RESOURCES.getString("read.error.nonumber"), lines);
        }
  
        
        if (tokens.hasMoreTokens()) 
          tag = tokens.nextToken();
        else {
          tag = "_TAG";
        }
          
        
        if (level==0&&tag.startsWith("@")) {
  
          
          if (!tag.endsWith("@")||tag.length()<=2)
            throw new GedcomFormatException(RESOURCES.getString("read.error.invalidid"), lines);
   
          
          xref = tag.substring(1,tag.length()-1);
          
          
          tag = tokens.nextToken();
  
        } else {
  
          
          xref = "";
        }
  
        
        if (tokens.hasMoreElements()) {
          
          
          value = tokens.nextToken("\n");
          
          
          if (value.startsWith(" "))
            value = value.substring(1);
        } else {
          value = "";
        }
  
      } catch (NoSuchElementException ex) {
        
        throw new GedcomFormatException(RESOURCES.getString("read.error.cantparse"), lines);
      }
      
      
      
      
      tag = tag.intern();
          
    }
    
    
    if (consume)
      line = null;
      
    
    return true;
  }
  
  
  protected void link(PropertyXRef xref, int line) {
    if (collectXRefs!=null)
      collectXRefs.add(xref);
    else try {
      xref.link();
    } catch (Throwable t) {
      
    }
  }
  
  
  protected void trackEmptyLine() {
  }
  
  
  protected void trackBadLevel(int level, Property parent) {
  }
  
  
  protected void trackBadProperty(Property property, String message) {
  }
  
} 
