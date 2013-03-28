

package edu.rice.cs.drjava.model.definitions.reducedmodel;

import static edu.rice.cs.drjava.model.definitions.reducedmodel.ReducedModelStates.*;


public class InsideDoubleQuote extends ReducedModelState {
  
  public static final InsideDoubleQuote ONLY = new InsideDoubleQuote();
  
  private InsideDoubleQuote() { }
  
  
  ReducedModelState update(TokenList.Iterator copyCursor) {
    if (copyCursor.atEnd()) {
      return STUTTER;
    }
    copyCursor._splitCurrentIfCommentBlock(true,false);
    _combineCurrentAndNextIfFind("","", copyCursor);
    _combineCurrentAndNextIfEscape(copyCursor);
    String type = copyCursor.current().getType();
    
    if (type.equals("\n")) {
      copyCursor.current().setState(FREE);
      copyCursor.next();
      return FREE;
    }
    else if (type.equals("\"")) {
      
      if (copyCursor.current().isOpen())
        copyCursor.current().flip();
      
      copyCursor.current().setState(FREE);
      copyCursor.next();
      return FREE;
    }
    else {
      copyCursor.current().setState(INSIDE_DOUBLE_QUOTE);
      copyCursor.next();
      return INSIDE_DOUBLE_QUOTE;
    }
  }
}
