

package edu.rice.cs.drjava.model.repl.newjvm;

import java.rmi.RemoteException;
import java.util.Vector;
import java.util.List;
import java.io.File;
import edu.rice.cs.util.newjvm.*;


public interface InterpreterJVMRemoteI extends SlaveRemote {
  
  public List<String> findTestClasses(List<String> classNames, List<File> files)
    throws RemoteException;
  
  public boolean runTestSuite() throws RemoteException;
  
  public void setPackageScope(String s) throws RemoteException;
  

  
  public void setShowMessageOnResetFailure(boolean show) throws RemoteException;

  
  public void addJavaInterpreter(String name) throws RemoteException;

  
  public void addDebugInterpreter(String name, String className) throws RemoteException;

  
  public void removeInterpreter(String name) throws RemoteException;

  
  public boolean setActiveInterpreter(String name) throws RemoteException;

  
  public boolean setToDefaultInterpreter() throws RemoteException;

  
  public Vector<String> getAugmentedClassPath() throws RemoteException;

  
  public String getVariableToString(String var) throws RemoteException;

  
  public String getVariableClassName(String var) throws RemoteException;

  
  public void setPrivateAccessible(boolean allow) throws RemoteException;






 
  
  public void interpret(String s) throws RemoteException;
  
  
  public void addProjectClassPath(String s) throws RemoteException;
  
  
  public void addBuildDirectoryClassPath(String s) throws RemoteException;
  
  
  public void addProjectFilesClassPath(String s) throws RemoteException;
  
  
  public void addExternalFilesClassPath(String s) throws RemoteException;
  
  
  public void addExtraClassPath(String s) throws RemoteException;
  
}
