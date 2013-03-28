

package edu.rice.cs.drjava.model.definitions.indent;

import edu.rice.cs.drjava.model.AbstractDJDocument;


public class QuestionExistsCharInStmt extends IndentRuleQuestion {
  
  private char _findChar;
  
  
  private char _endChar;
  
  
  public QuestionExistsCharInStmt(char findChar, char endChar, IndentRule yesRule, IndentRule noRule) {
    super(yesRule, noRule);
    _findChar = findChar;
    _endChar = endChar;
  }
  
  
  boolean applyRule(AbstractDJDocument doc, Indenter.IndentReason reason) {
    
    
    int endCharPos = doc.findCharOnLine(doc.getCurrentLocation(), _endChar);
    return doc.findCharInStmtBeforePos(_findChar, endCharPos);
  }
}
