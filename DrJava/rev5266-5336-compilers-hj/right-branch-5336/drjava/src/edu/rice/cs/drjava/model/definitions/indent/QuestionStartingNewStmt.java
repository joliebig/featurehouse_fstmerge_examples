

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.BadLocationException;

import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.drjava.model.AbstractDJDocument;


public class QuestionStartingNewStmt extends IndentRuleQuestion {
  
  
  public QuestionStartingNewStmt(IndentRule yesRule, IndentRule noRule) {
    super(yesRule, noRule);
  }
 
  
  boolean applyRule(AbstractDJDocument doc, Indenter.IndentReason reason) {
    
    char[] delims = {';', '{', '}'};
    int lineStart = doc._getLineStartPos(doc.getCurrentLocation());
    int prevDelimiterPos;
    
    try {
      prevDelimiterPos = doc.findPrevDelimiter(lineStart, delims);
    } catch (BadLocationException e) {
      
      throw new UnexpectedException(e);
    }
    
    
    
    
    int firstNonWSAfterDelimiter;
    try {
      firstNonWSAfterDelimiter = doc.getFirstNonWSCharPos(prevDelimiterPos + 1);
      
    } 
    catch (BadLocationException e) { throw new UnexpectedException(e); }
    
    
    
    return (firstNonWSAfterDelimiter >= lineStart || firstNonWSAfterDelimiter == -1);
  }
}

