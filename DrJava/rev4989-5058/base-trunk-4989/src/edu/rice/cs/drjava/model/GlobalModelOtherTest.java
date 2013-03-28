

package edu.rice.cs.drjava.model;

import java.io.*;
import javax.swing.text.BadLocationException;
import javax.swing.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.*;
import edu.rice.cs.drjava.model.repl.*;
import edu.rice.cs.util.Log;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.text.EditDocumentException;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.plt.iter.IterUtil;

import static edu.rice.cs.plt.debug.DebugUtil.debug;


public final class GlobalModelOtherTest extends GlobalModelTestCase implements OptionConstants {
  
  
  Log _log = new Log("GlobalModelOtherTest.txt", false);
  
  private static final String FOO_CLASS =
    "package bar;\n" +
    "public class Foo {\n" +
    "  public static void main(String[] args) {\n" +
    "    System.out.println(\"Foo\");\n" +
    "  }\n" +
    "}\n";
  
  
  private File makeCanonical(File f) {
    try { return f.getCanonicalFile(); }
    catch (IOException e) { fail("Can't get a canonical path for file " + f); return null; }
  }
  
  
  public void testUndoEventsOccur()  {
    debug.logStart();
    
    final TestListener listener = new TestListener() { 
      public void undoableEditHappened() { 
        undoableEditCount++; 

      } 
    };
    
    final OpenDefinitionsDocument doc = _model.newFile();

    Utilities.clearEventQueue();
    
    _model.addListener(listener);
        
    doc.addUndoableEditListener(new UndoableEditListener() {
      public void undoableEditHappened(UndoableEditEvent e) { 

        doc.getUndoManager().addEdit(e.getEdit()); 
      }
    });
    
    changeDocumentText("test", doc);
        
    Utilities.clearEventQueue();  
    Utilities.invokeAndWait(new Runnable() {
      public void run() {
        listener.assertUndoableEditCount(1);
        
        _model.removeListener(listener);
      }
    });
    Utilities.clearEventQueue();
    _log.log("testUndoEventsOccur() completed");
    debug.logEnd();
  }
  
  
  
