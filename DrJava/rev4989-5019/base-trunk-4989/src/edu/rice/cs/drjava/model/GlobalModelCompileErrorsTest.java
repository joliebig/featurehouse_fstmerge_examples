

package edu.rice.cs.drjava.model;

import java.io.*;

import javax.swing.text.BadLocationException;
import javax.swing.text.Position;

import edu.rice.cs.drjava.model.compiler.*;

import static edu.rice.cs.plt.debug.DebugUtil.debug;


public final class GlobalModelCompileErrorsTest extends GlobalModelTestCase {
  
  private static final String FOO_MISSING_CLOSE_TEXT = "class DrJavaTestFoo {";
  private static final String BAR_MISSING_SEMI_TEXT = "class DrJavaTestBar { int x }";
  private static final String FOO_PACKAGE_AFTER_IMPORT = "import java.util.*;\npackage a;\n" + FOO_TEXT;
  private static final String FOO_PACKAGE_INSIDE_CLASS = "class DrJavaTestFoo { package a; }";
  private static final String FOO_PACKAGE_AS_FIELD = "class DrJavaTestFoo { int package; }";
  private static final String FOO_PACKAGE_AS_FIELD_2 = "class DrJavaTestFoo { int package = 5; }";
  private static final String BAR_MISSING_SEMI_TEXT_MULTIPLE_LINES =
    "class DrJavaTestFoo {\n  int a = 5;\n  int x\n }";
  





  













  
  
