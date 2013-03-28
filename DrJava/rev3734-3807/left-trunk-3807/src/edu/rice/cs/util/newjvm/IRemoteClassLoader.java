

package edu.rice.cs.util.newjvm;

import java.rmi.*;
import java.net.URL;

public interface IRemoteClassLoader extends Remote{

  
  public Class<?> loadRemoteClass(String name) throws ClassNotFoundException, RemoteException;
  
  
  public URL getRemoteResource(String name) throws ClassNotFoundException, RemoteException;
}




