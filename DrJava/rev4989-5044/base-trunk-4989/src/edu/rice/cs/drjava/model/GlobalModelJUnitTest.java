

package edu.rice.cs.drjava.model;

import junit.framework.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import edu.rice.cs.drjava.model.compiler.CompilerListener;
import edu.rice.cs.drjava.model.junit.*;
import edu.rice.cs.util.Log;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.swing.Utilities;


public final class GlobalModelJUnitTest extends GlobalModelTestCase {
  
  private static Log _log = new Log("GlobalModelJUnit.txt", false);
  
  
  static final boolean printMessages = false;
  
  private static final String ELSPETH_ERROR_TEXT = 
    "import junit.framework.TestCase;" +
    "public class Elspeth extends TestCase {" +
    "    public void testMe() {" +
    "        String s = \"elspeth\";" +
    "        assertEquals(\"they match\", s, \"elspeth4\");" +
    "    }" +
    "  public Elspeth() {" +
    "    super();" +
    "  }" +
    "  public java.lang.String toString() {" +
    "    return \"Elspeth(\" + \")\";" +
    "  }" +
    "  public boolean equals(java.lang.Object o) {" +
    "    if ((o == null) || getClass() != o.getClass()) return false;" +
    "    return true;" +
    "  }" +
    "  public int hashCode() {" +
    "    return getClass().hashCode();" +
    "  }" +
    "}";
  
  private static final String MONKEYTEST_PASS_TEXT =
    "import junit.framework.*; \n" +
    "import java.io.*; \n" +
    "public class MonkeyTestPass extends TestCase { \n" +
    "  public MonkeyTestPass(String name) { super(name); } \n" +
    "  public void testShouldPass() { \n" +
    "    assertEquals(\"monkey\", \"monkey\"); \n" +
    "  } \n" +
    "}\n";
  
  private static final String MONKEYTEST_PASS_ALT_TEXT =
    "import junit.framework.*; \n" +
    "import java.io.*; \n" +
    "public class MonkeyTestPass extends TestCase { \n" +
    "  public MonkeyTestPass(String name) { super(name); } \n" +
    "  public void testShouldPass() { \n" +
    "    assertEquals(\"monkeys\", \"monkeys\"); \n" +
    "  } \n" +
    "}\n";
  
  private static final String MONKEYTEST_FAIL_TEXT =
    "import junit.framework.*; " +
    "public class MonkeyTestFail extends TestCase { " +
    "  public MonkeyTestFail(String name) { super(name); } " +
    "  public void testShouldFail() { " +
    "    assertEquals(\"monkey\", \"baboon\"); " +
    "  } " +
    "}";
  
  private static final String MONKEYTEST_ERROR_TEXT =
    "import junit.framework.*; " +
    "public class MonkeyTestError extends TestCase { " +
    "  public MonkeyTestError(String name) { super(name); } " +
    "  public void testThrowsError() { " +
    "    throw new Error(\"This is an error.\"); " +
    "  } " +
    "}";
  








  
  private static final String NONPUBLIC_TEXT =
    "import junit.framework.*; " +
    "class NonPublic extends TestCase { " +
    "  NonPublic(String name) { super(name); } " +
    "  void testShouldFail() { " +
    "    assertEquals(\"monkey\", \"baboon\"); " +
    "  } " +
    "}";
  
  private static final String NON_TESTCASE_TEXT =
    "public class NonTestCase {}";
  
  private static final String MONKEYTEST_INFINITE_TEXT =
    "import junit.framework.*; " +
    "public class MonkeyTestInfinite extends TestCase { " +
    "  public MonkeyTestInfinite(String name) { super(name); } " +
    "  public void testInfinite() { " +
    "    while(true) {}" +
    "  } " +
    "}";
  
  private static final String HAS_MULTIPLE_TESTS_PASS_TEXT =
    "import junit.framework.*; " +
    "public class HasMultipleTestsPass extends TestCase { " +
    "  public HasMultipleTestsPass(String name) { super(name); } " +
    "  public void testShouldPass() { " +
    "    assertEquals(\"monkey\", \"monkey\"); " +
    "  } " +
    "  public void testShouldAlsoPass() { " +
    "    assertTrue(true); " +
    "  } " +
    "}";
  
