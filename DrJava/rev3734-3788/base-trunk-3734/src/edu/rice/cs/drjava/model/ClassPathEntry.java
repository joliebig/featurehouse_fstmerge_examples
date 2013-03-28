

package edu.rice.cs.drjava.model;

import java.lang.ClassLoader;
import java.net.URLClassLoader;
import java.net.URL;



public class ClassPathEntry{
  URL item;
  
  public ClassPathEntry(URL entry) {
    item = entry;
  }
  
  public URL getEntry() {
    return item;
  }
  
  public ClassLoader getClassLoader(ClassLoader c) {
    return new URLClassLoader(new URL[] { item }, c);
  }
  
  public ClassLoader getClassLoader() {
    return new URLClassLoader(new URL[] { item }, new DeadClassLoader());
  }
}




