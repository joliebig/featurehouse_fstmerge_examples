

package edu.rice.cs.util.swing;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.io.*;

import edu.rice.cs.util.FileOps;


public class DirectorySelectorComponent extends JPanel {
  
  
  public static final int DEFAULT_NUM_COLS = 30;
  
  
  public static final float DEFAULT_FONT_SIZE = 10f;
  
  
  protected final Component _parent;
  
  
  protected final JTextField _fileField;
  
  
  protected final JButton _chooserButton;
  
  
  protected final DirectoryChooser _chooser;
  
  
  protected File _file;
  
  
  protected boolean _mustExist;
  
  
  public DirectorySelectorComponent(Component parent, DirectoryChooser chooser) {
    this(parent, chooser, DEFAULT_NUM_COLS, DEFAULT_FONT_SIZE);
  }
  
  
  public DirectorySelectorComponent(Component parent, DirectoryChooser chooser, int numCols, float fontSize) {
    this(parent, chooser, numCols, fontSize, true);
  }
  
  
  public DirectorySelectorComponent(Component parent, DirectoryChooser chooser, int numCols, float fontSize,
                                    boolean mustExist) {
    
    _parent = parent;
    _chooser = chooser;
    _file = FileOps.NULL_FILE;
    _mustExist = mustExist;
    
    _fileField = new JTextField(numCols) {
      public Dimension getMaximumSize() { return new Dimension(Short.MAX_VALUE, super.getPreferredSize().height); }
    };
    
    _fileField.setFont(_fileField.getFont().deriveFont(fontSize));
    _fileField.setPreferredSize(new Dimension(22,22));
    _fileField.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) { validateTextField(); }
    });
    
    _fileField.addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {  }
      public void focusLost(FocusEvent e) { validateTextField(); }
    });
    
    _chooserButton = new JButton("...");
    _chooserButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) { _chooseFile(); }
    });
    
    _chooserButton.setMaximumSize(new Dimension(22, 22));
    _chooserButton.setMargin(new Insets(0,5,0,5));
    
    
    this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    this.add(_fileField);
    this.add(_chooserButton);
  }
  
  public void setEnabled(boolean isEnabled) {
    _fileField.setEnabled(isEnabled);
    _chooserButton.setEnabled(isEnabled);
    super.setEnabled(isEnabled);
  }
  
  
  public JTextField getFileField() { return _fileField; }
  
  
  public DirectoryChooser getFileChooser() { return _chooser; }
  
  
  public File getFileFromField() {
    String txt = _fileField.getText().trim();
    if (txt.equals("")) _file = FileOps.NULL_FILE;
    else _file = new File(txt);
    
    return _file;
  }
  
  
  public void setFileField(File file) {
    _file = file;
    if (file != null && ! file.getPath().equals("")) {
      try { _file = file.getCanonicalFile(); }
      catch(IOException e) {  }
    }
    resetFileField();
  }
  
  public void resetFileField() {
    if (_file == null) _fileField.setText("");
    else {
      _fileField.setText(_file.toString());
      _fileField.setCaretPosition(_fileField.getText().length());
    }
  }
  
  public void setToolTipText(String text) {
    super.setToolTipText(text);
    _fileField.setToolTipText(text);
    _chooserButton.setToolTipText(text);
  }
  
  
  public void addChoosableFileFilter(FileFilter filter) { _chooser.addChoosableFileFilter(filter); }
  
  
  public void removeChoosableFileFilter(FileFilter filter) { _chooser.removeChoosableFileFilter(filter); }
  
  public void clearChoosableFileFilters() { _chooser.resetChoosableFileFilters(); }
  
  
  private boolean _validationInProgress = false;
  
  
  public boolean validateTextField() {
    if (_validationInProgress) return true;
    _validationInProgress = true;
    
    String newValue = _fileField.getText().trim();
    
    File newFile = FileOps.NULL_FILE;
    if (! newValue.equals("")) {
      newFile = new File(newValue);
      if (! newFile.isDirectory() && _chooser.isFileSelectionEnabled()) newFile = newFile.getParentFile();
    }
    
    if (newFile != FileOps.NULL_FILE && _mustExist && ! newFile.exists()) {
      JOptionPane.showMessageDialog(_parent, "The file '"+ newValue + "'\nis invalid because it does not exist.",
                                    "Invalid File Name", JOptionPane.ERROR_MESSAGE);
      resetFileField(); 
      _validationInProgress = false;
      return false;
    }
    else {
      setFileField(newFile);
      _validationInProgress = false;
      return true;
    }
  }
  
  
  protected void _chooseFile() {
    
    
    int returnValue = _chooser.showDialog(_file);
    if (returnValue == DirectoryChooser.APPROVE_OPTION) {
      File chosen = _chooser.getSelectedDirectory();
      if (chosen != null) setFileField(chosen);
    }
  }
  
}