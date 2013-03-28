

package edu.rice.cs.drjava.ui;

import javax.swing.*;
import javax.swing.undo.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.LinkedList;

import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.OperationCanceledException;
import edu.rice.cs.util.swing.HighlightManager;
import edu.rice.cs.util.swing.RightClickMouseAdapter;
import edu.rice.cs.util.text.SwingDocument;
import edu.rice.cs.drjava.model.*;
import edu.rice.cs.drjava.model.definitions.CompoundUndoManager;
import edu.rice.cs.drjava.model.definitions.DefinitionsEditorKit;
import edu.rice.cs.drjava.model.definitions.NoSuchDocumentException;
import edu.rice.cs.drjava.model.definitions.indent.Indenter;
import edu.rice.cs.drjava.model.definitions.reducedmodel.ReducedModelState;
import edu.rice.cs.drjava.config.*;
import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.model.debug.Breakpoint;

import static edu.rice.cs.drjava.model.definitions.reducedmodel.ReducedModelStates.*;


public class DefinitionsPane extends AbstractDJPane implements Finalizable<DefinitionsPane> {

  
  private static DefinitionsEditorKit EDITOR_KIT;
  
  
  private static int INDENT_WARNING_THRESHOLD = 200000;
    
  
  private final MainFrame _mainFrame;
  
  private final OpenDefinitionsDocument _doc;
  
  private volatile UndoAction _undoAction;
  private volatile RedoAction _redoAction;
  
  private volatile boolean testVariable;   

  
  
  private volatile boolean _hasWarnedAboutModified = false;




  
  private volatile boolean _antiAliasText = false;

  
  private volatile HighlightManager.HighlightInfo _errorHighlightTag = null;

  
  static volatile ReverseHighlighter.DefaultUnderlineHighlightPainter BOOKMARK_PAINTER =
    new ReverseHighlighter.DefaultUnderlineHighlightPainter(DrJava.getConfig().getSetting(BOOKMARK_COLOR), 3);

  
  static volatile LayeredHighlighter.LayerPainter[] FIND_RESULTS_PAINTERS;
  
