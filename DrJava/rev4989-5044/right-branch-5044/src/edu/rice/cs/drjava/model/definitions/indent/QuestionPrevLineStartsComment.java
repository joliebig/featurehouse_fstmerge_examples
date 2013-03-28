

package edu.rice.cs.drjava.model.definitions.indent;

import edu.rice.cs.drjava.model.AbstractDJDocument;
import edu.rice.cs.drjava.model.definitions.reducedmodel.*;


class QuestionPrevLineStartsComment extends IndentRuleQuestion {
  
  QuestionPrevLineStartsComment(IndentRule yesRule, IndentRule noRule) {
    super(yesRule, noRule);
  }
  
  
  boolean applyRule(AbstractDJDocument doc, Indenter.IndentReason reason) {

      int cursor;

    
    cursor = doc._getLineStartPos(doc.getCurrentLocation());
    
    
    if (cursor == 0) return false;
    
    
    cursor = cursor - 1;
    
    
    cursor = doc._getLineStartPos(cursor);
    
    

    doc.resetReducedModelLocation();
    ReducedModelState state = doc.stateAtRelLocation(cursor - doc.getCurrentLocation());
    return ! state.equals(ReducedModelStates.INSIDE_BLOCK_COMMENT);
  }
}

