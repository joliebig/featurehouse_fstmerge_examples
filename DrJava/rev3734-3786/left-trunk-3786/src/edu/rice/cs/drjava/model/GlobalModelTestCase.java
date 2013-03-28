

package edu.rice.cs.drjava.model;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.model.compiler.CompilerListener;
import edu.rice.cs.drjava.model.repl.InteractionsDocument;
import edu.rice.cs.util.FileOpenSelector;
import edu.rice.cs.util.FileOps;
import edu.rice.cs.util.OperationCanceledException;
import edu.rice.cs.util.StringOps;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.classloader.ClassFileError;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.util.text.EditDocumentException;
import edu.rice.cs.util.text.EditDocumentInterface;

import javax.swing.text.BadLocationException;
import java.io.File;
import java.io.IOException;
import java.util.regex.*;


public abstract class GlobalModelTestCase extends MultiThreadedTestCase {

  protected DefaultGlobalModel _model;
  protected File _tempDir;
  
  protected volatile boolean _junitDone;
  protected final Object _junitLock = new Object();
  
  protected void _logJUnitStart() { _junitDone = false; }
  
  protected void _runJUnit(OpenDefinitionsDocument doc) throws IOException, ClassNotFoundException, 
    InterruptedException {

    _logJUnitStart();

    doc.startJUnit();

    _waitJUnitDone();
  }
  
  protected void _runJUnit() throws IOException, ClassNotFoundException, InterruptedException {  
    _logJUnitStart();

    _model.getJUnitModel().junitAll();
    _waitJUnitDone();
  }
   
  protected void _waitJUnitDone() throws InterruptedException {
    synchronized(_junitLock) {
      while (!_junitDone) {

        _junitLock.wait();
      }
    }
  }
  
  protected static final String FOO_TEXT = "class DrJavaTestFoo {}";
  protected static final String BAR_TEXT = "class DrJavaTestBar {}";
  protected static final String BAZ_TEXT = "class DrJavaTestBaz extends DrJavaTestFoo { public static int x = 3; }";
  protected static final String FOO_MISSING_CLOSE_TEXT = "class DrJavaTestFoo {";
  protected static final String FOO_PACKAGE_AFTER_IMPORT = "import java.util.*;\npackage a;\n" + FOO_TEXT;
  protected static final String FOO_PACKAGE_INSIDE_CLASS = "class DrJavaTestFoo { package a; }";
  protected static final String FOO_PACKAGE_AS_FIELD = "class DrJavaTestFoo { int package; }";
  protected static final String FOO_PACKAGE_AS_FIELD_2 = "class DrJavaTestFoo { int package = 5; }";
  protected static final String FOO_PACKAGE_AS_PART_OF_FIELD = "class DrJavaTestFoo { int cur_package = 5; }";

  
  public void setUp() throws Exception {
    super.setUp();
    DrJava.getConfig().resetToDefaults();
    createModel();
    _model.setResetAfterCompile(false);
    String user = System.getProperty("user.name");
    _tempDir = FileOps.createTempDirectory("DrJava-test-" + user);



  }

  
  public void tearDown() throws Exception {
    boolean ret = FileOps.deleteDirectory(_tempDir);
    assertTrue("delete temp directory " + _tempDir, ret);

    _model.dispose();
    _tempDir = null;
    _model = null;

    super.tearDown();
  }

  
  protected void createModel() {
    
    _model = new TestGlobalModel();

    
    _model._jvm.ensureInterpreterConnected();
    
  }

  
  protected void changeDocumentText(String s, OpenDefinitionsDocument doc) throws BadLocationException {
    doc.clear();
    assertLength(0, doc);
    doc.insertString(0, s, null);
    assertModified(true, doc);
    assertContents(s, doc);
  }

  
  protected File tempFile() throws IOException {
    File f = File.createTempFile("DrJava-test", ".java", _tempDir).getCanonicalFile();

    return f;
  }

  
  protected File tempFile(int i) throws IOException {
    return File.createTempFile("DrJava-test" + i, ".java", _tempDir).getCanonicalFile();
  }

  
  protected File tempDirectory() throws IOException {
    return FileOps.createTempDirectory("DrJava-test", _tempDir);
  }

