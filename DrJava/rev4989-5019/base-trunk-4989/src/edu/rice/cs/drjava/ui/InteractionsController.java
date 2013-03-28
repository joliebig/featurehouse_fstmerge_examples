

package edu.rice.cs.drjava.ui;

import java.awt.Toolkit;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.RenderingHints;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.io.File;

import java.util.Vector;
import java.util.ArrayList;
import java.util.EventListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.config.OptionListener;
import edu.rice.cs.drjava.config.OptionEvent;
import edu.rice.cs.drjava.model.definitions.indent.Indenter;
import edu.rice.cs.drjava.model.repl.InputListener;
import edu.rice.cs.drjava.model.repl.InteractionsDocument;
import edu.rice.cs.drjava.model.repl.InteractionsDJDocument;
import edu.rice.cs.drjava.model.repl.InteractionsListener;
import edu.rice.cs.drjava.model.repl.InteractionsModel;

import edu.rice.cs.drjava.config.OptionConstants;

import edu.rice.cs.plt.lambda.Lambda;
import edu.rice.cs.plt.concurrent.CompletionMonitor;
import edu.rice.cs.util.text.ConsoleDocument;
import edu.rice.cs.util.UnexpectedException;

import static edu.rice.cs.plt.debug.DebugUtil.debug;



public class InteractionsController extends AbstractConsoleController {
  
  
  
  
  private static final String INPUT_ENTERED_NAME = "Input Entered";
  private static final String INSERT_NEWLINE_NAME = "Insert Newline";
  private static final String INSERT_END_OF_STREAM = "Insert End of Stream";
  
  
  public static final String INPUT_BOX_STYLE = "input.box.style";
  
  
  public static final String INPUT_BOX_SYMBOL = "[DrJava Input Box]";
  
  
  private volatile InteractionsModel _model;
  
  
  private volatile InteractionsDocument _doc;
  
  
  private volatile SimpleAttributeSet _errStyle;
  
  
  private final SimpleAttributeSet _debugStyle;
  
  
  private volatile Lambda<String, String> _insertTextCommand;
  
  
  private volatile Runnable _inputCompletionCommand;
  
  
  private final Runnable _disableCloseSystemInMenuItemCommand;
  
  
  private static final Lambda<String, String> _defaultInsertTextCommand = 
    new Lambda<String,String>() {
    public String value(String input) {
      throw new UnsupportedOperationException("Cannot insert text. There is no console input in progress");
    }
  };
  
  
  private static final Runnable _defaultInputCompletionCommand = 
    new Runnable() { public void run() {  } };
  
  
  private volatile InputBox _box;
  
  private volatile String _result;
  
  private volatile boolean _endOfStream = false;
  
  
  protected volatile InputListener _inputListener = new InputListener() {
    public String getConsoleInput() {
      if (_endOfStream) return ""; 
      final CompletionMonitor completionMonitor = new CompletionMonitor();
      _box = new InputBox(_endOfStream);  
      
      
      EventQueue.invokeLater(new Runnable() {  
        public void run() { 
          
          
          final Lambda<String,String> insertTextCommand = _box.makeInsertTextCommand();  
          
          final Runnable inputCompletionCommand = new Runnable() {  
            public void run() {
              assert EventQueue.isDispatchThread();
              
              _setConsoleInputCommands(_defaultInputCompletionCommand, _defaultInsertTextCommand);
              
              _box.disableInputs();
              _result = _box.getText();
              if (_box.wasClosedWithEnter()) {
                _result += "\n";
              }
              setEndOfStream(_box.isEndOfStream());
              
              
              _pane.setEditable(true);
              _pane.setCaretPosition(_doc.getLength()); 
              _pane.requestFocusInWindow();
              
              completionMonitor.signal();
            }
          };
          
          _box.setInputCompletionCommand(inputCompletionCommand);
          _setConsoleInputCommands(inputCompletionCommand, insertTextCommand);
          _pane.setEditable(true);
          
          
          MutableAttributeSet inputAttributes = new SimpleAttributeSet();
          
          
          StyleConstants.setComponent(inputAttributes, _box);
          
          
          _doc.insertBeforeLastPrompt(" ", InteractionsDocument.DEFAULT_STYLE);
          
          
          _interactionsDJDocument.setDocStyle(INPUT_BOX_STYLE, inputAttributes);
          
          
          _doc.insertBeforeLastPrompt(INPUT_BOX_SYMBOL, INPUT_BOX_STYLE);
          
          _doc.insertBeforeLastPrompt("\n", InteractionsDocument.DEFAULT_STYLE);
          
          _box.setVisible(true);
          EventQueue.invokeLater(new Runnable() { public void run() { _box.requestFocusInWindow(); } });
          
          _pane.setEditable(false);
        }
      });
      fireConsoleInputStarted();
      
      
      completionMonitor.attemptEnsureSignaled();
      
      fireConsoleInputCompleted(_result);
      
      return _result;
    }
  };
  
