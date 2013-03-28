

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.*;
import edu.rice.cs.util.UnexpectedException;

import edu.rice.cs.drjava.model.AbstractDJDocument;


public class QuestionPrevLineStartsWith extends IndentRuleQuestion {
  private String _prefix;
  
  
  public QuestionPrevLineStartsWith(String prefix, IndentRule yesRule, IndentRule noRule) {
    super(yesRule, noRule);
    _prefix = prefix;
  }
  
  
  boolean applyRule(AbstractDJDocument doc, Indenter.IndentReason reason) {

    try {
      
      int here = doc.getCurrentLocation();
      int startLine = doc._getLineStartPos(here);
      
      if (startLine > 0) {
        
        int startPrevLine = doc._getLineStartPos(startLine - 1);
        int firstChar = doc._getLineFirstCharPos(startPrevLine);
        
        
        String actualPrefix = doc.getText(firstChar, _prefix.length());
        return _prefix.equals(actualPrefix);
      }
    }
    catch (BadLocationException e) {
      
      throw new UnexpectedException(e);
    }
    
    return false;
  }

}
