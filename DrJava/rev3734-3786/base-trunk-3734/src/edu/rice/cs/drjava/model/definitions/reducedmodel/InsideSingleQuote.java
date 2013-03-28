

package edu.rice.cs.drjava.model.definitions.reducedmodel;


public class InsideSingleQuote extends ReducedModelState {
  
  public static final InsideSingleQuote ONLY = new InsideSingleQuote();

  
  private InsideSingleQuote() {
  }

  
  ReducedModelState update(TokenList.Iterator copyCursor) {
    if (copyCursor.atEnd()) {
      return STUTTER;
    }
    copyCursor._splitCurrentIfCommentBlock(true, false);
    _combineCurrentAndNextIfFind("", "", copyCursor);
    _combineCurrentAndNextIfEscape(copyCursor);

    String type = copyCursor.current().getType();

    if (type.equals("\n")) {
      copyCursor.current().setState(FREE);
      copyCursor.next();
      return FREE;
    }
    else if (type.equals("\'")) {
      
      if (copyCursor.current().isOpen()) {
        copyCursor.current().flip();
      }

      copyCursor.current().setState(FREE);
      copyCursor.next();
      return FREE;
    }
    else {
      copyCursor.current().setState(INSIDE_SINGLE_QUOTE);
      copyCursor.next();
      return INSIDE_SINGLE_QUOTE;
    }
  }
}
