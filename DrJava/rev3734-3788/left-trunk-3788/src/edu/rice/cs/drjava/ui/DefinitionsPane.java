

package edu.rice.cs.drjava.ui;

import javax.swing.*;
import javax.swing.undo.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.LinkedList;


import edu.rice.cs.util.Pair;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.OperationCanceledException;
import edu.rice.cs.util.swing.HighlightManager;
import edu.rice.cs.util.text.SwingDocument;
import edu.rice.cs.drjava.model.*;
import edu.rice.cs.drjava.model.definitions.CompoundUndoManager;
import edu.rice.cs.drjava.model.definitions.DefinitionsEditorKit;
import edu.rice.cs.drjava.model.definitions.NoSuchDocumentException;
import edu.rice.cs.drjava.model.definitions.indent.Indenter;
import edu.rice.cs.drjava.model.definitions.reducedmodel.ReducedModelState;
import edu.rice.cs.drjava.config.*;
import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.CodeStatus;
import edu.rice.cs.drjava.model.debug.Breakpoint;


public class DefinitionsPane extends AbstractDJPane implements Finalizable<DefinitionsPane> {

  
  private static DefinitionsEditorKit EDITOR_KIT;
    
  
  private MainFrame _mainFrame;
  
  private final OpenDefinitionsDocument _doc;
  
  private UndoAction _undoAction;
  private RedoAction _redoAction;
  private boolean testVariable;   

  
  
