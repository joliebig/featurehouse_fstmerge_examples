

package edu.rice.cs.drjava.model;

import edu.rice.cs.drjava.DrJava;

import edu.rice.cs.drjava.model.compiler.CompilerListener;
import edu.rice.cs.drjava.model.repl.InteractionsDocument;
import edu.rice.cs.drjava.model.junit.JUnitModel;
import edu.rice.cs.drjava.ui.InteractionsController;
import edu.rice.cs.util.FileOpenSelector;
import edu.rice.cs.plt.concurrent.CompletionMonitor;
import edu.rice.cs.plt.io.IOUtil;
import edu.rice.cs.util.FileOps;
import edu.rice.cs.util.Log;
import edu.rice.cs.util.OperationCanceledException;
import edu.rice.cs.util.StringOps;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.classloader.ClassFileError;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.util.swing.AsyncTask;
import edu.rice.cs.util.text.EditDocumentException;

import javax.swing.text.BadLocationException;
import java.io.File;
import java.io.IOException;
import java.rmi.UnmarshalException;
import java.util.regex.*;
import java.util.List;
import junit.framework.Assert;

import static edu.rice.cs.plt.debug.DebugUtil.debug;


public abstract class GlobalModelTestCase extends MultiThreadedTestCase {
  
  public static final Log _log  = new Log("GlobalModel.txt", false);

  protected volatile DefaultGlobalModel _model;
  protected volatile InteractionsController _interactionsController;
  protected volatile File _tempDir;
  protected volatile OpenDefinitionsDocument _doc;  

  protected static final String FOO_TEXT = "class DrJavaTestFoo {}";
  protected static final String BAR_TEXT = "class DrJavaTestBar {}";
  protected static final String BAZ_TEXT = "class DrJavaTestBaz extends DrJavaTestFoo { public static int x = 3; }";
  protected static final String FOO_MISSING_CLOSE_TEXT = "class DrJavaTestFoo {";
  protected static final String FOO_PACKAGE_AFTER_IMPORT = "import java.util.*;\npackage a;\n" + FOO_TEXT;
  protected static final String FOO_PACKAGE_INSIDE_CLASS = "class DrJavaTestFoo { package a; }";
  protected static final String FOO_PACKAGE_AS_FIELD = "class DrJavaTestFoo { int package; }";
  protected static final String FOO_PACKAGE_AS_FIELD_2 = "class DrJavaTestFoo { int package = 5; }";
  protected static final String FOO_PACKAGE_AS_PART_OF_FIELD = "class DrJavaTestFoo { int cur_package = 5; }";
  
  public GlobalModelTestCase() { _log.log("Constructing a " + this); }

  
  public void setUp() throws Exception {
    super.setUp();  
    Utilities.invokeAndWait(new Runnable() {
      public void run() {
        try {
          debug.logStart();
          _log.log("Setting up " + this);
          _model = new TestGlobalModel();
          
          
          
          _model.ensureJVMStarterFinished();
          
          _interactionsController =  
            new InteractionsController(_model.getInteractionsModel(), _model.getSwingInteractionsDocument(),
                                       new Runnable() { public void run() { } });
          _log.log("Global model created for " + this);
          DrJava.getConfig().resetToDefaults();
          String user = System.getProperty("user.name");
          
          _tempDir =  FileOps.createTempDirectory("DrJava-test-" + user );

          
          _model.setResetAfterCompile(false);
          _log.log("Completed (GlobalModelTestCase) set up of " + this);
          debug.logEnd();
          

        }
        catch(IOException e) {
          fail("IOException thrown with traceback: \n" + e);
        }
      }
    });
  }

  
  public void tearDown() throws Exception {
    debug.logStart();
    _log.log("Tearing down " + this);

    _model.dispose();
    
    
    
    _model.getInteractionsModel().removeAllInteractionListeners();

     IOUtil.deleteOnExitRecursively(_tempDir);
    

    _tempDir = null;
    _model = null;

    super.tearDown();
    _log.log("Completed tear down of " + this);
    debug.logEnd();

  }

  
  protected void changeDocumentText(final String s, final OpenDefinitionsDocument doc) {
    Utilities.invokeAndWait(new Runnable() {
      public void run() {
        try{
          doc.clear();
          assertLength(0, doc);
          doc.append(s, null);
          assertModified(true, doc);
          assertContents(s, doc);
        }
        catch(BadLocationException e) { throw new UnexpectedException(e); }
      }
    });
  }

  
  protected File tempFile() throws IOException {
    File f = File.createTempFile("DrJava-test", ".java", _tempDir).getCanonicalFile();

    return f;
  }

  
  protected File tempFile(int i) throws IOException {
    return File.createTempFile("DrJava-test" + i, ".java", _tempDir).getCanonicalFile();
  }

  
  protected File tempDirectory() throws IOException {
    return IOUtil.createAndMarkTempDirectory("DrJava-test", "", _tempDir);
  }

