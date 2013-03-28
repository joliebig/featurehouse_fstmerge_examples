

package edu.rice.cs.drjava.ui.config;

import javax.swing.*;
import java.awt.*;

import java.io.Serializable;
import java.util.ArrayList;

import edu.rice.cs.drjava.config.*;
import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.util.Lambda;


public abstract class OptionComponent<T> implements Serializable {
  protected Option<T> _option;
  protected JLabel _label;
  protected Frame _parent;
    
  public OptionComponent(Option<T> option, String labelText, Frame parent) {
    _option = option;
    _label = new JLabel(labelText);
    _label.setHorizontalAlignment(JLabel.RIGHT);
    _parent = parent;
    if (option != null) {
      DrJava.getConfig().addOptionListener(option, new OptionListener<T>() {
        public void optionChanged(OptionEvent<T> oe) {
          resetToCurrent();
        }
      });
    }
  }
  
  
  public OptionComponent (String labelText, Frame parent) { this(null, labelText, parent); }
  
  public Option<T> getOption() { return _option; }
  
  public String getLabelText() { return _label.getText(); } 
  
  public JLabel getLabel() { return _label; } 
  
  
  public abstract JComponent getComponent();

  
  public abstract void setDescription(String description);

   
  public abstract boolean updateConfig();

  
  public void resetToCurrent() {
    if (_option != null) {
      setValue(DrJava.getConfig().getSetting(_option));
    }
  }
  
  
  public void resetToDefault() {
    if (_option != null) {
      setValue(_option.getDefault());
      notifyChangeListeners();
    }
  }
  
  
  public abstract void setValue(T value);
  
  public void showErrorMessage(String title, OptionParseException e) {
    showErrorMessage(title, e.value, e.message);
  }
  
  public void showErrorMessage(String title, String value, String message) {
    JOptionPane.showMessageDialog(_parent,
                                  "There was an error in one of the options that you entered.\n" +
                                  "Option: '" + getLabelText() + "'\n" +
                                  "Your value: '" + value + "'\n" +
                                  "Error: "+ message,
                                  title,
                                  JOptionPane.WARNING_MESSAGE);
  }
  
  
  public static interface ChangeListener extends Lambda<Object, Object> {
    public abstract Object apply(Object c);
  }
  
  
  public void addChangeListener(ChangeListener listener) {
    _changeListeners.add(listener);
  }
  
  
  public void removeChangeListener(ChangeListener listener) {
    _changeListeners.remove(listener);
  }
  
  
  protected void notifyChangeListeners() {
    for(ChangeListener l: _changeListeners) {
      l.apply(this);
    }
  }
  
  
  private ArrayList<ChangeListener> _changeListeners = new ArrayList<ChangeListener>();
}
                                      
  