  public void testExitInteractions() throws EditDocumentException, InterruptedException {
    debug.logStart();
    InteractionListener listener = new InteractionListener() {
      

    };
    
    _model.addListener(listener);
    
    listener.logInteractionStart();
    interpretIgnoreResult("System.exit(23);"); 
    listener.waitInteractionDone();
    listener.waitResetDone();
    Utilities.clearEventQueue();
    
    _model.removeListener(listener);
    

    listener.assertInteractionStartCount(1);
    listener.assertInterpreterResettingCount(1);
    listener.assertInterpreterReadyCount(1);
    listener.assertInterpreterExitedCount(1);
    assertEquals("exit status", 23, listener.getLastExitStatus());
    
    _log.log("testExitInteractions() completed");
    debug.logEnd();
  }
  
  
  public void testInteractionsCanSeeCompiledClasses() throws BadLocationException, EditDocumentException,
    IOException, InterruptedException {
    debug.logStart();
    
    
    OpenDefinitionsDocument doc1 = setupDocument(FOO_TEXT);
    File dir1 = makeCanonical(new File(_tempDir, "dir1"));
    dir1.mkdir();
    File file1 = makeCanonical(new File(dir1, "TestFile1.java"));
    doCompile(doc1, file1);
    
    assertEquals("interactions result", "\"DrJavaTestFoo\"", interpret("new DrJavaTestFoo().getClass().getName()"));
    
    
    Vector<File> cp = new Vector<File>();
    cp.add(dir1);
    DrJava.getConfig().setSetting(EXTRA_CLASSPATH, cp);
    
    Utilities.clearEventQueue();
    _model.closeFile(doc1);
    
    
    OpenDefinitionsDocument doc2 = setupDocument(BAZ_TEXT);
    File dir2 = makeCanonical(new File(_tempDir, "dir2"));
    dir2.mkdir();
    File file2 = makeCanonical(new File(dir2, "TestFile1.java"));
    doCompile(doc2, file2);
    
    
    assertEquals("interactions result", "\"DrJavaTestBaz\"", interpret("new DrJavaTestBaz().getClass().getName()"));
    
    
    assertEquals("result of static field", "3", interpret("DrJavaTestBaz.x"));
    
    
    assertEquals("interactions result", "\"DrJavaTestFoo\"", interpret("new DrJavaTestFoo().getClass().getName()"));
    
    _log.log("testInteractionsCanSeeCompletedClasses() completed");
    debug.logEnd();
  }
  
  
  public void testInteractionsVariableWithLowercaseClassName() throws BadLocationException, EditDocumentException,
    IOException, InterruptedException {
    debug.logStart();
    
    
    OpenDefinitionsDocument doc1 = setupDocument("public class DrJavaTestClass {}");
    File file1 = makeCanonical(new File(_tempDir, "DrJavaTestClass.java"));
    doCompile(doc1, file1);
    
    
    assertEquals("interactions result", "", interpret("Object drJavaTestClass = new DrJavaTestClass();"));
    _log.log("testInteractionsVariableWithLowercaseClassName() completed");
    debug.logEnd();
  }
  
  
  public void testInteractionsCanSeeChangedClass() throws BadLocationException, EditDocumentException,
    IOException, InterruptedException {
    debug.logStart();
    
    final String text_before = "class DrJavaTestFoo { public int m() { return ";
    final String text_after = "; } }";
    final int num_iterations = 3;
    File file;
    OpenDefinitionsDocument doc;
    
    for (int i = 0; i < num_iterations; i++) {
      doc = setupDocument(text_before + i + text_after);
      file = tempFile(i);
      doCompile(doc, file);
      
      assertEquals("interactions result, i=" + i, String.valueOf(i), interpret("new DrJavaTestFoo().m()"));
    }
    _log.log("testInteractionsCanSeeChangedClass() completed");
    debug.logEnd();
  }
  
  
  public void testInteractionsDefineAnonymousInnerClass() throws BadLocationException, EditDocumentException,
    IOException, InterruptedException {
    debug.logStart();
    
    final String interface_text = "public interface I { int getValue(); }";
    final File file = createFile("I.java");
    
    OpenDefinitionsDocument doc;
    
    doc = setupDocument(interface_text);
    doCompile(doc, file);
    
    for (int i = 0; i < 3; i++) {
      String s = "new I() { public int getValue() { return " + i + "; } }.getValue()";
      
      assertEquals("interactions result, i=" + i, String.valueOf(i), interpret(s));
    }
    _log.log("testInteractionsDefineAnonymousInnerClass() completed");
    debug.logEnd();
  }
  
  public void testGetSourceRootDefaultPackage() throws BadLocationException, IOException {
    debug.logStart();
    
    
    Iterable<File> roots = _model.getSourceRootSet();
    assertEquals("number of source roots", 0, IterUtil.sizeOf(roots));
    
    
    File baseTempDir = tempDirectory();
    
    
    File subdir = makeCanonical(new File(baseTempDir, "a"));
    subdir = makeCanonical(new File(subdir, "b"));
    subdir = makeCanonical(new File(subdir, "c"));
    subdir.mkdirs();
    
    
    File fooFile = makeCanonical(new File(subdir, "DrJavaTestFoo.java"));
    OpenDefinitionsDocument doc = setupDocument(FOO_TEXT);
    saveFileAs(doc, new FileSelector(fooFile));
    
    
    _model.addListener(new TestListener());
    
    
    roots = _model.getSourceRootSet();
    assertEquals("number of source roots", 1, IterUtil.sizeOf(roots));
    
    assertEquals("source root", subdir, IterUtil.first(roots));
    
    _log.log("testGetSourceRootDefaultPackage() completed");
    debug.logEnd();
  }
  
