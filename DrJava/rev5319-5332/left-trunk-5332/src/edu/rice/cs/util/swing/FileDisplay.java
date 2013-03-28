
package edu.rice.cs.util.swing;

import java.io.File;


public class FileDisplay {
  
  private File _file;
  private String _rep;
  private boolean _isNew;
  
  protected FileDisplayManager _fdm;
  
  FileDisplay(File f, FileDisplayManager fdm) { 
    this(fdm);
    _file = f;
    _rep = formatRep(f);
  }
  
  FileDisplay(File parent, String child, FileDisplayManager fdm) {
    this(fdm);
    if (child == null || child.equals("")) {
      _file = new File(parent, ".");
    }
    else {
      _file = new File(parent, child);
    }
    _rep = formatRep(_file);
  }
  
  private FileDisplay(FileDisplayManager fdm) {
    _fdm = fdm;
  }
  
  public static FileDisplay newFile(File parent, FileDisplayManager fdm) {
    FileDisplay fd = new FileDisplay(parent, "", fdm);
    fd._isNew = true;
    fd._rep = getDefaultNewFileRep();
    return fd;
  }
  
  public File getParentFile() { return _file.getParentFile(); }
  
  public File getFile() { return _file; }
  
  
  public boolean isEditable() { return (_isNew || (_file.canWrite() && _rep.equals(_file.getName()))); }
  
  public boolean isNew() { return _isNew; }
  
  public String getRepresentation() { return _rep; }
  
  public final String toString() { return _rep; }
  
  protected String formatRep(File file) {
    return _fdm.getName(file);
  }
    
  protected static String getDefaultNewFileRep() {
    return "New Folder";
  }
  
  
}