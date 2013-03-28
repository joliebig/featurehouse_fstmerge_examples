

package edu.rice.cs.drjava.model.junit;

import edu.rice.cs.drjava.model.GlobalModelTestCase;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.util.swing.Utilities;

import java.io.File;


public final class JUnitErrorModelTest extends GlobalModelTestCase {

  private JUnitErrorModel _m;
  
  private static final String MONKEYTEST_FAIL_TEXT =
    "import junit.framework.*; \n" +
    "import java.io.*; \n" +
    "public class MonkeyTestFail extends TestCase { \n" +
    "  public MonkeyTestFail(String name) { super(name); } \n" +
    "  public void testShouldFail() { \n" +
    "    assertEquals(\"monkey\", \"baboon\"); \n" +
    "  } \n" +
    "  public void testShouldErr() throws Exception { \n" +
    "    throw new IOException(\"Error\"); \n" +
    "  } \n" +
    "}";

  private static final String TEST_ONE =
    "import junit.framework.TestCase;\n" +
    "public class TestOne extends TestCase {\n" +
    "  public void testMyMethod() {\n" +
    "    assertTrue(false);\n" +
    "  }\n" +
    "  public TestOne() {\n" +
    "    super();\n" +
    "  }\n" +
    "  public java.lang.String toString() {\n" +
    "    return \"TestOne(\" + \")\";\n" +
    "  }\n" +
    "  public boolean equals(java.lang.Object o) {\n" +
    "    if ((o == null) || getClass() != o.getClass()) return false;\n" +
    "    return true;\n" +
    "  }\n" +
    "  public int hashCode() {\n" +
    "    return getClass().hashCode();\n" +
    "  }\n" +
    "  public void testThrowing() throws Exception{\n" +
    "    throw new Exception(\"here\");\n" +
    "  }\n" +
    "  public void testFail(){\n" +
    "    fail(\"i just failed the test\");\n" +
    "  }\n" +
    "}";

  private static final String TEST_TWO =
    "import junit.framework.TestCase;\n" +
    "public class TestTwo extends TestOne {\n" +
    "  public void testTwo() {\n" +
    "    assertTrue(true);\n" +
    "  }\n" +
    "  public TestTwo() {\n" +
    "    super();\n" +
    "  }\n" +
    "  public java.lang.String toString() {\n" +
    "    return \"TestTwo(\" + \")\";\n" +
    "  }\n" +
    "  public boolean equals(java.lang.Object o) {\n" +
    "    if ((o == null) || getClass() != o.getClass()) return false;\n" +
    "    return true;\n" +
    "  }\n" +
    "  public int hashCode() {\n" +
    "    return getClass().hashCode();\n" +
    "  }\n" +
    "}";










  private static final String ABC_CLASS_ONE =
    "class ABC extends java.util.Vector {}\n";

  private static final String ABC_CLASS_TWO =
    "class ABC extends java.util.ArrayList {}\n";

  private static final String ABC_TEST =
    "public class ABCTest extends junit.framework.TestCase {\n" +
    "  public void testABC() {\n" +
    "    new ABC().get(0);\n" +
    "  }\n" +
    "}";

