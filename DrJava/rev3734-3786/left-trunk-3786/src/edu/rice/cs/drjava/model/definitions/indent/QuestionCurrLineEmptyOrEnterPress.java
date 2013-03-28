

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.*;

import edu.rice.cs.drjava.model.AbstractDJDocument;

import edu.rice.cs.util.UnexpectedException;


class QuestionCurrLineEmptyOrEnterPress extends IndentRuleQuestion {
  
  QuestionCurrLineEmptyOrEnterPress(IndentRule yesRule, IndentRule noRule) {
    super(yesRule, noRule);
  }

  
  boolean applyRule(AbstractDJDocument doc, int reason) {
    if (reason == Indenter.ENTER_KEY_PRESS) return true;
    try {
      
      
      int here = doc.getCurrentLocation();
      int endOfLine = doc.getLineEndPos(here);
      int firstNonWS = doc.getLineFirstCharPos(here);
      return (endOfLine == firstNonWS);
    }
    catch (BadLocationException e) {
      
      throw new UnexpectedException(e);
    }
  }
}
