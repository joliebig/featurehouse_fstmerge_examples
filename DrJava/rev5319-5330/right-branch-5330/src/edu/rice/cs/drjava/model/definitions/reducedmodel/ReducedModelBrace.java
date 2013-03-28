

package edu.rice.cs.drjava.model.definitions.reducedmodel;

import java.util.Stack;


public class ReducedModelBrace extends AbstractReducedModel {
  
  private ReducedModelControl _parent;  
  
  public ReducedModelBrace(ReducedModelControl parent) {
    super();
    _parent = parent;
  }
  
  public void insertChar(char ch) {
    switch(ch) {
      case '{':
      case '}':
      case '[':
      case ']':
      case '(':
      case ')':
        _insertBrace(String.valueOf(ch));
        break;
      default:
        _insertGap(1);
        break;
    }
  }
  
  
  private void _insertBrace(String text) {
    if (_cursor.atStart() || _cursor.atEnd()) _cursor.insertNewBrace(text); 
    else if (current().isGap()) _cursor.insertBraceToGap(text);
    else _cursor.insertNewBrace(text);
  }
  
  
  protected void insertGapBetweenMultiCharBrace(int length) {
    throw new RuntimeException("ReducedModelBrace does not keep track of multi-character braces.");
  }
  
  
  public void move(int count) { _cursor.move(count); }
  
  
  public void delete(int count) {
    if (count == 0) return;
    _cursor.delete(count);
    return;
  }
  
  
  private boolean _isCurrentBraceMatchable() { return _cursor.current().isMatchable(); }
  
  public boolean isShadowed() { return _parent.isShadowed(); }
  
  
  public int previousBrace() {
    int relDistance;
    int dist = 0;
    resetWalkerLocationToCursor(); 
    
    TokenList.Iterator copyCursor = _cursor.copy();
    if (! copyCursor.atStart()) copyCursor.prev();
    
    if (copyCursor.atStart()) {
      copyCursor.dispose();
      return -1;
    }
    
    
    dist += _cursor.getBlockOffset();
    relDistance = dist;
    
    
    
    while (! copyCursor.atStart()) {
      if (! copyCursor.current().isGap()) {
        if (moveWalkerGetState(-relDistance) == FREE) {
          copyCursor.dispose();
          return dist + copyCursor.current().getSize();
        }
        relDistance = 0;
      }
      
      dist += copyCursor.current().getSize();
      relDistance += copyCursor.current().getSize();
      copyCursor.prev();
    }
    copyCursor.dispose();
    return -1;
  }
  
  
  public int nextBrace() {
    int relDistance = 0;
    int dist = 0;
    TokenList.Iterator copyCursor = _cursor.copy();
    
    resetWalkerLocationToCursor();
    
    if (copyCursor.atStart()) copyCursor.next();
    int offset = getBlockOffset();
    if (offset > 0) {
      dist = copyCursor.current().getSize() - offset;
      relDistance = dist;
      copyCursor.next();
    }
    
    while (! copyCursor.atEnd()) {
      if (! copyCursor.current().isGap()) {
        if (moveWalkerGetState(relDistance) == FREE) {
          copyCursor.dispose();
          return dist;
        }
        relDistance = 0;
      }
      relDistance += copyCursor.current().getSize();
      dist += copyCursor.current().getSize();
      copyCursor.next();
    }
    copyCursor.dispose();
    return -1;
  }
  
  
  public int balanceForward() {

    resetWalkerLocationToCursor();

    
    Stack<Brace> braceStack = new Stack<Brace>();
    TokenList.Iterator iter = _cursor.copy();
    
    if (! openBraceImmediatelyLeft() || isShadowed()) {

      iter.dispose();


      return -1;
    }
    
    iter.prev();
    ReducedToken curToken = iter.current();
    
    assert curToken instanceof Brace; 
    

    

    braceStack.push((Brace) curToken);
    iter.next();  

    
    int relDistance = 0;  
    int distance = 0;     
    
    
    while (! iter.atEnd() && ! braceStack.isEmpty()) {
      curToken = iter.current(); 
      if (! curToken.isGap()) {  
        Brace curBrace = (Brace) curToken;
        ReducedModelState curBraceState = moveWalkerGetState(relDistance); 
        relDistance = 0;


        if (curBraceState == FREE && ! curToken.isCommentStart()) {  

          
          if (curBrace.isClosedBrace()) {

            Brace popped = braceStack.pop();
            if (! curBrace.isMatch(popped)) {
              iter.dispose();


              return -1;
            }
          }
          
          else {
            braceStack.push(curBrace);

          }
        }

      }
      
      int size = curToken.getSize();
      distance += size;     
      relDistance += size;

      
      iter.next();
    }
    
    
    if (! braceStack.isEmpty()) {
      iter.dispose();


      return -1;
    }
    
    else {
      iter.dispose();

      return distance;
    }
  }
  










  
  public boolean openBraceImmediatelyLeft() {
    if (_cursor.atStart() || _cursor.atFirstItem()) return false;
    else {
      int offset = getBlockOffset();
      prev();  
      assert offset == getBlockOffset();






      boolean isLeft = (getBlockOffset() == 0 && current().isOpen() && _isCurrentBraceMatchable());

      next();
      return isLeft;
    }
  }
  
