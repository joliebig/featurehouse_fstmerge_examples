

package edu.rice.cs.drjava.ui;

import edu.rice.cs.drjava.model.DJError;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.drjava.model.compiler.CompilerErrorModel;
import edu.rice.cs.util.swing.Utilities;

import java.awt.EventQueue;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.Position;


public class ErrorCaretListener implements CaretListener {
  private final OpenDefinitionsDocument _openDoc;
  private final DefinitionsPane _definitionsPane;
  protected final MainFrame _frame;

  
  public ErrorCaretListener(OpenDefinitionsDocument doc, DefinitionsPane defPane, MainFrame frame) {
    _openDoc = doc;
    _definitionsPane = defPane;
    _frame = frame;
  }

  
  public OpenDefinitionsDocument getOpenDefDoc() { return _openDoc; }

  
  public void caretUpdate(final CaretEvent evt) {
    assert EventQueue.isDispatchThread();
    if (_frame.getSelectedCompilerErrorPanel() == null) return;
    updateHighlight(evt.getDot());
  }
  
  
  public void updateHighlight(final int curPos) {
    assert EventQueue.isDispatchThread();
    CompilerErrorPanel panel = _frame.getSelectedCompilerErrorPanel();
    if (panel == null) return;  
    
    CompilerErrorModel model =  panel.getErrorModel();
    
    if (! model.hasErrorsWithPositions(_openDoc)) return;
    

    
    DJError error = model.getErrorAtOffset(_openDoc, curPos);
    
    ErrorPanel.ErrorListPane errorListPane = panel.getErrorListPane();
    
    if (error == null) errorListPane.selectNothing();
    else {      
      if (errorListPane.shouldShowHighlightsInSource()) {
        
        _highlightErrorInSource(model.getPosition(error));
      }
      
      errorListPane.selectItem(error);
    }
  } 
  
  
  public void removeHighlight() { 
    assert EventQueue.isDispatchThread();
    _definitionsPane.removeErrorHighlight(); 
  }

  
  private void _highlightErrorInSource(Position pos) {
    assert EventQueue.isDispatchThread();
    if (pos == null) return;
    int errPos = pos.getOffset();
    
    String text = _openDoc.getText();
    
    
    
    
    
    int prevNewline = text.lastIndexOf('\n', errPos - 1);
    if (prevNewline == -1) prevNewline = 0;
    
    int nextNewline = text.indexOf('\n', errPos);
    if (nextNewline == -1) nextNewline = text.length();
    
    removeHighlight();
    
    
    
    if (prevNewline > 0) prevNewline++;      
    
    if (prevNewline <= nextNewline) {
      _definitionsPane.addErrorHighlight(prevNewline, nextNewline);
    }
  }
}

