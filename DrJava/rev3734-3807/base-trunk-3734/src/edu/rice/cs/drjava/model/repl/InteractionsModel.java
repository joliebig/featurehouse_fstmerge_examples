

package edu.rice.cs.drjava.model.repl;

import java.io.*;
import java.net.ServerSocket;
import java.util.List;
import java.util.ArrayList;
import java.net.URL;

import edu.rice.cs.drjava.CodeStatus;
import edu.rice.cs.util.FileOpenSelector;
import edu.rice.cs.util.OperationCanceledException;
import edu.rice.cs.util.*;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.util.text.EditDocumentInterface;
import edu.rice.cs.util.text.ConsoleDocument;
import edu.rice.cs.util.text.EditDocumentException;


public abstract class InteractionsModel implements InteractionsModelCallback {
  
  
  public static final String BANNER_PREFIX = "Welcome to DrJava.";
  
  
  protected final InteractionsEventNotifier _notifier = new InteractionsEventNotifier();

  
  protected static final String _newLine = System.getProperty("line.separator");

  
  protected final InteractionsDocument _document;

  
  protected volatile boolean _waitingForFirstInterpreter;

  
  protected volatile File _workingDirectory;

  
  private final Object _interpreterLock;

  
  private final Object _writerLock;

  
  private final int _writeDelay;

  
  private volatile int _debugPort;

  
  private volatile boolean _debugPortSet;
  
  
  private volatile String _toAddToHistory = "";

  
  protected volatile InputListener _inputListener;

  protected final EditDocumentInterface _adapter;
  
  
  private volatile String _banner;
  
  
  public InteractionsModel(EditDocumentInterface adapter, File wd, int historySize, int writeDelay) {
    _writeDelay = writeDelay;
    _document = new InteractionsDocument(adapter, historySize);
    _adapter = adapter;
    _waitingForFirstInterpreter = true;
    _workingDirectory = wd;
    _interpreterLock = new Object();
    _writerLock = new Object();
    _debugPort = -1;
    _debugPortSet = false;
    _inputListener = NoInputListener.ONLY;
  }

  
  public void addListener(InteractionsListener listener) { _notifier.addListener(listener); }

  
  public void removeListener(InteractionsListener listener) { _notifier.removeListener(listener); }

  
  public void removeAllInteractionListeners() { _notifier.removeAllListeners(); }

  
  public InteractionsDocument getDocument() { return _document; }

  public void interactionContinues() {
    _document.setInProgress(false);
    _notifyInteractionEnded();
    _notifyInteractionIncomplete();
  }
  
  
  public void setWaitingForFirstInterpreter(boolean waiting) { _waitingForFirstInterpreter = waiting; }

  
  public void interpretCurrentInteraction() {
    synchronized(_interpreterLock) {
      
      if (_document.inProgress()) return;

      String text = _document.getCurrentInteraction();
      String toEval = text.trim();
      if (toEval.startsWith("java ")) toEval = _testClassCall(toEval);

      _prepareToInterpret(text);
      interpret(toEval);
    }
  }

  
  private void _prepareToInterpret(String text) {
    addNewLine();
    _notifyInteractionStarted();
    _document.setInProgress(true);
    _toAddToHistory = text; 
    
  }
  
