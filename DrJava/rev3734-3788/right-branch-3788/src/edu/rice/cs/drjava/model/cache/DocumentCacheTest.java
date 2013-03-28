

package edu.rice.cs.drjava.model.cache;

import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.drjava.model.*;
import edu.rice.cs.drjava.model.definitions.DefinitionsDocument;
import edu.rice.cs.util.FileOpenSelector;
import edu.rice.cs.util.FileOps;
import edu.rice.cs.util.OperationCanceledException;

import javax.swing.text.BadLocationException;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;


public class DocumentCacheTest extends DrJavaTestCase {
  
  private DefaultGlobalModel _model;
  private DocumentCache _cache;
  private Hashtable<OpenDefinitionsDocument, DCacheAdapter> _adapterTable;
  
  private int _doc_made;
  private int _doc_saved;
  
  protected File _tempDir;

  public void setUp() throws Exception {
    super.setUp();
    createModel();
    
    String user = System.getProperty("user.name");
    _tempDir = FileOps.createTempDirectory("DrJava-test-" + user);
    
    _cache = _model.getDocumentCache();
    _cache.setCacheSize(4);
    _adapterTable = new Hashtable<OpenDefinitionsDocument, DCacheAdapter> ();
    _cache.addRegistrationListener(new DocumentCache.RegistrationListener() {
      public void registered(OpenDefinitionsDocument odd, DCacheAdapter a) {
        _adapterTable.put(odd, a);
      }
    });
    _doc_made = 0;
    _doc_saved = 0;
  }
  
  public void tearDown() throws Exception {
    boolean ret = FileOps.deleteDirectory(_tempDir);
    assertTrue("delete temp directory " + _tempDir, ret);
    _model.dispose();

    _tempDir = null;
    _model = null;
    super.tearDown();
  }
  
  
  protected void createModel() {
    
    _model = new TestGlobalModel();

    
    _model.waitForInterpreter();
  }

  
  protected File tempFile() throws IOException {
    return File.createTempFile("DrJava-test", ".java", _tempDir).getCanonicalFile();
  }
  
  
  protected File tempFile(int i) throws IOException {
    return File.createTempFile("DrJava-test" + i, ".java", _tempDir).getCanonicalFile();
  }
  
  protected OpenDefinitionsDocument openFile(final File f) throws IOException {
    try{
      OpenDefinitionsDocument doc = _model.openFile(new FileOpenSelector() {        
        public File[] getFiles() { return new File[] {f}; }
      });
      return doc;
    }
    catch(AlreadyOpenException e) { throw new IOException(e.getMessage()); }
    catch(OperationCanceledException e) { throw new IOException(e.getMessage());}
  }
  
  
  public void testCacheSize() {
    _cache.setCacheSize(6);
    assertEquals("Wrong cache size", 6, _cache.getCacheSize());
  }
  
