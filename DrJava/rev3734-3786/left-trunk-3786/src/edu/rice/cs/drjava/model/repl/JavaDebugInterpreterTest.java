

package edu.rice.cs.drjava.model.repl;

import edu.rice.cs.drjava.model.debug.*;
import edu.rice.cs.drjava.model.*;
import edu.rice.cs.util.text.EditDocumentException;
import edu.rice.cs.util.swing.Utilities;

import java.util.LinkedList;
import java.io.*;
import javax.swing.text.*;

import koala.dynamicjava.util.*;
import koala.dynamicjava.tree.*;
import koala.dynamicjava.interpreter.context.*;


public final class JavaDebugInterpreterTest extends DebugTestCase {
  private static final String _newLine = System.getProperty("line.separator");
  private JavaDebugInterpreter _debugInterpreter;



  protected static final String MONKEY_STUFF =
     "class MonkeyStuff {\n" +
     "  int foo = 6;\n" +
     "  class MonkeyInner {\n" +
     "    int innerFoo = 8;\n" +
     "    public class MonkeyTwoDeep {\n" +
     "      int twoDeepFoo = 13;\n" +
     "      class MonkeyThreeDeep {\n" +
     "        public int threeDeepFoo = 18;\n" +
     "        public void threeDeepMethod() {\n" +
    "          int blah;\n" +
    "          System.out.println(MonkeyStuff.MonkeyInner.MonkeyTwoDeep.MonkeyThreeDeep.this.threeDeepFoo);\n" +
    "        }\n" +
    "      }\n" +
    "      int getNegativeTwo() { return -2; }\n" +
    "    }\n" +
    "  }\n" +
    "\n" +
    "  public static void main(String[] args) {\n" +
    "    new MonkeyStuff().new MonkeyInner().new MonkeyTwoDeep().new MonkeyThreeDeep().threeDeepMethod();\n" +
    "  }\n" +
    "}";

  protected static final String MONKEY_STATIC_STUFF =
     "package monkey;\n" +
     "public class MonkeyStaticStuff {\n" +
     "  static int foo = 6;\n" +
     "  static class MonkeyInner {\n" +
     "    static int innerFoo = 8;\n" +
     "    static public class MonkeyTwoDeep {\n" +
     "      static int twoDeepFoo = 13;\n" +
     "      public static class MonkeyThreeDeep {\n" +
     "        public static int threeDeepFoo = 18;\n" +
     "        public static void threeDeepMethod() {\n" +
    "          System.out.println(MonkeyStaticStuff.MonkeyInner.MonkeyTwoDeep.MonkeyThreeDeep.threeDeepFoo);\n" +
    "          System.out.println(MonkeyTwoDeep.twoDeepFoo);\n" +
    "          System.out.println(twoDeepFoo);\n" +
    "        }\n" +
    "      }\n" +
    "      static int getNegativeTwo() { return -2; }\n" +
    "    }\n" +
    "  }\n" +
    "}";

  protected static final String MONKEY_WITH_INNER_CLASS =
        "class Monkey {\n" +
        "  static int foo = 6; \n" +
        "  class MonkeyInner { \n" +
        "    int innerFoo = 8;\n" +
        "    class MonkeyInnerInner { \n" +
        "      int innerInnerFoo = 10;\n" +
        "      public void innerMethod() { \n" +
        "        int innerMethodFoo;\n" +
        "        String nullString = null;\n" +
       "        innerMethodFoo = 12;\n" +
       "        foo++;\n" +
       "        innerFoo++;\n" +
       "        innerInnerFoo++;\n" +
       "        innerMethodFoo++;\n" +
       "        staticMethod();\n" +
       "        System.out.println(\"innerMethodFoo: \" + innerMethodFoo);\n" +
       "      }\n" +
       "    }\n" +
       "  }\n" +
       "  public void bar() {\n" +
       "    final MonkeyInner.MonkeyInnerInner mi = \n" +
       "      new MonkeyInner().new MonkeyInnerInner();\n" +
       "    mi.innerMethod();\n" +
       "    final int localVar = 99;\n" +
       "    new Thread() {\n" +
       "      public void run() {\n" +
       "        final int localVar = mi.innerInnerFoo;\n" +
       "        new Thread() {\n" +
       "          public void run() {\n" +
       "            new Thread() {\n" +
       "              public void run() {\n" +
       "                System.out.println(\"localVar = \" + localVar);\n" +
       "              }\n" +
       "            }.run();\n" +
       "          }\n" +
       "        }.run();\n" +
       "      }\n" +
       "    }.run();\n" +
       "  }\n" +
       "  public static void staticMethod() {\n" +
       "    int z = 3;\n" +
       "  }\n" +
       "}\n";


