

package edu.rice.cs.drjava.model.repl;

import java.io.*;
import java.awt.print.*;

import edu.rice.cs.drjava.model.print.DrJavaBook;

import edu.rice.cs.drjava.model.FileSaveSelector;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.text.EditDocumentInterface;
import edu.rice.cs.util.text.EditDocumentException;
import edu.rice.cs.util.text.ConsoleDocument;
import edu.rice.cs.drjava.config.OptionListener;


public class InteractionsDocument extends ConsoleDocument {

  
  public static final String DEFAULT_PROMPT = "> ";

  
  public static final String ERROR_STYLE = "error";

  
  public static final String DEBUGGER_STYLE = "debugger";

  public static final String OBJECT_RETURN_STYLE = "object.return.style";
  
  public static final String STRING_RETURN_STYLE = "string.return.style";
  
  public static final String CHARACTER_RETURN_STYLE = "character.return.style";
  
  public static final String NUMBER_RETURN_STYLE = "number.return.style";
  
   
  private final History _history;

  

  
  public InteractionsDocument(EditDocumentInterface document) { this(document, new History()); }

  
  public InteractionsDocument(EditDocumentInterface document, int maxHistorySize) {
    this(document, new History(maxHistorySize));
  }
  
  
  public InteractionsDocument(EditDocumentInterface document, History history) {
    super(document);
    _history = history;
    _hasPrompt = true;
    _prompt = DEFAULT_PROMPT;
    reset(InteractionsModel.getStartUpBanner());
  }

  
  public void setInProgress(boolean inProgress) { 
    acquireWriteLock();
    _hasPrompt = ! inProgress;
    releaseWriteLock();
  }

  
  public boolean inProgress() { return ! _hasPrompt; }

  
  public void reset(String banner) {
    acquireWriteLock();
    try {
      forceRemoveText(0, _document.getLength());
      forceInsertText(0, banner, OBJECT_RETURN_STYLE);
      insertPrompt();
      _history.moveEnd();
      setInProgress(false);
    }
    catch (EditDocumentException e) { throw new UnexpectedException(e); }
    finally { releaseWriteLock(); }
  }

  
  private void _replaceCurrentLineFromHistory() {
    try {
      _clearCurrentInputText();
      append(_history.getCurrent(), DEFAULT_STYLE);
    }
    catch (EditDocumentException ble) { throw new UnexpectedException(ble); }
  }

  
  public OptionListener<Integer> getHistoryOptionListener() { return _history.getHistoryOptionListener(); }

  
  public void addToHistory(String text) { 
    acquireWriteLock();
    try { _history.add(text); } 
    finally { releaseWriteLock(); }
  }

  
  public void saveHistory(FileSaveSelector selector) throws IOException {
    acquireReadLock();  
    try { _history.writeToFile(selector); }
    finally { releaseReadLock(); }
  }

  
  public void saveHistory(FileSaveSelector selector, String editedVersion) throws IOException {
      acquireReadLock();  
      try { _history.writeToFile(selector, editedVersion); }
      finally { releaseReadLock(); }
  }

  
  public String getHistoryAsStringWithSemicolons() {
    acquireReadLock();
    try { return _history.getHistoryAsStringWithSemicolons(); }
    finally { releaseReadLock(); }
  }

  
  public String getHistoryAsString() { 
    acquireReadLock();
    try { return _history.getHistoryAsString(); }
    finally { releaseReadLock(); }
  }

  
  public void clearHistory() { 
    acquireWriteLock();
    try { _history.clear(); }
    finally { releaseWriteLock(); }
  }
  
  public String lastEntry() { 
    acquireReadLock();
    try { return _history.lastEntry(); }  
    finally { releaseReadLock(); }
  }
  
