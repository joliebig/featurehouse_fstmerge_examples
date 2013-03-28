

package edu.rice.cs.drjava.model.repl;

import java.net.URL;


public class WrapperClassLoader extends ClassLoader{
  ClassLoader cl;
  public WrapperClassLoader(ClassLoader c) {
    cl = c;
  }
  
  public URL getResource(String name) {
    if (name.startsWith("edu/rice/cs/")) {
      return null;
    }else{
      return cl.getResource(name);
    }
  }
}
