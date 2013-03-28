

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.*;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.drjava.model.AbstractDJDocument;


class ActionStartPrevLinePlusMultiline extends IndentRuleAction {
  private String[] _suffices;
  private int _line = 0;
  
  private int _offset = 0;

  
  public ActionStartPrevLinePlusMultiline(String suffices[],
                                          int line, int position) {
    _suffices = suffices;
    
    
    if ((line >= 0) && (line < suffices.length)) {
      _line = line;
    }
    else {
      throw new IllegalArgumentException
        ("The specified line was outside the bounds of the specified array.");
    }
    
    if ((position < 0) || (position > suffices[line].length())) {
      throw new IllegalArgumentException
        ("The specified position was not within the bounds of the specified line.");
    }
    
    
    for (int i = 0; i < line; i++) {
      _offset += _suffices[i].length();
    }
    _offset += position;
  }
  
  
  public boolean indentLine(AbstractDJDocument doc, Indenter.IndentReason reason) {
    super.indentLine(doc, reason);
    try {
      
      int here = doc.getCurrentLocation();
      int startLine = doc._getLineStartPos(here);

      if (startLine > 0) {
        
        int startPrevLine = doc._getLineStartPos(startLine - 1);
        int firstChar = doc._getLineFirstCharPos(startPrevLine);
        String prefix = doc.getText(startPrevLine, firstChar - startPrevLine);
        
        
        for (int i = 0; i < _suffices.length; i++) {
          doc.setTab(prefix + _suffices[i], here);
          here += prefix.length() + _suffices[i].length();
        }
        
        
        int newPos = startLine + _offset + (prefix.length() * (_line + 1));
        doc.setCurrentLocation(newPos);
      }
      else {
        
        for (int i = 0; i < _suffices.length; i++) {
          doc.setTab(_suffices[i], here);
          here += _suffices[i].length();
        }
      }
      return false;
    }
    catch (BadLocationException e) {
      
      throw new UnexpectedException(e);
    }
  }
}