  private static final String STATIC_INNER_TEST_TEXT = 
    "import junit.framework.TestCase;" +
    " public class StaticInnerTestCase{" +
    "   public static class Sadf extends TestCase {" +
    "     public Sadf() {" +
    "       super();" +
    "     }" +
    "     public Sadf(String name) {" +
    "       super(name);" +
    "     }" +
    "     public void testX() {" +
    "       assertTrue(\"this is true\", true);" +
    "     }" +
    "     public void testY() {" +
    "       assertFalse(\"this is false\", false);" +
    "     }" +
    "   }" +
    "}";
  
  private static final String MULTI_CLASSES_IN_FILE_TEXT = 
    "import junit.framework.TestCase;" +
    " class A { } " +
    " class B /* with syntax error */ { public void foo(int x) { } } " +
    " public class Test extends TestCase { " + 
    "   public void testAB() { assertTrue(\"this is true\", true); } " +
    " }";
  




  
  
  public void testNoJUnitErrors() throws Exception {
    if (printMessages) System.out.println("----testNoJUnitErrors-----");

    
    final OpenDefinitionsDocument doc = setupDocument(MONKEYTEST_PASS_TEXT);
    final File file = new File(_tempDir, "MonkeyTestPass.java");
    saveFile(doc, new FileSelector(file));
    JUnitTestListener listener = new JUnitTestListener();
    _model.addListener(listener);
    
    listener.compile(doc); 
    listener.checkCompileOccurred();
    
    listener.runJUnit(doc);
    
    listener.assertJUnitStartCount(1);
    
    if (printMessages) System.out.println("errors: "+_model.getJUnitModel().getJUnitErrorModel());
    
    listener.assertNonTestCaseCount(0);
    assertEquals("test case should have no errors reported",  0,
                 _model.getJUnitModel().getJUnitErrorModel().getNumErrors());
    
    _model.removeListener(listener);
    _log.log("testNoJUnitErrors completed");
  }
  
  
  public void testOneJUnitError() throws Exception {
    if (printMessages) System.out.println("----testOneJUnitError-----");

    
    final OpenDefinitionsDocument doc = setupDocument(MONKEYTEST_FAIL_TEXT);
    final File file = new File(_tempDir, "MonkeyTestFail.java");
    saveFile(doc, new FileSelector(file));
    JUnitTestListener listener = new JUnitTestListener();
    _model.addListener(listener);
    
    listener.compile(doc);
    listener.checkCompileOccurred();
    
    listener.runJUnit(_model.getJUnitModel());
    
    assertEquals("test case has one error reported", 1, _model.getJUnitModel().getJUnitErrorModel().getNumErrors());
    _model.removeListener(listener);
    
    _log.log("testOneJUnitError completed");
  }
  
  
  public void testElspethOneJUnitError() throws Exception {
    if (printMessages) System.out.println("----testElspethOneJUnitError-----");

    
    OpenDefinitionsDocument doc = setupDocument(ELSPETH_ERROR_TEXT);
    final File file = new File(_tempDir, "Elspeth.java");
    saveFile(doc, new FileSelector(file));
    JUnitTestListener listener = new JUnitTestListener();
    _model.addListener(listener);
    
    listener.compile(doc);
    listener.checkCompileOccurred();
    
    listener.runJUnit(doc);
    
    JUnitErrorModel jem = _model.getJUnitModel().getJUnitErrorModel();
    assertEquals("test case has one error reported", 1, jem.getNumErrors());
    assertTrue("first error should be an error not a warning", !jem.getError(0).isWarning());
    _model.removeListener(listener);
    
    _log.log("testElspethOneJUnitError completed");
  }
  
  
  public void testRealError() throws Exception {
    if (printMessages) System.out.println("----testRealError-----");

    
    OpenDefinitionsDocument doc = setupDocument(MONKEYTEST_ERROR_TEXT);
    final File file = new File(_tempDir, "MonkeyTestError.java");
    saveFile(doc, new FileSelector(file));
    JUnitTestListener listener = new JUnitTestListener();
    _model.addListener(listener);
    
    listener.compile(doc);
    listener.checkCompileOccurred();
    
    listener.runJUnit(doc);
    
    assertEquals("test case has one error reported", 1, _model.getJUnitModel().getJUnitErrorModel().getNumErrors());
    listener.assertJUnitEndCount(1);
    _model.removeListener(listener);
    
    _log.log("testRealError completed");
  }
  
  
  public void testNonTestCaseError() throws Exception {
    if (printMessages) System.out.println("----testNonTestCaseError-----");

    
    final OpenDefinitionsDocument doc = setupDocument(NON_TESTCASE_TEXT);
    final File file = new File(_tempDir, "NonTestCase.java");
    saveFile(doc, new FileSelector(file));
    
    JUnitTestListener listener = new JUnitNonTestListener();
    
    _model.addListener(listener);
    
    listener.compile(doc);
    listener.checkCompileOccurred();
    
    listener.runJUnit(doc);
    
    if (printMessages) System.out.println("after test");
    
    
    listener.assertJUnitStartCount(0);  
    listener.assertJUnitEndCount(0); 
    listener.assertNonTestCaseCount(1);
    listener.assertJUnitSuiteStartedCount(0);
    listener.assertJUnitTestStartedCount(0);
    listener.assertJUnitTestEndedCount(0);
    _model.removeListener(listener);
    
    _log.log("testNonTestCaseError completed");
  }
  
  
  public void testResultOfNonPublicTestCase() throws Exception {
    if (printMessages) System.out.println("----testResultOfNonPublicTestCase-----");

    
    final OpenDefinitionsDocument doc = setupDocument(NONPUBLIC_TEXT);
    final File file = new File(_tempDir, "NonPublic.java");
    saveFile(doc, new FileSelector(file));
    
    JUnitTestListener listener = new JUnitTestListener();
    
    _model.addListener(listener);
    
    listener.compile(doc);
    listener.checkCompileOccurred();
    
    listener.runJUnit(doc);
    
    if (printMessages) System.out.println("after test");
    
    
    
    
    listener.assertJUnitStartCount(1);
    listener.assertJUnitEndCount(1);
    
    assertEquals("test case has one error reported", 1, _model.getJUnitModel().getJUnitErrorModel().getNumErrors());
    _model.removeListener(listener);
    
    _log.log("testResultOfNonPublicTestCase completed");
  }
  
  





















  
  
