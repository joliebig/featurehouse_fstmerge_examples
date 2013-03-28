

package org.jmol.util;

import java.io.PrintStream;


public class DefaultLogger implements LoggerInterface {

  
  protected void log(PrintStream out, int level, String txt, Throwable e) {
    if (out == System.err)
      System.out.flush();
    if ((out != null) && ((txt != null) || (e != null))) {
      txt = (txt != null ? txt : "");
      out.println(
          (Logger.logLevel() ? "[" + Logger.getLevel(level) + "] " : "") +
          txt +
          (e != null ? ": " + e.getMessage() : ""));
      if (e != null) {
        StackTraceElement[] elements = e.getStackTrace();
        if (elements != null) {
          for (int i = 0; i < elements.length; i++) {
            out.println(
                elements[i].getClassName() + " - " +
                elements[i].getLineNumber() + " - " +
                elements[i].getMethodName());
          }
        }
      }
    }
    if (out == System.err)
      System.err.flush();
  }

  
  public void debug(String txt) {
    log(System.out, Logger.LEVEL_DEBUG, txt, null);
  }

  
  public void info(String txt) {
    log(System.out, Logger.LEVEL_INFO, txt, null);
  }

  
  public void warn(String txt) {
    log(System.out, Logger.LEVEL_WARN, txt, null);
  }

  
  public void warn(String txt, Throwable e) {
    log(System.out, Logger.LEVEL_WARN, txt, e);
  }

  
  public void error(String txt) {
    log(System.err, Logger.LEVEL_ERROR, txt, null);
  }

  
  public void error(String txt, Throwable e) {
    log(System.err, Logger.LEVEL_ERROR, txt, e);
  }

  
  public void fatal(String txt) {
    log(System.err, Logger.LEVEL_FATAL, txt, null);
  }

  
  public void fatal(String txt, Throwable e) {
    log(System.err, Logger.LEVEL_FATAL, txt, e);
  }
}
