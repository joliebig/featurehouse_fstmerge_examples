

package edu.rice.cs.drjava.plugins.jedit.repl;

import java.io.File;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

import org.gjt.sp.jedit.*;

import edu.rice.cs.drjava.plugins.jedit.JEditPlugin;
import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.OptionConstants;


import edu.rice.cs.drjava.model.repl.RMIInteractionsModel;
import edu.rice.cs.drjava.model.repl.InteractionsListener;
import edu.rice.cs.drjava.model.repl.InteractionsDocument;
import edu.rice.cs.drjava.model.repl.newjvm.MainJVM;
import edu.rice.cs.drjava.model.definitions.InvalidPackageException;
import edu.rice.cs.util.text.SwingDocumentAdapter;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.Log;


public class JEditInteractionsModel extends RMIInteractionsModel {
  
  protected static int HISTORY_SIZE =
    DrJava.getConfig().getSetting(OptionConstants.HISTORY_MAX_SIZE).intValue();








  

  
  protected static final int WRITE_DELAY = 50;

  
  private static final boolean DEBUG = false;

  
  protected final List<InteractionsListener> _listeners;

  
  private final Log _log;

  
  public JEditInteractionsModel() {
    this(new MainJVM(), new SwingDocumentAdapter());
  }

  
  public JEditInteractionsModel(SwingDocumentAdapter adapter) {
    this(new MainJVM(), adapter);
  }

  
  public JEditInteractionsModel(MainJVM control, SwingDocumentAdapter adapter) {
    super(control, adapter, HISTORY_SIZE, WRITE_DELAY);
    _listeners = new ArrayList<InteractionsListener>();
    if (DEBUG) {
      _debugSystemOutAndErr();
    }
    _log = new Log("JEditInteractionsModelLog", DEBUG);

    _interpreterControl.setInteractionsModel(this);
    String classpath = System.getProperty("java.class.path");
    String pathSep = System.getProperty("path.separator");
    classpath += pathSep;
    classpath += JEditPlugin.getDefault().getPluginJAR().getPath();
    Vector<File> cp = DrJava.getConfig().getSetting(OptionConstants.EXTRA_CLASSPATH);
    for (int i = 0; i < cp.size(); i++) {
      classpath += pathSep;
      classpath += cp.elementAt(i).getAbsolutePath();
    }
    _interpreterControl.setStartupClasspath(classpath);
    _interpreterControl.startInterpreterJVM();
  }

  
  private void _log(String message) {
    _log.logTime(message);
  }

  
  private void _log(String message, Throwable t) {
    _log.logTime(message, t);
  }

  
  public void dispose() {
    _interpreterControl.killInterpreter(false);
    String warning = "You may only have one instance of the Interactions Pane at a time,\n" +
      "so this pane will no longer be functional.";
    _document.setInProgress(true);
    _document.insertBeforeLastPrompt(warning, InteractionsDocument.ERROR_STYLE);
  }

  
  public void addInteractionsListener(InteractionsListener l) {
    _listeners.add(l);
  }

  
  public void removeInteractionsListener(InteractionsListener l) {
    _listeners.remove(l);
  }

  
  public void removeAllInteractionsListeners() {
    _listeners.clear();
  }

  
  public String getConsoleInput() {
    return JEditPlugin.getDefault().getConsoleInput();
  }

  
  public void interpreterReady() {
    _resetInteractionsClasspath();
    super.interpreterReady();
  }

  
  protected void _notifyInteractionStarted() {
    for (int i=0; i < _listeners.size(); i++) {
      _listeners.get(i).interactionStarted();
    }
  }

  
  protected void _notifyInteractionEnded() {
    for (int i=0; i < _listeners.size(); i++) {
      _listeners.get(i).interactionEnded();
    }
  }

  
  protected void _notifySyntaxErrorOccurred(final int offset, final int length) {
    for (int i=0; i < _listeners.size(); i++) {
      _listeners.get(i).interactionErrorOccurred(offset, length);
    }
  }

  
  protected void _notifyInterpreterResetting() {
    for (int i=0; i < _listeners.size(); i++) {
      _listeners.get(i).interpreterResetting();
    }
  }

  
  protected void _notifyInterpreterReady() {
    for (int i=0; i < _listeners.size(); i++) {
      _listeners.get(i).interpreterReady();
    }
  }

  
  protected void _notifyInterpreterExited(final int status) {
    for (int i=0; i < _listeners.size(); i++) {
      _listeners.get(i).interpreterExited(status);
    }
  }

  
  protected void _notifyInterpreterChanged(final boolean inProgress) {
    for (int i=0; i < _listeners.size(); i++) {
      _listeners.get(i).interpreterChanged(inProgress);
    }
  }

  
  protected void _notifyInterpreterResetFailed(final Throwable t) {
    for (int i=0; i < _listeners.size(); i++) {
      _listeners.get(i).interpreterResetFailed(t);
    }
  }

  
  protected void _notifyInteractionIncomplete() {
    for (int i=0; i < _listeners.size(); i++) {
      _listeners.get(i).interactionIncomplete();
    }
  }

