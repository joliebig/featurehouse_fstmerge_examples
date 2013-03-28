

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.*;
import edu.rice.cs.util.UnexpectedException;

import edu.rice.cs.drjava.model.AbstractDJDocument;


public class QuestionPrevLineStartsJavaDocWithText extends IndentRuleQuestion {
  
  
  public QuestionPrevLineStartsJavaDocWithText(IndentRule yesRule, IndentRule noRule) { super(yesRule, noRule); }
  
  
  boolean applyRule(AbstractDJDocument doc, Indenter.IndentReason reason) {

    try {
      
      int here = doc.getCurrentLocation();
      int startLine = doc._getLineStartPos(here);
      
      if (startLine <= 0) return false;  
      
      
      int endPrevLine = startLine - 1;
      int startPrevLine = doc._getLineStartPos(endPrevLine);
      int firstChar = doc._getLineFirstCharPos(startPrevLine);
      
      
      String actualPrefix = doc.getText(firstChar, 3);
      if (! actualPrefix.equals("/**")) return false;
      int nextNonWSChar = doc.getFirstNonWSCharPos(firstChar + 3, true);
      return nextNonWSChar != -1 && nextNonWSChar <= endPrevLine;     
    }
    catch (BadLocationException e) {
      
      throw new UnexpectedException(e);
    }
  }
}
