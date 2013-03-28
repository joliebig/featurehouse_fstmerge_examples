package edu.rice.cs.dynamicjava.interpreter;

import java.io.PrintWriter;

import koala.dynamicjava.tree.SourceInfo;


public class InternalException extends InterpreterException implements SourceInfo.Wrapper {
  
  private final SourceInfo _si;
  
  public InternalException(RuntimeException e, SourceInfo si) { super(e); _si = si; }
  
  public SourceInfo getSourceInfo() { return _si; }

  public void printUserMessage(PrintWriter out) {
    out.println("Internal error at " + _si);
    getCause().printStackTrace(out);
  }

}