  protected File createFile(String name) { return new File(_tempDir, name); }

  
  protected File classForJava(File sourceFile, String className) {
    assertTrue(sourceFile.getName().endsWith(".java"));
    String cname = className + ".class";
    return new File(sourceFile.getParent(), cname);
  }

  
  protected File writeToNewTempFile(String text) throws IOException {
    File temp = tempFile();
    FileOps.writeStringToFile(temp, text);
    return temp;
  }


  
  protected OpenDefinitionsDocument setupDocument(String text) throws BadLocationException {
    TestListener listener = new TestListener() {
      public void newFileCreated(OpenDefinitionsDocument doc) { newCount++; }
    };

    _model.addListener(listener);

    
    int numOpen = _model.getOpenDefinitionsDocuments().size();
    OpenDefinitionsDocument doc = _model.newFile();
    assertNumOpenDocs(numOpen + 1);

    listener.assertNewCount(1);
    assertLength(0, doc);
    assertModified(false, doc);

    changeDocumentText(text, doc);
    assertModified(true, doc);
    _model.removeListener(listener); 

    return doc;
  }

  
  protected synchronized OpenDefinitionsDocument doCompile(String text, File file) throws IOException, 
    BadLocationException, InterruptedException {
    
    OpenDefinitionsDocument doc = setupDocument(text);
    doCompile(doc, file);
    return doc;
  }

  
  protected void doCompile(OpenDefinitionsDocument doc, File file) throws IOException, 
    InterruptedException {
    doc.saveFile(new FileSelector(file));

    
    
    try { interpret("2+2"); }
    catch (EditDocumentException e) {
      throw new UnexpectedException(e);
    }

    CompileShouldSucceedListener listener = new CompileShouldSucceedListener(true);
    _model.setResetAfterCompile(true);
    _model.addListener(listener);
    synchronized(listener) {
      doc.startCompile();
      if (_model.getCompilerModel().getNumErrors() > 0) {
        fail("compile failed: " + getCompilerErrorString());
      }
      while (listener.notDone()) listener.wait();
    }
    listener.checkCompileOccurred();
    assertCompileErrorsPresent(false);
    _model.removeListener(listener);
  }

  
  protected String getCompilerErrorString() {
    StringBuffer buf = new StringBuffer();
    buf.append(" compiler error(s):\n");
    buf.append(_model.getCompilerModel().getCompilerErrorModel().toString());
    return buf.toString();
  }

  
  protected String interpret(String input) throws EditDocumentException {
    InteractionsDocument interactionsDoc = _model.getInteractionsDocument();
    interactionsDoc.insertText(interactionsDoc.getLength(), input, InteractionsDocument.DEFAULT_STYLE);

    
    int newLineLen = System.getProperty("line.separator").length();
    final int resultsStartLocation = interactionsDoc.getLength() + newLineLen;

    TestListener listener = new TestListener() {
      public void interactionStarted() { interactionStartCount++; }

      public void interactionEnded() {
        assertInteractionStartCount(1);

        synchronized(this) {
          interactionEndCount++;
          this.notify();
        }
      }

    };

    _model.addListener(listener);
    try {
      synchronized(listener) {
        _model.interpretCurrentInteraction();
        listener.wait();  
        
 
 
 
 
 
      }
    }
    catch (InterruptedException ie) {
      throw new UnexpectedException(ie);
    }
    _model.removeListener(listener);
    listener.assertInteractionStartCount(1);
    listener.assertInteractionEndCount(1);

    
    final int resultsEndLocation = interactionsDoc.getLength() - newLineLen -
                                   interactionsDoc.getPrompt().length();

    final int resultsLen = resultsEndLocation - resultsStartLocation;
    
    
    if (resultsLen <= 0)
      return "";
    return interactionsDoc.getDocText(resultsStartLocation, resultsLen);
  }

  protected void interpretIgnoreResult(String input) throws EditDocumentException {
    EditDocumentInterface interactionsDoc = _model.getInteractionsDocument();
    interactionsDoc.insertText(interactionsDoc.getLength(), input, InteractionsDocument.DEFAULT_STYLE);

    _model.interpretCurrentInteraction();
  }

  
  protected void assertInteractionsContains(String text) throws EditDocumentException {
    _assertInteractionContainsHelper(text, true);
  }

  
  protected void assertInteractionsDoesNotContain(String text)
    throws EditDocumentException {
    _assertInteractionContainsHelper(text, false);
  }

