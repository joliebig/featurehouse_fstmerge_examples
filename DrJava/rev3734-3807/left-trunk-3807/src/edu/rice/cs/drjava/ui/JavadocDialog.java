

package edu.rice.cs.drjava.ui;

import edu.rice.cs.util.swing.DirectorySelectorComponent;
import edu.rice.cs.util.swing.DirectoryChooser;
import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.Configuration;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.util.DirectorySelector;
import edu.rice.cs.util.OperationCanceledException;

import javax.swing.*;
import java.io.File;


public class JavadocDialog implements DirectorySelector {
  
  private final JFrame _frame;

  
  private final DirectorySelectorComponent _selector;

  
  private final JCheckBox _checkBox;

  
  private final JOptionPane _optionPane;

  
  private final JDialog _dialog;

  
  private boolean _useSuggestion;

  
  private File _suggestedDir;

  
  public JavadocDialog(JFrame frame) {
    _frame = frame;
    _useSuggestion = true;
    _suggestedDir = null;

    
    DirectoryChooser chooser = new DirectoryChooser();
    chooser.setMultiSelectionEnabled(false);
    chooser.setApproveButtonText("Select");

    
    
    String msg = "Select a destination directory for the Javadoc files:";
    _selector = new DirectorySelectorComponent(_frame, chooser);
    _checkBox = new JCheckBox("Always Prompt For Destination");
    Object[] components = new Object[] { msg, _selector, _checkBox };

    _optionPane = new JOptionPane(components,
                                  JOptionPane.QUESTION_MESSAGE,
                                  JOptionPane.OK_CANCEL_OPTION);
    _dialog = _optionPane.createDialog(_frame, "Select Javadoc Destination");
  }


  public boolean isRecursive() { return false; }
  
  
  public File getDirectory(File start) throws OperationCanceledException {
    if (start != null) {
      
      _selector.setFileField(start);
    }
    else if (_useSuggestion && (_suggestedDir != null)) {
      
      _selector.setFileField(_suggestedDir);
    }

    Configuration config = DrJava.getConfig();
    boolean ask = config.getSetting(OptionConstants.JAVADOC_PROMPT_FOR_DESTINATION).booleanValue();

    if (ask) {
      
      _checkBox.setSelected(true);

      
      _dialog.setVisible(true);

      
      if (!_isPositiveResult()) {
        throw new OperationCanceledException();
      }

      
      if (!_checkBox.isSelected()) {
        config.setSetting(OptionConstants.JAVADOC_PROMPT_FOR_DESTINATION,
                          Boolean.FALSE);
      }

      
      if ((start == null) &&
          (_useSuggestion && (_suggestedDir != null)) &&
          !_selector.getFileFromField().equals(_suggestedDir)) {
        _useSuggestion = false;
      }
    }
    return _selector.getFileFromField();
  }

  
  public boolean askUser(String message, String title) {
    int choice = JOptionPane.showConfirmDialog(_frame, message, title, JOptionPane.YES_NO_OPTION);
    return (choice == JOptionPane.YES_OPTION);
  }

  
  public void warnUser(String message, String title) {
    JOptionPane.showMessageDialog(_frame, message, title, JOptionPane.ERROR_MESSAGE);
  }

  
  public void setSuggestedDir(File dir) { _suggestedDir = dir; }

  
  public void setUseSuggestion(boolean use) { _useSuggestion = use; }

  
  private boolean _isPositiveResult() {
    Object result = _optionPane.getValue();
    if ((result != null) && (result instanceof Integer)) {
      int rc = ((Integer)result).intValue();
      return rc == JOptionPane.OK_OPTION;
    }
    else return false;
  }
}