

package edu.rice.cs.drjava.model.repl;

import java.util.ArrayList;
import java.util.HashMap;
import edu.rice.cs.util.FileOps;
import edu.rice.cs.util.StringOps;
import edu.rice.cs.drjava.config.*;
import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.model.*;
import edu.rice.cs.util.OperationCanceledException;

import java.io.Serializable;
import java.io.IOException;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.BufferedWriter;


public class History implements OptionConstants, Serializable {

  public static final String INTERACTION_SEPARATOR = "//End of Interaction//";

  
  private volatile int _maxSize;

  
  public static final String HISTORY_FORMAT_VERSION_2 = "// DrJava saved history v2" + StringOps.EOL;

  private final ArrayList<String> _vector = new ArrayList<String>();
  private volatile int _cursor = -1;

  
  private final HashMap<Integer, String> _editedEntries = new HashMap<Integer, String>();

  
  private volatile String _currentSearchString = "";
  
   
  public final OptionListener<Integer> historyOptionListener = new OptionListener<Integer>() {
    public void optionChanged (OptionEvent<Integer> oce) {
      int newSize = oce.value;

      setMaxSize(newSize);
    }
    public String toString() { return "HISTORY_MAX_SIZE OptionListener #" + hashCode(); }
  };
    

  
  public History() {
    this(DrJava.getConfig().getSetting(HISTORY_MAX_SIZE));
    
    DrJava.getConfig().addOptionListener(HISTORY_MAX_SIZE, historyOptionListener);  
  }

  
  public History(int maxSize) {
    if (maxSize < 0) maxSize = 0;   
    _maxSize = maxSize;
  }
  
    
  public OptionListener<Integer> getHistoryOptionListener() { return historyOptionListener; }

  
  public void setEditedEntry(String entry) {
    if (! entry.equals(getCurrent())) _editedEntries.put(Integer.valueOf(_cursor), entry);
  }

  
  public void add(String item) {
    
    if (item.trim().length() > 0) {
      _vector.add(item);
      
      if (_vector.size() > _maxSize) _vector.remove(0);

      moveEnd();
      _editedEntries.clear();
    }
  }
  
  
  public String removeLast() {
    if (_vector.size() == 0) { return null; }
    String last = _vector.remove(_vector.size()-1);
    if (_cursor > _vector.size()) { _cursor = _vector.size()-1; }
    return last;
  }

  
  public void moveEnd() { _cursor = _vector.size(); }

  
  public void movePrevious(String entry) {
    if (! hasPrevious()) throw new ArrayIndexOutOfBoundsException();
    setEditedEntry(entry);
    _cursor--;
  }
  
  
  public String lastEntry() { return _vector.get(_cursor - 1); }

  
  public void moveNext(String entry) {
    if (! hasNext()) throw  new ArrayIndexOutOfBoundsException();
    setEditedEntry(entry);
    _cursor++;
  }

  
  public boolean hasNext() { return  _cursor < (_vector.size()); }

  
  public boolean hasPrevious() { return  _cursor > 0; }

  
  public String getCurrent() {
    Integer cursor = Integer.valueOf(_cursor);
    if (_editedEntries.containsKey(cursor))  return _editedEntries.get(cursor);

    if (hasNext()) return _vector.get(_cursor);
    return "";
  }

  
  public int size() { return _vector.size(); }

  
  public void clear() { _vector.clear(); }

  
  public String getHistoryAsStringWithSemicolons() {
    final StringBuilder s = new StringBuilder();
    final String delimiter = INTERACTION_SEPARATOR + StringOps.EOL;
    for (int i = 0; i < _vector.size(); i++) {
      String nextLine = _vector.get(i);





      s.append(nextLine);
      s.append(delimiter);
    }
    return s.toString();
  }

  
  public String getHistoryAsString() {
    final StringBuilder sb = new StringBuilder();
    final String delimiter = StringOps.EOL;
    for (String s: _vector) sb.append(s).append(delimiter);
    return sb.toString();
  }

  
  public void writeToFile(FileSaveSelector selector) throws IOException {
    writeToFile(selector, getHistoryAsStringWithSemicolons());
  }

  
  public static void writeToFile(FileSaveSelector selector, final String editedVersion) throws IOException {
    File c;
    
    try { c = selector.getFile(); }
    catch (OperationCanceledException oce) { return; }
    
    
    if (c != null) {
      if (! c.exists() || selector.verifyOverwrite()) {
        FileOps.DefaultFileSaver saver = new FileOps.DefaultFileSaver(c) {
          public void saveTo(OutputStream os) throws IOException {

            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
            String file = HISTORY_FORMAT_VERSION_2 + editedVersion;
            bw.write(file, 0, file.length());
            bw.close();
          }
        };
        FileOps.saveFile(saver);
      }
    }
  }

  
  public void setMaxSize(int newSize) {
    if (newSize < 0) newSize = 0;    

    
    if (size() > newSize) {

      int numToDelete = size() - newSize;
      for (int i = 0; i < numToDelete; i++) { _vector.remove(0); }

      moveEnd();
    }
    _maxSize = newSize;
  }

  
  public void reverseSearch(String currentInteraction) {
    if (_currentSearchString.equals("") || ! currentInteraction.startsWith(_currentSearchString))
      _currentSearchString = currentInteraction;

    setEditedEntry(currentInteraction);
    while (hasPrevious()) {
      movePrevious(getCurrent());
      if (getCurrent().startsWith(_currentSearchString, 0)) break;
    }
    
    if (! getCurrent().startsWith(_currentSearchString, 0))  moveEnd();
  }

  
  public void forwardSearch(String currentInteraction) {
    if (_currentSearchString.equals("") || ! currentInteraction.startsWith(_currentSearchString))
      _currentSearchString = currentInteraction;

    setEditedEntry(currentInteraction);
    while (hasNext()) {
      moveNext(getCurrent());
      if (getCurrent().startsWith(_currentSearchString, 0))  break;
    }
    
    if (! getCurrent().startsWith(_currentSearchString, 0)) moveEnd();
  }
}
