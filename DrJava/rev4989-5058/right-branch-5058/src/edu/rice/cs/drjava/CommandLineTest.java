

package edu.rice.cs.drjava;

import javax.swing.text.BadLocationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.drjava.model.definitions.InvalidPackageException;
import edu.rice.cs.drjava.ui.MainFrame;
import edu.rice.cs.plt.io.IOUtil;
import edu.rice.cs.util.Log;
import edu.rice.cs.util.StringOps;
import edu.rice.cs.util.swing.Utilities;


public final class CommandLineTest extends DrJavaTestCase {
  
  private static final char FS = File.separatorChar;
  
  
  private MainFrame _mf;
  
  
  private volatile File f1;
  private volatile String f1_name;
  private volatile String f1_contents;
  private volatile File f2;
  private volatile String f2_name;
  private volatile String f2_contents;
  private volatile File f3;
  private volatile String f3_name;
  private volatile String f3_contents;
  private volatile File f4;
  private volatile String f4_name;
  private volatile String f4_contents;
  private volatile File f5;
  private volatile String f5_name;
  private volatile String f5_contents;
  private volatile File f6;
  private volatile String f6_name;
  private volatile String f6_contents;
  private volatile File f7;
  private volatile String f7_name;
  private volatile String f7_contents;
  private volatile File f8;
  private volatile String f8_name;
  private volatile String f8_contents;
  
  
  
  private volatile File nof1;
  private volatile File nof2;
  private volatile File nof3;
  private volatile File nof4;
  private volatile File nof5;
  private volatile String nof1_name;
  private volatile String nof2_name;
  private volatile String nof3_name;
  private volatile String nof4_name;
  private volatile String nof5_name;
  
  private Log _log = new Log("CommandLineTest.txt", false);
  
  
  public CommandLineTest(String name) { super(name); }
  
  public void setUp() throws Exception {
    super.setUp();
    


    Utilities.invokeAndWait(new Runnable() { 
      public void run() {
        
        _log.log("Creating a MainFrame for " + this);
        _mf = new MainFrame(); 
        _log.log("Created a MainFrame for " + this + "; stating file setup");
        
        try {
          f1 = File.createTempFile("DrJava-test", ".java").getCanonicalFile();
          f1.deleteOnExit();
          f1_name = f1.getAbsolutePath();
          f1_contents = "abcde";
          FileWriter fw1 = new FileWriter(f1);
          fw1.write(f1_contents,0,f1_contents.length());
          fw1.close();
          f2 = File.createTempFile("DrJava-test", ".java").getCanonicalFile();
          f2.deleteOnExit();
          f2_name = f2.getAbsolutePath();
          f2_contents = "fghijklm";
          FileWriter fw2 = new FileWriter(f2);
          fw2.write(f2_contents,0,f2_contents.length());
          fw2.close();
          f3 = File.createTempFile("DrJava-test", ".java").getCanonicalFile();
          f3.deleteOnExit();
          f3_name = f3.getAbsolutePath();
          f3_contents = "nopqrstuvwxyz";
          FileWriter fw3 = new FileWriter(f3);
          fw3.write(f3_contents,0,f3_contents.length());
          fw3.close();
          f4 = File.createTempFile("DrJava-test", ".java").getCanonicalFile();
          f4.deleteOnExit();
          f4_name = f4.getAbsolutePath();
          f4_contents = "abcde";
          FileWriter fw4 = new FileWriter(f4);
          fw4.write(f4_contents,0,f4_contents.length());
          fw4.close();
          f5 = File.createTempFile("DrJava-test", ".java").getCanonicalFile();
          f5.deleteOnExit();
          f5_name = f5.getAbsolutePath();
          f5_contents = "fghijklm";
          FileWriter fw5 = new FileWriter(f5);
          fw5.write(f5_contents,0,f5_contents.length());
          fw5.close();
          f6 = File.createTempFile("DrJava-test", ".java").getCanonicalFile();
          f6.deleteOnExit();
          f6_name = f6.getAbsolutePath();
          f6_contents = "nopqrstuvwxyz";
          FileWriter fw6 = new FileWriter(f6);
          fw6.write(f6_contents,0,f6_contents.length());
          fw6.close();
          f7 = File.createTempFile("DrJava-test", ".java").getCanonicalFile();
          f7.deleteOnExit();
          f7_name = f7.getAbsolutePath();
          f7_contents = "abcde";
          FileWriter fw7 = new FileWriter(f7);
          fw7.write(f7_contents,0,f7_contents.length());
          fw7.close();
          f8 = File.createTempFile("DrJava-test", ".java").getCanonicalFile();
          f8.deleteOnExit();
          f8_name = f8.getAbsolutePath();
          f8_contents = "fghijklm";
          FileWriter fw8 = new FileWriter(f8);
          fw8.write(f8_contents,0,f8_contents.length());
          fw8.close();
          
          nof1 = File.createTempFile("DrJava-test", ".java").getCanonicalFile();
          nof1_name = nof1.getAbsolutePath();
          nof1.delete();
          nof2 = File.createTempFile("DrJava-test", ".java").getCanonicalFile();
          nof2_name = nof2.getAbsolutePath();
          nof2.delete();
          nof3 = File.createTempFile("DrJava-test", ".java").getCanonicalFile();
          nof3_name = nof3.getAbsolutePath();
          nof3.delete();
          nof4 = File.createTempFile("DrJava-test", ".java").getCanonicalFile();
          nof4_name = nof4.getAbsolutePath();
          nof4.delete();
          nof5 = File.createTempFile("DrJava-test", ".java").getCanonicalFile();
          nof5_name = nof5.getAbsolutePath();
          nof5.delete();
          
          _log.log("File initialization (setUp) is complete");
        }
        catch (IOException e) {
          System.out.print("createTempFile failed.  This should not happen.");
          throw new RuntimeException(e.toString());
        }
      }
    });
  }
  
