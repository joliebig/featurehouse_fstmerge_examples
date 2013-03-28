

package edu.rice.cs.drjava.model.repl;

import java.io.File;
import java.io.IOException;


public interface InteractionsModelCallback {

  
  public int getDebugPort() throws IOException;

  
  public void replSystemOutPrint(String s);

  
  public String getConsoleInput();

  
  public void setInputListener(InputListener listener);

  
  public void changeInputListener(InputListener oldListener, InputListener newListener);

  
  public void replSystemErrPrint(String s);

  
  public void replReturnedVoid();

  
  public void replReturnedResult(String result, String style);

  
  public void replThrewException(String exceptionClass, String message, String stackTrace, String specialMessage);

  
  public void replReturnedSyntaxError(String errorMessage, String interaction, int startRow, int startCol,
                                      int endRow, int endCol);

  
  public void replCalledSystemExit(int status);

  
  public void interpreterResetFailed(Throwable th);

  
  public void interpreterResetting();

  
  public void interpreterReady(File wd);
  
  
  public void slaveJVMUsed();
}