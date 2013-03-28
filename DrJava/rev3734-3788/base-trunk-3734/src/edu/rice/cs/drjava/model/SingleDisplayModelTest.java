

package edu.rice.cs.drjava.model;

import javax.swing.text.BadLocationException;
import java.io.File;
import java.io.IOException;

import edu.rice.cs.util.FileOpenSelector;
import edu.rice.cs.util.OperationCanceledException;


public class SingleDisplayModelTest extends GlobalModelTestCase {
  
  protected DefaultGlobalModel _sdModel;



  
  protected void createModel() {
    
    _model = new DefaultGlobalModel();
    _sdModel = getSDModel();
  }

  
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
      public void newFileCreated(OpenDefinitionsDocument doc) { newCount++; }
      public void activeDocumentChanged(OpenDefinitionsDocument doc) { switchCount++; }
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
    


    return doc;
  }

  
  public static class SDTestListener extends TestListener implements GlobalModelListener {
    
    
    protected int switchCount;

    public void resetCounts() {
      super.resetCounts();
      switchCount = 0;
    }

    public void assertSwitchCount(int i) {
      assertEquals("number of active document switches", i, switchCount);
    }

    public void activeDocumentChanged(OpenDefinitionsDocument doc) {
      fail("activeDocumentChanged fired unexpectedly");
    }
  }

  
  public void testNotEmptyOnStartup() throws BadLocationException {
    
    assertNumOpenDocs(1);
    OpenDefinitionsDocument doc = getSDModel().getActiveDocument();
    assertModified(false, doc);
    assertLength(0, doc);

  }

  
  public void testDocumentSwitching() throws BadLocationException {
    
    SDTestListener listener = new SDTestListener() {
      public void newFileCreated(OpenDefinitionsDocument doc) { newCount++; }
      public void activeDocumentChanged(OpenDefinitionsDocument doc) { switchCount++; }
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
        File file = null;
        try { file = doc.getFile(); }
        catch (FileMovedException fme) {
          
          fail("file does not exist");
        }
        try {
          assertEquals("file to open", tempFile.getCanonicalFile(), file.getCanonicalFile());
          openCount++;
        }
        catch (IOException ioe) { fail("could not get canonical file"); }
      }
      public void fileClosed(OpenDefinitionsDocument doc) { closeCount++; }
      public void activeDocumentChanged(OpenDefinitionsDocument doc) { switchCount++; }
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

  }

  
  public void testCloseFiles() throws BadLocationException {
    
    SDTestListener listener = new SDTestListener() {
      public boolean canAbandonFile(OpenDefinitionsDocument doc) {
        canAbandonCount++;
        return true; 
      }
      public void newFileCreated(OpenDefinitionsDocument doc) { newCount++; }
      public void fileClosed(OpenDefinitionsDocument doc) { closeCount++; }
      public void activeDocumentChanged(OpenDefinitionsDocument doc) { switchCount++; }
    };
    _sdModel.addListener(listener);

    
    OpenDefinitionsDocument doc1 = _sdModel.getActiveDocument();
    changeDocumentText(FOO_TEXT, doc1);
    OpenDefinitionsDocument doc2 = setupDocument(BAR_TEXT);
    assertActiveDocument(doc2);
    assertNumOpenDocs(2);
    listener.assertNewCount(1);
    listener.assertSwitchCount(1);

    
    _sdModel.closeFile(_sdModel.getActiveDocument());
    assertNumOpenDocs(1);
    listener.assertCloseCount(1);
    listener.assertAbandonCount(1);
    listener.assertSwitchCount(2);
    assertActiveDocument(doc1);
    assertContents(FOO_TEXT, _sdModel.getActiveDocument());

    
    _sdModel.closeFile(_sdModel.getActiveDocument());
    listener.assertCloseCount(2);
    listener.assertAbandonCount(2);

    
    assertNumOpenDocs(1);
    listener.assertNewCount(2);
    listener.assertSwitchCount(3);
    assertLength(0, _sdModel.getActiveDocument());

    
    doc1 = _sdModel.getActiveDocument();
    changeDocumentText(FOO_TEXT, doc1);
    doc2 = setupDocument(BAR_TEXT);
    assertNumOpenDocs(2);
    listener.assertNewCount(3);

    
    _sdModel.closeAllFiles();
    assertNumOpenDocs(1);
    assertLength(0, _sdModel.getActiveDocument());
    listener.assertNewCount(4);
    listener.assertCloseCount(4);
    listener.assertAbandonCount(4);

    _sdModel.removeListener(listener);

  }

  
  public void testDisplayFilename() throws IOException, OperationCanceledException, AlreadyOpenException {
    
    OpenDefinitionsDocument doc = _sdModel.getActiveDocument();
    assertEquals("untitled display filename", "(Untitled)", _sdModel.getDisplayFilename(doc));

    
    File file = File.createTempFile("DrJava-filename-test", ".java", _tempDir).getCanonicalFile();
    file.deleteOnExit();
    String name = file.getName();
    doc = _sdModel.openFile(new FileSelector(file));
    assertEquals(".java display filename",
                 name.substring(0, name.length()-5),
                 _sdModel.getDisplayFilename(doc));

    
    file = File.createTempFile("DrJava-filename-test", ".txt", _tempDir).getCanonicalFile();
    file.deleteOnExit();
    name = file.getName();
    doc = _sdModel.openFile(new FileSelector(file));
    assertEquals(".txt display filename", name, _sdModel.getDisplayFilename(doc));

    
    file = File.createTempFile("DrJava-filename-test", ".java.txt", _tempDir).getCanonicalFile();
    file.deleteOnExit();
    name = file.getName();
    doc = _sdModel.openFile(new FileSelector(file));
    assertEquals(".java.txt display filename", name, _sdModel.getDisplayFilename(doc));

  }
  
  public void testDeleteFileWhileOpen() 
    throws IOException, OperationCanceledException, AlreadyOpenException  {
    String txt = "This is some test text";
    File f = writeToNewTempFile(txt);
    OpenDefinitionsDocument doc1 = _sdModel.openFile(new FileSelector(f));
    OpenDefinitionsDocument doc2 = _sdModel.newFile();
    f.delete();
    _sdModel.closeFile(doc1);

    
    
    
    
    
    
    
    
    
    
    
  }
  public void testDeleteFileBeforeCloseAll() 
    throws IOException, OperationCanceledException, AlreadyOpenException {
    final File[] files = new File[10];
    for (int i=0; i < 10; i++) {
      String txt = "Text for file " + i;
      files[i] = writeToNewTempFile(txt);
    }
    FileOpenSelector fos = new FileOpenSelector() {
      public File[] getFiles() throws OperationCanceledException { return files; }
    };
    _sdModel.openFiles(fos);
    OpenDefinitionsDocument doc = _sdModel.getOpenDefinitionsDocuments().get(5);
    _sdModel.setActiveDocument(doc);
    files[5].delete();
    _sdModel.closeAllFiles();

  }
}
