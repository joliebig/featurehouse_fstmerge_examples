

package edu.rice.cs.drjava.model.repl.newjvm;


public interface InterpretResultVisitor<T> {
  public T forVoidResult(VoidResult that);
  public T forValueResult(ValueResult that);
  public T forExceptionResult(ExceptionResult that);
  public T forSyntaxErrorResult(SyntaxErrorResult that);
}
