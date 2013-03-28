

package edu.rice.cs.plt.debug;


public interface Log {
  
  
  public void log();
  
  
  public void log(String message);
  
  
  public void log(Throwable t);
  
  
  public void log(String message, Throwable t);
  
  
  public void logStart();
  
  
  public void logStart(String message);
  
  
  public void logStart(String name, Object value);
  
  
  public void logStart(String message, String name, Object value);
  
  
  public void logStart(String[] names, Object... values);
  
  
  public void logStart(String message, String[] names, Object... values);
  
  
  public void logEnd();

  
  public void logEnd(String message);
  
  
  public void logEnd(String name, Object value);

  
  public void logEnd(String message, String name, Object value);
  
  
  public void logEnd(String[] names, Object... values);

  
  public void logEnd(String message, String[] names, Object... values);
  
  
  public void logStack();
  
  
  public void logStack(String message);
  
  
  public void logValue(String name, Object value);

  
  public void logValue(String message, String name, Object value);

  
  public void logValues(String[] names, Object... values);

  
  public void logValues(String message, String[] names, Object... values);

}
