

package edu.rice.cs.drjava.model.repl;

import edu.rice.cs.drjava.model.repl.newjvm.*;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.tuple.Option;
import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.util.text.ConsoleDocumentInterface;

import java.io.File;
import java.awt.EventQueue;

import static edu.rice.cs.plt.debug.DebugUtil.debug;


public abstract class RMIInteractionsModel extends InteractionsModel {
  
  
  protected final MainJVM _jvm;
  
  
  public RMIInteractionsModel(MainJVM jvm, ConsoleDocumentInterface cDoc, File wd, int historySize, int writeDelay) {
    super(cDoc, wd, historySize, writeDelay);
    _jvm = jvm;
  }
  
  
  protected void _interpret(String toEval) {
    debug.logStart("Interpret " + toEval);
    _jvm.interpret(toEval);
    debug.logEnd();
  }
  
  
  public String getVariableToString(String var, int... indices) {
    Option<String> result = _jvm.getVariableToString(var, indices);
    return result.unwrap("");
  }
  
  
  public String getVariableType(String var, int... indices) {
    Option<String> result = _jvm.getVariableType(var, indices);
    return result.unwrap("");
  }
  
  
  public void addProjectClassPath(File f) { _jvm.addProjectClassPath(f); }
  
  
  public void addBuildDirectoryClassPath(File f) { _jvm.addBuildDirectoryClassPath(f); }
  
  
  public void addProjectFilesClassPath(File f) { _jvm.addProjectFilesClassPath(f); }
  
  
  public void addExternalFilesClassPath(File f) { _jvm.addExternalFilesClassPath(f); }
  
  
  public void addExtraClassPath(File f) { _jvm.addExtraClassPath(f); }
  
  
  protected void _resetInterpreter(File wd, boolean force) {
    setToDefaultInterpreter();
    _jvm.setWorkingDirectory(wd);
    _jvm.restartInterpreterJVM(force);
  }
  
  
  public void addInterpreter(String name) { _jvm.addInterpreter(name); }
  
  
  public void removeInterpreter(String name) { _jvm.removeInterpreter(name); }
  
  
  public void setActiveInterpreter(String name, String prompt) {
    Option<Pair<Boolean, Boolean>> result = _jvm.setActiveInterpreter(name);
    debug.logValue("result", result);
    if (result.isSome() && result.unwrap().first()) { 
      boolean inProgress = result.unwrap().second();
      _updateDocument(prompt, inProgress);
      _notifyInterpreterChanged(inProgress);
    }
  }
  
  
  public void setToDefaultInterpreter() {
    Option<Pair<Boolean, Boolean>> result = _jvm.setToDefaultInterpreter();
    if (result.isSome() && result.unwrap().first()) { 
      boolean inProgress = result.unwrap().second();
      _updateDocument(InteractionsDocument.DEFAULT_PROMPT, inProgress);
      _notifyInterpreterChanged(inProgress);
    }
  }
  
  
  private void _updateDocument(String prompt, boolean inProgress) {
    assert EventQueue.isDispatchThread();
    _document.setPrompt(prompt);
    _document.insertNewline(_document.getLength());
    _document.insertPrompt();


    _document.setInProgress(inProgress);
    scrollToCaret();
  }
  
  
  protected abstract void _notifyInterpreterChanged(boolean inProgress);

  
  public void setEnforceAllAccess(boolean enforce) { _jvm.setEnforceAllAccess(enforce); }
  
  
  public void setEnforcePrivateAccess(boolean enforce) { _jvm.setEnforcePrivateAccess(enforce); }

  
  public void setRequireSemicolon(boolean require) { _jvm.setRequireSemicolon(require); }
  
  
  public void setRequireVariableType(boolean require) { _jvm.setRequireVariableType(require); }
  
  
  public Iterable<File> getClassPath() { 
    Option<Iterable<File>> result = _jvm.getClassPath();
    return result.unwrap(IterUtil.<File>empty());
  }
  
}
