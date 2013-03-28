

package edu.rice.cs.drjava.model.repl;

import java.io.*;
import java.awt.print.*;

import edu.rice.cs.drjava.model.print.DrJavaBook;

import edu.rice.cs.drjava.model.FileSaveSelector;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.OperationCanceledException;
import edu.rice.cs.util.text.ConsoleDocumentInterface;
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
  
  
  
  
  public InteractionsDocument(ConsoleDocumentInterface document) { 
    this(document, new History()); 
  }
  
  
  public InteractionsDocument(ConsoleDocumentInterface document, int maxHistorySize) {
    this(document, new History(maxHistorySize));
  }
  
  
  public InteractionsDocument(ConsoleDocumentInterface document, History history) {
    super(document);  
    _history = history;
    _document.setHasPrompt(true);
    _prompt = DEFAULT_PROMPT;
  }
  
  
  public void setInProgress(boolean inProgress) { _document.setHasPrompt(! inProgress); }
  
  
  public boolean inProgress() { return ! _document.hasPrompt(); }
  
  
  public void setBanner(String banner) {
    try {
      setPromptPos(0);
      insertText(0, banner, OBJECT_RETURN_STYLE);
      insertPrompt();
      _history.moveEnd();
    }
    catch (EditDocumentException e) { throw new UnexpectedException(e); }
  }
    
  
  public void reset(String banner) {
    try {

      
      setHasPrompt(false);
      setPromptPos(0);
      removeText(0, _document.getLength());
      insertText(0, banner, OBJECT_RETURN_STYLE);

      insertPrompt();
      _history.moveEnd();
      setInProgress(false);  
    }
    catch (EditDocumentException e) { throw new UnexpectedException(e); }
  }
  
  
  private void _replaceCurrentLineFromHistory() {
    try {
      _clearCurrentInputText();
      append(_history.getCurrent(), DEFAULT_STYLE);
    }
    catch (EditDocumentException ble) { throw new UnexpectedException(ble); }
  }
  
  
  public OptionListener<Integer> getHistoryOptionListener() { return _history.getHistoryOptionListener(); }
  
  
  public void addToHistory(String text) { _history.add(text); }
  
  
  public String removeLastFromHistory() { return _history.removeLast(); }
  
  
  public void saveHistory(FileSaveSelector selector) throws IOException { _history.writeToFile(selector); }
  
  
  public void saveHistory(FileSaveSelector selector, String editedVersion) throws IOException {
    History.writeToFile(selector, editedVersion); 
  }
  
  
  public String getHistoryAsStringWithSemicolons() {
    return _history.getHistoryAsStringWithSemicolons(); 
  }
  
  
  public String getHistoryAsString() { 
    return _history.getHistoryAsString(); 
  }
  
  
  public void clearHistory() { _history.clear(); }
  
  public String lastEntry() { return _history.lastEntry(); }
  
  public void moveHistoryPrevious(String entry) {
    _history.movePrevious(entry);
    _replaceCurrentLineFromHistory();
  }
  
  
  public void moveHistoryNext(String entry) {
    _history.moveNext(entry);
    _replaceCurrentLineFromHistory();
  }
  
  
  private boolean hasHistoryPrevious() { return _history.hasPrevious(); }
  
  
  public boolean hasHistoryNext() { return _history.hasNext(); }
  
  
  public void reverseHistorySearch(String searchString) {
    _history.reverseSearch(searchString);
    _replaceCurrentLineFromHistory();
  }
  
  
  public void forwardHistorySearch(String searchString) {
    _history.forwardSearch(searchString);
    _replaceCurrentLineFromHistory();
  }
  
  
  public boolean recallPreviousInteractionInHistory() {   
    if (hasHistoryPrevious()) {
      moveHistoryPrevious(getCurrentInteraction());
      return true;
    }
    _beep.run();
    return false;
  }
  
  
  public boolean recallNextInteractionInHistory() {
    if (hasHistoryNext()) {
      moveHistoryNext(getCurrentInteraction());
      return true;
    }
    _beep.run();
    return false;
  }
  
  
  
  public void reverseSearchInteractionsInHistory() {
    if (hasHistoryPrevious()) reverseHistorySearch(getCurrentInteraction());
    else _beep.run();
  }
  
  
  public void forwardSearchInteractionsInHistory() {
    if (hasHistoryNext()) forwardHistorySearch(getCurrentInteraction());
    else _beep.run();
  }
  
  
  public void appendExceptionResult(String message, String styleName) {
    
    
    
    
    


    try { append(message + "\n", styleName); }
    catch (EditDocumentException ble) { throw new UnexpectedException(ble); }
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
    super.clearCurrentInput();
    _history.moveEnd();
  }  
  
  
  public String getCurrentInteraction() { return getCurrentInput(); }
  
  public String getDefaultStyle() { return InteractionsDocument.DEFAULT_STYLE; }
  
  
  public void preparePrintJob() {
    _book = new DrJavaBook(getDocText(0, getLength()), "Interactions", new PageFormat());
  }
  
  
  protected History getHistory() { return _history; }
}
