

package edu.rice.cs.drjava.model.definitions.reducedmodel;


public class TokenList extends ModelList<ReducedToken> implements  ReducedModelStates {
  
  
  public Iterator getIterator() { return new Iterator(); }
  
  public class Iterator extends ModelIterator {
    
    private int _offset;
    
    public Iterator() {
      super();
      _offset = 0;
    }
    
    private Iterator(Iterator that) {
      super(that);
      _offset = that.getBlockOffset();
    }
    
    
    public Iterator copy() { return new Iterator(this); }
    
    public void setTo(Iterator that) {
      super.setTo(that);
      _offset = that.getBlockOffset();
    }
    
    public int getBlockOffset() { return _offset; }
    
    public void setBlockOffset(int offset) { _offset = offset; }
    
    
    public ReducedModelState getStateAtCurrent() {
      if (atFirstItem() || atStart() || TokenList.this.isEmpty())  return FREE;
      else if (prevItem().isLineComment() || (prevItem().getState() == INSIDE_LINE_COMMENT))
        return INSIDE_LINE_COMMENT;
      else if (prevItem().isBlockCommentStart() || (prevItem().getState() == INSIDE_BLOCK_COMMENT))
        return INSIDE_BLOCK_COMMENT;
      else if ((prevItem().isDoubleQuote() && prevItem().isOpen() && (prevItem().getState() == FREE)) ||
               (prevItem().getState() == INSIDE_DOUBLE_QUOTE))
        return INSIDE_DOUBLE_QUOTE;
      else if ((prevItem().isSingleQuote() && prevItem().isOpen() && (prevItem().getState() == FREE)) ||
               (prevItem().getState() == INSIDE_SINGLE_QUOTE))
        return INSIDE_SINGLE_QUOTE;
      else return FREE;
    }
    
    
    
