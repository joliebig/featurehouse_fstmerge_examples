

package edu.rice.cs.drjava.model.definitions.indent;

import edu.rice.cs.drjava.model.AbstractDJDocument;


public class QuestionLineContains extends IndentRuleQuestion {
  
  private char _findChar;
  
  
  public QuestionLineContains(char findChar, IndentRule yesRule, IndentRule noRule) {
    super(yesRule, noRule);
    _findChar = findChar;
  }
  
  
  boolean applyRule(AbstractDJDocument doc, int reason) {

    int charPos = doc.findCharOnLine(doc.getCurrentLocation(), _findChar);
    if (charPos == AbstractDJDocument.ERROR_INDEX) {
      return false;
    } else {
      return true;
    }
  }
}

