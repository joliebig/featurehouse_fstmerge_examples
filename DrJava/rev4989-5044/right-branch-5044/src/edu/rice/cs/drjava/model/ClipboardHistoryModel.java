

package edu.rice.cs.drjava.model;

import java.util.List;
import java.util.LinkedList;


public class ClipboardHistoryModel {
  
  private int _maxSize;
  
  
  private LinkedList<String> _history = new LinkedList<String>();
  
  
  private static ClipboardHistoryModel ONLY = null;
  
  
  public static synchronized ClipboardHistoryModel singleton() {
    if (ONLY == null) ONLY = new ClipboardHistoryModel(10);
    return ONLY;
  }
  
  
  private ClipboardHistoryModel(int maxSize) {
    _maxSize = maxSize;
  }
  
  
  public void resize(int maxSize) {
    _maxSize = maxSize;
    while (_history.size()>_maxSize) { _history.removeFirst(); }
  }
  
  
  public synchronized void put(String s) {
    _history.remove(s);
    _history.add(s);
    while (_history.size()>_maxSize) { _history.removeFirst(); }
  }
  
  
  public synchronized List<String> getStrings() {
    return new LinkedList<String>(_history);
  }
  
  
  public synchronized String getMostRecent() {
    if (_history.size() == 0) { return null; }
    else { return _history.getLast(); }
  }
}
