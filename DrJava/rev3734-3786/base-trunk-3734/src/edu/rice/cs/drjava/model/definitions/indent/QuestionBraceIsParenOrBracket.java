

package edu.rice.cs.drjava.model.definitions.indent;

import edu.rice.cs.drjava.model.AbstractDJDocument;
import edu.rice.cs.drjava.model.definitions.reducedmodel.*;


public class QuestionBraceIsParenOrBracket extends IndentRuleQuestion {
  
  public QuestionBraceIsParenOrBracket(IndentRule yesRule, IndentRule noRule) {
    super(yesRule, noRule);
  }
  
  
  boolean applyRule(AbstractDJDocument doc, int reason) {
    

    IndentInfo info = doc.getIndentInformation();

    
    

    return info.braceType.equals(IndentInfo.openParen) 
        || info.braceType.equals(IndentInfo.openBracket); 
  }
}
