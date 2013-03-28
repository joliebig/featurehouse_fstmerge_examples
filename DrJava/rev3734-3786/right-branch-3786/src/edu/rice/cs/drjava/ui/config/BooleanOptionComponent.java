

package edu.rice.cs.drjava.ui.config;

import javax.swing.*;
import edu.rice.cs.drjava.config.*;
import edu.rice.cs.drjava.*;
import java.awt.*;
import java.awt.event.*;



public class BooleanOptionComponent extends OptionComponent<Boolean> {
  protected JCheckBox _jcb;

  
  public BooleanOptionComponent(BooleanOption opt, String text, Frame parent) {
    super(opt, "", parent);
    _jcb = new JCheckBox();
    _jcb.setText(text);
    _jcb.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        notifyChangeListeners();
      }
    });
    
    _jcb.setSelected(DrJava.getConfig().getSetting(_option).booleanValue());
  }

  
  public BooleanOptionComponent(BooleanOption opt, String text,
                                Frame parent, String description) {
    this(opt, text, parent);
    setDescription(description);
  }

  
  public void setDescription(String description) {
    _jcb.setToolTipText(description);
    _label.setToolTipText(description);
  }

  
  public boolean updateConfig() {
    Boolean oldValue = DrJava.getConfig().getSetting(_option);
    Boolean newValue = Boolean.valueOf(_jcb.isSelected());
    
    if (!oldValue.equals(newValue)) {
      DrJava.getConfig().setSetting(_option, newValue);      
    }
    return true;
  } 
  
  
  public void setValue(Boolean value) {
    _jcb.setSelected(value.booleanValue());
  }
  
  
  public JComponent getComponent() { return _jcb; }
}
