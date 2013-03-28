

package edu.rice.cs.drjava.model;

import java.io.*;

import javax.swing.text.BadLocationException;


public final class GlobalModelCompileIOTest extends GlobalModelTestCase {
  
  
  public void testClassFileSynchronization() throws BadLocationException, IOException, InterruptedException {
    final OpenDefinitionsDocument doc = setupDocument(FOO_TEXT);
    final File file = tempFile();

    doc.saveFile(new FileSelector(file));

    CompileShouldSucceedListener listener = new CompileShouldSucceedListener(false);
    _model.addListener(listener);
    assertTrue("Class file should not exist before compile", doc.getCachedClassFile() == null);
    assertTrue("should not be in sync before compile", ! doc.checkIfClassFileInSync());
    assertTrue("The state of all open documents should be out of sync", _model.hasOutOfSyncDocuments());
    doc.startCompile();
    if (_model.getCompilerModel().getNumErrors() > 0) {
      fail("compile failed: " + getCompilerErrorString());
    }
    _model.removeListener(listener);
    listener.checkCompileOccurred();
    assertTrue("should be in sync after compile", doc.checkIfClassFileInSync());

    assertTrue("The state of all open documents should be in sync", ! _model.hasOutOfSyncDocuments());
    doc.insertString(0, "hi", null);
    assertTrue("should not be in sync after modification", ! doc.checkIfClassFileInSync());

    
    Thread.sleep(2000);

    doc.saveFile(new FileSelector(file));
    assertTrue("should not be in sync after save", ! doc.checkIfClassFileInSync());

    
    File compiled = classForJava(file, "DrJavaTestFoo");
    assertTrue(" Class file should exist after compile", compiled.exists());
  }

  
  public void testClassFileSynchronizationAfterRename()
    throws BadLocationException, IOException, IllegalStateException,
    InterruptedException
  {
    final OpenDefinitionsDocument doc = setupDocument(FOO_TEXT);
    final File file = tempFile();
    final File file2 = tempFile(2);

    doc.saveFile(new FileSelector(file));

    CompileShouldSucceedListener listener = new CompileShouldSucceedListener(false);
    _model.addListener(listener);
    assertTrue("Class file should not exist before compile",
               doc.getCachedClassFile() == null);
    assertTrue("should not be in sync before compile",
               !doc.checkIfClassFileInSync());
    doc.startCompile();
    if (_model.getCompilerModel().getNumErrors() > 0) {
      fail("compile failed: " + getCompilerErrorString());
    }
    _model.removeListener(listener);
    listener.checkCompileOccurred();
    assertTrue("should be in sync after compile",
               doc.checkIfClassFileInSync());

    
    Thread.sleep(2000);

    
    doc.saveFileAs(new FileSelector(file2));
    assertTrue("should not be in sync after renaming", ! doc.checkIfClassFileInSync());
  }

  
  public void testCompileAfterFileMoved() throws BadLocationException, IOException {
    OpenDefinitionsDocument doc = setupDocument(FOO_TEXT);
    final File file = tempFile();
    doc.saveFile(new FileSelector(file));
    TestListener listener = new TestListener();
    _model.addListener(listener);
    file.delete();
    try {
      doc.startCompile();
      fail("Compile should not have begun.");
    }
    catch (FileMovedException fme) {
      
      
    }

    assertCompileErrorsPresent("compile should succeed", false);

    
    File compiled = classForJava(file, "DrJavaTestFoo");
    assertTrue("Class file shouldn't exist after compile", !compiled.exists());
    _model.removeListener(listener);
  }

}