  private static final String LANGUAGE_LEVEL_TEST =
    "class MyTest extends junit.framework.TestCase {\n"+
    "  void testMyMethod() {\n"+
    "    assertEquals(\"OneString\", \"TwoStrings\");\n"+
    "  }\n"+
    "}\n";

  
  public void testErrorsArrayInOrder() throws Exception {
    _m = new JUnitErrorModel(new JUnitError[0], _model, false);
    OpenDefinitionsDocument doc = setupDocument(MONKEYTEST_FAIL_TEXT);
    final File file = new File(_tempDir, "MonkeyTestFail.java");
    doc.saveFile(new FileSelector(file));

    JUnitTestListener listener = new JUnitTestListener();
    _model.addListener(listener);
    doc.startCompile();
    if (_model.getCompilerModel().getNumErrors() > 0) fail("compile failed: " + getCompilerErrorString());
    listener.checkCompileOccurred();
    
    _runJUnit(doc);
    
    listener.assertJUnitStartCount(1);
    
    _model.getJUnitModel().getJUnitDocument().remove
      (0, _model.getJUnitModel().getJUnitDocument().getLength() - 1);
    

    
    _m = _model.getJUnitModel().getJUnitErrorModel();

    
    
    

    assertEquals("the test results should have one error and one failure "+_m.getNumErrors(), 2, _m.getNumErrors());

    assertEquals("test case has one error reported" + _m.getError(0).message(), _m.getError(0).isWarning(), false);

    assertEquals("test case has one failure reported" + _m.getError(1).message(), _m.getError(1).isWarning(), true);
    
  }

  
  public void testVerifyErrorHandledCorrectly() throws Exception {
    OpenDefinitionsDocument doc = setupDocument(ABC_CLASS_ONE);
    final File file = new File(_tempDir, "ABC1.java");
    doc.saveFile(new FileSelector(file));

    OpenDefinitionsDocument doc2 = setupDocument(ABC_TEST);
    final File file2 = new File(_tempDir, "ABCTest.java");
    doc2.saveFile(new FileSelector(file2));

    


    _model.getCompilerModel().compileAll();

    OpenDefinitionsDocument doc3 = setupDocument(ABC_CLASS_TWO);
    final File file3 = new File(_tempDir, "ABC2.java");
    doc3.saveFile(new FileSelector(file3));

    JUnitTestListener listener = new JUnitNonTestListener();
    

    doc3.startCompile();
    if (_model.getCompilerModel().getNumErrors() > 0) {
      fail("compile failed: " + getCompilerErrorString());
    }
    _model.addListener(listener);
    



    listener.assertClassFileErrorCount(0);
    _runJUnit(doc2);
    double version = Double.valueOf(System.getProperty("java.specification.version"));
    if (version < 1.5) listener.assertClassFileErrorCount(1);
    else 
      assertEquals("Should report one error", 1, _model.getJUnitModel().getJUnitErrorModel().getNumErrors());
    
    _model.removeListener(listener);
  }


  

  public void testLanguageLevelJUnitErrorLine() throws Exception {
    
    _m = new JUnitErrorModel(new JUnitError[0], _model, false);
    OpenDefinitionsDocument doc = setupDocument(LANGUAGE_LEVEL_TEST);
    final File file = new File(_tempDir, "MyTest.dj0");
    doc.saveFile(new FileSelector(file));

    JUnitTestListener listener = new JUnitTestListener();
    _model.addListener(listener);
    Utilities.clearEventQueue();
    doc.startCompile();
    if (_model.getCompilerModel().getNumErrors() > 0) {
      fail("compile failed: " + getCompilerErrorString());
    }
    listener.checkCompileOccurred();
    
    _runJUnit(doc);
    
    listener.assertJUnitStartCount(1);

    
    _model.getJUnitModel().getJUnitDocument().remove(0, _model.getJUnitModel().getJUnitDocument().getLength() - 1);

    _m = _model.getJUnitModel().getJUnitErrorModel();

    assertEquals("the test results should have one failure "+_m.getNumErrors(), 1, _m.getNumErrors());

    assertEquals("the error line should be line number 2", 2, _m.getError(0).lineNumber());
    
  }


  
  public void testErrorInSuperClass() throws Exception {
    OpenDefinitionsDocument doc1 = setupDocument(TEST_ONE);
    OpenDefinitionsDocument doc2 = setupDocument(TEST_TWO);
    final File file1 = new File(_tempDir, "TestOne.java");
    final File file2 = new File(_tempDir, "TestTwo.java");
    doc1.saveFile(new FileSelector(file1));
    doc2.saveFile(new FileSelector(file2));
    JUnitTestListener listener = new JUnitTestListener();
    _model.addListener(listener);
    _model.getCompilerModel().compileAll();


    
    
    _runJUnit(doc1);
    
    listener.assertJUnitStartCount(1);
    
    _m = _model.getJUnitModel().getJUnitErrorModel();
    
    assertEquals("test case has one error reported", 3, _m.getNumErrors());
    assertTrue("first error should be an error not a warning", !_m.getError(0).isWarning());

    assertTrue("it's a junit error", _m.getError(0) instanceof JUnitError);

    assertEquals("The first error is on line 5", 3, _m.getError(0).lineNumber());
    assertEquals("The first error is on line 5", 19, _m.getError(1).lineNumber());
    assertEquals("The first error is on line 5", 22, _m.getError(2).lineNumber());
    
    _runJUnit(doc2);
    
    listener.assertJUnitStartCount(2);
    
    assertEquals("test case has one error reported", 3, _m.getNumErrors());
    assertTrue("first error should be an error not a warning", !_m.getError(0).isWarning());
    assertEquals("The first error is on line 5", 3, _m.getError(0).lineNumber());
    assertEquals("The first error is on line 5", 19, _m.getError(1).lineNumber());
    assertEquals("The first error is on line 5", 22, _m.getError(2).lineNumber());

    _model.removeListener(listener);
    
  }
}

