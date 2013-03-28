

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.*;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.drjava.model.AbstractDJDocument;


class ActionStartPrevLinePlusBackup extends IndentRuleAction {
  private String _suffix;
  private int _position = 0;

  
  public ActionStartPrevLinePlusBackup(String suffix, int position) {
    _suffix = suffix;
    
    if ((position >= 0) && (position <= suffix.length())) {
      _position = position;
    }
    else {
      throw new IllegalArgumentException
        ("The specified position was not within the bounds of the suffix.");
    }
  }

  
  public boolean indentLine(AbstractDJDocument doc, int reason) {
    super.indentLine(doc, reason);
    try {
      
      int here = doc.getCurrentLocation();
      int startLine = doc.getLineStartPos(here);

      if (startLine > AbstractDJDocument.DOCSTART) {
        
        int startPrevLine = doc.getLineStartPos(startLine - 1);
        int firstChar = doc.getLineFirstCharPos(startPrevLine);
        String prefix = doc.getText(startPrevLine, firstChar - startPrevLine);
        
        
        doc.setTab(prefix + _suffix, here);
        
        
        doc.setCurrentLocation(startLine + prefix.length() + _position);
      }
      else {
        
        doc.setTab(_suffix, here);
        
        
        doc.setCurrentLocation(here + _position);
      }
      
      return false;
    }
    catch (BadLocationException e) {
      
      throw new UnexpectedException(e);
    }
  }
}
