

package edu.rice.cs.drjava.model.repl;

import java.io.*;
import java.net.ServerSocket;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.text.BadLocationException;
import java.awt.EventQueue;

import edu.rice.cs.drjava.ui.DrJavaErrorHandler;
import edu.rice.cs.drjava.ui.InteractionsPane;
import edu.rice.cs.util.FileOpenSelector;
import edu.rice.cs.util.OperationCanceledException;
import edu.rice.cs.util.StringOps;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.*;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.util.text.ConsoleDocumentInterface;
import edu.rice.cs.util.text.ConsoleDocument;
import edu.rice.cs.util.text.EditDocumentException;
import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.OptionConstants;

import static edu.rice.cs.plt.debug.DebugUtil.debug;


public abstract class InteractionsModel implements InteractionsModelCallback {
  
  
  public static final String BANNER_PREFIX = "Welcome to DrJava.";
  

  
  
  protected final InteractionsEventNotifier _notifier = new InteractionsEventNotifier();
  
  
  protected volatile InteractionsDocument _document;
  
  
  protected volatile boolean _waitingForFirstInterpreter;
  
  
  protected volatile File _workingDirectory;
  
  
  public final Object _writerLock;
  
  
  private final int _writeDelay;
  
  
  private volatile int _debugPort;
  
  
  private volatile boolean _debugPortSet;
  
  
  private volatile String _toAddToHistory = "";
  
  
  protected volatile InputListener _inputListener;
  
  
  protected final ConsoleDocumentInterface _cDoc;
  
  
  public volatile InteractionsPane _pane;  
  
  
  private volatile String _banner;
  
  
  protected volatile String _lastError = null;
  protected volatile String _secondToLastError = null;
  
  
  protected final HashSet<String> _autoImportSet = new HashSet<String>();
  
  
  public InteractionsModel(ConsoleDocumentInterface cDoc, final File wd, int historySize, int writeDelay) {
    _document = new InteractionsDocument(cDoc, historySize);
    _cDoc = cDoc;
    _writeDelay = writeDelay;
    _waitingForFirstInterpreter = true;
    _workingDirectory = wd;
    _writerLock = new Object();
    _debugPort = -1;
    _debugPortSet = false;
    _inputListener = NoInputListener.ONLY;
    Utilities.invokeLater(new Runnable() {
      public void run() { _document.setBanner(generateBanner(wd));}
    });
  }
  
  
  public void setUpPane(InteractionsPane pane) { 
    _pane = pane;
    _pane.setCaretPosition(_document.getLength());

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

    Utilities.invokeLater(new Runnable() {
      public void run() {
        
        if (_document.inProgress()) return;  
        
        String text = _document.getCurrentInteraction();
        String toEval = text.trim();
        _prepareToInterpret(toEval);  
        if (toEval.startsWith("java ")) toEval = _transformJavaCommand(toEval);
        else if (toEval.startsWith("applet ")) toEval = _transformAppletCommand(toEval);
        else if (DrJava.getConfig().getSetting(OptionConstants.DEBUG_AUTO_IMPORT).booleanValue() &&
            toEval.startsWith("import ")) {
          
          
          
          
          
          String line = toEval;
          do {
            line = line.substring("import ".length());
            String substr = line;
            int endPos = 0;
            while((endPos<substr.length()) &&
                  ((Character.isJavaIdentifierPart(substr.charAt(endPos))) ||
                   (substr.charAt(endPos) == '.') ||
                   (substr.charAt(endPos) == '*'))) ++endPos;
            substr = substr.substring(0,endPos);
            _autoImportSet.add(substr);
            
            
            line = line.substring(substr.length()).trim();
            if (!line.startsWith(";")) break;
            line = line.substring(1).trim();
          } while(line.startsWith("import "));
        }

        final String evalText = toEval;

        new Thread(new Runnable() { 
          public void run() { 
            try { interpret(evalText); } 
            catch(Throwable t) { DrJavaErrorHandler.record(t); }
          } 
        }).start(); 
      }
    });
  }
  
  
  public void autoImport() {
    java.util.Vector<String> classes = DrJava.getConfig().getSetting(OptionConstants.INTERACTIONS_AUTO_IMPORT_CLASSES);
    final StringBuilder sb = new StringBuilder();
    
    for(String s: classes) {
      String name = s.trim();
      if (s.length() > 0) {
        sb.append("import ");
        sb.append(s.trim());
        sb.append("; ");
      }
    }    
    if (DrJava.getConfig().getSetting(OptionConstants.DEBUG_AUTO_IMPORT).booleanValue()) {
      for(String s: _autoImportSet) {
        sb.append("import ");
        sb.append(s);
        sb.append("; ");
      }
    }

    if (sb.length() > 0) {
      interpret(sb.toString());
      _document.insertBeforeLastPrompt("Auto-import: " + sb.toString() + "\n", InteractionsDocument.DEBUGGER_STYLE);
    }
  }
  
  
  private void _prepareToInterpret(String text) {
    _addNewline();
    _notifyInteractionStarted();
    _document.setInProgress(true);
    _toAddToHistory = text; 
    
  }
  
  
  public void _addNewline() { append(StringOps.NEWLINE, InteractionsDocument.DEFAULT_STYLE); }
  
  
  public final void interpret(String toEval) { _interpret(toEval); }
  
  
  protected abstract void _interpret(String toEval);
  
  
  protected abstract void _notifyInteractionIncomplete();
  
  
  public abstract void _notifyInteractionStarted();
  
  
  public abstract String getVariableToString(String var, int... indices);
  
  
  public abstract String getVariableType(String var, int... indices);
  
  
  public final void resetInterpreter(File wd, boolean force) {
    _workingDirectory = wd;
    _autoImportSet.clear(); 
    _resetInterpreter(wd, force);
  }
  
  
  protected abstract void _resetInterpreter(File wd, boolean force);
  
  
  public File getWorkingDirectory() { return _workingDirectory; }
  
  
  public abstract void addProjectClassPath(File f);

  
  public abstract void addBuildDirectoryClassPath(File f);

  
  public abstract void addProjectFilesClassPath(File f);

  
  public abstract void addExternalFilesClassPath(File f);

  
  public abstract void addExtraClassPath(File f);
  
  
  protected abstract void _notifySyntaxErrorOccurred(int offset, int length);
  
 
  
