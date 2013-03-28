

package edu.rice.cs.drjava.ui;

import java.awt.event.*;
import javax.swing.*;


import edu.rice.cs.drjava.model.repl.InteractionsScriptModel;


public class InteractionsScriptController  {
  
  private InteractionsScriptModel _model;
  
  private InteractionsScriptPane _pane;
  
  private InteractionsPane _interactionsPane;

  
  public InteractionsScriptController(InteractionsScriptModel model, Action closeAction,
                                      InteractionsPane interactionsPane) {
    _model = model;
    _closeScriptAction = closeAction;
    _interactionsPane = interactionsPane;
    _pane = new InteractionsScriptPane(4, 1);

    
    _setupAction(_prevInteractionAction, "Previous", "Insert Previous Interaction from Script");
    _pane.addButton(_prevInteractionAction);
    
    _setupAction(_nextInteractionAction, "Next", "Insert Next Interaction from Script");
    _pane.addButton(_nextInteractionAction);
    
    _setupAction(_executeInteractionAction, "Execute", "Execute Current Interaction");
    _pane.addButton(_executeInteractionAction);
    
    _setupAction(_closeScriptAction, "Close", "Close Interactions Script");
    _pane.addButton(_closeScriptAction);
    setActionsEnabled();
  }

  
  public void setActionsEnabled() {
    _nextInteractionAction.setEnabled(_model.hasNextInteraction());
    _prevInteractionAction.setEnabled(_model.hasPrevInteraction());
    _executeInteractionAction.setEnabled(true);
  }

  
  public void setActionsDisabled() {
    _nextInteractionAction.setEnabled(false);
    _prevInteractionAction.setEnabled(false);
    _executeInteractionAction.setEnabled(false);
  }

  
  public InteractionsScriptPane getPane() { return _pane; }

  
  private Action _prevInteractionAction = new AbstractAction("Previous") {
    public void actionPerformed(ActionEvent e) {
      _model.prevInteraction();
      setActionsEnabled();
      _interactionsPane.requestFocusInWindow();
    }
  };
  
  private Action _nextInteractionAction = new AbstractAction("Next") {
    public void actionPerformed(ActionEvent e) {
      _model.nextInteraction();
      setActionsEnabled();
      _interactionsPane.requestFocusInWindow();
    }
  };
  
  private Action _executeInteractionAction = new AbstractAction("Execute") {
    public void actionPerformed(ActionEvent e) {
      _model.executeInteraction();
      _interactionsPane.requestFocusInWindow();
    }
  };
  
  private Action _closeScriptAction; 

  
  protected void _setupAction(Action a, String name, String desc) {
    a.putValue(Action.DEFAULT, name);
    a.putValue(Action.SHORT_DESCRIPTION, desc);
  }
}