  private boolean _hasWarnedAboutModified = false;




  
  private boolean _antiAliasText = false;

  
  private HighlightManager.HighlightInfo _errorHighlightTag = null;

  
  static DefaultHighlighter.DefaultHighlightPainter BREAKPOINT_PAINTER =
    new DefaultHighlighter.DefaultHighlightPainter(DrJava.getConfig().getSetting(DEBUG_BREAKPOINT_COLOR));

  
  static DefaultHighlighter.DefaultHighlightPainter DISABLED_BREAKPOINT_PAINTER =
    new DefaultHighlighter.DefaultHighlightPainter(DrJava.getConfig().getSetting(DEBUG_BREAKPOINT_DISABLED_COLOR));

  
  static DefaultHighlighter.DefaultHighlightPainter THREAD_PAINTER =
    new DefaultHighlighter.DefaultHighlightPainter(DrJava.getConfig().getSetting(DEBUG_THREAD_COLOR));

  
  public static final String INDENT_KEYMAP_NAME = "INDENT_KEYMAP";

  
  protected void _updateMatchHighlight() {
    int to = getCaretPosition();
    int from = _doc.balanceBackward(); 
    if (from > -1) {
      
      from = to - from;
      _addHighlight(from, to);
      
      
      String matchText = _matchText(from);
      
      if (matchText != null) _mainFrame.updateFileTitle("Matches: " + matchText);
      else _mainFrame.updateFileTitle();
    }
    
    
    else {
      
      from = to;

      to = _doc.balanceForward();
      if (to > -1) {
        to = to + from;
        _addHighlight(from - 1, to);

      }
      _mainFrame.updateFileTitle();         
    }
  }
  
  
  private String _matchText(int braceIndex) {
    DJDocument doc = _doc;
    String docText;
    docText = doc.getText();
   
    if (docText.charAt(braceIndex) == '{') {
      Character charBefore = null;
      int charBeforeIndex = braceIndex-1;
      boolean previousLine = false;
      
      if (charBeforeIndex != -1) charBefore = docText.charAt(charBeforeIndex);
      
      charBeforeIndex--;
      
      while (charBeforeIndex >= 0 && (charBefore == '\n' || charBefore == ' ')) {
        charBefore = docText.charAt(charBeforeIndex);
        if (!previousLine &&  charBefore != '\n' && charBefore != ' ') charBeforeIndex = braceIndex-1;
        if (charBefore == '\n')  previousLine = true;
        charBeforeIndex--;
      }
      
      StringBuffer returnText = new StringBuffer(docText.substring(0, charBeforeIndex+2));          
      if (previousLine) returnText.append("...");
      returnText.append("{");
      
      int lastNewLineIndex = returnText.lastIndexOf("\n");
      return returnText.toString().substring(lastNewLineIndex+1);
    }
    else 
      return null;     
  }  
    
  
  private class MatchColorOptionListener implements OptionListener<Color> {
    public void optionChanged(OptionEvent<Color> oce) {
      MATCH_PAINTER = new DefaultHighlighter.DefaultHighlightPainter(oce.value);
      if (_matchHighlight != null) {
        int start = _matchHighlight.getStartOffset();
        int end = _matchHighlight.getEndOffset();
        _matchHighlight.remove();
        _addHighlight(start, end);
      }
    }
  }

  
  private class ErrorColorOptionListener implements OptionListener<Color> {
    public void optionChanged(OptionEvent<Color> oce) {
      ERROR_PAINTER = new DefaultHighlighter.DefaultHighlightPainter(oce.value);
      if (_errorHighlightTag != null) {
        int start = _errorHighlightTag.getStartOffset();
        int end = _errorHighlightTag.getEndOffset();
        _errorHighlightTag.remove();
        addErrorHighlight(start, end);
      }
    }
  }

  
  private static class BreakpointColorOptionListener implements OptionListener<Color> {
    public void optionChanged(OptionEvent<Color> oce) {
      BREAKPOINT_PAINTER = new DefaultHighlighter.DefaultHighlightPainter(oce.value);
    }
  }

  
  private static class DisabledBreakpointColorOptionListener implements OptionListener<Color> {
    public void optionChanged(OptionEvent<Color> oce) {
      DISABLED_BREAKPOINT_PAINTER = new DefaultHighlighter.DefaultHighlightPainter(oce.value);
    }
  }

  
  private static class ThreadColorOptionListener implements OptionListener<Color> {
    public void optionChanged(OptionEvent<Color> oce) {
      THREAD_PAINTER = new DefaultHighlighter.DefaultHighlightPainter(oce.value);
    }
  }

  
  private class AntiAliasOptionListener implements OptionListener<Boolean> {
    public void optionChanged(OptionEvent<Boolean> oce) {
      _antiAliasText = oce.value.booleanValue();
      DefinitionsPane.this.repaint();
    }
  }

  
  private UndoableEditListener _undoListener = new UndoableEditListener() {
    
    
    public void undoableEditHappened(UndoableEditEvent e) {
      UndoWithPosition undo = new UndoWithPosition(e.getEdit(), getCaretPosition());
      if (!_inCompoundEdit) {
        CompoundUndoManager undoMan = _doc.getUndoManager();
        _inCompoundEdit = true;
        _compoundEditKey = undoMan.startCompoundEdit();
        getUndoAction().updateUndoState();
        getRedoAction().updateRedoState();
      }
      _doc.getUndoManager().addEdit(undo);
      getRedoAction().setEnabled(false);
    }
  };

  
  private JMenuItem _toggleBreakpointMenuItem;






  
  private JPopupMenu _popMenu;

  
  private PopupMenuMouseAdapter _popupMenuMA;

  
  private ErrorCaretListener _errorListener;

