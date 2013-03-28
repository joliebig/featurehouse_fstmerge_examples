

package edu.rice.cs.drjava.model.definitions.reducedmodel;


public abstract class ReducedToken implements ReducedModelStates {
  private ReducedModelState _state;

  public ReducedToken(ReducedModelState state) {
    _state = state;
  }

  
  public abstract int getSize();

  
  public abstract String getType();

  
  public abstract void setType(String type);

  
  public abstract void flip();

  
  public abstract boolean isMatch(ReducedToken other);

  
  public ReducedModelState getState() { return  _state; }

  
  public int getHighlightState() {
    String type = getType();
    if (type.equals("//") || (_state == INSIDE_LINE_COMMENT) || type.equals("/*")
        || type.equals("*/") || (_state == INSIDE_BLOCK_COMMENT)) {
      return  HighlightStatus.COMMENTED;
    }
    if ((type.equals("'") && (_state == FREE)) || (_state == INSIDE_SINGLE_QUOTE)) {
      return  HighlightStatus.SINGLE_QUOTED;
    }
    if ((type.equals("\"") && (_state == FREE)) || (_state == INSIDE_DOUBLE_QUOTE)) {
      return  HighlightStatus.DOUBLE_QUOTED;
    }
    return  HighlightStatus.NORMAL;
  }

  
  public void setState(ReducedModelState state) {
    _state = state;
  }

  
  public boolean isShadowed() { return  _state != FREE; }

  
  public boolean isQuoted() {
    return  _state == INSIDE_DOUBLE_QUOTE;
  }

  
  public boolean isCommented() { return  isInBlockComment() || isInLineComment(); }

  
  public boolean isInBlockComment() { return  _state == INSIDE_BLOCK_COMMENT; }

  
  public boolean isInLineComment() { return  _state == INSIDE_LINE_COMMENT; }

  
  public abstract boolean isMultipleCharBrace();

  
  public abstract boolean isGap();

  
  public abstract boolean isLineComment();

  
  public abstract boolean isBlockCommentStart();

  
  public abstract boolean isBlockCommentEnd();

  
  public abstract boolean isNewline();

  
  public abstract boolean isSlash();

  
  public abstract boolean isStar();

  
  public abstract boolean isDoubleQuote();

  
  public abstract boolean isSingleQuote();

  
  public abstract boolean isDoubleEscapeSequence();

  
  public abstract boolean isDoubleEscape();

  
  public abstract boolean isEscapedSingleQuote();

  
  public abstract boolean isEscapedDoubleQuote();

  
  public abstract void grow(int delta);

  
  public abstract void shrink(int delta);

  
  public abstract boolean isOpen();

  
  public abstract boolean isClosed();

  
  public abstract boolean isOpenBrace();

  
  public abstract boolean isClosedBrace();
}



