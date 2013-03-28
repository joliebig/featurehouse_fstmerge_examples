

package edu.rice.cs.drjava.ui.config;

import javax.swing.*;
import javax.swing.event.*;
import edu.rice.cs.drjava.config.*;
import edu.rice.cs.drjava.*;
import edu.rice.cs.util.swing.FileSelectorComponent;
import edu.rice.cs.util.swing.SwingFrame;

import java.io.File;
import javax.swing.filechooser.FileFilter;


public class FileOptionComponent extends OptionComponent<File> implements OptionConstants {

  private volatile FileSelectorComponent _component;

  public FileOptionComponent (FileOption opt, String text, SwingFrame parent, JFileChooser jfc) {
    super(opt, text, parent);
    _component = new FileSelectorComponent(parent, jfc, 30, 10f);
    File setting = DrJava.getConfig().getSetting(_option);
    if (setting != _option.getDefault()) { _component.setFileField(setting); }
    _component.getFileField().getDocument().addDocumentListener(new DocumentListener() {
      public void insertUpdate(DocumentEvent e) { notifyChangeListeners(); }
      public void removeUpdate(DocumentEvent e) { notifyChangeListeners(); }
      public void changedUpdate(DocumentEvent e) { notifyChangeListeners(); }
    });
  }

  
  public FileOptionComponent (FileOption opt, String text, SwingFrame parent, String description, JFileChooser jfc) {
    this(opt, text, parent, jfc);
    setDescription(description);
  }

  
  public void setDescription(String description) {
    _component.setToolTipText(description);
    _label.setToolTipText(description);
  }

  
  public boolean updateConfig() {
    File componentFile = _component.getFileFromField();
    File currentFile = DrJava.getConfig().getSetting(_option);
    
    if (componentFile != null && ! componentFile.equals(currentFile)) {
      DrJava.getConfig().setSetting(_option, componentFile);
    }
    else if (componentFile == null) {
      DrJava.getConfig().setSetting(_option, _option.getDefault());
    }

    return true;
  }

  
  public void setValue(File value) { _component.setFileField(value); }

  
  public JComponent getComponent() { return _component; }
  
  
  public void setFileFilter(FileFilter fileFilter) { _component.setFileFilter(fileFilter); }
}
