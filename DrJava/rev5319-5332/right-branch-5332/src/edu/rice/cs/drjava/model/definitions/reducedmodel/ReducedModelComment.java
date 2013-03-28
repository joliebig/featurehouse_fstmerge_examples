

package edu.rice.cs.drjava.model.definitions.reducedmodel;



public class ReducedModelComment extends AbstractReducedModel {
  
  
  TokenList.Iterator _walker;
  
  
  public ReducedModelComment() {
    super();
    _walker = _cursor.copy();
  }
  
  public void insertChar(char ch) {
    switch(ch) {
      case '*': insertSpecial("*"); break;
      case '/': insertSpecial("/"); break;
      case '\n': insertNewline(); break;
      case '\\': insertSpecial("\\"); break;
      case '\'': insertQuote("'"); break;
      case '\"': insertQuote("\""); break;
      default:
        _insertGap(1); break;
    }
  }
  
  
  private void insertSpecial(String special) {
    
    if (_tokens.isEmpty()) {
      _cursor.insertNewBrace(special); 
      return;
    }
    
    if (_cursor.atStart()) _cursor.next();
    
    
    if (_cursor.atEnd()) _checkPreviousInsertSpecial(special);
    
    
    else if (_cursor.getBlockOffset() > 0 && _cursor.current().isMultipleCharBrace()) {
      _cursor._splitCurrentIfCommentBlock(true,true);
      
      _cursor.next(); 
      _cursor.insertNewBrace(special); 
      move(-2);
      _updateBasedOnCurrentState();
      move(2);
    }
    
    else if (_cursor.getBlockOffset() > 0 && _cursor.current().isGap()) {
      _cursor.insertBraceToGap(special);
      _cursor.prev();
      _cursor.prev();
      _updateBasedOnCurrentState();
      
      _cursor.next();
      _cursor.next();
      
    }
    
    else if ((_cursor.getBlockOffset() == 0) && _cursor.current().isMultipleCharBrace()) {
      
      
      
      
      _cursor._splitCurrentIfCommentBlock(false,special.equals("\\"));
      
      
      _checkPreviousInsertSpecial(special);
    }
    else _checkPreviousInsertSpecial(special);
  }
  
  
  private void _checkPreviousInsertSpecial(String special) {
    if (special.equals("\\")) {
      _checkPreviousInsertBackSlash();
    }
    else {
      _checkPreviousInsertCommentChar(special);
    }
  }
  
  
  
  private void _checkPreviousInsertBackSlash() {
    if (!_cursor.atStart()  && !_cursor.atFirstItem()) {
      if (_cursor.prevItem().getType().equals("\\")) {
        _cursor.prevItem().setType("\\\\");
        _updateBasedOnCurrentState();
        return;
      }
    }
    
    _cursor.insertNewBrace("\\"); 
    _cursor.prev();
    _updateBasedOnCurrentState();
    if (_cursor.current().getSize() == 2) _cursor.setBlockOffset(1);
    else _cursor.next();
  }
  
  
  private void _checkPreviousInsertCommentChar(String special) {
    if (!_cursor.atStart()  && !_cursor.atFirstItem()) {
      if ((_cursor.prevItem().getType().equals("/")) && (_cursor.prevItem().getState() == FREE)) {
        _cursor.prevItem().setType("/" + special);
        _updateBasedOnCurrentState();
        return;
      }
      
      else if (_cursor.prevItem().getType().equals("*") &&
               getStateAtCurrent() == INSIDE_BLOCK_COMMENT &&
               special.equals("/")) {
        _cursor.prevItem().setType("*" + special);
        _cursor.prevItem().setState(FREE);
        _updateBasedOnCurrentState();
        return;
      }
    }
    
    _cursor.insertNewBrace(special); 
    _cursor.prev();
    _updateBasedOnCurrentState();
    if (_cursor.current().getSize() == 2) _cursor.setBlockOffset(1);
    else _cursor.next();
  }
  
  
  public void insertNewline() {
    if (_cursor.atStart()) {
      _insertNewEndOfLine();
    }
    else if (_cursor.atEnd()) {
      _insertNewEndOfLine();
    }
    else if ((_cursor.getBlockOffset() > 0) && _cursor.current().isMultipleCharBrace()) {
      _cursor._splitCurrentIfCommentBlock(true, true);
      _cursor.next();
      _cursor.insert(Brace.MakeBrace("\n", getStateAtCurrent()));
      _cursor.prev();
      _updateBasedOnCurrentState();
      _cursor.next();
      _cursor.next();
      _cursor.setBlockOffset(0);
    }
    else if ((_cursor.getBlockOffset() > 0) && _cursor.current().isGap()) {
      _cursor.insertBraceToGap("\n");
      _cursor.prev();
      _cursor.prev();
      _updateBasedOnCurrentState();
      
      _cursor.next();
      _cursor.next();
    }
    else {
      _insertNewEndOfLine();
    }
    return;
  }
  
