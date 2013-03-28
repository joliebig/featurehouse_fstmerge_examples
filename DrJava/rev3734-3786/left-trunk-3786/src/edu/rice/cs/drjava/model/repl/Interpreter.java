

package edu.rice.cs.drjava.model.repl;


public interface Interpreter {
  
  public static final Object NO_RESULT = new Object();

  
  public Object interpret(String s) throws ExceptionReturnedException;
  public Object parse(String s);
}
