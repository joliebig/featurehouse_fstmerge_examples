

package edu.rice.cs.drjava.model.definitions.reducedmodel;


class Gap extends ReducedToken {
  private volatile int _size;
  
  
  Gap(int size, ReducedModelState state) {
    super(state);
    _size = size;
  }
  
  
  public int getSize() { return _size; }
  
  
  public String getType() { return ""; }
  
  
  public void setType(String type) { throw new RuntimeException("Can't set type on Gap!"); }
  
  
  public void flip() { throw  new RuntimeException("Can't flip a Gap!"); }
  
  
  public void grow(int delta) { if (delta >= 0) _size += delta; }
  
  
  public void shrink(int delta) { if (delta <= _size && delta >= 0) _size -= delta; }
  
  
  public String toString() {




    return "Gap<" + _size + ">";
  }
  
  
  public boolean isMultipleCharBrace() { return false; }
  
  
  public boolean isGap() { return true; }
  
  
  public boolean isLineComment() { return false; }
  
  
  public boolean isBlockCommentStart() { return false; }
  
  
  public boolean isBlockCommentEnd() { return false; }
  
  
  public boolean isNewline() { return false; }
  
  
  public boolean isSlash() { return false; }
  
  
  public boolean isStar() { return false; }
  
  
  public boolean isDoubleQuote() { return false; }
  
  
  public boolean isSingleQuote() { return false; }
  
  
  public boolean isDoubleEscapeSequence() { return false; }
  
  
  public boolean isDoubleEscape() { return false; }
  
  
  public boolean isEscapedSingleQuote() { return false; }
  
  
  public boolean isEscapedDoubleQuote() { return false; }
  
  
  public boolean isOpen() { return false; }
  
  
  public boolean isClosed() { return false; }
  
  
  public boolean isMatch(Brace other) { return false; }
  
  
  public boolean isMatchable() { return false; }
  
  
  public boolean isOpenBrace() { return false; }
  
  
  public boolean isClosedBrace() { return false; }
}
