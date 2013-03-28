

package edu.rice.cs.util.classloader;

import java.util.Arrays;
import java.net.URL;
import java.io.IOException;

import edu.rice.cs.util.FileOps;


public class StickyClassLoader extends ClassLoader {
  private final ClassLoader _newLoader;
  private final String[] _classesToLoadWithOld;

  
  public StickyClassLoader(final ClassLoader newLoader,
                           final ClassLoader oldLoader)
  {
    this(newLoader, oldLoader, new String[0]);
  }

  
  public StickyClassLoader(final ClassLoader newLoader,
                           final ClassLoader oldLoader,
                           final String[] classesToLoadWithOld)
  {
    super(oldLoader);
    _newLoader = newLoader; 
    _classesToLoadWithOld = new String[classesToLoadWithOld.length];
    System.arraycopy(classesToLoadWithOld, 0, _classesToLoadWithOld, 0,
                     classesToLoadWithOld.length);
    Arrays.sort(_classesToLoadWithOld);
  }

  
  public URL getResource(String name) {
    URL resource = _newLoader.getResource(name);
    if (resource == null) {
      resource = getParent().getResource(name);
    }

    

    return resource;
  }

  
  protected Class<?> loadClass(String name, boolean resolve) 
    throws ClassNotFoundException
  {
    
    Class<?> clazz;
    clazz = findLoadedClass(name);
    if (clazz != null) {
      return clazz;
    }
    
    if (name.startsWith("java.") ||
        name.startsWith("javax.") ||
        name.startsWith("sun.") ||
        name.startsWith("com.sun.") ||
        name.startsWith("org.omg.") ||
        name.startsWith("sunw.") ||
        name.startsWith("org.w3c.dom.") ||
        name.startsWith("org.xml.sax.") ||
        name.startsWith("net.jini.")) 
    {
      try {
        clazz = getSystemClassLoader().loadClass(name);
      }
      catch (ClassNotFoundException e) {
        
        
        clazz = _loadWithSecondary(name);
      }
    }
    else if (Arrays.binarySearch(_classesToLoadWithOld, name) >= 0) {
      
      clazz = getParent().loadClass(name);
    }
    else {
      
      clazz = _loadWithSecondary(name);
      
      
      
    }

    if (resolve) {
      resolveClass(clazz);
    }

    
    return clazz;
  }
  
  
  protected Class _loadWithSecondary(String name) throws ClassNotFoundException {
    
    
    
    
    
    try {
      String fileName = name.replace('.', '/') + ".class";
      
      URL resource = getResource(fileName); 
      if (resource == null) {
        throw new ClassNotFoundException("Resource not found: " + fileName);
      }
      
      byte[] data = FileOps.readStreamAsBytes(resource.openStream());
      try {
        return defineClass(name, data, 0, data.length);
      }
      catch (Error t) {
        
        
        throw t;
      }
    }
    catch (IOException ioe) {
      throw new ClassNotFoundException(ioe.toString());
    } 
  }
}
