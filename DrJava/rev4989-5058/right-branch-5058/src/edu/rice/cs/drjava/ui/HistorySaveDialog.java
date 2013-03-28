


package edu.rice.cs.drjava.ui;

import javax.swing.*;
import java.awt.event.*;



public class HistorySaveDialog extends DrJavaScrollableDialog  {
  
  
  private String _history;
  
  
  public HistorySaveDialog (JFrame parent) {
    super(parent, "Save Interactions History",
          "Make any changes to the history, and then click \"Save\".", "");
  }
  
  
  protected void _addButtons() {
    
    Action saveAction = new AbstractAction("Save") {
      public void actionPerformed (ActionEvent ae) {
        _history = _textArea.getText();
        _dialog.dispose();
      }
    };
    
    
    Action cancelAction = new AbstractAction("Cancel") {
      public void actionPerformed (ActionEvent ae) { _dialog.dispose(); }
    };
    
    JButton saveButton = new JButton(saveAction);
    JButton cancelButton = new JButton(cancelAction);
    _buttonPanel.add(saveButton);
    _buttonPanel.add(cancelButton);
    _dialog.getRootPane().setDefaultButton(saveButton);
  }
  
  
  public String editHistory(String history) {

    _history = null; 
    _textArea.setText(history);
    _textArea.setEditable(true);
    
    
    show();
    
    
    return _history;

  }
}