  static {
    FIND_RESULTS_PAINTERS = new LayeredHighlighter.LayerPainter[FIND_RESULTS_COLORS.length+1];
    for(int i = 0; i < FIND_RESULTS_COLORS.length; ++i) {
      FIND_RESULTS_PAINTERS[i] =
        new ReverseHighlighter.DefaultFrameHighlightPainter(DrJava.getConfig().getSetting(FIND_RESULTS_COLORS[i]), 2);
    }
    FIND_RESULTS_PAINTERS[FIND_RESULTS_COLORS.length] =
        new ReverseHighlighter.DefaultUnderlineHighlightPainter(Color.WHITE, 0);
  }
  
  
  static volatile int[] FIND_RESULTS_PAINTERS_USAGE = new int[FIND_RESULTS_COLORS.length];

  
  static ReverseHighlighter.DrJavaHighlightPainter BREAKPOINT_PAINTER =
    new ReverseHighlighter.DrJavaHighlightPainter(DrJava.getConfig().getSetting(DEBUG_BREAKPOINT_COLOR));

  
  static volatile ReverseHighlighter.DrJavaHighlightPainter DISABLED_BREAKPOINT_PAINTER =
    new ReverseHighlighter.DrJavaHighlightPainter(DrJava.getConfig().getSetting(DEBUG_BREAKPOINT_DISABLED_COLOR));

  
  static volatile ReverseHighlighter.DrJavaHighlightPainter THREAD_PAINTER =
    new ReverseHighlighter.DrJavaHighlightPainter(DrJava.getConfig().getSetting(DEBUG_THREAD_COLOR));

  
  public static final String INDENT_KEYMAP_NAME = "INDENT_KEYMAP";
  
  
  protected void matchUpdate(int offset) { 
    _doc.setCurrentLocation(offset);  
    _removePreviousHighlight();
    
    
    int to = getCaretPosition();
    int from = _doc.balanceBackward();
    if (from > -1) {
      
      from = to - from;
      _addHighlight(from, to);
      
      
      String matchText = _matchText(from);
      
      if (matchText != null) _mainFrame.updateStatusField("Bracket matches: " + matchText);
      else updateStatusField();
    }
    
    
    else {
      
      from = to;
      
      to = _doc.balanceForward();
      if (to > -1) {
        to = to + from;
        _addHighlight(from - 1, to);

      }
      updateStatusField();
    }
  }
  
  
  protected void updateStatusField() { _mainFrame.updateStatusField(); }
  
  
  private String _matchText(int braceIndex) {
    DJDocument doc = _doc;
    String docText;
    docText = doc.getText();
   
    char ch = docText.charAt(braceIndex);
    if ( ch == '{' || ch == '(') { 
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
      
      final StringBuilder returnText = new StringBuilder(docText.substring(0, charBeforeIndex+2));          
      if (previousLine) returnText.append("...");
      returnText.append(ch);
      
      int lastNewlineIndex = returnText.lastIndexOf("\n");
      return returnText.substring(lastNewlineIndex+1);
    }
    else 
      return null;     
  }  
    
  
  private class MatchColorOptionListener implements OptionListener<Color> {
    public void optionChanged(OptionEvent<Color> oce) {
      MATCH_PAINTER = new ReverseHighlighter.DrJavaHighlightPainter(oce.value);
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
      ERROR_PAINTER = new ReverseHighlighter.DrJavaHighlightPainter(oce.value);
      if (_errorHighlightTag != null) {
        int start = _errorHighlightTag.getStartOffset();
        int end = _errorHighlightTag.getEndOffset();
        _errorHighlightTag.remove();
        addErrorHighlight(start, end);
      }
    }
  }

  
  private class BookmarkColorOptionListener implements OptionListener<Color> {
    public void optionChanged(OptionEvent<Color> oce) {
      BOOKMARK_PAINTER = 
        new ReverseHighlighter.DefaultUnderlineHighlightPainter(oce.value, BOOKMARK_PAINTER.getThickness());
      _mainFrame.refreshBookmarkHighlightPainter();
    }
  }

  
  private static class FindResultsColorOptionListener implements OptionListener<Color> {
    private int _index;
    public FindResultsColorOptionListener(int i) { _index = i; }
    public void optionChanged(OptionEvent<Color> oce) {
      synchronized(FIND_RESULTS_PAINTERS) {
        FIND_RESULTS_PAINTERS[_index] = new ReverseHighlighter.DefaultFrameHighlightPainter(oce.value, 2);
      }
    }
  }

  
  private class BreakpointColorOptionListener implements OptionListener<Color> {
    public void optionChanged(OptionEvent<Color> oce) {
      BREAKPOINT_PAINTER = new ReverseHighlighter.DrJavaHighlightPainter(oce.value);
      _mainFrame.refreshBreakpointHighlightPainter();
    }
  }

  
  private class DisabledBreakpointColorOptionListener implements OptionListener<Color> {
    public void optionChanged(OptionEvent<Color> oce) {
      DISABLED_BREAKPOINT_PAINTER =  new ReverseHighlighter.DrJavaHighlightPainter(oce.value);
      _mainFrame.refreshBreakpointHighlightPainter();
    }
  }

  
  private static class ThreadColorOptionListener implements OptionListener<Color> {
    public void optionChanged(OptionEvent<Color> oce) {
      THREAD_PAINTER = new ReverseHighlighter.DrJavaHighlightPainter(oce.value);
    }
  }

  
  private class AntiAliasOptionListener implements OptionListener<Boolean> {
    public void optionChanged(OptionEvent<Boolean> oce) {
      _antiAliasText = oce.value.booleanValue();
      DefinitionsPane.this.repaint();
    }
  }

  
  private final UndoableEditListener _undoListener = new UndoableEditListener() {
    
    
    public void undoableEditHappened(UndoableEditEvent e) {

      UndoableEdit undo = e.getEdit();
      if (! _inCompoundEdit) {
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






  
  private volatile JPopupMenu _popMenu;

  
  private volatile PopupMenuMouseAdapter _popupMenuMA;

  
  private volatile ErrorCaretListener _errorListener;

  private volatile ActionListener _setSizeListener = null;

  
  private class IndentKeyActionTab extends AbstractAction {
    
    
    public void actionPerformed(ActionEvent e) {
      
      
      
      _mainFrame.hourglassOn();
      try {
        indent();
      } finally {
        _mainFrame.hourglassOff();
      }
    }
  }

  
  private class IndentKeyAction extends AbstractAction {
    
    
    @SuppressWarnings("unused") private final String _key;

    
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

    
    protected Indenter.IndentReason getIndentReason() { return Indenter.IndentReason.OTHER; }

    
    public void actionPerformed(ActionEvent e) {
      
      
      if ((e != null) &&
          (e.getActionCommand() != null) &&
          (e.getActionCommand().equals("{") || e.getActionCommand().equals("}"))) {
        ActionEvent e2 = new ActionEvent(e.getSource(),
                                         e.getID(),
                                         e.getActionCommand(),
                                         e.getWhen(),
                                         ActionEvent.SHIFT_MASK);
        _defaultAction.actionPerformed(e2);
      }
      else {
        _defaultAction.actionPerformed(e);
      }
      
      

      updateCurrentLocationInDoc();
      ReducedModelState state = _doc.getStateAtCurrent();
      if (state.equals(FREE) || _indentNonCode) indent(getIndentReason());
    }
  }
  
  
  public void updateCurrentLocationInDoc() {
    _doc.setCurrentLocation(getCaretPosition());
  }

  
  private volatile Action _indentKeyActionTab = new IndentKeyActionTab();

  
  private final Action _indentKeyActionLine =
    new IndentKeyAction("\n", (Action) getActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0)),
                        true  ) {
    
    protected Indenter.IndentReason getIndentReason() { return Indenter.IndentReason.ENTER_KEY_PRESS; }
  };

  
  private final Action _indentKeyActionCurly = new IndentKeyAction("}", getKeymap().getDefaultAction());
  private final Action _indentKeyActionOpenCurly = new IndentKeyAction("{", getKeymap().getDefaultAction());
  private final Action _indentKeyActionColon = new IndentKeyAction(":", getKeymap().getDefaultAction());

  
  public volatile boolean _inCompoundEdit = false;
  private volatile int _compoundEditKey;

  
  final Keymap ourMap;
  
  
  public DefinitionsPane(MainFrame mf, final OpenDefinitionsDocument doc) {
    super(new SwingDocument());
    
    _mainFrame = mf;
    
    addFocusListener(new FocusAdapter() {
      public void focusGained(FocusEvent e) {  
        _mainFrame.getModel().getDocumentNavigator().requestSelectionUpdate(doc);
      }
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
    ourMap.addActionForKeyStroke(KeyStroke.getKeyStroke('}'), _indentKeyActionCurly);
    ourMap.addActionForKeyStroke(KeyStroke.getKeyStroke('{'), _indentKeyActionOpenCurly);
    ourMap.addActionForKeyStroke(KeyStroke.getKeyStroke(':'), _indentKeyActionColon);
    setKeymap(ourMap);












  


    _antiAliasText = DrJava.getConfig().getSetting(TEXT_ANTIALIAS).booleanValue();

    OptionListener<Color> temp;
    Pair<Option<Color>, OptionListener<Color>> pair;
      
    
    
    temp = new ForegroundColorListener(this);
    pair = new Pair<Option<Color>, OptionListener<Color>>(OptionConstants.DEFINITIONS_NORMAL_COLOR, temp);
    _colorOptionListeners.add(pair);
    
    temp = new BackgroundColorListener(this);
    pair = new Pair<Option<Color>, OptionListener<Color>>(OptionConstants.DEFINITIONS_BACKGROUND_COLOR, temp);
    _colorOptionListeners.add(pair);

    
    temp = new MatchColorOptionListener();
    pair = new Pair<Option<Color>, OptionListener<Color>>(OptionConstants.DEFINITIONS_MATCH_COLOR, temp);
    _colorOptionListeners.add(pair);
    DrJava.getConfig().addOptionListener(OptionConstants.DEFINITIONS_MATCH_COLOR, temp);
    
    temp = new ErrorColorOptionListener();
    pair = new Pair<Option<Color>, OptionListener<Color>>(OptionConstants.COMPILER_ERROR_COLOR, temp);
    _colorOptionListeners.add(pair);
    DrJava.getConfig().addOptionListener(OptionConstants.COMPILER_ERROR_COLOR, temp);

    temp = new BookmarkColorOptionListener();
    pair = new Pair<Option<Color>, OptionListener<Color>>(OptionConstants.BOOKMARK_COLOR, temp);
    _colorOptionListeners.add(pair);
    DrJava.getConfig().addOptionListener(OptionConstants.BOOKMARK_COLOR, temp);

    for (int i = 0; i < FIND_RESULTS_COLORS.length; ++i) {
      temp = new FindResultsColorOptionListener(i);
      pair = new Pair<Option<Color>, OptionListener<Color>>(OptionConstants.FIND_RESULTS_COLORS[i], temp);
      _colorOptionListeners.add(pair);
      DrJava.getConfig().addOptionListener(OptionConstants.FIND_RESULTS_COLORS[i], temp);
    }
    
    temp = new BreakpointColorOptionListener();
    pair = new Pair<Option<Color>, OptionListener<Color>>(OptionConstants.DEBUG_BREAKPOINT_COLOR, temp);
    _colorOptionListeners.add(pair);
    DrJava.getConfig().addOptionListener(OptionConstants.DEBUG_BREAKPOINT_COLOR, temp);
    
    temp = new DisabledBreakpointColorOptionListener();
    pair = new Pair<Option<Color>, OptionListener<Color>>(OptionConstants.DEBUG_BREAKPOINT_DISABLED_COLOR, temp);
    _colorOptionListeners.add(pair);
    DrJava.getConfig().addOptionListener( OptionConstants.DEBUG_BREAKPOINT_DISABLED_COLOR, temp);
    
    temp = new ThreadColorOptionListener();
    pair = new Pair<Option<Color>, OptionListener<Color>>(OptionConstants.DEBUG_THREAD_COLOR, temp);
    _colorOptionListeners.add(pair);
    DrJava.getConfig().addOptionListener( OptionConstants.DEBUG_THREAD_COLOR, temp);

    OptionListener<Boolean> aaTemp = new AntiAliasOptionListener();
    Pair<Option<Boolean>, OptionListener<Boolean>> aaPair = 
      new Pair<Option<Boolean>, OptionListener<Boolean>>(OptionConstants.TEXT_ANTIALIAS, aaTemp);
    _booleanOptionListeners.add(aaPair);
    DrJava.getConfig().addOptionListener( OptionConstants.TEXT_ANTIALIAS, aaTemp);

    createPopupMenu();

    
    _popupMenuMA = new PopupMenuMouseAdapter();
    this.addMouseListener(_popupMenuMA);
    this.setHighlighter(new ReverseHighlighter());
    _highlightManager = new HighlightManager(this);

    int rate = this.getCaret().getBlinkRate();
    
    



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
      Action a = KeyBindingManager.ONLY.get(ks);
      
      if ((ks != KeyStrokeOption.NULL_KEYSTROKE) && (a != null)) {

        endCompoundEdit();
        
        SwingUtilities.notifyAction(a, ks, e, e.getSource(), e.getModifiers());
        
        
        e.consume();
      }
      else {
        
        Keymap km = getKeymap();
        
        if (km.isLocallyDefined(ks) || km.isLocallyDefined(KeyStroke.getKeyStroke(ks.getKeyChar()))) {
          
          if (e.getKeyCode() == KeyEvent.VK_ENTER) endCompoundEdit();
          



          
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
            String name = KeyBindingManager.ONLY.getName(newKs);
            
            if (name != null && (name.equals("Delete Previous") || name.equals("Delete Next"))) {
              endCompoundEdit();
              
              
              SwingUtilities.notifyAction(KeyBindingManager.ONLY.get(newKs), newKs, e, e.getSource(), newModifiers);
              e.consume();
              
              return;
            }
          }
          
          
          if (e.getID() != KeyEvent.KEY_TYPED) {
            super.processKeyEvent(e);
            return;
          }
        }
        
        if ((e.getModifiers() & InputEvent.ALT_MASK) != 0) testVariable = true; 
        else testVariable = false;
        
        super.processKeyEvent(e);
      }
    }
  }

  
  public static void setEditorKit(DefinitionsEditorKit editorKit) { EDITOR_KIT = editorKit; }

  
  protected void paintComponent(Graphics g) {
    if (_antiAliasText && g instanceof Graphics2D) {
      Graphics2D g2d = (Graphics2D) g;
      g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
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
      public void actionPerformed(ActionEvent ae) {
        _mainFrame.hourglassOn();
        try {
          indent();
        } finally {
          _mainFrame.hourglassOff();
        }
      }
    });
    _popMenu.add(indentItem);

    JMenuItem commentLinesItem = new JMenuItem("Comment Line(s)");
    commentLinesItem.addActionListener(new AbstractAction() {
      public void actionPerformed( ActionEvent ae) {
        _mainFrame.hourglassOn();
        try{
          updateCurrentLocationInDoc();
          _commentLines();
        }
        finally{ _mainFrame.hourglassOff(); }
      }
    });
    _popMenu.add(commentLinesItem);

    JMenuItem uncommentLinesItem = new JMenuItem("Uncomment Line(s)");
    uncommentLinesItem.addActionListener ( new AbstractAction() {
      public void actionPerformed( ActionEvent ae) {
        updateCurrentLocationInDoc();
        _uncommentLines();
      }
    });
    _popMenu.add(uncommentLinesItem);

    
    _popMenu.addSeparator();
    JMenuItem gotoFileUnderCursorItem = new JMenuItem("Go to File Under Cursor");
    gotoFileUnderCursorItem.addActionListener ( new AbstractAction() {
      public void actionPerformed( ActionEvent ae) {
        updateCurrentLocationInDoc();
        _mainFrame._gotoFileUnderCursor();
      }
    });
    _popMenu.add(gotoFileUnderCursorItem);

    
    JMenuItem toggleBookmarkItem = new JMenuItem("Toggle Bookmark");
    toggleBookmarkItem.addActionListener ( new AbstractAction() {
      
      public void actionPerformed( ActionEvent ae) {
        
        _mainFrame.toggleBookmark();
      }
    });
    _popMenu.add(toggleBookmarkItem);
      
    if (_mainFrame.getModel().getDebugger().isAvailable()) {
      _popMenu.addSeparator();

      
      JMenuItem breakpointItem = new JMenuItem("Toggle Breakpoint");
      breakpointItem.addActionListener( new AbstractAction() {
        public void actionPerformed( ActionEvent ae ) {
          
          setCaretPosition(viewToModel(_popupMenuMA.getLastMouseClick().getPoint()));
          _mainFrame.debuggerToggleBreakpoint();
        }
      });
      _popMenu.add(breakpointItem);
    }
  }

  
  private class PopupMenuMouseAdapter extends RightClickMouseAdapter {

