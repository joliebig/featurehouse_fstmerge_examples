

package org.jmol.util;


public class JUnitLogger implements LoggerInterface {

  
  public static void activateLogger() {
    Logger.setLogger(new JUnitLogger());
  }

  private static String information = null;
  
  
  public static void setInformation(String txt) {
    information = txt;
  }
  
  
  private JUnitLogger() {
    
  }

  
  private void logError(String txt, Throwable e) {
    System.err.println(
        "Error: " +
        ((information != null) ? ("[" + information + "] ") : "") +
        ((txt != null) ? (txt + " - ") : "") +
        ((e != null) ? e.getClass().getName() : "") +
        ((e != null) && (e.getMessage() != null) ? (" - " + e.getMessage()) : ""));
    if (e != null) {
      StackTraceElement[] elements = e.getStackTrace();
      if (elements != null) {
        for (int i = 0; i < elements.length; i++) {
          System.err.println(
              elements[i].getClassName() + " - " +
              elements[i].getLineNumber() + " - " +
              elements[i].getMethodName());
        }
      }
    }
  }

  
  public void debug(String txt) {
    System.out.println(txt);
  }

  
  public void info(String txt) {
    System.out.println(txt);
  }

  
  public void warn(String txt) {
    System.out.println(txt);
  }

  
  public void warn(String txt, Throwable e) {
    logError(txt, e);
  }

  
  public void error(String txt) {
    
  }

  
  public void error(String txt, Throwable e) {
    logError(txt, e);
  }

  
  public void fatal(String txt) {
    
  }

  
  public void fatal(String txt, Throwable e) {
    logError(txt, e);
  }

}
