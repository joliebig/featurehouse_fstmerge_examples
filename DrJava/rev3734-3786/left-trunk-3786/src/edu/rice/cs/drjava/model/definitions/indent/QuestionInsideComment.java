

package edu.rice.cs.drjava.model.definitions.indent;

import edu.rice.cs.drjava.model.AbstractDJDocument;
import edu.rice.cs.drjava.model.definitions.reducedmodel.*;


class QuestionInsideComment extends IndentRuleQuestion {
  
  QuestionInsideComment(final IndentRule yesRule, final IndentRule noRule) {
    super(yesRule, noRule);
  }

  
  boolean applyRule(AbstractDJDocument doc, int reason) {

    int here = doc.getCurrentLocation();
    int distToStart = here - doc.getLineStartPos(here);
    doc.resetReducedModelLocation();
    ReducedModelState state = doc.stateAtRelLocation(-distToStart);
    
    return (state.equals(ReducedModelStates.INSIDE_BLOCK_COMMENT));
  }
}