  protected File createFile(String name) { return new File(_tempDir, name); }

  
  protected File classForJava(File sourceFile, String className) {
    assertTrue(sourceFile.getName().endsWith(".java"));
    String cname = className + ".class";
    return new File(sourceFile.getParent(), cname);
  }

  
  protected File writeToNewTempFile(String text) throws IOException {
    File temp = tempFile();
    IOUtil.writeStringToFile(temp, text);
    return temp;
  }

  
  protected OpenDefinitionsDocument setupDocument(final String text) throws BadLocationException {
    TestListener listener = new TestListener() {
      public void newFileCreated(OpenDefinitionsDocument doc) { newCount++; }
    };

    _model.addListener(listener);

    
    int numOpen = _model.getOpenDefinitionsDocuments().size();
    
    
    Utilities.invokeAndWait(new Runnable() { public void run () { _doc = _model.newFile(); } });
    
    assertNumOpenDocs(numOpen + 1);

    listener.assertNewCount(1);
    assertLength(0, _doc);
    assertModified(false, _doc);

    Utilities.invokeAndWait(new Runnable() { public void run() { changeDocumentText(text, _doc); } });
    
    assertModified(true, _doc);
    _model.removeListener(listener); 

    return _doc;
  }
  
  protected void safeLoadHistory(final FileSelector fs) {
    Utilities.invokeAndWait(new Runnable() { public void run() { _model.loadHistory(fs); } });
  }
  
  protected void safeSaveHistory(final FileSelector fs) {
    Utilities.invokeAndWait(new Runnable() { 
      public void run() { 
        try {_model.saveHistory(fs); } 
        catch(IOException e) { throw new UnexpectedException(e); }
      }
    });
  }
                                
  
  protected static void testStartCompile(final OpenDefinitionsDocument doc) {
    Utilities.invokeLater(new Runnable() { 
      public void run() { 
        try { doc.startCompile(); }
        catch(IOException e) { throw new UnexpectedException(); }
      } 
    });
  }
  
  protected synchronized OpenDefinitionsDocument doCompile(String text, File file) throws IOException, 
    BadLocationException, InterruptedException {
    
    OpenDefinitionsDocument doc = setupDocument(text);
    doCompile(doc, file);
    return doc;
  }

  
  protected void doCompile(final OpenDefinitionsDocument doc, File file) throws IOException,  InterruptedException {
    saveFile(doc, new FileSelector(file));

    
    try { interpret("0"); }
    catch (EditDocumentException e) { throw new UnexpectedException(e); }
    Utilities.clearEventQueue();
    
    _model.setResetAfterCompile(true);
    CompileShouldSucceedListener listener = new CompileShouldSucceedListener();
    _model.addListener(listener);
    
    listener.logCompileStart();

    testStartCompile(doc);

    
    if (_model.getCompilerModel().getNumErrors() > 0)  fail("compile failed: " + getCompilerErrorString());

    listener.waitCompileDone();

    listener.checkCompileOccurred();
    assertCompileErrorsPresent(false);
    
    listener.waitResetDone();
    Utilities.clearEventQueue();
    _model.removeListener(listener);
  }

  
  protected String getCompilerErrorString() {
    final StringBuilder buf = new StringBuilder();
    buf.append(" compiler error(s):\n");
    buf.append(_model.getCompilerModel().getCompilerErrorModel().toString());
    return buf.toString();
  }

  
  protected String interpret(final String input) throws EditDocumentException {
    
    final InteractionsDocument interactionsDoc = _model.getInteractionsDocument();

    InteractionListener listener = new InteractionListener();
    _model.addListener(listener);
    
    
    Utilities.invokeAndWait(new Runnable() {
      public void run() {
        interactionsDoc.setInProgress(false);  
        interactionsDoc.append(input, InteractionsDocument.DEFAULT_STYLE);
      }
    });
    
    
    
    final int newLineLen = 1; 
    final int resultsStartLocation = interactionsDoc.getLength() + newLineLen;

    listener.logInteractionStart();
    
    
    Utilities.invokeLater(new Runnable() { public void run() { _model.interpretCurrentInteraction(); } });
    
    listener.waitInteractionDone();

    Utilities.clearEventQueue();
    _model.removeListener(listener);
    
    listener.assertInteractionStartCount(1);
    listener.assertInteractionEndCount(1);

    
    final int resultsEndLocation = interactionsDoc.getLength() - newLineLen - interactionsDoc.getPrompt().length();
    
    final int resultsLen = resultsEndLocation - resultsStartLocation;
    _log.log("resultsStartLoc = " + resultsStartLocation + " resultsEndLocation = " + resultsEndLocation);
    _log.log("Contents = '" + interactionsDoc.getDocText(0, resultsEndLocation+1) + "'");
    if (resultsLen <= 0) return "";
    return interactionsDoc.getDocText(resultsStartLocation, resultsLen);
  }

  
  protected void interpretIgnoreResult(String input) throws EditDocumentException {
    InteractionsDocument interactionsDoc = _model.getInteractionsDocument();
    interactionsDoc.append(input, InteractionsDocument.DEFAULT_STYLE);
    try { _model.interpretCurrentInteraction(); }
    catch(RuntimeException re) { 
      Throwable cause = re.getCause();
      if (! (cause instanceof UnmarshalException)) throw re; 
    }
  }

  
  protected void assertInteractionsContains(String text) throws EditDocumentException {
    _assertInteractionContainsHelper(text, true);
  }

  
  protected void assertInteractionsDoesNotContain(String text) throws EditDocumentException {
    _assertInteractionContainsHelper(text, false);
  }

