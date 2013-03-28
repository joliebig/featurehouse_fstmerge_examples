

package edu.rice.cs.drjava.config;

import java.io.*;


public class FileOption extends Option<File> {
  
  
  public static final File NULL_FILE = new File("") {
    public boolean canRead() { return false; }
    public boolean canWrite() { return false; }
    public int compareTo(File f) { return (f == this) ? 0 : -1; }
    public boolean createNewFile() { return false; }
    public boolean delete() { return false; }
    public void deleteOnExit() {}
    public boolean equals(Object o) { return o == this; }
    public boolean exists() { return true; }
    public File getAbsoluteFile() { return this; }
    public String getAbsolutePath() { return ""; }
    public File getCanonicalFile() { return this; }
    public String getCanonicalPath() { return ""; }
    public String getName() { return ""; }
    public String getParent() { return null; }
    public File getParentFile() { return null; }
    public String getPath() { return ""; }
    public int hashCode() { return getClass().hashCode(); }
    public boolean isAbsolute() { return false; }
    public boolean isDirectory() { return false; }
    public boolean isFile() { return false; }
    public boolean isHidden() { return false; }
    public long lastModified() { return 0L; }
    public long length() { return 0L; }
    public String[] list() { return null; }
    public String[] list(FilenameFilter filter) { return null; }
    public File[] listFiles() { return null; }
    public File[] listFiles(FileFilter filter) { return null; }
    public File[] listFiles(FilenameFilter filter) { return null; }
    public boolean mkdir() { return false; }
    public boolean mkdirs() { return false; }
    public boolean renameTo(File dest) { return false; }
    public boolean setLastModified(long time) { return false; }
    public boolean setReadOnly() { return false; }
    public String toString() { return ""; }
    
    
  };
  
  
  public FileOption(String key, File def) { super(key,def); }
  
  
  public File parse(String s) { 
    if (s.trim().equals("")) return NULL_FILE;
    
    try { return new File(s).getAbsoluteFile(); }
    catch (NullPointerException e) { throw new OptionParseException(name, s, "Must have a legal filename."); }
  }

  
  public String format(File f) { return f.getAbsolutePath(); }
}