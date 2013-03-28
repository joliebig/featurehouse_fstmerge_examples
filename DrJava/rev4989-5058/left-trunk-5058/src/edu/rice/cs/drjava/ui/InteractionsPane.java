

package edu.rice.cs.drjava.ui;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

import java.awt.event.KeyEvent;
import java.awt.datatransfer.*;
import java.util.Vector;

import edu.rice.cs.util.swing.*;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.drjava.config.*;
import edu.rice.cs.drjava.*;
import edu.rice.cs.drjava.model.DJDocument;
import edu.rice.cs.drjava.model.definitions.indent.Indenter;
import edu.rice.cs.util.OperationCanceledException;
import edu.rice.cs.drjava.model.repl.*;


public abstract class InteractionsPane extends AbstractDJPane implements OptionConstants, ClipboardOwner {
  
  
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
  }
  
  
  public void lostOwnership(Clipboard clipboard, Transferable contents) {
    
  }
  
  
  public void processKeyEvent(KeyEvent e) { super.processKeyEvent(e); }
  
  
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
  
  
  protected void matchUpdate(int offset) {
    if (! _doc.hasPrompt()) return;
    _doc.setCurrentLocation(offset); 
    _removePreviousHighlight();
    

    int to = getCaretPosition();
    int from = _doc.balanceBackward(); 
    if (from > -1) {
      
      from = to - from;
       _addHighlight(from, to);  
      
    }
    
    else {
      
      from = to;
      to = _doc.balanceForward();
      
      if (to > -1) {
        to = to + from;
         _addHighlight(from - 1, to);  

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
}