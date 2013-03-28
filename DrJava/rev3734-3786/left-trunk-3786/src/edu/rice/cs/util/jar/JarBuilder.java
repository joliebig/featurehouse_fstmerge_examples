

package edu.rice.cs.util.jar;

import java.io.*;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class JarBuilder {
  private JarOutputStream _output;

  
  public JarBuilder(File file) throws IOException {
    _output = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(file)), ManifestWriter.DEFAULT);
  }

  
  public JarBuilder(File jar, File manifest) throws IOException {
    _output = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(jar)), new Manifest(new FileInputStream(manifest)));
  }

  
  public JarBuilder(File jar, Manifest manifest) {
    try {
      _output = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(jar)), manifest);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  
  private String makeName(String parent, String name) {
    String sep = "/"; 
    if( parent.equals("") )
      return name;
    if (parent.endsWith(sep))
      return parent + name;
    return parent + sep + name;
  }

  
  public void addFile(File file, String parent, String fileName) throws IOException {
    byte data[] = new byte[2048];

    FileInputStream fi = new FileInputStream(file.getAbsolutePath());
    BufferedInputStream origin = new BufferedInputStream(fi, 2048);

    JarEntry entry = new JarEntry(makeName(parent, fileName));
    _output.putNextEntry(entry);

    int count = origin.read(data, 0, 2048);
    while (count != -1) {
      _output.write(data, 0, count);
      count = origin.read(data, 0, 2048);
    }

    origin.close();
  }

  
  public void addDirectoryRecursive(File dir, String parent) {
    addDirectoryRecursiveHelper(dir, parent, new byte[2048], new FileFilter() {
      public boolean accept(File pathname) { return true; }
    });
  }

  
  public void addDirectoryRecursive(File dir, String parent, FileFilter filter) {
    addDirectoryRecursiveHelper(dir, parent, new byte[2048], filter);
  }

  
  private boolean addDirectoryRecursiveHelper(File dir, String parent, byte[] buffer, FileFilter filter) {
    try {
      File[] files = dir.listFiles(filter);
      BufferedInputStream origin = null;

      if( files == null ) 
        return true;
      for (int i = 0; i < files.length; i++) {
        if( files[i].isFile() ) {
          origin = new BufferedInputStream(new FileInputStream(files[i]), 2048);

          JarEntry entry = new JarEntry(makeName(parent, files[i].getName()));
          _output.putNextEntry(entry);

          int count;
          while((count = origin.read(buffer, 0, 2048)) != -1) {
            _output.write(buffer, 0, count);
          }
          origin.close();
        }
        else if( files[i].isDirectory() ) {
          addDirectoryRecursiveHelper(files[i], makeName(parent, files[i].getName()),buffer,filter);
        }
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
    return true;
  }

  
  public boolean makeDirectory(String parent, String dirName) {
    JarEntry entry = new JarEntry(makeName(parent, dirName));
    try {
      _output.putNextEntry(entry);
    }
    catch (IOException e) {
      return false;
    }
    return true;
  }

  
  public void close() throws IOException {
    _output.flush();
    _output.close();
  }
}