  public void testNoClassFile() throws Exception {
    if (printMessages) System.out.println("----testNoClassFile-----");

    
    final OpenDefinitionsDocument doc = setupDocument(MONKEYTEST_PASS_TEXT);
    final File file = new File(_tempDir, "MonkeyTestPass.java");
    saveFile(doc, new FileSelector(file));
    
    JUnitTestListener listener = new JUnitCompileBeforeTestListener();
    
    _model.addListener(listener);
    

    
    listener.runJUnit(doc);

    
    if (printMessages) System.out.println("after test");
    listener.assertCompileBeforeJUnitCount(1);
    listener.assertNonTestCaseCount(0);
    listener.assertJUnitStartCount(1);
    listener.assertJUnitEndCount(1);
    listener.assertJUnitSuiteStartedCount(1);
    listener.assertJUnitTestStartedCount(1);
    listener.assertJUnitTestEndedCount(1);
    _model.removeListener(listener);
    _log.log("testNoClassFile completed");
  }
  
  
  
  
  public void testInfiniteLoop() throws Exception {
    if (printMessages) System.out.println("----testInfiniteLoop-----");

    
    final OpenDefinitionsDocument doc = setupDocument(MONKEYTEST_INFINITE_TEXT);
    final File file = new File(_tempDir, "MonkeyTestInfinite.java");
    saveFile(doc, new FileSelector(file));
    
    JUnitTestListener listener = new JUnitTestListener(false) {
      public void junitSuiteStarted(int numTests) {
        assertEquals("should run 1 test", 1, numTests);
        synchronized(this) { junitSuiteStartedCount++; }
        
        _model.resetInteractions(new File(System.getProperty("user.dir")));
      }
    };
    
    _model.addListener(listener);
    listener.compile(doc);
    

    
    if (_model.getCompilerModel().getNumErrors() > 0) {
      fail("compile failed: " + getCompilerErrorString());
    }
    listener.checkCompileOccurred();
    




    
    listener.logJUnitStart();
    try {

      doc.startJUnit();
      listener.waitJUnitDone();
      
    }
    catch (Exception e) { fail("Aborting unit testing runs recovery code in testing thread; no exception is thrown"); }
    
    listener.waitResetDone();  
        

    
    if (printMessages) System.out.println("after test");
    listener.assertJUnitStartCount(1);
    _model.removeListener(listener);
    listener.assertJUnitEndCount(1);  

    _log.log("testInfiniteLoop completed");
  }
  
  
  public void testUnsavedAndUnCompiledChanges() throws Exception {
    if (printMessages) System.out.println("-----testUnsavedAndUnCompiledChanges-----");
    
    OpenDefinitionsDocument doc = setupDocument(MONKEYTEST_PASS_TEXT);
    final File file = new File(_tempDir, "MonkeyTestPass.java");
    saveFile(doc, new FileSelector(file));
    
    List<OpenDefinitionsDocument> docs = _model.getSortedOpenDefinitionsDocuments();
    
    final OpenDefinitionsDocument untitled = docs.get(0);
    

    
    Utilities.invokeAndWait(new Runnable() {
      public void run() { 
        untitled.quitFile();
        _model.closeFileWithoutPrompt(untitled);
      }
    });
    
    
    JUnitTestListener listener = new JUnitCompileBeforeTestListener();
    _model.addListener(listener);
    
    testStartCompile(doc);
    

    listener.waitCompileDone();
    
    listener.resetCompileCounts();
    
    changeDocumentText(MONKEYTEST_PASS_ALT_TEXT, doc);

    
    listener.runJUnit(doc);
    

    
    
    
    listener.assertSaveBeforeCompileCount(1);
    listener.assertCompileBeforeJUnitCount(1);
    listener.assertNonTestCaseCount(0);
    listener.assertJUnitStartCount(1);
    listener.assertJUnitEndCount(1);
    listener.assertJUnitSuiteStartedCount(1);
    listener.assertJUnitTestStartedCount(1);
    listener.assertJUnitTestEndedCount(1);
    
    if (printMessages) System.out.println("after test");
    _model.removeListener(listener);
    
    assertEquals("test case should have no errors reported after modifying", 0,
                 _model.getJUnitModel().getJUnitErrorModel().getNumErrors());
    
    saveFile(doc, new FileSelector(file));
    
    listener = new JUnitTestListener();
    _model.addListener(listener);
    
    
    assertEquals("test case should have no errors reported after saving", 0,
                 _model.getJUnitModel().getJUnitErrorModel().getNumErrors());
    _model.removeListener(listener);
    
    _log.log("testUnsavedAndUnCompiledChanges completed");
  }
  
  
  public void safeJUnitAllWithNoValidTests() throws Exception {
    

    
    JUnitNonTestListener listener = new JUnitNonTestListener(true);
    _model.addListener(listener);
    
    listener.runJUnit(_model.getJUnitModel());
    
    listener.assertNonTestCaseCount(1);
    listener.assertJUnitSuiteStartedCount(0);
    listener.assertJUnitTestStartedCount(0);
    listener.assertJUnitTestEndedCount(0);
    _model.removeListener(listener);
    
    JUnitCompileBeforeTestListener listener2 = new JUnitCompileBeforeTestListener();
    _model.addListener(listener2);
    OpenDefinitionsDocument doc = setupDocument(NON_TESTCASE_TEXT);
    File file = new File(_tempDir, "NonTestCase.java");

    saveFile(doc, new FileSelector(file));
    
    listener2.compile(doc);
    listener2.checkCompileOccurred();

    listener2.resetCompileCounts();
    
    
    File file2 = new File(_tempDir, "MonkeyTestPass.java");
    OpenDefinitionsDocument doc2 = setupDocument(MONKEYTEST_PASS_TEXT);
    saveFile(doc2, new FileSelector(file2));
    listener2.runJUnit(_model.getJUnitModel());
    
    listener2.assertNonTestCaseCount(0);
    listener2.assertJUnitSuiteStartedCount(1);
    listener2.assertJUnitTestStartedCount(1);
    listener2.assertJUnitTestEndedCount(1);
    _model.removeListener(listener2);
    
    _log.log("testJUnitAllWithNoValidTests completed");
  }
  
  
  public void safeJUnitAllWithNoErrors() throws Exception {

    













    OpenDefinitionsDocument doc = setupDocument(MONKEYTEST_PASS_TEXT);
    File file = new File(_tempDir, "MonkeyTestPass.java");
    saveFile(doc, new FileSelector(file));
    JUnitTestListener listener = new JUnitNonTestListener(true);
    _model.addListener(listener);
    listener.compile(doc);
    listener.checkCompileOccurred();
    
    listener.runJUnit(_model.getJUnitModel());
    
    listener.assertNonTestCaseCount(0);
    listener.assertJUnitSuiteStartedCount(1);
    listener.assertJUnitTestStartedCount(1);
    listener.assertJUnitTestEndedCount(1);
    _model.removeListener(listener);
    
    doc = setupDocument(HAS_MULTIPLE_TESTS_PASS_TEXT);
    file = new File(_tempDir, "HasMultipleTestsPass.java");
    saveFile(doc, new FileSelector(file));
    
    listener = new JUnitNonTestListener(true);
    _model.addListener(listener);
    
    listener.compile(doc);
    
    listener.runJUnit(_model.getJUnitModel());
    
    listener.assertNonTestCaseCount(0);
    listener.assertJUnitSuiteStartedCount(1);
    listener.assertJUnitTestStartedCount(3);
    listener.assertJUnitTestEndedCount(3);
    _model.removeListener(listener);
    
    _log.log("testJUnitAllWithNoErrors completed");
  }
  
  
  public void safeJUnitAllWithErrors() throws Exception {
    
    if (printMessages) System.out.println("-----testJUnitAllWithErrors-----");
    
    OpenDefinitionsDocument doc = setupDocument(MONKEYTEST_ERROR_TEXT);
    OpenDefinitionsDocument doc2 = setupDocument(MONKEYTEST_FAIL_TEXT);
    File file = new File(_tempDir, "MonkeyTestError.java");
    File file2 = new File(_tempDir, "MonkeyTestFail.java");
    saveFile(doc, new FileSelector(file));
    saveFile(doc2, new FileSelector(file2));
    JUnitNonTestListener listener = new JUnitNonTestListener(true);
    _model.addListener(listener);
    listener.compile(doc);
    listener.checkCompileOccurred();
    listener.resetCompileCounts();
    listener.compile(doc2);
    listener.checkCompileOccurred();
    
    listener.runJUnit(_model.getJUnitModel());
    
    listener.assertNonTestCaseCount(0);
    listener.assertJUnitSuiteStartedCount(1);
    listener.assertJUnitTestStartedCount(2);
    listener.assertJUnitTestEndedCount(2);
    _model.removeListener(listener);
    
    JUnitErrorModel jem = _model.getJUnitModel().getJUnitErrorModel();
    assertEquals("test case has one error reported", 2, jem.getNumErrors());
    
    assertTrue("first error should be an error", jem.getError(0).isWarning());
    assertFalse("second error should be a failure", jem.getError(1).isWarning());
    
    _log.log("testJUnitAllWithErrors completed");
  } 
  
  
  public void safeJUnitStaticInnerClass() throws Exception {
    if (printMessages) System.out.println("-----testJUnitAllWithStaticInnerClass-----");
    
    OpenDefinitionsDocument doc = setupDocument(NON_TESTCASE_TEXT);
    OpenDefinitionsDocument doc2 = setupDocument(STATIC_INNER_TEST_TEXT);
    File file = new File(_tempDir, "NonTestCase.java");
    File file2 = new File(_tempDir, "StaticInnerTestCase.java");
    saveFile(doc, new FileSelector(file));
    saveFile(doc2, new FileSelector(file2));
    
    JUnitNonTestListener listener = new JUnitNonTestListener(true);
    _model.addListener(listener);
    listener.compile(doc);
    listener.checkCompileOccurred();
    listener.resetCompileCounts();
    listener.compile(doc2);
    listener.checkCompileOccurred();
    
    listener.runJUnit(_model.getJUnitModel());
    
    listener.assertNonTestCaseCount(0);
    listener.assertJUnitSuiteStartedCount(1);
    listener.assertJUnitTestStartedCount(2);
    listener.assertJUnitTestEndedCount(2);
    _model.removeListener(listener);
    if (printMessages) System.out.println("----testJUnitAllWithNoErrors-----"); 
    
    _log.log("testJUnitStaticInnerClass completed");
  }  
  
  
  public class JUnitCompileBeforeTestListener extends JUnitTestListener {
    
    
    public void compileBeforeJUnit(final CompilerListener testAfterCompile, List<OpenDefinitionsDocument> outOfSync) {

      synchronized(this) { compileBeforeJUnitCount++; }
      
      _model.getCompilerModel().addListener(testAfterCompile);  

      try { _model.getCompilerModel().compileAll();   }
      catch(IOException e) { fail("Compile step generated IOException"); }
      

    }
    