  private void _assertInteractionContainsHelper(String text, boolean shouldContain) throws EditDocumentException {

    String interactText = getInteractionsText();
    int contains = interactText.lastIndexOf(text);
    assertTrue("Interactions document should " +
               (shouldContain ? "" : "not ")
                 + "contain:\n"
                 + text
                 + "\nActual contents of Interactions document:\n"
                 + interactText,
               (contains != -1) == shouldContain);
  }

  
  protected void assertInteractionsMatches(String regex) throws EditDocumentException {
    _assertInteractionMatchesHelper(regex, true);
  }

  
  protected void assertInteractionsDoesNotMatch(String regex)
    throws EditDocumentException {
    _assertInteractionMatchesHelper(regex, false);
  }
  
  private void _assertInteractionMatchesHelper(String regex, boolean shouldMatch) throws EditDocumentException {

    String interactText = getInteractionsText();
    boolean matches = Pattern.compile(regex, Pattern.MULTILINE|Pattern.DOTALL).matcher(interactText).matches();
    assertTrue("Interactions document should " +
               (shouldMatch ? "" : "not ")
                 + "match:\n"
                 + regex
                 + "\nActual contents of Interactions document:\n"
                 + interactText,
               matches == shouldMatch);
  }

  
  protected String getInteractionsText() throws EditDocumentException {
    InteractionsDocument doc = _model.getInteractionsDocument();
    return doc.getText();
  }

  protected void assertNumOpenDocs(int num) {
    assertEquals("number of open documents", num, _model.getOpenDefinitionsDocuments().size());
  }

  protected void assertModified(boolean b, OpenDefinitionsDocument doc) {
    assertEquals("document isModifiedSinceSave", b, doc.isModifiedSinceSave());
  }

  protected void assertLength(int len, OpenDefinitionsDocument doc) {
    assertEquals("document length", len, doc.getLength());
  }

  protected void assertContents(String s, OpenDefinitionsDocument doc) throws BadLocationException {
    assertEquals("document contents", s, doc.getText());
  }
  
    
  protected void saveFile(final OpenDefinitionsDocument doc, final FileSaveSelector fss) {
    Utilities.invokeAndWait(new Runnable() { 
      public void run() { 
        try { doc.saveFile(fss); }
        catch(Exception e) { throw new UnexpectedException(e); }
      } });
  }
  
  
  protected void saveFileAs(final OpenDefinitionsDocument doc, final FileSaveSelector fss) {
    Utilities.invokeAndWait(new Runnable() { 
      public void run() { 
        try { doc.saveFileAs(fss); }
        catch(Exception e) { throw new UnexpectedException(e); }
      } });
  }
  
  protected void saveAllFiles(final GlobalModel model, final FileSaveSelector fs) {
    Utilities.invokeAndWait(new Runnable() {
      public void run() { 
        try { model.saveAllFiles(fs); } 
        catch(Exception e) { throw new UnexpectedException(e); }
      }
    });
  }

  protected void assertCompileErrorsPresent(boolean b) { assertCompileErrorsPresent("", b); }

  protected void assertCompileErrorsPresent(String name, boolean b) {
    
    int numErrors = _model.getCompilerModel().getNumErrors();

    if (name.length() > 0)  name += ": ";

    
    
    
    

    assertEquals(name + "compile errors > 0? numErrors =" + numErrors, b, numErrors > 0);
  }

    
    
  public static class OverwriteException extends RuntimeException{ }
  public static class OpenWarningException extends RuntimeException{ }
  public static class FileMovedWarningException extends RuntimeException{ }

  public static class WarningFileSelector implements FileOpenSelector, FileSaveSelector {
    private volatile File _file;
    public WarningFileSelector(File f) { _file = f; }
    public File getFile() throws OperationCanceledException { return _file; }
    public File[] getFiles() throws OperationCanceledException { return new File[] {_file}; }
    public boolean warnFileOpen(File f) { throw new OpenWarningException(); }
    public boolean verifyOverwrite() { throw new OverwriteException(); }
    public boolean shouldSaveAfterFileMoved(OpenDefinitionsDocument doc, File oldFile) {
      throw new FileMovedWarningException();
    }
  }

  

  public static class FileSelector implements FileOpenSelector, FileSaveSelector {
    private volatile File _file1, _file2;
    public FileSelector(File f) { _file1 = f; }
    public FileSelector(File f1, File f2) {
      _file1 = f1;
      _file2 = f2;
    }

