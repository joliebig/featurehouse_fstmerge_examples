

package edu.rice.cs.plt.swing;

import java.util.LinkedList;


public abstract class ComposedListener<T> {
  private final LinkedList<T> _listeners;
  public ComposedListener() { _listeners = new LinkedList<T>(); }
  public void add(T listener) { _listeners.addFirst(listener); }
  public void remove(T listener) { _listeners.remove(listener); }
  protected Iterable<T> listeners() { return _listeners; }
}
