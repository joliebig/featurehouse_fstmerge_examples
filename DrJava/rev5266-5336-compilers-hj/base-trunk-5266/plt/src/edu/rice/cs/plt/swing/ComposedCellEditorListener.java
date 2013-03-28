

package edu.rice.cs.plt.swing;

import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;


public class ComposedCellEditorListener extends ComposedListener<CellEditorListener> implements CellEditorListener {
  public void editingCanceled(ChangeEvent e) {
    for (CellEditorListener l : listeners()) { l.editingCanceled(e); }
  }
  public void editingStopped(ChangeEvent e) {
    for (CellEditorListener l : listeners()) { l.editingStopped(e); }
  }
}