  public void testGetSourceRootPackageThreeDeepValid() throws BadLocationException, IOException {
    debug.logStart();
    
    
    File baseTempDir = tempDirectory();
    
    
    File subdir = makeCanonical(new File(baseTempDir, "a"));
    subdir = makeCanonical(new File(subdir, "b").getCanonicalFile());
    subdir = makeCanonical(new File(subdir, "c").getCanonicalFile());
    subdir.mkdirs();
    
    
    File fooFile = makeCanonical(new File(subdir, "DrJavaTestFoo.java"));
    OpenDefinitionsDocument doc = setupDocument("package a.b.c;\n" + FOO_TEXT);
    saveFileAs(doc, new FileSelector(fooFile));

    
    
    _model.addListener(new TestListener());
    
    
    Iterable<File> roots = _model.getSourceRootSet();
    assertEquals("number of source roots", 1, IterUtil.sizeOf(roots));
    assertEquals("source root", baseTempDir.getCanonicalFile(), IterUtil.first(roots).getCanonicalFile());
    
    _log.log("testGetSourceRootPackageThreeDeepValid() completed");
    debug.logEnd();
  }
  
  
  public void testGetSourceRootPackageThreeDeepValidRelative() throws BadLocationException, IOException {
    debug.logStart();
    
    
    File baseTempDir = tempDirectory();
    File subdir = makeCanonical(new File(baseTempDir, "a"));
    subdir = makeCanonical(new File(subdir, "b"));
    subdir = makeCanonical(new File(subdir, "c"));
    subdir.mkdirs();
    
    
    
    File relDir = makeCanonical(new File(baseTempDir, "./a/b/../b/c"));
    File fooFile = makeCanonical(new File(relDir, "DrJavaTestFoo.java"));
    OpenDefinitionsDocument doc =
      setupDocument("package a.b.c;\n" + FOO_TEXT);
    saveFileAs(doc, new FileSelector(fooFile));
    
    
    _model.addListener(new TestListener());
    
    
    Iterable<File> roots = _model.getSourceRootSet();
    assertEquals("number of source roots", 1, IterUtil.sizeOf(roots));
    assertEquals("source root", baseTempDir.getCanonicalFile(), IterUtil.first(roots).getCanonicalFile());
    
    _log.log("testGetSourceRootPackageThreeDeepValidRelative() completed");
    debug.logEnd();
  }
  
  public void testGetSourceRootPackageThreeDeepInvalid() throws BadLocationException, IOException {
    debug.logStart();
    
    
    File baseTempDir = tempDirectory();
    
    
    File subdir = makeCanonical(new File(baseTempDir, "a"));
    subdir = makeCanonical(new File(subdir, "b"));
    subdir = makeCanonical(new File(subdir, "d"));
    subdir.mkdirs();
    
    
    File fooFile = makeCanonical(new File(subdir, "DrJavaTestFoo.java"));
    OpenDefinitionsDocument doc = setupDocument("package a.b.c;\n" + FOO_TEXT);
    saveFileAs(doc, new FileSelector(fooFile));
    
    
    _model.addListener(new TestListener());
    
    
    Iterable<File> roots = _model.getSourceRootSet();
    assertEquals("number of source roots", 0, IterUtil.sizeOf(roots));
    
    _log.log("testGetSourceRootPackageThreeDeepInvalid() completed");
    debug.logEnd();
  }
  
