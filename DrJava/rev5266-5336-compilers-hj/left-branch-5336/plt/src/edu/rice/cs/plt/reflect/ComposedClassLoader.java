

package edu.rice.cs.plt.reflect;

import java.net.URL;


public class ComposedClassLoader extends ClassLoader {
  
  private final ClassLoader _child;
  
  public ComposedClassLoader(ClassLoader parent, ClassLoader child) {
    super(parent);
    _child = child;
  }
  
  @Override protected Class<?> findClass(String name) throws ClassNotFoundException {
    return _child.loadClass(name);
  }
  
  @Override protected URL findResource(String name) {
    return _child.getResource(name);
  }
  
}
