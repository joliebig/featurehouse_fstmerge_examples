


package edu.rice.cs.util.newjvm;

import java.rmi.*;


public interface SlaveRemote extends Remote {
  
  public void quit() throws RemoteException;
  
  
  public void start(MasterRemote master) throws RemoteException;
}
