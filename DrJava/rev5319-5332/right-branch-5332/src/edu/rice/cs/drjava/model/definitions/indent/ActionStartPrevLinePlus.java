

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.*;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.drjava.model.AbstractDJDocument;


class ActionStartPrevLinePlus extends IndentRuleAction {
  private String _suffix;

  
  public ActionStartPrevLinePlus(String suffix) { _suffix = suffix; }

  
  public boolean indentLine(AbstractDJDocument doc, Indenter.IndentReason reason) {
    boolean supResult = super.indentLine(doc, reason);
    try {
      
      int here = doc.getCurrentLocation();
      int startLine = doc._getLineStartPos(here);
      String prefix;
      
      if (startLine > 0) {
        
        int startPrevLine = doc._getLineStartPos(startLine - 1);
        int firstChar = doc._getLineFirstCharPos(startPrevLine);
        String prevPrefix = doc.getText(startPrevLine, firstChar - startPrevLine);
        prefix = prevPrefix + _suffix;
      }
      else prefix = _suffix;  
      
      if (AbstractDJDocument.hasOnlySpaces(prefix)) doc.setTab(prefix.length(), here);
      else doc.setTab(prefix, here);
      
      return supResult;
    }
    catch (BadLocationException e) { throw new UnexpectedException(e); } 
  }
}