  public void setUp() throws Exception {
    super.setUp();
    
    _debugInterpreter = new JavaDebugInterpreter("test", "") {
      public EvaluationVisitorExtension makeEvaluationVisitor(Context context) {
        return new DebugEvaluationVisitor(context, _name);





      }
    };

  }

  public void notifyInterpreterAssignment(String name) {

  }

  public void testVerifyClassName() {
    _debugInterpreter.setClassName("bar.baz.Foo$FooInner$FooInnerInner");
    assertEquals("verify failed", 0, _debugInterpreter.verifyClassName("bar.baz.Foo.FooInner.FooInnerInner"));
    assertEquals("verify failed", 1, _debugInterpreter.verifyClassName("bar.baz.Foo.FooInner"));
    assertEquals("verify failed", 2, _debugInterpreter.verifyClassName("bar.baz.Foo"));
    assertEquals("verify failed", -1, _debugInterpreter.verifyClassName("bar.baz"));
    assertEquals("verify failed", 0, _debugInterpreter.verifyClassName("Foo.FooInner.FooInnerInner"));
    assertEquals("verify failed", 2, _debugInterpreter.verifyClassName("Foo"));
    assertEquals("verify failed", 1, _debugInterpreter.verifyClassName("FooInner"));
    assertEquals("verify failed", 0, _debugInterpreter.verifyClassName("FooInnerInner"));
    assertEquals("verify failed", 1, _debugInterpreter.verifyClassName("Foo.FooInner"));
    assertEquals("verify failed", 0, _debugInterpreter.verifyClassName("FooInner.FooInnerInner"));
    assertEquals("verify failed", -1, _debugInterpreter.verifyClassName("FooInner.FooInnerInner.Foo"));
    assertEquals("verify failed", -1, _debugInterpreter.verifyClassName("FooInner.FooInnerInner.foo"));
    assertEquals("verify failed", -1, _debugInterpreter.verifyClassName("o.FooInner"));
    _debugInterpreter.setClassName("Foo$FooInner$FooInnerInner");
    assertEquals("verify failed", 0, _debugInterpreter.verifyClassName("Foo.FooInner.FooInnerInner"));
    assertEquals("verify failed", 2, _debugInterpreter.verifyClassName("Foo"));
    assertEquals("verify failed", 1, _debugInterpreter.verifyClassName("FooInner"));
    assertEquals("verify failed", 0, _debugInterpreter.verifyClassName("FooInnerInner"));
    assertEquals("verify failed", 1, _debugInterpreter.verifyClassName("Foo.FooInner"));
    assertEquals("verify failed", 0, _debugInterpreter.verifyClassName("FooInner.FooInnerInner"));
    assertEquals("verify failed", -1, _debugInterpreter.verifyClassName("FooInner.FooInnerInner.Foo"));
    assertEquals("verify failed", -1, _debugInterpreter.verifyClassName("FooInner.FooInnerInner.foo"));
    assertEquals("verify failed", -1, _debugInterpreter.verifyClassName("o.FooInner"));
  }

