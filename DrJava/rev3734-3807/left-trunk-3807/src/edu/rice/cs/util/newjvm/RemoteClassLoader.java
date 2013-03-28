

package edu.rice.cs.util.newjvm;

import java.rmi.*;
import java.net.URL;


public class RemoteClassLoader extends ClassLoader implements IRemoteClassLoader{

  
  public RemoteClassLoader(ClassLoader c){
    super(c);
  }
  
  
  public Class<?> loadRemoteClass(String name) throws ClassNotFoundException, RemoteException{
    return getParent().loadClass(name);
  }
  
  
  public URL getRemoteResource(String name) throws ClassNotFoundException, RemoteException{
    return getParent().getResource(name);
  }
}




