

package edu.rice.cs.util.swing;

import edu.rice.cs.util.FileOps;
import edu.rice.cs.util.swing.Utilities;

import java.io.File;

import javax.swing.JFileChooser;

import javax.swing.filechooser.FileView;

import java.awt.Component;

public class FileChooser extends JFileChooser {
  
  
  protected File _root;
  
  
  public FileChooser(File root) { 
    super(root);
    _init(root);
  }

  
    
  
  private void _init(final File root) {
    
    _root = root; 
    if (root != null) {
      if (! root.exists()) _root = null;
      else if (! root.isDirectory()) _root = root.getParentFile();
    }

    setFileSelectionMode(FILES_ONLY);
    setDialogType(CUSTOM_DIALOG);
    setApproveButtonText("Select");
  }
 
  public boolean isTraversable(File f) {
    if (_root == null) return super.isTraversable(f);

    return f != null && f.isDirectory() && FileOps.isInFileTree(f, _root);
  }
}
