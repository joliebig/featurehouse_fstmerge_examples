

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.*;
import edu.rice.cs.util.UnexpectedException;

import edu.rice.cs.drjava.model.AbstractDJDocument;


public class QuestionCurrLineStartsWith extends IndentRuleQuestion {
  private String _prefix;
  
  
  public QuestionCurrLineStartsWith(String prefix, IndentRule yesRule, IndentRule noRule) {
    super(yesRule, noRule);
    _prefix = prefix;
  }
  
  
  boolean applyRule(AbstractDJDocument doc, Indenter.IndentReason reason) {
    
    try {
      
      int here = doc.getCurrentLocation();
      int firstCharPos = doc._getLineFirstCharPos(here);
      int lineEndPos = doc._getLineEndPos(here);
      
      
      if (firstCharPos + _prefix.length() > lineEndPos) {
        return false;
      }
      
      
      String actualPrefix = doc.getText(firstCharPos, _prefix.length());
      return _prefix.equals(actualPrefix);
    }
    catch (BadLocationException e) { throw new UnexpectedException(e); }  
  }
}