    void insertBraceToGap(String text) {
      current().shrink(getBlockOffset());
      insert(Brace.MakeBrace(text, getStateAtCurrent()));
      
      
      if (getBlockOffset() > 0) {
        insert(new Gap(getBlockOffset(), getStateAtCurrent()));
        next(); 
      }
      next(); 
      setBlockOffset(0);
    }
    
    
    void insertNewBrace(String text) {
      insert(Brace.MakeBrace(text, getStateAtCurrent()));
      next();
      setBlockOffset(0);
    }
    
    
    void _splitCurrentIfCommentBlock(boolean splitClose, boolean splitEscape) {
      String type = current().getType();
      if (type.equals("//") || type.equals("/*") ||
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
      if (atStart()) next();
      
      
      if (atEnd()) return;
      
      ReducedModelState curState = getStateAtCurrent();
      
      while (! atEnd()) { curState = curState.update(this); }
    }
    
    
    public void move(int count) { _offset = _move(count, _offset); }
    
    
    private int _move(int count, int currentOffset) {
      if (count == 0) return currentOffset;
      Iterator it = this;
      
      
      if (count > 0) return it._moveRight(count, currentOffset);
      return it._moveLeft(- count, currentOffset);  
    }
    
    
    private int _moveRight(int count, int currentOffset) {
      
      if (atStart()) {
        currentOffset = 0;
        next();
      }
      if (atEnd()) throw new IllegalArgumentException("At end");
      
      
      int size = current().getSize();
      count = count + currentOffset;
      
      
      while (count >= size) { 
        count = count - size;
        next();
        if (atEnd()) {
          if (count == 0) break;
          else throw new IllegalArgumentException("At end");
        }
        size = current().getSize();
      }
      return count; 
    }
    
    
    private int _moveLeft(int count, int currentOffset) {
      
      
      if (atEnd()) {
        assert currentOffset == 0;
        prev();
        if (atStart()) throw new IllegalArgumentException("At Start");  
        currentOffset = current().getSize(); 
      }
      else if (atStart()) throw new IllegalArgumentException("At Start");
      
      while (count > currentOffset) {
        count = count - currentOffset;
        prev();
        
        if (atStart()) throw new IllegalArgumentException("At Start");  
        currentOffset = current().getSize();
      }
      return currentOffset - count;  
    }
    
    
    public void delete(int count) {
      if (count == 0) return;
      Iterator copyCursor = copy();
      
      
      _offset = _delete(count, copyCursor);
      copyCursor.dispose();
      return;
    }
    
    
    private int _delete(int count, Iterator copyCursor) {
      
      try {
        if (count > 0) copyCursor.move(count);
        else move(count); 
        return deleteRight(copyCursor);
      }
      catch (Exception e) { throw new IllegalArgumentException("Trying to delete past end of file."); }
    }
    
    
    void clipLeft() {
      if (atStart())  return;
      else if (getBlockOffset() == 0) remove();
      else if (current().isGap()) {
        int size = current().getSize();
        current().shrink(size - getBlockOffset());
      }
      else if (current().isMultipleCharBrace()) {
        if (getBlockOffset() != 1) throw new IllegalArgumentException("Offset incorrect");
        else {
          String type = current().getType();
          String first = type.substring(0, 1);
          current().setType(first);
        }
      }
      else throw new IllegalArgumentException("Cannot clip left.");
    }
    
    
    void clipRight() {
      if (atEnd()) return;
      else if (getBlockOffset() == 0) return;
      else if (getBlockOffset() == current().getSize()) remove();
      else if (current().isGap()) current().shrink(getBlockOffset());
      else if (current().isMultipleCharBrace()) {
        if (getBlockOffset() != 1) throw new IllegalArgumentException("Offset incorrect");
        else {
          String type = current().getType();
          String second = type.substring(1, 2);
          current().setType(second);
        }
      }
      else throw new IllegalArgumentException("Cannot clip left.");
    }
    
    
    int deleteRight(Iterator delTo) {
      collapse(delTo);
      
      
      if (eq(delTo) && current().isGap()) {
        
        current().shrink(delTo.getBlockOffset() - getBlockOffset());
        return getBlockOffset();
      }
      
      
      
      
      if (! eq(delTo)) clipLeft();
      delTo.clipRight();
      
      if (! atStart()) prev();
      int delToSizeCurr;
      String delToTypeCurr;
      if (delTo.atEnd()) {
        setTo(delTo);
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
        setTo(delTo);
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
    
    
    
    private int _calculateOffset(int delToSizePrev, String delToTypePrev, int delToSizeCurr, String delToTypeCurr,
                                 Iterator delTo) {
      int offset;
      int delToSizeChange = delTo.current().getSize();

      
      
      
      
      
      if (delTo.atEnd()) throw new IllegalArgumentException("Shouldn't happen");
      if (delTo.current().isGap()) return delToSizeChange - delToSizeCurr;
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      if ((delToTypePrev.equals("/") &&
           
           ((delToTypeCurr.equals("/*") && _checkPrevEquals(delTo, "//")) ||
            
            (delToTypeCurr.equals("//") && _checkPrevEquals(delTo, "//")))) ||
          
          (delToTypePrev.equals("*") &&
           
           ((delToTypeCurr.equals("/*") && _checkPrevEquals(delTo, "*/")) ||
            
            (delToTypeCurr.equals("//") && _checkPrevEquals(delTo, "*/")))) ||
          
          (delToTypePrev.equals("\\") &&
           
           ((delToTypeCurr.equals("\\\\") && _checkPrevEquals(delTo, "\\")) ||
            
            (delToTypeCurr.equals("\\'") && _checkPrevEquals(delTo, "'")) ||
            
            (delToTypeCurr.equals("\\\"") && _checkPrevEquals(delTo, "\""))))) {
        delTo.prev();
        offset = 1;
      }
      
      else if ((delToTypePrev.equals("/") &&
                
                ((delToTypeCurr.equals("*/") && delTo.current().getType().equals("/*")) ||
                 (delToTypeCurr.equals("*") && delTo.current().getType().equals("/*")) ||
                 (delToTypeCurr.equals("/") && delTo.current().getType().equals("//")))) ||
               
               (delToTypePrev.equals("*") &&
                delToTypeCurr.equals("/") && delTo.current().getType().equals("*/")) ||
               
               (delToTypePrev.equals("\\") &&
                ((delToTypeCurr.equals("\\") && delTo.current().getType().equals("\\\\")) ||
                 (delToTypeCurr.equals("'") && delTo.current().getType().equals("\\'")) ||
                 (delToTypeCurr.equals("\"") && delTo.current().getType().equals("\\\""))))) {
        offset = 1;
      }
      
      else offset = 0;
      return offset;
    }
    
    
    private boolean _checkPrevEquals(Iterator delTo, String match) {
      if (delTo.atFirstItem() || delTo.atStart()) return false;
      return delTo.prevItem().getType().equals(match);
    }
    
    public String toString() { return "" + current(); }
    
  }
}
