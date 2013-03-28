

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.BadLocationException;

import edu.rice.cs.drjava.model.AbstractDJDocument;


public class QuestionExistsCharInPrevStmt extends IndentRuleQuestion {
  
  private char _lookFor;
  
  public QuestionExistsCharInPrevStmt(char lookFor, IndentRule yesRule, IndentRule noRule) {
    super(yesRule, noRule);
    _lookFor = lookFor;
  }
  
  
  boolean applyRule(AbstractDJDocument doc, Indenter.IndentReason reason) {
    
    int endPreviousStatement;
    try { endPreviousStatement = doc.findPrevDelimiter(doc.getCurrentLocation(), new char[] {';','}','{'}); } 
    catch (BadLocationException ble) {
      
      return false;
    }
    
    
    if (endPreviousStatement == -1) return false;
    
      
    return doc.findCharInStmtBeforePos(_lookFor, endPreviousStatement);
  }
}