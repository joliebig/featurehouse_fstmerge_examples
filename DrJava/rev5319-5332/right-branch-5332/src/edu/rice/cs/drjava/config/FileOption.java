

package edu.rice.cs.drjava.config;

import java.io.*;
import edu.rice.cs.util.FileOps;


public class FileOption extends Option<File> {
  
  
  public FileOption(String key, File def) { super(key,def); }
  
  
  public File parse(String s) { 
    if (s.trim().equals("")) return FileOps.NULL_FILE;
    
    try { return new File(s).getAbsoluteFile(); }
    catch (NullPointerException e) { throw new OptionParseException(name, s, "Must have a legal filename."); }
  }
  
  
  public String format(File f) { return f.getAbsolutePath(); }
}