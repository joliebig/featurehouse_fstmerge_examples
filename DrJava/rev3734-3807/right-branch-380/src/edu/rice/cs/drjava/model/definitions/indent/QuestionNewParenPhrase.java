

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.*;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.drjava.model.AbstractDJDocument;


public class QuestionNewParenPhrase extends IndentRuleQuestion {
  
  
  public QuestionNewParenPhrase(IndentRule yesRule, IndentRule noRule) {
    super(yesRule, noRule);
  }
 
  
  boolean applyRule(AbstractDJDocument doc, int reason) {

    try {
      
      int here = doc.getCurrentLocation();
      int startLine = doc.getLineStartPos(here);
      
      if (startLine > AbstractDJDocument.DOCSTART) {
        
        char[] delims = {';', ',', '(', '[', 
          '&', '|', '+', '-', '*', '/', '%', 
          '=', '<', '>', '}'
        };
        int prevDelim = doc.findPrevDelimiter(startLine, delims, false);
        if (prevDelim == AbstractDJDocument.ERROR_INDEX) {
          return false;
        }
        
        
        int nextNonWS = doc.getFirstNonWSCharPos(prevDelim + 1);
        if (nextNonWS == AbstractDJDocument.ERROR_INDEX) {
          nextNonWS = startLine;
        }
        return (nextNonWS >= startLine);
      }
    }
    catch (BadLocationException e) {
      
      throw new UnexpectedException(e);
    }
    
    return false;
  }
}
