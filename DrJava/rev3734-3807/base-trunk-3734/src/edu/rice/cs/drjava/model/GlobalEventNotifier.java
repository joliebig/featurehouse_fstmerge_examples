

package edu.rice.cs.drjava.model;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import edu.rice.cs.util.classloader.ClassFileError;
import edu.rice.cs.util.FileOpenSelector;
import edu.rice.cs.util.swing.Utilities;


public class GlobalEventNotifier extends EventNotifier<GlobalModelListener>
    implements GlobalModelListener, Serializable {

  public void fileNotFound(File f) {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.fileNotFound(f); } }
    finally { _lock.endRead(); }
  }
  
  
  public void projectOpened(File pfile, FileOpenSelector files) {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.projectOpened(pfile, files); } }
    finally { _lock.endRead(); }
  }
  
  public void projectClosed() {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.projectClosed();} }
    finally { _lock.endRead(); }
  }
  
  public void projectModified() {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.projectModified(); } }
    finally { _lock.endRead(); }
  }
  
  public void projectBuildDirChanged() {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.projectBuildDirChanged(); } }
    finally { _lock.endRead(); }
  }
  
  public void projectWorkDirChanged() {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.projectWorkDirChanged(); } }
    finally { _lock.endRead(); }
  }
  
  public void projectRunnableChanged() {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.projectRunnableChanged(); } }
    finally { _lock.endRead(); }
  }
  
  
  

  
  public void notifyListeners(Notifier n) {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { n.notifyListener(l); } }
    finally { _lock.endRead(); }
  }

  
  @Deprecated
  public boolean pollListeners(Poller p) {
    _lock.startRead();
    try {
      for (GlobalModelListener l: _listeners) { if (! p.poll(l)) return false; }
      return true;
    }
    finally { _lock.endRead(); }
  }

  
  @Deprecated
  public abstract static class Notifier {
    public abstract void notifyListener(GlobalModelListener l);
  }

  
  @Deprecated
  public abstract static class Poller {
    public abstract boolean poll(GlobalModelListener l);
  }

  



  

  
  public void runStarted(OpenDefinitionsDocument doc) {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.runStarted(doc); } }
    finally { _lock.endRead(); }
  }

  
  public void newFileCreated(OpenDefinitionsDocument doc) {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.newFileCreated(doc); } }
    finally { _lock.endRead(); }
  }

  
  public void consoleReset() {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.consoleReset(); } }
    finally { _lock.endRead(); }
  }

  
  public void fileSaved(OpenDefinitionsDocument doc) {


    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.fileSaved(doc); } }
    finally { _lock.endRead(); }
  }

  
  public void fileOpened(OpenDefinitionsDocument doc) {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.fileOpened(doc); } }
    finally { _lock.endRead(); }
  }

  
  public void fileClosed(OpenDefinitionsDocument doc) {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.fileClosed(doc); } }
    finally { _lock.endRead(); }
  }

  
  public void fileReverted(OpenDefinitionsDocument doc) {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.fileReverted(doc); } }
    finally { _lock.endRead(); }
  }

  
  public void undoableEditHappened() {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.undoableEditHappened(); } }
    finally { _lock.endRead(); }
  }

  
  public boolean canAbandonFile(OpenDefinitionsDocument doc) {
    _lock.startRead();
    try {
      for (GlobalModelListener l: _listeners) { if (! l.canAbandonFile(doc)) return false; }
      return true;
    }
    finally { _lock.endRead(); }
  }

   
  public void quitFile(OpenDefinitionsDocument doc) {
    _lock.startRead();
    try {
      for (GlobalModelListener l: _listeners) { l.quitFile(doc); }
    }
    finally { _lock.endRead(); }
  }

  
  public boolean shouldRevertFile(OpenDefinitionsDocument doc) {
    _lock.startRead();
    try { 
      for (GlobalModelListener l: _listeners) { if (! l.shouldRevertFile(doc)) return false; }
      return true;
    }
    finally { _lock.endRead(); }
  }

  
  public void currentDirectoryChanged(File dir) {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.currentDirectoryChanged(dir); } }
    finally { _lock.endRead(); }
  }
  
  
  public void activeDocumentChanged(OpenDefinitionsDocument active) {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.activeDocumentChanged(active); } }
    finally { _lock.endRead(); }
  }
  
   
  public void focusOnDefinitionsPane() {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.focusOnDefinitionsPane(); } }
    finally { _lock.endRead(); }
  }









  

  
  public void interactionStarted() {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.interactionStarted(); } }
    finally { _lock.endRead(); }
  }

  
  public void interactionEnded() {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.interactionEnded(); } }
    finally { _lock.endRead(); }
  }

  
  public void interactionErrorOccurred(int offset, int length) {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.interactionErrorOccurred(offset, length); } }
    finally { _lock.endRead(); }
  }

  
  public void interpreterResetting() {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.interpreterResetting(); } }
    finally { _lock.endRead(); }
  }

  
  public void interpreterReady(File wd) {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.interpreterReady(wd); } }
    finally { _lock.endRead(); }
  }

  
  public void interpreterResetFailed(final Throwable t) {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.interpreterResetFailed(t); } }
    finally { _lock.endRead(); }
  }

  
  public void interpreterExited(int status) {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.interpreterExited(status); } }
    finally { _lock.endRead(); }
  }

  
  public void interpreterChanged(boolean inProgress) {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.interpreterChanged(inProgress); } }
    finally { _lock.endRead(); }
  }

  

  
  public void compileStarted() {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.compileStarted(); }
    }
    finally { _lock.endRead(); }
  }

  
  public void compileEnded(File workDir) {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.compileEnded(workDir); } }
    finally { _lock.endRead(); }
  }

  
  public void saveBeforeCompile() {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.saveBeforeCompile(); } }
    finally { _lock.endRead(); }
  }

  
  public void saveUntitled() {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.saveUntitled(); } }
    finally { _lock.endRead(); }
  }
  
  

  
  public void nonTestCase(boolean isTestAll) {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.nonTestCase(isTestAll); } }
    finally { _lock.endRead(); }
  }

  
  public void classFileError(ClassFileError e) {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.classFileError(e); } }
    finally { _lock.endRead(); }
  }
  
  
  public void compileBeforeJUnit() {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.compileBeforeJUnit(); } }
    finally { _lock.endRead(); }
  }
  
  
  public void junitStarted() {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.junitStarted(); } }
    finally { _lock.endRead(); }
  }

  
  public void junitClassesStarted() {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.junitClassesStarted(); } }
    finally { _lock.endRead(); }
  }
  
  
  public void junitSuiteStarted(int numTests) {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.junitSuiteStarted(numTests); } }
    finally { _lock.endRead(); }
  }

  
  public void junitTestStarted(String name) {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.junitTestStarted(name); } }
    finally { _lock.endRead(); }
  }

  
  public void junitTestEnded(String name, boolean wasSuccessful, boolean causedError) {
    _lock.startRead();
    try { 
      for (GlobalModelListener l : _listeners) { l.junitTestEnded(name, wasSuccessful, causedError); }
    }
    finally { _lock.endRead(); }
  }

  
  public void junitEnded() {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.junitEnded(); } }
    finally { _lock.endRead(); }
  }
















  

  
  public void javadocStarted() {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.javadocStarted(); } }
    finally { _lock.endRead(); }
  }

  
  public void javadocEnded(boolean success, File destDir, boolean allDocs) {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.javadocEnded(success, destDir, allDocs); } }
    finally { _lock.endRead(); }
  }


  
  public void saveBeforeJavadoc() {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.saveBeforeJavadoc(); } }
    finally { _lock.endRead(); }
  }










  
  public void interactionIncomplete() {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.interactionIncomplete(); } }
    finally { _lock.endRead(); }
  }
  
  public void slaveJVMUsed() {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.slaveJVMUsed(); } }
    finally { _lock.endRead(); }
  }

  
  public void filePathContainsPound() {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.filePathContainsPound(); } }
    finally { _lock.endRead(); }
  }
  
  
  public void documentNotFound(OpenDefinitionsDocument d, File f) {
    _lock.startRead();
    try { for (GlobalModelListener l : _listeners) { l.documentNotFound(d,f); } }
    finally { _lock.endRead(); } 
  }
  
}
