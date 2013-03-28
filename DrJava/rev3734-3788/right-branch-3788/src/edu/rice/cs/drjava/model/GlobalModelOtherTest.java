

package edu.rice.cs.drjava.model;

import java.io.*;
import javax.swing.text.BadLocationException;
import javax.swing.event.*;
import java.util.Vector;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.*;
import edu.rice.cs.drjava.model.repl.*;
import edu.rice.cs.util.text.EditDocumentException;
import edu.rice.cs.util.swing.Utilities;


public final class GlobalModelOtherTest extends GlobalModelTestCase implements OptionConstants {
  private static final String FOO_CLASS =
    "package bar;\n" +
    "public class Foo {\n" +
    "  public static void main(String[] args) {\n" +
    "    System.out.println(\"Foo\");\n" +
    "  }\n" +
    "}\n";
  
  private boolean _resetDone = false;
  private final Object _resetDoneLock = new Object();
  

  private final Object _interactionDoneLock = new Object();
  


  
  public void testUndoEventsOccur() throws BadLocationException {
    final OpenDefinitionsDocument doc = _model.newFile();

    
    doc.addUndoableEditListener(new UndoableEditListener() {
      public void undoableEditHappened(UndoableEditEvent e) {
        doc.getUndoManager().addEdit(e.getEdit());
      }
    });

    TestListener listener = new TestListener() {
      public void undoableEditHappened() {
        undoableEditCount++;
      }
    };
    _model.addListener(listener);
    changeDocumentText("test", doc);
    
    Utilities.clearEventQueue();
    _model.removeListener(listener);
    listener.assertUndoableEditCount(1);

    

  }

  
  public void testExitInteractions() throws EditDocumentException, InterruptedException{
    TestListener listener = new TestListener() {
      public void interactionStarted() {

        interactionStartCount++;
      }

      public void interpreterExited(int status) {



        interpreterExitedCount++;

        lastExitStatus = status;
      }

      public void interpreterResetting() {



        interpreterResettingCount++;

      }

      public void interpreterReady(File wd) {

        synchronized(_resetDoneLock) {



          interpreterReadyCount++;

          _resetDone = true;
          _resetDoneLock.notify();
        }
      }
      

    };

    _model.addListener(listener);
    _resetDone = false;
    synchronized(_resetDoneLock) {

      interpretIgnoreResult("System.exit(23);");


      while (! _resetDone) { _resetDoneLock.wait(); }
    }
    _model.removeListener(listener);


    listener.assertInteractionStartCount(1);
    listener.assertInterpreterResettingCount(1);
    listener.assertInterpreterReadyCount(1);
    listener.assertInterpreterExitedCount(1);
    assertEquals("exit status", 23, listener.lastExitStatus);


  }

  
  public void testInteractionAbort() throws BadLocationException, EditDocumentException, InterruptedException, 
    IOException {
    
    doCompile(setupDocument(FOO_TEXT), tempFile());
    final String beforeAbort = interpret("DrJavaTestFoo.class.getName()");
    assertEquals("\"DrJavaTestFoo\"", beforeAbort);

    TestListener listener = new TestListener() {
      public void interactionStarted() {
        interactionStartCount++;
      }

      public void interactionEnded() {
        

        interactionEndCount++;
      }

      public void interpreterResetting() {



        interpreterResettingCount++;
      }

      public void interpreterReady(File wd) {
        synchronized(_resetDoneLock) {



          interpreterReadyCount++;
          _resetDone = true;
          _resetDoneLock.notify();
        }
      }

      public void consoleReset() { consoleResetCount++; }
    };

    _model.addListener(listener);
    _resetDone = false;
    synchronized(_resetDoneLock) {
      interpretIgnoreResult("while (true) {}");
      
      Utilities.clearEventQueue();
      listener.assertInteractionStartCount(1);
      _model.resetInteractions(FileOption.NULL_FILE);
      _resetDoneLock.wait();
    }
    listener.assertInterpreterResettingCount(1);
    listener.assertInterpreterReadyCount(1);
    listener.assertInterpreterExitedCount(0);
    listener.assertConsoleResetCount(0);

    
    assertEquals("5", interpret("5"));
    _model.removeListener(listener);

    
    final String afterAbort = interpret("DrJavaTestFoo.class.getName()");
    assertEquals("\"DrJavaTestFoo\"", afterAbort);

  }

  
  public void testResetConsole() throws EditDocumentException, InterruptedException {
    
    TestListener listener = new TestListener() {
      public void interactionStarted() { }
      public void interactionEnded() {
        synchronized(_interactionDoneLock) {
          interactionEndCount++;

          _interactionDoneLock.notify();
        }
      }

      public void consoleReset() { consoleResetCount++; }
    };

    _model.addListener(listener);

    _model.resetConsole();
    assertEquals("Length of console text", 0, _model.getConsoleDocument().getLength());

    listener.assertConsoleResetCount(1);

    synchronized(_interactionDoneLock) {
      interpretIgnoreResult("System.out.print(\"a\");");
      _interactionDoneLock.wait();  
    }

    assertEquals("Length of console text", 1, _model.getConsoleDocument().getLength());

    _model.resetConsole();
    
    Utilities.clearEventQueue();
    assertEquals("Length of console text", 0, _model.getConsoleDocument().getLength());

    listener.assertConsoleResetCount(2);

  }

  
  public void testInteractionsCanSeeCompiledClasses() throws BadLocationException, EditDocumentException,
    IOException, InterruptedException {
    
    OpenDefinitionsDocument doc1 = setupDocument(FOO_TEXT);
    File dir1 = new File(_tempDir, "dir1");
    dir1.mkdir();
    File file1 = new File(dir1, "TestFile1.java");
    doCompile(doc1, file1);

    assertEquals("interactions result", "\"DrJavaTestFoo\"", interpret("new DrJavaTestFoo().getClass().getName()"));

    
    Vector<File> cp = new Vector<File>();
    cp.add(dir1);
    DrJava.getConfig().setSetting(EXTRA_CLASSPATH, cp);
    
    Utilities.clearEventQueue();
    _model.closeFile(doc1);

    
    OpenDefinitionsDocument doc2 = setupDocument(BAZ_TEXT);
    File dir2 = new File(_tempDir, "dir2");
    dir2.mkdir();
    File file2 = new File(dir2, "TestFile1.java");
    doCompile(doc2, file2);

    
    assertEquals("interactions result", "\"DrJavaTestBaz\"", interpret("new DrJavaTestBaz().getClass().getName()"));

    
    assertEquals("result of static field", "3", interpret("DrJavaTestBaz.x"));

    
    assertEquals("interactions result", "\"DrJavaTestFoo\"", interpret("new DrJavaTestFoo().getClass().getName()"));
    

  }

  
  public void testInteractionsVariableWithLowercaseClassName() throws BadLocationException, EditDocumentException,
    IOException, InterruptedException {
    
    OpenDefinitionsDocument doc1 = setupDocument("public class DrJavaTestClass {}");
    File file1 = new File(_tempDir, "DrJavaTestClass.java");
    doCompile(doc1, file1);

    
    assertEquals("interactions result", "", interpret("drJavaTestClass = new DrJavaTestClass();"));

  }

  
  public void testInteractionsCanSeeChangedClass() throws BadLocationException, EditDocumentException,
    IOException, InterruptedException {
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

  }

  
  public void testInteractionsDefineAnonymousInnerClass() throws BadLocationException, EditDocumentException,
    IOException, InterruptedException {
    final String interface_text = "public interface I { int getValue(); }";
    final File file = createFile("I.java");

    OpenDefinitionsDocument doc;

    doc = setupDocument(interface_text);
    doCompile(doc, file);

    for (int i = 0; i < 3; i++) {
      String s = "new I() { public int getValue() { return " + i + "; } }.getValue()";

      assertEquals("interactions result, i=" + i, String.valueOf(i), interpret(s));
    }

  }

