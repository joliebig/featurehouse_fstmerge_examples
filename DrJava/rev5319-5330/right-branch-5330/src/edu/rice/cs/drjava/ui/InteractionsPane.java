

package edu.rice.cs.drjava.ui;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import javax.swing.undo.*;  
import javax.swing.event.*;
import java.awt.event.*;

import edu.rice.cs.drjava.model.definitions.*;

import java.awt.event.KeyEvent;
import java.awt.datatransfer.*;
import java.util.Vector;

import edu.rice.cs.util.swing.*;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.drjava.config.*;
import edu.rice.cs.drjava.*;
import edu.rice.cs.drjava.model.DJDocument;
import edu.rice.cs.drjava.model.definitions.indent.Indenter;
import edu.rice.cs.util.OperationCanceledException;
import edu.rice.cs.drjava.model.repl.*;


public abstract class InteractionsPane extends AbstractDJPane implements OptionConstants, ClipboardOwner {
  
  static edu.rice.cs.util.Log LOG = new edu.rice.cs.util.Log("InteractionsPane.txt", false);
  
  
  private volatile UndoAction _undoAction;
  private volatile RedoAction _redoAction;
  public volatile boolean _inCompoundEdit = false;
  private volatile int _compoundEditKey;
  private volatile boolean deleteCEBool = true;
    
  
  protected final Keymap _keymap;
  
  
  private boolean _antiAliasText = false;
  
  static StyledEditorKit EDITOR_KIT;
  
  static { EDITOR_KIT = new InteractionsEditorKit();  }
  
  
  protected Runnable _beep = new Runnable() {
    public void run() { Toolkit.getDefaultToolkit().beep(); }
  };
  
  
  private class AntiAliasOptionListener implements OptionListener<Boolean> {
    public void optionChanged(OptionEvent<Boolean> oce) {
      _antiAliasText = oce.value.booleanValue();
      InteractionsPane.this.repaint();
    }
  }
  
  
  private class leftUndoBreak extends MouseAdapter {
    public void mouseClicked(MouseEvent e){
     endCompoundEdit(); 
    }
  }
  
  
  public Runnable getBeep() { return _beep; }
  
  private final InteractionsDJDocument _doc;
  

  
  
  public InteractionsPane(InteractionsDJDocument doc) { this("INTERACTIONS_KEYMAP", doc); }
  
  
  public InteractionsPane(String keymapName, InteractionsDJDocument doc) {
    super(doc);
    _doc = doc;
    
    _keymap = addKeymap(keymapName, getKeymap());
    
    setCaretPosition(doc.getLength());
    
    setHighlighter(new ReverseHighlighter());
    _highlightManager = new HighlightManager(this);
    
    _antiAliasText = DrJava.getConfig().getSetting(TEXT_ANTIALIAS).booleanValue();
    
    
    
    
    
    new ForegroundColorListener(this);
    new BackgroundColorListener(this);
    
    OptionListener<Boolean> aaTemp = new AntiAliasOptionListener();
    DrJava.getConfig().addOptionListener(OptionConstants.TEXT_ANTIALIAS, aaTemp);
    
    _resetUndo(); 
    addMouseListener(new leftUndoBreak());
  }
  
  
  public void lostOwnership(Clipboard clipboard, Transferable contents) {
    
  }
  
  
  public void processKeyEvent(KeyEvent e) { 
    
    
    if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE && deleteCEBool){
      endCompoundEdit(); 
      deleteCEBool=false;
    }
    else if(e.getID()==KeyEvent.KEY_PRESSED && e.getKeyCode() != KeyEvent.VK_BACK_SPACE){
      deleteCEBool = true;
    }
    
    KeyStroke ks = KeyStroke.getKeyStrokeForEvent(e);
    Action a = KeyBindingManager.ONLY.get(ks);
    
    if ((ks != KeyStrokeOption.NULL_KEYSTROKE) && (a != null)) {
      endCompoundEdit();
    }
    
    if ((e.getModifiers() & e.SHIFT_MASK)!=0 && e.getKeyCode()==KeyEvent.VK_ENTER) endCompoundEdit();  
    
