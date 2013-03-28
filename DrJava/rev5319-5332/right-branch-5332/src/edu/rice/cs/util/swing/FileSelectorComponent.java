

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
import edu.rice.cs.util.UnexpectedException;


public class FileSelectorComponent extends JPanel {
  
  
  public static final int DEFAULT_NUM_COLS = 30;
  
  
  public static final float DEFAULT_FONT_SIZE = 10f;
  
  
  protected final SwingFrame _parent;
  
  
  protected final JTextField _fileField;
  
  
  protected final JButton _chooserButton;
  
  
  protected final JFileChooser _chooser;
  
  
  protected volatile FileFilter _fileFilter;
  
  
  protected volatile File _file;
  
  
  protected volatile boolean _mustExist;
  
  
  public FileSelectorComponent(SwingFrame parent, JFileChooser chooser) {
    this(parent, chooser, DEFAULT_NUM_COLS, DEFAULT_FONT_SIZE, true);
  }
  
  
  public FileSelectorComponent(SwingFrame parent, JFileChooser chooser, int numCols, float fontSize) {
    this(parent, chooser, numCols, fontSize, true);
  }
  
  
  public FileSelectorComponent(SwingFrame parent, JFileChooser chooser, int numCols, float fontSize, boolean mustExist) {
    
    if (chooser == null) throw new UnexpectedException("Error: called new FileSelectorComponent(...) with a null chooser!");
    
    _parent = parent;
    _chooser = chooser;
    _fileFilter = null;
    _mustExist = mustExist;
    
    _fileField = new JTextField(numCols) {
      public Dimension getMaximumSize() {
        return new Dimension(Short.MAX_VALUE, super.getPreferredSize().height);
      }
    };
    
    _fileField.setFont(_fileField.getFont().deriveFont(fontSize));
    _fileField.setPreferredSize(new Dimension(22, 22));
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
    _chooserButton.setMargin(new Insets(0, 5 ,0, 5));
    
    
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
  
  
  public JFileChooser getFileChooser() { return _chooser; }
  
  
  public File getFileFromField() {
    String txt = _fileField.getText().trim();
    if (txt.equals("")) _file = FileOps.NULL_FILE;
    else _file = new File(txt);
    
    return _file;
  }
  
  
  public void setFileField(File file) {
    _file = file;
    if (file != null && !file.getPath().equals("")) {
      try {_file = file.getCanonicalFile(); }
      catch(IOException e) {  }
    }
    resetFileField();
  }
  
  public void resetFileField() {
    if (_file == null) _fileField.setText("");
    else {
      _fileField.setText(_file.getPath());
      _fileField.setCaretPosition(_fileField.getText().length());
    }
  }
  
  
  public void setFileFilter(FileFilter filter) { _fileFilter = filter; }
  
  public void setToolTipText(String text) {
    super.setToolTipText(text);
    _fileField.setToolTipText(text);
    _chooser.setToolTipText(text);
  }
  
  
  protected void _chooseFile() {
    
    if (_file != null && _file.exists()) {
      _chooser.setCurrentDirectory(_file);
      _chooser.setSelectedFile(_file);
    }
    
    
    if (_fileFilter != null) _chooser.setFileFilter(_fileFilter);
    
    
    int returnValue = _chooser.showDialog(_parent, null);
    if (returnValue == JFileChooser.APPROVE_OPTION) {
      File chosen = _chooser.getSelectedFile();
      if (chosen != null) setFileField(chosen);
    }
  }
  
  
  
  

  
  
  public boolean validateTextField() {


    
    String newValue = _fileField.getText().trim();
    
    File newFile = FileOps.NULL_FILE;
    if (! newValue.equals(""))
      newFile = new File(newValue);
    
    if (newFile != FileOps.NULL_FILE && _mustExist && !newFile.exists()) {
      JOptionPane.showMessageDialog(_parent, "The file '" +  newValue + "'\nis invalid because it does not exist.",
                                    "Invalid File Name", JOptionPane.ERROR_MESSAGE);
      if (_file != null && ! _file.exists()) _file = FileOps.NULL_FILE;
      resetFileField(); 
      

      return false;
    }
    else {
      setFileField(newFile);

      return true;
    }
  }
}
