

package edu.rice.cs.util.newjvm;

import java.rmi.*;




public interface MasterRemote extends Remote {
  
  
  public void registerSlave(SlaveRemote slave) throws RemoteException;

  
  public void checkStillAlive() throws RemoteException;
  
  
  public void errorStartingSlave(Throwable cause) throws RemoteException;
}
