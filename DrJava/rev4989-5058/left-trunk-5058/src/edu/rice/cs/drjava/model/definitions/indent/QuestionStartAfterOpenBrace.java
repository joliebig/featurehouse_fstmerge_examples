

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.BadLocationException;
import edu.rice.cs.drjava.model.AbstractDJDocument;
import edu.rice.cs.drjava.model.definitions.reducedmodel.*;
import edu.rice.cs.util.UnexpectedException;


public class QuestionStartAfterOpenBrace extends IndentRuleQuestion {
  
  public QuestionStartAfterOpenBrace(IndentRule yesRule, IndentRule noRule) { super(yesRule, noRule); }
  
  
  boolean applyRule(AbstractDJDocument doc, Indenter.IndentReason reason)  {
    
    int origin = doc.getCurrentLocation();
    int lineStart = doc._getLineStartPos(origin);
    
    if (lineStart <= 1) return false;  
    
    doc.setCurrentLocation(lineStart);
    BraceInfo info = doc._getLineEnclosingBrace();
    doc.setCurrentLocation(origin);    
    
    if (! info.braceType().equals(BraceInfo.OPEN_CURLY) || info.distance() <= 0)
      
      return false;
    int bracePos = lineStart - info.distance();    
    

    int braceEndLinePos = doc._getLineEndPos(bracePos);
    
    
    int nextNonWS = -1;

    try { nextNonWS = doc.getFirstNonWSCharPos(braceEndLinePos ); }
    catch (BadLocationException e) { throw new UnexpectedException(e); } 
    
    if (nextNonWS == -1) return true;
    
    return (nextNonWS >= lineStart);
  }
}
