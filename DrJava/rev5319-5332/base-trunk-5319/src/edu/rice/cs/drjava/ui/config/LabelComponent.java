

package edu.rice.cs.drjava.ui.config;

import javax.swing.*;

import edu.rice.cs.util.swing.SwingFrame;


public class LabelComponent extends OptionComponent<Object,JTextArea> {
  private JTextArea _text;

  public LabelComponent(String text, SwingFrame parent, boolean left) {
    super(left?text:"", parent);
    _text = new JTextArea(left?"":text);
    _text.setEditable(false);
    _text.setBackground(parent.getBackground());

  }

  public LabelComponent(String text, SwingFrame parent, String description, boolean left) {
    this(text, parent, left);
    setDescription(description);
  }

  public LabelComponent(String text, SwingFrame parent) { this(text,parent,false); }

  public LabelComponent(String text, SwingFrame parent, String description) {
    this(text, parent, description, false);
  }

  public void setDescription(String description) {
    _text.setToolTipText(description);
    _label.setToolTipText(description);
  }

  
  public boolean updateConfig() { return true; }

  
  public void setValue(Object value) { }

  
  public JTextArea getComponent() { return _text; }
}