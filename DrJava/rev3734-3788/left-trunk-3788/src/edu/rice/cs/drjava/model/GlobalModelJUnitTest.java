

package edu.rice.cs.drjava.model;

import junit.framework.*;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;
import javax.swing.SwingUtilities;

import edu.rice.cs.drjava.model.compiler.CompilerListener;
import edu.rice.cs.drjava.model.junit.*;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.swing.Utilities;


public final class GlobalModelJUnitTest extends GlobalModelTestCase {
  
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
    "public class NonPublic extends TestCase { " +
    "  public NonPublic(String name) { super(name); } " +
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

  
  public static Test suite() {
    return  new TestSuite(GlobalModelJUnitTest.class);
  }
  
  
  public void testNoJUnitErrors() throws Exception {
    if (printMessages) System.out.println("----testNoJUnitErrors-----");

    
    OpenDefinitionsDocument doc = setupDocument(MONKEYTEST_PASS_TEXT);
    final File file = new File(_tempDir, "MonkeyTestPass.java");
    doc.saveFile(new FileSelector(file));
    JUnitTestListener listener = new JUnitTestListener();
    _model.addListener(listener);
    
    doc.startCompile(); 
    listener.checkCompileOccurred();
    
    _runJUnit(doc);

    Utilities.clearEventQueue();
    
    listener.assertJUnitStartCount(1);
    
    if (printMessages) System.out.println("errors: "+_model.getJUnitModel().getJUnitErrorModel());
    
    listener.assertNonTestCaseCount(0);
    assertEquals("test case should have no errors reported",  0,
                 _model.getJUnitModel().getJUnitErrorModel().getNumErrors());
                 
    _model.removeListener(listener);
    
  }
  
  
  public void testOneJUnitError() throws Exception {
    if (printMessages) System.out.println("----testOneJUnitError-----");


    OpenDefinitionsDocument doc = setupDocument(MONKEYTEST_FAIL_TEXT);
    final File file = new File(_tempDir, "MonkeyTestFail.java");
    doc.saveFile(new FileSelector(file));
    JUnitTestListener listener = new JUnitTestListener();
    _model.addListener(listener);
    if (printMessages) System.out.println("before compile");
    doc.startCompile();
    if (printMessages) System.out.println("after compile");
    
    _runJUnit();

    assertEquals("test case has one error reported",
                 1,
                 _model.getJUnitModel().getJUnitErrorModel().getNumErrors());
    _model.removeListener(listener);
  }
  
  
  public void testElspethOneJUnitError() throws Exception {
    if (printMessages) System.out.println("----testElspethOneJUnitError-----");


    OpenDefinitionsDocument doc = setupDocument(ELSPETH_ERROR_TEXT);
    final File file = new File(_tempDir, "Elspeth.java");
    doc.saveFile(new FileSelector(file));
    JUnitTestListener listener = new JUnitTestListener();
    _model.addListener(listener);
    if (printMessages) System.out.println("before compile");
    doc.startCompile();
    if (printMessages) System.out.println("after compile");
    
    _runJUnit(doc);

    JUnitErrorModel jem = _model.getJUnitModel().getJUnitErrorModel();
    assertEquals("test case has one error reported", 1, jem.getNumErrors());
    assertTrue("first error should be an error not a warning", !jem.getError(0).isWarning());
    _model.removeListener(listener);
  }

  
  public void testRealError() throws Exception {
    if (printMessages) System.out.println("----testRealError-----");

    
    OpenDefinitionsDocument doc = setupDocument(MONKEYTEST_ERROR_TEXT);
    final File file = new File(_tempDir, "MonkeyTestError.java");
    doc.saveFile(new FileSelector(file));
    JUnitTestListener listener = new JUnitTestListener();
    _model.addListener(listener);
    if (printMessages) System.out.println("before compile");
    doc.startCompile();
    if (printMessages) System.out.println("after compile");
    
    _runJUnit(doc);

    assertEquals("test case has one error reported",
                 1,
                 _model.getJUnitModel().getJUnitErrorModel().getNumErrors());
    listener.assertJUnitEndCount(1);
    _model.removeListener(listener);
  }

  
  public void testNonTestCaseError() throws Exception {
    if (printMessages) System.out.println("----testNonTestCaseError-----");


    final OpenDefinitionsDocument doc = setupDocument(NON_TESTCASE_TEXT);
    final File file = new File(_tempDir, "NonTestCase.java");
    doc.saveFile(new FileSelector(file));

    JUnitTestListener listener = new JUnitNonTestListener();

    _model.addListener(listener);
    if (printMessages) System.out.println("before compile");
    doc.startCompile();
    if (printMessages) System.out.println("after compile");

    _runJUnit(doc);

    if (printMessages) System.out.println("after test");

    
    listener.assertJUnitStartCount(0);  
    listener.assertJUnitEndCount(0); 
    listener.assertNonTestCaseCount(1);
    listener.assertJUnitSuiteStartedCount(0);
    listener.assertJUnitTestStartedCount(0);
    listener.assertJUnitTestEndedCount(0);
    _model.removeListener(listener);
  }
  
  
  public void testResultOfNonPublicTestCase() throws Exception {
    if (printMessages) System.out.println("----testResultOfNonPublicTestCase-----");


    final OpenDefinitionsDocument doc = setupDocument(NONPUBLIC_TEXT);
    final File file = new File(_tempDir, "NonPublic.java");
    doc.saveFile(new FileSelector(file));

    JUnitTestListener listener = new JUnitTestListener();

    _model.addListener(listener);

    if (printMessages) System.out.println("before compile");
    doc.startCompile();
    if (printMessages) System.out.println("after compile");

    _runJUnit(doc);
   
    if (printMessages) System.out.println("after test");

    

    
    listener.assertJUnitStartCount(1);
    listener.assertJUnitEndCount(1);

    assertEquals("test case has one error reported",
                 1,
                 _model.getJUnitModel().getJUnitErrorModel().getNumErrors());
    _model.removeListener(listener);
  }

