

package edu.rice.cs.plt.swing;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;


public class ComposedChangeListener extends ComposedListener<ChangeListener> implements ChangeListener {
  public void stateChanged(ChangeEvent e) {
    for (ChangeListener l : listeners()) { l.stateChanged(e); }
  }
}
