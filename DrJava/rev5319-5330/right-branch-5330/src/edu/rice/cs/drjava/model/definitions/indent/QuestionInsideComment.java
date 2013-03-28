

package edu.rice.cs.drjava.model.definitions.indent;

import edu.rice.cs.drjava.model.AbstractDJDocument;


class QuestionInsideComment extends IndentRuleQuestion {
  
  QuestionInsideComment(final IndentRule yesRule, final IndentRule noRule) { super(yesRule, noRule); }
  
  
  boolean applyRule(AbstractDJDocument doc, Indenter.IndentReason reason) { 
    return doc._inBlockComment(doc.getCurrentLocation()); 
  }
}
