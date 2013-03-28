

package edu.rice.cs.drjava.model.definitions.indent;

import edu.rice.cs.drjava.model.AbstractDJDocument;


public interface IndentRule {
  
  public boolean indentLine(AbstractDJDocument doc, Indenter.IndentReason reason);
}
