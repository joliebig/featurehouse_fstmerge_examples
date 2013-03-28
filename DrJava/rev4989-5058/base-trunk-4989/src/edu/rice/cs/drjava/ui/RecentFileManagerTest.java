

package edu.rice.cs.drjava.ui;

import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.drjava.config.*;
import edu.rice.cs.plt.io.IOUtil;
import junit.framework.Test;
import junit.framework.TestSuite;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Vector;


public final class RecentFileManagerTest extends DrJavaTestCase {
  
  protected static final String FOO_TEXT = "class DrJavaTestFoo {}";
  protected static final String BAR_TEXT = "class DrJavaTestBar {}";
  private RecentFileManager _rfm;
  private JMenu _menu;
  protected File _tempDir;
  
  
  public static Test suite() { return  new TestSuite(RecentFileManagerTest.class); }
  
  
  public void setUp() throws Exception {
    super.setUp();
    _menu = new JMenu();
    _rfm = new RecentFileManager(0, _menu, null, OptionConstants.RECENT_FILES);
    String user = System.getProperty("user.name");
    _tempDir = IOUtil.createAndMarkTempDirectory("DrJava-test-" + user, "");
  }
  
  public void tearDown() throws Exception {
    _menu = null;
    _rfm = null;
    IOUtil.deleteRecursively(_tempDir);
    _tempDir = null;
    super.tearDown();
  }
  
  
  protected File tempFile() throws IOException {
    return File.createTempFile("DrJava-test", ".java", _tempDir).getCanonicalFile();
  }
  
  
  protected File writeToNewTempFile(String text) throws IOException {
    File temp = tempFile();
    IOUtil.writeStringToFile(temp, text);
    return temp;
  }
  
  
  public void testAddMoreThanMaxSize() throws IOException {
    
    final File tempFile = writeToNewTempFile(BAR_TEXT);
    final File tempFile2 = writeToNewTempFile(FOO_TEXT);
    _rfm.updateMax(1);
    _rfm.updateOpenFiles(tempFile);
    _rfm.updateOpenFiles(tempFile2);
    Vector<File> vector = _rfm.getFileVector();
    assertEquals("number of recent files", 1, vector.size());
    assertEquals("text of recent file", FOO_TEXT, IOUtil.toString(vector.get(0)));
  }
  
  
  public void testShrinksToMaxSize() throws IOException {
    
    final File tempFile = writeToNewTempFile(BAR_TEXT);
    final File tempFile2 = writeToNewTempFile(FOO_TEXT);
    _rfm.updateMax(2);
    
    _rfm.updateOpenFiles(tempFile);
    _rfm.updateOpenFiles(tempFile2);
    Vector<File> vector = _rfm.getFileVector();
    assertEquals("number of recent files", 2, vector.size());
    assertEquals("text of most-recent file", FOO_TEXT, IOUtil.toString(vector.get(0)));
    assertEquals("text of second-most recent file", BAR_TEXT, IOUtil.toString(vector.get(1)));
    _rfm.updateMax(1);
    _rfm.numberItems();
    vector = _rfm.getFileVector();
    assertEquals("number of recent files", 1, vector.size());
    assertEquals("text of recent file", FOO_TEXT, IOUtil.toString(vector.get(0)));
  }
  
  
  public void testRemoveFile() throws Exception {
    
    final File tempFile = writeToNewTempFile(BAR_TEXT);
    final File tempFile2 = writeToNewTempFile(FOO_TEXT);
    _rfm.updateMax(2);
    _rfm.updateOpenFiles(tempFile);
    _rfm.updateOpenFiles(tempFile2);
    Vector<File> vector = _rfm.getFileVector();
    assertEquals("tempFile2 should be at top", vector.get(0), tempFile2);
    
    
    _rfm.removeIfInList(tempFile2);
    assertEquals("number of recent files", 1, vector.size());
    assertEquals("tempFile should be at top", vector.get(0), tempFile);
    
    
    _rfm.removeIfInList(tempFile2);
    assertEquals("number of recent files", 1, vector.size());
    assertEquals("tempFile should still be at top", vector.get(0), tempFile);
    
    
    _rfm.removeIfInList(tempFile);
    assertEquals("number of recent files", 0, vector.size());
  }
  
  
  public void testReopenFiles() throws Exception {
    final File tempFile = writeToNewTempFile(BAR_TEXT);
    final File tempFile2 = writeToNewTempFile(FOO_TEXT);
    
    _rfm.updateMax(2);
    _rfm.updateOpenFiles(tempFile2);
    _rfm.updateOpenFiles(tempFile);
    Vector<File> vector = _rfm.getFileVector();
    
    assertEquals("tempFile should be at top", vector.get(0), tempFile);
    
    
    _rfm.updateOpenFiles(tempFile2);
    vector = _rfm.getFileVector();
    assertEquals("tempFile2 should be at top", vector.get(0), tempFile2);
    
    
    File parent = tempFile.getParentFile();
    String dotSlash = "." + System.getProperty("file.separator");
    parent = new File(parent, dotSlash);
    File sameFile = new File(parent, tempFile.getName());
    
    _rfm.updateOpenFiles(sameFile);
    vector = _rfm.getFileVector();
    assertEquals("sameFile should be at top", vector.get(0), sameFile);
    assertEquals("should only have two files", 2, vector.size());
    assertTrue("should not contain tempFile", !(vector.contains(tempFile)));
  }
  
  
  public void testDirectoryFilterDescription() {
    DirectoryFilter f = new DirectoryFilter();
    assertEquals("Should have the correct description.", "Directories", f.getDescription());
    f = new DirectoryFilter("Other directories");
    assertEquals("Should have allowed an alternate description.", "Other directories", f.getDescription());
  }
}
