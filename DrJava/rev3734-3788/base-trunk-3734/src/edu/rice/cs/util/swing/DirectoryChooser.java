

package edu.rice.cs.util.swing;

import edu.rice.cs.util.FileOps;
import edu.rice.cs.util.swing.Utilities;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;

import java.awt.Component;

public class DirectoryChooser extends JFileChooser {
  
  
  protected Component _owner;
  
  
  protected File _root;
  
  
  public DirectoryChooser() { this(null, null, false, false); }
  
  
  public DirectoryChooser(Component owner) { this(owner, null, false, false); }
  
  
  public DirectoryChooser(Component owner, boolean allowMultiple) { this(owner, null, allowMultiple, false); }
  
  
  public DirectoryChooser(Component owner, File root) { this(owner, root, false, false); }
  
  
  public DirectoryChooser(Component owner, File root, boolean allowMultiple, boolean showHidden) {
    
    super(root);
    _init(owner, root, allowMultiple, showHidden);
  }
  
  
    
  
  private void _init(Component owner, final File root, boolean allowMultiple, boolean showHidden) {
    
    






    
    _owner = owner;
    _root = root; 
    if (root != null) {
      if (! root.exists()) _root = null;
      else if (! root.isDirectory()) _root = root.getParentFile();
    }

    setMultiSelectionEnabled(allowMultiple);
    setFileHidingEnabled(! showHidden);
    setFileSelectionMode(DIRECTORIES_ONLY);
    setDialogType(CUSTOM_DIALOG);
    setApproveButtonText("Select");
    setFileFilter(new FileFilter() {
      public boolean accept(File f) { return true; }
      public String getDescription() { return "All Folders"; }
    });
  }
  
  public int showDialog(File initialSelection) {
    setCurrentDirectory(initialSelection);
    return showDialog(_owner, null);  
  }
  
  
  public int showDialog() { return showDialog(_owner, null); }
  
  
  public File[] getSelectedDirectories() { return getSelectedFiles(); }
  
  
  public File getSelectedDirectory() { return getSelectedFile(); }
  





}
