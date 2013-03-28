

package edu.rice.cs.drjava.model.definitions;

import java.awt.EventQueue;
import java.util.LinkedList;
import javax.swing.undo.*;

import edu.rice.cs.drjava.model.GlobalEventNotifier;


public class CompoundUndoManager extends UndoManager {
  
  static edu.rice.cs.util.Log LOG = new edu.rice.cs.util.Log("CompoundUndoManager.txt", false);
  
  private static volatile int counter = 0;
  
  private final int id;
  
  
  private final LinkedList<CompoundEdit> _compoundEdits;
  
  
  private final LinkedList<Integer> _keys;
  
  
  private volatile int _nextKey;
  
  
  private volatile UndoableEdit _savePoint;
  
  
  private final GlobalEventNotifier _notifier;
  
  
  public CompoundUndoManager(GlobalEventNotifier notifier) {
    super();
    counter++;
    id = counter;
    _compoundEdits = new LinkedList<CompoundEdit>();
    _keys = new LinkedList<Integer>();
    _nextKey = 0;
    _savePoint = null;
    _notifier = notifier;
  }
  
  
  public  int startCompoundEdit() {
    _compoundEdits.add(0, new CompoundEdit());
    _keys.add(0, Integer.valueOf(_nextKey));
    if (_nextKey < Integer.MAX_VALUE) _nextKey++;
    else _nextKey = Integer.MIN_VALUE;
    return _keys.get(0).intValue();
  }
  
  
  public  void endLastCompoundEdit() {
    if (_keys.size() == 0) return;
    
    endCompoundEdit(_keys.get(0).intValue());
  }
  
  
  public  void endCompoundEdit(int key) {
    if (_keys.size() == 0) return;
    
    if (_keys.get(0) == key) {
      _keys.remove(0);
      final CompoundEdit ce = _compoundEdits.remove(0);
      
      ce.end();
      if (ce.canUndo()) {
        if (! _compoundEditInProgress()) {
          super.addEdit(ce);
          _notifyUndoHappened();
        }
        else _compoundEdits.get(0).addEdit(ce);
      } 
    }
    else throw new IllegalStateException("Improperly nested compound edits.");
  }
  
  
  public  CompoundEdit getLastCompoundEdit() { return _compoundEdits.get(0); }
  
  
  public UndoableEdit getNextUndo() { return editToBeUndone(); }
  
  
  public UndoableEdit getNextRedo() { return editToBeRedone(); }
  
  
  public  boolean addEdit(UndoableEdit e) {
    if (_compoundEditInProgress()) {
      
      return _compoundEdits.get(0).addEdit(e);
    }
    else {
      boolean result = super.addEdit(e);
      _notifyUndoHappened();
      return result;
    }
  }
  
  
  public  boolean _compoundEditInProgress() { return ! _compoundEdits.isEmpty(); }
  
  
  public  boolean canUndo() { return _compoundEditInProgress() || super.canUndo(); }
  
  
  public  String getUndoPresentationName() {
    if (_compoundEditInProgress()) return "Undo Previous Command";
    return super.getUndoPresentationName();
  }
  
  
  public  void undo() {
    endCompoundEdit();
    super.undo();
  }
  
  





















  
  
  public  void redo() {
    endCompoundEdit();
    super.redo();
  }
  
  
  private void _notifyUndoHappened() { 
    
    EventQueue.invokeLater(new Runnable() { public void run() { _notifier.undoableEditHappened(); } });
  }
  
  
  private  void endCompoundEdit() {
    Integer[] keys = _keys.toArray(new Integer[_keys.size()]);  
    if (_compoundEditInProgress()) {
      for (int key: keys) endCompoundEdit(key);
    }
  }
  
  
  public  void documentSaved() {
    endCompoundEdit();
    _savePoint = editToBeUndone(); 

  }
  
  
  public  boolean isModified() { 

    return editToBeUndone() != _savePoint; 
  }
  
  public String toString() { return "(CompoundUndoManager: " + id + ")"; }
  
  
  
  
  
}
