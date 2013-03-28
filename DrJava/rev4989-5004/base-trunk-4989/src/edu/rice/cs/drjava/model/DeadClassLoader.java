

package edu.rice.cs.drjava.model;

import java.lang.ClassLoader;
import java.lang.ClassNotFoundException;
import java.net.URL;

public class DeadClassLoader extends ClassLoader {
  
  public URL getResource(String name) { return null; }
  
  protected URL findResource(String name) { return null; }
  
  public Class<?> loadClass(String name) throws ClassNotFoundException {
    throw new ClassNotFoundException(name);
  }
  
  protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    throw new ClassNotFoundException(name);
  }
  
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    throw new ClassNotFoundException(name);
  }
}




