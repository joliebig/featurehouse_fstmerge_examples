

package edu.rice.cs.drjava.model.definitions.indent;

import edu.rice.cs.drjava.model.AbstractDJDocument;
import edu.rice.cs.drjava.model.definitions.reducedmodel.*;


public class ActionStartStmtOfBracePlus extends IndentRuleAction {
  private int _suffix;
  
  
  public ActionStartStmtOfBracePlus(int suffix) {
    super();
    _suffix = suffix;
  }

  
  public boolean indentLine(AbstractDJDocument doc, Indenter.IndentReason reason) {

    boolean supResult = super.indentLine(doc, reason); 
    int pos = doc.getCurrentLocation();

    
    int lineStart = doc._getLineStartPos(pos);
    if (lineStart < 0) lineStart = 0;
    BraceInfo info = doc._getLineEnclosingBrace();
    int distToLineEnclosingBrace = info.distance();


    
    if (distToLineEnclosingBrace == -1) {
      doc.setTab(_suffix, pos);
      return supResult;
    }

    
    final int bracePos = lineStart - distToLineEnclosingBrace;

    final int indent = doc._getIndentOfCurrStmt(bracePos) + _suffix;


    doc.setTab(indent, pos);
    
    return supResult;
  }

}
