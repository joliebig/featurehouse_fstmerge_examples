

package edu.rice.cs.drjava.ui;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;


public class CommonCloseButton extends JButton {

  public CommonCloseButton() {
    super(MainFrame.getIcon("CloseX10.gif"));
    setMargin(new Insets(0,0,0,0));
  }
  
  public CommonCloseButton(ActionListener l) {
    this();
    addActionListener(l);
  }
}
