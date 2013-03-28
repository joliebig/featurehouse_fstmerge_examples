

package edu.rice.cs.drjava.model.repl.newjvm;

import java.rmi.RemoteException;
import java.util.List;
import java.io.File;

import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.util.newjvm.*;


public interface InterpreterJVMRemoteI extends SlaveRemote {
  
  public List<String> findTestClasses(List<String> classNames, List<File> files)
    throws RemoteException;
  
  public boolean runTestSuite() throws RemoteException;
  
  
  public void setEnforceAllAccess(boolean enforce) throws RemoteException;
  
  
  public void setEnforcePrivateAccess(boolean enforce) throws RemoteException;

  
  public void setRequireSemicolon(boolean require) throws RemoteException;
  
  
  public void setRequireVariableType(boolean require) throws RemoteException;
  
  
  public void addInterpreter(String name) throws RemoteException;
  
  
  public void removeInterpreter(String name) throws RemoteException;
  
  
  public Pair<Boolean, Boolean> setActiveInterpreter(String name) throws RemoteException;
  
  
  public Pair<Boolean, Boolean> setToDefaultInterpreter() throws RemoteException;
  
  
  public InterpretResult interpret(String s) throws RemoteException;
  
  
  public String getVariableToString(String var, int... indices) throws RemoteException;
  
  
  public String getVariableType(String var, int... indices) throws RemoteException;
  
  
  public Iterable<File> getClassPath() throws RemoteException;  
  
  
  public void addProjectClassPath(File f) throws RemoteException;
  
  
  public void addBuildDirectoryClassPath(File f) throws RemoteException;
  
  
  public void addProjectFilesClassPath(File f) throws RemoteException;
  
  
  public void addExternalFilesClassPath(File f) throws RemoteException;
  
  
  public void addExtraClassPath(File f) throws RemoteException;
  
}
