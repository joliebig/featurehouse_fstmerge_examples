

package edu.rice.cs.drjava.ui.config;

import javax.swing.*;
import javax.swing.event.*;

import edu.rice.cs.drjava.config.*;
import edu.rice.cs.drjava.*;
import edu.rice.cs.util.swing.SwingFrame;


public class IntegerOptionComponent extends OptionComponent<Integer> {
  private volatile JTextField _jtf;
  
  public IntegerOptionComponent (IntegerOption opt, String text, SwingFrame parent) {
    super(opt, text, parent);
    _jtf = new JTextField();
    _jtf.setText(_option.format(DrJava.getConfig().getSetting(_option)));
    _jtf.getDocument().addDocumentListener(new DocumentListener() {
      public void insertUpdate(DocumentEvent e) { notifyChangeListeners(); }
      public void removeUpdate(DocumentEvent e) { notifyChangeListeners(); }
      public void changedUpdate(DocumentEvent e) { notifyChangeListeners(); }
    });
  }
  
  
  public IntegerOptionComponent (IntegerOption opt, String text, SwingFrame parent, String description) {
    this(opt, text, parent);
    setDescription(description);
  }

  
  public void setDescription(String description) {
    _jtf.setToolTipText(description);
    _label.setToolTipText(description);
  }

  
  public boolean updateConfig() {
  
    Integer currentValue = DrJava.getConfig().getSetting(_option);
    String enteredString = _jtf.getText().trim();
    
    if (currentValue.toString().equals(enteredString)) return true;
    
    Integer enteredValue;
    try { enteredValue = _option.parse(enteredString); }
    catch (OptionParseException ope) {
      showErrorMessage("Invalid Integer!", ope);
      return false;
    }
    
    DrJava.getConfig().setSetting(_option, enteredValue);
    return true;
  } 
  
  
  public void setValue(Integer value) {
    _jtf.setText(_option.format(value));
  }
  
  
  public JComponent getComponent() { return _jtf; }
    
}