

package edu.rice.cs.drjava.model;

import java.util.Arrays;

import static edu.rice.cs.plt.object.ObjectUtil.hash;

public interface Query {
  
  abstract static class Pos implements Query {
    private final int _pos;
    
    Pos(final int pos) { _pos = pos; }
    
    public boolean equals(Object other) {
      if (other == null || other.getClass() != this.getClass()) return false;
      Pos o = (Pos) other;
      return o._pos == _pos;
    }
    
    public int hashCode() { return hash(getClass().hashCode(), _pos); }
  }
  
  public static class IndentInformation extends Pos {
    public IndentInformation(int pos) { super(pos); }
  }
  
  abstract static class AbstractEnclosingBrace implements Query {
    private final int _pos;
    private final char _opening;
    private final char _closing;
    
    public AbstractEnclosingBrace(final int pos, final char opening, final char closing) { 
      _pos = pos; 
      _opening = opening;
      _closing = closing;
    }
    
    public boolean equals(Object other) {
      if (other == null || other.getClass() != getClass()) return false;
      AbstractEnclosingBrace o = (AbstractEnclosingBrace) other;
      return o._pos == _pos && o._opening == _opening && o._closing == _closing;
    }
    
    public int hashCode() { return hash(getClass().hashCode(), _pos, _opening, _closing); }
  }
  
  public static class PrevEnclosingBrace extends AbstractEnclosingBrace {
    public PrevEnclosingBrace(int pos, char opening, char closing) { super(pos, opening, closing); }
  }
  
  public static class NextEnclosingBrace extends AbstractEnclosingBrace {
    public NextEnclosingBrace(int pos, char opening, char closing) { super(pos, opening, closing); }
  }
  
  abstract static class CharArrayAndFlag implements Query {
    private final int _pos;
    private final char[] _chars;
    private final boolean _flag;
    
    public CharArrayAndFlag(int pos, char[] chars, boolean flag) { 
      _pos = pos; 
      _chars = chars;
      _flag = flag;
    }
    
    public boolean equals(Object other) {
      if (other == null || other.getClass() != getClass()) return false;
      CharArrayAndFlag o = (CharArrayAndFlag) other;
      return o._pos == _pos && Arrays.equals(o._chars, _chars) && o._flag == _flag;
    }
    
    public int hashCode() { 
      return hash(getClass().hashCode(), _pos, _chars[0], _chars[_chars.length-1], (_flag ? 1 : 0)); 
    }
  }
  
  public static class PrevDelimiter extends CharArrayAndFlag {
    public PrevDelimiter(int pos, char[] delims, boolean skipParenPhrases) { super(pos, delims, skipParenPhrases); }
  }
  
  public static class PrevCharPos implements Query {
    private final int _pos;
    private final char[] _whitespace;
    
    public PrevCharPos(int pos, final char[] whitespace) { 
      _pos = pos; 
      _whitespace = whitespace;
    }
    
    public boolean equals(Object other) {
      if (other == null || other.getClass() != getClass()) return false;
      PrevCharPos o = (PrevCharPos) other;
      return o._pos == _pos && Arrays.equals(o._whitespace, _whitespace);
    }
    
    public int hashCode() { 
      return hash(getClass().hashCode(), _pos, _whitespace[0], _whitespace[_whitespace.length-1]); 
    }
  }
  
  public static class IndentOfCurrStmt implements Query {
    private final int _pos;
    private final char[] _delims;
    private final char[] _whitespace;
    
    public IndentOfCurrStmt(int pos, char[] delims, char[] whitespace) { 
      _pos = pos;
      _delims = delims;
      _whitespace = whitespace;
    }
    
    public boolean equals(Object other) {
      if (other == null || other.getClass() != getClass()) return false;
      IndentOfCurrStmt o = (IndentOfCurrStmt) other;
      return o._pos == _pos && Arrays.equals(o._delims, _delims) && Arrays.equals(o._whitespace, _whitespace);
    }
    
    public int hashCode() { 
      return hash(getClass().hashCode(), _pos ^_delims[0], _delims[_delims.length-1], _whitespace[0], 
        _whitespace[_whitespace.length-1]); 
    }
  }
  
  public static class CharOnLine implements Query {
    private final int _pos;
    private final char _findChar;
    
    public CharOnLine(int pos, char findChar) { 
      _pos = pos;
      _findChar = findChar;
    }
    
    public boolean equals(Object other) {
      if (other == null || other.getClass() != getClass()) return false;
      CharOnLine o = (CharOnLine) other;
      return o._pos == _pos && o._findChar == _findChar;
    }
    
    public int hashCode() { return hash(getClass().hashCode(), _pos, _findChar); }
  }
  
  public static class LineStartPos extends Pos {
    public LineStartPos(int pos) { super(pos); }
  }
  
  public static class LineEndPos extends Pos {
    public LineEndPos(int pos) { super(pos); }
  }
  
  public static class LineFirstCharPos extends Pos {
    public LineFirstCharPos(int pos) { super(pos); }
  }
  
  public static class FirstNonWSCharPos extends CharArrayAndFlag {
    FirstNonWSCharPos(int pos, char[] whitespace, boolean acceptComments) { super(pos, whitespace, acceptComments); }
  }
  
  public static class PosInParenPhrase extends Pos {
    public PosInParenPhrase(int pos) { super(pos); }
  }
  
  public static class LineEnclosingBrace extends Pos {
    public LineEnclosingBrace(int pos) { super(pos); }
  }
  
  public static class EnclosingBrace extends Pos {
    public EnclosingBrace(int pos) { super(pos); }
  }
  
  public static class PosNotInBlock extends Pos {
    public PosNotInBlock(int pos) { super(pos); }
  }
  
  public static class PosInBlockComment extends Pos {
    public PosInBlockComment(int pos) { super(pos); }
  }
  
  public static class AnonymousInnerClass implements Query {
    private final int _pos;
    private final int _openCurlyPos;
    
    public AnonymousInnerClass(int pos, int openCurlyPos) {
      _pos = pos;
      _openCurlyPos = openCurlyPos;
    }
    
    public boolean equals(Object other) {
      if (other == null || other.getClass() != getClass()) return false;
      AnonymousInnerClass o = (AnonymousInnerClass) other;
      return o._pos == _pos && o._openCurlyPos == _openCurlyPos;
    }
    
    public int hashCode() { return hash(getClass().hashCode(), _pos, _openCurlyPos); }
  }
  
  public static class AnonymousInnerClassIndex extends Pos {
    public AnonymousInnerClassIndex(int pos) { super(pos); }
  }
  
  public static class EnclosingClassName implements Query {
    private int _pos;
    private boolean _qual;
    public EnclosingClassName(int pos, boolean qual) {
      _pos = pos;
      _qual = qual;
    }
    public boolean equals(Object other) {
      if (other == null || other.getClass() != getClass()) return false;
      EnclosingClassName o = (EnclosingClassName) other;
      return o._pos == _pos && o._qual == _qual;
    }
    
    public int hashCode() { return hash(getClass().hashCode(), _pos, (_qual ? 1 : 0)); }
  }
}