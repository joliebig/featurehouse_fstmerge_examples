

package edu.rice.cs.drjava.model.definitions.reducedmodel;


public class TokenList extends ModelList<ReducedToken>
    implements  ReducedModelStates {
  
  public TokenList.Iterator _getIterator() {
    return new TokenList.Iterator();
  }

  public class Iterator extends ModelList<ReducedToken>.Iterator {

    private int _offset;

    public Iterator() {
      ((ModelList<ReducedToken>) TokenList.this).
      super();
      _offset = 0;
    }

    Iterator(Iterator that) {
      ((ModelList<ReducedToken>) TokenList.this).
      super(that);
      _offset = that.getBlockOffset();
    }

    
    public TokenList.Iterator _copy() { return new Iterator(this); }

    public void setTo(TokenList.Iterator that) {
      super.setTo(that);
      _offset = that.getBlockOffset();
    }

    public int getBlockOffset() { return _offset; }

    public void setBlockOffset(int offset) { _offset = offset; }

    
    public ReducedModelState getStateAtCurrent() {
      if (atFirstItem() || atStart() || TokenList.this.isEmpty()) {
        return FREE;
      }
      else if (prevItem().isLineComment() ||
               (prevItem().getState() == INSIDE_LINE_COMMENT)) {
        return INSIDE_LINE_COMMENT;
      }
      else if (prevItem().isBlockCommentStart() ||
               (prevItem().getState() == INSIDE_BLOCK_COMMENT)) {
        return INSIDE_BLOCK_COMMENT;
      }
      else if ((prevItem().isDoubleQuote() && prevItem().isOpen() &&
                (prevItem().getState() == FREE)) ||
               (prevItem().getState() == INSIDE_DOUBLE_QUOTE)) {
        return INSIDE_DOUBLE_QUOTE;
      }
      else if ((prevItem().isSingleQuote() && prevItem().isOpen() &&
                (prevItem().getState() == FREE)) ||
               (prevItem().getState() == INSIDE_SINGLE_QUOTE)) {
        return INSIDE_SINGLE_QUOTE;
      }
      else {
        return FREE;
      }
    }


    
    void insertBraceToGap(String text) {
      this.current().shrink(this.getBlockOffset());
      this.insert(Brace.MakeBrace(text, getStateAtCurrent()));
      
      
      if (this.getBlockOffset() > 0) {
        this.insert(new Gap(this.getBlockOffset(), getStateAtCurrent()));
        this.next(); 
      }
      this.next(); 
      this.setBlockOffset(0);
    }

    
    void insertNewBrace(String text) {
      this.insert(Brace.MakeBrace(text, getStateAtCurrent()));
      this.next();
      this.setBlockOffset(0);
    }

    
    void _splitCurrentIfCommentBlock(boolean splitClose,
                                     boolean splitEscape) {
      String type = current().getType();
      if (type.equals("//") ||
          type.equals("/*") ||
          (splitClose && type.equals("*/")) ||
          (splitEscape && type.equals("\\\\")) ||
          (splitEscape && type.equals("\\\"")) ||
          (splitEscape && type.equals("\\'"))) {
        String first = type.substring(0, 1);
        String second = type.substring(1, 2);
        
        current().setType(first);
        ReducedModelState oldState = current().getState();

        
        next();
        insert(Brace.MakeBrace(second, oldState));
        
        prev();
      }
    }


    
    void updateBasedOnCurrentState() {
      if (this.atStart()) this.next();

      
      if (this.atEnd()) return;

      ReducedModelState curState = this.getStateAtCurrent();
      
      while (! this.atEnd()) {
        curState = curState.update(this);
      }
    }


    
    public void move(int count) { _offset = _move(count, _offset); }

    
    private int _move(int count, int currentOffset) {
      int retval = currentOffset;
      if (count == 0)  return retval;

      TokenList.Iterator it = this._copy();

      
      if (count > 0) {
        retval = it._moveRight(count, currentOffset);
      }
      else {
        retval = it._moveLeft(Math.abs(count), currentOffset);
      }
      this.setTo(it);
      it.dispose();
      return retval;
    }

    
    private int _moveRight(int count, int currentOffset) {
      if (this.atStart()) {
        currentOffset = 0;
        this.next();
      }
      if (this.atEnd()) {
        throw new IllegalArgumentException("At end");
      }
      while (count >= this.current().getSize() - currentOffset) {
        count = count - this.current().getSize() + currentOffset;
        this.next();
        currentOffset = 0;
        if (this.atEnd()) {
          if (count == 0) {
            break;
          }
          else {
            throw new IllegalArgumentException("Moved into tail");
          }
        }
      }
      return count + currentOffset; 
    }

    
    private int _moveLeft(int count, int currentOffset) {
      if (this.atEnd()) {
        this.prev();
        if (!this.atStart()) 
        {
          currentOffset = this.current().getSize();
        }
      }

      if (this.atStart()) {
        throw new IllegalArgumentException("At Start");
      }
      while (count > currentOffset) {
        count = count - currentOffset;
        this.prev();

        if (this.atStart()) {
          if (count > 0) {
            throw new IllegalArgumentException("At Start");
          }
          else {
            this.next();
            currentOffset = 0;
          }
        }
        else {
          currentOffset = this.current().getSize();
        }
      }
      return currentOffset - count;
    }


    
    public void delete(int count) {
      if (count == 0) return;
      TokenList.Iterator copyCursor = this._copy();
      
      
      _offset = _delete(count, copyCursor);
      copyCursor.dispose();
      return;
    }

    
    private int _delete(int count, TokenList.Iterator copyCursor) {
      
      try {
        if (count > 0) {
          copyCursor.move(count);
        }
        else { 
          this.move(count);
        }
        return deleteRight(copyCursor);
      }
      catch (Exception e) {
        throw new IllegalArgumentException("Trying to delete past end of file.");
      }
    }

    
    void clipLeft() {
      if (atStart()) {
        return;
      }
      else if (getBlockOffset() == 0) {
        remove();
      }
      else if (current().isGap()) {
        int size = current().getSize();
        this.current().shrink(size - getBlockOffset());
      }
      else if (current().isMultipleCharBrace()) {
        if (getBlockOffset() != 1) {
          throw new IllegalArgumentException("Offset incorrect");
        }
        else {
          String type = current().getType();
          String first = type.substring(0, 1);
          this.current().setType(first);
        }
      }
      else {
        throw new IllegalArgumentException("Cannot clip left.");
      }
    }

    
    void clipRight() {
      if (this.atEnd()) {
        return;
      }
      else if (this.getBlockOffset() == 0) {
        return;
      }
      else if (this.getBlockOffset() == this.current().getSize()) {
        this.remove();
      }
      else if (this.current().isGap()) {
        this.current().shrink(this.getBlockOffset());
      }
      else if (this.current().isMultipleCharBrace()) {
        if (this.getBlockOffset() != 1) {
          throw new IllegalArgumentException("Offset incorrect");
        }
        else {
          String type = this.current().getType();
          String second = type.substring(1, 2);
          this.current().setType(second);
        }
      }
      else {
        throw new IllegalArgumentException("Cannot clip left.");
      }
    }

    
    int deleteRight(TokenList.Iterator delTo) {
      this.collapse(delTo);

      
      if (this.eq(delTo) && this.current().isGap()) {
        
        this.current().shrink(delTo.getBlockOffset() - this.getBlockOffset());
        return this.getBlockOffset();
      }


      
      
      if (!this.eq(delTo)) {
        this.clipLeft();
      }
      delTo.clipRight();

      if (!this.atStart()) {
        this.prev();
      }
      int delToSizeCurr;
      String delToTypeCurr;
      if (delTo.atEnd()) {
        this.setTo(delTo);
        return 0;
      }
      else {
        delToSizeCurr = delTo.current().getSize();
        delToTypeCurr = delTo.current().getType();
      }

      
      delTo.prev(); 

      int delToSizePrev;
      String delToTypePrev;
      if (delTo.atStart()) { 
        delTo.next();
        this.setTo(delTo);
        return 0;
      }
      else {
        delToSizePrev = delTo.current().getSize();
        delToTypePrev = delTo.current().getType();
      }
      delTo.next(); 

      int temp = _calculateOffset(delToSizePrev, delToTypePrev,
                                  delToSizeCurr, delToTypeCurr,
                                  delTo);
      this.setTo(delTo);
      return temp;
    }


    
    private int _calculateOffset(int delToSizePrev, String delToTypePrev,
                                 int delToSizeCurr, String delToTypeCurr,
                                 TokenList.Iterator delTo) {
      int offset;
      int delToSizeChange = delTo.current().getSize();


      
      
      
      
      if (delTo.atEnd()) {
        throw new IllegalArgumentException("Shouldn't happen");
      }
      if (delTo.current().isGap()) {
        return delToSizeChange - delToSizeCurr;
      }
      
      

      
      
      
      

      
      
      
      
      
      
      

      
      
      
      
      
      
      if (((delToTypePrev.equals("/")) &&
           
           ((delToTypeCurr.equals("/*") &&
             _checkPrevEquals(delTo, "//")) ||
            
            (delToTypeCurr.equals("//") &&
             _checkPrevEquals(delTo, "//")))) ||

          ((delToTypePrev.equals("*")) &&
           
           ((delToTypeCurr.equals("/*") &&
             _checkPrevEquals(delTo, "*/")) ||
            
            (delToTypeCurr.equals("//") &&
             _checkPrevEquals(delTo, "*/")))) ||

          ((delToTypePrev.equals("\\")) &&
           
           ((delToTypeCurr.equals("\\\\") &&
             _checkPrevEquals(delTo, "\\")) ||
            
            (delToTypeCurr.equals("\\'") &&
             _checkPrevEquals(delTo, "'")) ||
            
            (delToTypeCurr.equals("\\\"") &&
             _checkPrevEquals(delTo, "\""))))) {
        delTo.prev();
        offset = 1;
      }
      
      else if (((delToTypePrev.equals("/")) &&
                
                ((delToTypeCurr.equals("*/") &&
                  delTo.current().getType().equals("/*")) ||
                 (delToTypeCurr.equals("*") &&
                  delTo.current().getType().equals("/*")) ||
                 (delToTypeCurr.equals("/") &&
                  delTo.current().getType().equals("//")))) ||

               ((delToTypePrev.equals("*")) &&
                ((delToTypeCurr.equals("/") &&
                  delTo.current().getType().equals("*/")))) ||

               ((delToTypePrev.equals("\\")) &&
                ((delToTypeCurr.equals("\\") &&
                  delTo.current().getType().equals("\\\\")) ||
                 (delToTypeCurr.equals("'") &&
                  delTo.current().getType().equals("\\'")) ||
                 (delToTypeCurr.equals("\"") &&
                  delTo.current().getType().equals("\\\""))))) {
        offset = 1;
      }
      
      
      else {
        offset = 0;
      }
      return offset;
    }

    
    private boolean _checkPrevEquals(TokenList.Iterator delTo,
                                     String match) {
      if (delTo.atFirstItem() || delTo.atStart()) return false;
      return delTo.prevItem().getType().equals(match);
    }

    public String toString() { return "" + this.current(); }

  }
}
