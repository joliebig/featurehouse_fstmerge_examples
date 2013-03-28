

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.*;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.drjava.model.AbstractDJDocument;


public class QuestionNewParenPhrase extends IndentRuleQuestion {
  
  
  public QuestionNewParenPhrase(IndentRule yesRule, IndentRule noRule) {
    super(yesRule, noRule);
  }
 
  
  boolean applyRule(AbstractDJDocument doc, Indenter.IndentReason reason) {

    try {
      
      int here = doc.getCurrentLocation();
      int startLine = doc._getLineStartPos(here);
      
      if (startLine > 0) {
        
        char[] delims = {';', ',', '(', '[', '&', '|', '+', '-', '*', '/', '%', '=', '<', '>', '}'};
        int prevDelim = doc.findPrevDelimiter(startLine, delims, false);
        if (prevDelim == -1) {
          return false;
        }
        
        
        int nextNonWS = doc.getFirstNonWSCharPos(prevDelim + 1);
        if (nextNonWS == -1) {
          nextNonWS = startLine;
        }
        boolean result = nextNonWS >= startLine;

        return result;
      }
    }
    catch (BadLocationException e) {
      
      throw new UnexpectedException(e);
    }
    
    return false;
  }
}
