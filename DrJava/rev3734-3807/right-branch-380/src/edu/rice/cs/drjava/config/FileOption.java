

package edu.rice.cs.drjava.config;

import java.io.*;


public class FileOption extends Option<File> {
  
  
  public static final File NULL_FILE = new File("") {
    public String getAbsolutePath() { return ""; }
    public String getName() { return ""; }
    public String toString() { return ""; }
    public boolean exists() { return true; }
  };
  
  
  public FileOption(String key, File def) { super(key,def); }
  
  
  public File parse(String s) { 
    if (s.trim().equals("")) return NULL_FILE;
    
    try { return new File(s).getAbsoluteFile(); }
    catch (NullPointerException e) { throw new OptionParseException(name, s, "Must have a legal filename."); }
  }

  
  public String format(File f) { return f.getAbsolutePath(); }
}