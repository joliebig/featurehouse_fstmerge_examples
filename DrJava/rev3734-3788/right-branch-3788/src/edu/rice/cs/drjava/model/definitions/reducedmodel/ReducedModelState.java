

package edu.rice.cs.drjava.model.definitions.reducedmodel;


public abstract class ReducedModelState implements  ReducedModelStates {
  
  abstract ReducedModelState update(TokenList.Iterator copyCursor);

  
  boolean _combineCurrentAndNextIfFind(String first, String second, TokenList.Iterator copyCursor) {
    if (copyCursor.atStart() || copyCursor.atEnd() || copyCursor.atLastItem() ||
        !copyCursor.current().getType().equals(first))
      return false;

    copyCursor.next(); 

    
    
    if (copyCursor.current().getType().equals(second)) {
      if (copyCursor.current().getType().equals("") && copyCursor.prevItem().getType().equals("")) {
        
        copyCursor.prev();
        int growth = copyCursor.current().getSize();
        copyCursor.remove();
        copyCursor.current().grow(growth);
      }
      else if (copyCursor.current().getType().length() == 2) {
        String tail = copyCursor.current().getType().substring(1,2);
        String head = copyCursor.prevItem().getType() +
          copyCursor.current().getType().substring(0,1);
        copyCursor.current().setType(tail);
        copyCursor.prev();
        copyCursor.current().setType(head);
        copyCursor.current().setState(FREE);
      }
      else {
        
        copyCursor.prev();
        copyCursor.remove();
        copyCursor.current().setType(first + second);
      }
      return true;
    }
    else {
      
      copyCursor.prev();
      return false;
    }
  }

  boolean _combineCurrentAndNextIfEscape(TokenList.Iterator copyCursor) {
    boolean combined = _combineCurrentAndNextIfFind("\\","\\",copyCursor);  
    combined = combined || _combineCurrentAndNextIfFind("\\","\'",copyCursor);  
    combined = combined || _combineCurrentAndNextIfFind("\\","\\'",copyCursor);
    combined = combined || _combineCurrentAndNextIfFind("\\","\"",copyCursor);  
    combined = combined || _combineCurrentAndNextIfFind("\\","\\\"",copyCursor);
    combined = combined || _combineCurrentAndNextIfFind("\\","\\\\",copyCursor);
    return combined;
  }
}