  public void testGetSourceRootDefaultPackage() throws BadLocationException, IOException {

    
    File[] roots = _model.getSourceRootSet();
    assertEquals("number of source roots", 0, roots.length);

     
    File baseTempDir = tempDirectory();

    
    File subdir = new File(baseTempDir, "a");
    subdir = new File(subdir, "b");
    subdir = new File(subdir, "c");
    subdir.mkdirs();

    
    File fooFile = new File(subdir, "DrJavaTestFoo.java");
    OpenDefinitionsDocument doc = setupDocument(FOO_TEXT);
    doc.saveFileAs(new FileSelector(fooFile));

    
    _model.addListener(new TestListener());

    
    roots = _model.getSourceRootSet();
    assertEquals("number of source roots", 1, roots.length);
    
    assertEquals("source root", subdir, roots[0]);
    

  }

  public void testGetSourceRootPackageThreeDeepValid() throws BadLocationException, IOException {
    
    File baseTempDir = tempDirectory();

    
    File subdir = new File(baseTempDir, "a");
    subdir = new File(subdir, "b");
    subdir = new File(subdir, "c");
    subdir.mkdirs();

    
    File fooFile = new File(subdir, "DrJavaTestFoo.java");
    OpenDefinitionsDocument doc =
      setupDocument("package a.b.c;\n" + FOO_TEXT);
    doc.saveFileAs(new FileSelector(fooFile));

    
    _model.addListener(new TestListener());

    
    File[] roots = _model.getSourceRootSet();
    assertEquals("number of source roots", 1, roots.length);
    assertEquals("source root", baseTempDir.getCanonicalFile(), roots[0].getCanonicalFile());
    

  }

  
  public void testGetSourceRootPackageThreeDeepValidRelative() throws BadLocationException, IOException {
    
    File baseTempDir = tempDirectory();
    File subdir = new File(baseTempDir, "a");
    subdir = new File(subdir, "b");
    subdir = new File(subdir, "c");
    subdir.mkdirs();

    
    
    File relDir = new File(baseTempDir, "./a/b/../b/c");
    File fooFile = new File(relDir, "DrJavaTestFoo.java");
    OpenDefinitionsDocument doc =
      setupDocument("package a.b.c;\n" + FOO_TEXT);
    doc.saveFileAs(new FileSelector(fooFile));

    
    _model.addListener(new TestListener());

    
    File[] roots = _model.getSourceRootSet();
    assertEquals("number of source roots", 1, roots.length);
    assertEquals("source root", baseTempDir.getCanonicalFile(), roots[0].getCanonicalFile());
    

  }

