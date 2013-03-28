
package genj.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public class ChangeSupport implements DocumentListener, ChangeListener, ActionListener {

  
  private List<ChangeListener> listeners = new CopyOnWriteArrayList<ChangeListener>();
  
  
  private Object source;
  
  
  private boolean hasChanged = false;
  
  private boolean mute = false;
  
  
  public ChangeSupport(Object source) {
    this.source = source;
  }
  
  
  public boolean hasChanged() {
    return hasChanged;
  }
  
  public void setChanged(boolean set) {
    hasChanged = set;
    if (set)
      fireChangeEvent();
  }

  
  public void addChangeListener(ChangeListener l) {
    if (listeners.isEmpty())
      hasChanged = false;
    listeners.add(l);
  }
  
  
  public void removeChangeListener(ChangeListener l) {
    listeners.remove(l);
  }
  
  
  public void removeAllChangeListeners() {
    listeners.clear();
  }
  
  
  public void fireChangeEvent() {
    fireChangeEvent(source);
  }
  protected void fireChangeEvent(Object source) {
    hasChanged = true;
    
    if (!mute) {
      ChangeEvent e = new ChangeEvent(source);
      for (ChangeListener listener : listeners)
        listener.stateChanged(e);
    }
  }
  
  
  public void stateChanged(ChangeEvent e) {
    fireChangeEvent(e.getSource());
  }

    
  public void changedUpdate(DocumentEvent e) {
    fireChangeEvent();
  }
  public void insertUpdate(DocumentEvent e) {
    fireChangeEvent();
  }
  public void removeUpdate(DocumentEvent e) {
    fireChangeEvent();
  }
  
  
  public void actionPerformed(ActionEvent e) {
    fireChangeEvent(e.getSource());
  }

  public void mute() {
    mute = true;
  }

  public void unmute() {
    mute = false;
  }
  
} 
