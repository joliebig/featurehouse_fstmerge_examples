

package edu.rice.cs.drjava.model;

import java.io.*;
import edu.rice.cs.util.FileOps;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.swing.Utilities;

import javax.swing.text.BadLocationException;


public final class GlobalModelCompileIOTest extends GlobalModelTestCase {
  
  
  public void testClassFileSynchronization() throws BadLocationException, IOException, InterruptedException {
    final OpenDefinitionsDocument doc = setupDocument(FOO_TEXT);
    final File file = tempFile();

    
    saveFile(doc, new FileSelector(file));
    
    CompileShouldSucceedListener listener = new CompileShouldSucceedListener();
    _model.addListener(listener);

    assertTrue("Class file should not exist before compile", doc.getCachedClassFile() == FileOps.NULL_FILE);
    assertTrue("should not be in sync before compile", ! doc.checkIfClassFileInSync());
    assertTrue("The state of all open documents should be out of sync", _model.hasOutOfSyncDocuments());
    testStartCompile(doc);
    listener.waitCompileDone();
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
    
    saveFile(doc, new FileSelector(file));
    assertTrue("should not be in sync after save", ! doc.checkIfClassFileInSync());
    
    
    File compiled = classForJava(file, "DrJavaTestFoo");
    assertTrue(" Class file should exist after compile", compiled.exists());
  }
  
  
  public void testClassFileSynchronizationAfterRename() throws BadLocationException, IOException, IllegalStateException,
    InterruptedException {
    
    final OpenDefinitionsDocument doc = setupDocument(FOO_TEXT);
    final File file = tempFile();
    final File file2 = tempFile(2);
    
    saveFile(doc, new FileSelector(file));
    
    CompileShouldSucceedListener listener = new CompileShouldSucceedListener();
    _model.addListener(listener);

    assertTrue("Class file should not exist before compile", doc.getCachedClassFile() == FileOps.NULL_FILE);
    assertTrue("should not be in sync before compile", !doc.checkIfClassFileInSync());
    testStartCompile(doc);
    listener.waitCompileDone();
    if (_model.getCompilerModel().getNumErrors() > 0) {
      fail("compile failed: " + getCompilerErrorString());
    }
    _model.removeListener(listener);
    listener.checkCompileOccurred();
    assertTrue("should be in sync after compile",
               doc.checkIfClassFileInSync());
    
    
    Thread.sleep(2000);
    
    
    saveFileAs(doc, new FileSelector(file2));
    assertTrue("should not be in sync after renaming", ! doc.checkIfClassFileInSync());
  }
  
  
  public void testCompileAfterFileMoved() throws BadLocationException, IOException {
    final OpenDefinitionsDocument doc = setupDocument(FOO_TEXT);
    final File file = tempFile();
    saveFile(doc, new FileSelector(file));
    TestListener listener = new TestListener();
    _model.addListener(listener);
    file.delete();
    Utilities.invokeLater(new Runnable() { 
      public void run() {
        try {
          doc.startCompile();
          fail("Compile should not have begun.");
        }
        catch(FileMovedException e) {  }
        catch(Exception e) { throw new UnexpectedException(e); }
      }
    });

    assertCompileErrorsPresent("compile should succeed", false);
    
    
    File compiled = classForJava(file, "DrJavaTestFoo");
    assertTrue("Class file shouldn't exist after compile", !compiled.exists());
    _model.removeListener(listener);
  }
  
}
