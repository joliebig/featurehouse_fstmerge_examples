

package edu.rice.cs.drjava.model.repl;

import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.util.FileOpenSelector;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.util.OperationCanceledException;
import edu.rice.cs.drjava.model.repl.newjvm.MainJVM;

import edu.rice.cs.drjava.model.FileSaveSelector;
import edu.rice.cs.util.text.ConsoleDocument;
import edu.rice.cs.util.text.EditDocumentException;

import java.io.File;
import java.io.IOException;
import java.net.URL;


public final class InteractionsModelTest extends DrJavaTestCase {
  protected InteractionsDJDocument _adapter;
  protected InteractionsModel _model;
  
  public InteractionsModelTest(String name) {
    super(name);
    _adapter = new InteractionsDJDocument();
    _model = new TestInteractionsModel(_adapter);
  }

  public void tearDown() throws Exception {
    _model = null;
    _adapter = null;
    super.tearDown();
  }

  
  protected void _assertProcessedContents(String typed, String expected) throws EditDocumentException {
    assertTrue(_model instanceof TestInteractionsModel);
    TestInteractionsModel model = (TestInteractionsModel)_model;
    InteractionsDocument doc = model.getDocument();
    doc.reset("");
    doc.insertText(doc.getLength(), typed, InteractionsDocument.DEFAULT_STYLE);
    model.interpretCurrentInteraction();
    assertEquals("processed output should match expected", expected, model.toEval);
  }

  
  protected void _assertMainTransformation(String typed, String expected) {
    assertEquals("main transformation should match expected",
                 expected, TestInteractionsModel._testClassCall(typed));
  }


  
  public void testInterpretCurrentInteraction() throws EditDocumentException {
    assertTrue(_model instanceof TestInteractionsModel);
    TestInteractionsModel model = (TestInteractionsModel)_model;
    String code = "int x = 3;";
    InteractionsDocument doc = model.getDocument();
    model.interpretCurrentInteraction();
    
    model.replReturnedVoid();
    assertEquals("string being interpreted", "", model.toEval);

    
    doc.insertText(doc.getLength(), code, InteractionsDocument.DEFAULT_STYLE);
    model.interpretCurrentInteraction();
    
    model.replReturnedVoid();
    assertEquals("string being interpreted", code, model.toEval);
  }

  public void testInterpretCurrentInteractionWithIncompleteInput() throws EditDocumentException {
    _model = new IncompleteInputInteractionsModel(_adapter);   
    assertReplThrewContinuationException("void m() {");
    _model = new IncompleteInputInteractionsModel(_adapter);
    assertReplThrewContinuationException("void m() {;");
    _model = new IncompleteInputInteractionsModel(_adapter);
    assertReplThrewContinuationException("1+");
    _model = new IncompleteInputInteractionsModel(_adapter);
    assertReplThrewContinuationException("(1+2");
    _model = new IncompleteInputInteractionsModel(_adapter);
    assertReplThrewSyntaxException("(1+2;");
    _model = new IncompleteInputInteractionsModel(_adapter);
    assertReplThrewContinuationException("for (;;");
  }

  protected void assertReplThrewContinuationException(String code) throws EditDocumentException {
    assertTrue(_model instanceof IncompleteInputInteractionsModel);
    IncompleteInputInteractionsModel model = (IncompleteInputInteractionsModel)_model;
    InteractionsDocument doc = model.getDocument();
    doc.insertText(doc.getLength(), code, InteractionsDocument.DEFAULT_STYLE);
    model.interpretCurrentInteraction();
    try { Thread.sleep(5000); } catch(InterruptedException ie) { }; 
    assertTrue("Code '"+code+"' should generate a continuation exception but not a syntax exception",
               (model.isContinuationException() == true) && (model.isSyntaxException() == false));
  }

