

package edu.rice.cs.util.classloader;

import java.net.*;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

import edu.rice.cs.util.FileOps;
import edu.rice.cs.util.swing.Utilities;


public class ToolsJarClassLoader extends URLClassLoader {
  
  
  
  
  public ToolsJarClassLoader(File toolsJar) { super(getToolsJarURLs(toolsJar)); }
  public ToolsJarClassLoader() { this(FileOps.NONEXISTENT_FILE); }

  
  public static File[] getToolsJarFiles(File toolsJar) {
    File javaHome = FileOps.getCanonicalFile(new File(System.getProperty("java.home")));
    
    
    
    
    LinkedHashSet<File> javaHomeParents = new LinkedHashSet<File>();
    javaHomeParents.add(FileOps.getCanonicalFile(new File(javaHome, "..")));
    javaHomeParents.add(FileOps.getCanonicalFile(new File(javaHome, "../..")));
    javaHomeParents.add(FileOps.getCanonicalFile(new File("/C:/Program Files/Java/")));
    javaHomeParents.add(FileOps.getCanonicalFile(new File("/C:/Program Files/")));
    javaHomeParents.add(FileOps.getCanonicalFile(new File("/C:/Java/")));
    javaHomeParents.add(FileOps.getCanonicalFile(new File("/C:/")));
    javaHomeParents.add(FileOps.getCanonicalFile(new File("/usr/")));
    javaHomeParents.add(FileOps.getCanonicalFile(new File("/usr/java/")));
    javaHomeParents.add(FileOps.getCanonicalFile(new File("/usr/j2se/")));
    javaHomeParents.add(FileOps.getCanonicalFile(new File("/usr/local/")));
    javaHomeParents.add(FileOps.getCanonicalFile(new File("/usr/local/java/")));
    javaHomeParents.add(FileOps.getCanonicalFile(new File("/usr/local/j2se/")));
    
    LinkedHashSet<File> javaHomes = new LinkedHashSet<File>();
    javaHomes.add(javaHome);
    String version = System.getProperty("java.specification.version");
    final String prefix1 = "j2sdk" + version;
    final String prefix2 = "jdk" + version;
    for (File parent : javaHomeParents) {
      javaHomes.addAll(FileOps.getFilesInDir(parent, false, new FileFilter() {
        public boolean accept(File f) {
          String name = f.getName();
          return name.startsWith(prefix1) || name.startsWith(prefix2);
        }
      }));
    }
    
    LinkedHashSet<File> result = new LinkedHashSet<File>();
    if (toolsJar.exists()) result.add(FileOps.getCanonicalFile(toolsJar));
    for (File home : javaHomes) {
      File tools = new File(home, "lib/tools.jar");
      if (tools.exists()) { result.add(FileOps.getCanonicalFile(toolsJar)); }
    }

    return result.toArray(new File[0]);
  }
  
  
  public static URL[] getToolsJarURLs() { return getToolsJarURLs(FileOps.NONEXISTENT_FILE); }
  
  
  public static URL[] getToolsJarURLs(File toolsJar) {
    File[] files = getToolsJarFiles(toolsJar);
    try {
      URL[] urls = new URL[files.length];
      for (int i=0; i < files.length; i++) {
        urls[i] = files[i].toURL();
      }
      return urls;
    }
    catch (MalformedURLException e) {
      return new URL[0];
    }
  }
  
   
  public static String getToolsJarClassPath() { return getToolsJarClassPath(FileOps.NONEXISTENT_FILE); }

  
  public static String getToolsJarClassPath(File toolsJar) {
    File[] files = getToolsJarFiles(toolsJar);
    StringBuffer classPath = new StringBuffer();
    String pathSep = System.getProperty("path.separator");

    for (int i=0; i < files.length; i++) {
      if (i > 0) classPath.append(pathSep);
      classPath.append(files[i].getAbsolutePath());
    }
    return classPath.toString();
  }

  
  public URL getResource(String name) { return findResource(name); }
}
