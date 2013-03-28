

package edu.rice.cs.drjava.ui.config;

import java.io.File;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;

import edu.rice.cs.drjava.config.*;
import edu.rice.cs.drjava.*;
import edu.rice.cs.util.swing.DirectorySelectorComponent;
import edu.rice.cs.util.swing.DirectoryChooser;
import edu.rice.cs.util.swing.SwingFrame;


public class DirectoryOptionComponent extends OptionComponent<File,DirectorySelectorComponent> implements OptionConstants {

  private DirectorySelectorComponent _component;

  public DirectoryOptionComponent (FileOption opt, String text, SwingFrame parent, DirectoryChooser dc) {
    super(opt, text, parent);
    
    _component = new DirectorySelectorComponent(parent, dc, 30, 10f);
    _component.setFileField(DrJava.getConfig().getSetting(_option));
    _component.getFileField().getDocument().addDocumentListener(new DocumentListener() {
      public void insertUpdate(DocumentEvent e) { notifyChangeListeners(); }
      public void removeUpdate(DocumentEvent e) { notifyChangeListeners(); }
      public void changedUpdate(DocumentEvent e) { notifyChangeListeners(); }
    });
    
    setComponent(_component);
  }

  
  public DirectoryOptionComponent (FileOption opt, String text, SwingFrame parent, String desc, DirectoryChooser dc) {
    this(opt, text, parent, dc);
    setDescription(desc);
  }

  
  public void setDescription(String description) { _label.setToolTipText(description); }

  
  public boolean updateConfig() {
    File componentFile = _component.getFileFromField();
    File currentFile = DrJava.getConfig().getSetting(_option);
    
    if (componentFile != null && !componentFile.equals(currentFile)) {
      DrJava.getConfig().setSetting(_option, componentFile);
    }
    else if (componentFile == null) {
      DrJava.getConfig().setSetting(_option, _option.getDefault());
    }

    return true;
  }

  
  public void setValue(File value) { _component.setFileField(value); }




  
  public void addChoosableFileFilter(FileFilter filter) { _component.addChoosableFileFilter(filter); }
}
