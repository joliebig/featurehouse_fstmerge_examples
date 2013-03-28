

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.*;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.drjava.model.AbstractDJDocument;


class ActionStartPrevLinePlusMultilinePreserve extends IndentRuleAction {
  private String[] _suffices;
  private int _cursorLine, _cursorPos, _psrvLine, _psrvPos;

  
  public ActionStartPrevLinePlusMultilinePreserve(String suffices[],
                                                  int cursorLine, int cursorPos,
                                                  int psrvLine, int psrvPos) {
    _suffices = suffices;
    _cursorLine = cursorLine;
    _cursorPos = cursorPos;
    _psrvLine = psrvLine;
    _psrvPos = psrvPos;
  }

  
  public boolean indentLine(AbstractDJDocument doc, Indenter.IndentReason reason) {
    try {
      
      String[] suffices = new String[_suffices.length];
      for(int i = 0; i < _suffices.length; i++) suffices[i] = _suffices[i];
      
      
      int here = doc.getCurrentLocation();
      int lineStart = doc._getLineStartPos(here);
      int lineEnd = doc._getLineEndPos(here);

      
      int lineLength = lineEnd-lineStart;
      String preserved = doc.getText(lineStart, lineLength);
      doc.remove(lineStart, lineLength);

      
      String prefix = suffices[_psrvLine].substring(0,_psrvPos);
      String suffix = suffices[_psrvLine].substring(_psrvPos);
      suffices[_psrvLine] = prefix + preserved + suffix;

      
      ActionStartPrevLinePlusMultiline a;
      
      
      
      a = new ActionStartPrevLinePlusMultiline(suffices, _cursorLine, _cursorPos);
      return a.indentLine(doc, reason);
    }
    catch (BadLocationException e) {
      
      throw new UnexpectedException(e);
    }
  }
}