    super.processKeyEvent(e);
  }
  
  
  public void addActionForKeyStroke(KeyStroke stroke, Action action) {
    
    KeyStroke[] keys = _keymap.getKeyStrokesForAction(action);
    if (keys != null) {
      for (int i = 0; i < keys.length; i++) _keymap.removeKeyStrokeBinding(keys[i]);
    }
    _keymap.addActionForKeyStroke(stroke, action);
    setKeymap(_keymap);
  }

  
  public void addActionForKeyStroke(Vector<KeyStroke> stroke, Action action) {
    
    KeyStroke[] keys = _keymap.getKeyStrokesForAction(action);
    if (keys != null) {
      for (int i = 0; i < keys.length; i++) _keymap.removeKeyStrokeBinding(keys[i]);
    }
    for (KeyStroke ks: stroke) {
      _keymap.addActionForKeyStroke(ks, action);
    }
    setKeymap(_keymap);
  }
  
  
  public void setBeep(Runnable beep) { _beep = beep; }
  
  
  public void highlightError(int offset, int length) {
    _highlightManager.addHighlight(offset, offset+length, ERROR_PAINTER);
  }
  
  
  protected EditorKit createDefaultEditorKit() { return EDITOR_KIT; }
  
  
  protected void paintComponent(Graphics g) {
    if (g == null) return;  
    if (_antiAliasText && g instanceof Graphics2D) {
      Graphics2D g2d = (Graphics2D)g;
      g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
    super.paintComponent(g);
  }
  
  
  public DJDocument getDJDocument() { return _doc; }
  
  
  protected void matchUpdate(int offset, boolean opening) {
    if (! _doc.hasPrompt()) return;
    _doc.setCurrentLocation(offset); 
    _removePreviousHighlight();
    
    int caretPos = getCaretPosition();
    
    if (opening) {
      
      
      int to = _doc.balanceForward();  
      
      if (to > -1) {  
        int end = caretPos + to;
        _addHighlight(caretPos - 1, end);  
      }
    }
    else {
      int from = _doc.balanceBackward();
      if (from > -1) {  
        int start = caretPos - from;
        _addHighlight(start, caretPos);
      }
    }
  }
  
  
  protected void updateStatusField() {  }
  
  
  protected void indentLines(int selStart, int selEnd, Indenter.IndentReason reason, ProgressMonitor pm) {
    assert EventQueue.isDispatchThread();
    try {
      _doc.indentLines(selStart, selEnd, reason, pm);
      setCaretPos(_doc.getCurrentLocation());    
    }
    catch (OperationCanceledException oce) { throw new UnexpectedException(oce); }
  }
  
  
  protected boolean shouldIndent(int selStart, int selEnd) { return true; }
  
  
  public abstract int getPromptPos();
   
  
  private final UndoableEditListener _undoListener = new UndoableEditListener() {
    
    
    public void undoableEditHappened(UndoableEditEvent e) {
      assert EventQueue.isDispatchThread() || Utilities.TEST_MODE;
      UndoableEdit undo = e.getEdit();
      LOG.log("In undoableEditHappened - _inCompoundEdit is "+ _inCompoundEdit);
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

  
  
  public void endCompoundEdit() {
    if (_inCompoundEdit) {
      CompoundUndoManager undoMan = _doc.getUndoManager();
      _inCompoundEdit = false;
      undoMan.endCompoundEdit(_compoundEditKey);
    }
  }

  
  public UndoAction getUndoAction() { return  _undoAction; }

  
  public RedoAction getRedoAction() { return  _redoAction; }
  
  

  
  public class UndoAction extends AbstractAction {
    
    
    private UndoAction() {
      super("Undo");
      setEnabled(false);
    }

    
    public void actionPerformed(ActionEvent e) {
      try {
        _doc.getUndoManager().undo();
        _doc.updateModifiedSinceSave();
      }
      catch (CannotUndoException ex) {
        throw new UnexpectedException(ex);
      }
      updateUndoState();
      _redoAction.updateRedoState();
    }

    
    protected void updateUndoState() {
      if (_doc.undoManagerCanUndo() && isEditable()) {
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
      } catch (CannotRedoException ex) {
        throw new UnexpectedException(ex);
      }
      updateRedoState();
      _undoAction.updateUndoState();
    }

    
    protected void updateRedoState() {
      if (_doc.undoManagerCanRedo() && isEditable()) {
        setEnabled(true);
        putValue(Action.NAME, _doc.getUndoManager().getRedoPresentationName());
      }
      else {
        setEnabled(false);
        putValue(Action.NAME, "Redo");
      }
    }
  }
  
  
  private void resetUndo() {
    _doc.getUndoManager().discardAllEdits();
    _undoAction.updateUndoState();
    _redoAction.updateRedoState();
  }
  
  
  public void discardUndoEdits(){
    endCompoundEdit();
    resetUndo();
  }
  
  
  public void _resetUndo() {
    if (_undoAction == null) _undoAction = new UndoAction();
    if (_redoAction == null) _redoAction = new RedoAction();
    
    _doc.resetUndoManager();
    
    _doc.addUndoableEditListener(_undoListener);
    _undoAction.updateUndoState();
    _redoAction.updateRedoState();
  }  
}