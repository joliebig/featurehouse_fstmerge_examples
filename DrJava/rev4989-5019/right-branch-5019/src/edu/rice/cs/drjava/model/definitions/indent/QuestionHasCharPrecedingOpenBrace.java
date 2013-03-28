

package edu.rice.cs.drjava.model.definitions.indent;

import edu.rice.cs.drjava.model.AbstractDJDocument;
import edu.rice.cs.drjava.model.definitions.reducedmodel.*;
import javax.swing.text.BadLocationException;


public class QuestionHasCharPrecedingOpenBrace extends IndentRuleQuestion {
  private char[] _prefix;

  
  public QuestionHasCharPrecedingOpenBrace(char[] prefix, IndentRule yesRule, IndentRule noRule) {
    super(yesRule, noRule);
    _prefix = prefix;
  }
  
  
  boolean applyRule(AbstractDJDocument doc, Indenter.IndentReason reason) {
    
    int origin = doc.getCurrentLocation();
    int lineStart = doc._getLineStartPos(origin);
    
    
    BraceInfo info = doc._getLineEnclosingBrace();   
    
    int dist = info.distance();
    
    if (! info.braceType().equals(BraceInfo.OPEN_CURLY) || dist < 0) {  
      return false;
    }
    int bracePos = lineStart - dist;
    
    
    try {
      int loc = doc._findPrevNonWSCharPos(bracePos);
      char ch = doc.getText(loc,1).charAt(0);
      for (char pch: _prefix) if (ch == pch) return true;
    }
    catch (BadLocationException e) { }    
    return false;
  }
}
