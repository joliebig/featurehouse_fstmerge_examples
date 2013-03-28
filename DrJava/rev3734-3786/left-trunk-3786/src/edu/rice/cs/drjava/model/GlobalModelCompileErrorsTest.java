

package edu.rice.cs.drjava.model;

import  junit.framework.*;

import java.io.*;

import javax.swing.text.BadLocationException;
import javax.swing.text.Position;

import edu.rice.cs.drjava.model.compiler.*;


public final class GlobalModelCompileErrorsTest extends GlobalModelTestCase {
  
  
  private boolean _compileDone = false;
  
  
  private final Object _compileLock = new Object();
  
  private final CompileShouldFailListener _failListener = new CompileShouldFailListener() {
      public void compileEnded(File workDir, File[] excludedFiles) {
        super.compileEnded(workDir, excludedFiles);
        _compileDone = true;
        synchronized(_compileLock) { _compileLock.notify(); }
      }
    };
  
  private void _waitCompileDone() {
    synchronized(_compileLock) {
      try { while (! _compileDone) _compileLock.wait(); }
      catch (InterruptedException ie) { fail("Unexpected interrupted exception: " + ie.getMessage()); }
    }
  }
  
  private static final String FOO_MISSING_CLOSE_TEXT = "class DrJavaTestFoo {";
  private static final String BAR_MISSING_SEMI_TEXT = "class DrJavaTestBar { int x }";
  private static final String FOO_PACKAGE_AFTER_IMPORT = "import java.util.*;\npackage a;\n" + FOO_TEXT;
  private static final String FOO_PACKAGE_INSIDE_CLASS = "class DrJavaTestFoo { package a; }";
  private static final String FOO_PACKAGE_AS_FIELD = "class DrJavaTestFoo { int package; }";
  private static final String FOO_PACKAGE_AS_FIELD_2 = "class DrJavaTestFoo { int package = 5; }";
  private static final String BAR_MISSING_SEMI_TEXT_MULTIPLE_LINES =
    "class DrJavaTestFoo {\n  int a = 5;\n  int x\n }";
  





  
  