  public void addNewLine() { append(_newLine, InteractionsDocument.DEFAULT_STYLE); }

  
  public final void interpret(String toEval) {
    synchronized(_interpreterLock) { _interpret(toEval); }
  }

  
  protected abstract void _interpret(String toEval);

  
  protected abstract void _notifyInteractionIncomplete();

  
  protected abstract void _notifyInteractionStarted();

  
  public abstract String getVariableToString(String var);

  
  public abstract String getVariableClassName(String var);

  
  public final void resetInterpreter(File wd) {
    _workingDirectory = wd;
    _resetInterpreter(wd);
  }

  
  protected abstract void _resetInterpreter(File wd);
  
  
  public File getWorkingDirectory() { return _workingDirectory; }

  
  public abstract void addProjectClassPath(URL path);
  public abstract void addBuildDirectoryClassPath(URL path);
  public abstract void addProjectFilesClassPath(URL path);
  public abstract void addExternalFilesClassPath(URL path);
  public abstract void addExtraClassPath(URL path);
 
  
  protected abstract void _notifySyntaxErrorOccurred(int offset, int length);

  
  protected ArrayList<String> _getHistoryText(FileOpenSelector selector)
    throws IOException, OperationCanceledException {
    File[] files = selector.getFiles();
    if (files == null) throw new IOException("No Files returned from FileSelector");
    
    ArrayList<String> histories = new ArrayList<String>();
    ArrayList<String> strings = new ArrayList<String>();
    
    for (File f: files) {
      if (f == null) throw new IOException("File name returned from FileSelector is null");
      try {
        FileInputStream fis = new FileInputStream(f);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        while (true) {
          String line = br.readLine();
          if (line == null) break;
          strings.add(line);
        }
        br.close(); 
      }
      catch (IOException ioe) { throw new IOException("File name returned from FileSelector is null"); }
    
      
      StringBuffer text = new StringBuffer();
      boolean firstLine = true;
      int formatVersion = 1;
      for (String s: strings) {
        int sl = s.length();
        if (sl > 0) {
          
          
          if (firstLine && (s.trim().equals(History.HISTORY_FORMAT_VERSION_2.trim()))) formatVersion = 2;
          
          switch (formatVersion) {
            case (1):
              
              
              text.append(s);
              if (s.charAt(sl - 1) != ';') text.append(';');
              text.append(_newLine);
              break;
            case (2):
              if (!firstLine) text.append(s).append(_newLine); 
              break;
          }
          firstLine = false;
        }
      }
      
      
      histories.add(text.toString());
    }
    return histories;
  }

  
  protected ArrayList<String> _removeSeparators(String text) {
    String sep = History.INTERACTION_SEPARATOR;
    int len = sep.length();
    ArrayList<String> interactions = new ArrayList<String>();

    
    
    int index = text.indexOf(sep);
    int lastIndex = 0;
    while (index != -1) {
      interactions.add(text.substring(lastIndex, index).trim());
      lastIndex = index + len;
      index = text.indexOf(sep, lastIndex);
    }

    
    String last = text.substring(lastIndex, text.length()).trim();
    if (!"".equals(last)) interactions.add(last);
    return interactions;
  }

  
  public void loadHistory(FileOpenSelector selector) throws IOException {
    ArrayList<String> histories;
    try { histories = _getHistoryText(selector); }
    catch (OperationCanceledException oce) { return; }
    _document.clearCurrentInteraction();

    
    StringBuffer buf = new StringBuffer();
    for (String hist: histories) {
      ArrayList<String> interactions = _removeSeparators(hist);
      for (String curr: interactions) {
        int len = curr.length();
        buf.append(curr);
        if (len > 0 && curr.charAt(len - 1) != ';')  buf.append(';');
        buf.append(_newLine);
      }
    }
    append(buf.toString().trim(), InteractionsDocument.DEFAULT_STYLE);
    interpretCurrentInteraction();
  }

  
  public InteractionsScriptModel loadHistoryAsScript(FileOpenSelector selector)
    throws IOException, OperationCanceledException {
    ArrayList<String> histories = _getHistoryText(selector);
    ArrayList<String> interactions = new ArrayList<String>();
    for (String hist: histories) interactions.addAll(_removeSeparators(hist));
    return new InteractionsScriptModel(this, interactions);
  }

  
  public int getDebugPort() throws IOException {
    if (!_debugPortSet) _createNewDebugPort();
    return _debugPort;
  }

  
  protected void _createNewDebugPort() throws IOException {

    try {
      ServerSocket socket = new ServerSocket(0);
      _debugPort = socket.getLocalPort();
      socket.close();
    }
    catch (java.net.SocketException se) {
      
      _debugPort = -1;
    }
    _debugPortSet = true;
    if (CodeStatus.DEVELOPMENT) {
      System.setProperty("drjava.debug.port", String.valueOf(_debugPort));
    }
  }

  
  public void setDebugPort(int port) {
    _debugPort = port;
    _debugPortSet = true;
  }

  
  public void replSystemOutPrint(String s) {
    _document.insertBeforeLastPrompt(s, InteractionsDocument.SYSTEM_OUT_STYLE);
  }

  
  public void replSystemErrPrint(String s) {
    _document.insertBeforeLastPrompt(s, InteractionsDocument.SYSTEM_ERR_STYLE);
  }

  
  public String getConsoleInput() { return _inputListener.getConsoleInput(); }

  
  public void setInputListener(InputListener listener) {
    if (_inputListener == NoInputListener.ONLY) _inputListener = listener;
    else  throw new IllegalStateException("Cannot change the input listener until it is released.");
  }

  
  public void changeInputListener(InputListener oldListener, InputListener newListener) {
    
    synchronized(NoInputListener.ONLY) {
      if (_inputListener == oldListener) _inputListener = newListener;
      else
        throw new IllegalArgumentException("The given old listener is not installed!");      
    }
  }

  
  protected void _interactionIsOver() {
    _document.addToHistory(_toAddToHistory);
    _document.setInProgress(false);
    _document.insertPrompt();
    _notifyInteractionEnded();
  }

  
  protected abstract void _notifyInteractionEnded();

  
  public void append(String s, String styleName) {
    synchronized(_writerLock) {
      try {
        _document.append(s, styleName);
        
        
        _writerLock.wait(_writeDelay);
      }
      catch (EditDocumentException e) { throw new UnexpectedException(e); }
      catch (InterruptedException e) {
        
      }
    }
  }

  
  public void replReturnedVoid() {
    _interactionIsOver();
  }

  
  public void replReturnedResult(String result, String style) {
    append(result + _newLine, style);
    _interactionIsOver();
  }

  
  public void replThrewException(String exceptionClass, String message, String stackTrace, String shortMessage) {
    if (shortMessage!=null) {
      if (shortMessage.endsWith("<EOF>\"")) {
        interactionContinues();
        return;
      }
    }
    _document.appendExceptionResult(exceptionClass, message, stackTrace, InteractionsDocument.ERROR_STYLE);
    _interactionIsOver();
  }

  
  public void replReturnedSyntaxError(String errorMessage, String interaction, int startRow, int startCol,
                                      int endRow, int endCol ) {
    if (errorMessage!=null) {
      if (errorMessage.endsWith("<EOF>\"")) {
        interactionContinues();
        return;
      }
    }
    
    edu.rice.cs.util.Pair<Integer,Integer> oAndL =
      StringOps.getOffsetAndLength(interaction, startRow, startCol, endRow, endCol);

    _notifySyntaxErrorOccurred(_document.getPromptPos() + oAndL.getFirst().intValue(),oAndL.getSecond().intValue());

    _document.appendSyntaxErrorResult(errorMessage, interaction, startRow, startCol, endRow, endCol,
                                      InteractionsDocument.ERROR_STYLE);

    _interactionIsOver();
  }

  
  public void replCalledSystemExit(int status) { 

    _notifyInterpreterExited(status); 
  }

  
  protected abstract void _notifyInterpreterExited(int status);

  
  public void interpreterResetting() {


    if (! _waitingForFirstInterpreter) {
      _document.acquireWriteLock();
      try {
        _document.insertBeforeLastPrompt("Resetting Interactions..." + _newLine, InteractionsDocument.ERROR_STYLE);
        _document.setInProgress(true);
      }
      finally { _document.releaseWriteLock(); }


      
      try { _createNewDebugPort(); }
      catch (IOException ioe) {
        
      }
      _notifyInterpreterResetting();

    }
  }

  
  protected abstract void _notifyInterpreterResetting();

  
  public void interpreterResetFailed(Throwable t) {
    _interpreterResetFailed(t);
    _document.setInProgress(false);
    _notifyInterpreterResetFailed(t);
  }

  
  protected abstract void _interpreterResetFailed(Throwable t);

  
  protected abstract void _notifyInterpreterResetFailed(Throwable t);
  
