

package edu.rice.cs.drjava.model.repl;

import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.drjava.model.*;
import edu.rice.cs.drjava.model.repl.newjvm.MainJVM;

import edu.rice.cs.util.FileOpenSelector;
import edu.rice.cs.util.Log;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.util.text.ConsoleDocument;
import edu.rice.cs.util.text.EditDocumentException;
import javax.swing.text.BadLocationException;
import edu.rice.cs.plt.tuple.Pair;


import edu.rice.cs.drjava.model.repl.newjvm.*;
import edu.rice.cs.drjava.DrJavaTestCase;

import edu.rice.cs.plt.tuple.OptionVisitor;
import edu.rice.cs.plt.reflect.ReflectUtil;
import edu.rice.cs.plt.text.TextUtil;

import edu.rice.cs.dynamicjava.Options;
import edu.rice.cs.dynamicjava.interpreter.*;
import edu.rice.cs.dynamicjava.symbol.*;
import edu.rice.cs.dynamicjava.symbol.type.Type;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;

import java.rmi.RemoteException;

import edu.rice.cs.drjava.model.GlobalModelTestCase;

import static edu.rice.cs.drjava.model.repl.InteractionsModelTest.TestInteractionsModel;
import static edu.rice.cs.drjava.model.repl.InteractionsModelTest.IncompleteInputInteractionsModel;


public final class InteractionsModelErrorTest extends GlobalModelTestCase {
  protected static final String UNARY_FUN_NON_PUBLIC_INTERFACE_TEXT = 
    "interface UnaryFun {\n"+
    "  public Object apply(final Object arg);\n"+
    "}";
  protected static final String UNARY_FUN_PUBLIC_INTERFACE_TEXT = 
    "public interface UnaryFun {\n"+
    "  public Object apply(final Object arg);\n"+
    "}";

  protected static final String UNARY_FUN_NON_PUBLIC_CLASS_TEXT = 
    "abstract class UnaryFun {\n"+
    "  public abstract Object apply(final Object arg);\n"+
    "}";
  protected static final String UNARY_FUN_PUBLIC_CLASS_TEXT = 
    "public abstract class UnaryFun {\n"+
    "  public abstract Object apply(final Object arg);\n"+
    "}";
  protected static final String CLASS_IN_PACKAGE_CLASS_TEXT = 
    "package foo;\n"+
    "public class Bar {\n"+
    "  public void run() { }\n"+
    "}";

  private volatile InteractionsPaneOptions _interpreterOptions;
  private volatile Interpreter _interpreter;  
  private volatile ClassPathManager _classPathManager;
  private volatile ClassLoader _interpreterLoader;
  
  private static Log _log = new Log("InteractionsModelErrorTest.txt", false);
  
  public InteractionsModelErrorTest() {
    super();

    _classPathManager = new ClassPathManager(ReflectUtil.SYSTEM_CLASS_PATH);
    _interpreterLoader = _classPathManager.makeClassLoader(null);
    
    
    _interpreterOptions = new InteractionsPaneOptions();
    _interpreter = new Interpreter(_interpreterOptions, _interpreterLoader);
  }
  
  
  private void tester(Pair<String,Object>[] cases) throws InterpreterException {
    for (int i = 0; i < cases.length; i++) {
      Object out = interpretDirectly(cases[i].first());
      assertEquals(cases[i].first() + " interpretation wrong!", cases[i].second(), out);
    }
  }
  
  private Object interpretDirectly(String s) throws InterpreterException {
    return _interpreter.interpret(s).apply(new OptionVisitor<Object, Object>() {
      public Object forNone() { return null; }
      public Object forSome(Object obj) { return obj; }
    });
  }
  