    public File getFile() throws OperationCanceledException { return _file1; }
    
    public File[] getFiles() throws OperationCanceledException {
      if (_file2 != null) return new File[] {_file1, _file2};
      else return new File[] {_file1};
    }
    public boolean warnFileOpen(File f) { return true; }
    public boolean verifyOverwrite() { return true; }
    public boolean shouldSaveAfterFileMoved(OpenDefinitionsDocument doc, File oldFile) { return true; }
  }

  public static class CancelingSelector implements FileOpenSelector, FileSaveSelector {
    public File getFile() throws OperationCanceledException { throw new OperationCanceledException(); }
    public File[] getFiles() throws OperationCanceledException { throw new OperationCanceledException(); }
    public boolean warnFileOpen(File f) { return true; }
    public boolean verifyOverwrite() {return true; }
    public boolean shouldSaveAfterFileMoved(OpenDefinitionsDocument doc, File oldFile) {  return true; }
  }

  
  public static class TestListener implements GlobalModelListener {
    
    protected volatile Exception _startupTrace;
    
    protected volatile boolean hasClearedEventQueue;
    protected volatile int fileNotFoundCount;
    protected volatile int newCount;
    protected volatile int openCount;
    protected volatile int closeCount;
    protected volatile int saveCount;
    protected volatile int canAbandonCount;
    protected volatile int quitFileCount;
    protected volatile int classFileErrorCount;
    protected volatile int compileStartCount;
    protected volatile int compileEndCount;
    protected volatile int activeCompilerChangedCount;
    protected volatile int runStartCount;
    protected volatile int junitStartCount;
    protected volatile int junitSuiteStartedCount;
    protected volatile int junitTestStartedCount;
    protected volatile int junitTestEndedCount;
    protected volatile int junitEndCount;
    protected volatile int interactionStartCount;
    protected volatile int interactionEndCount;
    protected volatile int interactionErrorCount;
    protected volatile int interpreterResettingCount;
    protected volatile int interpreterReadyCount;
    protected volatile int interpreterExitedCount;
    protected volatile int interpreterResetFailedCount;
    protected volatile int interpreterChangedCount;
    
    protected volatile int consoleResetCount;
    protected volatile int saveBeforeCompileCount;
    
    protected volatile int compileBeforeJUnitCount;
    protected volatile int saveBeforeJavadocCount;
    
    protected volatile int nonTestCaseCount;
    protected volatile int lastExitStatus;
    protected volatile int fileRevertedCount;
    protected volatile int shouldRevertFileCount;
    protected volatile int undoableEditCount;
    protected volatile int interactionIncompleteCount;
    protected volatile int filePathContainsPoundCount;

    public TestListener() {
      _startupTrace = new Exception();
      resetCounts();
    }

    public synchronized void resetCounts() {
      fileNotFoundCount = 0;
      newCount = 0;
      openCount = 0;
      closeCount = 0;
      saveCount = 0;
      canAbandonCount = 0;
      quitFileCount = 0;
      classFileErrorCount = 0;
      compileStartCount = 0;
      compileEndCount = 0;
      activeCompilerChangedCount = 0;
      runStartCount = 0;
      junitStartCount = 0;
      junitSuiteStartedCount = 0;
      junitTestStartedCount = 0;
      junitTestEndedCount = 0;
      junitEndCount = 0;
      interactionStartCount = 0;
      interactionEndCount = 0;
      interactionErrorCount = 0;
      interpreterChangedCount = 0;
      
      consoleResetCount = 0;
      interpreterResettingCount = 0;
      interpreterReadyCount = 0;
      interpreterExitedCount = 0;
      interpreterResetFailedCount = 0;
      saveBeforeCompileCount = 0;
      
      compileBeforeJUnitCount = 0;
      saveBeforeJavadocCount = 0;
      
      nonTestCaseCount = 0;
      lastExitStatus = 0;
      fileRevertedCount = 0;
      shouldRevertFileCount = 0;
      undoableEditCount = 0;
      interactionIncompleteCount = 0;
      filePathContainsPoundCount = 0;
      hasClearedEventQueue = false;
    }

    public void projectModified() { }
    public void openProject(File pfile, FileOpenSelector files) { }
    public void projectClosed() { }
    public void projectBuildDirChanged() { }
    public void projectWorkDirChanged() { }
    public void projectRunnableChanged() { }
    
    public void currentDirectoryChanged(File dir) { }
    
    
    public void listenerFail(String message) {
      String header = "\nTestListener creation stack trace:\n" + StringOps.getStackTrace(_startupTrace);
      MultiThreadedTestCase.listenerFail(message + header);
    }
    

























    public void assertFileNotFoundCount(int i) {
      assertEquals("number of times fileNotFound fired", i, fileNotFoundCount);
    }

    public void assertAbandonCount(int i) {
      assertEquals("number of times canAbandon fired", i, canAbandonCount);
    }

