

package edu.rice.cs.drjava.model.definitions.indent;

import edu.rice.cs.drjava.model.AbstractDJDocument;


public class QuestionLineContains extends IndentRuleQuestion {
  
  private char _findChar;
  
  
  public QuestionLineContains(char findChar, IndentRule yesRule, IndentRule noRule) {
    super(yesRule, noRule);
    _findChar = findChar;
  }
  
  
  boolean applyRule(AbstractDJDocument doc, Indenter.IndentReason reason) {

    int charPos = doc.findCharOnLine(doc.getCurrentLocation(), _findChar);
    if (charPos == -1) {
      return false;
    } else {
      return true;
    }
  }
}