  protected void assertReplThrewSyntaxException(String code) throws EditDocumentException {
    assertTrue(_model instanceof IncompleteInputInteractionsModel);
    IncompleteInputInteractionsModel model = (IncompleteInputInteractionsModel)_model;
    InteractionsDocument doc = model.getDocument();
    doc.insertText(doc.getLength(), code, InteractionsDocument.DEFAULT_STYLE);
    model.interpretCurrentInteraction();
    try { Thread.sleep(5000); } catch(InterruptedException ie) { }; 
    assertTrue("Code '"+code+"' should generate a syntax exception but not a continuation exception",
               (model.isSyntaxException() == true) && (model.isContinuationException() == false));
  }


  
  public void testInterpretJavaArguments() {
    
    
    _assertMainTransformation("java Foo a b c", "Foo.main(new String[]{\"a\",\"b\",\"c\"});");
    
    
    _assertMainTransformation("java Foo \"a b c\"", "Foo.main(new String[]{\"a b c\"});");
    
    
    
    
    _assertMainTransformation("java Foo \"a b\"c d", "Foo.main(new String[]{\"a bc\",\"d\"});");

    
    
    _assertMainTransformation("java Foo c:\\\\file.txt", "Foo.main(new String[]{\"c:\\\\file.txt\"});");

    
    
    _assertMainTransformation("java Foo /home/user/file", "Foo.main(new String[]{\"/home/user/file\"});");
  }

  
  public void testInterpretJavaEscapedArgs() {
    
    
    _assertMainTransformation("java Foo \\j", "Foo.main(new String[]{\"j\"});");
    
    
    _assertMainTransformation("java Foo \\\"", "Foo.main(new String[]{\"\\\"\"});");
    
    
    _assertMainTransformation("java Foo \\\\", "Foo.main(new String[]{\"\\\\\"});");
    
    
    _assertMainTransformation("java Foo a\\ b", "Foo.main(new String[]{\"a b\"});");
  }

  
  public void testInterpretJavaQuotedEscapedArgs() {
    
    
    _assertMainTransformation("java Foo \"a \\\" b\"", "Foo.main(new String[]{\"a \\\" b\"});");
    
    
    _assertMainTransformation("java Foo \"\\'\"", "Foo.main(new String[]{\"\\\\'\"});");
    
    
    _assertMainTransformation("java Foo \"\\\\\"", "Foo.main(new String[]{\"\\\\\"});");
    
    
    _assertMainTransformation("java Foo \"\\\" \\d\"", "Foo.main(new String[]{\"\\\" \\\\d\"});");
    
    

  }

  
  public void testInterpretJavaSingleQuotedArgs() {
    
    
    _assertMainTransformation("java Foo 'asdf'", "Foo.main(new String[]{\"asdf\"});");
    
    
    _assertMainTransformation("java Foo 'a b c'", "Foo.main(new String[]{\"a b c\"});");

    
    _assertMainTransformation("java Foo 'a b'c", "Foo.main(new String[]{\"a bc\"});");
  }

  
  


  
  public void testDebugPort() throws IOException {
    int port = _model.getDebugPort();
    assertTrue("generated debug port", port != -1);

    
    _model.setWaitingForFirstInterpreter(false);
    _model.interpreterResetting();
    int newPort = _model.getDebugPort();
    assertTrue("debug port should change", newPort != port);

    
    _model.setDebugPort(5);
    assertEquals("manually set debug port", 5, _model.getDebugPort());

    
    _model.setDebugPort(-1);
    assertEquals("debug port should be -1", -1, _model.getDebugPort());
  }

  
  public void testScriptLoading() throws IOException, OperationCanceledException {
    assertTrue(_model instanceof TestInteractionsModel);
    TestInteractionsModel model = (TestInteractionsModel)_model;
    
    String line1 = "System.out.println(\"hi\")";
    String line2 = "System.out.println(\"bye\")";

    final File temp = File.createTempFile("drjava-test", ".hist").getCanonicalFile();
    temp.deleteOnExit();
    History history = new History(5);
    history.add(line1);
    history.add(line2);
    history.writeToFile(new FileSaveSelector() {
      public File getFile() { return temp; }
      public boolean warnFileOpen(File f) { return true; }
      public boolean verifyOverwrite() { return true; }
      public boolean shouldSaveAfterFileMoved(OpenDefinitionsDocument doc, File oldFile) { return true; }
    });

    
    InteractionsScriptModel ism = model.loadHistoryAsScript(new FileOpenSelector() {
      public File[] getFiles() {
        return new File[] {temp};
      }
    });
    InteractionsDocument doc = model.getDocument();

    
    assertTrue("Should have no previous", !ism.hasPrevInteraction());
    try {
      ism.prevInteraction();
      fail("Should not have been able to get previous interaction!");
    }
    catch (IllegalStateException ise) {
      
    }

    
    assertTrue("Should have next", ism.hasNextInteraction());
    ism.nextInteraction();
    assertEquals("Should have put the first line into the document.", line1, doc.getCurrentInteraction());

    
    assertTrue("Should have no previous", !ism.hasPrevInteraction());
    try {
      ism.prevInteraction();
      fail("Should not have been able to get previous interaction!");
    }
    catch (IllegalStateException ise) {
      
    }

    
    assertTrue("Should have next", ism.hasNextInteraction());
    ism.nextInteraction();
    assertEquals("Should have put the second line into the document.", line2, doc.getCurrentInteraction());

    
    assertTrue("Should have previous", ism.hasPrevInteraction());
    ism.prevInteraction();
    assertEquals("Should have put the first line into the document.", line1, doc.getCurrentInteraction());

    
    ism.nextInteraction();
    ism.executeInteraction();
    assertEquals("Should have \"executed\" the second interaction.", line2, model.toEval);
    
    model.replReturnedVoid();

    
    assertTrue("Should have no next", !ism.hasNextInteraction());
    try {
      ism.nextInteraction();
      fail("Should not have been able to get next interaction!");
    }
    catch (IllegalStateException ise) {
      
    }

    
    assertTrue("Should have previous", ism.hasPrevInteraction());
    ism.prevInteraction();
    assertEquals("Should have put the second line into the document.", line2, doc.getCurrentInteraction());

    
    assertTrue("Should have previous", ism.hasPrevInteraction());
    ism.prevInteraction();
    assertEquals("Should have put the first line into the document.", line1, doc.getCurrentInteraction());

    
    assertTrue("Should have no previous", !ism.hasPrevInteraction());

    
    ism.executeInteraction();
    assertEquals("Should have \"executed\" the first interaction.", line1, model.toEval);
    
    model.replReturnedVoid();

    
    assertTrue("Should have previous", ism.hasPrevInteraction());
    ism.prevInteraction();
    assertEquals("Should have put the first line into the document.", line1, doc.getCurrentInteraction());

    
    assertTrue("Should have no previous", !ism.hasPrevInteraction());
    try {
      ism.prevInteraction();
      fail("Should not have been able to get previous interaction!");
    }
    catch (IllegalStateException ise) {
      
    }
  }

  
  public void testSetChangeInputListener() {
    InputListener listener1 = new InputListener() {
      public String getConsoleInput() { return "input1"; }
    };

    InputListener listener2 = new InputListener() {
      public String getConsoleInput() { return "input2"; }
    };

    try {
      _model.getConsoleInput();
      fail("Should not have allowed getting input before a listener is installed!");
    }
    catch (IllegalStateException ise) {
      assertEquals("Should have thrown the correct exception.",
                   "No input listener installed!", ise.getMessage());
    }

    _model.setInputListener(listener1);
    assertEquals("First input listener should return correct input", "input1", _model.getConsoleInput());
    _model.changeInputListener(listener1, listener2);
    assertEquals("Second input listener should return correct input", "input2", _model.getConsoleInput());
  }

  
  public void testInteractionsHistoryStoredCorrectly() throws EditDocumentException {
    final Object _lock = new Object();
    String code = "public class A {\n";

    InteractionsDocument doc = _model.getDocument();

    
    doc.insertText(doc.getLength(), code, InteractionsDocument.DEFAULT_STYLE);

    _model.interpretCurrentInteraction();
    
    _model.replReturnedSyntaxError("Encountered Unexpected \"<EOF>\"", "public class A {\n", -1, -1, -1, -1);

    assertEquals("Current interaction should still be there - should not have interpreted", "public class A {\n" + System.getProperty("line.separator"),
                 doc.getCurrentInteraction());
    History h = doc.getHistory();
    assertEquals("History should be empty", 0, h.size());

    code = "}\n";

    doc.insertText(doc.getLength(), code, InteractionsDocument.DEFAULT_STYLE);

    synchronized(_lock) {
      _model.interpretCurrentInteraction();
      _model.replReturnedVoid();
    }

    synchronized(_lock) {
      assertEquals("Current interaction should not be there - should have interpreted", "", doc.getCurrentInteraction());
      assertEquals("History should contain one interaction", 1, h.size());
    }
  }

  
  public static class TestInteractionsModel extends InteractionsModel {
    String toEval = null;
    String addedClass = null;

    
    public TestInteractionsModel(InteractionsDJDocument adapter) {
      
      super(adapter, new File(System.getProperty("user.dir")), 1000, 25);
    }

