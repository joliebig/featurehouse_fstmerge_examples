

package edu.rice.cs.drjava.model.definitions.indent;

import edu.rice.cs.drjava.model.AbstractDJDocument;
import edu.rice.cs.drjava.model.definitions.reducedmodel.*;


public class QuestionBraceIsCurly extends IndentRuleQuestion 
{
  
  public QuestionBraceIsCurly(IndentRule yesRule, IndentRule noRule)
  {
    super(yesRule, noRule);
  }
  
  
  boolean applyRule(AbstractDJDocument doc, int reason)
  {
    
    

    IndentInfo info = doc.getIndentInformation();

    return info.braceType.equals(IndentInfo.openSquiggly);
  }
}
