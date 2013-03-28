

package edu.rice.cs.drjava.model.definitions.indent;

import edu.rice.cs.drjava.model.AbstractDJDocument;


public class ActionDoNothing extends IndentRuleAction {
  
  
  
  
  
  public boolean indentLine(AbstractDJDocument doc, Indenter.IndentReason reason) {
    return super.indentLine(doc, reason);
  }
}
