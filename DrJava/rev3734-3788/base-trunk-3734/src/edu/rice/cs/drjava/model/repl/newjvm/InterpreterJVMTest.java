

package edu.rice.cs.drjava.model.repl.newjvm;

import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.drjava.model.repl.DynamicJavaAdapter;
import edu.rice.cs.drjava.model.repl.ExceptionReturnedException;
import edu.rice.cs.drjava.model.repl.JavaInterpreter;

import java.util.Hashtable;


public final class InterpreterJVMTest extends DrJavaTestCase {
  private InterpreterJVM jvm = InterpreterJVM.ONLY;
  private Hashtable<String, InterpreterData> _debugInterpreters;
  private JavaInterpreter _interpreter1;
  private JavaInterpreter _interpreter2;
  private JavaInterpreter _interpreter3;
  
  private void _addInterpreter(String name, JavaInterpreter interpreter) {
    InterpreterJVM.ONLY.addInterpreter(name, interpreter);
  }
  
  public void setUp() throws Exception {
    super.setUp();
    _debugInterpreters = InterpreterJVM.ONLY.getInterpreters();
    _interpreter1 = new DynamicJavaAdapter(new ClassPathManager());
    _interpreter2 = new DynamicJavaAdapter(new ClassPathManager());
    _interpreter3 = new DynamicJavaAdapter(new ClassPathManager());
  }
  
  public void testAddNamedDebugInterpreter() {
    assertTrue(_debugInterpreters.isEmpty());
    _addInterpreter("interpreter1", _interpreter1);
    assertSame(_interpreter1, _debugInterpreters.get("interpreter1").getInterpreter());
    assertTrue(!_debugInterpreters.containsKey("interpreter2"));
    
    _addInterpreter("interpreter2", _interpreter2);
    assertSame(_interpreter1, _debugInterpreters.get("interpreter1").getInterpreter());
    assertSame(_interpreter2, _debugInterpreters.get("interpreter2").getInterpreter());
    
    try {
      _addInterpreter("interpreter1", _interpreter3);
      fail();
    }
    catch (IllegalArgumentException ex) {
      assertSame(_interpreter1, _debugInterpreters.get("interpreter1").getInterpreter());
      assertSame(_interpreter2, _debugInterpreters.get("interpreter2").getInterpreter());
    }
  }
  
  
  public void testSwitchingActiveInterpreter() throws ExceptionReturnedException {
    String var0 = "stuff";
    String var1 = "junk";
    String var2 = "raargh";
    Object val0 = new Byte("5");
    Object val1 = new Short("2");
    Object val2 = new Long(2782);
    _addInterpreter("1",_interpreter1);
    _addInterpreter("2",_interpreter2);
    
    JavaInterpreter interpreter = (JavaInterpreter) jvm.getActiveInterpreter();
    interpreter.defineVariable(var0, val0);
    assertEquals(val0, interpreter.interpret(var0));

    jvm.setActiveInterpreter("1");
    interpreter = (JavaInterpreter) jvm.getActiveInterpreter();
    try {
      interpreter.interpret(var0);
      fail();
    }
    catch (ExceptionReturnedException ex) {
      
    }
    interpreter.defineVariable(var1,val1);
    assertEquals(val1, interpreter.interpret(var1));
    
    jvm.setActiveInterpreter("2");
    interpreter = (JavaInterpreter) jvm.getActiveInterpreter();
    try {
      interpreter.interpret(var0);
      fail();
    }
    catch (ExceptionReturnedException ex) {
    }
    try {
      interpreter.interpret(var1);
      fail();
    }
    catch (ExceptionReturnedException ex) {
      
    }
    interpreter.defineVariable(var2,val2);
    assertEquals(val2, interpreter.interpret(var2));

    jvm.setToDefaultInterpreter();
    interpreter = (JavaInterpreter) jvm.getActiveInterpreter();
    try {
      interpreter.interpret(var1);
      fail();
    }
    catch (ExceptionReturnedException ex) {
    }
    try {
      interpreter.interpret(var2);
      fail();
    }
    catch (ExceptionReturnedException ex) {
      
    }
    assertEquals(val0, jvm.getActiveInterpreter().interpret(var0));

    jvm.setActiveInterpreter("1");
    interpreter = (JavaInterpreter) jvm.getActiveInterpreter();
    try {
      interpreter.interpret(var0);
      fail();
    }
    catch (ExceptionReturnedException ex) {
    }
    try {
      interpreter.interpret(var2);
      fail();
    }
    catch (ExceptionReturnedException ex) {
      
    }
    assertEquals(val1, interpreter.interpret(var1));

    try {
      jvm.setActiveInterpreter("not an interpreter");
      fail();
    }
    catch (IllegalArgumentException ex) {
      assertEquals("Interpreter 'not an interpreter' does not exist.", ex.getMessage());
    }
  }
}
