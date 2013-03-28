

package edu.rice.cs.drjava.model.definitions.reducedmodel;

import static edu.rice.cs.drjava.model.definitions.reducedmodel.ReducedModelStates.*;


public class InsideBlockComment extends ReducedModelState {
  
  public static final InsideBlockComment ONLY = new InsideBlockComment();
  
  private InsideBlockComment() { }
  
  
  ReducedModelState update(TokenList.Iterator copyCursor) {
    if (copyCursor.atEnd()) return STUTTER;
    _combineCurrentAndNextIfFind("*", "/", copyCursor);
    _combineCurrentAndNextIfFind("*","//", copyCursor);
    _combineCurrentAndNextIfFind("*","/*", copyCursor);
    _combineCurrentAndNextIfFind("","", copyCursor);    
    _combineCurrentAndNextIfEscape(copyCursor);                                              
        
    copyCursor._splitCurrentIfCommentBlock(false, false);
    
    String type = copyCursor.current().getType();
    if (type.equals("*/")) {
      copyCursor.current().setState(FREE);
      copyCursor.next();
      return FREE;
    }
    
    else {
      copyCursor.current().setState(INSIDE_BLOCK_COMMENT);
      copyCursor.next();
      return INSIDE_BLOCK_COMMENT;
    }
  }
}