    public void assertQuitFileCount(int i) {
      assertEquals("number of times quitFile fired", i, quitFileCount);
    }
     public void assertClassFileErrorCount(int i) {
      assertEquals("number of times classFileError fired", i, classFileErrorCount);
    }
    public void assertNewCount(int i) {
      assertEquals("number of times newFile fired", i, newCount);
    }

    public void assertOpenCount(int i) {
      assertEquals("number of times openFile fired", i, openCount);
    }

    public void assertCloseCount(int i) {
      assertEquals("number of times closeFile fired", i, closeCount);
    }

    public void assertSaveCount(int i) {
      assertEquals("number of times saveFile fired", i, saveCount);
    }

    public void assertJUnitStartCount(int i) {
      assertEquals("number of times junitStarted fired", i, junitStartCount);
    }

    public void assertJUnitSuiteStartedCount(int i) {
      assertEquals("number of times junitSuiteStarted fired", i, junitSuiteStartedCount);
    }

    public void assertJUnitTestStartedCount(int i) {
      assertEquals("number of times junitTestStarted fired", i, junitTestStartedCount);
    }

    public void assertJUnitTestEndedCount(int i) {
      assertEquals("number of times junitTestEnded fired", i, junitTestEndedCount);
    }

    public void assertJUnitEndCount(int i) {
      assertEquals("number of times junitEnded fired", i, junitEndCount);
    }

    public void assertInteractionStartCount(int i) {
      assertEquals("number of times interactionStarted fired", i, interactionStartCount);
    }

    public void assertInteractionEndCount(int i) {
      assertEquals("number of times interactionEnded fired", i, interactionEndCount);
    }

    public void assertInteractionErrorCount(int i) {
      assertEquals("number of times interactionError fired", i, interactionErrorCount );
    }

    public void assertInterpreterChangedCount(int i) {
      assertEquals("number of times interpreterChanged fired", i, interpreterChangedCount);
    }






    public void assertCompileStartCount(int i) {
      assertEquals("number of times compileStarted fired", i, compileStartCount);
    }

    public void assertCompileEndCount(int i) {
      assertEquals("number of times compileEnded fired", i, compileEndCount);
    }

    public void assertActiveCompilerChangedCount(int i) {
      assertEquals("number of times activeCompilerChanged fired", i, activeCompilerChangedCount);
    }

    public void assertRunStartCount(int i) {
      assertEquals("number of times prepareForRun fired", i, runStartCount);
    }

    public void assertInterpreterResettingCount(int i) {
      assertEquals("number of times interpreterResetting fired", i, interpreterResettingCount);
    }

    public void assertInterpreterReadyCount(int i) {
      assertEquals("number of times interpreterReady fired", i, interpreterReadyCount);
    }

    public void assertInterpreterResetFailedCount(int i) {
      assertEquals("number of times interpreterResetFailed fired", i, interpreterResetFailedCount);
    }

    public void assertInterpreterExitedCount(int i) {
      assertEquals("number of times interpreterExited fired", i, interpreterExitedCount);
    }

    public void assertInteractionsErrorCount(int i) {
      assertEquals("number of times interactionsError fired", i, interactionErrorCount);
    }

    public void assertConsoleResetCount(int i) {
      assertEquals("number of times consoleReset fired", i, consoleResetCount);
    }

    public void assertSaveBeforeCompileCount(int i) {
      assertEquals("number of times saveBeforeCompile fired", i, saveBeforeCompileCount);
    }






    public void assertCompileBeforeJUnitCount(int i) {
      assertEquals("number of times compileBeforeJUnit fired", i, compileBeforeJUnitCount);
    }

    public void assertSaveBeforeJavadocCount(int i) {
      assertEquals("number of times saveBeforeJavadoc fired", i, saveBeforeJavadocCount);
    }








    public void assertNonTestCaseCount(int i) {
      assertEquals("number of times nonTestCase fired", i,  nonTestCaseCount);
    }

    public void assertFileRevertedCount(int i) {
      assertEquals("number of times fileReverted fired", i, fileRevertedCount);
    }

    public void assertUndoableEditCount(int i) {
      assertEquals("number of times undoableEditHappened fired", i, undoableEditCount);
    }

    public void assertShouldRevertFileCount(int i) {
      assertEquals("number of times shouldRevertFile fired", i, shouldRevertFileCount);
    }

    public void assertInteractionIncompleteCount(int i) {
      assertEquals("number of times interactionIncomplete fired", i, interactionIncompleteCount);
    }

    public <P,R> void executeAsyncTask(AsyncTask<P,R> task, P param, boolean showProgress, boolean lockUI) {  
      listenerFail("executeAswyncTask fired unexpectedly");
    }
       
    public void handleAlreadyOpenDocument(OpenDefinitionsDocument doc) {
      listenerFail("handleAlreadyOpenDocument fired unexpectedly");
    }
      