  private String _name() { 
    return "compiler=" + _model.getCompilerModel().getActiveCompiler().getName() + ": "; 
  }
  
  
  public void testCompileAllFailsDifferentSourceRoots() throws BadLocationException, IOException, InterruptedException {
    debug.logStart();
    
    File aDir = new File(_tempDir, "a");
    File bDir = new File(_tempDir, "b");
    aDir.mkdir();
    bDir.mkdir();
    
    OpenDefinitionsDocument doc1 = setupDocument(FOO_MISSING_CLOSE_TEXT);
    final File file1 = new File(aDir, "DrJavaTestFoo.java");
    saveFile(doc1, new FileSelector(file1));  
    
    OpenDefinitionsDocument doc2 = setupDocument(BAR_MISSING_SEMI_TEXT);
    final File file2 = new File(bDir, "DrJavaTestBar.java");
    saveFile(doc2, new FileSelector(file2));  
    
    CompileShouldFailListener listener = new CompileShouldFailListener();
    
    _model.addListener(listener);
    
    CompilerModel cm = _model.getCompilerModel();    
    cm.compileAll();
    listener.waitCompileDone();
    
    assertCompileErrorsPresent(_name(), true);

    assertEquals("Should have 2 compiler errors", 2, cm.getNumErrors());
    listener.checkCompileOccurred();
    
    
    File compiled1 = classForJava(file1, "DrJavaTestFoo");
    assertEquals(_name() + "Class file exists after failing compile (1)", false, compiled1.exists());
    File compiled2 = classForJava(file2, "DrJavaTestBar");
    assertEquals(_name() + "Class file exists after failing compile (2)", false, compiled2.exists());
    _model.removeListener(listener);
    
    debug.logEnd();
  }
  
  
  public void testCompilePackageAsField() throws BadLocationException, IOException, InterruptedException {
    debug.logStart();
    
    OpenDefinitionsDocument doc = setupDocument(FOO_PACKAGE_AS_FIELD);
    final File file = tempFile();
    saveFile(doc,new FileSelector(file));
    
    CompileShouldFailListener listener = new CompileShouldFailListener();
    _model.addListener(listener);
    
    testStartCompile(doc);
    
    listener.waitCompileDone();
    listener.checkCompileOccurred();
    
    
    assertCompileErrorsPresent(_name(), true);
    
    File compiled = classForJava(file, "DrJavaTestFoo");
    assertEquals(_name() + "Class file exists after failing compile", false, compiled.exists());
    _model.removeListener(listener);
    
    debug.logEnd();
  }
  
  
  public void testCompilePackageAsField2() throws BadLocationException, IOException, InterruptedException {
    debug.logStart();
    
    final OpenDefinitionsDocument doc = setupDocument(FOO_PACKAGE_AS_FIELD_2);
    final File file = tempFile();
    saveFile(doc, new FileSelector(file));
    
    CompileShouldFailListener listener = new CompileShouldFailListener();
    _model.addListener(listener);
    
    testStartCompile(doc);

    listener.waitCompileDone();
    listener.checkCompileOccurred();
    
    
    assertCompileErrorsPresent(_name(), true);
    
    File compiled = classForJava(file, "DrJavaTestFoo");
    assertEquals(_name() + "Class file exists after failing compile", false, compiled.exists());
    _model.removeListener(listener);
    
    debug.logEnd();
  }
  
  
  public void testCompileMissingCloseCurly() throws BadLocationException, IOException, InterruptedException {
    debug.logStart();
    
    final OpenDefinitionsDocument doc = setupDocument(FOO_MISSING_CLOSE_TEXT);
    final File file = tempFile();
    saveFile(doc, new FileSelector(file));
    
    CompileShouldFailListener listener = new CompileShouldFailListener();
    _model.addListener(listener);
    
    testStartCompile(doc);
    
    listener.waitCompileDone();
    assertCompileErrorsPresent(_name(), true);
    listener.checkCompileOccurred();
    
    File compiled = classForJava(file, "DrJavaTestFoo");
    assertTrue(_name() + "Class file exists after compile?!", !compiled.exists());
    _model.removeListener(listener);
    
    debug.logEnd();
  }
  
  
  public void testCompileWithPackageStatementInsideClass() throws BadLocationException, IOException, 
    InterruptedException {
    debug.logStart();
    
    
    File baseTempDir = tempDirectory();
    File subdir = new File(baseTempDir, "a");
    File fooFile = new File(subdir, "DrJavaTestFoo.java");
    File compiled = classForJava(fooFile, "DrJavaTestFoo");
    
    
    subdir.mkdir();
    
    
    OpenDefinitionsDocument doc = setupDocument(FOO_PACKAGE_INSIDE_CLASS);
    saveFileAs(doc, new FileSelector(fooFile));
    
    
    CompileShouldFailListener listener = new CompileShouldFailListener();
    _model.addListener(listener);
    
    testStartCompile(doc);
    listener.waitCompileDone();
    
    listener.checkCompileOccurred();
    assertCompileErrorsPresent(_name(), true);
    assertTrue(_name() + "Class file exists after failed compile", !compiled.exists());
    
    
    _model.getCompilerModel().resetCompilerErrors();
    CompilerErrorModel cem = _model.getCompilerModel().getCompilerErrorModel();
    assertEquals("CompilerErrorModel has errors after reset", 0, cem.getNumErrors());
    _model.removeListener(listener);
    
    debug.logEnd();
  }
  
  
  
  
  public void testCompileFailsCorrectLineNumbers() throws BadLocationException, IOException, InterruptedException {
    debug.logStart();
    
    File aDir = new File(_tempDir, "a");
    File bDir = new File(_tempDir, "b");
    aDir.mkdir();
    bDir.mkdir();
    OpenDefinitionsDocument doc = setupDocument(FOO_PACKAGE_AFTER_IMPORT);
    final File file = new File(aDir, "DrJavaTestFoo.java");
    saveFile(doc, new FileSelector(file));
    OpenDefinitionsDocument doc2 = setupDocument(BAR_MISSING_SEMI_TEXT_MULTIPLE_LINES);
    final File file2 = new File(bDir, "DrJavaTestBar.java");
    saveFile(doc2, new FileSelector(file2));
    
    
    CompileShouldFailListener listener = new CompileShouldFailListener();
    _model.addListener(listener);
    
    CompilerModel cm = _model.getCompilerModel();
    cm.compileAll();
    debug.log("Before wait");
    listener.waitCompileDone();
    debug.log("After wait");
    
    assertCompileErrorsPresent(_name(), true);
    assertEquals("Should have 2 compiler errors", 2, _model.getCompilerModel().getNumErrors());
    listener.checkCompileOccurred();
    _model.removeListener(listener);
    
    CompilerErrorModel cme = cm.getCompilerErrorModel();
    assertEquals("Should have had two errors", 2, cme.getNumErrors());
    
    DJError ce1 = cme.getError(0);
    DJError ce2 = cme.getError(1);
    assertEquals("first doc should have an error", file.getCanonicalFile(), ce1.file().getCanonicalFile());
    assertEquals("second doc should have an error", file2.getCanonicalFile(), ce2.file().getCanonicalFile());
    
    Position p1 = cme.getPosition(ce1);
    Position p2 = cme.getPosition(ce2);
    assertTrue("location of first error should be between 20 and 29 inclusive (line 2), but was " + p1.getOffset(),
               p1.getOffset() <= 20 && p1.getOffset() <= 29);
    assertTrue("location of error should be after 34 (line 3 or 4)", p2.getOffset() >= 34);
    
    debug.logEnd();
  }
}