  public void testGetSourceRootPackageOneDeepValid() throws BadLocationException, IOException {
    debug.logStart();
    
    
    File baseTempDir = tempDirectory();
    
    
    File subdir = makeCanonical(new File(baseTempDir, "a"));
    subdir.mkdir();
    
    
    File fooFile = makeCanonical(new File(subdir, "DrJavaTestFoo.java"));
    OpenDefinitionsDocument doc = setupDocument("package a;\n" + FOO_TEXT);
    saveFileAs(doc, new FileSelector(fooFile));
    
    
    _model.addListener(new TestListener());
    
    
    Iterable<File> roots = _model.getSourceRootSet();
    assertEquals("number of source roots", 1, IterUtil.sizeOf(roots));
    assertEquals("source root", baseTempDir.getCanonicalFile(), IterUtil.first(roots).getCanonicalFile());
    
    _log.log("testGetSourceRootPackageOneDeepValid() completed");
    debug.logEnd();
  }
  
  
  public void testGetMultipleSourceRootsDefaultPackage() throws BadLocationException, IOException {
    debug.logStart();
    
    
    File baseTempDir = tempDirectory();
    
    
    File subdir1 = makeCanonical(new File(baseTempDir, "a"));
    subdir1.mkdir();
    File subdir2 = makeCanonical(new File(baseTempDir, "b"));
    subdir2.mkdir();
    
    
    File file1 = makeCanonical(new File(subdir1, "DrJavaTestFoo.java"));
    OpenDefinitionsDocument doc1 = setupDocument(FOO_TEXT);
    saveFileAs(doc1, new FileSelector(file1));
    
    
    File file2 = makeCanonical(new File(subdir1, "Bar.java"));
    OpenDefinitionsDocument doc2 = setupDocument(BAR_TEXT);
    saveFileAs(doc2, new FileSelector(file2));
    
    
    File file3 = makeCanonical(new File(subdir2, "Bar.java"));
    OpenDefinitionsDocument doc3 = setupDocument(BAR_TEXT);
    saveFileAs(doc3, new FileSelector(file3));
    
    Utilities.clearEventQueue();
    
    
    _model.addListener(new TestListener());
    
    
    Iterable<File> roots = _model.getSourceRootSet();
    assertEquals("number of source roots", 2, IterUtil.sizeOf(roots));
    Iterator<File> i = roots.iterator();
    File root1 = i.next();
    File root2 = i.next();
    
    
    
    if (!( (root1.equals(subdir1) && root2.equals(subdir2)) || (root1.equals(subdir2) && root2.equals(subdir1)) )) {
      fail("source roots did not match");
    }
    
    _log.log("testGetMultipleSourceRootsDefaultPackage() completed");
    debug.logEnd();
  }
  
  
  public void testInteractionsLiveUpdateClassPath() throws BadLocationException, EditDocumentException,
    IOException, InterruptedException {
    debug.logStart();
    
    OpenDefinitionsDocument doc = setupDocument(FOO_TEXT);
    Utilities.clearEventQueue();
    
    File f = tempFile();
    
    doCompile(doc, f);
    
    
    String tempPath = f.getParent();
    File tempDir = makeCanonical(new File(tempPath));
    tempDir.renameTo(makeCanonical(new File(tempPath + "a")));
    
    String result = interpret("new DrJavaTestFoo().getClass().getName()");
    
    
    
    assertFalse("interactions should have an error, not the correct answer", "\"DrJavaTestFoo\"".equals(result));

    
    
    Vector<File> cp = new Vector<File>();
    cp.add(makeCanonical(new File(tempPath + "a")));
    DrJava.getConfig().setSetting(EXTRA_CLASSPATH, cp);
    
    Utilities.clearEventQueue();
    _model.resetInteractionsClassPath();
    
    result = interpret("new DrJavaTestFoo().getClass().getName()");
    
    
    assertEquals("interactions result", "\"DrJavaTestFoo\"", result);
    
    
    tempDir = makeCanonical(new File(tempPath + "a"));
    tempDir.renameTo(makeCanonical(new File(tempPath)));
    
    _log.log("testInteractionsLiveUpdateClasspath() completed");
    debug.logEnd();
  }
  
  
  public void testSwitchInterpreters() {
    debug.logStart();
    TestListener listener = new TestListener() {
      public void interpreterChanged(boolean inProgress) {
        assertTrue("should not be in progress", !inProgress);
        interpreterChangedCount++;
      }
    };
    _model.addListener(listener);
    
    final DefaultInteractionsModel dim = _model.getInteractionsModel();
    
    
    
    Utilities.invokeAndWait(new Runnable() { 
      public void run() { 
        dim.addInterpreter("testInterpreter");
        dim.setActiveInterpreter("testInterpreter", "myPrompt>"); 
      }
    });
    

    listener.assertInterpreterChangedCount(1);
    _model.removeListener(listener);
    
    _log.log("testSwitchInterpreters() completed");
    debug.logEnd();
  }
  
  public void testRunMainMethod() throws Exception {
    debug.logStart();
    
    File dir = makeCanonical(new File(_tempDir, "bar"));
    dir.mkdir();
    File file = makeCanonical(new File(dir, "Foo.java"));
    final OpenDefinitionsDocument doc = doCompile(FOO_CLASS, file);
    Utilities.invokeAndWait(new Runnable() { 
      public void run() { 
        try { doc.runMain(null); }
        catch(Exception e) { throw new UnexpectedException(e); }
      }
    });
    

    assertInteractionsContains(InteractionsModel.BANNER_PREFIX);
    doc.insertString(doc.getLength(), " ", null);
    
    Utilities.invokeAndWait(new Runnable() { 
      public void run() { 
        try { doc.runMain(null); }
        catch(Exception e) { throw new UnexpectedException(e); }
      }
    });
    

    assertInteractionsContains(DefaultGlobalModel.DOCUMENT_OUT_OF_SYNC_MSG);
    Utilities.clearEventQueue();  
    
    
    _log.log("testRunMainMethod() completed");
    debug.logEnd();
  }
  