    public void saveBeforeCompile() {

      synchronized(this) { saveBeforeCompileCount++; }
      
      saveAllFiles(_model, new FileSaveSelector() {
        public File getFile() { throw new UnexpectedException ("Test should not ask for save file name"); }
        public boolean warnFileOpen(File f) { return false; }
        public boolean verifyOverwrite() { return true; }
        public boolean shouldSaveAfterFileMoved(OpenDefinitionsDocument doc, File oldFile) { return false; }
      });
    }
    public void fileSaved(OpenDefinitionsDocument doc) { }
  }
  
  
  public void testCorrectFilesAfterIncorrectChanges() throws Exception {

    
    OpenDefinitionsDocument doc = setupDocument(NON_TESTCASE_TEXT);
    JUnitNonTestListener listener = new JUnitNonTestListener(true);
    File file = new File(_tempDir, "NonTestCase.java");
    saveFile(doc, new FileSelector(file));
    _model.addListener(listener);
   
    listener.compile(doc);
    listener.checkCompileOccurred();
    _model.removeListener(listener);
    
    
    doc = setupDocument(MULTI_CLASSES_IN_FILE_TEXT);
    file = new File(_tempDir, "Test.java");
    saveFile(doc, new FileSelector(file));
    
    listener = new JUnitNonTestListener(true);
    _model.addListener(listener);
    listener.compile(doc);
    
    listener.runJUnit(_model.getJUnitModel());
    
    listener.assertNonTestCaseCount(0);
    listener.assertJUnitSuiteStartedCount(1);
    listener.assertJUnitTestStartedCount(1);
    listener.assertJUnitTestEndedCount(1);
    _model.removeListener(listener);
    
    doc.remove(87,4);
    
    JUnitTestListener listener2 = new JUnitCompileBeforeTestListener();
    
    _model.addListener(listener2);
    

    
    listener2.runJUnit(doc);

    
    if (printMessages) System.out.println("after test");
    listener2.assertCompileBeforeJUnitCount(1);
    listener2.assertNonTestCaseCount(1);
    listener2.assertJUnitStartCount(0);
    listener2.assertJUnitEndCount(0);
    listener2.assertJUnitSuiteStartedCount(0);
    listener2.assertJUnitTestStartedCount(0);
    listener2.assertJUnitTestEndedCount(0);
    _model.removeListener(listener2);
    _log.log("testCorrectFilesAfterIncorrectChanges completed");
  }
}
