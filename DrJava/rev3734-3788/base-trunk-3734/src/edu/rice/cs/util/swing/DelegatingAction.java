

package edu.rice.cs.util.swing;

import javax.swing.*;
import java.awt.event.*;
import java.beans.*;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.LinkedList;

public class DelegatingAction implements Action {
  
  private static final String[] KEYS_TO_DELEGATE = {
    
    NAME,
    
    
    
    
    
    
  };

  
  private HashMap<String, Object> _localProperties = new HashMap<String, Object>();

  
  private Action _delegatee;
  private final LinkedList<PropertyChangeListener> _listenerList =
    new LinkedList<PropertyChangeListener>();

  
  public Object getValue(String key) {
    _checkState();

    if (_isDelegatedKey(key)) {
      return _delegatee.getValue(key);
    }
    else {
      return _localProperties.get(key);
    }
  }

  private boolean _isDelegatedKey(String key) {
    for (int i = 0; i < KEYS_TO_DELEGATE.length; i++) {
      if (KEYS_TO_DELEGATE[i].equals(key)) {
        return true;
      }
    }

    return false;
  }

  public void putValue(String key, Object value) {
    _checkState();

    if (_isDelegatedKey(key)) {
      _delegatee.putValue(key, value);
    }
    else {
      Object old = _localProperties.get(key);
      _localProperties.put(key, value);
      ListIterator itor = _listenerList.listIterator();

      PropertyChangeEvent event = new PropertyChangeEvent(this, key, old, value);

      while (itor.hasNext()) {
        PropertyChangeListener listener = (PropertyChangeListener) itor.next();
        listener.propertyChange(event);
      }
    }
  }

  public void setEnabled(boolean b) {
    _checkState();
    _delegatee.setEnabled(b);
  }

  public boolean isEnabled() {
    _checkState();
    return _delegatee.isEnabled();
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    _checkState();
    _delegatee.addPropertyChangeListener(listener);
    _listenerList.add(listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    _checkState();
    _delegatee.removePropertyChangeListener(listener);
    _listenerList.remove(listener);
  }

  public void actionPerformed(ActionEvent ae) {
    _checkState();
    _delegatee.actionPerformed(ae);
  }

  public void setDelegatee(final Action newDelegatee) {
    if (newDelegatee == null) {
      throw new IllegalArgumentException("setDelegatee(null) is not allowed!");
    }

    
    Boolean enabled = newDelegatee.isEnabled() ? Boolean.TRUE : Boolean.FALSE;

    PropertyChangeEvent enabledEvent
      =new PropertyChangeEvent(newDelegatee, "enabled", Boolean.FALSE, enabled);

    PropertyChangeEvent[] events = null;

    if (_delegatee != null) {
      events = new PropertyChangeEvent[KEYS_TO_DELEGATE.length];

      for (int i = 0; i < KEYS_TO_DELEGATE.length; i++) {
        Object oldValue = _delegatee.getValue(KEYS_TO_DELEGATE[i]);
        Object newValue = newDelegatee.getValue(KEYS_TO_DELEGATE[i]);

        events[i] = new PropertyChangeEvent(newDelegatee,
                                            KEYS_TO_DELEGATE[i],
                                            oldValue,
                                            newValue);
      }
    }

    
    ListIterator itor = _listenerList.listIterator();
    while (itor.hasNext()) {
      PropertyChangeListener listener = (PropertyChangeListener) itor.next();

      if (_delegatee != null) {
        _delegatee.removePropertyChangeListener(listener);
      }

      newDelegatee.addPropertyChangeListener(listener);

      

      if (events != null) {
        for (int i = 0; i < events.length; i++) {
          listener.propertyChange(events[i]);
        }
      }

      listener.propertyChange(enabledEvent);
    }

    _delegatee = newDelegatee;
  }

  private void _checkState() {
    if (_delegatee == null) {
      throw new IllegalStateException("delegatee is null!");
    }
  }
}


