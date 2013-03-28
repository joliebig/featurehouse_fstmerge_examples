

package edu.rice.cs.plt.reflect;

import java.io.InputStream;
import java.io.IOException;

import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.io.IOUtil;


public class PreemptingClassLoader extends AbstractClassLoader {
  
  private Iterable<String> _prefixes;
  
  
  public PreemptingClassLoader(ClassLoader parent, String... prefixes) {
    this(parent, IterUtil.asIterable(prefixes));
  }
  
  
  public PreemptingClassLoader(ClassLoader parent, Iterable<? extends String> prefixes) {
    super(parent);
    _prefixes = IterUtil.snapshot(prefixes);
  }
  
  
  protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    if (!shouldPreempt(name)) { return super.loadClass(name, resolve); }
    else {
      Class<?> result = findLoadedClass(name); 
      if (result == null) {
        String filename = name.replace('.', '/') + ".class";
        InputStream in = IOUtil.asBuffered(getResourceAsStream(filename));
        if (in == null) { throw new ClassNotFoundException("Resource not found: " + filename); }
        try {
          byte[] data = IOUtil.toByteArray(in);

          
          definePackageForClass(name);
          
          result = defineClass(name, data, 0, data.length);
        }
        catch (IOException e) {
          throw new ClassNotFoundException("Error in reading " + filename, e);
        }
        finally {
          try { in.close(); }
          catch (IOException e) {  }
        }
      }
      
      if (resolve) { resolveClass(result); }
      return result;
    }
  }
  
  private boolean shouldPreempt(String name) {
    
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
