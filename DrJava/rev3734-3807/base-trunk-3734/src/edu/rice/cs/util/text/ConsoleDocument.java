

package edu.rice.cs.util.text;

import java.awt.print.*;

import edu.rice.cs.drjava.model.print.DrJavaBook;

import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.text.EditDocumentInterface;
import edu.rice.cs.util.text.DocumentEditCondition;
import edu.rice.cs.util.text.EditDocumentException;


public class ConsoleDocument implements EditDocumentInterface {
  
  
  public static final String DEFAULT_STYLE = "default";

  
  public static final String SYSTEM_OUT_STYLE = "System.out";

  
  public static final String SYSTEM_ERR_STYLE = "System.err";

  
  public static final String SYSTEM_IN_STYLE = "System.in";

  
  public static final String DEFAULT_CONSOLE_PROMPT = "";

  
  protected final EditDocumentInterface _document;

  
  protected volatile Runnable _beep;

  
  protected volatile int _promptPos;

  
  protected volatile String _prompt;

  
  protected volatile boolean _hasPrompt;
  
  
  protected DrJavaBook _book;

  
  public ConsoleDocument(EditDocumentInterface adapter) {
    _document = adapter;
    
    _beep = new Runnable() { public void run() { } };
    _promptPos = 0;
    _prompt = DEFAULT_CONSOLE_PROMPT;
    _hasPrompt = false;
   
    
    _document.setEditCondition(new ConsoleEditCondition());
  }

  
  public boolean hasPrompt() { return _hasPrompt; }

  
  public String getPrompt() { return _prompt; }

  
  public void setPrompt(String prompt) { 
    acquireWriteLock();
    _prompt = prompt;
    releaseWriteLock();
  }

  
  public DocumentEditCondition getEditCondition() { return _document.getEditCondition(); }

  
  public void setEditCondition(DocumentEditCondition condition) { _document.setEditCondition(condition); }

  
  public int getPromptPos() { return _promptPos; }

  
  public void setPromptPos(int newPos) { 
    acquireReadLock();
    _promptPos = newPos; 
    releaseReadLock();
  }

  
  public void setBeep(Runnable beep) { 
    acquireReadLock();
    _beep = beep; 
    releaseReadLock();
  }

  
  public void reset(String banner) {
    acquireWriteLock();
    try {
      forceRemoveText(0, _document.getLength());
      forceInsertText(0, banner, DEFAULT_STYLE);
      _promptPos = 0;
    }
    catch (EditDocumentException e) { throw new UnexpectedException(e); }
    finally { releaseWriteLock(); }
  }

  
  public void insertPrompt() {
    acquireWriteLock();
    try {



      forceInsertText(_document.getLength(), _prompt, DEFAULT_STYLE);
      _promptPos = _document.getLength();
      _hasPrompt = true;
       
    }
    catch (EditDocumentException e) { throw new UnexpectedException(e);  }
    finally { releaseWriteLock(); }
  }

  
  public void disablePrompt() {
    acquireWriteLock();
    try {
    _hasPrompt = false;
    _promptPos = _document.getLength();
    }
    finally { releaseWriteLock(); }
  }

  
  public void insertNewLine(int pos) {
    
    acquireWriteLock();
    try {
      int len = _document.getLength();
      if (pos > len)  pos = len;
      else if (pos < 0) pos = 0;
      
      String newLine = System.getProperty("line.separator");
      insertText(pos, newLine, DEFAULT_STYLE);
    }
    catch (EditDocumentException e) { throw new UnexpectedException(e); }
    finally { releaseWriteLock(); }
  }

  
  public int getPositionBeforePrompt() {
    acquireReadLock();
    try {
      if (_hasPrompt) return _promptPos - _prompt.length();
      return _document.getLength();
    }
    finally { releaseReadLock(); }
  }

  
  public void insertBeforeLastPrompt(String text, String style) {
    acquireWriteLock();
    try {
      int pos = getPositionBeforePrompt();
      _promptPos += text.length();
      _addToStyleLists(pos, text, style);
      _document.forceInsertText(pos, text, style);
    }
    catch (EditDocumentException ble) { throw new UnexpectedException(ble); }
    finally { releaseWriteLock(); }
  }

  
  public void insertText(int offs, String str, String style) throws EditDocumentException {
    acquireWriteLock();
    try {
      if (offs < _promptPos) _beep.run();
      else {
        _addToStyleLists(offs, str, style);
        _document.insertText(offs, str, style);
      }
    }
    finally { releaseWriteLock(); }
  }
  
  
  public void append(String str, String style) throws EditDocumentException {
    acquireWriteLock();
    try {
      int offs = _document.getLength();
      _addToStyleLists(offs, str, style);
      _document.insertText(offs, str, style);
    }
    finally { releaseWriteLock(); }
  }
  
  
  public void forceInsertText(int offs, String str, String style) throws EditDocumentException {
    acquireWriteLock();
    try {
      _addToStyleLists(offs, str, style);
      _document.forceInsertText(offs, str, style);
    }
    finally { releaseWriteLock(); }
  }
  
