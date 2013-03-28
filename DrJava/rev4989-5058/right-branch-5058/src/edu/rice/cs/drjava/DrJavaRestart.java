

package edu.rice.cs.drjava;

import edu.rice.cs.plt.concurrent.JVMBuilder;
import edu.rice.cs.util.FileOps;

import java.io.*;
import javax.swing.JOptionPane;


public class DrJavaRestart {
  
  public static final int ATTEMPTS = 5;
  public static final int TIME_BETWEEN_ATTEMPTS = 1000;
  
  public static edu.rice.cs.util.Log LOG = new edu.rice.cs.util.Log("version.txt",false);
  public static void message(String message) {
    LOG.log(message);
    JOptionPane.showMessageDialog(null, message,
                                  "Error Updating DrJava", JOptionPane.ERROR_MESSAGE);

  }
  public static boolean delete(File f) {
    for(int i = 0; i < ATTEMPTS; ++i) {
      if (f.delete()) return true;
      LOG.log("Failed to delete " + f + ", trying again");
      try {
        Thread.sleep(TIME_BETWEEN_ATTEMPTS);
      }
      catch(InterruptedException ie) {  }
    }
    return false;
  }
  public static boolean deleteRecursively(File f) {
    for(int i = 0; i < ATTEMPTS; ++i) {
      if (edu.rice.cs.plt.io.IOUtil.deleteRecursively(f)) return true;
      LOG.log("Failed to recursively delete " + f + ", trying again");
      try {
        Thread.sleep(TIME_BETWEEN_ATTEMPTS);
      }
      catch(InterruptedException ie) {  }
    }
    return false;
  }
  public static boolean rename(File from, File to) {
    for(int i = 0; i < ATTEMPTS; ++i) {
      if (from.renameTo(to)) return true;
      LOG.log("Failed to rename " + from + " to " + to + ", trying again");
      try {
        Thread.sleep(TIME_BETWEEN_ATTEMPTS);
      }
      catch(InterruptedException ie) {  }
    }
    return false;
  }
  public static void main(final String[] args) {
    
    
    
    File source = new File(args[0]);
    File dest = new File(args[1]);
    File exec = dest;
    
    try {      
      LOG.log("source: " + source.getAbsolutePath());
      LOG.log("dest  : " + dest.getAbsolutePath());
      
      if (dest.exists()) {
        if (dest.isFile()) {
          
          if (delete(dest)) {
            
            if (!rename(source,dest)) {
              
              exec = source;
              message("A new version of DrJava was downloaded. However, it could not be\n" + 
                      "installed in the same place as the old DrJava.\n\n" + 
                      "The new copy is now installed at:\n" + 
                      source.getAbsolutePath() + "\n\n" + 
                      "The old copy has been deleted.");
            }
          }
          else {
            
            exec = source;
            message("A new version of DrJava was downloaded. However, it could not be\n" + 
                    "installed in the same place as the old DrJava.\n\n" + 
                    "The new copy is now installed at:\n" + 
                    source.getAbsolutePath() + "\n\n" + 
                    "The old copy is still installed at:\n" + 
                    dest.getAbsolutePath());
          }
          LOG.log("Restarting...");
          Process p = JVMBuilder.DEFAULT.classPath(exec).start(DrJava.class.getName(), "-new", "-delete-after-restart", args[2]);
          LOG.log("Done with DrJavaRestart");
          System.exit(0);
        }
        else {
          
          
          File old = FileOps.generateNewFileName(dest);
          if (rename(dest,old)) {
            if (rename(source,dest)) {
              
              delete(source.getParentFile());
              if (!deleteRecursively(old)) {
                message("A new version of DrJava was downloaded. However, the old version" + 
                        "could not be deleted.\n\n" + 
                        "The new copy is now installed at:\n" + 
                        dest.getAbsolutePath() + "\n\n" + 
                        "The old copy is still installed at:\n" + 
                        old.getAbsolutePath());
              }
            }
            else {
              
              exec = source;
              
              if (rename(old,dest)) {
                
                message("A new version of DrJava was downloaded. However, it could not be\n" + 
                        "installed in the same place as the old DrJava.\n\n" + 
                        "The new copy is now installed at:\n" + 
                        source.getAbsolutePath() + "\n\n" + 
                        "The old copy is still installed at:\n" + 
                        dest.getAbsolutePath());
              }
              else {
                
                message("A new version of DrJava was downloaded. However, it could not be\n" + 
                        "installed in the same place as the old DrJava.\n\n" + 
                        "The new copy is now installed at:\n" + 
                        source.getAbsolutePath() + "\n\n" + 
                        "The old copy is still installed at:\n" + 
                        old.getAbsolutePath());
              }
            }
          }
          else {
            
            exec = source;
            message("A new version of DrJava was downloaded. However, it could not be\n" + 
                    "installed in the same place as the old DrJava.\n\n" + 
                    "The new copy is now installed at:\n" + 
                    source.getAbsolutePath() + "\n\n" + 
                    "The old copy is still installed at:\n" + 
                    dest.getAbsolutePath());
          }
          
          
          File macOpenFile = new File("/usr/bin/open");
          LOG.log("Searching for " + macOpenFile);
          if (!macOpenFile.exists()) {
            String path = System.getenv("PATH");
            for(String p: path.split(System.getProperty("path.separator"))) {
              macOpenFile = new File(p, "tar");
              LOG.log("Searching for " + macOpenFile);
              if (macOpenFile.exists()) break;
            }
          }
          if (macOpenFile.exists()) {
            LOG.log("Restarting using ProcessBuilder...");
            Process p = new ProcessBuilder()
              .command(macOpenFile.getAbsolutePath(), exec.getAbsolutePath())
              .redirectErrorStream(true)
              .start();
            System.exit(0);
          }
          else {
            LOG.log("Restarting using JVMBuilder...");
            exec = new File(exec,"Contents/Resources/Java/drjava.jar");
            Process p = JVMBuilder.DEFAULT.classPath(exec).start(DrJava.class.getName(), "-new", "-delete-after-restart", args[2]);
            LOG.log("Done with DrJavaRestart");
            System.exit(0);
          }
        }
      }
    }
    catch(Exception e) {
      message("A new version of DrJava was downloaded. However, there was an error" + 
              "during installation:\n" + e.getMessage());
    }
  }
}
