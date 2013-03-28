

package edu.rice.cs.drjava.platform;

import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import edu.rice.cs.util.*;
import edu.rice.cs.drjava.config.OptionConstants;


class WindowsPlatform extends DefaultPlatform {
  
  public static WindowsPlatform ONLY = new WindowsPlatform();
  
  
  protected WindowsPlatform() {};
  
  
  public boolean isWindowsPlatform() {
    return true;
  }
 
  public boolean openURL(URL address) {
    
    if (super.openURL(address)) {
      return true;
    }
    else {
      try {
        
        
        
        
        String addressString = address.toString();
        if (addressString.startsWith("file:/")) {
          String suffix = addressString.substring("file:/".length(), addressString.length());
          addressString = "file://" + suffix;
        }
        
        
        
        Runtime.getRuntime().exec(new String[] {
          "rundll32", "url.dll,FileProtocolHandler", addressString });
        
        
      }
      catch (Throwable t) {
        
        return false;
      }
    }
    
    
    return true;
  }
  
  
  public boolean canRegisterFileExtensions() {
    
    try {
      return getDrJavaFile().getName().endsWith(".exe");
    }
    catch(IOException ioe) { return false; }
  }
  
  private static final String DRJAVA_PROJECT_PROGID = "DrJava.Project";
  private static final String DRJAVA_EXTPROCESS_PROGID = "DrJava.ExtProcess";
  private static final String DRJAVA_JAVA_PROGID = "DrJava.Java";
  
  
  public boolean registerDrJavaFileExtensions() {
    boolean retval = registerFileExtension(OptionConstants.PROJECT_FILE_EXTENSION, DRJAVA_PROJECT_PROGID, "DrJava project file", "text", "text/plain");
    retval &= registerFileExtension(OptionConstants.EXTPROCESS_FILE_EXTENSION, DRJAVA_EXTPROCESS_PROGID, "DrJava addon file", "program", "multipart/mixed");
    return retval;
  }

  
  public boolean unregisterDrJavaFileExtensions() {
    boolean retval = unregisterFileExtension(OptionConstants.PROJECT_FILE_EXTENSION, DRJAVA_PROJECT_PROGID);
    retval &= unregisterFileExtension(OptionConstants.EXTPROCESS_FILE_EXTENSION, DRJAVA_EXTPROCESS_PROGID);
    return retval;
  }
  
  
  public boolean areDrJavaFileExtensionsRegistered() {
    return
      isFileExtensionRegistered(OptionConstants.PROJECT_FILE_EXTENSION, DRJAVA_PROJECT_PROGID) && 
      isFileExtensionRegistered(OptionConstants.EXTPROCESS_FILE_EXTENSION, DRJAVA_EXTPROCESS_PROGID);
  }
  
  
  public boolean registerJavaFileExtension() {
    return registerFileExtension(".java", DRJAVA_JAVA_PROGID, "Java source file", "text", "text/plain");
  }
  
  
  public boolean unregisterJavaFileExtension() {
    return unregisterFileExtension(".java", DRJAVA_JAVA_PROGID);
  }
  
  
  public boolean isJavaFileExtensionRegistered() {
    return isFileExtensionRegistered(".java", DRJAVA_JAVA_PROGID);
  }
     
  
  private boolean isFileExtensionRegistered(String extension, String progid) {
    try {
      
      String oldDefault = WindowsRegistry.getKey(WindowsRegistry.HKEY_CLASSES_ROOT, extension, "");
      if ((oldDefault==null) || (!progid.equals(oldDefault))) return false; 
      
      
      String cmdLine = getCommandLine()+" \"%1\" %*";
      String oldCmdLine = WindowsRegistry.getKey(WindowsRegistry.HKEY_CLASSES_ROOT, progid+"\\shell\\open\\command", "");
      return ((oldCmdLine!=null) && (cmdLine.equals(oldCmdLine)));
    }
    catch(WindowsRegistry.RegistryException re) {
      return false;
    }
    catch(IOException ioe) {
      return false;
    }
  }
  
  
  
  
  private boolean registerFileExtension(String extension, String progid, String fileDesc, String perceived, String mime) {
    try {
      String cmdLine = getCommandLine();
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      try {
        String oldDefault = WindowsRegistry.getKey(WindowsRegistry.HKEY_CLASSES_ROOT, extension, "");
        if ((oldDefault!=null) && (!progid.equals(oldDefault))) {
          
          WindowsRegistry.setKey(WindowsRegistry.HKEY_CLASSES_ROOT, extension+"\\OpenWithProgids", oldDefault, "");
          WindowsRegistry.setKey(WindowsRegistry.HKEY_CLASSES_ROOT, extension+"\\OpenWithList", oldDefault, "");
        }
      }
      catch(WindowsRegistry.RegistryException re) {  }
      
      WindowsRegistry.setKey(WindowsRegistry.HKEY_CLASSES_ROOT, extension, "", progid);
      WindowsRegistry.setKey(WindowsRegistry.HKEY_CLASSES_ROOT, extension, "PerceivedType", perceived);
      WindowsRegistry.setKey(WindowsRegistry.HKEY_CLASSES_ROOT, extension, "Content Type", mime);
      
      WindowsRegistry.setKey(WindowsRegistry.HKEY_CLASSES_ROOT, extension+"\\OpenWithProgids", progid, "");
      WindowsRegistry.setKey(WindowsRegistry.HKEY_CLASSES_ROOT, extension+"\\OpenWithList", progid, "");
      
      WindowsRegistry.setKey(WindowsRegistry.HKEY_CLASSES_ROOT, progid, "", fileDesc);
      
      WindowsRegistry.setKey(WindowsRegistry.HKEY_CLASSES_ROOT, progid+"\\shell\\open", "FriendlyAppName", "DrJava");
      WindowsRegistry.setKey(WindowsRegistry.HKEY_CLASSES_ROOT, progid+"\\shell\\open\\command", "",
                             cmdLine+" \"%1\" %*");
      return true;
    }
    catch(WindowsRegistry.RegistryException re) {
      return false;
    }
    catch(IOException ioe) {
      return false;
    }
  }

  
  public boolean unregisterFileExtension(String extension, String progid) {
    boolean otherProgidsLeft = false; 
    try {
      int handle;
      WindowsRegistry.QueryInfoResult qir;
      try {
        handle = WindowsRegistry.openKey(WindowsRegistry.HKEY_CLASSES_ROOT, extension+"\\OpenWithProgids",
                                         WindowsRegistry.KEY_ALL_ACCESS);
        try {
          WindowsRegistry.deleteValue(handle, progid);
        }
        catch(WindowsRegistry.RegistryException re) {  }
        qir = WindowsRegistry.queryInfoKey(handle);
        otherProgidsLeft |= (qir.valueCount>0);
        otherProgidsLeft |= (qir.subkeyCount>0);
        WindowsRegistry.flushKey(handle);
        WindowsRegistry.closeKey(handle);
        
        handle = WindowsRegistry.openKey(WindowsRegistry.HKEY_CLASSES_ROOT, extension+"\\OpenWithList",
                                         WindowsRegistry.KEY_ALL_ACCESS);
        try {
          WindowsRegistry.deleteValue(handle, progid);
        }
        catch(WindowsRegistry.RegistryException re) {  }
        qir = WindowsRegistry.queryInfoKey(handle);
        otherProgidsLeft |= (qir.valueCount>0);
        otherProgidsLeft |= (qir.subkeyCount>0);
        WindowsRegistry.flushKey(handle);
        WindowsRegistry.closeKey(handle);
        
        if (!otherProgidsLeft) {
          WindowsRegistry.delKey(WindowsRegistry.HKEY_CLASSES_ROOT, extension+"\\OpenWithProgids");
          WindowsRegistry.delKey(WindowsRegistry.HKEY_CLASSES_ROOT, extension);
        }
        WindowsRegistry.delKey(WindowsRegistry.HKEY_CLASSES_ROOT, progid+"\\shell\\open");
        WindowsRegistry.delKey(WindowsRegistry.HKEY_CLASSES_ROOT, progid);
      }
      catch(WindowsRegistry.RegistryException re) {
        
      }
      
      
      
      File drjavaFile = null;
      String ourCmdLine = null;
      try {
        drjavaFile = getDrJavaFile();
        ourCmdLine = getCommandLine()+" \"%1\" %*";
      }
      catch(IOException ioe) { return false; }

      try {
        
        handle = WindowsRegistry.openKey(WindowsRegistry.HKEY_CURRENT_USER,
                                         "Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\FileExts\\"+extension,
                                         WindowsRegistry.KEY_ALL_ACCESS);
        
        try {
          String s = WindowsRegistry.queryValue(handle, "Application");
          
          if ((s!=null) && (s.equals(drjavaFile.getName()))) {
            
            WindowsRegistry.deleteValue(handle, progid);
            
          }
        }
        catch(WindowsRegistry.RegistryException re) {
          
          
        }
        
        
        WindowsRegistry.flushKey(handle);
        
        WindowsRegistry.closeKey(handle);
        
      }
      catch(WindowsRegistry.RegistryException re) {
        
        
      }

      
      otherProgidsLeft = false;
      try {
        
        handle = WindowsRegistry.openKey(WindowsRegistry.HKEY_CURRENT_USER,
                                         "Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\FileExts\\"+extension+"\\OpenWithProgids",
                                         WindowsRegistry.KEY_ALL_ACCESS);
        
        try {
          
          WindowsRegistry.deleteValue(handle, progid);
          
        }
        catch(WindowsRegistry.RegistryException re) {
          
          
        }
        
        qir = WindowsRegistry.queryInfoKey(handle);
        
        otherProgidsLeft |= (qir.valueCount>0);
        otherProgidsLeft |= (qir.subkeyCount>0);
        
        WindowsRegistry.flushKey(handle);
        
        WindowsRegistry.closeKey(handle);
        

        if (!otherProgidsLeft) {
          
          WindowsRegistry.delKey(WindowsRegistry.HKEY_CURRENT_USER,
                                 "Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\FileExts\\"+extension+"\\OpenWithProgids");
          
        }
      }
      catch(WindowsRegistry.RegistryException re) {
        
        
      }

      String mruList = "";
      
      
      try {
        
        handle = WindowsRegistry.openKey(WindowsRegistry.HKEY_CURRENT_USER,
                                         "Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\FileExts\\"+extension+"\\OpenWithList",
                                         WindowsRegistry.KEY_ALL_ACCESS);
        
        try {
          String s = WindowsRegistry.queryValue(handle, "MRUList");
          
          if (s!=null) mruList = s;
        }
        catch(WindowsRegistry.RegistryException re) {
          
          
        }
        
        String newMRUList = "";
        for(int i=0; i<mruList.length(); ++i) {
          String letter = mruList.substring(i,i+1); 
          
          boolean keep = true;
          try {
            
            String value = WindowsRegistry.queryValue(handle, letter);
            
            if (value!=null) {
              
              
              
              
              try {
                String cmdLine =
                  WindowsRegistry.getKey(WindowsRegistry.HKEY_LOCAL_MACHINE,
                                         "SOFTWARE\\Classes\\Applications\\"+value+"\\shell\\open\\command","");
                
                
                if ((cmdLine!=null) && (cmdLine.equals(ourCmdLine))) {
                  
                  
                  keep = false;
                  WindowsRegistry.delKey(WindowsRegistry.HKEY_LOCAL_MACHINE,
                                         "SOFTWARE\\Classes\\Applications\\"+value+"\\shell\\open\\command");
                  
                  WindowsRegistry.delKey(WindowsRegistry.HKEY_LOCAL_MACHINE,
                                         "SOFTWARE\\Classes\\Applications\\"+value+"\\shell\\open");
                  
                  WindowsRegistry.delKey(WindowsRegistry.HKEY_LOCAL_MACHINE,
                                         "SOFTWARE\\Classes\\Applications\\"+value+"\\shell");
                  
                  WindowsRegistry.delKey(WindowsRegistry.HKEY_LOCAL_MACHINE,
                                         "SOFTWARE\\Classes\\Applications\\"+value);
                  
                }
              }
              catch(WindowsRegistry.RegistryException re) {
                
                
              }
              
              
              
              try {
                String cmdLine =
                  WindowsRegistry.getKey(WindowsRegistry.HKEY_CURRENT_USER,
                                         "Software\\Classes\\Applications\\"+value+"\\shell\\open\\command","");
                
                
                if ((cmdLine!=null) && (cmdLine.equals(ourCmdLine))) {
                  
                  
                  keep = false;
                  WindowsRegistry.delKey(WindowsRegistry.HKEY_CURRENT_USER,
                                         "Software\\Classes\\Applications\\"+value+"\\shell\\open\\command");
                  
                  WindowsRegistry.delKey(WindowsRegistry.HKEY_CURRENT_USER,
                                         "Software\\Classes\\Applications\\"+value+"\\shell\\open");
                  
                  WindowsRegistry.delKey(WindowsRegistry.HKEY_CURRENT_USER,
                                         "Software\\Classes\\Applications\\"+value+"\\shell");
                  
                  WindowsRegistry.delKey(WindowsRegistry.HKEY_CURRENT_USER,
                                         "Software\\Classes\\Applications\\"+value);
                  
                }
              }
              catch(WindowsRegistry.RegistryException re) {
                
                
              }
              
              if (!keep) {
                try {
                  
                  WindowsRegistry.deleteValue(handle, letter);
                  
                }
                catch(WindowsRegistry.RegistryException re) {
                  
                  
                }
              }
            }
          }
          catch(WindowsRegistry.RegistryException re) {
            
            
          }
          
          if (keep) newMRUList = newMRUList + letter;
          
        }
        
        
        if (!mruList.equals(newMRUList)) {
          
          
          WindowsRegistry.setValue(handle, "MRUList", newMRUList);
          
        }
        
        WindowsRegistry.flushKey(handle);
        
        WindowsRegistry.closeKey(handle);
        
      }
      catch(WindowsRegistry.RegistryException re) {
        
        
      }      
      return true;
    }
    catch(WindowsRegistry.RegistryException re) {
      
      return false;
    }
  }

