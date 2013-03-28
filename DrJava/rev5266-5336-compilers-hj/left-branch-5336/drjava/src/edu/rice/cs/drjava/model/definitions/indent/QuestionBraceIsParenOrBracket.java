

package edu.rice.cs.drjava.model.definitions.indent;

import edu.rice.cs.drjava.model.AbstractDJDocument;
import edu.rice.cs.drjava.model.definitions.reducedmodel.*;


public class QuestionBraceIsParenOrBracket extends IndentRuleQuestion {
  
  public QuestionBraceIsParenOrBracket(IndentRule yesRule, IndentRule noRule) { super(yesRule, noRule); }
  
  
  protected boolean applyRule(AbstractDJDocument doc, Indenter.IndentReason reason) {
    
    






    
    BraceInfo info = doc._getLineEnclosingBrace();
    String braceType = info.braceType();
    return braceType.equals(BraceInfo.OPEN_PAREN) || braceType.equals(BraceInfo.OPEN_BRACKET); 
  }
}
