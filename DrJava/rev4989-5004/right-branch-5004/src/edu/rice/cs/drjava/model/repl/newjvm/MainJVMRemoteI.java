

package edu.rice.cs.drjava.model.repl.newjvm;

import java.io.File;
import java.rmi.RemoteException;
import edu.rice.cs.util.newjvm.*;
import edu.rice.cs.drjava.model.junit.JUnitError;
import edu.rice.cs.util.classloader.ClassFileError;


public interface MainJVMRemoteI extends MasterRemote {
  
  
  public void systemErrPrint(String s) throws RemoteException;
  
  
  public void systemOutPrint(String s) throws RemoteException;
  
  
  public String getConsoleInput() throws RemoteException;
  
  
  public void nonTestCase(boolean isTestAll, boolean didCompileFail) throws RemoteException;
  
  
  public void classFileError(ClassFileError e) throws RemoteException;
  
  
  public void testSuiteStarted(int numTests) throws RemoteException;
  
  
  public void testStarted(String testName) throws RemoteException;
  
  
  public void testEnded(String testName, boolean wasSuccessful, boolean causedError)
    throws RemoteException;
  
  
  public void testSuiteEnded(JUnitError[] errors) throws RemoteException;
  
  
  public File getFileForClassName(String className) throws RemoteException;
  
  
  
}
