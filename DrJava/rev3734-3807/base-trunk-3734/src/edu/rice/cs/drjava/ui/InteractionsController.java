

package edu.rice.cs.drjava.ui;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import javax.swing.border.Border;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.DefaultStyledDocument;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.io.File;

import java.util.EventListener;
import java.util.Vector;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.config.OptionListener;
import edu.rice.cs.drjava.config.OptionEvent;

import edu.rice.cs.drjava.model.repl.InputListener;
import edu.rice.cs.drjava.model.repl.InteractionsDocument;
import edu.rice.cs.drjava.model.repl.InteractionsDJDocument;
import edu.rice.cs.drjava.model.repl.InteractionsListener;
import edu.rice.cs.drjava.model.repl.InteractionsModel;

import edu.rice.cs.util.swing.InputBox;
import edu.rice.cs.util.swing.PopupConsole;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.util.text.ConsoleDocument;
import edu.rice.cs.util.CompletionMonitor;
import edu.rice.cs.util.Lambda;
import edu.rice.cs.util.UnexpectedException;


public class InteractionsController extends AbstractConsoleController {
  
  private static final String INPUT_ENTERED_NAME = "Input Entered";
  private static final String INSERT_NEWLINE_NAME = "Insert Newline";
  
  
  private InteractionsModel _model;

  
  private InteractionsDocument _doc;

  
  private SimpleAttributeSet _errStyle;

  
  private final SimpleAttributeSet _debugStyle;

  
  private Lambda<String, String> _insertTextCommand;
  
  
  private Runnable _inputCompletionCommand;
    
  
  private Object _consoleInputCommandLock = new Object();
  
  
  private static final Lambda<String, String> _defaultInsertTextCommand = 
    new Lambda<String,String>() {
      public String apply(String input) {
        throw new UnsupportedOperationException("Cannot insert text. There is no console input in progress");
      }
    };
  
  
  private static final Runnable _defaultInputCompletionCommand = 
    new Runnable() {
      public void run() {
        
      }    
    };
  
  
  protected InputListener _inputListener = new InputListener() {
    public String getConsoleInput() {
      
      final InputBox box = new InputBox();
      final CompletionMonitor completionMonitor = new CompletionMonitor();
      
      Runnable inputCompletionCommand = new Runnable() {
        public void run() {
          
          _setConsoleInputCommands(_defaultInputCompletionCommand, _defaultInsertTextCommand);
          
          box.dissableInputs();
                    
          completionMonitor.set();
          
          
          _pane.setEditable(true);
          _pane.setCaretPosition(_doc.getLength());
          _pane.requestFocus();
        }
      };
      
      Lambda<String,String> insertTextCommand = box.makeInsertTextCommand();
      
      box.setInputCompletionCommand(inputCompletionCommand);
      
      _setConsoleInputCommands(inputCompletionCommand, insertTextCommand);
      
      
      
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {       
          
          _pane.setEditable(true);
          
          int pos = _doc.getPositionBeforePrompt();
          _doc.insertBeforeLastPrompt(" ", _doc.DEFAULT_STYLE);
           
          javax.swing.text.MutableAttributeSet inputAttributes = _pane.getInputAttributes();
          inputAttributes.removeAttributes(inputAttributes);
          StyleConstants.setComponent(inputAttributes, box);
          try {
            DefaultStyledDocument.ElementSpec[] specs = new DefaultStyledDocument.ElementSpec[]{ 
              new DefaultStyledDocument.ElementSpec(inputAttributes, DefaultStyledDocument.ElementSpec.ContentType, "[component]".toCharArray(), 0, 11)
            };
            
            _pane.getStyledDocument().insertString(pos, "[component]", inputAttributes);
          }
          catch(BadLocationException e) {
            completionMonitor.set();
            return;
          }
          finally {
            inputAttributes.removeAttributes(inputAttributes);
          }
          
          _doc.insertBeforeLastPrompt("\n", _doc.DEFAULT_STYLE);
          
          box.setVisible(true);
          box.requestFocus();

          _pane.setEditable(false);
        }
      });
      fireConsoleInputStarted();
      
      
      completionMonitor.waitOne();
            
