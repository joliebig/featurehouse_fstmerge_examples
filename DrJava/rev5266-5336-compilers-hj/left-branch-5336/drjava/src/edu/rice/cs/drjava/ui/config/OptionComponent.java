

package edu.rice.cs.drjava.ui.config;

import javax.swing.*;
import java.awt.*;

import java.io.Serializable;
import java.util.Vector;

import edu.rice.cs.drjava.config.*;
import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.plt.lambda.Lambda;
import edu.rice.cs.util.swing.SwingFrame;
import edu.rice.cs.util.swing.Utilities;


public abstract class OptionComponent<T,C extends JComponent> implements Serializable {
  protected final Option<T> _option;
  protected final JLabel _label;
  protected final SwingFrame _parent;
  protected volatile boolean _entireColumn;
  protected volatile String _labelText;
  protected volatile C _guiComponent;
    
  public OptionComponent(Option<T> option, String labelText, SwingFrame parent) {
    _option = option;
    _labelText = labelText;
    _label = new JLabel(_labelText);
    _label.setHorizontalAlignment(JLabel.RIGHT);
    _parent = parent;
    if (option != null) {
      DrJava.getConfig().addOptionListener(option, new OptionListener<T>() {
        public void optionChanged(OptionEvent<T> oe) { resetToCurrent(); }
      });
    }
  }
  
  
  public OptionComponent (String labelText, SwingFrame parent) { this(null, labelText, parent); }
  
  public Option<T> getOption() { return _option; }
  
  public String getLabelText() { return _label.getText(); } 
  
  public JLabel getLabel() { return _label; } 
  
  public boolean useEntireColumn() { return _entireColumn; }
  
  
  public C getComponent() { return _guiComponent; }
  
  
  public void setComponent(C component) {
    _guiComponent = component;
    if ((_guiComponent!=null) && (_option!=null)) {
      _guiComponent.setEnabled(DrJava.getConfig().isEditable(_option));
      
      for (Component subComponent: _guiComponent.getComponents()) {
        subComponent.setEnabled(DrJava.getConfig().isEditable(_option));
      }
    }
  }

  
  public abstract void setDescription(String description);

  
  public OptionComponent<T,C> setEntireColumn(boolean entireColumn) { 
    _entireColumn = entireColumn; 
    return this; 
  }

  
  public boolean getEntireColumn() { return _entireColumn; }
  
   
  public abstract boolean updateConfig();

  
  public void resetToCurrent() {
    if (_option != null) setValue(DrJava.getConfig().getSetting(_option));
  }
  
  
  public void resetToDefault() {
    if (_option != null) {
      setValue(_option.getDefault());
      notifyChangeListeners();  
    }
  }
  
  
  public abstract void setValue(T value);
  
  public void showErrorMessage(String title, OptionParseException e) { showErrorMessage(title, e.value, e.message); }
  
  public void showErrorMessage(String title, String value, String message) {
    JOptionPane.showMessageDialog(_parent,
                                  "There was an error in one of the options that you entered.\n" +
                                  "Option: '" + getLabelText() + "'\n" +
                                  "Your value: '" + value + "'\n" +
                                  "Error: " +  message,
                                  title,
                                  JOptionPane.WARNING_MESSAGE);
  }
  
  
  public static interface ChangeListener extends Lambda<Object, Object> { }
  
  
  public void addChangeListener(ChangeListener listener) { _changeListeners.add(listener); }
  
  
  public void removeChangeListener(ChangeListener listener) { _changeListeners.remove(listener); }
  
  
  protected void notifyChangeListeners() {
    assert _parent.duringInit() || Utilities.TEST_MODE || EventQueue.isDispatchThread();


      
      ChangeListener[] listeners = _changeListeners.toArray(new ChangeListener[_changeListeners.size()]);
    for (ChangeListener l: listeners)  l.value(OptionComponent.this); 


  }
  
  
  private volatile Vector<ChangeListener> _changeListeners = new Vector<ChangeListener>();  
}
                                      
  
