

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
  
  
  boolean applyRule(AbstractDJDocument doc, int reason) {
    
    
    int origin = doc.getCurrentLocation();
    int lineStart = doc.getLineStartPos(origin);
    
    
    doc.move(lineStart - origin);
    IndentInfo info = doc.getIndentInformation();
    doc.move(origin - lineStart);
    
    if ((!info.braceType.equals(IndentInfo.openSquiggly)) ||
        (info.distToBrace < 0)) {
      
      return false;
    }
    int bracePos = lineStart - info.distToBrace;
    
    
    int prevNonWS = -1;
    try {
      prevNonWS = doc.findPrevNonWSCharPos(bracePos);
      char c = doc.getText(prevNonWS,1).charAt(0);
      for (char pchar: _prefix) if (c == pchar) return true;
    }
    catch (BadLocationException e) {
    }    
    return false;
  }
}