      String text = box.getText() + "\n";
      fireConsoleInputCompleted(text);
      
      return text;
    }
  };
  
  private Vector<ConsoleStateListener> _consoleStateListeners;
  
  private InteractionsListener _viewListener = new InteractionsListener() {
    public void interactionStarted() { }
    public void interactionEnded() { }    
    public void interactionErrorOccurred(int offset, int length) { }    
    
    public void interpreterResetting() {
      Runnable command = new Runnable() { 
        public void run() { 
          _adapter.clearColoring();
          _pane.resetPrompts();
        }
      };
      Utilities.invokeLater(command);
    }
    
    public void interpreterReady(File wd) { }
    public void interpreterResetFailed(Throwable t) { }
    public void interpreterExited(int status) { }
    public void interpreterChanged(boolean inProgress) { }
    public void interactionIncomplete() { }
    public void slaveJVMUsed() { }
  };

  
  public InteractionsController(final InteractionsModel model, InteractionsDJDocument adapter) {
    this(model, adapter, 
         new InteractionsPane(adapter) { 
           public int getPromptPos() { 
             return model.getDocument().getPromptPos(); 
           }
         }); 
  }

  
  public InteractionsController(InteractionsModel model, InteractionsDJDocument adapter, 
                                InteractionsPane pane) {
    super(adapter, pane);
    DefaultEditorKit d = pane.EDITOR_KIT;
    
    for (Action a : d.getActions()) {
      if (a.getValue(Action.NAME).equals(DefaultEditorKit.upAction))  defaultUpAction = a;
      if (a.getValue(Action.NAME).equals(DefaultEditorKit.downAction)) defaultDownAction = a;
    }
    
    _model = model;
    _doc = model.getDocument();
    _errStyle = new SimpleAttributeSet();
    _debugStyle = new SimpleAttributeSet();

    _model.setInputListener(_inputListener);
    _model.addListener(_viewListener);
    
    _inputCompletionCommand = _defaultInputCompletionCommand;
    _insertTextCommand = _defaultInsertTextCommand;
    _consoleStateListeners = new Vector<ConsoleStateListener>();
    
    _init();
  }
  
  public void addConsoleStateListener(ConsoleStateListener listener) {
    _consoleStateListeners.add(listener);
  }
  
  public void removeConsoleStateListener(ConsoleStateListener listener) {
    _consoleStateListeners.remove(listener);
  }
  
  private void fireConsoleInputStarted() {
    for(ConsoleStateListener listener : _consoleStateListeners) {
      listener.consoleInputStarted(this);
    }
  }
  
  private void fireConsoleInputCompleted(String text) {
    for(ConsoleStateListener listener : _consoleStateListeners) {
      listener.consoleInputCompleted(text, this);
    }
  }
  
  
  public InputListener getInputListener() { return _inputListener; }

  
  public void interruptConsoleInput() {
    synchronized(_consoleInputCommandLock) {
      SwingUtilities.invokeLater(_inputCompletionCommand);
    }
  }
  
  
  public void insertConsoleText(String input) {
    synchronized(_consoleInputCommandLock) {
      _insertTextCommand.apply(input);
    }
  }

  
  public InteractionsModel getInteractionsModel() {  return _model; }

  
  public ConsoleDocument getConsoleDoc() { return _doc; }

  
  public InteractionsDocument getDocument() { return _doc; }

  
  protected void _addDocumentStyles() {
    
    super._addDocumentStyles();

    
    _errStyle.addAttributes(_defaultStyle);
    _errStyle.addAttribute(StyleConstants.Foreground, 
                           DrJava.getConfig().getSetting(OptionConstants.INTERACTIONS_ERROR_COLOR));
    _errStyle.addAttribute(StyleConstants.Bold, Boolean.TRUE);
    _adapter.setDocStyle(InteractionsDocument.ERROR_STYLE, _errStyle);
    DrJava.getConfig().addOptionListener(OptionConstants.INTERACTIONS_ERROR_COLOR, new OptionListener<Color>() {
      public void optionChanged(OptionEvent<Color> oe) {
        _errStyle.addAttribute(StyleConstants.Foreground, oe.value);
      }
    });

    
    _debugStyle.addAttributes(_defaultStyle);
    _debugStyle.addAttribute(StyleConstants.Foreground, 
                             DrJava.getConfig().getSetting(OptionConstants.DEBUG_MESSAGE_COLOR));
    _debugStyle.addAttribute(StyleConstants.Bold, Boolean.TRUE);
    _adapter.setDocStyle(InteractionsDocument.DEBUGGER_STYLE, _debugStyle);
    DrJava.getConfig().addOptionListener(OptionConstants.DEBUG_MESSAGE_COLOR, new OptionListener<Color>() {
      public void optionChanged(OptionEvent<Color> oe) {
        _debugStyle.addAttribute(StyleConstants.Foreground, oe.value);
      }
    });
  }

  
  protected void _updateStyles(AttributeSet newSet) {
    super._updateStyles(newSet);
    _errStyle.addAttributes(newSet);
    StyleConstants.setBold(_errStyle, true);  
    _debugStyle.addAttributes(newSet);
    StyleConstants.setBold(_debugStyle, true);  
  }

  
  protected void _setupModel() {
    _adapter.addDocumentListener(new CaretUpdateListener());
    _doc.setBeep(_pane.getBeep());
  }

  
  protected void _setupView() {
    super._setupView();

    
    int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

    
    _pane.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), evalAction);
    _pane.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, java.awt.Event.SHIFT_MASK), newLineAction);
    _pane.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_B, mask), clearCurrentAction);

    
    _pane.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_KP_UP, 0), moveUpAction);
    _pane.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), moveUpAction);
    _pane.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_UP, mask), historyPrevAction);
    _pane.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_KP_DOWN, 0), moveDownAction);
    _pane.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), moveDownAction);
    _pane.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, mask), historyNextAction);
    _pane.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), historyReverseSearchAction);
    _pane.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, java.awt.Event.SHIFT_MASK),
                                historyForwardSearchAction);


    
    
    
    
    _pane.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_KP_LEFT, 0), moveLeftAction);
    _pane.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), moveLeftAction);
    _pane.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_KP_RIGHT, 0), moveRightAction);
    _pane.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), moveRightAction);

    
    _pane.addActionForKeyStroke(DrJava.getConfig().getSetting(OptionConstants.KEY_PREVIOUS_WORD), prevWordAction);
    DrJava.getConfig().addOptionListener(OptionConstants.KEY_PREVIOUS_WORD, new OptionListener<KeyStroke>() {
      public void optionChanged(OptionEvent<KeyStroke> oe) {
        _pane.addActionForKeyStroke(DrJava.getConfig().getSetting(OptionConstants.KEY_PREVIOUS_WORD), prevWordAction);
      }
    });

    _pane.addActionForKeyStroke(DrJava.getConfig().getSetting(OptionConstants.KEY_NEXT_WORD), nextWordAction);
    DrJava.getConfig().addOptionListener(OptionConstants.KEY_NEXT_WORD, new OptionListener<KeyStroke>() {
      public void optionChanged(OptionEvent<KeyStroke> oe) {
        _pane.addActionForKeyStroke(DrJava.getConfig().getSetting(OptionConstants.KEY_NEXT_WORD), nextWordAction);
      }
    });
  }
  
  
  private void _setConsoleInputCommands(Runnable inputCompletionCommand, Lambda<String,String> insertTextCommand) {
    synchronized(_consoleInputCommandLock) {
      _insertTextCommand = insertTextCommand;
      _inputCompletionCommand = inputCompletionCommand;
    }
  }

  

  
  AbstractAction evalAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) {
      if (! _adapter.isInCommentBlock()) {
        Thread command = new Thread("Evaluating Interaction") { 
          public void run() { _model.interpretCurrentInteraction(); }
        };
        command.start();
      }
      else {
        _model.addNewLine();
        _model.interactionContinues();
      }
    }
  };

  
  AbstractAction historyPrevAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) {
      if (!_busy()) {
        if (_doc.recallPreviousInteractionInHistory()) moveToEnd();
        if (!_isCursorAfterPrompt()) moveToPrompt();
      }
    }
  };

  
  AbstractAction historyNextAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) {
      if (!_busy()) {
        if (_doc.recallNextInteractionInHistory() || !_isCursorAfterPrompt()) moveToPrompt();
      }
    }
  };
  
  
  AbstractAction moveUpAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) {
      if (!_busy()) {
        if (_shouldGoIntoHistory(_doc.getPromptPos(), _pane.getCaretPosition())) 
          historyPrevAction.actionPerformed(e);
        else {
          defaultUpAction.actionPerformed(e);
          if (! _isCursorAfterPrompt()) moveToPrompt();
        }
      }
    }
  };
    
  
  AbstractAction moveDownAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) {
      if (!_busy()) {
        if (_shouldGoIntoHistory(_pane.getCaretPosition(), _adapter.getLength()))
          historyNextAction.actionPerformed(e);
        else defaultDownAction.actionPerformed(e);
      }
    }
  };
  
    
  private boolean _shouldGoIntoHistory(int start, int end) {
    if (_isCursorAfterPrompt() && end >= start) {
      String text = "";
      try { text = _adapter.getText(start, end - start); }
      catch(BadLocationException ble) {
        throw new UnexpectedException(ble); 
      }
      if (text.indexOf("\n") != -1) return false;
    }
    return true;
  }
  
  private boolean _isCursorAfterPrompt() { return _pane.getCaretPosition() >= _doc.getPromptPos(); }
  
  Action defaultUpAction;
  Action defaultDownAction;
  
  
  AbstractAction historyReverseSearchAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) {
      if (!_busy()) {
        _doc.reverseSearchInteractionsInHistory();
        moveToEnd();
      }
    }
  };

  
  AbstractAction historyForwardSearchAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) {
      if (!_busy()) {
        _doc.forwardSearchInteractionsInHistory();
        moveToEnd();
      }
    }
  };

  
  AbstractAction moveLeftAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) {
      if (!_busy()) {
        int position = _pane.getCaretPosition();
        if (position < _doc.getPromptPos()) {
          moveToPrompt();
        }
        else if (position == _doc.getPromptPos()) {
          
          moveToEnd();
        }
        else { 
          _pane.setCaretPosition(position - 1);
        }
      }
    }
  };

  
  AbstractAction moveRightAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) {
      int position = _pane.getCaretPosition();
      if (position < _doc.getPromptPos()) {
        moveToEnd();
      }
      else if (position >= _doc.getLength()) {
        
        moveToPrompt();
      }
      else { 
        _pane.setCaretPosition(position + 1);
      }
    }
  };

  
  AbstractAction prevWordAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) {
      int position = _pane.getCaretPosition();
      int promptPos = _doc.getPromptPos();
      if (position < promptPos) {
        moveToPrompt();
      }
      else if (position == promptPos) {
        
        moveToEnd();
      }
     else {
        _pane.getActionMap().get(DefaultEditorKit.previousWordAction).actionPerformed(e);
      }
    }
  };

  
  AbstractAction nextWordAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) {
      int position = _pane.getCaretPosition();
      int promptPos = _doc.getPromptPos();
      if (position < promptPos) {
        moveToEnd();
      }
      else if (position >= _doc.getLength()) {
        
        moveToPrompt();
      }
      else {
        _pane.getActionMap().get(DefaultEditorKit.nextWordAction).actionPerformed(e);
      }
    }
  };
  

  
  private static class InputBox extends JTextArea {
    private static final int BORDER_WIDTH = 1;
    private static final int INNER_BUFFER_WIDTH = 3;
    private static final int OUTER_BUFFER_WIDTH = 2;
    private Color _bgColor = DrJava.getConfig().getSetting(OptionConstants.DEFINITIONS_BACKGROUND_COLOR);
    private Color _fgColor = DrJava.getConfig().getSetting(OptionConstants.DEFINITIONS_NORMAL_COLOR);
    private Color _sysInColor = DrJava.getConfig().getSetting(OptionConstants.SYSTEM_IN_COLOR);
    private boolean _antiAliasText = DrJava.getConfig().getSetting(OptionConstants.TEXT_ANTIALIAS);
    
    public InputBox() {
      setForeground(_sysInColor);
      setBackground(_bgColor);
      setCaretColor(_fgColor);
      setBorder(_createBorder());
      setLineWrap(true);
      
      DrJava.getConfig().addOptionListener(OptionConstants.DEFINITIONS_NORMAL_COLOR,
                                           new OptionListener<Color>() {
        public void optionChanged(OptionEvent<Color> oe) {
          _fgColor = oe.value;
          setBorder(_createBorder());
          setCaretColor(oe.value);
        }
      });
      DrJava.getConfig().addOptionListener(OptionConstants.DEFINITIONS_BACKGROUND_COLOR,
                                           new OptionListener<Color>() {
        public void optionChanged(OptionEvent<Color> oe) {
          _bgColor = oe.value;
          setBorder(_createBorder());
          setBackground(oe.value);
        }
      });
      DrJava.getConfig().addOptionListener(OptionConstants.SYSTEM_IN_COLOR,
                                           new OptionListener<Color>() {
        public void optionChanged(OptionEvent<Color> oe) {
          _sysInColor = oe.value;
          setForeground(oe.value);
        }
      });
      DrJava.getConfig().addOptionListener(OptionConstants.TEXT_ANTIALIAS,
                                           new OptionListener<Boolean>() {
        public void optionChanged(OptionEvent<Boolean> oce) {
          _antiAliasText = oce.value.booleanValue();
          InputBox.this.repaint();
        }
      });
      
      
      Action newLineAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
           insert("\n", getCaretPosition());
        }
      };
      
      InputMap im = getInputMap(WHEN_FOCUSED);
      im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,java.awt.Event.SHIFT_MASK), INSERT_NEWLINE_NAME);
      
      ActionMap am = getActionMap();
      am.put(INSERT_NEWLINE_NAME, newLineAction);
       
    }
        
    private Border _createBorder() {
      Border outerouter = BorderFactory.createLineBorder(_bgColor, OUTER_BUFFER_WIDTH);
      Border outer = BorderFactory.createLineBorder(_fgColor, BORDER_WIDTH);
      Border inner = BorderFactory.createLineBorder(_bgColor, INNER_BUFFER_WIDTH);
      Border temp = BorderFactory.createCompoundBorder(outer, inner);
      return BorderFactory.createCompoundBorder(outerouter, temp);
    }
    
    protected void paintComponent(Graphics g) {
      if (_antiAliasText && g instanceof Graphics2D) {
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      }
      super.paintComponent(g);
    }
    
    
    public void setInputCompletionCommand(final Runnable command) {
      InputMap im = getInputMap(WHEN_FOCUSED);
      im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0), INPUT_ENTERED_NAME);
      
      ActionMap am = getActionMap();
      am.put(INPUT_ENTERED_NAME, new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          command.run();
        }
      });
    }
    
    
    public Lambda<String,String> makeInsertTextCommand() {
      return new Lambda<String, String>() {
        public String apply(String input) {
          insert(input, getCaretPosition());
          return input;
        }
      };
    }
    
    
    public void dissableInputs() {
      setEditable(false);
      
      ActionMap am = getActionMap();
      Action action;
      
      action = am.get(INPUT_ENTERED_NAME);
      if (action != null) {
        action.setEnabled(false);
      }
      
      action = am.get(INSERT_NEWLINE_NAME);
      if (action != null) {
        action.setEnabled(false);
      }
      
      getCaret().setVisible(false);
    }
  }
  
  
  public interface ConsoleStateListener extends EventListener {
    
    
    public void consoleInputStarted(InteractionsController c);
    
    
    public void consoleInputCompleted(String result, InteractionsController c);
  
  }
}
