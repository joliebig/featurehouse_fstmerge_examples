

package edu.rice.cs.util;


public class StreamRedirectException extends RuntimeException {
  public StreamRedirectException(String s) { super(s); }
  public StreamRedirectException(String s, Throwable t) { super(s,t); }
}
