

package edu.rice.cs.util.newjvm;

import java.rmi.*;
import edu.rice.cs.util.FileOps;
import java.io.*;
import java.net.URL;


public class CustomSystemClassLoader extends ClassLoader{
  
  IRemoteClassLoader _master;
  
  
  public CustomSystemClassLoader(ClassLoader c){
    super(c);
    _master = null;
  }
  
  
  public void setMasterRemote(IRemoteClassLoader m){
    _master = m;
  }
  
  
  
  public Class<?> loadClass(String name) throws ClassNotFoundException{
    Class c;
    
    c = findLoadedClass(name);
    if (c!= null) {
      return c;
    }
    
    try{
      String fileName = name.replace('.', '/') + ".class";
      URL resource = getParent().getResource(fileName); 
      if (resource == null) {
        throw new ClassNotFoundException("Resource not found: " + fileName);
      }
      else if(fileName.startsWith("edu/rice/cs/util/newjvm/SlaveJVMRunner.class")){
        byte[] data = FileOps.readStreamAsBytes(resource.openStream());
        try { return defineClass(name, data, 0, data.length); }
        catch (Error t) { throw t; }
      }
      
      return getParent().loadClass(name);
    }
    catch(ClassNotFoundException e) {  }
    catch(IOException e) {  }
    
    
    try {
      if (_master != null) {
        String fileName = name.replace('.', '/') + ".class";
        URL resource = _master.getRemoteResource(fileName); 
        if (resource == null) {
          throw new ClassNotFoundException("Resource not found: " + fileName);
        }
        else {
          byte[] data = FileOps.readStreamAsBytes(resource.openStream());
          try { return defineClass(name, data, 0, data.length); }
          catch (Error t) { throw t; }
        }
      }
      else throw new ClassNotFoundException();
    }
    catch(RemoteException e) { throw new ClassNotFoundException(); }
    catch(IOException e) { throw new ClassNotFoundException(); }
  }
}




