

package edu.rice.cs.util.newjvm;

import java.io.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
  
import edu.rice.cs.util.StringOps;
import edu.rice.cs.util.swing.ScrollableDialog;



public final class SlaveJVMRunner {

  
  public static final boolean SHOW_DEBUG_DIALOGS = false;
  
  
  private SlaveJVMRunner() { }

  private static SlaveRemote _getInstance(Class clazz) throws Exception {
    try {
      return (SlaveRemote) clazz.getField("ONLY").get(null);
    }
    catch (Throwable t) {
      return (SlaveRemote) clazz.newInstance();
    }
  }

  
  
  public static void main(String[] args) {
    try {
      
      System.setProperty("java.rmi.server.hostname", "127.0.0.1");
      
      if (args.length != 3 && args.length != 2) System.exit(1);
      
      
      if (args.length == 3) {
        
        IRemoteClassLoader remote = null;
        FileInputStream fstream = new FileInputStream(args[2]);
        ObjectInputStream ostream = new ObjectInputStream(fstream);
        remote = (IRemoteClassLoader) ostream.readObject();
        if (ClassLoader.getSystemClassLoader() instanceof CustomSystemClassLoader) {
          CustomSystemClassLoader loader = (CustomSystemClassLoader) ClassLoader.getSystemClassLoader();
          loader.setMasterRemote(remote);
        }
      }

      
      FileInputStream fstream = new FileInputStream(args[0]);
      ObjectInputStream ostream = new ObjectInputStream(fstream);
      MasterRemote remote = (MasterRemote) ostream.readObject();
      
      try {
        Class slaveClass = Class.forName(args[1]);
        SlaveRemote slave = _getInstance(slaveClass);
        
        
        SlaveRemote stub = (SlaveRemote) UnicastRemoteObject.exportObject(slave);
        
        
        

        
        slave.start(remote);
        remote.registerSlave(slave);
      }
      catch (Throwable t) {
        
        try {
          
          remote.errorStartingSlave(t);
        }
        catch (RemoteException re) {
          
          String msg = "Couldn't instantiate and register the slave.\n" +
            "  Also failed to display error through master JVM, because:\n" +
            StringOps.getStackTrace(re) + "\n";
          _showErrorMessage(msg, t);
        }
        System.exit(3);
      }
    }
    catch (Throwable t) {
      
      _showErrorMessage("Couldn't deserialize remote stub for the master JVM.", t);
      System.exit(2);
    }
  }
  
  
  private static void _showErrorMessage(String cause, Throwable t) {
    String msg = "An error occurred while starting the slave JVM:\n  " +
      cause + "\n\nOriginal error:\n" + StringOps.getStackTrace(t);

    if (SHOW_DEBUG_DIALOGS) new ScrollableDialog(null, "Error", "Error details:", msg).show();
    else System.out.println(msg);
  }
}