    public void newFileCreated(OpenDefinitionsDocument doc) { listenerFail("newFileCreated fired unexpectedly"); } 
    public void filesNotFound(File... f) { listenerFail("fileNotFound fired unexpectedly"); }
    public File[] filesReadOnly(File... f) { listenerFail("filesReadOnly fired unexpectedly"); return f; }
    
    public void fileOpened(OpenDefinitionsDocument doc) {  }
    public void fileClosed(OpenDefinitionsDocument doc) { listenerFail("fileClosed fired unexpectedly"); }
    public void fileSaved(OpenDefinitionsDocument doc) { listenerFail("fileSaved fired unexpectedly"); }
    public void fileReverted(OpenDefinitionsDocument doc) { listenerFail("fileReverted fired unexpectedly"); }
    public void undoableEditHappened() { listenerFail("undoableEditHappened fired unexpectedly"); }
    public void saveBeforeCompile() { listenerFail("saveBeforeCompile fired unexpectedly"); }
    
    public void junitStarted() { listenerFail("junitStarted fired unexpectedly"); }
    public void junitClassesStarted() { listenerFail("junitAllStarted fired unexpectedly"); }
    public void junitSuiteStarted(int numTests) { listenerFail("junitSuiteStarted fired unexpectedly"); }
    public void junitTestStarted(String name) { listenerFail("junitTestStarted fired unexpectedly"); }
    public void junitTestEnded(String name, boolean wasSuccessful, boolean causedError) {
      listenerFail("junitTestEnded fired unexpectedly");
    }
    public void junitEnded() { listenerFail("junitEnded fired unexpectedly"); }
    
    public void javadocStarted() { listenerFail("javadocStarted fired unexpectedly"); }
    public void javadocEnded(boolean success, File destDir, boolean allDocs) {
      listenerFail("javadocEnded fired unexpectedly");
    }

    public void interactionStarted() { listenerFail("interactionStarted fired unexpectedly"); }
    public void interactionEnded() { listenerFail("interactionEnded fired unexpectedly"); }
    public void interactionErrorOccurred(int offset, int length) {
      listenerFail("interpreterErrorOccurred fired unexpectedly");
    }

    public void interpreterChanged(boolean inProgress) { listenerFail("interpreterChanged fired unexpectedly"); }






    public void compileStarted() { listenerFail("compileStarted fired unexpectedly"); }
    public void compileEnded(File workDir, List<? extends File> excludedFiles) { 
      listenerFail("compileEnded fired unexpectedly"); 
    }
    public void compileAborted(Exception e) { listenerFail("compileAborted fired unexpectedly"); }
    public void activeCompilerChanged() { listenerFail("activeCompilerChanged fired unexpectedly"); }

    public void prepareForRun(OpenDefinitionsDocument doc) { listenerFail("prepareForRun fired unexpectedly"); }
    
    public void interpreterResetting() { listenerFail("interpreterResetting fired unexpectedly"); }

    public void interpreterReady(File wd) { listenerFail("interpreterReady fired unexpectedly");  }
    public void interpreterExited(int status) {
      listenerFail("interpreterExited(" + status + ") fired unexpectedly");
    }
    public void interpreterResetFailed(Throwable t) { listenerFail("interpreterResetFailed fired unexpectedly"); }
    public void consoleReset() { listenerFail("consoleReset fired unexpectedly"); }
    public void saveUntitled() { listenerFail("saveUntitled fired unexpectedly"); }
    
    public void compileBeforeJUnit(CompilerListener cl, List<OpenDefinitionsDocument> outOfSync) { compileBeforeJUnitCount++; }

    public void saveBeforeJavadoc() { listenerFail("saveBeforeJavadoc fired unexpectedly"); }
    public void nonTestCase(boolean isTestAll, boolean didCompileFail) { listenerFail("nonTestCase fired unexpectedly"); }

    public boolean canAbandonFile(OpenDefinitionsDocument doc) {
      listenerFail("canAbandonFile fired unexpectedly");
      throw new UnexpectedException();
    }
    
    public boolean quitFile(OpenDefinitionsDocument doc) {
      listenerFail("quitFile fired unexpectedly");
      throw new UnexpectedException();
    }
    
    public void classFileError(ClassFileError e) {
      listenerFail("classFileError fired unexpectedly");
    }
    
    public boolean shouldRevertFile(OpenDefinitionsDocument doc) {
      listenerFail("shouldRevertfile fired unexpectedly");
      throw new UnexpectedException();
    }

    public void interactionIncomplete() { listenerFail("interactionIncomplete fired unexpectedly"); }
    public void filePathContainsPound() { listenerFail("filePathContainsPound fired unexpectedly"); }
    
    public void documentNotFound(OpenDefinitionsDocument d, File f) {
      listenerFail("documentNotFound fired unexpectedly");
    }
    
    public void activeDocumentChanged(OpenDefinitionsDocument active) {  }
    public void activeDocumentRefreshed(OpenDefinitionsDocument active) {  }    
    public void focusOnDefinitionsPane() {   }
    public void focusOnLastFocusOwner() {   }
    public void browserChanged() {  }
    public void updateCurrentLocationInDoc() {  }
  }
  
