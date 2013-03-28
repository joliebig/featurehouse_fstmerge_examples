

package edu.rice.cs.drjava.ui.config;

import javax.swing.*;
import edu.rice.cs.drjava.ui.*;
import edu.rice.cs.drjava.config.*;
import edu.rice.cs.drjava.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.filechooser.FileFilter;

import java.util.Vector;


public class VectorFileOptionComponent extends VectorOptionComponent<File> implements OptionConstants {
  private FileFilter _fileFilter;
  private JFileChooser _jfc;

  
  public VectorFileOptionComponent (VectorOption<File> opt, String text, Frame parent) {
    super(opt, text, parent);  

    
    File workDir = DrJava.getConfig().getSetting(WORKING_DIRECTORY);
    if (workDir == FileOption.NULL_FILE)  workDir = new File(System.getProperty("user.dir"));
    if (workDir.isFile() && workDir.getParent() != null) workDir = workDir.getParentFile();

    _jfc = new JFileChooser(workDir);
    _jfc.setDialogTitle("Select");
    _jfc.setApproveButtonText("Select");
    _jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    _jfc.setMultiSelectionEnabled(true);
    _fileFilter = ClassPathFilter.ONLY;
  }
  
  
  public VectorFileOptionComponent (VectorOption<File> opt, String text, Frame parent, String description) {
    this(opt, text, parent);
    setDescription(description);
  }
  
  
  protected void _addButtons() {
    super._addButtons();
    _buttonPanel.add(_moveUpButton);
    _buttonPanel.add(_moveDownButton);
  }

  
  public void setValue(Vector<File> value) {
    File[] array = new File[value.size()];
    value.copyInto(array);
    _listModel.clear();
    for (int i = 0; i < array.length; i++) { _listModel.addElement(array[i]); }
  }

  
  public void setFileFilter(FileFilter fileFilter) {
    _fileFilter = fileFilter;
  }
  
  
  public void chooseFile() {
    File selection = (File) _list.getSelectedValue();
    if (selection != null) {
      File parent = selection.getParentFile();
      if (parent != null) {
        _jfc.setCurrentDirectory(parent);
      }
    }

    _jfc.setFileFilter(_fileFilter);

    File[] c = null;
    int returnValue = _jfc.showDialog(_parent, null);
    if (returnValue == JFileChooser.APPROVE_OPTION) {
      c = _jfc.getSelectedFiles();
    }
    if (c != null) {
      for(int i = 0; i < c.length; i++) {
        _listModel.addElement(c[i]);
      }
    }
  }
  
  protected Action _getAddAction() {
    return new AbstractAction("Add") {
      public void actionPerformed(ActionEvent ae) {
        chooseFile();
        _list.setSelectedIndex(_listModel.getSize() - 1);
        notifyChangeListeners();
      }
    };
  }
}