  public void testGetSourceRootPackageThreeDeepInvalid() throws BadLocationException, IOException {
    
    File baseTempDir = tempDirectory();

    








    
    File subdir = new File(baseTempDir, "a");
    subdir = new File(subdir, "b");
    subdir = new File(subdir, "d");
    subdir.mkdirs();

    
    File fooFile = new File(subdir, "DrJavaTestFoo.java");
    OpenDefinitionsDocument doc =
      setupDocument("package a.b.c;\n" + FOO_TEXT);
    doc.saveFileAs(new FileSelector(fooFile));

    
    _model.addListener(new TestListener());

    
    File[] roots = _model.getSourceRootSet();
    assertEquals("number of source roots", 0, roots.length);
    

  }

  public void testGetSourceRootPackageOneDeepValid() throws BadLocationException, IOException {
    
    File baseTempDir = tempDirectory();

    
    File subdir = new File(baseTempDir, "a");
    subdir.mkdir();

    
    File fooFile = new File(subdir, "DrJavaTestFoo.java");
    OpenDefinitionsDocument doc = setupDocument("package a;\n" + FOO_TEXT);
    doc.saveFileAs(new FileSelector(fooFile));

    
    _model.addListener(new TestListener());

    
    File[] roots = _model.getSourceRootSet();
    assertEquals("number of source roots", 1, roots.length);
    assertEquals("source root", baseTempDir.getCanonicalFile(), roots[0].getCanonicalFile());
    

  }


