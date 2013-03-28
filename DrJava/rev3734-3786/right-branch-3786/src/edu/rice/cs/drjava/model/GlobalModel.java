

package edu.rice.cs.drjava.model;

import java.awt.print.PageFormat;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import edu.rice.cs.drjava.model.compiler.CompilerModel;
import edu.rice.cs.drjava.model.debug.Debugger;
import edu.rice.cs.drjava.model.definitions.DefinitionsEditorKit;
import edu.rice.cs.drjava.model.junit.JUnitModel;
import edu.rice.cs.drjava.model.repl.DefaultInteractionsModel;
import edu.rice.cs.drjava.model.repl.InteractionsDJDocument;
import edu.rice.cs.drjava.model.repl.InteractionsDocument;
import edu.rice.cs.drjava.model.repl.InteractionsScriptModel;
import edu.rice.cs.drjava.project.DocumentInfoGetter;
import edu.rice.cs.drjava.project.MalformedProjectFileException;
import edu.rice.cs.util.ClassPathVector;
import edu.rice.cs.util.FileOpenSelector;
import edu.rice.cs.util.OperationCanceledException;
import edu.rice.cs.util.docnavigation.IDocumentNavigator;
import edu.rice.cs.util.swing.DocumentIterator;
import edu.rice.cs.util.text.AbstractDocumentInterface;
import edu.rice.cs.util.text.ConsoleDocument;


public interface GlobalModel extends ILoadDocuments {

  

  
  public void addListener(GlobalModelListener listener);

  
  public void removeListener(GlobalModelListener listener);

  

  
  public DefaultInteractionsModel getInteractionsModel();

  
  public CompilerModel getCompilerModel();

  
  public JUnitModel getJUnitModel();

  
  public JavadocModel getJavadocModel();

  
  public Debugger getDebugger();
  
  
  public IDocumentNavigator<OpenDefinitionsDocument> getDocumentNavigator();
   
  public void setDocumentNavigator(IDocumentNavigator<OpenDefinitionsDocument> newnav);
  







  

  
  public OpenDefinitionsDocument newFile();

  
  public OpenDefinitionsDocument newTestCase(String name, boolean makeSetUp, boolean makeTearDown);

  
  public boolean closeFile(OpenDefinitionsDocument doc);
  
  
  public boolean closeFileWithoutPrompt(OpenDefinitionsDocument doc);

  
  public boolean closeAllFiles();

  
  public void openFolder(File dir, boolean rec) throws IOException, OperationCanceledException, AlreadyOpenException;

  
  public void saveAllFiles(FileSaveSelector com) throws IOException;
  
  
  public void createNewProject(File projFile);
  
  
  public void configNewProject() throws IOException;
  
  
  public void saveProject(File f, Hashtable<OpenDefinitionsDocument,DocumentInfoGetter> info) throws IOException;
  
  
  public String fixPathForNavigator(String path) throws IOException;

  
  public String getSourceBinTitle();
  
  
  public String getExternalBinTitle();
  
  
  public String getAuxiliaryBinTitle();
  
  
  public void addAuxiliaryFile(OpenDefinitionsDocument doc);
  
  
  public void removeAuxiliaryFile(OpenDefinitionsDocument doc);
  
  
  public File[] openProject(File file) throws IOException, MalformedProjectFileException;

  
  public void closeProject(boolean qutting);
  
  
  public File getSourceFile(String fileName);

  
  public File getSourceFileFromPaths(String fileName, List<File> paths);

  
  public File[] getSourceRootSet();

        
  public String getDisplayFilename(OpenDefinitionsDocument doc);
  
  
  public String getDisplayFullPath(int index);

  

  
  public DefinitionsEditorKit getEditorKit();

  
  public DocumentIterator getDocumentIterator();

  

  
  public ConsoleDocument getConsoleDocument();

  
  public InteractionsDJDocument getSwingConsoleDocument();

  
  public void resetConsole();

  
  public void systemOutPrint(String s);

  
  public void systemErrPrint(String s);

  

  
  public InteractionsDocument getInteractionsDocument();

  
  public InteractionsDJDocument getSwingInteractionsDocument();

  
  public void resetInteractions(File wd);

  
  public void waitForInterpreter();

  
  public void interpretCurrentInteraction();

  
  public ClassPathVector getClassPath();

  
  public ClassPathVector getExtraClassPath();
  
  
  public void setExtraClassPath(ClassPathVector cp);
  
  

  
  public void loadHistory(FileOpenSelector selector) throws IOException;

  
  public InteractionsScriptModel loadHistoryAsScript(FileOpenSelector selector)
    throws IOException, OperationCanceledException;

  
  public void clearHistory();

  
  public void saveHistory(FileSaveSelector selector) throws IOException;

  
  public void saveHistory(FileSaveSelector selector, String editedVersion) throws IOException;

  
  public String getHistoryAsStringWithSemicolons();

  
  public String getHistoryAsString();

  

  
  public void printDebugMessage(String s);

  
  public int getDebugPort() throws IOException;

  

  
  public PageFormat getPageFormat();

  
  public void setPageFormat(PageFormat format);

  
  public void quit();
  
  
  public int getDocumentCount();
  
  
  
  public OpenDefinitionsDocument getODDForDocument(AbstractDocumentInterface doc);
  
  
  public List<OpenDefinitionsDocument> getNonProjectDocuments();

  
  public List<OpenDefinitionsDocument> getProjectDocuments();
  
  
  public void compileAll() throws IOException;
  
  
  public boolean isProjectActive();
  
  
  public void junitAll();
  
  
  public File getProjectFile();
  
  
  public File[] getProjectFiles();

  
  public File getProjectRoot();
  
  
  public void setProjectFile(File f);
  
  
  public void setProjectRoot(File f);

  
  public File getBuildDirectory();
  
   
  public void setBuildDirectory(File f);
  
  
  public File getMasterWorkingDirectory();
  
  
  public File getWorkingDirectory();
  
   
  public void setWorkingDirectory(File f);

  
  public void setMainClass(File f);
  
  
  public File getMainClass();

  
  public void setCreateJarFile(File f);
  
  
  public File getCreateJarFile();

  
  public void setCreateJarFlags(int f);
  
  
  public int getCreateJarFlags();
  
  
  public boolean inProject(File f);
  
  
  public boolean isInProjectPath(OpenDefinitionsDocument doc);
  
  
  public void setProjectChanged(boolean changed);
  
  
  public boolean isProjectChanged();
  
  
  public boolean hasOutOfSyncDocuments();
  
  
  public void cleanBuildDirectory();
  
  
  public List<File> getClassFiles();
  
  
  public List<OpenDefinitionsDocument> getOpenDefinitionsDocuments();
  
  
   public boolean hasModifiedDocuments();
   
   
  public boolean hasUntitledDocuments();
  
   
   public OpenDefinitionsDocument getDocumentForFile(File file) throws IOException;
}
