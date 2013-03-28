

package org.jmol.util;


public interface LoggerInterface {

  
  public void debug(String txt);

  
  public void info(String txt);

  
  public void warn(String txt);

  
  public void warn(String txt, Throwable e);

  
  public void error(String txt);

  
  public void error(String txt, Throwable e);

  
  public void fatal(String txt);

  
  public void fatal(String txt, Throwable e);
}
