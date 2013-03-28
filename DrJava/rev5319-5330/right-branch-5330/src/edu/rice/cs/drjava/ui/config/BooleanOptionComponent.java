

package edu.rice.cs.drjava.ui.config;

import java.awt.event.*;
import javax.swing.*;
import edu.rice.cs.drjava.config.*;
import edu.rice.cs.drjava.*;
import edu.rice.cs.util.swing.SwingFrame;


public class BooleanOptionComponent extends OptionComponent<Boolean,JCheckBox> {
  protected JCheckBox _jcb;

  
  public BooleanOptionComponent(BooleanOption opt, String text, SwingFrame parent, boolean left) {
    super(opt, left?text:"", parent);
    _jcb = new JCheckBox();
    _jcb.setText(left?"":text);
    _jcb.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) { notifyChangeListeners(); }
    });
    
    _jcb.setSelected(DrJava.getConfig().getSetting(_option).booleanValue());
    setComponent(_jcb);
  }

  
  public BooleanOptionComponent(BooleanOption opt, String text, SwingFrame parent, String description, boolean left) {
    this(opt, text, parent, left);
    setDescription(description);
  }
  
    
  public BooleanOptionComponent(BooleanOption opt, String text, SwingFrame parent) {
    this(opt, text, parent, true);
  }

  
  public BooleanOptionComponent(BooleanOption opt, String text, SwingFrame parent, String description) {
    this(opt, text, parent, true);
  }

  
  public void setDescription(String description) {
    _jcb.setToolTipText(description);
    _label.setToolTipText(description);
  }

  
  public boolean updateConfig() {
    Boolean oldValue = DrJava.getConfig().getSetting(_option);
    Boolean newValue = Boolean.valueOf(_jcb.isSelected());
    
    if (! oldValue.equals(newValue)) DrJava.getConfig().setSetting(_option, newValue);      

    return true;
  } 
  
  
  public void setValue(Boolean value) { _jcb.setSelected(value.booleanValue()); }
  


  
  
  public BooleanOptionComponent setEntireColumn(boolean entireColumn) {
    _entireColumn = entireColumn;
    return this;
  }

}
