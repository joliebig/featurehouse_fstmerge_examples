

package koala.dynamicjava.interpreter.error;

import java.io.*;
import koala.dynamicjava.interpreter.*;
import koala.dynamicjava.tree.*;
import koala.dynamicjava.util.*;



public class ExecutionError extends Error {
  
  protected Throwable thrown;
  
  
  private final static String BUNDLE
    = "koala.dynamicjava.interpreter.resources.messages";
  
  public final static String SHOW_CAUSE_PROPERTY
    = "koala.dynamicjava.interpreter.showCause";
  
  public final static String SHOW_TRACE_PROPERTY
    = "koala.dynamicjava.interpreter.showTrace";
  
  
  private final static LocalizedMessageReader reader
    = new LocalizedMessageReader(BUNDLE);
  
  
  private Node node;
  
  
  private String rawMessage;
  
  
  public ExecutionError(String s, Node n) {
    rawMessage = s;
    node       = n;
    getMessage(); 
    NodeProperties.setError(n, this);
  }
  
  
  public Node getNode() {
    return node;
  }
  
  
  public void printStackTrace() {
    this.printStackTrace(System.err);
  }
  
  
  public void printStackTrace(PrintStream s) {
    this.printStackTrace(new PrintWriter(s, true));
  }
  
  
  public void printStackTrace(PrintWriter w) {
    String trace = System.getProperty(SHOW_TRACE_PROPERTY);
    if (trace != null && !new Boolean(trace).booleanValue()) {
      w.println(this);
    } else {
      super.printStackTrace(w);
      String cause = System.getProperty(SHOW_CAUSE_PROPERTY);
      if (cause == null || new Boolean(cause).booleanValue()) {
        if (thrown != null) {
          w.println("Caused by: ");
          thrown.printStackTrace(w);
        }
      }
    }
  }
  
  
  public String getMessage() {
    String [] args = new String[0];
    if (node != null && NodeProperties.hasErrorStrings(node)) { args = NodeProperties.getErrorStrings(node); }
    return reader.getMessage(rawMessage, args);
  }
}
