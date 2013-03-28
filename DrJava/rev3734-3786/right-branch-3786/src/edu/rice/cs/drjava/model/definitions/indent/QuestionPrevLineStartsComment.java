



package edu.rice.cs.drjava.model.definitions.indent;

import edu.rice.cs.drjava.model.AbstractDJDocument;
import edu.rice.cs.drjava.model.definitions.reducedmodel.*;


class QuestionPrevLineStartsComment extends IndentRuleQuestion {
  
  QuestionPrevLineStartsComment(IndentRule yesRule, IndentRule noRule) {
    super(yesRule, noRule);
  }
  
  
  boolean applyRule(AbstractDJDocument doc, int reason) {

      int cursor;

    
    cursor = doc.getLineStartPos(doc.getCurrentLocation());
    
    
    
    
    if (cursor == AbstractDJDocument.DOCSTART) {
      return false;
    } else {
      
      cursor = cursor - 1;
      
      
      cursor = doc.getLineStartPos(cursor);
      
      
      
      
      doc.resetReducedModelLocation();
      ReducedModelState state = doc.stateAtRelLocation(cursor -
          doc.getCurrentLocation());
      return !state.equals(ReducedModelState.INSIDE_BLOCK_COMMENT);
    }
  }
}

