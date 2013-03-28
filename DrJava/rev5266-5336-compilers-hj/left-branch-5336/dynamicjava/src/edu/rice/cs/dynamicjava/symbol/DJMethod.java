package edu.rice.cs.dynamicjava.symbol;

import edu.rice.cs.dynamicjava.Options;
import edu.rice.cs.dynamicjava.interpreter.RuntimeBindings;
import edu.rice.cs.dynamicjava.interpreter.EvaluatorException;


public interface DJMethod extends Function, Access.Limited {
  
  public DJClass declaringClass();
  public boolean isStatic();
  public boolean isAbstract();
  public boolean isFinal();
  public Access accessibility();
  public Access.Module accessModule();
  
  public DJMethod declaredSignature();
  
  public Object evaluate(Object receiver, Iterable<Object> args, RuntimeBindings bindings, Options options) 
    throws EvaluatorException;
}