  private ArrayList<ConsoleStateListener> _consoleStateListeners;
  
  private InteractionsListener _viewListener = new InteractionsListener() {
    public void interactionStarted() { }
    public void interactionEnded() { _pane.requestFocusInWindow(); }    
    public void interactionErrorOccurred(int offset, int length) { }    
    
    public void interpreterResetting() {
      assert EventQueue.isDispatchThread(); 
      _interactionsDJDocument.clearColoring();
      _endOfStream = false;
    }
    
    public void interpreterReady(File wd) { }
    public void interpreterResetFailed(Throwable t) { }
    public void interpreterExited(int status) { }
    public void interpreterChanged(boolean inProgress) { }
    public void interactionIncomplete() { }
  };
  
  
  public InteractionsController(final InteractionsModel model,
                                InteractionsDJDocument adapter,
                                Runnable disableCloseSystemInMenuItemCommand) {
    this(model, adapter, new InteractionsPane(adapter) {  
      public int getPromptPos() { return model.getDocument().getPromptPos(); }
    }, disableCloseSystemInMenuItemCommand);
  }
  
  
  public InteractionsController(InteractionsModel model,
                                InteractionsDJDocument adapter,
                                InteractionsPane pane,
                                Runnable disableCloseSystemInMenuItemCommand) {
    super(adapter, pane);
    _disableCloseSystemInMenuItemCommand = disableCloseSystemInMenuItemCommand;
    DefaultEditorKit d = InteractionsPane.EDITOR_KIT;
    
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
    _model.setUpPane(pane);    
    
    _inputCompletionCommand = _defaultInputCompletionCommand;
    _insertTextCommand = _defaultInsertTextCommand;
    _consoleStateListeners = new ArrayList<ConsoleStateListener>();






    
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
    for(ConsoleStateListener listener : _consoleStateListeners) { listener.consoleInputCompleted(text, this); }
  }
  
  
  public void setEndOfStream(boolean tf) {
    _endOfStream = tf;
    if (_box!=null) { _box.setEndOfStream(tf); }
    if (tf) { _disableCloseSystemInMenuItemCommand.run(); }
  }
  
  
  
