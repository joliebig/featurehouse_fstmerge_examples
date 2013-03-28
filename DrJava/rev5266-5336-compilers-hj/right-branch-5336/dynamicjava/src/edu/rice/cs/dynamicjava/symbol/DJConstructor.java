package edu.rice.cs.dynamicjava.symbol;

import edu.rice.cs.dynamicjava.Options;
import edu.rice.cs.dynamicjava.interpreter.RuntimeBindings;
import edu.rice.cs.dynamicjava.interpreter.EvaluatorException;


public interface DJConstructor extends Function, Access.Limited {
  
  public DJClass declaringClass();
  public Access accessibility();
  public Access.Module accessModule();
  
  public DJConstructor declaredSignature();
  
  public Object evaluate(Object outer, Iterable<Object> args, RuntimeBindings bindings, Options options) 
    throws EvaluatorException;
}