  private File getDrJavaFile() throws IOException {
    
    String[] cps = System.getProperty("java.class.path").split("\\;", -1);
    File found = null;
    for(String cp: cps) {
      try {
        File f = new File(cp);
        if (!f.exists()) { continue; }
        if (f.isDirectory()) {
          
          File cf = new File(f, edu.rice.cs.drjava.DrJava.class.getName().replace('.', File.separatorChar)+".class");
          if (cf.exists() && cf.isFile()) {
            found = f;
            break;
          }
        }
        else if (f.isFile()) {
          
          java.util.jar.JarFile jf = new java.util.jar.JarFile(f);
          
          
          
          if (jf.getJarEntry(edu.rice.cs.drjava.DrJava.class.getName().replace('.', '/')+".class")!=null) {
            found = f;
            break;
          }
        }
      }
      catch(IOException e) {  }
    }
    if (found==null) throw new IOException("DrJava file not found");
    return found;
  }
  
  private String getCommandLine() throws WindowsRegistry.RegistryException, IOException {
    final File drjavaFile = getDrJavaFile();
    
    String cmdLine = drjavaFile.getAbsolutePath();
    if (!drjavaFile.getAbsolutePath().endsWith(".exe")) {
      
      
      String jarProgid = WindowsRegistry.getKey(WindowsRegistry.HKEY_CLASSES_ROOT, ".jar", "");
      String jarLine = WindowsRegistry.getKey(WindowsRegistry.HKEY_CLASSES_ROOT, jarProgid+"\\shell\\open\\command", "");
      BalancingStreamTokenizer tok = new BalancingStreamTokenizer(new StringReader(jarLine));
      tok.wordRange(0,255);
      tok.whitespaceRange(' ',' ');
      tok.addQuotes("\"","\"");
      String jarCommand = tok.getNextToken();
      cmdLine = jarCommand + " -jar "+drjavaFile.getAbsolutePath();
    }
    return cmdLine;
  }
}
