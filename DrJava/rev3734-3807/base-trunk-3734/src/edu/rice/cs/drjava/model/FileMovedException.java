

package edu.rice.cs.drjava.model;

import java.io.*;


public class FileMovedException extends IOException {
  private File _file;
  
  
  public FileMovedException(File f, String s) {
    super(s);
    _file = f;
  }
  
  
  public File getFile() { return _file;}
}