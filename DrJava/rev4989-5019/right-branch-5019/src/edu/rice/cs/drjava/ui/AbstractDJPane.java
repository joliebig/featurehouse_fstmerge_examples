

package edu.rice.cs.drjava.ui;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.*;
import edu.rice.cs.drjava.model.*;
import edu.rice.cs.drjava.model.definitions.indent.Indenter;

import edu.rice.cs.util.swing.*;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.util.text.SwingDocument;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.text.JTextComponent;
import javax.swing.text.BadLocationException;
import java.awt.dnd.*;
import edu.rice.cs.drjava.DrJavaRoot;


public abstract class AbstractDJPane extends JTextPane
  implements OptionConstants, DropTargetListener {
  
  
  
  
  private static final double SCROLL_UNIT = .05;

  
  
  static ReverseHighlighter.DrJavaHighlightPainter MATCH_PAINTER;

  static {
    Color highColor = DrJava.getConfig().getSetting(DEFINITIONS_MATCH_COLOR);
    MATCH_PAINTER = new ReverseHighlighter.DrJavaHighlightPainter(highColor);
  }
  
  
  static ReverseHighlighter.DrJavaHighlightPainter ERROR_PAINTER =
    new ReverseHighlighter.DrJavaHighlightPainter(DrJava.getConfig().getSetting(COMPILER_ERROR_COLOR));
  
  private static final int ALT_CTRL_META_MASK = Event.ALT_MASK | Event.CTRL_MASK | Event.META_MASK;
  
  protected volatile HighlightManager _highlightManager;
  
  
  protected final CaretListener _matchListener = new CaretListener() {
    
    
    public void caretUpdate(final CaretEvent ce) { 
           
      assert EventQueue.isDispatchThread();


          _removePreviousHighlight();
          
          int offset = ce.getDot();
          if (offset < 1) return;
          DJDocument doc = getDJDocument();
          try { 
            char prevChar = doc.getText(offset - 1, 1).charAt(0);
            if (prevChar == '{' || prevChar == '(' || prevChar == '}' || prevChar == ')') matchUpdate(offset);
            else updateStatusField();  
            
          }
          catch(BadLocationException e) { DrJavaErrorHandler.record(e); }


    }
    
  };
  
  
  protected volatile HighlightManager.HighlightInfo _matchHighlight = null;
  
  protected static final SwingDocument NULL_DOCUMENT = new SwingDocument() {
    public void addDocumentListener(DocumentListener listener) {
      
    }
    public void addUndoableEditListener(UndoableEditListener listener) {
      
    }
  };
  
  
  
  AbstractDJPane(SwingDocument doc) {
    super(doc);
    setContentType("text/java");
    
    
    addCaretListener(_matchListener);
    disableAltCntlMetaChars(this);
  }
  
  
  
  
  public static void disableAltCntlMetaChars(JTextComponent p) {
    Keymap km = p.getKeymap();
    final Action defaultAction = km.getDefaultAction();
    km.setDefaultAction(new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        if ((e.getModifiers() & ALT_CTRL_META_MASK) != 0) return;
        defaultAction.actionPerformed(e);
      }
    });
  }
 
  
  protected void _addHighlight(int from, int to) {
    _matchHighlight = _highlightManager.addHighlight(from, to, MATCH_PAINTER);
  }
  
  
  protected abstract void matchUpdate(int offset);
  
  
  protected abstract void updateStatusField();

  
  protected void _removePreviousHighlight() {
    if (_matchHighlight != null) {
      _matchHighlight.remove();
      
      _matchHighlight = null;
    }
  }
  
  
  public void setCaretPos(int pos) {

    DJDocument doc = getDJDocument();
    int len = doc.getLength();
    if (pos > len) {
      setCaretPosition(len);
      return;
    }
    setCaretPosition(pos);
  }







  public int getScrollableUnitIncrement(Rectangle visibleRectangle, int orientation, int direction) {
    return (int) (visibleRectangle.getHeight() * SCROLL_UNIT);
  }
  
  
  public void indent() { indent(Indenter.IndentReason.OTHER); }

  
  public void indent(final Indenter.IndentReason reason) {

    
    
    
    getDJDocument().setCurrentLocation(getCaretPosition());
    
    
    
    final int selStart = getSelectionStart();
    final int selEnd = getSelectionEnd();
    
    ProgressMonitor pm = null;
    
    
    
    
    
    
    
    
    
    if (shouldIndent(selStart,selEnd)) { indentLines(selStart, selEnd, reason, pm); }

  }

  
  protected abstract void indentLines(int selStart, int selEnd, Indenter.IndentReason reason, ProgressMonitor pm);
     
  
  protected abstract boolean shouldIndent(int selStart, int selEnd);
  
  
  public abstract DJDocument getDJDocument();
  
  
  DropTarget dropTarget = new DropTarget(this, this);  

  
  public void dragEnter(DropTargetDragEvent dropTargetDragEvent) {
    DrJavaRoot.dragEnter(dropTargetDragEvent);
  }
  
  public void dragExit(DropTargetEvent dropTargetEvent) {}
  public void dragOver(DropTargetDragEvent dropTargetDragEvent) {}
  public void dropActionChanged(DropTargetDragEvent dropTargetDragEvent){}
  
  
  public synchronized void drop(DropTargetDropEvent dropTargetDropEvent) {
    DrJavaRoot.drop(dropTargetDropEvent);
  }
}