

package edu.rice.cs.drjava.ui.config;


import java.awt.event.*;
import java.io.File;
import javax.swing.filechooser.FileFilter;
import javax.swing.*;

import java.util.Vector;
import java.util.List;

import edu.rice.cs.drjava.ui.*;
import edu.rice.cs.drjava.config.*;
import edu.rice.cs.util.swing.SwingFrame;


public class VectorFileOptionComponent extends VectorOptionComponent<File> implements OptionConstants {
  private FileFilter _fileFilter;
  private JFileChooser _jfc;
  protected File _baseDir = null;
  
  public VectorFileOptionComponent(VectorOption<File> opt, String text, SwingFrame parent) {
    this(opt, text, parent, null);
  }
  
  
  public VectorFileOptionComponent(VectorOption<File> opt, String text, SwingFrame parent, String description) {
    this(opt, text, parent, description, false);
  }

  
  public VectorFileOptionComponent(VectorOption<File> opt, String text, SwingFrame parent,
                                   String description, boolean moveButtonEnabled) {
    super(opt, text, parent, new String[] { }, description, moveButtonEnabled);  
    
    
    File workDir = new File(System.getProperty("user.home"));

    _jfc = new JFileChooser(workDir);
    _jfc.setDialogTitle("Select");
    _jfc.setApproveButtonText("Select");
    _jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    _jfc.setMultiSelectionEnabled(true);
    _fileFilter = ClassPathFilter.ONLY;    
  }
  
  
  public void setFileFilter(FileFilter fileFilter) {
    _fileFilter = fileFilter;
  }
  
  
  public void setBaseDir(File f) {
    if (f.isDirectory()) { _baseDir = f; }
  }
  
  
  public void chooseFile() {
    int[] rows = _table.getSelectedRows();
    File selection = (rows.length==1)?_data.get(rows[0]):null;
    if (selection != null) {
      File parent = selection.getParentFile();
      if (parent != null) {
        _jfc.setCurrentDirectory(parent);
      }
    }
    else {
      if (_baseDir!=null) { _jfc.setCurrentDirectory(_baseDir); }
    }

    _jfc.setFileFilter(_fileFilter);

    File[] c = null;
    int returnValue = _jfc.showDialog(_parent, null);
    if (returnValue == JFileChooser.APPROVE_OPTION) {
      c = _jfc.getSelectedFiles();
    }
    if (c != null) {
      _table.getSelectionModel().clearSelection();
      for(int i = 0; i < c.length; i++) {
        _addValue(c[i]);
      }
    }
  }
  
  protected Action _getAddAction() {
    return new AbstractAction("Add") {
      public void actionPerformed(ActionEvent ae) {
        chooseFile();
      }
    };
  }
}
