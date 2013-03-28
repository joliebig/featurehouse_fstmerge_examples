

package edu.rice.cs.plt.reflect;

import java.net.URL;
import java.io.InputStream;


public class EmptyClassLoader extends ClassLoader {
  
  public static final EmptyClassLoader INSTANCE = new EmptyClassLoader();
  
  private EmptyClassLoader() { super(null); }
  
  @Override public Class<?> loadClass(String name) throws ClassNotFoundException {
    throw new ClassNotFoundException();
  }
  
  @Override protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    throw new ClassNotFoundException();
  }
  
  @Override public URL getResource(String name) { return null; }
  





  
  @Override public InputStream getResourceAsStream(String name) { return null; }
  
  @Override protected String findLibrary(String libName) { return null; }
  
}
