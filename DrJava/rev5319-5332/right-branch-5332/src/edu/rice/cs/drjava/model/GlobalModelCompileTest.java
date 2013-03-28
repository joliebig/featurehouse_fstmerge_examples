

package edu.rice.cs.drjava.model;

import java.io.*;

import javax.swing.text.BadLocationException;


import edu.rice.cs.util.Log;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.util.text.EditDocumentException;


public final class GlobalModelCompileTest extends GlobalModelTestCase {
  protected static final Log _log  = new Log("GlobalModelCompileTest.txt", false);
  
  
  public void testCompileAllWithNoFiles() throws BadLocationException, IOException, InterruptedException {
    
    _model.newFile();
    
    CompileShouldSucceedListener listener = new CompileShouldSucceedListener();
    _model.addListener(listener);
    Utilities.invokeLater(new Runnable() { 
      public void run() { 
        try { _model.getCompilerModel().compileAll(); } 
        catch(Exception e) { throw new UnexpectedException(e); }
      }
    });
    listener.waitCompileDone();
    if (_model.getCompilerModel().getNumErrors() > 0) {
      fail("compile failed: " + getCompilerErrorString());
    }
    assertCompileErrorsPresent("compile should succeed", false);
    listener.checkCompileOccurred();
    _model.removeListener(listener);
    _log.log("testCompileAllWithNoFiles complete");
  }
  
  
  public void testCompileResetsInteractions() throws BadLocationException, IOException, InterruptedException,
    EditDocumentException {
    

    
    OpenDefinitionsDocument doc = setupDocument(FOO_TEXT);
    final File file = new File(_tempDir, "DrJavaTestFoo.java");
    saveFile(doc, new FileSelector(file));
    
    
    interpret("0");
    
    CompileShouldSucceedListener listener = new CompileShouldSucceedListener();
    _model.setResetAfterCompile(true);
    _model.addListener(listener);
     Utilities.invokeLater(new Runnable() { 
      public void run() { 
        try { _model.getCompilerModel().compileAll(); } 
        catch(Exception e) { throw new UnexpectedException(e); }
      }
    });
    listener.waitCompileDone();
    
    if (_model.getCompilerModel().getNumErrors() > 0) {

      fail("compile failed: " + getCompilerErrorString());
    }
    listener.waitResetDone();
    _log.log("reset confirmed");

    assertCompileErrorsPresent("compile should succeed", false);
    listener.checkCompileOccurred();
    

    _model.removeListener(listener);

    _log.log("testCompileResetsInteractions complete");
  }
  
  
  public void testCompileAbortsIfUnsaved() throws Exception {
    final OpenDefinitionsDocument doc = setupDocument(FOO_TEXT);
    
    CompileShouldSucceedListener listener = new CompileShouldSucceedListener() {
      public void saveBeforeCompile() {
        assertModified(true, doc);
        synchronized(this) { saveBeforeCompileCount++; }
        
      }
    };
    
    _model.addListener(listener);
    listener.compile(doc);
    _log.log("critical compile complete");
    listener.assertSaveBeforeCompileCount(1);
    assertModified(true, doc);
    assertContents(FOO_TEXT, doc);
    _model.removeListener(listener);
    _log.log("testCompileAbortsIfUnsaved complete");
  }
  
  
  public void testCompileAbortsIfAnyUnsaved() throws Exception {
    final OpenDefinitionsDocument doc = setupDocument(FOO_TEXT);
    final OpenDefinitionsDocument doc2 = setupDocument(BAR_TEXT);
    
    CompileShouldFailListener listener = new CompileShouldFailListener() {
      public void saveBeforeCompile() {
        assertModified(true, doc);
        assertModified(true, doc2);
        synchronized(this) { saveBeforeCompileCount++; }
        
      }
    };
    
    _model.addListener(listener);
    listener.compile(doc);
    listener.assertSaveBeforeCompileCount(1);
    assertModified(true, doc);
    assertModified(true, doc2);
    assertContents(FOO_TEXT, doc);
    assertContents(BAR_TEXT, doc2);
    _model.removeListener(listener);
    _log.log("testCompileAbortsIfAnyUnsaved complete");
  }
  
  
  public void testCompileAnyUnsavedButSaveWhenAsked() throws BadLocationException, IOException, InterruptedException {
    final OpenDefinitionsDocument doc = setupDocument(FOO_TEXT);
    final OpenDefinitionsDocument doc2 = setupDocument(BAR_TEXT);
    final File file = tempFile();
    final File file2 = tempFile(2);
    
    CompileShouldSucceedListener listener = new CompileShouldSucceedListener() {
      public void saveBeforeCompile() {
        assertModified(true, doc);
        assertModified(true, doc2);
        assertSaveCount(0);
        assertCompileStartCount(0);
        assertCompileEndCount(0);
        assertInterpreterReadyCount(0);
        assertConsoleResetCount(0);
        
        saveFile(doc, new FileSelector(file));
        saveFile(doc2, new FileSelector(file2));
        
        synchronized(this) { saveBeforeCompileCount++; }
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
        
        synchronized(this) { saveCount++; }
      }
    };
    
    _model.addListener(listener);
    testStartCompile(doc);
    listener.waitCompileDone();
    
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
    _log.log("testCompileAnyUnsavedButSaveWhenAsked complete");
  }
  
  
  public void testCompileActiveSavedAnyUnsavedButSaveWhenAsked() throws BadLocationException, IOException, 
    InterruptedException {
    
    final OpenDefinitionsDocument doc = setupDocument(FOO_TEXT);
    final OpenDefinitionsDocument doc2 = setupDocument(BAR_TEXT);
    final File file = tempFile();
    final File file2 = tempFile(1);
    
    CompileShouldSucceedListener listener = new CompileShouldSucceedListener() {
      public void saveBeforeCompile() {
        assertModified(false, doc);
        assertModified(true, doc2);
        assertSaveCount(0);
        assertCompileStartCount(0);
        assertCompileEndCount(0);
        assertInterpreterReadyCount(0);
        assertConsoleResetCount(0);
        
        saveFile(doc2, new FileSelector(file2)); 
        
        synchronized(this) { saveBeforeCompileCount++; }
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
        synchronized(this) { saveCount++; }
      }
    };
    
    assertModified(true, doc);
    saveFile(doc, new FileSelector(file));
    assertModified(false, doc);
    assertModified(true, doc2);
    _model.addListener(listener);
    testStartCompile(doc);
    listener.waitCompileDone();
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
    _log.log("testCompileActiveSavedAnyUnsavedButSaveWhenAsked complete");
  }
}
