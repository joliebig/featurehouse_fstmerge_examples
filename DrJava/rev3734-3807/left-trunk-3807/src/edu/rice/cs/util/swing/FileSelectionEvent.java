

package edu.rice.cs.util.swing;

import java.util.EventObject;
import java.io.File;

public class FileSelectionEvent extends EventObject {
  
  protected File[] _changed;
  protected boolean[] _areNew;
  protected File _newLead;
  protected File _oldLead;
  
  public FileSelectionEvent(Object source, File changed, boolean isNew, File newLead, File oldLead) {
    this(source, new File[]{changed}, new boolean[]{isNew}, newLead, oldLead);
  }
  
  public FileSelectionEvent(Object source, File[] changed, boolean[] areNew, File newLead, File oldLead) {
    super(source);
    _changed = changed;
    _areNew = areNew;
    _newLead = newLead;
    _oldLead = oldLead;
  }
  
  public File getOldLeadSelectionFile() { return _oldLead; }
  
  public File getNewLeadSelectionFile() { return _newLead; }
  
  public File getFile() { return _changed[0]; }
  
  public File[] getFiles() { return _changed; }
  
  public boolean isAddedFile() { return _areNew[0]; }
  
  public boolean isAddedFile(int i) { return _areNew[i]; }
  
  public boolean isAddedFile(File f) {
    for (int i = 0; i < _changed.length; i++) {
      if (f.equals(_changed[i])) return _areNew[i];
    }
    throw new IllegalArgumentException("File, " + f + ", not found in changed files");
  }  
}