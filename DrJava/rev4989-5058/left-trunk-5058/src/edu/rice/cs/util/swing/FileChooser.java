

package edu.rice.cs.util.swing;

import edu.rice.cs.plt.io.IOUtil;

import java.io.File;

import javax.swing.JFileChooser;

public class FileChooser extends JFileChooser {
  
  
  protected File _root;
  
  
  public FileChooser(File root) { 
    super(root);
    _init(root);
  }
  
  
  
  
  private void _init(final File root) {
    
    setRoot(root);
    
    setFileSelectionMode(FILES_ONLY);
    setDialogType(CUSTOM_DIALOG);
    setApproveButtonText("Select");
  }
  
  public void setRoot(File root) {
    _root = root; 
    if (root != null) {
      if (! root.exists()) _root = null;
      else if (! root.isDirectory()) _root = root.getParentFile();
    }
  }
  
  public File getRoot() { return _root; }
  
  public boolean isTraversable(File f) {
    if (_root == null) return super.isTraversable(f);

    return f != null && f.isDirectory() && IOUtil.isMember(f, _root);
  }
}
