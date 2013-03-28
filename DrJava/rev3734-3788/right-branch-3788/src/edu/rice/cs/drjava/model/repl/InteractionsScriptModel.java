

package edu.rice.cs.drjava.model.repl;

import java.io.Serializable;

import java.util.List;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.text.EditDocumentException;


public class InteractionsScriptModel implements Serializable {
  
  private InteractionsModel _model;
  
  private InteractionsDocument _doc;
  
  private List<String> _interactions;
  
  private int _currentInteraction;
  
  private boolean _passedCurrent;

  
  public InteractionsScriptModel(InteractionsModel model, List<String> interactions) {
    _model = model;
    _doc = model.getDocument();
    _interactions = interactions;
    _currentInteraction = -1;
    _passedCurrent = false;
  }

  
  public void nextInteraction() {
    if (!hasNextInteraction()) {
      throw new IllegalStateException("There is no next interaction!");
    }
    _currentInteraction++;
    _showCurrentInteraction();
    _passedCurrent = false;
  }

  

  
  public void prevInteraction() {
    if (!hasPrevInteraction()) {
      throw new IllegalStateException("There is no previous interaction!");
    }
    
    if (!_passedCurrent) {
      _currentInteraction--;
    }
    _showCurrentInteraction();
    _passedCurrent = false;
  }

  
  protected void _showCurrentInteraction() {
    try {
      _doc.clearCurrentInteraction();
      String text = _interactions.get(_currentInteraction);
      _doc.insertText(_doc.getLength(), text, _doc.DEFAULT_STYLE);
    }
    catch (EditDocumentException dae) {
      throw new UnexpectedException(dae);
    }
  }

  
  public void executeInteraction() {
    _model.interpretCurrentInteraction();
    _passedCurrent = true;
  }

  
  public void closeScript() {
    
    _currentInteraction = -1;
    _passedCurrent = false;
  }

  
  public boolean hasNextInteraction() {
    return _currentInteraction < _interactions.size() - 1;
  }

  

  
  public boolean hasPrevInteraction() {
    int index = _currentInteraction;
    if (_passedCurrent) {
      
      index++;
    }
    return index > 0;
  }
}