  public boolean closedBraceImmediatelyLeft() {
    if (_cursor.atStart() || _cursor.atFirstItem()) return false;
    else {
      int offset = getBlockOffset();
      prev();  
      assert offset == getBlockOffset();






      boolean isLeft = (offset == 0 && current().isClosed() && _isCurrentBraceMatchable());

      next();  
      return isLeft;
    }
  }
  
  
  public int balanceBackward() {

    
    Stack<Brace> braceStack = new Stack<Brace>();
    TokenList.Iterator iter = _cursor.copy();
    resetWalkerLocationToCursor();  

    
    if (! closedBraceImmediatelyLeft() || isShadowed()) {

      iter.dispose();

      return -1;
    }
    
    int relDistance = 0; 
    int distance = 0;    
    
    
    iter.prev();   
    assert iter.current() instanceof Brace;  
    
    
    do {
      ReducedToken curToken = iter.current();
      int size = curToken.getSize();
      distance += size;
      relDistance += size;

      
      if (! curToken.isGap()) {  
        Brace curBrace = (Brace) curToken;
        ReducedModelState curBraceState = moveWalkerGetState(- relDistance);  
        relDistance = 0;


        if (curBraceState == FREE && ! curToken.isCommentStart()) { 

          if (curBrace.isOpenBrace()) {

            Brace popped = braceStack.pop();
            if (! curBrace.isMatch(popped)) {
              iter.dispose();

              return -1;
            }
          }
          else { 

            braceStack.push(curBrace);
          }
        }
      }

      iter.prev();
    }
    while (! iter.atStart() && ! braceStack.isEmpty());
    
    
    
    if (! braceStack.isEmpty()) {
      iter.dispose();


      return -1;
    }
    
    else {
      iter.dispose();

      return distance;
    }
  }
  
  protected ReducedModelState moveWalkerGetState(int relDistance) { return _parent.moveWalkerGetState(relDistance); }
  
  protected void resetWalkerLocationToCursor() { _parent.resetLocation(); }
  
  
  public BraceInfo _getLineEnclosingBrace() {
    Stack<Brace> braceStack = new Stack<Brace>();
    TokenList.Iterator iter = _cursor.copy();
    resetWalkerLocationToCursor();  
    
    final int distToStart = _parent.getDistToStart();

    
    if (distToStart == -1) {
      iter.dispose();
      return BraceInfo.NULL;
    }
    
    int relDistance = distToStart + 1;  
    int distance = 1;                   
    
    
    iter.move(-relDistance);

    final int offset = iter.getBlockOffset();
    relDistance += offset;
    distance += offset;
    
    if (iter.atStart() || iter.atFirstItem()) { 
      iter.dispose();
      return BraceInfo.NULL;
    }
    
    iter.prev(); 
    
    
    String braceType;
    
    
    
    
    
    while (! iter.atStart()) {
      
      ReducedToken curToken = iter.current();
      int size = curToken.getSize();
      distance += size;

      relDistance += size;
      
      if (! curToken.isGap()) {
        
        Brace curBrace = (Brace) curToken;
        
        if (moveWalkerGetState(-relDistance) == FREE && ! curToken.isCommentStart()) {
          
          if (curBrace.isOpenBrace()) {
            if (braceStack.isEmpty()) {
              braceType = curBrace.getType();
              
              iter.dispose();
              return new BraceInfo(braceType, distance);
            }
            Brace popped = braceStack.pop();
            if (! curBrace.isMatch(popped)) {
              iter.dispose();
              return BraceInfo.NULL;
            }
          }
          
          else braceStack.push(curBrace);
        }
        relDistance = 0;
      }
      
      
      iter.prev();
    }
    
    
    iter.dispose();
    return BraceInfo.NULL;
  }
  
  
  protected BraceInfo _getEnclosingBrace() {
    Stack<Brace> braceStack = new Stack<Brace>();
    TokenList.Iterator iter = _cursor.copy();
    resetWalkerLocationToCursor();
    int relDistance = 0;
    int distance = relDistance;
    
    
    
    int offset = iter.getBlockOffset();
    relDistance += offset;
    distance += offset;
    
    if (iter.atStart() || iter.atFirstItem()) {
      iter.dispose();
      return BraceInfo.NULL;
    }
    
    iter.prev();
    
    String braceType;
    
    
    
    while (! iter.atStart()) {
      
      ReducedToken curToken = iter.current();
      int size = curToken.getSize();
      distance += size;
      relDistance += size;
      
      if (! curToken.isGap()) {
        Brace curBrace = (Brace) curToken;
        if (moveWalkerGetState(-relDistance) == FREE && ! curToken.isCommentStart()) {
          
          if (curBrace.isOpenBrace()) {
            if (braceStack.isEmpty()) {
              braceType = curBrace.getType();
              iter.dispose();
              return new BraceInfo(braceType, distance);
            }
            Brace popped = braceStack.pop();
            if (! curBrace.isMatch(popped)) {
              iter.dispose();
              return BraceInfo.NULL;
            }
          }
          
          else braceStack.push(curBrace);
        }
        relDistance = 0;
      }
      
      
      iter.prev();
    }
    
    iter.dispose();
    return BraceInfo.NULL;
  }  
}
