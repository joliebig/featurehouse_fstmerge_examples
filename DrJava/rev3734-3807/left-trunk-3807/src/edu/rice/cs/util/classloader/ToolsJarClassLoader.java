

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
    
    String winPrograms = System.getenv("ProgramFiles");
    if (winPrograms != null) {
      javaHomeParents.add(FileOps.getCanonicalFile(new File(winPrograms, "Java")));
      javaHomeParents.add(FileOps.getCanonicalFile(new File(winPrograms)));
    }
    else {  
      javaHomeParents.add(FileOps.getCanonicalFile(new File("/C:/Program Files/Java/")));
      javaHomeParents.add(FileOps.getCanonicalFile(new File("/C:/Program Files/")));
    }

    String winSystem = System.getenv("SystemDrive");
    if (winSystem != null) {
      javaHomeParents.add(FileOps.getCanonicalFile(new File(winSystem, "Java")));
      javaHomeParents.add(FileOps.getCanonicalFile(new File(winSystem)));
    }
    else { 
      javaHomeParents.add(FileOps.getCanonicalFile(new File("/C:/Java/")));
      javaHomeParents.add(FileOps.getCanonicalFile(new File("/C:/")));
    }
    
    javaHomeParents.add(FileOps.getCanonicalFile(new File("/usr/")));
    javaHomeParents.add(FileOps.getCanonicalFile(new File("/usr/java/")));
    javaHomeParents.add(FileOps.getCanonicalFile(new File("/usr/j2se/")));
    javaHomeParents.add(FileOps.getCanonicalFile(new File("/usr/local/")));
    javaHomeParents.add(FileOps.getCanonicalFile(new File("/usr/local/java/")));
    javaHomeParents.add(FileOps.getCanonicalFile(new File("/usr/local/j2se/")));
    
    
    
    LinkedHashSet<File> javaHomes = new LinkedHashSet<File>();
    
    try {
      if (javaHome.isDirectory()) { javaHomes.add(javaHome); }
    }
    catch (SecurityException e) {  }
    
    String version = System.getProperty("java.specification.version");
    final String prefix1 = "j2sdk" + version;
    final String prefix2 = "jdk" + version;
    FileFilter matchHomes = new FileFilter() {
      public boolean accept(File f) {
        return f.isDirectory() && (f.getName().startsWith(prefix1) || f.getName().startsWith(prefix2));
      }
    };
    for (File parent : javaHomeParents) {
      try {
        File[] files = parent.listFiles(matchHomes);
        if (files != null) {
          for (File f : files) { javaHomes.add(f); }
        }
      }
      catch (SecurityException e) {  }
    }
    
    
    LinkedHashSet<File> result = new LinkedHashSet<File>();
    
    try {
      if (toolsJar.isFile()) result.add(FileOps.getCanonicalFile(toolsJar));
    }
    catch (SecurityException e) {  }
    
    for (File home : javaHomes) {
      try {
        File tools = new File(home, "lib/tools.jar");
        if (tools.isFile()) { result.add(FileOps.getCanonicalFile(tools)); }
      }
      catch (SecurityException e) {  }
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
