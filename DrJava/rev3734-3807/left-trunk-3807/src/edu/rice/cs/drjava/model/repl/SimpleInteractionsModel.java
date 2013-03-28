

package edu.rice.cs.drjava.model.repl;

import java.net.URL;
import java.io.File;


import edu.rice.cs.drjava.model.repl.newjvm.InterpreterJVM;
import edu.rice.cs.drjava.model.repl.newjvm.ClassPathManager;

import edu.rice.cs.util.swing.Utilities;

import edu.rice.cs.util.text.ConsoleDocument;


public class SimpleInteractionsModel extends InteractionsModel {

  
  protected static final int WRITE_DELAY = 5;

  
  protected JavaInterpreter _interpreter;

  
  public SimpleInteractionsModel() { this(new InteractionsDJDocument()); }

  
  public SimpleInteractionsModel(InteractionsDJDocument document) {
    super(document, new File(System.getProperty("user.dir")), 1000, WRITE_DELAY);
    _interpreter = new DynamicJavaAdapter(new ClassPathManager());

    _interpreter.defineVariable("INTERPRETER", _interpreter);
  }

  
  protected void _interpret(String toEval) {
    try {
      Object result = _interpreter.interpret(toEval);
      if (result != Interpreter.NO_RESULT) {
        append(String.valueOf(result) + System.getProperty("line.separator"),
                   InteractionsDocument.OBJECT_RETURN_STYLE);
      }
    }
    catch (ExceptionReturnedException e) {
      Throwable t = e.getContainedException();
      
      _document.appendExceptionResult(t.getClass().getName(),
                                      t.getMessage(),
                                      InterpreterJVM.getStackTrace(t),
                                      InteractionsDocument.DEFAULT_STYLE);
    }
    finally {
      _interactionIsOver();
    }
  }

  
  public String getVariableToString(String var) {
    Object value = _interpreter.getVariable(var);
    return value.toString();
  }

  
  public String getVariableClassName(String var) {
    Class c = _interpreter.getVariableClass(var);
    return c.getName();
  }

  
  public void addProjectClassPath(URL path) { _interpreter.addProjectClassPath(path); }

  
  public void addBuildDirectoryClassPath(URL path) { _interpreter.addBuildDirectoryClassPath(path); }

  
  public void addProjectFilesClassPath(URL path) { _interpreter.addProjectFilesClassPath(path); }

  
  public void addExternalFilesClassPath(URL path) { _interpreter.addExternalFilesClassPath(path); }

  
  public void addExtraClassPath(URL path) { _interpreter.addExtraClassPath(path); }


  
  public void defineVariable(String name, Object value) { _interpreter.defineVariable(name, value); }

  
  public void defineConstant(String name, Object value) { _interpreter.defineConstant(name, value); }

  
  public void setInterpreterPrivateAccessible(boolean accessible) { _interpreter.setPrivateAccessible(accessible); }

  
  protected void _interpreterResetFailed(Throwable t) {
    _document.insertBeforeLastPrompt("Reset Failed!" + _newLine, InteractionsDocument.ERROR_STYLE);
  }

  
  protected void _resetInterpreter(File wd) {
    interpreterResetting();
    _interpreter = new DynamicJavaAdapter(new ClassPathManager());
    interpreterReady(wd);
  }

  
  protected void _notifyInteractionStarted() { 
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
  
  
  protected void _notifySlaveJVMUsed() {  }
   
  
  public ConsoleDocument getConsoleDocument() { return null; }
}
