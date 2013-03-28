

package edu.rice.cs.drjava.model.definitions.indent;

import edu.rice.cs.drjava.model.AbstractDJDocument;
import edu.rice.cs.drjava.model.definitions.reducedmodel.*;

import static edu.rice.cs.drjava.model.definitions.reducedmodel.ReducedModelStates.*;


public class QuestionCurrLineStartsWithSkipComments extends IndentRuleQuestion {
  
  private String _prefix;
  
  
  public QuestionCurrLineStartsWithSkipComments(String prefix, IndentRule yesRule, IndentRule noRule) {
    super(yesRule, noRule);
    _prefix = prefix;
  }
  
  
  boolean applyRule(AbstractDJDocument doc, Indenter.IndentReason reason) {
    
    
    int origPos = doc.getCurrentLocation();
    int startPos   = doc._getLineFirstCharPos(origPos);
    int endPos     = doc._getLineEndPos(origPos);
    int lineLength = endPos - startPos;
    
    char prevChar = '\0';
    String text = doc._getText(startPos, lineLength);

    
    doc.setCurrentLocation(startPos);
    try { 
      for (int i = 0; i < lineLength; i++, doc.move(1)) {
        
        ReducedModelState state = doc.getStateAtCurrent();
        
        if (state.equals(INSIDE_BLOCK_COMMENT)) {  
          assert prevChar == '\0'; 
          continue;
        }
        char currentChar = text.charAt(i);

        
        if (currentChar == '/') {
          if (prevChar == '/') return false;  
          if (prevChar == '\0') {
            prevChar = currentChar;
            continue;     
          }
        }
        else if (currentChar == '*' && prevChar == '/') { 
          prevChar = '\0';
          continue;      
        }
        else if (currentChar == ' ' || currentChar == '\t') {  
          if (prevChar == '\0') {
            continue;  
          }
        }
        return text.startsWith(_prefix, i);   
      }
    }
    finally { doc.setCurrentLocation(origPos); }
    return false;
  }
}
