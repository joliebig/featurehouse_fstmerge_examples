

package edu.rice.cs.plt.reflect;

import java.net.URL;
import edu.rice.cs.plt.iter.IterUtil;

import static edu.rice.cs.plt.debug.DebugUtil.debug;


public class ShadowingClassLoader extends ClassLoader {
  
  private final Iterable<? extends String> _prefixes;
  private final boolean _blackList;
  private final boolean _filterBootClasses;
  
  
  public static ShadowingClassLoader blackList(ClassLoader parent, String... excludePrefixes) {
    return new ShadowingClassLoader(parent, true, IterUtil.asIterable(excludePrefixes), false);
  }
    
  
  public static ShadowingClassLoader whiteList(ClassLoader parent, String... includePrefixes) {
    return new ShadowingClassLoader(parent, false, IterUtil.asIterable(includePrefixes), false);
  }
  
  
  public ShadowingClassLoader(ClassLoader parent, boolean blackList, Iterable<? extends String> prefixes,
                              boolean filterBootClasses) {
    super(parent);
    _blackList = blackList;
    _prefixes = prefixes;
    _filterBootClasses = filterBootClasses;
  }
  
  
  @Override protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    if ((_filterBootClasses || !isBootClass(name)) && matchesPrefixes(name) == _blackList) {
      throw new ClassNotFoundException(name + " is being shadowed");
    }
    else {
      
      return super.loadClass(name, resolve);
    }
  }
  
  @Override public URL getResource(String name) {
    if ((_filterBootClasses || !isBootResource(name)) &&
        matchesPrefixes(name.replace('/', '.')) == _blackList) {
      return null;
    }
    else {
      
      return super.getResource(name);
    }
  }
  
  
  
  
  private boolean isBootClass(String name) {
    try { ReflectUtil.BOOT_CLASS_LOADER.loadClass(name); return true; }
    catch (ClassNotFoundException e) { return false; }
  }
  
  private boolean isBootResource(String name) {
    return ReflectUtil.BOOT_CLASS_LOADER.getResource(name) != null;
  }
  
  private boolean matchesPrefixes(String name) {
    
    for (String p : _prefixes) {
      if (name.startsWith(p)) {
        if (name.equals(p) || name.startsWith(p + ".") || name.startsWith(p + "$")) {
          return true;
        }
      }
    }
    return false;
  }
  
}