  public void testNewDocumentsInAndOutOfTheCache() throws BadLocationException, IOException {
    assertEquals("Wrong Cache Size", 4, _cache.getCacheSize());
    
    
    
    
    OpenDefinitionsDocument doc1 =  _model.newFile();
    assertEquals("There should be 0 documents in the cache", 0, _cache.getNumInCache()); 
    
    OpenDefinitionsDocument doc2 =  _model.newFile();
    assertEquals("There should be 0 documents in the cache", 0, _cache.getNumInCache()); 
    
    OpenDefinitionsDocument doc3 =  _model.newFile();
    assertEquals("There should be 0 documents in the cache", 0, _cache.getNumInCache()); 
    
    OpenDefinitionsDocument doc4 =  _model.newFile();
    assertEquals("There should be 0 documents in the cache", 0, _cache.getNumInCache()); 
    
    OpenDefinitionsDocument doc5 =  _model.newFile();
    assertEquals("There should be 0 documents in the cache", 0, _cache.getNumInCache()); 
    
    OpenDefinitionsDocument doc6 =  _model.newFile();
    assertEquals("There should be 0 documents in the cache", 0, _cache.getNumInCache()); 
    
    
    

    assertFalse("Document 1 shouldn't be modified", doc1.isModifiedSinceSave());
    assertFalse("Document 2 shouldn't be modified", doc2.isModifiedSinceSave());
    assertFalse("Document 3 shouldn't be modified", doc3.isModifiedSinceSave());
    assertFalse("Document 4 shouldn't be modified", doc4.isModifiedSinceSave());
    assertFalse("Document 5 shouldn't be modified", doc5.isModifiedSinceSave());
    assertFalse("Document 6 shouldn't be modified", doc6.isModifiedSinceSave());
    assertEquals("There should be 0 documents in the cache", 0, _cache.getNumInCache());
    
    assertFalse("Document 1 shouldn't be ready", _adapterTable.get(doc1).isReady());
    assertFalse("Document 2 shouldn't be ready", _adapterTable.get(doc2).isReady());
    assertFalse("Document 3 shouldn't be ready", _adapterTable.get(doc3).isReady());
    assertFalse("Document 4 shouldn't be ready", _adapterTable.get(doc4).isReady());
    assertFalse("Document 5 shouldn't be ready", _adapterTable.get(doc5).isReady());
    assertFalse("Document 6 shouldn't be ready", _adapterTable.get(doc6).isReady());
    
    
    
    doc1.getLength();
    doc2.getLength();
    doc3.getLength();
    doc4.getLength();
    doc5.getLength();
    doc6.getLength();
    
    assertTrue("Document 1 should be ready", _adapterTable.get(doc1).isReady());
    assertTrue("Document 2 should be ready", _adapterTable.get(doc2).isReady());
    assertTrue("Document 3 should be ready", _adapterTable.get(doc3).isReady());
    assertTrue("Document 4 should be ready", _adapterTable.get(doc4).isReady());
    assertTrue("Document 5 should be ready", _adapterTable.get(doc5).isReady());
    assertTrue("Document 6 should be ready", _adapterTable.get(doc6).isReady());
    
    
    
    assertEquals("Confirm that cache is empty", 0, _cache.getNumInCache());
    
  }
  
