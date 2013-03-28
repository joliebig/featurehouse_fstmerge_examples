

package edu.rice.cs.drjava.model;

import java.io.*;

import javax.swing.text.BadLocationException;


import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.util.text.EditDocumentException;


public final class GlobalModelCompileTest extends GlobalModelTestCase {
  
  public void testCompileAllWithNoFiles() throws BadLocationException, IOException, InterruptedException {
    
    _model.newFile();
    
    CompileShouldSucceedListener listener = new CompileShouldSucceedListener(false);
    _model.addListener(listener);
    _model.getCompilerModel().compileAll();
    if (_model.getCompilerModel().getNumErrors() > 0) {
      fail("compile failed: " + getCompilerErrorString());
    }
    assertCompileErrorsPresent("compile should succeed", false);
    listener.checkCompileOccurred();
    _model.removeListener(listener);
  }

  
  public void testCompileResetsInteractions() throws BadLocationException, IOException, InterruptedException,
    EditDocumentException {
    

    
    OpenDefinitionsDocument doc = setupDocument(FOO_TEXT);
    final File file = new File(_tempDir, "DrJavaTestFoo.java");
    doc.saveFile(new FileSelector(file));
    
    
    interpret("0");

    
    CompileShouldSucceedListener listener = new CompileShouldSucceedListener(true);
    _model.setResetAfterCompile(true);
    _model.addListener(listener);
    synchronized(listener) {
      _model.getCompilerModel().compileAll();
      if (_model.getCompilerModel().getNumErrors() > 0) {

        fail("compile failed: " + getCompilerErrorString());
      }
      while (listener.notDone()) listener.wait();
    }

    assertCompileErrorsPresent("compile should succeed", false);
    listener.checkCompileOccurred();
 

    _model.removeListener(listener);

  }

  
  public void testCompileAbortsIfUnsaved() throws BadLocationException, IOException {
    final OpenDefinitionsDocument doc = setupDocument(FOO_TEXT);

    TestListener listener = new TestListener() {
      public void saveBeforeCompile() {
        assertModified(true, doc);
        saveBeforeCompileCount++;
        
      }
    };

    _model.addListener(listener);
    doc.startCompile();
    listener.assertSaveBeforeCompileCount(1);
    assertModified(true, doc);
    assertContents(FOO_TEXT, doc);
    _model.removeListener(listener);
  }

  
  public void testCompileAbortsIfAnyUnsaved() throws BadLocationException, IOException {
    final OpenDefinitionsDocument doc = setupDocument(FOO_TEXT);
    final OpenDefinitionsDocument doc2 = setupDocument(BAR_TEXT);

    TestListener listener = new TestListener() {
      public void saveBeforeCompile() {
        assertModified(true, doc);
        assertModified(true, doc2);
        saveBeforeCompileCount++;
        
      }
    };

    _model.addListener(listener);
    doc.startCompile();
    listener.assertSaveBeforeCompileCount(1);
    assertModified(true, doc);
    assertModified(true, doc2);
    assertContents(FOO_TEXT, doc);
    assertContents(BAR_TEXT, doc2);
    _model.removeListener(listener);
  }

  
  public void testCompileAnyUnsavedButSaveWhenAsked() throws BadLocationException, IOException, InterruptedException {
    final OpenDefinitionsDocument doc = setupDocument(FOO_TEXT);
    final OpenDefinitionsDocument doc2 = setupDocument(BAR_TEXT);
    final File file = tempFile();
    final File file2 = tempFile(2);

    CompileShouldSucceedListener listener = new CompileShouldSucceedListener(false) {
      public void saveBeforeCompile() {
        assertModified(true, doc);
        assertModified(true, doc2);
        assertSaveCount(0);
        assertCompileStartCount(0);
        assertCompileEndCount(0);
        assertInterpreterReadyCount(0);
        assertConsoleResetCount(0);

        try {
          doc.saveFile(new FileSelector(file));
          doc2.saveFile(new FileSelector(file2));
        }
        catch (IOException ioe) {
          fail("Save produced exception: " + ioe);
        }

        saveBeforeCompileCount++;
      }

      public void fileSaved(OpenDefinitionsDocument doc) {
        assertModified(false, doc);
        assertSaveBeforeCompileCount(0);
        assertCompileStartCount(0);
        assertCompileEndCount(0);
        assertInterpreterReadyCount(0);
        assertConsoleResetCount(0);

        try { doc.getFile(); }
        catch (FileMovedException fme) {
          
          fail("file does not exist");
        }
        
        saveCount++;
      }
    };

    _model.addListener(listener);
    doc.startCompile();
    if (_model.getCompilerModel().getNumErrors() > 0) {
      fail("compile failed: " + getCompilerErrorString());
    }

    
    listener.assertSaveBeforeCompileCount(1);
    listener.assertSaveCount(2);
    assertCompileErrorsPresent("compile should succeed", false);
    listener.checkCompileOccurred();

    
    File compiled = classForJava(file, "DrJavaTestFoo");
    assertTrue("Class file doesn't exist after compile", compiled.exists());
    _model.removeListener(listener);
  }

  
  public void testCompileActiveSavedAnyUnsavedButSaveWhenAsked()
    throws BadLocationException, IOException, InterruptedException
  {
    final OpenDefinitionsDocument doc = setupDocument(FOO_TEXT);
    final OpenDefinitionsDocument doc2 = setupDocument(BAR_TEXT);
    final File file = tempFile();
    final File file2 = tempFile(1);

    CompileShouldSucceedListener listener = new CompileShouldSucceedListener(false) {
      public void saveBeforeCompile() {
        assertModified(false, doc);
        assertModified(true, doc2);
        assertSaveCount(0);
        assertCompileStartCount(0);
        assertCompileEndCount(0);
        assertInterpreterReadyCount(0);
        assertConsoleResetCount(0);

        try {
          doc2.saveFile(new FileSelector(file2));
        }
        catch (IOException ioe) {
          fail("Save produced exception: " + ioe);
        }

        saveBeforeCompileCount++;
        assertModified(false, doc);
        assertModified(false, doc2);
        assertTrue(!_model.hasModifiedDocuments());
      }

      public void fileSaved(OpenDefinitionsDocument doc) {
        assertModified(false, doc);
        assertSaveBeforeCompileCount(0);
        assertCompileStartCount(0);
        assertCompileEndCount(0);
        assertInterpreterReadyCount(0);
        assertConsoleResetCount(0);

        File f = null;
        try { f = doc.getFile(); }
        catch (FileMovedException fme) {
          
          fail("file does not exist");
        }
        assertEquals("file saved", file2, f);
        saveCount++;
      }
    };

    assertModified(true, doc);
    doc.saveFile(new FileSelector(file));
    assertModified(false, doc);
    assertModified(true, doc2);
    _model.addListener(listener);
    doc.startCompile();
    if (_model.getCompilerModel().getNumErrors() > 0) {
      fail("compile failed: " + getCompilerErrorString());
    }
    assertTrue(!_model.hasModifiedDocuments());

    
    listener.assertCompileStartCount(1);
    listener.assertSaveBeforeCompileCount(1);
    listener.assertSaveCount(1);
    assertCompileErrorsPresent("compile should succeed", false);
    listener.checkCompileOccurred();

    
    File compiled = classForJava(file, "DrJavaTestFoo");
    assertTrue("Class file doesn't exist after compile", compiled.exists());
    _model.removeListener(listener);
  }


}
