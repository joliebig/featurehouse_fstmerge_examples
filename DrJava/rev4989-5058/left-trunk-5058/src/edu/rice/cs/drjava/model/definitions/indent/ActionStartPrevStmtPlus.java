

package edu.rice.cs.drjava.model.definitions.indent;

import edu.rice.cs.drjava.model.AbstractDJDocument;
import edu.rice.cs.util.UnexpectedException;

import javax.swing.text.BadLocationException;


public class ActionStartPrevStmtPlus extends IndentRuleAction {
  private int _suffix;  
  private boolean _useColon;

  
  public ActionStartPrevStmtPlus(int suffix, boolean colonIsDelim) {
    super();
    _suffix = suffix;
    _useColon = colonIsDelim;
  }

  
  public boolean indentLine(AbstractDJDocument doc, Indenter.IndentReason reason) {
    boolean supResult = super.indentLine(doc, reason);
    int here = doc.getCurrentLocation();
    
    
    char[] delims = {';', '{', '}'};
    int lineStart = doc._getLineStartPos(here);  
    int prevDelimiterPos;
    try { prevDelimiterPos = doc.findPrevDelimiter(lineStart, delims); }  
    catch (BadLocationException e) { throw new UnexpectedException(e); }
    
    
    if (prevDelimiterPos <= 0) {
      doc.setTab(_suffix, here);
      return supResult;
    }
    
    try {
      char delim = doc.getText(prevDelimiterPos, 1).charAt(0);    
      char[] ws = {' ', '\t', '\n', ';'};  
      if (delim == ';') {
        int testPos = doc._findPrevCharPos(prevDelimiterPos, ws);  
        char testDelim = doc.getText(testPos,1).charAt(0);
        if ( testDelim == '}' || testDelim == ')') {
          prevDelimiterPos = testPos;                             
        }
      }
    } catch (BadLocationException e) {
      
    }
    
    try {
      
      char delim = doc.getText(prevDelimiterPos, 1).charAt(0);
      
      if (delim == '}' || delim == ')') {
        
        

        
        assert doc.getCurrentLocation() == here;
        doc.setCurrentLocation(prevDelimiterPos + 1);   
        int delta = doc.balanceBackward(); 
        if (delta < 0) { 
          throw new UnexpectedException("No matching '{' or '(' preceding '" + delim + "' at offset " + here + " in "
                                       + doc);
        }
        prevDelimiterPos -= delta - 1;  
        doc.setCurrentLocation(here);
        
        assert doc.getText(prevDelimiterPos, 1).charAt(0) == '{' || 
          doc.getText(prevDelimiterPos, 1).charAt(0) == '(';
      }
    }
    catch (BadLocationException e) { throw new UnexpectedException(e); }
    
    
    
    
    char[] indentDelims;
    char[] indentDelimsWithColon = {';', '{', '}', ':'};
    char[] indentDelimsWithoutColon = {';', '{', '}'};
    if (_useColon) indentDelims = indentDelimsWithColon;
    else indentDelims = indentDelimsWithoutColon;
    
    int indent = doc._getIndentOfCurrStmt(prevDelimiterPos, indentDelims);
    
    indent = indent + _suffix;
    doc.setTab(indent, here);
    return supResult;
  }
}

