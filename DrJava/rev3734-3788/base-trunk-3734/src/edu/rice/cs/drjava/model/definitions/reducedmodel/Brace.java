

package edu.rice.cs.drjava.model.definitions.reducedmodel;


class Brace extends ReducedToken implements ReducedModelStates {
  
  public static final String[] braces =  {
    "{", "}", "(", ")", "[", "]", "/*", "*/", "//", "\n", "/", "*", "\"", "\"",
    "'", "'", "\\\\", "\\", "\\'", "\\\"", ""
  };
  public static final String BLK_CMT_BEG = "/*";
  public static final String BLK_CMT_END = "*/";
  public static final String EOLN = "\n";
  public static final String LINE_CMT = "//";
  public static final String SINGLE_QUOTE = "'";
  public static final String DOUBLE_QUOTE = "\"";
  public static final String STAR = "*";
  public static final String SLASH = "/";

  
  protected int _type;

  
  public static Brace MakeBrace(String type, ReducedModelState state) {
    int index = findBrace(type);
    if (index == braces.length) {
      throw new BraceException("Invalid brace type \"" + type + "\"");
    }
    else {
      return new Brace(index, state);
    }
  }

  
  private Brace(int type, ReducedModelState state) {
    super(state);
    _type = type;
  }

  
  public String getType() {
    return (_type == braces.length) ? "!" : braces[_type];
  }

  
  public int getSize() {
    return getType().length();
  }

  
  public String toString() {
    
    StringBuffer val = new StringBuffer();
    int i;
    for (i = 0; i < getSize(); i++) {
      val.append(' ');
      val.append(getType().charAt(i));
    }
    return val.toString();
  }

  
  public void flip() {
    if (isOpen()) _type += 1;
    else if (_type < braces.length - 1) _type -= 1;
  }

  
  public boolean isOpen() {
    return (((_type%2) == 0) && (_type < braces.length - 1));
  }

  
  public boolean isOpenBrace() {
    return ((_type == 0) || (_type == 2) || (_type == 4));
  }

  
  public boolean isClosedBrace() {
    return ((_type == 1) || (_type == 3) || (_type == 5));
  }

  
  public boolean isClosed() {
    return !isOpen();
  }

  
  public void setType(String type) {
    int index = findBrace(type);
    if (index == braces.length) {
      throw new BraceException("Invalid brace type \"" + type + "\"");
    }
    else {
      _type = index;
    }
  }

  
  protected static int findBrace(String type) {
    int i;
    for (i = 0; i < braces.length; i++) {
      if (type.equals(braces[i]))
        break;
    }
    return  i;
  }

  
  public boolean isMatch(ReducedToken other) {
    if (this.getType().equals("")) {
      return false;
    }
    int off = (this.isOpen()) ? 1 : -1;
    return (braces[_type + off].equals(other.getType()));
  }

  
  public boolean isDoubleQuote() {
    return this.getType().equals(DOUBLE_QUOTE);
  }

  public boolean isSingleQuote() {
    return this.getType().equals(SINGLE_QUOTE);
  }


  
  public boolean isLineComment() {
    return this.getType().equals(LINE_CMT);
  }

  
  public boolean isBlockCommentStart() {
    return this.getType().equals(BLK_CMT_BEG);
  }

  
  public boolean isBlockCommentEnd() {
    return this.getType().equals(BLK_CMT_END);
  }

  
  public boolean isNewline() {
    return this.getType().equals(EOLN);
  }

  
  public boolean isMultipleCharBrace() {
    return isLineComment() || isBlockCommentStart() ||
           isBlockCommentEnd() || isDoubleEscapeSequence();
  }

  
  public boolean isDoubleEscapeSequence() {
    return  isDoubleEscape() || isEscapedDoubleQuote() ||
      isEscapedSingleQuote();
  }

  
  public boolean isDoubleEscape() {
    return  this.getType().equals("\\\\");
  }

  
  public boolean isEscapedDoubleQuote() {
    return  this.getType().equals("\\\"");
  }

  
  public boolean isEscapedSingleQuote() {
    return this.getType().equals("\\'");
  }

  
  public boolean isGap() {
    return  false;
  }

  
  public boolean isSlash() {
    return this.getType().equals(SLASH);
  }

  
  public boolean isStar() {
    return this.getType().equals(STAR);
  }

  
  public void grow(int delta) {
    throw new RuntimeException("Braces can't grow.");
  }

  
  public void shrink(int delta) {
    throw new RuntimeException("Braces can't shrink.");
  }

}



class BraceException extends RuntimeException {

  
  public BraceException(String s) {
    super(s);
  }
}