  protected void _interpreterResetFailed(Throwable t) {
  }

  
  protected void _resetInteractionsClasspath() {
    Buffer[] buffers = jEdit.getBuffers();
    for (int i = 0; i < buffers.length; i++) {
      addToClassPath(buffers[i]);
    }
  }

  
  public void addToClassPath(Buffer b) {
    try {
      if (_isJavaFile(b)) {
        String srcRoot = _getSourceRoot(b);
        if (srcRoot != null) {
          _interpreterControl.addClassPath(srcRoot);
        }
        else {
          replSystemOutPrint(b.getPath());
        }
      }
    }
    catch (InvalidPackageException ipe) {
      
      
      _log("Error adding to classpath", ipe);
    }
  }

  
  private boolean _isJavaFile(Buffer b) {
    return b.isLoaded() && !b.isNewFile() && !b.isUntitled() && b.getName().endsWith(".java");
  }

  
  private String _getSourceRoot(Buffer b) throws InvalidPackageException {
    String packageName = _getPackageName(b);
    String sourcePath = b.getPath();
    File sourceFile = new File(sourcePath);

    if (packageName.equals("")) {
      return sourceFile.getParent();
    }

    LinkedList<String> packageStack = new LinkedList<String>();
    int dotIndex = packageName.indexOf('.');
    int curPartBegins = 0;

    while (dotIndex != -1) {
      packageStack.addFirst(packageName.substring(curPartBegins, dotIndex));
      curPartBegins = dotIndex + 1;
      dotIndex = packageName.indexOf('.', curPartBegins);
    }
    
    packageStack.addFirst(packageName.substring(curPartBegins));

    
    
    try {
      File parentDir = sourceFile.getCanonicalFile();
      while (packageStack.size() > 0) {
        String part = packageStack.removeFirst();
        parentDir = parentDir.getParentFile();
        if (parentDir == null) {
          throw new RuntimeException("parent dir is null?!");
        }

        
        if (!part.equals(parentDir.getName())) {
          String msg = "The source file " + sourcePath +
            " is in the wrong directory or in the wrong package. " +
            "The directory name " + parentDir.getName() +
            " does not match the package component " + part + ".";
          throw new InvalidPackageException(-1, msg);
        }
      }

      
      
      return parentDir.getParent();
    }
    catch (IOException ioe) {
      String msg = "Could not locate directory of the source file: " + ioe;
      throw new InvalidPackageException(-1, msg);
    }
  }

  
  private String _getPackageName(Buffer b) throws InvalidPackageException {
    String text = b.getText(0, b.getLength());
    StreamTokenizer st = new StreamTokenizer(new StringReader(text));
    st.slashSlashComments(true);
    st.slashStarComments(true);
    try {
      if (st.nextToken() == StreamTokenizer.TT_WORD && st.sval.equals("package")) {
        
        if (st.nextToken() == StreamTokenizer.TT_WORD) {
          
          String packageName = st.sval;
          if (st.nextToken() == ';') {
            
            return packageName;
          }
        }
        
        throw new InvalidPackageException(-1, "Invalid package statement");
      }
      return "";
    }
    catch (IOException ioe) {
      
      throw new UnexpectedException(ioe);
    }
  }

  
  protected void _warnUserToReset() {
    if (interpreterUsed()) {
      String warning =
        "Warning: Interactions are out of sync with the current class files.\n" +
        "You should reset interactions before contuing.\n";
      _document.insertBeforeLastPrompt(warning, InteractionsDocument.ERROR_STYLE);
    }
  }

  
  private void _debugSystemOutAndErr() {
    System.setOut(new java.io.PrintStream(new edu.rice.cs.util.OutputStreamRedirector() {
      public void print(String s) {
        _log("stdout:  " + s);
      }
    }));
    System.setErr(new java.io.PrintStream(new edu.rice.cs.util.OutputStreamRedirector() {
      public void print(String s) {
        _log("stderr: " + s);
      }
    }));
  }
}
