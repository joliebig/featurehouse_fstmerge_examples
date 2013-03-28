

package edu.rice.cs.drjava.model.definitions.reducedmodel;

import java.util.ArrayList;

import edu.rice.cs.util.UnexpectedException;


public class ReducedModelControl implements BraceReduction {
  
  final ReducedModelBrace _rmb;   
  final ReducedModelComment _rmc; 
  volatile int _offset;
  
  
  public ReducedModelControl() {
    _rmb = new ReducedModelBrace(this);
    _rmc = new ReducedModelComment();
  }
  
  
  public int braceCursorOffset() { return _rmb.absOffset(); }  
  
  public int commentCursorOffset() { return _rmc.absOffset(); } 
  
  
  public int walkerOffset() { return _rmc.walkerOffset(); }
  

  

  
  public void insertChar(char ch) {
    _rmb.insertChar(ch);
    _rmc.insertChar(ch);
  }
  
  
  public void move(int count) {
    try {
      _rmb.move(count);
      _rmc.move(count);
    }
    catch(IllegalArgumentException e) { 
      resetLocation();  
      throw new UnexpectedException(e);
    }
  }
  
  
  public void delete(int count) {
    _rmb.delete(count);
    _rmc.delete(count);
  }
  
  
  public boolean isShadowed() { return _rmc.isShadowed(); }
  
  
  public boolean isWeaklyShadowed() { return _rmc.isWeaklyShadowed(); }
  
  
  public int balanceForward() { return _rmb.balanceForward(); }
  
  
  public int balanceBackward() { return _rmb.balanceBackward(); }
  
  
  public ReducedModelState moveWalkerGetState(int relDistance) { return _rmc.moveWalkerGetState(relDistance); }
  
  
  public void resetLocation() { _rmc.resetWalkerLocationToCursor(); }
  
  
  public ReducedToken currentToken() {
    
    ReducedToken rmcToken = _rmc.current();
    if (! rmcToken.isGap()) return rmcToken;
    
    ReducedToken rmbToken = _rmb.current();
    if (! rmbToken.isGap()) {
      rmbToken.setState(_rmc.getStateAtCurrent());
      return rmbToken;
    }
    
    int size = getSize(rmbToken,rmcToken);
    return new Gap(size, _rmc.getStateAtCurrent());
  }
  
  
  public ReducedModelState getStateAtCurrent() { return _rmc.getStateAtCurrent(); }
  
  
  String getType() {
    ReducedToken rmcToken = _rmc.current();
    if (! rmcToken.isGap())
      return rmcToken.getType();
    
    ReducedToken rmbToken = _rmb.current();
    if (! rmbToken.isGap()) {
      return rmbToken.getType();
    }
    return ""; 
  }
  
  
  int getSize() {
    return getSize(_rmb.current(),_rmc.current());
  }
  
  int getSize(ReducedToken rmbToken, ReducedToken rmcToken) {
    int rmb_offset = _rmb.getBlockOffset();
    int rmc_offset = _rmc.getBlockOffset();
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
    if (_rmc._cursor.atStart()) {
      _rmc.next();
      _rmb.next();
      return;
    }
    int size = getSize(_rmb.current(), _rmc.current());
    _rmc.move(size - _offset);
    _rmb.move(size - _offset);
  }
  
  
  void prev() {
    int size;
    if (_rmc._cursor.atEnd()) {
      _rmc.prev();
      _rmb.prev();
      if (_rmc._cursor.atStart()) {
        return; 
      }
      
      if (_rmc.current().getSize() < _rmb.current().getSize()) {
        size = -_rmc.current().getSize();
      }
      else {
        size = -_rmb.current().getSize();
      }
      _rmc.next();
      _rmb.next();
      move(size);
    }
    else if (_rmb.getBlockOffset() < _rmc.getBlockOffset()) {
      _rmb.prev();
      size = _rmb.current().getSize() + _rmb.getBlockOffset();
      _rmb.next();
      if (size < _rmc.getBlockOffset()) {
        move(-size);
      }
      else {
        move(-_rmc.getBlockOffset());
      }
    }
    else if (_rmb.getBlockOffset() == _rmc.getBlockOffset()) {
      _rmb.prev();
      _rmc.prev();
      _rmb.setBlockOffset(0);
      _rmc.setBlockOffset(0);
    }
    else {
      _rmc.prev();
      size = _rmc.current().getSize() + _rmc.getBlockOffset();
      _rmc.next();
      if (size < _rmb.getBlockOffset()) {
        move(-size);
      }
      else {
        move(-_rmb.getBlockOffset());
      }
    }
  }
  
  
  public ReducedToken prevItem() {
    int rmbOffset = _rmb.getBlockOffset();
    int rmcOffset = _rmc.getBlockOffset();
    
    prev();
    ReducedToken temp = currentToken();
    next();
    
    _rmb.setBlockOffset(rmbOffset);
    _rmc.setBlockOffset(rmcOffset);
    return temp;
  }
  
  
  public ReducedToken nextItem() {
    int rmbOffset = _rmb.getBlockOffset();
    int rmcOffset = _rmc.getBlockOffset();
    next();
    ReducedToken temp = currentToken();
    prev();
    _rmb.setBlockOffset(rmbOffset);
    _rmc.setBlockOffset(rmcOffset);
    return temp;
  }
  
  
  boolean atEnd() { return (_rmb._cursor.atEnd() || _rmc._cursor.atEnd()); }
  
  
  boolean atStart() { return (_rmb._cursor.atStart() || _rmc._cursor.atStart()); }
  
  
  int getBlockOffset() {
    if (_rmb.getBlockOffset() < _rmc.getBlockOffset()) return _rmb.getBlockOffset();
    return _rmc.getBlockOffset();
  }
  
  
  public int absOffset() {
    int offset = _rmc.absOffset();
    assert offset == _rmb.absOffset();
    return offset; 
  }
  
  
  
  public String simpleString() {
    return "\n********\n" + _rmb.simpleString() + "\n________\n" + _rmc.simpleString();
  }
  

























  
  public int getDistToIdentNewline() { return -1; }
  public int getDistToEnclosingBraceStart() { return -1; }
  
  
  public BraceInfo _getLineEnclosingBrace() { return _rmb._getLineEnclosingBrace(); }
  
  
  public BraceInfo _getEnclosingBrace() { return _rmb._getEnclosingBrace(); }
  
  public int getDistToStart() { return _rmc.getDistToStart(); }
  
  public int getDistToStart(int relLoc) { return _rmc.getDistToStart(relLoc); }
  
  public int getDistToNextNewline() { return _rmc.getDistToNextNewline(); }
  
  
  public ArrayList<HighlightStatus> getHighlightStatus(final int start, final int length) {
    ArrayList<HighlightStatus> vec = new ArrayList<HighlightStatus>();
    
    int curState;
    int curLocation;
    int curLength;
    
    TokenList.Iterator cursor = _rmc._cursor.copy();

    curLocation = start;
    


    if (cursor.atEnd() || cursor.atStart()) { 
      curLength = length;
      curState = 0;
    }
    else {
      curLength = cursor.current().getSize() - _rmc.getBlockOffset();
      curState = cursor.current().getHighlightState();
    }
    
    while (curLocation + curLength < start + length) {
      cursor.next();
      
      
      if (cursor.atEnd()) break;
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
    if (curLocation + curLength > requestEnd)  curLength = requestEnd - curLocation;
    
    
    vec.add(new HighlightStatus(curLocation, curLength, curState));
    
    cursor.dispose();
    
    return vec;
  }
}
