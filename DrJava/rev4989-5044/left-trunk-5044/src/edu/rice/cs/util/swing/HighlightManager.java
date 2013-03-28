

package edu.rice.cs.util.swing;

import java.util.Stack;
import java.util.Vector;

import javax.swing.text.JTextComponent;
import javax.swing.text.Highlighter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Position;

import edu.rice.cs.util.UnexpectedException;

import static edu.rice.cs.plt.object.ObjectUtil.hash;


public class HighlightManager {
  
  
  
  
  private Vector<Stack<HighlightInfo>> _highlights;
  
  
  private JTextComponent _component;
  
  
  public HighlightManager(JTextComponent jtc) {
    _component = jtc;
    _highlights = new Vector<Stack<HighlightInfo>>();
  }
  
  
  
  public String toString() { return "HighLightManager(" + _highlights + ")"; }
  
  
  
  public int size() { return _highlights.size(); }
  
  
  public synchronized HighlightInfo addHighlight(int startOffset, int endOffset, Highlighter.HighlightPainter p) {
    
    HighlightInfo newLite = new HighlightInfo(startOffset,endOffset,p);
    

    Stack<HighlightInfo> lineStack = _getStackAt(newLite);
    
    if (lineStack != null) {
      int searchResult = lineStack.search(newLite);
      if (searchResult == 1) return lineStack.peek();
      if (searchResult > 1) {
        lineStack.remove(newLite);
      }
    }
    else {
      
      lineStack = new Stack<HighlightInfo>();
       _highlights.add(lineStack);
    }
    
    try {
      Object highlightTag = _component.getHighlighter().addHighlight(startOffset,endOffset,p);
      newLite.setHighlightTag(highlightTag);
      lineStack.push(newLite);
      return newLite;
    }
    catch (BadLocationException ble) {
      
      if (lineStack.isEmpty()) {
        _highlights.remove(lineStack);
      }
      throw new UnexpectedException(ble);
    }
  }
  
  
  private Stack<HighlightInfo> _getStackAt(HighlightInfo h) {
    
    for (Stack<HighlightInfo> stack : _highlights) {
      if (stack.get(0).matchesRegion(h)) {
        return stack;
      }
    }
    
    return null;
  }
  
  
  public synchronized void removeHighlight(int startOffset, int endOffset, Highlighter.HighlightPainter p) {
    HighlightInfo newLite = new HighlightInfo(startOffset, endOffset, p);
    removeHighlight(newLite);
  }
  
  
  public void removeHighlight(HighlightInfo newLite) {
    
    


    
    Stack<HighlightInfo> lineStack = _getStackAt(newLite);
    
    if (lineStack== null) {
      
      return;
    }
    
    int searchResult = lineStack.search(newLite);
    
    
    if (searchResult == 1) {
      HighlightInfo liteToRemove = lineStack.pop();
      _component.getHighlighter().removeHighlight(liteToRemove.getHighlightTag());
      
    }
    else if (searchResult > 1) {
      
      lineStack.remove(newLite);
      _component.getHighlighter().removeHighlight(newLite.getHighlightTag());
    }
    
    if (lineStack.isEmpty()) {
      
      
      _highlights.remove(lineStack);
    }
    
  }
  
  
  public class HighlightInfo {
    private Object _highlightTag;
    private Position _startPos;
    private Position _endPos;
    private Highlighter.HighlightPainter _painter;
    
    
    public HighlightInfo(int from, int to, Highlighter.HighlightPainter p) {
      
      _highlightTag = null;
      try {
        _startPos = _component.getDocument().createPosition(from);
        _endPos = _component.getDocument().createPosition(to);
      }
      catch (BadLocationException ble) { throw new UnexpectedException(ble); }
      
      _painter = p;
    }
    
    
    public void setHighlightTag ( Object highlightTag) { _highlightTag = highlightTag; }
    
    
    public boolean equals(Object o) {
      
      if (o == null || ! (o instanceof HighlightInfo)) return false;  
      
        
      HighlightInfo hi = (HighlightInfo)o;
      
      boolean result = getStartOffset() == hi.getStartOffset() && 
        getEndOffset() == hi.getEndOffset() &&
        getPainter() == hi.getPainter();
      
      
      return result;
    }
    
    
    public int hashCode() { return hash(getPainter(), getStartOffset(), getEndOffset()); }
    
    public void remove() { removeHighlight(this); }
    
    
    public Object getHighlightTag() { return _highlightTag; }
    
    
    public Highlighter.HighlightPainter getPainter() { return _painter; }
    
    
    public int getStartOffset() { return _startPos.getOffset(); }
    
    
    public int getEndOffset() { return _endPos.getOffset(); }
    
    
    public boolean matchesRegion(HighlightInfo h) {
      return (getStartOffset() == h.getStartOffset() && getEndOffset() == h.getEndOffset());
    }
    
    
    public void refresh(Highlighter.HighlightPainter p ) {
      
      this.remove();
      HighlightInfo newHighlight = addHighlight(getStartOffset(), getEndOffset(), p);
      
      _painter = p;
      
      _highlightTag = newHighlight.getHighlightTag();
    }
  }
}
