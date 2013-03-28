

package edu.rice.cs.drjava.model.repl;

import edu.rice.cs.drjava.model.repl.newjvm.*;
import edu.rice.cs.drjava.DrJavaTestCase;

import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.plt.tuple.OptionVisitor;
import edu.rice.cs.plt.reflect.ReflectUtil;
import edu.rice.cs.plt.text.TextUtil;

import edu.rice.cs.dynamicjava.Options;
import edu.rice.cs.dynamicjava.interpreter.*;
import edu.rice.cs.dynamicjava.symbol.*;
import edu.rice.cs.dynamicjava.symbol.type.Type;


public class JavaInterpreterTest extends DrJavaTestCase {
  
  
  
  
  
  
  
  
  
  private volatile InteractionsPaneOptions _interpreterOptions;
  private volatile Interpreter _interpreter;  
  private volatile ClassPathManager _classPathManager;
  private volatile ClassLoader _interpreterLoader;

  static public boolean testValue;

  
  protected void setUp() throws Exception {
    super.setUp();


    _classPathManager = new ClassPathManager(ReflectUtil.SYSTEM_CLASS_PATH);
    _interpreterLoader = _classPathManager.makeClassLoader(null);
    
    
    _interpreterOptions = new InteractionsPaneOptions();
    _interpreter = new Interpreter(_interpreterOptions, _interpreterLoader);
  }

  
  private void tester(Pair<String,Object>[] cases) throws InterpreterException {
    for (int i = 0; i < cases.length; i++) {
      Object out = interpret(cases[i].first());
      assertEquals(cases[i].first() + " interpretation wrong!", cases[i].second(), out);
    }
  }
  
