 

package edu.rice.cs.drjava.model;

import java.io.*;

import javax.swing.text.BadLocationException;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.OptionConstants;

import edu.rice.cs.util.swing.Utilities;


public final class GlobalModelCompileSuccessOptionsTest extends GlobalModelCompileSuccessTestCase {

  
  public void testCompileReferenceToNonPublicClass() 
    throws BadLocationException, IOException, InterruptedException {

    OpenDefinitionsDocument doc = setupDocument(FOO_NON_PUBLIC_CLASS_TEXT);
    OpenDefinitionsDocument doc2 = setupDocument(FOO2_REFERENCES_NON_PUBLIC_CLASS_TEXT);
    final File file = tempFile();
    final File file2 = tempFile(1);
    saveFile(doc, new FileSelector(file));
    saveFile(doc2, new FileSelector(file2));
    CompileShouldSucceedListener listener = new CompileShouldSucceedListener();
    _model.addListener(listener);
    listener.compile(doc);
    if (_model.getCompilerModel().getNumErrors() > 0) {
      fail("compile failed: " + getCompilerErrorString());
    }
    listener.checkCompileOccurred();
    _model.removeListener(listener);
    CompileShouldSucceedListener listener2 = new CompileShouldSucceedListener();
    _model.addListener(listener2);
    listener2.compile(doc2);
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
      saveFile(doc, new FileSelector(file));
      CompileShouldFailListener listener = new CompileShouldFailListener();
      _model.addListener(listener);
      
      
      listener.compile(doc);
      
      assertCompileErrorsPresent(_name(), true);
      listener.checkCompileOccurred();
      File compiled = classForJava(file, "DrJavaTestFoo");
      assertTrue(_name() + "Class file exists after compile?!", !compiled.exists());
      _model.removeListener(listener);
      
      
      
      String version = System.getProperty("java.version");
      if ((version != null) && ("1.4.0".compareTo(version) <= 0)) {
        
        DrJava.getConfig().setSetting(OptionConstants.RUN_WITH_ASSERT,
                                      Boolean.TRUE);
        
        CompileShouldSucceedListener listener2 = new CompileShouldSucceedListener();
        _model.addListener(listener2);
        listener2.compile(doc);
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
      saveFile(doc, new FileSelector(file));
      
      CompileShouldSucceedListener listener = new CompileShouldSucceedListener();
      _model.addListener(listener);
      _model.getCompilerModel().compileAll();
      Utilities.clearEventQueue();
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
