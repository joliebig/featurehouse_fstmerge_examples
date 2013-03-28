

package edu.rice.cs.plt.swing;

import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;


public class ComposedCaretListener extends ComposedListener<CaretListener> implements CaretListener {
  public void caretUpdate(CaretEvent e) {
    for (CaretListener l : listeners()) { l.caretUpdate(e); }
  }
}
