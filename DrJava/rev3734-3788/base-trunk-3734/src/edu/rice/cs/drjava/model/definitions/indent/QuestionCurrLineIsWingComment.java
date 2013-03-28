

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.BadLocationException;

import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.drjava.model.AbstractDJDocument;



public class QuestionCurrLineIsWingComment extends IndentRuleQuestion {
  
  
  public QuestionCurrLineIsWingComment(IndentRule yesRule, IndentRule noRule) { super(yesRule, noRule); }
  
  
  boolean applyRule(AbstractDJDocument doc, int reason) {
    try {
      
      
      int currentPos = doc.getCurrentLocation();
      int startPos   = doc.getLineStartPos(currentPos);
      int maxPos     = doc.getLength();
      int diff       = maxPos - startPos;
      
      if (diff < 2) return false;
      
      String text = doc.getText(startPos, 2);
      
      return text.equals("//");
    }
    catch (BadLocationException e) { throw new UnexpectedException();
    }
  }
  
}