

package edu.rice.cs.drjava.ui;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.*;
import edu.rice.cs.drjava.model.*;
import edu.rice.cs.drjava.model.definitions.indent.Indenter;

import edu.rice.cs.util.swing.*;
import edu.rice.cs.util.text.SwingDocument;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;


public abstract class AbstractDJPane extends JTextPane implements OptionConstants {
  
  
  
  
  private static final double SCROLL_UNIT = .05;
  
  
  static DefaultHighlighter.DefaultHighlightPainter MATCH_PAINTER;

  static {
    Color highColor = DrJava.getConfig().getSetting(DEFINITIONS_MATCH_COLOR);
    MATCH_PAINTER = new DefaultHighlighter.DefaultHighlightPainter(highColor);
  }
  
  
  static DefaultHighlighter.DefaultHighlightPainter ERROR_PAINTER =
    new DefaultHighlighter.DefaultHighlightPainter(DrJava.getConfig().getSetting(COMPILER_ERROR_COLOR));
  
  protected HighlightManager _highlightManager;
  
  
  protected CaretListener _matchListener = new CaretListener() {
    
    
    public void caretUpdate(CaretEvent e) {

      getDJDocument().setCurrentLocation(getCaretPosition());


          _removePreviousHighlight();
          _updateMatchHighlight();



    }
  };
  
  
  protected HighlightManager.HighlightInfo _matchHighlight = null;
  
  protected final SwingDocument NULL_DOCUMENT = new SwingDocument();
  
  
  
  AbstractDJPane(SwingDocument doc) {
    super(doc);
    setContentType("text/java");
    
    
    this.addCaretListener(_matchListener);
  }
  
  
 
  
  protected void _addHighlight(int from, int to) {
    _matchHighlight = _highlightManager.addHighlight(from, to, MATCH_PAINTER);
  }
  
  protected abstract void _updateMatchHighlight();

  
  protected void _removePreviousHighlight() {
    if (_matchHighlight != null) {
      _matchHighlight.remove();
      
      _matchHighlight = null;
    }
  }
  
  
  public void setCaretPosition(int pos) {
        super.setCaretPosition(pos);
        getDJDocument().setCurrentLocation(pos);

  }
  
  public int getScrollableUnitIncrement(Rectangle visibleRectangle, int orientation, int direction) {
    return (int) (visibleRectangle.getHeight() * SCROLL_UNIT);
  }
  
  
   
  public void moveCaretPosition(int pos) { super.moveCaretPosition(pos); }
  
  
  public void indent() { indent(Indenter.OTHER); }

  
  public void indent(final int reason) {

    
    getDJDocument().setCurrentLocation(getCaretPosition());
    
    
    
    final int selStart = getSelectionStart();
    final int selEnd = getSelectionEnd();
    
    ProgressMonitor pm = null;
    
    
    
    
    
    
    
    
    
    if (shouldIndent(selStart,selEnd)) { indentLines(selStart, selEnd, reason, pm); }

  }

  
  protected abstract void indentLines(int selStart, int selEnd, int reason, ProgressMonitor pm);
     
  
  protected abstract boolean shouldIndent(int selStart, int selEnd);
  
  
  public abstract DJDocument getDJDocument();
}