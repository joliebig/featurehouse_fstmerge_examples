

package edu.rice.cs.util.text;

import java.awt.EventQueue;
import java.awt.print.Pageable;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.Position;
import javax.swing.text.BadLocationException;

import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.swing.Utilities;

import java.util.HashMap;


public class SwingDocument extends DefaultStyledDocument implements EditDocumentInterface, AbstractDocumentInterface {
  
  
  protected volatile boolean _isModifiedSinceSave = false;
  
  
  final protected HashMap<String, AttributeSet> _styles;
  
  
  protected DocumentEditCondition _condition;
  
  
  protected static final Object _wrappedPosListLock = new Object();
  
  
  public SwingDocument() { 
    _styles = new HashMap<String, AttributeSet>();
    _condition = new DocumentEditCondition();
  }
  
  
  public void setDocStyle(String name, AttributeSet s) {
    _styles.put(name, s);  
  }
  
  
  public AttributeSet getDocStyle(String name) {
    return _styles.get(name);  
  }
  
  
  public void addColoring(int start, int end, String style) { }
  
  
  public DocumentEditCondition getEditCondition() { return _condition; }
  
  
  public void setEditCondition(DocumentEditCondition condition) { _condition = condition;  }
  
  
  public void clear() {
    try { remove(0, getLength()); }
    catch(BadLocationException e) { throw new UnexpectedException(e); }
  }
  
  
  public void insertText(int offs, String str, String style) {
    if (_condition.canInsertText(offs)) forceInsertText(offs, str, style); 
  }
  
  
  public void forceInsertText(int offs, String str, String style) {
    int len = getLength();
    if ((offs < 0) || (offs > len)) {
      String msg = "Offset " + offs + " passed to SwingDocument.forceInsertText is out of bounds [0, " + len + "]";
      throw new EditDocumentException(null, msg);
    }
    AttributeSet s = null;
    if (style != null) s = getDocStyle(style);
    try { super.insertString(offs, str, s); }
    catch (BadLocationException e) { throw new EditDocumentException(e); }  
  }
  
 
  public void insertString(int offs, String str, AttributeSet set) throws BadLocationException {
    
    
    if (_condition.canInsertText(offs)) super.insertString(offs, str, set);
  }
  
  
  public void removeText(int offs, int len) {
    if (_condition.canRemoveText(offs)) forceRemoveText(offs, len); 
  }
  
  
  public void forceRemoveText(int offs, int len) {
    try { super.remove(offs, len); }
    catch (BadLocationException e) { throw new EditDocumentException(e); }
  }
  
  
  public void remove(int offs, int len) throws BadLocationException {
    if (_condition.canRemoveText(offs))  super.remove(offs, len); 
  }
  


  
  
  public String getDocText(int offs, int len) {
    try { return getText(offs, len); }  
    catch (BadLocationException e) { throw new EditDocumentException(e); }
  }
  
  
  public String getText() { 
    try { return getText(0, getLength()); }  
    catch (BadLocationException e) { throw new UnexpectedException(e); }  
  }
 
  
  public String _getText(int pos, int len) { 
    try { return getText(pos, len); }  
    catch (BadLocationException e) { throw new UnexpectedException(e); }
  }
  
  
  public void append(String str, AttributeSet set) {
    try { insertString(getLength(), str, set); }
    catch (BadLocationException e) { throw new UnexpectedException(e); }  
  }
  
  
  public void append(String str, String style) { append(str, style == null ? null : getDocStyle(style)); }
  
  
  public void append(String str) { append(str, (AttributeSet) null); }
  
  
  public String getDefaultStyle() { return null; }
  
  public void print() { throw new UnsupportedOperationException("Printing not supported"); }
  
  public Pageable getPageable() { throw new UnsupportedOperationException("Printing not supported"); }

  
  public Position createUnwrappedPosition(int offs) throws BadLocationException { return super.createPosition(offs); }
}

