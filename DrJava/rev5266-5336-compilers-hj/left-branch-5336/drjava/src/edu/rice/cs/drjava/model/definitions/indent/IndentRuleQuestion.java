

package edu.rice.cs.drjava.model.definitions.indent;

import edu.rice.cs.drjava.model.AbstractDJDocument;


public abstract class IndentRuleQuestion extends IndentRuleWithTrace {
  
  private final IndentRule _yesRule;
  
  
  private final IndentRule _noRule;
  
  
  public IndentRuleQuestion(final IndentRule yesRule, final IndentRule noRule) {
    _yesRule = yesRule;
    _noRule = noRule;
  }
  
  
  abstract boolean applyRule(AbstractDJDocument doc, Indenter.IndentReason reason);
  
  
  boolean applyRule(AbstractDJDocument doc, int pos, Indenter.IndentReason reason) {
    int oldPos = doc.getCurrentLocation();
    doc.setCurrentLocation(pos);
    boolean result = applyRule(doc, reason);
    if (oldPos > doc.getLength()) oldPos = doc.getLength();
    doc.setCurrentLocation(oldPos);
    return result;
  }
  
  
  public boolean indentLine(AbstractDJDocument doc, Indenter.IndentReason reason) {
    if (applyRule(doc, reason)) {
      _addToIndentTrace(getRuleName(), YES, false);
      return _yesRule.indentLine(doc, reason);
    }
    else {
      _addToIndentTrace(getRuleName(), NO, false);
      return _noRule.indentLine(doc, reason);
    }
  }
  
  
  boolean testApplyRule(AbstractDJDocument doc, Indenter.IndentReason reason) { return applyRule(doc, reason); }
  
  
  boolean testApplyRule(AbstractDJDocument doc, int pos, Indenter.IndentReason reason) {
    return applyRule(doc, pos, reason); 
  }
  
  
  public boolean testIndentLine(AbstractDJDocument doc, Indenter.IndentReason reason) {
    return indentLine(doc, reason); 
  }
}