  public void testDoNotRunJUnitIfFileHasBeenMoved() throws Exception {
    if (printMessages) System.out.println("----testDoNotRunJUnitIfFileHasBeenMoved-----");

    

    final OpenDefinitionsDocument doc = setupDocument(MONKEYTEST_PASS_TEXT);
    final File file = new File(_tempDir, "MonkeyTestPass.java");
    doc.saveFile(new FileSelector(file));

    TestListener listener = new TestListener();

    _model.addListener(listener);
    file.delete();

    doc.startJUnit();
    listener.assertJUnitStartCount(0);
    listener.assertJUnitTestStartedCount(0);

    _model.removeListener(listener);
  }
  
  
  public void testNoClassFile() throws Exception {
    if (printMessages) System.out.println("----testNoClassFile-----");


    final OpenDefinitionsDocument doc = setupDocument(MONKEYTEST_PASS_TEXT);
    final File file = new File(_tempDir, "MonkeyTestPass.java");
    doc.saveFile(new FileSelector(file));

    JUnitTestListener listener = new JUnitCompileBeforeTestListener();
      
    _model.addListener(listener);
    

    
    _runJUnit(doc);

    
    if (printMessages) System.out.println("after test");
    listener.assertCompileBeforeJUnitCount(1);
    listener.assertNonTestCaseCount(0);
    listener.assertJUnitStartCount(1);
    listener.assertJUnitEndCount(1);
    listener.assertJUnitSuiteStartedCount(1);
    listener.assertJUnitTestStartedCount(1);
    listener.assertJUnitTestEndedCount(1);
    _model.removeListener(listener);
  }
  
  
  public void testInfiniteLoop() throws Exception {
    if (printMessages) System.out.println("----testInfiniteLoop-----");

    
    final OpenDefinitionsDocument doc = setupDocument(MONKEYTEST_INFINITE_TEXT);
    final File file = new File(_tempDir, "MonkeyTestInfinite.java");
    doc.saveFile(new FileSelector(file));
    
    CompileShouldSucceedListener listener = new CompileShouldSucceedListener(false);
    TestListener listener2 = new TestListener() {
      public void junitStarted() {

        junitStartCount++;
      }
      public void junitSuiteStarted(int numTests) {

        assertEquals("should run 1 test", 1, numTests);
        junitSuiteStartedCount++;
        
        _model.resetInteractions(new File(System.getProperty("user.dir")));
      }
      public void junitTestStarted(String name) {
        assertEquals("running wrong test", "testInfinite", name);
        junitTestStartedCount++;
      }
      public void junitEnded() {

        
        junitEndCount++;
        synchronized(_junitLock) {
          _junitDone = true;
          _junitLock.notify();
        }
      }
      public void interpreterResetting() {
        assertInterpreterReadyCount(0);
        interpreterResettingCount++;
      }
      public void interpreterReady(File wd) {
        assertInterpreterResettingCount(1);
        assertJUnitEndCount(0);
        interpreterReadyCount++;
      }
      
      public void consoleReset() { consoleResetCount++; }
    };
    _model.addListener(listener);
    if (printMessages) System.out.println("before compile");
    doc.startCompile();
    if (_model.getCompilerModel().getNumErrors() > 0) {
      fail("compile failed: " + getCompilerErrorString());
    }
    listener.checkCompileOccurred();
    if (printMessages) System.out.println("after compile");
    _model.removeListener(listener);
    _model.addListener(listener2);
    
    _logJUnitStart();
    try {

      doc.startJUnit();
      _waitJUnitDone();
      fail("slave JVM should throw an exception because testing is interrupted by resetting interactions");
    }
    catch (UnexpectedException e) {  }
    
    if (printMessages) System.out.println("after test");
    listener2.assertJUnitStartCount(1);
    _model.removeListener(listener2);
    listener2.assertJUnitEndCount(1);
  }
  
  
  public void testUnsavedAndUnCompiledChanges() throws Exception {
    if (printMessages) System.out.println("-----testUnsavedAndUnCompiledChanges-----");
    
    OpenDefinitionsDocument doc = setupDocument(MONKEYTEST_PASS_TEXT);
    final File file = new File(_tempDir, "MonkeyTestPass.java");
    doc.saveFile(new FileSelector(file));
    
    List<OpenDefinitionsDocument> docs = _model.getOpenDefinitionsDocuments();
    
    OpenDefinitionsDocument isUntitled = docs.get(0);
    

    
    isUntitled.quitFile();
    _model.closeFileWithoutPrompt(isUntitled);
    
    
    JUnitTestListener listener = new JUnitCompileBeforeTestListener();
    _model.addListener(listener);
    
    doc.startCompile();
    

    
    listener.resetCompileCounts();
    
    changeDocumentText(MONKEYTEST_PASS_ALT_TEXT, doc);

    
    _runJUnit(doc);
    

    
    
    
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
    
    assertEquals("test case should have no errors reported after modifying",
                 0,
                 _model.getJUnitModel().getJUnitErrorModel().getNumErrors());
    
    doc.saveFile(new FileSelector(file));
    
    listener = new JUnitTestListener();
    _model.addListener(listener);
    
    
    assertEquals("test case should have no errors reported after saving",
                 0,
                 _model.getJUnitModel().getJUnitErrorModel().getNumErrors());
    _model.removeListener(listener);
  }
  
  
  public void testJUnitAllWithNoValidTests() throws Exception {
    

    
    JUnitNonTestListener listener = new JUnitNonTestListener(true);
    _model.addListener(listener);
    
    _runJUnit();
    
    listener.assertNonTestCaseCount(1);
    listener.assertJUnitSuiteStartedCount(0);
    listener.assertJUnitTestStartedCount(0);
    listener.assertJUnitTestEndedCount(0);
    _model.removeListener(listener);
    

    
    OpenDefinitionsDocument doc = setupDocument(NON_TESTCASE_TEXT);
    JUnitCompileBeforeTestListener listener2 = new JUnitCompileBeforeTestListener();
    File file = new File(_tempDir, "NonTestCase.java");
    doc.saveFile(new FileSelector(file));
    doc.startCompile();
    
    listener2.resetCompileCounts();
    
    doc = setupDocument(MONKEYTEST_PASS_TEXT);
    file = new File(_tempDir, "MonkeyTestPass.java");
    doc.saveFile(new FileSelector(file));
    _model.addListener(listener2);
    
    _runJUnit();
    
    listener2.assertNonTestCaseCount(0);
    listener2.assertJUnitSuiteStartedCount(1);
    listener2.assertJUnitTestStartedCount(1);
    listener2.assertJUnitTestEndedCount(1);
    _model.removeListener(listener2);
  }
  
  
  public void testJUnitAllWithNoErrors() throws Exception {
    if (printMessages) System.out.println("-----testJUnitAllWithNoErrors-----");
    
    OpenDefinitionsDocument doc = setupDocument(NON_TESTCASE_TEXT);
    JUnitNonTestListener listener = new JUnitNonTestListener(true);
    File file = new File(_tempDir, "NonTestCase.java");
    doc.saveFile(new FileSelector(file));
    doc.startCompile();
    doc = setupDocument(MONKEYTEST_PASS_TEXT);
    file = new File(_tempDir, "MonkeyTestPass.java");
    doc.saveFile(new FileSelector(file));
    doc.startCompile();
    _model.addListener(listener);
    
    _runJUnit();
    
    listener.assertNonTestCaseCount(0);
    listener.assertJUnitSuiteStartedCount(1);
    listener.assertJUnitTestStartedCount(1);
    listener.assertJUnitTestEndedCount(1);
    _model.removeListener(listener);
    
    listener = new JUnitNonTestListener(true);
    doc = setupDocument(HAS_MULTIPLE_TESTS_PASS_TEXT);
    file = new File(_tempDir, "HasMultipleTestsPass.java");
    doc.saveFile(new FileSelector(file));
    doc.startCompile();
    _model.addListener(listener);
    
    _runJUnit();
    
    listener.assertNonTestCaseCount(0);
    listener.assertJUnitSuiteStartedCount(1);
    listener.assertJUnitTestStartedCount(3);
    listener.assertJUnitTestEndedCount(3);
    _model.removeListener(listener);
  }
  
  
  public void testJUnitAllWithErrors() throws Exception {
    
    if (printMessages) System.out.println("-----testJUnitAllWithErrors-----");
    
    OpenDefinitionsDocument doc = setupDocument(MONKEYTEST_ERROR_TEXT);
    JUnitNonTestListener listener = new JUnitNonTestListener(true);
    File file = new File(_tempDir, "MonkeyTestError.java");
    doc.saveFile(new FileSelector(file));
    doc.startCompile();
    doc = setupDocument(MONKEYTEST_FAIL_TEXT);
    file = new File(_tempDir, "MonkeyTestFail.java");
    doc.saveFile(new FileSelector(file));
    doc.startCompile();
    _model.addListener(listener);
    
    _runJUnit();
    
    listener.assertNonTestCaseCount(0);
    listener.assertJUnitSuiteStartedCount(1);
    listener.assertJUnitTestStartedCount(2);
    listener.assertJUnitTestEndedCount(2);
    _model.removeListener(listener);
    
    JUnitErrorModel jem = _model.getJUnitModel().getJUnitErrorModel();
    assertEquals("test case has one error reported", 2, jem.getNumErrors());
    
    assertTrue("first error should be an error", jem.getError(0).isWarning());
    assertFalse("second error should be a failure", jem.getError(1).isWarning());
  }
  
  
  