  public InputListener getInputListener() { return _inputListener; }
  
  
  public void interruptConsoleInput() { EventQueue.invokeLater(_inputCompletionCommand); }
  
  
  public void insertConsoleText(String input) { _insertTextCommand.value(input); }
  
  
  public InteractionsModel getInteractionsModel() {  return _model; }
  
  
  public ConsoleDocument getConsoleDoc() { return _doc; }
  
  
  public InteractionsDocument getDocument() { return _doc; }
  
  
  protected void _addDocumentStyles() {
    
    super._addDocumentStyles();
    
    
    _errStyle.addAttributes(_defaultStyle);
    _errStyle.addAttribute(StyleConstants.Foreground, 
                           DrJava.getConfig().getSetting(OptionConstants.INTERACTIONS_ERROR_COLOR));
    _errStyle.addAttribute(StyleConstants.Bold, Boolean.TRUE);
    _interactionsDJDocument.setDocStyle(InteractionsDocument.ERROR_STYLE, _errStyle);
    DrJava.getConfig().addOptionListener(OptionConstants.INTERACTIONS_ERROR_COLOR, new OptionListener<Color>() {
      public void optionChanged(OptionEvent<Color> oe) {
        _errStyle.addAttribute(StyleConstants.Foreground, oe.value);
      }
    });
    
    
    _debugStyle.addAttributes(_defaultStyle);
    _debugStyle.addAttribute(StyleConstants.Foreground, 
                             DrJava.getConfig().getSetting(OptionConstants.DEBUG_MESSAGE_COLOR));
    _debugStyle.addAttribute(StyleConstants.Bold, Boolean.TRUE);
    _interactionsDJDocument.setDocStyle(InteractionsDocument.DEBUGGER_STYLE, _debugStyle);
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
  
  
  protected void _setupModel() { _doc.setBeep(_pane.getBeep()); }
  
  
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
    DrJava.getConfig().addOptionListener(OptionConstants.KEY_PREVIOUS_WORD, new OptionListener<Vector<KeyStroke>>() {
      public void optionChanged(OptionEvent<Vector<KeyStroke>> oe) {
        _pane.addActionForKeyStroke(DrJava.getConfig().getSetting(OptionConstants.KEY_PREVIOUS_WORD), prevWordAction);
      }
    });
    
    _pane.addActionForKeyStroke(DrJava.getConfig().getSetting(OptionConstants.KEY_NEXT_WORD), nextWordAction);
    DrJava.getConfig().addOptionListener(OptionConstants.KEY_NEXT_WORD, new OptionListener<Vector<KeyStroke>>() {
      public void optionChanged(OptionEvent<Vector<KeyStroke>> oe) {
        _pane.addActionForKeyStroke(DrJava.getConfig().getSetting(OptionConstants.KEY_NEXT_WORD), nextWordAction);
      }
    });
  }
  
  
  private void _setConsoleInputCommands(Runnable inputCompletionCommand, Lambda<String,String> insertTextCommand) {
    _insertTextCommand = insertTextCommand;
    _inputCompletionCommand = inputCompletionCommand;
  }
  
  
  
  
  AbstractAction evalAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) { _model.interpretCurrentInteraction(); }
  };
  






























         
  
  AbstractAction historyPrevAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) {
      if (! _busy()) {
        if (_doc.recallPreviousInteractionInHistory()) moveToEnd();
        if (!_isCursorAfterPrompt()) moveToPrompt();
      }
    }
  };
  
  
  AbstractAction historyNextAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) {
      if (! _busy() && (_doc.recallNextInteractionInHistory() || !_isCursorAfterPrompt())) moveToPrompt(); 
    }
  };
  
  
  AbstractAction moveUpAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) {
      if (! _busy()) {
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
      if (! _busy()) {
        if (_shouldGoIntoHistory(_pane.getCaretPosition(), _interactionsDJDocument.getLength())) {
          historyNextAction.actionPerformed(e);
        } else { defaultDownAction.actionPerformed(e); }
      }
    }
  };
  
    
  private boolean _shouldGoIntoHistory(int start, int end) {
    if (_isCursorAfterPrompt() && end >= start) {
      String text = "";
      try { text = _interactionsDJDocument.getText(start, end - start); }
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
      if (! _busy()) {
        _doc.forwardSearchInteractionsInHistory();
        moveToEnd();
      }
    }
  };
  
  
  AbstractAction moveLeftAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) {
      if (! _busy()) {
        int promptPos = _doc.getPromptPos();
        int pos = _pane.getCaretPosition();
        if (pos < promptPos) moveToPrompt();
        else if (pos == promptPos) moveToEnd(); 
        else _pane.setCaretPosition(pos - 1); 
      }
    }
  };
  
  
  AbstractAction moveRightAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) {
      int pos = _pane.getCaretPosition();
      if (pos < _doc.getPromptPos()) moveToEnd();
      else if (pos >= _doc.getLength()) moveToPrompt(); 
      else {
        _pane.setCaretPosition(pos + 1); 

      }
    }
  };
  
  
  AbstractAction prevWordAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) {
      int position = _pane.getCaretPosition();
      int promptPos = _doc.getPromptPos();
      if (position < promptPos) moveToPrompt();
      else if (position == promptPos) moveToEnd(); 
      else _pane.getActionMap().get(DefaultEditorKit.previousWordAction).actionPerformed(e);
    }
  };
  
  
  AbstractAction nextWordAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) {
      int position = _pane.getCaretPosition();
      int promptPos = _doc.getPromptPos();
      if (position < promptPos) moveToEnd();
      else if (position >= _doc.getLength()) moveToPrompt(); 
      else _pane.getActionMap().get(DefaultEditorKit.nextWordAction).actionPerformed(e);
    }
  };
  
  
  AbstractAction indentKeyActionTab = new AbstractAction() {
    public void actionPerformed(ActionEvent e) { _pane.indent(); }
  };
   
  
  AbstractAction indentKeyActionLine = new AbstractAction() {
    public void actionPerformed(ActionEvent e) { 
      _doc.append("\n", null);  
      _pane.indent(Indenter.IndentReason.ENTER_KEY_PRESS); }
  };
 
  
  private static class InputBox extends JTextArea {
    private static final int BORDER_WIDTH = 1;
    private static final int INNER_BUFFER_WIDTH = 3;
    private static final int OUTER_BUFFER_WIDTH = 2;
    private volatile Color _bgColor = DrJava.getConfig().getSetting(OptionConstants.DEFINITIONS_BACKGROUND_COLOR);
    private volatile Color _fgColor = DrJava.getConfig().getSetting(OptionConstants.DEFINITIONS_NORMAL_COLOR);
    private volatile Color _sysInColor = DrJava.getConfig().getSetting(OptionConstants.SYSTEM_IN_COLOR);
    private volatile boolean _antiAliasText = DrJava.getConfig().getSetting(OptionConstants.TEXT_ANTIALIAS);
    private volatile boolean _endOfStream = false;
    private volatile boolean _closedWithEnter = false;
    
    public InputBox(boolean endOfStream) {
      _endOfStream = endOfStream;
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
      
      
      final Action newLineAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) { insert("\n", getCaretPosition()); }
      };
      
      final InputMap im = getInputMap(WHEN_FOCUSED);
      im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,java.awt.Event.SHIFT_MASK), INSERT_NEWLINE_NAME);
      im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,java.awt.Event.CTRL_MASK), INSERT_NEWLINE_NAME);
      
      final ActionMap am = getActionMap();
      am.put(INSERT_NEWLINE_NAME, newLineAction);
    }
    
    
    public boolean isEndOfStream() { return _endOfStream; }

    
    public void setEndOfStream(boolean tf) { _endOfStream = tf; }
    
    
    public boolean wasClosedWithEnter() { return _closedWithEnter; }
    
    private Border _createBorder() {
      Border outerouter = BorderFactory.createLineBorder(_bgColor, OUTER_BUFFER_WIDTH);
      Border outer = BorderFactory.createLineBorder(_fgColor, BORDER_WIDTH);
      Border inner = BorderFactory.createLineBorder(_bgColor, INNER_BUFFER_WIDTH);
      Border temp = BorderFactory.createCompoundBorder(outer, inner);
      return BorderFactory.createCompoundBorder(outerouter, temp);
    }
    
    
    protected void paintComponent(Graphics g) {
      if (_antiAliasText && g instanceof Graphics2D) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      }
      super.paintComponent(g);
    }
    
    
    void setInputCompletionCommand(final Runnable command) {
      final InputMap im = getInputMap(WHEN_FOCUSED);
      im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0), INPUT_ENTERED_NAME);
      for(KeyStroke k: DrJava.getConfig().getSetting(OptionConstants.KEY_CLOSE_SYSTEM_IN)) im.put(k, INSERT_END_OF_STREAM);
      
      final ActionMap am = getActionMap();
      am.put(INPUT_ENTERED_NAME, new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          _closedWithEnter = true; 
          command.run();
        }
      });

      
      am.put(INSERT_END_OF_STREAM, new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          _endOfStream = true;
          command.run();
        }
      });
    }
    
    
    Lambda<String,String> makeInsertTextCommand() {
      return new Lambda<String, String>() {
        public String value(String input) {
          insert(input, getCaretPosition());
          return input;
        }
      };
    }
    
    
    void disableInputs() {
      setEditable(false);
      
      ActionMap am = getActionMap();
      Action action;
      
      action = am.get(INPUT_ENTERED_NAME);
      if (action != null) action.setEnabled(false);
      
      action = am.get(INSERT_NEWLINE_NAME);
      if (action != null) action.setEnabled(false);
      
      getCaret().setVisible(false);
    }
  }
  
  
  public interface ConsoleStateListener extends EventListener {
    
    
    public void consoleInputStarted(InteractionsController c);
    
    
    public void consoleInputCompleted(String result, InteractionsController c);
    
  }
}
