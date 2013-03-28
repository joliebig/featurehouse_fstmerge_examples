

package edu.rice.cs.drjava.model.definitions;

import javax.swing.undo.*;
import java.util.LinkedList;

import edu.rice.cs.drjava.model.GlobalEventNotifier;
import edu.rice.cs.util.swing.Utilities;


public class CompoundUndoManager extends UndoManager {
  
  private static int counter = 0;
  
  private int id;
  
  
  private LinkedList<CompoundEdit> _compoundEdits;
  
  
  private LinkedList<Integer> _keys;
  
  
  private int _nextKey;
  
  
  private UndoableEdit _savePoint;
  
  
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
  
  
  public synchronized int startCompoundEdit() {
    _compoundEdits.add(0, new CompoundEdit());
    _keys.add(0, new Integer(_nextKey));
    if (_nextKey < Integer.MAX_VALUE) _nextKey++;
    else _nextKey = Integer.MIN_VALUE;
    return _keys.get(0).intValue();
  }
  
  
  public synchronized void endLastCompoundEdit() {
    if (_keys.size() == 0) return;
    
    
    endCompoundEdit(_keys.get(0).intValue());
  }
  
  
  public synchronized void endCompoundEdit(int key) {
    if (_keys.size() > 0) {
      if (_keys.get(0).intValue() == key) {
        CompoundEdit compoundEdit = _compoundEdits.remove(0);
        compoundEdit.end();
        
        if (compoundEdit.canUndo()) {
          if (!_compoundEditInProgress()) {
            super.addEdit(compoundEdit);
            
            
            
            _notifyUndoHappened();
          }
          else _compoundEdits.get(0).addEdit(compoundEdit);
        }
        _keys.remove(0);
        
        
      }
      else throw new IllegalStateException("Improperly nested compound edits.");
    }
  }
  
  
  public synchronized CompoundEdit getLastCompoundEdit() { return _compoundEdits.get(0); }
  
  
  public UndoableEdit getNextUndo() { return editToBeUndone(); }
  
  
  public UndoableEdit getNextRedo() { return editToBeRedone(); }
  
  
  public synchronized boolean addEdit(UndoableEdit e) {
    if (_compoundEditInProgress()) {
      
      return _compoundEdits.get(0).addEdit(e);
    }
    else {
      boolean result = super.addEdit(e);
      _notifyUndoHappened();
      return result;
    }
  }
  
  
  public boolean _compoundEditInProgress() { return !_compoundEdits.isEmpty(); }
  
  
  public boolean canUndo() {
    return _compoundEditInProgress() || super.canUndo();
  }
  
  
  public String getUndoPresentationName() {
    if (_compoundEditInProgress()) return "Undo Previous Command";
    return super.getUndoPresentationName();
  }
  
  
  public void undo() {
    endCompoundEdit();
    super.undo();
  }
  
  
  public synchronized void undo(int key) {
    if (_keys.get(0).intValue() == key) {
      CompoundEdit compoundEdit = _compoundEdits.get(0);
      _compoundEdits.remove(0);
      _keys.remove(0);
      
      compoundEdit.end();
      compoundEdit.undo();
      compoundEdit.die();
    }
    else throw new IllegalArgumentException("Bad undo key " + key + "!");
  }
  
  
  public void redo() {
    endCompoundEdit();  
    super.redo();
  }
  
  
  private void _notifyUndoHappened() { 
    Utilities.invokeLater(new Runnable() { public void run() { _notifier.undoableEditHappened(); } });
  }
  
  
  private synchronized void endCompoundEdit() {
    if (_compoundEditInProgress()) {
      while (_keys.size() > 0) {
        endCompoundEdit(_keys.get(0).intValue());
      }
    }
  }
  
  
  public void documentSaved() {
    endCompoundEdit();
    _savePoint = editToBeUndone(); 

  }
  
  
  public boolean isModified() { 

    return editToBeUndone() != _savePoint; 
  }
  
  public String toString() { return "(CompoundUndoManager: " + id + ")"; }
  
  
  
  
  
}
