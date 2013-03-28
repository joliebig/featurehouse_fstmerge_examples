

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.*;
import edu.rice.cs.util.UnexpectedException;

import edu.rice.cs.drjava.model.AbstractDJDocument;


public class QuestionFollowedByStar extends IndentRuleQuestion {
  
  
  public QuestionFollowedByStar(IndentRule yesRule, IndentRule noRule) { super(yesRule, noRule); }
  
  
  boolean applyRule(AbstractDJDocument doc, Indenter.IndentReason reason) {
    try {
      int charPos = doc.getFirstNonWSCharPos(doc.getCurrentLocation(), true);
      return (charPos != -1) && doc.getText(charPos, 1).equals("*");
    }
    catch (BadLocationException ble) { throw new UnexpectedException(ble); }
  }
}

