

package edu.rice.cs.util.classloader;

import java.util.*;


public class LimitingClassLoader extends ClassLoader {
  private List<String> _restrictedList = new LinkedList<String>();

  
  public LimitingClassLoader(ClassLoader parent) { super(parent); }

  public void addToRestrictedList(String name) { _restrictedList.add(name); }

  public void clearRestrictedList() { _restrictedList.clear(); }

  
  protected Class<?> loadClass(String name, boolean resolve)
    throws ClassNotFoundException
  {
    ListIterator itor = _restrictedList.listIterator();

    while (itor.hasNext()) {
      String current = (String) itor.next();
      if (current.equals(name)) {
        throw new ClassNotFoundException("Class " + name +
                                         " is on the restricted list.");
      }
    }

    
    Class<?> clazz = getParent().loadClass(name);

    
    
    if (resolve) {
      resolveClass(clazz);
    }

    return clazz;
  }
}
