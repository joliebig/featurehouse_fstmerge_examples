


package edu.rice.cs.util.newjvm;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface SlaveRemote extends Remote {
  
  public void start(MasterRemote master) throws RemoteException;

  
  public void quit() throws RemoteException;
}
