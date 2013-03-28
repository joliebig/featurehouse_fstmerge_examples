

package edu.rice.cs.util.swing;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import edu.rice.cs.util.FileOps;


public class FileSelectorStringComponent extends JPanel {
  
  
  public static final int DEFAULT_NUM_COLS = 30;
  
  
  public static final float DEFAULT_FONT_SIZE = 10f;
  
  
  protected final Component _parent;
  
  
  protected final JTextField _textField;
  
  
  protected final JButton _chooserButton;
  
  
  protected final FileChooser _chooser;
  
  
  protected volatile File _file;
  
  
  public FileSelectorStringComponent(Component parent, FileChooser chooser) {
    this(parent, chooser, DEFAULT_NUM_COLS, DEFAULT_FONT_SIZE);
  }
  
  
  public FileSelectorStringComponent(Component parent, FileChooser chooser, int numCols, float fontSize) {
    _parent = parent;
    _chooser = chooser;
    _file = FileOps.NULL_FILE;
    
    _textField = new JTextField(numCols) {
      public Dimension getMaximumSize() { return new Dimension(Short.MAX_VALUE, super.getPreferredSize().height); }
    };
    _textField.setFont(_textField.getFont().deriveFont(fontSize));
    _textField.setPreferredSize(new Dimension(22,22));
    
    _chooserButton = new JButton("...");
    _chooserButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) { _chooseFile(); }
    });
    _chooserButton.setMaximumSize(new Dimension(22, 22));
    _chooserButton.setMargin(new Insets(0,5,0,5));
    
    this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    this.add(_textField);
    this.add(_chooserButton);
  }
  
  public void setEnabled(boolean isEnabled) {
    _textField.setEnabled(isEnabled);
    _chooserButton.setEnabled(isEnabled);
    super.setEnabled(isEnabled);
  }
  
  
  public JTextField getTextField() { return _textField; }
  
  
  public FileChooser getFileChooser() { return _chooser; }
  
  
  public File convertStringToFile(String s) {
    s = s.trim();
    if (s.equals("")) return null;
    return new File(s);
  }
  
  
  public String convertFileToString(File f) {    
    if (f == null)  return "";
    return f.toString();
  }
  
  
  public File getFileFromField() {
    
    String newValue = _textField.getText();
    File newFile = FileOps.NULL_FILE;
    if (! newValue.equals("")) {
      newFile = convertStringToFile(newValue);
      if (! newFile.isDirectory() && ! _chooser.isFileSelectionEnabled()) newFile = newFile.getParentFile();
    }
    
    if (newFile != null && ! newFile.exists()) newFile = _file;
    
    return newFile;
  }
  
  
  public String getText() { return _textField.getText(); }
  
  
  public void setText(String s) { _textField.setText(s); }
  
  
  public void setFileField(File file) {
    _file = file;
    if (file != null && ! file.getPath().equals("")) {
      try { _file = file.getCanonicalFile(); }
      catch(IOException e) {  }
    }
    resetFileField();
  }
  
  public void resetFileField() {
    _textField.setText(convertFileToString(_file));
    _textField.setCaretPosition(_textField.getText().length());
  }
  
  public void setToolTipText(String text) {
    super.setToolTipText(text);
    _textField.setToolTipText(text);
    _chooserButton.setToolTipText(text);
  }
  
  
  public void addChoosableFileFilter(FileFilter filter) {
    _chooser.addChoosableFileFilter(filter);
  }
  
  
  public void removeChoosableFileFilter(FileFilter filter) {
    _chooser.removeChoosableFileFilter(filter);
  }
  
  public void clearChoosableFileFilters() {
    _chooser.resetChoosableFileFilters();
  }
  
  
  protected void _chooseFile() { 
    File f = getFileFromField();
    if (f != null && f.exists()) {
      _chooser.setCurrentDirectory(f);
      _chooser.setSelectedFile(f);
    }
    int returnValue = _chooser.showDialog(_parent, null);
    if (returnValue == FileChooser.APPROVE_OPTION) {
      File chosen = _chooser.getSelectedFile();
      if (chosen != null) { setFileField(chosen); }
    }
  }
  
}