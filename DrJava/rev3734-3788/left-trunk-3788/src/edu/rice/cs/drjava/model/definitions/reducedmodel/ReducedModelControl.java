

package edu.rice.cs.drjava.model.definitions.reducedmodel;

import java.util.Vector;


public class ReducedModelControl implements BraceReduction {
  ReducedModelBrace rmb;
  ReducedModelComment rmc;
  int _offset;

  public ReducedModelControl() {
    rmb = new ReducedModelBrace(this);
    rmc = new ReducedModelComment();
  }

  public void insertChar(char ch) {
    rmb.insertChar(ch);
    rmc.insertChar(ch);
  }

  
  public void move(int count) {
    rmb.move(count);
    rmc.move(count);
  }

  
  public void delete(int count) {
    rmb.delete(count);
    rmc.delete(count);
  }


  
  public int balanceForward() {
    return rmb.balanceForward();
  }
  
  public int balanceBackward() {
    return rmb.balanceBackward();
  }

  
  public ReducedModelState moveWalkerGetState(int relDistance) {
    return rmc.moveWalkerGetState(relDistance);
  }

  
  public void resetLocation() {
    rmc.resetWalkerLocationToCursor();
  }

  
  public ReducedToken currentToken() {
    
    ReducedToken rmcToken = rmc.current();
    if (!rmcToken.isGap()) {
        return rmcToken;
    }
    
    ReducedToken rmbToken = rmb.current();
    if (!rmbToken.isGap()) {
      rmbToken.setState(rmc.getStateAtCurrent());
      return rmbToken;
    }
    
    int size = getSize(rmbToken,rmcToken);
    return new Gap(size, rmc.getStateAtCurrent());
  }

  
  public ReducedModelState getStateAtCurrent() {
      return rmc.getStateAtCurrent();
  }

  
  String getType() {
    ReducedToken rmcToken = rmc.current();
    if (!rmcToken.isGap())
      return rmcToken.getType();

    ReducedToken rmbToken = rmb.current();
    if (!rmbToken.isGap()) {
      return rmbToken.getType();
    }
    return ""; 
  }

  
  int getSize() {
    return getSize(rmb.current(),rmc.current());
  }

  int getSize(ReducedToken rmbToken, ReducedToken rmcToken) {
    int rmb_offset = rmb.getBlockOffset();
    int rmc_offset = rmc.getBlockOffset();
    int rmb_size = rmbToken.getSize();
    int rmc_size = rmcToken.getSize();
    int size;
    if (rmb_offset < rmc_offset) {
      size = rmb_offset;
      _offset = size;
    }
    else {
      size = rmc_offset;
      _offset = size;
    }

    if (rmb_size - rmb_offset < rmc_size - rmc_offset) {
      size += (rmb_size - rmb_offset);
    }
    else {
      size += (rmc_size - rmc_offset);
    }
    return size;
  }

  
  void next() {
    if (rmc._cursor.atStart()) {
      rmc.next();
      rmb.next();
      return;
    }
    int size = getSize(rmb.current(),rmc.current());
    rmc.move(size - _offset);
    rmb.move(size - _offset);
  }

  
  void prev() {
    int size;
    if (rmc._cursor.atEnd()) {
      rmc.prev();
      rmb.prev();
      if (rmc._cursor.atStart()) {
        return; 
      }

      if (rmc.current().getSize() < rmb.current().getSize()) {
        size = -rmc.current().getSize();
      }
      else {
        size = -rmb.current().getSize();
      }
      rmc.next();
      rmb.next();
      move(size);
    }
    else if (rmb.getBlockOffset() < rmc.getBlockOffset()) {
      rmb.prev();
      size = rmb.current().getSize() + rmb.getBlockOffset();
      rmb.next();
      if (size < rmc.getBlockOffset()) {
        move(-size);
      }
      else {
        move(-rmc.getBlockOffset());
      }
    }
    else if (rmb.getBlockOffset() == rmc.getBlockOffset()) {
      rmb.prev();
      rmc.prev();
      rmb.setBlockOffset(0);
      rmc.setBlockOffset(0);
    }
    else {
      rmc.prev();
      size = rmc.current().getSize() + rmc.getBlockOffset();
      rmc.next();
      if (size < rmb.getBlockOffset()) {
        move(-size);
      }
      else {
        move(-rmb.getBlockOffset());
      }
    }
  }

  
  public ReducedToken prevItem() {
    int rmbOffset = rmb.getBlockOffset();
    int rmcOffset = rmc.getBlockOffset();

    prev();
    ReducedToken temp = currentToken();
    next();

    rmb.setBlockOffset(rmbOffset);
    rmc.setBlockOffset(rmcOffset);
    return temp;
  }

  
  public ReducedToken nextItem() {
    int rmbOffset = rmb.getBlockOffset();
    int rmcOffset = rmc.getBlockOffset();
    next();
    ReducedToken temp = currentToken();
    prev();
    rmb.setBlockOffset(rmbOffset);
    rmc.setBlockOffset(rmcOffset);
    return temp;
  }

  
  boolean atEnd() {
    return (rmb._cursor.atEnd() || rmc._cursor.atEnd());
  }

  
  boolean atStart() {
    return (rmb._cursor.atStart() || rmc._cursor.atStart());
  }

  
  int getBlockOffset() {
    if (rmb.getBlockOffset() < rmc.getBlockOffset())
      return rmb.getBlockOffset();
    return rmc.getBlockOffset();
  }

  
  public int absOffset() {
    return rmc.absOffset();
  }


  
  public String simpleString() {
    return "\n********\n" + rmb.simpleString() + "\n________\n" +
      rmc.simpleString();
  }

  
  public IndentInfo getIndentInformation() {
    IndentInfo braceInfo = new IndentInfo();
    
    rmc.getDistToPreviousNewline(braceInfo);
    
    rmb.getDistToEnclosingBrace(braceInfo);
    
    rmc.getDistToIndentNewline(braceInfo);
    
    rmb.getDistToEnclosingBraceCurrent(braceInfo);
    
    rmc.getDistToCurrentBraceNewline(braceInfo);
    return braceInfo;
  }

  
  public int getDistToPreviousNewline(int relLoc) {
    return rmc.getDistToPreviousNewline(relLoc);
  }

  public int getDistToNextNewline() {
    return rmc.getDistToNextNewline();
  }

  
  public Vector<HighlightStatus> getHighlightStatus(final int start, final int length) {
    Vector<HighlightStatus> vec = new Vector<HighlightStatus>();

    int curState;
    int curLocation;
    int curLength;

    TokenList.Iterator cursor = rmc._cursor._copy();

    curLocation = start;
    curLength = cursor.current().getSize() - rmc.getBlockOffset();
    curState = cursor.current().getHighlightState();

    while ((curLocation + curLength) < (start + length)) {
      cursor.next();
      
      
      if(cursor.atEnd())
        break;
      int nextState = cursor.current().getHighlightState();

      if (nextState == curState) {
        
        curLength += cursor.current().getSize();
      }
      else {
        
        vec.add(new HighlightStatus(curLocation, curLength, curState));
        curLocation += curLength; 
        curLength = cursor.current().getSize();
        curState = nextState;
      }
    }

    
    
    
    int requestEnd = start + length;
    if ((curLocation + curLength) > requestEnd) {
      curLength = requestEnd - curLocation;
    }

    
    vec.add(new HighlightStatus(curLocation, curLength, curState));

    cursor.dispose();

    return vec;
  }
}
