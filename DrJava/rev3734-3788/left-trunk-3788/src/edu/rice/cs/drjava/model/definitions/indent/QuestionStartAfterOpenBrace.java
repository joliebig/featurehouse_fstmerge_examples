

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.BadLocationException;
import edu.rice.cs.drjava.model.AbstractDJDocument;
import edu.rice.cs.drjava.model.definitions.reducedmodel.*;
import edu.rice.cs.util.UnexpectedException;


public class QuestionStartAfterOpenBrace extends IndentRuleQuestion {
  
  public QuestionStartAfterOpenBrace(IndentRule yesRule, IndentRule noRule) { super(yesRule, noRule); }
  
  
  boolean applyRule(AbstractDJDocument doc, int reason)  {
    
    int origin = doc.getCurrentLocation();
    
    int lineStart = doc.getLineStartPos(doc.getCurrentLocation());
    
    
    doc.move(lineStart - origin);
    IndentInfo info = doc.getIndentInformation();
    doc.move(origin - lineStart);    
    
    if ((!info.braceType.equals(IndentInfo.openSquiggly)) ||
        (info.distToBrace < 0))
      
      return false;
    int bracePos = lineStart - info.distToBrace;    
    
    
    int braceEndLinePos = doc.getLineEndPos(bracePos);
    
    
    int nextNonWS = -1;
    try { nextNonWS = doc.getFirstNonWSCharPos(braceEndLinePos); }
    catch (BadLocationException e) {
      
      throw new UnexpectedException(e);
    }
    
    if (nextNonWS == AbstractDJDocument.ERROR_INDEX) return true;
    
    return (nextNonWS >= lineStart);
  }
}
