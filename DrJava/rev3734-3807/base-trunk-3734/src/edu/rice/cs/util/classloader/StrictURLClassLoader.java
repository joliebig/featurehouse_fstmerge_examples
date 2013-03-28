

package edu.rice.cs.util.classloader;

import java.net.URL;
import java.net.URLClassLoader;


public class StrictURLClassLoader extends URLClassLoader {

  
  public StrictURLClassLoader(URL[] urls) {
    
    super(urls, null);
  }
  
  
  public URL getResource(String name) {
    return findResource(name);
  }
}