    protected void _interpret(String toEval) { this.toEval = toEval; }
    
    public String getVariableToString(String var) {
      fail("cannot getVariableToString in a test");
      return null;
    }
    public String getVariableClassName(String var) {
      fail("cannot getVariableClassName in a test");
      return null;
    }
    
    public void addProjectClassPath(URL path) { fail("cannot add to classpath in a test"); }
    public void addBuildDirectoryClassPath(URL path) { fail("cannot add to classpath in a test"); }
    public void addProjectFilesClassPath(URL path) { fail("cannot add to classpath in a test"); }
    public void addExternalFilesClassPath(URL path) { fail("cannot add to classpath in a test"); }
    public void addExtraClassPath(URL path) { fail("cannot add to classpath in a test"); }
    protected void _resetInterpreter(File wd) { fail("cannot reset interpreter in a test"); }
    
    protected void _notifyInteractionStarted() { }
    protected void _notifyInteractionEnded() { }
    protected void _notifySyntaxErrorOccurred(int offset, int length) { }
    protected void _notifyInterpreterExited(int status) { }
    protected void _notifyInterpreterResetting() { }
    protected void _notifyInterpreterResetFailed(Throwable t) { }
    public void _notifyInterpreterReady(File wd) { }
    protected void _interpreterResetFailed(Throwable t) { }
    protected void _notifyInteractionIncomplete() { }
    protected void _notifySlaveJVMUsed() { }
    public ConsoleDocument getConsoleDocument() { return null; }
  }

