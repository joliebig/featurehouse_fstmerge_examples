

package edu.rice.cs.drjava.model.definitions.indent;

import edu.rice.cs.drjava.model.AbstractDJDocument;
import edu.rice.cs.util.UnexpectedException;

import javax.swing.text.BadLocationException;


public class ActionStartCurrStmtPlus extends IndentRuleAction {
  private String _suffix;

  
  public ActionStartCurrStmtPlus(String suffix) {
    super();
    _suffix = suffix;
  }

  
  public boolean indentLine(AbstractDJDocument doc, int reason) {
    boolean supResult = super.indentLine(doc, reason);

    

    String indent = "";

    try {
      indent = doc.getIndentOfCurrStmt(doc.getCurrentLocation(),
                                       new char[] {';','{','}'},
                                       new char[] {' ', '\t','\n'});
    } catch (BadLocationException e) {
      throw new UnexpectedException(e);
    }

    indent = indent + _suffix;
    doc.setTab(indent, doc.getCurrentLocation());
    
    return supResult;
  }
}
