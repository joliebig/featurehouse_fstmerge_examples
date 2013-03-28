 

package edu.rice.cs.drjava.model.definitions.reducedmodel;


public abstract class AbstractReducedModel implements ReducedModelStates {
    
  
  public static final char PTR_CHAR = '#';
  
  
  TokenList _tokens;
  
  
  TokenList.Iterator _cursor;
  
  
  public AbstractReducedModel() {
    _tokens = new TokenList();
    _cursor = _tokens._getIterator();
    
    _cursor.setBlockOffset(0);
  }
 
  
  int getBlockOffset() { return _cursor.getBlockOffset(); }
  
  
  void setBlockOffset(int offset) { _cursor.setBlockOffset(offset); }
  
  
  int absOffset() {
    int off = _cursor.getBlockOffset();
    TokenList.Iterator it = _cursor._copy();
    if (! it.atStart()) it.prev();
    
    while (! it.atStart()) {
      off += it.current().getSize();
      it.prev();
    }
    it.dispose();
    return off;
  }
  
  
  public String simpleString() {
    StringBuffer val = new StringBuffer();
    ReducedToken tmp;
    
    TokenList.Iterator it = _tokens._getIterator();
    it.next(); 
    
    if (_cursor.atStart())  val.append(PTR_CHAR).append(_cursor.getBlockOffset());
    
    while (!it.atEnd()) {
      tmp = it.current();
      
      if (!_cursor.atStart() && !_cursor.atEnd() && (tmp == _cursor.current())) {
        val.append(PTR_CHAR).append(_cursor.getBlockOffset());
      }
      
      val.append('|').append(tmp).append('|').append('\t');
      it.next();
    }
    
    if (_cursor.atEnd()) val.append(PTR_CHAR).append(_cursor.getBlockOffset());
    
    val.append("|end|");
    it.dispose();
    return val.toString();
  }
  
  
  public abstract void insertChar(char ch);
  
  
  public void _insertGap( int length ) {
    if (_cursor.atStart()) {
      if (_gapToRight()) {
        _cursor.next();
        _augmentCurrentGap(length); 
      }
      else _insertNewGap(length);
    }
    else if (_cursor.atEnd()) {
      if (_gapToLeft()) {
        _augmentGapToLeft(length);
        
        
      }
      else _insertNewGap(length); 
    }
    
    else if ((_cursor.getBlockOffset() > 0) && _cursor.current().isMultipleCharBrace())
      insertGapBetweenMultiCharBrace(length);
    
    else if (_cursor.current().isGap()) {
      _cursor.current().grow(length);
      _cursor.setBlockOffset(_cursor.getBlockOffset() + length);
    }
    else if (!_cursor.atFirstItem() && _cursor.prevItem().isGap())
      
      _cursor.prevItem().grow(length);
    else  
      _insertNewGap(length); 
    return;
  }

  
  protected abstract void insertGapBetweenMultiCharBrace(int length);
  
  
  public TokenList.Iterator makeCopyCursor() {
    return _cursor._copy();
  }
  
  
  protected ReducedModelState getStateAtCurrent() {
    return _cursor.getStateAtCurrent();
  }

  
  protected boolean _gapToRight() {
    
    return (!_tokens.isEmpty() && !_cursor.atEnd() &&
            !_cursor.atLastItem() && _cursor.nextItem().isGap());
  }
  
  
  protected boolean _gapToLeft() {
    
    return (!_tokens.isEmpty() && !_cursor.atStart() && !_cursor.atFirstItem() &&_cursor.prevItem().isGap());
  }
  
  
  protected void _augmentGapToLeft(int length) { _cursor.prevItem().grow(length); }
  
  
  protected void _augmentCurrentGap(int length) {
    _cursor.current().grow(length);
    _cursor.setBlockOffset(length);
  }
  
  
  protected void _insertNewGap(int length) {
    _cursor.insert(new Gap(length, getStateAtCurrent()));
    _cursor.next();
    _cursor.setBlockOffset(0);
  }
  
  
  protected abstract ReducedModelState moveWalkerGetState(int relLocation);
  
  
  protected abstract void resetWalkerLocationToCursor();

  
  protected ReducedToken current() { return _cursor.current(); }
  
  
  protected void  next() { _cursor.next(); }

  
  protected void prev() {
    _cursor.prev();
  }

}