  private ActionListener _setSizeListener = null;

  
  private class IndentKeyActionTab extends AbstractAction {
    
    
    public void actionPerformed(ActionEvent e) {
      
      
      indent();
    }
  }

  
  private class IndentKeyAction extends AbstractAction {
    
    
    private final String _key;

    
    private final Action _defaultAction;

    
    private final boolean _indentNonCode;

    
    IndentKeyAction(String key, Action defaultAction) {
      this(key, defaultAction, false);
    }

    
    IndentKeyAction(String key, Action defaultAction, boolean indentNonCode) {
      _key = key;
      _defaultAction = defaultAction;
      _indentNonCode = indentNonCode;
    }

    
    protected int getIndentReason() { return Indenter.OTHER; }

    
    public void actionPerformed(ActionEvent e) {
      _defaultAction.actionPerformed(e);
      
      
      
      _doc.setCurrentLocation(getCaretPosition());
      ReducedModelState state = _doc.getStateAtCurrent();
      if (state.equals(ReducedModelState.FREE) || _indentNonCode) indent(getIndentReason());
    }
  }

  
  private Action _indentKeyActionTab = new IndentKeyActionTab();

  
  private Action _indentKeyActionLine =
    new IndentKeyAction("\n", (Action) getActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0)),
                        true  ) {
    
    protected int getIndentReason() {
      return Indenter.ENTER_KEY_PRESS;
    }
  };

  
  private Action _indentKeyActionSquiggly = new IndentKeyAction("}", getKeymap().getDefaultAction());
  private Action _indentKeyActionOpenSquiggly = new IndentKeyAction("{", getKeymap().getDefaultAction());
  private Action _indentKeyActionColon = new IndentKeyAction(":", getKeymap().getDefaultAction());

  
  private boolean _inCompoundEdit = false;
  private int _compoundEditKey;


  
  Keymap ourMap;
  
  
  public DefinitionsPane(MainFrame mf, final OpenDefinitionsDocument doc) {
    super(new SwingDocument());
    
    _mainFrame = mf;
    
    addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {  
        _mainFrame.getModel().getDocumentNavigator().requestSelectionUpdate(doc);
      }
      public void focusLost(FocusEvent e) {  }
    });
    
    _doc = doc;  
    
    
    
    _selStart = _doc.getInitialSelectionStart();
    _selEnd = _doc.getInitialSelectionEnd();
    _savedVScroll = _doc.getInitialVerticalScroll();
    _savedHScroll = _doc.getInitialHorizontalScroll();
    
    
    _resetUndo();
    
    
    Font mainFont = DrJava.getConfig().getSetting(FONT_MAIN);
    setFont(mainFont);

    
    setEditable(true);
    
    
    ourMap = addKeymap(INDENT_KEYMAP_NAME, getKeymap());
    ourMap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), _indentKeyActionLine);
    ourMap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), _indentKeyActionTab);
    ourMap.addActionForKeyStroke(KeyStroke.getKeyStroke('}'), _indentKeyActionSquiggly);
    ourMap.addActionForKeyStroke(KeyStroke.getKeyStroke('{'), _indentKeyActionOpenSquiggly);
    ourMap.addActionForKeyStroke(KeyStroke.getKeyStroke(':'), _indentKeyActionColon);
    setKeymap(ourMap);












  


    if (CodeStatus.DEVELOPMENT) _antiAliasText = DrJava.getConfig().getSetting(TEXT_ANTIALIAS).booleanValue();

    OptionListener<Color> temp;
    Pair<Option<Color>, OptionListener<Color>> pair;
      
    
    
    temp = new ForegroundColorListener(this);
    pair = new Pair<Option<Color>, OptionListener<Color>>(OptionConstants.DEFINITIONS_NORMAL_COLOR,temp);
    _colorOptionListeners.add(pair);
    
    temp = new BackgroundColorListener(this);
    pair = new Pair<Option<Color>, OptionListener<Color>>(OptionConstants.DEFINITIONS_BACKGROUND_COLOR,temp);
    _colorOptionListeners.add(pair);

    
    temp = new MatchColorOptionListener();
    pair = new Pair<Option<Color>, OptionListener<Color>>(OptionConstants.DEFINITIONS_MATCH_COLOR, temp);
    _colorOptionListeners.add(pair);
    DrJava.getConfig().addOptionListener( OptionConstants.DEFINITIONS_MATCH_COLOR, temp);
    
    temp = new ErrorColorOptionListener();
    pair = new Pair<Option<Color>, OptionListener<Color>>(OptionConstants.COMPILER_ERROR_COLOR, temp);
    _colorOptionListeners.add(pair);
    DrJava.getConfig().addOptionListener( OptionConstants.COMPILER_ERROR_COLOR, temp);
    
    temp = new BreakpointColorOptionListener();
    pair = new Pair<Option<Color>, OptionListener<Color>>(OptionConstants.DEBUG_BREAKPOINT_COLOR, temp);
    _colorOptionListeners.add(pair);
    DrJava.getConfig().addOptionListener( OptionConstants.DEBUG_BREAKPOINT_COLOR, temp);
    
    temp = new DisabledBreakpointColorOptionListener();
    pair = new Pair<Option<Color>, OptionListener<Color>>(OptionConstants.DEBUG_BREAKPOINT_DISABLED_COLOR, temp);
    _colorOptionListeners.add(pair);
    DrJava.getConfig().addOptionListener( OptionConstants.DEBUG_BREAKPOINT_DISABLED_COLOR, temp);
    
    temp = new ThreadColorOptionListener();
    pair = new Pair<Option<Color>, OptionListener<Color>>(OptionConstants.DEBUG_THREAD_COLOR, temp);
    _colorOptionListeners.add(pair);
    DrJava.getConfig().addOptionListener( OptionConstants.DEBUG_THREAD_COLOR, temp);

    if (CodeStatus.DEVELOPMENT) {
      OptionListener<Boolean> aaTemp = new AntiAliasOptionListener();
      Pair<Option<Boolean>, OptionListener<Boolean>> aaPair = new Pair<Option<Boolean>, OptionListener<Boolean>>(OptionConstants.TEXT_ANTIALIAS, aaTemp);
      _booleanOptionListeners.add(aaPair);
      DrJava.getConfig().addOptionListener( OptionConstants.TEXT_ANTIALIAS, aaTemp);
    }

    createPopupMenu();

    
    _popupMenuMA = new PopupMenuMouseAdapter();
    this.addMouseListener( _popupMenuMA );

    _highlightManager = new HighlightManager(this);

    int rate = this.getCaret().getBlinkRate();
    
    
    this.setCaret(new DefaultCaret() {
      public void focusLost(FocusEvent e) { setVisible(false); }
    });
    this.getCaret().setBlinkRate(rate);

  }
  
  
  public void endCompoundEdit() {
    if (_inCompoundEdit) {
      CompoundUndoManager undoMan = _doc.getUndoManager();
      _inCompoundEdit = false;
      undoMan.endCompoundEdit(_compoundEditKey);
    }
  }

  
  public void processKeyEvent(KeyEvent e) {
    if (_mainFrame.getAllowKeyEvents()) {
      KeyStroke ks = KeyStroke.getKeyStrokeForEvent(e);
      Action a = KeyBindingManager.Singleton.get(ks);
      
      if ((ks != KeyStrokeOption.NULL_KEYSTROKE) && (a != null)) {

        endCompoundEdit();
        
        SwingUtilities.notifyAction(a, ks, e, e.getSource(), e.getModifiers());
        
        
        e.consume();
      }
      else {
        
        Keymap km = getKeymap();
        
        if (km.isLocallyDefined(ks) || km.isLocallyDefined(KeyStroke.getKeyStroke(ks.getKeyChar()))) {
          
          if (e.getKeyCode() == KeyEvent.VK_ENTER) endCompoundEdit();
         
          CompoundUndoManager undoMan = _doc.getUndoManager();


             
          super.processKeyEvent(e);
          
          
          endCompoundEdit();


        }
        else {
          
          
          
          
          
          if ((e.getModifiers() & InputEvent.META_MASK) != 0 
                
                && e.getKeyCode() == KeyEvent.VK_UNDEFINED) {
            

            return;
          }
                        
          
          
          
          if ((e.getModifiers() & InputEvent.SHIFT_MASK) != 0) {
            int newModifiers = e.getModifiers() & ~(InputEvent.SHIFT_MASK);
            
            KeyStroke newKs = KeyStroke.getKeyStroke(ks.getKeyCode(), newModifiers, ks.isOnKeyRelease());
            String name = KeyBindingManager.Singleton.getName(newKs);
            
            if (name != null && (name.equals("Delete Previous") || name.equals("Delete Next"))) {
              endCompoundEdit();
              
              
              SwingUtilities.notifyAction(KeyBindingManager.Singleton.get(newKs), newKs, e, e.getSource(), newModifiers);
              e.consume();
              
              return;
            }
          }
          
          
          if (e.getID() != KeyEvent.KEY_TYPED) {
            super.processKeyEvent(e);
            return;
          }
        }
        
        if ((e.getModifiers()&InputEvent.ALT_MASK) != 0) testVariable = true; 
        else testVariable = false;
        
        super.processKeyEvent(e);
      }
    }
  }

  
  public static void setEditorKit(DefinitionsEditorKit editorKit) { EDITOR_KIT = editorKit; }


  
  protected void paintComponent(Graphics g) {
    if (CodeStatus.DEVELOPMENT) {
      if (_antiAliasText && g instanceof Graphics2D) {
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                             RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      }
    }
    super.paintComponent(g);
  }

  
  private void createPopupMenu() {
    
    _popMenu = new JPopupMenu();

    _popMenu.add(_mainFrame.cutAction);
    _popMenu.add(_mainFrame.copyAction);
    _popMenu.add(_mainFrame.pasteAction);
    _popMenu.addSeparator();

    JMenuItem indentItem = new JMenuItem("Indent Line(s)");
    indentItem.addActionListener(new AbstractAction() {
      public void actionPerformed(ActionEvent ae) { indent(); }
    });
    _popMenu.add(indentItem);

    JMenuItem commentLinesItem = new JMenuItem("Comment Line(s)");
    commentLinesItem.addActionListener ( new AbstractAction() {
      public void actionPerformed( ActionEvent ae) {
        _mainFrame.hourglassOn();
        try{
          _doc.setCurrentLocation(getCaretPosition());
          _commentLines();
        }
        finally{ _mainFrame.hourglassOff(); }
      }
    });
    _popMenu.add(commentLinesItem);

    JMenuItem uncommentLinesItem = new JMenuItem("Uncomment Line(s)");
    uncommentLinesItem.addActionListener ( new AbstractAction() {
      public void actionPerformed( ActionEvent ae) {
        _doc.setCurrentLocation(getCaretPosition());
        _uncommentLines();
      }
    });
    _popMenu.add(uncommentLinesItem);

    
    _popMenu.addSeparator();
    JMenuItem gotoFileUnderCursorItem = new JMenuItem("Go to File Under Cursor");
    gotoFileUnderCursorItem.addActionListener ( new AbstractAction() {
      public void actionPerformed( ActionEvent ae) {
        _doc.setCurrentLocation(getCaretPosition());
        _mainFrame._gotoFileUnderCursor();
      }
    });
    _popMenu.add(gotoFileUnderCursorItem);
      
    if (_mainFrame.getModel().getDebugger().isAvailable()) {
      _popMenu.addSeparator();

      
      JMenuItem breakpointItem = new JMenuItem("Toggle Breakpoint");
      breakpointItem.addActionListener( new AbstractAction() {
        public void actionPerformed( ActionEvent ae ) {
          
          setCaretPosition(viewToModel(_popupMenuMA.getLastMouseClick().getPoint()));
          _mainFrame.debuggerToggleBreakpoint();
        }
      });
      _toggleBreakpointMenuItem = _popMenu.add(breakpointItem);
    }
  }

  
  private class PopupMenuMouseAdapter extends RightClickMouseAdapter {

    private MouseEvent _lastMouseClick = null;

    public void mousePressed(MouseEvent e) {
      super.mousePressed(e);

      _lastMouseClick = e;

      endCompoundEdit();

      
      if ((viewToModel(e.getPoint()) < getSelectionStart()) ||
          (viewToModel(e.getPoint()) > getSelectionEnd()) ) {
        
        setCaretPosition(viewToModel(e.getPoint()));
      }
    }

    protected void _popupAction(MouseEvent e) {
      requestFocusInWindow();
      _popMenu.show(e.getComponent(), e.getX(), e.getY());
    }

    public MouseEvent getLastMouseClick() {
      return _lastMouseClick;
    }
  }

  
  private void _commentLines() {
      _mainFrame.commentLines();

  }

  
  private void _uncommentLines() {
    _mainFrame.uncommentLines();

  }

  
  public UndoAction getUndoAction() { return  _undoAction; }

  
  public RedoAction getRedoAction() { return  _redoAction; }

  
  public OpenDefinitionsDocument getOpenDefDocument() { return _doc; }
  
  
  public DJDocument getDJDocument() { return _doc; }

  
  public HighlightManager getHighlightManager() { return _highlightManager; }

  
  public void setPositionAndScroll(int pos) {
    try {
      setCaretPosition(pos);
      scrollRectToVisible(modelToView(pos));
    }
    catch (BadLocationException ble) { throw new UnexpectedException(ble); }
  }

  
  public void setDocument(Document d) {
    if (_doc != null) {  
      if ((d == null) || (!d.equals(_doc))) {
        throw new IllegalStateException("Cannot set the document of a DefinitionsPane to a different document.");
      }
    }
    super.setDocument(d);  
  }

  public boolean checkAltKey() { 
    return testVariable;
  }
  
  
  public void addErrorCaretListener(ErrorCaretListener listener) {
    _errorListener = listener;
    addCaretListener(listener);
  }

  
  public ErrorCaretListener getErrorCaretListener() {
    return _errorListener;
  }

  
  public void addErrorHighlight(int from, int to)  {
    removeErrorHighlight();
    _errorHighlightTag = _highlightManager.addHighlight(from, to, ERROR_PAINTER);
  }

  
  public void removeErrorHighlight() {
    if (_errorHighlightTag != null) {
      _errorHighlightTag.remove();
      _errorHighlightTag = null;
    }
  }

  public boolean hasWarnedAboutModified() {
    return _hasWarnedAboutModified;
  }

  public void hasWarnedAboutModified( boolean hasWarned) {
    _hasWarnedAboutModified = hasWarned;
  }

  public void addBreakpointHighlight( Breakpoint bp ) {
    
  }

  public void removeBreakpointHighlight( Breakpoint bp) {

  }
  






  
  
  private JScrollPane _scrollPane;
  
  public void setScrollPane(JScrollPane s) { _scrollPane = s; }
  
  
  
  
  private int _savedVScroll;
  private int _savedHScroll;
  private int _position;
  private int _selStart;
  private int _selEnd;
  
  
  public void notifyInactive() {
    
    
    
    
    try {
      
      getOpenDefDocument().setCurrentLocation(getCaretPosition());
      
      
      removeErrorHighlight();
      
      _position = _doc.getCurrentLocation();
      _selStart = getSelectionStart();
      _selEnd = getSelectionEnd();

      _savedVScroll = _scrollPane.getVerticalScrollBar().getValue();
      _savedHScroll = _scrollPane.getHorizontalScrollBar().getValue();

      super.setDocument(NULL_DOCUMENT);
    }
    catch(NoSuchDocumentException e) {
      
      
      
    }
  }
    
  
  public void notifyActive() {
    super.setDocument(_doc);
    if (_doc.getUndoableEditListeners().length == 0) _resetUndo();
    
    _doc.modifyLock();
    int len = _doc.getLength();
    if (len < _position || len < _selEnd) {
      
      
      _position = len;
      _selStart = len;
      _selEnd = len;
    }
    try {
      if (_position == _selStart) {
        setCaretPosition(_selEnd);
        moveCaretPosition(_selStart);
        _doc.setCurrentLocation(_selStart);
      }
      else {
        setCaretPosition(_selStart);
        moveCaretPosition(_selEnd);
        _doc.setCurrentLocation(_selEnd);
      }
    }
    finally { _doc.modifyUnlock(); }

    _scrollPane.getVerticalScrollBar().setValue(_savedVScroll);
    _scrollPane.getHorizontalScrollBar().setValue(_savedHScroll);
    
    _scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    _scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
  }
  
  public int getVerticalScroll() {
    if (getDocument() == NULL_DOCUMENT) return _savedVScroll;
    else return _scrollPane.getVerticalScrollBar().getValue();    
  }
  
  public int getHorizontalScroll() {
    if (getDocument() == NULL_DOCUMENT) return _savedHScroll;
    else return _scrollPane.getHorizontalScrollBar().getValue();
  }
  
  public int getCurrentLine() {
    try {
      int pos = getCaretPosition();
      FontMetrics metrics = getFontMetrics(getFont());
      Rectangle startRect = modelToView(pos);
      if (startRect == null) return 1;
      
      return (new Double (startRect.getY() / metrics.getHeight()).intValue() + 1);
    } catch (BadLocationException e) {
      
      
      throw new UnexpectedException(e);
    }
  }

  public int getCurrentCol() { return _doc.getCurrentCol(); }
  
  public void setSize(int width, int height) {
    super.setSize(width, height);
    if (_setSizeListener != null) _setSizeListener.actionPerformed(null);
  }

  public void addSetSizeListener(ActionListener listener) { _setSizeListener = listener; }
  public void removeSetSizeListener() { _setSizeListener = null; }

  public void centerViewOnOffset(int offset) {
    try {
      FontMetrics metrics = getFontMetrics(getFont());
      JViewport defViewPort = _mainFrame.getDefViewport();
      double viewWidth = defViewPort.getWidth();
      double viewHeight = defViewPort.getHeight();
      
      
      Rectangle startRect;
      startRect = this.modelToView(offset);

      if (startRect != null) {
        int startRectX = (int)startRect.getX();
        int startRectY = (int)startRect.getY();
        startRect.setLocation(startRectX-(int)(viewWidth/2),
                              startRectY-(int)(viewHeight/2));
        Point endPoint = new Point(startRectX+(int)(viewWidth/2),
                                   startRectY+(int)(viewHeight/2 +
                                                    metrics.getHeight()/2));

        
        
        startRect.add(endPoint);

        this.scrollRectToVisible(startRect);
      }
      removeSetSizeListener();

      setCaretPosition(offset);
    }
    catch (BadLocationException e) { throw new UnexpectedException(e); }
  }

  public void centerViewOnLine(int lineNumber) {
    FontMetrics metrics = getFontMetrics(getFont());
    Point p = new Point(0, metrics.getHeight() * (lineNumber));
    int offset = this.viewToModel(p);
    this.centerViewOnOffset(offset);
  }

  
  public void select(int selectionStart, int selectionEnd) {
    if (selectionStart < 0) selectionStart = 0;
    if (selectionEnd < 0) selectionEnd = 0;
    setCaretPosition(selectionStart);
    moveCaretPosition(selectionEnd);  
  }

  
  public void resetUndo() {
    _doc.getUndoManager().discardAllEdits();

    _undoAction.updateUndoState();
    _redoAction.updateRedoState();
  }

  
  private void _resetUndo() {
    if (_undoAction == null) _undoAction = new UndoAction();
    if (_redoAction == null) _redoAction = new RedoAction();

    _doc.resetUndoManager();
    
    getDocument().addUndoableEditListener(_undoListener);
    _undoAction.updateUndoState();
    _redoAction.updateRedoState();
  }


  
  protected EditorKit createDefaultEditorKit() {
    
    return EDITOR_KIT;
  }
  
  
  protected boolean shouldIndent(int selStart, int selEnd) {
    if (selEnd > (selStart + 10000)) {
      Object[] options = {"Yes", "No"};
      int n = JOptionPane.showOptionDialog
        (_mainFrame,
         "Re-indenting this block may take a very long time.  Are you sure?",
         "Confirm Re-indent",
         JOptionPane.YES_NO_OPTION,
         JOptionPane.QUESTION_MESSAGE,
         null,
         options,
         options[1]);
      switch (n) {
        case JOptionPane.CANCEL_OPTION:
        case JOptionPane.CLOSED_OPTION:
        case JOptionPane.NO_OPTION:
          return false;
        default:
          return true;
      }
    }
    return true;
  }
  
  
  
  protected void indentLines(int selStart, int selEnd, int reason, ProgressMonitor pm) {
    
    
    try {
      _doc.indentLines(selStart, selEnd, reason, pm);
      
      
      endCompoundEdit();
    }
    catch (OperationCanceledException oce) {
      
      
      endCompoundEdit();
      _doc.getUndoManager().undo();
      
      throw new UnexpectedException(oce);
    }
    catch (RuntimeException e) {
      
      
      
      
      
      
      
      endCompoundEdit();
      throw e;
    }
    
    
    setCaretPosition(_doc.getCurrentLocation());
    
    
    
    
    
    
    
  }
    
  
  private List<Pair<Option<Color>, OptionListener<Color>>> _colorOptionListeners = 
    new LinkedList<Pair<Option<Color>, OptionListener<Color>>>();
    
  private List<Pair<Option<Boolean>, OptionListener<Boolean>>> _booleanOptionListeners = 
    new LinkedList<Pair<Option<Boolean>, OptionListener<Boolean>>>();
  
  
  public void close() {
    for (Pair<Option<Color>, OptionListener<Color>> p: _colorOptionListeners) {
      DrJava.getConfig().removeOptionListener(p.getFirst(), p.getSecond());
    }
    for (Pair<Option<Boolean>, OptionListener<Boolean>> p: _booleanOptionListeners) {
      DrJava.getConfig().removeOptionListener(p.getFirst(), p.getSecond());
    }
    _colorOptionListeners.clear();
    _booleanOptionListeners.clear();
    
    ourMap.removeBindings();
    removeKeymap(ourMap.getName());
    
    _popMenu.removeAll();
  }

  
  public class UndoAction extends AbstractAction {
    
    
    private UndoAction() {
      super("Undo");
      setEnabled(false);
    }

    
    public void actionPerformed(ActionEvent e) {
      try {
        
        
        
        
        
        
        
        
        
        
        _doc.getUndoManager().undo();
        _doc.updateModifiedSinceSave();
        _mainFrame.updateFileTitle();
      }
      catch (CannotUndoException ex) {
        throw new UnexpectedException(ex);
      }
      updateUndoState();
      _redoAction.updateRedoState();
    }


    
    protected void updateUndoState() {
      if (_doc.undoManagerCanUndo()) {
        setEnabled(true);
        putValue(Action.NAME, _doc.getUndoManager().getUndoPresentationName());
      }
      else {
        setEnabled(false);
        putValue(Action.NAME, "Undo");
      }
    }
  }

  
  public class RedoAction extends AbstractAction {

    
    private RedoAction() {
      super("Redo");
      setEnabled(false);
    }

    
    public void actionPerformed(ActionEvent e) {
      try {
        
        
        
        
        
        _doc.getUndoManager().redo();

        
        
        
        
        _doc.updateModifiedSinceSave();
        _mainFrame.updateFileTitle();
      } catch (CannotRedoException ex) {
        throw new UnexpectedException(ex);
      }
      updateRedoState();
      _undoAction.updateUndoState();
    }

    
    protected void updateRedoState() {
      if (_doc.undoManagerCanRedo()) {
        setEnabled(true);
        putValue(Action.NAME, _doc.getUndoManager().getRedoPresentationName());
      }
      else {
        setEnabled(false);
        putValue(Action.NAME, "Redo");
      }
    }
  }

  
  private class UndoWithPosition implements UndoableEdit {
    private UndoableEdit _undo;
    private int _pos;

    public UndoWithPosition(UndoableEdit undo, int pos) {
      _undo = undo;
      _pos = pos;
    }

    public int getPosition() {
      return _pos;
    }

    public boolean addEdit(UndoableEdit ue) {
      return _undo.addEdit(ue);
    }

    public boolean canRedo() {
      return _undo.canRedo();
    }

    public boolean canUndo() {
      return _undo.canUndo();
    }

    public void die() {
      _undo.die();
    }

    public String getPresentationName() {
      return _undo.getPresentationName();
    }

    public String getUndoPresentationName() {
      return _undo.getUndoPresentationName();
    }

    public String getRedoPresentationName() {
      return _undo.getRedoPresentationName();
    }

    public boolean isSignificant() {
      return _undo.isSignificant();
    }

    public void redo() {
      _undo.redo();
      if (_pos > -1) setCaretPosition(_pos);
    }

    public boolean replaceEdit(UndoableEdit ue) { return _undo.replaceEdit(ue); }

    public void undo() {
      if (_pos > -1) setCaretPosition(_pos);
      _undo.undo();
    }
  }
  
  
  
  
  private List<FinalizationListener<DefinitionsPane>> _finalizationListeners = 
    new LinkedList<FinalizationListener<DefinitionsPane>>();
  
  
  public void addFinalizationListener(FinalizationListener<DefinitionsPane> fl) {
    _finalizationListeners.add(fl);
  }

  public List<FinalizationListener<DefinitionsPane>> getFinalizationListeners() {
    return _finalizationListeners;
  }

  
  protected void finalize() {
    FinalizationEvent<DefinitionsPane> fe = new FinalizationEvent<DefinitionsPane>(this);
    for (FinalizationListener<DefinitionsPane> fl: _finalizationListeners) {
      fl.finalized(fe);
    }
  }
}