  public void testBookmark() throws Exception {
    debug.logStart();
    
    File dir = makeCanonical(new File(_tempDir, "bar"));
    dir.mkdir();
    final File file = makeCanonical(new File(dir, "Foo.java"));
    java.io.FileWriter fw = new java.io.FileWriter(file);
    fw.write(FOO_CLASS);
    fw.close();
    _model.openFile(new edu.rice.cs.util.FileOpenSelector() {
      public File[] getFiles() throws edu.rice.cs.util.OperationCanceledException {
        return new File[] { file };
      }
    });
    assertEquals("Should be 0 bookmarks", 0, _model.getBookmarkManager().getRegions().size());
    Utilities.invokeAndWait(new Runnable() { public void run() { _model.toggleBookmark(3,3); } });
    ArrayList<MovingDocumentRegion> bms = _model.getBookmarkManager().getRegions();
    assertEquals("Should be 1 bookmarks", 1, bms.size());
    assertEquals("Start offset should be 0", 0, bms.get(0).getStartOffset());
    assertEquals("End offset should be "+FOO_CLASS.indexOf('\n'), FOO_CLASS.indexOf('\n'), bms.get(0).getEndOffset());
    Utilities.invokeAndWait(new Runnable() { public void run() { _model.toggleBookmark(3,3); } });
    bms = _model.getBookmarkManager().getRegions();
    assertEquals("Should be 0 bookmarks", 0, bms.size());
    Utilities.invokeAndWait(new Runnable() { public void run() { _model.toggleBookmark(3,6); } });
    bms = _model.getBookmarkManager().getRegions();
    assertEquals("Should be 1 bookmarks", 1, bms.size());
    assertEquals("Start offset should be 3", 3, bms.get(0).getStartOffset());
    assertEquals("End offset should be 6", 6, bms.get(0).getEndOffset());
    Utilities.invokeAndWait(new Runnable() { public void run() { _model.toggleBookmark(12,8); } });
    bms = _model.getBookmarkManager().getRegions();
    
    assertEquals("Should be 2 bookmarks", 2, bms.size());  
    assertEquals("Start offset should be 3", 3, bms.get(0).getStartOffset());
    assertEquals("End offset should be 6", 6, bms.get(0).getEndOffset());
    assertEquals("Start offset should be 8", 8, bms.get(1).getStartOffset());
    assertEquals("End offset should be 12", 12, bms.get(1).getEndOffset());
    Utilities.invokeAndWait(new Runnable() { public void run() { _model.toggleBookmark(5,10); } });
    bms = _model.getBookmarkManager().getRegions();
    assertEquals("Should be 0 bookmarks", 0, bms.size());  






    Utilities.invokeAndWait(new Runnable() { public void run() { _model.toggleBookmark(8,12); } });
    bms = _model.getBookmarkManager().getRegions();
    assertEquals("Should be 1 bookmarks", 1, bms.size());  
    assertEquals("Start offset should be 8", 8, bms.get(0).getStartOffset());
    assertEquals("End offset should be 12", 12, bms.get(0).getEndOffset());


    Utilities.invokeAndWait(new Runnable() { public void run() { _model.toggleBookmark(3,6); } });
    bms = _model.getBookmarkManager().getRegions();
    assertEquals("Should be 2 bookmarks", 2, bms.size());  
    assertEquals("Start offset should be 3", 3, bms.get(0).getStartOffset());
    assertEquals("End offset should be 6", 6, bms.get(0).getEndOffset());
    assertEquals("Start offset should be 8", 8, bms.get(1).getStartOffset());
    assertEquals("End offset should be 12", 12, bms.get(1).getEndOffset());
    Utilities.invokeAndWait(new Runnable() { public void run() { _model.toggleBookmark(10,5); } });
    bms = _model.getBookmarkManager().getRegions();
    assertEquals("Should be 0 bookmarks", 0, bms.size());
    
    debug.logEnd();
  }
  
}
