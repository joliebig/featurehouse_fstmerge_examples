

package edu.rice.cs.drjava.model.definitions.indent;

import edu.rice.cs.drjava.model.AbstractDJDocument;


class QuestionCurrLineEmptyOrEnterPress extends IndentRuleQuestion {
  
  QuestionCurrLineEmptyOrEnterPress(IndentRule yesRule, IndentRule noRule) { super(yesRule, noRule); }

  
  boolean applyRule(AbstractDJDocument doc, Indenter.IndentReason reason) {
    if (reason == Indenter.IndentReason.ENTER_KEY_PRESS) return true;
    
    int here = doc.getCurrentLocation();
    int endOfLine = doc._getLineEndPos(here);
    int firstNonWS = doc._getLineFirstCharPos(here);
    return (endOfLine == firstNonWS);
  }
}
