

package edu.rice.cs.drjava.ui;

import java.awt.*;
import javax.swing.*;


public class InteractionsScriptPane extends JPanel {
  
  
  public InteractionsScriptPane(int rows, int cols) {
    setLayout(new GridLayout(rows, cols));
  }
  
  
  public void addButton(Action a) {
    JButton b = new JButton(a);
    add(b);      
  }
}
