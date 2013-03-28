

package edu.rice.cs.util.newjvm;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface MasterRemote extends Remote {
  
  public void checkStillAlive() throws RemoteException;
}
