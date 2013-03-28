package edu.rice.cs.dynamicjava.interpreter;

import java.io.PrintWriter;

import static edu.rice.cs.plt.debug.DebugUtil.debug;

public class EvaluatorException extends InterpreterException {
  
  public EvaluatorException(Throwable cause) {
    super(cause);
    updateAllStacks(cause, new String[0][]);
  }
  
  public EvaluatorException(Throwable cause, String... extraStackElements) {
    super(cause);
    updateAllStacks(cause, new String[][]{ extraStackElements });
  }
  
  
  public EvaluatorException(Throwable cause, String[]... extraStackElements) {
    super(cause);
    updateAllStacks(cause, extraStackElements);
  }
  
  
  private void updateAllStacks(Throwable cause, String[][]extraStack) {
    StackTraceElement[] current = new Throwable().getStackTrace();
    while (cause != null) {
      updateStack(cause, current, extraStack);
      cause = cause.getCause();
    }
  }
  
  
  private void updateStack(Throwable cause, StackTraceElement[] current, String[][] extraStack) {
    StackTraceElement[] stack = cause.getStackTrace();
    int offset = stack.length - current.length;
    int minMatch = stack.length;
    boolean allMatch = true;
    
    while (minMatch-1 >= 0 && minMatch-1-offset >= 2) {
      StackTraceElement stackElt = stack[minMatch-1];
      StackTraceElement currentElt = current[minMatch-1-offset];
      if (stackElt.getClassName().equals(currentElt.getClassName()) &&
          stackElt.getMethodName().equals(currentElt.getMethodName())) {
        minMatch--;
      }
      else { allMatch = false; break; }
    }
    if (allMatch && minMatch > 0) {
      int bestExtraMatch = 0;
      boolean bestExtraMatchesAll = true;
      for (String[] extras : extraStack) {
        int extraMatch = 0;
        boolean extraMatchesAll = true;
        while (extraMatch < extras.length && minMatch-extraMatch-1 >= 0) {
          StackTraceElement stackElt = stack[minMatch-extraMatch-1];
          if (extras[extraMatch].equals(stackElt.getClassName() + "." + stackElt.getMethodName())) {
            extraMatch++;
          }
          else { extraMatchesAll = false; break; }
        }
        if (extraMatch > bestExtraMatch) {
          bestExtraMatch = extraMatch;
          bestExtraMatchesAll = extraMatchesAll;
        }
        else if (extraMatch == bestExtraMatch) {
          bestExtraMatchesAll |= extraMatchesAll;
        }
      }
      minMatch -= bestExtraMatch;
      allMatch &= bestExtraMatchesAll;
    }
    
    if (!allMatch) { debug.log("Stack has unmatched elements"); }
    if (minMatch < stack.length) {
      StackTraceElement[] newStack = new StackTraceElement[minMatch];
      System.arraycopy(stack, 0, newStack, 0, minMatch);
      cause.setStackTrace(newStack);
    }
  }
    
  public void printUserMessage(PrintWriter out) { getCause().printStackTrace(out); }
  
}
