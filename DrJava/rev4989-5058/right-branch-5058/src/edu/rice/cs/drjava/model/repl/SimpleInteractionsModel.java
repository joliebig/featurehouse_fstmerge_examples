

package edu.rice.cs.drjava.model.repl;

import java.io.File;
import java.io.StringWriter;
import java.io.PrintWriter;

import edu.rice.cs.drjava.model.repl.newjvm.ClassPathManager;
import edu.rice.cs.drjava.model.AbstractGlobalModel;

import edu.rice.cs.util.StringOps;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.util.text.ConsoleDocument;
import edu.rice.cs.plt.reflect.ReflectUtil;
import edu.rice.cs.plt.tuple.Option;
import edu.rice.cs.plt.text.TextUtil;

import edu.rice.cs.dynamicjava.Options;
import edu.rice.cs.dynamicjava.interpreter.Interpreter;
import edu.rice.cs.dynamicjava.interpreter.InterpreterException;
import edu.rice.cs.dynamicjava.interpreter.EvaluatorException;


public class SimpleInteractionsModel extends InteractionsModel {
  
  


  
  protected ClassPathManager _classPathManager;
  protected Interpreter _interpreter;
  protected final InteractionsPaneOptions _interpreterOptions;
  
  
  public SimpleInteractionsModel() { this(new InteractionsDJDocument()); }
  
  
  public SimpleInteractionsModel(InteractionsDJDocument document) {
    super(document, new File(System.getProperty("user.dir")), 1000, AbstractGlobalModel.WRITE_DELAY);
    _classPathManager = new ClassPathManager(ReflectUtil.SYSTEM_CLASS_PATH);
    _interpreterOptions = new InteractionsPaneOptions();
    _interpreter = new Interpreter(_interpreterOptions, _classPathManager.makeClassLoader(null));
    
  }
  
  
  protected void _interpret(String toEval) {
    try {
      Option<Object> result = _interpreter.interpret(toEval);
      if (result.isSome()) {
        String objString = null;
        try { objString = TextUtil.toString(result.unwrap()); }
        catch (Throwable t) { throw new EvaluatorException(t); }
        append(objString + "\n", InteractionsDocument.OBJECT_RETURN_STYLE);
      }
    }
    catch (InterpreterException e) {
      StringWriter msg = new StringWriter();
      e.printUserMessage(new PrintWriter(msg));
      _document.appendExceptionResult(msg.toString(), InteractionsDocument.DEFAULT_STYLE);
    }
    finally { _interactionIsOver(); }
  }
  
  
  public String getVariableToString(String var, int... indices) {
    try {
      Option<Object> value = _interpreter.interpret(var);
      try { return TextUtil.toString(value.unwrap("")); }
      catch (Throwable t) { throw new EvaluatorException(t); }
    }
    catch (InterpreterException e) { return ""; }
  }
  
  
  public String getVariableType(String var, int... indices) {
    return null; 


  }
  
  
  public void addProjectClassPath(File path) { _classPathManager.addProjectCP(path); }
  
  
  public void addBuildDirectoryClassPath(File path) { _classPathManager.addBuildDirectoryCP(path); }
  
  
  public void addProjectFilesClassPath(File path) { _classPathManager.addProjectFilesCP(path); }
  
  
  public void addExternalFilesClassPath(File path) { _classPathManager.addExternalFilesCP(path); }
  
  
  public void addExtraClassPath(File path) { _classPathManager.addExtraCP(path); }
  
  
  public void setEnforceAllAccess(boolean enforce) { _interpreterOptions.setEnforceAllAccess(enforce); }
  
  
  public void setEnforcePrivateAccess(boolean enforce) { _interpreterOptions.setEnforcePrivateAccess(enforce); }

  
  public void setRequireSemicolon(boolean require) { _interpreterOptions.setRequireSemicolon(require); }
  
  
  public void setRequireVariableType(boolean require) { _interpreterOptions.setRequireVariableType(require); }
  
  
  protected void _interpreterResetFailed(Throwable t) {
    _document.insertBeforeLastPrompt("Reset Failed!" + StringOps.NEWLINE, InteractionsDocument.ERROR_STYLE);
  }
  
  protected void _interpreterWontStart(Exception e) {
    _document.insertBeforeLastPrompt("JVM failed to start." + StringOps.NEWLINE, InteractionsDocument.ERROR_STYLE);
  }
  
  
  protected void _resetInterpreter(File wd, boolean force) {
    interpreterResetting();
    _classPathManager = new ClassPathManager(ReflectUtil.SYSTEM_CLASS_PATH);
    _interpreter = new Interpreter(Options.DEFAULT, _classPathManager.makeClassLoader(null));
    interpreterReady(wd);
  }
  
  
  public void _notifyInteractionStarted() { 
    Utilities.invokeLater(new Runnable() { public void run() { _notifier.interactionStarted(); } });
  }
  
  
  protected void _notifyInteractionEnded() {
    Utilities.invokeLater(new Runnable() { public void run() { _notifier.interactionEnded(); } });
  }
  
  
  protected void _notifySyntaxErrorOccurred(final int offset, final int length) {
    Utilities.invokeLater(new Runnable() { public void run() { _notifier.interactionErrorOccurred(offset, length); } });
  }
  
  
  protected void _notifyInterpreterResetting() {    }
  
  
  public void _notifyInterpreterReady(File wd) {
    
  }
  
  
  protected void _notifyInterpreterExited(final int status) {
    
  }
  
  
  protected void _notifyInterpreterResetFailed(Throwable t) {
    
  }
  
  
  protected void _notifyInteractionIncomplete() {
    
  }
  
  
  public ConsoleDocument getConsoleDocument() { return null; }
}
