

package edu.rice.cs.drjava.model.repl;

import java.awt.EventQueue;

import java.util.List;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.text.EditDocumentException;


public class InteractionsScriptModel  {
  
  private volatile InteractionsModel _model;
  
  private volatile InteractionsDocument _doc;
  
  private volatile List<String> _interactions;
  
  private volatile int _currentInteraction;
  
  private volatile boolean _passedCurrent;

  
  public InteractionsScriptModel(InteractionsModel model, List<String> interactions) {
    _model = model;
    _doc = model.getDocument();
    _interactions = interactions;
    _currentInteraction = -1;
    _passedCurrent = false;
  }

  
  public void nextInteraction() {
    if (! hasNextInteraction()) { throw new IllegalStateException("There is no next interaction!"); }
    _currentInteraction++;
    _showCurrentInteraction();
    _passedCurrent = false;
  }
















  
  public void prevInteraction() {
    if (! hasPrevInteraction()) throw new IllegalStateException("There is no previous interaction!");

    
    if (! _passedCurrent)  _currentInteraction--;
    _showCurrentInteraction();
    _passedCurrent = false;
  }

  
  private void _showCurrentInteraction() {
    try {
      _doc.clearCurrentInteraction();
      String text = _interactions.get(_currentInteraction);
      _doc.insertText(_doc.getLength(), text, InteractionsDocument.DEFAULT_STYLE);
    }
    catch (EditDocumentException dae) {
      throw new UnexpectedException(dae);
    }
  }

  
  public void executeInteraction() {
    _passedCurrent = true;
    
    EventQueue.invokeLater(new Runnable() { public void run() { _model.interpretCurrentInteraction(); } });
  }







  
  public boolean hasNextInteraction() {
    return _currentInteraction < _interactions.size() - 1; 
  }






  
  public boolean hasPrevInteraction() {
    int index = _currentInteraction;
    if (_passedCurrent) index++; 
    return index > 0;
  }
}