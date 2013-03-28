

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.*;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.drjava.model.AbstractDJDocument;


class ActionStartPrevLinePlus extends IndentRuleAction {
  private String _suffix;

  
  public ActionStartPrevLinePlus(String suffix) {
    _suffix = suffix;
  }

  
  public boolean indentLine(AbstractDJDocument doc, int reason) {
    boolean supResult = super.indentLine(doc, reason);
    try {
      
      int here = doc.getCurrentLocation();
      int startLine = doc.getLineStartPos(here);

      if (startLine > AbstractDJDocument.DOCSTART) {
        
        int startPrevLine = doc.getLineStartPos(startLine - 1);
        int firstChar = doc.getLineFirstCharPos(startPrevLine);
        String prefix = doc.getText(startPrevLine, firstChar - startPrevLine);
        doc.setTab(prefix + _suffix, here);
      }
      else {
        
        doc.setTab(_suffix, here);
      }
      return supResult;
    }
    catch (BadLocationException e) {
      
      throw new UnexpectedException(e);
    }
  }
}