  public void tearDown() throws Exception {
    Utilities.invokeAndWait(new Runnable() { public void run() { _mf.dispose(); } });
    _mf = null;
    super.tearDown();
  }
  
  
  public void testNone() {
    DrJavaRoot.openCommandLineFiles(_mf, new String[0],false);
    
    
    List<OpenDefinitionsDocument> docs = _mf.getModel().getOpenDefinitionsDocuments();
    assertEquals("Only one document?", 1, docs.size());
    OpenDefinitionsDocument doc = docs.get(0);
    assertTrue("Is new document untitled?", doc.isUntitled());
    _log.log("testNone() completed");
    
  }
  
  
  public void testOpenOne() throws BadLocationException {
    String[] list = new String[1];
    list[0] = f1_name;
    DrJavaRoot.openCommandLineFiles(_mf, list,false);

    List<OpenDefinitionsDocument> docs = _mf.getModel().getOpenDefinitionsDocuments();


    assertEquals("Only one document opened?", 1, docs.size());
    OpenDefinitionsDocument doc = docs.get(0);

    assertEquals("Correct length of file?", f1_contents.length(), doc.getLength());

    assertEquals("Do the contents match?", f1_contents, doc.getText(0,f1_contents.length()));
    _log.log("testOpenOne completed");
  }
  
  
  public void testNE() {
    String[] list = new String[1];
    list[0] = nof1_name;
    DrJavaRoot.openCommandLineFiles(_mf, list, false);

    List<OpenDefinitionsDocument> docs = _mf.getModel().getOpenDefinitionsDocuments();
    assertEquals("Exactly one document?", 1, docs.size());
    OpenDefinitionsDocument doc = docs.get(0);
    assertTrue("Is document untitled?", doc.isUntitled());
    _log.log("testNE completed");
  }
  
  
  public void testOpenMany() throws BadLocationException {
    String[] list = new String[3];
    list[0] = f1_name;
    list[1] = f2_name;
    list[2] = f3_name;
    DrJavaRoot.openCommandLineFiles(_mf, list, false);

    List<OpenDefinitionsDocument> docs = _mf.getModel().getOpenDefinitionsDocuments();
    assertEquals("Exactly three documents?", 3, docs.size());
    OpenDefinitionsDocument doc1 = docs.get(0);
    assertEquals("Correct length of file 1?", f1_contents.length(), doc1.getLength());
    assertEquals("Do the contents of file 1 match?", f1_contents, doc1.getText(0,f1_contents.length()));
    
    OpenDefinitionsDocument doc2 = docs.get(1);
    assertEquals("Correct length of file 2?", f2_contents.length(), doc2.getLength());
    assertEquals("Do the contents of file 2 match?", f2_contents, doc2.getText(0,f2_contents.length()));
    
    OpenDefinitionsDocument doc3 = docs.get(2);
    assertEquals("Correct length of file 3?", f3_contents.length(), doc3.getLength());
    assertEquals("Do the contents of file 3 match?", f3_contents, doc3.getText(0,f3_contents.length()));
    
    assertEquals("Is the last document the active one?", doc3, _mf.getModel().getActiveDocument());
    _log.log("testOpenMany completed");
  }
  
  
  public void testMixed() throws BadLocationException {
    String[] list = new String[6];
    list[0] = f4_name;
    list[1] = nof1_name;
    list[2] = nof2_name;
    list[3] = f5_name;
    list[4] = f6_name;
    list[5] = nof3_name;
    DrJavaRoot.openCommandLineFiles(_mf, list, false);

    List<OpenDefinitionsDocument> docs = _mf.getModel().getOpenDefinitionsDocuments();
    assertEquals("Exactly three documents?", 3, docs.size());
    OpenDefinitionsDocument doc1 = docs.get(0);
    assertEquals("Correct length of file 1?", f4_contents.length(), doc1.getLength());
    assertEquals("Do the contents of file 1 match?", f4_contents, doc1.getText(0,f4_contents.length()));
    
    OpenDefinitionsDocument doc2 = docs.get(1);
    assertEquals("Correct length of file 2?", f5_contents.length(), doc2.getLength());
    assertEquals("Do the contents of file 2 match?", f5_contents, doc2.getText(0,f5_contents.length()));
    
    OpenDefinitionsDocument doc3 = docs.get(2);
    assertEquals("Correct length of file 3?", f6_contents.length(), doc3.getLength());
    assertEquals("Do the contents of file 3 match?", f6_contents, doc3.getText(0,f6_contents.length()));
    
    assertEquals("Is the last document the active one?", doc3, _mf.getModel().getActiveDocument());
    _log.log("testMixed completed");
  }
  
  
  public void testDups() throws BadLocationException {
    String[] list = new String[6];
    list[0] = f7_name;
    list[1] = nof4_name;
    list[2] = nof5_name;
    list[3] = f8_name;
    list[4] = f8_name;
    list[5] = f7_name;
    DrJavaRoot.openCommandLineFiles(_mf, list, false);

    
    List<OpenDefinitionsDocument> docs = _mf.getModel().getOpenDefinitionsDocuments();
    Utilities.clearEventQueue();
    assertEquals("Exactly two documents?", 2, docs.size());
    OpenDefinitionsDocument doc1 = docs.get(0);
    assertEquals("Correct length of file 1?", f7_contents.length(), doc1.getLength());
    assertEquals("Do the contents of file 1 match?", f7_contents, doc1.getText(0,f7_contents.length()));
    Utilities.clearEventQueue();
    OpenDefinitionsDocument doc2 = docs.get(1);
    assertEquals("Correct length of file 2?", f8_contents.length(), doc2.getLength());
    assertEquals("Do the contents of file 2 match?", f8_contents, doc2.getText(0,f8_contents.length()));
    
    assertEquals("Is the last document the active one?", doc2, _mf.getModel().getActiveDocument());

  }
  
  
  public void testRelativePath() throws IOException, InvalidPackageException {
    String funnyName = "DrJava_automatically_deletes_this_1";
    File newDirectory = mkTempDir(funnyName);
    File relativeFile = new File(newDirectory, "X.java");
    
    assertEquals(relativeFile + " is absolute?", false, relativeFile.isAbsolute());
    
    try { checkFile(relativeFile, funnyName); }
    catch (Exception e) { fail("Exception thrown: " + StringOps.getStackTrace(e)); }
    finally { IOUtil.deleteOnExitRecursively(newDirectory); }
    _log.log("testRelativePath completed");
  }
  
  
  public void testDotPaths() {
    String funnyName = "DrJava_automatically_deletes_this_2";
    File newDirectory = mkTempDir(funnyName);
    
    assertTrue("child directory created OK", new File(newDirectory, "childDir").mkdir());
    
    File relativeFile = new File(newDirectory, "." + FS + "X.java");
    File relativeFile2 = new File(newDirectory, "." + FS + "Y.java");
    File relativeFile3 = new File(newDirectory, "childDir" + FS + ".." + FS + "Z.java");
    
    try {
      checkFile(relativeFile, funnyName);
      checkFile(relativeFile2, funnyName);
      checkFile(relativeFile3, funnyName);
    }
    catch (Exception e) { fail("Exception thrown: " + StringOps.getStackTrace(e)); }
    finally { IOUtil.deleteOnExitRecursively(newDirectory); }
    _log.log("testDotPaths completed");
  }
  
  
  private File mkTempDir(String funnyName) {
    
    
    File newDirectory = new File(funnyName);
    if (newDirectory.exists()) IOUtil.deleteOnExitRecursively(newDirectory);

    assertTrue("directory created OK", newDirectory.mkdir());

    return newDirectory;
  }
  
  
  private void checkFile(File relativeFile, String funnyName) throws IOException, InvalidPackageException {
    IOUtil.writeStringToFile(relativeFile, "package " + funnyName + "; class X { }");
    assertTrue("file exists", relativeFile.exists());
    
    String path = relativeFile.getCanonicalPath();
    DrJavaRoot.openCommandLineFiles(_mf, new String[] { path }, false);
    
    List<OpenDefinitionsDocument> docs = _mf.getModel().getOpenDefinitionsDocuments();
    assertEquals("Number of open documents", 1, docs.size());
    
    OpenDefinitionsDocument doc = docs.get(0);
    
    assertEquals("OpenDefDoc file is the right one and is canonical", relativeFile.getCanonicalFile(), doc.getFile());
    
    
    Utilities.clearEventQueue();
    File root = doc.getSourceRoot();
    Utilities.clearEventQueue();


    assertEquals("source root", IOUtil.WORKING_DIRECTORY.getCanonicalFile(), root);
    
    
    _mf.getModel().closeFile(doc);
  }
}
