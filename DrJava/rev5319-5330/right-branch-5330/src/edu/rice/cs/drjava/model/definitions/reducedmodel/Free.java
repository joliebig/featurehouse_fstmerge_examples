

package edu.rice.cs.drjava.model.definitions.reducedmodel;

import static edu.rice.cs.drjava.model.definitions.reducedmodel.ReducedModelStates.*;


public class Free extends ReducedModelState {
  public static final Free ONLY = new Free();
  
  private Free() { }
  
  
  ReducedModelState update(TokenList.Iterator copyCursor) {
    if (copyCursor.atEnd()) return STUTTER;
    
    _combineCurrentAndNextIfFind("/", "*", copyCursor);
    _combineCurrentAndNextIfFind("/", "/", copyCursor);
    _combineCurrentAndNextIfFind("","", copyCursor);
    
    _combineCurrentAndNextIfFind("/","/*",copyCursor);
    _combineCurrentAndNextIfFind("/","//",copyCursor);
    _combineCurrentAndNextIfEscape(copyCursor);
    
    String type = copyCursor.current().getType();
    if (type.equals("*/")) {
      copyCursor._splitCurrentIfCommentBlock(true,false);
      copyCursor.prev();
      return STUTTER;
    }
    else if (type.equals("//")) {
      
      copyCursor.current().setState(FREE);
      copyCursor.next();
      return INSIDE_LINE_COMMENT;
    }
    else if (type.equals("/*")) {
      
      copyCursor.current().setState(FREE);
      copyCursor.next();
      return INSIDE_BLOCK_COMMENT;
    }
    else if (type.equals("\'")) {
      
      if (copyCursor.current().isClosed()) {
        copyCursor.current().flip();
      }
      copyCursor.current().setState(FREE);
      copyCursor.next();
      return INSIDE_SINGLE_QUOTE;
    }
    else if (type.equals("\"")) {
      
      if (copyCursor.current().isClosed()) {
        copyCursor.current().flip();
      }
      copyCursor.current().setState(FREE);
      copyCursor.next();
      return INSIDE_DOUBLE_QUOTE;
    }
    else {
      copyCursor.current().setState(FREE);
      copyCursor.next();
      return FREE;
    }
  }
}
