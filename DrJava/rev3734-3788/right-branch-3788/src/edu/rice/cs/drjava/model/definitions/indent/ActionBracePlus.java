

package edu.rice.cs.drjava.model.definitions.indent;

import edu.rice.cs.drjava.model.AbstractDJDocument;
import edu.rice.cs.drjava.model.definitions.reducedmodel.IndentInfo;


public class ActionBracePlus extends IndentRuleAction {
  
  private String _suffix;

  
  public ActionBracePlus(String suffix) { _suffix = suffix; }

  
  public boolean indentLine(AbstractDJDocument doc, int reason) {
    boolean supResult = super.indentLine(doc, reason);
    int here = doc.getCurrentLocation();
    int startLine = doc.getLineStartPos(here);
    doc.setCurrentLocation(startLine);
    IndentInfo ii = doc.getIndentInformation();

    
    if ((ii.braceType.equals("")) ||
        (ii.distToBrace < 0)) {
      
      return supResult;
    }

    
    int bracePos = startLine - ii.distToBrace;
    int braceNewLine = 0;
    if (ii.distToNewline >=0) {
      braceNewLine = startLine - ii.distToNewline;
    }
    int braceLen = bracePos - braceNewLine;

    
    StringBuffer tab = new StringBuffer(_suffix.length() + braceLen);
    for (int i=0; i < braceLen; i++) {
      tab.append(" ");
    }
    tab.append(_suffix);

    if (here > doc.getLength()) {
      here = doc.getLength() - 1;
    }
    doc.setCurrentLocation(here);

    doc.setTab(tab.toString(), here);
    
    return supResult;
  }
}
