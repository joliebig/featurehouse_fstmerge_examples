   

package edu.rice.cs.drjava.model.repl.newjvm;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import edu.rice.cs.dynamicjava.interpreter.InterpreterException;
import edu.rice.cs.dynamicjava.interpreter.EvaluatorException;


public abstract class InterpretResult implements Serializable {  
  public abstract <T> T apply(Visitor<T> v);

  public static interface Visitor<T> {
    public T forNoValue();
    public T forStringValue(String val);
    public T forCharValue(Character val);
    public T forNumberValue(Number val);
    public T forBooleanValue(Boolean val);
    public T forObjectValue(String valString);
    public T forException(String message);
    public T forEvalException(String message, StackTraceElement[] stackTrace);
    public T forUnexpectedException(Throwable t);
    public T forBusy();
  }
  
  public static InterpretResult busy() { return BusyResult.INSTANCE; }
  
  
  
  private static class BusyResult extends InterpretResult {
    public static final BusyResult INSTANCE = new BusyResult();
    public <T> T apply(Visitor<T> v) { return v.forBusy(); }
  }
  
  
  public static InterpretResult exception(InterpreterException e) { return new ExceptionResult(e); }
  
  private static class ExceptionResult extends InterpretResult {
    private final String _msg;
    private final StackTraceElement[] _stackTrace;
    public ExceptionResult(InterpreterException e) {
      if (e instanceof EvaluatorException) {
        
        _msg = e.getMessage();
        _stackTrace = e.getCause().getStackTrace();
      }
      else {
        
        StringWriter msg = new StringWriter();
        e.printUserMessage(new PrintWriter(msg));
        _msg = msg.toString().trim();
        _stackTrace = null;
      }
    }
    public <T> T apply(Visitor<T> v) {
      if (_stackTrace != null) 
        return v.forEvalException(_msg, _stackTrace);
      else
        return v.forException(_msg);
    }
  }
  

  public static InterpretResult unexpectedException(Throwable t) {
    return new UnexpectedExceptionResult(t);
  }
  
  private static class UnexpectedExceptionResult extends InterpretResult {
    private final Throwable _t;
    public UnexpectedExceptionResult(Throwable t) { _t = t; }
    public <T> T apply(Visitor<T> v) { return v.forUnexpectedException(_t); }
  }
  
  
  public static InterpretResult noValue() { return NoValueResult.INSTANCE; }
  
  private static class NoValueResult extends InterpretResult {
    public static final NoValueResult INSTANCE = new NoValueResult();
    public <T> T apply(Visitor<T> v) { return v.forNoValue(); }
  }
  
  
  public static InterpretResult stringValue(String s) { return new StringValueResult(s); }
  
  private static class StringValueResult extends InterpretResult {
    private final String _val;
    public StringValueResult(String val) { _val = val; }
    public <T> T apply(Visitor<T> v) { return v.forStringValue(_val); }
  }
  
  
  public static InterpretResult charValue(Character c) { return new CharValueResult(c); }
  
  private static class CharValueResult extends InterpretResult {
    private final Character _val;
    public CharValueResult(Character val) { _val = val; }
    public <T> T apply(Visitor<T> v) { return v.forCharValue(_val); }
  }
  

  public static InterpretResult numberValue(Number n) { return new NumberValueResult(n); }
  
  private static class NumberValueResult extends InterpretResult {
    private final Number _val;
    public NumberValueResult(Number val) { _val = val; }
    public <T> T apply(Visitor<T> v) { return v.forNumberValue(_val); }
  }
  

  public static InterpretResult booleanValue(Boolean b) { return new BooleanValueResult(b); }
  
  private static class BooleanValueResult extends InterpretResult {
    private final Boolean _val;
    public BooleanValueResult(Boolean val) { _val = val; }
    public <T> T apply(Visitor<T> v) { return v.forBooleanValue(_val); }
  }
  

  public static InterpretResult objectValue(String objS) { return new ObjectValueResult(objS); }

  private static class ObjectValueResult extends InterpretResult {
    private final String _objString;
    public ObjectValueResult(String objString) { _objString = objString; }
    public <T> T apply(Visitor<T> v) { return v.forObjectValue(_objString); }
  } 
  
}
