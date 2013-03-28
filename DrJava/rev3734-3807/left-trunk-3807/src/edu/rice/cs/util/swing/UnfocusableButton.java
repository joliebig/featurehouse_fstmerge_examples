package edu.rice.cs.util.swing;

import javax.swing.*;


public class UnfocusableButton extends JButton {
  
  public UnfocusableButton() { super(); }

  
  public UnfocusableButton(Action a) { super(a); }

  
  public UnfocusableButton(String s) { super(s); }

  
  public UnfocusableButton(Icon i) { super(i); }

  
  public UnfocusableButton(String s, Icon i) { super(s, i); }

  
  @Deprecated
  public boolean isFocusTraversable() { return false; }

  
  public boolean isFocusable() { return false; }
}