  private void _assertInteractionContainsHelper(String text, boolean shouldContain)
    throws EditDocumentException {

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
  
  private void _assertInteractionMatchesHelper(String regex, boolean shouldMatch)
    throws EditDocumentException {

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
    EditDocumentInterface doc = _model.getInteractionsDocument();
    return doc.getDocText(0, doc.getLength());
  }



  protected void assertNumOpenDocs(int num) {
    assertEquals("number of open documents",
                 num,
                 _model.getOpenDefinitionsDocuments().size());
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
    private File _file;
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
    private File _file, _file2;
    public FileSelector(File f) { _file = f; }
    public FileSelector(File f1, File f2) {
      _file = f1;
      _file2 = f2;
    }

    public File getFile() throws OperationCanceledException { return _file; }
    
    public File[] getFiles() throws OperationCanceledException {
      if (_file2 != null) return new File[] {_file, _file2};
      else return new File[] {_file};
    }
    public boolean warnFileOpen(File f) { return true; }
    public boolean verifyOverwrite() { return true; }
    public boolean shouldSaveAfterFileMoved(OpenDefinitionsDocument doc, File oldFile) {
      return true;
    }
  }

  public static class CancelingSelector implements FileOpenSelector, FileSaveSelector {
    public File getFile() throws OperationCanceledException {
      throw new OperationCanceledException();
    }
    public File[] getFiles() throws OperationCanceledException {
      throw new OperationCanceledException();
    }
    public boolean warnFileOpen(File f) { return true; }
    public boolean verifyOverwrite() {return true; }
    public boolean shouldSaveAfterFileMoved(OpenDefinitionsDocument doc, File oldFile) {  return true; }
  }

  
  public static class TestListener implements GlobalModelListener {
    
    protected Exception _startupTrace;
    protected int fileNotFoundCount;
    protected int newCount;
    protected int openCount;
    protected int closeCount;
    protected int saveCount;
    protected int canAbandonCount;
    protected int quitFileCount;
    protected int classFileErrorCount;
    protected int compileStartCount;
    protected int compileEndCount;
    protected int runStartCount;
    protected int junitStartCount;
    protected int junitSuiteStartedCount;
    protected int junitTestStartedCount;
    protected int junitTestEndedCount;
    protected int junitEndCount;
    protected int interactionStartCount;
    protected int interactionEndCount;
    protected int interactionErrorCount;
    protected int interpreterResettingCount;
    protected int interpreterReadyCount;
    protected int interpreterExitedCount;
    protected int interpreterResetFailedCount;
    protected int interpreterChangedCount;
    
    protected int consoleResetCount;
    protected int saveBeforeCompileCount;
    
    protected int compileBeforeJUnitCount;
    protected int saveBeforeJavadocCount;
    
    protected int nonTestCaseCount;
    protected int lastExitStatus;
    protected int fileRevertedCount;
    protected int shouldRevertFileCount;
    protected int undoableEditCount;
    protected int interactionIncompleteCount;
    protected int filePathContainsPoundCount;

    public TestListener() {
      _startupTrace = new Exception();
      resetCounts();
    }

    public void resetCounts() {
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
    }

    public void projectModified() { }
    public void projectOpened(File pfile, FileOpenSelector files) { }
    public void projectClosed() { }
    public void projectBuildDirChanged() { }
    public void projectWorkDirChanged() { }
    public void projectRunnableChanged() { }
    
    public void currentDirectoryChanged(File dir) { }
    
    
    public void listenerFail(String message) {
      String header = "\nTestListener creation stack trace:\n" +
        StringOps.getStackTrace(_startupTrace);
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

    public void assertRunStartCount(int i) {
      assertEquals("number of times runStarted fired", i, runStartCount);
    }

    public void assertInterpreterResettingCount(int i) {
      assertEquals("number of times interactionsResetting fired", i, interpreterResettingCount);
    }

    public void assertInterpreterReadyCount(int i) {
      assertEquals("number of times interactionsReset fired", i, interpreterReadyCount);
    }

    public void assertInterpreterResetFailedCount(int i) {
      assertEquals("number of times interactionsResetFailed fired", i, interpreterResetFailedCount);
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

    public void newFileCreated(OpenDefinitionsDocument doc) {
      listenerFail("newFileCreated fired unexpectedly");
    }
    
    public void fileNotFound(File f) {
      listenerFail("fileNotFound fired unexpectedly");
    }
    
    public void fileOpened(OpenDefinitionsDocument doc) {
       listenerFail("fileOpened fired unexpectedly");  
    }

    public void fileClosed(OpenDefinitionsDocument doc) {
      listenerFail("fileClosed fired unexpectedly");
    }

    public void fileSaved(OpenDefinitionsDocument doc) {
      listenerFail("fileSaved fired unexpectedly");
    }

    public void fileReverted(OpenDefinitionsDocument doc) {
      listenerFail("fileReverted fired unexpectedly");
    }
    
    public void undoableEditHappened() {
      listenerFail("undoableEditHappened fired unexpectedly");
    }
    
    public void saveBeforeCompile() {
      listenerFail("saveBeforeCompile fired unexpectedly");
    }

    public void junitStarted() {
      listenerFail("junitStarted fired unexpectedly");
    }

    public void junitClassesStarted() {
      listenerFail("junitAllStarted fired unexpectedly");
    }

    public void junitSuiteStarted(int numTests) {
      listenerFail("junitSuiteStarted fired unexpectedly");
    }

    public void junitTestStarted(String name) {
      listenerFail("junitTestStarted fired unexpectedly");
    }

    public void junitTestEnded(String name, boolean wasSuccessful, boolean causedError) {
      listenerFail("junitTestEnded fired unexpectedly");
    }

    public void junitEnded() {
      listenerFail("junitEnded fired unexpectedly");
    }

    public void javadocStarted() {
      listenerFail("javadocStarted fired unexpectedly");
    }

    public void javadocEnded(boolean success, File destDir, boolean allDocs) {
      listenerFail("javadocEnded fired unexpectedly");
    }

    public void interactionStarted() {
      listenerFail("interactionStarted fired unexpectedly");
    }

    public void interactionEnded() {
      listenerFail("interactionEnded fired unexpectedly");
    }

    public void interactionErrorOccurred(int offset, int length) {
      listenerFail("interpreterErrorOccurred fired unexpectedly");
    }

    public void interpreterChanged(boolean inProgress) {
      listenerFail("interpreterChanged fired unexpectedly");
    }






    public void compileStarted() {
      listenerFail("compileStarted fired unexpectedly");
    }

    public void compileEnded(File workDir, File[] excludedFiles) {
      listenerFail("compileEnded fired unexpectedly");
    }

    public void runStarted(OpenDefinitionsDocument doc) {
      listenerFail("runStarted fired unexpectedly");
    }

    public void interpreterResetting() {
      listenerFail("interactionsResetting fired unexpectedly");
    }

    public void interpreterReady(File wd) {
      listenerFail("interactionsReset fired unexpectedly");
    }

    public void interpreterExited(int status) {
      listenerFail("interpreterExited(" + status + ") fired unexpectedly");
    }

    public void interpreterResetFailed(Throwable t) {
      listenerFail("interpreterResetFailed fired unexpectedly");
    }
    
    public void slaveJVMUsed() {  }
    
    public void consoleReset() {
      listenerFail("consoleReset fired unexpectedly");
    }

    public void saveUntitled() {
      listenerFail("saveUntitled fired unexpectedly");
    }
    
    public void compileBeforeJUnit(CompilerListener cl) {
      compileBeforeJUnitCount++;
    }

    public void saveBeforeJavadoc() {
      listenerFail("saveBeforeJavadoc fired unexpectedly");
    }
    
    public void nonTestCase(boolean isTestAll) {
      listenerFail("nonTestCase fired unexpectedly");
    }

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

    public void interactionIncomplete() {
      listenerFail("interactionIncomplete fired unexpectedly");
    }

    public void filePathContainsPound() {
      listenerFail("filePathContainsPound fired unexpectedly");
    }
    
    public void documentNotFound(OpenDefinitionsDocument d, File f) {
      listenerFail("documentNotFound fired unexpectedly");
    }
    
    public void activeDocumentChanged(OpenDefinitionsDocument active) {
      
    }
    
    public void focusOnDefinitionsPane() {
      
    }
  }

  
  
  
  public static class CompileShouldSucceedListener extends TestListener {
    private boolean _expectReset;
    
    private boolean _interactionsNotYetReset = true;  

    
    public CompileShouldSucceedListener(boolean expectReset) { _expectReset = expectReset; }
    
    public CompileShouldSucceedListener() { this(false); }
    
    public boolean notDone() { return _interactionsNotYetReset; }

    public void compileStarted() {

      assertCompileStartCount(0);
      assertCompileEndCount(0);
      assertInterpreterResettingCount(0);
      assertInterpreterReadyCount(0);
      assertConsoleResetCount(0);
      compileStartCount++;
    }

    public void compileEnded(File workDir, File[] excludedFiles) {

      assertCompileEndCount(0);
      assertCompileStartCount(1);
      assertInterpreterResettingCount(0);
      assertInterpreterReadyCount(0);
      assertConsoleResetCount(0);
      compileEndCount++;
    }

    public void interpreterResetting() {
      assertInterpreterResettingCount(0);
      assertInterpreterReadyCount(0);
      assertCompileStartCount(1);
      assertCompileEndCount(1);
      
      interpreterResettingCount++;
    }

    public void interpreterReady(File wd) {
      synchronized(this) {
        assertInterpreterResettingCount(1);
        assertInterpreterReadyCount(0);
        assertCompileStartCount(1);
        assertCompileEndCount(1);
        
        interpreterReadyCount++;
        _interactionsNotYetReset = false;

        notify();
      }
    }

    public void consoleReset() {
      assertConsoleResetCount(0);
      assertCompileStartCount(1);
      assertCompileEndCount(1);
      
      consoleResetCount++;
    }

    public void checkCompileOccurred() {
      assertCompileEndCount(1);
      assertCompileStartCount(1);
      if (_expectReset) {
        assertInterpreterResettingCount(1);
        assertInterpreterReadyCount(1);
      }
      else {
        assertInterpreterResettingCount(0);
        assertInterpreterReadyCount(0);
      }

      
      
    }
  }

  
  public static class CompileShouldFailListener extends TestListener {
    public void compileStarted() {
      assertCompileStartCount(0);
      assertCompileEndCount(0);
      compileStartCount++;
    }

    public void compileEnded(File workDir, File[] excludedFiles) {
      assertCompileEndCount(0);
      assertCompileStartCount(1);
      compileEndCount++;
    }

    public void checkCompileOccurred() {
      assertCompileEndCount(1);
      assertCompileStartCount(1);
    }
  }
  

  public class JUnitTestListener extends CompileShouldSucceedListener {
    
    protected boolean printMessages = GlobalModelJUnitTest.printMessages;
    
    public JUnitTestListener() { this(false, false);  }
    public JUnitTestListener(boolean shouldResetAfterCompile) {  this(shouldResetAfterCompile, false); }
    public JUnitTestListener(boolean shouldResetAfterCompile, boolean printListenerMessages) {
      super(shouldResetAfterCompile);
      this.printMessages = printListenerMessages;
    }
    public void resetCompileCounts() { 
      compileStartCount = 0; 
      compileEndCount = 0;
    }
    public void junitStarted() {
      if (printMessages) System.out.println("listener.junitStarted");
      junitStartCount++;
    }
    public void junitSuiteStarted(int numTests) {
      if (printMessages) System.out.println("listener.junitSuiteStarted, numTests = "+numTests);
      assertJUnitStartCount(1);
      junitSuiteStartedCount++;
    }
    public void junitTestStarted(String name) {
      if (printMessages) System.out.println("  listener.junitTestStarted, " + name);
      junitTestStartedCount++;
    }
    public void junitTestEnded(String name, boolean wasSuccessful, boolean causedError) {
      if (printMessages) System.out.println("  listener.junitTestEnded, name = " + name + " succ = " + wasSuccessful + 
                                            " err = " + causedError);
      junitTestEndedCount++;
      assertEquals("junitTestEndedCount should be same as junitTestStartedCount", junitTestEndedCount, 
                   junitTestStartedCount);
    }
    public void nonTestCase(boolean isTestAll) {
      if (printMessages) System.out.println("listener.nonTestCase, isTestAll=" + isTestAll);
      nonTestCaseCount++;
      synchronized(_junitLock) {
        _junitDone = true;
        _junitLock.notify();
      }
    }
    public void classFileError(ClassFileError e) {
      if (printMessages) System.out.println("listener.classFileError, e="+e);
      classFileErrorCount++;
      synchronized(_junitLock) {
        _junitDone = true;
        _junitLock.notify();
      }
    }
    public synchronized void junitEnded() {
      
      if (printMessages) System.out.println("junitEnded event!");
      junitEndCount++;
      synchronized(_junitLock) {

        _junitDone = true;
        _junitLock.notify();
      }
    }
  }
  
  
  public class JUnitNonTestListener extends JUnitTestListener {
    private boolean _shouldBeTestAll;
    public JUnitNonTestListener() {  this(false); }
    public JUnitNonTestListener(boolean shouldBeTestAll) { _shouldBeTestAll = shouldBeTestAll; }
    public void nonTestCase(boolean isTestAll) {
      nonTestCaseCount++;
      assertEquals("Non test case heard the wrong value for test current/test all", _shouldBeTestAll, isTestAll);

      synchronized(_junitLock) {

        _junitDone = true;
        _junitLock.notify();
      }

    }
  }

  
  public class TestGlobalModel extends DefaultGlobalModel {
    public File getWorkingDirectory() { return getMasterWorkingDirectory(); }
  } 
}