  public void runBare() throws Throwable {
    CompilerInterface[] compilers = CompilerRegistry.ONLY.getAvailableCompilers();
    for (int i = 0; i < compilers.length; i++) {
      
      setUp();
      _model.getCompilerModel().setActiveCompiler(compilers[i]);
      try { runTest();  }
      finally { tearDown(); }
    }
  }

  
  private String _name() { 
    return "compiler=" + _model.getCompilerModel().getActiveCompiler().getName() + ": "; 
  }

  
  public void testCompileAllFailsDifferentSourceRoots() throws BadLocationException, IOException {
    
    File aDir = new File(_tempDir, "a");
    File bDir = new File(_tempDir, "b");
    aDir.mkdir();
    bDir.mkdir();
    
    OpenDefinitionsDocument doc = setupDocument(FOO_MISSING_CLOSE_TEXT);
    final File file = new File(aDir, "DrJavaTestFoo.java");
    doc.saveFile(new FileSelector(file));
                      
    OpenDefinitionsDocument doc2 = setupDocument(BAR_MISSING_SEMI_TEXT);
    final File file2 = new File(bDir, "DrJavaTestBar.java");
    doc2.saveFile(new FileSelector(file2));

    _compileDone = false;
    _model.addListener(_failListener);
    
    CompilerModel cm = _model.getCompilerModel();    
    cm.compileAll();
    _waitCompileDone();

    assertCompileErrorsPresent(_name(), true);
    assertEquals("Should have 2 compiler errors", 2, _model.getCompilerModel().getNumErrors());
    _failListener.checkCompileOccurred();

    
    File compiled = classForJava(file, "DrJavaTestFoo");
    assertEquals(_name() + "Class file exists after failing compile (1)", false, compiled.exists());
    File compiled2 = classForJava(file2, "DrJavaTestBar");
    assertEquals(_name() + "Class file exists after failing compile (2)", false, compiled2.exists());
    _model.removeListener(_failListener);
  }

  
  public void testCompilePackageAsField() throws BadLocationException, IOException {
    OpenDefinitionsDocument doc = setupDocument(FOO_PACKAGE_AS_FIELD);
    final File file = tempFile();
    doc.saveFile(new FileSelector(file));
    
    _compileDone = false;
    _model.addListener(_failListener);
    doc.startCompile();
    _waitCompileDone();
    _failListener.checkCompileOccurred();

    
    assertCompileErrorsPresent(_name(), true);

    File compiled = classForJava(file, "DrJavaTestFoo");
    assertEquals(_name() + "Class file exists after failing compile", false, compiled.exists());
    _model.removeListener(_failListener);
  }

  
  public void testCompilePackageAsField2() throws BadLocationException, IOException {
    OpenDefinitionsDocument doc = setupDocument(FOO_PACKAGE_AS_FIELD_2);
    final File file = tempFile();
    doc.saveFile(new FileSelector(file));

    _compileDone = false;
    _model.addListener(_failListener);
    doc.startCompile();
    _waitCompileDone();
    _failListener.checkCompileOccurred();

    
    assertCompileErrorsPresent(_name(), true);

    File compiled = classForJava(file, "DrJavaTestFoo");
    assertEquals(_name() + "Class file exists after failing compile", false, compiled.exists());
    _model.removeListener(_failListener);
  }

  
  public void testCompileMissingCloseSquiggly() throws BadLocationException, IOException {
    OpenDefinitionsDocument doc = setupDocument(FOO_MISSING_CLOSE_TEXT);
    final File file = tempFile();
    doc.saveFile(new FileSelector(file));
   
    _compileDone = false;
    _model.addListener(_failListener);
    doc.startCompile();
    _waitCompileDone();
    assertCompileErrorsPresent(_name(), true);
    _failListener.checkCompileOccurred();

    File compiled = classForJava(file, "DrJavaTestFoo");
    assertTrue(_name() + "Class file exists after compile?!", !compiled.exists());
    _model.removeListener(_failListener);
  }

  
  public void testCompileWithPackageStatementInsideClass() throws BadLocationException, IOException {
    
    File baseTempDir = tempDirectory();
    File subdir = new File(baseTempDir, "a");
    File fooFile = new File(subdir, "DrJavaTestFoo.java");
    File compiled = classForJava(fooFile, "DrJavaTestFoo");

    
    subdir.mkdir();

    
    OpenDefinitionsDocument doc = setupDocument(FOO_PACKAGE_INSIDE_CLASS);
    doc.saveFileAs(new FileSelector(fooFile));

    
    _compileDone = false;
    _model.addListener(_failListener);
    doc.startCompile();
    _waitCompileDone();

    _failListener.checkCompileOccurred();
    assertCompileErrorsPresent(_name(), true);
    assertTrue(_name() + "Class file exists after failed compile", !compiled.exists());

    
    _model.getCompilerModel().resetCompilerErrors();
    CompilerErrorModel cem = _model.getCompilerModel().getCompilerErrorModel();
    assertEquals("CompilerErrorModel has errors after reset", 0, cem.getNumErrors());
    _model.removeListener(_failListener);
  }
  
   

  
  public void testCompileFailsCorrectLineNumbers() throws BadLocationException, IOException {
    File aDir = new File(_tempDir, "a");
    File bDir = new File(_tempDir, "b");
    aDir.mkdir();
    bDir.mkdir();
    OpenDefinitionsDocument doc = setupDocument(FOO_PACKAGE_AFTER_IMPORT);
    final File file = new File(aDir, "DrJavaTestFoo.java");
    doc.saveFile(new FileSelector(file));
    OpenDefinitionsDocument doc2 = setupDocument(BAR_MISSING_SEMI_TEXT_MULTIPLE_LINES);
    final File file2 = new File(bDir, "DrJavaTestBar.java");
    doc2.saveFile(new FileSelector(file2));    
    _compileDone = false;
    _model.addListener(_failListener);
    CompilerModel cm = _model.getCompilerModel();
    cm.compileAll();
    _waitCompileDone();
    
    assertCompileErrorsPresent(_name(), true);
    assertEquals("Should have 2 compiler errors", 2, _model.getCompilerModel().getNumErrors());
    _failListener.checkCompileOccurred();
    _model.removeListener(_failListener);

    CompilerErrorModel cme = cm.getCompilerErrorModel();
    assertEquals("Should have had two errors", 2, cme.getNumErrors());

    CompilerError ce1 = cme.getError(0);
    CompilerError ce2 = cme.getError(1);
    assertEquals("first doc should have an error", file.getCanonicalFile(), ce1.file().getCanonicalFile());
    assertEquals("second doc should have an error", file2.getCanonicalFile(), ce2.file().getCanonicalFile());

    Position p1 = cme.getPosition(ce1);
    Position p2 = cme.getPosition(ce2);
    assertTrue("location of first error should be between 20 and 29 inclusive (line 2), but was " + p1.getOffset(),
               p1.getOffset() <= 20 && p1.getOffset() <= 29);
    assertTrue("location of error should be after 34 (line 3 or 4)", p2.getOffset() >= 34);
  }
}
