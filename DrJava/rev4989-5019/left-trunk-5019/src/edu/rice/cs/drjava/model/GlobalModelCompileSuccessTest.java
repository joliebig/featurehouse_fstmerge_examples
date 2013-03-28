

package edu.rice.cs.drjava.model;

import edu.rice.cs.drjava.model.definitions.InvalidPackageException; 
import edu.rice.cs.util.swing.Utilities;
import java.io.*;

import javax.swing.text.BadLocationException;


public final class GlobalModelCompileSuccessTest extends GlobalModelCompileSuccessTestCase {

  
  public void testCompileAllDifferentSourceRoots() throws BadLocationException, IOException, InterruptedException {

    File aDir = new File(_tempDir, "a");
    File bDir = new File(_tempDir, "b");
    aDir.mkdir();
    bDir.mkdir();
    OpenDefinitionsDocument doc = setupDocument(FOO_TEXT);
    final File file = new File(aDir, "DrJavaTestFoo.java");
    saveFile(doc, new FileSelector(file));
    OpenDefinitionsDocument doc2 = setupDocument(BAR_TEXT);
    final File file2 = new File(bDir, "DrJavaTestBar.java");
    saveFile(doc2, new FileSelector(file2));
    
    CompileShouldSucceedListener listener = new CompileShouldSucceedListener();
    _model.addListener(listener);
    _model.getCompilerModel().compileAll();
    
    Utilities.clearEventQueue();
    
    if (_model.getCompilerModel().getNumErrors() > 0) {
      fail("compile failed: " + getCompilerErrorString());
    }
    assertCompileErrorsPresent(_name(), false);
    listener.checkCompileOccurred();

    
    File compiled = classForJava(file, "DrJavaTestFoo");
    assertTrue(_name() + "Foo Class file doesn't exist after compile",
               compiled.exists());
    File compiled2 = classForJava(file2, "DrJavaTestBar");
    assertTrue(_name() + "Bar Class file doesn't exist after compile",
               compiled2.exists());
    _model.removeListener(listener);
  }
  

  
  public void testCompileClassPathOKDefaultPackage()
    throws BadLocationException, IOException, InterruptedException
  {

    
    OpenDefinitionsDocument doc1 = setupDocument(FOO_PACKAGE_AS_PART_OF_FIELD);
    final File fooFile = new File(_tempDir, "DrJavaTestFoo.java");
    
    saveFile(doc1, new FileSelector(fooFile));
    CompileShouldSucceedListener listener = new CompileShouldSucceedListener();
    _model.addListener(listener);
    testStartCompile(doc1);
    listener.waitCompileDone();
    if (_model.getCompilerModel().getNumErrors() > 0) {
      fail("compile failed: " + getCompilerErrorString());
    }
    listener.checkCompileOccurred();
    _model.removeListener(listener);

    OpenDefinitionsDocument doc2 = setupDocument(FOO2_EXTENDS_FOO_TEXT);
    final File foo2File = new File(_tempDir, "DrJavaTestFoo2.java");
    saveFile(doc2, new FileSelector(foo2File));

    CompileShouldSucceedListener listener2 = new CompileShouldSucceedListener();
    _model.addListener(listener2);
    testStartCompile(doc2);
    listener2.waitCompileDone();
    if (_model.getCompilerModel().getNumErrors() > 0) {
      fail("compile failed: " + getCompilerErrorString());
    }
    assertCompileErrorsPresent(_name(), false);
    listener2.checkCompileOccurred();

    
    File compiled = classForJava(foo2File, "DrJavaTestFoo2");
    assertTrue(_name() + "Class file doesn't exist after compile",
               compiled.exists());
    _model.removeListener(listener2);
  }

  
  public void testCompileClassPathOKDifferentPackages() throws BadLocationException, IOException, InterruptedException,
    InvalidPackageException {

    File aDir = new File(_tempDir, "a");
    File bDir = new File(_tempDir, "b");
    aDir.mkdir();
    bDir.mkdir();

    
    
    OpenDefinitionsDocument doc1 = setupDocument("package a;\n" + "public " + FOO_TEXT);
    final File fooFile = new File(aDir, "DrJavaTestFoo.java");

    saveFile(doc1, new FileSelector(fooFile));
    
    assertEquals("Check package name of doc1", "a", ((AbstractGlobalModel.ConcreteOpenDefDoc) doc1)._packageName); 


    CompileShouldSucceedListener listener = new CompileShouldSucceedListener();
    _model.addListener(listener);
    
    testStartCompile(doc1);
    
    listener.waitCompileDone();
    if (_model.getCompilerModel().getNumErrors() > 0) {
      fail("compile failed: " + getCompilerErrorString());
    }
    listener.checkCompileOccurred();
    _model.removeListener(listener);
    
    OpenDefinitionsDocument doc2 =
      setupDocument("package b;\nimport a.DrJavaTestFoo;\n" + FOO2_EXTENDS_FOO_TEXT);
    final File foo2File = new File(bDir, "DrJavaTestFoo2.java");

    saveFile(doc2, new FileSelector(foo2File));
    
    assertEquals("Check packangeName of doc2", "b", ((AbstractGlobalModel.ConcreteOpenDefDoc) doc2)._packageName); 


    CompileShouldSucceedListener listener2 = new CompileShouldSucceedListener();
    _model.addListener(listener2);
    
    testStartCompile(doc2);
    listener2.waitCompileDone();
    if (_model.getCompilerModel().getNumErrors() > 0) {
      fail("compile failed: " + getCompilerErrorString());
    }
    assertCompileErrorsPresent(_name(), false);
    listener2.checkCompileOccurred();
    _model.removeListener(listener2);

    
    File compiled = classForJava(foo2File, "DrJavaTestFoo2");
    assertTrue(_name() + "Class file doesn't exist after compile",
               compiled.exists());
  }
}