  public static class InteractionListener extends TestListener {
    private static final int WAIT_TIMEOUT = 20000; 
    private CompletionMonitor _interactionDone;
    private CompletionMonitor _resetDone;
    
    private volatile int _lastExitStatus = -1;
    
    public InteractionListener() {
      _interactionDone = new CompletionMonitor();
      _resetDone = new CompletionMonitor();
    }
    
    public synchronized void interactionStarted() { interactionStartCount++; }
    
    public void interactionEnded() {

      
      synchronized(this) { interactionEndCount++; }
      _interactionDone.signal();
    }
    
    public void interpreterExited(int status) {



      synchronized(this) { 
        interpreterExitedCount++;
        _lastExitStatus = status;
      }
      _interactionDone.signal();
    }
    
    public void interpreterResetting() {
      assertInterpreterResettingCount(0);
      assertInterpreterReadyCount(0);
      synchronized(this) { interpreterResettingCount++; }
    }
    
    public void interpreterReady(File wd) {

      synchronized(this) { interpreterReadyCount++; }
      _resetDone.signal();
    }
    
    public void consoleReset() {
      assertConsoleResetCount(0);


      
      synchronized(this) { consoleResetCount++; }
    }
    
    public void resetConsoleResetCount() { consoleResetCount = 0; }
    
    public void logInteractionStart() {
      _interactionDone.reset();
      _resetDone.reset();
    }
    
    public void waitInteractionDone() {
      assertTrue("Interaction did not complete before timeout",
                 _interactionDone.attemptEnsureSignaled(WAIT_TIMEOUT));
    }
    
    public void waitResetDone() throws InterruptedException {
      assertTrue("Reset did not complete before timeout",
                 _resetDone.attemptEnsureSignaled(WAIT_TIMEOUT));
    }
    
    public int getLastExitStatus() { return _lastExitStatus; }
  };
  
  
  
  public static class CompileShouldSucceedListener extends InteractionListener {
    
    private volatile boolean _compileDone = false;        
    private final Object _compileLock = new Object();     
    
    public void logCompileStart() { 
      logInteractionStart();
      _compileDone = false; 
    }
    
    public void compile(OpenDefinitionsDocument doc) throws IOException, InterruptedException {
      logCompileStart();
      testStartCompile(doc);
      waitCompileDone();
    }
    
    public void waitCompileDone() throws InterruptedException {
      synchronized(_compileLock) {
        while (! _compileDone) {

          _compileLock.wait();
        }
      }
    }
  
    private void _notifyCompileDone() {
      synchronized(_compileLock) {
        _compileDone = true;  
        _compileLock.notifyAll();
      }
    }
    
    @Override public void newFileCreated(OpenDefinitionsDocument doc) {  }
    
    @Override public void compileStarted() {

      assertCompileStartCount(0);
      assertCompileEndCount(0);
      assertActiveCompilerChangedCount(0);
      assertInterpreterResettingCount(0);
      assertInterpreterReadyCount(0);
      assertConsoleResetCount(0);
      synchronized(this) { compileStartCount++; }
    }
    
    @Override public void compileEnded(File workDir, List<? extends File> excludedFiles) {

      assertCompileEndCount(0);
      assertActiveCompilerChangedCount(0);
      assertCompileStartCount(1);
      assertInterpreterResettingCount(0);
      assertInterpreterReadyCount(0);
      assertConsoleResetCount(0);
      synchronized(this) { compileEndCount++; }
      _notifyCompileDone();
    }
    
    @Override public void compileAborted(Exception e) {
      _notifyCompileDone();
    }

    @Override public void activeCompilerChanged() {

      synchronized(this) { activeCompilerChangedCount++; }
      _notifyCompileDone();
    }
    
    public void checkCompileOccurred() {
      assertCompileEndCount(1);
      assertActiveCompilerChangedCount(0);
      assertCompileStartCount(1);
    }
  }
    
  
  public static class CompileShouldFailListener extends TestListener {
    
    private volatile boolean _compileDone = false;        
    private final Object _compileLock = new Object();     
    
    public void logCompileStart() {
      synchronized(_compileLock) { _compileDone = false; }
    }
    
    public void waitCompileDone() throws InterruptedException {
      synchronized(_compileLock) {
        while (! _compileDone) {

          _compileLock.wait();
        }
      }
    }
    
    public void compile(OpenDefinitionsDocument doc) throws IOException, InterruptedException {
      logCompileStart();
      testStartCompile(doc);
      waitCompileDone();
    }
    
    private void _notifyCompileDone() {
      synchronized(_compileLock) {
        _compileDone = true;
        _compileLock.notify();
      }
    }
    
    @Override public void compileStarted() {
      assertCompileStartCount(0);
      assertCompileEndCount(0);
      assertActiveCompilerChangedCount(0);
      synchronized(this) { compileStartCount++; }
    }
    
