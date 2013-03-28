

package edu.rice.cs.util;

import java.io.*;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;


public abstract class RunnableEST implements Runnable {
  public final RunnableEST.Exception _creation = new RunnableEST.Exception("Exception thrown in runnable.");
  public void run() {
    try {
      runEST();
    }
    catch(Throwable t) {
      _creation.initCause(t);
      throw _creation;
    }
  }
  public abstract void runEST();
  
  public static class Exception extends RuntimeException {
    public Exception(String reason) {
      super(reason);
    }
    
    public String getMessage() {
        return super.getCause().getMessage();
    }

    public String getLocalizedMessage() {
        return super.getCause().getMessage();
    }

    public Throwable getCause() {
        return super.getCause();
    }
    
    public Throwable initCause(Throwable t) {
      super.initCause(t);
      StackTraceElement[] ts = t.getStackTrace();
      StackTraceElement[] cs = super.getStackTrace();
      java.util.ArrayList<StackTraceElement> list = new java.util.ArrayList<StackTraceElement>();
      for(int i = 0; i < ts.length-9; ++i) list.add(ts[i]);
      for(int i=2; i < cs.length; ++i) list.add(cs[i]);
      setStackTrace(list.toArray(new StackTraceElement[list.size()]));
      return this;
    }

    public String toString() {
        String s = getClass().getName() + ": " + super.getCause().getClass();
        String message = getLocalizedMessage();
        return (message != null) ? (s + ": " + message) : s;
    }
  }
}
