
package genj.gedcom;

import genj.util.EnvironmentChecker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;


public class Grammar {

  private final static Logger LOG = Logger.getLogger("genj.gedcom"); 
  
  
  public final static Grammar 
    V55 = new Grammar("contrib/LDS/gedcom-5-5.xml"),
    V551 = new Grammar("contrib/LDS/gedcom-5-5-1.xml");
  
  
  private String version;
  
  
  private Map tag2root = new HashMap();
  
  
  private Grammar(String descriptor) {
    
    SAXParser parser;
    
    try {
      parser = SAXParserFactory.newInstance().newSAXParser();
    } catch (Throwable t) {
      Gedcom.LOG.log(Level.SEVERE, "couldn't setup SAX parser", t);
      throw new Error(t);
    }

    try {
      
      InputStream in = getClass().getResourceAsStream("/"+descriptor);
      if (in!=null) {
          LOG.info("Loading grammar through classloader");
      } else {
    	try {
    		in = new FileInputStream(new File(EnvironmentChecker.getProperty(this, "user.dir", ".", "current directory for grammar"),descriptor));
    	} catch(FileNotFoundException e) {
    		
    		in = new FileInputStream(new File("../app/"+descriptor));
    	}
      }

      
      parser.parse(new InputSource(new InputStreamReader(in)), new Parser());
    } catch (Throwable t) {
      Gedcom.LOG.log(Level.SEVERE, "couldn't parse grammar", t);
      throw new Error(t);
    }

    
  }
  
  
  public String getVersion() {
    return version;
  }
  
  
  public TagPath[] getAllPaths(String etag, Class property) {
    return getPathsRecursively(etag, property);
  }
  
  private TagPath[] getPathsRecursively(String etag, Class property) {
    
    
    List result = new ArrayList();
    
    for (Iterator it=tag2root.values().iterator(); it.hasNext(); ) {
      MetaProperty root = (MetaProperty)it.next();
      String tag = root.getTag();
      if (etag==null||tag.equals(etag))
        getPathsRecursively(root, property, new TagPath(tag), result);
    }
    
    return TagPath.toArray(result);
  }

  private void getPathsRecursively(MetaProperty meta, Class property, TagPath path, Collection result) {
  
    
    if (!meta.isInstantiated) 
      return;
    
    
    if (property.isAssignableFrom(meta.getType())) 
      result.add(path);
      
    
    for (Iterator it=meta.nested.iterator();it.hasNext();) {
      MetaProperty nested = (MetaProperty)it.next();
      getPathsRecursively(nested, property, new TagPath(path, nested.getTag()), result);
    }
    
    
  }

  
  public MetaProperty getMeta(TagPath path) {
    return getMeta(path, true);
  }

  
  public MetaProperty getMeta(TagPath path, boolean persist) {
    return getMetaRecursively(path, persist);
  }
  
  
   MetaProperty getMetaRecursively(TagPath path, boolean persist) {
    
    String tag = path.get(0);
    
    MetaProperty root = (MetaProperty)tag2root.get(tag);
    
    
    if (root==null) {
      root = new MetaProperty(this, tag, new HashMap(), false);
      tag2root.put(tag, root);
    }
    
    
    return root.getNestedRecursively(path, 1, persist);
  }

  
  private class Parser extends DefaultHandler {
    
    private Stack stack = null;
    
    
    public void startElement(java.lang.String uri, java.lang.String localName, java.lang.String qName, Attributes attributes) throws org.xml.sax.SAXException {
      
      
      if (stack==null) {
        if (!"GEDCOM".equals(qName)) 
          throw new RuntimeException("expected GEDCOM");
        version = attributes.getValue("version");
        if (version==null)
          throw new RuntimeException("expected GEDCOM version");
        stack = new Stack();
        return;
      }
      
      
      Map properties = new HashMap();
      for (int i=0,j=attributes.getLength();i<j;i++)
        properties.put(attributes.getQName(i), attributes.getValue(i));
      
      
      MetaProperty meta;
      try {
        meta = new MetaProperty(Grammar.this, qName, properties, true);
      } catch (Throwable t) {
        LOG.log(Level.SEVERE, "Problem instantiating meta property for "+qName+" with "+properties, t.getCause()!=null?t.getCause():t);
        throw new Error("Can't parse Gedcom Grammar");
      }

      
      if (stack.isEmpty())  {
        meta.isInstantiated = true;
        tag2root.put(qName, meta);
      } else
        ((MetaProperty)stack.peek()).addNested(meta);
        
      
      stack.push(meta);
      
      
    }

    
    public void endElement(java.lang.String uri, java.lang.String localName, java.lang.String qName) throws org.xml.sax.SAXException {
      
      if ("GEDCOM".equals(qName))
        stack = null;
      else
        stack.pop();
    }
  } 
  
} 