  public void testGetMultipleSourceRootsDefaultPackage() throws BadLocationException, IOException {
    
    File baseTempDir = tempDirectory();

    
    File subdir1 = new File(baseTempDir, "a");
    subdir1.mkdir();
    File subdir2 = new File(baseTempDir, "b");
    subdir2.mkdir();

    
    File file1 = new File(subdir1, "DrJavaTestFoo.java");
    OpenDefinitionsDocument doc1 = setupDocument(FOO_TEXT);
    doc1.saveFileAs(new FileSelector(file1));

    
    File file2 = new File(subdir1, "Bar.java");
    OpenDefinitionsDocument doc2 = setupDocument(BAR_TEXT);
    doc2.saveFileAs(new FileSelector(file2));

    
    File file3 = new File(subdir2, "Bar.java");
    OpenDefinitionsDocument doc3 = setupDocument(BAR_TEXT);
    doc3.saveFileAs(new FileSelector(file3));
    
    Utilities.clearEventQueue();

    
    _model.addListener(new TestListener());

    
    File[] roots = _model.getSourceRootSet();
    assertEquals("number of source roots", 2, roots.length);
    File root1 = roots[0];
    File root2 = roots[1];

    
    
    if (!( (root1.equals(subdir1) && root2.equals(subdir2)) || (root1.equals(subdir2) && root2.equals(subdir1)) )) {
      fail("source roots did not match");
    }
    

  }

  
  public void testInteractionsLiveUpdateClassPath() throws BadLocationException, EditDocumentException,
    IOException, InterruptedException {

    OpenDefinitionsDocument doc = setupDocument(FOO_TEXT);
    Utilities.clearEventQueue();
        
    File f = tempFile();

    doCompile(doc, f);

    
    String tempPath = f.getParent();
    File tempDir = new File(tempPath);
    tempDir.renameTo(new File(tempPath + "a"));

    String result = interpret("new DrJavaTestFoo().getClass().getName()");

    
    
    assertFalse("interactions should have an error, not the correct answer", "\"DrJavaTestFoo\"".equals(result));


    
    Vector<File> cp = new Vector<File>();
    cp.add(new File(tempPath + "a"));
    DrJava.getConfig().setSetting(EXTRA_CLASSPATH, cp);
    
    Utilities.clearEventQueue();
    _model.resetInteractionsClassPath();


    result = interpret("new DrJavaTestFoo().getClass().getName()");

    
    assertEquals("interactions result", "\"DrJavaTestFoo\"", result);


    
    tempDir = new File(tempPath + "a");
    boolean renamed = tempDir.renameTo(new File(tempPath));

    

  }

  
  public void testSwitchInterpreters() {
    TestListener listener = new TestListener() {
      public void interpreterChanged(boolean inProgress) {
        assertTrue("should not be in progress", !inProgress);
        interpreterChangedCount++;
      }
    };
    _model.addListener(listener);

    DefaultInteractionsModel dim = _model.getInteractionsModel();

    
    dim.addJavaInterpreter("testInterpreter");
    dim.setActiveInterpreter("testInterpreter", "myPrompt>");
    
    Utilities.clearEventQueue();
    listener.assertInterpreterChangedCount(1);
    _model.removeListener(listener);
    

  }

  public void testRunMainMethod() throws Exception {
    File dir = new File(_tempDir, "bar");
    dir.mkdir();
    File file = new File(dir, "Foo.java");
    OpenDefinitionsDocument doc = doCompile(FOO_CLASS, file);
    doc.runMain();
    
    Utilities.clearEventQueue();
    assertInteractionsContains(InteractionsModel.BANNER_PREFIX);
    doc.insertString(doc.getLength(), " ", null);
    doc.runMain();
    
    Utilities.clearEventQueue();
    assertInteractionsContains(DefaultGlobalModel.DOCUMENT_OUT_OF_SYNC_MSG);
    Utilities.clearEventQueue();  
    
    

  }
}
