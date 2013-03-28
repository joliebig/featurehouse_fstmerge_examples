

package edu.rice.cs.drjava.model;

import java.io.File;
import edu.rice.cs.drjava.model.repl.InteractionsListener;
import edu.rice.cs.drjava.model.compiler.CompilerListener;
import edu.rice.cs.drjava.model.junit.JUnitListener;

import edu.rice.cs.util.FileOpenSelector;
import edu.rice.cs.util.swing.AsyncTask;


public interface GlobalModelListener extends InteractionsListener,
  JavadocListener, CompilerListener, JUnitListener {
  
	
	public <P,R> void executeAsyncTask(AsyncTask<P,R> task, P param, boolean showProgress, boolean lockUI);
	
  
  public void fileNotFound(File f);
  
  
  public void newFileCreated(OpenDefinitionsDocument doc);

  
  public void fileSaved(OpenDefinitionsDocument doc);

  
  public void fileOpened(OpenDefinitionsDocument doc);

  
  public void fileClosed(OpenDefinitionsDocument doc);

  
  public void fileReverted(OpenDefinitionsDocument doc);

  
  public boolean canAbandonFile(OpenDefinitionsDocument doc);

  
  public void quitFile(OpenDefinitionsDocument doc);
  
  
  public boolean shouldRevertFile(OpenDefinitionsDocument doc);

  
  public void runStarted(OpenDefinitionsDocument doc);

  
  public void consoleReset();

  
  public void undoableEditHappened();

  
  public void filePathContainsPound();
   
  
  public void activeDocumentChanged(OpenDefinitionsDocument active);
  
  
  public void focusOnDefinitionsPane();
  
  
  public void currentDirectoryChanged(File dir);
    
  
  public void projectBuildDirChanged();
  
  
  public void projectWorkDirChanged();
  
  
  public void projectOpened(File projectFile, FileOpenSelector files);

  
  public void projectClosed();

  
  public void projectModified();
  
  
  public void projectRunnableChanged();
  
  
  public void documentNotFound(OpenDefinitionsDocument d, File f);
}