  public synchronized String getBanner() { return _banner; }
  
  public static String getStartUpBanner() { return getBanner(new File(System.getProperty("user.dir"))); }
  
  public static String getBanner(File wd) { return BANNER_PREFIX + "  Working directory is " + wd + '\n'; }

  private synchronized String generateBanner(File wd) {
    _banner = getBanner(wd);
    return _banner;
  }

  
  public void interpreterReady(File wd) {


    if (! _waitingForFirstInterpreter) {
      _document.reset(generateBanner(wd));
      _document.setInProgress(false);
      _notifyInterpreterReady(wd);
    }
    _waitingForFirstInterpreter = false;
  }
  
  
  public abstract void _notifyInterpreterReady(File wd);
  
    
  public void slaveJVMUsed() { _notifySlaveJVMUsed(); }
  
  
  protected abstract void _notifySlaveJVMUsed();

  
  protected static String _testClassCall(String s) {
    if (s.endsWith(";"))  s = _deleteSemiColon(s);
    List<String> args = ArgumentTokenizer.tokenize(s, true);
    boolean seenArg = false;
    String className = args.get(1);
    StringBuffer mainCall = new StringBuffer();
    mainCall.append(className.substring(1, className.length() - 1));
    mainCall.append(".main(new String[]{");
    for (int i = 2; i < args.size(); i++) {
      if (seenArg) mainCall.append(",");
      else seenArg = true;
      mainCall.append(args.get(i));
    }
    mainCall.append("});");
    return mainCall.toString();
  }

  
  protected static String _deleteSemiColon(String s) { return  s.substring(0, s.length() - 1); }

  
  private static class NoInputListener implements InputListener {
    public static final NoInputListener ONLY = new NoInputListener();
    private NoInputListener() { }

    public String getConsoleInput() { throw new IllegalStateException("No input listener installed!"); }
  }
  
  
  public abstract ConsoleDocument getConsoleDocument();
}