  protected String _name() {
    return "compiler=" + _model.getCompilerModel().getActiveCompiler().getName() + ": ";
  }

  
  @SuppressWarnings("unchecked")
  public void testInterpretExtendNonPublic()
    throws BadLocationException, IOException, InterruptedException, InterpreterException {
    _log.log("testInterpretExtendNonPublic started");
    
    OpenDefinitionsDocument doc = setupDocument(UNARY_FUN_NON_PUBLIC_INTERFACE_TEXT);
    final File file = tempFile();
    saveFile(doc, new FileSelector(file));
    CompileShouldSucceedListener listener = new CompileShouldSucceedListener();
    _model.addListener(listener);
    listener.compile(doc);
    if (_model.getCompilerModel().getNumErrors() > 0) {
      fail("compile failed: " + getCompilerErrorString());
    }
    listener.checkCompileOccurred();
    _model.removeListener(listener);
    assertCompileErrorsPresent(_name(), false);
    
    
    File compiled = classForJava(file, "UnaryFun");
    assertTrue(_name() + "Class file should exist after compile", compiled.exists());    
    
    _classPathManager.addBuildDirectoryCP(compiled.getParentFile());
    
    try {
      _interpreter.interpret("UnaryFun f = new UnaryFun() { Object apply(Object arg) { return (Integer)arg * (Integer)arg; }}");
      fail("Should fail with 'cannot access its superinterface' exception.");
    }
    catch(edu.rice.cs.dynamicjava.interpreter.CheckerException ce) {
      assertTrue(ce.getMessage().indexOf("cannot access its superinterface")>=0);
    }
  }
  
  
  @SuppressWarnings("unchecked")
  public void testInterpretExtendPublic()
    throws BadLocationException, IOException, InterruptedException, InterpreterException {
    _log.log("testInterpretExtendPublic started");
    
    OpenDefinitionsDocument doc = setupDocument(UNARY_FUN_PUBLIC_INTERFACE_TEXT);
    final File file = createFile("UnaryFun.java");
    saveFile(doc, new FileSelector(file));
    CompileShouldSucceedListener listener = new CompileShouldSucceedListener();
    _model.addListener(listener);
    listener.compile(doc);
    if (_model.getCompilerModel().getNumErrors() > 0) {
      fail("compile failed: " + getCompilerErrorString());
    }
    listener.checkCompileOccurred();
    _model.removeListener(listener);
    assertCompileErrorsPresent(_name(), false);
    
    
    File compiled = classForJava(file, "UnaryFun");
    assertTrue(_name() + "Class file should exist after compile", compiled.exists());    
    
    _classPathManager.addBuildDirectoryCP(compiled.getParentFile());
    
    _interpreter.interpret("UnaryFun f = new UnaryFun() { Object apply(Object arg) { return (Integer)arg * (Integer)arg; }}");
  }
  
  
  @SuppressWarnings("unchecked")
  public void testInterpretExtendNonPublicClass()
    throws BadLocationException, IOException, InterruptedException, InterpreterException {
    _log.log("testInterpretExtendNonPublic started");
    
    OpenDefinitionsDocument doc = setupDocument(UNARY_FUN_NON_PUBLIC_CLASS_TEXT);
    final File file = tempFile();
    saveFile(doc, new FileSelector(file));
    CompileShouldSucceedListener listener = new CompileShouldSucceedListener();
    _model.addListener(listener);
    listener.compile(doc);
    if (_model.getCompilerModel().getNumErrors() > 0) {
      fail("compile failed: " + getCompilerErrorString());
    }
    listener.checkCompileOccurred();
    _model.removeListener(listener);
    assertCompileErrorsPresent(_name(), false);
    
    
    File compiled = classForJava(file, "UnaryFun");
    assertTrue(_name() + "Class file should exist after compile", compiled.exists());    
    
    _classPathManager.addBuildDirectoryCP(compiled.getParentFile());
    
    try {
      _interpreter.interpret("UnaryFun f = new UnaryFun() { public Object apply(Object arg) { return (Integer)arg * (Integer)arg; }}");
      fail("Should fail with 'cannot access its superclass' exception.");
    }
    catch(edu.rice.cs.dynamicjava.interpreter.CheckerException ce) {
      assertTrue(ce.getMessage().indexOf("cannot access its superclass")>=0);
    }
  }
  
  
  @SuppressWarnings("unchecked")
  public void testInterpretExtendPublicClass()
    throws BadLocationException, IOException, InterruptedException, InterpreterException {
    _log.log("testInterpretExtendPublic started");
    
    OpenDefinitionsDocument doc = setupDocument(UNARY_FUN_PUBLIC_CLASS_TEXT);
    final File file = createFile("UnaryFun.java");
    saveFile(doc, new FileSelector(file));
    CompileShouldSucceedListener listener = new CompileShouldSucceedListener();
    _model.addListener(listener);
    listener.compile(doc);
    if (_model.getCompilerModel().getNumErrors() > 0) {
      fail("compile failed: " + getCompilerErrorString());
    }
    listener.checkCompileOccurred();
    _model.removeListener(listener);
    assertCompileErrorsPresent(_name(), false);
    
    
    File compiled = classForJava(file, "UnaryFun");
    assertTrue(_name() + "Class file should exist after compile", compiled.exists());    
    
    _classPathManager.addBuildDirectoryCP(compiled.getParentFile());
    
    _interpreter.interpret("UnaryFun f = new UnaryFun() { public Object apply(Object arg) { return (Integer)arg * (Integer)arg; }}");
  }
  
  
  @SuppressWarnings("unchecked")
  public void testInterpretGetPackageClass()
    throws BadLocationException, IOException, InterruptedException, InterpreterException {
    _log.log("testInterpretGetPackageClass started");
    
    OpenDefinitionsDocument doc = setupDocument(CLASS_IN_PACKAGE_CLASS_TEXT);

    final File dir = tempDirectory();
    final File packDir = new File(dir, "foo");
    packDir.mkdir();
    final File file = new File(packDir, "Bar.java");
    saveFile(doc, new FileSelector(file));
    CompileShouldSucceedListener listener = new CompileShouldSucceedListener();
    _model.addListener(listener);
    listener.compile(doc);
    if (_model.getCompilerModel().getNumErrors() > 0) {
      fail("compile failed: " + getCompilerErrorString());
    }
    listener.checkCompileOccurred();
    _model.removeListener(listener);
    assertCompileErrorsPresent(_name(), false);
    
    
    File compiled = classForJava(file, "Bar");
    assertTrue(_name() + "Class file should exist after compile", compiled.exists());    
    
    _classPathManager.addBuildDirectoryCP(compiled.getParentFile().getParentFile());
    
    Object out = interpretDirectly("new foo.Bar().getClass().getPackage().getName()");
    assertEquals("Package of foo.Bar should be foo", "foo", out);
  }
  
  
  @SuppressWarnings("unchecked")
  public void testInterpretGetPackageAnonymous()
    throws BadLocationException, IOException, InterruptedException, InterpreterException {
    _log.log("testInterpretGetPackageAnonymous started");

    Object out = interpretDirectly("new Runnable() { public void run() { } }.getClass().getPackage()");
    assertEquals("Package of $1 should be null", null, out);
    
    out = interpretDirectly("package foo; new Runnable() { public void run() { } }.getClass().getPackage().getName()");
    assertEquals("Package of foo.$1 should be foo", "foo", out);
  }
}
