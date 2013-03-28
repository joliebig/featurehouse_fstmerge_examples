

package edu.rice.cs.drjava.model;

import java.io.File;
import java.util.List;
import edu.rice.cs.util.classloader.ClassFileError;
import edu.rice.cs.util.swing.AsyncTask;
import edu.rice.cs.util.FileOpenSelector;


public class DummyGlobalModelListener implements GlobalModelListener {

	
	public <P,R> void executeAsyncTask(AsyncTask<P,R> task, P param, boolean showProgress, boolean lockUI) {  }
	
  
  public void fileNotFound(File f) {  }
  
  
  public void projectBuildDirChanged() {  }
  
  
  public void projectWorkDirChanged() {  }
  
  
  public void projectOpened(File pfile, FileOpenSelector files) {  }
  
  
  public void projectClosed() {  }
  
  
  public void projectModified() {  }

  
  public void projectRunnableChanged() {  }
  
  
  public void newFileCreated(OpenDefinitionsDocument doc) { }

  
  public void fileSaved(OpenDefinitionsDocument doc) { }

  
  public void fileOpened(OpenDefinitionsDocument doc) { }

  
  public void fileClosed(OpenDefinitionsDocument doc) { }

  
  public void fileReverted(OpenDefinitionsDocument doc) { }
  
  
  public void undoableEditHappened() { }

  
  public void compileStarted() { }

  
  public void compileEnded(File workDir) { }
  
  
  public void runStarted(OpenDefinitionsDocument doc) { }
  
  
  public void filePathContainsPound() { }
  
  
  public void compileBeforeJUnit() { }

  
  public void junitStarted() { }
  
  
  public void junitClassesStarted() {  }

  
  public void junitSuiteStarted(int numTests) { }
  
  
  public void junitTestStarted(String name) { }
  
  
  public void junitTestEnded(String name, boolean wasSuccessful, boolean causedError) { }
  
  
  public void junitEnded() { }

  
  
  
  public void interactionStarted() { }

  
  public void interactionEnded() { }
  
  
  public void interactionErrorOccurred(int offset, int length) { }

  
  public void interpreterResetting() { }
  
  
  public void interpreterReady(File wd) { }

  
  public void interpreterExited(int status) { }
  
  
  public void interpreterResetFailed(Throwable t) { }
  
  
  public void interpreterChanged(boolean inProgress) { }

  
  public void interactionIncomplete() { }
  
  
  public void slaveJVMUsed() { }

  
  
  
  public void consoleReset() { }
  
  
  public void saveBeforeCompile() { }

  public void saveUntitled() { }
  
  
  public void saveBeforeJavadoc() { }
  
  

  
  
  public void currentDirectoryChanged(File dir) { }
  
  
  public void nonTestCase(boolean isTestAll) { }
  
  
  public void classFileError(ClassFileError e) { }

  
  public boolean canAbandonFile(OpenDefinitionsDocument doc) { return true; }
  
  
  public void quitFile(OpenDefinitionsDocument doc) { }
  
  
  public boolean shouldRevertFile(OpenDefinitionsDocument doc) { return true; }

  
  public void javadocStarted() { }
  
  
  public void javadocEnded(boolean success, File destDir, boolean allDocs) { }
  
  public void activeDocumentChanged(OpenDefinitionsDocument active) { }
  
  public void focusOnDefinitionsPane() { }
  
  public void documentNotFound(OpenDefinitionsDocument d, File f) { }
}
