

package edu.rice.cs.drjava.model;

import java.io.*;

import javax.swing.text.BadLocationException;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.OptionConstants;


public final class GlobalModelCompileSuccessOptionsTest extends GlobalModelCompileSuccessTestCase {

  
  public void testCompileReferenceToNonPublicClass() 
    throws BadLocationException, IOException, InterruptedException {

    OpenDefinitionsDocument doc = setupDocument(FOO_NON_PUBLIC_CLASS_TEXT);
    OpenDefinitionsDocument doc2 = setupDocument(FOO2_REFERENCES_NON_PUBLIC_CLASS_TEXT);
    final File file = tempFile();
    final File file2 = tempFile(1);
    doc.saveFile(new FileSelector(file));
    doc2.saveFile(new FileSelector(file2));
    CompileShouldSucceedListener listener = new CompileShouldSucceedListener(false);
    _model.addListener(listener);
    doc.startCompile();
    if (_model.getCompilerModel().getNumErrors() > 0) {
      fail("compile failed: " + getCompilerErrorString());
    }
    listener.checkCompileOccurred();
    _model.removeListener(listener);
    CompileShouldSucceedListener listener2 = new CompileShouldSucceedListener(false);
    _model.addListener(listener2);
    doc2.startCompile();
    if (_model.getCompilerModel().getNumErrors() > 0) {
      fail("compile failed: " + getCompilerErrorString());
    }    
    
    listener2.checkCompileOccurred();
    _model.removeListener(listener2);
    assertCompileErrorsPresent(_name(), false);
    
    
    File compiled = classForJava(file, "DrJavaTestFoo");
    File compiled2 = classForJava(file, "DrJavaTestFoo2");
    assertTrue(_name() + "Class file should exist after compile", compiled.exists());
    assertTrue(_name() + "Class file should exist after compile", compiled2.exists());
  }
  
  
  public void testCompileWithJavaAssert()
    throws BadLocationException, IOException, InterruptedException {

    
    if (Float.valueOf(System.getProperty("java.specification.version")) < 1.5) {
      OpenDefinitionsDocument doc = setupDocument(FOO_WITH_ASSERT);
      final File file = tempFile();
      doc.saveFile(new FileSelector(file));
      CompileShouldFailListener listener = new CompileShouldFailListener();
      _model.addListener(listener);
      
      
      doc.startCompile();
      
      assertCompileErrorsPresent(_name(), true);
      listener.checkCompileOccurred();
      File compiled = classForJava(file, "DrJavaTestFoo");
      assertTrue(_name() + "Class file exists after compile?!", !compiled.exists());
      _model.removeListener(listener);
      
      
      
      String version = System.getProperty("java.version");
      if ((version != null) && ("1.4.0".compareTo(version) <= 0)) {
        
        DrJava.getConfig().setSetting(OptionConstants.JAVAC_ALLOW_ASSERT,
                                      Boolean.TRUE);
        
        CompileShouldSucceedListener listener2 = new CompileShouldSucceedListener(false);
        _model.addListener(listener2);
        doc.startCompile();
        if (_model.getCompilerModel().getNumErrors() > 0) {
          fail("compile failed: " + getCompilerErrorString());
        }
        _model.removeListener(listener2);
        assertCompileErrorsPresent(_name(), false);
        listener2.checkCompileOccurred();
        
        
        compiled = classForJava(file, "DrJavaTestFoo");
        assertTrue(_name() + "Class file doesn't exist after compile",
                   compiled.exists());
      }
    }
  }

  
  public void testCompileWithGenerics()
    throws BadLocationException, IOException, InterruptedException
  {

    
    if (_isGenericCompiler()) {
      
      OpenDefinitionsDocument doc = setupDocument(FOO_WITH_GENERICS);
      final File file = new File(_tempDir, "DrJavaTestFooGenerics.java");
      doc.saveFile(new FileSelector(file));
      
      CompileShouldSucceedListener listener = new CompileShouldSucceedListener(false);
      _model.addListener(listener);
      _model.getCompilerModel().compileAll();
      if (_model.getCompilerModel().getNumErrors() > 0) {
        fail("compile failed: " + getCompilerErrorString());
      }
      assertCompileErrorsPresent(_name(), false);
      listener.checkCompileOccurred();
      _model.removeListener(listener);
      
      
      File compiled = classForJava(file, "DrJavaTestFooGenerics");
      assertTrue(_name() + "FooGenerics Class file doesn't exist after compile", compiled.exists());
    }
  }
}