    private MouseEvent _lastMouseClick = null;

    public void mousePressed(MouseEvent e) {
      super.mousePressed(e);

      _lastMouseClick = e;
      endCompoundEdit();

      
      
    }

    protected void _popupAction(MouseEvent e) {
      requestFocusInWindow();
      _popMenu.show(e.getComponent(), e.getX(), e.getY());
    }

    public MouseEvent getLastMouseClick() { return _lastMouseClick; }
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
    assert EventQueue.isDispatchThread();
    try {
      setCaretPos(pos);
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

  
  public ErrorCaretListener getErrorCaretListener() { return _errorListener; }

  
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

  public boolean hasWarnedAboutModified() { return _hasWarnedAboutModified; }

  public void hasWarnedAboutModified( boolean hasWarned) {
    _hasWarnedAboutModified = hasWarned;
  }

  public void addBreakpointHighlight( Breakpoint bp ) { }

  public void removeBreakpointHighlight( Breakpoint bp) { }

  
  private volatile JScrollPane _scrollPane;
  
  public void setScrollPane(JScrollPane s) { _scrollPane = s; }
  
  
  private volatile int _savedVScroll;
  private volatile int _savedHScroll;
  private volatile int _position;
  private volatile int _selStart;
  private volatile int _selEnd;
  
  
  public void notifyInactive() {
    
    
    
    
    try {
      
      updateCurrentLocationInDoc();
      
      
      removeErrorHighlight();
      
      _position = _doc.getCurrentLocation();
      _selStart = super.getSelectionStart();
      _selEnd = super.getSelectionEnd();

      _savedVScroll = _scrollPane.getVerticalScrollBar().getValue();
      _savedHScroll = _scrollPane.getHorizontalScrollBar().getValue();

      super.setDocument(NULL_DOCUMENT);
    }
    catch(NoSuchDocumentException e) {
      
      
      
    }
  }
    
  
  public void notifyActive() {
    assert ! _mainFrame.isVisible() || EventQueue.isDispatchThread();
    super.setDocument(_doc);
    if (_doc.getUndoableEditListeners().length == 0) _resetUndo();
    
    int len = _doc.getLength();
    if (len < _position || len < _selEnd) {
      
      
      _position = len;
      _selStart = len;
      _selEnd = len;
    }
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
    _scrollPane.getVerticalScrollBar().setValue(_savedVScroll);
    _scrollPane.getHorizontalScrollBar().setValue(_savedHScroll);
    
    _scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    _scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
  }
  
  
  public int getSelectionStart() {
    if (getDocument() == NULL_DOCUMENT) return _selStart;
    else return super.getSelectionStart();
  }
  
  public int getSelectionEnd() {
    if (getDocument() == NULL_DOCUMENT) return _selEnd;
    else return super.getSelectionEnd();
  }
  
  public int getVerticalScroll() {
    if (getDocument() == NULL_DOCUMENT) return _savedVScroll;
    else return _scrollPane.getVerticalScrollBar().getValue();    
  }
  
  public int getHorizontalScroll() {
    if (getDocument() == NULL_DOCUMENT) return _savedHScroll;
    else return _scrollPane.getHorizontalScrollBar().getValue();
  }
  
  
  public int getCurrentLine() { return _doc.getLineOfOffset(getCaretPosition())+1; }













  
  public int getCurrentLinefromDoc() { return _doc.getCurrentLine(); }  
  
  public int getCurrentCol() { return _doc.getCurrentCol(); }
  
  public void setSize(int width, int height) {
    super.setSize(width, height);
    if (_setSizeListener != null) _setSizeListener.actionPerformed(null);
  }




  
  public void centerViewOnOffset(int offset) {
    assert EventQueue.isDispatchThread();
    try {
      FontMetrics metrics = getFontMetrics(getFont());
      JViewport defViewPort = _mainFrame.getDefViewport();
      double viewWidth = defViewPort.getWidth();
      double viewHeight = defViewPort.getHeight();
      
      
      Rectangle startRect;
      startRect = modelToView(offset);

      if (startRect != null) {
        int startRectX = (int) startRect.getX();
        int startRectY = (int) startRect.getY();
        startRect.setLocation(startRectX - (int)(viewWidth*.5), startRectY - (int)(viewHeight*.5));
        Point endPoint = new Point(startRectX + (int)(viewWidth*.5),
                                   startRectY + (int)(viewHeight*.5) + metrics.getHeight()/2);

        
        
        startRect.add(endPoint);

        scrollRectToVisible(startRect);
      }


      setCaretPos(offset);
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
    if (selEnd > (selStart + INDENT_WARNING_THRESHOLD)) {
      Object[] options = {"Yes", "No"};
      int n = JOptionPane.showOptionDialog
        (_mainFrame,
         "Re-indenting this block may take a long time.  Are you sure?",
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
  
  
  protected void indentLines(int selStart, int selEnd, Indenter.IndentReason reason, ProgressMonitor pm) {
    
    
    try {
      _doc.indentLines(selStart, selEnd, reason, pm);
      endCompoundEdit();
      setCaretPosition(_doc.getCurrentLocation());  
    }
    catch(OperationCanceledException oce) {
      
      endCompoundEdit();
      _doc.getUndoManager().undo();
      
      throw new UnexpectedException(oce);
    }
  }
    
  
  private List<Pair<Option<Color>, OptionListener<Color>>> _colorOptionListeners = 
    new LinkedList<Pair<Option<Color>, OptionListener<Color>>>();
    
  private List<Pair<Option<Boolean>, OptionListener<Boolean>>> _booleanOptionListeners = 
    new LinkedList<Pair<Option<Boolean>, OptionListener<Boolean>>>();
  
  
  public void close() {
    for (Pair<Option<Color>, OptionListener<Color>> p: _colorOptionListeners) {
      DrJava.getConfig().removeOptionListener(p.first(), p.second());
    }
    for (Pair<Option<Boolean>, OptionListener<Boolean>> p: _booleanOptionListeners) {
      DrJava.getConfig().removeOptionListener(p.first(), p.second());
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
        _mainFrame.updateStatusField();
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
        _mainFrame.updateStatusField();
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







































  
  
  private List<FinalizationListener<DefinitionsPane>> _finalizationListeners = 
    new LinkedList<FinalizationListener<DefinitionsPane>>();
  
  
  public void addFinalizationListener(FinalizationListener<DefinitionsPane> fl) { _finalizationListeners.add(fl); }

  public List<FinalizationListener<DefinitionsPane>> getFinalizationListeners() { return _finalizationListeners; }

  
  protected void finalize() {
    FinalizationEvent<DefinitionsPane> fe = new FinalizationEvent<DefinitionsPane>(this);
    for (FinalizationListener<DefinitionsPane> fl: _finalizationListeners) fl.finalized(fe);
  }
}