  public static class IncompleteInputInteractionsModel extends RMIInteractionsModel {
    boolean continuationException;
    boolean syntaxException;

    
    public IncompleteInputInteractionsModel(InteractionsDJDocument adapter) {
      
      super(new MainJVM(null), adapter, new File(System.getProperty("user.dir")), 1000, 25);
      _jvm.setInteractionsModel(this); 
      _jvm.startInterpreterJVM();
      continuationException = false;
      syntaxException = false;
    }
    
    protected void _notifyInteractionStarted() { }
    protected void _notifyInteractionEnded() { }
    protected void _notifySyntaxErrorOccurred(int offset, int length) { }
    protected void _notifyInterpreterExited(int status) { }
    protected void _notifyInterpreterResetting() { }
    protected void _notifyInterpreterResetFailed(Throwable t) { }
    public void _notifyInterpreterReady(File wd) { }
    protected void _interpreterResetFailed(Throwable t) { }
    protected void _notifyInteractionIncomplete() { }
    protected void _notifyInterpreterChanged(boolean inProgress) { }
    protected void _notifySlaveJVMUsed() { }
    
    public ConsoleDocument getConsoleDocument() { return null; }

    public void replThrewException(String exceptionClass, String message, String stackTrace, String shortMessage) {
      if (shortMessage != null) {
        if (shortMessage.endsWith("<EOF>\"")) {
          continuationException = true;
          syntaxException = false;
          return;
        }
      }
      syntaxException = true;
      continuationException = false;
    }

    public void replReturnedSyntaxError(String errorMessage, String interaction, int startRow, int startCol, int endRow,
                                        int endCol) {
      if (errorMessage!=null) {
        if (errorMessage.endsWith("<EOF>\"")) {
          continuationException = true;
          syntaxException = false;
          return;
        }
      }
      syntaxException = true;
      continuationException = false;
    }

    public boolean isContinuationException() {
      return continuationException;
    }

    public boolean isSyntaxException() {
      return syntaxException;
    }
  }
}
