

package edu.rice.cs.util.classloader;

import java.net.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import edu.rice.cs.util.FileOps;
import edu.rice.cs.util.swing.Utilities;


public class ToolsJarClassLoader extends URLClassLoader {
  
  
  
  
  public ToolsJarClassLoader(File toolsJar) { super(getToolsJarURLs(toolsJar)); }
  public ToolsJarClassLoader() { this(FileOps.NONEXISTENT_FILE); }

  
  public static File[] getToolsJarFiles(File toolsJar) {
    String javaHome = System.getProperty("java.home");
    File home = new File(javaHome);
    ArrayList<File> files = new ArrayList<File>();
    
    
    if (toolsJar.exists()) files.add(toolsJar);

    
    File libDir = new File(home, "lib");
    File jar1 = new File(libDir, "tools.jar");
    if (jar1.exists()) files.add(jar1);

    
    File libDir2 = new File(home.getParentFile(), "lib");
    File jar2 = new File(libDir2, "tools.jar");
    if (jar2.exists()) files.add(jar2);

    if (javaHome.toLowerCase().indexOf("program files") != -1) {
      
      File jar3 = new File(getWindowsToolsJar(javaHome));
      if (jar3.exists()) files.add(jar3);
    }

    File[] fileArray = new File[files.size()];
    files.toArray(fileArray);
    return fileArray;
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

  
  public static String getWindowsToolsJar(String javaHome) {
    if (javaHome.indexOf("Program Files") == -1) return "";

    String prefix = "C:\\j2sdk";
    String suffix = "\\lib\\tools.jar";
    int versionIndex;

    if (javaHome.indexOf("JavaSoft") != -1) {
      prefix = "C:\\jdk";
      versionIndex = javaHome.indexOf("JRE\\") + 4;
    }
    else { versionIndex = javaHome.indexOf("j2re") + 4; }
    
    String version = javaHome.substring(versionIndex);

    return prefix + version + suffix;
  }

  
  public URL getResource(String name) { return findResource(name); }
}
