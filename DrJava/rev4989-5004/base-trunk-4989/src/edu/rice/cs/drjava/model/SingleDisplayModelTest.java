

package edu.rice.cs.drjava.model;

import javax.swing.text.BadLocationException;
import java.io.File;
import java.io.IOException;

import java.util.Arrays;

import edu.rice.cs.util.FileOpenSelector;
import edu.rice.cs.util.FileOps;
import edu.rice.cs.util.OperationCanceledException;
import edu.rice.cs.util.swing.Utilities;


public class SingleDisplayModelTest extends GlobalModelTestCase {

  
  
  
  private DefaultGlobalModel getSDModel() { return  _model; }

  protected void assertNotEmpty() {
    assertTrue("number of documents", getSDModel().getOpenDefinitionsDocuments().size() > 0);
  }

  protected void assertActiveDocument(OpenDefinitionsDocument doc) {
    assertEquals("active document", doc, getSDModel().getActiveDocument());
  }

  
  protected OpenDefinitionsDocument setupDocument(String text) throws BadLocationException {
    
    assertNotEmpty();
    SDTestListener listener = new SDTestListener() {
      public synchronized void newFileCreated(OpenDefinitionsDocument doc) { newCount++; }
      public synchronized void activeDocumentChanged(OpenDefinitionsDocument doc) { switchCount++; }
    };

    getSDModel().addListener(listener);

    listener.assertSwitchCount(0);

    
    int numOpen = getSDModel().getOpenDefinitionsDocuments().size();
    OpenDefinitionsDocument doc = getSDModel().newFile();
    assertNumOpenDocs(numOpen + 1);

    listener.assertNewCount(1);
    listener.assertSwitchCount(1);
    assertLength(0, doc);
    assertModified(false, doc);

    changeDocumentText(text, doc);  
    getSDModel().removeListener(listener);
    
    _log.log("New File " + doc + " created");

    return doc;
  }

  
  public void testNotEmptyOnStartup() throws BadLocationException {
    
    assertNumOpenDocs(1);
    OpenDefinitionsDocument doc = getSDModel().getActiveDocument();
    assertModified(false, doc);
    assertLength(0, doc);
    _log.log("testNotEmptyOnStartup completed");
  }

  
  public void testDocumentSwitching() throws BadLocationException {
    
    SDTestListener listener = new SDTestListener() {
      public synchronized void newFileCreated(OpenDefinitionsDocument doc) { newCount++; }
      public synchronized void activeDocumentChanged(OpenDefinitionsDocument doc) { switchCount++; }
    };
    getSDModel().addListener(listener);

    
    OpenDefinitionsDocument doc3 = getSDModel().getActiveDocument();
    changeDocumentText(FOO_TEXT, doc3);
    listener.assertSwitchCount(0);

    
    OpenDefinitionsDocument doc2 = setupDocument(BAR_TEXT);
    assertNumOpenDocs(2);
    listener.assertNewCount(1);
    listener.assertSwitchCount(1);
    assertActiveDocument(doc2);

    OpenDefinitionsDocument doc1 = setupDocument(BAZ_TEXT);
    assertNumOpenDocs(3);
    listener.assertNewCount(2);
    listener.assertSwitchCount(2);
    assertActiveDocument(doc1);

    
    getSDModel().setActivePreviousDocument();
    listener.assertSwitchCount(3);
    assertActiveDocument(doc3);

    
    getSDModel().setActiveNextDocument();
    listener.assertSwitchCount(4);
    assertActiveDocument(doc1);
    
    
    getSDModel().setActiveNextDocument();
    listener.assertSwitchCount(5);
    assertActiveDocument(doc2);

    getSDModel().setActiveNextDocument();
    listener.assertSwitchCount(6);
    assertActiveDocument(doc3);

    
    getSDModel().setActiveNextDocument();
    listener.assertSwitchCount(7);
    assertActiveDocument(doc1);

    
    getSDModel().setActivePreviousDocument();
    listener.assertSwitchCount(8);
    assertActiveDocument(doc3);

    
    getSDModel().setActiveDocument(doc1);
    listener.assertSwitchCount(9);
    assertActiveDocument(doc1);

    
    assertNumOpenDocs(3);
    getSDModel().removeListener(listener);
    _log.log("testDocumentSwitching completed");
  }

  
  public void testCloseUnmodifiedAutomatically() throws BadLocationException, IOException,
    OperationCanceledException, AlreadyOpenException {
    
    assertNumOpenDocs(1); 
    OpenDefinitionsDocument doc = getSDModel().getActiveDocument();
    assertModified(false, doc);
    assertLength(0, doc);

    final File tempFile = writeToNewTempFile(BAR_TEXT);

    
    SDTestListener listener = new SDTestListener() {
      public void fileOpened(OpenDefinitionsDocument doc) {
        File file = FileOps.NULL_FILE;
        try { file = doc.getFile(); }
        catch (FileMovedException fme) {
          
          fail("file does not exist");
        }
        try {
          assertEquals("file to open", tempFile.getCanonicalFile(), file.getCanonicalFile());
          synchronized(this) { openCount++; }
        }
        catch (IOException ioe) { fail("could not get canonical file"); }
      }
      public synchronized void fileClosed(OpenDefinitionsDocument doc) { closeCount++; }
      public synchronized void activeDocumentChanged(OpenDefinitionsDocument doc) { switchCount++; }
    };
    getSDModel().addListener(listener);

    
    doc = getSDModel().openFile(new FileSelector(tempFile));
    listener.assertOpenCount(1);
    listener.assertCloseCount(1);
    listener.assertSwitchCount(1);
    assertNumOpenDocs(1);
    assertModified(false, doc);
    assertContents(BAR_TEXT, doc);
    getSDModel().removeListener(listener);
    _log.log("testCloseUnmodifiedAutomatically completed");
  }

  
  public void testCloseFiles() throws BadLocationException {
    
    SDTestListener listener = new SDTestListener() {
      public synchronized boolean canAbandonFile(OpenDefinitionsDocument doc) {
        canAbandonCount++;
        return true; 
      }
      public synchronized void newFileCreated(OpenDefinitionsDocument doc) { newCount++; }
      public synchronized void fileClosed(OpenDefinitionsDocument doc) { closeCount++; }
      public synchronized void activeDocumentChanged(OpenDefinitionsDocument doc) { switchCount++; }
      public synchronized void interpreterReady(File wd) { interpreterReadyCount++; }
    };
    _model.addListener(listener);
    
    
    OpenDefinitionsDocument doc1 = _model.getActiveDocument();
    changeDocumentText(FOO_TEXT, doc1);
    OpenDefinitionsDocument doc2 = setupDocument(BAR_TEXT);
    assertActiveDocument(doc2);
    assertNumOpenDocs(2);
    listener.assertNewCount(1);
    listener.assertSwitchCount(1);
    listener.assertInterpreterResettingCount(0);

    
    _model.closeFile(_model.getActiveDocument());
    assertNumOpenDocs(1);
    listener.assertCloseCount(1);
    listener.assertAbandonCount(1);
    listener.assertSwitchCount(2);
    listener.assertInterpreterResettingCount(0);
    assertActiveDocument(doc1);
    assertContents(FOO_TEXT, _model.getActiveDocument());

    
    _model.closeFile(_model.getActiveDocument());
    listener.assertCloseCount(2);
    listener.assertAbandonCount(2);
    listener.assertInterpreterResettingCount(0);

    
    assertNumOpenDocs(1);
    listener.assertNewCount(2);
    listener.assertSwitchCount(3);
    listener.assertInterpreterResettingCount(0);
    assertLength(0, _model.getActiveDocument());
    
    _log.log("Starting second phase of testCloseFiles");

    
    doc1 = _model.getActiveDocument();
    changeDocumentText(FOO_TEXT, doc1);
    doc2 = setupDocument(BAR_TEXT);
    assertNumOpenDocs(2);
    listener.assertNewCount(3);
    listener.assertInterpreterResettingCount(0);
    
    _log.log("Just before calling _model.closeAllFiles()");
    
    Utilities.invokeAndWait(new Runnable() { public void run() { _model.closeAllFiles(); } });
    Utilities.clearEventQueue();
    Utilities.clearEventQueue();
    
    
    
    listener.assertInterpreterReadyCount(1);
    assertNumOpenDocs(1);
    assertLength(0, _model.getActiveDocument());
    
    listener.assertNewCount(4);
    listener.assertCloseCount(4);
    listener.assertAbandonCount(4);
    
    _model.removeListener(listener);

  }

  
  public void testCompleteFilename() throws BadLocationException, IOException, OperationCanceledException, 
    AlreadyOpenException {
    
    OpenDefinitionsDocument doc = _model.getActiveDocument();
    assertEquals("untitled display filename", "(Untitled)", doc.getCompletePath());

    
    File file = File.createTempFile("DrJava-filename-test", ".java", _tempDir).getCanonicalFile();
    file.deleteOnExit();
    String name = file.getAbsolutePath();
    doc = _model.openFile(new FileSelector(file));
            
    assertEquals(".java display filename", name, doc.getCompletePath());

    
    file = File.createTempFile("DrJava-filename-test", ".txt", _tempDir).getCanonicalFile();
    file.deleteOnExit();
    name = file.getAbsolutePath();
    doc = _model.openFile(new FileSelector(file));
    assertEquals(".txt display filename", name, doc.getCompletePath());

    
    file = File.createTempFile("DrJava-filename-test", ".java", _tempDir).getCanonicalFile();
    file.deleteOnExit();
    name = file.getAbsolutePath();
    doc = _model.openFile(new FileSelector(file));
    changeDocumentText("foo", doc);
    assertEquals(".java.txt display filename", name + " *", doc.getCompletePath());
    _log.log("testDisplayFilename completed");
  }
  