  public void testOldDocumentsInAndOutOfTheCache() throws BadLocationException, IOException {
    
    File file1 = tempFile(1);
    File file2 = tempFile(2);
    File file3 = tempFile(3);
    File file4 = tempFile(4);
    File file5 = tempFile(5);
    File file6 = tempFile(6);
    
    
    OpenDefinitionsDocument doc1 = openFile(file1);
    assertEquals("There should be 1 document in the cache", 1, _cache.getNumInCache());
    OpenDefinitionsDocument doc2 = openFile(file2);
    assertEquals("There should be 2 documents in the cache", 2, _cache.getNumInCache());
    OpenDefinitionsDocument doc3 = openFile(file3);
    assertEquals("There should be 3 documents in the cache", 3, _cache.getNumInCache());
    OpenDefinitionsDocument doc4 = openFile(file4);
    assertEquals("There should be 4 documents in the cache", 4, _cache.getNumInCache());
    OpenDefinitionsDocument doc5 = openFile(file5);
    assertEquals("There should be 4 documents in the cache", 4, _cache.getNumInCache());
    OpenDefinitionsDocument doc6 = openFile(file6);
    assertEquals("There should be 4 documents in the cache", 4, _cache.getNumInCache());
    
    assertEquals("Wrong Cache Size", 4, _cache.getCacheSize());

    
    
    

    assertFalse("Document 1 shouldn't be modified", doc1.isModifiedSinceSave());
    assertFalse("Document 2 shouldn't be modified", doc2.isModifiedSinceSave());
    assertFalse("Document 3 shouldn't be modified", doc3.isModifiedSinceSave());
    assertFalse("Document 4 shouldn't be modified", doc4.isModifiedSinceSave());
    assertFalse("Document 5 shouldn't be modified", doc5.isModifiedSinceSave());
    assertFalse("Document 6 shouldn't be modified", doc6.isModifiedSinceSave());
    
    assertEquals("There should be 4 documents in the cache", 4, _cache.getNumInCache());
    
    assertFalse("Document 1 shouldn't be ready", _adapterTable.get(doc1).isReady());
    assertFalse("Document 2 shouldn't be ready", _adapterTable.get(doc2).isReady());
    assertTrue("Document 3 should be ready", _adapterTable.get(doc3).isReady());
    assertTrue("Document 4 should be ready", _adapterTable.get(doc4).isReady());
    assertTrue("Document 5 should be ready", _adapterTable.get(doc5).isReady());
    assertTrue("Document 6 should be ready", _adapterTable.get(doc6).isReady());
    
    
    
 
    doc1.getLength();
    doc2.getLength();
    doc3.getLength();
    doc4.getLength();
    
   
    
    assertTrue("Document 1 should be ready", _adapterTable.get(doc1).isReady());
    assertTrue("Document 2 should be ready", _adapterTable.get(doc2).isReady());
    assertTrue("Document 3 should be ready", _adapterTable.get(doc3).isReady());
    assertTrue("Document 4 should be ready", _adapterTable.get(doc4).isReady());
    
    doc5.getLength();
    
    assertFalse("Document 1 is not longer ready", _adapterTable.get(doc1).isReady());
    assertTrue("Document 5 should be ready", _adapterTable.get(doc5).isReady());
    
    doc6.getLength();
    
    assertFalse("Document 2 is not longer ready", _adapterTable.get(doc2).isReady());
    assertTrue("Document 6 should be ready", _adapterTable.get(doc6).isReady());
    assertTrue("Document 3 should be ready", _adapterTable.get(doc3).isReady());
    assertTrue("Document 4 should be ready", _adapterTable.get(doc4).isReady());
    assertTrue("Document 5 should be ready", _adapterTable.get(doc5).isReady());
    
    doc1.getLength(); 
    assertTrue("The document 1 should should now be in the cache", _adapterTable.get(doc1).isReady());    
    assertEquals("There should still be 4 documents in the cache", 4, _cache.getNumInCache()); 
    assertFalse("The document 3 should have been kicked out of the cache", _adapterTable.get(doc3).isReady());
    
    doc2.getLength(); 
    assertTrue("The document 2 should should now be in the cache", _adapterTable.get(doc2).isReady());
    assertEquals("There should still be 4 documents in the cache", 4, _cache.getNumInCache());
    assertFalse("The document 4 should have been kicked out of the cache", _adapterTable.get(doc4).isReady());
    
    doc3.getLength(); 
    assertTrue("The document 3 should should now be in the cache", _adapterTable.get(doc3).isReady());
    assertEquals("There should still be 4 documents in the cache", 4, _cache.getNumInCache());
    assertFalse("The document 5 should have been kicked out of the cache", _adapterTable.get(doc5).isReady());
    
    doc4.getLength(); 
    assertTrue("The document 4 should should now be in the cache", _adapterTable.get(doc4).isReady());
    assertEquals("There should still be 4 documents in the cache", 4, _cache.getNumInCache());
    assertFalse("The document 6 should have been kicked out of the cache", _adapterTable.get(doc6).isReady());
    
    doc5.getLength(); 
    assertTrue("The document 5 should should now be in the cache", _adapterTable.get(doc5).isReady());
    assertEquals("There should still be 4 documents in the cache", 4, _cache.getNumInCache());
    assertFalse("The document 1 should have been kicked out of the cache", _adapterTable.get(doc1).isReady());
    
    doc6.getLength(); 
    assertTrue("The document 6 should should now be in the cache", _adapterTable.get(doc6).isReady());
    assertEquals("There should still be 4 documents in the cache", 4, _cache.getNumInCache());
    assertFalse("The document 2 should have been kicked out of the cache", _adapterTable.get(doc2).isReady());
    
    
    doc4.getLength(); 
    assertTrue("The document 3 should should still be in the cache", _adapterTable.get(doc3).isReady());    
    assertEquals("There should still be 4 documents in the cache", 4, _cache.getNumInCache());
    doc5.getLength(); 
    assertTrue("The document 3 should should still be in the cache", _adapterTable.get(doc3).isReady());    
    assertEquals("There should still be 4 documents in the cache", 4, _cache.getNumInCache());
    doc3.getLength(); 
    assertTrue("The document 6 should should still be in the cache", _adapterTable.get(doc6).isReady());    
    assertEquals("There should still be 4 documents in the cache", 4, _cache.getNumInCache());
    doc4.getLength(); 
    assertTrue("The document 6 should should still be in the cache", _adapterTable.get(doc6).isReady());    
    
    assertEquals("There should be 4 documents in the cache", 4, _cache.getNumInCache());
    assertFalse("The document 1 should still be out of the cache", _adapterTable.get(doc1).isReady());
    assertFalse("The document 2 should still be out of the cache", _adapterTable.get(doc2).isReady());
    
    
    _cache.setCacheSize(5); 
    assertEquals("The cache size should now be 5", 5, _cache.getCacheSize());
    assertEquals("There should still only be 4 files in the cache", 4, _cache.getNumInCache());
    
    doc2.getLength(); 
    assertTrue("The document 2 should now be in the cache", _adapterTable.get(doc2).isReady());
    assertFalse("The document 1 should still be out of the cache", _adapterTable.get(doc1).isReady());
    assertEquals("There should be 5 documents in the cache", 5, _cache.getNumInCache());
    
    _cache.setCacheSize(3); 
    
    assertEquals("The cache size should now be 3", 3, _cache.getCacheSize());
    assertEquals("There should be 3 documents in the cache", 3, _cache.getNumInCache());
    assertTrue("The document 2 should be in the cache", _adapterTable.get(doc2).isReady());
    assertTrue("The document 6 should be in the cache", _adapterTable.get(doc6).isReady());
    assertTrue("The document 5 should be in the cache", _adapterTable.get(doc5).isReady());
    assertFalse("The document 3 should now be out of the cache", _adapterTable.get(doc3).isReady());
    assertFalse("The document 4 should now be out of the cache", _adapterTable.get(doc4).isReady());
    assertFalse("The document 1 should still be out of the cache", _adapterTable.get(doc1).isReady());
  }
  