  public void testJUnitStaticInnerClass() throws Exception {
    if (printMessages) System.out.println("-----testJUnitAllWithStaticInnerClass-----");
        
    OpenDefinitionsDocument doc = setupDocument(NON_TESTCASE_TEXT);
    JUnitNonTestListener listener = new JUnitNonTestListener(true);
    File file = new File(_tempDir, "NonTestCase.java");
    doc.saveFile(new FileSelector(file));
    doc.startCompile();
    doc = setupDocument(STATIC_INNER_TEST_TEXT);
    file = new File(_tempDir, "StaticInnerTestCase.java");
    doc.saveFile(new FileSelector(file));
    doc.startCompile();
    _model.addListener(listener);
    
    _runJUnit();
    
    listener.assertNonTestCaseCount(0);
    listener.assertJUnitSuiteStartedCount(1);
    listener.assertJUnitTestStartedCount(2);
    listener.assertJUnitTestEndedCount(2);
    _model.removeListener(listener);
    if (printMessages) System.out.println("----testJUnitAllWithNoErrors-----");  
  }  
 
  public class JUnitCompileBeforeTestListener extends JUnitTestListener {
    
    
    public void compileBeforeJUnit(final CompilerListener testAfterCompile) {

      compileBeforeJUnitCount++;
      
      _model.getCompilerModel().addListener(testAfterCompile);  

      try { _model.getCompilerModel().compileAll();   }
      catch(IOException e) { fail("Compile step generated IOException"); }
      

    }
    
    public void saveBeforeCompile() {

      saveBeforeCompileCount++; 
      
      try {
        _model.saveAllFiles(new FileSaveSelector() {
          public File getFile() { throw new UnexpectedException ("Test should not ask for save file name"); }
          public boolean warnFileOpen(File f) { return false; }
          public boolean verifyOverwrite() { return true; }
          public boolean shouldSaveAfterFileMoved(OpenDefinitionsDocument doc, File oldFile) { return false; }
        });
      }
      catch(IOException e) { throw new UnexpectedException(e); }  
    }
    public void fileSaved(OpenDefinitionsDocument doc) { }
  }
}
