

package edu.rice.cs.drjava.model.compiler.descriptors;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Set;
import java.util.jar.JarFile;
import edu.rice.cs.plt.reflect.JavaVersion;


public interface JDKDescriptor {
  
  public String getName();
  
  
  public Set<String> getToolsPackages();

  
  public Iterable<File> getSearchDirectories();
  
  
  public Iterable<File> getSearchFiles();
  
  
  public boolean isCompound();
  
  
  public String getAdapterForCompiler();
  
  
  public String getAdapterForDebugger();
  
  
  public boolean containsCompiler(File f);

  
  public JavaVersion getMinimumMajorVersion();
  
  
  public Iterable<File> getAdditionalCompilerFiles(File compiler) throws FileNotFoundException;
  
  
  public static class Util {
    
    public static boolean exists(File jarOrDir, String... fileNames) {
      if (jarOrDir.isFile()) {
        try {
          JarFile jf = new JarFile(jarOrDir);
          for(String fn: fileNames) {
            if (jf.getJarEntry(fn)==null) return false;
          }
          return true;
        }
        catch(IOException ioe) { return false; }
      }
      else if (jarOrDir.isDirectory()) {
        for(String fn: fileNames) {
          if (!(new File(jarOrDir,fn).exists())) return false;
        }
        return true;
      }
      return false;
    }
  }
}
