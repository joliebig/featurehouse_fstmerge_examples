

package edu.rice.cs.plt.swing;

import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;


public class ComposedDocumentListener extends ComposedListener<DocumentListener> implements DocumentListener {
  public void changedUpdate(DocumentEvent e) {
    for (DocumentListener l : listeners()) { l.changedUpdate(e); }
  }
  public void insertUpdate(DocumentEvent e) {
    for (DocumentListener l : listeners()) { l.insertUpdate(e); }
  }
  public void removeUpdate(DocumentEvent e) {
    for (DocumentListener l : listeners()) { l.removeUpdate(e); }
  }
}
