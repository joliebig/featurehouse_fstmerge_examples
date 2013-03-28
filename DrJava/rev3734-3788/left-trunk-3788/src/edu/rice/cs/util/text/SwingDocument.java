

package edu.rice.cs.util.text;

import edu.rice.cs.util.UnexpectedException;

import java.awt.print.Pageable;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

import java.util.Hashtable;


public class SwingDocument extends DefaultStyledDocument implements EditDocumentInterface, AbstractDocumentInterface {
  
  
  final protected Hashtable<String, AttributeSet> _styles;

  
  protected DocumentEditCondition _condition;

  
  public SwingDocument() { 
    _styles = new Hashtable<String, AttributeSet>();
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

  
  public void setEditCondition(DocumentEditCondition condition) {
    modifyLock();
    try { _condition = condition; }
    finally { modifyUnlock(); }
  }

  
  public void insertText(int offs, String str, String style) {
    modifyLock();
    try { if (_condition.canInsertText(offs)) forceInsertText(offs, str, style); }
    finally { modifyUnlock(); }
  }

  
  public void forceInsertText(int offs, String str, String style) {
    AttributeSet s = null;
    if (style != null) s = getDocStyle(style);
    
    try { super.insertString(offs, str, s); }
    catch (BadLocationException e) { throw new EditDocumentException(e); }
  }

  
  public void insertString(int offs, String str, AttributeSet set) throws BadLocationException {
    modifyLock();  
    try { if (_condition.canInsertText(offs)) super.insertString(offs, str, set); }
    finally { modifyUnlock(); }
  }

  
  public void removeText(int offs, int len) {
    modifyLock();  
    try { if (_condition.canRemoveText(offs)) forceRemoveText(offs, len); }
    finally { modifyUnlock(); }
  }

  
  public void forceRemoveText(int offs, int len) {
    
    try { super.remove(offs, len); }
    catch (BadLocationException e) { throw new EditDocumentException(e); }
  }

  
  public void remove(int offs, int len) throws BadLocationException {
    modifyLock(); 
    try { if (_condition.canRemoveText(offs))  super.remove(offs, len); }
    finally { modifyUnlock(); }
  }




  
  public String getDocText(int offs, int len) {
    try { return getText(offs, len); }  
    catch (BadLocationException e) { throw new EditDocumentException(e); }
  }
  
  
  public String getText() {
    readLock();
    try { return getText(0, getLength()); }
    catch (BadLocationException e) { throw new UnexpectedException(e); }  
    finally { readUnlock(); }
  }
  
  
  public void append(String str, AttributeSet set) {
    modifyLock();
    try { insertString(getLength(), str, set); }
    catch (BadLocationException e) { throw new UnexpectedException(e); }  
    finally { modifyUnlock(); }
  }
  
  
  public void append(String str, String style) { append(str, style == null ? null : getDocStyle(style)); }
  
  
  public String getDefaultStyle() { return null; }
  
  public void print() {
    throw new UnsupportedOperationException("Printing not supported");
  }
  
  public Pageable getPageable() {
    throw new UnsupportedOperationException("Printing not supported");
  }
  
  
  
  
  
  

  
  public void modifyLock() { writeLock(); }
  
   
  public void modifyUnlock() { writeUnlock(); }
}

