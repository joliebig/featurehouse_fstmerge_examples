

package edu.rice.cs.drjava.ui.config;

import javax.swing.*;
import edu.rice.cs.drjava.config.*;
import edu.rice.cs.drjava.*;
import java.awt.*;
import java.awt.event.*;



public class ButtonComponent extends OptionComponent<Object> {
  protected JButton _jb;

  
  public ButtonComponent(ActionListener l, String text, Frame parent) {
    super(null, "", parent);
    _jb = new JButton(text);
    _jb.addActionListener(l);
    _jb.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        notifyChangeListeners();
      }
    });
  }

  
  public ButtonComponent(ActionListener l, String text, Frame parent, String description) {
    this(l, text, parent);
    setDescription(description);
  }

  
  public void setDescription(String description) {
    _jb.setToolTipText(description);
    _label.setToolTipText(description);
  }

  
  public boolean updateConfig() {
    
    return true;
  } 
  
  
  public void setValue(Object value) {
    
  }
  
  
  public JComponent getComponent() { return _jb; }
}
