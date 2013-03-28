

package edu.rice.cs.drjava.model.definitions.indent;

import edu.rice.cs.drjava.model.AbstractDJDocument;
import edu.rice.cs.drjava.model.definitions.reducedmodel.BraceInfo;


public class ActionBracePlus extends IndentRuleAction {
  
  private int _suffixCt;

  
  public ActionBracePlus(int ct) { _suffixCt = ct; }

  
  public boolean indentLine(AbstractDJDocument doc, Indenter.IndentReason reason) {
    boolean supResult = super.indentLine(doc, reason);
    int here = doc.getCurrentLocation();
    int startLine = doc._getLineStartPos(here);
    doc.setCurrentLocation(startLine);  
    BraceInfo info = doc._getLineEnclosingBrace();
    int dist = info.distance();

    
    if (info.braceType().equals("") || dist < 0) {  
      
      return supResult;
    }

    
    int bracePos = startLine - dist;
    
    int braceNewline = doc._getLineStartPos(bracePos);
    int braceIndent = bracePos - braceNewline;

    
    final int tab = _suffixCt + braceIndent;

    if (here > doc.getLength()) here = doc.getLength() - 1;
    doc.setCurrentLocation(here);

    doc.setTab(tab, here);
    
    return supResult;
  }
}
