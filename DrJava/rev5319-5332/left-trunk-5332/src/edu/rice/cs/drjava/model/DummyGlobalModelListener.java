

package edu.rice.cs.drjava.model;

import java.io.File;
import java.util.List;
import edu.rice.cs.drjava.model.compiler.CompilerListener;
import edu.rice.cs.drjava.model.repl.DummyInteractionsListener;
import edu.rice.cs.util.classloader.ClassFileError;
import edu.rice.cs.util.swing.AsyncTask;
import edu.rice.cs.util.FileOpenSelector;


public class DummyGlobalModelListener extends DummyInteractionsListener implements GlobalModelListener {
  
  
  public <P,R> void executeAsyncTask(AsyncTask<P,R> task, P param, boolean showProgress, boolean lockUI) {  }
  
  public void handleAlreadyOpenDocument(OpenDefinitionsDocument doc) { }
  
  
  public void filesNotFound(File... f) {  }

  
  public File[] filesReadOnly(File... f) { return f; }
  
  
  public void newFileCreated(OpenDefinitionsDocument doc) { }
  
  
  public void fileSaved(OpenDefinitionsDocument doc) { }
  
  
  public void fileOpened(OpenDefinitionsDocument doc) { }
  
  
  public void fileClosed(OpenDefinitionsDocument doc) { }
  
  
  public void fileReverted(OpenDefinitionsDocument doc) { }
  
  
  public void undoableEditHappened() { }
  
  
  public void compileStarted() { }
  
  
  public void compileEnded(File workDir, List<? extends File> excludedFiles) { }
  
  
  public void compileAborted(Exception e) { }

  
  public void activeCompilerChanged() { }

  
  public void prepareForRun(OpenDefinitionsDocument doc) { }
  
  
  public void filePathContainsPound() { }
  
  
  public void compileBeforeJUnit(final CompilerListener l, List<OpenDefinitionsDocument> outOfSync) { }
  
  
  public void junitStarted() { }
  
  
  public void junitClassesStarted() {  }
  
  
  public void junitSuiteStarted(int numTests) { }
  
  
  public void junitTestStarted(String name) { }
  
  
  public void junitTestEnded(String name, boolean wasSuccessful, boolean causedError) { }
  
  
  public void junitEnded() { }
  
  
  public void consoleReset() { }
  
  
  public void saveBeforeCompile() { }
  
  public void saveUntitled() { }
  
  
  public void saveBeforeJavadoc() { }

  
  public void compileBeforeJavadoc(final CompilerListener afterCompile) { }
  
  

  
  
  public void currentDirectoryChanged(File dir) { }
  
  
  public void nonTestCase(boolean isTestAll, boolean didCompileFail) { }
  
  
  public void classFileError(ClassFileError e) { }
  
  
  public boolean canAbandonFile(OpenDefinitionsDocument doc) { return true; }
  
  
  public boolean quitFile(OpenDefinitionsDocument doc) { return true; }
  
  
  public boolean shouldRevertFile(OpenDefinitionsDocument doc) { return true; }
  
  
  public void javadocStarted() { }
  
  
  public void javadocEnded(boolean success, File destDir, boolean allDocs) { }
  
  public void activeDocumentChanged(OpenDefinitionsDocument active) { }
  
  public void activeDocumentRefreshed(OpenDefinitionsDocument active) { }
  
  public void focusOnLastFocusOwner() { }
  
  public void focusOnDefinitionsPane() { }
  
  public void documentNotFound(OpenDefinitionsDocument d, File f) { }
  
  
  public void projectBuildDirChanged() {  }
  
  
  public void projectWorkDirChanged() {  }
  
  
  public void openProject(File pfile, FileOpenSelector files) {  }
  
  
  public void projectClosed() {  }
  
  
  public void allFilesClosed() {  }
  
  
  public void projectModified() {  }
  
  
  public void projectRunnableChanged() {  }
  
  
  public void browserChanged() { }
  
  
  public void updateCurrentLocationInDoc() { }
}