  private Object interpret(String s) throws InterpreterException {
    return _interpreter.interpret(s).apply(new OptionVisitor<Object, Object>() {
      public Object forNone() { return null; }
      public Object forSome(Object obj) { return obj; }
    });
  }

  
  @SuppressWarnings("unchecked")
  public void testConstants() throws InterpreterException {
    Pair<String,Object>[] cases = new Pair[] {
      Pair.make("5", new Integer(5)),
        Pair.make("1356", new Integer(1356)),
        Pair.make("true", Boolean.TRUE),
        Pair.make("false", Boolean.FALSE),
        Pair.make("\'c\'", new Character('c')),
        Pair.make("1.345", new Double(1.345)),
        Pair.make("\"buwahahahaha!\"", "buwahahahaha!"),
        Pair.make("\"yah\\\"eh\\\"\"", "yah\"eh\""),
        Pair.make("'\\''", new Character('\''))
    };
    tester(cases);
  }

  
  @SuppressWarnings("unchecked")
  public void testBooleanOps() throws InterpreterException {
    Pair<String,Object>[] cases = new Pair[] {
      
      Pair.make("true && false", Boolean.FALSE), Pair.make("true && true",
          Boolean.TRUE),
      
      Pair.make("true || true", Boolean.TRUE), Pair.make("false || true", Boolean.TRUE),
          Pair.make("false || false", Boolean.FALSE),
      
      Pair.make("!true", Boolean.FALSE), Pair.make("!false", Boolean.TRUE),
          
      Pair.make("true == true", Boolean.TRUE), Pair.make("false == true", Boolean.FALSE),
          Pair.make("false == false", Boolean.TRUE),
      
      Pair.make("false ^ false", Boolean.valueOf(false ^ false)), Pair.make("false ^ true ",
          Boolean.valueOf(false ^ true))
    };
    tester(cases);
  }

  
  @SuppressWarnings("unchecked")
  public void testShortCircuit() throws InterpreterException {
    Pair<String,Object>[] cases = new Pair[] {
      Pair.make("false && (3 == 1/0)", Boolean.FALSE),
      Pair.make("true || (1/0 != 43)", Boolean.TRUE)
    };
    tester(cases);
  }

  
  @SuppressWarnings("unchecked")
  public void testIntegerOps() throws InterpreterException {
    Pair<String,Object>[] cases = new Pair[] {
      
      Pair.make("5+6", new Integer(5 + 6)),
      
      Pair.make("6-5", new Integer(6 - 5)),
      
      Pair.make("6*5", new Integer(6*5)),
      
      Pair.make("6/5", new Integer(6/5)),
      
      Pair.make("6%5", new Integer(6%5)),
      
      Pair.make("6&5", new Integer(6 & 5)),
      
      Pair.make("6 | 5", new Integer(6 | 5)),
      
      Pair.make("6^5", new Integer(6 ^ 5)),
      
      Pair.make("~6", new Integer(~6)),
      
      Pair.make(" + 5", new Integer(+5)),
      
      Pair.make("-5", new Integer(-5)),
      
      Pair.make("400 << 5", new Integer(400 << 5)),
      
      Pair.make("400 >> 5", new Integer(400 >> 5)),
      
      Pair.make("400 >>> 5", new Integer(400 >>> 5)),
      
      Pair.make("5 < 4", Boolean.valueOf(5 < 4)),
      
      Pair.make("4 <= 4", Boolean.valueOf(4 <= 4)), Pair.make("4 <= 5", Boolean.valueOf(4 <= 5)),
          
      Pair.make("5 > 4", Boolean.valueOf(5 > 4)), Pair.make("5 > 5", Boolean.valueOf(5 > 5)),
          
      Pair.make("5 >= 4", Boolean.valueOf(5 >= 4)), Pair.make("5 >= 5", Boolean.valueOf(5 >= 5)),
          
      Pair.make("5 == 5", Boolean.valueOf(5 == 5)), Pair.make("5 == 6", Boolean.valueOf(
          5 == 6)),
      
      Pair.make("5 != 6", Boolean.valueOf(5 != 6)), Pair.make("5 != 5", Boolean.valueOf(5 != 5))
    };
    tester(cases);
  }

  
  @SuppressWarnings("unchecked")
  public void testDoubleOps() throws InterpreterException {
    Pair<String,Object>[] cases = new Pair[] {
      
      Pair.make("5.6 < 6.7", Boolean.valueOf(5.6 < 6.7)),
      
      Pair.make("5.6 <= 5.6", Boolean.valueOf(5.6 <= 5.6)),
      
      Pair.make("5.6 > 4.5", Boolean.valueOf(5.6 > 4.5)),
      
      Pair.make("5.6 >= 56.4", Boolean.valueOf(5.6 >= 56.4)),
      
      Pair.make("5.4 == 5.4", Boolean.valueOf(5 == 5)),
      
      Pair.make("5.5 != 5.5", Boolean.valueOf(5 != 5)),
      
      Pair.make(" + 5.6", new Double(+5.6)),
      
      Pair.make("-5.6", new Double(-5.6)),
      
      Pair.make("5.6 * 4.5", new Double(5.6*4.5)),
      
      Pair.make("5.6 / 3.4", new Double(5.6/3.4)),
      
      Pair.make("5.6 % 3.4", new Double(5.6%3.4)),
      
      Pair.make("5.6 + 6.7", new Double(5.6 + 6.7)),
      
      Pair.make("4.5 - 3.4", new Double(4.5 - 3.4)),
    };
    tester(cases);
  }

  
  @SuppressWarnings("unchecked")
  public void testStringOps() throws InterpreterException {
    Pair<String,Object>[] cases = new Pair[] {
      
      Pair.make("\"yeah\" + \"and\"", "yeah" + "and"),
      
      Pair.make("\"yeah\".equals(\"yeah\")", Boolean.valueOf("yeah".equals("yeah"))),

    };
    tester(cases);
  }

  
  @SuppressWarnings("unchecked")
  public void testCharacterOps()  throws InterpreterException{
    Pair<String,Object>[] cases = new Pair[] {
      
      Pair.make("'c' == 'c'", Boolean.valueOf('c' == 'c'))
    };
    tester(cases);
  }

  
  @SuppressWarnings("unchecked")
  public void testSemicolon() throws InterpreterException {
    Pair<String,Object>[] cases = new Pair[] {
      Pair.make("'c' == 'c'", Boolean.valueOf('c' == 'c')),
      Pair.make("'c' == 'c';", null),
      Pair.make("String s = \"hello\"", null),
      Pair.make("String x = \"hello\";", null),
      Pair.make("char c = 'c'", null),
      Pair.make("Character d = new Character('d')", null),
      Pair.make("s", "hello"), Pair.make("s;", null),
      Pair.make("x", "hello"), Pair.make("x;", null),
      Pair.make("c", 'c'), Pair.make("d", 'd')
    };
    tester(cases);
  }

  
  @SuppressWarnings("unchecked")
  public void testNullInstanceOf() throws InterpreterException {
    Pair<String,Object>[] cases = new Pair[] {
      Pair.make("null instanceof Object", Boolean.valueOf(null instanceof Object)),
      Pair.make("null instanceof String", Boolean.valueOf(null instanceof String))
    };
    tester(cases);
  }

  
  @SuppressWarnings("unchecked")
  public void testVariableDefinition() throws InterpreterException {
    _interpreter.interpret("int a = 5;");
    _interpreter.interpret("int b = a;");

    _interpreter.interpret("int c = a++;");
  }

  
  @SuppressWarnings("unchecked")
  public void testVariableDefaultValues() throws InterpreterException {
    _interpreter.interpret("byte b");
    _interpreter.interpret("short s");
    _interpreter.interpret("int i");
    _interpreter.interpret("long l");
    _interpreter.interpret("float f");
    _interpreter.interpret("double d");
    _interpreter.interpret("char c");
    _interpreter.interpret("boolean bool");
    _interpreter.interpret("String str");
    Pair<String,Object>[] cases = new Pair[] {
      Pair.make("b", new Byte((byte)0)),
      Pair.make("s", new Short((short)0)),
      Pair.make("i", new Integer(0)),
      Pair.make("l", new Long(0L)),
      Pair.make("f", new Float(0.0f)),
      Pair.make("d", new Double(0.0d)),
      Pair.make("c", new Character('\u')),
      Pair.make("bool", Boolean.valueOf(false)),
      Pair.make("str", null)
    };
    tester(cases);
  }

  
  public void testVariableRedefinition() throws InterpreterException {
    
    try {
      _interpreter.interpret("String s = abc;");
      fail("variable definition should have failed");
    }
    catch (InterpreterException e) {
      
    }
    
    try {
      _interpreter.interpret("Vector v = new Vector();");
      fail("variable definition should have failed");
    }
    catch (InterpreterException e) {
      
    }
    try {
      _interpreter.interpret("File f;");
      fail("variable definition should have failed");
    }
    catch (InterpreterException e) {
      
    }
    try {
      
      _interpreter.interpret("import java.util.Vector;");
      _interpreter.interpret("Vector v = new Vector();");
      _interpreter.interpret("String s = \"abc\";");
      _interpreter.interpret("import java.io.File;");
      _interpreter.interpret("File f = new File(\"\");");
    }
    catch (InterpreterException e) {
      fail("These interpret statements shouldn't cause errors");
    }
    

    
    try {
      _interpreter.interpret("String z = new String(Integer.getInteger(\"somebadproperty\").toString());");
      fail("variable definition should have failed");
    }
    catch (InterpreterException e) {
    }
    
    
    
    _interpreter.interpret("String z = \"z\";");
    
  }

  
  public void testIncompatibleAssignment() throws InterpreterException {
    try {
      _interpreter.interpret("Integer i = new Object()");
      fail("incompatible assignment should have failed");
    }
    catch (InterpreterException e) {
      
    }
    try {
      _interpreter.interpret("Integer i2 = (Integer)new Object();");
      fail("incompatible assignment should have failed");
    }
    catch (InterpreterException e) {
      
    }

    
    _interpreter.interpret("Object o = new Integer(3)");
  }

 
  public void testTypeCheckerExtension() {
    try { _interpreter.interpret("(false) ? 2/0 : 1 "); }
    catch(InterpreterException e) {
      if ( e.getCause() instanceof ArithmeticException ) {
        fail("testTypeCheckerExtension failed to prevent short circuit DivideByZeroException");
      }
    }

    try { _interpreter.interpret("(false) ? 2%0 : 1 "); }
    catch(InterpreterException e) {
      if ( e.getCause() instanceof ArithmeticException ) {
        fail("testTypeCheckerExtension failed to prevent short circuit DivideByZeroException");
      }
    }
  }

  
  public void testEvaluationVisitorExtensionNO_RESULT() {
    try {
      Object out = interpret("true;");
      assertEquals("testEvaluationVisitorExtension", null, out);
    }
    catch(InterpreterException e) {
      fail("testEvaluationVisitorExtension Exception returned for none exceptional code!" + e);
    }
  }










































  
  public void testInitializeArrays() throws InterpreterException {
    try {
      _interpreter.interpret("int i[] = new int[]{1,2,3};");
      _interpreter.interpret("int j[][] = new int[][]{{1}, {2,3}};");
      _interpreter.interpret("int k[][][][] = new int[][][][]{{{{1},{2,3}}}};");
    }
    catch(IllegalArgumentException iae) {
      fail("Legal array initializations were not accepted.");
    }
  }

  
  public void testArrayCloning() throws InterpreterException {
    try { _interpreter.interpret("new int[]{0}.clone()"); }
    catch(RuntimeException e) { fail("Array cloning failed."); }
  }
  





























  
  public void testDeclareVoidMethod() {
    try { _interpreter.interpret("void method() {}"); }
    catch (InterpreterException ere) { fail("Should be able to declare void methods."); }
  }

  
  public void testUserDefinedVoidMethod() throws InterpreterException {
     Object result = interpret("public void foo() {}; foo()");
     assertSame("Should have returned NO_RESULT.", null, result);
   }
  
  
  public void testThrowNull() throws InterpreterException {
    try {
      _interpreter.interpret("throw null");
      fail("Should have thrown an EvaluatorException with a NullPointerException as cause.");
    }
    catch(Throwable t) {
      if ((t == null) || (!(t instanceof EvaluatorException))) {
        fail("Should have thrown an EvaluatorException with a NullPointerException as cause.");
      }
      else {
        Throwable cause = t.getCause();
        if (!(cause instanceof NullPointerException)) {
          fail("Should have thrown an EvaluatorException with a NullPointerException as cause.");
        }
      }
    }
  }

}