  private void _addToStyleLists(int offs, String str, String style) {
    if (_document instanceof SwingDocument) 
      ((SwingDocument)_document).addColoring(offs, offs + str.length(), style);
  }

  
  public void removeText(int offs, int len) throws EditDocumentException {
    acquireWriteLock();
    try {
      if (offs < _promptPos) _beep.run();
      else _document.removeText(offs, len);
    }
    finally { releaseWriteLock(); }
  }

  
  public void forceRemoveText(int offs, int len) throws EditDocumentException {
    _document.forceRemoveText(offs, len);
  }

  
  public int getLength() { return _document.getLength(); }

  
  public String getDocText(int offs, int len) throws EditDocumentException {
    return _document.getDocText(offs, len);
  }

  
  public String getText() {
    acquireWriteLock();
    try {
      return _document.getDocText(0, getLength());
    }
    finally { releaseWriteLock(); }
  }
  
  
  public String getCurrentInput() {
    acquireReadLock();
    try {
      try { return getDocText(_promptPos, _document.getLength() - _promptPos); }
      catch (EditDocumentException e) { throw new UnexpectedException(e); }
    }
    finally { releaseReadLock(); }
  }

  
  public void clearCurrentInput() {  _clearCurrentInputText(); }

  
  protected void _clearCurrentInputText() {
    acquireWriteLock();
    try {
      
      removeText(_promptPos, _document.getLength() - _promptPos);
    }
    catch (EditDocumentException ble) { throw new UnexpectedException(ble); }
    finally { releaseWriteLock(); }
  }
  
  
  public String getDefaultStyle() { return ConsoleDocument.DEFAULT_STYLE; }

  
  public Pageable getPageable() throws IllegalStateException { return _book; }
  
  
  public void preparePrintJob() {
    _book = new DrJavaBook(getDocText(0, getLength()), "Console", new PageFormat());
  }
  
  
  public void print() throws PrinterException {
    preparePrintJob();
    PrinterJob printJob = PrinterJob.getPrinterJob();
    printJob.setPageable(_book);
    if (printJob.printDialog()) printJob.print();
    cleanUpPrintJob();
  }
  
  
  public void cleanUpPrintJob() { _book = null; }
  
  
  class ConsoleEditCondition extends DocumentEditCondition {
    public boolean canInsertText(int offs) { return canRemoveText(offs); }
    
    public boolean canRemoveText(int offs) {
      if (offs < _promptPos) {
        _beep.run();
        return false;
      }
      return true;
    }
  }
  
  
  
  
  public void acquireReadLock() { _document.acquireReadLock(); }
  
  
  public void releaseReadLock() { _document.releaseReadLock(); }
  
  
  public void acquireWriteLock() { _document.acquireWriteLock(); }
  
  
  public void releaseWriteLock() { _document.releaseWriteLock(); }
}
