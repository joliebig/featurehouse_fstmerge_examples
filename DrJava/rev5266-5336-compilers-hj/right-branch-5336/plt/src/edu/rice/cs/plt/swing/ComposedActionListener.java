

package edu.rice.cs.plt.swing;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class ComposedActionListener extends ComposedListener<ActionListener> implements ActionListener {
  public void actionPerformed(ActionEvent e) {
    for (ActionListener l : listeners()) { l.actionPerformed(e); }
  }
}