  public void testDeleteFileWhileOpen() 
    throws IOException, OperationCanceledException, AlreadyOpenException  {
    String txt = "This is some test text";
    File f = writeToNewTempFile(txt);
    OpenDefinitionsDocument doc1 = _model.openFile(new FileSelector(f));
    @SuppressWarnings("unused") OpenDefinitionsDocument doc2 = _model.newFile();
    f.delete();
    _model.closeFile(doc1);
     _log.log("testDeleteFileWhileOpen completed");
    
    
    
    
    
    
    
    
    
    
    
  }
  public void testDeleteFileBeforeCloseAll() 
    throws IOException, OperationCanceledException, AlreadyOpenException {
    final File[] files = new File[10];
    for (int i = 0; i < 10; i++) {
      String txt = "Text for file " + i;
      files[i] = writeToNewTempFile(txt);
    }
    FileOpenSelector fos = new FileOpenSelector() {
      public File[] getFiles() throws OperationCanceledException { return files; }
    };
    _model.openFiles(fos);
    _log.log("Opened files " + Arrays.toString(files));
    OpenDefinitionsDocument doc = _model.getSortedOpenDefinitionsDocuments().get(5);
    _model.setActiveDocument(doc);
    _log.log("Active document is: " + doc);
    files[5].delete();
    _log.log("Delected document: " + doc);
    _model.closeAllFiles();
    _log.log("testDeleteFileBeforeCloseAll completed");
  }
  
  
  public static class SDTestListener extends TestListener implements GlobalModelListener {
    
    
    protected volatile int switchCount;

    public void resetCounts() {
      super.resetCounts();
      switchCount = 0;
    }

    public void assertSwitchCount(int i) { assertEquals("number of active document switches", i, switchCount); }

    public void activeDocumentChanged(OpenDefinitionsDocument doc) {
      fail("activeDocumentChanged fired unexpectedly");
    }
    
    public void activeDocumentRefreshed(OpenDefinitionsDocument doc) {
      fail("activeDocumentRefreshed fired unexpectedly");
    }
    
    public int getInterpreterReadyCount() { return interpreterReadyCount; }
  }
}
