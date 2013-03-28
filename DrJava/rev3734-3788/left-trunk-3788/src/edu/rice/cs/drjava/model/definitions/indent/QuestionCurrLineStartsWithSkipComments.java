

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.*;
import edu.rice.cs.util.UnexpectedException;

import edu.rice.cs.drjava.model.AbstractDJDocument;
import edu.rice.cs.drjava.model.definitions.reducedmodel.*;


public class QuestionCurrLineStartsWithSkipComments extends IndentRuleQuestion {
  
  private String _prefix;
  
  
  public QuestionCurrLineStartsWithSkipComments(String prefix, IndentRule yesRule, IndentRule noRule) {
    super(yesRule, noRule);
    _prefix = prefix;
  }
  
  
  boolean applyRule(AbstractDJDocument doc, int reason) {
    try {
      
      
      int currentPos = doc.getCurrentLocation(),
        startPos   = doc.getLineFirstCharPos(currentPos),
        endPos     = doc.getLineEndPos(currentPos),
        lineLength = endPos - startPos;
      
      char currentChar, previousChar = '\0';
      String text = doc.getText(startPos, lineLength);
      
      for (int i = 0; i < lineLength; i++) {
        
        

        doc.move( startPos - currentPos + i);
        ReducedModelState state = doc.getStateAtCurrent();
        doc.move(-startPos + currentPos - i);
        
        
        currentChar = text.charAt(i);
        
        if (state.equals(ReducedModelState.INSIDE_LINE_COMMENT)) return false;
        if (state.equals(ReducedModelState.INSIDE_BLOCK_COMMENT)) {  
          previousChar = '\0'; 
          continue;
        }
        if (state.equals(ReducedModelState.FREE)) { 
          if (_prefix.length() > lineLength - i) return false;
          else if (text.substring(i, i+_prefix.length()).equals(_prefix) && previousChar != '/') {
            
            
            
            return true;
          }
          else if (currentChar == '/') {
            if (previousChar == '/') return false;
          }
          else if (currentChar == ' ' || currentChar == '\t') {  }
          else if (!(currentChar == '*' && previousChar == '/')) return false;
        }
        if (previousChar == '/' && currentChar != '*') return false;
        previousChar = currentChar;
      }
      return false;
    }
    catch (BadLocationException e) {
      
      throw new UnexpectedException(new RuntimeException("Bug in QuestionCurrLineStartsWithSkipComments"));
    }
  }
}
