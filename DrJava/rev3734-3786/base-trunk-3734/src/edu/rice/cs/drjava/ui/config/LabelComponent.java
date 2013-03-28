

package edu.rice.cs.drjava.ui.config;

import javax.swing.*;
import java.awt.*;


public class LabelComponent extends OptionComponent<Object> {
  private JTextArea _text;

  public LabelComponent(String text, Frame parent) {
    super("", parent);
    _text = new JTextArea(text);
    _text.setEditable(false);
    _text.setBackground(parent.getBackground());

  }

  public LabelComponent(String text, Frame parent, String description) {
    this(text, parent);
    setDescription(description);
  }

  public void setDescription(String description) {
    _text.setToolTipText(description);
    _label.setToolTipText(description);
  }

  
  public boolean updateConfig() {
    return true;
  }

  
  public void setValue(Object value) {
  }

  
  public JComponent getComponent() { return _text; }

}