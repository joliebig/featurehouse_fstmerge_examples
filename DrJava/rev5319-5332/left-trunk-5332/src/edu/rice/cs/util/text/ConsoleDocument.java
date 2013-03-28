

package edu.rice.cs.util.text;

import java.io.*;
import java.awt.EventQueue;
import java.awt.print.*;
import java.awt.EventQueue;

import edu.rice.cs.drjava.model.print.DrJavaBook;

import edu.rice.cs.drjava.model.FileSaveSelector;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.OperationCanceledException;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.util.text.ConsoleDocumentInterface;
import edu.rice.cs.util.text.DocumentEditCondition;
import edu.rice.cs.util.text.EditDocumentException;
import edu.rice.cs.util.FileOps;


public class ConsoleDocument implements ConsoleDocumentInterface {
  
  
  public static final String DEFAULT_CONSOLE_PROMPT = "";
  
  
  public static final String DEFAULT_STYLE = "default";
  
  
  public static final String SYSTEM_OUT_STYLE = "System.out";
  
  
  public static final String SYSTEM_ERR_STYLE = "System.err";
  
  
  public static final String SYSTEM_IN_STYLE = "System.in";
  
  
  protected final ConsoleDocumentInterface _document;
  
  
  protected volatile Runnable _beep;
  
  
  private volatile int _promptPos;
  
  
  protected volatile String _prompt;
  
  
  protected volatile DrJavaBook _book;
  
  
  public ConsoleDocument(ConsoleDocumentInterface doc) {
    _document = doc;
    
    _beep = new Runnable() { public void run() { } };
    _prompt = DEFAULT_CONSOLE_PROMPT;
    _promptPos = DEFAULT_CONSOLE_PROMPT.length();
    _document.setHasPrompt(false);
    _document.setEditCondition(new ConsoleEditCondition()); 
  }
  
  
  public boolean hasPrompt() { return _document.hasPrompt(); }
  
  public void setHasPrompt(boolean val) { _document.setHasPrompt(val); }
  
  
  public String getPrompt() { return _prompt; }
  
  
  public void setPrompt(String prompt) { _prompt = prompt; }
  
  
  public int getPromptLength() { return _prompt.length(); }
  
  
  public DocumentEditCondition getEditCondition() { return _document.getEditCondition(); }
  
  
  public void setEditCondition(DocumentEditCondition condition) { _document.setEditCondition(condition); }
  
  
  public int getPromptPos() { return _promptPos; }
  
  
  public void setPromptPos(int newPos) { 
    _promptPos = newPos; 
  }
  
  
  public void setBeep(Runnable beep) { _beep = beep; }
  
  
  public void reset(String banner) {
    assert EventQueue.isDispatchThread();
    try {
      forceRemoveText(0, _document.getLength());
      forceInsertText(0, banner, DEFAULT_STYLE);
      _promptPos = banner.length();
    }
    catch (EditDocumentException e) { throw new UnexpectedException(e); }
  }
  
  
  public void insertPrompt() {
    try {
      int len = _document.getLength();
      
      _promptPos = len + _prompt.length();
      forceInsertText(len, _prompt, DEFAULT_STYLE); 
      _document.setHasPrompt(true);
    }
    catch (EditDocumentException e) { throw new UnexpectedException(e);  }
  }
  
  
  public void disablePrompt() {
    _document.setHasPrompt(false);
    _promptPos = _document.getLength();
  }
  
  
  public void insertNewline(int pos) {
    
    try {
      int len = _document.getLength();
      if (pos > len)  pos = len;
      else if (pos < 0) pos = 0;
      
      String newLine = "\n";  
      insertText(pos, newLine, DEFAULT_STYLE);
    }
    catch (EditDocumentException e) { throw new UnexpectedException(e); }
  }
  
  
  private int _getPositionBeforePrompt() {
    int len = _document.getLength();
    if (_document.hasPrompt()) {
      int promptStart = _promptPos - _prompt.length();
      return (promptStart < len && promptStart >= 0) ? promptStart : len;  
    }
    return len;
  }
  
  
  public void insertBeforeLastPrompt(String text, String style) {
 assert Utilities.TEST_MODE || EventQueue.isDispatchThread();
    try {
      int pos = _getPositionBeforePrompt();

      _promptPos = _promptPos + text.length();
      forceInsertText(pos, text, style);
    }
    catch (EditDocumentException ble) { throw new UnexpectedException(ble); }
  }
  
  
  public void insertText(int offs, String str, String style) throws EditDocumentException {
    if (offs < _promptPos) _beep.run();
    else {
      _addToStyleLists(offs, str, style);
      _document.insertText(offs, str, style);
    }
  }   
  
  
  public void append(String str, String style) throws EditDocumentException {
 assert Utilities.TEST_MODE || EventQueue.isDispatchThread();
    int offs = _document.getLength();
    _addToStyleLists(offs, str, style);
    _document.insertText(offs, str, style);
  }
  
  
  public void forceInsertText(int offs, String str, String style) throws EditDocumentException {      
    _addToStyleLists(offs, str, style);

    _document.forceInsertText(offs, str, style);
  }
  
  
  private void _addToStyleLists(int offs, String str, String style) {
    if (_document instanceof SwingDocument)
      ((SwingDocument)_document).addColoring(offs, offs + str.length(), style);
  }
  
  
  public void removeText(int offs, int len) throws EditDocumentException {
    if (offs < _promptPos) _beep.run();
    else _document.removeText(offs, len);
  }
  
  
  public void forceRemoveText(int offs, int len) throws EditDocumentException {
    _document.forceRemoveText(offs, len);
  }
  
  
  public int getLength() { return _document.getLength(); }
  
  
  public String getDocText(int offs, int len) throws EditDocumentException {
    return _document.getDocText(offs, len);
  }
  
  
  public String getText() { return _document.getDocText(0, getLength()); }
  
  
  public String getCurrentInput() {
      try { return getDocText(_promptPos, _document.getLength() - _promptPos); }
      catch (EditDocumentException e) { throw new UnexpectedException(e); }
  }
  
  
  public void clearCurrentInput() {  _clearCurrentInputText(); }
  
  
  protected void _clearCurrentInputText() {
    try {
      
      removeText(_promptPos, _document.getLength() - _promptPos);
    }
    catch (EditDocumentException ble) { throw new UnexpectedException(ble); }
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
  
  
  public void saveCopy(FileSaveSelector selector) throws IOException {
    assert EventQueue.isDispatchThread();
    try {
      final File file = selector.getFile().getCanonicalFile();
      if (! file.exists() || selector.verifyOverwrite(file)) {  
        FileOps.saveFile(new FileOps.DefaultFileSaver(file) {
          
          public void saveTo(OutputStream os) throws IOException {
            final String text = getDocText(0, getLength());
            OutputStreamWriter osw = new OutputStreamWriter(os);
            osw.write(text,0,text.length());
            osw.flush();
          }
        });
      }
    }
    catch (OperationCanceledException oce) {
      
      
      return;
    }
  }
}
