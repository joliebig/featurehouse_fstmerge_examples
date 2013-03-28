

package edu.rice.cs.plt.reflect;

import java.io.InputStream;
import java.io.IOException;

import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.io.IOUtil;


public abstract class AbstractClassLoader extends ClassLoader {
    
  protected AbstractClassLoader() { super(); }
  
  
  protected AbstractClassLoader(ClassLoader parent) { super(parent); }
  
  
  protected Package definePackageForClass(String className) {
    int lastDotPos = className.lastIndexOf('.');
    if (lastDotPos<0) return null; 
    
    String packageName = className.substring(0,lastDotPos);
    Package pack = getPackage(packageName);
    if (pack==null) {
      
      pack = definePackage(packageName, null, null, null, null, null, null, null);
    }
    return pack;
  }
}
