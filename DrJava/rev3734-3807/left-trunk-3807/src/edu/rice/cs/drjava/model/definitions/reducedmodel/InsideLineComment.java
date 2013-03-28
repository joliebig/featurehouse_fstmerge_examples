

package edu.rice.cs.drjava.model.definitions.reducedmodel;


public class InsideLineComment extends ReducedModelState {
  public static final InsideLineComment ONLY = new InsideLineComment();

  private InsideLineComment() { }

    
  ReducedModelState update(TokenList.Iterator copyCursor) {
    if (copyCursor.atEnd())  return STUTTER;
    copyCursor._splitCurrentIfCommentBlock(true, false);
    _combineCurrentAndNextIfFind("","", copyCursor);
    _combineCurrentAndNextIfEscape(copyCursor);

    String type = copyCursor.current().getType();

    if (type.equals("\n")) {
      copyCursor.current().setState(FREE);
      copyCursor.next();
      return FREE;
    }
    else {
      copyCursor.current().setState(INSIDE_LINE_COMMENT);
      copyCursor.next();
      return INSIDE_LINE_COMMENT;
    }
  }
}
