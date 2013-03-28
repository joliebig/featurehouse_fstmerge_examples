package edu.rice.cs.dynamicjava.sourcechecker;

import java.io.IOException;
import java.io.PrintWriter;

import edu.rice.cs.dynamicjava.interpreter.InterpreterException;


public class SourceException extends InterpreterException {
  
  public SourceException(IOException e) { super(e); }

  public void printUserMessage(PrintWriter out) {
    getCause().printStackTrace(out);
  }

}
