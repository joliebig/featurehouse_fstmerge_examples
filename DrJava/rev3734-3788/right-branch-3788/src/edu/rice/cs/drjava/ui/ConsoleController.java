

package edu.rice.cs.drjava.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;
import java.awt.Event;
import java.io.Serializable;

import edu.rice.cs.drjava.model.repl.*;
import edu.rice.cs.util.text.ConsoleDocument;
import edu.rice.cs.util.swing.Utilities;


public class ConsoleController extends AbstractConsoleController implements Serializable {
  protected ConsoleDocument _doc;

  
  private Object _inputWaitObject = new Object();

  
  private boolean _blockedForConsoleInput;

  public ConsoleController(final ConsoleDocument doc, InteractionsDJDocument adapter) {
    super(adapter, new InteractionsPane("CONSOLE_KEYMAP", adapter) {
      public int getPromptPos() { return doc.getPromptPos(); }
    });
    _doc = doc;
    _blockedForConsoleInput = false;
    _pane.setEditable(false);


    _init();
  }

  
  public ConsoleDocument getConsoleDoc() { return _doc; }

  
  public InputListener getInputListener() { return _inputListener; }

  protected void _setupModel() {
    _adapter.addDocumentListener(new CaretUpdateListener());
    _doc.setBeep(_pane.getBeep());
  }

  
  protected InputListener _inputListener = new InputListener() {
    public String getConsoleInput() {


      Utilities.invokeAndWait(new Runnable() {
        public void run() { _pane.setEditable(true); }
      });
      
      _waitForInput();
      String s = _doc.getCurrentInput();
      _doc.disablePrompt();
      return s;
    }
  };

  
  Object getInputWaitObject() { return _inputWaitObject; }

  
  protected void _waitForInput() {
    synchronized(_inputWaitObject) {
      try {
        _blockedForConsoleInput = true;
        while (_blockedForConsoleInput) _inputWaitObject.wait();
      }
      catch (InterruptedException ie) { 
        
      }
    }
  }

  
  protected void _setupView() {
    super._setupView();

    
    int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

    _pane.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                                enterAction);
    _pane.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
                                                       Event.SHIFT_MASK),
                                newLineAction);

    _pane.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_B, mask),
                                clearCurrentAction);

    
    
    
    
    _pane.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_KP_LEFT, 0),
                                moveLeftAction);
    _pane.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0),
                                moveLeftAction);

    _pane.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_KP_RIGHT, 0),
                                moveRightAction);
    _pane.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0),
                                moveRightAction);

    _pane.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_KP_UP, 0),
                                moveUpDownAction);
    _pane.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
                                moveUpDownAction);
    _pane.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_KP_DOWN, 0),
                                moveUpDownAction);
    _pane.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
                                moveUpDownAction);
  }

  AbstractAction enterAction = new EnterAction();
  private class EnterAction extends AbstractAction implements Serializable {
    public void actionPerformed(ActionEvent e) {
      synchronized(_inputWaitObject) {
        if (_blockedForConsoleInput) {
          _pane.setEditable(false);
          _pane.getCaret().setVisible(false);
          _doc.insertNewLine(_doc.getLength());
          _blockedForConsoleInput = false; 
          _inputWaitObject.notify();  
        }
      }
    }
  }

  
  AbstractAction moveLeftAction = new LeftAction();
  private class LeftAction extends AbstractAction implements Serializable {
    public void actionPerformed(ActionEvent e) {
      int position = _pane.getCaretPosition();
      if (position < _doc.getPromptPos()) moveToPrompt();
      else if (position == _doc.getPromptPos())_pane.getBeep().run();
      else 
        _pane.setCaretPosition(position - 1);
    }
  }

  
  AbstractAction moveRightAction = new RightAction();
  
  private class RightAction extends AbstractAction implements Serializable {
    public void actionPerformed(ActionEvent e) {
      int position = _pane.getCaretPosition();
      if (position < _doc.getPromptPos()) moveToEnd();
      else if (position >= _doc.getLength()) _pane.getBeep().run();
      else 
        _pane.setCaretPosition(position + 1);
    }
  }


  
  AbstractAction moveUpDownAction = new UpDownAction();
  private class UpDownAction extends AbstractAction implements Serializable {
    public void actionPerformed(ActionEvent e) {
      int position = _pane.getCaretPosition();
      if (position < _doc.getPromptPos()) moveToPrompt();
      else _pane.getBeep().run();
    }
  }
}

