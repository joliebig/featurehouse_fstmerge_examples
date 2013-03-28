

package edu.rice.cs.drjava.model.definitions.reducedmodel;


class Brace extends ReducedToken implements ReducedModelStates {
  
  
  public static final String[] braces =  {
    "{", "}", "(", ")", "[", "]", "/*", "*/", "//", "\n", "/", "*", "\"", "\"", "'", "'", "\\\\", "\\", "\\'", "\\\"", ""
  };
  
  public static final int BRACES_LENGTH = braces.length;
  public static final int LAST_BRACE_INDEX = braces.length - 1;
  
  public static final int BLK_CMT_BEG_TYPE = findBrace("/*");
  public static final int BLK_CMT_END_TYPE = findBrace("*/");
  public static final int EOLN_TYPE = findBrace("\n");
  public static final int LINE_CMT_TYPE = findBrace("//");
  public static final int SINGLE_QUOTE_TYPE = findBrace("'");
  public static final int DOUBLE_QUOTE_TYPE = findBrace("\"");
  public static final int STAR_TYPE = findBrace("*");
  public static final int SLASH_TYPE = findBrace("/");
  public static final int DOUBLE_ESCAPE_TYPE = findBrace("\\\\");
  public static final int ESCAPED_SINGLE_QUOTE_TYPE = findBrace("\\'");
  public static final int ESCAPED_DOUBLE_QUOTE_TYPE = findBrace("\\\"");

  
  private volatile int _type;  
  private volatile int _size;  

  
  public static Brace MakeBrace(String type, ReducedModelState state) {
    int index = findBrace(type.intern());
    if (index == BRACES_LENGTH) throw new BraceException("Invalid brace type \"" + type + "\"");
    else return new Brace(index, state);
  }

  
  private Brace(int type, ReducedModelState state) {
    super(state);
    _type = type;
    _size = getType().length();
  }

  
  public String getType() { return (_type == BRACES_LENGTH) ? "!" : braces[_type]; }

  
  public int getSize() { return _size; }

  
  public String toString() {
    
    final StringBuilder val = new StringBuilder("Brace<");





    val.append(getType());
    return val.append('>').toString();
  }

  
  public void flip() {
    if (isOpen()) _type += 1;
    else if (_type < braces.length - 1) _type -= 1;
  }

  
  public boolean isOpen() { return (((_type % 2) == 0) && (_type < braces.length - 1)); }

  
  public boolean isOpenBrace() { return ((_type == 0) || (_type == 2) || (_type == 4)); }

  
  public boolean isClosedBrace() { return ((_type == 1) || (_type == 3) || (_type == 5)); }

  
  public boolean isClosed() { return ! isOpen(); }

  
  public void setType(String type) {
    type = type.intern();
    int index = findBrace(type);
    if (index == braces.length) throw new BraceException("Invalid brace type \"" + type + "\"");
    _type = index;
    _size = getType().length();
  }

  
  private static int findBrace(String type) {
    assert type == type.intern();
    int i;
    for (i = 0; i < braces.length; i++) {
      if (type == braces[i]) break;
    }
    return  i;
  }

  
  public boolean isMatch(Brace other) {
    int off = isOpen() ? 1 : -1;
    return _type + off == other._type;
  }
  
  
  public boolean isMatchable() { return _type < BLK_CMT_BEG_TYPE; }

  
  public boolean isDoubleQuote() { return _type == DOUBLE_QUOTE_TYPE; }

  
  public boolean isSingleQuote() { return _type == SINGLE_QUOTE_TYPE; }

  
  public boolean isLineComment() { return _type == LINE_CMT_TYPE; }

  
  public boolean isBlockCommentStart() { return _type == BLK_CMT_BEG_TYPE; }

  
  public boolean isBlockCommentEnd() { return _type == BLK_CMT_END_TYPE; }

  
  public boolean isNewline() { return _type == EOLN_TYPE; }

  
  public boolean isMultipleCharBrace() {
    return isLineComment() || isBlockCommentStart() || isBlockCommentEnd() || isDoubleEscapeSequence();
  }

  
  public boolean isDoubleEscapeSequence() { 
    return isDoubleEscape() || isEscapedDoubleQuote() || isEscapedSingleQuote(); 
  }

  
  public boolean isDoubleEscape() { return _type == DOUBLE_ESCAPE_TYPE; }

  
  public boolean isEscapedDoubleQuote() {return _type == ESCAPED_DOUBLE_QUOTE_TYPE; }

  
  public boolean isEscapedSingleQuote() { return _type == ESCAPED_SINGLE_QUOTE_TYPE; }

  
  public boolean isGap() { return false; }

  
  public boolean isSlash() { return _type == SLASH_TYPE; }

  
  public boolean isStar() { return _type == STAR_TYPE; }

  
  public void grow(int delta) { throw new BraceException("Braces can't grow."); }

  
  public void shrink(int delta) { throw new BraceException("Braces can't shrink."); }
}