  public void testGetDDocFromCache() throws BadLocationException, IOException, OperationCanceledException {
    File file1 = tempFile(1);
    File file2 = tempFile(2);
    File file3 = tempFile(3);
    File file4 = tempFile(4);
    File file5 = tempFile(5);
    File file6 = tempFile(6);
    
    
    OpenDefinitionsDocument doc1 = openFile(file1);
    assertTrue("The document should not start out in the cache", _adapterTable.get(doc1).isReady());
    assertEquals("There should be 1 documents in the cache", 1, _cache.getNumInCache());
    OpenDefinitionsDocument doc2 = openFile(file2);
    assertTrue("The document should not start out in the cache", _adapterTable.get(doc2).isReady());
    assertEquals("There should be 2 documents in the cache", 2, _cache.getNumInCache());
    OpenDefinitionsDocument doc3 = openFile(file3);
    assertTrue("The document should not start out in the cache", _adapterTable.get(doc3).isReady());
    assertEquals("There should be 3 documents in the cache", 3, _cache.getNumInCache());
    OpenDefinitionsDocument doc4 = openFile(file4);
    assertTrue("The document should not start out in the cache", _adapterTable.get(doc4).isReady());
    assertEquals("There should be 4 documents in the cache", 4, _cache.getNumInCache());
    OpenDefinitionsDocument doc5 = openFile(file5);
    assertTrue("The document should not start out in the cache", _adapterTable.get(doc5).isReady());
    assertFalse("The document should not start out in the cache", _adapterTable.get(doc1).isReady());
    assertEquals("There should be 4 documents in the cache", 4, _cache.getNumInCache());
    OpenDefinitionsDocument doc6 = openFile(file6);
    assertTrue("The document should not start out in the cache", _adapterTable.get(doc6).isReady());
    assertFalse("The document should not start out in the cache", _adapterTable.get(doc2).isReady());
    assertEquals("There should be 4 documents in the cache", 4, _cache.getNumInCache());
  }
  
  
  private DefinitionsDocument _saved; 
  
  public void testReconstructor() throws IOException{
    DDReconstructor d = new DDReconstructor() {
      public DefinitionsDocument make() {
        _doc_made++;
        return _saved;
      }
      public void saveDocInfo(DefinitionsDocument doc) {
        _doc_saved++;
      }
      public void addDocumentListener(javax.swing.event.DocumentListener dl) {
        
      }
    };
    
    OpenDefinitionsDocument doc1 =  _model.newFile();
    assertFalse("The document should not be in the cache", _adapterTable.get(doc1).isReady());
    _saved = _adapterTable.get(doc1).getDocument();
    assertTrue("The document should be in the cache", _adapterTable.get(doc1).isReady());
    







  }
  
  
  
