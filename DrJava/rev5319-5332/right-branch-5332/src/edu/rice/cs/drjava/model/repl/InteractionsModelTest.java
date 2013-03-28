

package edu.rice.cs.drjava.model.repl;

import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.drjava.model.repl.newjvm.MainJVM;
import edu.rice.cs.drjava.model.FileSaveSelector;

import edu.rice.cs.util.FileOpenSelector;
import edu.rice.cs.util.Log;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.util.text.ConsoleDocument;
import edu.rice.cs.util.text.EditDocumentException;
import edu.rice.cs.plt.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;

import java.rmi.RemoteException;


public final class InteractionsModelTest extends DrJavaTestCase {
  
  private static Log _log = new Log("InteractionsModelTest.txt", false);
  protected InteractionsDJDocument _adapter;
  protected InteractionsModel _model;
  
  public InteractionsModelTest(String name) {
    super(name);
    _adapter = new InteractionsDJDocument();
    _model = new TestInteractionsModel(_adapter);
  }
  
  public void tearDown() throws Exception {
    
    if (_model instanceof IncompleteInputInteractionsModel) ((IncompleteInputInteractionsModel) _model).dispose();
    _model = null;
    _adapter = null;
    super.tearDown();
  }
  
  
  protected void _assertProcessedContents(final String typed, final String expected) throws Exception {
    assertTrue(_model instanceof TestInteractionsModel);
    final TestInteractionsModel model = (TestInteractionsModel)_model;
    final InteractionsDocument doc = model.getDocument();
    Utilities.invokeAndWait(new Runnable() { 
      public void run() { 
        doc.reset("This is a test"); 
        doc.append(typed, InteractionsDocument.DEFAULT_STYLE); 
      }
    });
    Utilities.clearEventQueue();
    model._logInteractionStart();
    model.interpretCurrentInteraction();
    model._waitInteractionDone();
    Utilities.clearEventQueue();
    assertEquals("processed output should match expected", expected, model.toEval);
  }
  
  
  protected void _assertJavaTransformationTail(final String typed, final String expected) {
    assertTrue("main transformation should match expected",
               edu.rice.cs.drjava.model.compiler.JavacCompiler.transformJavaCommand(typed).endsWith(expected));
  }

  
  protected void _assertAppletTransformation(final String typed, final String expected) {
    assertEquals("applet transformation should match expected",
                 expected, 
                 edu.rice.cs.drjava.model.compiler.JavacCompiler.transformAppletCommand(typed));
  }
  
  
  public void testInterpretCurrentInteraction() throws Exception {
    _log.log("testInterpretCurrentInteraction started");
    assertTrue(_model instanceof TestInteractionsModel);
    final TestInteractionsModel model = (TestInteractionsModel) _model;
    final InteractionsDocument doc = model.getDocument();
    model._logInteractionStart();
    model.interpretCurrentInteraction();
    model._waitInteractionDone();
    Utilities.clearEventQueue();
    assertEquals("string being interpreted", "", model.toEval);
    
    final String code = "int x = 3;";
    
    Utilities.invokeAndWait(new Runnable() { 
      public void run() { 
        doc.append(code, InteractionsDocument.DEFAULT_STYLE); 
      } 
    });
    Utilities.clearEventQueue();

    assertTrue("Code appended correctly to interactions document", doc.getText().endsWith(code));

    Utilities.clearEventQueue();
    assertTrue("Current interaction text is correct", doc.getCurrentInteraction().equals(code));
    
    model._logInteractionStart();
    model.interpretCurrentInteraction(); 
    model._waitInteractionDone();

    Utilities.clearEventQueue();
    assertEquals("string being interpreted", code, model.toEval);
    _log.log("testInterpretCurrentInteraction ended");
  }
  
  
  public void testInterpretCurrentInteractionWithIncompleteInput_NOJOIN() throws EditDocumentException, InterruptedException,
    RemoteException {
    _log.log("testInterpretCurrentInteractionWithIncompleteInput started");
    _model = new IncompleteInputInteractionsModel(_adapter);   
    assertReplThrewContinuationException("void m() {");
    assertReplThrewContinuationException("void m() {;");
    assertReplThrewContinuationException("1 + ");
    assertReplThrewContinuationException("(1+2");
    assertReplThrewSyntaxException("(1+2;");
    assertReplThrewContinuationException("for (;;");
    _log.log("testInterpretCurrentInteractionWithIncompleteInput ended");
  }
  
  
  protected void assertReplThrewContinuationException(final String code) throws EditDocumentException, InterruptedException {
    assertTrue(_model instanceof IncompleteInputInteractionsModel);
    final IncompleteInputInteractionsModel model = (IncompleteInputInteractionsModel) _model;
    final InteractionsDocument doc = model.getDocument();
    Utilities.invokeAndWait(new Runnable() { 
      public void run() {
        doc.reset("This is a test");
        doc.append(code, InteractionsDocument.DEFAULT_STYLE);
      }
    });
    Utilities.clearEventQueue();
    model._logInteractionStart();
    model.interpretCurrentInteraction();
    Utilities.clearEventQueue();
    _log.log("Waiting for InteractionDone()");
    model._waitInteractionDone();

    Utilities.clearEventQueue();
    assertTrue("Code '" + code + "' should generate a continuation exception but not a syntax exception",
               (model.isContinuationException() == true) && (model.isSyntaxException() == false));
  }
  
  
  protected void assertReplThrewSyntaxException(final String code) throws EditDocumentException, InterruptedException {
    assertTrue(_model instanceof IncompleteInputInteractionsModel);
    final IncompleteInputInteractionsModel model = (IncompleteInputInteractionsModel)_model;
    final InteractionsDocument doc = model.getDocument();
    Utilities.invokeAndWait(new Runnable() { 
      public void run() {
        doc.reset("This is a test");
        doc.append(code, InteractionsDocument.DEFAULT_STYLE);
      }
    });
    Utilities.clearEventQueue();

    model._logInteractionStart();
    model.interpretCurrentInteraction();
    model._waitInteractionDone();

    Utilities.clearEventQueue();
    assertTrue("Code '" + code +  "' should generate a syntax exception but not a continuation exception",
               (model.isSyntaxException() == true) && (model.isContinuationException() == false));
  }
    
  
  public void testInterpretJavaArguments() {
    _log.log("testInterpretJavaArguments started");
    
    
    _assertJavaTransformationTail("java Foo a b c", "Foo.main(new String[]{\"a\",\"b\",\"c\"});");
    
    
    _assertJavaTransformationTail("java Foo \"a b c\"", "Foo.main(new String[]{\"a b c\"});");
    
    
    
    
    _assertJavaTransformationTail("java Foo \"a b\"c d", "Foo.main(new String[]{\"a bc\",\"d\"});");
    
    
    
    _assertJavaTransformationTail("java Foo c:\\\\file.txt", "Foo.main(new String[]{\"c:\\\\file.txt\"});");
    
    
    
    _assertJavaTransformationTail("java Foo /home/user/file", "Foo.main(new String[]{\"/home/user/file\"});");
    _log.log("testInterpretJavaArguments ended");
  }
  
  
  public void testInterpretJavaEscapedArgs() {
    _log.log("testInterpretJavaEscapedArgs started");
    
    
    _assertJavaTransformationTail("java Foo \\j", "Foo.main(new String[]{\"j\"});");
    
    
    _assertJavaTransformationTail("java Foo \\\"", "Foo.main(new String[]{\"\\\"\"});");
    
    
    _assertJavaTransformationTail("java Foo \\\\", "Foo.main(new String[]{\"\\\\\"});");
    
    
    _assertJavaTransformationTail("java Foo a\\ b", "Foo.main(new String[]{\"a b\"});");
    _log.log("testInterpretJavaEscapedArgs ended");
  }
  
  
  public void testInterpretJavaQuotedEscapedArgs() {
    _log.log("testInterpretJavaQuotedEscapedArgs started");
    
    
    _assertJavaTransformationTail("java Foo \"a \\\" b\"", "Foo.main(new String[]{\"a \\\" b\"});");
    
    
    _assertJavaTransformationTail("java Foo \"\\'\"", "Foo.main(new String[]{\"\\\\'\"});");
    
    
    _assertJavaTransformationTail("java Foo \"\\\\\"", "Foo.main(new String[]{\"\\\\\"});");
    
    
    _assertJavaTransformationTail("java Foo \"\\\" \\d\"", "Foo.main(new String[]{\"\\\" \\\\d\"});");
    
    
    
    _log.log("testInterpretJavaQuotedEscapedArgs started");
  }
  
  
  public void testInterpretJavaSingleQuotedArgs() {
    _log.log("testInterpretJavaSingleQuotedArgs started");
    
    _assertJavaTransformationTail("java Foo 'asdf'", "Foo.main(new String[]{\"asdf\"});");
    
    
    _assertJavaTransformationTail("java Foo 'a b c'", "Foo.main(new String[]{\"a b c\"});");
    
    
    _assertJavaTransformationTail("java Foo 'a b'c", "Foo.main(new String[]{\"a bc\"});");
     _log.log("testInterpretJavaSingleQuotedArgs ended");
  }
  
  
  public void testInterpretAppletArguments() {
    _log.log("testInterpretAppletArguments started");
    
    
    _assertAppletTransformation("applet Foo a b c", "edu.rice.cs.plt.swing.SwingUtil.showApplet(new Foo(\"a\",\"b\",\"c\"), 400, 300);");
    
    
    _assertAppletTransformation("applet Foo \"a b c\"", "edu.rice.cs.plt.swing.SwingUtil.showApplet(new Foo(\"a b c\"), 400, 300);");
    
    
    
    
    _assertAppletTransformation("applet Foo \"a b\"c d", "edu.rice.cs.plt.swing.SwingUtil.showApplet(new Foo(\"a bc\",\"d\"), 400, 300);");
    
    
    
    _assertAppletTransformation("applet Foo c:\\\\file.txt", "edu.rice.cs.plt.swing.SwingUtil.showApplet(new Foo(\"c:\\\\file.txt\"), 400, 300);");
    
    
    
    _assertAppletTransformation("applet Foo /home/user/file", "edu.rice.cs.plt.swing.SwingUtil.showApplet(new Foo(\"/home/user/file\"), 400, 300);");
    _log.log("testInterpretAppletArguments ended");
  }
  
  
  public void testInterpretAppletEscapedArgs() {
    _log.log("testInterpretAppletEscapedArgs started");
    
    
    _assertAppletTransformation("applet Foo \\j", "edu.rice.cs.plt.swing.SwingUtil.showApplet(new Foo(\"j\"), 400, 300);");
    
    
    _assertAppletTransformation("applet Foo \\\"", "edu.rice.cs.plt.swing.SwingUtil.showApplet(new Foo(\"\\\"\"), 400, 300);");
    
    
    _assertAppletTransformation("applet Foo \\\\", "edu.rice.cs.plt.swing.SwingUtil.showApplet(new Foo(\"\\\\\"), 400, 300);");
    
    
    _assertAppletTransformation("applet Foo a\\ b", "edu.rice.cs.plt.swing.SwingUtil.showApplet(new Foo(\"a b\"), 400, 300);");
    _log.log("testInterpretAppletEscapedArgs ended");
  }
  
  
  public void testInterpretAppletQuotedEscapedArgs() {
    _log.log("testInterpretAppletQuotedEscapedArgs started");
    
    
    _assertAppletTransformation("applet Foo \"a \\\" b\"", "edu.rice.cs.plt.swing.SwingUtil.showApplet(new Foo(\"a \\\" b\"), 400, 300);");
    
    
    _assertAppletTransformation("applet Foo \"\\'\"", "edu.rice.cs.plt.swing.SwingUtil.showApplet(new Foo(\"\\\\'\"), 400, 300);");
    
    
    _assertAppletTransformation("applet Foo \"\\\\\"", "edu.rice.cs.plt.swing.SwingUtil.showApplet(new Foo(\"\\\\\"), 400, 300);");
    
    
    _assertAppletTransformation("applet Foo \"\\\" \\d\"", "edu.rice.cs.plt.swing.SwingUtil.showApplet(new Foo(\"\\\" \\\\d\"), 400, 300);");
    
    
    
    _log.log("testInterpretAppletQuotedEscapedArgs started");
  }
  
  
  public void testInterpretAppletSingleQuotedArgs() {
    _log.log("testInterpretAppletSingleQuotedArgs started");
    
    _assertAppletTransformation("applet Foo 'asdf'", "edu.rice.cs.plt.swing.SwingUtil.showApplet(new Foo(\"asdf\"), 400, 300);");
    
    
    _assertAppletTransformation("applet Foo 'a b c'", "edu.rice.cs.plt.swing.SwingUtil.showApplet(new Foo(\"a b c\"), 400, 300);");
    
    
    _assertAppletTransformation("applet Foo 'a b'c", "edu.rice.cs.plt.swing.SwingUtil.showApplet(new Foo(\"a bc\"), 400, 300);");
     _log.log("testInterpretAppletSingleQuotedArgs ended");
  }
  
  
  
  
  
  
  public void testDebugPort() throws IOException {
     _log.log("testDebugPort started");
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
    _log.log("testDebugPort ended");
  }
  
  
  public void testScriptLoading() throws Exception {
    _log.log("testScriptLoading started");
    assertTrue(_model instanceof TestInteractionsModel);
    final TestInteractionsModel model = (TestInteractionsModel)_model;
    
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
      public boolean verifyOverwrite(File f) { return true; }
      public boolean shouldSaveAfterFileMoved(OpenDefinitionsDocument doc, File oldFile) { return true; }
      public boolean shouldUpdateDocumentState() { return true; }
    });
    
    
    final InteractionsScriptModel ism = model.loadHistoryAsScript(new FileOpenSelector() {
      public File[] getFiles() {
        return new File[] { temp };
      }
    });
    final InteractionsDocument doc = model.getDocument();
    
    
    assertTrue("Should have no previous", !ism.hasPrevInteraction());
    try {
      ism.prevInteraction();
      fail("Should not have been able to get previous interaction!");
    }
    catch (IllegalStateException ise) {
      
    }
    
    
    assertTrue("Should have next", ism.hasNextInteraction());
    ism.nextInteraction();
    Utilities.clearEventQueue();
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
    Utilities.clearEventQueue();
    assertEquals("Should have put the second line into the document.", line2, doc.getCurrentInteraction());
    
    
    assertTrue("Should have previous", ism.hasPrevInteraction());
    ism.prevInteraction();
    Utilities.clearEventQueue();
    assertEquals("Should have put the first line into the document.", line1, doc.getCurrentInteraction());
    
    
    Utilities.invokeAndWait(new Runnable() { public void run() { ism.nextInteraction(); } });
    Utilities.clearEventQueue();
    
    model._logInteractionStart();
    Utilities.invokeAndWait(new Runnable() { 
      public void run() { ism.executeInteraction(); } 
    });
    model._waitInteractionDone();

    assertEquals("Should have \"executed\" the second interaction.", line2, model.toEval);
    
    
    assertTrue("Should have no next", !ism.hasNextInteraction());
    try {
      ism.nextInteraction();
      fail("Should not have been able to get next interaction!");
    }
    catch (IllegalStateException ise) {
      
    }
    
    Utilities.clearEventQueue();
    
    assertTrue("Should have previous", ism.hasPrevInteraction());
    ism.prevInteraction();
    Utilities.clearEventQueue();
    assertEquals("Should have put the second line into the document.", line2, doc.getCurrentInteraction());
    
    
    assertTrue("Should have previous", ism.hasPrevInteraction());
    ism.prevInteraction();
    Utilities.clearEventQueue();

    assertEquals("Should have put the first line into the document.", line1, doc.getCurrentInteraction());
    
    
    assertFalse("Should have no previous", ism.hasPrevInteraction());
    


    model._logInteractionStart();
    
    Utilities.invokeAndWait(new Runnable() { public void run() { ism.executeInteraction();  } });
    model._waitInteractionDone();


    assertEquals("Should have \"executed\" the first interaction.", line1, model.toEval);
    
    
    assertTrue("Should have previous", ism.hasPrevInteraction());
    ism.prevInteraction();
    Utilities.clearEventQueue();
    assertEquals("Should have put the first line into the document.", line1, doc.getCurrentInteraction());
    
    
    assertTrue("Should have no previous", !ism.hasPrevInteraction());
    try {
      ism.prevInteraction();
      fail("Should not have been able to get previous interaction!");
    }
    catch (IllegalStateException ise) {  }
    _log.log("testScriptLoading ended");
  }
  
  
  public void testSetChangeInputListener() {
    _log.log("testSetChangeInputListener started");
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
    _log.log("testSetChangeInputListener ended");
  }
  
  
  public void testInteractionsHistoryStoredCorrectly() throws Exception {
    _log.log("testInteractionsHistoryStoredCorrectly started");
    final String code = "public class A {\n";
    
    _model = new BadSyntaxInteractionsModel(_adapter);  
    final BadSyntaxInteractionsModel model = (BadSyntaxInteractionsModel) _model;
    
    final InteractionsDocument doc = model.getDocument();
    
    
    model._logInteractionStart();
    Utilities.invokeAndWait(new Runnable() { 
      public void run() { 
        doc.insertText(doc.getLength(), code, InteractionsDocument.DEFAULT_STYLE);
        model.setSyntaxErrorStrings("Encountered Unexpected \"<EOF>\"", "public class A {\n");
      }
    });
    Utilities.clearEventQueue();
    model.interpretCurrentInteraction();
    model._waitInteractionDone();
    
    

    
    String expected = "public class A {\n" + "\n";  
    String result = doc.getCurrentInteraction();



    assertEquals("Current interaction should still be there - should not have interpreted", expected, result);
    History h = doc.getHistory();
    assertEquals("History should be empty", 0, h.size());
    
    final String code1 = "}\n";
    model.disableSyntaxError();
    model._logInteractionStart();
    Utilities.invokeAndWait(new Runnable() { 
      public void run() { doc.insertText(doc.getLength(), code1, InteractionsDocument.DEFAULT_STYLE); }
    });
    Utilities.clearEventQueue();
    _model.interpretCurrentInteraction();
    model._waitInteractionDone();
    
    assertEquals("Current interaction should not be there - should have interpreted", "", doc.getCurrentInteraction());
    assertEquals("History should contain one interaction", 1, h.size());
    _log.log("testInteractionsHistoryStoredCorrectly ended");
  }
  
  
  public static class TestInteractionsModel extends InteractionsModel {
    String toEval = null;
    String addedClass = null;
    
    private volatile boolean _interactionDone = false;
    private final Object _interactionLock = new Object();
    
    public void _logInteractionStart() { _interactionDone = false; }
    
    public void _waitInteractionDone() throws InterruptedException { 
      synchronized(_interactionLock) { 
        while (! _interactionDone) _interactionLock.wait(); }
    }
    
    
    public TestInteractionsModel(InteractionsDJDocument adapter) {
      
      super(adapter, new File(System.getProperty("user.dir")), 1000, 25);
    }
    
    
    protected void _interpret(String toEval) {

      this.toEval = toEval; 
      replReturnedVoid(); 
    }
    
    protected void _notifyInteractionEnded() { 
      _log.log("_notifyInteractionEnded called.");
      synchronized(_interactionLock) {
        _interactionDone = true;
        _interactionLock.notify();
      }
    }
        
    public Pair<String,String> getVariableToString(String var) {
      fail("cannot getVariableToString in a test");
      return null;
    }
    
    public void addProjectClassPath(File path) { fail("cannot add to classpath in a test"); }
    public void addBuildDirectoryClassPath(File path) { fail("cannot add to classpath in a test"); }
    public void addProjectFilesClassPath(File path) { fail("cannot add to classpath in a test"); }
    public void addExternalFilesClassPath(File path) { fail("cannot add to classpath in a test"); }
    public void addExtraClassPath(File path) { fail("cannot add to classpath in a test"); }
    protected void _resetInterpreter(File wd, boolean force) { fail("cannot reset interpreter in a test"); }
    public List<File> getCompilerBootClassPath() {
      
      return new ArrayList<File>();
    }
    public String transformCommands(String interactionsString) {
      
      return interactionsString;
    }
    
    public void _notifyInteractionStarted() { }
    protected void _notifySyntaxErrorOccurred(int offset, int length) { }
    protected void _notifyInterpreterExited(int status) { }
    protected void _notifyInterpreterResetting() { }
    protected void _notifyInterpreterResetFailed(Throwable t) { }
    public void _notifyInterpreterReady(File wd) { }
    protected void _interpreterResetFailed(Throwable t) { }
    protected void _interpreterWontStart(Exception e) { }
    protected void _notifyInteractionIncomplete() { }
    public ConsoleDocument getConsoleDocument() { return null; }
  }
  
  
  private static class BadSyntaxInteractionsModel extends TestInteractionsModel {
    
    private String errorString1, errorString2;
    private boolean errorPresent = false;
    
    BadSyntaxInteractionsModel(InteractionsDJDocument adapter) { super(adapter); }
    
    protected void setSyntaxErrorStrings(String s1, String s2) { 
      errorString1 = s1; 
      errorString2 = s2; 
      errorPresent = true;
    }
    
    protected void disableSyntaxError() { errorPresent = false; }
    
    
    protected void _interpret(String toEval) {

      this.toEval = toEval; 
      if (errorPresent) replReturnedSyntaxError(errorString1, errorString2, -1, -1, -1, -1); 
      else replReturnedVoid();  
    }
  }

  
  
  public static class IncompleteInputInteractionsModel extends RMIInteractionsModel {
    boolean continuationException;  
    boolean syntaxException;
    
    private volatile boolean _interactionDone = false;
    private final Object _interactionLock = new Object();  
    
    public void _logInteractionStart() { _interactionDone = false; }
    
    public void _waitInteractionDone() throws InterruptedException { 
      synchronized(_interactionLock) { 
        while (! _interactionDone) _interactionLock.wait(); }
    }
    
    
    public IncompleteInputInteractionsModel(InteractionsDJDocument adapter) throws RemoteException {
      
      super(new MainJVM(null), adapter, new File(System.getProperty("user.dir")), 1000, 25);
      _jvm.setInteractionsModel(this); 
      _jvm.startInterpreterJVM();
      continuationException = false;
      syntaxException = false;
    }
    
    public void _notifyInteractionStarted() { }
    protected void _notifyInteractionEnded() { 
      _log.log("_notifyInteractionEnded called.");
      synchronized(_interactionLock) {
        _interactionDone = true;
        _interactionLock.notify();
      }
    }
    protected void _notifySyntaxErrorOccurred(int offset, int length) { }
    protected void _notifyInterpreterExited(int status) { }
    protected void _notifyInterpreterResetting() { }
    protected void _notifyInterpreterResetFailed(Throwable t) { }
    public void _notifyInterpreterReady(File wd) { }
    protected void _interpreterResetFailed(Throwable t) { }
    protected void _interpreterWontStart(Exception e) { }
    protected void _notifyInteractionIncomplete() { _notifyInteractionEnded(); }
    protected void _notifyInterpreterChanged(boolean inProgress) { }
    
    public void dispose() throws RemoteException { _jvm.dispose(); }
    
    public ConsoleDocument getConsoleDocument() { return null; }
    
    @Override public void replThrewException(String message, StackTraceElement[] stackTrace) {
      StringBuilder sb = new StringBuilder(message);
      for(StackTraceElement ste: stackTrace) {
        sb.append("\n\tat ");
        sb.append(ste);
      }
      replThrewException(sb.toString().trim());
    }
    
    @Override public void replThrewException(String message) {
      _log.log("replThrewException called");
      if (message != null) {
        if (message.endsWith("<EOF>\"")) {
          continuationException = true;
          syntaxException = false;
          _interactionIsOver();
          return;
        }
      }
      syntaxException = true; 
      continuationException = false;
      _interactionIsOver();
    }
    
    @Override public void replReturnedSyntaxError(String errorMessage, String interaction, int startRow, int startCol, int endRow,
                                                  int endCol) {
      _log.log("replReturnedSyntaxError called");
      if (errorMessage != null) {
        if (errorMessage.endsWith("<EOF>\"")) {
          continuationException = true;
          syntaxException = false;
          _interactionIsOver();
          return;
        }
      }
      syntaxException = true;
      continuationException = false;
      _interactionIsOver();
    }
    
    public boolean isContinuationException() { return continuationException; }
    public boolean isSyntaxException() { return syntaxException; }
    
    public List<File> getCompilerBootClassPath() {
      
      return new ArrayList<File>();
    }
    
    public String transformCommands(String interactionsString) {
      
      return interactionsString;
    }
  }
  


















}