    @Override public void compileEnded(File workDir, List<? extends File> excludedFiles) {
      assertCompileEndCount(0);
      assertActiveCompilerChangedCount(0);
      assertCompileStartCount(1);
      assertInterpreterResettingCount(0);
      assertInterpreterReadyCount(0);
      assertConsoleResetCount(0);
      synchronized(this) { compileEndCount++; }
      _notifyCompileDone();
    }
    
    @Override public void compileAborted(Exception e) {
      _notifyCompileDone();
    }

    
    public void checkCompileOccurred() {
      assertCompileEndCount(1);
      assertCompileStartCount(1);
      assertActiveCompilerChangedCount(0);
    }
    
  }
  
  public static class JUnitTestListener extends CompileShouldSucceedListener {
    
    protected volatile boolean _junitDone = false;
    protected final Object _junitLock = new Object();
    
    
    protected volatile boolean printMessages = GlobalModelJUnitTest.printMessages;
    
    
    public JUnitTestListener() { this(false);  }
    public JUnitTestListener(boolean printListenerMessages) {
      this.printMessages = printListenerMessages;
    }
    
    public void logJUnitStart() { 
      logCompileStart();
      _junitDone = false; 
    }
    
    
    public void runJUnit(OpenDefinitionsDocument doc) throws IOException, ClassNotFoundException, 
      InterruptedException {
      logJUnitStart();

      doc.startJUnit();

      waitJUnitDone();
    }
    
    public void runJUnit(JUnitModel jm) throws IOException, ClassNotFoundException, InterruptedException {  
      logJUnitStart();

      jm.junitAll();
      waitJUnitDone();
    }
    
    public void waitJUnitDone() throws InterruptedException {
      synchronized(_junitLock) { while (! _junitDone) { _junitLock.wait(); } }
    }
    
    private void _notifyJUnitDone() {
      synchronized(_junitLock) {
        _junitDone = true;
        _junitLock.notify();
      }
    }
    
    public void resetCompileCounts() { 
      compileStartCount = 0; 
      compileEndCount = 0;
      activeCompilerChangedCount = 0;
    }
    
    public void resetJUnitCounts() { 
      junitStartCount = 0;
      junitSuiteStartedCount = 0;
      junitTestStartedCount = 0;
      junitTestEndedCount = 0;
      junitEndCount = 0;
    }
     
    @Override public void junitStarted() {
      if (printMessages) System.out.println("listener.junitStarted");
      synchronized(this) { junitStartCount++; }
    }
    @Override public void junitSuiteStarted(int numTests) {
      if (printMessages) System.out.println("listener.junitSuiteStarted, numTests = "+numTests);
      assertJUnitStartCount(1);
      synchronized(this) { junitSuiteStartedCount++; }
    }
    @Override public void junitTestStarted(String name) {
      if (printMessages) System.out.println("  listener.junitTestStarted, " + name);
      synchronized(this) { junitTestStartedCount++; }
    }
    @Override public void junitTestEnded(String name, boolean wasSuccessful, boolean causedError) {
      if (printMessages) System.out.println("  listener.junitTestEnded, name = " + name + " succ = " + wasSuccessful + 
                                            " err = " + causedError);
      synchronized(this) { junitTestEndedCount++; }
      assertEquals("junitTestEndedCount should be same as junitTestStartedCount", junitTestEndedCount, 
                   junitTestStartedCount);
    }
    @Override public void nonTestCase(boolean isTestAll, boolean didCompileFail) {
      if (printMessages) System.out.println("listener.nonTestCase, isTestAll=" + isTestAll);
      synchronized(this) { nonTestCaseCount++; }
      _log.log("nonTestCase() called; notifying JUnitDone");
      _notifyJUnitDone();
    }
    @Override public void classFileError(ClassFileError e) {
      if (printMessages) System.out.println("listener.classFileError, e="+e);
      synchronized(this) { classFileErrorCount++; }
      _log.log("classFileError() called; notifying JUnitDone");
      _notifyJUnitDone();
    }
    @Override public void junitEnded() {
      
      if (printMessages) System.out.println("junitEnded event!");
      synchronized(this) { junitEndCount++; }
      _log.log("junitEnded() called; notifying JUnitDone");
      _notifyJUnitDone();
    }
  }
  
  
  public static class JUnitNonTestListener extends JUnitTestListener {
    private volatile boolean _shouldBeTestAll;
    public JUnitNonTestListener() {  this(false); }
    public JUnitNonTestListener(boolean shouldBeTestAll) { _shouldBeTestAll = shouldBeTestAll; }
    public void nonTestCase(boolean isTestAll, boolean didCompileFail) {
      synchronized(this) { nonTestCaseCount++; }
      assertEquals("Non test case heard the wrong value for test current/test all", _shouldBeTestAll, isTestAll);

      synchronized(_junitLock) {

        _junitDone = true;
        _junitLock.notify();
      }
    }
  }
  
  
   public static class TestGlobalModel extends DefaultGlobalModel {
    public File getWorkingDirectory() { return getMasterWorkingDirectory(); }
  } 
}