  public void testNoDDocInCache() {
   OpenDefinitionsDocument doc1 = _model.newFile();
   _model.closeFile(doc1);
   assertFalse("The document should now be closed", _adapterTable.get(doc1).isReady());
  }


  public void testNumListeners() {
   OpenDefinitionsDocument doc1 = _model.newFile();
   OpenDefinitionsDocument doc2 = _model.newFile();
   OpenDefinitionsDocument doc3 = _model.newFile();
   OpenDefinitionsDocument doc4 = _model.newFile();
   OpenDefinitionsDocument doc5 = _model.newFile();

   int numDocListeners = doc1.getDocumentListeners().length;
   int numUndoListeners = doc1.getUndoableEditListeners().length;
   
   doc1.getLength();
   doc2.getLength();
   doc3.getLength();
   doc4.getLength();

   
   doc5.getLength();
 
   
   doc1.getLength();
   
   assertEquals("the number of document listeners is the same after reconstruction", numDocListeners, 
                doc1.getDocumentListeners().length);
   assertEquals("the number of undoableEditListeners is the same after reconstruction", numUndoListeners, 
                doc1.getUndoableEditListeners().length);

  }
  
  
  public void testMemoryLeak() throws InterruptedException, IOException {
    _memLeakCounter=0;
    FinalizationListener<DefinitionsDocument> fl = new FinalizationListener<DefinitionsDocument>() {
      public void finalized(FinalizationEvent<DefinitionsDocument> fe) {
        _memLeakCounter++;
      }
    };
    
    
    
   
    OpenDefinitionsDocument doc1 = openFile(tempFile(1));
    OpenDefinitionsDocument doc2 = openFile(tempFile(2));
    OpenDefinitionsDocument doc3 = openFile(tempFile(3));
    OpenDefinitionsDocument doc4 = openFile(tempFile(4));
    OpenDefinitionsDocument doc5 = openFile(tempFile(5));
        
    doc1.addFinalizationListener(fl);
    doc2.addFinalizationListener(fl);
    doc3.addFinalizationListener(fl);
    doc4.addFinalizationListener(fl);
    doc5.addFinalizationListener(fl); 
    
    assertEquals("There should be 4 in the QUEUE", 4, _cache.getNumInCache());
    System.gc();
    Thread.sleep(100);
    
    
    assertFalse("doc1 should be the one that's not ready", _adapterTable.get(doc1).isReady());
    assertEquals("One doc should have been collected", 1, _memLeakCounter);
    
    doc1.getLength(); 
    
    
    List<FinalizationListener<DefinitionsDocument>> list = doc1.getFinalizationListeners();
    assertEquals("There should only be one finalization listener", 1, list.size());
    assertEquals("The finalization listener should be fl", fl, list.get(0));
    
    doc2.getLength(); 
    doc3.getLength(); 
    doc4.getLength(); 
    doc5.getLength(); 
    
    System.gc();
    Thread.sleep(100);
    assertEquals("several docs should have been collected", 6, _memLeakCounter);
    
  }
  private int _memLeakCounter;
  
  
  private class TestGlobalModel extends DefaultGlobalModel {
    public void aboutToSaveFromSaveAll(OpenDefinitionsDocument doc) {  }
    public void saveAllFiles(FileSaveSelector fs) throws IOException { saveAllFilesHelper(fs); }
    
    public OpenDefinitionsDocument newFile() { return newFile(getMasterWorkingDirectory()); }
    public OpenDefinitionsDocument openFile(FileOpenSelector fs) 
      throws IOException, OperationCanceledException, AlreadyOpenException { 
      return openFileHelper(fs); 
    }
    public boolean closeFile(OpenDefinitionsDocument doc) { return closeFileHelper(doc); }
    public OpenDefinitionsDocument[] openFiles(FileOpenSelector com)
      throws IOException, OperationCanceledException, AlreadyOpenException {
      return openFilesHelper(com); 
    }
    public boolean closeAllFiles() { 
      closeAllFilesOnQuit();
      return true;
    }
  }
}