  public void loadHistory(final FileOpenSelector selector) throws IOException {
    ArrayList<String> histories;
    try { histories = _getHistoryText(selector); }
    catch (OperationCanceledException oce) { return; }
    final ArrayList<String> _histories = histories;
    
    _document.clearCurrentInteraction();
    
    
    final StringBuilder buf = new StringBuilder();
    for (String hist: _histories) {
      ArrayList<String> interactions = _removeSeparators(hist);
      for (String curr: interactions) {
        int len = curr.length();
        buf.append(curr);
        if (len > 0 && curr.charAt(len - 1) != ';')  buf.append(';');
        buf.append(StringOps.EOL);
      }
    }
    String text = buf.toString().trim();

    append(text, InteractionsDocument.DEFAULT_STYLE);
    interpretCurrentInteraction();  

    
  }
  
   
  protected static ArrayList<String> _getHistoryText(FileOpenSelector selector)
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
      
      
      final StringBuilder text = new StringBuilder();
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
              text.append(StringOps.EOL);
              break;
            case (2):
              if (!firstLine) text.append(s).append(StringOps.EOL); 
              break;
          }
          firstLine = false;
        }
      }
      
      
      histories.add(text.toString());
    }
    return histories;
  }
  
  
  public InteractionsScriptModel loadHistoryAsScript(FileOpenSelector selector)
    throws IOException, OperationCanceledException {
    ArrayList<String> histories = _getHistoryText(selector);
    ArrayList<String> interactions = new ArrayList<String>();
    for (String hist: histories) interactions.addAll(_removeSeparators(hist));
    return new InteractionsScriptModel(this, interactions);
  }
  
  
  protected static ArrayList<String> _removeSeparators(String text) {
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
    System.setProperty("drjava.debug.port", String.valueOf(_debugPort));
  }
  
  
  public void setDebugPort(int port) {
    _debugPort = port;
    _debugPortSet = true;
  }
    
  private static final int DELAY_INTERVAL = 10;
  private volatile int delayCount = DELAY_INTERVAL;
  
  
  public void replSystemOutPrint(final String s) {
    Utilities.invokeLater(new Runnable() {
      public void run() { _document.insertBeforeLastPrompt(s, InteractionsDocument.SYSTEM_OUT_STYLE); }
    });
    if (delayCount == 0) {
      scrollToCaret();

      _writerDelay();
      delayCount = DELAY_INTERVAL;
    }
    else delayCount--;   
  }
  
  
  public void replSystemErrPrint(final String s) {
      Utilities.invokeLater(new Runnable() {
        public void run() { _document.insertBeforeLastPrompt(s, InteractionsDocument.SYSTEM_ERR_STYLE); } 
      });
      if (delayCount == 0) {
        scrollToCaret();

        _writerDelay();
        delayCount = DELAY_INTERVAL;
      }
      else delayCount--;
  }
  
  
  public String getConsoleInput() { return _inputListener.getConsoleInput(); }
  
  
  public void setInputListener(InputListener listener) {
    if (_inputListener == NoInputListener.ONLY || Utilities.TEST_MODE) { _inputListener = listener; }
    else throw new IllegalStateException("Cannot change the input listener until it is released.");
  }
  
  
  public void changeInputListener(InputListener oldListener, InputListener newListener) {
    if (_inputListener == oldListener) _inputListener = newListener;
    else
      throw new IllegalArgumentException("The given old listener is not installed!");      
  }
  
  
  public void _interactionIsOver() {
    Utilities.invokeLater(new Runnable() {
      public void run() {
        _document.addToHistory(_toAddToHistory);
        _document.setInProgress(false);
        _document.insertPrompt();
        _notifyInteractionEnded();
      }
    });
    scrollToCaret();
  }
  
  
  protected abstract void _notifyInteractionEnded();
  
  
  public void append(final String s, final String styleName) {
    Utilities.invokeLater(new Runnable() { public void run() { _document.append(s, styleName); } });
    scrollToCaret();
  }
  
  
  public void _writerDelay() {
    synchronized(_writerLock) {
      try {
        
        _writerLock.wait(_writeDelay);
      }
      catch (EditDocumentException e) { throw new UnexpectedException(e); }
      catch (InterruptedException e) { }
    }
  }
  
  
  public void replReturnedVoid() {
    _secondToLastError = _lastError;
    _lastError = null;
    _interactionIsOver();
  }
  
  
  public void replReturnedResult(String result, String style) {

    _secondToLastError = _lastError;
    _lastError = null;
    append(result + "\n", style);
    _interactionIsOver();
  }
  
  
  public StackTraceElement[] replaceLLException(StackTraceElement[] sT) {
    return sT;
    
  }
  
  
  public void replThrewException(String message, StackTraceElement[] stackTrace) {
    stackTrace = replaceLLException(stackTrace);
    StringBuilder sb = new StringBuilder(message);
    for(StackTraceElement ste: stackTrace) {
      sb.append("\n\tat ");
      sb.append(ste);
    }
    replThrewException(sb.toString().trim());
  }
  

  
  public void replThrewException(final String message) {
    if (message.endsWith("<EOF>\"")) {
      interactionContinues();
    }
    else {
      Utilities.invokeLater(new Runnable() {
        public void run() { _document.appendExceptionResult(message, InteractionsDocument.ERROR_STYLE); }
      });
      _secondToLastError = _lastError;
      _lastError = message;
      _interactionIsOver();
    }
  }
  
  
  public void replReturnedSyntaxError(String errorMessage, String interaction, int startRow, int startCol,
                                      int endRow, int endCol ) {
    
    
    _secondToLastError = _lastError;
    _lastError = errorMessage;
    if (errorMessage != null) {
      if (errorMessage.endsWith("<EOF>\"")) {
        interactionContinues();
        return;
      }
    }
    
    Pair<Integer,Integer> oAndL =
      StringOps.getOffsetAndLength(interaction, startRow, startCol, endRow, endCol);
    
    _notifySyntaxErrorOccurred(_document.getPromptPos() + oAndL.first().intValue(),oAndL.second().intValue());
    
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
      Utilities.invokeLater(new Runnable() {
        public void run() {
          _document.insertBeforeLastPrompt(" Resetting Interactions ...\n", InteractionsDocument.ERROR_STYLE);
          _document.setInProgress(true);
        }
      });
      
      
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
  
  public void interpreterWontStart(Exception e) {
    _interpreterWontStart(e);
    _document.setInProgress(true); 
  }
  
  
  protected abstract void _interpreterResetFailed(Throwable t);
  
  
  protected abstract void _notifyInterpreterResetFailed(Throwable t);
  
  
  protected abstract void _interpreterWontStart(Exception e);
  
  public String getBanner() { return _banner; }
  
  public String getStartUpBanner() { return getBanner(_workingDirectory); }
  
  public static String getBanner(File wd) { return BANNER_PREFIX + "  Working directory is " + wd + '\n'; }
  
  private String generateBanner(File wd) {
    _banner = getBanner(wd);
    return _banner;
  }
  


  













  
  protected void scrollToCaret() {
    Utilities.invokeLater(new Runnable() {
      public void run() {
        final InteractionsPane pane = _pane; 
        if (pane == null) return;  
        int pos = pane.getCaretPosition();
        try { pane.scrollRectToVisible(pane.modelToView(pos)); }
        catch(BadLocationException e) { throw new UnexpectedException(e); }
      }
    });
  }
  
  
  public void interpreterReady(final File wd) {
    debug.logStart();


    if (! _waitingForFirstInterpreter) {
      Utilities.invokeLater(new Runnable() {
        public void run() {
          _document.reset(generateBanner(wd));
          _document.setInProgress(false);
          if (_pane != null) _pane.setCaretPosition(_document.getLength());
          
          performDefaultImports();
          
          _notifyInterpreterReady(wd);
        }
      });
    }
    _waitingForFirstInterpreter = false;
    debug.logEnd();
  }

  
  public void performDefaultImports() {
    java.util.Vector<String> classes = DrJava.getConfig().getSetting(OptionConstants.INTERACTIONS_AUTO_IMPORT_CLASSES);
    final StringBuilder sb = new StringBuilder();
    
    for(String s: classes) {
      String name = s.trim();
      if (s.length() > 0) {
        sb.append("import ");
        sb.append(s.trim());
        sb.append("; ");
      }
    }
    if (sb.length() > 0) {
      interpret(sb.toString());
      _document.insertBeforeLastPrompt("Default imports: " + sb.toString() + "\n", InteractionsDocument.DEBUGGER_STYLE);
    }
  }
  
  
  public abstract void _notifyInterpreterReady(File wd);

  protected static String _transformJavaCommand(String s) {
    
    String command = "try '{'\n" +
                     "  java.lang.reflect.Method m = {0}.class.getMethod(\"main\", java.lang.String[].class);\n" +
                     "  if (!m.getReturnType().equals(void.class)) throw new java.lang.NoSuchMethodException();\n" +
                     "'}'\n" +
                     "catch (java.lang.NoSuchMethodException e) '{'\n" +
                     "  throw new java.lang.NoSuchMethodError(\"main\");\n" +
                     "'}'\n" +
                     "{0}.main(new String[]'{'{1}'}');";
    return _transformCommand(s, command);
  }
  
  protected static String _transformAppletCommand(String s) {
    return _transformCommand(s,"edu.rice.cs.plt.swing.SwingUtil.showApplet(new {0}({1}), 400, 300);");
  }
  
  
  protected static String _transformCommand(String s, String command) {
    if (s.endsWith(";"))  s = _deleteSemiColon(s);
    List<String> args = ArgumentTokenizer.tokenize(s, true);
    final String classNameWithQuotes = args.get(1); 
    final String className = classNameWithQuotes.substring(1, classNameWithQuotes.length() - 1); 
    final StringBuilder argsString = new StringBuilder();
    boolean seenArg = false;
    for (int i = 2; i < args.size(); i++) {
      if (seenArg) argsString.append(",");
      else seenArg = true;
      argsString.append(args.get(i));
    }
    return java.text.MessageFormat.format(command, className, argsString.toString());
  }
  
  
  protected static String _deleteSemiColon(String s) { return  s.substring(0, s.length() - 1); }
  
  
  private static class NoInputListener implements InputListener {
    public static final NoInputListener ONLY = new NoInputListener();
    private NoInputListener() { }
    
    public String getConsoleInput() { throw new IllegalStateException("No input listener installed!"); }
  }
  
  
  public abstract ConsoleDocument getConsoleDocument();
  
  
  public String getLastError() {
    return _lastError;
  }
  
  
  public String getSecondToLastError() {
    return _secondToLastError;
  }
  
  
  public void resetLastErrors() {
    _lastError = _secondToLastError = null;
  }
  
  
  public String removeLastFromHistory() {
    return _document.removeLastFromHistory();
  }
}
