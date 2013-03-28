

package edu.rice.cs.drjava.model.repl;

import edu.rice.cs.drjava.model.repl.newjvm.*;
import edu.rice.cs.util.text.EditDocumentInterface;

import java.net.URL;
import java.util.Vector;
import java.io.File;


public abstract class RMIInteractionsModel extends InteractionsModel {

  
  protected final MainJVM _jvm;

  
  public RMIInteractionsModel(MainJVM jvm, EditDocumentInterface adapter, File wd, int historySize, int writeDelay) {
    super(adapter, wd, historySize, writeDelay);
    _jvm = jvm;
  }

  
  protected void _interpret(String toEval) { _jvm.interpret(toEval); }

  
  public String getVariableToString(String var) { return _jvm.getVariableToString(var); }

  
  public String getVariableClassName(String var) {
    return _jvm.getVariableClassName(var);
  }

  




  public void addProjectClassPath(URL path) { _jvm.addProjectClassPath(path); }

  public void addBuildDirectoryClassPath(URL path) { _jvm.addBuildDirectoryClassPath(path); }
  
  public void addProjectFilesClassPath(URL path) { 

    _jvm.addProjectFilesClassPath(path); 
  }
  
  public void addExternalFilesClassPath(URL path) { _jvm.addExternalFilesClassPath(path); }
  
  public void addExtraClassPath(URL path) { _jvm.addExtraClassPath(path); }
  
  
  protected void _resetInterpreter(File wd) { _jvm.killInterpreter(wd); }

  
  public void addJavaInterpreter(String name) { _jvm.addJavaInterpreter(name); }

  
  public void addDebugInterpreter(String name, String className) {
    _jvm.addDebugInterpreter(name, className);
  }

  
  public void removeInterpreter(String name) {
    _jvm.removeInterpreter(name);
  }

  
  public void setActiveInterpreter(String name, String prompt) {
    String currName = _jvm.getCurrentInterpreterName();
    boolean inProgress = _jvm.setActiveInterpreter(name);
    _updateDocument(prompt, inProgress, !currName.equals(name));
    _notifyInterpreterChanged(inProgress);
  }

  
  public void setToDefaultInterpreter() {
    
    String currName = _jvm.getCurrentInterpreterName();
    boolean printPrompt = !MainJVM.DEFAULT_INTERPRETER_NAME.equals(currName);

    boolean inProgress = _jvm.setToDefaultInterpreter();

    _updateDocument(InteractionsDocument.DEFAULT_PROMPT, inProgress, printPrompt);
    _notifyInterpreterChanged(inProgress);
  }

  
  protected void _updateDocument(String prompt, boolean inProgress, boolean updatePrompt) {
    if (updatePrompt) {
      _document.setPrompt(prompt);
      _document.insertNewLine(_document.getLength());
      _document.insertPrompt();
    }
    _document.setInProgress(inProgress);
  }

  
  protected abstract void _notifyInterpreterChanged(boolean inProgress);

  
  public void setPrivateAccessible(boolean allow) {
    _jvm.setPrivateAccessible(allow);
  }

  
  public Vector<URL> getClassPath() { return _jvm.getClassPath(); }
}