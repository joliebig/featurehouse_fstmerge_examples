

package edu.rice.cs.drjava.model.definitions.indent;

import edu.rice.cs.drjava.model.AbstractDJDocument;


public class ActionStartCurrStmtPlus extends IndentRuleAction {
  private int _suffix;

  
  public ActionStartCurrStmtPlus(int suffix) {
    super();
    _suffix = suffix;
  }

  
  public boolean indentLine(AbstractDJDocument doc, Indenter.IndentReason reason) {
    boolean supResult = super.indentLine(doc, reason);

    

    int indent = 0;


    indent = doc._getIndentOfCurrStmt(doc.getCurrentLocation(), new char[] {';','{','}'}, new char[] {' ', '\t','\n'});


    indent = indent + _suffix;
    doc.setTab(indent, doc.getCurrentLocation());
    
    return supResult;
  }
}