  private void _insertNewEndOfLine() {
    _cursor.insertNewBrace("\n");
    _cursor.prev();
    _updateBasedOnCurrentState();
    _cursor.next();
    _cursor.setBlockOffset(0);
  }
  
  
  public void insertQuote(String quote) {
    if (_cursor.atStart()) {
      _insertNewQuote(quote);
    }
    else if (_cursor.atEnd()) {
      _insertNewQuote(quote);
    }
    
    else if ((_cursor.getBlockOffset() > 0) && _cursor.current().isMultipleCharBrace()) {
      _cursor._splitCurrentIfCommentBlock(true,true);
      _cursor.next();
      _cursor.insert(Brace.MakeBrace(quote, getStateAtCurrent()));
      _cursor.prev();
      _updateBasedOnCurrentState();
      if (!_cursor.current().isMultipleCharBrace())
        _cursor.next();
      _cursor.next();
      _cursor.setBlockOffset(0);
    }
    
    else if ((_cursor.getBlockOffset() > 0) && _cursor.current().isGap()) {
      _cursor.insertBraceToGap(quote);
      _cursor.prev();
      _cursor.prev();
      _updateBasedOnCurrentState();
      
      _cursor.next();
      _cursor.next();
      
    }
    else _insertNewQuote(quote);
    return;
  }
  
  
  private void _insertNewQuote(String quote) {
    String insert = _getQuoteType(quote);
    _cursor.insertNewBrace(insert);
    _cursor.prev();
    _updateBasedOnCurrentState();
    _cursor.next();
    _cursor.setBlockOffset(0);
  }
  
  
  public ReducedModelState getStateAtCurrent() { return _cursor.getStateAtCurrent(); }
  
  public int walkerOffset() { return absOffset(_walker); }
  
  
  private String _getQuoteType(String quote) {
    if (_cursor.atStart() || _cursor.atFirstItem()) return quote;
    else if (_cursor.prevItem().getType().equals("\\")) {
      _cursor.prev();
      _cursor.remove();
      return "\\" + quote;
    }
    else return quote;
  }
  
  
  protected void insertGapBetweenMultiCharBrace(int length) {
    if (_cursor.getBlockOffset() > 1)
      throw new IllegalArgumentException("OFFSET TOO BIG:  " + _cursor.getBlockOffset());
    
    _cursor._splitCurrentIfCommentBlock(true, true);
    _cursor.next();
    _insertNewGap(length);  
    
    
    
    
    _cursor.prev();
    _cursor.prev();
    _updateBasedOnCurrentState();
    
    _cursor.next();
    _cursor.next();
    return;
  }
  
  
  
  private void _updateBasedOnCurrentState() {
    TokenList.Iterator copyCursor = _cursor.copy();
    copyCursor.updateBasedOnCurrentState();
    copyCursor.dispose();
  }
  
  
  public void move(int count) { _cursor.move(count); }
  
  
  public void delete(int count) {
    if (count == 0) return;
    
    _cursor.delete(count);
    
    
    
    
    
    
    
    
    int absOff = this.absOffset();
    int movement;
    if (absOff < 2) movement = absOff;
    else movement = 2;
    _cursor.move(-movement);
    
    _updateBasedOnCurrentState();
    
    _cursor.move(movement);
    return;
  }
  
  
  public boolean isShadowed() {

    return getStateAtCurrent() != FREE ; 
  }
  
  public boolean isWeaklyShadowed() { return isShadowed() || isOpenComment(); }
  
  public boolean isOpenComment() {
    if (_cursor.atStart() || ! _cursor.atEnd()) return false;
    ReducedToken curToken = _cursor.current();
    return curToken.isCommentStart();
  }
  
  
  
  
  
  
  
  protected ReducedModelState moveWalkerGetState(int relLocation) {
    _walker.move(relLocation);
    return _walker.getStateAtCurrent();
  }
  
  
  protected void resetWalkerLocationToCursor() {
    _walker.dispose();
    _walker = _cursor.copy();
  }
  







  
  
  public int getDistToStart() { 


    return _getDistToStart(_cursor.copy()); 
  }
  
  
  private int _getDistToStart(TokenList.Iterator copyCursor) {

    
    int walkcount = copyCursor.getBlockOffset();
    if (! copyCursor.atStart()) copyCursor.prev();

    
    while (! copyCursor.atStart() && ! copyCursor.current().isNewline()) {
      
      walkcount += copyCursor.current().getSize();

      copyCursor.prev();


    }

    if (copyCursor.atStart()) return -1;



    
    assert copyCursor.current().isNewline();

    return walkcount;
  }
  
  
  int getDistToEnclosingBraceStart(int distToEnclosingBrace) {
    
    TokenList.Iterator copyCursor = _cursor.copy();
    
    if (distToEnclosingBrace == -1 || copyCursor.atStart()) return -1; 
    
    copyCursor.move(- distToEnclosingBrace);
    int walkcount = _getDistToStart(copyCursor);
    
    if (walkcount == -1) return  -1;  
    else return walkcount + distToEnclosingBrace;
  }
  
  
  public int getDistToStart(int relLoc) {
    TokenList.Iterator copyCursor = _cursor.copy();
    copyCursor.move(-relLoc);
    int dist = _getDistToStart(copyCursor);
    copyCursor.dispose();
    if (dist == -1) return -1;
    return dist + relLoc;
  }
  
  
  public int getDistToNextNewline() {
    TokenList.Iterator copyCursor = _cursor.copy();
    if (copyCursor.atStart()) {
      copyCursor.next();
    }
    if (copyCursor.atEnd() || copyCursor.current().getType().equals("\n")) {
      return 0;
    }
    int walkcount = copyCursor.current().getSize() - _cursor.getBlockOffset();
    copyCursor.next();
    
    while ((!copyCursor.atEnd()) &&
           (!(copyCursor.current().getType().equals("\n"))))
    {
      
      walkcount += copyCursor.current().getSize();
      copyCursor.next();
    }
    return walkcount;
  }
}
