

package edu.rice.cs.drjava.model.junit;

import junit.runner.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.util.zip.*;





public final class DrJavaTestCaseClassLoader extends TestCaseClassLoader {
  
  ClassLoader _loader;
  
  
  private Vector<String> fPathItems;
  
  
  private String[] defaultExclusions= {"junit.framework.", "junit.extensions.", "junit.runner.", "java."};
  
  
  static final String EXCLUDED_FILE= "excluded.properties";
  
  
  private Vector<String> fExcluded;
  
  
  public DrJavaTestCaseClassLoader() { this(System.getProperty("java.class.path")); }
  
  
  public DrJavaTestCaseClassLoader(String classPath) {
    _loader = getClass().getClassLoader();
    scanPath(classPath);
    readExcludedPackages();
  }
  
  
  private void scanPath(String classPath) {
    String separator= System.getProperty("path.separator");
    fPathItems= new Vector<String>(10);
    StringTokenizer st= new StringTokenizer(classPath, separator);
    String item;
    while (st.hasMoreTokens()) {
      item = st.nextToken();
      fPathItems.addElement(item);
      try{ _loader = new DrJavaURLClassLoader(new URL[]{new File(item).toURL()}, _loader); }
      catch(MalformedURLException e) { 
        
      }
    }
  }
  
  
  public URL getResource(String name) { return _loader.getResource(name); }
  
  
  public InputStream getResourceAsStream(String name) {
    return _loader.getResourceAsStream(name);
  } 
  
  
  public boolean isExcluded(String name) {
    for (int i= 0; i < fExcluded.size(); i++) {
      if (name.startsWith(fExcluded.elementAt(i))) return true;
    }
    return false; 
  }
  
  
  public synchronized Class<?> loadClass(String name, boolean resolve)
    throws ClassNotFoundException {
    Class<?> c= findLoadedClass(name);
    if (c != null)
      return c;
    
    
    
    
    if (isExcluded(name)) {
      try {
        c= findSystemClass(name);
        return c;
      } catch (ClassNotFoundException e) {
        
      }
    }
    
    try{
      if (c == null) {
        byte[] data= lookupClassData(name);
        if (data == null) {
          throw new ClassNotFoundException();
        }
        c= defineClass(name, data, 0, data.length);
      }
      if (resolve) 
        resolveClass(c);
    }catch(ClassNotFoundException e) {
      
      return findSystemClass(name);
    }
    return c;
  }
  
  
  private byte[] lookupClassData(String className) throws ClassNotFoundException {
    byte[] data= null;
    for (int i= 0; i < fPathItems.size(); i++) {
      String path= fPathItems.elementAt(i);
      String fileName= className.replace('.', '/')+".class";
      if (isJar(path)) {
        data= loadJarData(path, fileName);
      } else {
        data= loadFileData(path, fileName);
      }
      if (data != null) {
        return data;
      }
    }
    throw new ClassNotFoundException(className);
  }
  
  
  private boolean isJar(String pathEntry) {
    return pathEntry.endsWith(".jar") || pathEntry.endsWith(".zip");
  }
  
  
  private byte[] loadFileData(String path, String fileName) {
    File file= new File(path, fileName);
    if (file.exists()) { 
      return getClassData(file);
    }
    return null;
  }
  
  
  private byte[] getClassData(File f) {
    try {
      FileInputStream stream= new FileInputStream(f);
      ByteArrayOutputStream out= new ByteArrayOutputStream(1000);
      byte[] b= new byte[1000];
      int n;
      while ((n= stream.read(b)) != -1) 
        out.write(b, 0, n);
      stream.close();
      out.close();
      return out.toByteArray();
      
    } catch (IOException e) {
    }
    return null;
  }
  
  
  private byte[] loadJarData(String path, String fileName) {
    ZipFile zipFile= null;
    InputStream stream= null;
    File archive= new File(path);
    if (!archive.exists())
      return null;
    try {
      zipFile= new ZipFile(archive);
    } catch(IOException io) {
      return null;
    }
    ZipEntry entry= zipFile.getEntry(fileName);
    if (entry == null)
      return null;
    int size= (int) entry.getSize();
    try {
      stream= zipFile.getInputStream(entry);
      byte[] data= new byte[size];
      int pos= 0;
      while (pos < size) {
        int n= stream.read(data, pos, data.length - pos);
        pos += n;
      }
      zipFile.close();
      return data;
    } catch (IOException e) {
    } finally {
      try {
        if (stream != null)
          stream.close();
      } catch (IOException e) {
      }
    }
    return null;
  }
  
  
  private void readExcludedPackages() {  
    fExcluded = new Vector<String>(10);
    for (String de: defaultExclusions) fExcluded.addElement(de);
    
    InputStream is = getClass().getResourceAsStream(EXCLUDED_FILE);
    if (is == null) return;
    Properties p = new Properties();
    try { p.load(is); }
    catch (IOException e) { return; } 
    finally {
      try { is.close(); } 
      catch (IOException e) {
        
      }
    }
    
    
    Enumeration pnames = p.propertyNames();
    
    while (pnames.hasMoreElements()) {
      String key = (String) pnames.nextElement();
      if (key.startsWith("excluded.")) {
        String path = p.getProperty(key);
        path = path.trim();
        if (path.endsWith("*")) path= path.substring(0, path.length()-1);
        if (path.length() > 0) fExcluded.addElement(path);    
      }
    }
  }
  
  
  private static class DrJavaURLClassLoader extends URLClassLoader{
    
    public DrJavaURLClassLoader(URL[] urls, ClassLoader c) { super(urls, c); }
    
    public URL getResource(String name) {
      URL ret = getParent().getResource(name);
      if (ret == null) ret = super.getResource(name);
      return ret;
    }
  }
}