  public void moveHistoryPrevious(String entry) {
    acquireWriteLock();
    try { 
      _history.movePrevious(entry);
      _replaceCurrentLineFromHistory();
    }
    finally { releaseWriteLock(); }
  }
  
  
  public void moveHistoryNext(String entry) {
    acquireWriteLock();
    try {
      _history.moveNext(entry);
      _replaceCurrentLineFromHistory();
    }
   finally { releaseWriteLock(); }
  }
  
  
  public boolean hasHistoryPrevious() { 
    acquireReadLock();
    try { return _history.hasPrevious(); }
    finally { releaseReadLock(); }
  }

  
  public boolean hasHistoryNext() { 
    acquireReadLock();
    try { return _history.hasNext(); }
    finally { releaseReadLock(); }
  }
  
  
  public void reverseHistorySearch(String searchString) {
    acquireWriteLock();
    try {
      _history.reverseSearch(searchString);
      _replaceCurrentLineFromHistory();
    }
    finally { releaseWriteLock(); }
  }
  
  
  public void forwardHistorySearch(String searchString) {
    acquireWriteLock();
    try {   
      _history.forwardSearch(searchString);
      _replaceCurrentLineFromHistory();
    }
    finally { releaseWriteLock(); }
  }
  
  
  public boolean recallPreviousInteractionInHistory() {
    acquireWriteLock();
    try {    
      if (hasHistoryPrevious()) {
        moveHistoryPrevious(getCurrentInteraction());
        return true;
      }
      _beep.run();
      return false;
    }
    finally { releaseWriteLock(); }
  }
  
  
  public boolean recallNextInteractionInHistory() {
    acquireWriteLock();
    try {    
      if (hasHistoryNext()) {
        moveHistoryNext(getCurrentInteraction());
        return true;
      }
      _beep.run();
      return false;
    }
    finally { releaseWriteLock(); }
  }
  

  
  public void reverseSearchInteractionsInHistory() {
    acquireWriteLock();
    try {   
      if (hasHistoryPrevious()) reverseHistorySearch(getCurrentInteraction());
      else _beep.run();
    }
    finally { releaseWriteLock(); }
  }
  
  
  public void forwardSearchInteractionsInHistory() {
    acquireWriteLock();
    try {   
      if (hasHistoryNext()) forwardHistorySearch(getCurrentInteraction());
      else _beep.run();
    }
    finally { releaseWriteLock(); }
  }
  
  
  public void appendExceptionResult(String exceptionClass, String message, String stackTrace, String styleName) {
    
    
    if (message.equals("Connection refused to host: 127.0.0.1; nested exception is: \n" +
                       "\tjava.net.ConnectException: Connection refused: connect")) return;

    if (null == message || "null".equals(message)) message = "";
    
    
    if ("koala.dynamicjava.interpreter.error.ExecutionError".equals(exceptionClass) ||
        "edu.rice.cs.drjava.model.repl.InteractionsException".equals(exceptionClass)) {
      exceptionClass = "Error";
    }
    
    
    
    
    
    String c = exceptionClass;
    if (c.indexOf('.') != -1) c = c.substring(c.lastIndexOf('.') + 1, c.length());
    
    acquireWriteLock();
    try {
      append(c + ": " + message + "\n", styleName);
      
      
      
      
      
      
      if (! stackTrace.trim().equals("")) {
        BufferedReader reader = new BufferedReader(new StringReader(stackTrace));
        
        String line;
        
        
        while ((line = reader.readLine()) != null) {
          String fileName;
          int lineNumber;
          
          
          int openLoc = line.indexOf('(');
          if (openLoc != -1) {
            int closeLoc = line.indexOf(')', openLoc + 1);
            
            if (closeLoc != -1) {
              int colonLoc = line.indexOf(':', openLoc + 1);
              if ((colonLoc > openLoc) && (colonLoc < closeLoc)) {
                
                String lineNumStr = line.substring(colonLoc + 1, closeLoc);
                try {
                  lineNumber = Integer.parseInt(lineNumStr);
                  fileName = line.substring(openLoc + 1, colonLoc);
                }
                catch (NumberFormatException nfe) {
                  
                }
              }
            }
          }
          
          append(line, styleName);
          
          
          append("\n", styleName);
          
        } 
      }
    }
    catch (IOException ioe) { throw new UnexpectedException(ioe); }
    catch (EditDocumentException ble) { throw new UnexpectedException(ble); }
    finally { releaseWriteLock(); }
  }  

  public void appendSyntaxErrorResult(String message, String interaction, int startRow, int startCol,
                                      int endRow, int endCol, String styleName) {
    try {
      if (null == message || "null".equals(message))  message = "";
      
      if (message.indexOf("Lexical error") != -1) {
        int i = message.lastIndexOf(':');
        if (i != -1) message = "Syntax Error:" + message.substring(i+2, message.length());                                
      }
      
      if (message.indexOf("Error") == -1) message = "Error: " + message;
      
      append(message + "\n" , styleName);
    }
    catch (EditDocumentException ble) { throw new UnexpectedException(ble); }
  }

  
  public void clearCurrentInteraction() {
    acquireWriteLock();
    try {
      super.clearCurrentInput();
      _history.moveEnd();
    }
    finally { releaseWriteLock(); }
  }  

  
  public String getCurrentInteraction() { return getCurrentInput(); }
  
  public String getDefaultStyle() { return InteractionsDocument.DEFAULT_STYLE; }
  
  
  public void preparePrintJob() {
    _book = new DrJavaBook(getDocText(0, getLength()), "Interactions", new PageFormat());
  }
  
  
  protected History getHistory() { return _history; }
}