  private void assertEqualsNodes(String message, Node expected, Node actual) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DisplayVisitor dve = new DisplayVisitor(baos);
    expected.acceptVisitor(dve);
    String s1 = baos.toString();
    baos.reset();
    actual.acceptVisitor(dve);
    String s2 = baos.toString();
    
    assertEquals(message, s1, s2);
  }

  
  public void testConvertToName() {
    ThisExpression thisExp = _debugInterpreter.buildUnqualifiedThis();
    Node n = _debugInterpreter.visitThis(thisExp);
    LinkedList<IdentifierToken> thisList = new LinkedList<IdentifierToken>(); 
    thisList.add(new Identifier("this"));
    QualifiedName expected = new QualifiedName(thisList);
    assertEqualsNodes("convertThisToName did not return the correct QualifiedName", expected, n);
  }

  
  public void testConvertToObjectFieldAccess() {
    _debugInterpreter.setClassName("bar.baz.Foo$FooInner$FooInnerInner");
    LinkedList<IdentifierToken> ids = new LinkedList<IdentifierToken>(); 
    ids.add(new Identifier("Foo"));
    ThisExpression thisExp = new ThisExpression(ids, "", 0, 0, 0, 0);
    Node n = _debugInterpreter.visitThis(thisExp);
    Node expected = 
      new ObjectFieldAccess(
        new ObjectFieldAccess(_debugInterpreter._convertThisToName(_debugInterpreter.buildUnqualifiedThis()), "this$1"),
          "this$0");

    assertEqualsNodes("convertThisToObjectFieldAccess did not return the correct ObjectFieldAccess",
                      expected,
                      n);
  }

  
  public void testAccessFieldsAndMethodsOfOuterClasses()
    throws DebugException, BadLocationException, EditDocumentException, IOException, InterruptedException {
    File file = new File(_tempDir, "MonkeyStuff.java");
    OpenDefinitionsDocument doc = doCompile(MONKEY_STUFF, file);
    BreakpointTestListener debugListener = new BreakpointTestListener();
    _debugger.addListener(debugListener);
    
    synchronized(_notifierLock) {
      _debugger.startup();
      _setPendingNotifies(1);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }

    
    int index = MONKEY_STUFF.indexOf("System.out.println");
    _debugger.toggleBreakpoint(doc, index, 11, true);

    
    synchronized(_notifierLock) {
      interpretIgnoreResult("java MonkeyStuff");
       _setPendingNotifies(3); 
       while (_pendingNotifies > 0) _notifierLock.wait();
     }

    

    
    
    interpret("try {\n" +
              "  for (int i = MonkeyStuff.this.foo; i < 7; i++) {\n"+
              "    do{System.out.println(MonkeyInner.this.innerFoo);}\n" +
              "    while(MonkeyStuff.MonkeyInner.this.innerFoo == MonkeyThreeDeep.this.threeDeepFoo);\n" +
              "    switch(MonkeyStuff.MonkeyInner.MonkeyTwoDeep.this.twoDeepFoo) {\n" +
              "      case 13: if (this.threeDeepFoo == 5) {\n" +
              "                  System.out.println(MonkeyThreeDeep.this.threeDeepFoo);\n" +
              "               }\n" +
              "               else {\n" +
              "                  MonkeyThreeDeep.this.threeDeepFoo = MonkeyThreeDeep.this.threeDeepFoo + MonkeyStuff.this.foo;\n" +
              "               }\n" +
              "    }\n" +
              "  }\n" +
              "}\n" +
              "catch(Exception e) { System.out.println(MonkeyThreeDeep.this.threeDeepFoo);}\n" +
              "finally {System.out.println(MonkeyInner.MonkeyTwoDeep.this.twoDeepFoo);}");
    assertInteractionsDoesNotMatch(".*^18$.*");
    assertInteractionsDoesNotMatch(".*^6$.*");
    assertInteractionsMatches(".*^8" + _newLine + "13$.*");
    
    
    interpret("foo");
    


    assertInteractionsMatches(".*^6$.*");
    
    interpret("foo = 123");
    assertEquals("foo should have been modified" , "123", interpret("MonkeyStuff.this.foo"));
    interpret("int foo = 999;");
    assertEquals("foo should refer to defined foo", "999", interpret("foo"));
    assertEquals("declaring foo should not change MonkeyStuff.this.foo", "123", interpret("MonkeyStuff.this.foo"));

    assertEquals("call method of outer class #1", "-2", interpret("getNegativeTwo()"));
    assertEquals("call method of outer class #2", "-2", interpret("MonkeyTwoDeep.this.getNegativeTwo()"));
    assertEquals("call method of outer class #3", "-2",
                 interpret("MonkeyInner.MonkeyTwoDeep.this.getNegativeTwo()"));
    assertEquals("call method of outer class #4", "-2",
                 interpret("MonkeyStuff.MonkeyInner.MonkeyTwoDeep.this.getNegativeTwo()"));

    
    _model.closeFile(doc);
    
    Utilities.clearEventQueue();
    debugListener.assertBreakpointRemovedCount(1);  

    
    if (printMessages) printStream.println("Shutting down...");
    synchronized(_notifierLock) {
      _debugger.shutdown();
      _setPendingNotifies(1);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }

    debugListener.assertDebuggerShutdownCount(1);  
    if (printMessages) printStream.println("Shut down.");
    _debugger.removeListener(debugListener);
  }

  
  public void testAccessStaticFieldsAndMethodsOfOuterClasses()
    throws DebugException, BadLocationException, EditDocumentException, IOException, InterruptedException {
    File dir = new File(_tempDir, "monkey");
    dir.mkdir();
    File file = new File(dir, "MonkeyStaticStuff.java");
    OpenDefinitionsDocument doc = doCompile(MONKEY_STATIC_STUFF, file);
    BreakpointTestListener debugListener = new BreakpointTestListener();
    _debugger.addListener(debugListener);
    
    synchronized(_notifierLock) {
      _debugger.startup();
      _setPendingNotifies(1);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }

    
    int index = MONKEY_STATIC_STUFF.indexOf("System.out.println");
    _debugger.toggleBreakpoint(doc,index,11,true);

    
    synchronized(_notifierLock) {
      
      interpretIgnoreResult("monkey.MonkeyStaticStuff.MonkeyInner.MonkeyTwoDeep.MonkeyThreeDeep.threeDeepMethod();");
      _setPendingNotifies(3); 
      while (_pendingNotifies > 0) _notifierLock.wait();
    }

    assertEquals("should find field of static outer class",
                 "13",
                 interpret("twoDeepFoo"));
    assertEquals("should find field of static outer class",
                 "13",
                 interpret("MonkeyInner.MonkeyTwoDeep.twoDeepFoo"));

    interpret("twoDeepFoo = 100;");
    assertEquals("should have assigned field of static outer class",
                 "100",
                 interpret("twoDeepFoo"));
    assertEquals("should have assigned the field of static outer class",
                 "100",
                 interpret("MonkeyStaticStuff.MonkeyInner.MonkeyTwoDeep.twoDeepFoo"));
    assertEquals("should have assigned the field of static outer class",
                 "100",
                 interpret("monkey.MonkeyStaticStuff.MonkeyInner.MonkeyTwoDeep.twoDeepFoo"));

    interpret("int twoDeepFoo = -10;");
    assertEquals("Should have successfully shadowed field of static outer class", "-10", interpret("twoDeepFoo"));
    
    assertEquals("should have assigned the field of static outer class", "100",
                 interpret("MonkeyTwoDeep.twoDeepFoo"));
    
    assertEquals("should have assigned the field of static outer class", "100",
                 interpret("MonkeyStaticStuff.MonkeyInner.MonkeyTwoDeep.twoDeepFoo"));

    assertEquals("Should be able to access a static field of a non-static outer class", "6", interpret("foo"));
    assertEquals("Should be able to access a static field of a non-static outer class", "6",
                 interpret("MonkeyStaticStuff.foo"));

    interpret("foo = 987;");
    assertEquals("Should have changed the value of a static field of a non-static outer class", "987",
                 interpret("foo"));
    
    assertEquals("Should have changed the value of a static field of a non-static outer class", "987",
                 interpret("MonkeyStaticStuff.foo"));

    interpret("int foo = 56;");
    assertEquals("Should have defined a new variable", "56", interpret("foo"));
    assertEquals("Should have shadowed the value of a static field of a non-static outer class", "987",
                 interpret("MonkeyStaticStuff.foo"));

    assertEquals("should be able to call method of outer class", "-2", interpret("getNegativeTwo()"));
    assertEquals("should be able to call method of outer class", "-2", interpret("MonkeyTwoDeep.getNegativeTwo()"));
    assertEquals("should be able to call method of outer class", "-2",
                 interpret("MonkeyInner.MonkeyTwoDeep.getNegativeTwo()"));
    assertEquals("should be able to call method of outer class", "-2",
                 interpret("MonkeyStaticStuff.MonkeyInner.MonkeyTwoDeep.getNegativeTwo()"));

    
    if (printMessages) printStream.println("Shutting down...");

    synchronized(_notifierLock) {
      _debugger.shutdown();
      _setPendingNotifies(1);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }

    debugListener.assertDebuggerShutdownCount(1);  
    if (printMessages) printStream.println("Shut down.");

    _debugger.removeListener(debugListener);
  }

  public void testAccessNullFieldsAndFinalLocalVariables()
    throws DebugException, BadLocationException, EditDocumentException, IOException, InterruptedException {
    File file = new File(_tempDir, "Monkey.java");
    OpenDefinitionsDocument doc = doCompile(MONKEY_WITH_INNER_CLASS, file);
    BreakpointTestListener debugListener = new BreakpointTestListener();
    _debugger.addListener(debugListener);
    
    synchronized(_notifierLock) {
      _debugger.startup();
      _setPendingNotifies(1);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }

    
    int index = MONKEY_WITH_INNER_CLASS.indexOf("innerMethodFoo = 12;");
    _debugger.toggleBreakpoint(doc,index,10,true);
    index = MONKEY_WITH_INNER_CLASS.indexOf("System.out.println(\"localVar = \" + localVar);");
    _debugger.toggleBreakpoint(doc,index,32,true);

    
    synchronized(_notifierLock) {
      interpretIgnoreResult("new Monkey().bar()");
       _setPendingNotifies(3); 
       while (_pendingNotifies > 0) _notifierLock.wait();
     }

    
    assertEquals("nullString should be null", "null", interpret("nullString"));
    interpret("nullString = new Integer(3)");
    assertInteractionsContains("Error: Bad types in assignment");
    assertEquals("nullString should still be null", "null", interpret("nullString"));
    assertEquals("Should be able to assign a string to nullString", "\"asdf\"", interpret("nullString = \"asdf\""));
    assertEquals("Should equal \"asdf\"", "true", interpret("nullString.equals(\"asdf\")"));

    
    synchronized(_notifierLock) {
      _asyncResume();
      _setPendingNotifies(3);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    
    assertEquals("Should be able to access localVar", "11", interpret("localVar"));
    interpret("localVar = 5");
    
    
    

    
    if (printMessages) {
      printStream.println("Shutting down...");
    }
    synchronized(_notifierLock) {
      _debugger.shutdown();
      _setPendingNotifies(1);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }

    debugListener.assertDebuggerShutdownCount(1);  
    if (printMessages) {
      printStream.println("Shut down.");
    }
    _debugger.removeListener(debugListener);
  }

  

  
}
