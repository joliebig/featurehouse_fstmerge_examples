

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
    if (_cursor.atStart() || _cursor.atEnd()) {
      _cursor.insertNewBrace(text); 
    }
    else if (_cursor.current().isGap()) {
      _cursor.insertBraceToGap(text);
    }

    else {
      _cursor.insertNewBrace(text);
    }
  }

  
  protected void insertGapBetweenMultiCharBrace(int length) {
    throw new RuntimeException("ReducedModelBrace does not keep " +
                               "track of multi-character braces.");
  }


  
  public void move(int count) {
    _cursor.move(count);
  }

  
  public void delete( int count ) {
    if (count == 0)  return;
    _cursor.delete(count);
    return;
  }


  
  private boolean _isCurrentBraceMatchable() {
    String type = _cursor.current().getType();
    return (((type.equals("{")) ||
             (type.equals("}")) ||
             (type.equals("(")) ||
             (type.equals(")")) ||
             (type.equals("[")) ||
             (type.equals("]"))) &&
            (_parent.getStateAtCurrent() == FREE));
  }

  
  public int previousBrace() {
    int relDistance;
    int dist = 0;
    resetWalkerLocationToCursor();

    TokenList.Iterator copyCursor = _cursor._copy();
    if (!copyCursor.atStart()) {
      copyCursor.prev();
    }
    if (copyCursor.atStart()) {
      copyCursor.dispose();
      return -1;
    }
    
    dist += _cursor.getBlockOffset();
    relDistance = dist;

    
    

    while (!copyCursor.atStart()) {
      if (!copyCursor.current().isGap()) {
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
    TokenList.Iterator copyCursor = _cursor._copy();

    resetWalkerLocationToCursor();

    if ( copyCursor.atStart())
      copyCursor.next();
    if (_cursor.getBlockOffset() > 0) {
      dist = copyCursor.current().getSize() - _cursor.getBlockOffset();
      relDistance = dist;
      copyCursor.next();
    }
    
    while (!copyCursor.atEnd() ) {
      if (!copyCursor.current().isGap()) {
        if (moveWalkerGetState(relDistance) ==
            FREE) {
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
    
    Stack<ReducedToken> braceStack = new Stack<ReducedToken>();
    TokenList.Iterator iter = _cursor._copy();
    resetWalkerLocationToCursor();
    int relDistance;
    int distance = 0;
    if (iter.atStart() || iter.atFirstItem() || !openBraceImmediatelyLeft()) {

      iter.dispose();

      return -1;
    }

    iter.prev();
    relDistance = -iter.current().getSize();
    
    
    if (iter.current().isOpenBrace()) {
      if (moveWalkerGetState(relDistance) == FREE) {
        
        braceStack.push(iter.current());

        
        iter.next();
        moveWalkerGetState(-relDistance);
        relDistance = 0;
      }
      else {
        
        iter.dispose();

        return -1;
      }
    }
    else {
      
      iter.dispose();

      return -1;
    }
    
    
    
    
    while (!iter.atEnd() && !braceStack.isEmpty()) {
      if (!iter.current().isGap()) {
        if (moveWalkerGetState(relDistance) == FREE) {
              
              if (iter.current().isClosedBrace()) {
                ReducedToken popped = braceStack.pop();
                if (!iter.current().isMatch(popped)) {
                  iter.dispose();

                  return -1;
                }
              }
              
              else {
                braceStack.push(iter.current());
              }
            }
        relDistance = 0;
      }
      
      
        distance += iter.current().getSize();
        relDistance += iter.current().getSize();
        iter.next();
    }

    
    if (!braceStack.isEmpty()) {
      iter.dispose();

      return -1;
    }
    
    else {
      iter.dispose();
      return distance;
    }
  }














  public boolean openBraceImmediatelyLeft() {
    if (_cursor.atStart() || _cursor.atFirstItem()) {
      return false;
    }
    else {
      _cursor.prev();
      
      boolean isLeft = ((_cursor.getBlockOffset() == 0) && _cursor.current().isOpen() &&
                        _isCurrentBraceMatchable());
      
      _cursor.next();
      
      
      return isLeft;
    }
  }

  public boolean closedBraceImmediatelyLeft() {
    if (_cursor.atStart() || _cursor.atFirstItem()) {
      return false;
    }
    else {
      _cursor.prev();
      
      boolean isLeft = ((_cursor.getBlockOffset() == 0) && _cursor.current().isClosed() &&
                        _isCurrentBraceMatchable());
      
      _cursor.next();
      
      
      return isLeft;
    }
  }

  
  public int balanceBackward() {
    
    Stack<ReducedToken> braceStack = new Stack<ReducedToken>();
    TokenList.Iterator iter = _cursor._copy();
    resetWalkerLocationToCursor();
    int relDistance;
    int distance = 0;
    if (iter.atStart() || iter.atFirstItem() || !closedBraceImmediatelyLeft()) {
      
      iter.dispose();
      
      return -1;
    }

    iter.prev();
    relDistance = iter.current().getSize();
    
    
    if (iter.current().isClosedBrace()) {
      if (moveWalkerGetState(-relDistance) == FREE) {
        
        

        braceStack.push(iter.current());
        distance += iter.current().getSize();
        iter.prev();
        if (!iter.atStart()) {
          distance += iter.current().getSize();
          relDistance = iter.current().getSize();
        }
      }
      else {
        iter.dispose();
        
        return -1;
      }
    }
    else {
      iter.dispose();
      
      return -1;
    }
    
    
    
    
    while (!iter.atStart() && !braceStack.isEmpty()) {
      if (!iter.current().isGap()) {
        if (moveWalkerGetState(-relDistance) ==
            FREE) {
              
              if (iter.current().isOpenBrace()) {
                ReducedToken popped = braceStack.pop();
                if (!iter.current().isMatch(popped)) {
                  iter.dispose();
                  
                  return -1;
                }
              }
              
              else {
                braceStack.push(iter.current());
              }
            }
        relDistance = 0;
      }
      
      
      iter.prev();
      if (!iter.atStart() && !braceStack.isEmpty()) {
        distance += iter.current().getSize();
        relDistance += iter.current().getSize();
      }
    }

    
    if (!braceStack.isEmpty()) {
      iter.dispose();
      
      return -1;
    }
    
    else {
      iter.dispose();
      return distance;
    }
  }

  protected ReducedModelState moveWalkerGetState(int relDistance) {
    return _parent.moveWalkerGetState(relDistance);
  }

  protected void resetWalkerLocationToCursor() {
    _parent.resetLocation();
  }

  
  protected void getDistToEnclosingBrace(IndentInfo braceInfo) {
    Stack<ReducedToken> braceStack = new Stack<ReducedToken>();
    TokenList.Iterator iter = _cursor._copy();
    resetWalkerLocationToCursor();
    
    int relDistance = braceInfo.distToNewline + 1;
    int distance = relDistance;

    if (braceInfo.distToNewline == -1) {
      iter.dispose();
      return;
    }
    
    
    iter.move(-braceInfo.distToNewline - 1);
    relDistance += iter.getBlockOffset();
    distance += iter.getBlockOffset();

    
    
    braceInfo.distToNewline = -1;

    if (iter.atStart() || iter.atFirstItem()) {
      iter.dispose();
      return;
    }

    iter.prev();

    
    
    
    
    while (!iter.atStart()) {

      distance += iter.current().getSize();
      relDistance += iter.current().getSize();

      if (!iter.current().isGap()) {

        if (moveWalkerGetState(-relDistance) == FREE) {
              
              if (iter.current().isOpenBrace()) {
                if (braceStack.isEmpty()) {
                  braceInfo.braceType = iter.current().getType();
                  braceInfo.distToBrace = distance;
                  iter.dispose();
                  return;
                }
                ReducedToken popped = braceStack.pop();
                if (!iter.current().isMatch(popped)) {
                  iter.dispose();
                  return;
                }
              }
              
              else {
                braceStack.push(iter.current());
              }
            }
        relDistance = 0;
      }
      
      
      iter.prev();
    }

    iter.dispose();
    return;
  }


  
  protected void getDistToEnclosingBraceCurrent(IndentInfo braceInfo) {
    Stack<ReducedToken> braceStack = new Stack<ReducedToken>();
    TokenList.Iterator iter = _cursor._copy();
    resetWalkerLocationToCursor();
    int relDistance = 0;
    int distance = relDistance;


    
    

    relDistance += iter.getBlockOffset();
    distance += iter.getBlockOffset();

    
    
    braceInfo.distToNewlineCurrent = -1;

    if (iter.atStart() || iter.atFirstItem()) {
      iter.dispose();
      return;
    }

    iter.prev();

    
    
    
    
    while (!iter.atStart()) {

      distance += iter.current().getSize();
      relDistance += iter.current().getSize();

      if (!iter.current().isGap()) {

        if (moveWalkerGetState(-relDistance) == FREE) {
              
              if (iter.current().isOpenBrace()) {
                if (braceStack.isEmpty()) {
    braceInfo.braceTypeCurrent = iter.current().getType();
                  braceInfo.distToBraceCurrent = distance;
                  iter.dispose();
                  return;
                }
                ReducedToken popped = braceStack.pop();
                if (!iter.current().isMatch(popped)) {
                  iter.dispose();
                  return;
                }
              }
              
              else {
                braceStack.push(iter.current());
              }
            }
        relDistance = 0;
      }
      
      
      iter.prev();
    }

    iter.dispose();
    return;
  }
}
