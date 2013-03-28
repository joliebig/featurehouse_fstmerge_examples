

package edu.rice.cs.drjava.model.definitions.indent;

import edu.rice.cs.util.UnexpectedException;

import edu.rice.cs.drjava.model.AbstractDJDocument;
import edu.rice.cs.drjava.model.definitions.reducedmodel.*;

import javax.swing.text.BadLocationException;


public class ActionStartStmtOfBracePlus extends IndentRuleAction {
  private String _suffix;

  
  public ActionStartStmtOfBracePlus(String suffix) {
    super();
    _suffix = suffix;
  }

  
  public boolean indentLine(AbstractDJDocument doc, int reason) {
    boolean supResult = super.indentLine(doc, reason);
    int pos = doc.getCurrentLocation();

    
    IndentInfo info = doc.getIndentInformation();
    int distToBrace = info.distToBrace;

    
    if (distToBrace == -1) {
      doc.setTab(_suffix, pos);
      return supResult;
    }

    
    int bracePos = pos - distToBrace;

    String indent = "";
    try {
      indent = doc.getIndentOfCurrStmt(bracePos);
    } catch (BadLocationException e) {
      
      throw new UnexpectedException(e);
    }
    indent = indent + _suffix;

    doc.setTab(indent, pos);
    
    return supResult;
  }

}
