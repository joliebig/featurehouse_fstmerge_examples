

package edu.rice.cs.drjava.model.repl;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.config.OptionListener;
import edu.rice.cs.drjava.config.OptionEvent;
import edu.rice.cs.drjava.model.repl.newjvm.ClassPathManager;
import edu.rice.cs.util.swing.Utilities;

import junit.framework.*;


public final class JavaInterpreterTest extends DrJavaTestCase {
  private JavaInterpreter _interpreter;
  static public boolean testValue;

  
  protected void setUp() throws Exception {
    super.setUp();
    _interpreter = new DynamicJavaAdapter(new ClassPathManager());
    testValue = false;
  }

  
  private void tester(Pair[] cases) throws ExceptionReturnedException {
    for (int i = 0; i < cases.length; i++) {
      Object out = _interpreter.interpret(cases[i].first());
      assertEquals(cases[i].first() + " interpretation wrong!", cases[i].second(), out);
    }
  }

  
  public void testConstants() throws ExceptionReturnedException {
    Pair[] cases = new Pair[] {
      Pair.make("5", new Integer(5)),
        Pair.make("1356", new Integer(1356)),
        Pair.make("true", Boolean.TRUE),
        Pair.make("false", Boolean.FALSE),
        Pair.make("\'c\'", "'" + new Character('c') + "'"),
        Pair.make("1.345", new Double(1.345)),
        Pair.make("\"buwahahahaha!\"", new String("\"buwahahahaha!\"")),
        Pair.make("\"yah\\\"eh\\\"\"", new String("\"yah\"eh\"\"")),
        Pair.make("'\\''", "'" + new Character('\'') + "'")
    };
    tester(cases);
  }

  
  public void testBooleanOps() throws ExceptionReturnedException {
    Pair[] cases = new Pair[] {
      
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

  
  public void testShortCircuit() throws ExceptionReturnedException {
    Pair[] cases = new Pair[] {
      Pair.make("false && (3 == 1/0)", Boolean.FALSE),
      Pair.make("true || (1/0 != 43)", Boolean.TRUE)
    };
    tester(cases);
  }

  
  public void testIntegerOps() throws ExceptionReturnedException {
    Pair[] cases = new Pair[] {


















      



      
      Pair.make("400 << 5", new Integer(400 << 5)),
      
      Pair.make("400 >> 5", new Integer(400 >> 5)),
      
      Pair.make("400 >>> 5", new Integer(400 >>> 5)),












      Pair.make("5 != 6", Boolean.valueOf(5 != 6)), Pair.make("5 != 5", Boolean.valueOf(5 != 5))
    };
    tester(cases);
  }

  
  public void testDoubleOps() throws ExceptionReturnedException {
    Pair[] cases = new Pair[] {
      
      Pair.make("5.6 < 6.7", Boolean.valueOf(5.6 < 6.7)),
      
      Pair.make("5.6 <= 5.6", Boolean.valueOf(5.6 <= 5.6)),
      
      Pair.make("5.6 > 4.5", Boolean.valueOf(5.6 > 4.5)),
      
      Pair.make("5.6 >= 56.4", Boolean.valueOf(5.6 >= 56.4)),
      
      Pair.make("5.4 == 5.4", Boolean.valueOf(5 == 5)),
      
      Pair.make("5.5 != 5.5", Boolean.valueOf(5 != 5)),
      
      Pair.make("+5.6", new Double(+5.6)),
      
      Pair.make("-5.6", new Double(-5.6)),
      
      Pair.make("5.6 * 4.5", new Double(5.6*4.5)),
      
      Pair.make("5.6 / 3.4", new Double(5.6/3.4)),
      
      Pair.make("5.6 % 3.4", new Double(5.6%3.4)),
      
      Pair.make("5.6 + 6.7", new Double(5.6 + 6.7)),
      
      Pair.make("4.5 - 3.4", new Double(4.5 - 3.4)),
    };
    tester(cases);
  }

  
  public void testStringOps() throws ExceptionReturnedException {
    Pair[] cases = new Pair[] {
      
      Pair.make("\"yeah\" + \"and\"", new String("\"yeah" + "and\"")),
      
      Pair.make("\"yeah\".equals(\"yeah\")", Boolean.valueOf("yeah".equals("yeah"))),

    };
    tester(cases);
  }

  
  public void testCharacterOps()  throws ExceptionReturnedException{
    Pair[] cases = new Pair[] {
      
      Pair.make("'c' == 'c'", Boolean.valueOf('c' == 'c'))
    };
    tester(cases);
  }

  
  public void testSemicolon() throws ExceptionReturnedException {
    Pair[] cases = new Pair[] {
      Pair.make("'c' == 'c'", Boolean.valueOf('c' == 'c')),
      Pair.make("'c' == 'c';", JavaInterpreter.NO_RESULT),
      Pair.make("String s = \"hello\"", JavaInterpreter.NO_RESULT),
      Pair.make("String x = \"hello\";", JavaInterpreter.NO_RESULT),
      Pair.make("char c = 'c'", JavaInterpreter.NO_RESULT),
      Pair.make("Character d = new Character('d')", JavaInterpreter.NO_RESULT),
      Pair.make("s", "\"hello\""), Pair.make("s;", JavaInterpreter.NO_RESULT),
      Pair.make("x", "\"hello\""), Pair.make("x;", JavaInterpreter.NO_RESULT),
      Pair.make("c", "'c'"), Pair.make("d", "'d'")
    };
    tester(cases);
  }

  
  public void testNullInstanceOf() throws ExceptionReturnedException {
    Pair[] cases = new Pair[] {
      Pair.make("null instanceof Object", Boolean.valueOf(null instanceof Object)),
      Pair.make("null instanceof String", Boolean.valueOf(null instanceof String))
    };
    tester(cases);
  }

  
  public void testVariableDefinition() throws ExceptionReturnedException {
    _interpreter.interpret("int a = 5;");
    _interpreter.interpret("int b = a;");

    _interpreter.interpret("int c = a++;");
  }

  
  public void testVariableDefaultValues() throws ExceptionReturnedException {
    _interpreter.interpret("byte b");
    _interpreter.interpret("short s");
    _interpreter.interpret("int i");
    _interpreter.interpret("long l");
    _interpreter.interpret("float f");
    _interpreter.interpret("double d");
    _interpreter.interpret("char c");
    _interpreter.interpret("boolean bool");
    _interpreter.interpret("String str");
    Pair[] cases = new Pair[] {
      Pair.make("b", new Byte((byte)0)),
      Pair.make("s", new Short((short)0)),
      Pair.make("i", new Integer(0)),
      Pair.make("l", new Long(0L)),
      Pair.make("f", new Float(0.0f)),
      Pair.make("d", new Double(0.0d)),
      Pair.make("c", "'" + new Character('\u') + "'"), 
      Pair.make("bool", Boolean.valueOf(false)),
      Pair.make("str", null)
    };
    tester(cases);
  }

  
  public void testVariableRedefinition() throws ExceptionReturnedException{
    
    try {
      _interpreter.interpret("String s = abc;");
      fail("variable definition should have failed");
    }
    catch (ExceptionReturnedException e) {
      
    }
    
    try {
      _interpreter.interpret("Vector v = new Vector();");
      fail("variable definition should have failed");
    }
    catch (ExceptionReturnedException e) {
      
    }
    try {
      _interpreter.interpret("File f;");
      fail("variable definition should have failed");
    }
    catch (ExceptionReturnedException e) {
      
    }
    try {
      
      _interpreter.interpret("import java.util.Vector;");
      _interpreter.interpret("Vector v = new Vector();");
      _interpreter.interpret("String s = \"abc\";");
      _interpreter.interpret("import java.io.File;");
      _interpreter.interpret("File f = new File(\"\");");
    }
    catch (ExceptionReturnedException e) {
      fail("These interpret statements shouldn't cause errors");
    }
    

    
    try {
      _interpreter.interpret("String z = new String(Integer.getInteger(\"somebadproperty\").toString());");
      fail("variable definition should have failed");
    }
    catch (ExceptionReturnedException e) {
    }
    
    
    
    _interpreter.interpret("String z = \"z\";");
    
  }

  
  public void testIncompatibleAssignment() throws ExceptionReturnedException {
    try {
      _interpreter.interpret("Integer i = new Object()");
      fail("incompatible assignment should have failed");
    }
    catch (ExceptionReturnedException e) {
      
    }
    try {
      _interpreter.interpret("Integer i2 = (Integer)new Object();");
      fail("incompatible assignment should have failed");
    }
    catch (ExceptionReturnedException e) {
      
    }

    
    _interpreter.interpret("Object o = new Integer(3)");
  }

 
  public void testTypeCheckerExtension() {
    try { _interpreter.interpret("(false) ? 2/0 : 1 "); }
    catch(ExceptionReturnedException e) {
      if ( e.getContainedException() instanceof ArithmeticException ) {
        fail("testTypeCheckerExtension failed to prevent short circuit DivideByZeroException");
      }
    }

    try { _interpreter.interpret("(false) ? 2%0 : 1 "); }
    catch(ExceptionReturnedException e) {
      if ( e.getContainedException() instanceof ArithmeticException ) {
        fail("testTypeCheckerExtension failed to prevent short circuit DivideByZeroException");
      }
    }
  }

  
  public void testEvaluationVisitorExtensionNO_RESULT() {
    try {
      Object out = _interpreter.interpret("true;");
      assertEquals("testEvaluationVisitorExtension", JavaInterpreter.NO_RESULT, out);
    }
    catch(ExceptionReturnedException e) {
      fail("testEvaluationVisitorExtension Exception returned for none exceptional code!" + e);
    }
  }

  
  public void testDefineVariableExternally() throws ExceptionReturnedException {
    _interpreter.defineVariable("foo", new String("hello"));
    assertEquals("manipulated externally defined variable",
                 "\"ello\"", _interpreter.interpret("foo.substring(1,5)"));
    _interpreter.defineVariable("x", 3);
    assertEquals("externally defined variable x",
                 new Integer(3), _interpreter.interpret("x"));
    assertEquals("incremented externally defined variable x",
                 new Integer(4), _interpreter.interpret("++x"));
  }

  
  public void testQueryVariableExternally() {
    _interpreter.defineVariable("x", 7);
    
    assertEquals("external query for x",
                 new Integer(7), _interpreter.getVariable("x"));

    
    try {
      _interpreter.getVariable("undefined");
      fail("Should have thrown IllegalStateException");
    }
    catch (IllegalStateException e) {
      
    }
  }

  
  public void testDefineConstantExternally() {
    _interpreter.defineConstant("y", 3);
    try {
      _interpreter.interpret("y = 4");
      fail("should not be able to assign to a constant");
    }
    catch (ExceptionReturnedException e) {
      
    }
  }

  
  public void testInitializeArrays() throws ExceptionReturnedException {
    try {
      _interpreter.interpret("int i[] = new int[]{1,2,3};");
      _interpreter.interpret("int j[][] = new int[][]{{1}, {2,3}};");
      _interpreter.interpret("int k[][][][] = new int[][][][]{{{{1},{2,3}}}};");
    }
    catch(IllegalArgumentException iae) {
      fail("Legal array initializations were not accepted.");
    }
  }

  
  public void testArrayCloning() throws ExceptionReturnedException {
    try { _interpreter.interpret("new int[]{0}.clone()"); }
    catch(RuntimeException e) { fail("Array cloning failed."); }
  }
  
  
  public void testAllowPrivateAccess() throws ExceptionReturnedException {
    
    DrJava.getConfig().addOptionListener(OptionConstants.ALLOW_PRIVATE_ACCESS, new OptionListener<Boolean>() {
      public void optionChanged(OptionEvent<Boolean> oce) {
        _interpreter.setPrivateAccessible(oce.value.booleanValue());
      }
    });
    DrJava.getConfig().setSetting(OptionConstants.ALLOW_PRIVATE_ACCESS, Boolean.valueOf(false));
    try {
      _interpreter.interpret("class A { private int i = 0; }");
      _interpreter.interpret("new A().i");
      fail("Should not have access to the private field i inside class A.");
    }
    catch (ExceptionReturnedException ere) {
      assertTrue(ere.getContainedException() instanceof IllegalAccessException);
    }
    DrJava.getConfig().setSetting(OptionConstants.ALLOW_PRIVATE_ACCESS, Boolean.valueOf(true));
    Utilities.clearEventQueue();
    assertEquals("Should be able to access private field i whose value should be 0",
                 new Integer(0),
                 _interpreter.interpret("new A().i"));
  }

  
  public void testDeclareVoidMethod() {
    try { _interpreter.interpret("void method() {}"); }
    catch (ExceptionReturnedException ere) { fail("Should be able to declare void methods."); }
  }

  
  public void testUserDefinedVoidMethod() throws ExceptionReturnedException {
     Object result = _interpreter.interpret("public void foo() {}; foo()");
     assertSame("Should have returned NO_RESULT.", Interpreter.NO_RESULT, result);
   }
}


class Pair extends edu.rice.cs.util.Pair<String, Object> {
  
  public Pair(String f, Object s) { super(f, s); }

  
  public static Pair make(String first, Object second) { return new Pair(first, second); }

  
  public String first() { return getFirst(); }

  
  public Object second() { return getSecond(); }
}
