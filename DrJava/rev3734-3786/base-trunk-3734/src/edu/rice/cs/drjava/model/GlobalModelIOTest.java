

package edu.rice.cs.drjava.model;

import java.io.*;

import java.util.List;
import javax.swing.text.BadLocationException;

import edu.rice.cs.util.FileOpenSelector;
import edu.rice.cs.util.FileOps;
import edu.rice.cs.util.OperationCanceledException;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.text.ConsoleDocument;
import edu.rice.cs.util.text.EditDocumentException;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.drjava.model.repl.*;
import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.OptionConstants;


public final class GlobalModelIOTest extends GlobalModelTestCase
  implements OptionConstants {
  
  public void testMultipleFiles() throws BadLocationException {
    assertNumOpenDocs(1);

    OpenDefinitionsDocument doc1 = setupDocument(FOO_TEXT);
    assertNumOpenDocs(2);

    
    OpenDefinitionsDocument doc2 = _model.newFile();
    assertNumOpenDocs(3);
    assertModified(true, doc1);
    assertModified(false, doc2);
    assertContents(FOO_TEXT, doc1);
    assertLength(0, doc2);

    
    changeDocumentText(BAR_TEXT, doc2);
    assertModified(true, doc2);
    assertContents(FOO_TEXT, doc1);
    assertContents(BAR_TEXT, doc2);
  }

  
  public void testMultipleFilesArray() throws BadLocationException {
    OpenDefinitionsDocument doc1, doc2, doc3;
    doc1 = setupDocument(FOO_TEXT);
    doc2 = setupDocument(BAR_TEXT);
    doc3 = setupDocument(FOO_TEXT);
    assertNumOpenDocs(4);

    List<OpenDefinitionsDocument> docs = _model.getOpenDefinitionsDocuments();
    assertEquals("size of document array", 4, docs.size());

    assertEquals("document 1", doc1, docs.get(1));
    assertEquals("document 2", doc2, docs.get(2));
    assertEquals("document 3", doc3, docs.get(3));
  }


  
  public void testCloseMultipleFiles() throws BadLocationException {
    assertNumOpenDocs(1);
    OpenDefinitionsDocument doc1 = setupDocument(FOO_TEXT);
    assertNumOpenDocs(2);
    OpenDefinitionsDocument doc2 = setupDocument(BAR_TEXT);
    assertNumOpenDocs(3);

    _model.closeFile(doc1);
    assertNumOpenDocs(2);

    List<OpenDefinitionsDocument> docs = _model.getOpenDefinitionsDocuments();
    assertEquals("size of document array", 2, docs.size());
    assertContents(BAR_TEXT, docs.get(1));

    _model.closeFile(doc2);
    assertNumOpenDocs(1);
    docs = _model.getOpenDefinitionsDocuments();
    assertEquals("size of document array", 1, docs.size());
  }


  
  public void testCloseFileAllowAbandon() throws BadLocationException {
    OpenDefinitionsDocument doc = setupDocument(FOO_TEXT);

    
    TestListener listener = new TestListener() {
      public boolean canAbandonFile(OpenDefinitionsDocument doc) {
        canAbandonCount++;
        return true; 
      }

      public void fileClosed(OpenDefinitionsDocument doc) {
        assertAbandonCount(1);
        closeCount++;
      }
    };

    _model.addListener(listener);
    _model.closeFile(doc);
    listener.assertCloseCount(1);
  }

  
  public void testCloseFileDisallowAbandon() throws BadLocationException {
    OpenDefinitionsDocument doc = setupDocument(FOO_TEXT);

    TestListener listener = new TestListener() {
      public boolean canAbandonFile(OpenDefinitionsDocument doc) {
        canAbandonCount++;
        return false; 
      }

      public void fileClosed(OpenDefinitionsDocument doc) {
        closeCount++;
      }
    };

    _model.addListener(listener);
    _model.closeFile(doc);
    listener.assertAbandonCount(1);
    listener.assertCloseCount(0);
  }

  
  public void testOpenRealFile() throws BadLocationException, IOException {
    final File tempFile = writeToNewTempFile(BAR_TEXT);

    TestListener listener = new TestListener() {
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
      
      public void fileClosed(OpenDefinitionsDocument doc) {
        


      }
    };

    _model.addListener(listener);
    try {
      OpenDefinitionsDocument doc = _model.openFile(new FileSelector(tempFile));
      listener.assertOpenCount(1);
      assertModified(false, doc);
      assertContents(BAR_TEXT, doc);
    }
    catch (AlreadyOpenException aoe) {
      
      fail("File was already open!");
    }
    catch (OperationCanceledException oce) {
      
      fail("Open was unexpectedly canceled!");
    }
  }

  
  public void testCancelOpenFile() throws BadLocationException, IOException {

    OpenDefinitionsDocument doc = setupDocument(FOO_TEXT);
    assertNumOpenDocs(2);

    TestListener listener = new TestListener() {
      public boolean canAbandonFile(OpenDefinitionsDocument doc) {
        canAbandonCount++;
        return true; 
      }

      public void fileOpened(OpenDefinitionsDocument doc) {
        openCount++;
      }
      
      public void fileClosed(OpenDefinitionsDocument doc) {
        


      }
    };

    _model.addListener(listener);
    try {
      
        _model.openFile(new CancelingSelector());
    }
    catch (AlreadyOpenException aoe) {
      
      fail("File was already open!");
    }
    catch (OperationCanceledException oce) {
      
    }
    finally {
      assertNumOpenDocs(2);
      listener.assertOpenCount(0);

      List<OpenDefinitionsDocument> docs = _model.getOpenDefinitionsDocuments();
      doc = docs.get(1);
      assertModified(true, doc);
      assertContents(FOO_TEXT, doc);
    }
  }


  
  public void testOpenNonexistentFile() throws IOException {
    _model.addListener(new TestListener());

    OpenDefinitionsDocument doc = null;

    try {
      doc = _model.openFile(new FileSelector(new File("fake-file")));
      fail("IO exception was not thrown!");
    }
    catch (FileNotFoundException fnf) {
      
    }
    catch (AlreadyOpenException aoe) {
      
      fail("File was already open!");
    }
    catch (OperationCanceledException oce) {
      
      fail("Open was unexpectedly canceled!");
    }

    assertEquals("non-existant file", doc, null);
  }

  
  public void testReopenFile() throws BadLocationException, IOException {
    final File tempFile = writeToNewTempFile(BAR_TEXT);

    TestListener listener = new TestListener() {
      public void fileOpened(OpenDefinitionsDocument doc) {
        File file = null;
        try { file = doc.getFile(); }
        catch (FileMovedException fme) {
          
          fail("file does not exist");
        }
        try {
          assertEquals("file to open", tempFile.getCanonicalPath(),
                       file.getCanonicalPath());
        }
        catch (IOException ioe) {
          throw new UnexpectedException(ioe);
        }
        openCount++;
      }
      
      public void fileClosed(OpenDefinitionsDocument doc) {
        


      }
    };

    _model.addListener(listener);
    try {
      OpenDefinitionsDocument doc = _model.openFile(new FileSelector(tempFile));
      listener.assertOpenCount(1);
      assertModified(false, doc);
      assertContents(BAR_TEXT, doc);
    }
    catch (AlreadyOpenException aoe) {
      
      fail("File was already open!");
    }
    catch (OperationCanceledException oce) {
      
      fail("Open was unexpectedly canceled!");
    }

    
    try {
      
        _model.openFile(new FileSelector(tempFile));
      fail("file should already be open");
    }
    catch (AlreadyOpenException aoe) {
      
      listener.assertOpenCount(1);
    }
    catch (OperationCanceledException oce) {
      
      fail("Open was unexpectedly canceled!");
    }

    
    
    try {
      File parent = tempFile.getParentFile();
      String dotSlash = "." + System.getProperty("file.separator");
      parent = new File(parent, dotSlash);
      File sameFile = new File(parent, tempFile.getName());
      
        _model.openFile(new FileSelector(sameFile));
      fail("file should already be open");
    }
    catch (AlreadyOpenException aoe) {
      
      listener.assertOpenCount(1);
    }
    catch (OperationCanceledException oce) {
      
      fail("Open was unexpectedly canceled!");
    }

  }

  
  public void testOpenMultipleFiles() throws BadLocationException, IOException {
    final File tempFile1 = writeToNewTempFile(FOO_TEXT);
    final File tempFile2 = writeToNewTempFile(BAR_TEXT);

    TestListener listener = new TestListener() {
      public void fileOpened(OpenDefinitionsDocument doc) {
        File file = null;
        try { file = doc.getFile(); }
        catch (FileMovedException fme) {
          
          fail("file does not exist");
        }

        try {
          if (tempFile1.equals(file)) {
            assertEquals("file to open", tempFile1.getCanonicalFile(), file.getCanonicalFile());
          } else {
            assertEquals("file to open", tempFile2.getCanonicalFile(), file.getCanonicalFile());
          }
          openCount++;
        }
        catch (IOException ioe) {
          fail("could not get canonical file");
        }
      }
      public void fileClosed(OpenDefinitionsDocument doc) {
        


      }
    };

    _model.addListener(listener);
    try {
      OpenDefinitionsDocument doc = _model.openFiles(new FileSelector(tempFile1,tempFile2));
      listener.assertOpenCount(2);
      assertModified(false, doc);
      assertContents(BAR_TEXT, doc);
    }
    catch (AlreadyOpenException aoe) {
      
      fail("File was already open!");
    }
    catch (OperationCanceledException oce) {
      
      fail("Open was unexpectedly canceled!");
    }
    listener.assertOpenCount(2);
    List<OpenDefinitionsDocument> docs = _model.getOpenDefinitionsDocuments();
    assertEquals("size of document array", 2, docs.size());
    assertContents(FOO_TEXT, docs.get(0));
    assertContents(BAR_TEXT, docs.get(1));

  }

  
  public void testCancelOpenMultipleFiles()
    throws BadLocationException, IOException
  {

    OpenDefinitionsDocument doc1 = setupDocument(FOO_TEXT);
    OpenDefinitionsDocument doc2 = setupDocument(BAR_TEXT);
    assertNumOpenDocs(3);

    TestListener listener = new TestListener() {
      public boolean canAbandonFile(OpenDefinitionsDocument doc) {
        canAbandonCount++;
        return true; 
      }

      public void fileOpened(OpenDefinitionsDocument doc) {
        openCount++;
      }
      
      public void fileClosed(OpenDefinitionsDocument doc) {
        


      }
    };

    _model.addListener(listener);
    try {
      
        _model.openFiles(new CancelingSelector());
    }
    catch (AlreadyOpenException aoe) {
      
      fail("File was already open!");
    }
    catch (OperationCanceledException oce) {
      
    }
    finally {
      assertNumOpenDocs(3);
      listener.assertOpenCount(0);

      List<OpenDefinitionsDocument> docs = _model.getOpenDefinitionsDocuments();
      doc1 = docs.get(1);
      assertModified(true, doc1);
      assertContents(FOO_TEXT, doc1);

      doc2 = docs.get(2);
      assertModified(true, doc2);
      assertContents(BAR_TEXT, doc2);
    }
  }

  
  public void testOpenMultipleNonexistentFiles() throws IOException {

    OpenDefinitionsDocument doc = null;
    final File tempFile1 = writeToNewTempFile(FOO_TEXT);

    
    TestListener listener = new TestListener() {

      public void fileNotFound(File f) { fileNotFoundCount++; }

      public void fileOpened(OpenDefinitionsDocument doc) {
        File file = null;
        try { file = doc.getFile(); }
        catch (FileMovedException fme) {
          
          fail("file does not exist");
        }
        try {
          assertEquals("file to open", tempFile1.getCanonicalFile(), file.getCanonicalFile());
          openCount++;
        }
        catch (IOException ioe) { fail("could not get canonical file"); }
      }
      
      public void fileClosed(OpenDefinitionsDocument doc) {
        


      }
    };
    _model.addListener(listener);


    try {
      doc = _model.openFiles(new FileSelector(tempFile1,
                                              new File("fake-file")));
    }
    catch (FileNotFoundException fnf) {
      fail("FileNotFound exception was not thrown!");
    }
    catch (AlreadyOpenException aoe) {
      
      fail("File was already open!");
    }
    catch (OperationCanceledException oce) {
      
      fail("Open was unexpectedly canceled!");
    }
    assertTrue("one file was opened", doc instanceof OpenDefinitionsDocument);
    listener.assertOpenCount(1);
    listener.assertFileNotFoundCount(1);
  }

  
  public void testOpenMultipleFilesError() {

    OpenDefinitionsDocument doc = null;
    

    try {
      doc = _model.openFiles(new FileOpenSelector() {
        public File[] getFiles() {
          return new File[] {null};
        }
      });
      fail("IO exception was not thrown!");
    }
    catch (IOException e) {
      
    }
    catch (Exception e) {
      fail("Unexpectedly exception caught!");
    }

    try {
      doc = _model.openFiles(new FileOpenSelector() {
        public File[] getFiles() {
          return null;
        }
      });

      fail("IO exception was not thrown!");
    }
    catch (IOException e) {
      
    }
    catch (Exception e) {
      fail("Unexpectedly exception caught!");
    }

    assertEquals("non-existant file", doc, null);
  }

  
  public void testForceFileOpen()
    throws BadLocationException, IOException, OperationCanceledException,
    AlreadyOpenException
  {
    final File tempFile1 = writeToNewTempFile(FOO_TEXT);
    final File tempFile2 = writeToNewTempFile(BAR_TEXT);
    

    TestListener listener = new TestListener() {
      public void fileOpened(OpenDefinitionsDocument doc) {
        try { doc.getFile(); }
        catch (FileMovedException fme) {
          
          fail("file does not exist");
        }
        openCount++;
      }
      
      public void fileClosed(OpenDefinitionsDocument doc) {
        


      }
    };

    _model.addListener(listener);
    
    OpenDefinitionsDocument doc = _model.openFile(new FileSelector(tempFile1));
    listener.assertOpenCount(1);
    assertModified(false, doc);
    assertContents(FOO_TEXT, doc);

    
    OpenDefinitionsDocument doc1 = _model.getDocumentForFile(tempFile1);
    listener.assertOpenCount(1);
    assertEquals("opened document", doc, doc1);
    assertContents(FOO_TEXT, doc1);

    
    OpenDefinitionsDocument doc2 = _model.getDocumentForFile(tempFile2);
    listener.assertOpenCount(2);
    assertContents(BAR_TEXT, doc2);
  }

  
  public void testCancelFirstSave() throws BadLocationException, IOException {
    OpenDefinitionsDocument doc = setupDocument(FOO_TEXT);

    
    _model.addListener(new TestListener());

    boolean saved = doc.saveFile(new CancelingSelector());
    assertTrue("doc should not have been saved", !saved);
    assertModified(true, doc);
    assertContents(FOO_TEXT, doc);
  }

  
  public void testRealSaveFirstSave() throws BadLocationException, IOException {
    OpenDefinitionsDocument doc = setupDocument(FOO_TEXT);
    final File file = tempFile();

    TestListener listener = new TestListener() {
      public void fileSaved(OpenDefinitionsDocument doc) {
        File f = null;
        try { f = doc.getFile(); }
        catch (FileMovedException fme) {
          
          fail("file does not exist");
        }
        try {
          assertEquals("saved file name", file.getCanonicalFile(), f.getCanonicalFile());
          saveCount++;
        }
        catch (IOException ioe) {
          fail("could not get canonical file");
        }
      }
    };

    _model.addListener(listener);

    doc.saveFile(new FileSelector(file));
    listener.assertSaveCount(1);
    assertModified(false, doc);
    assertContents(FOO_TEXT, doc);

    assertEquals("contents of saved file",
                 FOO_TEXT,
                 FileOps.readFileAsString(file));
  }

  
  public void testSaveAlreadySaved() throws BadLocationException, IOException {
    
    Boolean backupStatus = DrJava.getConfig().getSetting(BACKUP_FILES);
    DrJava.getConfig().setSetting(BACKUP_FILES, Boolean.FALSE);

    OpenDefinitionsDocument doc = setupDocument(FOO_TEXT);
    final File file = tempFile();

    
    doc.saveFile(new FileSelector(file));
    assertModified(false, doc);
    assertContents(FOO_TEXT, doc);
    assertEquals("contents of saved file",
                 FOO_TEXT,
                 FileOps.readFileAsString(file));

    
    TestListener listener = new TestListener() {
      public void fileSaved(OpenDefinitionsDocument doc) {
        File f = null;
        try { f = doc.getFile(); }
        catch (FileMovedException fme) {
          
          fail("file does not exist");
        }
        try {
          assertEquals("saved file", file.getCanonicalFile(), f.getCanonicalFile());
          saveCount++;
        }
        catch (IOException ioe) { fail("could not get canonical file"); }
      }
    };

    File backup = new File(file.getPath() + "~");
    backup.delete();

    _model.addListener(listener);

    
    changeDocumentText(BAR_TEXT, doc);

    
    doc.saveFile(new FileSelector(file));
    
    Utilities.clearEventQueue();
    listener.assertSaveCount(1);
    assertEquals("contents of saved file 2nd write", BAR_TEXT, FileOps.readFileAsString(file));
    assertFalse("no backup was made", backup.exists());

    
    DrJava.getConfig().setSetting(BACKUP_FILES, Boolean.TRUE);

    
    changeDocumentText(FOO_TEXT, doc);
    Utilities.clearEventQueue();
    
    
    doc.saveFile(new FileSelector(file));
        
    Utilities.clearEventQueue();
    listener.assertSaveCount(2);
    assertEquals("contents of saved file 3rd write", FOO_TEXT, FileOps.readFileAsString(file));
    assertEquals("contents of backup file 3rd write", BAR_TEXT, FileOps.readFileAsString(backup));

    
    DrJava.getConfig().setSetting(BACKUP_FILES, backupStatus);
  }

  
  public void testCancelSaveAlreadySaved()
    throws BadLocationException, IOException
  {
    OpenDefinitionsDocument doc = setupDocument(FOO_TEXT);
    final File file = tempFile();

    
    doc.saveFile(new FileSelector(file));
    assertModified(false, doc);
    assertContents(FOO_TEXT, doc);
    assertEquals("contents of saved file",
                 FOO_TEXT,
                 FileOps.readFileAsString(file));

    TestListener listener = new TestListener() {
      public void fileSaved(OpenDefinitionsDocument doc) {
        File f = null;
        try { f = doc.getFile(); }
        catch (FileMovedException fme) {
          
          fail("file does not exist");
        }
        try {
          assertEquals("saved file", file.getCanonicalFile(), f.getCanonicalFile());
          saveCount++;
        }
        catch (IOException ioe) { fail("could not get canonical file"); }
      }
    };

    _model.addListener(listener);

    
    changeDocumentText(BAR_TEXT, doc);

    doc.saveFile(new CancelingSelector());

    
    
    listener.assertSaveCount(1);
    assertModified(false, doc);
    assertContents(BAR_TEXT, doc);

    assertEquals("contents of saved file",
                 BAR_TEXT,
                 FileOps.readFileAsString(file));
  }

  
  public void testCancelSaveAsAlreadySaved()
    throws BadLocationException, IOException
  {
    OpenDefinitionsDocument doc = setupDocument(FOO_TEXT);
    final File file = tempFile();

    
    doc.saveFile(new FileSelector(file));
    assertModified(false, doc);
    assertContents(FOO_TEXT, doc);
    assertEquals("contents of saved file",
                 FOO_TEXT,
                 FileOps.readFileAsString(file));

    
    _model.addListener(new TestListener());

    
    changeDocumentText(BAR_TEXT, doc);

    doc.saveFileAs(new CancelingSelector());

    assertEquals("contents of saved file",
                 FOO_TEXT,
                 FileOps.readFileAsString(file));
  }

  
  public void testSaveAsAlreadySaved()
    throws BadLocationException, IOException
  {
    OpenDefinitionsDocument doc = setupDocument(FOO_TEXT);
    final File file1 = tempFile();
    final File file2 = tempFile();

    
    doc.saveFile(new FileSelector(file1));
    assertModified(false, doc);
    assertContents(FOO_TEXT, doc);
    assertEquals("contents of saved file",
                 FOO_TEXT,
                 FileOps.readFileAsString(file1));

    
    TestListener listener = new TestListener() {
      public void fileSaved(OpenDefinitionsDocument doc) {
        File f = null;
        try { f = doc.getFile(); }
        catch (FileMovedException fme) {
          
          fail("file does not exist");
        }
        try {
          assertEquals("saved file", file2.getCanonicalFile(), f.getCanonicalFile());
          saveCount++;
        }
        catch (IOException ioe) { fail("could not get canonical file"); }
      }
    };

    _model.addListener(listener);

    
    changeDocumentText(BAR_TEXT, doc);

    doc.saveFileAs(new FileSelector(file2));

    assertEquals("contents of saved file1", FOO_TEXT, FileOps.readFileAsString(file1));

    assertEquals("contents of saved file2", BAR_TEXT, FileOps.readFileAsString(file2));
  }

  public void testSaveAsExistsForOverwrite() throws BadLocationException, IOException {

    OpenDefinitionsDocument doc = setupDocument(FOO_TEXT);
    final File file1 = tempFile();
    try {
      doc.saveFileAs(new WarningFileSelector(file1));
      fail("Did not ask to verify overwrite as expected");
    }
    catch (OverwriteException e1) {
      
    }
  }

  public void testSaveAsExistsAndOpen() throws BadLocationException, IOException {
    OpenDefinitionsDocument doc1,doc2;
    final File file1,file2;

    file1 = tempFile(1);
    doc1 = _model.getDocumentForFile(file1);
    changeDocumentText(FOO_TEXT,doc1);
    doc1.saveFileAs(new FileSelector(file1));

    file2 = tempFile(2);
    doc2 = _model.getDocumentForFile(file2);
    changeDocumentText(BAR_TEXT, doc2);

    try {
      doc2.saveFileAs(new WarningFileSelector(file1));
      fail("Did not warn of open file as expected");
    }
    catch (OpenWarningException e) {
      
    }
  }


  
  public void testSaveAllSaveCorrectFiles()
    throws BadLocationException, IOException {
    OpenDefinitionsDocument fooDoc = setupDocument(FOO_TEXT);
    OpenDefinitionsDocument barDoc = setupDocument(BAR_TEXT);
    OpenDefinitionsDocument trdDoc = setupDocument("third document contents");
    final File file1 = tempFile();
    final File file2 = tempFile();
    final File file3 = tempFile();
    fooDoc.setFile(file1);
    barDoc.setFile(file2);
    trdDoc.setFile(file3);

    
    FileSelector fs = new FileSelector(file1);

    _model.saveAllFiles(fs); 

    assertEquals("contents of saved file1",
                 FOO_TEXT,
                 FileOps.readFileAsString(file1));
    assertEquals("contents of saved file2",
                 BAR_TEXT,
                 FileOps.readFileAsString(file2));
    assertEquals("contents of saved file3",
                 "third document contents",
                 FileOps.readFileAsString(file3));
  }
  
  public void testRevertFile()
    throws BadLocationException, IOException, OperationCanceledException,
    AlreadyOpenException
  {
    final File tempFile1 = writeToNewTempFile(FOO_TEXT);
    


    TestListener listener = new TestListener() {
      public void fileOpened(OpenDefinitionsDocument doc) {
        try { assertTrue("Source file should exist", doc.getFile() != null); }
        catch (FileMovedException fme) {
          
          fail("file does not exist");
        }
        openCount++;
      }
      public void fileClosed(OpenDefinitionsDocument doc) {
        


      }
      public void fileReverted(OpenDefinitionsDocument doc) {
        fileRevertedCount++;
      }
    };

    _model.addListener(listener);
    
    OpenDefinitionsDocument doc = _model.openFile(new FileSelector(tempFile1));
    listener.assertOpenCount(1);
    assertModified(false, doc);
    assertContents(FOO_TEXT, doc);

    assertEquals("original doc unmodified",doc.isModifiedSinceSave(), false);
    changeDocumentText(BAR_TEXT, doc);
    assertEquals("doc now modified",doc.isModifiedSinceSave(), true);
    tempFile1.delete();
    try {
      doc.revertFile();
      fail("File should not be on disk.");
    }
    catch (FileMovedException fme) {
      
    }
    assertEquals("doc NOT reverted",doc.isModifiedSinceSave(), true);
    assertContents(BAR_TEXT, doc);
  }


  public void testModifiedByOther()
    throws BadLocationException, IOException, OperationCanceledException,
    AlreadyOpenException, InterruptedException
  {
    final File tempFile1 = writeToNewTempFile(FOO_TEXT);
    

    TestListener listener = new TestListener() {
      public void fileOpened(OpenDefinitionsDocument doc) { }
      public void fileClosed(OpenDefinitionsDocument doc) {
        


      }
      public void fileReverted(OpenDefinitionsDocument doc) {
        fileRevertedCount++;
      }
      public boolean shouldRevertFile(OpenDefinitionsDocument doc) {
        shouldRevertFileCount++;
        return true;
      }
    };

    _model.addListener(listener);
    
    OpenDefinitionsDocument doc = _model.openFile(new FileSelector(tempFile1));
    listener.assertShouldRevertFileCount(0);
    listener.assertFileRevertedCount(0);
    assertModified(false, doc);

    doc.revertIfModifiedOnDisk();


    listener.assertShouldRevertFileCount(0);
    listener.assertFileRevertedCount(0);
    synchronized(tempFile1) {
      tempFile1.wait(2000);
    }

    String s = "THIS IS ONLY A TEST";
    FileOps.writeStringToFile(tempFile1, s);
    assertEquals("contents of saved file",
                 s,
                 FileOps.readFileAsString(tempFile1));

    tempFile1.setLastModified((new java.util.Date()).getTime());

    assertTrue("modified on disk1", doc.isModifiedOnDisk());
    boolean res = doc.revertIfModifiedOnDisk();
    assertTrue("file reverted", res);


    listener.assertShouldRevertFileCount(1);
    listener.assertFileRevertedCount(1);
    assertContents(s,doc);
  }

  public void testModifiedByOtherFalse()
    throws BadLocationException, IOException, OperationCanceledException,
    AlreadyOpenException, InterruptedException
  {
    final File tempFile1 = writeToNewTempFile(FOO_TEXT);
    

    TestListener listener = new TestListener() {
      public void fileOpened(OpenDefinitionsDocument doc) { }
      
      public void fileClosed(OpenDefinitionsDocument doc) {
        


      }

      public void fileReverted(OpenDefinitionsDocument doc) {
        fileRevertedCount++;
      }
      public boolean shouldRevertFile(OpenDefinitionsDocument doc) {
        shouldRevertFileCount++;
        return false;
      }
    };

    _model.addListener(listener);
    
    OpenDefinitionsDocument doc = _model.openFile(new FileSelector(tempFile1));
    listener.assertShouldRevertFileCount(0);
    listener.assertFileRevertedCount(0);
    assertModified(false, doc);

    doc.revertIfModifiedOnDisk();
    listener.assertShouldRevertFileCount(0);
    listener.assertFileRevertedCount(0);

    synchronized(tempFile1) {
      tempFile1.wait(2000);
    }

    String s = "THIS IS ONLY A TEST";
    FileOps.writeStringToFile(tempFile1, s);
    assertEquals("contents of saved file",
                 s,
                 FileOps.readFileAsString(tempFile1));

    assertTrue("modified on disk1", doc.isModifiedOnDisk());
    boolean reverted = doc.revertIfModifiedOnDisk();
    assertTrue("modified on disk", reverted == false);
    listener.assertShouldRevertFileCount(1);
    listener.assertFileRevertedCount(0);
    assertContents(FOO_TEXT, doc);
  }

  
  public void testSaveClearAndLoadHistory()
      throws EditDocumentException, IOException
  {
    String newLine = System.getProperty("line.separator");
    TestListener listener = new TestListener() {
      public void interactionStarted() {
        synchronized(this) {
          interactionStartCount++;
        }
      }
      public void interactionEnded() {
        synchronized(this) {
          interactionEndCount++;
          
          this.notify();
        }
      }
    };

    _model.addListener(listener);
    File f = tempFile();
    FileSelector fs = new FileSelector(f);
    String s1 = "int x = 5;";
    String s2 = "System.out.println(\"x = \" + x)";
    String s3 = "int y;" + newLine + "int z;";
    listener.assertInteractionStartCount(0);
    listener.assertInteractionEndCount(0);
    interpretIgnoreResult(s1);
    
    while (listener.interactionEndCount == 0) {
      synchronized(listener) {
        try {
          listener.wait();
        }
        catch (InterruptedException ie) {
          throw new UnexpectedException(ie);
        }
      }
    }
    listener.assertInteractionEndCount(1);
    listener.assertInteractionStartCount(1);
    interpretIgnoreResult(s2);
    while (listener.interactionEndCount == 1) {
      synchronized(listener) {
        try {
          listener.wait();
        }
        catch (InterruptedException ie) {
          throw new UnexpectedException(ie);
        }
      }
    }
    interpretIgnoreResult(s3);
    while (listener.interactionEndCount == 2) {
      synchronized(listener) {
        try {
          listener.wait();
        }
        catch (InterruptedException ie) {
          throw new UnexpectedException(ie);
        }
      }
    }
    
    assertEquals("History and getHistoryAsString should be the same.",
                 s1 + newLine + s2 + newLine + s3 + newLine,
                 _model.getHistoryAsString());
    String delim = History.INTERACTION_SEPARATOR + newLine;
    assertEquals("History and getHistoryAsStringWithSemicolons don't match up correctly.",
                 s1 + delim + s2 + delim + s3 + delim,
                 _model.getHistoryAsStringWithSemicolons());
    listener.assertInteractionEndCount(3);
    listener.assertInteractionStartCount(3);
    _model.saveHistory(fs);

    
    assertEquals("contents of saved file",
                 History.HISTORY_FORMAT_VERSION_2 +
                 s1 + delim + s2 + delim + s3 + delim,
                 FileOps.readFileAsString(f));

    _model.clearHistory();
    
    assertEquals("History is not clear",
                 "",
                 _model.getHistoryAsString());
    _model.loadHistory(fs);
    while (listener.interactionEndCount == 3) {
      synchronized(listener) {
        try {
          listener.wait();
        }
        catch (InterruptedException ie) {
          throw new UnexpectedException(ie);
        }
      }
    }
    
    ConsoleDocument con = _model.getConsoleDocument();
    assertEquals("Output of loaded history is not correct",
                 "x = 5",
                 con.getDocText(0, con.getLength()).trim());
    listener.assertInteractionStartCount(4);
    listener.assertInteractionEndCount(4);
    _model.removeListener(listener);
  }

  
  public void testLoadHistoryWithAndWithoutSemicolons() throws IOException, EditDocumentException {
    TestListener listener = new TestListener() {
      public void interactionStarted() {
        synchronized(this) {
          interactionStartCount++;
        }
      }
      public void interactionEnded() {
        synchronized(this) {
          interactionEndCount++;
          
          this.notify();
        }
      }
    };

    _model.addListener(listener);
    File f1 = tempFile(1);
    File f2 = tempFile(2);
    FileSelector fs1 = new FileSelector(f1);
    FileSelector fs2 = new FileSelector(f2);
    String s1 = "int x = 5;";
    String s2 = "System.out.println(\"x = \" + x)";
    String s3 = "x = 5;";
    String s4 = "System.out.println(\"x = \" + x)";
    FileOps.writeStringToFile(f1,s1+'\n'+s2+'\n');
    FileOps.writeStringToFile(f2,s3+'\n'+s4+'\n');

    listener.assertInteractionStartCount(0);
    _model.loadHistory(fs1);
    while (listener.interactionEndCount == 0) {
      synchronized(listener) {
        try {
          listener.wait();
        }
        catch (InterruptedException ie) {
          throw new UnexpectedException(ie);
        }
      }
    }
    _model.loadHistory(fs2);
    while (listener.interactionEndCount < 2) {
      synchronized(listener) {
        try {
          listener.wait();
        }
        catch (InterruptedException ie) {
          throw new UnexpectedException(ie);
        }
      }
    }
    
    ConsoleDocument con = _model.getConsoleDocument();
    assertEquals("Output of loaded history is not correct: " +
                 con.getDocText(0, con.getLength()).trim(),
                 "x = 5"+System.getProperty("line.separator")+"x = 5",
                 con.getDocText(0, con.getLength()).trim());
  }

  
  public void testFileMovedWhenTriedToSave()
    throws BadLocationException, IOException {

    OpenDefinitionsDocument doc = setupDocument(FOO_TEXT);
    final File file = tempFile();

    doc.saveFile(new FileSelector(file));

    TestListener listener = new TestListener();

    _model.addListener(listener);

    file.delete();
    changeDocumentText(BAR_TEXT, doc);
    try {
      doc.saveFile(new WarningFileSelector(file));
      fail("Save file should have thrown an exception");
    }
    catch (GlobalModelTestCase.FileMovedWarningException fme) {
      
      
    }

    assertModified(true, doc);
    assertContents(BAR_TEXT, doc);
  }

  
  public void testConsoleInput() throws EditDocumentException {
    _model.getInteractionsModel().setInputListener(new InputListener() {
      int n = 0;
      public String getConsoleInput() {
        n++;
        if (n > 1) {
          throw new IllegalStateException("Input should only be requested once!");
        }
        return "input\n";
      }
    });

    String result = interpret("System.in.read()");
    String expected = 
      String.valueOf((int)'i');
    assertEquals("read() should prompt for input and return the first byte of \"input\"",
                 expected, result);

    interpret("import java.io.*;");
    interpret("br = new BufferedReader(new InputStreamReader(System.in))");
    result = interpret("br.readLine()");
    assertEquals("readLine() should return the rest of \"input\" without prompting for input",
                 "\"nput\"", result);
  }
}
